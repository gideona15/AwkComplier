import java.util.Optional;

public class VariableReferenceNode extends StatementNode {
    private String name;
    private Optional<Node> expression;

    // Constructor
    public VariableReferenceNode(String name, Optional<Node> expression) {
        this.name = name;
        this.expression = expression;
    }
    public String getName(){
        return name;
    }
    public Optional<Node> getExperssion(){
        return expression;
    }
    public String getNameAndExperssion(){
        return name + "["+expression+"]";
    }

    @Override
    public String toString() {
        return "VariableReferenceNode [Name=" + name + ", Expression=" + expression +"] ";
    }
}

