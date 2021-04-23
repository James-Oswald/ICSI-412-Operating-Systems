
public class Main {

    public static void main(String[] args) {

        //Test 1: Page allocation outside of our process
        int pid1 = (int)KernelBindings.CreateProcess();
        int pid2 = (int)KernelBindings.CreateProcess();
        KernelBindings.Print("Test 1: Page Allocation:");
        KernelBindings.allocateMemory(pid1, 5*1024); //Virtual 0-5k, real 0-5k belongs to pid1
        KernelBindings.allocateMemory(pid2, 5*1024); //Virtual 0-5k, real 5k - 10k belong to pid2
        KernelBindings.printMemoryMap();    //The first 5 bits in the memory map should be owned by pid1 (2), the next 4 by pid2 (3)

        //Test 2: TLBs testing
        KernelBindings.Print("\nTest 2: TLB testing: (see code comments for expected behavior)");
        KernelBindings.Reschedule(pid1);
        KernelBindings.AccessMemory(1500);  //Block 2 Not in TLB, get memory and write to TLB
        KernelBindings.AccessMemory(1600);  //Block 2 in TLB, access via TLB
        KernelBindings.Reschedule(pid2);    
        KernelBindings.AccessMemory(1500);  //should be Block 7 in real memory
        KernelBindings.Reschedule(pid1);    //Simulate switching processes, clear TLB
        KernelBindings.AccessMemory(1600);  //Block 2 no longer in TLB due to context swicth, get memory and write to TLB

        //Test 3: Free memory outside of our process testing
        KernelBindings.Print("\nTest 3: Free testing: (see code comments for expected behavior)");
        KernelBindings.Reschedule(pid2);
        KernelBindings.freeMemory(pid1);
        KernelBindings.printMemoryMap(); //pid2 (3) is the only process with memory left
        
        //Test 4: Page fault testing
        KernelBindings.Print("\nTest 4: Page fault testing: (see code comments for expected behavior)");
        KernelBindings.Reschedule(pid2);
        KernelBindings.AccessMemory(2*1024); //We own this in virtual, no pagefault
        //While pid2 owns this real address, as a virtual address pid2 only owns 0-5k so this pagefaults
        KernelBindings.AccessMemory(7*1024); 
        
        //Test 5: Delete process frees all memory
        KernelBindings.Print("\nTest 5: Delete process testing: (see code comments for expected behavior)");
        KernelBindings.DeleteProcess(pid1);
        KernelBindings.DeleteProcess(pid2);
        KernelBindings.printMemoryMap(); //no memory left allocated
    }
}
