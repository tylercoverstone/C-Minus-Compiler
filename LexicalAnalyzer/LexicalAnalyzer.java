import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.ArrayList;

public class LexicalAnalyzer 
{
    public static String[] KEYWORDS = {"else", "if", "int", "float", "return", "void", "while"};
    public static Symbol[] SYMBOLS = {
	new Symbol("+", false),
	new Symbol("-", false),
	new Symbol("*", false),
	new Symbol("/", false),
	new Symbol("<", false),
	new Symbol("<=", false),
	new Symbol(">", false),
	new Symbol(">=", false),
	new Symbol("==", false),
	new Symbol("!=", false),
	new Symbol("=", false),
	new Symbol(";", false),
	new Symbol(",", false),
	new Symbol("(", true),
	new Symbol(")", true),
	new Symbol("[", true),
	new Symbol("]", true),
	new Symbol("{", true),
	new Symbol("}", true),
	new Symbol("*/", false),
	new Symbol("/*", false)
    };
    public static ArrayList<SymTabElement> SYMTAB = new ArrayList<SymTabElement>();

    public static void main(String[] args) throws FileNotFoundException, IOException
    {
	File file = new File(args[0]);
	Scanner s = new Scanner(file);
		
	FileWriter fileWriter = new FileWriter("intermediate.txt");
        PrintWriter p = new PrintWriter(fileWriter);

        int commentDepth = 0;
        int depth = 0;
        
        while(s.hasNextLine())
        {
            String lineStr = s.nextLine();
            char[] line = lineStr.toCharArray();

            if(!lineStr.equals("")) //Skip empty line
            {
                //System.out.println("INPUT: " + lineStr);
                p.println("INPUT: " + lineStr);
            }

            String currentStr = "";

            int currentChar = 0;

            boolean isBlockComment = false;

            while(currentChar < line.length)
            {
                if(line[currentChar] == '/' && currentChar + 1 < line.length) //Look for block comment
                {
                    if(line[currentChar + 1] == '/')
                    {
                        break;
                    }
                    else if(line[currentChar + 1] == '*')
                    {
                        commentDepth ++;
                        isBlockComment = true;
                    }
                }

                if(commentDepth > 0) //Current character is within a comment
                {
                    if(line[currentChar] == '*' && currentChar + 1 < line.length) //Look for end to comment
                    {
                        if(line[currentChar + 1] == '/')
                        {
                            commentDepth --;
                            isBlockComment = true;
                        }
                    }
                }  
                else if(commentDepth == 0) //Current character isn't within a comment
                {
                    char currentCharacter = line[currentChar];
                    
                    if(currentCharacter == ' ' || currentCharacter == '\t') //Read character is a space
                    {
                        if(isKeyword(currentStr)) //Check if accumulated string is a keyword
                        {
                            //System.out.println("Keyword:" + currentStr);
                            p.println("Keyword:" + currentStr);
                            currentStr = "";
                        }
                        else if(allAlphabetic(currentStr) && !currentStr.equals("")) //Check if accumulated string is an identifier
                        {
                            //System.out.println("Identifier:" + currentStr);
                            p.println("Identifier:" + currentStr);
                            SYMTAB.add(new SymTabElement(currentStr, depth));
                            currentStr = "";
                        }
                        else if(isSymbol(currentStr)) //Check if accumulated string is a symbol
                        {
                            //System.out.println(currentStr);
                            p.println(currentStr);
                            currentStr = "";
                        }
                        else if(allNumeric(currentStr) && !currentStr.equals("")) //Check if accumulated string is an int
                        {
                            //System.out.println("Int:" + currentStr);
                            p.println("Int:" + currentStr);
                            currentStr = "";
                        }
                        else if(isFloat(currentStr)) //Check if accumulated string is a float
                        {
                            //System.out.println("Float:" + currentStr);
                            p.println("Float:" + currentStr);
                            currentStr = "";
                        } 
                        else if(currentStr.equals("")) //Check if accumulated string is empty
                        {
                            currentStr = "";
                        }
                        else //Error if none of these
                        {
                            //System.out.println("Error:" + currentStr);
                            p.println("Error:" + currentStr);
                            currentStr = "";
                        }
                    }
                    else if(isAlphabetic(currentCharacter) && currentCharacter != ' ') //Read character is alphabetic
                    {
                        if(allAlphabetic(currentStr) || currentStr.equals("")) //Append character if accumulated string is all alphabetic or empty
                        {
                            currentStr = currentStr + currentCharacter;
                        }
                        else if(allNumeric(currentStr) && !currentStr.equals("")) //If accumulated string is int, append to end to cycle through as error
                        {
                            currentStr = currentStr + currentCharacter;
                        }
                        else if(isFloat(currentStr)) //If accumulated string is float, append to end to cycle through as error
                        {
                            currentStr = currentStr + currentCharacter;
                        }
                        else if(isSymbol(currentStr)) //If accumulated string is a symbol, report the symbol and start a new string
                        {
                            //System.out.println(currentStr);
                            p.println(currentStr);
                            currentStr = "";
                            currentStr = currentStr + currentCharacter;
                        }
                        else ///append to end of string, will be caught when it reaches a symbol or a space or the end of the line
                        {
                            currentStr = currentStr + currentCharacter;
                        }
                    }
                    else if(isSymbol(String.valueOf(currentCharacter))) //Read character is a symbol
                    {
                        if(currentCharacter == '{')
                        {
                            depth ++;
                        }
                        else if(currentCharacter == '}')
                        {
                            depth --;
                        }
                        
                        if(isKeyword(currentStr)) //Check if accumulated string is a keyword
                        {
                            //System.out.println("Keyword:" + currentStr);
                            p.println("Keyword:" + currentStr);
                            currentStr = "";
                            currentStr = currentStr + currentCharacter;
                        }
                        else if(allAlphabetic(currentStr) && !currentStr.equals("")) //Check if accumulated string is an identifier
                        {
                            //System.out.println("Identifier:" + currentStr);
                            p.println("Identifier:" + currentStr);
                            SYMTAB.add(new SymTabElement(currentStr, depth));
                            currentStr = "";
                            currentStr = currentStr + currentCharacter;
                        }
                        else if(allNumeric(currentStr) && !currentStr.equals("")) //Check if accumulated string is an int
                        {
                            //System.out.println("Int:" + currentStr);
                            p.println("Int:" + currentStr);
                            currentStr = "";
                            currentStr = currentStr + currentCharacter;
                        }
                        else if(currentStr.matches("[0-9]+(.[0-9]*)?E")) //Check if accumulated string adheres to potential float
                        {
                            if(currentCharacter == '+' || currentCharacter == '-')
                            {
                                currentStr = currentStr + currentCharacter;
                            }
                            else
                            {
                                //System.out.println("Error:" + currentStr);
                                p.println("Error:" + currentStr);
                                currentStr = "";
                                currentStr = currentStr + currentCharacter;
                            }
                        }
                        else if(isFloat(currentStr)) //Check if accumulated string is a float
                        {
                            //System.out.println("Float:" + currentStr);
                            p.println("Float:" + currentStr);
                            currentStr = "";
                            currentStr = currentStr + currentCharacter;
                        }
                        else if(currentStr.equals("")) //Handle if previous string is empty
                        {
                            currentStr = currentStr + currentCharacter;
                        }
                        else if(isSymbol(currentStr) || currentStr.equals("!")) //Check if previous string is a symbol
                        {
                            if((currentStr.equals("<") || currentStr.equals(">") || currentStr.equals("!") || currentStr.equals("=")) && currentCharacter == '=') //Check for double character symbol
                            {
                                currentStr = currentStr + currentCharacter;
                            }
                            else //Handle single character symbol
                            {
                                //System.out.println(currentStr);
                                p.println(currentStr);
                                currentStr = "";
                                currentStr = currentStr + currentCharacter;
                            }
                        }
                        else //Error if none of these
                        {
                            //System.out.println("Error:" + currentStr);
                            p.println("Error:" + currentStr);
                            currentStr = "";
                            currentStr = currentStr + currentCharacter;
                        }  
                    }
                    else if((isNumeric(currentCharacter) && currentCharacter != ' ')) //Check if current character is numeric
                    {
                        if(isKeyword(currentStr)) //Check if accumulated string is keyword
                        {
                            currentStr = currentStr + currentCharacter;
                        }
                        else if(allAlphabetic(currentStr) && !currentStr.equals("")) //Check if accumulated string is identifier
                        {
                            currentStr = currentStr + currentCharacter;
                        }
                        else if(currentStr.equals("")) //Handle if previous string is empty
                        {
                            currentStr = currentStr + currentCharacter;
                        }
                        else if(allNumeric(currentStr) && !currentStr.equals("")) //Check if accumulated string is an int
                        {
                            currentStr = currentStr + currentCharacter;
                        }
                        else if(currentStr.matches("[0-9]+(.[0-9]*)?(E[+-]?[0-9]*)?")) //Check if accumulated string is a float (or adheres to float rules by regex)
                        {
                            currentStr = currentStr + currentCharacter;
                        }
                        else if(isSymbol(currentStr)) //Check if accumulated string is a symbol
                        {
                            //System.out.println(currentStr);
                            p.println(currentStr);
                            currentStr = "";
                            currentStr = currentStr + currentCharacter;
                        }
                        else //append to end of number, will be caught when it reaches a symbol or a space or the end of the line
                        {
                            currentStr = currentStr + currentCharacter;
                        }
                    }
                    else //If current character is none of these
                    {
                        if(isKeyword(currentStr))
                        {
                            //System.out.println("Keyword:" + currentStr);
                            p.println("Keyword:" + currentStr);
                            currentStr = "";
                            currentStr = currentStr + currentCharacter;
                        }
                        else if(allAlphabetic(currentStr) && !currentStr.equals("")) //Check if accumulated string is an identifier
                        {
                            //System.out.println("Identifier:" + currentStr);
                            p.println("Identifier:" + currentStr);
                            SYMTAB.add(new SymTabElement(currentStr, depth));
                            currentStr = "";
                            currentStr = currentStr + currentCharacter;
                        }
                        else if(currentStr.equals("")) //Handle if accumulated string is empty
                        {
                            currentStr = currentStr + currentCharacter;
                        }
                        else if(allNumeric(currentStr) && !currentStr.equals("")) //Handle if accumulated string is an int
                        {
                            if(currentCharacter == '.') //Append decimal point 
                            {
                                currentStr = currentStr + currentCharacter;
                            }
                            else if(currentCharacter == 'E') //Append E
                            {
                                currentStr = currentStr + currentCharacter;
                            }
                            else //Append for future error check
                            {
                                //System.out.println("Int:" + currentStr);
                                p.println("Int:" + currentStr);
                                currentStr = "";
                                currentStr = currentStr + currentCharacter;
                            }
                        }
                        else if(currentStr.matches("[0-9]+(.[0-9]+)?"))
                        {
                            if(currentCharacter == 'E') //Append E
                            {
                                currentStr = currentStr + currentCharacter;
                            }
                            else
                            {
                                //System.out.println("Float:" + currentStr);
                                p.println("Float:" + currentStr);
                                currentStr = "";
                                currentStr = currentStr + currentCharacter;
                            }
                        }
                        else if(isSymbol(currentStr)) //Check if accumulated string is a symbol
                        {
                            //System.out.println(currentStr);
                            p.println(currentStr);
                            currentStr = "";
                            currentStr = currentStr + currentCharacter;
                        }
                        else //Append to string if none of these
                        {
                            currentStr = currentStr + currentCharacter;
                        }   
                    }
                }
                
                if(currentChar == line.length - 1)
                {
                    if(!currentStr.equals("") && !currentStr.matches("(\\s)+"))
                    {
                        if(isKeyword(currentStr)) //Check if accumulated string is a keyword
                        {
                            //System.out.println("Keyword:" + currentStr);
                            p.println("Keyword:" + currentStr);
                            currentStr = "";
                        }
                        else if(allAlphabetic(currentStr) && !currentStr.equals("")) //Check if accumulated string is an identifier
                        {
                            //System.out.println("Identifier:" + currentStr);
                            p.println("Identifier:" + currentStr);
                            SYMTAB.add(new SymTabElement(currentStr, depth));
                            currentStr = "";
                        }
                        else if(isSymbol(currentStr)) //Check if accumulated string is a symbol
                        {
                            //System.out.println(currentStr);
                            p.println(currentStr);
                            currentStr = "";
                        }
                        else if(allNumeric(currentStr) && !currentStr.equals("")) //Check if accumulated string is an int
                        {
                            //System.out.println("Int:" + currentStr);
                            p.println("Int:" + currentStr);
                            currentStr = "";
                        }
                        else if(isFloat(currentStr)) //Check if accumulated string is a float
                        {
                            //System.out.println("Float:" + currentStr);
                            p.println("Float:" + currentStr);
                            currentStr = "";
                        } 
                        else //Error if none of these
                        {
                            //System.out.println("Error:" + currentStr);
                            p.println("Error:" + currentStr);
                            currentStr = "";
                        }
                    }
                    
                    if(commentDepth == 0)
                    {
                        //System.out.println("");
                        p.println("");
                    }
                }

                if(isBlockComment)
                {
                    currentChar += 2;
                    isBlockComment = false;

                }
                else
                {
                    currentChar ++;
                }
            }
        }
        
        /*Begin Prinitng SYMTAB===============================================================================*/
        /*System.out.println("SYMTAB Contents:");
        p.println("SYMTAB Contents:");
            
        for(int x=0; x<SYMTAB.size(); x++)
        {
            System.out.println("Identifier: " + SYMTAB.get(x).id + ", Depth: " + SYMTAB.get(x).depth);
            p.println("Identifier: " + SYMTAB.get(x).id + ", Depth: " + SYMTAB.get(x).depth);
        }*/
        /*End Prinitng SYMTAB=================================================================================*/
        
        //System.out.println("$");
        p.println("$");

        p.close();
	s.close();
        
    }
	
