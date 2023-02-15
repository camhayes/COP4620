import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;

public class Driver {

	
	public static void main( String[] args) throws Exception 
	{
		
		String fileName = "C:/Users/liamr/OneDrive/Desktop/Compilers/Step-2/Step2/inputs/test2.micro"; //Add file path/name for your computer
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
		
		///////////////////////////////////////////////////////////

		LittleLexer lexer = new LittleLexer(input);

		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();
		
		Vocabulary voc = lexer.getVocabulary();

		LittleParser parser = new LittleParser(tokens);
		
		ANTLRErrorStrategy es = new CustomErrorStrategy();
		parser.setErrorHandler(es);
		
		
		try{	
			parser.program();
			System.out.println("Accepted");
		}catch(Exception e){
			System.out.println("Not accepted");
		}
		
		
	}

}

class CustomErrorStrategy extends DefaultErrorStrategy{
	@Override
	public void recover(Parser recognizer, RecognitionException e){
			throw e;
	}
	
	@Override
    public Token recoverInline(Parser recognizer){
    	//return null;
    	RecognitionException e = null;
    	throw e;
   	
    }
    
	@Override
	public void reportError(Parser recognizer, RecognitionException e){
		throw e;
	}	
	
}

