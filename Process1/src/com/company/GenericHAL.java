package com.company;

import java.util.concurrent.Callable;

public class GenericHAL{

    public final int RegisterSize = 64;                // How big our register storage is
    public final Kernel kernel;                        // the kernel we are supporting

    public GenericHAL(Kernel k){                       // HAL knows kernel; kernel knows HAL
        kernel = k;
    }

    public void StoreProgramData(byte [] data){}       // Store registers to PCB. Does nothing in Java
    public void RestoreProgramData(byte [] data){}     // Loads registers from PCB. IBID

    public enum SyscallNumbers{                        // the ID for each syscall
        Exit, Print, CreateProcess
    }

    public enum InterruptTypes{                        // the interrupt numbers. In a real machine, these would be architecture specific
        Syscall
    }

    public Object HandleInterrupt(InterruptTypes it, Object p1, Object p2){
        try{
            // dispatch based on the interrupt ID                                        
            switch(it){
                //For syscalls, dispatch based on the syscall #
                case Syscall:                       
                    SyscallNumbers syscallNumber = (SyscallNumbers) p1;
                    switch(syscallNumber){
                        case Exit:
                            return kernel.Exit();
                        case Print:
                            kernel.Print((String)p2);
                            return null;
                        case CreateProcess: 
                            //if syscallNumber is CreateProcess we know our p2 is really a callable so we upcast it back.    
                            return kernel.CreateProcess((Callable<?>)p2);  
                        default:
                            throw new Exception("What syscall is that?");
                    }
                default:
                    throw new Exception("What interrupt is that?");
            }
        }
        catch(Exception e){
            System.out.println("You have a serious error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    public void RunCode(){                          // This is our Java hack for no PC control.
        while (true){
            try{
                kernel.current.action.call();       // Run the next callable on the current PCB
            } catch (SyscallException e){           // This exception indicates that we are doing a syscall
                kernel.current.action = e.next;     // Set the next action for this process to the one chained in the exception
                HandleInterrupt(InterruptTypes.Syscall, e.which, e.param);
            } catch (Exception e) {
                System.out.println("Some other exception occurred.");
                e.printStackTrace();
            }
        }                                           // After the syscall/interrupt, run "userland" code
    }

}
