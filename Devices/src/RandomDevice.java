

import java.util.Random;

public class RandomDevice implements Device{

    //Singleton static member
    private static RandomDevice instance = new RandomDevice();
    private Random[] openInstances = new Random[10];

    //Singleton Accessor
    public static RandomDevice getInstance(){
        return instance;
    }

    //Force private constructor for singleton
    private RandomDevice(){}


    public int Open(String s){
        long seed = 0;
        try{
            seed = Long.parseLong(s);
        }catch(NumberFormatException e){
            seed = 0;
        }
        int id = 0;
        for(;openInstances[id] != null; id++); //Search for non-null device id
        openInstances[id] = new Random(seed);
        return id;
    }

    public void Close(int id){
        openInstances[id] = null;
    }

    public byte[] Read(int id,int size){
        byte[] rv = new byte[size];
        openInstances[id].nextBytes(rv);
        return rv;
    }

    public void Seek(int id,int to){
        byte[] rv = new byte[to];
        openInstances[id].nextBytes(rv);
    }

    public int Write(int id, byte[] data){
        return 0;
    }
}
