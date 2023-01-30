import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;

public class Driver {

	
	public static void main( String[] args) throws Exception 
	{
		
		String fileName = "C:/Users/liamr/OneDrive/Desktop/Compilers/Step-1/Step1/inputs/fibonacci.micro"; //Add file path/name for your computer
		//String fileName = args[0];
		File file = new File(fileName);
		FileInputStream fis = null;
		
		try {
			fis = new FileInputStream(file);
		}
		catch (IOException e){
			System.out.println("Couldn't find file");
			System.exit(1);
		}
		
		@SuppressWarnings("deprecation")
		ANTLRInputStream input = new ANTLRInputStream(fis);

		Little lexer = new Little(input);

		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();
		
		Vocabulary vocab = lexer.getVocabulary();
		
		
		for(Object o: tokens.getTokens()) {
			CommonToken token = (CommonToken)o;
			System.out.println("Token Type: " + vocab.getSymbolicName(token.getType()));
			System.out.println("Value: " + token.getText());
			
			
		}
		
		//LittleParser parser = new LittleParser(tokens);
		//ParseTree tree = parser(); // begin parsing at rule 'r'               //Remove/Replace for our grammer/code
		
		
		System.out.println(); // print LISP-style tree
	}

}
