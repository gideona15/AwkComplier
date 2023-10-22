public class DeleteNode extends StatementNode {
    private Node assignment;

    // Constructor
    public DeleteNode(Node assignment) {
        this.assignment = assignment;
    }

    // Getter method
    public Node getAssignment() {
        return assignment;
    }

    // toString method
    @Override
    public String toString() {
        return "DeleteNode{" + assignment + '}';
    }
}