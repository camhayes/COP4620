import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

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
        //ast.astOut();
        ArrayList<CodeObject> IR = ast.generateIR();
        Hashtable<String, SymbolData> symbols = Compiler.symbols;
        for (int i = 0; i < IR.size(); i++) {
            System.out.println(IR.get(i).toString());
        }
        ArrayList<String> tiny = ast.generateTiny(IR, symbols);
        for (int i = 0; i < tiny.size(); i++) {
            System.out.println(tiny.get(i));
        }
    }

}

