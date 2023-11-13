import java.util.LinkedList;

public class IfNode extends StatementNode {

    private Node Condition;
    private LinkedList<StatementNode> Statement;
    private Node Else;

    // Constructor
    public IfNode(Node condition, LinkedList<StatementNode> statement, Node elseNode) {
        Condition = condition;
        Statement = statement;
        Else = elseNode;
    }
    public IfNode(Node condition, LinkedList<StatementNode> statement) {
        Condition = condition;
        Statement = statement;
    }


    // Getter methods
    public Node getCondition() {
        return Condition;
    }

    public LinkedList<StatementNode> getStatement() {
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
            return "IfNode{" + "Condition=" + Condition + ", Statement=" + Statement + ", ElseIf=" + Else + '}';
        else 
            return "IfNode{" + "Condition=" + Condition + ", Statement=" + Statement + ", Else= " + Else + '}';

   
    }
}
