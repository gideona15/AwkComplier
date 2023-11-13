import java.util.LinkedList;

public class WhileNode extends StatementNode {
    private Node condition;
    private LinkedList<StatementNode> statement;

    // Constructor
    public WhileNode(Node condition, LinkedList<StatementNode> statement) {
        this.condition = condition;
        this.statement = statement;
    }

    // Getter methods
    public Node getCondition() {
        return condition;
    }

    public LinkedList<StatementNode> getStatement() {
        return statement;
    }

    // toString method
    @Override
    public String toString() {
        

        return "WhileNode{" +  "Condition= " + this.condition +", Statement= " + this.statement +'}';
    }
}