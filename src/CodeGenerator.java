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
    
    // System call values
    private static final int SYS_PRINT_INT = 1;  
    private static final int SYS_PRINT_STRING = 2; 
    
    private static final int MEMORY_START = 0x0010;
    
   
    private int currentMemoryAddress;

    private java.util.Map<String, Integer> variableAddresses;
    private int labelCounter = 0;
    
    private static final int TEMP_RESULT_ADDRESS = 0x00FF;
    
    private static final int STRING_MEMORY_START = 0x0100;
    private int currentStringAddress;
    private java.util.Map<String, Integer> stringAddresses;
        
    public CodeGenerator(ASTNode ast, SymbolTable symbolTable, boolean verboseMode) {
        this.ast = ast;
        this.symbolTable = symbolTable;
        this.verboseMode = verboseMode;
        this.machineCode = new StringBuilder();
        this.currentMemoryAddress = MEMORY_START;
        this.variableAddresses = new java.util.HashMap<>();
        this.currentStringAddress = STRING_MEMORY_START;
        this.stringAddresses = new java.util.HashMap<>();
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
            
            case "IntLiteral":
                generateIntLiteralCode(node);
                break;
            
            case "Identifier":
                generateIdentifierCode(node);
                break;
                
            case "Addition":
                generateAdditionCode(node);
                break;
                
            case "StringLiteral":
                generateStringLiteralCode(node);
                break;
                
            case "Equals":
                generateEqualsCode(node);
                break;
                
            case "NotEquals":
                generateNotEqualsCode(node);
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
        if (node.getChildren().isEmpty()) {
            appendComment("ERROR: Print statement with no expression");
            return;
        }
        
        appendComment("Print Statement");
        
        ASTNode exprNode = node.getChildren().get(0);
        
        generateCode(exprNode);
        
        //accumulator to Y register
        append(LDY_CONST, "LDY", "Copy accumulator value to Y register for printing");
        machineCode.append("   00\n"); // Placeholder, will be replaced with actual value transfer logic
        
        // Load into X register
        append(LDX_CONST, "LDX", "Load system call code for print integer");
        machineCode.append("   0").append(SYS_PRINT_INT).append("\n");
        
       
        append(SYS, "SYS", "System call - Print integer");
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
        if (node.getChildren().size() >= 2) {
            ASTNode idNode = node.getChildren().get(0);
            ASTNode exprNode = node.getChildren().get(1);
            
            String varName = idNode.getValue();
            Integer address = variableAddresses.get(varName);
            
            if (address == null) {
                appendComment("ERROR: Undefined variable: " + varName);
                return;
            }
            
            appendComment("Assignment to variable " + varName);
            
            generateCode(exprNode);
            
            append(STA, "STA", "Store accumulator to " + varName + " at " + toHexString(address));
            machineCode.append("   ").append(toHexWithoutPrefix(address)).append("\n");
        }
    }
    
    private void generateIntLiteralCode(ASTNode node) {
        // load the literal value into the accumulator
        int value = Integer.parseInt(node.getValue());
        append(LDA_CONST, "LDA", "Load value " + value);
        machineCode.append("   ").append(toHexWithoutPrefix(value)).append("\n");
    }
    
    private void generateStringLiteralCode(ASTNode node) {
        // Get string value
        String stringValue = node.getValue();
        
        // Check if we've already allocated this string
        Integer address = stringAddresses.get(stringValue);
        
        if (address == null) {
            
            address = allocateStringMemory(stringValue);
            
            appendComment("String literal: \"" + stringValue + "\" stored at " + toHexString(address));
        }
        
        append(LDA_CONST, "LDA", "Load address of string \"" + stringValue + "\"");
        machineCode.append("   ").append(toHexWithoutPrefix(address)).append("\n");
    }
    
    private void generateIdentifierCode(ASTNode node) {
        // Load the var value into the accumulator
        String varName = node.getValue();
        Integer address = variableAddresses.get(varName);
        
        if (address == null) {
            appendComment("ERROR: Undefined variable: " + varName);
            return;
        }
        
        // Load from vars memory location to accumulator
        append(LDA_MEM, "LDA", "Load " + varName + " from " + toHexString(address));
        machineCode.append("   ").append(toHexWithoutPrefix(address)).append("\n");
    }
    
    private void generateAdditionCode(ASTNode node) {
        if (node.getChildren().size() < 2) {
            appendComment("ERROR: Addition requires two operands");
            return;
        }
        
        appendComment("Addition expression");
        
        ASTNode leftOperand = node.getChildren().get(0);
        generateCode(leftOperand);
        
        append(STA, "STA", "Store left operand result to temp memory");
        machineCode.append("   ").append(toHexWithoutPrefix(TEMP_RESULT_ADDRESS)).append("\n");
        
        ASTNode rightOperand = node.getChildren().get(1);
        generateCode(rightOperand);
        
        append(ADC, "ADC", "Add left operand to accumulator");
        machineCode.append("   ").append(toHexWithoutPrefix(TEMP_RESULT_ADDRESS)).append("\n");
    }
    
    private void generateEqualsCode(ASTNode node) {
        if (node.getChildren().size() < 2) {
            appendComment("ERROR: Equality comparison requires two operands");
            return;
        }
        
        appendComment("Equality comparison (==)");
        
        ASTNode leftOperand = node.getChildren().get(0);
        generateCode(leftOperand);
        
        append(LDX_CONST, "LDX", "Transfer accumulator to X register");
        machineCode.append("   00\n"); // Placeholder for transfer logic
        
        ASTNode rightOperand = node.getChildren().get(1);
        generateCode(rightOperand);
        
        String trueLabel = generateLabel("equal_true");
        String doneLabel = generateLabel("equal_done");
        
        append(STA, "STA", "Store right operand to temporary memory");
        machineCode.append("   ").append(toHexWithoutPrefix(TEMP_RESULT_ADDRESS)).append("\n");
        
        append(CPX, "CPX", "Compare X register with memory");
        machineCode.append("   ").append(toHexWithoutPrefix(TEMP_RESULT_ADDRESS)).append("\n");
        
        appendComment("Branch if equal (Z flag = 1)");
        
        append(LDA_CONST, "LDA", "Load 0 (false) into accumulator");
        machineCode.append("   00\n");
        
        append(BNE, "BNE", "Branch if not equal to " + doneLabel);
        machineCode.append("   ").append("XX\n"); // Placeholder for branch offset
        
        appendComment(trueLabel + ":");
        append(LDA_CONST, "LDA", "Load 1 (true) into accumulator");
        machineCode.append("   01\n");
        
        appendComment(doneLabel + ":");
    }
    
    private void generateNotEqualsCode(ASTNode node) {
        if (node.getChildren().size() < 2) {
            appendComment("ERROR: Not equals comparison requires two operands");
            return;
        }
        
        appendComment("Not equals comparison (!=)");
        
        ASTNode leftOperand = node.getChildren().get(0);
        generateCode(leftOperand);
        
        append(LDX_CONST, "LDX", "Transfer accumulator to X register");
        machineCode.append("   00\n");
        
        ASTNode rightOperand = node.getChildren().get(1);
        generateCode(rightOperand);
        
        String trueLabel = generateLabel("not equal true");
        String doneLabel = generateLabel("not equal done");
        
        append(STA, "STA", "Store right operand to temporary memory");
        machineCode.append("   ").append(toHexWithoutPrefix(TEMP_RESULT_ADDRESS)).append("\n");
        
        append(CPX, "CPX", "Compare X register with memory");
        machineCode.append("   ").append(toHexWithoutPrefix(TEMP_RESULT_ADDRESS)).append("\n");
        
        appendComment("Branch if not equal (Z flag = 0)");
        
        append(LDA_CONST, "LDA", "Load 1 (true) into accumulator");
        machineCode.append("   01\n");
        
        append(BNE, "BNE", "Branch if not equal to " + doneLabel);
        machineCode.append("   ").append("XX\n"); 
        
        appendComment(trueLabel + ":");
        append(LDA_CONST, "LDA", "Load 0 (false) into accumulator");
        machineCode.append("   00\n");
        
        appendComment(doneLabel + ":");
    }
    
    private void generateIfCode(ASTNode node) {
        if (node.getChildren().size() < 2) {
            appendComment("ERROR: If statement requires condition and block");
            return;
        }
        
        appendComment("If Statement");
        
        ASTNode conditionNode = node.getChildren().get(0);
        generateCode(conditionNode);
        
        String endIfLabel = generateLabel("end if");
        
        // Compare accumulator with 0
        append(LDX_CONST, "LDX", "Load 0 for comparison");
        machineCode.append("   00\n");
        
        append(STA, "STA", "Store condition result");
        machineCode.append("   ").append(toHexWithoutPrefix(TEMP_RESULT_ADDRESS)).append("\n");
        
        append(CPX, "CPX", "Compare condition with 0)");
        machineCode.append("   ").append(toHexWithoutPrefix(TEMP_RESULT_ADDRESS)).append("\n");
        
        // If equal branch past the if block
        append(BNE, "BNE", "Execute if block if condition is true");
        machineCode.append("   02\n");
        
        // Generate the if block code
        ASTNode blockNode = node.getChildren().get(1);
        generateCode(blockNode);
        
        appendComment("End of if statement");
    }
    
    private void generateWhileCode(ASTNode node) {
        if (node.getChildren().size() < 2) {
            appendComment("ERROR: While statement requires condition and block");
            return;
        }
        
        appendComment("While Loop");
        
        String whileStartLabel = generateLabel("whilestart");
        String whileEndLabel = generateLabel("whileend");
        
        
        appendComment(whileStartLabel + ":");
        
        // Generate the condition code 
        ASTNode conditionNode = node.getChildren().get(0);
        generateCode(conditionNode);
        
        
        append(LDX_CONST, "LDX", "Load 0 for comparison");
        machineCode.append("   00\n");
        
        append(STA, "STA", "Store condition result");
        machineCode.append("   ").append(toHexWithoutPrefix(TEMP_RESULT_ADDRESS)).append("\n");
        
        append(CPX, "CPX", "Compare condition with 0");
        machineCode.append("   ").append(toHexWithoutPrefix(TEMP_RESULT_ADDRESS)).append("\n");
        
        // If condition is false exit loop
        append(BNE, "BNE", "Execute loop body if condition is true");
        machineCode.append("   02\n");
        
        ASTNode blockNode = node.getChildren().get(1);
        generateCode(blockNode);
        
        append(LDA_CONST, "LDA", "Jump back to start of while loop");
        machineCode.append("   ").append(whileStartLabel.hashCode() & 0xFF).append("\n");
        
        appendComment(whileEndLabel + ":");
        appendComment("End of while loop");
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
    
    private int allocateStringMemory(String stringValue) {
        if (stringAddresses.containsKey(stringValue)) {
            return stringAddresses.get(stringValue);
        }
        
        int address = currentStringAddress;
        stringAddresses.put(stringValue, address);
        
        currentStringAddress += stringValue.length() + 1;
        
        return address;
    }
    
    private String generateLabel(String prefix) {
        return prefix + "_" + (labelCounter++);
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
    
    private String toHexWithoutPrefix(int value) {
        return Integer.toHexString(value).toUpperCase();
    }
} 