public class TernaryNode extends StatementNode {
    

     private Node booleanNode;
     private Node TrueCase;
     private Node FalseCase;


    public TernaryNode(Node boolNode, Node caseOne, Node caseTwo){
        this.booleanNode = boolNode;
        this.TrueCase = caseOne;
        this.FalseCase = caseTwo;
    }
    public Node getBoolNode(){
        return booleanNode;
    }
    public Node getTrueCase(){
        return TrueCase;
    }
    public Node getFalseNode(){
        return FalseCase;
    }

    public String toString() {
        return "TernaryNode{" +
                "booleanNode=" + booleanNode +
                ", TrueCase=" + TrueCase +
                ", FalseCase=" + FalseCase +
                '}';
    }
    
}
