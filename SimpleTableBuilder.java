import java.util.*;
import java.util.ArrayList;

public class SimpleTableBuilder extends LittleBaseListener {
    @Override public void enterProgram(LittleParser.ProgramContext ctx)
    {

    }

    @Override public void enterString_decl(LittleParser.String_declContext ctx)
    {
        String name = ctx.id().getText();
        String type = "STRING";
        String value = ctx.str().getText();
        System.out.println(name + ", " + type + ", " + value);
    }
    public void prettyPrint() {
        
    }
}