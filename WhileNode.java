public class WhileNode extends StatementNode {
    private Node condition;
    private Node statement;

    // Constructor
    public WhileNode(Node condition, Node statement) {
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
        

        return "WhileNode{" +  "Condition= " + this.condition +", Statement= " + this.statement +'}';
    }
}