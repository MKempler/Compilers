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
} 