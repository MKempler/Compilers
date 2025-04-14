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
    
    public Symbol lookup(String name) {
        // Start at current scope and work up
        for (int scope = currentScope; scope >= 0; scope--) {
            Symbol symbol = lookupInScope(name, scope);
            if (symbol != null) {
                return symbol;
            }
        }
        return null;
    }
    
    private Symbol lookupInScope(String name, int scope) {
        List<Symbol> symbols = table.get(name);
        if (symbols != null) {
            for (Symbol symbol : symbols) {
                if (symbol.getScope() == scope) {
                    return symbol;
                }
            }
        }
        return null;
    }
    
    public void display() {
        System.out.println("\nSymbol Table");
        System.out.println("------------------------");
        System.out.println("Name\tType\tScope\tLine");
        System.out.println("------------------------");
        
        // Get all symbols
        List<Symbol> allSymbols = new ArrayList<>();
        for (List<Symbol> symbols : table.values()) {
            allSymbols.addAll(symbols);
        }
        
        // Sort by scope
        allSymbols.sort((s1, s2) -> {
            if (s1.getScope() != s2.getScope()) {
                return Integer.compare(s1.getScope(), s2.getScope());
            }
            return s1.getName().compareTo(s2.getName());
        });
        
        // Display the symbols
        for (Symbol symbol : allSymbols) {
            System.out.println(symbol);
        }
        System.out.println("------------------------");
    }
} 