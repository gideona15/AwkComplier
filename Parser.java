import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;


public  class Parser {
    // Fields to store the token stream, a token manager helper, and the program node
    private LinkedList<Token> TokenStream = new LinkedList<>();
    private TokenManager helper;
    private ProgramNode pro;

    // Constructor to initialize the parser with a token stream
    public  Parser(LinkedList<Token> input) {
        TokenStream = input;
        helper = new TokenManager(input);
        pro = new ProgramNode();
    }

    // Parse the entire program
    public  ProgramNode program() throws AwkException {
        Boolean Flnode = false, Anode = false;
        // Remove separators from the token stream
        while (helper.AcceptSeperators());

        while (true) {
            Flnode = ParseFunction();
            Anode = ParseAction();
            if (Flnode == false && Anode == false)
                break;
        }
        return pro;
    }

    // Parse a function definition
    private boolean ParseFunction() throws AwkException {
        if (!helper.Peek(0).isPresent())
            return false;

        if (helper.Peek(0).get().getType() != Token.TokenType.FUNCTION)
            return false;
        else {
            String name;
            helper.MatchAndRemove(Token.TokenType.FUNCTION);

            if (helper.Peek(0).get().getType() == Token.TokenType.WORD) {
                name = helper.Peek(0).get().getValue().toString();
                helper.MatchAndRemove(Token.TokenType.WORD);
            } else
                throw new AwkException("This function needs a name");

            LinkedList<Token> pholder = ParseParameters();
            LinkedList<StatementNode> sholder = new LinkedList<StatementNode>();

            BlockNode node = ParseBlock();
            sholder.add(node);

            pro.getFnode().add(new FunctionDefinitionNode(name, pholder, sholder));
            return true;
        }
    }
    
    // Parse an action block
    private boolean ParseAction() throws AwkException {
        if (!helper.Peek(0).isPresent())
            return false;

        if (helper.Peek(0).get().getType() == Token.TokenType.BEGIN) {
            helper.MatchAndRemove(Token.TokenType.BEGIN);
            BlockNode node = ParseBlock();
            pro.getBegin().add(node);

        } else if (helper.Peek(0).get().getType() == Token.TokenType.END) {
            helper.MatchAndRemove(Token.TokenType.END);
            BlockNode node = ParseBlock();
            pro.getEnd().add(node);
        } else {

            var Pnode = ParseOperation();
            var Bnode = ParseBlock();

                if (Pnode.isPresent())
                    pro.getOther().add(Pnode.get());
                if (Bnode != null)
                    pro.getOther().add(Bnode);

          
        }
        return true;
    }
    protected BlockNode ParseBlock() throws AwkException {
        // Check if there are tokens left to parse
        if (!helper.Peek(0).isPresent())
            return null;
    
        // Create a new BlockNode to represent a block of statements
        BlockNode node = new BlockNode();
    
        // Check if the next token is a left curly bracket, indicating the start of a block
        if (helper.Peek(0).get().getType() == Token.TokenType.LEFT_CURLY_BRACKETS) {
            // Consume the left curly bracket
            helper.MatchAndRemove(Token.TokenType.LEFT_CURLY_BRACKETS);
            // Parse statements within the block until a right curly bracket is encountered
            while (helper.Peek(0).get().getType() != Token.TokenType.RIGHT_CURLY_BRACKETS) {
                // Parse a statement within the block
                var Pnode = ParseStatement();
                if (Pnode == null)
                    break; // Break the loop if no statement can be parsed
                else
                    node.getSnode().add(Pnode); // Add the parsed statement to the block
            }
    
            // Check if a right curly bracket is found to indicate the end of the block
            if (helper.Peek(0).get().getType() == Token.TokenType.RIGHT_CURLY_BRACKETS)
                helper.MatchAndRemove(Token.TokenType.RIGHT_CURLY_BRACKETS);
            else
                throw new AwkException("Block needs a right curly bracket"); // Throw an exception if the right curly bracket is missing
        } else {
            // If there is no left curly bracket, parse a single statement and add it to the block
            var Pnode = ParseStatement();
            if (Pnode != null)
                node.getSnode().add(Pnode);
        }
    
        // Return the BlockNode representing the parsed block
        return node;
    }
    
