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
    
     @Override public void enterVar_decl(LittleParser.Var_declContext ctx)
    {
        String type = ctx.var_type().getText();
        if (type.compareTo("INT") == 0) {
        	String name = ctx.id_list().getText();
        	String[] int_val = ctx.id_list().getText().split(",");
            System.out.println(int_val[0] + ", " + type);
        }
        else if (type.compareTo("FLOAT") == 0) {
        	String name = ctx.id_list().getText();
        	String[] int_val = ctx.id_list().getText().split(",");
            System.out.println(int_val[0] + ", " + type);
        }
       
        // Next: Create symbol table using the information above and insert at the top of the stack
    }
    
    public void prettyPrint() {
        // print all sumbol tables in the order they were created.
    }
}
