import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;

import org.junit.Test;


public class AwkJunit{
// Lexer
@Test
public void TestLexMethodOneWORD() throws Exception{

    //This Unit test takes in the string while and the lex method is expected to return the Token list (hello ,SEPARTOR)
       Lexer lex = new Lexer("hello");
       //The expected Tokens
       ArrayList<Token> TestingTokens = new ArrayList<>();
       TestingTokens.add(new Token(Token.TokenType.WORD, "hello", 1, 1));
       TestingTokens.add(new Token(Token.TokenType.SEPARATOR, "", 1, 2));


    assertEquals(TestingTokens.toString(), lex.Lex().toString());

}
@Test
public void TestLexMethodOneMUTILINE() throws Exception{

    //This Unit test takes in the string (311 \n hello \n world) while and the lex method is expected to return the Token list (311\n, hello\n ,SEPARTOR)
       Lexer lex = new Lexer("311 \n hello \n world");
       //The expected Tokens
       ArrayList<Token> TestingTokens = new ArrayList<>();
       TestingTokens.add(new Token(Token.TokenType.NUMBER, "311", 1, 1));
       TestingTokens.add(new Token(Token.TokenType.SEPARATOR, "", 1, 2));
       TestingTokens.add(new Token(Token.TokenType.WORD, "hello", 1, 1));
       TestingTokens.add(new Token(Token.TokenType.SEPARATOR, "", 1, 2));
       TestingTokens.add(new Token(Token.TokenType.WORD, "world", 1, 1));
       TestingTokens.add(new Token(Token.TokenType.SEPARATOR, "", 1, 2));


    assertEquals(TestingTokens.toString(), lex.Lex().toString());

}
@Test
public void TestProcessLettersAndNumbers() throws Exception{

    //This Unit test takes in the string (311ready hell311o wor56ld) while and the lex method is expected to return the Token list (311,ready, hell311o, wor56ld ,SEPARTOR)
       Lexer lex = new Lexer("311ready hell311o wor56ld");
       //The expected Tokens
       ArrayList<Token> TestingTokens = new ArrayList<>();
       TestingTokens.add(new Token(Token.TokenType.NUMBER, "311", 1, 1));
       TestingTokens.add(new Token(Token.TokenType.WORD, "ready", 1, 1));
       TestingTokens.add(new Token(Token.TokenType.WORD, "hell311o", 1, 1));
       TestingTokens.add(new Token(Token.TokenType.WORD, "wor56ld", 1, 1));
       TestingTokens.add(new Token(Token.TokenType.SEPARATOR, "", 1, 2));


    assertEquals(TestingTokens.toString(), lex.Lex().toString());

}
@Test
public void TestFullLine1() throws Exception
        {
            var lexer = new Lexer("$0 = toblower($0)");
            var tokens = lexer.Lex();
            assertEquals(9, tokens.size());
            assertEquals(new Token(Token.TokenType.FIELD_REFERENCE,"$",0, 0).toString(), tokens.get(0).toString());
            assertEquals(new Token(Token.TokenType.NUMBER,"0",0, 0).toString(), tokens.get(1).toString());
            assertEquals(new Token(Token.TokenType.EQUAL,"=",0, 0).toString(), tokens.get(2).toString());
            assertEquals(new Token(Token.TokenType.WORD,"toblower",0, 0).toString(), tokens.get(3).toString());
            assertEquals(new Token(Token.TokenType.LEFT_PAREN,"(",0, 0).toString(), tokens.get(4).toString());
            assertEquals(new Token(Token.TokenType.FIELD_REFERENCE,"$",0, 0).toString(), tokens.get(5).toString());
            assertEquals(new Token(Token.TokenType.NUMBER,"0",0, 0).toString(), tokens.get(6).toString());
            assertEquals(new Token(Token.TokenType.RIGHT_PAREN,")",0, 0).toString(), tokens.get(7).toString());
            assertEquals(new Token(Token.TokenType.SEPARATOR,0, 0).toString(), tokens.get(8).toString());
            
        }
        @Test
public void TestFullLine2() throws Exception
        {
            var lexer = new Lexer("print($p) \n Hello \n \"thisastring\" ");
            var tokens = lexer.Lex();
            assertEquals(10, tokens.size());
            assertEquals(new Token(Token.TokenType.PRINT,"print",0, 0).toString(), tokens.get(0).toString());
            assertEquals(new Token(Token.TokenType.LEFT_PAREN,"(",0, 0).toString(), tokens.get(1).toString());
            assertEquals(new Token(Token.TokenType.FIELD_REFERENCE,"$",0, 0).toString(), tokens.get(2).toString());
            assertEquals(new Token(Token.TokenType.WORD,"p",0, 0).toString(), tokens.get(3).toString());
            assertEquals(new Token(Token.TokenType.RIGHT_PAREN,")",0, 0).toString(), tokens.get(4).toString());
            assertEquals(new Token(Token.TokenType.SEPARATOR,0, 0).toString(), tokens.get(5).toString());
            assertEquals(new Token(Token.TokenType.WORD,"Hello",0, 0).toString(), tokens.get(6).toString());
            assertEquals(new Token(Token.TokenType.SEPARATOR,0, 0).toString(), tokens.get(7).toString());
            assertEquals(new Token(Token.TokenType.STRINGLITERAL, "thisastring",0, 0).toString(), tokens.get(8).toString());
            assertEquals(new Token(Token.TokenType.SEPARATOR,0, 0).toString(), tokens.get(9).toString());
            
        }
        @Test
public void TestFullLine3() throws Exception
        {
            var lexer = new Lexer("5 - 6 + (5 * 7)");
            var tokens = lexer.Lex();
            assertEquals(10, tokens.size());
            assertEquals(new Token(Token.TokenType.NUMBER,"5",0, 0).toString(), tokens.get(0).toString());
            assertEquals(new Token(Token.TokenType.SUBTRACTION,"-",0, 0).toString(), tokens.get(1).toString());
            assertEquals(new Token(Token.TokenType.NUMBER,"6",0, 0).toString(), tokens.get(2).toString());
            assertEquals(new Token(Token.TokenType.ADDITION,"+",0, 0).toString(), tokens.get(3).toString());
            assertEquals(new Token(Token.TokenType.LEFT_PAREN,"(",0, 0).toString(), tokens.get(4).toString());
            assertEquals(new Token(Token.TokenType.NUMBER, "5",0, 0).toString(), tokens.get(5).toString());
            assertEquals(new Token(Token.TokenType.MULTIPLICATION,"*",0, 0).toString(), tokens.get(6).toString());
            assertEquals(new Token(Token.TokenType.NUMBER, "7",0, 0).toString(), tokens.get(7).toString());
            assertEquals(new Token(Token.TokenType.RIGHT_PAREN, ")",0, 0).toString(), tokens.get(8).toString());
            assertEquals(new Token(Token.TokenType.SEPARATOR,0, 0).toString(), tokens.get(9).toString());
            
        }
        @Test

//Parser
public void TestTokenManager() throws AwkException{
        var lexer = new Lexer("function factorial(f, 7, 9){ }");
        var tokens = lexer.Lex();                                   // Full of Seperator tokens tokens
        var Tmanager = new TokenManager(tokens);

        String FunctionToken = Tmanager.Peek(0).get().getType().toString();
        assertEquals(Token.TokenType.FUNCTION.toString(), FunctionToken);          //Test peek
        assertEquals(true, Tmanager.MoreTokens());                        //Test MoreTokens
}
      @Test
public void TestAcceptSeperators() throws AwkException{
        var lexer = new Lexer("\n \n \n \n \n \n \n");
        var tokens = lexer.Lex();                                   // Full of Seperator tokens tokens
        var separators = new TokenManager(tokens);

        while(separators.AcceptSeperators());
        assertEquals(false, separators.MoreTokens());      // Are all the sperators gone

}
       @Test
public void TestParser() throws AwkException {

        // Create a lexer to tokenize the input string
        var lexer = new Lexer("function factorial(f, 7, 9){ }");
    
        // Tokenize the input string
        var tokens = lexer.Lex(); // Full of Separator tokens tokens
    
        // Create a parser and initialize program and other data structures
        var parse = new Parser(tokens);
        var program = new ProgramNode();
        var par = new LinkedList<Node>();
        var block = new LinkedList<StatementNode>();
    
        // Create tokens for function parameters
        par.add(new VariableReferenceNode("f", Optional.empty()));
        par.add(new ConstantNode(7));
        par.add(new ConstantNode(9));
    
        // Create a placeholder for the function body (block)
        block.add(new BlockNode());
    
        // Create a FunctionDefinitionNode and add it to the program
        program.getFnode().add(new FunctionDefinitionNode("factorial", par, block));
    
        // Assert that the parsed program matches the expected program
        assertEquals(program.toString(), parse.program().toString());
    
    }
       @Test
public void TestMatchandRemove() throws AwkException{
        var lexer = new Lexer("1 3 4 while for { }");
        var tokens = lexer.Lex();  
        var tok = new TokenManager(tokens);

        tok.MatchAndRemove(Token.TokenType.NUMBER);
        tok.MatchAndRemove(Token.TokenType.NUMBER);                                
        tok.MatchAndRemove(Token.TokenType.NUMBER);                                
        tok.MatchAndRemove(Token.TokenType.WHILE);                                
        tok.MatchAndRemove(Token.TokenType.FOR);                                
        tok.MatchAndRemove(Token.TokenType.LEFT_CURLY_BRACKETS);                               
        tok.MatchAndRemove(Token.TokenType.RIGHT_CURLY_BRACKETS);
        tok.MatchAndRemove(Token.TokenType.SEPARATOR);
        assertEquals(false, tok.MoreTokens()); 
        
}     

//Parser 2

// Test case to check if parsing a single constant value (e.g., "1") results in a ConstantNode.
@Test
public void TestyParseOperation1() throws AwkException {
    // Create a lexer for the input string "1".
    var lexer = new Lexer(" 1 ");
    var tokens = lexer.Lex();  
    var parse = new Parser(tokens);

    // Parse the operation and get the resulting node.
    var node = parse.ParseOperation().get();

    // Check if the parsed node is an instance of ConstantNode.
    boolean same = node instanceof ConstantNode;
    assertEquals(true, same);

    // Check if the parsed ConstantNode matches the expected ConstantNode with a value of 1.
    assertEquals(new ConstantNode(1).toString(), node.toString());
}
// Test case to check if parsing a variable reference with an index (e.g., "a[67]") results in a VariableReferenceNode.
@Test
public void TestyParseOperation2() throws AwkException {
    // Create a lexer for the input string "a[67]".
    var lexer = new Lexer(" a[67] ");
    var tokens = lexer.Lex();  
    var parse = new Parser(tokens);

    // Parse the operation and get the resulting node.
    var node = parse.ParseOperation().get();

    // Check if the parsed node is an instance of VariableReferenceNode.
    boolean same = node instanceof VariableReferenceNode;
    assertEquals(true, same);

    // Check if the parsed VariableReferenceNode matches the expected node with the variable name "a" and index 67.
    assertEquals(new VariableReferenceNode("a", Optional.of(new ConstantNode(67))).toString(), node.toString());
}

// Test case to check if parsing a field reference (e.g., "$7") results in an OperationNode.
@Test
public void TestyParseOperation3() throws AwkException {
    // Create a lexer for the input string "$7".
    var lexer = new Lexer(" $7 ");
    var tokens = lexer.Lex();  
    var parse = new Parser(tokens);

    // Parse the operation and get the resulting node.
    var node = parse.ParseOperation().get();

    // Check if the parsed node is an instance of OperationNode.
    boolean same = node instanceof OperationNode;
    assertEquals(true, same);

    // Check if the parsed OperationNode matches the expected node representing field reference to field 7.
    assertEquals(new OperationNode(new ConstantNode(7), OperationNode.Operation.FIELD_REFERENCE).toString(), node.toString());
}

// Test case to check if parsing a variable reference with a field reference as an index (e.g., "e[$7]") results in a VariableReferenceNode.
@Test
public void TestyParseOperation4() throws AwkException {
    // Create a lexer for the input string "e[$7]".
    var lexer = new Lexer(" e[$7] ");
    var tokens = lexer.Lex();  
    var parse = new Parser(tokens);

    // Parse the operation and get the resulting node.
    var node = parse.ParseOperation().get();

    // Check if the parsed node is an instance of VariableReferenceNode.
    boolean same = node instanceof VariableReferenceNode;
    assertEquals(true, same);

    // Check if the parsed VariableReferenceNode matches the expected node with the variable name "e" and a field reference as an index.
    assertEquals(new VariableReferenceNode("e", Optional.of(new OperationNode(new ConstantNode(7), OperationNode.Operation.FIELD_REFERENCE))).toString(), node.toString());
}

// Test case to check if parsing a pattern within backticks (e.g., "`a[err]`") results in a PatternNode.
@Test
public void TestyParseOperation5() throws AwkException {
    // Create a lexer for the input string "`a[err]`".
    var lexer = new Lexer(" `a[err]` ");
    var tokens = lexer.Lex();  
    var parse = new Parser(tokens);

    // Parse the operation and get the resulting node.
    var node = parse.ParseOperation().get();

    // Check if the parsed node is an instance of PatternNode.
    boolean same = node instanceof PatternNode;
    assertEquals(true, same);

    // Check if the parsed PatternNode matches the expected node with the pattern "a[err]".
    assertEquals(new PatternNode("a[err]").toString(), node.toString());
}


//Parser 3

@Test
public void TestyyParseOperation() throws AwkException {
    var lexer = new Lexer(" 4 + 7 / 7 ");
    var tokens = lexer.Lex();  
    var parse = new Parser(tokens);

    // Parse the operation and get the resulting node.
    var node = parse.ParseOperation().get();

    // Check if the parsed node is an instance of OperationNode.
    boolean same = node instanceof OperationNode;
    assertEquals(true, same);

     assertEquals(new OperationNode(new ConstantNode(4), Optional.of(new OperationNode(new ConstantNode(7),
     Optional.of(new ConstantNode(7)), OperationNode.Operation.DIVISION)), 
     OperationNode.Operation.ADDITION).toString(), node.toString());

}

@Test
public void TestyyParseOperation2() throws AwkException {

    var lexer = new Lexer("++ a");
    var tokens = lexer.Lex();  
    var parse = new Parser(tokens);

    // Parse the operation and get the resulting node.
    var node = parse.ParseOperation().get
    ();

    // Check if the parsed node is an instance of OperationNode.
    boolean same = node instanceof OperationNode;
    assertEquals(true, same);

     assertEquals(new OperationNode(
        new VariableReferenceNode("a", Optional.empty()), OperationNode.Operation.PRE_INCREMENT).toString(), node.toString());

  }

