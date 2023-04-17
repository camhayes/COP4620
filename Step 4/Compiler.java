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

    public ArrayList<CodeObject> generateIR() {
        ArrayList<CodeObject> IR = new ArrayList<CodeObject>();
        ArrayList<String> vars = new ArrayList<>(); // We want to maintain a list of variables that we have to declare at some point.

        int tmpNum = 1;

        for (int i = 0; i < ast.size(); i++) {
            // We convert our nodes to a string, remove any null values and clean up the end.
            String prettify = ast.get(i).toString();
            prettify = prettify.substring(0, prettify.length() - 1 );
            char firstChar = prettify.charAt(0);
            switch(firstChar) {
                case '=' : // assignment statements
                    String[] inst = prettify.split(",");
                    int codeEval = inst.length;
                    // We have to differentiate between direct assignments and expressions
                    switch(codeEval) {
                        case 3 : // direct assignment to ID or String decl
                            String type = inst[2];
                            if (type.contains("INTLITERAL")) {
                                // ex: [=,ID(var),INTLITERAL(3)]
                                String id = inst[1].substring(3, inst[1].length() - 1);
                                String literal = inst[2].substring(11, inst[2].length() - 1);
                                CodeObject assignTempNode = new CodeObject("STOREI", literal, "$T" + tmpNum);
                                CodeObject assignVarNode = new CodeObject("STOREI", "$T" + tmpNum, id);
                                IR.add(assignTempNode);
                                IR.add(assignVarNode);
                                tmpNum++;

                            } else if (type.contains("FLOATLITERAL")) {
                                // ex: [=,ID(var),FLOATLITERAL(3)]
                                String id = inst[1].substring(3, inst[1].length() - 1);
                                String literal = inst[2].substring(13, inst[2].length() - 1);
                                CodeObject assignTempNode = new CodeObject("STOREF", literal, "$T" + tmpNum);
                                CodeObject assignVarNode = new CodeObject("STOREF", "$T" + tmpNum, id);
                                IR.add(assignTempNode);
                                IR.add(assignVarNode);
                                tmpNum++;
                            } else if (type.contains("STRING")) {
                                // [=,STRING(var),"STRING"]
                                // We send this to our vars table - need to figure this out...

                            }

                            break; // end direct assignments
                        case 5: // Assignment statements with expressions
                            // [=,ID(var),OPCODE,OPERAND1,OPERAND2]

                            break;
                    }
                    break;
            }
        }
        for (int i = 0; i < IR.size(); i++) {
            System.out.println(IR.get(i).toString());
        }

        return IR;
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

    public String toString() {
        String string = "";
        string += opcode != null ? this.opcode + ' ' : "";
        string += opOne != null ? this.opOne + ' ' : "";
        string += opTwo != null ? this.opTwo + ' ' : "";
        string += result != null ? this.result + ' ' : "";
        return string;
    }
}