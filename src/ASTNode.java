import java.util.ArrayList;
import java.util.List;

public class ASTNode {
    private String type;
    private String value;
    private int line;
    private int column;
    private List<ASTNode> children;
    
    public ASTNode(String type, int line, int column) {
        this.type = type;
        this.value = null;
        this.line = line;
        this.column = column;
        this.children = new ArrayList<>();
    }
    
    public ASTNode(String type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
        this.children = new ArrayList<>();
    }
    
    public void addChild(ASTNode child) {
        children.add(child);
    }
    
    public String getType() {
        return type;
    }
    
    public String getValue() {
        return value;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    public List<ASTNode> getChildren() {
        return children;
    }
    
    // displays the AST from this node down
    public void display() {
        display(0);
    }
    
    private void display(int level) {
        StringBuilder indent = new StringBuilder();
        
        for (int i = 0; i < level; i++) {
            indent.append("--");
        }
        
        if (value != null) {
            System.out.println(indent + "[ " + value + " ]");
        } else {
            System.out.println(indent + "< " + type + " >");
        }
        
        for (ASTNode child : children) {
            child.display(level + 1);
        }
    }
} 