
public class KernelBindings {
    public static Kernel k;

    static {
        k = new Kernel();
        k.Init();
    }

    public static Object Exit() {
        return k.Exit();
    }
    public static Object Print(String s) {
        return k.Print(s);
    }
    public static Object CreateProcess()  {
        return k.CreateProcess();
    }

    public static Object DeleteProcess(int pid)  {
        return k.DeleteProcess(pid);
    }

    public static Object printMemoryMap(){
        return k.printMemoryMap();
    }

    public static Object Reschedule(int pid){
        return k.Reschedule(pid);
    }

    public static Object allocateMemory(int pid, int ammount){
        return k.allocateMemory(pid, ammount);
    }

    public static Object freeMemory(int pid){
        return k.freeMemory(pid);
    }

    public static Object AccessMemory(int address){
        GenericHAL.AccessMemory(address);
        return null;
    }
}
