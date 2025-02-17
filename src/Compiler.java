public class Compiler {
    private Lexer lexer;
    private boolean verboseMode = true;  

   
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
        boolean FinalTokenWasEOP = false;
        int errorCount = 0;
        int warningCount = 0;
        
        System.out.println("INFO  Lexer - Lexing program " + programCount + "...");
        
        do {
            token = lexer.nextToken();
            
            if (verboseMode) {
                System.out.println("DEBUG Lexer - " + token);
            }
            
            // Check for end of program marker
            if (token.getType() == Token.Type.EOP) {
                FinalTokenWasEOP = true;
                System.out.println("INFO  Lexer - The Lexer finished with " + errorCount + " errors and " + warningCount + " warnings");
            } else if (token.getType() != Token.Type.EOF) {
                FinalTokenWasEOP = false;
            }
            
            // Count all errors and warnings
            if (token.getType() == Token.Type.EOF && token.getLexeme().startsWith("Error")) {
                errorCount++;
                break;  // Stop if there are errors
            } else if (token.getType() == Token.Type.WARNING) {
                warningCount++;
                // but continue on warnings
            }
            
        } while (token.getType() != Token.Type.EOF);
        
        // Check for missing $ at end of program
        if (!FinalTokenWasEOP && token.getType() == Token.Type.EOF) {
            System.out.println("WARNING: Missing $ at end of program " + programCount);
            warningCount++;
            System.out.println("INFO  Lexer - The Lexer finished with " + errorCount + " errors and " + warningCount + " warnings");
        }
        
        // final stats
        System.out.println("\nFinal Stats! :");
        System.out.println("Total Programs: " + programCount);
        System.out.println("Total Errors: " + errorCount);
        System.out.println("Total Warnings: " + warningCount);
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
