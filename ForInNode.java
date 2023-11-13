import java.util.LinkedList;

public class ForInNode extends StatementNode{
    private Node value;
    private Node IterationArray;
    private LinkedList<StatementNode> Statement;

    public ForInNode(Node value, Node IterationArray, LinkedList<StatementNode> Statement) {
        this.value = value;
        this.IterationArray = IterationArray;
        this.Statement = Statement;
    }

    public Node getValue() {
        return value;
    }

    public Node getIterationArray() {
        return IterationArray;
    }
    public LinkedList<StatementNode> getStatement(){
      return Statement;
    }

    @Override
    public String toString() {
        return "ForInNode{" +
                "value=" + value +
                ", IterationArray=" + IterationArray +
                ", Statement=" + Statement +
                '}';
    }
}

