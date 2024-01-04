import java.util.Optional;

public class VariableReferenceNode extends StatementNode {
    private String name;
    private Optional<Node> expression = Optional.empty();

    // Constructor
    public VariableReferenceNode(String name, Optional<Node> expression) {
        this.name = name;
        this.expression = expression;
    }
    public String getName(){
        return name;
    }
    public Optional<Node> getExpression(){
        return expression;
    }
    public String getNameAndExperession(){
        if(expression.isPresent())
            return name + "["+expression.get()+"]";
        else 
             return name;
    }

    @Override
    public String toString() {
        return "VariableReferenceNode [Name=" + name + ", Expression=" + expression +"] ";
    }
}