  @Test
public void TestyyParseOperation3() throws AwkException {

    var lexer = new Lexer("(2^3)^4 ");
    var tokens = lexer.Lex();  
    var parse = new Parser(tokens);

    // Parse the operation and get the resulting node.
    var node = parse.ParseOperation().get
    ();

    // Check if the parsed node is an instance of OperationNode.
    boolean same = node instanceof OperationNode;
    assertEquals(true, same);

     assertEquals(new OperationNode(
        new OperationNode(new ConstantNode(2),
        Optional.of(new ConstantNode(3)), 
        OperationNode.Operation.EXPONENT),Optional.of(new ConstantNode(4)), OperationNode.Operation.EXPONENT).toString()
        
        , node.toString());

  }
  
  @Test
  public void TestyyParseOperation4() throws AwkException {
      // Create a lexer for the expression "4 * 6 - 2 / 4^2".
      var lexer = new Lexer("4 * 6 - 2 / 4^2");
      var tokens = lexer.Lex();
  
      // Create a parser and parse the operation to obtain the syntax tree node.
      var parse = new Parser(tokens);
      var node = parse.ParseOperation().get();
  
      // Check if the parsed node is an instance of OperationNode.
      boolean isOperationNode = node instanceof OperationNode;
      assertEquals(true, isOperationNode);
  
      // Compare the parsed node to the expected syntax tree structure.
      assertEquals(new OperationNode(
          new OperationNode(new ConstantNode(4), Optional.of(new ConstantNode(6)), OperationNode.Operation.MULTIPLICATION),
          Optional.of(new OperationNode(new ConstantNode(2), Optional.of(new OperationNode(new ConstantNode(4),
              Optional.of(new ConstantNode(2)), OperationNode.Operation.EXPONENT)), OperationNode.Operation.DIVISION)),
          OperationNode.Operation.SUBTRACTION).toString(), node.toString());
  }
  
