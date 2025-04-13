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
            
            if (childName.equals("Print Statement")) {
                convertPrintStatement(statementChild, parentNode);
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
            
            } else {
                ASTNode valueNode = new ASTNode("Expression", 
                                               exprChild.getToken() != null ? exprChild.getToken().getLine() : 0,
                                               exprChild.getToken() != null ? exprChild.getToken().getColumn() : 0);
                parentNode.addChild(valueNode);
            }
        }
    }
} 