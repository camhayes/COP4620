import java.util.ArrayList;

public class Compiler extends LittleBaseListener
{
    ArrayList<Node> ast = new ArrayList<>();
    Node current;

    @Override public void enterFunc_decl(LittleParser.Func_declContext ctx) {
        Node node = new Node("FUNCTION(" + ctx.id().getText() + ")");
        current = node;
        ast.add(current);
    }
    @Override public void exitFunc_decl(LittleParser.Func_declContext ctx) {
        Node node = new Node("RETURN");
        current = node;
        ast.add(current);
    }
    @Override public void enterString_decl(LittleParser.String_declContext ctx)
    {
        Node node = new Node("=");
        current = node;
        Node left = new Node("STRING(" + ctx.id().getText() + ")");
        Node right = new Node(ctx.str().getText());
        node.addLeft(left);
        node.addRight(right);
        ast.add(current);
    }

    @Override public void enterVar_decl(LittleParser.Var_declContext ctx)
    {
        String type = ctx.var_type().getText();
        String idlist = ctx.id_list().getText();
        String[] ids = idlist.split(",");
        if (type.compareTo("INT") == 0) {
            for (int i = 0; i < ids.length; i ++) {
                Node node = new Node("INT(" + ids[i] + ")");
                current = node;
                ast.add(current);
            }
        } else if (type.compareTo("FLOAT") == 0) {
            for (int i = 0; i < ids.length; i ++) {
                Node node = new Node("FLOAT(" + ids[i] + ")");
                current = node;
                ast.add(current);
            }
        }
    }
    @Override public void enterAssign_stmt(LittleParser.Assign_stmtContext ctx) {
        // Creates an assignment node with the variable being updated
        Node node = new Node("=");
        current = node;
        Node left = new Node("ID(" + ctx.assign_expr().IDENTIFIER().getText() +")");
        current.addLeft(left);
        ast.add(current);
    }
    @Override public void enterExpr(LittleParser.ExprContext ctx) {
        // New node for the expression
        Node node = new Node();

        // Setting the assignment node to return to
        Node prev = current;
        current = node;
        prev.addRight(current);

    }
    @Override public void enterAddop(LittleParser.AddopContext ctx) {
        current.setValue(ctx.getText());
    }
    @Override public void enterMulop(LittleParser.MulopContext ctx) {
        current.setValue(ctx.getText());
    }
    @Override public void enterPrimary(LittleParser.PrimaryContext ctx) {
        Node node = new Node();
        Node prev = current;
        current = node;

        if (prev.left == null) {
            prev.addLeft(node);
        } else {
            prev.addRight(node);
        }
        if (ctx.id() != null) {
            current.setValue("ID(" + ctx.id().getText() + ")");
        } else if (ctx.INTLITERAL() != null) {
            current.setValue("INTLITERAL(" + ctx.INTLITERAL().getText() + ")");
        } else if (ctx.FLOATLITERAL() != null) {
            current.setValue("FLOATLITERAL(" + ctx.FLOATLITERAL().getText() + ")");
        }

        current = prev;
    }

    @Override public void enterRead_stmt(LittleParser.Read_stmtContext ctx) {
        String idlist = ctx.id_list().getText();
        String[] ids = idlist.split(",");
        for (int i = 0; i < ids.length; i++) {
            Node node = new Node("READ(" + ids[i] + ")");
            current = node;
            ast.add(current);
        }
    }

    @Override public void enterWrite_stmt(LittleParser.Write_stmtContext ctx) {
        String idlist = ctx.id_list().getText();
        String[] ids = idlist.split(",");
        for (int i = 0; i < ids.length; i++) {
            Node node = new Node("WRITE(" + ids[i] + ")");
            current = node;
            ast.add(current);
        }
    }

    public void printAST() {
        for (int i = 0; i < ast.size(); i++)

        {
            // Clean up our AST to remove all null values
            String prettify = ast.get(i).toString();
            prettify = prettify.substring(0, prettify.length() - 1);
            System.out.println(prettify);
        }
    }
    public ArrayList<CodeObject> generateIR() {
        ArrayList<CodeObject> IR = new ArrayList<CodeObject>();
        int tmpNum = 1;

        for (int i = 0; i < ast.size(); i++) {
            // Clean up our AST to remove all null values
            String prettify = ast.get(i).toString();
            prettify = prettify.substring(0, prettify.length() - 1 );
            char firstChar = prettify.charAt(0);
            switch(firstChar) {
                case '=' : // assignment statements
                    String[] inst = prettify.split(",");
                    int codeEval = inst.length;
                    // We have to differentiate between direct assignments and expressions
                    switch(codeEval) {
                        case 3 :
                            CodeObject irNode = new CodeObject(inst[0], generateTemp(tmpNum));
                            tmpNum++;
                            break;
                    }



                    //IR.add(irNode);

                    break;
            }


        }


        return IR;
    }

    public String generateTemp (int tmpNum){
        return "$T" + tmpNum;
    }


}
class Node {
    String value;
    Node left, right;

    public Node() { }
    public Node(String value) {
        this.value = value;
    }
    public void setValue(String value){
        this.value = value;
    }
    public void addLeft(Node left) {
        this.left = left;
    }
    public void addRight(Node right) {
        this.right = right;
    }
    public String toString() {
        String string = "";
        if (this.value != null ) {
            string += this.value + ',';
        }
        if (this.left != null) {
            string += this.left.toString();
        }
        if (this.right != null) {
            string += this.right.toString();
        }
        return string;
    }

}

class CodeObject {
    String opcode;
    String opOne, opTwo, result;
    // Used for expressions
    public CodeObject(String opcode, String opOne, String opTwo, String result) {
        this.opcode = opcode;
        this.opOne = opOne;
        this.opTwo = opTwo;
        this.result = result;
    }
    // Used for STORE
    public CodeObject(String opcode, String opOne, String result) {
        this.opcode = opcode;
        this.opOne = opOne;
        this.result = result;
    }
    // Used for READ/WRITE (For WRITE, result == OP1)
    public CodeObject(String opcode, String result) {
        this.opcode = opcode;
        this.result = result;
    }
}