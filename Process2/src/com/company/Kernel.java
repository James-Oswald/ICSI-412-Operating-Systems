//package com.company;

import java.util.concurrent.Callable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;


public class Kernel{

    public enum PCBStatus{
        NEW, RUNNING, WAITING, READY, TERMINATED
    }

    //inner class represting a process
    public class PCB{               // Process control block. Needs to be filled in.
        public PCBStatus status;
        public ArrayList<Mutex> mutexs; //Each process owns mutexs
        public Callable<?> action;  // The next code block to run for this process                    
        public int pid;             // The process ID
        public byte[] registers;    // register states
    }

    //inner class representing a mutex, directions specify all members need to be private and have accessors.
    public class Mutex {
        public String name;
        public PCB owner;
        public int ownerPID;

        public Mutex(){
            name = null;
            owner = null;
            ownerPID = 0;
        }

        public void setName(String name){
            this.name = name;
        }

        public void setOwner(PCB owner){
            this.owner = owner;
        }

        public void setOwnerPID(int ownerPID){
            this.ownerPID = ownerPID;
        }

        public String getName(){
            return name;
        }

        public PCB getOwner(){
            return owner;
        }

        public int getOwnerPID(){
            return ownerPID;
        }
    }

    public PCB current;                            // the currently running process
    public int pidCounter = 1;                     // the pid of the next created process, 1 because init is our process 0
    public GenericHAL hal = new GenericHAL(this);  // the HAL our kernel is running on                
    public ArrayList<PCB> activeProcesses = new ArrayList<PCB>(); //List of all active processes
    public HashMap<String, Queue<PCB>> waiting;  //Hashmap of which PCBs are in which state queues
    public Queue<PCB> ready, terminated;
    public HashMap<String, Mutex> mutexAccess;     //A hashmap of all active mutexs, allows for fast access to any mutex


    // called once when the system is started
    public void Init(Callable<?> code){
        mutexAccess = new HashMap<String, Mutex>();
        waiting = new HashMap<String, Queue<PCB>>();
        ready = new LinkedList<PCB>();
        terminated = new LinkedList<PCB>();

        //Create the init process
        PCB pcb = new PCB();
        pcb.status = PCBStatus.NEW;                     
        pcb.action = code;                               
        pcb.pid = pidCounter;
        pcb.mutexs = new ArrayList<Mutex>();
        pcb.registers = new byte[hal.RegisterSize];
        pidCounter++;
        activeProcesses.add(pcb);
        pcb.status = PCBStatus.RUNNING;
        current = pcb;   
        hal.RunCode();                     
    }

    //Create a new process, takes code to be ran in the new process
    public int CreateProcess(Callable<?> code){
        PCB pcb = new PCB();
        pcb.status = PCBStatus.NEW;                     
        pcb.action = code;                               
        pcb.pid = pidCounter;
        pcb.mutexs = new ArrayList<Mutex>();
        pcb.registers = new byte[hal.RegisterSize];
        pidCounter++;
        activeProcesses.add(pcb);
        pcb.status = PCBStatus.READY;
        ready.add(pcb);
        KernelLog.log("Process " + current.pid + " has created process " + (pidCounter - 1));
        return pcb.pid;
    }

    public void Reschedule(){
        hal.StoreProgramData(current.registers);
        PCB old = current;
        if(current.status != PCBStatus.TERMINATED){ //The process is terminated don't do it
            current.status = PCBStatus.READY;
            ready.add(current);
        }
        current = ready.poll();
        if(current == null){ //There are no processes to run
            System.exit(0);
            KernelLog.log("Nothing can run");
        }
        KernelLog.log(old.pid + " was rescheduled for " + current.pid);
        //KernelLog.logQueue(ready);
        current.status = PCBStatus.RUNNING;
        hal.RestoreProgramData(current.registers);
    }

    //Deletes the current process by its pid
    public void DeleteProcess(int pid){
        for(int i = 0; i < activeProcesses.size(); i++){ //linear search for active process
            PCB selectedProcess = activeProcesses.get(i);
            if(selectedProcess.pid == pid){
                if(selectedProcess.status == PCBStatus.READY)
                    ready.remove(selectedProcess);
                selectedProcess.status = PCBStatus.TERMINATED;
                terminated.add(selectedProcess);
                activeProcesses.remove(i);     //O(1) removal of selected process from activeProcesses by index
            }
        }
    }

    // Exit deletes the current process    
    public Object Exit(){
        DeleteProcess(current.pid);                        
        return 0;
    }

    // Print a line
    public void Print(String str){  
        System.out.println(str);
    }

    public void CreateMutex(String name){
        KernelLog.log(current.pid + " has created mutex: " + name);
        Mutex newMutex = new Mutex();
        newMutex.setName(name);
        newMutex.setOwner(current);
        newMutex.setOwnerPID(current.pid);
        current.mutexs.add(newMutex);
        mutexAccess.put(name, newMutex);
    }

    public void GetMutexAccess(String name){
        KernelLog.log(current.pid + " has requested access to mutex: " + name);
        Mutex selectedMutex = mutexAccess.get(name);
        if(selectedMutex.getOwnerPID() == 0){               //The mutex is not in use
            selectedMutex.setOwner(current);
            selectedMutex.setOwnerPID(current.pid);
        }else{                                              //The mutex is in use
            Queue<PCB> mutexQueue = waiting.get(name);
            if(mutexQueue != null){                         //A waiting Queue for the mutex exists, insert at end
                mutexQueue.add(current);
                current.status = PCBStatus.WAITING;
            }else{                                          //No Waiting Queue exists for the resource
                waiting.put(name, new LinkedList<PCB>());
                GetMutexAccess(name);
                return;
            }
        }
    }

    public void ReleaseMutex(String name){
        KernelLog.log(current.pid + " has released mutex: " + name);
        Mutex selectedMutex = mutexAccess.get(name);
        Queue<PCB> nextOwners = waiting.get(name);
        if(nextOwners != null && nextOwners.size() > 0){    //There is a process waiting to use this mutex
            PCB nextOwner = nextOwners.poll();
            nextOwner.status = PCBStatus.RUNNING;
            if(!nextOwner.mutexs.contains(selectedMutex))
                nextOwner.mutexs.add(selectedMutex);
            selectedMutex.setOwner(nextOwner);
            selectedMutex.setOwnerPID(nextOwner.pid);
        }else{                                              //No one wants to use this mutex
            selectedMutex.setOwner(null);
            selectedMutex.setOwnerPID(0);
        }
    }

    public void DeleteMutex(String name){
        KernelLog.log(current.pid + " has deleted mutex: " + name);
        Mutex selectedMutex = mutexAccess.get(name);
        selectedMutex.owner.mutexs.remove(selectedMutex);
        mutexAccess.remove(name);
    }
}
