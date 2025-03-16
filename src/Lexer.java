public class Lexer {
    private String programText;     
    private int position;      
    private int line;        
    private int column;       
    private boolean inString = false;  
    private int stringStartLine;
    private int stringStartColumn;
    
 
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
                if (inString) {
                    inString = false;
                    return new Token(Token.Type.ERROR,
                        String.format("Error: Unclosed string starting at (%d:%d) - missing closing quote", 
                        stringStartLine, stringStartColumn),
                        stringStartLine, stringStartColumn);
                }
                position++; column++;
                return new Token(Token.Type.CLOSE_BLOCK, "}", line, column - 1);

            case '$':
                if (inString) {
                    inString = false;
                    return new Token(Token.Type.ERROR,
                        String.format("Error: Unclosed string starting at (%d:%d) - missing closing quote", 
                        stringStartLine, stringStartColumn),
                        stringStartLine, stringStartColumn);
                }
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
                if (!inString) {
                    // Opening quote
                    stringStartLine = line;
                    stringStartColumn = column;
                    inString = true;
                    position++; column++;
                    return new Token(Token.Type.QUOTE, "\"", line, column - 1);
                } else {
                    // Closing quote
                    inString = false;
                    position++; column++;
                    return new Token(Token.Type.QUOTE, "\"", line, column - 1);
                }
            
            default:
                if (inString) {
                    if (programText.charAt(position) == '\n' || position >= programText.length()) {
                        inString = false;
                        return new Token(Token.Type.ERROR,
                            String.format("Error: Unclosed string starting at (%d:%d) - missing closing quote", 
                            stringStartLine, stringStartColumn),
                            stringStartLine, stringStartColumn);
                    }

                    if (currentChar == ' ') {
                        position++; column++;
                        return new Token(Token.Type.SPACE, " ", line, column - 1);
                    }

                    if (currentChar >= 'a' && currentChar <= 'z') {
                        position++; column++;
                        return new Token(Token.Type.CHAR, String.valueOf(currentChar), line, column - 1);
                    }
                    position++; column++;
                    
                    if (Character.isDigit(currentChar)) {
                        return new Token(Token.Type.ERROR,
                            String.format("Error: Unexpected character '%c' - numbers are not allowed in strings", 
                            currentChar), line, column - 1);
                    }

                    return new Token(Token.Type.ERROR,
                        String.format("Error: Unexpected character '%c' - Uppercase letters are invlaid and evil ", 
                        currentChar), line, column - 1);
                }
                
                if (currentChar >= 'a' && currentChar <= 'z') {
                    return handleIdentifier();

                } else if (Character.isLetter(currentChar)) {
                    position++; column++;

                    return new Token(Token.Type.ERROR,
                        String.format("Error: Unexpected character '%c' - Uppercase letters are invalid and evil", 
                        currentChar), line, column - 1);
                        
                } else if (Character.isDigit(currentChar)) {
                    return handleNumber();
                }
        }
        
        // Handle characters that aren't in the grammar
        position++; column++;
        return new Token(Token.Type.ERROR, 
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
        int startColumn = column;
        
        // Check for any keywords first
        if (position + 3 <= programText.length() && programText.substring(position, position + 3).equals("int")) {
            position += 3; column += 3;
            return new Token(Token.Type.I_TYPE, "int", line, startColumn);
        }
        
        if (position + 6 <= programText.length() && programText.substring(position, position + 6).equals("string")) {
            position += 6; column += 6;
            return new Token(Token.Type.I_TYPE, "string", line, startColumn);
        }
        
        if (position + 7 <= programText.length() && programText.substring(position, position + 7).equals("boolean")) {
            position += 7; column += 7;
            return new Token(Token.Type.I_TYPE, "boolean", line, startColumn);
        }
        
        if (position + 5 <= programText.length() && programText.substring(position, position + 5).equals("print")) {
            position += 5; column += 5;
            return new Token(Token.Type.PRINT, "print", line, startColumn);
        }
        
        if (position + 5 <= programText.length() && programText.substring(position, position + 5).equals("while")) {
            position += 5; column += 5;
            return new Token(Token.Type.WHILE, "while", line, startColumn);
        }
        
        if (position + 2 <= programText.length() && programText.substring(position, position + 2).equals("if")) {
            position += 2; column += 2;
            return new Token(Token.Type.IF, "if", line, startColumn);
        }
        
        if (position + 4 <= programText.length() && programText.substring(position, position + 4).equals("true")) {
            position += 4; column += 4;
            return new Token(Token.Type.BOOLVAL, "true", line, startColumn);
        }
        
        if (position + 5 <= programText.length() && programText.substring(position, position + 5).equals("false")) {
            position += 5; column += 5;
            return new Token(Token.Type.BOOLVAL, "false", line, startColumn);
        }
        
        // If it's not a keyword thenit's a single char identifier
        if (position < programText.length() && programText.charAt(position) >= 'a' && programText.charAt(position) <= 'z') {
            char id = programText.charAt(position);
            position++; column++;
            return new Token(Token.Type.ID, String.valueOf(id), line, startColumn);
        }
        
        // otherwise error
        StringBuilder sb = new StringBuilder();
        while (position < programText.length() && Character.isLetter(programText.charAt(position))) {
            sb.append(programText.charAt(position));
            position++; column++;
        }
        String word = sb.toString();
        
        return new Token(Token.Type.ERROR,
            String.format("Error: Invalid identifier '%s' - must be single letter", word),
            line, startColumn);
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
        while (position < programText.length() && 
               Character.isWhitespace(programText.charAt(position))) {
            if (inString) {
                return; // Don't skip any whitespace in strings
            }
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
