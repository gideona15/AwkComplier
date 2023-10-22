
public class AssignmentNode extends StatementNode {
    
   private Node target;
   private Node expression;
   private OperationNode.Operation op;

   public AssignmentNode(Node target, Node expression, OperationNode.Operation op){

    this.target = target;
    this.expression = expression;
    this.op = op;
   }

   public String toString(){

    return op.toString()+" "+ target +" => "+ expression;
   
}
}

