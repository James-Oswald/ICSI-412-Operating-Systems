import java.util.Arrays;

public class Pipe implements Device{

    private class PipeData{
        public String name;
        public int bufferIndex = 0; //We use this for seek so we can skip over bytes
        public byte[] buffer = null;
        public int connections = 0;

        public PipeData(String name){
            this.name = name;
        }
    }

    //Singleton static member
    private static Pipe instance = new Pipe();
    private PipeData[] openInstances = new PipeData[10];


    //Singleton Accessor
    public static Pipe getInstance(){
        return instance;
    }

    //Force private constructor for singleton
    private Pipe(){}

    public int Open(String s){
        //Search to see if pipe already exists
        for(int id = 0; id < openInstances.length; id++){
            if(openInstances[id] != null && openInstances[id].name.equals(s)){
                openInstances[id].connections++;
                return id;
            }
        }
        //Pipe does not exist, create new pipe
        int id = 0;
        for(;openInstances[id] != null; id++); //Search for non-null device id
        openInstances[id] = new PipeData(s);
        return id;
    }

    public void Close(int id){
        if(openInstances[id] != null){ //this is null in the case that we already closed the pipe in another thread
            openInstances[id].connections--;
            if(openInstances[id].connections == 0)
                openInstances[id] = null;
        }
    }

    public byte[] Read(int id,int size){
        PipeData pipe = openInstances[id];
        byte[] rv = Arrays.copyOfRange(pipe.buffer, pipe.bufferIndex, pipe.bufferIndex + size);
        pipe.bufferIndex += size;
        return rv;
    }

    public void Seek(int id, int to){
        openInstances[id].bufferIndex += to;
    }

    public int Write(int id, byte[] data){
        openInstances[id].buffer = data.clone();
        return openInstances[id].buffer.length;
    }
}
