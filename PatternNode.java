public class PatternNode extends Node {
    private Object value;

    public PatternNode(Object input){
        this.value = input;
      
    }
    @Override
    public String toString(){
        return value.toString();
    }
    
}
