import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;



/**
 * The main class for the AWK lexer and parser application.
 */
public class Awk {
    
    public static void main(String[] args) throws AwkException, IOException {
        String compileContent;
        Path compliePath;
        Path AwkPath;

        // if(args != null && args2 != null){

        //     compliePath = Paths.get(args.toString());
        //     compileContent = new String(Files.readAllBytes(compliePath));
        //     AwkPath = Paths.get(args2.toString());
        // }else{

        try {
            // Define the path to the input file and read the content of the file

            compliePath = Paths.get("/Users/gidhome/Desktop/text.txt");
            compileContent = new String(Files.readAllBytes(compliePath));
            AwkPath = Paths.get("/Users/gidhome/Desktop/Awkfile.txt");
           
        } catch (IOException e) {
            // Handle any IOException by creating an AwkException
            throw new AwkException(e.toString());
    
        
    }
        
        // Create a Lexer instance to tokenize the input content
        //System.out.println("_____________Lexer___________");
		Lexer lexer = new Lexer(compileContent);
        
        // Tokenize the input content using the Lexer
        LinkedList<Token> lexed = lexer.Lex();
    
        // Print out the tokens generated by the Lexer
        // for (int i = 0; i < lexer.TokenHolder().size(); i++) 
        //     System.out.print(lexer.TokenHolder().get(i));
        

        //System.out.println("\n_____________Parser___________");
       
        Parser parse = new Parser(lexed);
        ProgramNode parsed = parse.program();

    //    System.out.println(parsed.toString());


        //System.out.println("\n_____________Interpreter___________");
       
        Interpreter interpret = new Interpreter(parsed, AwkPath);
        interpret.InterpretProgram(parsed);       


    }
}
