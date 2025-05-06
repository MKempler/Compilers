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
    
    private static final int MEMORY_START = 0x0080;
    
    private int currentMemoryAddress;

    private java.util.Map<String, Integer> variableAddresses;
    private int labelCounter = 0;
    
    // Temporary memory locations 
    private static final int TEMP_RESULT_ADDRESS = 0x00FC; 
    private static final int TEMP_LEFT_OPERAND = 0x00FF;
    private static final int TEMP_RIGHT_OPERAND = 0x00FE;
    private static final int TEMP_STRING_PTR = 0x00FD;
    
    private static final int STRING_MEMORY_START = 0x0090;
    private int currentStringAddress;
    private java.util.Map<String, Integer> stringAddresses;
    
    // Label resolution
    private StringBuilder codeBuffer;
    private java.util.Map<String, Integer> labelPositions;
    private java.util.List<BranchFix> branchFix;
    
    // track branch instruction
    private static class BranchFix {
        final String targetLabel;
        final int instructionPosition;
        
        BranchFix(String targetLabel, int instructionPosition) {
            this.targetLabel = targetLabel;
            this.instructionPosition = instructionPosition;
        }
    }
    
    public CodeGenerator(ASTNode ast, SymbolTable symbolTable, boolean verboseMode) {
        this.ast = ast;
        this.symbolTable = symbolTable;
        this.verboseMode = verboseMode;
        this.machineCode = new StringBuilder();
        this.codeBuffer = new StringBuilder();
        this.currentMemoryAddress = MEMORY_START;
        this.variableAddresses = new java.util.HashMap<>();
        this.currentStringAddress = STRING_MEMORY_START;
        this.stringAddresses = new java.util.HashMap<>();
        this.labelPositions = new java.util.HashMap<>();
        this.branchFix = new java.util.ArrayList<>();
    }
    
    public String generate() {
        if (verboseMode) {
            System.out.println("CODE GENERATOR: Starting code generation");
        }
        
        // Clear previous code
        machineCode.setLength(0);
        codeBuffer.setLength(0);
        labelPositions.clear();
        branchFix.clear();
        
        // First pass
        appendComment("6502a Machine Code - Generated from source");
        appendComment("Memory allocation starts at " + toHexString(MEMORY_START));
        appendComment("String storage starts at " + toHexString(STRING_MEMORY_START));
        
        generateCode(ast);
        
        append(BRK, "BRK", "Program End");
        
        // Second pass - resolve branch offsets
        resolveBranchOffsets();
        
        // Output all string literals
        appendComment("String Data Section (Zero Page)");
        for (java.util.Map.Entry<String, Integer> entry : stringAddresses.entrySet()) {
            String stringValue = entry.getKey();
            int address = entry.getValue();
            
            appendComment("String: \"" + stringValue + "\" at " + toHexString(address));
            
            //  ASCII bytes for each char in the string
            for (int i = 0; i < stringValue.length(); i++) {
                char c = stringValue.charAt(i);
                machineCode.append(String.format("   %02X ; '%c'\n", (int)c, c));
            }
            
            machineCode.append("   00 ; null terminator\n");
        }
        
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
                
            case "Value":
                if (node.getValue() != null && node.getValue().matches("\\d+")) {
                    generateIntLiteralCode(node); 
                } else {
                    generateBooleanExpressionCode(node);
                }
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
                generateBooleanExpressionCode(node);
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
        
        ASTNode literal = exprNode;
        if ("Expression".equals(literal.getType()) && literal.getChildren().size() == 1) {
            literal = literal.getChildren().get(0);
        }
        
        // check type of expression to determine how to print it
        if (literal.getType().equals("StringLiteral")) {
        
            String s = literal.getValue();
            int addr = allocateStringMemory(s);
            appendComment("Print String");
            // load low byte into A
            append(LDA_CONST, "LDA", "Load address of string \"" + s + "\"");
            emitImmediate(addr & 0xFF);
            
            append(STA, "STA", "Store string address in temp");
            emitAddress(TEMP_STRING_PTR);
           
            append(LDY_MEM, "LDY", "Load Y with string address from temp");
            emitAddress(TEMP_STRING_PTR);
            
            append(LDX_CONST, "LDX", "Load system call code for print string");
            emitImmediate(SYS_PRINT_STRING);
            append(SYS, "SYS", "System call - Print string");
            return;
        } else {
            // Default int print
            appendComment("Print Integer");
            generateCode(exprNode);
            
            append(STA, "STA", "Store accumulator value in temporary memory");
            emitAddress(TEMP_LEFT_OPERAND);
    
            append(LDY_MEM, "LDY", "Load Y register from temporary memory");
            emitAddress(TEMP_LEFT_OPERAND);
            
            // Load into X register
            append(LDX_CONST, "LDX", "Load system call code for print integer");
            emitImmediate(SYS_PRINT_INT);
            
            append(SYS, "SYS", "System call - Print integer");
        }
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
            emitAddress(address);
        }
    }
    
    private void generateIntLiteralCode(ASTNode node) {
        // load the literal value into the accumulator
        int value = Integer.parseInt(node.getValue());
        append(LDA_CONST, "LDA", "Load value " + value);
        emitImmediate(value);
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
        emitImmediate(address & 0xFF);
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
        emitAddress(address);
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
        emitAddress(TEMP_LEFT_OPERAND);
        
        ASTNode rightOperand = node.getChildren().get(1);
        generateCode(rightOperand);
        
        append(ADC, "ADC", "Add left operand to accumulator");
        emitAddress(TEMP_LEFT_OPERAND);
    }
    
    private void generateEqualsCode(ASTNode node) {
        if (node.getChildren().size() < 2) {
            appendComment("ERROR: Equality comparison requires two operands");
            return;
        }
        
        appendComment("Equality comparison (==)");
        
        ASTNode leftOperand = node.getChildren().get(0);
        generateCode(leftOperand);
        
        append(STA, "STA", "Store left operand to temporary memory");
        emitAddress(TEMP_LEFT_OPERAND);
        
        append(LDX_MEM, "LDX", "Load left operand from temp into X");
        emitAddress(TEMP_LEFT_OPERAND);
        
        ASTNode rightOperand = node.getChildren().get(1);
        generateCode(rightOperand);
        
        append(STA, "STA", "Store right operand to temporary memory");
        emitAddress(TEMP_RIGHT_OPERAND);
        
        append(CPX, "CPX", "Compare left operand (X) with right operand (in memory)");
        emitAddress(TEMP_RIGHT_OPERAND);
        
        String doneLabel = generateLabel("equal_done");
        
        
        append(LDA_CONST, "LDA", "Load 0 (false) into accumulator assuming inequality");
        emitImmediate(0);
        
        // Skip to end if Z flag is 0 )
        int branchPos = getCurrentPosition();
        append(BNE, "BNE", "Branch to " + doneLabel + " if not equal (Z=0)");
        insertPlaceholder();
        addBranchFix(doneLabel, branchPos);
        
        // If here they are equal, load 1 
        append(LDA_CONST, "LDA", "Load 1 (true) into accumulator");
        emitImmediate(1);
        
        defineLabel(doneLabel);
        appendComment("End of equality comparison");
    }
    
    private void generateNotEqualsCode(ASTNode node) {
        if (node.getChildren().size() < 2) {
            appendComment("ERROR: Not equals comparison requires two operands");
            return;
        }
        
        appendComment("Not equals comparison (!=)");
        
        ASTNode leftOperand = node.getChildren().get(0);
        generateCode(leftOperand);
        
        append(STA, "STA", "Store left operand to temporary memory");
        emitAddress(TEMP_LEFT_OPERAND);
        
        append(LDX_MEM, "LDX", "Load left operand from temp into X");
        emitAddress(TEMP_LEFT_OPERAND);
        
        ASTNode rightOperand = node.getChildren().get(1);
        generateCode(rightOperand);
        
        append(STA, "STA", "Store right operand to temporary memory");
        emitAddress(TEMP_RIGHT_OPERAND);
        
        append(CPX, "CPX", "Compare left operand (X) with right operand (in memory)");
        emitAddress(TEMP_RIGHT_OPERAND);
        
        String doneLabel = generateLabel("not_equal_done");
        
        append(LDA_CONST, "LDA", "Load 0 (false) into accumulator assuming equality");
        emitImmediate(0);
        
        int branchPos = getCurrentPosition();
        append(BNE, "BNE", "Branch to " + doneLabel + " if not equal (Z=0)");
        insertPlaceholder();
        addBranchFix(doneLabel, branchPos);
        
        append(LDA_CONST, "LDA", "Load 1 (true) into accumulator for inequality");
        emitImmediate(1);
        
        defineLabel(doneLabel);
        appendComment("End of inequality comparison");
    }
    
    private void generateIfCode(ASTNode node) {
        if (node.getChildren().size() < 2) {
            appendComment("ERROR: If statement requires condition and block");
            return;
        }
        
        appendComment("If Statement");
        
        ASTNode conditionNode = node.getChildren().get(0);
        generateCode(conditionNode);
        
        String skipBlockLabel = generateLabel("if_skip_block");
        
        append(STA, "STA", "Store condition result");
        emitAddress(TEMP_LEFT_OPERAND);
        
        append(LDX_CONST, "LDX", "Load 1 for comparison");
        emitImmediate(1);
        
        append(CPX, "CPX", "Compare condition with 1");
        emitAddress(TEMP_LEFT_OPERAND);
        
        // position for branch fix
        int branchPos = getCurrentPosition();
        append(BNE, "BNE", "Skip next instruction if condition is 0");
        insertPlaceholder();
        
        // Record branch 
        addBranchFix(skipBlockLabel, branchPos);
        
        append(NOP, "NOP", "Placeholder skipped when condition is false");
        
        // Generate the if block code
        ASTNode blockNode = node.getChildren().get(1);
        generateCode(blockNode);
        
        defineLabel(skipBlockLabel);
        
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
        
        defineLabel(whileStartLabel);
        
        // Generate the condition code 
        ASTNode conditionNode = node.getChildren().get(0);
        generateCode(conditionNode);
        
        append(STA, "STA", "Store condition result");
        emitAddress(TEMP_LEFT_OPERAND);
        
        append(LDX_CONST, "LDX", "Load 0 for comparison");
        emitImmediate(0);
        
        append(CPX, "CPX", "Compare condition with 0");
        emitAddress(TEMP_LEFT_OPERAND);
        
        // position for branch fix
        int branchPos = getCurrentPosition();
        append(BNE, "BNE", "Execute loop body if condition is true");
        insertPlaceholder();
        
        // branch to end of loop when condition is false
        addBranchFix(whileEndLabel, branchPos);
        
        ASTNode blockNode = node.getChildren().get(1);
        generateCode(blockNode);
        
        // Always branch back to the start of the loop
        append(LDA_CONST, "LDA", "Load 1 to make sure we always loop back");
        emitImmediate(1);
       
        int jumpBranchPos = getCurrentPosition();
        append(BNE, "BNE", "Jump back to start of while loop");
        insertPlaceholder();
        
        // Add branch fix for the backward jump
        addBranchFix(whileStartLabel, jumpBranchPos);
        
        defineLabel(whileEndLabel);
        
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
    
    private void generateBooleanExpressionCode(ASTNode node) {
        if (node.getChildren().size() < 2) {
            
            // Simple boolean value or identifier
            if (node.getValue() != null) {
                String boolValue = node.getValue();
                
                // true or false
                if (boolValue.equals("true")) {
                    append(LDA_CONST, "LDA", "Load 1 (true)");
                    emitImmediate(1);
                } else {
                    append(LDA_CONST, "LDA", "Load 0 (false)");
                    emitImmediate(0);
                }
                return;
            }
            // if no children or value it's a simple expression
            if (node.getChildren().size() > 0) {
                generateCode(node.getChildren().get(0));
            }
            return;
        }
        
        // Get the left and right sides of the comparison
        ASTNode leftNode = node.getChildren().get(0);
        ASTNode rightNode = node.getChildren().get(1);
        
        generateCode(leftNode);
        
        append(STA, "STA", "Store left operand");
        emitAddress(TEMP_LEFT_OPERAND);
        
        append(LDX_MEM, "LDX", "Load X with left operand");
        emitAddress(TEMP_LEFT_OPERAND);
        
        generateCode(rightNode);
        
        append(STA, "STA", "Store right operand to temporary memory");
        emitAddress(TEMP_RIGHT_OPERAND);
        
        append(CPX, "CPX", "Compare left operand with right operand");
        emitAddress(TEMP_RIGHT_OPERAND);
        
        String doneLabel = generateLabel("bool_expr_done");
        
        append(LDA_CONST, "LDA", "Load 0 (false) into accumulator by default");
        emitImmediate(0);
        
        int branchPos = getCurrentPosition();
        append(BNE, "BNE", "Branch to " + doneLabel + " if not equal (Z=0)");
        
        insertPlaceholder();
        addBranchFix(doneLabel, branchPos);
        
        append(LDA_CONST, "LDA", "Load 1 (true) into accumulator for equality");
        emitImmediate(1);
        
        defineLabel(doneLabel);
        appendComment("End of boolean expression");
    }
    
    // creates a two byte little endian address
    private void emitAddress(int addr) {
        machineCode.append(String.format("   %02X %02X\n", addr & 0xFF, (addr >> 8) & 0xFF));
    }

    // creates a one byte immediate value
    private void emitImmediate(int value) {
        machineCode.append(String.format("   %02X\n", value & 0xFF));
    }

    private int getCurrentPosition() {
        // count the lines in the machineCode that contain actual code 
        String[] lines = machineCode.toString().split("\n");
        int codeLines = 0;
        for (String line : lines) {
            if (!line.trim().startsWith(";") && !line.trim().isEmpty()) {
                codeLines++;
            }
        }
        return codeLines;
    }
    
    private void defineLabel(String label) {
        int position = getCurrentPosition();
        labelPositions.put(label, position);
        appendComment("LABEL: " + label);
        
        if (verboseMode) {
            System.out.println("CODE GENERATOR: Defined label '" + label + "' at position " + position);
        }
    }
    
    private void addBranchFix(String targetLabel, int position) {
        branchFix.add(new BranchFix(targetLabel, position));
        
        if (verboseMode) {
            System.out.println("CODE GENERATOR: Added branch fix for label '" + targetLabel + 
                              "' at position " + position);
        }
    }
    
    private void insertPlaceholder() {
        machineCode.append("   XX\n");
    }
    
    private void resolveBranchOffsets() {
       
        String[] lines = machineCode.toString().split("\n");
        
        for (BranchFix fixup : branchFix) {
            String targetLabel = fixup.targetLabel;
            int branchPosition = fixup.instructionPosition;
            
            Integer targetPosition = labelPositions.get(targetLabel);
            if (targetPosition == null) {
                if (verboseMode) {
                    System.out.println("ERROR: Could not find target label: " + targetLabel);
                }
                continue;
            }
            
            int branchOffset = targetPosition - (branchPosition + 2); 
            
            if (verboseMode) {
                System.out.println("CODE GENERATOR: Branch from position " + branchPosition + 
                                  " to label " + targetLabel + " at position " + targetPosition + 
                                  " (offset = " + branchOffset + ")");
            }
            
            if (branchOffset < -128 || branchOffset > 127) {
                appendComment("ERROR: Branch offset too large: " + branchOffset);
                if (verboseMode) {
                    System.out.println("ERROR: Branch offset too large: " + branchOffset);
                }
                continue;
            }
            
            int offsetByte = branchOffset < 0 ? 256 + branchOffset : branchOffset;
            
            String fixedLine = String.format("   %02X", offsetByte & 0xFF);
            
            // finds the next placeholder line and replace it
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].trim().equals("XX")) {
                    lines[i] = fixedLine;
                    break;
                }
            }
        }
        
        machineCode.setLength(0);
        for (String line : lines) {
            machineCode.append(line).append("\n");
        }
    }
} 