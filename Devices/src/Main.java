import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        //Test 1
        KernelBindings.Print("Test 1: Random Device Testing");
        int pid1 = (int)KernelBindings.CreateProcess();
        int randDescriptor = KernelBindings.open("random 101"); 
        byte[] randomBytes = KernelBindings.read(randDescriptor, 10);
        KernelBindings.Print("Random Returns " + Arrays.toString(randomBytes));
        KernelBindings.seek(randDescriptor, 20);
        randomBytes = KernelBindings.read(randDescriptor, 10);
        KernelBindings.Print("After Seek Random Returns " + Arrays.toString(randomBytes));
        KernelBindings.write(randDescriptor, randomBytes); //Write should do nothing and cause no errors
        KernelBindings.close(randDescriptor);

        //Test 2
        KernelBindings.Print("\n\nTest 2: Pipe Testing");
        int pipeDescriptor = KernelBindings.open("pipe DasPipe");
        int numWrote = KernelBindings.write(pipeDescriptor, "Hello World! This is Assignment 5.".getBytes());
        KernelBindings.Print("Wrote " + numWrote + " bytes to the pipe");
        byte[] pipeData = KernelBindings.read(pipeDescriptor, 5); 
        KernelBindings.Print("Read " + pipeData.length + " bytes from pipe: " +
            new String(pipeData, StandardCharsets.UTF_8)); //Should read "Hello"
        KernelBindings.seek(pipeDescriptor, 8); //Jump over "World! "
        pipeData = KernelBindings.read(pipeDescriptor, 21); 
        KernelBindings.Print("Read " + pipeData.length + " bytes from pipe: " +
            new String(pipeData, StandardCharsets.UTF_8)); //Should read "This is Assignment 5"
        KernelBindings.close(pipeDescriptor);

        //Test 3
        KernelBindings.Print("\n\nTest 3: File Testing");
        int fileDescriptor = KernelBindings.open("file TheFile");
        numWrote = KernelBindings.write(fileDescriptor, "Hello World! This is Assignment 5.".getBytes());
        KernelBindings.Print("Wrote " + numWrote + " bytes to the file");
        byte[] fileData = KernelBindings.read(fileDescriptor, 5); 
        KernelBindings.Print("Read " + fileData.length + " bytes from file: " +
            new String(fileData, StandardCharsets.UTF_8)); //Should read "Hello"
        KernelBindings.seek(fileDescriptor, 13); //Seek for RandAccessFile jumps to this index
        fileData = KernelBindings.read(fileDescriptor, 21); 
        KernelBindings.Print("Read " + fileData.length + " bytes from file: " +
            new String(fileData, StandardCharsets.UTF_8)); //Should read "This is Assignment 5"
        KernelBindings.close(fileDescriptor);

        //Test 4
        KernelBindings.Print("\n\nTest 4: Multi-Process Multi-Device Access");
        int pid2 = (int)KernelBindings.CreateProcess();
        int pid3 = (int)KernelBindings.CreateProcess();
        KernelBindings.Reschedule(pid2);
        int randDescriptor2 = KernelBindings.open("random 123456789");
        int pipeDescriptor2 = KernelBindings.open("pipe aPipe");
        byte[] randNums = KernelBindings.read(randDescriptor2, 10);
        int numWritten = KernelBindings.write(pipeDescriptor2, randNums);
        KernelBindings.Print("Rand generated " + Arrays.toString(randNums) + " wrte to pipe");
        KernelBindings.Reschedule(pid3);  //We now switch process and extract from pipe with same name
        int pipeDescriptor3 = KernelBindings.open("pipe aPipe"); 
        byte[] pipeOutput = KernelBindings.read(randDescriptor2, numWritten);
        KernelBindings.Print("Process 3 read from pipe: " + Arrays.toString(pipeOutput));
        KernelBindings.Print("This should match across processes, if it does, then devices work properly");
        KernelBindings.close(pipeDescriptor3);
        KernelBindings.Reschedule(pid2);
        KernelBindings.close(randDescriptor2);
        KernelBindings.close(pipeDescriptor2);
    }
}
