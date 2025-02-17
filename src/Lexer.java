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
            case '=':
                if (position + 1 < input.length() && input.charAt(position + 1) == '=') {
                    position += 2; column += 2;
                    return new Token(Token.Type.EQUALS, "==", line, column - 2);
                }
                position++; column++;
                return new Token(Token.Type.ASSIGN_OP, "=", line, column - 1);
            case '!':
                if (position + 1 < input.length() && input.charAt(position + 1) == '=') {
                    position += 2; column += 2;
                    return new Token(Token.Type.NOT_EQUALS, "!=", line, column - 2);
                }
                // Handle unexpected !
                position++; column++;
                return new Token(Token.Type.EOF, 
                    String.format("Error: Unexpected character '!'", currentChar), 
                    line, column - 1);
            case '+':
                position++; column++;
                return new Token(Token.Type.PLUS, "+", line, column - 1);
            case '(':
                position++; column++;
                return new Token(Token.Type.LPAREN, "(", line, column - 1);
            case ')':
                position++; column++;
                return new Token(Token.Type.RPAREN, ")", line, column - 1);
            case '"':
                return handleString();
            default:
                if (Character.isLetter(currentChar)) {
                    return handleIdentifier();
                } else if (Character.isDigit(currentChar)) {
                    return handleNumber();
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
            //show a warning but keep going
            return new Token(Token.Type.WARNING,
                String.format("Unclosed comment starting at (%d:%d) - comment will be closed", 
                            startLine, startColumn),
                startLine, startColumn);
        }
        
        return nextToken(); // Skip the comment and move on
    }
    
    private Token handleIdentifier() {
        StringBuilder sb = new StringBuilder();
        int startColumn = column;
        
        // Collect all letters
        while (position < input.length() && 
               Character.isLetter(input.charAt(position))) {
            sb.append(input.charAt(position));
            position++; column++;
        }
        
        String word = sb.toString();
        
        // Check for keywords
        switch (word) {
            case "print": return new Token(Token.Type.PRINT, word, line, startColumn);
            case "while": return new Token(Token.Type.WHILE, word, line, startColumn);
            case "if": return new Token(Token.Type.IF, word, line, startColumn);
            case "int": 
            case "string":
            case "boolean": return new Token(Token.Type.I_TYPE, word, line, startColumn);
            case "true":
            case "false": return new Token(Token.Type.BOOLVAL, word, line, startColumn);
            default: 
                if (word.length() == 1) {
                    return new Token(Token.Type.ID, word, line, startColumn);
                } else {
                    return new Token(Token.Type.EOF,
                        String.format("Error: Invalid identifier '%s' - must be single letter", word),
                        line, startColumn);
                }
        }
    }
    
    private Token handleNumber() {
        StringBuilder sb = new StringBuilder();
        int startColumn = column;
        
        // Collect the digits
        while (position < input.length() && 
               Character.isDigit(input.charAt(position))) {
            sb.append(input.charAt(position));
            position++; column++;
        }
        
        // check if the next char is a letter
        if (position < input.length() && Character.isLetter(input.charAt(position))) {
            return new Token(Token.Type.EOF,
                String.format("Error: Invalid number format at (%d:%d) - letters cannot immediately follow numbers", 
                            line, startColumn),
                line, startColumn);
        }
        
        return new Token(Token.Type.NUMBER, sb.toString(), line, startColumn);
    }
    
    private Token handleString() {
        StringBuilder sb = new StringBuilder();
        int startColumn = column;
        
        // Skip opening quote
        position++; column++;
        
        // main loop for collecting the string
        while (position < input.length() && 
               input.charAt(position) != '"') {
            // If we hit a newline or end of input then the string wasn't closed
            if (input.charAt(position) == '\n' || position >= input.length()) {
                return new Token(Token.Type.EOF,
                    String.format("Error: Unclosed string starting at (%d:%d) - missing closing quote", 
                    line, startColumn),
                    line, startColumn);
            }
            sb.append(input.charAt(position));
            position++; column++;
        }
        
        // Skip closing quote
        position++; column++;
        
        return new Token(Token.Type.STRING_LIT, sb.toString(), line, startColumn);
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
