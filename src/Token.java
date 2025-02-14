public class Token {
    
    public enum Type {
        
        PRINT,      
        WHILE,     
        IF,       
        TYPE_INT,  
        TYPE_STRING,
        TYPE_BOOL,  
        TRUE,      
        FALSE,  
        
        // Operators
        ASSIGN,    
        EQUALS,     
        NOT_EQUALS, 
        PLUS,   
        
        // Symbols
        LPAREN,    
        RPAREN,   
        LBRACE,     
        RBRACE,    
        EOP,       
        
        // Other tokens
        IDENTIFIER, 
        NUMBER,    
        STRING_LIT, 
        EOF       
    }

    //store token info
    private final Type type;      
    private final String lexeme;
    private final int line;      
    private final int column;    

    // Constructor
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
        return String.format("Token[type=%s, lexeme='%s', line=%d, column=%d]",
                           type, lexeme, line, column);
    }
}
