import java.util.HashMap;

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

    public void setValue(String key, InterpreterDataType value) {
        array.put(key, value);
    }
}
