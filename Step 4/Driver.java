import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;

public class Driver {


    public static void main( String[] args) throws Exception
    {
        ANTLRInputStream input = new ANTLRInputStream(System.in);

        LittleLexer lexer = new LittleLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        LittleParser parser = new LittleParser(tokens);

        ParseTree tree = parser.program();

        ParseTreeWalker walker = new ParseTreeWalker();

        Compiler ast = new Compiler();

        walker.walk(ast, tree);
        ast.printAST();
        //ast.generateIR();

    }

}

