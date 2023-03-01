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
        	for(int i = 0;i < int_val.length; i++)
            System.out.println(int_val[i] + ", " + type);
        }
        else if (type.compareTo("FLOAT") == 0) {
        	String name = ctx.id_list().getText();
        	String[] int_val = ctx.id_list().getText().split(",");
        	for(int i = 0;i < int_val.length; i++)
            System.out.println(int_val[i] + ", " + type);
        }
       
        // Next: Create symbol table using the information above and insert at the top of the stack
    }
    
    public void prettyPrint() {
        // print all sumbol tables in the order they were created.
    }
}
