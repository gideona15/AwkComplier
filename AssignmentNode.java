
public class AssignmentNode extends StatementNode {
    
   private Node target;
   private Node expression;
   private OperationNode.Operation op;

   public AssignmentNode(Node target, Node expression, OperationNode.Operation op){

    this.target = target;
    this.expression = expression;
    this.op = op;
   }

   public Node getTargert(){
    return target;
   }
   public Node getExpression(){
    return expression;
   }
   public OperationNode.Operation getOperattion(){
    return op;
   }

   public String toString(){

    return op.toString()+" "+ target +" => "+ expression;
   
}

}

