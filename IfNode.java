public class IfNode extends StatementNode {

    private Node Condition;
    private Node Statement;
    private Node Else;

    // Constructor
    public IfNode(Node condition, Node statement, Node elseNode) {
        Condition = condition;
        Statement = statement;
        Else = elseNode;
    }
    public IfNode(Node condition, Node statement) {
        Condition = condition;
        Statement = statement;
    }


    // Getter methods
    public Node getCondition() {
        return Condition;
    }

    public Node getStatement() {
        return Statement;
    }

    public Node getElse() {
        return Else;
    }

    // toString method
    @Override
    public String toString() {

        if(Else == null)
            return "IfNode{" + "Condition=" + Condition + ", Statement=" + Statement + '}'; 
        
        if(Else instanceof IfNode)
            return "IfNode{" + "Condition=" + Condition + ", Statement=" + Statement + ", Else=" + Else + '}';
        else 
            return "IfNode{" + "Condition=" + Condition + ", Statement=" + Statement + ", " + Else + '}';

   
    }
}
