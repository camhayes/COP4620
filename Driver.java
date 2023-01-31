import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;

public class Driver {


    public static void main( String[] args) throws Exception
    {

        //String fileName = ""; //Add file path/name for your computer

        InputStream inputStream = System.in;

        @SuppressWarnings("deprecation")
        ANTLRInputStream input = new ANTLRInputStream(inputStream);

        Little lexer = new Little(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();

        Vocabulary vocab = lexer.getVocabulary();

        for(Token token: tokens.getTokens()) {
            if (vocab.getSymbolicName(token.getType()) != "EOF") {
                System.out.println("Token Type: " + vocab.getSymbolicName(token.getType()));
                System.out.println("Value: " + token.getText());
            }
        }
    }

}