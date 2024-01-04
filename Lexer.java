import java.util.HashMap;
import java.util.LinkedList;

public class Lexer {

    private LinkedList<Token> Tokens = new LinkedList<Token>();            // ArrayList to store tokens
    private StringHandler helper;                                         // Helper object to manage string processing
    private Integer lineNumber;                                           // Track the current line number
    private Integer position;                                             // Track the current position
    private HashMap<String, Token.TokenType> SingleSymbols;              // Map to store single symbols and their token types
    private HashMap<String, Token.TokenType> DoubleSymbols;              // Map to store double symbols and their token types
    private HashMap<String, Token.TokenType> specialWords;               // Map to store special words and their token types

   public Lexer(String line) {
        this.helper = new StringHandler(line);         // Initialize the helper with the input line
        this.specialWords = addSpecialTokens();        // Populate the map of special words
        this.DoubleSymbols = addDoubleSymbols();       // Populate the map of double symbols
        this.SingleSymbols = addSingleSymbols();       // Populate the map of single symbols
        this.lineNumber = 1;                          // Initialize the line number
        this.position = 1;                            // Initialize the current position
    }

    public LinkedList<Token> Lex() throws AwkException {

        // Loop through the input string
        while (!helper.IsDone()) {

            // Check for newline character
            if (helper.CheckChar() == '\n') {
                helper.Swallow(0);             // Consume the newline character
                Tokens.add(ProcessSeperator());  // Add a separator token
                lineNumber++;                    // Increment line number
                position = 1;                    // Reset position
            }

            // Check for carriage return character
            else if (helper.CheckChar() == '\r') {
                helper.Swallow(0);              // Consume the carriage return character
            }

            // Check for whitespace (space or tab)
            else if (Character.isWhitespace(helper.CheckChar())) {
                helper.Swallow(0);              // Consume whitespace
            }

            // Check for letters or underscores
            else if (Character.isLetter(helper.CheckChar()) || helper.CheckChar() == '_') {
                Tokens.add(ProcessWord());         // Process and add word token
                if (helper.Peek(0) == null)
                    break;
            }

            // Check for digits or underscores
            else if (Character.isDigit(helper.CheckChar()) || helper.CheckChar() == '_' || helper.CheckChar() == '.') {
                Tokens.add(ProcessNumber());        // Process and add number token
                if (helper.Peek(0) == null)
                    break;
            }
            else if(helper.CheckChar() == '#'){
                   ProcessComments();              // Process comments in the code
            }
            else if (helper.CheckChar() == '\"'){
                   Tokens.add(ProcessStringLiteral()); // Process and add string literal token
            }else if (helper.CheckChar() == '`'){
                   Tokens.add(HandlePattern());      // Process and add pattern token
            }else if(SingleSymbols.containsKey(helper.CheckChar().toString())){
                  Tokens.add(ProcessSymbols());     // Process and add symbol token
            }
            else {
               throw new AwkException(" This character is not recognized -->" + helper.CheckChar());
            }
        }

        Tokens.add(ProcessSeperator()); // Add a separator token at the end of file
        return Tokens;
    }
/*
 *   Process letters, digits, and underscores
 *   Return a word token
 */
    private Token ProcessWord() {
        String word = "";
        word += helper.GetChar();

         if (helper.Peek(0) != null){

        while (Character.isLetterOrDigit(helper.Peek(0)) || helper.Peek(0) == '_') {
            if (helper.Peek(1) == null) {
                word += helper.GetChar();
                break;
            } else
                word += helper.GetChar();
        }
       }
         if(this.specialWords.containsKey(word.toUpperCase()))
             return new Token(this.specialWords.get(word.toUpperCase()), word, lineNumber, position++);
           else
        // Return a word token
         return new Token(Token.TokenType.WORD, word, lineNumber, position++);

    }
/*
 * Process digits and decimal point
 * Return a number token
 */
    private Token ProcessNumber() throws AwkException {
        String word = "";
        
        while (Character.isDigit(helper.Peek(0)) || helper.Peek(0) == '.') {
            if (helper.Peek(0) == '.' && word.contains(".")) {
                throw new AwkException("Can not have two decimals in a number!");
            }
            if (helper.Peek(1) == null) {
                word += helper.GetChar();
                break;
            }
            word += helper.GetChar();
        }

       
           return new Token(Token.TokenType.NUMBER, word, lineNumber, position++);

    }
/*
 *  Return a separator token
 */
    private Token ProcessSeperator() {

		return new Token(Token.TokenType.SEPARATOR, lineNumber, position++);
    }

