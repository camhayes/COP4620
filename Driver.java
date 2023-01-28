import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;

public class Driver {

	public static void main( String[] args) throws Exception 
	{
		
		String fileName = "C:/Users/liamr/OneDrive/Desktop/Compilers/Step-1/Step1/inputs/fibonacci.micro"; //Add file path/name for your computer
		File file = new File(fileName);
		FileInputStream fis = null;
		
		try {
			fis = new FileInputStream(file);
		}
		catch (IOException e){
			System.out.println("Couldn't find file");
			System.exit(1);
		}
		
		ANTLRInputStream input = new ANTLRInputStream(fis);

		HelloLexer lexer = new HelloLexer(input);

		CommonTokenStream tokens = new CommonTokenStream(lexer);

		HelloParser parser = new HelloParser(tokens);
		ParseTree tree = parser.r(); // begin parsing at rule 'r'               //Remove/Replace for our grammer/code
		System.out.println(tree.toStringTree(parser)); // print LISP-style tree
	}

}
