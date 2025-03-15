import java.util.ArrayList;
import java.util.List;


public class CSTNode {
    private String name;
    private Token token;
    private List<CSTNode> children;
    
   
      
     
    public CSTNode(String name) {
        this.name = name;
        this.token = null;
        this.children = new ArrayList<>();
    }
    
 
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
    
   
    public boolean isTerminal() {
        return token != null;
    }
    

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