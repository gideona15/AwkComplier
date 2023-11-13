import java.util.LinkedList;
import java.util.Optional;

public class BlockNode extends StatementNode {
    private LinkedList<StatementNode> snode = new LinkedList<>();
    private Optional<Node> condition ;

    // Constructor
    public BlockNode() {
        // You can initialize any default values or perform other setup here if needed.
    }

    // Setter methods
    public void setSnode(LinkedList<StatementNode> snode) {
        this.snode = snode;
    }

    public void setCondition(Optional<Node> condition) {
        this.condition = condition;
    }

    // Getter methods
    public LinkedList<StatementNode> getSnode() {
        return snode;
    }


    public Optional<Node> getCondition() {
        return condition;
    }

    // toString method
    @Override
    public String toString() {
        if(condition != null)
           return "BlockNode {" + "Condition= " + condition + "StatementNodes= " + snode + '}';
        else 
           return "BlockNode {" +  "StatementNodes= " + snode + '}';
    }
}
