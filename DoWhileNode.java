public class DoWhileNode extends StatementNode {
private Node condition;
    private Node statement;

    // Constructor
    public DoWhileNode(Node condition, Node statement) {
        this.condition = condition;
        this.statement = statement;
    }

    // Getter methods
    public Node getCondition() {
        return condition;
    }

    public Node getStatement() {
        return statement;
    }

    // toString method
    @Override
    public String toString() {
        

        return "DoNode{" +  "Condition= " + this.condition +", Statement= " + this.statement +'}';
    }
}