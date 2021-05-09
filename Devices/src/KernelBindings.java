
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

    public static Integer open(String s){
        return k.open(s);
    }

    public static Object close(int id){
        return k.close(id);
    }

    public static Object seek(int id, int to){
        return k.seek(id, to);
    }

    public static byte[] read(int id, int count){
        return k.read(id, count);
    }

    public static Integer write(int id, byte[] data){
        return k.write(id, data);
    }

    public static Object printMemoryMap(){
        return k.printMemoryMap();
    }

    public static Object Reschedule(int pid){
        return k.Reschedule(pid);
    }

    public static Boolean allocateProgramMemory(int pid, int ammount){
        return k.allocateProgramMemory(pid, ammount);
    }

    public static Boolean allocateStackMemory(int pid, int ammount){
        return k.allocateStackMemory(pid, ammount);
    }

    public static Boolean allocateHeapMemory(int pid, int ammount){
        return k.allocateHeapMemory(pid, ammount);
    }

    public static Object freeProgramMemory(int pid){
        return k.freeProgramMemory(pid);
    }

    public static Object freeStackMemory(int pid){
        return k.freeStackMemory(pid);
    }

    public static Object freeHeapMemory(int pid){
        return k.freeHeapMemory(pid);
    }

    public static Object testProgramAccess(int address){
        k.hal.AccessProgram(address);
        return null;
    }

    public static Object testStackAccess(int address){
        k.hal.AccessStack(address);
        return null;
    }

    public static Object testHeapAccess(int address){
        k.hal.AccessHeap(address);
        return null;
    }
}
