import java.util.HashMap;
import java.util.List;

public class InterpreterArrayDataType extends InterpreterDataType {
    
    private HashMap<String, InterpreterDataType> array;

    public InterpreterArrayDataType() {
        this.array = new HashMap<>();
    }

    public HashMap<String, InterpreterDataType> getArray() {
        return array;
    }

    public void setArray(HashMap<String, InterpreterDataType> array) {
        this.array = array;
    }

    public InterpreterDataType getValue(String key) {
        return array.get(key);
    }
    public Object getkeys() {
        return array.values();
    }

    public void setValue(String key, InterpreterDataType value) {
        array.put(key, value);
    }
     public List<InterpreterDataType> getValues() {
        return (List<InterpreterDataType>) array.values();
    }
    @Override
    public String toString(){
        return array.toString();
    }
}