  // ProcessComments() method processes comments in the input code line.
private void ProcessComments() {
    String word = "";

    // Loop until a newline character is encountered
    while (helper.GetChar() != '\n') {
        // Check if there are no more characters to process
        if (helper.Peek(1) == null) {
            word += helper.GetChar();
            break;
        }

        word += helper.GetChar();
    }
     new Token( Token.TokenType.COMMENTS,word,this.lineNumber, this.position);
}

  // ProcessStringLiteral() method processes string literals in the input code line.
private Token ProcessStringLiteral() {
    String word = "";
    helper.Swallow(0); // Consume the opening double quote

    // Loop until the closing double quote is encountered
    while (helper.Peek(0) != '\"') {

        // Check if there are no more characters to process
        if (helper.Peek(1) == null) {
            word += helper.GetChar();
            break;
        } else {
            word += helper.GetChar();
        }
    }
    
    helper.Swallow(0); // Consume the closing double quote
    return new Token(Token.TokenType.STRINGLITERAL, word, lineNumber, position++);
}

  // HandlePattern() method processes pattern literals enclosed in backticks in the input code line.
private Token HandlePattern() {
    String word = "";
    helper.Swallow(0); // Consume the opening backtick

    // Loop until the closing backtick is encountered
    while (helper.Peek(0) != '`') {

        // Check if there are no more characters to process
        if (helper.Peek(1) == null || helper.Peek(1) == '\n'||helper.Peek(1) == '}') {
            word += helper.GetChar();
            break;
        } else {
            word += helper.GetChar();
        }
    }

    return new Token(Token.TokenType.Pattern, word, lineNumber, position++);
}

 // ProcessSymbols() method processes single-character symbols in the input code line.
private Token ProcessSymbols() throws AwkException {
    String word = "";
    word += helper.GetChar();

    // Check if the symbol is at the end of the input line
    if (helper.CheckChar() == null) {
        return new Token(SingleSymbols.get(word), word, lineNumber, position++);
    }

    // Check if the symbol is followed by whitespace
    else if (Character.isWhitespace(helper.CheckChar())) {
        return new Token(SingleSymbols.get(word), word, lineNumber, position++);
    }

    // Check if the symbol is part of a double symbol (e.g., '==', '!=', etc.)
    else if (DoubleCheck().containsKey(helper.CheckChar().toString())) {
        word += helper.GetChar();

        // If the combination is a valid double symbol, return the corresponding token
        if (DoubleSymbols.containsKey(word)) {
            return new Token(DoubleSymbols.get(word), word, lineNumber, position++);
        }
    }
    
    // Check if the symbol is part of a parentheses pair (e.g., '(', ')', etc.)
    else if (ParenCheck().containsKey(helper.CheckChar().toString())) {
        return new Token(SingleSymbols.get(word), word, lineNumber, position++);
    }
       

     return new Token(SingleSymbols.get(word), word, lineNumber, position++); 
}

 // TokenHolder() method returns the ArrayList of tokens.
public LinkedList<Token> TokenHolder() {
    return Tokens;
}

 // Helper methods to populate the map of special words and their token types.
    private HashMap<String, Token.TokenType> addSpecialTokens() {
    HashMap<String, Token.TokenType> STT = new HashMap<>();
    
    // Iterate through each TokenType enum value and add it to the map
    for (Token.TokenType tokenType : Token.TokenType.values()) {
        STT.put(tokenType.toString(), tokenType);
    }
    return STT;
}

