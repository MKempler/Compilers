public class Lexer {
    private String programText;     
    private int position;      
    private int line;        
    private int column;       
    private boolean inString = false;  
    
 
    public Lexer(String programText) {
        this.programText = programText; //source code
        this.position = 0; // position we are at in the program
        this.line = 1; 
        this.column = 1; 
    }
    
    
    public Token nextToken() {
        // Skip whitespace and comments before the next token
        skipWhitespace();
        
        // Check if we've reached the end of the program
        if (position >= programText.length()) {
            return new Token(Token.Type.EOF, "", line, column);
        }
        
        char currentChar = programText.charAt(position);
        
        // Handles the different types of tokens
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
                if (position + 1 < programText.length() && programText.charAt(position + 1) == '*') {
                    return handleComment();
                }
                
            case '=':
                if (position + 1 < programText.length() && programText.charAt(position + 1) == '=') {
                    position += 2; column += 2;
                    return new Token(Token.Type.EQUALS, "==", line, column - 2);
                }
                position++; column++;
                return new Token(Token.Type.ASSIGN_OP, "=", line, column - 1);

            case '!':
                if (position + 1 < programText.length() && programText.charAt(position + 1) == '=') {
                    position += 2; column += 2;
                    return new Token(Token.Type.NOT_EQUALS, "!=", line, column - 2);
                }
                // Handle "illegal" !
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
                inString = !inString;
                position++; column++;
                return new Token(Token.Type.QUOTE, "\"", line, column - 1);
            
            default:
                if (inString) {
                    if (Character.isWhitespace(currentChar)) {
                        position++; column++;
                        return new Token(Token.Type.SPACE, " ", line, column - 1);
                    }
                    position++; column++;
                    return new Token(Token.Type.CHAR, String.valueOf(currentChar), line, column - 1);
                }
                if (Character.isLetter(currentChar)) {
                    return handleIdentifier();
                } else if (Character.isDigit(currentChar)) {
                    return handleNumber();
                }
        }
        
        // Handle characters that aren't in the grammar
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
        
        while (position < programText.length() - 1) {
            // Check for comment end 
            if (programText.charAt(position) == '*' && programText.charAt(position + 1) == '/') {
                foundEnd = true;
                position += 2;
                column += 2;
                break;
            }
            
            // Handle when the string contains a new line
            if (programText.charAt(position) == '\n') {
                line++;
                column = 1;
                comment.append('\n');
            } else {
                column++;
                comment.append(programText.charAt(position));
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
        while (position < programText.length() && 
               Character.isLetter(programText.charAt(position))) {
            sb.append(programText.charAt(position));
            position++; column++;
        }
        //converts collected letters into a string
        String word = sb.toString();
        
        // Check for keywords
        switch (word) {
            //output
            case "print": return new Token(Token.Type.PRINT, word, line, startColumn);
            //loops
            case "while": return new Token(Token.Type.WHILE, word, line, startColumn);
            case "if": return new Token(Token.Type.IF, word, line, startColumn);
            //Data types
            case "int": 
            case "string":
            case "boolean": return new Token(Token.Type.I_TYPE, word, line, startColumn);
            //booleans
            case "true":
            case "false": return new Token(Token.Type.BOOLVAL, word, line, startColumn);
            
            //variables
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
        while (position < programText.length() && 
               Character.isDigit(programText.charAt(position))) {
            sb.append(programText.charAt(position));
            position++; column++;
        }
        
        // check if the next char is a letter
        if (position < programText.length() && Character.isLetter(programText.charAt(position))) {
            return new Token(Token.Type.EOF,
                String.format("Error: Invalid number format at (%d:%d) - letters cannot immediately follow numbers", 
                            line, startColumn),
                line, startColumn);
        }
        
        return new Token(Token.Type.NUMBER, sb.toString(), line, startColumn);
    }
    
    
    //  skip whitespace
    private void skipWhitespace() {
        //skips any spaces and newlines
        while (position < programText.length() && 
               Character.isWhitespace(programText.charAt(position))) {
                //line number for error tracking
            if (programText.charAt(position) == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
            position++;
        }
    }
}
