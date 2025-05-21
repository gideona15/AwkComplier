# AwkComplier
===========

A Java-based interpreter/compiler for a subset of the AWK programming language. This project simulates the functionality of AWK by processing input text files according to user-defined pattern-action rules.

Overview
--------

AWK is a powerful language for text processing and data extraction. This project is designed to help understand how AWK-like behavior can be implemented using a custom-built parser and execution engine in Java.

The AwkCompiler reads AWK-like scripts and applies the defined logic to input text files, making it a useful tool for pattern matching, reporting, and filtering tasks.

Features
--------

- Parse and interpret AWK-like pattern-action syntax
- Support for:
  - BEGIN/END blocks
  - Field splitting using delimiters
  - Regular expression pattern matching
  - Print and assignment statements
  - Arithmetic and logical operations
- Tokenizer and parser built from scratch in Java
- Modular design for ease of extension

Example
-------

Given a script:

    BEGIN { print "Start Processing" }
    $1 == "ERROR" { print $0 }
    END { print "Done" }

And an input file:

    INFO Initialization complete
    ERROR Failed to load module
    WARNING Deprecated method used
    ERROR Disk not found

The output will be:

    Start Processing
    ERROR Failed to load module
    ERROR Disk not found
    Done

Getting Started
---------------

### Prerequisites

- Java 11 or later
- Maven (optional, for build management)

### Running the Project

1. Clone the repository:

       git clone https://github.com/gideona15/AwkComplier.git
       cd AwkComplier

2. Compile the Java files:

       javac -d out src/*.java

3. Run the main class (adjust as needed):

       java -cp out Main awk_script.awk input.txt

### Example Files

- awk_script.awk: Your AWK-like script
- input.txt: Input file to process

Project Structure
-----------------

    src/
    ├── Main.java               # Entry point
    ├── Tokenizer.java          # Converts script into tokens
    ├── Parser.java             # Parses token stream into execution logic
    ├── Interpreter.java        # Runs the parsed logic on input
    ├── Context.java            # Stores variable and field state
    └── Node.java               # Represents parsed elements (AST)

Future Improvements
-------------------

- Add support for arrays
- Implement string manipulation functions (e.g., substr, split)
- Improve error handling and reporting
- Add support for user-defined functions

Author
------
Michael Phillips
Gideon Appenteng  
LinkedIn: https://www.linkedin.com/in/gideon-appenteng/  
GitHub: https://github.com/gideona15

License
-------

This project is open-source and available under the MIT License.
