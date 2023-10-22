import java.util.Optional;

public class ForNode extends StatementNode {

    private Optional<Node> Itialization;
    private Optional<Node> Condition;
    private Optional<Node> Iteration;
    private Optional<Node> Statement;
    private Node IterationArray;
    
     
    
    // Constructor
    public ForNode(Optional<Node> Itialization, Optional<Node> Condition, Optional<Node> Iteration, Optional<Node> Statement) {
        this.Itialization = Itialization;
        this.Condition = Condition;
        this.Iteration = Iteration;
        this.Statement = Statement;
    }

    public ForNode(Node IterationArray) {
           
            this.IterationArray = IterationArray;
        }

    // Getter methods
    public Optional<Node> getAssignment() {
        return Itialization;
    }

    public Optional<Node> getCondition() {
        return Condition;
    }

    public Optional<Node> getIteration() {
        return Iteration;
    }

    public Optional<Node> getStatement() {
        return Statement;
    }

    
    public Node getIterationArray() {
            return IterationArray;
        }

    // toString method
    @Override
    public String toString() {
        if(this.IterationArray == null)
            return "ForNode{" + " Itialization= " + Itialization + ", Condition= " + Condition + ", Iteration= " + Iteration + ", Statement= " + Statement + '}';
        else
             return "ForInNode{" + ", IterationArray=" + IterationArray + '}';
    
    }
}

