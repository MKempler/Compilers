public class SemanticAnalyzer {
    private ASTNode ast;
    private SymbolTable symbolTable;
    private boolean verboseMode;
    private int errorCount;
    private int warningCount;
    
    public SemanticAnalyzer(ASTNode ast, boolean verboseMode) {
        this.ast = ast;
        this.symbolTable = new SymbolTable();
        this.verboseMode = verboseMode;
        this.errorCount = 0;
        this.warningCount = 0;
    }
    
    public void analyze() {
        if (verboseMode) {
            System.out.println("SEMANTIC: Starting semantic analysis");
        }
        
        // starts with the root block node
        analyzeBlock(ast);
        
        if (errorCount == 0) {
            System.out.println("SEMANTIC: Analysis completed successfully");
        } else {
            System.out.println("SEMANTIC: Analysis completed with " + errorCount + " errors and " + warningCount + " warnings");
        }
    }
    
    private void analyzeBlock(ASTNode blockNode) {
        if (verboseMode) {
            System.out.println("SEMANTIC: Analyzing block");
        }
        
        // Enter a new scope for this block
        symbolTable.enterScope();
        
        // Analyze each statement in the block
        for (ASTNode child : blockNode.getChildren()) {
            analyzeNode(child);
        }
        
      
        symbolTable.exitScope();
    }
    
    private void analyzeNode(ASTNode node) {
        
        if (verboseMode) {
            System.out.println("SEMANTIC: Analyzing node of type " + node.getType());
        }
        
        // Handle different node types
        String nodeType = node.getType();
        
        if (nodeType.equals("Variable_Declaration")) {
            analyzeVariableDeclaration(node);
        }
    }
    
    private void analyzeVariableDeclaration(ASTNode varDeclNode) {
        if (verboseMode) {
            System.out.println("SEMANTIC: Analyzing variable declaration");
        }
        
        //  type and identifier
        if (varDeclNode.getChildren().size() >= 2) {
            ASTNode typeNode = varDeclNode.getChildren().get(0);
            ASTNode idNode = varDeclNode.getChildren().get(1);
            
            String type = typeNode.getValue();
            String name = idNode.getValue();
            int line = idNode.getLine();
            int column = idNode.getColumn();
            
            Symbol symbol = new Symbol(name, type, symbolTable.getCurrentScope(), line, column);
            
            boolean added = symbolTable.add(symbol);
            
            if (!added) {
                reportError("Variable '" + name + "' is already declared in this scope", line, column);
           
            } else if (verboseMode) {
                System.out.println("SEMANTIC: Added variable '" + name + "' of type '" + type + 
                                 "' to scope " + symbolTable.getCurrentScope());
            }
        }
    }
    
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
    
    public int getErrorCount() {
        return errorCount;
    }
    
    public int getWarningCount() {
        return warningCount;
    }
    
    private void reportError(String message, int line, int column) {
        errorCount++;
        System.out.println("SEMANTIC ERROR at line " + line + ", column " + column + ": " + message);
    }
    
    private void reportWarning(String message, int line, int column) {
        warningCount++;
        System.out.println("SEMANTIC WARNING at line " + line + ", column " + column + ": " + message);
    }
} 