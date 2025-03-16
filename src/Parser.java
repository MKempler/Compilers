public class Parser {
    private Lexer lexer;
    private Token currentToken;
    private boolean verboseMode = true;
    private int errorCount = 0;
    
   
    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.nextToken();
    }
    
    // parse the program and return the CST
    public CSTNode parse() {
        if (verboseMode) {
            System.out.println("PARSER: parse()");
        }
        
        // Start parsing from the Program rule
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
    
    private CSTNode parseStatement() {
        if (verboseMode) {
            System.out.println("PARSER: parseStatement()");
        }
        
        CSTNode statementNode = new CSTNode("Statement");
        
        //handle blocks
        if (match(Token.Type.OPEN_BLOCK)) {
            // Block statement
            CSTNode blockNode = parseBlock();
            statementNode.addChild(blockNode);

        } else if (match(Token.Type.PRINT)) {
            // Print statement
            CSTNode printNode = parsePrintStatement();
            statementNode.addChild(printNode);

        } else if (match(Token.Type.I_TYPE)) {
            // Variable declaration
            CSTNode varDeclNode = parseVarDecl();
            statementNode.addChild(varDeclNode);

        } else if (match(Token.Type.ID)) {
            // Assignment statement
            CSTNode assignmentNode = parseAssignmentStatement();
            statementNode.addChild(assignmentNode);

        } else if (match(Token.Type.IF)) {
            // If statement
            CSTNode ifNode = parseIfStatement();
            statementNode.addChild(ifNode);

        } else if (match(Token.Type.WHILE)) {
            // While statement
            CSTNode whileNode = parseWhileStatement();
            statementNode.addChild(whileNode);

        } else {
            reportError("Expected a statement");
        }
        
        return statementNode;
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
        
         // checks if the current token could start a statement
        if (match(Token.Type.OPEN_BLOCK) || match(Token.Type.PRINT) || 
            match(Token.Type.I_TYPE) || match(Token.Type.ID) || 
            match(Token.Type.IF) || match(Token.Type.WHILE)) {
            // Parse statement
            CSTNode statementNode = parseStatement();
            statementListNode.addChild(statementNode);
            
            CSTNode restOfStatements = parseStatementList();
            
            // Add children from the rest of the statements
            for (CSTNode child : restOfStatements.getChildren()) {
                statementListNode.addChild(child);
            }
        }
        
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
    
    private CSTNode parsePrintStatement() {
        if (verboseMode) {
            System.out.println("PARSER: parsePrintStatement()");
        }
        
        // print statement
        CSTNode printNode = new CSTNode("Print Statement");
        
        if (match(Token.Type.PRINT)) {
            printNode.addChild(new CSTNode("print", currentToken));
            nextToken();
            
            //  opening parenthesis
            if (match(Token.Type.LPAREN)) {
                printNode.addChild(new CSTNode("(", currentToken));
                nextToken();
                
                // Parse expression inside the parentheses
                CSTNode exprNode = parseExpr();
                printNode.addChild(exprNode);
                
                // closing parenthesis
                if (match(Token.Type.RPAREN)) {
                    printNode.addChild(new CSTNode(")", currentToken));
                    nextToken();
                } else {
                    reportError("Expected closing parenthesis ')'");
                }
            } else {
                reportError("Expected opening parenthesis '('");
            }
        } else {
            reportError("Expected 'print' keyword");
        }
        
        return printNode;
    }
    
    // Expr 
    private CSTNode parseExpr() {
        if (verboseMode) {
            System.out.println("PARSER: parseExpr()");
        }
        
        CSTNode exprNode = new CSTNode("Expression");
        
        // Check what type of expression we have
        if (match(Token.Type.ID)) {
            // Identifier
            CSTNode idNode = parseId();
            exprNode.addChild(idNode);
        
        } else if (match(Token.Type.NUMBER)) {
            // Int
            CSTNode intExprNode = parseIntExpr();
            exprNode.addChild(intExprNode);
       
        } else if (match(Token.Type.QUOTE)) {
            // String
            CSTNode stringExprNode = parseStringExpr();
            exprNode.addChild(stringExprNode);
        
        } else if (match(Token.Type.BOOLVAL)) {
            // Boolean literal 
            CSTNode booleanExprNode = parseBooleanExpr();
            exprNode.addChild(booleanExprNode);
        
        } else if (match(Token.Type.LPAREN)) {
            // boolean expression
            CSTNode booleanExprNode = parseParenBooleanExpr();
            exprNode.addChild(booleanExprNode);
        
        } else {
           
        }
        
        return exprNode;
    }
    
    // IntExpr
    private CSTNode parseIntExpr() {
        if (verboseMode) {
            System.out.println("PARSER: parseIntExpr()");
        }
        
        CSTNode intExprNode = new CSTNode("Integer Expression");
        
        // Handle a digit
        if (match(Token.Type.NUMBER)) {
            intExprNode.addChild(new CSTNode(currentToken.getLexeme(), currentToken));
            nextToken();
            
            // Check if there's a + operator
            if (match(Token.Type.PLUS)) {
                // Add the operator
                intExprNode.addChild(new CSTNode("+", currentToken));
                nextToken();
                
                // Parse the right side of the operation
                CSTNode rightExpr = parseExpr();
                intExprNode.addChild(rightExpr);
            }
        } else {
            reportError("Expected a number");
        }
        
        return intExprNode;
    }
    
    // char
    private CSTNode parseId() {
        if (verboseMode) {
            System.out.println("PARSER: parseId()");
        }
        
        CSTNode idNode = new CSTNode("Identifier");
        
        if (match(Token.Type.ID)) {
            idNode.addChild(new CSTNode(currentToken.getLexeme(), currentToken));
            nextToken();
        } else {
            reportError("Expected an identifier");
        }
        
        return idNode;
    }
    
    // String
    private CSTNode parseStringExpr() {
        if (verboseMode) {
            System.out.println("PARSER: parseStringExpr()");
        }
        
        CSTNode stringExprNode = new CSTNode("String Expression");
        
        // opening quote
        if (match(Token.Type.QUOTE)) {
            stringExprNode.addChild(new CSTNode("\"", currentToken));
            nextToken();
            
            CSTNode charListNode = new CSTNode("CharList");
            
            // Collect chars until closing quote
            while (!match(Token.Type.QUOTE) && !match(Token.Type.EOP)) {
                if (match(Token.Type.CHAR) || match(Token.Type.SPACE)) {
                    charListNode.addChild(new CSTNode(currentToken.getLexeme(), currentToken));
                    nextToken();
                } else {
                    reportError("Expected a character or space in string");
                    break;
                }
            }
            
            stringExprNode.addChild(charListNode);
            
            // Handle closing quote
            if (match(Token.Type.QUOTE)) {
                stringExprNode.addChild(new CSTNode("\"", currentToken));
                nextToken();
            } else {
                reportError("Expected closing quote");
            }
        } else {
            reportError("Expected opening quote");
        }
        
        return stringExprNode;
    }
    
    // Booleans
    private CSTNode parseBooleanExpr() {
        if (verboseMode) {
            System.out.println("PARSER: parseBooleanExpr()");
        }
        
        CSTNode booleanExprNode = new CSTNode("Boolean Expression");
        
        if (match(Token.Type.BOOLVAL)) {
            booleanExprNode.addChild(new CSTNode(currentToken.getLexeme(), currentToken));
            nextToken();
        } else {
            reportError("Expected a boolean value (true or false)");
        }
        
        return booleanExprNode;
    }
    
    // BooleanExpr
    private CSTNode parseParenBooleanExpr() {
        if (verboseMode) {
            System.out.println("PARSER: parseParenBooleanExpr()");
        }
        
        CSTNode booleanExprNode = new CSTNode("Boolean Expression");
        
        // Handle opening parenthesis
        if (match(Token.Type.LPAREN)) {
            booleanExprNode.addChild(new CSTNode("(", currentToken));
            nextToken();
            
            // Parse left expression
            CSTNode leftExpr = parseExpr();
            booleanExprNode.addChild(leftExpr);
            
            // Parse  operator
            if (match(Token.Type.EQUALS) || match(Token.Type.NOT_EQUALS)) {
                String operator = currentToken.getType() == Token.Type.EQUALS ? "==" : "!=";
                booleanExprNode.addChild(new CSTNode(operator, currentToken));
                nextToken();
                
                // Parse right expression
                CSTNode rightExpr = parseExpr();
                booleanExprNode.addChild(rightExpr);
                
                // Handle closing parenthesis
                if (match(Token.Type.RPAREN)) {
                    booleanExprNode.addChild(new CSTNode(")", currentToken));
                    nextToken();

                } else {
                    reportError("Expected closing parenthesis ')'");
                }
                
            } else {
                reportError("Expected boolean operator (== or !=)");
            }
        } else {
            reportError("Expected opening parenthesis '('");
        }
        
        return booleanExprNode;
    }
    
    // Var
    private CSTNode parseVarDecl() {
        if (verboseMode) {
            System.out.println("PARSER: parseVarDecl()");
        }
        
        CSTNode varDeclNode = new CSTNode("Variable Declaration");
        
        // Handle type 
        if (match(Token.Type.I_TYPE)) {
            varDeclNode.addChild(new CSTNode(currentToken.getLexeme(), currentToken));
            nextToken();
            
            // Handle identifier
            CSTNode idNode = parseId();
            varDeclNode.addChild(idNode);
        } else {
            reportError("Expected a type (int, string, or boolean)");
        }
        
        return varDeclNode;
    }
    
    // AssignmentStatement
    private CSTNode parseAssignmentStatement() {
        if (verboseMode) {
            System.out.println("PARSER: parseAssignmentStatement()");
        }
        
        CSTNode assignmentNode = new CSTNode("Assignment Statement");
        
        // Handle identifier
        CSTNode idNode = parseId();
        assignmentNode.addChild(idNode);
        
        // Handle assignment operator
        if (match(Token.Type.ASSIGN_OP)) {
            assignmentNode.addChild(new CSTNode("=", currentToken));
            nextToken();
            
            // Handle expression
            CSTNode exprNode = parseExpr();
            assignmentNode.addChild(exprNode);
        } else {
            reportError("Expected assignment operator (=)");
        }
        
        return assignmentNode;
    }
    
    // If Statement
    private CSTNode parseIfStatement() {
        if (verboseMode) {
            System.out.println("PARSER: parseIfStatement()");
        }
        
        CSTNode ifNode = new CSTNode("If Statement");
        
        //if keyword
        if (match(Token.Type.IF)) {
            ifNode.addChild(new CSTNode("if", currentToken));
            nextToken();
        
            if (match(Token.Type.BOOLVAL)) {
                CSTNode boolExpr = parseBooleanExpr();
                ifNode.addChild(boolExpr);
            
            } else if (match(Token.Type.LPAREN)) {
                CSTNode boolExpr = parseParenBooleanExpr();
                ifNode.addChild(boolExpr);
            } else {
                reportError("Expected a boolean expression");
            }
            
            // Parse block
            CSTNode blockNode = parseBlock();
            ifNode.addChild(blockNode);
        } else {
            reportError("Expected 'if' keyword");
        }
        
        return ifNode;
    }
    
    // WhileStatement
    private CSTNode parseWhileStatement() {
        if (verboseMode) {
            System.out.println("PARSER: parseWhileStatement()");
        }
        
        CSTNode whileNode = new CSTNode("While Statement");
        
        // while
        if (match(Token.Type.WHILE)) {
            whileNode.addChild(new CSTNode("while", currentToken));
            nextToken();
            
            if (match(Token.Type.BOOLVAL)) {
                CSTNode boolExpr = parseBooleanExpr();
                whileNode.addChild(boolExpr);
                
            } else if (match(Token.Type.LPAREN)) {
                CSTNode boolExpr = parseParenBooleanExpr();
                whileNode.addChild(boolExpr);
            } else {
                reportError("Expected a boolean expression");
            }
            
            // Parse block
            CSTNode blockNode = parseBlock();
            whileNode.addChild(blockNode);
        } else {
            reportError("Expected 'while' keyword");
        }
        
        return whileNode;
    }
} 