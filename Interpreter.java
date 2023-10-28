import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {
    private HashMap<String, InterpreterDataType> globalVariables = null;
    private HashMap<String, FunctionDefinitionNode> functionDefinitions = null;
    private LineManager lineManagement;

    public Interpreter(ProgramNode input, Path file) throws IOException {
        globalVariables = new HashMap<>();
        functionDefinitions = new HashMap<>();
        lineManagement = startLineManager(file);
        addFunctionDefinition(input);

    }

     class LineManager {
        private List<String> inputLines;
        private int currentIndex;
       
        public LineManager(List<String> inputLines){
            this.inputLines = inputLines;
            this.currentIndex = 0;
           }

        public boolean SplitAndAssign() {
            if (currentIndex >= inputLines.size()) {
                return false; // No more lines to process
            }

            String line = inputLines.get(currentIndex);
            String fieldSeparator;

            // Split the line using the field separator (FS)
            if(globalVariables.containsKey("FS"))
                fieldSeparator = globalVariables.get("FS").getValue(); 
                else
                  fieldSeparator = " "; // Default to space as field separator

        
            String[] fields = line.split(fieldSeparator);

            // Assign fields to $0, $1, $2, etc.
            for (int i = 0; i < fields.length; i++) {
                String variableName = "$" + i;
                globalVariables.put(variableName, new InterpreterDataType(fields[i]));
            }

            // Set NF (Number of Fields)
            globalVariables.put("NF", new InterpreterDataType(Integer.toString(fields.length)));
            globalVariables.put("NR", new InterpreterDataType(Integer.toString(currentIndex + 1)));
            globalVariables.put("FNR", new InterpreterDataType(Integer.toString(currentIndex + 1)));
            globalVariables.put("OFMT", new InterpreterDataType("%.6g"));
            globalVariables.put("OFS", new InterpreterDataType(" "));
            globalVariables.put("ORS", new InterpreterDataType("\n"));

            currentIndex++;
            return true;
        }
    }
    public void addGlobalVariable(String name, InterpreterDataType value) {
        globalVariables.put(name, value);
    }

    public InterpreterDataType getGlobalVariable(String name) {
        return globalVariables.get(name);
    }

    public void addFunctionDefinition(ProgramNode input) {

         for(FunctionDefinitionNode function : input.getFnode())
            functionDefinitions.put(function.getName(), function);

    }

    public FunctionDefinitionNode getFunctionDefinition(String name) {
        return functionDefinitions.get(name);
    }
    
    public LineManager startLineManager(Path file) throws IOException{

        if(file != null)
            return new LineManager(Files.readAllLines(file));
        else 
            return new LineManager(new LinkedList<String>());
            
    }
    
    public void addBuiltInFunctions(){

         HashMap<String, BuiltInFunctionDefinitionNode> operations = new HashMap<>();
      
        BuiltInFunctionDefinitionNode printfBuilt = new BuiltInFunctionDefinitionNode("printf",test -> builtInPrint(test), true);
        BuiltInFunctionDefinitionNode printBuilt = new BuiltInFunctionDefinitionNode("print",test -> builtInPrint(test), true);
        BuiltInFunctionDefinitionNode getLineBuilt = new BuiltInFunctionDefinitionNode("getline",test -> builtInGetline(test), true);
        BuiltInFunctionDefinitionNode Nextbuilt= new BuiltInFunctionDefinitionNode("next",test -> builtInGetline(test), false);
        BuiltInFunctionDefinitionNode getLineBuilt = new BuiltInFunctionDefinitionNode("getline",test -> builtInGetline(test), true);
        BuiltInFunctionDefinitionNode printBuilt = new BuiltInFunctionDefinitionNode("print",test -> builtInPrint(test), true);
        BuiltInFunctionDefinitionNode getLineBuilt = new BuiltInFunctionDefinitionNode("getline",test -> builtInGetline(test), true);
        
        //getline and next : these will call SplitAndAssign – we won’t do the other forms.
        operations.put("print", printBuilt);
        operations.put("getline", getLineBuilt);
        operations.put("printf", printBuilt);
        operations.put("next", getLineBuilt);
        operations.put("next", getLineBuilt);
        operations.put("next", getLineBuilt);

    }
    public String builtInPrint( HashMap<String, InterpreterDataType> str){
        System.out.printf("%d %d %d", str);
        return " ";
    }
     public String builtInGetline( HashMap<String, InterpreterDataType> str){
        return Boolean.toString(this.lineManagement.SplitAndAssign());

    }  
    public String builtInGsub(String regexp, String replacement, String input) {
         return java.util.regex.Pattern.compile(regexp).matcher(input).replaceAll(replacement);
    }
    public String builtInMatch(String regexp, String input) {
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(input);
       // java.util.regex.Pattern.compile(regexp).matcher(input).find();
        return Boolean.toString(matcher.find());
    }
    public String builtISub(String regexp, String replacement, String input) {
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(input);
        String result = matcher.replaceFirst(replacement);
       //return java.util.regex.Pattern.compile(regexp).matcher(input).replaceFirst(replacement);
        return result;
    }

    // Define other interpreter methods and logic here
}