    public static boolean isAlphabetic(char x)
    {
	if(x == 'a' || x == 'b' || x == 'c' || x == 'd' || x == 'e' || x == 'f' || x == 'g' || x == 'h' || x == 'i' || x == 'j' || x == 'k' || x == 'l' || x == 'm' || x == 'n' || x == 'o' || x == 'p' || x == 'q' || x == 'r' || x == 's' || x == 't' || x == 'u' || x == 'v' || x == 'w' || x == 'x' || x == 'y' || x == 'z')
	{
            return true;
	}
	else
	{
            return false;
	}
    }
        
    public static boolean isNumeric(char x)
    {
        if(x == '0' || x == '1' || x == '2' || x == '3' || x == '4' || x == '5' || x == '6' || x == '7' || x == '8' || x == '9')
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public static boolean allAlphabetic(String s)
    {
        boolean isAlphabetic = true;
        
        char[] Str = s.toCharArray();
        
        for(int x=0; x<Str.length; x++)
        {
            if(isAlphabetic(Str[x]))
            {
                continue;
            }
            else
            {
                isAlphabetic = false;
            }
        }
        
        return isAlphabetic;
    }
    public static boolean allNumeric(String s)
    {
        boolean isNumeric = true;
        
        char[] Str = s.toCharArray();
        
        for(int x=0; x<Str.length; x++)
        {
            if(isNumeric(Str[x]))
            {
                continue;
            }
            else
            {
                isNumeric = false;
            }
        }
        
        return isNumeric;
    }
    
    public static boolean isFloat(String s)
    {
        String regex1 = "[0-9]+(.[0-9]+)?(E[+-]?[0-9]+)?";
        String regex2 = "[0-9]+[a-z]";
        
        return (s.matches(regex1) && !s.matches(regex2));
    }

    public static boolean isKeyword(String x)
    {
        boolean check = false;
		
        for(int i=0; i<KEYWORDS.length; i++)
        {
            if(KEYWORDS[i].equals(x))
            {
		check = true;
            }
        }
		
        return check;
    }
	
    public static boolean isSymbol(String x)
    {
        boolean check = false;
		
        for(int i=0; i<SYMBOLS.length; i++)
        {
            if(SYMBOLS[i].symbol.equals(x))
            {
                check = true;
            }
        }
		
        return check;
    }	
}

class Symbol
{
    public String symbol;
    public boolean isDelimeter;
	
    public Symbol(String s, boolean d) 
    {
	this.symbol = s;
	this. isDelimeter = d;
    }
}

class SymTabElement
{
    public String id;
    public int depth;
    
    public SymTabElement(String s, int d)
    {
        this.id = s;
        this.depth = d;
    }
}