    protected StatementNode ParseStatement() throws AwkException {
        // Check the type of the next token
        if (helper.Peek(0).get().getType() == Token.TokenType.IF) {
            // If it's an "if" token, parse an if statement
            return ParseIf();
        } else if (helper.Peek(0).get().getType() == Token.TokenType.WHILE) {
            // If it's a "while" token, parse a while loop
            return ParseWhile();
        } else if (helper.Peek(0).get().getType() == Token.TokenType.FOR) {
            // If it's a "for" token, parse a for loop
            return ParseFor();
        } else if (helper.Peek(0).get().getType() == Token.TokenType.DELETE) {
            // If it's a "delete" token, parse a delete statement
            return ParseDelete();
        } else if (helper.Peek(0).get().getType() == Token.TokenType.DO) {
            // If it's a "do" token, parse a do-while loop
            return ParseDo();
        } else if (helper.Peek(0).get().getType() == Token.TokenType.RETURN) {
            // If it's a "return" token, parse a return statement
            return ParseReturn();
        } else if (helper.Peek(0).get().getType() == Token.TokenType.CONTINUE) {
            // If it's a "continue" token, parse a continue statement
            return ParseContine(); // There's a typo here, should be ParseContinue()
        } else if (helper.Peek(0).get().getType() == Token.TokenType.BREAK) {
            // If it's a "break" token, parse a break statement
            return ParseBreak();
        } else {
            // If none of the above conditions are met, parse an operation
            var ParseOp = ParseOperation();
            // Check if the operation parsing was successful and return it if it is
            if (ParseOp.isPresent() && ParseOp != null) {
                return (StatementNode) ParseOp.get();
            }
        }
        // Return null if none of the recognized statements or operations are found
        return null;
    }
    
    // Parse an operation
    public   Optional<Node> ParseOperation() throws AwkException {

       Node node = Assignment();

       if(node != null){
          Optional<Node> Newnode = Optional.of(node);

          return Newnode;

       }else 
          return Optional.empty();

    }
   
