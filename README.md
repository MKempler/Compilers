# Parser

A recursive-descent parser in Java — phase 2 of a complete compiler for a small C-like language. Consumes the token stream from the [lexer](https://github.com/MKempler/Compilers/tree/Lexer) and builds a concrete syntax tree (CST).

## Overview

Each grammar production maps to a parser method, so the code mirrors the language grammar one-to-one. A successful parse produces a concrete syntax tree preserving the full syntactic structure; a failed parse reports the exact token and position where the grammar was violated.

## Supported grammar

- Blocks `{ ... }` terminated by `$`
- Variable declarations (`int`, `string`, `boolean`)
- Assignment and `print()` statements
- `if` statements and `while` loops, including nested blocks
- Expressions with `+`, `==`, `!=`, and boolean logic

## Features

- Recursive descent: one method per grammar rule, making the grammar directly readable from the code
- Builds a concrete syntax tree (CST) capturing the complete parse
- Syntax errors reported with the offending token's line and column
- Handles nested expressions, nested control flow, empty blocks, and multiple programs per file

## Usage

```bash
cd src
javac *.java
java Compiler ../test/test4.txt
```

## Test suite

8 test programs covering basic program structure, operators, nested expressions and control structures, empty blocks, and error cases (unterminated strings, invalid identifiers) — all passing.

## Files

- `src/Parser.java` — recursive-descent parser (~500 lines)
- `src/CSTNode.java` — concrete syntax tree node
- `src/Lexer.java`, `src/Token.java` — phase 1, unchanged
- `src/Compiler.java` — driver

## A four-phase compiler

1. [Lexer](https://github.com/MKempler/Compilers/tree/Lexer) — character stream → tokens
2. **Parser** (this branch) — tokens → concrete syntax tree
3. [Semantic Analysis](https://github.com/MKempler/Compilers/tree/SemanticAnalysis) — abstract syntax tree, symbol table, type checking
4. [Code Generation](https://github.com/MKempler/Compilers/tree/CodeGeneration) — 6502 machine code
