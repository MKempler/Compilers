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
        
        return blockASTNode;
    }
} 