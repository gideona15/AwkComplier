import java.util.LinkedList;

public class FunctionDefinitionNode extends Node {
    
    // Stores the name of the function
    private String functionname;
    
    // Stores the list of parameters as tokens
    private LinkedList<Token> Parameters;
    
    // Stores the list of statement nodes representing the function's body
    private LinkedList<StatementNode> StateNode;
     
    // Constructor to initialize the FunctionDefinitionNode
    public FunctionDefinitionNode() {
        
    }
    public FunctionDefinitionNode(String name, LinkedList<Token> Parametersinput, LinkedList<StatementNode> Statementinput) {
        this.functionname = name;
        this.Parameters = Parametersinput;
        this.StateNode = Statementinput;
    }
    
    // Converts the list of parameter tokens to a list of their values as strings
    private LinkedList<String> TokenConverter() {
        LinkedList<String> convert = new LinkedList<>();

        for (int i = 0; i < Parameters.size(); i++) {
            convert.add(Parameters.get(i).getValue());
        }
        
        return convert;
    }
    public String getName(){
        return functionname;
    }

    // Returns a string representation of the FunctionDefinitionNode
    public String toString() {
        return "Function name: " + functionname + ", Parameters: " + TokenConverter() + ", Statements => " + StateNode;
    }
}
