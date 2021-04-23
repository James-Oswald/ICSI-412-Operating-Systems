import java.util.ArrayList;
import java.util.HashMap;

//alot of methods in here were static, they shouldn't be.

public class GenericHAL{

    public static final int RegisterSize = 64; // How big our register storage is
    public static Kernel kernel; // the kernel we are supporting

    //Our programs 4 TLBs are stored here
    public static ArrayList<HashMap<Integer, Integer>> TLBs;
    public static int activeTLB; //The currently active TLB, we simulate having a TLB swap on allocation

    public GenericHAL(Kernel k){
        kernel = k;
        activeTLB = 0;
        TLBs = new ArrayList<HashMap<Integer, Integer>>();
        clearTLBs();
    }

    //This function resets all TLBs at startup and durring a conext switch
    public static void clearTLBs(){
        TLBs.clear();
        for(int i = 0; i < 4; i++) //we create 4 TLBs
            TLBs.add(new HashMap<Integer, Integer>());
    }

    public static void StoreProgramData(byte [] data){} // Store registers to PCB. Does nothing in Java
    public static void RestoreProgramData(byte [] data){} // Loads registers from PCB. IBID

    public static int getMemorySize(){ //return the size of memory in bytes
        return 0b1<<20;   //2^20
    }

    public static void AccessMemory(int virtualAddress){
        int virtualBlockNumber = virtualAddress/1024; //TLBs map blocks to blocks, thus we do integer division to find the virtual block
        for(int i = 0; i < TLBs.size(); i++){
            Integer realBlockNumber = TLBs.get(i).get(virtualBlockNumber);
            if(realBlockNumber != null){
                kernel.Print("Memory accessed via TLB, Block #" + realBlockNumber); //Testing message
                return; //We have found our true memory address within one of our TLBs, memory has been accessed
            }
        }
        int realAddress = kernel.getMapping(kernel.current.pid, virtualAddress);
        if(realAddress == -1){  //A page fault occured, the message was printed in kernal
            return;
        }
        int realBlockNumber = realAddress/1024;
        kernel.Print("Memory not in TLB, written to TLB, Block #" + realBlockNumber); //Testing message
        TLBs.get(activeTLB).put(virtualBlockNumber, realBlockNumber); //We put our block into one of the TLBs
        activeTLB = (activeTLB + 1) % TLBs.size();  //to simulate multiple Available TLBs we swap the available TLB
    }
}
