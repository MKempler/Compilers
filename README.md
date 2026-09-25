# Code Generation

Phase 4 of a complete compiler for a small C-like language, in Java — and the finished product. Walks the abstract syntax tree from [semantic analysis](https://github.com/MKempler/Compilers/tree/SemanticAnalysis) and emits machine code for a 6502-inspired target, producing a 256-byte memory image runnable on an emulator.

## Overview

This is the phase where the compiler becomes real: source code in, executable machine code out. The generator traverses the AST and emits 6502 opcodes (`LDA`/`STA`/`ADC`/`BNE`/`BRK`, plus a custom `FF` system-call instruction for I/O), laying out variables, temporaries, string literals, and branch targets in a 256-byte memory image.

## Features

- Full pipeline in one command: lex → parse → analyze → generate, with errors at any stage halting codegen
- 6502 target with a custom `FF` system-call instruction powering `print` for integers and strings
- Static memory layout: user variables allocated from `0xC0` upward, reserved temporaries for expression evaluation
- Branch backpatching: forward jumps for `if`/`while` are emitted with placeholders and resolved in a second pass
- String interning: duplicate string literals share a single pooled address
- End-to-end verification: 19 tests including per-phase error cases (lexer, parser, and semantic errors each fail gracefully before codegen)

## Usage

```bash
cd src
javac *.java
java Compiler ../test/test4.txt
```

Example input:

```
{
  int x
  x = 1 + 2
  print(x)

  int y
  y = 4 + x
  print(y)
} $
```

The compiler lexes, parses, type-checks, and emits the corresponding 6502 machine code as a hex memory image.

## Test suite

19 test programs: variable declaration/assignment, arithmetic, `if`/`while` control flow, nested scopes with shadowing, loop backpatching across multiple branch instructions, a string-pool stress test, and error cases from every earlier phase — all passing.

## Files

- `src/CodeGenerator.java` — AST → 6502 machine code (~780 lines)
- `src/ASTBuilder.java`, `src/ASTNode.java`, `src/SemanticAnalyzer.java`, `src/SymbolTable.java`, `src/Symbol.java` — phase 3, unchanged
- `src/Parser.java`, `src/CSTNode.java`, `src/Lexer.java`, `src/Token.java` — phases 1–2, unchanged
- `src/Compiler.java` — driver (~3,000 lines total across the final branch)

## A four-phase compiler

1. [Lexer](https://github.com/MKempler/Compilers/tree/Lexer) — character stream → tokens
2. [Parser](https://github.com/MKempler/Compilers/tree/Parser) — tokens → concrete syntax tree
3. [Semantic Analysis](https://github.com/MKempler/Compilers/tree/SemanticAnalysis) — CST → AST, scoping, type checking
4. **Code Generation** (this branch) — AST → 6502 machine code