    private Node Assignment() throws AwkException {
        // This method appears to handle assignment operations in your Awk script.
        Node node = Ternary();
    

        if(!helper.MoreTokens())
                return node;

        // It checks various assignment operators (e.g., '=', '+=', '-=', etc.) and constructs AssignmentNode objects accordingly.
    
        if(helper.Peek(0).get().getType() ==  Token.TokenType.EQUAL){

            var output = OperationNode.Operation.ASSIGNMENT;
            helper.MatchAndRemove(Token.TokenType.EQUAL);
                return new AssignmentNode(node, Expression(), output);
        
        } else if(helper.Peek(0).get().getType() ==  Token.TokenType.EXPONENTIATION_ASSIGNMENT){

            var output = OperationNode.Operation.EXPONENTIATION_ASSIGNMENT;
            helper.MatchAndRemove(Token.TokenType.EXPONENTIATION_ASSIGNMENT);
                return new AssignmentNode(node, Expression(), output);
        
        } else if(helper.Peek(0).get().getType() ==  Token.TokenType.MODULUS_ASSIGNMENT){

            var output = OperationNode.Operation.MODULUS_ASSIGNMENT;
            helper.MatchAndRemove(Token.TokenType.MODULUS_ASSIGNMENT);
                
                return new AssignmentNode(node, Expression(), output);
      
        } else if(helper.Peek(0).get().getType() ==  Token.TokenType.MULTIPLICATION_ASSIGNMENT){

            var output = OperationNode.Operation.MULTIPLICATION_ASSIGNMENT;
            helper.MatchAndRemove(Token.TokenType.MULTIPLICATION_ASSIGNMENT);
                return new AssignmentNode(node, Expression(), output);
      
        } else if(helper.Peek(0).get().getType() ==  Token.TokenType.DIVISION_ASSIGNMENT){

            var output = OperationNode.Operation.DIVISION_ASSIGNMENT;
            helper.MatchAndRemove(Token.TokenType.DIVISION_ASSIGNMENT);
               
                return new AssignmentNode(node, Expression(), output);
        
        } else if(helper.Peek(0).get().getType() ==  Token.TokenType.SUBTRACTION_ASSIGNMENT){

            var output = OperationNode.Operation.SUBTRACTION_ASSIGNMENT;
            helper.MatchAndRemove(Token.TokenType.SUBTRACTION_ASSIGNMENT);
                return new AssignmentNode(node, Expression(), output);
       
           }
           else if(helper.Peek(0).get().getType() ==  Token.TokenType.ADDITION_ASSIGNMENT){

            var output = OperationNode.Operation.ADDITION_ASSIGNMENT;
            helper.MatchAndRemove(Token.TokenType.ADDITION_ASSIGNMENT);
                return new AssignmentNode(node, Expression(), output);
       
           }


        return node;
    }
    private Node Ternary() throws AwkException {
        // This method appears to handle the ternary conditional operator '?' in your Awk script.
        Node node = LogicalOr();


        if(!helper.MoreTokens())
                return node;
    
        if (helper.Peek(0).get().getType() == Token.TokenType.CONDITIONAL_QUESTION) {
            var output = OperationNode.Operation.CONDITIONAL_QUESTION;
            helper.MatchAndRemove(Token.TokenType.CONDITIONAL_QUESTION);
    
            // It constructs a TernaryNode and returns it.
            return new TernaryNode(node, Assignment(), Assignment());
        }
    
        return node;
    }  
    private Node LogicalOr() throws AwkException {
        // This method appears to handle logical OR operations in your Awk script.
        Node node = LogicalAnd();


        if(!helper.MoreTokens())
                return node;
    
        if (helper.Peek(0).get().getType() == Token.TokenType.LOGICAL_OR) {
            var output = OperationNode.Operation.LOGICAL_OR;
            helper.MatchAndRemove(Token.TokenType.LOGICAL_OR);
    
            // It constructs an OperationNode with logical OR operation.
            return new OperationNode(node, Optional.of(BooleanCompare()), output);
        }
    
        return node;
    }
    private Node LogicalAnd() throws AwkException {
        // This method handles logical AND operations in your Awk script.
        Node node = ArrayMembership();


        if(!helper.MoreTokens())
                return node;
    
        if (helper.Peek(0).get().getType() == Token.TokenType.LOGICAL_AND) {
            // Check if the next token is a logical AND operator '&&'.
            var output = OperationNode.Operation.LOGICAL_AND;
            helper.MatchAndRemove(Token.TokenType.LOGICAL_AND);
            // Construct an OperationNode for logical AND.
            return new OperationNode(node, Optional.of(BooleanCompare()), output);
        }
    
        // If there is no logical AND operator, return the original 'node'.
        return node;
    }
    private Node ArrayMembership() throws AwkException{

        Node node = Match();


        if(!helper.MoreTokens())
                return node;

        if (helper.Peek(0).get().getType() == Token.TokenType.IN) {
        // If the next token is a field reference symbol, parse it as a field reference operation.
         helper.MatchAndRemove(Token.TokenType.IN);

        return new OperationNode(node, Optional.of(Expression()), OperationNode.Operation.IN);
    }

        
        return node;
    }
    private Node Match() throws AwkException {
        // This method handles matching and non-matching operations in your Awk script.
        Node node = BooleanCompare();
    

        if(!helper.MoreTokens())
                return node;

        if (helper.Peek(0).get().getType() == Token.TokenType.MATCH) {
            // Check if the next token is a matching operator '~'.
            var output = OperationNode.Operation.MATCH;
            helper.MatchAndRemove(Token.TokenType.MATCH);
            // Construct an OperationNode for matching.
            return new OperationNode(node, Optional.of(BooleanCompare()), output);
        } else if (helper.Peek(0).get().getType() == Token.TokenType.NON_MATCH) {
            // Check if the next token is a non-matching operator '!~'.
            var output = OperationNode.Operation.NON_MATCH;
            helper.MatchAndRemove(Token.TokenType.NON_MATCH);
            // Construct an OperationNode for non-matching.
            return new OperationNode(node, Optional.of(BooleanCompare()), output);
        }
    
        // If there is no matching or non-matching operator, return the original 'node'.
        return node;
    }    
    private Node BooleanCompare() throws AwkException {
        // This method handles boolean comparison operations, such as less than, equal to, etc.
        Node node = Concatenation();

        if(!helper.MoreTokens())
                return node;
        // Check various boolean comparison operators and construct the corresponding OperationNode.
    
        if(helper.Peek(0).get().getType() ==  Token.TokenType.LESS_THAN){

            var output = OperationNode.Operation.LESS_THAN;
            helper.MatchAndRemove(Token.TokenType.LESS_THAN);
                return new OperationNode(node,Optional.of(Expression()), output);
        }
        else if(helper.Peek(0).get().getType() ==  Token.TokenType.LESS_THAN_EQUAL){

            var output = OperationNode.Operation.LESS_THAN_EQUAL;
            helper.MatchAndRemove(Token.TokenType.LESS_THAN_EQUAL);
                return new OperationNode(node,Optional.of(Expression()), output);
        }
        else if(helper.Peek(0).get().getType() ==  Token.TokenType.NOT_EQUAL){

            var output = OperationNode.Operation.NOT_EQUAL;
            helper.MatchAndRemove(Token.TokenType.NOT_EQUAL);
                return new OperationNode(node,Optional.of(Expression()), output);
        }
        else if(helper.Peek(0).get().getType() ==  Token.TokenType.LOGICAL_EQUAL){

            var output = OperationNode.Operation.LOGICAL_EQUAL;
            helper.MatchAndRemove(Token.TokenType.LOGICAL_EQUAL);
                return new OperationNode(node,Optional.of(Expression()), output);
        }
         else if(helper.Peek(0).get().getType() ==  Token.TokenType.GREATER_THAN){

            var output = OperationNode.Operation.GREATER_THAN;
            helper.MatchAndRemove(Token.TokenType.GREATER_THAN);
                return new OperationNode(node,Optional.of(Expression()), output);
        }
        else if(helper.Peek(0).get().getType() ==  Token.TokenType.GREATER_THAN_EQUAL){

            var output = OperationNode.Operation.GREATER_THAN_EQUAL;
            helper.MatchAndRemove(Token.TokenType.GREATER_THAN_EQUAL);
                return new OperationNode(node,Optional.of(Expression()), output);
        }
        return node;
    }
    private Node Concatenation() throws AwkException{

        Node node = Expression();
        
        


        return node;
    }
    private Node Expression() throws AwkException {
        // This method handles general expressions in your Awk script.
        Node node = Term();
    

        if(!helper.MoreTokens())
                return node;
        // Check for addition and subtraction operators and construct the corresponding OperationNode.
    
        if(helper.Peek(0).get().getType() ==  Token.TokenType.ADDITION){

            var output = OperationNode.Operation.ADDITION;
            helper.MatchAndRemove(Token.TokenType.ADDITION);
                return new OperationNode(node,Optional.of(Expression()), output);
        }
        else if(helper.Peek(0).get().getType() ==  Token.TokenType.SUBTRACTION){

            var output = OperationNode.Operation.SUBTRACTION;
            helper.MatchAndRemove(Token.TokenType.SUBTRACTION);
                return new OperationNode(node,Optional.of(Expression()), output);
        }
        
        return node;
    }
    private Node Term() throws AwkException{

        Node node = Factor();


        if(!helper.MoreTokens())
                return node;

        if(helper.Peek(0).get().getType() ==  Token.TokenType.MULTIPLICATION){

            var output = OperationNode.Operation.MULTIPLICATION;
            helper.MatchAndRemove(Token.TokenType.MULTIPLICATION);
                return new OperationNode(node,Optional.of(Term()), output);
        }
        else if(helper.Peek(0).get().getType() ==  Token.TokenType.DIVISION){

            var output = OperationNode.Operation.DIVISION;
            helper.MatchAndRemove(Token.TokenType.DIVISION);
                return new OperationNode(node,Optional.of(Term()), output);
        }
        else if(helper.Peek(0).get().getType() ==  Token.TokenType.MODULUS){

            var output = OperationNode.Operation.EXPONENT;
            helper.MatchAndRemove(Token.TokenType.EXPONENT);
                return new OperationNode(node,Optional.of(Term()), output);
        }
        return node;
    }
    private Node Factor() throws AwkException{

        
        Node node =  PostImcreDrce();

        if(!helper.MoreTokens())
                return node;

         if(helper.Peek(0).get().getType() ==  Token.TokenType.EXPONENT){

            var output = OperationNode.Operation.EXPONENT;
            helper.MatchAndRemove(Token.TokenType.EXPONENT);
            
                return new OperationNode(node,Optional.of(PostImcreDrce()), output);
        }
       
        return node;
    }
    private Node PostImcreDrce() throws AwkException{

        Optional<Node> node = ParseBottomLevel();

        if(!node.isPresent())
                return null;

        if(!helper.MoreTokens())
                return node.get();


        if(helper.Peek(0).get().getType() ==  Token.TokenType.INCREMENT){

            var output = OperationNode.Operation.POST_INCREMENT;
            helper.MatchAndRemove(Token.TokenType.INCREMENT);
                return new OperationNode(node.get(), output);
        }
        else if(helper.Peek(0).get().getType() ==  Token.TokenType.DECREMENT){

            var output = OperationNode.Operation.POST_INCREMENT;
            helper.MatchAndRemove(Token.TokenType.DECREMENT);
                return new OperationNode(node.get(), output);
        }

        return node.get();
    }
    private Optional<Node> ParseBottomLevel() throws AwkException {


        if (helper.Peek(0).get().getType() == Token.TokenType.NUMBER) {
            // If the next token is a number, parse it as a ConstantNode.
            var output = helper.Peek(0).get().getValue();
            helper.MatchAndRemove(Token.TokenType.NUMBER);
            return Optional.of(new ConstantNode(output));

        } else if (helper.Peek(0).get().getType() == Token.TokenType.STRINGLITERAL) {
            // If the next token is a string literal, parse it as a ConstantNode.
            var output = helper.Peek(0).get().getValue();
            helper.MatchAndRemove(Token.TokenType.STRINGLITERAL);
            return Optional.of(new ConstantNode(output));

        } else if (helper.Peek(0).get().getType() == Token.TokenType.Pattern) {
            // If the next token is a pattern, parse it as a PatternNode.
            var output = helper.Peek(0).get().getValue();
            helper.MatchAndRemove(Token.TokenType.Pattern);
            return Optional.of(new PatternNode(output));

        } else if (helper.Peek(0).get().getType() == Token.TokenType.LEFT_PAREN) {
            // If the next token is a left parenthesis, parse the enclosed expression.
            helper.MatchAndRemove(Token.TokenType.LEFT_PAREN);
            var node = ParseOperation();
            if (helper.Peek(0).get().getType() == Token.TokenType.RIGHT_PAREN)
                helper.MatchAndRemove(Token.TokenType.RIGHT_PAREN);
            else
                throw new AwkException("Need another right bracket to complete the statement");
            return node;

        } else if (helper.Peek(0).get().getType() == Token.TokenType.SUBTRACTION) {
            // If the next token is a subtraction symbol, parse it as a unary subtraction operation.
            var output = OperationNode.Operation.SUBTRACTION;
            helper.MatchAndRemove(Token.TokenType.SUBTRACTION);
            var node = ParseOperation();
            return Optional.of(new OperationNode(node.get(), output));

        } else if (helper.Peek(0).get().getType() == Token.TokenType.LOGICAL_NOT) {
            // If the next token is a logical NOT symbol, parse it as a logical NOT operation.
            var output = OperationNode.Operation.LOGICAL_NOT;
            helper.MatchAndRemove(Token.TokenType.LOGICAL_NOT);
            var node = ParseOperation();
            return Optional.of(new OperationNode(node.get(), output));

        } else if (helper.Peek(0).get().getType() == Token.TokenType.ADDITION) {
            // If the next token is an addition symbol, parse it as an addition operation.
            var output = OperationNode.Operation.ADDITION;
            helper.MatchAndRemove(Token.TokenType.ADDITION);
            var node = ParseOperation();
            return Optional.of(new OperationNode(node.get(), output));

        }  else if (helper.Peek(0).get().getType() == Token.TokenType.INCREMENT) {
            // If the next token is a pre-increment symbol, parse it as a pre-increment operation.
            var output = OperationNode.Operation.PRE_INCREMENT;
            helper.MatchAndRemove(Token.TokenType.INCREMENT);
            var Pnode = ParseBottomLevel();
            return Optional.of(new OperationNode(Pnode.get(), output));

        } else if(helper.Peek(0).get().getType() == Token.TokenType.CONDITIONAL_COLON) {
               helper.MatchAndRemove(Token.TokenType.CONDITIONAL_COLON);

              return Optional.of(Assignment());
        
        }  else if (helper.Peek(0).get().getType() == Token.TokenType.DECREMENT) {
            // If the next token is a pre-decrement symbol, parse it as a pre-decrement operation.
            var output = OperationNode.Operation.PRE_DECREMENT;
            helper.MatchAndRemove(Token.TokenType.DECREMENT);
            var Pnode = ParseOperation();
            return Optional.of(new OperationNode(Pnode.get(), output));

        }
        else if(helper.Peek(0).get().getType() == Token.TokenType.WORD) {               
               if (helper.Peek(1).get().getType() == Token.TokenType.LEFT_PAREN){

                return Optional.of(ParseFunctionCall());

               }
        } 
        else if(isBuiltIn(helper.Peek(0).get().getType())) {               
               
            
                return Optional.of(ParseFunctionCall());

               
        } 
            Optional<Node> Bnode = ParseLValue();
            // If none of the above conditions match, attempt to parse an LValue.
            return Bnode;
      
    }
    private Optional<Node> ParseLValue() throws AwkException {
                
        if (helper.Peek(0).get().getType() == Token.TokenType.FIELD_REFERENCE) {
        // If the next token is a field reference symbol, parse it as a field reference operation.
        helper.MatchAndRemove(Token.TokenType.FIELD_REFERENCE);
        var node = ParseBottomLevel();

        return Optional.of(new OperationNode(node.get(), null, OperationNode.Operation.FIELD_REFERENCE));
    }
     else if (helper.Peek(0).get().getType() == Token.TokenType.WORD) {
        // If the next token is a word (variable name), parse it as a variable reference.
          String name = helper.Peek(0).get().getValue().toString();
          helper.MatchAndRemove(Token.TokenType.WORD);

        if (helper.Peek(0).get().getType() == Token.TokenType.LEFT_BRACKET) {
            // If the next token is a left bracket, parse the enclosed expression as an index.
            helper.MatchAndRemove(Token.TokenType.LEFT_BRACKET);
            var node = ParseOperation();

            if (helper.Peek(0).get().getType() == Token.TokenType.RIGHT_BRACKETS)
                helper.MatchAndRemove(Token.TokenType.RIGHT_BRACKETS);
            else
                throw new AwkException("Need another right bracket to complete the statement");

            return Optional.of(new VariableReferenceNode(name, node));
        }

        return Optional.of(new VariableReferenceNode(name, Optional.empty()));
    } 
    

    // If none of the above conditions match, return an empty Optional.
    return Optional.empty();
}

