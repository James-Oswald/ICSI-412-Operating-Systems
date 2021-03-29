//package com.company;

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

    public static Object CreateMutex(String name, Callable<?> next) throws SyscallException{
        throw new SyscallException(GenericHAL.SyscallNumbers.CreateMutex, next, name);
    }

    public static Object GetMutexAccess(String name, Callable<?> next) throws SyscallException{
        throw new SyscallException(GenericHAL.SyscallNumbers.GetMutexAccess, next, name);
    }

    public static Object ReleaseMutex(String name, Callable<?> next) throws SyscallException{
        throw new SyscallException(GenericHAL.SyscallNumbers.ReleaseMutex, next, name);
    }

    public static Object DeleteMutex(String name, Callable<?> next) throws SyscallException{
        throw new SyscallException(GenericHAL.SyscallNumbers.DeleteMutex, next, name);
    }
}
