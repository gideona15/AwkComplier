public class ConstantNode extends StatementNode  {

    public static final String getValuObject = null;
    private Object value;

    public ConstantNode(Object input){

        this.value = input;
      
    }
    public Object getValuObject(){
        return value;
    }
    @Override
    public String toString(){
        return value.toString();
    }
    
}
