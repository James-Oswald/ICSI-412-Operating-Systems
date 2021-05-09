
import java.io.RandomAccessFile;

public class FileSystem implements Device{

    //Singleton static member
    private static FileSystem instance = new FileSystem();
    private RandomAccessFile[] openInstances = new RandomAccessFile[10];


    //Singleton Accessor
    public static FileSystem getInstance(){
        return instance;
    }

    //Force private constructor for singleton
    private FileSystem(){}

    //An interesting issue with all of these overloaded Device methods is that they cant throw since the device
    //interface doesn't allow them to. To handle this problem we catch and exit inside of them.
    public int Open(String s){
        int id = 0;
        try{
            if(s == null || s.equals(""))
                throw new Exception("Invlid File Name");
            for(;openInstances[id] != null; id++); //Search for non-null device id
            openInstances[id] = new RandomAccessFile(s, "rw");
        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return id;
    }

    public void Close(int id){
        try{
            openInstances[id].close();
        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public byte[] Read(int id,int size){
        byte[] rv = new byte[size];
        try{
            openInstances[id].read(rv);
        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return rv;
    }

    public void Seek(int id, int to){
        try{
            openInstances[id].seek(to);
        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public int Write(int id, byte[] data){
        try{
            openInstances[id].write(data);
            openInstances[id].seek(0); //We need to go back to the start
        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return data.length;
    }
}
