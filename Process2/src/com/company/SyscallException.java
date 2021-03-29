//package com.company;

import java.util.concurrent.Callable;

public class SyscallException extends Exception {
    private static final long serialVersionUID = 1L;
    public GenericHAL.SyscallNumbers which;
    public Object param;
    public Callable<?> next;

    public SyscallException(GenericHAL.SyscallNumbers num, Callable<?> nextCode) {
        which = num;
        next = nextCode;
    }

    public SyscallException(GenericHAL.SyscallNumbers num, Callable<?> nextCode, Object param1) {
        which = num;
        next = nextCode;
        param = param1;
    }
}
