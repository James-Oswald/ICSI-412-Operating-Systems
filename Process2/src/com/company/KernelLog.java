
import java.io.FileWriter;
import java.util.Queue;

//This class logs kernal info at runtime

public class KernelLog{
    private static FileWriter logger = null;

    public static void newLog(String fileName){
        try{
            logger = new FileWriter(fileName, false);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void log(String logString){     
        if(logger == null)
            return;
        try{
            logger.write(logString + "\n");
            logger.flush();
            //System.out.println(logString);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void logQueue(Queue<Kernel.PCB> queue){ 
        String str = "";
        Kernel.PCB[] pcbs = queue.toArray(new Kernel.PCB[0]);      
        for(int i = 0; i < pcbs.length; i++)
            str += pcbs[i].pid + ", ";
        log(str);
    }
}
