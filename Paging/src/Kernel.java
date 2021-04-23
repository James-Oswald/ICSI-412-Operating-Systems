
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;

public class Kernel {
    public class PCB { // Process control block. Needs to be filled in.
        byte[] registerData = new byte[GenericHAL.RegisterSize];
        int pid;
        HashMap<Integer, Integer> virtualMemoryMap; 
        int allocatedBlocks;

        PCB() {
            pid = maxPid++;
            allocatedBlocks = 0;
            virtualMemoryMap = new HashMap<>();
        }
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
        memoryMap = new BitSet(GenericHAL.getMemorySize() / 1024);
    }

    public int CreateProcess() {
        PCB newOne = new PCB();
        activePCBS.add(newOne);
        return newOne.pid;
    }

    public Object DeleteProcess(int pid) {
        for (int i=0;i<activePCBS.size(); i++) {
            if (activePCBS.get(i).pid == pid) {
                freeMemory(pid);
                activePCBS.remove(i);
                break;
            }
        }

        if (activePCBS.size() == 0)
            System.exit(0); // We deleted the last of the process. We are finished.
        return null;
    }

    public Object Reschedule(int pid) {
        GenericHAL.StoreProgramData(current.registerData);
        GenericHAL.clearTLBs(); //Reset our TLBs for the new process
        current = null;
        for (PCB pcb : activePCBS) {
            if (pcb.pid == pid)
                current = pcb;
        }
        if (current == null)
        {
            current = activePCBS.get(0);
        }
        GenericHAL.RestoreProgramData(current.registerData);
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

    

    public Object printMemoryMap(){
        System.out.print("  ");
        for(int i = 0; i < 32; i++)
            System.out.print(String.format("%x", i%16));
        System.out.print("\n0 ");
        ArrayList<Integer> pidMemoryMap = new ArrayList<>(Collections.nCopies(memoryMap.size(), 0));
        for(PCB p : activePCBS) //write real addresses to map
            for(int realBlockIndex: p.virtualMemoryMap.values())
                pidMemoryMap.set(realBlockIndex, p.pid);
        for(int i = 0; i < pidMemoryMap.size(); i++){  //print the map
            System.out.print(pidMemoryMap.get(i));
            if((i + 1) % 32 == 0)
                System.out.print("\n" + String.format("%x", (i + 1)/32%16) + " ");
        }
        System.out.print("\b\b");
        return null;
    }

    //returns a pointer to real memory to the start of the alloacted region
    public int getBlocks(int size){ //return a "pointer" to the allocated memory
        size = size / 1024 + 1; //size is now the number of blocks to allocate
        for(int i = 0; i < memoryMap.size() - size; i++)
            if(memoryMap.get(i, i + size).isEmpty()){
                memoryMap.set(i, i + size, true);
                return i*1024;
            }        
        return -1;
    }

    //frees one or more consecutive real blocks
    public void freeBlock(int location, int size){
        size = size / 1024 + 1; //size is now the number of blocks to deallocate
        memoryMap.set(location, location + size, false);
    }

    public Boolean allocateMemory(int pid, int ammount){
        for(int i = 0; i < activePCBS.size(); i++){ //linear search for process with right PID
            PCB proc = activePCBS.get(i);
            if(proc.pid == pid){
                int basePtr = getBlocks(ammount); //This is our Real memory address
                if(basePtr == -1)   //could not allocate memory
                    return false;  
                int numBlocks = ammount / 1024 + 1; //The number of blocks we just alloacted
                for(int j = 0; j < numBlocks; j++){  //Now map the real address block to virtual blocks
                    proc.virtualMemoryMap.put(j, basePtr/1024 + j); //We map our base block to 0
                }
                break;
            }
        }
        return false;  //could not find pid
    }

    public Object freeMemory(int pid){
        for(int i = 0; i < activePCBS.size(); i++){ //linear search for process with right PID
            PCB proc = activePCBS.get(i);
            if(proc.pid == pid){
                for(int realBlockBase: proc.virtualMemoryMap.values()) //free all real memory using our virtual memory
                    freeBlock(realBlockBase, 1024); //dealocate individual blocks, block by block
                proc.virtualMemoryMap.clear();
                break;
            }
        }
        return null;
    }

    public int getMapping(int pid, int address){
        for(int i = 0; i < activePCBS.size(); i++){ //linear search for process with right PID
            PCB proc = activePCBS.get(i);
            if(proc.pid == pid){
                int addressRemainder = address % 1024;  
                int virtualBlockNumber = address/ 1024;
                Integer realBlockNumber = proc.virtualMemoryMap.get(virtualBlockNumber);
                if(realBlockNumber == null){ //The virtual block was never allocated
                    Print("Page fault: " + address + " did not map to a real allocated address");
                    Exit();
                    return -1;
                }
                int realAddress = realBlockNumber * 1024 + addressRemainder;   //compute the real address
                return realAddress;
            }
        }
        return -1;
    }
}
