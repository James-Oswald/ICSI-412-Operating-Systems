
public class Main {

    public static void main(String[] args) {
        int pid1 = (int)KernelBindings.CreateProcess();
        KernelBindings.Print("Test 1: Memory Allocation:");
        KernelBindings.allocateProgramMemory(pid1, 5*1024); 
        KernelBindings.allocateStackMemory(pid1, 10*1024);
        KernelBindings.allocateHeapMemory(pid1, 7*1024);
        KernelBindings.printMemoryMap();           //The first 22 bits in the memory map should be set
        

        KernelBindings.Print("\n\nTest 2: Memory Deallocation:");
        KernelBindings.freeProgramMemory(pid1); 
        KernelBindings.freeStackMemory(pid1);
        KernelBindings.freeHeapMemory(pid1);
        KernelBindings.printMemoryMap();            //no bits should be set
        KernelBindings.DeleteProcess(pid1);
        
        KernelBindings.Print("\n\nTest 3: Complex Allocation:");
        int pid2 = (int)KernelBindings.CreateProcess();
        int pid3 = (int)KernelBindings.CreateProcess();
        KernelBindings.allocateProgramMemory(pid2, 5*1024); //11111000000
        KernelBindings.allocateStackMemory(pid3, 2*1024);   //11111110000
        KernelBindings.freeProgramMemory(pid2);             //00000110000
        KernelBindings.allocateHeapMemory(pid3, 3*1024);    //11100110000
        KernelBindings.allocateProgramMemory(pid3, 4*1024); //11100111111
        KernelBindings.printMemoryMap();         //should see 11100111111
        KernelBindings.freeProgramMemory(pid3);
        KernelBindings.freeHeapMemory(pid3);
        KernelBindings.freeStackMemory(pid3);
        KernelBindings.DeleteProcess(pid2);
        KernelBindings.DeleteProcess(pid3);

        KernelBindings.Print("\n\nTest 4: Segfault Testing:");
        KernelBindings.printMemoryMap();
        int pid4 = (int)KernelBindings.CreateProcess();
        int pid5 = (int)KernelBindings.CreateProcess();
        
        KernelBindings.allocateProgramMemory(pid4, 5*1024); 
        KernelBindings.allocateProgramMemory(pid5, 5*1024);  
        KernelBindings.Reschedule(pid4);
        KernelBindings.testProgramAccess(2500); //No segfault;
        KernelBindings.testProgramAccess(7500); //Segfault;
        KernelBindings.Reschedule(pid5);
        KernelBindings.testProgramAccess(2500); //Segfault
        KernelBindings.testProgramAccess(7500); //No segfault;
        KernelBindings.DeleteProcess(pid4);
        KernelBindings.DeleteProcess(pid5);
    }
}
