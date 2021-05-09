
import java.util.BitSet;
import java.util.ArrayList;

public class Kernel {
    public class PCB { // Process control block. Needs to be filled in.
        PCB() {
            pid = maxPid++;
        }
        int[] devicesAccessor = new int[10]; //maps descriptor numbers to device access numbers
        Device[] device = new Device[10];   //maps descriptor numbers to devices
        byte[] registerData = new byte[hal.RegisterSize];
        int pid;
        int programBase, stackBase, heapBase;
        int programLimit, stackLimit, heapLimit;
    }

    public GenericHAL hal = new GenericHAL(this); // the HAL our kernel is running on
    private BitSet memoryMap;
    public PCB current; // the currently running process
    int maxPid = 1;

    public ArrayList<PCB> activePCBS = new ArrayList<>();

    public void Init() { // called once when the system is started
        final PCB pcb = new PCB(); // make a PCB for the Init process
        current = pcb; // make Init the running process
        activePCBS.add(pcb);
        memoryMap = new BitSet(hal.getMemorySize() / 1024);
    }

    public int CreateProcess() {
        PCB newOne = new PCB();
        activePCBS.add(newOne);
        return newOne.pid;
    }

    public Object DeleteProcess(int pid) {
        for (int i=0;i<activePCBS.size(); i++) {
            if (activePCBS.get(i).pid == pid) {
                activePCBS.remove(i);
                break;
            }
        }

        if (activePCBS.size() == 0)
            System.exit(0); // We deleted the last of the process. We are finished.
        return null;
    }

    public Object Reschedule(int pid) {
        hal.StoreProgramData(current.registerData);
        current = null;
        for (PCB pcb : activePCBS) {
            if (pcb.pid == pid)
                current = pcb;
        }
        if (current == null)
        {
            current = activePCBS.get(0);
        }
        hal.programBase = current.programBase;
        hal.programLimit = current.programLimit;
        hal.stackBase = current.stackBase;
        hal.stackLimit = current.stackLimit;
        hal.heapBase = current.heapBase;
        hal.heapLimit = current.heapLimit;
        hal.RestoreProgramData(current.registerData);
        return null;
    }

    public Object Exit() { // Exit syscall. Needs more work when we finish processes
        DeleteProcess(current.pid);
        return 0;
    }

    public Object Print(String s) { // Print a line
        System.out.println(s);
        return null;
    }

    //helper to get an open device
    private int getOpenDevice(){
        int id = 0;
        for(;current.device[id] != null;id++);
        return id;
    }

    //create our devices
    public int open(String s){
        int index = s.indexOf(" ");
        String deviceName = s.substring(0, index);
        String arguments = s.substring(index + 1);
        int id = getOpenDevice();
        switch(deviceName){
            case "random":
                current.device[id] = RandomDevice.getInstance();
                current.devicesAccessor[id] = current.device[id].Open(arguments);
                break;
            case "pipe":
                current.device[id] = Pipe.getInstance();
                current.devicesAccessor[id] = current.device[id].Open(arguments);
                break;
            case "file":
                current.device[id] = FileSystem.getInstance();
                current.devicesAccessor[id] = current.device[id].Open(arguments);
                break;
            default:
                System.out.print("Invalid Device Name");
                System.exit(1);
        }
        return id;
    }

    public Object close(int id){
        current.device[id].Close(current.devicesAccessor[id]);
        return null;
    }

    public Object seek(int id, int to){
        current.device[id].Seek(current.devicesAccessor[id], to);
        return null;
    }

    public byte[] read(int id, int count){
        return current.device[id].Read(current.devicesAccessor[id], count);
    }

    public int write(int id, byte[] data){
        return current.device[id].Write(current.devicesAccessor[id], data);
    }

    public int getMemory(int size){ //return a "pointer" to the allocated memory
        size /= 1024;
        for(int i = 0; i < memoryMap.size() - size; i++)
            if(memoryMap.get(i, i + size).isEmpty()){
                memoryMap.set(i, i + size, true);
                return i;
            }        
        return -1;
    }

    public void freeMemory(int location, int size){
        size /= 1024;
        memoryMap.set(location, location + size, false);
    }

    public Object printMemoryMap(){
        System.out.print("  ");
        for(int i = 0; i < 32; i++)
            System.out.print(String.format("%x", i%16));
        System.out.print("\n0 ");
        for(int i = 0; i < memoryMap.size(); i++){
            System.out.print(memoryMap.get(i) ? "1" : "0");
            if((i + 1) % 32 == 0)
                System.out.print("\n" + String.format("%x", (i + 1)/32%16) + " ");
        }
        System.out.print("\b\b");
        return null;
    }

    public Boolean allocateProgramMemory(int pid, int ammount){
        for(int i = 0; i < activePCBS.size(); i++){ //linear search for process with right PID
            PCB proc = activePCBS.get(i);
            if(proc.pid == pid){
                int basePtr = getMemory(ammount);
                if(basePtr == -1)
                    return false;  //could not allocate memory
                proc.programBase = basePtr;
                proc.programLimit = ammount;
                break;
            }
        }
        return false;  //could not find pid
    }

    public Boolean allocateStackMemory(int pid, int ammount){
        for(int i = 0; i < activePCBS.size(); i++){ //linear search for process with right PID
            PCB proc = activePCBS.get(i);
            if(proc.pid == pid){
                int basePtr = getMemory(ammount);
                if(basePtr == -1)
                    return false;  //could not allocate memory
                proc.stackBase = basePtr;
                proc.stackLimit = ammount;
                return true;
            }
        }
        return false;  //could not find pid
    }

    public Boolean allocateHeapMemory(int pid, int ammount){
        for(int i = 0; i < activePCBS.size(); i++){ //linear search for process with right PID
            PCB proc = activePCBS.get(i);
            if(proc.pid == pid){
                int basePtr = getMemory(ammount);
                if(basePtr == -1)
                    return false;  //could not allocate memory
                proc.heapBase = basePtr;
                proc.heapLimit = ammount;
                break;
            }
        }
        return false;  //could not find pid
    }

    public Object freeProgramMemory(int pid){
        for(int i = 0; i < activePCBS.size(); i++){ //linear search for process with right PID
            PCB proc = activePCBS.get(i);
            if(proc.pid == pid){
                freeMemory(proc.programBase, proc.programLimit);
                break;
            }
        }
        return null;
    }

    public Object freeStackMemory(int pid){
        for(int i = 0; i < activePCBS.size(); i++){ //linear search for process with right PID
            PCB proc = activePCBS.get(i);
            if(proc.pid == pid){
                freeMemory(proc.stackBase, proc.stackLimit);
                break;
            }
        }
        return null;
    }

    public Object freeHeapMemory(int pid){
        for(int i = 0; i < activePCBS.size(); i++){ //linear search for process with right PID
            PCB proc = activePCBS.get(i);
            if(proc.pid == pid){
                freeMemory(proc.heapBase, proc.heapLimit);
                break;
            }
        }
        return null;
    }
}
