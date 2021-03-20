package com.company;

import java.util.concurrent.Callable;
import java.util.ArrayList;

public class Kernel{
    public static enum Status{NEW, RUNNING, WAITING, READY, TERMINATED}
    //inner class represting a process
    public class PCB{               // Process control block. Needs to be filled in.

        

        public Status status;
        public Callable<?> action;  // The next code block to run for this process                    
        public int pid;             // The process ID
        public byte[] registers;    // register states
    }

    public PCB current;                            // the currently running process
    public int pidCounter = 1;                     // the pid of the next created process, 1 because init is our process 0
    public GenericHAL hal = new GenericHAL(this);  // the HAL our kernel is running on                
    public ArrayList<PCB> activeProcesses = new ArrayList<PCB>(); //List of all active processes

    // called once when the system is started
    public void Init(Callable<?> code){                    
        final PCB pcb = new PCB();                     
        pcb.action = code;                               
        pcb.pid = 0;
        pcb.registers = new byte[hal.RegisterSize];
        current = pcb;
        activeProcesses.add(current);                            
        hal.RunCode();                            
    }

    //Create a new process, takes code to be ran in the new process
    public int CreateProcess(Callable<?> code){
        PCB pcb = new PCB();                     
        pcb.action = code;                               
        pcb.pid = pidCounter;
        pcb.registers = new byte[hal.RegisterSize];
        pidCounter++;
        activeProcesses.add(pcb);
        return pcb.pid;
    }

    public void Reschedule(){
        hal.StoreProgramData(current.registers);
        current = activeProcesses.get(0);
        hal.RestoreProgramData(current.registers);
    }

    //Deletes the current process by its pid
    public void DeleteProcess(int pid){
        for(int i = 0; i < activeProcesses.size(); i++){ //linear search for active process
            PCB selectedProcess = activeProcesses.get(i);
            if(selectedProcess.pid == pid){
                activeProcesses.remove(i);              //O(1) removal of selected process from activeProcesses by index
                if(selectedProcess == current)   //current process is deleting itself
                    if(activeProcesses.size() > 0)      //if there are other processes reschedule, else end the program
                        Reschedule();
                    else
                        System.exit(0);  //We just deleted ourself, the last process, there is nothing to reschedule
                return;
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
}
