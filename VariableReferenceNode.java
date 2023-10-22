import java.util.Optional;

public class VariableReferenceNode extends StatementNode {
    private String name;
    private Optional<Node> expression;

    // Constructor
    public VariableReferenceNode(String name, Optional<Node> expression) {
        this.name = name;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "VariableReferenceNode [Name=" + name + ", Expression=" + expression +"] ";
    }
}

