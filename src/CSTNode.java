import java.util.ArrayList;
import java.util.List;

// Node class for the Concrete Syntax Tree
public class CSTNode {
    private String name;
    private Token token;
    private List<CSTNode> children;
    
    // Constructor for non terminals
    public CSTNode(String name) {
        this.name = name;
        this.token = null;
        this.children = new ArrayList<>();
    }
    
    // For terminals
    public CSTNode(String name, Token token) {
        this.name = name;
        this.token = token;
        this.children = new ArrayList<>();
    }
    
    public void addChild(CSTNode child) {
        children.add(child);
    }
    
    public List<CSTNode> getChildren() {
        return children;
    }
    
    public String getName() {
        return name;
    }
    
    public Token getToken() {
        return token;
    }
    
    // Terminal nodes have tokens
    public boolean isTerminal() {
        return token != null;
    }
    
    // show the CST from this node down
    public void display() {
        display(0);
    }
   
    private void display(int level) {
        StringBuilder indent = new StringBuilder();
        
        for (int i = 0; i < level; i++) {
            indent.append("-");
        }
        
        if (isTerminal()) {
            
            System.out.println(indent + "[" + name + "]");
        } else {
           
            System.out.println(indent + "<" + name + ">");
            
            for (CSTNode child : children) {
                child.display(level + 1);
            }
        }
    }
} 