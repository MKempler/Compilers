import java.util.HashMap;
import java.util.List;
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
} 