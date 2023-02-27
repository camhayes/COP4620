import java.util.*;
import java.util.ArrayList;

public class SimpleTableBuilder extends LittleBaseListener {
    @Override public void enterProgram(LittleParser.ProgramContext ctx)
    {
        // Next:
        // Make symbol table for global
        // add it to the list of symbol tables
        // push it to the scope stack

    }

    @Override public void enterString_decl(LittleParser.String_declContext ctx)
    {
        String name = ctx.id().getText();
        String type = "STRING";
        String value = ctx.str().getText();
        System.out.println(name + ", " + type + ", " + value);
        // Next: Create synbol table using the information above and insert at the top of the stack
    }
    // Next: Create supporting methods for other decl types
    public void prettyPrint() {
        // print all sumbol tables in the order they were created.
    }
}