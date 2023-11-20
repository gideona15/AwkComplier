import java.util.LinkedList;
import java.util.Optional;

public class TokenManager {

    private LinkedList<Token> TokenStream = new LinkedList<>();

    // Constructor to initialize TokenManager with a LinkedList of Tokens
    public TokenManager(LinkedList<Token> input) {
        TokenStream = input;
    }

    // Peek at a Token at a specific index in the TokenStream
    public Optional<Token> Peek(int input) {
        if (!TokenStream.isEmpty())
            
            return Optional.of(TokenStream.get(input));
        else
            return Optional.empty();
    }

    // Check if there are more tokens in the TokenStream
    public boolean MoreTokens() {
        if(TokenStream.isEmpty() == true)
            return false;
        return true;
    }

    // Remove and accept separators from the TokenStream
    public Boolean AcceptSeperators() {
        Boolean hear = false;

            if (TokenStream.getFirst().getType() == Token.TokenType.SEPARATOR) {
               // TokenStream.remove(i);
                 MatchAndRemove(Token.TokenType.SEPARATOR);
                    if(TokenStream.isEmpty())
                        return false;
                hear = true;    
            }else
               hear = false;
            
        return hear;
    }

    // Match a specific TokenType and remove it from the TokenStream
    public Optional<Token> MatchAndRemove(Token.TokenType input) {
        if (TokenStream.peekFirst().getType() == null)
            return null;

        if (TokenStream.peekFirst().getType() == input) {
            return Optional.of(TokenStream.poll());
        } else
            return Optional.empty();
    }

   
}
