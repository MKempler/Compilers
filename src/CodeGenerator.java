public class CodeGenerator {
    private ASTNode ast;
    private SymbolTable symbolTable;
    private boolean verboseMode;
    
    public CodeGenerator(ASTNode ast, SymbolTable symbolTable, boolean verboseMode) {
        this.ast = ast;
        this.symbolTable = symbolTable;
        this.verboseMode = verboseMode;
    }
    
    public String generate() {
        if (verboseMode) {
            System.out.println("CODE GENERATOR: Starting code generation");
        }
        
        return "00 ; BRK - Program End\n";
    }
} 