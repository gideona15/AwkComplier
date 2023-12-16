import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Interpreter {
    // Declare a map to store global variables
    protected HashMap<String, InterpreterDataType> globalVariables = null;
    // Declare a map to store function definitions
    protected HashMap<String, FunctionDefinitionNode> functionDefinitions = null;
    // Declare a LineManager for managing input lines
    private LineManager lineManagement;

    // Constructor for the Interpreter class
    public Interpreter(ProgramNode input, Path file) throws IOException, NumberFormatException, AwkException {
        // Initialize the global variable map
        globalVariables = new HashMap<>();

        // Initialize the function definition map
        functionDefinitions = new HashMap<>();

        // Initialize the LineManager for processing input lines from a file
        lineManagement = startLineManager(file);

        // Add user-defined function definitions to the function map
        addFunctionDefinition(input);

        // Add built-in functions to the function map
        addBuiltInFunctions();

        // Continue processing input lines using the LineManager
        while (lineManagement.splitAndAssign());
          
    }


    class LineManager {
        // Store the input lines to be processed
        private List<String> inputLines = new ArrayList<>();
        // Keep track of the current line being processed
        private int currentIndex;
    
        // Constructor to initialize the LineManager with input lines
        public LineManager(List<String> inputLines) {
            this.inputLines = inputLines;
            this.currentIndex = 0;
            // Add the entire file content as $0 initially
            addFile();
        }
    
        // Process the next line, split it into fields, and assign variables
        private boolean splitAndAssign() {
            if (currentIndex >= inputLines.size()) {
                return false; // No more lines to process
            }
    
            // Get the current line
            String line = inputLines.get(currentIndex);
            String fieldSeparator;
    
            // Determine the field separator (FS)
            if (globalVariables.containsKey("FS")) {
                fieldSeparator = globalVariables.get("FS").getValue();
            } else {
                fieldSeparator = " "; // Default to space as the field separator
            }
    
            // Split the line using the field separator
            String[] fields = line.split(fieldSeparator);
            // Assign fields to $0, $1, $2, etc.
            for (int i = 1; i <= fields.length; i++) {
                String variableName = "$" + i;
                if (globalVariables.containsKey(variableName)) {
                    globalVariables.get(variableName).setValue(globalVariables.get(variableName).getValue() + " "+fields[i - 1]);
                }
                else 
                   globalVariables.put(variableName, new InterpreterDataType(fields[i - 1]));
            }
    
            // Set NF (Number of Fields)
            if (globalVariables.containsKey("NF")) {
                globalVariables.put("NF", new InterpreterDataType(Integer.toString(fields.length + Integer.parseInt(getGlobalVariable("NF").getValue()))));
            } else {
                globalVariables.put("NF", new InterpreterDataType(Integer.toString(fields.length)));
            }
    
            // Set other predefined variables like NR, FNR, OFMT, OFS, ORS, etc.
            globalVariables.put("NR", new InterpreterDataType(Integer.toString(currentIndex + 1)));
            globalVariables.put("FNR", new InterpreterDataType(Integer.toString(currentIndex + 1)));
            globalVariables.put("OFMT", new InterpreterDataType("%.6g"));
            globalVariables.put("OFS", new InterpreterDataType(" "));
            globalVariables.put("ORS", new InterpreterDataType("\n"));
    
            currentIndex++; // Move to the next line
            return true;
        }
    
        // Add the entire file content as $0
        private void addFile() {
            StringBuilder lines = new StringBuilder();
            for (String line : inputLines) {
                lines.append(line).append("\n");
            }
            globalVariables.put("$0", new InterpreterDataType(lines.toString()));
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
   
    public void InterpretProgram(ProgramNode program) throws AwkException {
        // Process 'BEGIN' blocks
        for (BlockNode block : program.getBegin()) {
            InterpretBlock(block);
            lineManagement.splitAndAssign();
        }
    
        // Process other blocks
        for (Node block : program.getOther()) {
            InterpretBlock((BlockNode) block);
            lineManagement.splitAndAssign();
        }
    
        // Process 'END' blocks
        for (BlockNode block : program.getEnd()) {
            InterpretBlock(block);
            lineManagement.splitAndAssign();
        }
    }
    private void InterpretBlock(BlockNode block) throws AwkException {
        // Check if the block is null
        if (block == null) {
            throw new AwkException("block is null");
        }
    
        // Check if the block has a condition
        if (block.getCondition().isPresent()) {
            // Evaluate the condition
            var condition = Boolean.parseBoolean(getInterpreterDataType(block.getCondition().get(), globalVariables).getValue());
    
            // If the condition is true, process the statements in the block
            if (condition) {
                for (StatementNode statement : block.getSnode()) {
                    ProcessStatement(new HashMap<>(), statement);
                }
            }
    
        } else {
            // If there is no condition, process all statements in the block
            for (StatementNode statement : block.getSnode()) {
                ProcessStatement(new HashMap<>(), statement);
            }
        }
    }
   
    private ReturnType InterpretListOfStatements(LinkedList<StatementNode> statements, HashMap<String, InterpreterDataType> locals) throws AwkException {

        ReturnType result = new ReturnType(ReturnType.Type.NONE);

        for (StatementNode statement : statements) {
            ReturnType statementResult = ProcessStatement(locals, statement);

            // Check the return type from each processStatement
            if (statementResult.getType() != ReturnType.Type.NONE) {
                // If it is not None, return passing "up" the same ReturnType
                result = statementResult;
                break;  // Exit the loop if a non-None result is encountered
            }
        }
        return result;
    }
    public ReturnType ProcessStatement(HashMap<String, InterpreterDataType> locals, StatementNode statement) throws AwkException {
        var node = statement;
    
        if (node instanceof DeleteNode) {
            // Process DeleteNode
            var delete = (DeleteNode) node;
            var interdelete = getInterpreterDataType(delete.getAssignment(), locals);
    
            // Remove the variable from either globalVariables or locals
            if (this.globalVariables.containsKey(interdelete.getValue()))
                this.globalVariables.remove(interdelete.getValue());

            else if (locals.containsKey(interdelete.getValue()))
                locals.remove(interdelete.getValue());
    
            return new ReturnType(ReturnType.Type.DELETE);

        } else if (node instanceof DoWhileNode) {
            // Process DoWhileNode
            DoWhileNode doWhile = (DoWhileNode) node;
            boolean cond = Boolean.parseBoolean(getInterpreterDataType(doWhile.getCondition(), locals).toString());
    
            ReturnType rtype;
            do {
                rtype = InterpretListOfStatements(doWhile.getStatement(), locals);
    
                // Check for BREAK or RETURN
                if (rtype.getType() == ReturnType.Type.BREAK)
                    break;
                if (rtype.getType() == ReturnType.Type.RETURN)
                    return rtype;
            } while (cond);

        } else if (node instanceof ForNode) {
            // Process ForNode
            ForNode fortype = (ForNode) node;
            ReturnType returntype = new ReturnType(ReturnType.Type.NONE);
            if (fortype.getItialization().isPresent()) {
                // Process initialization statement
                StatementNode iteration = (StatementNode) fortype.getItialization().get();

               ProcessStatement(locals, iteration);
            }
            while (Boolean.parseBoolean(getInterpreterDataType(fortype.getCondition().get(), locals).toString())) {
                // Process the loop statements
                returntype = InterpretListOfStatements(fortype.getStatement(), locals);
                ProcessStatement(locals, (StatementNode) fortype.getIteration().get());
    
                // Check for BREAK or RETURN
                if (returntype.getType() == ReturnType.Type.BREAK)
                    break;
                if (returntype.getType() == ReturnType.Type.RETURN)
                    return returntype;
            }
            return returntype;

        } else if (node instanceof ForInNode) {
            // Process ForInNode
            ForInNode forInType = (ForInNode) node;
            ReturnType returntype;
            HashMap<String, InterpreterDataType> foreach = new HashMap<String, InterpreterDataType>();
    
            if (globalVariables.containsKey(forInType.getIterationArray().toString())) {
                // Iterate over the array and execute the loop statements
                InterpreterArrayDataType array = (InterpreterArrayDataType) globalVariables
                        .get(forInType.getIterationArray().toString());
    
                for (InterpreterDataType type : array.getArray().values()) {
                    VariableReferenceNode variable = (VariableReferenceNode) forInType.getValue();
                    foreach.put(variable.getNameAndExperssion(), type);
                    returntype = InterpretListOfStatements(forInType.getStatement(), foreach);
                    foreach.clear();
    
                    // Check for BREAK or RETURN
                    if (returntype.getType() == ReturnType.Type.BREAK)
                        break;
                    if (returntype.getType() == ReturnType.Type.RETURN)
                        return returntype;
                }
            } else {
                // Throw an exception if the iterationArray is not found in globalVariables
                throw new AwkException("Not a valid ForInNode: Iteration array not found");
            }
        }

       else if (node instanceof IfNode) {
    // Process IfNode
    IfNode ifType = (IfNode) node;
    ReturnType returnType;

    boolean condition = Boolean.parseBoolean(getInterpreterDataType(ifType.getCondition(), locals).toString());

    if (condition) {
        // If the co4ndition is true, execute the if block
        returnType = InterpretListOfStatements(ifType.getStatement(), locals);

        // Check if the if block has a return type other than NONE
        if (returnType.getType() != ReturnType.Type.NONE)
            return returnType;
    } else if (ifType.getElse() instanceof IfNode) {
        // If the else block is another IfNode, recursively process it
        return ProcessStatement(locals, (StatementNode) ifType.getElse());


    } else if (ifType.getElse() instanceof BlockNode) {
        // If the else block is a BlockNode, execute its statements
        BlockNode elseI = (BlockNode) ifType.getElse();
        returnType = InterpretListOfStatements(elseI.getSnode(), locals);

        // Check if the else block has a return type
        if (returnType != null)
            return returnType;
    }
    return new ReturnType(ReturnType.Type.NONE);

} else if (node instanceof WhileNode) {
    // Process WhileNode
    WhileNode doWhile = (WhileNode) node;
    ReturnType rtype;

    while (Boolean.parseBoolean(getInterpreterDataType(doWhile.getCondition(), locals).toString())) {
        // Execute the statements in the while loop
        rtype = InterpretListOfStatements(doWhile.getStatement(), locals);

        // Check for BREAK or RETURN
        if (rtype.getType() == ReturnType.Type.BREAK)
            break;
        if (rtype.getType() == ReturnType.Type.RETURN)
            return rtype;
    }
    return new ReturnType(ReturnType.Type.RETURN);

} else if (node instanceof ReturnNode) {
    // Process ReturnNode

    ReturnNode returnN = (ReturnNode) node;
    if (returnN.getStatement() != null) {
        // If there is a return value, get and return it
        var value = getInterpreterDataType(returnN.getStatement(), locals);


        return new ReturnType(ReturnType.Type.RETURN, value.getValue());
    } else
        // If there is no return value, return a generic RETURN type
        return new ReturnType(ReturnType.Type.RETURN);
} 
else if (node instanceof BreakNode) {
    // Process BreakNode
    return new ReturnType(ReturnType.Type.BREAK);
}
 else if (node instanceof ContinueNode) {
    // Process ContinueNode
    return new ReturnType(ReturnType.Type.CONTINUE);
} 
 else if (node instanceof BlockNode) {
    BlockNode newBlock = (BlockNode)statement;

    return InterpretListOfStatements(newBlock.getSnode(), locals);
}

// Process statements that don't match any specific node type
    var someLine = getInterpreterDataType(statement, locals);


if (someLine == null)
    // Throw an exception for statements that don't match any node type
    throw new AwkException("None of the statements matched the input -> " + node);

    return new ReturnType(ReturnType.Type.NONE);
    }
    protected InterpreterDataType getInterpreterDataType(Node node, HashMap<String, InterpreterDataType> localVariables) throws AwkException {
        // Create a local variable storage for the current scope
        if(localVariables == null)
           localVariables = new HashMap<>();

        if (node instanceof AssignmentNode) {
            // Handle assignment nodes
            AssignmentNode newNode = (AssignmentNode) node;
    
            if ((newNode.getTargert() instanceof VariableReferenceNode) || (newNode.getOperattion() == OperationNode.Operation.FIELD_REFERENCE)) {
                var variable = (VariableReferenceNode) newNode.getTargert();
    
                // Check if the variable is in globalVariables
                 if (globalVariables.containsKey(variable.getName())|| globalVariables.containsKey(variable.getNameAndExperssion())) {
                    // Update the global variable's value
                    if(!variable.getExperssion().isEmpty()){

                        var arraytype = (InterpreterArrayDataType)getGlobalVariable(variable.getNameAndExperssion());
                        arraytype.setValue(variable.getNameAndExperssion(),getInterpreterDataType(newNode.getExpression(), localVariables));
                    }else
                       getGlobalVariable(variable.getName()).setValue(getInterpreterDataType(newNode.getExpression(), localVariables).toString());
                }
                else if (localVariables.containsKey(variable.getName())|| localVariables.containsKey(variable.getNameAndExperssion())) {
                    // Update the global variable's value

                    if(!variable.getExperssion().isEmpty()){

                        var arraytype = (InterpreterArrayDataType) localVariables.get(variable.getNameAndExperssion());
                        arraytype.setValue(variable.getNameAndExperssion(),getInterpreterDataType(newNode.getExpression(), localVariables));
                    }else
                       localVariables.get(variable.getName()).setValue(getInterpreterDataType(newNode.getExpression(), localVariables).toString());
               
                } 
                else if (!globalVariables.containsKey(variable.getName())|| !globalVariables.containsKey(variable.getNameAndExperssion())) {

                    // Add the variable to globalVariables
                    if(variable.getExperssion().isPresent()){
                        InterpreterArrayDataType arraytype = new InterpreterArrayDataType();
                        arraytype.setValue(variable.getNameAndExperssion(),getInterpreterDataType(newNode.getExpression(), localVariables));
                        
                        globalVariables.put(variable.getNameAndExperssion(), arraytype);
                    } else
                       globalVariables.put(variable.getName(), getInterpreterDataType(newNode.getExpression(), localVariables));
                }
                // Return the evaluated expression's value
                return getInterpreterDataType(newNode.getExpression(), localVariables);
            } else {
                // Throw an exception for an invalid AssignmentNode
                throw new AwkException("This is not a valid AssignmentNode >>" + node);
            }
        } else if (node instanceof TernaryNode) {
            // Handle ternary nodes
            TernaryNode newNode = (TernaryNode) node;
    
            var bool = getInterpreterDataType(newNode.getBoolNode(), localVariables);
    
            if (bool == null) {
                throw new AwkException("This is not a valid TernaryNode, Boolean is null");
            }
    
            if (!bool.toString().equals("false") || !bool.toString().equals("true")) {
                throw new AwkException("This is not a valid TernaryNode, needs a valid boolean expression");
            }
    
            if (Boolean.parseBoolean(bool.toString())) {
                return getInterpreterDataType(newNode.getTrueCase(), localVariables);
            } else {
                return getInterpreterDataType(newNode.getFalseNode(), localVariables);
            }
        } else if (node instanceof VariableReferenceNode) {
            // Handle variable reference nodes
            VariableReferenceNode newNode = (VariableReferenceNode) node;

            if (newNode.getExperssion().isEmpty()) {
                // Check for the variable in globalVariables
                if (this.globalVariables.containsKey(newNode.getName())) {
                    return getGlobalVariable(newNode.getName());
                }
            }
    
            if (newNode.getExperssion().isEmpty()) {
                // Check for the variable in localVariables
                if (localVariables.containsKey(newNode.getName())) {
                    return localVariables.get(newNode.getName());
                }
            } else if (newNode.getExperssion().isPresent()) {
                var exp = getInterpreterDataType(newNode.getExperssion().get(), localVariables);

                if (!(exp instanceof InterpreterDataType)) {
                    throw new AwkException("Not an Array DataType");
                }
                // Check for the variable in globalVariables and localVariables with the array index
                if (this.globalVariables.containsKey(newNode.getName() + "[" + exp.toString() + "]")) {
                    
                    return getGlobalVariable(newNode.getName() + "[" + exp.toString() + "]");
                }
    
                if (localVariables.containsKey(newNode.getName() + "[" + exp.toString() + "]")) {
                    return localVariables.get(newNode.getName() + "[" + exp.toString() + "]");
                }
            }
        } else if (node instanceof OperationNode) {
            // Handle operation nodes
            OperationNode newNode = (OperationNode) node;
            var leftNode = newNode.getLeftExpression();
            var rightNode = newNode.getRightExpression();
            var operationNode = newNode.getOp();
    
            if (leftNode == null) {
                throw new AwkException("Left operation is null");
            }
    
            // Call a helper method to evaluate the operation
           
            return getOperationalIDT(leftNode, rightNode, operationNode, localVariables);
      
        } else if (node instanceof FunctionCallNode) {
            // Handle function call nodes
            FunctionCallNode newNode = (FunctionCallNode) node;
            // Call a function and return the result
            return new InterpreterDataType(runFunctionCall(newNode, localVariables));

        } else if (node instanceof ConstantNode) {
            // Handle constant nodes
            ConstantNode newNode = (ConstantNode) node;
    
            // Return the constant value
            return new InterpreterDataType(newNode.toString());
        } else if (node instanceof PatternNode) {
            // Throw an exception for passing a pattern to a function or an assignment
            throw new AwkException("Cannot pass a pattern to a function or an assignment");
        }
    
        // Return null if the node type is not recognized
        return null;
    }  
    private String runFunctionCall(FunctionCallNode fNode,HashMap<String, InterpreterDataType> localVariables) throws AwkException{

        FunctionCallNode userCallFuction = fNode;
        if(functionDefinitions.containsKey(userCallFuction.getFunctionName())){

            FunctionDefinitionNode function = functionDefinitions.get(userCallFuction.getFunctionName());

                 if(function.getParameters().size() != userCallFuction.getParameters().size()&& !userCallFuction.getFunctionName().equals("print"))
                    throw new AwkException("The inputed function name \"" +userCallFuction.getFunctionName() +"\" had parameter size of " +userCallFuction.getParameters().size()+ " Compared to the expected " + function.getParameters().size());
   
                HashMap<String, InterpreterDataType> map = new HashMap<String, InterpreterDataType>();
                int counter = 0;

                for(Node para : userCallFuction.getParameters()){

                    if((function.getParameters().get(counter) instanceof VariableReferenceNode)){

                         VariableReferenceNode newpara = (VariableReferenceNode)function.getParameters().get(counter++);
             
                                   if(newpara.getExperssion().isPresent())
                                     map.put(newpara.getNameAndExperssion(), getInterpreterDataType(para, localVariables));
                                   else
                                     map.put(newpara.getName(), getInterpreterDataType(para, localVariables));
                                }
                         else{
                                     if((para instanceof VariableReferenceNode)){
                                         VariableReferenceNode newpara = (VariableReferenceNode)para;
                                          if(newpara.getExperssion().isPresent())
                                           map.put(newpara.getNameAndExperssion(), getInterpreterDataType(para, localVariables));
                                      else
                                           
                                          map.put(newpara.getName(), getInterpreterDataType(para, localVariables));
                                     }else

                                        map.put(function.getParameters().get(counter++).toString(), getInterpreterDataType(para, localVariables));
                                  }
                    }
                     if(function instanceof BuiltInFunctionDefinitionNode){

                               ((BuiltInFunctionDefinitionNode)function).execute(map);
                        }
                        else{
                           return InterpretListOfStatements(function.getStatementNodes(), map).toString();
                        }
     
       }else
            throw new AwkException("This fucntion \""+ userCallFuction.getFunctionName() + "\" has never been defined");

        return " ";
    }
    private InterpreterDataType getOperationalIDT(Node left, Optional<Node> right, OperationNode.Operation op, HashMap<String, InterpreterDataType> localVariables) throws AwkException{
     
                    
       switch(op){

                    case ADDITION:  
                        Double Avalue = Double.parseDouble(getInterpreterDataType(left, localVariables).toString()) + Double.parseDouble(getInterpreterDataType(right.get(), localVariables).toString());
                               return new InterpreterDataType(Avalue.toString());
                            
                    case SUBTRACTION:  

                        Double Svalue = Double.parseDouble(getInterpreterDataType(left, localVariables).toString()) - Double.parseDouble(getInterpreterDataType(right.get(), localVariables).toString());
                               return new InterpreterDataType(Svalue.toString());

                    case MULTIPLICATION:  
                        Double Mvalue = Double.parseDouble(getInterpreterDataType(left, localVariables).toString()) * Double.parseDouble(getInterpreterDataType(right.get(), localVariables).toString());
                               return new InterpreterDataType(Mvalue.toString());
                            
                    case DIVISION:  
                        Double Dvalue = Double.parseDouble(getInterpreterDataType(left, localVariables).toString()) / Double.parseDouble(getInterpreterDataType(right.get(), localVariables).toString());
                               return new InterpreterDataType(Dvalue.toString());

                    case MODULUS:  
                        Double MODvalue = Double.parseDouble(getInterpreterDataType(left, localVariables).toString()) % Double.parseDouble(getInterpreterDataType(right.get(), localVariables).toString());
                               return new InterpreterDataType(MODvalue.toString());



                     case LOGICAL_EQUAL:{
                        Object leftValue = getInterpreterDataType(left, localVariables);
                        Object rightValue = getInterpreterDataType(right.get(), localVariables);

                        if (leftValue instanceof String && rightValue instanceof String) {
                                   // Both values are strings, so compare them as strings
                                   boolean isEqual = leftValue.equals(rightValue);
                                   return new InterpreterDataType(String.valueOf(isEqual));
                                   
                               } else if (leftValue instanceof Number && rightValue instanceof Number) {
                                   // Both values are numbers, so compare them as numbers
                                   double leftNumber = ((Number) leftValue).doubleValue();
                                   double rightNumber = ((Number) rightValue).doubleValue();
                                   boolean isEqual = leftNumber == rightNumber;
                                   return new InterpreterDataType(String.valueOf(isEqual));
                                   
                               } else {
                                
                                   String leftNumber = leftValue.toString();
                                   String rightNumber = rightValue.toString();

                                   boolean isEqual = leftNumber.equals(rightNumber);
                                   if(isEqual)
                                     return new InterpreterDataType(String.valueOf(isEqual));
                                
                                   // Values are of different types (string and number), so they are not equal
                                   return new InterpreterDataType("false");
                               }
                            }

                    case LOGICAL_NOT:  
                       Object leftValue = getInterpreterDataType(left, localVariables);
                       Object rightValue = getInterpreterDataType(right.get(), localVariables);
                           
                               if (leftValue instanceof String && rightValue instanceof String) {
                                   // Both values are strings, so compare them as strings
                                   boolean isEqual = leftValue.equals(rightValue);
                                   return new InterpreterDataType(String.valueOf(isEqual));
                                   
                               } else if (leftValue instanceof Number && rightValue instanceof Number) {
                                   // Both values are numbers, so compare them as numbers
                                   double leftNumber = ((Number) leftValue).doubleValue();
                                   double rightNumber = ((Number) rightValue).doubleValue();
                                   boolean isEqual = leftNumber != rightNumber;
                                   return new InterpreterDataType(String.valueOf(isEqual));
                                   
                               } else {

                                   String leftNumber = leftValue.toString();
                                   String rightNumber = rightValue.toString();

                                   boolean isEqual = leftNumber.equals(rightNumber);
                                   if(isEqual)
                                     return new InterpreterDataType(String.valueOf(isEqual));

                                   // Values are of different types (string and number), so they are not equal
                                   return new InterpreterDataType("false");
                               }

                    case LESS_THAN:  
                        Boolean LESS_THANvalue = Double.parseDouble(getInterpreterDataType(left, localVariables).toString()) < Double.parseDouble(getInterpreterDataType(right.get(), localVariables).toString());
                               return new InterpreterDataType(LESS_THANvalue.toString());
                            
                    case LESS_THAN_EQUAL:  
                        Boolean LESS_THAN_EQUALvalue = Double.parseDouble(getInterpreterDataType(left, localVariables).toString()) <= Double.parseDouble(getInterpreterDataType(right.get(), localVariables).toString());
                               return new InterpreterDataType(LESS_THAN_EQUALvalue.toString());

                    case GREATER_THAN:  
                        Boolean GREATER_THANvalue = Double.parseDouble(getInterpreterDataType(left, localVariables).toString()) > Double.parseDouble(getInterpreterDataType(right.get(), localVariables).toString());
                               return new InterpreterDataType(GREATER_THANvalue.toString());
                            
                    case GREATER_THAN_EQUAL:  
                        Boolean GREATER_THAN_EQUALvalue = Double.parseDouble(getInterpreterDataType(left, localVariables).toString()) >= Double.parseDouble(getInterpreterDataType(right.get(), localVariables).toString());
                               return new InterpreterDataType(GREATER_THAN_EQUALvalue.toString());
      

      
                    case LOGICAL_AND:  

                         Boolean Lvalue = Boolean.parseBoolean(getInterpreterDataType(left, localVariables).toString());
                         Boolean Rvalue = Boolean.parseBoolean(getInterpreterDataType(right.get(), localVariables).toString());

                        Boolean LOGICAL_ANDvalue = Lvalue && Rvalue;
                               return new InterpreterDataType(LOGICAL_ANDvalue.toString());

                    case LOGICAL_OR:  
                         Boolean LLvalue = Boolean.parseBoolean(getInterpreterDataType(left, localVariables).toString());
                         Boolean RRvalue = Boolean.parseBoolean(getInterpreterDataType(right.get(), localVariables).toString());

                        Boolean LOGICAL_ORvalue = LLvalue || RRvalue;
                               return new InterpreterDataType(LOGICAL_ORvalue.toString());
     
                    case NOT_EQUAL:  
                         Boolean LLLvalue = Boolean.parseBoolean(getInterpreterDataType(left, localVariables).toString());
                         Boolean RRRvalue = Boolean.parseBoolean(getInterpreterDataType(right.get(), localVariables).toString());

                        Boolean NOT_EQUALvalue = LLLvalue || RRRvalue;
                               return new InterpreterDataType(NOT_EQUALvalue.toString());


                    case MATCH:  {
                         InterpreterDataType LvalueExp = getInterpreterDataType(left, localVariables);

                         if(!(right.get() instanceof PatternNode))
                               throw new AwkException("Must be a Pattern Node");
                           
                           Boolean match = java.util.regex.Pattern.compile(LvalueExp.toString()).matcher(right.get().toString()).find();

                               return new InterpreterDataType(match.toString());
                    }
                    case NON_MATCH:  {
                      InterpreterDataType LLvalueExp = getInterpreterDataType(left, localVariables);

                         if(!(right.get() instanceof PatternNode))
                               throw new AwkException("Must be a Pattern Node");
                           
                           Boolean nonmatch = !java.util.regex.Pattern.compile(LLvalueExp.toString()).matcher(right.get().toString()).find();

                               return new InterpreterDataType(nonmatch.toString());
                    }
     
                    case FIELD_REFERENCE:{
                      InterpreterDataType leftExp = getInterpreterDataType(left, localVariables);
                      if(this.globalVariables.containsKey("$" + leftExp.getValue()))
                            return getGlobalVariable("$"+leftExp.getValue());
                        else
                            return new InterpreterDataType("$" + leftExp.toString());

                    }
                    case POST_DECREMENT:  {
                        if(!(left instanceof VariableReferenceNode))
                            throw new AwkException("Must be a VariableReferenceNode Node");

                         VariableReferenceNode newNode = (VariableReferenceNode)left;

                       if(newNode.getExperssion().isPresent()){
                           if(this.globalVariables.containsKey(newNode.getName())){

                                  Double value = Double.parseDouble(getInterpreterDataType(newNode.getExperssion().get(), localVariables).toString()) - 1;
                                  
                                  InterpreterDataType hold = globalVariables.get(newNode.getName());
                                  getGlobalVariable(newNode.getName()).setValue(value.toString());
                               
                                  return hold;
                           }

                      }else
                          throw new AwkException("Its emtpy");

                         if(newNode.getExperssion().isPresent()){
                           if(localVariables.containsKey(newNode.getName())){

                                  Double value = Double.parseDouble(getInterpreterDataType(newNode.getExperssion().get(), localVariables).toString()) - 1;
                       
                                  InterpreterDataType hold = localVariables.get(newNode.getName());
                                  getGlobalVariable(newNode.getName()).setValue(value.toString());
                               
                                  return hold;
                           }

                      }else

                          throw new AwkException("Its emtpy");
                
                    }
                    case POST_INCREMENT:  {
                        if(!(left instanceof VariableReferenceNode))
                            throw new AwkException("Must be a VariableReferenceNode Node");

                         VariableReferenceNode newNode = (VariableReferenceNode)left;

                       if(newNode.getExperssion().isPresent()){
                           if(this.globalVariables.containsKey(newNode.getNameAndExperssion())){
                                  Double value = Double.parseDouble(globalVariables.get(newNode.getNameAndExperssion()).getValue()) + 1;
                                  
                                  InterpreterDataType hold = globalVariables.get(newNode.getNameAndExperssion());
                                  getGlobalVariable(newNode.getNameAndExperssion()).setValue(value.toString());
                               
                                  return hold;
                           }
                           else if(localVariables.containsKey(newNode.getNameAndExperssion())){

                                  Double value = Double.parseDouble(localVariables.get(newNode.getNameAndExperssion()).getValue()) + 1;
                                  
                                  InterpreterDataType hold = localVariables.get(newNode.getNameAndExperssion());
                                  getGlobalVariable(newNode.getNameAndExperssion()).setValue(value.toString());
                               
                                  return hold;
                           }

                      }
                         if(!newNode.getExperssion().isPresent()){
                             if(this.globalVariables.containsKey(newNode.getName())){

                                Double value = Double.parseDouble(globalVariables.get(newNode.getName()).getValue()) + 1;
                                  InterpreterDataType hold = globalVariables.get(newNode.getName());
                                  getGlobalVariable(newNode.getName()).setValue(value.toString());
                               

                                  return hold;
                           }
                           else if(localVariables.containsKey(newNode.toString())){

                                 Double value = Double.parseDouble(localVariables.get(newNode.getName()).getValue()) + 1;
                                  
                                  InterpreterDataType hold = localVariables.get(newNode.getName());
                                  getGlobalVariable(newNode.getName()).setValue(value.toString());
                               
                                  return hold;
                           }

                      }else

                          throw new AwkException("Its emtpy");
                
                    }
                    case PRE_DECREMENT:  {
                        if(!(left instanceof VariableReferenceNode))
                            throw new AwkException("Must be a VariableReferenceNode Node");

                         VariableReferenceNode newNode = (VariableReferenceNode)left;

                       if(newNode.getExperssion().isPresent()){
                           if(this.globalVariables.containsKey(newNode.getNameAndExperssion())){
                                  Double value = Double.parseDouble(globalVariables.get(newNode.getNameAndExperssion()).getValue()) - 1;
                                  
                                  InterpreterDataType hold = globalVariables.get(newNode.getNameAndExperssion());
                                  getGlobalVariable(newNode.getNameAndExperssion()).setValue(value.toString());
                               
                                  return hold;
                           }
                           else if(localVariables.containsKey(newNode.getNameAndExperssion())){

                                  Double value = Double.parseDouble(localVariables.get(newNode.getNameAndExperssion()).getValue()) -1;
                                  
                                  InterpreterDataType hold = localVariables.get(newNode.getNameAndExperssion());
                                  getGlobalVariable(newNode.getNameAndExperssion()).setValue(value.toString());
                               
                                  return hold;
                           }

                      }
                         if(!newNode.getExperssion().isPresent()){
                             if(this.globalVariables.containsKey(newNode.getName())){

                                Double value = Double.parseDouble(globalVariables.get(newNode.getName()).getValue()) - 1;
                                  InterpreterDataType hold = globalVariables.get(newNode.getName());
                                  getGlobalVariable(newNode.getName()).setValue(value.toString());
                               

                                  return hold;
                           }
                           else if(localVariables.containsKey(newNode.toString())){

                                 Double value = Double.parseDouble(localVariables.get(newNode.getName()).getValue()) - 1;
                                  
                                  InterpreterDataType hold = localVariables.get(newNode.getName());
                                  getGlobalVariable(newNode.getName()).setValue(value.toString());
                               
                                  return hold;
                           }
                        }
                    }

                    case PRE_INCREMENT:   {
                        if(!(left instanceof VariableReferenceNode))
                            throw new AwkException("Must be a VariableReferenceNode Node");

                         VariableReferenceNode newNode = (VariableReferenceNode)left;
                       if(newNode.getExperssion().isPresent()){
                           if(this.globalVariables.containsKey(newNode.toString())){

                                  Double value = Double.parseDouble(getInterpreterDataType(newNode.getExperssion().get(), localVariables).toString()) + 1;
                                  
                                  getGlobalVariable(newNode.getName()).setValue(value.toString());
                               
                                  return globalVariables.get(newNode.getName());
                           }

                      }else
                          throw new AwkException("Its emtpy");

                         if(newNode.getExperssion().isPresent()){
                            if(localVariables.containsKey(newNode.toString())){

                                  Double value = Double.parseDouble(getInterpreterDataType(newNode.getExperssion().get(), localVariables).toString()) + 1;
                       
                                  getGlobalVariable(newNode.getName()).setValue(value.toString());
                               
                                  return localVariables.get(newNode.getName());
                           }

                      }else
                          throw new AwkException("Its emtpy");
                
                    }
                    case Concatenation:{

                          String value = left.toString() + right.get().toString();

                            return new InterpreterDataType(value);
                    }
                    case IN:{

                        if(right.get() instanceof VariableReferenceNode){

                            VariableReferenceNode newNode = (VariableReferenceNode)right.get();
                              if(newNode.getExperssion().isEmpty())
                                 throw new AwkException("Must be a array");
                            
                                  String value = getInterpreterDataType(left, localVariables).toString();



                           if(this.globalVariables.containsKey(newNode.getName() +"["+value +"]")){

                                  return globalVariables.get(newNode.getName() +"["+value +"]");
                           
                           }
                           if(localVariables.containsKey(newNode.getName() +"["+value +"]")){
    
                                  return localVariables.get(newNode.getName() +"["+value +"]");

                           }
                           
                        }
                        else
                           throw new AwkException("Neesds a VariableReferenceNode");
                    }
                    default:
                        throw new AwkException("Something is wrong here");
                }    
                          
    }
    

    private LineManager startLineManager(Path file) throws IOException{

        if(file != null)
            return new LineManager(Files.readAllLines(file));
        else 
            return new LineManager(new LinkedList<String>());     
    }
    private  void addBuiltInFunctions() throws NumberFormatException, AwkException{

         HashMap<String, BuiltInFunctionDefinitionNode> operations = new HashMap<>();
      
        BuiltInFunctionDefinitionNode printfBuilt = new BuiltInFunctionDefinitionNode("printf",test -> builtInPrint(test), true);
        printfBuilt.getParameters().add(new Node());
        BuiltInFunctionDefinitionNode printBuilt = new BuiltInFunctionDefinitionNode("print",test -> builtInPrint(test), true);
        printBuilt.getParameters().add(new Node());

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

        if(print.get("0") instanceof InterpreterArrayDataType){
                for(InterpreterDataType out: str.values()){
                      InterpreterArrayDataType  arrayout = (InterpreterArrayDataType)out;
                    
                      System.out.printf ("%s", arrayout.getArray().values().toString());
                       
                }
      
         }else
             for(InterpreterDataType out: str.values())
                    System.out.printf("%s\n", out.getValue());
       
                
        return " ";
    }
    private  String builtInGetline(HashMap<String, InterpreterDataType> str) {
        return Boolean.toString(this.lineManagement.splitAndAssign());

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
   

}
