import java.util.*;
import java.util.ArrayList;

class Symbol{
	String name;
	String type;
	String value;

	public Symbol(String name, String type) {
		this.name = name;
		this.type = type;
		this.value = null;
	}

	public Symbol(String name, String type, String value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}

	public String getValue() {
		return this.value;
	}
}

	class SymbolTable{
		public String scope;
		public SymbolTable parent;
		public ArrayList<SymbolTable> children;
		public LinkedHashMap<String,Symbol> table;

		public SymbolTable(String scope){
			this.scope = scope;
			this.table = new LinkedHashMap<String,Symbol>();
		}

		public SymbolTable getParent(){
			return this.parent;
		}

		public LinkedHashMap<String, Symbol> getTable(){
			return this.table;
		}

		public void addSymbol(Symbol symbol) throws IllegalArgumentException{
			String name = symbol.getName();
			String type = symbol.getType();
			String valueString = symbol.getValue();
			if(table.containsKey(name)) {
				throw new IllegalArgumentException("DECLARATION ERROR " + name);
			}
			else {
				table.put(name, symbol);
			}
		}

		public void printTable(){
			System.out.println("Symbol table "+this.scope);
			Iterator<Symbol> symbols = table.values().iterator();
			while(symbols.hasNext()){
				Symbol currentSymbol = symbols.next();
				String name = currentSymbol.getName();
				String type = currentSymbol.getType();
				String value = currentSymbol.getValue();

				if(type.compareTo("STRING") == 0) {
					System.out.println("name " + name + " type " + type + " value " + value);
				}
				else {
					System.out.println("name " + name + " type " + type);
				}
			}
		}
	}


public class SimpleTableBuilder extends LittleBaseListener {

	ArrayList<LinkedHashMap<String,Symbol>> stack = new ArrayList<LinkedHashMap<String,Symbol>>();
	SymbolTable table = new SymbolTable("temp");

    @Override public void enterProgram(LittleParser.ProgramContext ctx)
    {
    	SymbolTable global_table = new SymbolTable("GLOBAL");
    	table = global_table;

    }

    @Override public void enterString_decl(LittleParser.String_declContext ctx)
    {
        String name = ctx.id().getText();
        String type = "STRING";
        String value = ctx.str().getText();
        Symbol symbol = new Symbol(name, type, value);
        table.addSymbol(symbol);
        stack.add(table.getTable());
        // Next: Create symbol table using the information above and insert at the top of the stack
    }

    @Override public void enterVar_decl(LittleParser.Var_declContext ctx)
    {
        String type = ctx.var_type().getText();
        if (type.compareTo("INT") == 0) {
        	String name = ctx.id_list().getText();
        	String[] int_val = ctx.id_list().getText().split(",");
        	for(int i = 0;i < int_val.length; i++) {
        		Symbol symbol = new Symbol(int_val[i], type);
        		table.addSymbol(symbol);
        		stack.add(table.getTable());
        	}
        }
        else if (type.compareTo("FLOAT") == 0) {
        	String name = ctx.id_list().getText();
        	String[] int_val = ctx.id_list().getText().split(",");
        	for(int i = 0;i < int_val.length; i++) {
        		Symbol symbol = new Symbol(int_val[i], type);
        		table.addSymbol(symbol);
        		stack.add(table.getTable());
        	}
        }

        // Next: Create symbol table using the information above and insert at the top of the stack
    }


    @Override public void enterFunc_decl(LittleParser.Func_declContext ctx) {
    	String name = ctx.id().getText();
    	SymbolTable func_table = new SymbolTable(name);
    	table = func_table;
    }


    // Next: Create supporting methods for other decl types
    public void prettyPrint() {
    	SymbolTable currentTable = table;
        while (currentTable != null) {
            currentTable.printTable();
            currentTable = currentTable.getParent();
        }
        // print all symbol tables in the order they were created.
    }
}
