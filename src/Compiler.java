public class Compiler {
    private Lexer lexer;
    private Parser parser;
    private ASTBuilder astBuilder;
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
        int programStartPos = 0;
        int totalLexerErrors = 0;
        int totalLexerWarnings = 0;
        int totalParserErrors = 0;
        int totalSemanticErrors = 0;
        int totalSemanticWarnings = 0;
        
        System.out.println("INFO  Lexer - Lexing program " + (programCount + 1) + "...");
        
        do {
            currentToken = lexer.nextToken();
            
            if (verboseMode) {
                System.out.println("DEBUG Lexer - " + currentToken);
            }
            
            // Count all errors and warnings for stats
            if (currentToken.getType() == Token.Type.ERROR) {
                errorCount++;
                totalLexerErrors++;
                // when an error is hit skip to the next token
                continue;

            } else if (currentToken.getType() == Token.Type.WARNING) {
                warningCount++; // skip to next token after warning
                totalLexerWarnings++;
            }
            
            // Check if the current token is the end of a program
            if (currentToken.getType() == Token.Type.EOP) {
                foundEOP = true;
                System.out.println("INFO  Lexer - The Lexer finished with " + errorCount + " errors and " + warningCount + " warnings");
                
                // If there are no errors begin parsing
                if (errorCount == 0) {
                    // just this program's text
                    int currentPos = programText.indexOf("$", programStartPos) + 1;
                    String currentProgramText = programText.substring(programStartPos, currentPos);
                    
                    // Create a new lexer with just this program's text
                    Lexer parserLexer = new Lexer(currentProgramText);
                    
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
                        
                        // Build AST from the CST
                        System.out.println("Building AST for program " + (programCount + 1) + "...");
                        astBuilder = new ASTBuilder(verboseMode);
                        ASTNode astRoot = astBuilder.buildAST(cstRoot);
                        
                        // Display the AST
                        System.out.println("AST for program " + (programCount + 1) + "...");
                        astRoot.display();
                        
                        // semantic analysis
                        System.out.println("Performing semantic analysis for program " + (programCount + 1) + "...");
                        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(astRoot, verboseMode);
                        semanticAnalyzer.analyze();
                        
                        totalSemanticErrors += semanticAnalyzer.getErrorCount();
                        totalSemanticWarnings += semanticAnalyzer.getWarningCount();
                        
                        // Generate code 
                        if (semanticAnalyzer.getErrorCount() == 0) {
                            System.out.println("Generating code for program " + (programCount + 1) + "...");
                            CodeGenerator codeGenerator = new CodeGenerator(astRoot, semanticAnalyzer.getSymbolTable(), verboseMode);
                            String machineCode = codeGenerator.generate();
                            
                            // Extract only the hex opcodes
                            StringBuilder rawOpcodes = new StringBuilder();
                            for (String line : machineCode.split("\n")) {
                                if (line.trim().startsWith(";") || line.trim().isEmpty()) continue;

                                // operand line
                                if (line.startsWith(" ")) {
                                    String trimmed = line.trim();
                                    for (String b : trimmed.split("\\s+")) {
                                        if (b.matches("[A-F0-9]{2}")) rawOpcodes.append(b);
                                    }
                                }
                                // opcode line 
                                else {
                                    String trimmed = line.trim();
                                    if (trimmed.length() >= 2 && trimmed.substring(0, 2).matches("[A-F0-9]{2}")) {
                                        rawOpcodes.append(trimmed.substring(0, 2));
                                    }
                                }
                            }
                            
                            
                            // Formats everything in rows of 8 bytes 
                            String opcodes = rawOpcodes.toString();
                            int totalBytes = opcodes.length() / 2;

                            // Create our 256 byte memory image 
                            byte[] memoryImage = new byte[256];
                            
                            // Fills the memory image with the generated opcodes
                            int codeLimit = Math.min(totalBytes, 0x0080);
                            for (int i = 0; i < codeLimit; i++) {
                                if (i * 2 + 2 <= opcodes.length()) {
                                    String byteStr = opcodes.substring(i * 2, i * 2 + 2);
                                    memoryImage[i] = (byte) Integer.parseInt(byteStr, 16);
                                }
                            }
                            
                            System.out.println("6502a Machine Code - 256-byte Memory Image:");
                            System.out.println("--------------------------------");
                            
                            // display the memory image
                            for (int i = 0; i < 256; i += 8) {
                                StringBuilder row = new StringBuilder();
                                for (int j = 0; j < 8 && i + j < 256; j++) {
                                    String byteStr = String.format("%02X", memoryImage[i + j] & 0xFF);
                                    row.append(byteStr);
                                    
                                    // space between bytes
                                    if (j < 7 && i + j + 1 < 256) {
                                        row.append(" ");
                                    }
                                }
                                System.out.println(row.toString());
                            }
                            
                            System.out.println("--------------------------------");
                            System.out.println("Total generated bytes: " + totalBytes);
                            System.out.println("Total memory size: 256 bytes");
                        } else {
                            System.out.println("Code generation for program " + (programCount + 1) + ": Skipped due to SEMANTIC error(s).");
                        }
                    } else {
                        System.out.println("CST for program " + (programCount + 1) + ": Skipped due to PARSER error(s).");
                        System.out.println("AST for program " + (programCount + 1) + ": Skipped due to PARSER error(s).");
                        System.out.println("Semantic analysis for program " + (programCount + 1) + ": Skipped due to PARSER error(s).");
                        totalParserErrors += parser.getErrorCount();
                    }
                } else {
                    System.out.println("PARSER: Skipped due to LEXER error(s)");
                    System.out.println("CST for program " + (programCount + 1) + ": Skipped due to LEXER error(s).");
                    System.out.println("AST for program " + (programCount + 1) + ": Skipped due to LEXER error(s).");
                    System.out.println("Semantic analysis for program " + (programCount + 1) + ": Skipped due to LEXER error(s).");
                }
                
                programCount++; 
                errorCount = 0;  // Reset the error count for next program
                warningCount = 0;  // Reset warning count for next program
                
                // Update start pos
                programStartPos = programText.indexOf("$", programStartPos) + 1;
                
                // Check reached end of input
                Token nextToken = lexer.nextToken();
                if (nextToken.getType() != Token.Type.EOF) {  // there are more programs
                    System.out.println("INFO  Lexer - Lexing program " + (programCount + 1) + "...");
                }
                currentToken = nextToken;

            } else if (currentToken.getType() != Token.Type.EOF) {
                foundEOP = false;
            }
            
        } while (currentToken.getType() != Token.Type.EOF);
        
        // Check for missing $ at end of program
        if (!foundEOP && currentToken.getType() == Token.Type.EOF) {
            System.out.println("WARNING: Missing $ at end of program " + (programCount + 1));
            warningCount++;
            totalLexerWarnings++;
            programCount++;
            System.out.println("INFO  Lexer - The Lexer finished with " + errorCount + " errors and " + warningCount + " warnings");
        }
        
        // final stats
        System.out.println("\nFinal Stats!:");
        System.out.println("Total Programs: " + programCount);
        System.out.println("Total Lexer Errors: " + totalLexerErrors);
        System.out.println("Total Lexer Warnings: " + totalLexerWarnings);
        System.out.println("Total Parser Errors: " + totalParserErrors);
        System.out.println("Total Semantic Errors: " + totalSemanticErrors);
        System.out.println("Total Semantic Warnings: " + totalSemanticWarnings);
        System.out.println("Total Errors: " + (totalLexerErrors + totalParserErrors + totalSemanticErrors));
        System.out.println("Total Warnings: " + (totalLexerWarnings + totalSemanticWarnings));
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
