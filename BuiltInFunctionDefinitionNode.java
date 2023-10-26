import java.util.HashMap;
import java.util.function.Function;

public class BuiltInFunctionDefinitionNode extends FunctionDefinitionNode {
    private Function<HashMap<String, InterpreterDataType>, String> execute;
    private boolean isVariadic;

    public BuiltInFunctionDefinitionNode(String name, Function<HashMap<String, InterpreterDataType>, String> execute, boolean isVariadic) {
        super(name, null, null);
        this.execute = execute;
        this.isVariadic = isVariadic;
    }

    public String execute(HashMap<String, InterpreterDataType> parameters) {
        return execute.apply(parameters);
    }

    public boolean isVariadic() {
        return isVariadic;
    }
}
