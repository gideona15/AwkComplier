import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

    public class Interpreter {


    protected HashMap<String, InterpreterDataType> globalVariables = null;
    protected HashMap<String, FunctionDefinitionNode> functionDefinitions = null; 
    private LineManager lineManagement;

    public  Interpreter(ProgramNode input, Path file) throws IOException {

        globalVariables = new HashMap<>();
        functionDefinitions = new HashMap<>();
        lineManagement = startLineManager(file);
        addFunctionDefinition(input);
        addBuiltInFunctions();
        while(lineManagement.SplitAndAssign());

    }

     class LineManager {

        private List<String> inputLines;
        private int currentIndex;
       
        public LineManager(List<String> inputLines){
            this.inputLines = inputLines;
            this.currentIndex = 0;
            addfile();
           }

        private  boolean SplitAndAssign() {
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

            for (int i = 1; i <= fields.length; i++) {
                String variableName = "$" + i;

                globalVariables.put(variableName, new InterpreterDataType(fields[i-1]));
            }

            // Set NF (Number of Fields)
            if(globalVariables.containsKey("NF"))
               globalVariables.put("NF", new InterpreterDataType(Integer.toString(fields.length + Integer.parseInt(getGlobalVariable("NF").getValue()))));
            else
                 globalVariables.put("NF", new InterpreterDataType(Integer.toString(fields.length)));

                
            globalVariables.put("NR", new InterpreterDataType(Integer.toString(currentIndex + 1)));
            globalVariables.put("FNR", new InterpreterDataType(Integer.toString(currentIndex + 1)));
            globalVariables.put("OFMT", new InterpreterDataType("%.6g"));
            globalVariables.put("OFS", new InterpreterDataType(" "));
            globalVariables.put("ORS", new InterpreterDataType("\n"));

            currentIndex++;
            return true;
        }
        private  void addfile(){

        String lines = "";
            for(String line: inputLines)
               lines += "\n" + line;

         globalVariables.put("$0", new InterpreterDataType(lines));

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
    
    private LineManager startLineManager(Path file) throws IOException{

        if(file != null)
            return new LineManager(Files.readAllLines(file));
        else 
            return new LineManager(new LinkedList<String>());     
    }
    private  void addBuiltInFunctions(){

         HashMap<String, BuiltInFunctionDefinitionNode> operations = new HashMap<>();
      
        BuiltInFunctionDefinitionNode printfBuilt = new BuiltInFunctionDefinitionNode("printf",test -> builtInPrint(test), true);
        BuiltInFunctionDefinitionNode printBuilt = new BuiltInFunctionDefinitionNode("print",test -> builtInPrint(test), true);
        
        BuiltInFunctionDefinitionNode getLineBuilt = new BuiltInFunctionDefinitionNode("getline",test -> builtInGetline(test), true);
        BuiltInFunctionDefinitionNode nextbuilt= new BuiltInFunctionDefinitionNode("next",test -> builtInGetline(test), false);
       
        BuiltInFunctionDefinitionNode gsubBuilt = new BuiltInFunctionDefinitionNode("gsub",test -> builtInGsub(test), true);
        BuiltInFunctionDefinitionNode matchBuilt = new BuiltInFunctionDefinitionNode("match",test -> builtInMatch(test), true);
        BuiltInFunctionDefinitionNode subBuilt = new BuiltInFunctionDefinitionNode("sub",test -> builtISub(test), true);
 
        BuiltInFunctionDefinitionNode indexBuilt = new BuiltInFunctionDefinitionNode("index",test -> builtIndex(test), true);
        BuiltInFunctionDefinitionNode lengthBuilt = new BuiltInFunctionDefinitionNode("length",test -> builtInLength(test), true);
       
        BuiltInFunctionDefinitionNode splitBuilt = new BuiltInFunctionDefinitionNode("getline",test -> builtInSplit(test), true);
        BuiltInFunctionDefinitionNode substrBuilt = new BuiltInFunctionDefinitionNode("gsub",test -> builtInSubstr(test), true);
        BuiltInFunctionDefinitionNode tolowerBuilt = new BuiltInFunctionDefinitionNode("match",test -> builtInTolower(test), true);
        BuiltInFunctionDefinitionNode toupperBuilt = new BuiltInFunctionDefinitionNode("sub",test -> builtInToupper(test), true);
    

        //getline and next : these will call SplitAndAssign – we won’t do the other forms.
        operations.put("print", printBuilt);
        operations.put("printf", getLineBuilt);

        operations.put("getline", printfBuilt);
        operations.put("next", nextbuilt);

        operations.put("gub", gsubBuilt);
        operations.put("match", matchBuilt);
        operations.put("sub", subBuilt);

        operations.put("index", indexBuilt);
        operations.put("length", lengthBuilt);
        
        operations.put("split", splitBuilt);
        operations.put("substr", substrBuilt);
        operations.put("tolower", tolowerBuilt);
        operations.put("toupper", toupperBuilt);


       functionDefinitions.putAll(operations);

    }
    private  String builtInPrint(HashMap<String, InterpreterDataType> str){
        HashMap<String, InterpreterDataType> print = str;

        if(print.get("print") instanceof InterpreterArrayDataType){
            InterpreterArrayDataType printStream =  (InterpreterArrayDataType)print.get("print");
            System.out.printf("%d %d %d", printStream);
         }else
         System.out.printf("%d %d %d", str);

        return " ";
    }
    private  String builtInGetline(HashMap<String, InterpreterDataType> str){
        return Boolean.toString(this.lineManagement.SplitAndAssign());

    }  
    private  String builtInGsub(HashMap<String, InterpreterDataType> str) {
        str.get("gsub");
        String regexp,  replacement, input;
        
         return java.util.regex.Pattern.compile("regexp").matcher("input").replaceAll("replacement");
    }
    private  String builtInMatch(HashMap<String, InterpreterDataType> str) {

        String regexp, input;
        Pattern pattern = Pattern.compile("regexp");
        Matcher matcher = pattern.matcher("input");
       // java.util.regex.Pattern.compile(regexp).matcher(input).find();
        return Boolean.toString(matcher.find());
    }
    private  String builtISub(HashMap<String, InterpreterDataType> str) {

        String regexp, replacement,  input;
        Pattern pattern = Pattern.compile("regexp");
        Matcher matcher = pattern.matcher("input");
        String result = matcher.replaceFirst("replacement");
       //return java.util.regex.Pattern.compile(regexp).matcher(input).replaceFirst(replacement);
        return result;
    }
    private  String builtIndex(HashMap<String, InterpreterDataType> str){
        
        String input = (str.get("index").getValue());
        int index = input.indexOf(input);
        
        return Integer.toString(index);
    }
    private  String builtInLength(HashMap<String, InterpreterDataType> str){
        
        String input = (str.get("length").getValue());
        int index = input.length();
        return Integer.toString(index);
    }   
    private  String builtInSplit( HashMap<String, InterpreterDataType> str){
        //split(string, array, separator)
        String input = (str.get("split").getValue());
         
        String linput = "apple,banana,cherry";
        String[] fruits = input.split(",");

        return fruits.toString();
    } 
    private  String builtInSubstr(HashMap<String, InterpreterDataType> str){
       // substr(string, start, length)
        String input = (str.get("substr").getValue());

       // String linput = "Hello, World";
        String sub = input.substring(1, 2);
        
        return sub;
    }   
    private  String builtInTolower(HashMap<String, InterpreterDataType> str){
        String input = (str.get("tolower").getValue());

        String linput = "apple,banana,cherry";
        String lower = input.toLowerCase();

        return lower;
    } 
    private  String builtInToupper(HashMap<String, InterpreterDataType> str){
         String input = (str.get("toupper").getValue());

        String linput = "apple,banana,cherry";
        String upper = input.toUpperCase();

        return upper;
    }    
    // Define other interpreter methods and logic here
}