    private HashMap<String, Token.TokenType> addDoubleSymbols(){

        HashMap<String, Token.TokenType> STT = new HashMap<>();
         
       STT.put(">=", Token.TokenType.GREATER_THAN_EQUAL);
       STT.put("<=", Token.TokenType.LESS_THAN_EQUAL);
       STT.put("++", Token.TokenType.INCREMENT);
       STT.put("--", Token.TokenType.DECREMENT);
       STT.put("==", Token.TokenType.LOGICAL_EQUAL);
       STT.put("!=", Token.TokenType.NOT_EQUAL);
       STT.put("^=", Token.TokenType.EXPONENTIATION_ASSIGNMENT);
       STT.put("%=", Token.TokenType.MODULUS_ASSIGNMENT);
       STT.put("*=", Token.TokenType.MULTIPLICATION_ASSIGNMENT);
       STT.put("/=", Token.TokenType.DIVISION_ASSIGNMENT);
       STT.put("+=", Token.TokenType.ADDITION_ASSIGNMENT);
       STT.put("-=", Token.TokenType.SUBTRACTION_ASSIGNMENT);
       STT.put("!~", Token.TokenType.NON_MATCH);
       STT.put("&&", Token.TokenType.LOGICAL_AND);
       STT.put(">>", Token.TokenType.APPEND);
       STT.put("||", Token.TokenType.LOGICAL_OR);
       STT.put("\n", Token.TokenType.SEPARATOR);


       
        return STT;
    }
   
    private HashMap<String, Token.TokenType> addSingleSymbols(){

        HashMap<String, Token.TokenType> STT = new HashMap<>();
        
       STT.put("{", Token.TokenType.LEFT_CURLY_BRACKETS);
       STT.put("[", Token.TokenType.LEFT_BRACKET);
       STT.put("(", Token.TokenType.LEFT_PAREN);
       STT.put(")", Token.TokenType.RIGHT_PAREN);
       STT.put("}", Token.TokenType.RIGHT_CURLY_BRACKETS);
       STT.put("]", Token.TokenType.RIGHT_BRACKETS);
       STT.put("$", Token.TokenType.FIELD_REFERENCE);
       STT.put("~", Token.TokenType.MATCH);
       STT.put("=", Token.TokenType.EQUAL);
       STT.put(">", Token.TokenType.GREATER_THAN);
       STT.put("<", Token.TokenType.LESS_THAN);
       STT.put("!", Token.TokenType.LOGICAL_NOT);
       STT.put("+", Token.TokenType.ADDITION);
       STT.put("^", Token.TokenType.EXPONENT);
       STT.put("-", Token.TokenType.SUBTRACTION);
       STT.put("?", Token.TokenType.CONDITIONAL_QUESTION);
       STT.put(":", Token.TokenType.CONDITIONAL_COLON);
       STT.put("*", Token.TokenType.MULTIPLICATION);
       STT.put("/", Token.TokenType.DIVISION);
       STT.put("%", Token.TokenType.MODULUS);
       STT.put(";", Token.TokenType.SEMICOLON);
       STT.put("\n",Token.TokenType.SEPARATOR);
       STT.put("|", Token.TokenType.BITWISE_OR);
       STT.put(",", Token.TokenType.COMMA);
       STT.put("&", Token.TokenType.COMMA);


        return STT;
    }

    private HashMap<String, Token.TokenType> ParenCheck(){

        HashMap<String, Token.TokenType> STT = new HashMap<>();
        
       STT.put("{", Token.TokenType.LEFT_CURLY_BRACKETS);
       STT.put("[", Token.TokenType.LEFT_BRACKET);
       STT.put("(", Token.TokenType.LEFT_PAREN);
       STT.put(")", Token.TokenType.RIGHT_PAREN);
       STT.put("}", Token.TokenType.RIGHT_CURLY_BRACKETS);
       STT.put("]", Token.TokenType.RIGHT_BRACKETS);
    
        return STT;
    }
 
    private HashMap<String, Token.TokenType> DoubleCheck(){

        HashMap<String, Token.TokenType> STT = new HashMap<>();
        
       STT.put("~", Token.TokenType.MATCH);
       STT.put("=", Token.TokenType.EQUAL);
       STT.put(">", Token.TokenType.GREATER_THAN);
       STT.put("+", Token.TokenType.ADDITION);
       STT.put("-", Token.TokenType.SUBTRACTION);
       STT.put("|", Token.TokenType.BITWISE_OR);
      STT.put("&", null);

        return STT;
    }

    
}
