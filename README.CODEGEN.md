# Code Generator - Project 4 (Please grade branch named: "CodeGeneration")

## Setup:
1. cd src
2. Compile: javac *.java
3. Run tests: java Compiler ../test/<input_file>

## My test files:
- test1.txt: Basic integer variable declaration with assignment and print
- test2.txt: Variable value transfer through multiple assignments
- test3.txt: If statement with equality operator and conditional string output
- test4.txt: Addition operator with both literal values and variable references
- test5.txt: Boolean evaluation in if statements with block execution
- test6.txt: Variable shadowing in nested scopes
- test7.txt: While loop control structure
- test8.txt: Not equals comparison operator in Boolean expressions
- test9.txt: Multi level scope nesting with shadowed variable resolution
- test10.txt: Multiple program test
- test11.txt: Lexer error test (unterminated string)
- test12.txt: Parser error test (missing parantheses)
- test13.txt: Semantic Error test (undeclared variable)
- test14.txt: Unused variable warning
- test15.txt: uninitalized variable warning
- test16.txt variable declaration error
- test17.txt: type mismatch error


### All test cases passed

## Project 4 Reflection
Wow...I really struggled with this project and at some points started losing my mind a bit but in the end I found the process to be rewarding.
I think this was the hardest of the projects, with implementing if and while loops successfully being very challenging for me. I specifically struggled with branches and getting the proper jump locations. I also spent lots of time trying to fix an issue in code gen while the root issue was in AST builder which reminded me how important it is to be meticulous and pay attention to the full output. In the end, despite the challanges, it was very interesting to see how all the pieces fit together and forms a full compiler. 

## AI usage:

### Tool used: Claude | Model: 3.7-Sonnet

#### How I used it for development
- Gave directional help when I got stuck with what to implement next
- Advice for overall structure and design
- gave it the emulator output and the machine code and asked to help debug exactly what was causing an issue (machine code less so)
- Got me through a huge issue where string literals were not being built correctly, advised me for how to refactor based on the logic I added
- Helped me with ideas for test cases
- overall debugging and code issues help


#### My Takeaway with this
- Using AI was incredibly helpful for project 4, there were moments where I was struggling to get past a bug but by giving the AI all the context it helped me pinpoint exactly what was happening and more importanlty why, allowing me to understand and make the necessary fixes. 
- Anything the AI gave me, I made sure I understood fully before implementing and tweaked it.
- Was the most helpful with debugging methods specifically my branching logic which is what tripped me up the most


#### What AI still struggles with
The AI struggled immensely when I gave it machine code, at points I gave it my 256 memory image to ask what was happening in the output and it got things blatantly wrong. I beleive this shows that there are curernt limits with its reasoning ability for deep complexity. Also with my code when I would ask why my branching wasn't working it would keep reccomending instructions that aren't in our set no matter how many times I corrected it.