import java.util.LinkedList;

public class ProgramNode extends Node {
    private LinkedList<BlockNode> begin = new LinkedList<>();
    private LinkedList<BlockNode> end = new LinkedList<>();
    private LinkedList<Node> other = new LinkedList<>();
    private LinkedList<FunctionDefinitionNode> fnode = new LinkedList<>();

    // Constructor
    public ProgramNode() {
        // You can initialize any default values or perform other setup here if needed.
    }

    // Setter methodss
    public void setBegin(LinkedList<BlockNode> begin) {
        this.begin = begin;
    }

    public void setEnd(LinkedList<BlockNode> end) {
        this.end = end;
    }

    public void setOther(LinkedList<Node> other) {
        this.other = other;
    }

    public void setFnode(LinkedList<FunctionDefinitionNode> fnode) {
        this.fnode = fnode;
    }

    // Getter methods
    public LinkedList<BlockNode> getBegin() {
        return begin;
    }

    public LinkedList<BlockNode> getEnd() {
        return end;
    }

    public LinkedList<Node> getOther() {
        return other;
    }

    public LinkedList<FunctionDefinitionNode> getFnode() {
        return fnode;
    }

    // toString method
    @Override
    public String toString() {
        return "ProgramNode{" + "Begin=" + begin + ", End=" + end + ", Other=" + other + ", Fnode=" + fnode + '}';
    }
}
