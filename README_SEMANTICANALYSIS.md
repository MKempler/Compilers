# Semantic Analyzer - Project 3 (Please grade branch named: "SemanticAnalysis")

## Setup:
1. cd src
2. Compile: javac *.java
3. Run tests: java Compiler ../test/<input_file>

## My test files:
- test1.txt: Variable declarations, scoping rules, and redeclaration tests
- test2.txt: Type checking for assignments and boolean comparisons
- test3.txt: Uninitialized variable detection and usage warnings
- test4.txt: Complex boolean expressions and conditional testing
- test5.txt: Nested control flow with if statements and while loops
- test6.txt: Boolean operations 
- test7.txt: Variable shadowing in nested scopes
- test8.txt: String to int type conversion 
- test9.txt: Unreachable code in while loops
- test10.txt: Empty blocks and edge case
- test11.txt: Error recovery after type errors

### All test cases passed

## Project 3 Reflection
The Semantic Analysis piece was difficult but interesting to build. This project really showed me how all of the projects are coming together. The most difficult part for me was just getting all the new files to work together, more than the other projects there were more working peices that all had to function together. I also struggled with the Boolean expression type checking for a while.

To me this was the hardest project so far, implemnting the AST and Semantic Analyzer took a lot more time then I had expected, and I found the scope manamgnemnt stuff to be pretty complex.

## AI usage:

### Tool used: Claude | Model: 3.7-Sonnet

#### How I used it for development
- Helped me with figuring out the best approach/ structure of the CST to AST conversion
- Guidance on how to create the symbol table
- Helped with debugging issues
- Suggestions for file organization and best way to structure for best practices
- Ideas for comprhensive test cases 

#### My Takeaway with this
- I made sure I understood everything the AI gave me and reread class notes to make sure I had a good understanding before implementing anything
- Was really helpful with best ways for CST to AST converting 
- Giving it my test file output and having it explain what exactly wasn't funcitoning properly and what could be causing that was incredibly helpful

#### What AI still struggles with
- When any project gets bigger, and there are more and more files that depend on other files it becomes more and more difficult for AI to understand the broader context of what the other pieces are doing which leads to increased halucinaitons.
- I found that I would ask it for help for the semantic Analyzer file and it would make incorrect assumptions about how other files were setup and give me unhelpful results