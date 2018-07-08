all: Parser.class LexicalAnalyzer/LexicalAnalyzer.class

Parser.class : Parser.java
	javac Parser.java

LexicalAnalyzer/LexicalAnalyzer.class : LexicalAnalyzer/LexicalAnalyzer.java
	javac LexicalAnalyzer/LexicalAnalyzer.java
