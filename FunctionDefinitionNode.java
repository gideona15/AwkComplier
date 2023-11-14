import java.util.LinkedList;

public class FunctionDefinitionNode extends Node {
    
    // Stores the name of the function
    private String functionname;
    
    // Stores the list of parameters as tokens
    private LinkedList<Node> Parameters;
    
    // Stores the list of statement nodes representing the function's body
    private LinkedList<StatementNode> StateNode;
     
    // Constructor to initialize the FunctionDefinitionNode
    public FunctionDefinitionNode() {
        
    }
    public FunctionDefinitionNode(String name, LinkedList<Node> Parametersinput, LinkedList<StatementNode> Statementinput) {
        this.functionname = name;
        this.Parameters = Parametersinput;
        this.StateNode = Statementinput;
    }
  
    public String getName(){
        return functionname;
    }
    public LinkedList<Node> getParameters(){
        return Parameters;
    }
    public LinkedList<StatementNode> getStatementNodes(){
        return StateNode;
    }

    // Returns a string representation of the FunctionDefinitionNode
    public String toString() {
        return "Function name: " + functionname + ", Parameters: " + Parameters + ", Statements => " + StateNode;
    }
}