    // Parse function parameters
    private LinkedList<Token> ParseParameters() throws AwkException {

        LinkedList<Token> parameters = new LinkedList<>();
        if (helper.Peek(0).get().getType() == Token.TokenType.LEFT_PAREN) {
            helper.MatchAndRemove(Token.TokenType.LEFT_PAREN);
            while (helper.Peek(0).get().getType() != Token.TokenType.RIGHT_PAREN) {
                if (helper.Peek(0).get().getType() == Token.TokenType.COMMA) {
                    helper.MatchAndRemove(Token.TokenType.COMMA);
                } else {
                    parameters.add(helper.Peek(0).get());
                    helper.MatchAndRemove(helper.Peek(0).get().getType());
                }
            }
            helper.MatchAndRemove(Token.TokenType.RIGHT_PAREN);
        } else
            throw new AwkException("Function is incomplete; it needs parentheses");
        return parameters;
    }
    private LinkedList<Node> ParseNodeParameters() throws AwkException {

        LinkedList<Node> parameters = new LinkedList<>();

        if (helper.Peek(0).get().getType() == Token.TokenType.LEFT_PAREN) {
            helper.MatchAndRemove(Token.TokenType.LEFT_PAREN);
            while (helper.Peek(0).get().getType() != Token.TokenType.RIGHT_PAREN) {
                if (helper.Peek(0).get().getType() == Token.TokenType.COMMA) {
                    helper.MatchAndRemove(Token.TokenType.COMMA);
                } else {
                    var Pnode = ParseOperation();
                    if(Pnode.isPresent()){
                    parameters.add(Pnode.get());
                    }
                }
            }
            helper.MatchAndRemove(Token.TokenType.RIGHT_PAREN);
        } else
            throw new AwkException("Function is incomplete; it needs parentheses");
        return parameters;
    }
    private boolean isBuiltIn(Token.TokenType type){

            HashMap<Token.TokenType,String> builtMap = new HashMap<>();

            builtMap.put(Token.TokenType.PRINT, "print");
            builtMap.put(Token.TokenType.LENGTH, "length");
            builtMap.put(Token.TokenType.INDEX, "index");
            builtMap.put(Token.TokenType.SUBSTR, "substr");
            builtMap.put(Token.TokenType.SPLIT, "spit");
            builtMap.put(Token.TokenType.GSUB, "gsub");
            builtMap.put(Token.TokenType.SPRINTF, "sprintf");
            builtMap.put(Token.TokenType.TOLOWER, "tolower");
            builtMap.put(Token.TokenType.TOUPPER, "toupper");
            builtMap.put(Token.TokenType.PRINTF, "toupper");
            builtMap.put(Token.TokenType.SUB, "tolower");
            builtMap.put(Token.TokenType.NEXT, "toupper");
            builtMap.put(Token.TokenType.MATCH, "toupper");


        return builtMap.containsKey(type);
    }
    private FunctionCallNode ParseFunctionCall() throws AwkException {


         if(helper.Peek(0).get().getType() == Token.TokenType.WORD) {               
            
                   
            String name = helper.MatchAndRemove(Token.TokenType.WORD).get().getValue().toString();

              var paralist = ParseNodeParameters();

            return (new FunctionCallNode(name, paralist));
         }
         else if(helper.Peek(0).get().getType() == Token.TokenType.PRINT) {               
            
            String name = helper.MatchAndRemove(Token.TokenType.PRINT).get().getValue().toString();

              var paralist = ParseNodeParameters();

            return (new FunctionCallNode(name, paralist));
         }
         else if(helper.Peek(0).get().getType() == Token.TokenType.PRINTF) {               
            
                   
            String name = helper.MatchAndRemove(Token.TokenType.PRINTF).get().getValue().toString();

              var paralist = ParseNodeParameters();

            return (new FunctionCallNode(name, paralist));
         }
         else if(helper.Peek(0).get().getType() == Token.TokenType.LENGTH) {               
            
                   
            String name = helper.MatchAndRemove(Token.TokenType.LENGTH).get().getValue().toString();

              var paralist = ParseNodeParameters();

            return (new FunctionCallNode(name, paralist));
         }
         else if(helper.Peek(0).get().getType() == Token.TokenType.INDEX) {               
            
                   
            String name = helper.MatchAndRemove(Token.TokenType.INDEX).get().getValue().toString();

              var paralist = ParseNodeParameters();

            return (new FunctionCallNode(name, paralist));
         }
         else if(helper.Peek(0).get().getType() == Token.TokenType.SUBSTR) {               
            
                   
            String name = helper.MatchAndRemove(Token.TokenType.SUBSTR).get().getValue().toString();

              var paralist = ParseNodeParameters();

            return (new FunctionCallNode(name, paralist));
         }
         else if(helper.Peek(0).get().getType() == Token.TokenType.SPLIT) {               
            
                   
            String name = helper.MatchAndRemove(Token.TokenType.SPLIT).get().getValue().toString();

              var paralist = ParseNodeParameters();

            return (new FunctionCallNode(name, paralist));
         }
         else if(helper.Peek(0).get().getType() == Token.TokenType.GSUB) {               
        
                   
            String name = helper.MatchAndRemove(Token.TokenType.GSUB).get().getValue().toString();

              var paralist = ParseNodeParameters();

              return (new FunctionCallNode(name, paralist));
         }
         else if(helper.Peek(0).get().getType() == Token.TokenType.SPRINTF) {               
            
                   
            String name = helper.MatchAndRemove(Token.TokenType.SPRINTF).get().getValue().toString();

              var paralist = ParseNodeParameters();

            return (new FunctionCallNode(name, paralist));
         }
         else if(helper.Peek(0).get().getType() == Token.TokenType.TOLOWER) {               
            
                   
            String name = helper.MatchAndRemove(Token.TokenType.TOLOWER).get().getValue().toString();

              var paralist = ParseNodeParameters();

            return (new FunctionCallNode(name, paralist));
         }
         else if(helper.Peek(0).get().getType() == Token.TokenType.TOUPPER) {               
            
                   
            String name = helper.MatchAndRemove(Token.TokenType.TOUPPER).get().getValue().toString();

              var paralist = ParseNodeParameters();

            return (new FunctionCallNode(name, paralist));
         }
         else if(helper.Peek(0).get().getType() == Token.TokenType.NEXT) {               
            
                   
            String name = helper.MatchAndRemove(Token.TokenType.NEXT).get().getValue().toString();

              var paralist = ParseNodeParameters();

            return (new FunctionCallNode(name, paralist));
         }
         else if(helper.Peek(0).get().getType() == Token.TokenType.MATCH) {               
            
                   
            String name = helper.MatchAndRemove(Token.TokenType.MATCH).get().getValue().toString();

              var paralist = ParseNodeParameters();

            return (new FunctionCallNode(name, paralist));
         }
          else if(helper.Peek(0).get().getType() == Token.TokenType.SUB) {               
            
                   
            String name = helper.MatchAndRemove(Token.TokenType.SUB).get().getValue().toString();

              var paralist = ParseNodeParameters();

            return (new FunctionCallNode(name, paralist));
         }
         return null;


    }
    private IfNode ParseIf() throws AwkException {
        helper.MatchAndRemove(Token.TokenType.IF); // Match and remove the "IF" token
    
        // Check if the next token is a left parenthesis
        if (helper.Peek(0).get().getType() == Token.TokenType.LEFT_PAREN) {
            helper.MatchAndRemove(Token.TokenType.LEFT_PAREN); // Consume the left parenthesis
    
            // Parse the condition as an operation
            var condition = ParseOperation();
    
            // Check if the condition is present (not empty)
            if (!condition.isPresent()) {
                throw new AwkException("Needs a valid condition; it can't be empty");
            }
    
            helper.MatchAndRemove(Token.TokenType.RIGHT_PAREN); // Consume the right parenthesis
    
            // Parse a block of statements for the "if" branch
            var block = ParseBlock();
    
            // Check if the block is null (not a valid "if" statement)
            if (block == null) {
                throw new AwkException("Not a valid If statement, needs a statement");
            }
    
            // Check if there's no token left to parse
            if (!helper.Peek(0).isPresent()) {
                return new IfNode(condition.get(), block);
            }
    
            // Check if there's an "ELSE" token
            if (helper.Peek(0).get().getType() == Token.TokenType.ELSE) {
                helper.MatchAndRemove(Token.TokenType.ELSE); // Consume the "ELSE" token
    
                // Check if the next token is "IF" (for "else if" condition)
                if (helper.Peek(0).get().getType() == Token.TokenType.IF) {
                    helper.MatchAndRemove(Token.TokenType.IF);
    
                    // Parse an "if" node recursively (for "else if" condition)
                    var NodeIf = ParseIf();
    
                    return new IfNode(condition.get(), block, NodeIf);
                }
    
                // Parse a block of statements for the "else" branch
                var elseBlock = ParseBlock();
    
                // Check if the "else" block is null
                if (elseBlock == null) {
                    throw new AwkException("Not a valid else statement, needs a statement");
                }
    
                return new IfNode(condition.get(), block, elseBlock);
            } else {
                // Return an IfNode with just the "if" condition and block
                return new IfNode(condition.get(), block);
            }
        } else {
            // Throw an exception if the left and right parentheses are not found
            throw new AwkException("This statement needs left and right parentheses");
        }
    } 
    private DoWhileNode ParseDo() throws AwkException{

              helper.MatchAndRemove(Token.TokenType.DO);

              Optional<Node> condition;
              Node FirstRun;

                   FirstRun = ParseBlock();
                       if(FirstRun == null)
                           throw new AwkException("Not a vaild If statement, needs a statement");


             if(helper.Peek(0).get().getType() == Token.TokenType.WHILE){
                              helper.MatchAndRemove(Token.TokenType.WHILE);


                  if(helper.Peek(0).get().getType() == Token.TokenType.LEFT_PAREN){
                      helper.MatchAndRemove(Token.TokenType.LEFT_PAREN);

                        condition = ParseOperation();
                            if(!condition.isPresent())
                               throw new AwkException("Needs a vaild condition can't be empty");
                
                    helper.MatchAndRemove(Token.TokenType.RIGHT_PAREN);
                   
             
              }else
                throw new AwkException("This statement needs left and right paren");  

            }else
               throw new AwkException("This statement needs while token");  

        return new DoWhileNode(condition.get(), FirstRun);
    }
    private WhileNode ParseWhile() throws AwkException{
        helper.MatchAndRemove(Token.TokenType.WHILE); // Match and remove the "WHILE" token

        // Check if the next token is a left parenthesis
        if (helper.Peek(0).get().getType() == Token.TokenType.LEFT_PAREN) {
            helper.MatchAndRemove(Token.TokenType.LEFT_PAREN); // Consume the left parenthesis
        
            // Parse the condition as an operation
            var condition = ParseOperation();
        
            // Check if the condition is present (not empty)
            if (!condition.isPresent()) {
                throw new AwkException("Needs a valid condition; it can't be empty");
            }
        
            helper.MatchAndRemove(Token.TokenType.RIGHT_PAREN); // Consume the right parenthesis
        
            // Parse a block of statements
            var block = ParseBlock();
        
            // Check if the block is null (not a valid "if" statement)
            if (block == null) {
                throw new AwkException("Not a valid If statement, needs a statement");
            }
        
            // Create and return a WhileNode with the condition and block
            return new WhileNode(condition.get(), block);
        } else {
            // Throw an exception if the left and right parentheses are not found
            throw new AwkException("This statement needs left and right parentheses");
        } 
    }
    private BreakNode ParseBreak(){
        helper.MatchAndRemove(Token.TokenType.BREAK);
            return new BreakNode();
        
    }
    private ForNode ParseFor() throws AwkException {
        // Match and remove the "FOR" token
        helper.MatchAndRemove(Token.TokenType.FOR);
    
        // Check if the next token is a left parenthesis
        if (helper.Peek(0).get().getType() == Token.TokenType.LEFT_PAREN) {
            // Consume the left parenthesis
            helper.MatchAndRemove(Token.TokenType.LEFT_PAREN);
    
            boolean isInArray = false;
    
            // Iterate through the tokens looking for  "in"
            for (int i = 0; helper.Peek(i).isPresent(); i++) {
                if (helper.Peek(0).get().getType() == Token.TokenType.IN)
                    isInArray = true;
    
                try {
                    if (helper.Peek(i + 1).get().getType() == Token.TokenType.IN)
                        isInArray = true;
                } catch (Exception e) {
                    
                break;
                }
    
               
            }
    
            // If "in" condition is met, parse an operation for the ForNode
            if (isInArray) {
                var In = ParseOperation();
                if (In.isPresent()) {
                    return new ForNode(In.get());
                } else {
                    throw new AwkException("Incorrect for in");
                }
            }
    
            // Parse an assignment operation
            var Assignment = ParseOperation();
            // Check if the next token is a separator
            if (helper.Peek(0).get().getType() != Token.TokenType.SEMICOLON) {
                throw new AwkException("Needs a colon");
            } else {
                // Consume the separator
                helper.MatchAndRemove(Token.TokenType.SEMICOLON);
            }
    
            // Parse a condition operation
            var Condition = ParseOperation();
    
            // Check if the next token is a separator
            if (helper.Peek(0).get().getType() != Token.TokenType.SEMICOLON) {
                throw new AwkException("Needs a colon");
            } else {
                // Consume the separator
                helper.MatchAndRemove(Token.TokenType.SEMICOLON);
            }
    
            // Parse an iteration operation
            var Iteration = ParseOperation();
    
            // Check if the next token is a right parenthesis
            if (helper.Peek(0).get().getType() != Token.TokenType.RIGHT_PAREN) {
                throw new AwkException("Needs a right parenthesis");
            } else {
                // Consume the right parenthesis
                helper.MatchAndRemove(Token.TokenType.RIGHT_PAREN);
            }
    
            // Parse a block of statements
            var block = ParseBlock();
    
            // Check if the block is null (not a valid "if" statement)
            if (block == null) {
                throw new AwkException("Not a valid If statement, needs a statement");
            }
    
            // Create and return a ForNode with Assignment, Condition, Iteration, and the optional block
            return new ForNode(Assignment, Condition, Iteration, Optional.of(block));
        } else {
            // Throw an exception if the left and right parentheses are not found
            throw new AwkException("This for statement needs left and right parentheses");
        }
    }
    
    private ContinueNode ParseContine() throws AwkException {
    helper.MatchAndRemove(Token.TokenType.CONTINUE); // Match and remove the "CONTINUE" token
    return new ContinueNode(); // Create and return a new ContinueNode
}

private ReturnNode ParseReturn() throws AwkException {
    helper.MatchAndRemove(Token.TokenType.RETURN); // Match and remove the "RETURN" token

    // Parse an operation for the return value
    var ParseOp = ParseOperation();

    // Check if the operation is not empty
    if (!ParseOp.isEmpty()) {
        return new ReturnNode(ParseOp.get()); // Create and return a new ReturnNode with the parsed operation
    } else {
        throw new AwkException("Needs a return statement"); // Throw an exception if no return value is provided
    }
}

private DeleteNode ParseDelete() throws AwkException {
    helper.MatchAndRemove(Token.TokenType.DELETE); // Match and remove the "DELETE" token

    // Parse an operation for the item to be deleted
    var node = ParseOperation();

    // Check if the operation is not empty
    if (!node.isPresent()) {
        throw new AwkException("Needs a valid statement; it can't be empty"); // Throw an exception if no statement is provided
    }

    return new DeleteNode(node.get()); // Create and return a new DeleteNode with the parsed operation
}

}
