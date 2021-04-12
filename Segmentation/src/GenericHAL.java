
//alot of methods in here were static, they shouldn't be.

public class GenericHAL {

    public final int RegisterSize = 64; // How big our register storage is
    public Kernel kernel; // the kernel we are supporting
    public int programBase, stackBase, heapBase;    //represent the hardware segemnt registers 
    public int programLimit, stackLimit, heapLimit;

    public GenericHAL(Kernel k) { // HAL knows kernel; kernel knows HAL
        kernel = k;
    }

    public void StoreProgramData(byte [] data) {} // Store registers to PCB. Does nothing in Java
    public void RestoreProgramData(byte [] data) {} // Loads registers from PCB. IBID

    public int getMemorySize(){ //return the size of memory in bytes
        return 0b1<<20;   //2^20
    }

    public void AccessProgram(int address){
        if(!(programBase < address && address < programBase + programLimit)){
            kernel.Print("Segfault");
            kernel.Exit();
        }
    }

    public void AccessStack(int address){
        if(!(stackBase < address && address < stackBase + stackLimit)){
            kernel.Print("Segfault");
            kernel.Exit();
        }
    }

    public void AccessHeap(int address){
        if(!(heapBase < address && address < heapBase + heapLimit)){
            kernel.Print("Segfault");
            kernel.Exit();
        }
    }

}
