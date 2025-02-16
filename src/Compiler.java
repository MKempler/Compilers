public class Compiler {
    private Lexer lexer;
    private boolean verboseMode = false;  

   
    public static void main(String[] args) {
        if (args.length == 0) {
            System.exit(1);
        }
        
        Compiler compiler = new Compiler();
        
        compiler.compile(args[0]);
    }
    
    public void compile(String fileName) {
        try {
            // Read the input file
            String input = readFile(fileName);
            
            // Create lexer
            lexer = new Lexer(input);
            
            // Process tokens
            processTokens();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    // Process tokens from the lexer
    private void processTokens() {
        Token token;
        int programCount = 1;
        
        System.out.println("INFO  Lexer - Lexing program " + programCount + "...");
        
        do {
            token = lexer.nextToken();
            
            if (verboseMode) {
                System.out.println("DEBUG Lexer - " + token);
            }
            
            // Check for end of program marker
            if (token.getType() == Token.Type.EOP) {
                System.out.println("INFO  Lexer - Lex completed with 0 errors");
                programCount++;
                System.out.println("INFO  Lexer - Lexing program " + programCount + "...");
            }
            
        } while (token.getType() != Token.Type.EOF);
    }
    
    // Read input file
    private String readFile(String fileName) throws Exception {
        try {
            return new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get(fileName)));
        } catch (Exception e) {
            throw new Exception("Could not read input file: " + fileName);
        }
    }
    
}
