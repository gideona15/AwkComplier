public class InterpreterDataType {
    private String value;

    public InterpreterDataType() {
        this.value = "";
    }

    public InterpreterDataType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public String toString(){

        return value;
    }
}