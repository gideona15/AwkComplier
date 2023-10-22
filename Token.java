import java.util.HashMap;


public class Token {

    // Enum to represent different token types
    protected enum TokenType {
        // Define various token types
        WORD, EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN, GREATER_THAN_EQUAL, LESS_THAN_EQUAL, FIELD_REFERENCE,
        CONDITIONAL_QUESTION, CONDITIONAL_COLON, DIVISION, LEFT_PAREN, RIGHT_PAREN, LEFT_BRACKET,
        EXPONENTIATION_ASSIGNMENT, MODULUS_ASSIGNMENT, INCREMENT,DECREMENT, MULTIPLICATION_ASSIGNMENT,
        LOGICAL_AND, MATCH, MODULUS, LOGICAL_EQUAL, LEFT_CURLY_BRACKETS, RIGHT_CURLY_BRACKETS, LOGICAL_NOT,
        APPEND,COMMENTS, COMMA, NON_MATCH, LOGICAL_OR, SUBTRACTION_ASSIGNMENT, ADDITION_ASSIGNMENT, DIVISION_ASSIGNMENT,
        RIGHT_BRACKETS, ADDITION, SUBTRACTION, MULTIPLICATION, EXPONENT, STRING_LITERAL, BACKTICK, BITWISE_OR,
        NUMBER, SEPARATOR, WHILE, IF, DO, FOR, BREAK, CONTINUE, ELSE, RETURN, BEGIN, END, PRINT, PRINTF, NEXT, IN,
        DELETE,STRINGLITERAL,PLUS, MINUS, GETLINE, EXIT, NEXTFILE, FUNCTION, Pattern
    }

    // Properties of the Token class
    private TokenType type;       // Type of the token (e.g., WORD, NUMBER, SEPARATOR)
    private String value = "";    // Value associated with the token (for WORD and NUMBER tokens)
    private int lineNumber;       // Line number where the token appears
    private int position;         // Position of the token within the line
    private HashMap<String, Token.TokenType> specialWords = addSpecialTokens(); // Map to store special words

    public void setType(TokenType type) {
        this.type = type;
    }
    public TokenType getType() {
        return type;
    }

    // Getter for value
    public String getValue() {
        return value;
    }

    // Setter for value
    public void setValue(String value) {
        this.value = value;
    }

    // Getter for lineNumber
    public int getLineNumber() {
        return lineNumber;
    }

    // Setter for lineNumber
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    // Getter for position
    public int getPosition() {
        return position;
    }

    // Setter for position
    public void setPosition(int position) {
        this.position = position;
    }

    // Constructor for tokens without a value
    public Token(TokenType type, int lineNumber, int position) {
        this.type = type;
        this.lineNumber = lineNumber;
        this.position = position;
    }

    // Constructor for tokens with a value (WORD, NUMBER, etc., with line number and position)
    public Token(TokenType type, String value, int lineNumber, int position) {
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
        this.position = position;
    }

    // Helper method to populate the specialWords map with token types
    private HashMap<String, Token.TokenType> addSpecialTokens() {
        HashMap<String, Token.TokenType> STT = new HashMap<>();

        // Iterate through each TokenType enum value and add it to the map
        for (Token.TokenType tokenType : Token.TokenType.values()) {
            STT.put(tokenType.toString(), tokenType);
        }
        return STT;
    }  
   
    // String representation of the tokenword plus line number and postion
    public String toString() {
         
		//UNIT TEST
        // if(type == Token.TokenType.SEPARATOR)
		//     return " TokenType:"+type + "  Line number:"+lineNumber+"  Postion:"+position+"\n";
		// else
		// 	return " TokenType:"+type + "  Token:"+value+"  Line number:"+lineNumber+"  Postion:"+position;
        if(this.specialWords.containsKey(value.toUpperCase()))
            return type + " ";

        else if (type == Token.TokenType.SEPARATOR)
            return type + "\n";
        else
            return type + "(" + value + ") ";
    }
}