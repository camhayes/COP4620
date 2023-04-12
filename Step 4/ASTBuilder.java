import java.util.ArrayList;

public class ASTBuilder extends LittleBaseListener
{
    ArrayList<Node> ast = new ArrayList<>();
    Node current;
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
    @Override public void enterString_decl(LittleParser.String_declContext ctx)
    {

    }

    @Override public void enterVar_decl(LittleParser.Var_declContext ctx)
    {

    }

    public void prettyPrint() {

        for (int i = 0; i < ast.size(); i++) {
            // cleans up the string before outputting it
            String miniAST = ast.get(i).toString();
            miniAST = miniAST.substring(0, miniAST.length() - 1 );
            System.out.println(miniAST);
        }

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