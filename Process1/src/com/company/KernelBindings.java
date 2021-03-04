package com.company;

import java.util.concurrent.Callable;

public class KernelBindings{

    public static Object Exit(int i, Callable<?> next) throws SyscallException{
        throw new SyscallException(GenericHAL.SyscallNumbers.Exit, next, i);
    }

    public static Object Print(String s, Callable<?> next) throws SyscallException{
        throw new SyscallException(GenericHAL.SyscallNumbers.Print, next, s);
    }

    //Kernel Bindings for CreateProcess
    //Takes code to create a process with and code for the next action in the current process
    public static Object CreateProcess(Callable<?> process, Callable<?> next) throws SyscallException{
        throw new SyscallException(GenericHAL.SyscallNumbers.CreateProcess, next, process);
    }
}