  @Test
  public void TestyyParseOperation5() throws AwkException {
      // Create a lexer for the expression "a = 34 + 4".
      var lexer = new Lexer("a = 34 + 4");
      var tokens = lexer.Lex();
  
      // Create a parser and parse the operation to obtain the syntax tree node.
      var parse = new Parser(tokens);
      var node = parse.ParseOperation().get();
  
      // Check if the parsed node is an instance of AssignmentNode.
      boolean isAssignmentNode = node instanceof AssignmentNode;
      assertEquals(true, isAssignmentNode);
  
      // Compare the parsed node to the expected syntax tree structure.
      assertEquals(new AssignmentNode(
          new VariableReferenceNode("a", Optional.empty()),
          new OperationNode(new ConstantNode(34), Optional.of(new ConstantNode(4)), OperationNode.Operation.ADDITION),
          OperationNode.Operation.ASSIGNMENT).toString(), node.toString());
  }
  
  @Test
  public void TestyyParseOperation6() throws AwkException {
      // Create a lexer for the expression "a *= 34 + 4".
      var lexer = new Lexer("a *= 34 + 4");
      var tokens = lexer.Lex();
  
      // Create a parser and parse the operation to obtain the syntax tree node.
      var parse = new Parser(tokens);
      var node = parse.ParseOperation().get();
  
      // Check if the parsed node is an instance of AssignmentNode.
      boolean isAssignmentNode = node instanceof AssignmentNode;
      assertEquals(true, isAssignmentNode);
  
      // Compare the parsed node to the expected syntax tree structure.
      assertEquals(new AssignmentNode(
          new VariableReferenceNode("a", Optional.empty()),
          new OperationNode(new ConstantNode(34), Optional.of(new ConstantNode(4)), OperationNode.Operation.ADDITION),
          OperationNode.Operation.MULTIPLICATION_ASSIGNMENT).toString(), node.toString());
  }
  
