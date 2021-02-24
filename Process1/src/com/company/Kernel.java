package com.company;

import java.util.concurrent.Callable;
import java.util.ArrayList;

public class Kernel{

    //inner class represting a process
    public class PCB{               // Process control block. Needs to be filled in.
        public Callable<?> action;  // The next code block to run for this process                    
        public int pid;
        public byte[] registers;    // register states
    }

    public PCB current;                            // the currently running process
    public int pidCounter = 1;                     // the pid of the next created process
    public GenericHAL hal = new GenericHAL(this);  // the HAL our kernel is running on                
    public ArrayList<PCB> activeProcesses = new ArrayList<PCB>();

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

    public void DeleteProcess(int pid){
        for(int i = 0; i < activeProcesses.size(); i++){ //linear search for active process
            PCB curProc = activeProcesses.get(i);
            if(curProc.pid == pid){
                activeProcesses.remove(i);  //O(1) removal of curProc from activeProcesses by index
                if(curProc == current)      //current process is deleting itself
                    if(activeProcesses.size() > 0) // if there are other processes reschedule, else end the program
                        Reschedule();
                    else
                        System.exit(0);     //We just deleted ourself, the last process, there is nothing to reschedule
                return;
            }
        }
    }

    // Exit syscall. Needs more work when we finish processes
    public Object Exit(){
        DeleteProcess(current.pid);                           
        return 0;
    }

    public void Print(String str){                       // Print a line
        System.out.println(str);
    }
}
