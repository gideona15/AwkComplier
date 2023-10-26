import java.util.LinkedList;

public class FunctionCallNode extends StatementNode {
    private String functionName;
    private LinkedList<Node> parameters;

    // Constructor to initialize the function name and parameters
    public FunctionCallNode(String functionName, LinkedList<Node> parameters) {
        this.functionName = functionName;
        this.parameters = parameters;
    }

    // Getter method to retrieve the function name
    public String getFunctionName() {
        return functionName;
    }

    // Getter method to retrieve the list of parameters
    public LinkedList<Node> getParameters() {
        return parameters;
    }

    // Override the toString method to provide a custom string representation
    @Override
    public String toString() {
        // Construct the string representation in the format "functionName(parameters)"
        // Note that parameters is a LinkedList, so we rely on its default toString()
        return "FunctionName: "+functionName + "(" + parameters + ")";
    }
}
