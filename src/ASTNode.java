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
    
    public List<ASTNode> getChildren() {
        return children;
    }
} 