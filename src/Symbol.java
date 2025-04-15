public class Symbol {
    private String name;        
    private String type;       
    private int scope;      
    private int line;   
    private int column;  
    private boolean isInitialized = false; 
    private boolean isUsed = false;       

    public Symbol(String name, String type, int scope, int line, int column) {
        this.name = name;
        this.type = type;
        this.scope = scope;
        this.line = line;
        this.column = column;
    }
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public int getScope() {
        return scope;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    public boolean isInitialized() {
        return isInitialized;
    }
    
    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }
    
    public boolean isUsed() {
        return isUsed;
    }
  
    public void setUsed(boolean used) {
        isUsed = used;
    }

    public String toString() {
        return name + "\t" + type + "\t" + scope + "\t" + line;
    }
} 