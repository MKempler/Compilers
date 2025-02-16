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
            case '/':
                if (position + 1 < input.length() && input.charAt(position + 1) == '*') {
                    return handleComment();
                }
        }
        
        // Handle unknown
        position++; column++;
        return new Token(Token.Type.EOF, 
            String.format("Error: Unexpected character '%c'", currentChar), 
            line, column - 1);
    }
    
    private Token handleComment() {
        int startLine = line;
        int startColumn = column;
        
        // Skip the '/*'
        position += 2;
        column += 2;
        
        boolean foundEnd = false;
        StringBuilder comment = new StringBuilder();
        
        while (position < input.length() - 1) {
            // Check for comment end 
            if (input.charAt(position) == '*' && input.charAt(position + 1) == '/') {
                foundEnd = true;
                position += 2;
                column += 2;
                break;
            }
            
            // Handle newlines
            if (input.charAt(position) == '\n') {
                line++;
                column = 1;
                comment.append('\n');
            } else {
                column++;
                comment.append(input.charAt(position));
            }
            position++;
        }
        
        if (!foundEnd) {
            // unclosed comment warning
            return new Token(Token.Type.EOF,
                String.format("Warning: unclosed comment starting at (%d:%d)", 
                            startLine, startColumn),
                startLine, startColumn);
        }
        
        return nextToken(); // Skip comment
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
