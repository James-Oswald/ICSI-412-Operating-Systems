package com.company;

import java.util.concurrent.Callable;

public class Kernel{
    public class PCB{                                   // Process control block. Needs to be filled in.
        public Callable<?> action;                      // The next code block to run for this process
    }

    public GenericHAL hal = new GenericHAL(this);       // the HAL our kernel is running on
    public PCB current;                                 // the currently running process

    public void Init(Callable<?> c){                    // called once when the system is started
        final PCB pcb = new PCB();                      // make a PCB for the Init process
        pcb.action = c;                                 // Set Init's action
        current = pcb;                                  // make Init the running process
        hal.RunCode();                                  // run Init
    }

    public Object Exit() {                              // Exit syscall. Needs more work when we finish processes
        return 0;
    }

    public void Print(String s) {                       // Print a line
        System.out.println(s);
    }
}
