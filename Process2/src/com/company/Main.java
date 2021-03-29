
//package com.company;

public class Main{

    //Same thing as last time, test that processes can be created.
    public static void test1(){
        KernelLog.newLog("./Test1Log.txt");
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

    public static void test2(){
        KernelLog.newLog("./Test2Log.txt");
        Kernel kernal = new Kernel(); 
        kernal.Init(
            ()->KernelBindings.CreateProcess(
                ()->KernelBindings.CreateMutex(
                    "Mutex1",
                    ()->KernelBindings.Print(              
                        "Mutex Print",
                        ()->KernelBindings.ReleaseMutex(
                            "Mutex1",   
                            ()->KernelBindings.Exit(0, null)
                        )
                    )
                ), 
                ()->KernelBindings.CreateProcess(
                    ()->KernelBindings.GetMutexAccess(
                        "Mutex1",
                        ()->KernelBindings.Print(              
                            "Mutex Print",
                            ()->KernelBindings.DeleteMutex(
                                "Mutex1",   
                                ()->KernelBindings.Exit(0, null)
                            )
                        )
                    ),
                    ()->KernelBindings.Exit(0, null)
                )
            )
        ); 
    }

    public static void main(String[] args) {
        String test = "1";
        if(args.length >= 1)
            test = args[0];
        System.out.println("Test" + test + ":");
        if(test.equals("1"))
            test1();
        else
            test2();
    }
}
