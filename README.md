Lexer
A hand-written lexical analyzer in Java — phase 1 of a complete compiler for a small C-like language.
Overview
The lexer reads source files character by character and converts them into a stream of tokens, each tagged with its type, lexeme, and exact line/column position. Its output feeds directly into the parser.
Features
Tokenizes keywords (print, while, if, int), identifiers, integer and string literals, boolean values, operators (=, ==, !=, +), block/parenthesis symbols, and the $ end-of-program marker
Skips /* */ block comments
Tracks line and column for every token, so diagnostics point at the exact source location
Lexical errors (unclosed strings/comments, invalid characters) are reported with position info instead of crashing the pipeline
Usage
cd src
javac *.java
java Compiler ../test/test4.txt
Example
Input:
{
  int x
  x = 1 + 2
  print(x)
} $
Output (abridged):
OPEN_BLOCK [ { ] found at (1:1)
I_TYPE [ int ] found at (2:3)
ID [ x ] found at (2:7)
...
Test suite
12 test programs covering empty input, comments, keywords, identifiers, operators, expressions, strings, number validation, and every error case — all passing.
Files
src/Lexer.java — the tokenizer (~260 lines)
src/Token.java — token types with line/column tracking
src/Compiler.java — driver
A four-phase compiler
Lexer (this branch) — character stream → tokens
Parser — recursive descent → concrete syntax tree
Semantic Analysis — abstract syntax tree, symbol table, type checking
Code Generation — 6502 machine code
