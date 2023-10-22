public class ForInNode {
    private Node value;
    private Node IterationArray;

    public ForInNode(Node value, Node IterationArray) {
        this.value = value;
        this.IterationArray = IterationArray;
    }

    public Node getValue() {
        return value;
    }

    public Node getIterationArray() {
        return IterationArray;
    }

    @Override
    public String toString() {
        return "ForInNode{" +
                "value=" + value +
                ", IterationArray=" + IterationArray +
                '}';
    }
}

