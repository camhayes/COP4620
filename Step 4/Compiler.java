import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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
        Hashtable<String, SymbolData> symbols = new Hashtable<>();

        int tmpNum = 1;

        IR.add(new CodeObject("IR", "CODE"));
        IR.add(new CodeObject("LABEL", "main"));
        IR.add(new CodeObject("LINK", ""));

        for (int i = 0; i < ast.size(); i++) {
            // We convert our nodes to a string, remove any null values and clean up the end.
            String prettify = ast.get(i).toString();
            prettify = prettify.substring(0, prettify.length() - 1 );
            String[] inst = prettify.split(",");
            int codeEval = inst.length;
            char firstChar = prettify.charAt(0);
            switch(firstChar) {
                case '=' : // assignment statements
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
                            } else if (inst[1].contains("STRING")) {
                                // [=,STRING(var),"STRING"]
                                String id = inst[1].substring(7, inst[1].length() - 1);
                                SymbolData symboldata = new SymbolData("STRING", inst[2]);
                                symbols.put(id, symboldata);
                            }

                            break; // end direct assignments
                        case 5: // Assignment statements with expressions
                            // [=,ID(var),OPCODE,OPERAND1,OPERAND2]
                        	
                        	String[] expression = parseString(prettify);
                        	String varPrint = prettify.substring(prettify.indexOf('(') + 1, prettify.indexOf(')'));
                        	
                        	SymbolData result = symbols.get(varPrint);
                        	char typeE = result.type.charAt(0);
                        	
                        	if(expression[1].equals("*")) {
                        		CodeObject assignTempNode = new CodeObject("MULT" + typeE, expression[2], expression[3], "$T" + tmpNum);
                                CodeObject assignVarNode = new CodeObject("STORE" + typeE, "$T" + tmpNum, expression[0]);
                                IR.add(assignTempNode);
                                IR.add(assignVarNode);
                                tmpNum++;
                        	}
                        	else if(expression[1].equals("+")) {
                
                        		CodeObject assignTempNode = new CodeObject("ADD" + typeE, expression[2], expression[3], "$T" + tmpNum);
                                CodeObject assignVarNode = new CodeObject("STORE" + typeE, "$T" + tmpNum, expression[0]);
                                IR.add(assignTempNode);
                                IR.add(assignVarNode);
                                tmpNum++;
                        	}
                        	else if(expression[1].equals("-")) {
                        		CodeObject assignTempNode = new CodeObject("SUB" + typeE, expression[2], expression[3], "$T" + tmpNum);
                                CodeObject assignVarNode = new CodeObject("STORE" + typeE, "$T" + tmpNum, expression[0]);
                                IR.add(assignTempNode);
                                IR.add(assignVarNode);
                                tmpNum++;
                        	}

                            break;
                    }
                    break;
                case 'W' : // write statements
                	String varPrint = prettify.substring(prettify.indexOf('(') + 1, prettify.indexOf(')'));
                	
                	SymbolData result = symbols.get(varPrint);
                	
                	if (result.type.equals("STRING")) {
                        CodeObject assignVarNode = new CodeObject("WRITES", varPrint);
                        IR.add(assignVarNode);
                	}
                	else if (result.type.equals("INT")) {
                        CodeObject assignVarNode = new CodeObject("WRITEI", varPrint);
                        IR.add(assignVarNode);
                	}
                	else if (result.type.equals("FLOAT")) {
                        CodeObject assignVarNode = new CodeObject("WRITEF", varPrint);
                        IR.add(assignVarNode);
                	}
                	
                    break;
                case 'R': // Read and return statement
                    if (prettify.contains("READ")) {
                    	
                        varPrint = prettify.substring(prettify.indexOf('(') + 1, prettify.indexOf(')'));
                    	result = symbols.get(varPrint);
                    	
                    	if (result.type.equals("STRING")) {
                            CodeObject assignVarNode = new CodeObject("READS", varPrint);
                            IR.add(assignVarNode);
                    	}
                    	else if (result.type.equals("INT")) {
                            CodeObject assignVarNode = new CodeObject("READI", varPrint);
                            IR.add(assignVarNode);
                    	}
                    	else if (result.type.equals("FLOAT")) {
                            CodeObject assignVarNode = new CodeObject("READF", varPrint);
                            IR.add(assignVarNode);
                    	}
                    	
                    }
                    else if (prettify.contains("RETURN")){
                    	CodeObject assignVarNode = new CodeObject("RET","");
                        IR.add(assignVarNode);
                    }
                    break;
                case 'I': // Int decl
                    if (inst[0].contains("INT")) {
                        String id = inst[0].substring(4, inst[0].length() - 1);
                        SymbolData symbolData = new SymbolData("INT");
                        symbols.put(id,symbolData);
                    }
                    break;
                case 'F': // [FLOAT(id)]
                    if (inst[0].contains("FLOAT")) {
                        String id = inst[0].substring(6, inst[0].length() - 1);
                        SymbolData symbolData = new SymbolData("FLOAT");
                        symbols.put(id,symbolData);
                    }

                    break;
            }
        }
        for (int i = 0; i < IR.size(); i++) {
            System.out.println(IR.get(i).toString());
        }


        return IR;
    }
    public static String[] parseString(String input) {
        String[] tokens = input.split(",");
        List<String> outputList = new ArrayList<String>();

        for (int i = 1; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.startsWith("ID(")) {
                outputList.add(token.substring(3, token.length() - 1));
            } else if (token.equals("*") || token.equals("+")) {
                outputList.add(token);
            }
        }

        String[] output = new String[outputList.size()];
        output = outputList.toArray(output);
        return output;
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
        return ";" + string;
    }
    
    
}

class SymbolData {
    String type;
    String data;
    public SymbolData(String type) {
        this.type = type;
    }
    public SymbolData(String type, String data) {
        this.type = type;
        this.data = data;
    }
    
}