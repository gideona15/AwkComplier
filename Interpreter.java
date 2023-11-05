import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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
   
     private InterpreterDataType getInterpreterDataType(Node node) throws AwkException{

        HashMap<String, InterpreterDataType> localVariables = new HashMap<>();

            if(node instanceof AssignmentNode){
             AssignmentNode newNode = (AssignmentNode)node;
             
                if(!(newNode.getTargert() instanceof VariableReferenceNode) || !(newNode.getOperattion()== OperationNode.Operation.FIELD_REFERENCE))
                   throw new AwkException("This is not a vaild AssignmentNode");
                
                     var variable =(VariableReferenceNode)newNode.getTargert();
                   if(globalVariables.containsKey(variable.getString())){
                      getGlobalVariable(variable.getString()).setValue(getInterpreterDataType(newNode.getExpression()).toString());
                   }
                     else
                       globalVariables.put(variable.getString(), getInterpreterDataType(newNode.getExpression()));
                   return getInterpreterDataType(newNode.getExpression());
            }
            else if (node instanceof TernaryNode){
              TernaryNode newNode = (TernaryNode)node;

             var bool = getInterpreterDataType(newNode.getBoolNode());
    
                if(bool == null)
                   throw new AwkException("This is not a vaild TernaryNode, Boolean id null");

                if(!bool.toString().equals("false") || !bool.toString().equals("true"))
                   throw new AwkException("This is not a vaild TernaryNode, needs a vaild boolean experssion");
            
                    if(Boolean.parseBoolean(bool.toString())){
                        return getInterpreterDataType(newNode.getTrueCase());
                    }
                    else 
                        return getInterpreterDataType(newNode.getFalseNode());

            }
            else if (node instanceof VariableReferenceNode ){
                VariableReferenceNode newNode = (VariableReferenceNode)node;

                if(newNode.getExperssion().isEmpty()){
                    if(this.globalVariables.containsKey(newNode.toString()))
                       return getGlobalVariable(newNode.getString());
                }
                if(newNode.getExperssion().isEmpty()){
                    if(localVariables.containsKey(newNode.toString()))
                        return localVariables.get(newNode.getString());
                }
                else if(newNode.getExperssion().isPresent()){
                  var exp = getInterpreterDataType(newNode.getExperssion().get());

                    if(!(exp instanceof InterpreterDataType))
                         throw new AwkException("Not an Array DataType");

                    if(this.globalVariables.containsKey(newNode.getString()+"["+ exp.toString() +"]"))
                        return getGlobalVariable(newNode.getString()+"["+ exp.toString() +"]");
                   
                    if(localVariables.containsKey(newNode.getString()+"["+ exp.toString() +"]"))
                        return localVariables.get(newNode.getString()+"["+ exp.toString() +"]");
                
                }
            }
            else if (node instanceof OperationNode){
                OperationNode newNode = (OperationNode)node;
                var leftNode = newNode.getLeftExpression();
                var rightNode = newNode.getRightExpression();
                var operationNode = newNode.getOp();
               
                if(leftNode == null)
                   throw new AwkException("Left operation is null");
                


                switch(operationNode){

                    case ADDITION:
                        if(!rightNode.isPresent())
                            throw new AwkException("Right operation is not present");
                       
                        Float value = Float.parseFloat(getInterpreterDataType(leftNode).toString() + getInterpreterDataType(rightNode.get()).toString());
                        
                        return new InterpreterDataType(value.toString());


                }


                
            }

            else if(node instanceof FunctionCallNode){
              FunctionCallNode newNode = (FunctionCallNode)node;
             
                   return new InterpreterDataType(runFunctionCall(newNode, localVariables));
            }
            else if (node instanceof ConstantNode){
              ConstantNode newNode = (ConstantNode)node;
             
                   return new InterpreterDataType(newNode.toString());

            }
            else if (node instanceof PatternNode){
                    throw new AwkException("Cannot pass a pattern to a function or an assignment");

            }
            
            



        
        return null;
    }
    private String runFunctionCall(FunctionCallNode fNode,HashMap<String, InterpreterDataType> localVariables){

        return "";
    }

    private InterpreterDataType getArithmeticIDT(Node left, Optional<Node> right, OperationNode.Operation op ) throws AwkException{
         if(!right.isPresent())
                            throw new AwkException("Right operation is not present");
                       
       switch(op){

                    case ADDITION:  
                        Float Avalue = Float.parseFloat(getInterpreterDataType(left).toString()) + Float.parseFloat(getInterpreterDataType(right.get()).toString());
                               return new InterpreterDataType(Avalue.toString());
                            
                    case SUBTRACTION:  
                        Float Svalue = Float.parseFloat(getInterpreterDataType(left).toString()) - Float.parseFloat(getInterpreterDataType(right.get()).toString());
                               return new InterpreterDataType(Svalue.toString());

                    case MULTIPLICATION:  
                        Float Mvalue = Float.parseFloat(getInterpreterDataType(left).toString()) * Float.parseFloat(getInterpreterDataType(right.get()).toString());
                               return new InterpreterDataType(Mvalue.toString());
                            
                    case DIVISION:  
                        Float Dvalue = Float.parseFloat(getInterpreterDataType(left).toString()) / Float.parseFloat(getInterpreterDataType(right.get()).toString());
                               return new InterpreterDataType(Dvalue.toString());

                    case MODULUS:  
                        Float MODvalue = Float.parseFloat(getInterpreterDataType(left).toString()) % Float.parseFloat(getInterpreterDataType(right.get()).toString());
                               return new InterpreterDataType(MODvalue.toString());



                    case LOGICAL_EQUAL:  
                        Boolean EQUALvalue = Float.parseFloat(getInterpreterDataType(left).toString()) == Float.parseFloat(getInterpreterDataType(right.get()).toString());
                               return new InterpreterDataType(EQUALvalue.toString());
                            
                    case LOGICAL_NOT:  
                        Boolean NOTvalue = Float.parseFloat(getInterpreterDataType(left).toString()) != Float.parseFloat(getInterpreterDataType(right.get()).toString());
                               return new InterpreterDataType(NOTvalue.toString());

                    case LESS_THAN:  
                        Boolean LESS_THANvalue = Float.parseFloat(getInterpreterDataType(left).toString()) < Float.parseFloat(getInterpreterDataType(right.get()).toString());
                               return new InterpreterDataType(LESS_THANvalue.toString());
                            
                    case LESS_THAN_EQUAL:  
                        Boolean LESS_THAN_EQUALvalue = Float.parseFloat(getInterpreterDataType(left).toString()) <= Float.parseFloat(getInterpreterDataType(right.get()).toString());
                               return new InterpreterDataType(LESS_THAN_EQUALvalue.toString());

                    case GREATER_THAN:  
                        Boolean GREATER_THANvalue = Float.parseFloat(getInterpreterDataType(left).toString()) > Float.parseFloat(getInterpreterDataType(right.get()).toString());
                               return new InterpreterDataType(GREATER_THANvalue.toString());
                            
                    case GREATER_THAN_EQUAL:  
                        Boolean GREATER_THAN_EQUALvalue = Float.parseFloat(getInterpreterDataType(left).toString()) >= Float.parseFloat(getInterpreterDataType(right.get()).toString());
                               return new InterpreterDataType(GREATER_THAN_EQUALvalue.toString());
      

      
                    case LOGICAL_AND:  

                         Boolean Lvalue = Boolean.parseBoolean(getInterpreterDataType(left).toString());
                         Boolean Rvalue = Boolean.parseBoolean(getInterpreterDataType(right.get()).toString());

                        Boolean LOGICAL_ANDvalue = Lvalue && Rvalue;
                               return new InterpreterDataType(LOGICAL_ANDvalue.toString());

                    case LOGICAL_OR:  
                         Boolean LLvalue = Boolean.parseBoolean(getInterpreterDataType(left).toString());
                         Boolean RRvalue = Boolean.parseBoolean(getInterpreterDataType(right.get()).toString());

                        Boolean LOGICAL_ORvalue = LLvalue || RRvalue;
                               return new InterpreterDataType(LOGICAL_ORvalue.toString());
     
                    case NOT_EQUAL:  
                         Boolean LLLvalue = Boolean.parseBoolean(getInterpreterDataType(left).toString());
                         Boolean RRRvalue = Boolean.parseBoolean(getInterpreterDataType(right.get()).toString());

                        Boolean NOT_EQUALvalue = LLLvalue || RRRvalue;
                               return new InterpreterDataType(NOT_EQUALvalue.toString());


                    case MATCH:  
                         InterpreterDataType LvalueExp = getInterpreterDataType(left);

                         Boolean RRvalue = Boolean.parseBoolean(getInterpreterDataType(right.get()).toString());

                        Boolean LOGICAL_ORvalue = LLvalue || RRvalue;

                             java.util.regex.Pattern.compile("regexp").matcher("input");

                               return new InterpreterDataType(LOGICAL_ORvalue.toString());
     
                    case NON_MATCH:  
                         Boolean LLLvalue = Boolean.parseBoolean(getInterpreterDataType(left).toString());
                         Boolean RRRvalue = Boolean.parseBoolean(getInterpreterDataType(right.get()).toString());

                        Boolean NOT_EQUALvalue = LLLvalue || RRRvalue;
                               return new InterpreterDataType(NOT_EQUALvalue.toString());
     

                }

                return null;
    }
}
