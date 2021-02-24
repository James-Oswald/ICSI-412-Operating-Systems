package com.company;

import java.util.concurrent.Callable;

public class KernelBindings{
    public static Object Exit(int i, Callable<?> next) throws SyscallException{
        throw new SyscallException(GenericHAL.SyscallNumbers.Exit,next,i);
    }
    public static Object Print(String s, Callable<?> next) throws SyscallException{
        throw new SyscallException(GenericHAL.SyscallNumbers.Print,next,s);
    }
}
