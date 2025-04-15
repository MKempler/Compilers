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
        symbolTable.display();
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
        } else if (nodeType.equals("Identifier")) {
            analyzeIdentifier(node);
        } else if (nodeType.equals("Assignment_Statement")) {
            analyzeAssignmentStatement(node);
        } else if (nodeType.equals("Print_Statement")) {
            analyzePrintStatement(node);
        } else if (nodeType.equals("If_Statement")) {
            analyzeIfStatement(node);
        } else if (nodeType.equals("While_Statement")) {
            analyzeWhileStatement(node);
        } else if (nodeType.equals("BLOCK")) {
            analyzeBlock(node);
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
    
    private void analyzeIdentifier(ASTNode idNode) {
        String name = idNode.getValue();
        int line = idNode.getLine();
        int column = idNode.getColumn();
        
        Symbol symbol = symbolTable.lookup(name);
        if (symbol == null) {
            reportError("Variable '" + name + "' used before declaration", line, column);
        } else {
            
            symbol.setUsed(true);
        }
    }
    
    private void analyzeAssignmentStatement(ASTNode assignNode) {
        if (verboseMode) {
            System.out.println("SEMANTIC: Analyzing assignment statement");
        }
        
        if (assignNode.getChildren().size() >= 2) {
            ASTNode idNode = assignNode.getChildren().get(0);
            ASTNode exprNode = assignNode.getChildren().get(1);
            
            // checks if the variable is declared
            if (idNode.getType().equals("Identifier")) {
                String name = idNode.getValue();
                int line = idNode.getLine();
                int column = idNode.getColumn();
                
                Symbol symbol = symbolTable.lookup(name);
                
                if (symbol == null) {
                    reportError("Variable '" + name + "' used before declaration", line, column);
                } else {
                    symbol.setInitialized(true);
                    
                    // Type check 
                    String exprType = getExpressionType(exprNode);
                    
                    if (!exprType.equals("unknown") && !symbol.getType().equals(exprType)) {
                        reportError("Type mismatch: cannot assign " + exprType + 
                                  " to variable '" + name + "' of type " + symbol.getType(), 
                                  line, column);
                    }
                }
            }
        
            analyzeExpressionForVariables(exprNode);
        }
    }
    
    private String getExpressionType(ASTNode exprNode) {
        if (verboseMode) {
            System.out.println("SEMANTIC: Getting type of expression: " + exprNode.getType());
        }
        
        //  get the type from the symbol table
        if (exprNode.getType().equals("Identifier")) {
            String name = exprNode.getValue();
            Symbol symbol = symbolTable.lookup(name);
            if (symbol != null) {
                return symbol.getType();
            }
            return "unknown";
        }
        
        // determine the type based on the content
        if (exprNode.getType().equals("Value")) {
            String value = exprNode.getValue();
            
            //  boolean literals
            if (value.equals("true") || value.equals("false")) {
                return "boolean";
            }
            
            // Check for string literals
            if (value.startsWith("\"")) {
                return "string";
            }
            
            // numeric literals
            try {
                Integer.parseInt(value);
                return "int";
            } catch (NumberFormatException e) {
               
            }
        }
       
        for (ASTNode child : exprNode.getChildren()) {
            String childType = getExpressionType(child);
            if (!childType.equals("unknown")) {
                return childType;
            }
        }
        
        return "unknown";
    }
    
    private void analyzeExpressionForVariables(ASTNode exprNode) {
        if (verboseMode) {
            System.out.println("SEMANTIC: Analyzing expression for variables: " + exprNode.getType());
        }
        
        // If identifier check if it exists and mark
        if (exprNode.getType().equals("Identifier")) {
            analyzeIdentifier(exprNode);
        }
        
        // Recursively check all children 
        for (ASTNode child : exprNode.getChildren()) {
            analyzeExpressionForVariables(child);
        }
    }
    
    private void analyzePrintStatement(ASTNode printNode) {
        if (verboseMode) {
            System.out.println("SEMANTIC: Analyzing print statement");
        }
    
        for (ASTNode child : printNode.getChildren()) {
            
            analyzeExpressionForVariables(child);
        }
    }
    
    private void analyzeIfStatement(ASTNode ifNode) {
        if (verboseMode) {
            System.out.println("SEMANTIC: Analyzing if statement");
        }
        
        if (ifNode.getChildren().size() >= 2) {

            ASTNode conditionNode = ifNode.getChildren().get(0);
        
            analyzeExpressionForVariables(conditionNode);
            
            // Verify it's a boolean expression
            String conditionType = getExpressionType(conditionNode);
            if (!conditionType.equals("unknown") && !conditionType.equals("boolean")) {
                reportError("Condition in if statement must be a boolean expression, found " + 
                           conditionType, conditionNode.getLine(), conditionNode.getColumn());
            }
            
            for (int i = 1; i < ifNode.getChildren().size(); i++) {
                analyzeNode(ifNode.getChildren().get(i));
            }
        }
    }
    
    private void analyzeWhileStatement(ASTNode whileNode) {
        if (verboseMode) {
            System.out.println("SEMANTIC: Analyzing while statement");
        }
        
        if (whileNode.getChildren().size() >= 2) {
          
            ASTNode conditionNode = whileNode.getChildren().get(0);
            
            analyzeExpressionForVariables(conditionNode);
            
            String conditionType = getExpressionType(conditionNode);
            if (!conditionType.equals("unknown") && !conditionType.equals("boolean")) {
                reportError("Condition in while statement must be a boolean expression, found " + 
                           conditionType, conditionNode.getLine(), conditionNode.getColumn());
            }
            
            for (int i = 1; i < whileNode.getChildren().size(); i++) {
                analyzeNode(whileNode.getChildren().get(i));
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