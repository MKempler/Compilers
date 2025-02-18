# Lexer - Project 1

## Setup:
1. cd src
2. Compile: javac *.java
3. Run tests: java Compiler ../test/<input_file>

## My test files:
- test1.txt: Empty file test
- test2.txt: normal comment test 
- test3.txt: Unclosed comment detection 
- test4.txt: Keywords and identifiers 
- test5.txt: Operators 
- test6.txt: Basic expressions 
- test7.txt: Multiple programs 
- test8.txt: Number validation 
- test9.txt: Parentheses 
- test10.txt: regular strings/ bad character test
- test11.txt: String missing closing quote with valid string after it
- test12.txt: Comment in the middle of a string

### All test cases passed

## Project 1 Reflection
I genuinly enjoyed working through this project, and while I feel good about how it turned out, I unfortunately procrastinated a lot with getting started on this project which I regret. As a result, I spent v e r y long hours over a few days working on it when I should have just started earlier. While it was very diffucult I liked how this project kind of forced incremental development, which is why I recognize I should have started early. Moving to project 2 I will defeintely start earlier and work in smaller chunks day by day. 

## AI usage:

### Tool used: Claude | Model: 3.5-Sonnet

#### How I used it for development
- Insight on code organization and how to efficiently structure my code with the context of best practices for lexers
- Suggested the use of Switch statements instead of if/else chains - prompted me to actually learn about something I hadn't used which was really cool and was definitely more efficient
- Guidance on method separation
- Helped me understand tracking of line and column positioning
- Was most helpful for debugging issues and helping with ideas for test cases

#### My Takeaway with this
- As I developed this project and used Claude when I was stuck - what I focused on was not using any code unless I absolutely understood what it was doing, and even then making it my own
- For example with the Switch statements, I made sure I had a real understanding of how it functioned before I implemented it in my code
- Claude was a fantastic tool to be able to help me better understand the concepts we covered in class and with debugging
  

#### What AI still struggles with
- What i found interesting that didn't work great is AI still struggles with hallucinations and being very confidently wrong
- For instance often it would offer a "solution" to a bug but the fix it was proposing would clearly break other sections of the code

