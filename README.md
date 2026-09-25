# Semantic Analysis

Phase 3 of a complete compiler for a small C-like language, in Java. Converts the concrete syntax tree from the [parser](https://github.com/MKempler/Compilers/tree/Parser) into an abstract syntax tree (AST), then runs full semantic analysis: scoping, symbol table management, and type checking.

## Overview

Syntax alone can't catch a program that declares `int x` and then assigns it a string. This phase gives the language meaning — it tracks every variable's scope, type, and initialization state, rejecting programs that parse cleanly but don't make sense.

## Features

- CST → AST conversion, stripping syntactic noise and keeping only semantically meaningful nodes
- Symbol table with nested scoping, including variable shadowing in inner blocks
- Type checking for assignments, boolean expressions, and comparisons
- Errors: undeclared variables, type mismatches, redeclarations in the same scope
- Warnings (with error recovery, so analysis continues): unused variables, uninitialized variable use
- Unreachable-code detection in loops

## Usage

```bash
cd src
javac *.java
java Compiler ../test/test7.txt
```

## Test suite

11 test programs covering declarations and scoping rules, type checking, uninitialized/unused variable warnings, shadowing in nested scopes, string/int conversions, unreachable code, empty blocks, and error recovery after type errors — all passing.

## Files

- `src/ASTBuilder.java` — CST → AST conversion
- `src/ASTNode.java` — abstract syntax tree node
- `src/SemanticAnalyzer.java` — type checking and scope analysis
- `src/SymbolTable.java`, `src/Symbol.java` — scoped symbol management
- `src/Parser.java`, `src/CSTNode.java`, `src/Lexer.java`, `src/Token.java` — phases 1–2, unchanged
- `src/Compiler.java` — driver

## A four-phase compiler

1. [Lexer](https://github.com/MKempler/Compilers/tree/Lexer) — character stream → tokens
2. [Parser](https://github.com/MKempler/Compilers/tree/Parser) — tokens → concrete syntax tree
3. **Semantic Analysis** (this branch) — CST → AST, scoping, type checking
4. [Code Generation](https://github.com/MKempler/Compilers/tree/CodeGeneration) — 6502 machine code
