import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class SymbolTable {
    private int currentScope;
    private Map<String, List<Symbol>> table;
    
    public SymbolTable() {
        currentScope = 0;
        table = new HashMap<>();
    }
    
    public void enterScope() {
        currentScope++;
    }
    
    public void exitScope() {
        if (currentScope > 0) {
            currentScope--;
        }
    }
    
    public int getCurrentScope() {
        return currentScope;
    }

    public boolean add(Symbol symbol) {
        // Check to see if the symbol already exists in the current scope
        List<Symbol> existingSymbols = table.get(symbol.getName());
        if (existingSymbols != null) {
            for (Symbol existing : existingSymbols) {
                if (existing.getScope() == symbol.getScope()) {
                    
                    return false;
                }
            }
        }
        
        // Get the list for this name
        List<Symbol> symbols = table.get(symbol.getName());
        if (symbols == null) {
            symbols = new ArrayList<>();
            table.put(symbol.getName(), symbols);
        }
        
        // Add the symbol
        symbols.add(symbol);
        return true;
    }
} 