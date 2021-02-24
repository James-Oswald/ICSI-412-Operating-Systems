package com.company;

import java.util.concurrent.Callable;

public class Main {

    public static class ExitTheThing implements Callable<Void> {
        public Void call() throws Exception {
            KernelBindings.Exit(0, null);
            return null;
        }
    }

    public static class PrintTheThing implements Callable<Void> {
        public Void call() throws Exception {
            KernelBindings.Print("Code Ran!", new ExitTheThing());
            return null;
        }
    }
    public static void main(String[] args) {
        Kernel kernal = new Kernel();
        kernal.Init(() ->{
            kernal.CreateProcess(()->KernelBindings.Print("Process 1", ()->KernelBindings.Exit(0, null)));
            kernal.CreateProcess(()->KernelBindings.Print("Process 2", ()->KernelBindings.Exit(0, null)));
            kernal.CreateProcess(()->KernelBindings.Print("Process 3", ()->KernelBindings.Exit(0, null)));
            kernal.CreateProcess(()->KernelBindings.Print("Process 4", ()->KernelBindings.Exit(0, null)));
            KernelBindings.Exit(0, null);
            return null;
        });
    }
}
