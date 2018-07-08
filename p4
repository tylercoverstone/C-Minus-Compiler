!#/bin/ksh

cd LexicalAnalyzer
java LexicalAnalyzer ../$1
cd ../
java Parser LexicalAnalyzer/intermediate.txt