  @Test
  public void TestyyParseOperation7() throws AwkException {
      // Create a lexer for the expression "3 == 2 ? a = 1 : a = 2".
      var lexer = new Lexer(" 3 == 2 ? a = 1 : a = 2");
      var tokens = lexer.Lex();
  
      // Create a parser and parse the operation to obtain the syntax tree node.
      var parse = new Parser(tokens);
      var node = parse.ParseOperation().get();
  
      // Check if the parsed node is an instance of TernaryNode.
      boolean isTernaryNode = node instanceof TernaryNode;
      assertEquals(true, isTernaryNode);
  
      // Compare the parsed node to the expected syntax tree structure.
      assertEquals(new TernaryNode(new OperationNode(new ConstantNode(3), Optional.of(new ConstantNode(2)),
          OperationNode.Operation.LOGICAL_EQUAL),
          new AssignmentNode(new VariableReferenceNode("a", Optional.empty()), new ConstantNode(1),
              OperationNode.Operation.ASSIGNMENT),
          new AssignmentNode(new VariableReferenceNode("a", Optional.empty()), new ConstantNode(2),
              OperationNode.Operation.ASSIGNMENT)).toString(), node.toString());
  }



  // Parser 4
   @Test
  public void TestFunctionCall() throws AwkException {
      var lexer = new Lexer(" a(1, 12, 13)");
      var tokens = lexer.Lex();
  
      var parse = new Parser(tokens);
      var node = parse.ParseOperation().get();
  
      boolean isTernaryNode = node instanceof FunctionCallNode;
      assertEquals(true, isTernaryNode);
  
      var paralist = new LinkedList<Node>();
       paralist.add(new ConstantNode(1));
       paralist.add(new ConstantNode(12));
       paralist.add(new ConstantNode(13));

      // Compare the parsed node to the expected syntax tree structure.
      assertEquals(new FunctionCallNode("a", paralist).toString(), node.toString());
  }
   @Test
  public void TestParseIf() throws AwkException {
      var lexer = new Lexer(" if(n == 1) { a += 1 }");
      var tokens = lexer.Lex();
      var parse = new Parser(tokens);

      var node = parse.ParseStatement();
  
      boolean isTernaryNode = node instanceof IfNode;
      assertEquals(true, isTernaryNode);
  
      var block = new BlockNode();
      block.getSnode().add(new AssignmentNode(new VariableReferenceNode("a", Optional.empty()), (new ConstantNode(1)),OperationNode.Operation.ADDITION_ASSIGNMENT));
       

      // Compare the parsed node to the expected syntax tree structure.
      assertEquals(new IfNode(new OperationNode(new VariableReferenceNode("n", Optional.empty()), Optional.of(new ConstantNode(1)), OperationNode.Operation.LOGICAL_EQUAL), 
      block.getSnode()).toString(), node.toString());
  }
    @Test
  public void TestParseWhile() throws AwkException {
      var lexer = new Lexer("while( 3 == 3) { a = 3 n = 4}");
      var tokens = lexer.Lex();
      var parse = new Parser(tokens);

      var node = parse.ParseStatement();
  
      boolean isTernaryNode = node instanceof WhileNode;
      assertEquals(true, isTernaryNode);
  
      var block = new BlockNode();
      block.getSnode().add(new AssignmentNode(new VariableReferenceNode("a", Optional.empty()), (new ConstantNode(3)),OperationNode.Operation.ASSIGNMENT));
      block.getSnode().add(new AssignmentNode(new VariableReferenceNode("n", Optional.empty()), (new ConstantNode(4)),OperationNode.Operation.ASSIGNMENT));
       

      // Compare the parsed node to the expected syntax tree structure.
      assertEquals(new WhileNode(new OperationNode(new ConstantNode(3), Optional.of(new ConstantNode(3)), OperationNode.Operation.LOGICAL_EQUAL), 
      block.getSnode()).toString(), node.toString());
 
 
 
    } 
     @Test
  public void TestParseFor() throws AwkException {
      var lexer = new Lexer(" for(a = 1; a > 3; a++){ a+=1}");
      var tokens = lexer.Lex();
      var parse = new Parser(tokens);

      var node = parse.ParseStatement();
  
      boolean isTernaryNode = node instanceof ForNode;
      assertEquals(true, isTernaryNode);
  
      var block = new BlockNode();
      block.getSnode().add(new AssignmentNode(new VariableReferenceNode("a", Optional.empty()), (new ConstantNode(1)),OperationNode.Operation.ADDITION_ASSIGNMENT));
       

      // Compare the parsed node to the expected syntax tree structure.
      assertEquals(new ForNode(Optional.of(new AssignmentNode(new VariableReferenceNode("a", Optional.empty()), new ConstantNode(1), OperationNode.Operation.ASSIGNMENT)),
       Optional.of(new OperationNode(new VariableReferenceNode("a", Optional.empty()), Optional.of(new ConstantNode(3)), OperationNode.Operation.GREATER_THAN)),
       Optional.of(new OperationNode(new VariableReferenceNode("a", Optional.empty()) , OperationNode.Operation.POST_INCREMENT)), 
       (block.getSnode())).toString(), node.toString());
  }



