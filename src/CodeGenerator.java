public class CodeGenerator {
    private ASTNode ast;
    private SymbolTable symbolTable;
    private boolean verboseMode;
    private StringBuilder machineCode;
    
    //  Opcodes
    private static final String LDA_CONST = "A9";   
    private static final String LDA_MEM = "AD";      
    private static final String STA = "8D";        
    private static final String ADC = "6D";         
    private static final String LDX_CONST = "A2";  
    private static final String LDX_MEM = "AE";     
    private static final String LDY_CONST = "A0";   
    private static final String LDY_MEM = "AC";     
    private static final String NOP = "EA";          
    private static final String CPX = "EC";        
    private static final String BNE = "D0";         
    private static final String INC = "EE";        
    private static final String SYS = "FF";
    private static final String BRK = "00";
    
    
    private static final int MEMORY_START = 0x0010;
    
   
    private int currentMemoryAddress;
    
    private java.util.Map<String, Integer> variableAddresses;
    
    public CodeGenerator(ASTNode ast, SymbolTable symbolTable, boolean verboseMode) {
        this.ast = ast;
        this.symbolTable = symbolTable;
        this.verboseMode = verboseMode;
        this.machineCode = new StringBuilder();
        this.currentMemoryAddress = MEMORY_START;
        this.variableAddresses = new java.util.HashMap<>();
    }
    
    public String generate() {
        if (verboseMode) {
            System.out.println("CODE GENERATOR: Starting code generation");
        }
        
        // Clear previous code
        machineCode.setLength(0);
        
        
        appendComment("6502a Machine Code - Generated from source");
        appendComment("Memory allocation starts at " + toHexString(MEMORY_START));
        
        generateCode(ast);
        
        append(BRK, "BRK", "Program End");
        
        return machineCode.toString();
    }
    
    private void generateCode(ASTNode node) {
        if (node == null) {
            return;
        }
        
        if (verboseMode) {
            System.out.println("CODE GENERATOR: Processing node of type " + node.getType());
        }
        
        // Process node based on its type
        switch (node.getType()) {
            case "BLOCK":
                generateBlockCode(node);
                break;
            
                case "Print_Statement":
                generatePrintCode(node);
                break;
            
                case "Variable_Declaration":
                generateVarDeclCode(node);
                break;
            
                case "Assignment_Statement":
                generateAssignmentCode(node);
                break;
            
                case "If_Statement":
                generateIfCode(node);
                break;
            
                case "While_Statement":
                generateWhileCode(node);
                break;
            
                default:
                break;
        }
    }
    
    private void generateBlockCode(ASTNode node) {
        appendComment("Block Start");
        
        // Process each child node in the block
        for (ASTNode child : node.getChildren()) {
            generateCode(child);
        }
        
        appendComment("Block End");
    }
    
    private void generatePrintCode(ASTNode node) {
    }
    
    private void generateVarDeclCode(ASTNode node) {
        // type and identifier
        if (node.getChildren().size() >= 2) {
            ASTNode typeNode = node.getChildren().get(0);
            ASTNode idNode = node.getChildren().get(1);
            
            String varType = typeNode.getValue();
            String varName = idNode.getValue();
            
            // Allocate memory for this variable
            int address = allocateMemory(varName);
            
            if (verboseMode) {
                System.out.println("CODE GENERATOR: Allocated variable '" + varName + "' at address " + toHexString(address));
            }
            
            appendComment("Variable Declaration: " + varType + " " + varName + " at address " + toHexString(address));
        }
    }
    
    private void generateAssignmentCode(ASTNode node) {
    }
    
    private void generateIfCode(ASTNode node) {
    }
    
    private void generateWhileCode(ASTNode node) {
    }
   
    private int allocateMemory(String varName) {
        
        if (variableAddresses.containsKey(varName)) {
            return variableAddresses.get(varName);
        }
        
        int address = currentMemoryAddress;
        variableAddresses.put(varName, address);
        
        currentMemoryAddress += 1;
        
        return address;
    }
   
    private void append(String opcode, String mnemonic, String comment) {
        machineCode.append(opcode).append(" ; ").append(mnemonic);
        if (comment != null && !comment.isEmpty()) {
            machineCode.append(" - ").append(comment);
        }
        machineCode.append("\n");
    }
    
 
    private void appendComment(String comment) {
        machineCode.append("; ").append(comment).append("\n");
    }
    
   
    private String toHexString(int value) {
        return "$" + Integer.toHexString(value).toUpperCase();
    }
} 