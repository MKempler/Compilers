# Parser - Project 2

## Setup:
1. cd src
2. Compile: javac *.java
3. Run tests: java Compiler ../test/<input_file>

## My test files:


### All test cases passed

## Project 2 Reflection
Building the parser was really interesting. It was cool buildig right on top of the lexer from Project 1. The recursive descent approach worked well as from class I understood how each grammar rule mapped to a method in the parser. My incremntal approach for this project worked well. I started with a basic program structure, then adding the parser methods one by one. 

 The most difficult part was handling the boolean expressions with operators, but breaking it down into smaller pieces made it better.

I did find this project easier then project one. I think it was because once I got the core of the CSTNode file and the helper functions of the parser file, it was really just incrementally adding each method for each thing in the grammar, which was pretty intutive.

## AI usage:

### Tool used: Claude | Model: 3.7-Sonnet

#### How I used it for development
- Guidance on implementing recursive descent parsing techniques
- Helped with understanding how to build and structure the CST
- Guidance on best practices for code structuring / layout
- Assistance with debugging parsing issues, especially with nested expressions
- Ideas for  test cases to validate each grammar rule

#### My Takeaway with this
- I made sure anything the AI gave me I comepletely understood before putting it in my code, and even then I made sure I made it my own
- I struggled with the cstnode file, but Claude really helped me understand the best way to build it out
It's defintiely best when it comes to debugging issues and providing ideas for testing

#### What AI still struggles with
- It weirdly will often suggest overly complex solutions to problems even when there are simpler more efficeint choices
- When I gave it examples from the class grammar it would ignore it when suggesting things often and provide more "general" ideas
- It also would make assumptions of how the lexer handled things that were not correct
