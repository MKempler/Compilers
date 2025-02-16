public class Lexer {
    private String input;     
    private int position;      
    private int line;        
    private int column;       
    
 
    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.line = 1;
        this.column = 1;
    }
    
    
    public Token nextToken() {
        // Skip whitespace and comments
        skipWhitespace();
        
        // Check if we've reached the end
        if (position >= input.length()) {
            return new Token(Token.Type.EOF, "", line, column);
        }
        
        char currentChar = input.charAt(position);
        
        // Handle basic symbols 
        switch (currentChar) {
            case '{':
                position++; column++;
                return new Token(Token.Type.OPEN_BLOCK, "{", line, column - 1);
            case '}':
                position++; column++;
                return new Token(Token.Type.CLOSE_BLOCK, "}", line, column - 1);
            case '$':
                position++; column++;
                return new Token(Token.Type.EOP, "$", line, column - 1);
        }
        
        // Handle unknown
        position++; column++;
        return new Token(Token.Type.EOF, 
            String.format("Error: Unexpected character '%c'", currentChar), 
            line, column - 1);
    }
    
    //  skip whitespace
    private void skipWhitespace() {
        while (position < input.length() && 
               Character.isWhitespace(input.charAt(position))) {
            if (input.charAt(position) == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
            position++;
        }
    }
}
