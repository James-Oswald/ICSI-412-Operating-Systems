package com.company;

import java.util.concurrent.Callable;
public class Main {

    public static class ExitTheThing implements Callable {
        public Void call() throws Exception {
            KernelBindings.Exit(0, null);
            return null;
        }
    }

    public static class PrintTheThing implements Callable {
        public Void call() throws Exception {
            KernelBindings.Print("Code Ran!", new ExitTheThing());
            return null;
        }
    }
    public static void main(String[] args) {
        Kernel k = new Kernel();
        k.Init(new PrintTheThing());
        /*
        k.Init(() -> {
            KernelBindings.Print("Code Ran!",
                    () -> KernelBindings.Exit(0, null));
                return null;
                }
        );
         */
    }
}
