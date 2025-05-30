public class Token {
    
    public enum Type {
        
        //keywords
        PRINT,      
        WHILE,     
        IF,       
        I_TYPE,   
        
        // Operators
        ASSIGN_OP,    
        EQUALS,     
        NOT_EQUALS, 
        PLUS,       
        
        // Symbols
        OPEN_BLOCK,    
        CLOSE_BLOCK,   
        LPAREN,     
        RPAREN,     
        EOP,         
        
        // values / identifiers
        ID,          
        CHAR,        
        SPACE,       
        QUOTE,       
        NUMBER,    
        STRING_LIT,
        BOOLVAL,     
        
        // Comments
        COMMENT_START,  
        COMMENT_END,    
        
        //error handling
        WARNING,
        ERROR,
        EOF       
    }

    // What we need to track for every token
    private final Type type;      
    private final String lexeme; // the text 
    private final int line;      
    private final int column;    

    public Token(Type type, String lexeme, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    // Getters
    public Type getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return String.format("%s [ %s ] found at (%d:%d)",
                           type, lexeme, line, column);
    }
}
