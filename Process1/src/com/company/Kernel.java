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
    public void Init(Callable<?> c){                    
        final PCB pcb = new PCB();                     
        pcb.action = c;                               
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
        activeProcesses.add(pcb);
        pidCounter++;
        return pcb.pid;
    }

    public void Reschedule(){
        
    }

    // Exit syscall. Needs more work when we finish processes
    public Object Exit(){                              
        return 0;
    }

    public void Print(String str){                       // Print a line
        System.out.println(str);
    }
}
