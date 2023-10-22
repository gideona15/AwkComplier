public class ReturnNode extends StatementNode {

    private Node ReturnStatement;

    public ReturnNode(Node input){
     this.ReturnStatement = input;
    }
    public Node getStatement(){
        return this.ReturnStatement;
    }

    public String toString(){
        return "Return Statement :"+ ReturnStatement;
    }
    
}
