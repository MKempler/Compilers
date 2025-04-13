public class ASTBuilder {
    private boolean verboseMode = true;
    
    public ASTBuilder() {
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
        }
    }
} 