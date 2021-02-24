package com.company;

import java.util.concurrent.Callable;

public class SyscallException extends Exception {
    public SyscallException(GenericHAL.SyscallNumbers num, Callable nextCode) {
        which = num;
        next = nextCode;
    }

    public SyscallException(GenericHAL.SyscallNumbers num, Callable nextCode, Object param1) {
        which = num;
        next = nextCode;
        param = param1;
    }


    public GenericHAL.SyscallNumbers which;
    public Object param;
    public Callable next;
}
