public class Compiler {
    private Lexer lexer;
    private Parser parser;
    private boolean verboseMode = true;  //always true for debugging
    private String programText; 

   
    public static void main(String[] args) {
        if (args.length == 0) {
            System.exit(1);
        }
        
        Compiler compiler = new Compiler();

        compiler.compile(args[0]);
    }
    
    public void compile(String fileName) {
        try {
            // Read the programText file
            programText = readFile(fileName);
            
            // Creates the lexer
            lexer = new Lexer(programText);
            
            // Process tokens
            processTokenStream();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    // Process tokens and handles multiple programs seperated by $
    private void processTokenStream() {
        Token currentToken;
        int programCount = 0;
        boolean foundEOP = false; // EOP = End of Program aka ($)
        int errorCount = 0;
        int warningCount = 0;
        
        System.out.println("INFO  Lexer - Lexing program " + (programCount + 1) + "...");
        
        do {
            currentToken = lexer.nextToken();
            
            if (verboseMode) {
                System.out.println("DEBUG Lexer - " + currentToken);
            }
            
            // Count all errors and warnings for stats
            if (currentToken.getType() == Token.Type.ERROR) {
                errorCount++;
                // when an error is hit skip to the next token
                continue;

            } else if (currentToken.getType() == Token.Type.WARNING) {
                warningCount++; // skip to next token after warning
            }
            
            // Check if the current token is the end of a program
            if (currentToken.getType() == Token.Type.EOP) {
                foundEOP = true;
                System.out.println("INFO  Lexer - The Lexer finished with " + errorCount + " errors and " + warningCount + " warnings");
                
                // If there are no errors begin parsing
                if (errorCount == 0) {
                    // Create a new lexer with the same program text
                    Lexer parserLexer = new Lexer(programText);
                    
                    // Parse
                    System.out.println("PARSER: Parsing program " + (programCount + 1) + "...");
                    parser = new Parser(parserLexer);
                    parser.setVerboseMode(verboseMode);
                    
                    // Get the CST
                    CSTNode cstRoot = parser.parse();
                    
                    // If parsing was successful display the CST
                    if (parser.getErrorCount() == 0) {
                        System.out.println("CST for program " + (programCount + 1) + "...");
                        cstRoot.display();
                    } else {
                        System.out.println("CST for program " + (programCount + 1) + ": Skipped due to PARSER error(s).");
                    }
                } else {
                    System.out.println("PARSER: Skipped due to LEXER error(s)");
                    System.out.println("CST for program " + (programCount + 1) + ": Skipped due to LEXER error(s).");
                }
                
                programCount++; 
                Token nextToken = lexer.nextToken();

                if (nextToken.getType() != Token.Type.EOF) {  // there are more programs
                    System.out.println("INFO  Lexer - Lexing program " + (programCount + 1) + "...");
                }
                currentToken = nextToken; 

            } else if (currentToken.getType() != Token.Type.EOF) {
                foundEOP = false;
            }
            
        } while (currentToken.getType() != Token.Type.EOF || currentToken.getLexeme().startsWith("Error"));
        
        // Check for missing $ at end of program
        if (!foundEOP && currentToken.getType() == Token.Type.EOF) {
            System.out.println("WARNING: Missing $ at end of program " + (programCount + 1));
            warningCount++;
            programCount++;
            System.out.println("INFO  Lexer - The Lexer finished with " + errorCount + " errors and " + warningCount + " warnings");
        }
        
        // final stats
        System.out.println("\nFinal Stats!:");
        System.out.println("Total Programs: " + programCount);
        System.out.println("Total Errors: " + errorCount);
        System.out.println("Total Warnings: " + warningCount);
    }
    
    // Reads the programText and returns it's contents as a string
    private String readFile(String fileName) throws Exception {
        try {
            return new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get(fileName)));
        } catch (Exception e) {
            throw new Exception("Could not read programText file: " + fileName);
        }
    }
    
}