  // Interpter
  @Test
public void TestParseBuiltIn() throws AwkException {
    // Create a lexer, tokenize the input, and parse an operation node
    var lexer = new Lexer("print (3)");
    var tokens = lexer.Lex();
    var parse = new Parser(tokens);

    var node = parse.ParseOperation().get();

    // Check if the parsed node is an instance of FunctionCallNode
    boolean isNodeFunctionCall = node instanceof FunctionCallNode;
    assertEquals(true, isNodeFunctionCall);

    // Create expected parameters and compare the parsed node to the expected syntax tree structure
    var para = new LinkedList<Node>();
    para.add(new ConstantNode(3));
    assertEquals(new FunctionCallNode("print", para).toString(), node.toString());
}

@Test
public void TestParseBuiltIn2() throws AwkException {
    // Create a lexer, tokenize the input, and parse an operation node
    var lexer = new Lexer("substr (a, 3, 7)");
    var tokens = lexer.Lex();
    var parse = new Parser(tokens);

    var node = parse.ParseOperation().get();

    // Check if the parsed node is an instance of FunctionCallNode
    boolean isNodeFunctionCall = node instanceof FunctionCallNode;
    assertEquals(true, isNodeFunctionCall);

    // Create expected parameters and compare the parsed node to the expected syntax tree structure
    var para = new LinkedList<Node>();
    para.add(new VariableReferenceNode("a", Optional.empty()));
    para.add(new ConstantNode(3));
    para.add(new ConstantNode(7));
    assertEquals(new FunctionCallNode("substr", para).toString(), node.toString());
}

@Test
public void TestInterpreter() throws AwkException, IOException {
    // Define the file path and create a lexer, tokenize the input, and parse the program
    var myPath = Paths.get("/Users/gidhome/Desktop/text.txt"); // needs do be "print helloo sub (t)"
    var lexer = new Lexer(" print helloo sub (t)");
    var tokens = lexer.Lex();
    var parse = new Parser(tokens);
    var parsed = parse.program();

    // Create an interpreter and evaluate the global variable "NF"
    Interpreter interpret = new Interpreter(parsed, myPath);
    assertEquals(interpret.getGlobalVariable("NF").toString(), Integer.toString(4).toString());
}

//Interpreter 2

@Test
public void TestgetInterpreterDataType2() throws AwkException, IOException {
    var myPath = Paths.get("/Users/gidhome/Desktop/text.txt");
    var lexer = new Lexer("5");
    var tokens = lexer.Lex();
    var parse = new Parser(tokens);
    var parsed = parse.program();

    // Create an interpreter

    Interpreter interpret = new Interpreter(parsed, myPath);

    var fin = interpret.getInterpreterDataType(parsed.getOther().get(0));

    boolean same = parsed.getOther().get(0) instanceof ConstantNode;
    assertEquals(true, same);

    assertEquals(new InterpreterDataType("5").toString(), fin.toString());
}
@Test
public void TestgetInterpreterDataType3() throws AwkException, IOException {
    var myPath = Paths.get("/Users/gidhome/Desktop/text.txt");
    var lexer = new Lexer(" {a = 1 \n b = 1 \n n = b + a}");
    var tokens = lexer.Lex();
    var parse = new Parser(tokens);
    var parsed = parse.program();

    // Create an interpreter 

    Interpreter interpret = new Interpreter(parsed, myPath);
    for( Node lines : parsed.getOther()){
        BlockNode block = (BlockNode)lines;
         for( Node line : block.getSnode())
            interpret.getInterpreterDataType(line);
}

    assertEquals(new InterpreterDataType("2.0").toString(), interpret.getGlobalVariable("n").toString());
}
@Test
public void TestgetInterpreterDataType4() throws AwkException, IOException {
    var myPath = Paths.get("/Users/gidhome/Desktop/text.txt");
    var lexer = new Lexer(" {a = 3 + 3 - 3 * 3 / 3}");
    var tokens = lexer.Lex();
    var parse = new Parser(tokens);
    var parsed = parse.program();

    // Create an interpreter

    Interpreter interpret = new Interpreter(parsed, myPath);
     for( Node lines : parsed.getOther()){
        BlockNode block = (BlockNode)lines;
         for( Node line : block.getSnode())
            interpret.getInterpreterDataType(line);
}
  
    assertEquals(new InterpreterDataType("3.0").toString(), interpret.getGlobalVariable("a").toString());
}
@Test
public void TestgetInterpreterDataType5() throws AwkException, IOException {
    var myPath = Paths.get("/Users/gidhome/Desktop/text.txt");
    var lexer = new Lexer(" {a = 7 >= 7}");
    var tokens = lexer.Lex();
    var parse = new Parser(tokens);
    var parsed = parse.program();

    // Create an interpreter 

    Interpreter interpret = new Interpreter(parsed, myPath);
     for( Node lines : parsed.getOther()){
        BlockNode block = (BlockNode)lines;
         for( Node line : block.getSnode())
            interpret.getInterpreterDataType(line);
}
  
    assertEquals(new InterpreterDataType("true").toString(), interpret.getGlobalVariable("a").toString());
}
@Test
public void TestgetInterpreterDataType6() throws AwkException, IOException {
    var myPath = Paths.get("/Users/gidhome/Desktop/text.txt");
    var lexer = new Lexer(" {a = \"hey\" == \" her\"}");
    var tokens = lexer.Lex();
    var parse = new Parser(tokens);
    var parsed = parse.program();

    // Create an interpreter 

    Interpreter interpret = new Interpreter(parsed, myPath);
     for( Node lines : parsed.getOther()){
        BlockNode block = (BlockNode)lines;
         for( Node line : block.getSnode())
            interpret.getInterpreterDataType(line);
}
  
    assertEquals(new InterpreterDataType("false").toString(), interpret.getGlobalVariable("a").toString());
}
@Test
public void TestgetInterpreterDataType7() throws AwkException, IOException {
    var myPath = Paths.get("/Users/gidhome/Desktop/text.txt");
    var lexer = new Lexer(" {a = 45>=45}");
    var tokens = lexer.Lex();
    var parse = new Parser(tokens);
    var parsed = parse.program();


    Interpreter interpret = new Interpreter(parsed, myPath);
     for( Node lines : parsed.getOther()){
        BlockNode block = (BlockNode)lines;
         for( Node line : block.getSnode())
            interpret.getInterpreterDataType(line);
}
  
    assertEquals(new InterpreterDataType("true").toString(), interpret.getGlobalVariable("a").toString());
}

@Test
public void TestgetInterpreterDataType9() throws AwkException, IOException {
    var myPath = Paths.get("/Users/gidhome/Desktop/text.txt");
    var lexer = new Lexer(" {a = \"he\" \"\" \"he\" }");
    var tokens = lexer.Lex();
    var parse = new Parser(tokens);
    var parsed = parse.program();


    Interpreter interpret = new Interpreter(parsed, myPath);
     for( Node lines : parsed.getOther()){
        BlockNode block = (BlockNode)lines;
         for( Node line : block.getSnode())
            interpret.getInterpreterDataType(line);
}
  
    assertEquals(new InterpreterDataType("hehe").toString(), interpret.getGlobalVariable("a").toString());
}

@Test
public void TestgetInterpreterDataType10() throws AwkException, IOException {
    var myPath = Paths.get("/Users/gidhome/Desktop/text.txt");
    var lexer = new Lexer(" { a = \"hey\" ~ `hey }");
    var tokens = lexer.Lex();
    var parse = new Parser(tokens);
    var parsed = parse.program();


    Interpreter interpret = new Interpreter(parsed, myPath);
     for( Node lines : parsed.getOther()){
        BlockNode block = (BlockNode)lines;
         for( Node line : block.getSnode())
            interpret.getInterpreterDataType(line);
     }
     //assertEquals(null, null;
    assertEquals(new InterpreterDataType("true").toString(), interpret.getGlobalVariable("a").toString());
}



//Interpreter 3
@Test
public void TestgetInterpreterStatements() throws AwkException, IOException {
    var myPath = Paths.get("/Users/gidhome/Desktop/text.txt");
    var lexer = new Lexer(" { n = 1 \n a = 20 \n if(n == 1) { a =  100 / a  } }");
    var tokens = lexer.Lex();
    var parse = new Parser(tokens);
    var parsed = parse.program();

    Interpreter interpret = new Interpreter(parsed, myPath);
     for( Node lines : parsed.getOther()){
        BlockNode block = (BlockNode)lines;
         for( StatementNode line : block.getSnode())
            interpret.ProcessStatement(new HashMap<String, InterpreterDataType>(), line);
     }
     //assertEquals(null, null;
    assertEquals(new InterpreterDataType("5.0").toString(), interpret.getGlobalVariable("a").toString());
}
@Test
public void TestgetInterpreterStatements2() throws AwkException, IOException {
    var myPath = Paths.get("/Users/gidhome/Desktop/text.txt");
    var lexer = new Lexer(" { n = 1 \n a = 20 \n if(n == 2) { a =  100 / a  } else { a = 2}}");
    var tokens = lexer.Lex();
    var parse = new Parser(tokens);
    var parsed = parse.program();

    Interpreter interpret = new Interpreter(parsed, myPath);
     for( Node lines : parsed.getOther()){
        BlockNode block = (BlockNode)lines;
         for( StatementNode line : block.getSnode())
            interpret.ProcessStatement(new HashMap<String, InterpreterDataType>(), line);
     }
     
     //assertEquals(null, null;
    assertEquals(new InterpreterDataType("2").toString(), interpret.getGlobalVariable("a").toString());
}

@Test
public void TestgetInterpreterStatements3() throws AwkException, IOException {
    var myPath = Paths.get("/Users/gidhome/Desktop/text.txt");
    var lexer = new Lexer(" { n = 1 \n a = 20 \n while(n < 2) { a =  5 + n \n n++} }");
    var tokens = lexer.Lex();
    var parse = new Parser(tokens);
    var parsed = parse.program();

    Interpreter interpret = new Interpreter(parsed, myPath);
     for( Node lines : parsed.getOther()){
        BlockNode block = (BlockNode)lines;
         for( StatementNode line : block.getSnode())
            interpret.ProcessStatement(new HashMap<String, InterpreterDataType>(), line);
     }
     
     //assertEquals(null, null;
    assertEquals(new InterpreterDataType("6.0").toString(), interpret.getGlobalVariable("a").toString());
}

}
