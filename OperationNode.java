import java.util.Optional;

public class OperationNode extends StatementNode {
    private Node leftExpression;
    private Optional<Node> rightExpression;
    private Operation op;

    // Enum defining the supported operations
    protected enum Operation {
        // Arithmetic and comparison operators
        NOT_EQUAL, GREATER_THAN, LESS_THAN, GREATER_THAN_EQUAL, LESS_THAN_EQUAL, FIELD_REFERENCE,
        
        // Conditional operators
        CONDITIONAL_QUESTION, CONDITIONAL_COLON,
        
        // Assignment operators
        ASSIGNMENT, DIVISION, EXPONENTIATION_ASSIGNMENT, MODULUS_ASSIGNMENT, PRE_INCREMENT, PRE_DECREMENT,
        MULTIPLICATION_ASSIGNMENT, LOGICAL_AND, MATCH, MODULUS, LOGICAL_EQUAL, LOGICAL_NOT, APPEND,
        NON_MATCH, LOGICAL_OR, SUBTRACTION_ASSIGNMENT, ADDITION_ASSIGNMENT, DIVISION_ASSIGNMENT,
        POST_DECREMENT,POST_INCREMENT,IN,Concatenation, 
        
        // Arithmetic operators
        ADDITION, SUBTRACTION, MULTIPLICATION, EXPONENT, BITWISE_OR, NUMBER, STRING_LITERAL,
    }

    // Constructor for binary operations (e.g., leftExpression + rightExpression)
    public OperationNode(Node leftExpression, Optional<Node> rightExpression, OperationNode.Operation op) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
        this.op = op;
    }
    
    // Constructor for unary operations (e.g., -leftExpression)
    public OperationNode(Node leftExpression, OperationNode.Operation op) {
        this.leftExpression = leftExpression;
        this.op = op;
    }

    public Node getLeftExpression() {
        return leftExpression;
    }


    public Optional<Node> getRightExpression() {
        return rightExpression;
    }


    public Operation getOp() {
        return op;
    }

    

    @Override
    public String toString() {
        if (this.rightExpression != null) {
            // Binary operation: includes left expression, operation, and right expression
            return "OperationNode [Left_Expression=" + leftExpression + ", Operation=" + op + ", Right_Expression=" + rightExpression.get() + "]";
        } else {
            // Unary operation: includes left expression and operation
            return "OperationNode [Left_Expression=" + leftExpression + ", Operation=" + op + "]";
        }
    }
}
