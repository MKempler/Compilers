public class Parser {
    private Lexer lexer;
    private Token currentToken;
    private boolean verboseMode = true;
    private int errorCount = 0;
    
   
    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.nextToken();
    }
    
    public CSTNode parse() {
        if (verboseMode) {
            System.out.println("PARSER: parse()");
        }
        
        // ztart parsing from the Program rule
        CSTNode rootNode = parseProgram();
        
        if (errorCount == 0) {
            System.out.println("PARSER: Parse completed successfully");
        } else {
            System.out.println("PARSER: Parse failed with " + errorCount + " error(s)");
        }
        
        return rootNode;
    }
    
    private CSTNode parseProgram() {
        if (verboseMode) {
            System.out.println("PARSER: parseProgram()");
        }
        
        CSTNode programNode = new CSTNode("Program");
        
        // Parse Block
        CSTNode blockNode = parseBlock();
        programNode.addChild(blockNode);
        
        // Match EOP ($)
        if (match(Token.Type.EOP)) {
            programNode.addChild(new CSTNode("$", currentToken));
            nextToken();
        } else {
            reportError("Expected end of program marker ($)");
        }
        
        return programNode;
    }
    
    private CSTNode parseBlock() {
        if (verboseMode) {
            System.out.println("PARSER: parseBlock()");
        }
        
        CSTNode blockNode = new CSTNode("Block");
        
        // Match opening brace
        if (match(Token.Type.OPEN_BLOCK)) {
            blockNode.addChild(new CSTNode("{", currentToken));
            nextToken();
            
            // Parse StatementList
            CSTNode statementListNode = parseStatementList();
            blockNode.addChild(statementListNode);
            
            // Match closing brace
            if (match(Token.Type.CLOSE_BLOCK)) {
                blockNode.addChild(new CSTNode("}", currentToken));
                nextToken();
            } else {
                reportError("Expected closing brace (})");
            }
        } else {
            reportError("Expected opening brace ({)");
        }
        
        return blockNode;
    }
    
    private CSTNode parseStatementList() {
        if (verboseMode) {
            System.out.println("PARSER: parseStatementList()");
        }
        
        CSTNode statementListNode = new CSTNode("Statement List");
        
        
        return statementListNode;
    }
    
    private boolean match(Token.Type expectedType) {
        return currentToken.getType() == expectedType;
    }
    
    private void nextToken() {
        currentToken = lexer.nextToken();
    }
    
    private void reportError(String message) {
        errorCount++;
        System.out.println("PARSER: ERROR: " + message + " got [" + 
                          currentToken.getType() + "] with value '" + 
                          currentToken.getLexeme() + "' on line " + 
                          currentToken.getLine());
    }
    
    public int getErrorCount() {
        return errorCount;
    }
    
    public void setVerboseMode(boolean verboseMode) {
        this.verboseMode = verboseMode;
    }
} 