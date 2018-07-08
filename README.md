# C-Minus-Compiler
Compiler for the C Minus Programming Language



This program is the code generator for a compiler of the language C-. This program 
is written in Java, and works by checking the outputs of the Lexical 
Analyzer against a context free grammar that defines the rules of the C- 
language. While parsing the code, the program will also be generating the assembly code 
that corresponds with the program. If the code parses correctly in its entirety, the program 
will output the word ACCEPT with the following code generation. Otherwise, it will output 
REJECT.  
This program takes the output file of the Lexical Analyzer (intermediate.txt) as an input.

This program was designed to replicate the actions of parsing a grammar, 
where the program will call functions as a non-terminal by observing 
terminals in the intermediate file. the program will push these functions 
in a stack-like manner, and check whether expected terminals are present. If 
not, the program will print REJECT and the program will terminate. Then, the program will examine
the tokens that were created for correct semantic structure. If correct semantics are found, the 
program will print ACCEPT and then terminate, and it will print REJECT and then terminate otherwise. 
This program does not create any files.

First, the user will need to compile source files just by running the make command:

make

To run this program, one could simply run the following commands using the 
p4 shell script:

./p4 *inputfile*

or

p4 *inputfile*

If one does not use the shell scripts, they can compile and run the program 
with the following commands:

javac LexicalAnalyzer/LexicalAnalyzer.java

javac Parser.java

java LexicalAnalyzer/LexicalAnalyzer *inputfile*

java Parser LexicalAnalyzer/intermediate.txt
