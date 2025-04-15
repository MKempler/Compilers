public class ASTBuilder {
    private boolean verboseMode = true;
    
    public ASTBuilder() {
    }
    
    public ASTBuilder(boolean verboseMode) {
        this.verboseMode = verboseMode;
    }
    
    public ASTNode buildAST(CSTNode cstRoot) {
        if (verboseMode) {
            System.out.println("AST Builder: Building AST from CST");
        }
        
        return convertProgram(cstRoot);
    }

    private ASTNode convertProgram(CSTNode programNode) {
        if (verboseMode) {
            System.out.println("AST Builder: Converting Program node");
        }
        
        CSTNode blockNode = programNode.getChildren().get(0);
        return convertBlock(blockNode);
    }

    private ASTNode convertBlock(CSTNode blockNode) {
        if (verboseMode) {
            System.out.println("AST Builder: Converting Block node");
        }
        
        ASTNode blockASTNode = new ASTNode("BLOCK", 
                                          blockNode.getToken() != null ? blockNode.getToken().getLine() : 0,
                                          blockNode.getToken() != null ? blockNode.getToken().getColumn() : 0);
        
        if (blockNode.getChildren().size() > 1) {
            CSTNode statementListNode = blockNode.getChildren().get(1);
            convertStatementList(statementListNode, blockASTNode);
        }
        
        return blockASTNode;
    }

    private void convertStatementList(CSTNode statementListNode, ASTNode parentNode) {
        if (verboseMode) {
            System.out.println("AST Builder: Converting Statement List");
        }
        
        for (CSTNode child : statementListNode.getChildren()) {
            if (child.getName().equals("Statement")) {
                convertStatement(child, parentNode);
            }
        }
    }

    private void convertStatement(CSTNode statementNode, ASTNode parentNode) {
        if (verboseMode) {
            System.out.println("AST Builder: Converting Statement");
        }
        
        if (!statementNode.getChildren().isEmpty()) {
            CSTNode statementChild = statementNode.getChildren().get(0);
            String childName = statementChild.getName();
            
            if (verboseMode) {
                System.out.println("AST Builder: Statement type is " + childName);
            }
            
            // Maps statement types to their converters
            if (childName.equals("Print Statement")) {
                convertPrintStatement(statementChild, parentNode);
            } 
            else if (childName.equals("Variable Declaration")) {
                convertVarDecl(statementChild, parentNode);
            } 
            else if (childName.equals("Assignment Statement")) {
                convertAssignmentStatement(statementChild, parentNode);
            } 
            else if (childName.equals("If Statement")) {
                convertIfStatement(statementChild, parentNode);
            } 
            else if (childName.equals("While Statement")) {
                convertWhileStatement(statementChild, parentNode);
            } 
            else if (childName.equals("Block")) {
                ASTNode blockNode = convertBlock(statementChild);
                parentNode.addChild(blockNode);
            }
        }
    }

    private void convertPrintStatement(CSTNode printNode, ASTNode parentNode) {
        if (verboseMode) {
            System.out.println("AST Builder: Converting Print Statement");
        }
        
        int line = 0;
        int column = 0;
        
        if (printNode.getChildren().size() >= 1) {
            Token printToken = printNode.getChildren().get(0).getToken();
            
            if (printToken != null) {
                line = printToken.getLine();
                column = printToken.getColumn();
            }
        }
        
        ASTNode printASTNode = new ASTNode("Print_Statement", line, column);
        parentNode.addChild(printASTNode);
        
        if (printNode.getChildren().size() >= 3) {
            CSTNode exprNode = printNode.getChildren().get(2);
            convertExpression(exprNode, printASTNode);
        }
    }

    private void convertVarDecl(CSTNode varDeclNode, ASTNode parentNode) {
        if (verboseMode) {
            System.out.println("AST Builder: Converting Variable Declaration");
        }
        
        
        int line = 0;
        int column = 0;
        
        if (!varDeclNode.getChildren().isEmpty()) {
            Token typeToken = varDeclNode.getChildren().get(0).getToken();
            
            if (typeToken != null) {
                line = typeToken.getLine();
                column = typeToken.getColumn();
            }
        }
        
        ASTNode varDeclASTNode = new ASTNode("Variable_Declaration", line, column);
        parentNode.addChild(varDeclASTNode);
        
        if (varDeclNode.getChildren().size() >= 2) {
            
            CSTNode typeNode = varDeclNode.getChildren().get(0);
            String type = typeNode.getToken().getLexeme();
            
            ASTNode typeASTNode = new ASTNode("Type", type, 
                                             typeNode.getToken().getLine(), 
                                             typeNode.getToken().getColumn());
            varDeclASTNode.addChild(typeASTNode);
            
            
            CSTNode idNode = varDeclNode.getChildren().get(1);
            if (idNode.getName().equals("Identifier")) {
                
                if (!idNode.getChildren().isEmpty()) {
                    CSTNode idChildNode = idNode.getChildren().get(0);
                    String id = idChildNode.getToken().getLexeme();
                    
                    ASTNode idASTNode = new ASTNode("Identifier", id, 
                                                idChildNode.getToken().getLine(), 
                                                   idChildNode.getToken().getColumn());
                    varDeclASTNode.addChild(idASTNode);
                }
            }
        }
    }

    private void convertAssignmentStatement(CSTNode assignNode, ASTNode parentNode) {
        if (verboseMode) {
            System.out.println("AST Builder: Converting Assignment Statement");
        }
        
        int line = 0;
        int column = 0;
        
        if (!assignNode.getChildren().isEmpty()) {
            CSTNode firstChild = assignNode.getChildren().get(0);
            if (firstChild.getToken() != null) {
                line = firstChild.getToken().getLine();
                column = firstChild.getToken().getColumn();
            }
        }
        
        ASTNode assignASTNode = new ASTNode("Assignment_Statement", line, column);
        parentNode.addChild(assignASTNode);
        
        if (assignNode.getChildren().size() >= 3) {

            CSTNode idNode = assignNode.getChildren().get(0);
            
            if (idNode.getName().equals("Identifier")) {
                if (!idNode.getChildren().isEmpty()) {
                    CSTNode idChildNode = idNode.getChildren().get(0);
                    String id = idChildNode.getToken().getLexeme();
                    
                    ASTNode idASTNode = new ASTNode("Identifier", id, 
                                                   idChildNode.getToken().getLine(), 
                                                   idChildNode.getToken().getColumn());
                    assignASTNode.addChild(idASTNode);
                }
            }
        
            CSTNode exprNode = assignNode.getChildren().get(2);
            convertExpression(exprNode, assignASTNode);
        }
    }

    private void convertIfStatement(CSTNode ifNode, ASTNode parentNode) {
        if (verboseMode) {
            System.out.println("AST Builder: Converting If Statement");
        }
        
        int line = 0;
        int column = 0;
        
        if (!ifNode.getChildren().isEmpty()) {
            Token ifToken = ifNode.getChildren().get(0).getToken();
            if (ifToken != null) {
                line = ifToken.getLine();
                column = ifToken.getColumn();
            }
        }
        
        ASTNode ifASTNode = new ASTNode("If_Statement", line, column);
        parentNode.addChild(ifASTNode);
        
        if (ifNode.getChildren().size() >= 3) {
            CSTNode boolExprNode = ifNode.getChildren().get(1);
            convertBooleanExpression(boolExprNode, ifASTNode);
            
            CSTNode blockNode = ifNode.getChildren().get(2);
            ASTNode blockASTNode = convertBlock(blockNode);
            ifASTNode.addChild(blockASTNode);
        }
    }

    private void convertWhileStatement(CSTNode whileNode, ASTNode parentNode) {
        if (verboseMode) {
            System.out.println("AST Builder: Converting While Statement");
        }
        
        int line = 0;
        int column = 0;
        
        if (!whileNode.getChildren().isEmpty()) {
            Token whileToken = whileNode.getChildren().get(0).getToken();
            if (whileToken != null) {
                line = whileToken.getLine();
                column = whileToken.getColumn();
            }
        }
        
        ASTNode whileASTNode = new ASTNode("While_Statement", line, column);
        parentNode.addChild(whileASTNode);
        
        if (whileNode.getChildren().size() >= 3) {
            CSTNode boolExprNode = whileNode.getChildren().get(1);
            convertBooleanExpression(boolExprNode, whileASTNode);
            
            CSTNode blockNode = whileNode.getChildren().get(2);
            ASTNode blockASTNode = convertBlock(blockNode);
            whileASTNode.addChild(blockASTNode);
        }
    }

    // Handles boolean literals and comparison expressions
    private void convertBooleanExpression(CSTNode boolExprNode, ASTNode parentNode) {
        if (verboseMode) {
            System.out.println("AST Builder: Converting Boolean Expression");
        }
        
        ASTNode boolExprASTNode = new ASTNode("Boolean_Expression", 
                                             boolExprNode.getToken() != null ? boolExprNode.getToken().getLine() : 0,
                                             boolExprNode.getToken() != null ? boolExprNode.getToken().getColumn() : 0);
        parentNode.addChild(boolExprASTNode);
        
      
        if (boolExprNode.getChildren().size() >= 5) {
            // Get left expression
            CSTNode leftExprNode = boolExprNode.getChildren().get(1);
            convertExpression(leftExprNode, boolExprASTNode);
            
            // Get right expression
            CSTNode rightExprNode = boolExprNode.getChildren().get(3);
            convertExpression(rightExprNode, boolExprASTNode);
        } else if (boolExprNode.getToken() != null) {
            
            String value = boolExprNode.getToken().getLexeme();
            ASTNode valueNode = new ASTNode("Value", value, 
                                           boolExprNode.getToken().getLine(), 
                                           boolExprNode.getToken().getColumn());
            boolExprASTNode.addChild(valueNode);
        }
    }

    // processes identifiers, literals, and complex expressions
    private void convertExpression(CSTNode exprNode, ASTNode parentNode) {
        if (verboseMode) {
            System.out.println("AST Builder: Converting Expression");
        }
        
        if (!exprNode.getChildren().isEmpty()) {
            CSTNode exprChild = exprNode.getChildren().get(0);
            
            if (exprChild.isTerminal()) {
                String value = exprChild.getToken().getLexeme();
                int line = exprChild.getToken().getLine();
                int column = exprChild.getToken().getColumn();
                
                ASTNode valueNode = new ASTNode("Value", value, line, column);
                parentNode.addChild(valueNode);
            
            } else if (exprChild.getName().equals("Identifier")) {
                //  handling for identifiers
                if (!exprChild.getChildren().isEmpty()) {
                    CSTNode idChildNode = exprChild.getChildren().get(0);
                    String id = idChildNode.getToken().getLexeme();
                    
                    ASTNode idASTNode = new ASTNode("Identifier", id, 
                                                  idChildNode.getToken().getLine(), 
                                                  idChildNode.getToken().getColumn());
                    parentNode.addChild(idASTNode);
                }
            } else {
                ASTNode valueNode = new ASTNode("Expression", 
                                               exprChild.getToken() != null ? exprChild.getToken().getLine() : 0,
                                               exprChild.getToken() != null ? exprChild.getToken().getColumn() : 0);
                parentNode.addChild(valueNode);
                
                // recursively convert the child expression
                if (exprChild.getName().equals("Integer Expression") || 
                    exprChild.getName().equals("String Expression") ||
                    exprChild.getName().equals("Boolean Expression")) {
                    convertExpression(exprChild, valueNode);
                }
            }
        }
    }
} 