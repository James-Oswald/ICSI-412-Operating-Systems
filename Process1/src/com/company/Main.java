package com.company;

public class Main{
    public static void main(String[] args) {
        Kernel kernal = new Kernel();                   //The structure of crating processes is recursive so I outline it once
        kernal.Init(() ->                               //each parameter gets its own line to make this function hell readable
            KernelBindings.CreateProcess(               
                ()->KernelBindings.Print(               //Paramter 1, process 1 code, prints than dies
                    "Process 1",   
                    ()->KernelBindings.Exit(0, null)),  //Kill process 1 after it prints
                ()->KernelBindings.CreateProcess(       //Paramter 2, next action in current (proc 0) process, create another process
                    ()->KernelBindings.Print(
                        "Process 2",
                        ()->KernelBindings.Exit(0, null)), 
                    ()->KernelBindings.CreateProcess(   
                        ()->KernelBindings.Print(
                            "Process 3",
                            ()->KernelBindings.Exit(0, null)),
                        ()->KernelBindings.CreateProcess(  
                            ()->KernelBindings.Print(
                                "Process 4", 
                                ()->KernelBindings.Exit(0, null)),
                            ()->KernelBindings.Exit(0, null)    //final action in process 0, delete our process creation thread.
                        )
                    )
                )
            )
        );  
    }
}
