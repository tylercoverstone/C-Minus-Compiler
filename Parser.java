//package project2netbeans;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

public class Parser 
{
    static String token;
    static String[][] code = new String[500][4];
    static int tcount = 0;
    static int codeCount = 0;

    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        File file = new File(args[0]);
	Scanner s = new Scanner(file);
        
        
        
        //Begin test values==========================================================================
        /*
        code[0][0] = "func";
        code[0][1] = "main";
        code[0][2] = "void";
        code[0][3] = "0";
        
        code[1][0] = "alloc";
        code[1][1] = "4";
        code[1][2] = null;
        code[1][3] = "x";
        
        code[2][0] = "alloc";
        code[2][1] = "4";
        code[2][2] = null;
        code[2][3] = "y";
        
        code[3][0] = "alloc";
        code[3][1] = "4";
        code[3][2] = null;
        code[3][3] = "z";
        
        code[4][0] = "mult";
        code[4][1] = "3";
        code[4][2] = "y";
        code[4][3] = "t0";
        */
        //End test values==========================================================================
        
        token = getLine(s);
        
        A(s);
        
        if(token.equals("$"))
        {
            System.out.println("ACCEPT");
            
            System.out.println("");
            
            for(int i=0; i<500; i++)
            {
                if(code[i][0] == null)
                {
                    break;
                }
                else
                {
                    String instruction = code[i][0];
                    String op1 = code[i][1];
                    String op2 = code[i][2];
                    String result = code[i][3];
                    
                    if(op1 == null)
                    {
                        op1 = "";
                    }
                    if(op2 == null)
                    {
                        op2 = "";
                    }
                    if(result == null)
                    {
                        result = "";
                    }
                    
                    System.out.format("%-5d %-20s %-20s %-20s %-20s", i+1, instruction, op1, op2, result);
                }
                System.out.println("");
            }
            
            
            //Parsing is correct at this point, build Symbol Table here
            
            ArrayList<String> tokens = new ArrayList<String>();
            
            Scanner sc = new Scanner(file);
            
            while(sc.hasNextLine()) //Create array of tokens for creating symbol table
            {
                tokens.add(sc.nextLine());
            }
            
            ArrayList<SymTabElement> SYMTAB = new ArrayList<SymTabElement>();
            
            int depth = 0;
            
            int i=0;
            while(i<tokens.size())
            {
                String token = tokens.get(i);
                
                String id = "";
                String type = "";
                boolean isArray = false;
                boolean isFunction = false;
                ArrayList<String> parameters = new ArrayList<String>();
                ArrayList<String> parameterTypes = new ArrayList<String>();
                ArrayList<Boolean> parameterIsArray = new ArrayList<Boolean>();
                
                if(token.equals("Keyword:int") || token.equals("Keyword:float") || token.equals("Keyword:void"))
                {
                    type = token.substring(8);
                    i++;
                    
                    if(tokens.get(i).matches("Identifier:.*"))
                    {
                        token = tokens.get(i);    
                        id = token.substring(11);
                        i++;
                        
                        if(tokens.get(i).equals("["))
                        {
                            isArray = true;
                            SymTabElement ste = new SymTabElement(id, type, depth, isArray, isFunction, parameters, parameterTypes, parameterIsArray);
                            SYMTAB.add(ste);
                            i++;
                        }
                        else if(tokens.get(i).equals("("))
                        {
                            isFunction = true;
                            i++;
                            
                            if(tokens.get(i).equals("Keyword:void"))
                            {
                                SymTabElement ste = new SymTabElement(id, type, depth, isArray, isFunction, parameters, parameterTypes, parameterIsArray);
                                SYMTAB.add(ste);
                                i++;
                            }
                            else
                            {
                                int j = i;
                                
                                int count = 0;
                                
                                while(!tokens.get(j).equals(")"))
                                {
                                    parameterTypes.add(tokens.get(j).substring(8));
                                    j++;
                                    count++;
                                    
                                    parameters.add(tokens.get(j).substring(11));
                                    j++;
                                    count++;
                                    
                                    if(tokens.get(j).equals("["))
                                    {
                                        parameterIsArray.add(true);
                                        j+=2;
                                        count+=2;
                                    }
                                    else
                                    {
                                        parameterIsArray.add(false);
                                    }
                                    
                                    if(tokens.get(j).equals(","))
                                    {
                                        j++;
                                        count++;
                                    }
                                         
                                }
                                
                                SymTabElement ste = new SymTabElement(id, type, depth, isArray, isFunction, parameters, parameterTypes, parameterIsArray);
                                SYMTAB.add(ste);
                                
                                i+=count;
                            }
                        }
                        else
                        {
                            SymTabElement ste = new SymTabElement(id, type, depth, isArray, isFunction, parameters, parameterTypes, parameterIsArray);
                            SYMTAB.add(ste);
                            i++;
                        }
                    }
                }
                else if(token.equals("{"))
                {
                    depth++;
                    i++;
                }
                else if(token.equals("}"))
                {
                    depth--;
                    i++;
                }
                else
                {
                    i++;
                }
            }
            
            //Symbol Table is now created
            
            /*System.out.println("\nSYMBOL TABLE");
            System.out.println("");
            for(int j=0; j<SYMTAB.size(); j++)
            {
                System.out.println("-------------------------------------------------------------");
                System.out.println("");
                System.out.println("Id: " + SYMTAB.get(j).id);
                System.out.println("Type: " + SYMTAB.get(j).type);
                System.out.println("Depth: " + SYMTAB.get(j).depth);
                System.out.println("Is Array: " + SYMTAB.get(j).isArray);
                System.out.println("Is Function: " + SYMTAB.get(j).isFunction);
                System.out.print("Parameters: ");
                for(int k=0; k<SYMTAB.get(j).parameters.size(); k++)
                {
                    System.out.print(SYMTAB.get(j).parameters.get(k) + ", ");
                }
                System.out.println("");
                System.out.print("Parameter Types: ");
                for(int k=0; k<SYMTAB.get(j).parameterTypes.size(); k++)
                {
                    System.out.print(SYMTAB.get(j).parameterTypes.get(k) + ", ");
                }
                System.out.println("");
                System.out.print("Parameter is Array: ");
                for(int k=0; k<SYMTAB.get(j).parameterIsArray.size(); k++)
                {
                    System.out.print(SYMTAB.get(j).parameterIsArray.get(k) + ", ");
                }
                System.out.println("");
                System.out.println("");
            }*/
            
            boolean goodSemantics = semanticAnalyzer(tokens, SYMTAB);
            
            if(goodSemantics)
            {
                System.out.print("ACCEPT");
            }
            else
            {
                System.out.print("REJECT");
            }
        }
        else
        {
            System.out.print("REJECT");
        }
        
    }
    
    //End Main ------------------------------------------------------------------------------------------------------------------------------------------------------------
    
    public static String getLine(Scanner s) // get valid input line from intermediate file
    {
        String temp = "";
        
        while(s.hasNextLine())
        {
            temp = s.nextLine();
            
            if(!temp.matches("INPUT:.*") && !temp.equals("")) // if not an input line or a space, return the line
            {
                break;
            }
        }
        
        return temp;
    }
    
    public static void A(Scanner s)
    {
        //System.out.println(token + "Accept in A");
        B(s);   
    }
    public static void B(Scanner s)
    {
        //System.out.println(token + "Accept in B");
        C(s);
        Bprime(s);
    }
    public static void Bprime(Scanner s)
    {
        if(token.equals("$"))
        {
            //System.out.println(token + "Accept in Bprime Dollar sign found");
        }
        else if(token.equals("Keyword:int") || token.equals("Keyword:void") || token.equals("Keyword:float"))
        {
            //System.out.println(token + "Accept in Bprime");
            C(s);
            Bprime(s);
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Bprime" + token);
            System.exit(0);
        }
    }
    public static void C(Scanner s)
    {
        String e = E(s);
        if(token.matches("Identifier:.*"))
        {
            String var = token.substring(11);
            //System.out.println(token + "Accept in C");
            token = getLine(s);
            String c = Cprime(s, e, var);
            
            //System.out.println(e + " " + var + c);
            
            if(!c.equals("") && c.substring(0,1).equals("["))
            {
                String[] first = c.split("\\[");
                String[] second = first[1].split("\\]");
                
                int alloc = Integer.parseInt(second[0]);
                
                code[codeCount][0] = "alloc";
                code[codeCount][1] = "";
                code[codeCount][2] = Integer.toString(alloc * 4);
                code[codeCount][3] = var; 

                codeCount++;
            }
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in C" + token);
            System.exit(0);
        }
    }
    public static String Cprime(Scanner s, String type, String name)
    {
        if(token.equals("("))
        {
            //System.out.println(token + "Accept in Cprime");
            token = getLine(s);
            String g = G(s);

            if(token.equals(")"))
            {
                //System.out.println(token + "Accept in Cprime");
                token = getLine(s);
                
                String[] parms = g.split(",");
                
                if(parms[0].equals("void"))
                {
                    code[codeCount][0] = "func";
                    code[codeCount][1] = name;
                    code[codeCount][2] = type;
                    code[codeCount][3] = "0"; //Integer.toString(codeCount+2);

                    codeCount++;
                }
                else
                {
                    code[codeCount][0] = "func";
                    code[codeCount][1] = name;
                    code[codeCount][2] = type;
                    code[codeCount][3] = Integer.toString(codeCount+2);

                    codeCount++;
                    
                    for(int i=0; i<parms.length; i++)
                    {
                        code[codeCount][0] = "param";
                        code[codeCount][1] = "";
                        code[codeCount][2] = "";
                        code[codeCount][3] = "";

                        codeCount++;
                        
                        code[codeCount][0] = "alloc";
                        code[codeCount][1] = "4";
                        code[codeCount][2] = "";
                        code[codeCount][3] = parms[i];

                        codeCount++;
                    }
                }

                J(s);
                
                code[codeCount][0] = "end";
                code[codeCount][1] = "func";
                code[codeCount][2] = name;
                code[codeCount][3] = "";

                codeCount++;
                
                return "(" + g + ")";
            }
            else
            {
                System.out.print("REJECT");
                //System.out.println(" in Cprime" + token);
                System.exit(0);
                return "";
            }
            
        }
        else if(token.equals(";") || token.equals("["))
        {
            //System.out.println(token + "Accept in Cprime");
            String d = D(s);
            return d;
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Cprime" + token);
            System.exit(0);
            return "";
        }
    }
    public static String D(Scanner s)
    {
        if(token.equals(";"))
        {
            //System.out.println(token + "Accept in D");
            token = getLine(s);
            return "";
        }
        else if(token.equals("["))
        {
            //System.out.println(token + "Accept in D");
            token = getLine(s);
            
            if(token.matches("Int:.*") || token.matches("Float:.*"))
            {
                //System.out.println(token + "Accept in D");
                String var = "";
                
                if(token.matches("Int:.*"))
                {
                    var = token.substring(4);
                }
                if(token.matches("Float:.*"))
                {
                    var = token.substring(6);
                }
                
                token = getLine(s);
                
                if(token.equals("]"))
                {
                    //System.out.println(token + "Accept in D");
                    token = getLine(s);
                    
                    if(token.equals(";"))
                    {
                        //System.out.println(token + "Accept in D");
                        token = getLine(s);
                        
                        return "[" + var + "]";
                    }
                    else
                    {
                        System.out.print("REJECT");
                        //System.out.println(" in D" + token);
                        System.exit(0);
                        return "";
                    }
                }
                else
                {
                    System.out.print("REJECT");
                    //System.out.println(" in D" + token);
                    System.exit(0);
                    return "";
                }
            }
            else
            {
                System.out.print("REJECT");
                //System.out.println(" in D" + token);
                System.exit(0);
                return "";
            }
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in D" + token);
            System.exit(0);
            return "";
        }
    }
    public static String E(Scanner s)
    {
        if(token.equals("Keyword:int") || token.equals("Keyword:void") || token.equals("Keyword:float"))
        {
            //System.out.println(token + "Accept in E");
            String var = token.substring(8);
            token = getLine(s);
            
            return var;
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in E" + token);
            System.exit(0);
            return "";
        }
    }
    public static String G(Scanner s)
    {
        if(token.equals("Keyword:int") || token.equals("Keyword:float"))
        {
            //System.out.println(token + "Accept in G");
            token = getLine(s);
            
            if(token.matches("Identifier:.*"))
            {
                //System.out.println(token + "Accept in G");
                String var = token.substring(11);
                
                token = getLine(s);
                Iprime(s);
                String h = Hprime(s);

                return var + h;
            }
            else
            {
                System.out.print("REJECT");
                //System.out.println(" in G" + token);
                System.exit(0); 
                return "";
            }
        }
        else if(token.equals("Keyword:void"))
        {
            //System.out.println(token + "Accept in G");
            token = getLine(s);
            String g = Gprime(s);
            
            return "void";
         
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in G" + token);
            System.exit(0);
            return "";
        }
        
    }
    public static String Gprime(Scanner s)
    {
        if(token.matches("Identifier:.*"))
        {
            //System.out.println(token + "Accept in Gprime");
            String var = token.substring(11);
            
            token = getLine(s);
            Iprime(s);
            String h = Hprime(s);
            
            return var + h;
            
        }
        else if(token.equals(")"))
        {
            //System.out.println(token + "Empty generated in Gprime");
            return "";
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Gprime" + token);
            System.exit(0);
            return "";
        }
    }
    public static String Hprime(Scanner s)
    {
        if(token.equals(","))
        {
            //System.out.println(token + "Accept in Hprime");
            String comma = token;
            
            token = getLine(s);
            String i = I(s);
            String h = Hprime(s);
            
            return comma + i + h;
        }
        else if(token.equals(")"))
        {
            //System.out.println(token + "Empty generated in Hprime");
            return "";
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Hprime" + token);
            System.exit(0);
            return "";
        }
    }
    public static String I(Scanner s)
    {
        String e = E(s);
        
        if(token.matches("Identifier:.*"))
        {
            //System.out.println(token + "Accept in I");
            String var = token.substring(11);
            
            token = getLine(s);
            Iprime(s);
            
            return var;
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in I" + token);
            System.exit(0);
            return "";
        }
        
    }
    public static void Iprime(Scanner s)
    {
        if(token.equals("["))
        {
            //System.out.println(token + "Accept in Iprime");
            token = getLine(s);
            
            if(token.equals("]"))
            {
                //System.out.println(token + "Accept in Iprime");
                token = getLine(s);
            }
        }
        else if(token.equals(",") || token.equals(")"))
        {
            //System.out.println(token + "Empty generated in Iprime");
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Iprime" + token);
            System.exit(0);
        }
    }
    public static void J(Scanner s)
    {
        if(token.equals("{"))
        {
            //System.out.println(token + "Accept in J");
            token = getLine(s);
            
            K(s);
            L(s);
            
            if(token.equals("}"))
            {
                //System.out.println(token + "Accept in J");
                token = getLine(s);
            }
            else
            {
                System.out.print("REJECT");
                //System.out.println(" in J" + token);
                System.exit(0);
            }
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in J" + token);
            System.exit(0);
        }
    }
    public static void K(Scanner s)
    {
        if(token.equals("Keyword:int") || token.equals("Keyword:void") || token.equals("Keyword:float") || token.matches("Identifier:.*") || token.equals("}") || token.equals("(") || token.matches("Int:.*") || token.matches("Float:.*") || token.equals("Keyword:if") || token.equals("Keyword:while") || token.equals("Keyword:return") || token.equals(";") || token.equals("{"))
        {
            //System.out.println(token + "Accept in K");
            Kprime(s);
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in K" + token);
            System.exit(0);
        }
    }
    public static String Kprime(Scanner s)
    {
        if(token.equals("Keyword:int") || token.equals("Keyword:void") || token.equals("Keyword:float"))
        {
            String e = E(s);
            
            if(token.matches("Identifier:.*"))
            {
                //System.out.println(token + "Accept in Kprime");
                String var = token.substring(11);
                
                token = getLine(s);
                
                String d = D(s);
                String k = Kprime(s);
                
                //System.out.println(e + " var:" + var + " d:" + d +" cc:" + codeCount);
                
                if(!d.equals("") && d.substring(0,1).equals("["))
                {
                    String[] first = d.split("\\[");
                    String[] second = first[1].split("\\]");

                    int alloc = Integer.parseInt(second[0]);

                    code[codeCount][0] = "alloc";
                    code[codeCount][1] = "";
                    code[codeCount][2] = Integer.toString(alloc * 4);
                    code[codeCount][3] = var; 

                    codeCount++;
                }
                else
                {
                    code[codeCount][0] = "alloc";
                    code[codeCount][1] = "";
                    code[codeCount][2] = "4";
                    code[codeCount][3] = var; 

                    codeCount++;
                }
                
                return var + d + k;
            }
            else
            {
                System.out.print("REJECT");
                //System.out.println(" in Kprime" + token);
                System.exit(0);
                return "";
            }
        }
        else if(token.matches("Identifier:.*") || token.equals("{") || token.equals("(") || token.matches("Int:.*") || token.matches("Float:.*") || token.equals("Keyword:if") || token.equals("Keyword:while") || token.equals("Keyword:return") || token.equals(";") || token.equals("}"))
        {
            //System.out.println(token + "Empty generated in Kprime");
            return "";
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Kprime" + token);
            System.exit(0);
            return "";
        }
    }
    public static void L(Scanner s)
    {
        if(token.matches("Identifier:.*") || token.equals("}") || token.equals("(") || token.matches("Int:.*") || token.matches("Float:.*") || token.equals("Keyword:if") || token.equals("Keyword:while") || token.equals("Keyword:return") || token.equals(";") || token.equals("{"))
        {
            //System.out.println(token + "Accepted in L");
            Lprime(s);
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in L" + token);
            System.exit(0);
        }
    }
    public static void Lprime(Scanner s)
    {
        if(token.equals("}"))
        {
            //System.out.println(token + "Empty generated in Lprime");
        }
        else if(token.matches("Identifier:.*") || token.equals("(") || token.matches("Int:.*") || token.matches("Float:.*") || token.equals("Keyword:if") || token.equals("Keyword:while") || token.equals("Keyword:return") || token.equals(";") || token.equals("{"))
        {
            M(s);
            Lprime(s);
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Lprime" + token);
            System.exit(0);
        }
    }
    public static void M(Scanner s)
    {
        if(token.matches("Identifier:.*") || token.matches("Int:.*") || token.matches("Float:.*") || token.equals("(") || token.equals(";"))
        {
            //System.out.println(token + "Accepted in M");
            N(s);
        }
        else if(token.equals("Keyword:if"))
        {
            //System.out.println(token + "Accepted in M");
            O(s);
        }
        else if(token.equals("Keyword:while"))
        {
            //System.out.println(token + "Accepted in M");
            P(s);
        }
        else if(token.equals("Keyword:return"))
        {
            //System.out.println(token + "Accepted in M");
            Q(s);
        }
        else if(token.equals("{"))
        {
            //System.out.println(token + "Accepted in M");
            J(s);
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in M" + token);
            System.exit(0);
        }
    }
    public static void N(Scanner s)
    {
        if(token.matches("Identifier:.*") || token.matches("Int:.*") || token.matches("Float:.*") || token.equals("("))
        {
            //System.out.println(token + "Accepted in N");
            R(s);
            
            if(token.equals(";"))
            {
                //System.out.println(token + "Accepted in N");
                token = getLine(s);
            }
            else
            {
                System.out.print("REJECT");
                //System.out.println(" in N" + token);
                System.exit(0);
            }
        }
        else if(token.equals(";"))
        {
            //System.out.println(token + "Accepted in N");
            token = getLine(s);
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in N" + token);
            System.exit(0);
        }
    }
    public static void O(Scanner s)
    {
        if(token.equals("Keyword:if"))
        {
            //System.out.println(token + "Accepted in O");
            token = getLine(s);
            
            if(token.equals("("))
            {
                //System.out.println(token + "Accepted in O");
                token = getLine(s);
                
                String r = R(s);
                
                //========================================================
                String var1 = "";
                String var2 = "";
                String op = "";
                
                if(r.matches(".*\\>\\=.*"))
                {
                    String [] first = r.split("\\>");
                    String [] second = r.split("\\=");
                    
                    var1 = first[0];
                    var2 = second[1];
                    op = "BRL";
                }
                else if(r.matches(".*\\<\\=.*"))
                {
                    String [] first = r.split("\\<");
                    String [] second = r.split("\\=");
                    
                    var1 = first[0];
                    var2 = second[1];
                    op = "BRG";
                }
                else if(r.matches(".*\\>.*"))
                {
                    String [] first = r.split("\\>");
                    
                    var1 = first[0];
                    var2 = first[1];
                    op = "BRLEQ";
                }
                else if(r.matches(".*\\<.*"))
                {
                    String [] first = r.split("\\<");
                    
                    var1 = first[0];
                    var2 = first[1];
                    op = "BRGEQ";
                }
                else if(r.matches(".*\\=\\=.*"))
                {
                    String [] first = r.split("\\=");
                    
                    var1 = first[0];
                    var2 = first[2];
                    op = "BRNEQ";
                }
                else if(r.matches(".*\\!\\=.*"))
                {
                    String [] first = r.split("\\!");
                    String [] second = r.split("\\=");
                    
                    var1 = first[0];
                    var2 = second[1];
                    op = "BREQ";
                }
                
                int tval = tcount;
                
                code[codeCount][0] = "comp";
                code[codeCount][1] = var1;
                code[codeCount][2] = var2;
                code[codeCount][3] = "t" + String.valueOf(tcount); 

                codeCount++;
                tcount++;
                //========================================================
                
                if(token.equals(")"))
                {
                    int backpatch = codeCount;
                    
                    codeCount++;
                    
                    //System.out.println(token + "Accepted in O");
                    token = getLine(s);
                    
                    code[codeCount][0] = "block";
                    code[codeCount][1] = "";
                    code[codeCount][2] = "";
                    code[codeCount][3] = ""; 

                    codeCount++;
                    
                    M(s);
                    
                    code[codeCount][0] = "end";
                    code[codeCount][1] = "block";
                    code[codeCount][2] = "";
                    code[codeCount][3] = ""; 

                    codeCount++;
                    
                    code[backpatch][0] = op;
                    code[backpatch][1] = "t" + Integer.toString(tval);
                    code[backpatch][2] = "";
                    code[backpatch][3] = Integer.toString(codeCount+2); 
                    
                    Oprime(s);
                }
                else
                {
                    System.out.print("REJECT");
                    //System.out.println(" in O" + token);
                    System.exit(0);
                }
            } 
            else
            {
                System.out.print("REJECT");
                //System.out.println(" in O" + token);
                System.exit(0);
            }
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in O" + token);
            System.exit(0);
        }
    }
    public static void Oprime(Scanner s)
    {
        if(token.matches("Identifier:.*") || token.equals("}") || token.equals("(") || token.matches("Int:.*") || token.matches("Float:.*") || token.equals("Keyword:if") || token.equals("Keyword:while") || token.equals("Keyword:return") || token.equals(";") || token.equals("}"))
        {
            //System.out.println(token + "Empty generated in Oprime");
        }
        else if(token.equals("Keyword:else"))
        {
            //System.out.println(token + "accepted in Oprime");
            int backpatch = codeCount;
                    
            codeCount++;
            
            token = getLine(s);
            
            code[codeCount][0] = "block";
            code[codeCount][1] = "";
            code[codeCount][2] = "";
            code[codeCount][3] = ""; 

            codeCount++;
            
            M(s);
            
            code[codeCount][0] = "end";
            code[codeCount][1] = "block";
            code[codeCount][2] = "";
            code[codeCount][3] = ""; 

            codeCount++;
            
            code[backpatch][0] = "BR";
            code[backpatch][1] = "";
            code[backpatch][2] = "";
            code[backpatch][3] = Integer.toString(codeCount+1); 
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Oprime" + token);
            System.exit(0);
        }
    }
    public static void P(Scanner s)
    {
        if(token.equals("Keyword:while"))
        {
            //System.out.println(token + "Accepted in P");
            token = getLine(s);
            
            if(token.equals("("))
            {
                //System.out.println(token + "Accepted in P");
                token = getLine(s);
                
                int back = codeCount + 1;
                
                
                String r = R(s);
                
                //System.out.println(r);
                
                String var1 = "";
                String var2 = "";
                String op = "";
                
                if(r.matches(".*\\>\\=.*"))
                {
                    String [] first = r.split("\\>");
                    String [] second = r.split("\\=");
                    
                    var1 = first[0];
                    var2 = second[1];
                    op = "BRL";
                }
                else if(r.matches(".*\\<\\=.*"))
                {
                    String [] first = r.split("\\<");
                    String [] second = r.split("\\=");
                    
                    var1 = first[0];
                    var2 = second[1];
                    op = "BRG";
                }
                else if(r.matches(".*\\>.*"))
                {
                    String [] first = r.split("\\>");
                    
                    var1 = first[0];
                    var2 = first[1];
                    op = "BRLEQ";
                }
                else if(r.matches(".*\\<.*"))
                {
                    String [] first = r.split("\\<");
                    
                    var1 = first[0];
                    var2 = first[1];
                    op = "BRGEQ";
                }
                else if(r.matches(".*\\=\\=.*"))
                {
                    String [] first = r.split("\\=");
                    
                    var1 = first[0];
                    var2 = first[2];
                    op = "BRNEQ";
                }
                else if(r.matches(".*\\!\\=.*"))
                {
                    String [] first = r.split("\\!");
                    String [] second = r.split("\\=");
                    
                    var1 = first[0];
                    var2 = second[1];
                    op = "BREQ";
                }
                
                int tval = tcount;
                
                code[codeCount][0] = "comp";
                code[codeCount][1] = var1;
                code[codeCount][2] = var2;
                code[codeCount][3] = "t" + String.valueOf(tcount); 

                codeCount++;
                tcount++;
                
                if(token.equals(")"))
                {
                    //System.out.println(token + "Accepted in P");
                    int backpatch = codeCount;
                    
                    codeCount++;
                    
                    token = getLine(s);
                    
                    code[codeCount][0] = "block";
                    code[codeCount][1] = "";
                    code[codeCount][2] = "";
                    code[codeCount][3] = ""; 

                    codeCount++;
                    
                    M(s);
                    
                    code[codeCount][0] = "end";
                    code[codeCount][1] = "block";
                    code[codeCount][2] = "";
                    code[codeCount][3] = ""; 

                    codeCount++;
                    
                    //End Branch
                    
                    code[codeCount][0] = "BR";
                    code[codeCount][1] = "";
                    code[codeCount][2] = "";
                    code[codeCount][3] = Integer.toString(back); 

                    codeCount++;
                    
                    //Backpatch first branch
                    
                    code[backpatch][0] = op;
                    code[backpatch][1] = "t" + Integer.toString(tval);
                    code[backpatch][2] = "";
                    code[backpatch][3] = Integer.toString(codeCount+1); 
                }
                else
                {
                    System.out.print("REJECT");
                    //System.out.println(" in P" + token);
                    System.exit(0);
                }
            } 
            else
            {
                System.out.print("REJECT");
                //System.out.println(" in P" + token);
                System.exit(0);
            }
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in P" + token);
            System.exit(0);
        }
    }
    public static void Q(Scanner s)
    {
        if(token.equals("Keyword:return"))
        {
            //System.out.println(token + "Accepted in Q");
            token = getLine(s);
            
            String q = Qprime(s);
            
            //System.out.println(q);
            
            String var = q;
            
            if(q.matches("\\(.*\\)"))
            {
                var = q.substring(1, q.length() - 1);
            }
            
            code[codeCount][0] = "return";
            code[codeCount][1] = "";
            code[codeCount][2] = "";
            code[codeCount][3] = var;
                    
            codeCount++;
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Q" + token);
            System.exit(0);
        }
    }
    public static String Qprime(Scanner s)
    {
        if(token.matches("Identifier:.*") || token.matches("Int:.*") || token.matches("Float:.*") || token.equals("("))
        {
            //System.out.println(token + "Accepted in Qprime");
            String r = R(s);
            
            if(token.equals(";"))
            {
                //System.out.println(token + "Accepted in Qprime");
                token = getLine(s);
                return r;
            }
            else
            {
                System.out.print("REJECT");
                //System.out.println(" in Qprime" + token);
                System.exit(0);
                return "";
            }
        }
        else if(token.equals(";"))
        {
            //System.out.println(token + "Accepted in Qprime");
            token = getLine(s);
            
            return "";
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Qprime" + token);
            System.exit(0);
            return "";
        }
    }
    public static String R(Scanner s)
    {
        if(token.matches("Identifier:.*"))
        {
            //System.out.println(token + "Accepted in R");
            String var = token.substring(11);
            token = getLine(s);
            
            String rs = Rprime(s);
            
            //System.out.println(var + " | " + rs);
            
            if(rs.matches("\\+.*"))
            {
                /*code[codeCount][0] = "add";
                code[codeCount][1] = var;
                code[codeCount][2] = rs.substring(1);
                code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                codeCount++;
                tcount++;*/
                
                //==================================
                int index = -1;
                        for(int j=0; j<rs.length(); j++)
                        {
                            if(rs.charAt(j) == '<' || rs.charAt(j) == '>' || rs.charAt(j) == '!' || rs.charAt(j) == '=')
                            {
                                index = j;
                                break;
                            }
                        }
                        
                        if(index == -1)
                        {
                            code[codeCount][0] = "add";
                            code[codeCount][1] = var;
                            code[codeCount][2] = rs.substring(1);
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;
                            
                            return "t" + String.valueOf(tcount-1);
                        }
                        else
                        {
                            String vark = rs.substring(1, index);
                            
                            code[codeCount][0] = "add";
                            code[codeCount][1] = var;
                            code[codeCount][2] = vark;
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;

                            return "t" + String.valueOf(tcount-1) + rs.substring(index);
                        }
                //==================================
                
                
                //return "t" + String.valueOf(tcount - 1);
            }
            else if(rs.matches("\\-.*"))
            {
                /*code[codeCount][0] = "sub";
                code[codeCount][1] = var;
                code[codeCount][2] = rs.substring(1);
                code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                codeCount++;
                tcount++;*/
                
                //==================================
                int index = -1;
                        for(int j=0; j<rs.length(); j++)
                        {
                            if(rs.charAt(j) == '<' || rs.charAt(j) == '>' || rs.charAt(j) == '!' || rs.charAt(j) == '=')
                            {
                                index = j;
                                break;
                            }
                        }
                        
                        if(index == -1)
                        {
                            code[codeCount][0] = "sub";
                            code[codeCount][1] = var;
                            code[codeCount][2] = rs.substring(1);
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;
                            
                            return "t" + String.valueOf(tcount-1);
                        }
                        else
                        {
                            String vark = rs.substring(1, index);
                            
                            code[codeCount][0] = "sub";
                            code[codeCount][1] = var;
                            code[codeCount][2] = vark;
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;

                            return "t" + String.valueOf(tcount-1) + rs.substring(index);
                        }
                //==================================
                
                
                //return "t" + String.valueOf(tcount - 1);
            }
            else if(rs.matches("\\*.*"))
            {
               /* code[codeCount][0] = "mult";
                code[codeCount][1] = var;
                code[codeCount][2] = rs.substring(1);
                code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                codeCount++;*/
                tcount++;
                
                //==================================
                int index = -1;
                        for(int j=0; j<rs.length(); j++)
                        {
                            if(rs.charAt(j) == '<' || rs.charAt(j) == '>' || rs.charAt(j) == '!' || rs.charAt(j) == '=')
                            {
                                index = j;
                                break;
                            }
                        }
                        
                        if(index == -1)
                        {
                            code[codeCount][0] = "mult";
                            code[codeCount][1] = var;
                            code[codeCount][2] = rs.substring(1);
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;
                            
                            return "t" + String.valueOf(tcount-1);
                        }
                        else
                        {
                            String vark = rs.substring(1, index);
                            
                            code[codeCount][0] = "mult";
                            code[codeCount][1] = var;
                            code[codeCount][2] = vark;
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;

                            return "t" + String.valueOf(tcount-1) + rs.substring(index);
                        }
                //==================================
                
                
                //return "t" + String.valueOf(tcount - 1);
            }
            else if(rs.matches("\\/.*"))
            {
                /*code[codeCount][0] = "div";
                code[codeCount][1] = var;
                code[codeCount][2] = rs.substring(1);
                code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                codeCount++;
                tcount++;*/
                
                //==================================
                int index = -1;
                        for(int j=0; j<rs.length(); j++)
                        {
                            if(rs.charAt(j) == '<' || rs.charAt(j) == '>' || rs.charAt(j) == '!' || rs.charAt(j) == '=')
                            {
                                index = j;
                                break;
                            }
                        }
                        
                        if(index == -1)
                        {
                            code[codeCount][0] = "div";
                            code[codeCount][1] = var;
                            code[codeCount][2] = rs.substring(1);
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;
                            
                            return "t" + String.valueOf(tcount-1);
                        }
                        else
                        {
                            String vark = rs.substring(1, index);
                            
                            code[codeCount][0] = "div";
                            code[codeCount][1] = var;
                            code[codeCount][2] = vark;
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;

                            return "t" + String.valueOf(tcount-1) + rs.substring(index);
                        }
                //==================================

            }
            else if(rs.matches("\\=.*") && !rs.matches("\\=\\=.*"))
            {
                code[codeCount][0] = "assgn";
                code[codeCount][1] = rs.substring(1);
                code[codeCount][2] = "";
                code[codeCount][3] = var;
                    
                codeCount++;               
                
                return "";
            }
            else if(rs.matches("\\[.*"))
            {
                String[] first = rs.split("\\[");
                String[] second = first[1].split("\\]");
                
                String vari = second[0];

                if(vari.matches("[0-9]*"))
                {
                    int varia = Integer.parseInt(vari);
                    int disp = varia * 4;
                    
                    code[codeCount][0] = "disp";
                    code[codeCount][1] = var;
                    code[codeCount][2] = Integer.toString(disp);
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                }
                else
                {
                    code[codeCount][0] = "mult";
                    code[codeCount][1] = vari;
                    code[codeCount][2] = "4";
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                    
                    String tvar = "t" + String.valueOf(tcount-1);
                    
                    code[codeCount][0] = "disp";
                    code[codeCount][1] = var;
                    code[codeCount][2] = tvar;
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                }
                
                
                //============================================================
                if(rs.charAt(rs.length()-1) != ']')
                {
                    String[] pSplit = rs.split("\\]");
                    String rrs = pSplit[1];
                    
                    if((rrs.charAt(0) == '<' || rrs.charAt(0) == '>' || rrs.charAt(0) == '!' || (rrs.charAt(0) == '=' && rrs.charAt(1) == '=')))
                    {
                        return "t" + String.valueOf(tcount-1) + rrs;
                    }
                    
                    String rest = "";
                    if(rrs.matches("\\=.*"))
                    {
                        code[codeCount][0] = "assgn";
                        code[codeCount][1] = rrs.substring(1);
                        code[codeCount][2] = "";
                        code[codeCount][3] = "t" + String.valueOf(tcount - 1);

                        codeCount++;
                        tcount++;
                    }
                    
                    
                    //Possiblity 1 of 4
                    if(rrs.matches("\\*.*\\-.*"))
                    {
                        String[] w = rrs.split("\\-");
                        String[] x = w[0].split("\\*");
                        
                        String varb = x[1];
                        
                        code[codeCount][0] = "mult";
                        code[codeCount][1] = "t" + String.valueOf(tcount-1);
                        code[codeCount][2] = varb;
                        code[codeCount][3] = "t" + String.valueOf(tcount);

                        codeCount++;
                        tcount++;
                        
                        int index = -1;
                        for(int j=0; j<w[1].length(); j++)
                        {
                            if(w[1].charAt(j) == '<' || w[1].charAt(j) == '>' || w[1].charAt(j) == '!' || w[1].charAt(j) == '=')
                            {
                                index = j;
                                break;
                            }
                        }
                        
                        if(index == -1)
                        {
                            code[codeCount][0] = "sub";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = w[1];
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;
                            
                            return "t" + String.valueOf(tcount-1);
                        }
                        else
                        {
                            String vark = w[1].substring(0, index);
                            
                            code[codeCount][0] = "sub";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = vark;
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;

                            return "t" + String.valueOf(tcount-1) + w[1].substring(index);
                        }
                    }
                    
                    //Possiblity 2 of 4
                    if(rrs.matches("\\*.*\\+.*"))
                    {
                        String[] w = rrs.split("\\+");
                        String[] x = w[0].split("\\*");
                        
                        String varb = x[1];
                        
                        code[codeCount][0] = "mult";
                        code[codeCount][1] = "t" + String.valueOf(tcount-1);
                        code[codeCount][2] = varb;
                        code[codeCount][3] = "t" + String.valueOf(tcount);

                        codeCount++;
                        tcount++;
                        
                        int index = -1;
                        for(int j=0; j<w[1].length(); j++)
                        {
                            if(w[1].charAt(j) == '<' || w[1].charAt(j) == '>' || w[1].charAt(j) == '!' || w[1].charAt(j) == '=')
                            {
                                index = j;
                                break;
                            }
                        }
                        
                        if(index == -1)
                        {
                            code[codeCount][0] = "add";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = w[1];
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;
                            
                            return "t" + String.valueOf(tcount-1);
                        }
                        else
                        {
                            String vark = w[1].substring(0, index);
                            
                            code[codeCount][0] = "add";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = vark;
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;

                            return "t" + String.valueOf(tcount-1) + w[1].substring(index);
                        }
                    }
                    
                    //Possiblity 3 of 4
                    if(rrs.matches("\\/.*\\+.*"))
                    {
                        String[] w = rrs.split("\\+");
                        String[] x = w[0].split("\\/");
                        
                        String varb = x[1];
                        
                        code[codeCount][0] = "div";
                        code[codeCount][1] = "t" + String.valueOf(tcount-1);
                        code[codeCount][2] = varb;
                        code[codeCount][3] = "t" + String.valueOf(tcount);

                        codeCount++;
                        tcount++;
                        
                        int index = -1;
                        for(int j=0; j<w[1].length(); j++)
                        {
                            if(w[1].charAt(j) == '<' || w[1].charAt(j) == '>' || w[1].charAt(j) == '!' || w[1].charAt(j) == '=')
                            {
                                index = j;
                                break;
                            }
                        }
                        
                        if(index == -1)
                        {
                            code[codeCount][0] = "add";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = w[1];
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;
                            
                            return "t" + String.valueOf(tcount-1);
                        }
                        else
                        {
                            String vark = w[1].substring(0, index);
                            
                            code[codeCount][0] = "add";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = vark;
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;

                            return "t" + String.valueOf(tcount-1) + w[1].substring(index);
                        }
                    }
                    
                    //Possiblity 4 of 4
                    if(rrs.matches("\\/.*\\-.*"))
                    {
                        String[] w = rrs.split("\\-");
                        String[] x = w[0].split("\\/");
                        
                        String varb = x[1];
                        
                        code[codeCount][0] = "div";
                        code[codeCount][1] = "t" + String.valueOf(tcount-1);
                        code[codeCount][2] = varb;
                        code[codeCount][3] = "t" + String.valueOf(tcount);

                        codeCount++;
                        tcount++;
                        
                        int index = -1;
                        for(int j=0; j<w[1].length(); j++)
                        {
                            if(w[1].charAt(j) == '<' || w[1].charAt(j) == '>' || w[1].charAt(j) == '!' || w[1].charAt(j) == '=')
                            {
                                index = j;
                                break;
                            }
                        }
                        
                        if(index == -1)
                        {
                            code[codeCount][0] = "sub";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = w[1];
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;
                            
                            return "t" + String.valueOf(tcount-1);
                        }
                        else
                        {
                            String vark = w[1].substring(0, index);
                            
                            code[codeCount][0] = "sub";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = vark;
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;

                            return "t" + String.valueOf(tcount-1) + w[1].substring(index);
                        }
                    }
                    
                    if(rrs.matches("\\+.*"))
                    {
                        String[] w = rrs.split("\\+");
                        
                        int index = -1;
                        for(int j=0; j<w[1].length(); j++)
                        {
                            if(w[1].charAt(j) == '<' || w[1].charAt(j) == '>' || w[1].charAt(j) == '!' || w[1].charAt(j) == '=')
                            {
                                index = j;
                                break;
                            }
                        }
                        
                        if(index == -1)
                        {
                            code[codeCount][0] = "add";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = w[1];
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;
                            
                            return "t" + String.valueOf(tcount-1);
                        }
                        else
                        {
                            String varb = w[1].substring(0, index);
                            
                            code[codeCount][0] = "add";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = varb;
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;

                            return "t" + String.valueOf(tcount-1) + w[1].substring(index);
                        }
                    }
                    
                    if(rrs.matches("\\-.*"))
                    {
                        String[] w = rrs.split("\\-");
                        
                        int index = -1;
                        for(int j=0; j<w[1].length(); j++)
                        {
                            if(w[1].charAt(j) == '<' || w[1].charAt(j) == '>' || w[1].charAt(j) == '!' || w[1].charAt(j) == '=')
                            {
                                index = j;
                                break;
                            }
                        }
                        
                        if(index == -1)
                        {
                            code[codeCount][0] = "sub";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = w[1];
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;
                            
                            return "t" + String.valueOf(tcount-1);
                        }
                        else
                        {
                            String varb = w[1].substring(0, index);
                            
                            code[codeCount][0] = "sub";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = varb;
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;

                            return "t" + String.valueOf(tcount-1) + w[1].substring(index);
                        }
                    }
                    
                }
                //============================================================
                
                
                return "t" + String.valueOf(tcount - 1);
            }
            else if(rs.matches("\\(.*"))
            {
                String[] split = rs.split("\\(");
                
                int parmcount = 0;
                
                if(split[1].substring(0,1).equals(")"))
                {
                    parmcount = 0;
                }
                else
                {
                    char [] parms = split[1].toCharArray();
                    int commaCount = 0;
                    
                    for(int i=0; i<parms.length; i++)
                    {
                        if(parms[i] == ',')
                        {
                            commaCount++;
                        }
                    }
                    
                    parmcount = commaCount + 1;
                }
                
                code[codeCount][0] = "call";
                code[codeCount][1] = var;
                code[codeCount][2] = Integer.toString(parmcount);
                code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                codeCount++;
                tcount++;
                
                //System.out.println(rs.charAt(rs.length()-1) != ')');

                if(rs.charAt(rs.length()-1) != ')')
                {
                    String[] pSplit = rs.split("\\)");
                    String rrs = pSplit[1];
                    
                    //System.out.println(rrs);
                    
                    if((rrs.charAt(0) == '<' || rrs.charAt(0) == '>' || rrs.charAt(0) == '!' || rrs.charAt(0) == '='))
                    {
                        //System.out.println("t" + String.valueOf(tcount-1) + rrs); 
                        return "t" + String.valueOf(tcount-1) + rrs;
                    }
                    
                    
                    String rest = "";
                    
                    //Possiblity 1 of 4
                    if(rrs.matches("\\*.*\\-.*"))
                    {
                        String[] w = rrs.split("\\-");
                        String[] x = w[0].split("\\*");
                        
                        String varb = x[1];
                        
                        code[codeCount][0] = "mult";
                        code[codeCount][1] = "t" + String.valueOf(tcount-1);
                        code[codeCount][2] = varb;
                        code[codeCount][3] = "t" + String.valueOf(tcount);

                        codeCount++;
                        tcount++;
                        
                        int index = -1;
                        for(int j=0; j<w[1].length(); j++)
                        {
                            if(w[1].charAt(j) == '<' || w[1].charAt(j) == '>' || w[1].charAt(j) == '!' || w[1].charAt(j) == '=')
                            {
                                index = j;
                                break;
                            }
                        }
                        
                        if(index == -1)
                        {
                            code[codeCount][0] = "sub";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = w[1];
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;
                            
                            return "t" + String.valueOf(tcount-1);
                        }
                        else
                        {
                            String vari = w[1].substring(0, index);
                            
                            code[codeCount][0] = "sub";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = vari;
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;

                            return "t" + String.valueOf(tcount-1) + w[1].substring(index);
                        }
                    }
                    
                    //Possiblity 2 of 4
                    if(rrs.matches("\\*.*\\+.*"))
                    {
                        String[] w = rrs.split("\\+");
                        String[] x = w[0].split("\\*");
                        
                        String varb = x[1];
                        
                        code[codeCount][0] = "mult";
                        code[codeCount][1] = "t" + String.valueOf(tcount-1);
                        code[codeCount][2] = varb;
                        code[codeCount][3] = "t" + String.valueOf(tcount);

                        codeCount++;
                        tcount++;
                        
                        int index = -1;
                        for(int j=0; j<w[1].length(); j++)
                        {
                            if(w[1].charAt(j) == '<' || w[1].charAt(j) == '>' || w[1].charAt(j) == '!' || w[1].charAt(j) == '=')
                            {
                                index = j;
                                break;
                            }
                        }
                        
                        if(index == -1)
                        {
                            code[codeCount][0] = "add";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = w[1];
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;
                            
                            return "t" + String.valueOf(tcount-1);
                        }
                        else
                        {
                            String vari = w[1].substring(0, index);
                            
                            code[codeCount][0] = "add";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = vari;
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;

                            return "t" + String.valueOf(tcount-1) + w[1].substring(index);
                        }
                    }
                    
                    //Possiblity 3 of 4
                    if(rrs.matches("\\/.*\\+.*"))
                    {
                        String[] w = rrs.split("\\+");
                        String[] x = w[0].split("\\/");
                        
                        String varb = x[1];
                        
                        code[codeCount][0] = "div";
                        code[codeCount][1] = "t" + String.valueOf(tcount-1);
                        code[codeCount][2] = varb;
                        code[codeCount][3] = "t" + String.valueOf(tcount);

                        codeCount++;
                        tcount++;
                        
                        int index = -1;
                        for(int j=0; j<w[1].length(); j++)
                        {
                            if(w[1].charAt(j) == '<' || w[1].charAt(j) == '>' || w[1].charAt(j) == '!' || w[1].charAt(j) == '=')
                            {
                                index = j;
                                break;
                            }
                        }
                        
                        if(index == -1)
                        {
                            code[codeCount][0] = "add";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = w[1];
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;
                            
                            return "t" + String.valueOf(tcount-1);
                        }
                        else
                        {
                            String vari = w[1].substring(0, index);
                            
                            code[codeCount][0] = "add";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = vari;
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;

                            return "t" + String.valueOf(tcount-1) + w[1].substring(index);
                        }
                    }
                    
                    //Possiblity 4 of 4
                    if(rrs.matches("\\/.*\\-.*"))
                    {
                        String[] w = rrs.split("\\-");
                        String[] x = w[0].split("\\/");
                        
                        String varb = x[1];
                        
                        code[codeCount][0] = "div";
                        code[codeCount][1] = "t" + String.valueOf(tcount-1);
                        code[codeCount][2] = varb;
                        code[codeCount][3] = "t" + String.valueOf(tcount);

                        codeCount++;
                        tcount++;
                        
                        int index = -1;
                        for(int j=0; j<w[1].length(); j++)
                        {
                            if(w[1].charAt(j) == '<' || w[1].charAt(j) == '>' || w[1].charAt(j) == '!' || w[1].charAt(j) == '=')
                            {
                                index = j;
                                break;
                            }
                        }
                        
                        if(index == -1)
                        {
                            code[codeCount][0] = "sub";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = w[1];
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;
                            
                            return "t" + String.valueOf(tcount-1);
                        }
                        else
                        {
                            String vari = w[1].substring(0, index);
                            
                            code[codeCount][0] = "sub";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = vari;
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;

                            return "t" + String.valueOf(tcount-1) + w[1].substring(index);
                        }
                    }
                    
                    if(rrs.matches("\\+.*"))
                    {
                        //System.out.println("hi");
                        
                        String[] w = rrs.split("\\+");
                        
                        int index = -1;
                        for(int j=0; j<w[1].length(); j++)
                        {
                            if(w[1].charAt(j) == '<' || w[1].charAt(j) == '>' || w[1].charAt(j) == '!' || w[1].charAt(j) == '=')
                            {
                                index = j;
                                break;
                            }
                        }
                        
                        if(index == -1)
                        {
                            code[codeCount][0] = "add";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = w[1];
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;
                            
                            return "t" + String.valueOf(tcount-1);
                        }
                        else
                        {
                            String vari = w[1].substring(0, index);
                            
                            code[codeCount][0] = "add";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = vari;
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;

                            return "t" + String.valueOf(tcount-1) + w[1].substring(index);
                        }
                    }
                    
                    if(rrs.matches("\\-.*"))
                    {
                        //System.out.println("hi");
                        
                        String[] w = rrs.split("\\-");
                        
                        int index = -1;
                        for(int j=0; j<w[1].length(); j++)
                        {
                            if(w[1].charAt(j) == '<' || w[1].charAt(j) == '>' || w[1].charAt(j) == '!' || w[1].charAt(j) == '=')
                            {
                                index = j;
                                break;
                            }
                        }
                        
                        if(index == -1)
                        {
                            code[codeCount][0] = "sub";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = w[1];
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;
                            
                            return "t" + String.valueOf(tcount-1);
                        }
                        else
                        {
                            String vari = w[1].substring(0, index);
                            
                            code[codeCount][0] = "sub";
                            code[codeCount][1] = "t" + String.valueOf(tcount-1);
                            code[codeCount][2] = vari;
                            code[codeCount][3] = "t" + String.valueOf(tcount);

                            codeCount++;
                            tcount++;

                            return "t" + String.valueOf(tcount-1) + w[1].substring(index);
                        }
                    }
                    
                }
                    
                return "t" + String.valueOf(tcount-1);
            }
            
            return var+rs;
        }
        else if(token.equals("("))
        {
            //System.out.println(token + "Accepted in R");
            String var1 = token;
            token = getLine(s);
            
            String r = R(s);
            
            if(token.equals(")"))
            {
                //System.out.println(token + "Accepted in R");
                String var2 = token;
                token = getLine(s);
                
                String var = r;
                
                String x = Xprime(s);
                String v = Vprime(s);
                String t = Tprime(s);
                
                System.out.println("Var:" + r + " X:" + x + " V:" + v + " T:" + t + " in R");
                
                //=================================================================================================
                String temp1 = "";
                String temp2 = "";

                if(v.matches("\\*.*"))
                {
                    code[codeCount][0] = "mult";
                    code[codeCount][1] = x.substring(1);
                    code[codeCount][2] = v.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);

                    codeCount++;
                    tcount++;

                    temp2 = x.substring(0,1) + "t" + String.valueOf(tcount - 1);
                }
                else if(v.matches("\\/.*"))
                {
                    code[codeCount][0] = "div";
                    code[codeCount][1] = x.substring(1);
                    code[codeCount][2] = v.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);

                    codeCount++;
                    tcount++;

                    temp2 = x.substring(0,1) + "t" + String.valueOf(tcount - 1);
                }

                if(!temp2.equals(""))
                {
                    if(temp2.matches("\\*.*"))
                    {
                        code[codeCount][0] = "mult";
                        code[codeCount][1] = var;
                        code[codeCount][2] = temp2.substring(1);
                        code[codeCount][3] = "t" + String.valueOf(tcount);

                        codeCount++;
                        tcount++;

                        return "t" + String.valueOf(tcount - 1) + t;
                    }
                    else if(temp2.matches("\\/.*"))
                    {
                        code[codeCount][0] = "div";
                        code[codeCount][1] = var;
                        code[codeCount][2] = temp2.substring(1);
                        code[codeCount][3] = "t" + String.valueOf(tcount);

                        codeCount++;
                        tcount++;

                        return "t" + String.valueOf(tcount - 1) + t;
                    }
                    else if(temp2.matches("\\-.*"))
                    {
                        code[codeCount][0] = "sub";
                        code[codeCount][1] = var;
                        code[codeCount][2] = temp2.substring(1);
                        code[codeCount][3] = "t" + String.valueOf(tcount);

                        codeCount++;
                        tcount++;

                        return "t" + String.valueOf(tcount - 1) + t;
                    }
                    else if(temp2.matches("\\+.*"))
                    {
                        code[codeCount][0] = "add";
                        code[codeCount][1] = var;
                        code[codeCount][2] = temp2.substring(1);
                        code[codeCount][3] = "t" + String.valueOf(tcount);

                        codeCount++;
                        tcount++;

                        return "t" + String.valueOf(tcount - 1) + t;
                    }
                }

                if(x.matches("\\*.*"))
                {
                    code[codeCount][0] = "mult";
                    code[codeCount][1] = var;
                    code[codeCount][2] = x.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);

                    codeCount++;
                    tcount++;

                    temp1 = "t" + String.valueOf(tcount - 1);
                }
                else if(x.matches("\\/.*"))
                {
                    code[codeCount][0] = "div";
                    code[codeCount][1] = var;
                    code[codeCount][2] = x.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);

                    codeCount++;
                    tcount++;

                    temp1 = "t" + String.valueOf(tcount - 1);
                }

                if(!temp1.equals(""))
                {
                    if(v.matches("\\-.*"))
                    {
                        code[codeCount][0] = "sub";
                        code[codeCount][1] = temp1;
                        code[codeCount][2] = v.substring(1);
                        code[codeCount][3] = "t" + String.valueOf(tcount);

                        codeCount++;
                        tcount++;

                        return "t" + String.valueOf(tcount - 1) + t;
                    }
                    else if(v.matches("\\+.*"))
                    {
                        code[codeCount][0] = "add";
                        code[codeCount][1] = temp1;
                        code[codeCount][2] = v.substring(1);
                        code[codeCount][3] = "t" + String.valueOf(tcount);

                        codeCount++;
                        tcount++;

                        return "t" + String.valueOf(tcount - 1) + t;
                    }
                }
                
                if(x.matches("\\*.*"))
                {
                    return "t" + String.valueOf(tcount - 1) + t;
                }
                else if(x.matches("\\/.*"))
                {
                    return "t" + String.valueOf(tcount - 1) + t;
                }

                if(v.matches("\\+.*"))
                {
                    code[codeCount][0] = "add";
                    code[codeCount][1] = var;
                    code[codeCount][2] = v.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);

                    codeCount++;
                    tcount++;

                    return "t" + String.valueOf(tcount - 1) + t;
                }
                else if(v.matches("\\-.*"))
                {
                    code[codeCount][0] = "sub";
                    code[codeCount][1] = var;
                    code[codeCount][2] = v.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);

                    codeCount++;
                    tcount++;

                    return "t" + String.valueOf(tcount - 1) + t;
                }
                
                
                //=================================================================================================
            
                
                //System.out.println(t);
                
                return r + x + v + t;
            }
            else
            {
                System.out.print("REJECT");
                //System.out.println(" in R" + token);
                System.exit(0);
                return "";
            }
        }
        else if(token.matches("Int:.*") || token.matches("Float:.*"))
        {
            //System.out.println(token + "Accepted in R");
            String var = "";
            if(token.matches("Int:.*"))
            {
                var = token.substring(4);
            }
            else if(token.matches("Float:.*"))
            {
                var = token.substring(6);
            }
                    
            token = getLine(s);
            
            String x = Xprime(s);
            String v = Vprime(s);
            String t = Tprime(s);
            
            //System.out.println("Var:" + var + " X:" + x + " V:" + v + " T:" + t + " in R");
            
            String temp1 = "";
            String temp2 = "";
            
            if(v.matches("\\*.*"))
            {
                code[codeCount][0] = "mult";
                code[codeCount][1] = x.substring(1);
                code[codeCount][2] = v.substring(1);
                code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                codeCount++;
                tcount++;
                
                temp2 = x.substring(0,1) + "t" + String.valueOf(tcount - 1);
            }
            else if(v.matches("\\/.*"))
            {
                code[codeCount][0] = "div";
                code[codeCount][1] = x.substring(1);
                code[codeCount][2] = v.substring(1);
                code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                codeCount++;
                tcount++;
                
                temp2 = x.substring(0,1) + "t" + String.valueOf(tcount - 1);
            }
            
            if(!temp2.equals(""))
            {
                if(temp2.matches("\\*.*"))
                {
                    code[codeCount][0] = "mult";
                    code[codeCount][1] = var;
                    code[codeCount][2] = temp2.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                    
                    return "t" + String.valueOf(tcount - 1) + t;
                }
                else if(temp2.matches("\\/.*"))
                {
                    code[codeCount][0] = "div";
                    code[codeCount][1] = var;
                    code[codeCount][2] = temp2.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                    
                    return "t" + String.valueOf(tcount - 1) + t;
                }
                else if(temp2.matches("\\-.*"))
                {
                    code[codeCount][0] = "sub";
                    code[codeCount][1] = var;
                    code[codeCount][2] = temp2.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                    
                    return "t" + String.valueOf(tcount - 1) + t;
                }
                else if(temp2.matches("\\+.*"))
                {
                    code[codeCount][0] = "add";
                    code[codeCount][1] = var;
                    code[codeCount][2] = temp2.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                    
                    return "t" + String.valueOf(tcount - 1) + t;
                }
            }
            
            if(x.matches("\\*.*"))
            {
                code[codeCount][0] = "mult";
                code[codeCount][1] = var;
                code[codeCount][2] = x.substring(1);
                code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                codeCount++;
                tcount++;
                
                temp1 = "t" + String.valueOf(tcount - 1);
            }
            else if(x.matches("\\/.*"))
            {
                code[codeCount][0] = "div";
                code[codeCount][1] = var;
                code[codeCount][2] = x.substring(1);
                code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                codeCount++;
                tcount++;
                
                temp1 = "t" + String.valueOf(tcount - 1);
            }

            if(!temp1.equals(""))
            {
                if(v.matches("\\-.*"))
                {
                    code[codeCount][0] = "sub";
                    code[codeCount][1] = temp1;
                    code[codeCount][2] = v.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                    
                    return "t" + String.valueOf(tcount - 1) + t;
                }
                else if(v.matches("\\+.*"))
                {
                    code[codeCount][0] = "add";
                    code[codeCount][1] = temp1;
                    code[codeCount][2] = v.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                    
                    return "t" + String.valueOf(tcount - 1) + t;
                }
            }
            
            if(x.matches("\\*.*"))
                {
                    return "t" + String.valueOf(tcount - 1) + t;
                }
                else if(x.matches("\\/.*"))
                {
                    return "t" + String.valueOf(tcount - 1) + t;
                }
            
            if(v.matches("\\+.*"))
            {
                code[codeCount][0] = "add";
                code[codeCount][1] = var;
                code[codeCount][2] = v.substring(1);
                code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                codeCount++;
                tcount++;
                
                return "t" + String.valueOf(tcount - 1) + t;
            }
            else if(v.matches("\\-.*"))
            {
                code[codeCount][0] = "sub";
                code[codeCount][1] = var;
                code[codeCount][2] = v.substring(1);
                code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                codeCount++;
                tcount++;
                
                return "t" + String.valueOf(tcount - 1) + t;
            }
            
            //System.out.println(t);
            
            return var + x + v + t;
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in R" + token);
            System.exit(0);
            return "";
        }
    }
    public static String Rprime(Scanner s)
    {
        if(token.equals(",") || token.equals(";") || token.equals(")") || token.equals("]") || token.equals("*") || token.equals("/") || token.equals("+") || token.equals("-") || token.equals(">") || token.equals("<") || token.equals(">=") || token.equals("<=") || token.equals("==") || token.equals("!="))
        {
            //System.out.println(token + "Accepted in Rprime");
            
            String x = Xprime(s);
            String v = Vprime(s);
            String t = Tprime(s);
            
            //System.out.println(x + v + t);
            if((x.matches("\\*.*") || x.matches("\\+.*")) && v.matches("\\*.*"))
            {
                code[codeCount][0] = "mult";
                code[codeCount][1] = x.substring(1);
                code[codeCount][2] = v.substring(1);
                code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                codeCount++;
                tcount++;
                
                
                return x.substring(0,1) + "t" + String.valueOf(tcount - 1);
            }
            else if((x.matches("\\*.*") || x.matches("\\+.*")) && v.matches("\\/.*"))
            {
                code[codeCount][0] = "div";
                code[codeCount][1] = x.substring(1);
                code[codeCount][2] = v.substring(1);
                code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                codeCount++;
                tcount++;
                
                
                return x.substring(0,1) + "t" + String.valueOf(tcount - 1);
            }
            if((x.matches("\\*.*") || x.matches("\\+.*"))&& v.matches("\\+.*"))
            {
                code[codeCount][0] = "add";
                code[codeCount][1] = x.substring(1);
                code[codeCount][2] = v.substring(1);
                code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                codeCount++;
                tcount++;
                
                
                return x.substring(0,1) + "t" + String.valueOf(tcount - 1);
            }
            else if((x.matches("\\*.*") || x.matches("\\+.*"))&& v.matches("\\-.*"))
            {
                code[codeCount][0] = "sub";
                code[codeCount][1] = x.substring(1);
                code[codeCount][2] = v.substring(1);
                code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                codeCount++;
                tcount++;
                
                
                return x.substring(0,1) + "t" + String.valueOf(tcount - 1);
            }
            
            //System.out.println(t); here
            
            
            return x + v + t;
        }
        else if(token.equals("("))
        {
            //System.out.println(token + "Accepted in Rprime");
            String var1 = token;
            token = getLine(s);
            
            Sigma(s);
            
            if(token.equals(")"))
            {
                //System.out.println(token + "Accepted in Rprime");
                String var2 = token;
                token = getLine(s);
                
                String x = Xprime(s);
                String v = Vprime(s);
                String t = Tprime(s);
                
                //System.out.println(t);
                
                return var1 + "Fix this" + var2 + x + v + t;
            }
            else
            {
                System.out.print("REJECT");
                //System.out.println(" in Rprime" + token);
                System.exit(0);
                return "";
            }
        }
        else if(token.equals("["))
        {
            String var1 = token;
            //System.out.println(token + "Accepted in Rprime");
            token = getLine(s);
            
            String r = R(s);
            
            //System.out.println(r + " Rprime");
            
            if(token.equals("]"))
            {
                String var2 = token;
                //System.out.println(token + "Accepted in Rprime");
                token = getLine(s);
                
                String rr = Rdoubleprime(s);
                
                return var1 + r + var2 + rr;
            }
            else
            {
                System.out.print("REJECT");
                //System.out.println(" in Rprime" + token);
                System.exit(0);
                return "";
            }
            
        }
        else if(token.equals("="))
        {
            //System.out.println(token + "Accepted in Rprime");
            String var = token;
            token = getLine(s);
            
            String r = R(s);
            
            //System.out.println(var + r);
            
            return var + r;
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Rprime" + token);
            System.exit(0);
            return "";
        }
    }
    public static String Rdoubleprime(Scanner s)
    {
        if(token.equals(",") || token.equals(";") || token.equals(")") || token.equals("]") || token.equals("*") || token.equals("/") || token.equals("+") || token.equals("-") || token.equals(">") || token.equals("<") || token.equals(">=") || token.equals("<=") || token.equals("==") || token.equals("!="))
        {
            //System.out.println(token + "Accepted in Rdoubleprime");
            
            String x = Xprime(s);
            String v = Vprime(s);
            String t = Tprime(s);
            
            //System.out.println(t);
            
            return x+v+t;
        }
        else if(token.equals("="))
        {
            //System.out.println(token + "Accepted in Rdoubleprime");
            String var = token;
            token = getLine(s);
            
            String rs = R(s);
            
            return var + rs;
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Rdoubleprime" + token);
            System.exit(0);
            return "";
        }
    }
    public static String Tprime(Scanner s)
    {
        if(token.equals(",") || token.equals(";") || token.equals(")") || token.equals("]"))
        {
            //System.out.println(token + "Empty generated in Tprime");
            return "";
        }
        else if(token.equals("<") || token.equals(">") || token.equals("<=") || token.equals(">=") || token.equals("==") || token.equals("!="))
        {
            //System.out.println(token + "Accepted in Tprime");
            
            String op = U(s);
            String rs = V(s);
            
            //System.out.println(op + rs);
            
            return op + rs;
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Tprime" + token);
            System.exit(0);
            return "";
        }
    }
    public static String U(Scanner s)
    {
        if(token.equals("<") || token.equals(">") || token.equals("<=") || token.equals(">=") || token.equals("==") || token.equals("!="))
        {
            //System.out.println(token + "Accepted in U");
            String var = token;
            token = getLine(s);
            return var;
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in U" + token);
            System.exit(0);
            return "";
        }
    }
    public static String V(Scanner s)
    {
        if(token.matches("Identifier:.*") || token.matches("Int:.*") || token.matches("Float:.*") || token.equals("("))
        {
            //System.out.println(token + "Accepted in V");
            
            String ls = X(s);
            String rs = Vprime(s);
            
            //System.out.println(ls + rs + " v");
            
            if(!rs.equals(""))
            {
                if(rs.substring(0,1).equals("+"))
                {
                    code[codeCount][0] = "add";
                    code[codeCount][1] = ls;
                    code[codeCount][2] = rs.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                }
                else if(rs.substring(0,1).equals("-"))
                {
                    code[codeCount][0] = "sub";
                    code[codeCount][1] = ls;
                    code[codeCount][2] = rs.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                }
                
                return "t" + String.valueOf(tcount - 1);
            }
            else
            {
                return ls;
            }
            
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in V" + token);
            System.exit(0);
            return "";
        }
    }
    public static String Vprime(Scanner s)
    {
        if(token.equals(",") || token.equals(";") || token.equals(")") || token.equals("]") || token.equals("<") || token.equals(">") || token.equals("<=") || token.equals(">=") || token.equals("==") || token.equals("!="))
        {
            //System.out.println(token + "Empty generated in Vprime");
            
            return "";
        }
        else if(token.equals("+") || token.equals("-"))
        {
            //System.out.println(token + "Accepted in Vprime");
            
            String op = W(s);
            String rs = X(s);
            String xtra = Vprime(s);
            
            String thing = op + rs;
            
            //System.out.println(thing + xtra + " vprime");
            
            if(!xtra.equals(""))
            {
                if(xtra.substring(0,1).equals("+"))
                {
                    code[codeCount][0] = "add";
                    code[codeCount][1] = rs;
                    code[codeCount][2] = xtra.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                }
                else if(xtra.substring(0,1).equals("-"))
                {
                    code[codeCount][0] = "sub";
                    code[codeCount][1] = rs;
                    code[codeCount][2] = xtra.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                }
                
                return op + "t" + String.valueOf(tcount-1);
            }
            
            
            return thing;
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Vprime" + token);
            System.exit(0);
            return "";
        }
    }
    public static String W(Scanner s)
    {
        if(token.equals("+") || token.equals("-"))
        {
            //System.out.println(token + "Accepted in W");
            String var = token;
            
            token = getLine(s);
            
            return var;
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in W" + token);
            System.exit(0);
            return "";
        }
    }
    public static String X(Scanner s)
    {
        if(token.matches("Identifier:.*") || token.matches("Int:.*") || token.matches("Float:.*") || token.equals("("))
        {
            //System.out.println(token + "Accepted in X");
            
            String ls = Z(s);
            String rs = Xprime(s);
            
            //System.out.println(ls + rs + " x");
            //System.out.println(rs);
            
            if(!rs.equals(""))
            {
                if(rs.substring(0,1).equals("*"))
                {
                    code[codeCount][0] = "mult";
                    code[codeCount][1] = ls;
                    code[codeCount][2] = rs.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                }
                else if(rs.substring(0,1).equals("/"))
                {
                    code[codeCount][0] = "div";
                    code[codeCount][1] = ls;
                    code[codeCount][2] = rs.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                }
                
                return "t" + String.valueOf(tcount - 1);
            }
            else
            {
                return ls;
            }
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in X" + token);
            System.exit(0);
            return "";
        }
    }
    public static String Xprime(Scanner s)
    {
        if(token.equals("+") || token.equals("-") || token.equals(",") || token.equals(";") || token.equals(")") || token.equals("]") || token.equals("<") || token.equals(">") || token.equals("<=") || token.equals(">=") || token.equals("==") || token.equals("!="))
        {
            //System.out.println(token + "Empty generated in Xprime");
            return "";
        }
        else if(token.equals("*") || token.equals("/"))
        {
            //System.out.println(token + "Accepted in Xprime");
            
            String op = Y(s);
            String rs = Z(s);
            String xtra = Xprime(s);
            
            String thing = op + rs;
            //System.out.println(thing +xtra + " xprime");
            
            if(!xtra.equals(""))
            {
                if(xtra.substring(0,1).equals("*"))
                {
                    code[codeCount][0] = "mult";
                    code[codeCount][1] = rs;
                    code[codeCount][2] = xtra.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                }
                else if(xtra.substring(0,1).equals("/"))
                {
                    code[codeCount][0] = "div";
                    code[codeCount][1] = rs;
                    code[codeCount][2] = xtra.substring(1);
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                }
                
                
                return op + "t" + String.valueOf(tcount-1);
            }
            
            return thing;
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Xprime" + token);
            System.exit(0);
            return "";
        }
    }
    public static String Y(Scanner s)
    {
        if(token.equals("*") || token.equals("/"))
        {
            //System.out.println(token + "Accepted in Y");
            String var = token;
            
            token = getLine(s);
            
            return var;
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Y" + token);
            System.exit(0);
            return "";
        }
    }
    public static String Z(Scanner s)
    {
        if(token.matches("Identifier:.*"))
        {
            //System.out.println(token + "Accepted in Z");
            String var = token.substring(11);
            token = getLine(s);
            
            String z = Zprime(s);
            
            if(!z.equals("") && z.substring(0,1).equals("("))
            {
                String[] split = z.split("\\(");
                
                int parmcount = 0;
                
                if(split[1].substring(0,1).equals(")"))
                {
                    parmcount = 0;
                }
                else
                {
                    char [] parms = split[1].toCharArray();
                    int commaCount = 0;
                    
                    for(int i=0; i<parms.length; i++)
                    {
                        if(parms[i] == ',')
                        {
                            commaCount++;
                        }
                    }
                    
                    parmcount = commaCount + 1;
                }
                
                code[codeCount][0] = "call";
                code[codeCount][1] = var;
                code[codeCount][2] = Integer.toString(parmcount);
                code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                codeCount++;
                tcount++;
                
                return "t" + String.valueOf(tcount-1);
            }
            else if(!z.equals("") && z.substring(0,1).equals("["))
            {
                String[] first = z.split("\\[");
                String[] second = first[1].split("\\]");
                
                String vari = second[0];
                
                //System.out.println(var + " | | |");
                
                if(vari.matches("[0-9]*"))
                {
                    int varia = Integer.parseInt(vari);
                    int disp = varia * 4;
                    
                    code[codeCount][0] = "disp";
                    code[codeCount][1] = var;
                    code[codeCount][2] = Integer.toString(disp);
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                }
                else
                {
                    code[codeCount][0] = "mult";
                    code[codeCount][1] = vari;
                    code[codeCount][2] = "4";
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                    
                    String tvar = "t" + String.valueOf(tcount-1);
                    
                    code[codeCount][0] = "disp";
                    code[codeCount][1] = var;
                    code[codeCount][2] = tvar;
                    code[codeCount][3] = "t" + String.valueOf(tcount);
                    
                    codeCount++;
                    tcount++;
                }
                
                return "t" + String.valueOf(tcount - 1);
            }
            
            return var;
        }
        else if(token.equals("("))
        {
            //System.out.println(token + "Accepted in Z");
            token = getLine(s);
            
            String r = R(s);
            
            if(token.equals(")"))
            {
                //System.out.println(token + "Accepted in Z");
                token = getLine(s);
            }
            else
            {
                System.out.print("REJECT");
                //System.out.println(" in Z" + token);
                System.exit(0);
            }
            
            return r;
        }
        else if(token.matches("Int:.*") || token.matches("Float:.*"))
        {
            //System.out.println(token + "Accepted in Z");
            String var = "";
            
            if(token.matches("Int:.*"))
            {
                var = token.substring(4);
            }
            if(token.matches("Float:.*"))
            {
                var = token.substring(6);
            }
            
            token = getLine(s);
            
            return var;
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Z" + token);
            System.exit(0);
            return "";
        }
    }
    public static String Zprime(Scanner s)
    {
        if(token.equals("*") || token.equals("/") || token.equals("+") || token.equals("-") || token.equals(",") || token.equals(";") || token.equals(")") || token.equals("]") || token.equals("<") || token.equals(">") || token.equals("<=") || token.equals(">=") || token.equals("==") || token.equals("!="))
        {
            //System.out.println(token + "Empty generated in Zprime");
            return "";
        }
        else if(token.equals("("))
        {
            //System.out.println(token + "Accepted in Zprime");
            String var1 = token;
            token = getLine(s);
            
            String sg = Sigma(s);
            
            if(token.equals(")"))
            {
                //System.out.println(token + "Accepted in Zprime");
                String var2 = token;
                token = getLine(s);
                
                return var1 + sg + var2;
            }
            else
            {
                System.out.print("REJECT");
                //System.out.println(" in Zprime" + token);
                System.exit(0);
                return "";
            }
        }
        else if(token.equals("["))
        {
            //System.out.println(token + "Accepted in Zprime");
            String var1 = token;
            token = getLine(s);
            
            String r = R(s);
            
            if(token.equals("]"))
            {
                //System.out.println(token + "Accepted in Zprime");
                String var2 = token;
                token = getLine(s);
                
                return var1 + r + var2;
            }
            else
            {
                System.out.print("REJECT");
                //System.out.println(" in Zprime" + token);
                System.exit(0);
                return "";
            }
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Zprime" + token);
            System.exit(0);
            return "";
        }
    }
    public static String Sigma(Scanner s)
    {
        if(token.equals("(") || token.matches("Identifier:.*") || token.matches("Int:.*") || token.matches("Float:.*"))
        {
            //System.out.println(token + "Accepted in Sigma");
            
            String d = Delta(s);
            //System.out.println(d);
            
            return d;
        }
        else if(token.equals(")"))
        {
            //System.out.println(token + "Empty Genereated in Sigma");
            return "";
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Sigma" + token);
            System.exit(0);
            return "";
        }
    }
    public static String Delta(Scanner s)
    {
        if(token.equals("(") || token.matches("Identifier:.*") || token.matches("Int:.*") || token.matches("Float:.*"))
        {
            //System.out.println(token + "Accepted in Delta");
            
            String r = R(s);
            String d = Deltaprime(s);
            
            //System.out.println(r+d);
            
            code[codeCount][0] = "arg";
            code[codeCount][1] = "";
            code[codeCount][2] = "";
            code[codeCount][3] = r;
                    
            codeCount++;
            
            return r + d;
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Delta" + token);
            System.exit(0);
            return "";
        }
    }
    public static String Deltaprime(Scanner s)
    {
        if(token.equals(","))
        {
            //System.out.println(token + "Accepted in Deltaprime");
            String var = token;
            token = getLine(s);
            
            String r = R(s);
            String d = Deltaprime(s);
            
            code[codeCount][0] = "arg";
            code[codeCount][1] = "";
            code[codeCount][2] = "";
            code[codeCount][3] = r;
                    
            codeCount++;
            
            return var + r + d;
        }
        else if(token.equals(")"))
        {
            //System.out.println(token + "Empty Genereated in Deltaprime");
            return "";
        }
        else
        {
            System.out.print("REJECT");
            //System.out.println(" in Deltaprime" + token);
            System.exit(0);
            return "";
        }
    }
    
    //Start of Semantics---------------------------------------------------------------------------------------------------------------------------
    
    public static boolean semanticAnalyzer(ArrayList<String> tokens, ArrayList<SymTabElement> SYMTAB)
    {
        boolean semantics = true;
        
        //Begin check void function for return=========================================================
        
        ArrayList<SymTabElement> functionList = new ArrayList<SymTabElement>();
        
        for(int i=0; i<SYMTAB.size(); i++)
        {
            if(SYMTAB.get(i).isFunction)
            {
                functionList.add(SYMTAB.get(i));
            }
        }
        
        int mainCount = 0;
        
        for(int i=0; i<functionList.size(); i++)
        {
            if(functionList.get(i).id.equals("main"))
            {
                mainCount++;
            }
        }
        
        if(mainCount != 1)
        {
            semantics = false;
            //System.out.println("More than one or no main function(s)");
        }
        
        ArrayList<SymTabElement> voidFunctionList = new ArrayList<SymTabElement>();
        
        for(int i=0; i<functionList.size(); i++)
        {
            if(functionList.get(i).type.equals("void"))
            {
                voidFunctionList.add(functionList.get(i));
            }
        }
        
        for(int i=0; i<voidFunctionList.size(); i++)
        {
            int j=0;
            boolean found = false;
            int index = 0;
            while(!found)
            {
                if(tokens.get(j).equals("Identifier:" + voidFunctionList.get(i).id))
                {
                    index = j;
                    found = true;
                }
                
                j++;
            }
            
            found = false;
            while(!found)
            {
                if(tokens.get(index).equals("{"))
                {
                    found = true;
                }
                
                index++;
            }

            int bracketCount = 1;
            while(bracketCount != 0)
            {
                if(tokens.get(index).equals("{"))
                {
                    bracketCount++;
                }
                else if(tokens.get(index).equals("}"))
                {
                    bracketCount--;
                }
                else if(tokens.get(index).equals("Keyword:return"))
                {
                    if(!tokens.get(index+1).equals(";"))
                    {
                        semantics = false;
                       // System.out.println("Void function has return statement");
                    }
                }
                
                index++;
            }
        }
        //End check void function for return=========================================================
        
        //Begin check for main function at end of program=========================================================
        
        boolean mainFound = false;
        for(int i=0; i<functionList.size(); i++)
        {
            if(functionList.get(i).id.equals("main"))
            {
                mainFound = true;
            }
        }
        
        if(!mainFound)
        {
            semantics = false;
            //System.out.println("Main function issue");
        }
        else
        {
            boolean mainAtEnd = false;
            
            if(functionList.get(functionList.size()-1).id.equals("main"))
            {
                mainAtEnd=true;
            }
            
            if(!mainAtEnd)
            {
                semantics=false;
                //System.out.println("Main function issue");
            }
            
            boolean correctMainValues = false;
            
            if(functionList.get(functionList.size()-1).type.equals("void") && functionList.get(functionList.size()-1).parameters.isEmpty())
            {
                correctMainValues = true;
            }
            
            if(!correctMainValues)
            {
                semantics = false;
                //System.out.println("Main function issue");
            }
            
        }
        
        //End check for main function at end of program=========================================================
        
        //Begin check for id's not of type void=========================================================
        
        ArrayList<SymTabElement> nonFunctionList = new ArrayList<SymTabElement>();
        
        for(int i=0; i<SYMTAB.size(); i++)
        {
            if(!SYMTAB.get(i).isFunction)
            {
                nonFunctionList.add(SYMTAB.get(i));
            }
        }
        
        for(int i=0; i<nonFunctionList.size(); i++)
        {
            if(nonFunctionList.get(i).type.equals("void"))
            {
                semantics = false;
                //System.out.println("Id is of type void");
            }
        }
        
        //End check for id's not of type void=========================================================
        
        //Begin check for duplicate id's in same scope=========================================================
        
        int currentDepth = SYMTAB.get(0).depth;
        
        ArrayList<String> ids = new ArrayList<String>();
        
        
        for(int i=0; i<SYMTAB.size(); i++)
        {
            if(!SYMTAB.get(i).isFunction && SYMTAB.get(i).depth != currentDepth)
            {
                ids.clear();
                currentDepth = SYMTAB.get(i).depth;
                i--;
            }
            else if(SYMTAB.get(i).isFunction)
            {
                ids.clear();

                for(int k=0; k<SYMTAB.get(i).parameters.size(); k++)
                {
                    for(int j=0; j<ids.size(); j++)
                    {
                        if(SYMTAB.get(i).parameters.get(k).equals(ids.get(j)))
                        {
                            semantics = false;
                            //System.out.println("Duplicate ids in same scope");
                        }  
                    }
                    ids.add(SYMTAB.get(i).parameters.get(k));
                }
                if(i != SYMTAB.size() - 1)
                {
                    currentDepth = SYMTAB.get(i+1).depth; 
                }
            }
            else
            {
                for(int j=0; j<ids.size(); j++)
                {
                    if(SYMTAB.get(i).id.equals(ids.get(j)))
                    {
                        semantics = false;
                        //System.out.println("Duplicate ids in same scope");
                    }
                }
                ids.add(SYMTAB.get(i).id);
            }
        }

        ids.clear();
        
        for(int i=0; i<SYMTAB.size(); i++)
        {
            if(SYMTAB.get(i).depth == 0)
            {
                for(int j=0; j<ids.size(); j++)
                {
                    if(SYMTAB.get(i).id.equals(ids.get(j)))
                    {
                        semantics = false;
                        //System.out.println("Duplicate ids in same scope");
                    }
                }
                ids.add(SYMTAB.get(i).id);
            }
        }

        //End check for duplicate id's in same scope=========================================================
        
        //Begin check for array index agreement=========================================================
        
        for(int i=0; i<tokens.size(); i++)
        {
            if(tokens.get(i).matches("Identifier:.*") && tokens.get(i+1).equals("["))
            {
                if(!tokens.get(i-1).equals("Keyword:int") && !tokens.get(i-1).equals("Keyword:float"))
                {
                    ArrayList<SymTabElement> neighboringVars = new ArrayList<SymTabElement>();
                    String parentFunction = null;
                    
                    int j = i;
                    boolean foundParent = false;
                    
                    while(!foundParent) //Find Parent function
                    {
                        if(tokens.get(j).matches("Identifier:.*") && (tokens.get(j-1).equals("Keyword:int") || tokens.get(j-1).equals("Keyword:float") || tokens.get(j-1).equals("Keyword:void")))
                        {
                            String s = tokens.get(j).substring(11);
                            
                            for(int k=0; k<SYMTAB.size(); k++)
                            {
                                if(SYMTAB.get(k).id.equals(s) && SYMTAB.get(k).isFunction)
                                {
                                    parentFunction = s;
                                    foundParent = true;
                                }
                            }
                        }
                        j--;
                    }
                    
                    for(int k=0; k<SYMTAB.size(); k++) //Get parameters of parent function
                    {
                        if(SYMTAB.get(k).id.equals(parentFunction) && SYMTAB.get(k).isFunction)
                        {
                            for(int l=0; l<SYMTAB.get(k).parameters.size(); l++)
                            {
                                if(SYMTAB.get(k).parameterTypes.get(l).equals("int"))
                                {
                                    SymTabElement temp = new SymTabElement(SYMTAB.get(k).parameters.get(l), SYMTAB.get(k).parameterTypes.get(l), 0, SYMTAB.get(k).parameterIsArray.get(l), false, null, null, null);
                                    neighboringVars.add(temp);
                                }
                            }
                        }
                    }
                    
                    int depthCount = 0;
                    int parenCount = 0;
                    for(int g=i; g>=0; g--) //Get all previous variables from valid scopes
                    {
                        if(tokens.get(g).equals("}"))
                        {
                            depthCount ++;
                        }
                        else if(tokens.get(g).equals("{"))
                        {
                            if(depthCount > 0)
                            {
                                depthCount --;
                            }
                        }
                        else if(tokens.get(g).equals(")"))
                        {
                            parenCount++;
                        }
                        else if(tokens.get(g).equals("("))
                        {
                            if(parenCount>0)
                            {
                                parenCount--;
                            }
                        }
                        
                        if(depthCount == 0 && parenCount == 0)
                        {
                            if(tokens.get(g).matches("Identifier:.*") && tokens.get(g-1).equals("Keyword:int"))
                            {
                                if(tokens.get(g+1).equals("["))
                                {
                                    boolean signal = false;
                                    
                                    for(int h=0; h<neighboringVars.size(); h++)
                                    {
                                        if(tokens.get(g).substring(11).equals(neighboringVars.get(h).id))
                                        {
                                            signal = true;
                                        }
                                    }
                                    
                                    if(!signal)
                                    {
                                        SymTabElement temp = new SymTabElement(tokens.get(g).substring(11), "int", 0, true, false, null, null, null);
                                        neighboringVars.add(temp);
                                    }
                                }
                                else if(!tokens.get(g+1).equals("("))
                                {
                                    boolean signal = false;
                                    
                                    for(int h=0; h<neighboringVars.size(); h++)
                                    {
                                        if(tokens.get(g).substring(11).equals(neighboringVars.get(h).id))
                                        {
                                            signal = true;
                                        }
                                    }
                                    
                                    if(!signal)
                                    {
                                        SymTabElement temp = new SymTabElement(tokens.get(g).substring(11), "int", 0, false, false, null, null, null);
                                        neighboringVars.add(temp);
                                    }
                                }
                            }
                        }
                    }
                    
                    boolean found = false;
                    for(int k=0; k<neighboringVars.size(); k++)
                    {
                        if(tokens.get(i+2).matches("Identifier:.*") && neighboringVars.get(k).id.equals(tokens.get(i+2).substring(11)))
                        {
                            found = true;
                        }
                    }
                    for(int k=0; k<SYMTAB.size(); k++)
                    {
                        if(tokens.get(i+2).matches("Identifier:.*") && SYMTAB.get(k).id.equals(tokens.get(i+2).substring(11)) && SYMTAB.get(k).isFunction && SYMTAB.get(k).type.equals("int"))
                        {
                            found = true;
                        }
                    }
                    
                    if(found == false && !tokens.get(i+2).matches("Int:.*"))
                    {
                        semantics = false;
                        //System.out.println("Array index does not agree");
                    }                   
                }
            }
        }
   
        //End check for array index agreement=========================================================
        
        //Begin check for number of parameters and arguments agreement=========================================================
        
        for(int i=0; i<tokens.size(); i++)
        {
            if(tokens.get(i).matches("Identifier:.*"))
            {
                if(!tokens.get(i-1).equals("Keyword:int") || !tokens.get(i-1).equals("Keyword:float") || !tokens.get(i-1).equals("Keyword:void"))
                {
                    for(int j=0; j<SYMTAB.size(); j++)
                    {
                        if(tokens.get(i).substring(11).equals(SYMTAB.get(j).id) && SYMTAB.get(j).isFunction)
                        {   
                            int parenDepth = 1;
                            int k = i + 2;
                            int commaCount = 0;
                            
                            while(parenDepth > 0)
                            {
                                if(tokens.get(k).equals("("))
                                {
                                    parenDepth ++;
                                }
                                else if(tokens.get(k).equals(")"))
                                {
                                    parenDepth --;
                                }
                                
                                if(parenDepth == 1)
                                {
                                    if(tokens.get(k).equals(","))
                                    {
                                        commaCount++;
                                    }
                                }
                                
                                k++;
                            }
                            
                           if(!(commaCount == SYMTAB.get(j).parameters.size()-1) && SYMTAB.get(j).parameters.size() > 0)
                           {
                               semantics = false;
                               //System.out.println("Number of parameters and arguments do not match");
        
                           }
                            
                        }
                    }
                }
            }
        }
        
        //End check for number of parameters and arguments agreement=========================================================

        //Begin check for whether function is defined=========================================================
        
        for(int i=0; i<tokens.size(); i++)
        {
            if(tokens.get(i).matches("Identifier:.*"))
            {
                if(!tokens.get(i-1).equals("Keyword:int") && !tokens.get(i-1).equals("Keyword:float") && !tokens.get(i-1).equals("Keyword:void"))
                {
                    if(tokens.get(i+1).equals("("))
                    {
                        ArrayList<String> prevFuncs = new ArrayList<String>();
                        
                        int depthCount = 0;
                        int parenCount = 0;
                        for(int g=i; g>0; g--) //Get all previous variables from valid scopes
                        {
                            if(tokens.get(g).equals("}"))
                            {
                                depthCount ++;
                            }
                            else if(tokens.get(g).equals("{"))
                            {
                                if(depthCount > 0)
                                {
                                    depthCount --;
                                }
                            }
                            else if(tokens.get(g).equals(")"))
                            {
                                parenCount++;
                            }
                            else if(tokens.get(g).equals("("))
                            {
                                if(parenCount>0)
                                {
                                    parenCount--;
                                }
                            }

                            if(depthCount == 0 && parenCount == 0)
                            {
                                if(tokens.get(g).matches("Identifier:.*") && (tokens.get(g-1).equals("Keyword:int") || tokens.get(g-1).equals("Keyword:float")) || tokens.get(g-1).equals("Keyword:void"))
                                {
                                    if(tokens.get(g+1).equals("("))
                                    {
                                        prevFuncs.add(tokens.get(g).substring(11));
                                    }
                                }
                            }  
                        }
                        
                        if(!prevFuncs.contains(tokens.get(i).substring(11)))
                        {
                            semantics = false;
                            //System.out.println("Function is not defined");
        
                        }
                    }
                }
            }
        }
        
        
        //End check for whether function is defined=========================================================

        //Begin check for variable declaration=========================================================
        
        for(int i=0; i<tokens.size(); i++)
        {
            if(tokens.get(i).matches("Identifier:.*"))
            {
                if(!tokens.get(i-1).equals("Keyword:int") && !tokens.get(i-1).equals("Keyword:float") && !tokens.get(i-1).equals("Keyword:void") && !tokens.get(i+1).equals("("))
                {
                    int j = i;
                    boolean found = false;
                    String parent = "";
                    SymTabElement parentFunction = new SymTabElement("", "", 0, false, false, null, null, null);
                    
                    while(!found) // Get parent function
                    {
                        if(tokens.get(j).matches("Identifier:.*"))
                        {
                            if(tokens.get(j+1).equals("(") && (tokens.get(j-1).equals("Keyword:int") || tokens.get(j-1).equals("Keyword:float") || tokens.get(j-1).equals("Keyword:void")))
                            {
                                parent = tokens.get(j).substring(11);
                                
                                for(int k=0; k<SYMTAB.size(); k++)
                                {
                                    if(SYMTAB.get(k).id.equals(parent))
                                    {
                                        parentFunction = SYMTAB.get(k);
                                    }
                                }
                                
                                found = true;
                            }
                        }
                        
                        j--;
                    }
                    
                    ArrayList<String> prevVars = new ArrayList<String>();
                    
                    for(int k=0; k<parentFunction.parameters.size(); k++)
                    {
                        prevVars.add(parentFunction.parameters.get(k));
                    }

                    int depthCount = 0;
                    int parenCount = 0;
                    for(int g=i; g>0; g--) //Get all previous variables from valid scopes
                    {
                        if(tokens.get(g).equals("}"))
                        {
                            depthCount ++;
                        }
                        else if(tokens.get(g).equals("{"))
                        {
                            if(depthCount > 0)
                            {
                                depthCount --;
                            }
                        }
                        else if(tokens.get(g).equals(")"))
                        {
                            parenCount++;
                        }
                        else if(tokens.get(g).equals("("))
                        {
                            if(parenCount>0)
                            {
                                parenCount--;
                            }
                        }

                        if(depthCount == 0 && parenCount == 0)
                        {
                            if(tokens.get(g).matches("Identifier:.*") && (tokens.get(g-1).equals("Keyword:int") || tokens.get(g-1).equals("Keyword:float")))
                            {
                                if(!tokens.get(g+1).equals("("))
                                {
                                    if(!prevVars.contains(tokens.get(g).substring(11)))
                                    {
                                        prevVars.add(tokens.get(g).substring(11));
                                    }
                                }
                            }
                        }  
                    }

                    if(!prevVars.contains(tokens.get(i).substring(11)))
                    {
                        semantics = false;
                        //System.out.println("Variable isn't declared");
                    }
                }
            }
        }
        
        //End check for variable declaration=========================================================
              
        //Begin check for function return statement for int and float=========================================================
        
        for(int i=0; i<SYMTAB.size(); i++)
        {
            if(SYMTAB.get(i).isFunction && !SYMTAB.get(i).type.equals("void"))
            {
                String func = SYMTAB.get(i).id;
                
                for(int j=0; j<tokens.size(); j++)
                {
                    if(tokens.get(j).matches("Identifier.*") && tokens.get(j).substring(11).equals(func))
                    {
                        if(tokens.get(j-1).equals("Keyword:int") || tokens.get(j-1).equals("Keyword:float"))
                        {
                            String type = tokens.get(j-1).substring(8);
                            
                            int bracket = 0;
                            boolean bracketFound = false;
                            int k = j;
                            while(!bracketFound)
                            {
                                if(tokens.get(k).equals("{"))
                                {
                                    bracket = k;
                                    bracketFound = true;
                                }
                                
                                k++;
                            }
                            
                            int depth = 1;
                            boolean correctReturn = false;
                            int index = bracket + 1;
                            
                            while(depth > 0)
                            {
                                if(tokens.get(index).equals("{"))
                                {
                                    depth++;
                                }
                                else if(tokens.get(index).equals("}"))
                                {
                                    depth --;
                                }
                                else if(tokens.get(index).equals("Keyword:return"))
                                {
                                    while(tokens.get(index+1).equals("("))
                                    {
                                        index++;
                                    }

                                    if(tokens.get(index + 1).matches("Int:.*"))
                                    {
                                        if(type.equals("int"))
                                        {
                                            correctReturn = true;
                                        }
                                    }
                                    else if(tokens.get(index + 1).matches("Float:.*"))
                                    {
                                        if(type.equals("float"))
                                        {
                                            correctReturn = true;
                                        }
                                    }
                                    else if(tokens.get(index + 1).matches("Identifier:.*"))
                                    {
                                        String ident = tokens.get(index + 1).substring(11);
                                        String identType = "void";
                                        
                                        for(int p=0; p<SYMTAB.size(); p++)
                                        {
                                            if(SYMTAB.get(p).id.equals(ident))
                                            {
                                                identType = SYMTAB.get(p).type;
                                            }
                                        }
                                        
                                        for(int p=0; p<SYMTAB.get(i).parameters.size(); p++)
                                        {
                                            if(SYMTAB.get(i).parameters.get(p).equals(ident))
                                            {
                                                identType = SYMTAB.get(i).parameterTypes.get(p);
                                            }
                                        }
                                        
                                        if(type.equals(identType))
                                        {
                                            correctReturn = true;
                                        }
                                    }
                                }
                                
                                index++;
                            }
                            
                            if(correctReturn == false)
                            {
                                semantics = false;
                                //System.out.println("Incorrect Function return statement for int or float");
                            }
                            
                        }
                    }
                }
            }
        }
        
        //End check for function return statement for int and float=========================================================
        
        //Begin check for parameters and arguments agreeing in type=========================================================
        
        for(int i=0; i<tokens.size(); i++)
        {
            if(tokens.get(i).matches("Identifier:.*"))
            {
                if(tokens.get(i+1).equals("(") && (!tokens.get(i-1).equals("Keyword:int") && !tokens.get(i-1).equals("Keyword:float") && !tokens.get(i-1).equals("Keyword:void")))
                {

                    SymTabElement func = new SymTabElement("","",0, false, false, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<Boolean>());
                    
                    for(int p=0; p<SYMTAB.size(); p++)
                    {
                        if(SYMTAB.get(p).id.equals(tokens.get(i).substring(11)))
                        {
                            func = SYMTAB.get(p);
                        }
                    }

                    //================================================================================
                    int j = i;
                    boolean found = false;
                    String parent = "";
                    SymTabElement parentFunction = new SymTabElement("", "", 0, false, false, null, null, null);
                    
                    while(!found) // Get parent function
                    {
                        if(tokens.get(j).matches("Identifier:.*"))
                        {
                            if(tokens.get(j+1).equals("(") && (tokens.get(j-1).equals("Keyword:int") || tokens.get(j-1).equals("Keyword:float") || tokens.get(j-1).equals("Keyword:void")))
                            {
                                parent = tokens.get(j).substring(11);
                                
                                for(int k=0; k<SYMTAB.size(); k++)
                                {
                                    if(SYMTAB.get(k).id.equals(parent))
                                    {
                                        parentFunction = SYMTAB.get(k);
                                    }
                                }
                                
                                found = true;
                            }
                        }
                        
                        j--;
                    }
                    
                    ArrayList<SymTabElement> prevVars = new ArrayList<SymTabElement>();
                    
                    for(int k=0; k<parentFunction.parameters.size(); k++)
                    {
                        SymTabElement temp = new SymTabElement(parentFunction.parameters.get(k), parentFunction.parameterTypes.get(k), 0 , parentFunction.parameterIsArray.get(k),false,null,null,null);
                        prevVars.add(temp);
                    }

                    int depthCount = 0;
                    int parenCount = 0;
                    for(int g=i; g>0; g--) //Get all previous variables from valid scopes
                    {
                        if(tokens.get(g).equals("}"))
                        {
                            depthCount ++;
                        }
                        else if(tokens.get(g).equals("{"))
                        {
                            if(depthCount > 0)
                            {
                                depthCount --;
                            }
                        }
                        else if(tokens.get(g).equals(")"))
                        {
                            parenCount++;
                        }
                        else if(tokens.get(g).equals("("))
                        {
                            if(parenCount>0)
                            {
                                parenCount--;
                            }
                        }

                        if(depthCount == 0 && parenCount == 0)
                        {
                            if(tokens.get(g).matches("Identifier:.*") && (tokens.get(g-1).equals("Keyword:int") || tokens.get(g-1).equals("Keyword:float")))
                            {
                                if(!tokens.get(g+1).equals("("))
                                {
                                    boolean foundit = false;
                                    for(int p=0; p<prevVars.size(); p++)
                                    {
                                        if(prevVars.get(p).id.equals(tokens.get(g).substring(11)))
                                        {
                                            foundit = true;
                                        }
                                    }
                                    
                                    if(foundit == false)
                                    {
                                        if(!tokens.get(g+1).equals("["))
                                        {
                                            SymTabElement temp = new SymTabElement(tokens.get(g).substring(11), tokens.get(g-1).substring(8),0,false,false,null,null,null);
                                            prevVars.add(temp);
                                        }
                                        else
                                        {
                                            SymTabElement temp = new SymTabElement(tokens.get(g).substring(11), tokens.get(g-1).substring(8),0,true,false,null,null,null);
                                            prevVars.add(temp);
                                        }
                                    }
                                }
                            }
                        }  
                    }

                    //================================================================================
                    
                    int index = i+2;
                    int parenDepth = 1;
                    int commaCount = 0;
                    
                    boolean good = true;
                    
                    while(parenDepth > 0 && commaCount < func.parameterTypes.size())
                    {
                        if(tokens.get(index).equals("("))
                        {
                            parenDepth++;
                        }
                        else if(tokens.get(index).equals(")"))
                        {
                            parenDepth--;
                        }
                        
                        if(parenDepth == 1)
                        {
                            if(tokens.get(index).equals(","))
                            {
                                commaCount++;
                            }
                            else if(tokens.get(index).matches("Int:.*"))
                            {
                                if(!func.parameterTypes.get(commaCount).equals("int"))
                                {
                                    good = false;
                                }
                            }
                            else if(tokens.get(index).matches("Float:.*"))
                            {
                                if(!func.parameterTypes.get(commaCount).equals("float"))
                                {
                                    good = false;
                                }
                            }
                            else if(tokens.get(index).matches("Identifier:.*"))
                            {
                                String type = "void";
                                boolean isArray = false;
                                 
                                for(int p=0; p<prevVars.size(); p++)
                                {
                                    if(prevVars.get(p).id.equals(tokens.get(index).substring(11)))
                                    {
                                        type = prevVars.get(p).type;
                                        isArray = prevVars.get(p).isArray;
                                    }
                                }
                                
                                if(!func.parameterTypes.get(commaCount).equals(type) || (isArray && func.parameterIsArray.get(commaCount) && tokens.get(index+1).equals("[")) || (isArray && !func.parameterIsArray.get(commaCount) && !tokens.get(index+1).equals("[")))
                                {
                                    good = false;
                                }
                            }
                        }
                        
                        index++;
                    }
                    
                    if(good == false)
                    {
                        semantics = false;
                        //System.out.println("Parameter arguments do not match in type");
                    }
                }
            }
        }
        
        //End check for parameters and arguments agreeing in type========================================================= 
        
        //Begin check for return of simple structures=========================================================
        
        for(int i=0; i<tokens.size(); i++)
        {
            if(tokens.get(i).equals("Keyword:return"))
            {
                if(tokens.get(i+1).matches("Identifier:.*"))
                {
                    String returnVar = tokens.get(i+1).substring(11);
                    
                    //=======================================================================
                    int j = i+1;
                    boolean found = false;
                    String parent = "";
                    SymTabElement parentFunction = new SymTabElement("", "", 0, false, false, null, null, null);
                    
                    while(!found) // Get parent function
                    {
                        if(tokens.get(j).matches("Identifier:.*"))
                        {
                            if(tokens.get(j+1).equals("(") && (tokens.get(j-1).equals("Keyword:int") || tokens.get(j-1).equals("Keyword:float") || tokens.get(j-1).equals("Keyword:void")))
                            {
                                parent = tokens.get(j).substring(11);
                                
                                for(int k=0; k<SYMTAB.size(); k++)
                                {
                                    if(SYMTAB.get(k).id.equals(parent))
                                    {
                                        parentFunction = SYMTAB.get(k);
                                    }
                                }
                                
                                found = true;
                            }
                        }
                        
                        j--;
                    }
                    
                    ArrayList<SymTabElement> prevVars = new ArrayList<SymTabElement>();
                    
                    for(int k=0; k<parentFunction.parameters.size(); k++)
                    {
                        SymTabElement temp = new SymTabElement(parentFunction.parameters.get(k), parentFunction.parameterTypes.get(k), 0 , parentFunction.parameterIsArray.get(k),false,null,null,null);
                        prevVars.add(temp);
                    }

                    int depthCount = 0;
                    int parenCount = 0;
                    for(int g=i; g>0; g--) //Get all previous variables from valid scopes
                    {
                        if(tokens.get(g).equals("}"))
                        {
                            depthCount ++;
                        }
                        else if(tokens.get(g).equals("{"))
                        {
                            if(depthCount > 0)
                            {
                                depthCount --;
                            }
                        }
                        else if(tokens.get(g).equals(")"))
                        {
                            parenCount++;
                        }
                        else if(tokens.get(g).equals("("))
                        {
                            if(parenCount>0)
                            {
                                parenCount--;
                            }
                        }

                        if(depthCount == 0 && parenCount == 0)
                        {
                            if(tokens.get(g).matches("Identifier:.*") && (tokens.get(g-1).equals("Keyword:int") || tokens.get(g-1).equals("Keyword:float")))
                            {
                                if(!tokens.get(g+1).equals("("))
                                {
                                    boolean foundit = false;
                                    for(int p=0; p<prevVars.size(); p++)
                                    {
                                        if(prevVars.get(p).id.equals(tokens.get(g).substring(11)))
                                        {
                                            foundit = true;
                                        }
                                    }
                                    
                                    if(foundit == false)
                                    {
                                        if(!tokens.get(g+1).equals("["))
                                        {
                                            SymTabElement temp = new SymTabElement(tokens.get(g).substring(11), tokens.get(g-1).substring(8),0,false,false,null,null,null);
                                            prevVars.add(temp);
                                        }
                                        else
                                        {
                                            SymTabElement temp = new SymTabElement(tokens.get(g).substring(11), tokens.get(g-1).substring(8),0,true,false,null,null,null);
                                            prevVars.add(temp);
                                        }
                                    }
                                }
                            }
                        }  
                    }
                    //=======================================================================
                    
                    int index = 0;
                    for(int k = 0; k<prevVars.size(); k++)
                    {
                        if(returnVar.equals(prevVars.get(k).id))
                        {
                            index = k;
                        }
                    }
                    
                    if(prevVars.get(index).isArray)
                    {
                        if(!tokens.get(i+2).equals("["))
                        {
                            semantics = false;
                            //System.out.println("cannot return type array");
                        }
                    }
                    
                }
            }
        }

        //End check for return of simple structures=========================================================
        
        //Begin check for operand/operand agreement=========================================================
        
        ArrayList<String> comparatives = new ArrayList<String>();
        comparatives.add("=");
        comparatives.add("==");
        comparatives.add("!=");
        comparatives.add("<");
        comparatives.add(">");
        comparatives.add("<=");
        comparatives.add(">=");
        
        ArrayList<String> operators = new ArrayList<String>();
        operators.add("+");
        operators.add("-");
        operators.add("/");
        operators.add("*");
        
        
        
        for(int i=0; i<tokens.size(); i++)
        {
            if(tokens.get(i).matches("Identifier:.*") || tokens.get(i).matches("Int:.*") || tokens.get(i).matches("Float:.*"))
            {
                if(!tokens.get(i-1).equals("Keyword:int") && !tokens.get(i-1).equals("Keyword:float") && !tokens.get(i-1).equals("Keyword:void"))
                {
                    //=======================================================================
                    int j = i+1;
                    boolean found = false;
                    String parent = "";
                    SymTabElement parentFunction = new SymTabElement("", "", 0, false, false, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<Boolean>());
                    
                    while(!found && j>0) // Get parent function
                    {
                        if(tokens.get(j).matches("Identifier:.*"))
                        {
                            if(tokens.get(j+1).equals("(") && (tokens.get(j-1).equals("Keyword:int") || tokens.get(j-1).equals("Keyword:float") || tokens.get(j-1).equals("Keyword:void")))
                            {
                                parent = tokens.get(j).substring(11);
                                
                                for(int k=0; k<SYMTAB.size(); k++)
                                {
                                    if(SYMTAB.get(k).id.equals(parent))
                                    {
                                        parentFunction = SYMTAB.get(k);
                                    }
                                }
                                
                                found = true;
                            }
                        }
                        
                        j--;
                    }
                    
                    ArrayList<SymTabElement> prevVars = new ArrayList<SymTabElement>();
                    
                    for(int k=0; k<parentFunction.parameters.size(); k++)
                    {
                        SymTabElement temp = new SymTabElement(parentFunction.parameters.get(k), parentFunction.parameterTypes.get(k), 0 , parentFunction.parameterIsArray.get(k),false,null,null,null);
                        prevVars.add(temp);
                    }

                    int depthCount = 0;
                    int parenCount = 0;
                    for(int g=i; g>0; g--) //Get all previous variables from valid scopes
                    {
                        if(tokens.get(g).equals("}"))
                        {
                            depthCount ++;
                        }
                        else if(tokens.get(g).equals("{"))
                        {
                            if(depthCount > 0)
                            {
                                depthCount --;
                            }
                        }
                        else if(tokens.get(g).equals(")"))
                        {
                            parenCount++;
                        }
                        else if(tokens.get(g).equals("("))
                        {
                            if(parenCount>0)
                            {
                                parenCount--;
                            }
                        }

                        if(depthCount == 0 && parenCount == 0)
                        {
                            if(tokens.get(g).matches("Identifier:.*") && (tokens.get(g-1).equals("Keyword:int") || tokens.get(g-1).equals("Keyword:float")))
                            {
                                if(!tokens.get(g+1).equals("(") || tokens.get(g+1).equals("("))
                                {
                                    boolean foundit = false;
                                    for(int p=0; p<prevVars.size(); p++)
                                    {
                                        if(prevVars.get(p).id.equals(tokens.get(g).substring(11)))
                                        {
                                            foundit = true;
                                        }
                                    }
                                    
                                    if(foundit == false)
                                    {
                                        if(tokens.get(g+1).equals("["))
                                        {
                                            SymTabElement temp = new SymTabElement(tokens.get(g).substring(11), tokens.get(g-1).substring(8),0,true,false,null,null,null);
                                            prevVars.add(temp);
                                        }
                                        else
                                        {
                                            if(!tokens.get(g+1).equals("("))
                                            {
                                                SymTabElement temp = new SymTabElement(tokens.get(g).substring(11), tokens.get(g-1).substring(8),0,false,false,null,null,null);
                                                prevVars.add(temp);
                                            }
                                            else
                                            {
                                                SymTabElement temp = new SymTabElement(tokens.get(g).substring(11), tokens.get(g-1).substring(8),0,false,true,null,null,null);
                                                prevVars.add(temp);
                                            }
                                        }
                                    }
                                }
                            }
                        }  
                    }
                    //=======================================================================
                    
                    //System.out.println(tokens.get(i));
                    /*for(int k=0; k<prevVars.size(); k++)
                    {
                        System.out.println(prevVars.get(k).id + " " + prevVars.get(k).type + " " + prevVars.get(k).isArray + " " + prevVars.get(k).isFunction);
                    }*/
                    
                    
                    int k = i+1;
                    int parenDepth = 0;
                    int sqBracketDepth = 0;
                    boolean done = false;
                    boolean check = true;
                    
                    while(!done)
                    {
                        if(tokens.get(k).equals("("))
                        {
                            parenDepth++;
                        }
                        else if(tokens.get(k).equals(")"))
                        {
                            parenDepth--;
                        }
                        else if(tokens.get(k).equals("["))
                        {
                            sqBracketDepth++;
                        }
                        else if(tokens.get(k).equals("]"))
                        {
                            sqBracketDepth--;
                        }
                        else if(tokens.get(k).equals("}") || tokens.get(k).equals(";") || tokens.get(k).equals("{"))
                        {
                            done = true;
                        }
                        
                        if(parenDepth == 0 && sqBracketDepth == 0)
                        {
                            if(tokens.get(k).matches("Identifier:.*") || tokens.get(k).matches("Int:.*") || tokens.get(k).matches("Float:.*"))
                            {
                                if(operators.contains(tokens.get(k-1)) || comparatives.contains(tokens.get(k-1)))
                                {
                                    /*System.out.println(tokens.get(i));
                                    System.out.println(tokens.get(k-1));
                                    System.out.println(tokens.get(k));*/
                                    
                                    if(tokens.get(i).matches("Identifier:.*"))
                                    {
                                        String varStr1 = tokens.get(i).substring(11);
                                        int index1 = 0;
                                        
                                        for(int p=0; p<prevVars.size(); p++)
                                        {
                                            if(prevVars.get(p).id.equals(varStr1))
                                            {
                                                index1 = p;
                                            }
                                        }
                                        
                                        if(tokens.get(k).matches("Identifier:.*"))
                                        {
                                            String varStr2 = tokens.get(k).substring(11);
                                            int index2 = 0;
                                            
                                            for(int p=0; p<prevVars.size(); p++)
                                            {
                                                if(prevVars.get(p).id.equals(varStr2))
                                                {
                                                    index2 = p;
                                                }
                                            }
                                            
                                            if(!prevVars.get(index1).type.equals(prevVars.get(index2).type))
                                            {
                                                check = false;
                                            }
                                        }
                                        else if(tokens.get(k).matches("Int:.*"))
                                        {
                                            if(!prevVars.get(index1).type.equals("int"))
                                            {
                                                check = false;
                                                
                                            }
                                        }
                                        else if(tokens.get(k).matches("Float:.*"))
                                        {
                                            if(!prevVars.get(index1).type.equals("float"))
                                            {
                                                check = false;
                                                
                                            }
                                        }
                                    }
                                    else if(tokens.get(i).matches("Int:.*"))
                                    {
                                        if(tokens.get(k).matches("Identifier:.*"))
                                        {
                                            String varStr1 = tokens.get(k).substring(11);
                                            int index1 = 0;

                                            for(int p=0; p<prevVars.size(); p++)
                                            {
                                                if(prevVars.get(p).id.equals(varStr1))
                                                {
                                                    index1 = p;
                                                }
                                            }
                                            
                                            if(!prevVars.get(index1).type.equals("int"))
                                            {
                                                check = false;
                                            }
                                        }
                                        else if(tokens.get(k).matches("Float:.*"))
                                        {
                                            check = false;
                                        }
                                    }
                                    else if(tokens.get(i).matches("Float:.*"))
                                    {
                                        if(tokens.get(k).matches("Identifier:.*"))
                                        {
                                            String varStr1 = tokens.get(k).substring(11);
                                            int index1 = 0;

                                            for(int p=0; p<prevVars.size(); p++)
                                            {
                                                if(prevVars.get(p).id.equals(varStr1))
                                                {
                                                    index1 = p;
                                                }
                                            }
                                            
                                            if(!prevVars.get(index1).type.equals("float"))
                                            {
                                                check = false;
                                            }
                                        }
                                        else if(tokens.get(k).matches("Int:.*"))
                                        {
                                            check = false;
                                        }
                                    }
                                    
                                    if(check == false)
                                    {
                                        semantics = false;
                                        //System.out.println("Operator/operand disagreement");
                                    }
                                    
                                    done = true;
                                    /*System.out.println(check);
                                    System.out.println("");*/
                                }
                            }
                        }
                        
                        k++;
                    }
                   
                } 
            }
        }
        
        //End check for operand/operand agreement=========================================================
        
        //Begin check for operand/operand agreement in parenthesis=========================================================
        
        for(int i=0; i<tokens.size(); i++)
        {
            if(operators.contains(tokens.get(i)) || comparatives.contains(tokens.get(i)))
            {
                
                //=======================================================================
                    int j = i+1;
                    boolean found = false;
                    String parent = "";
                    SymTabElement parentFunction = new SymTabElement("", "", 0, false, false, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<Boolean>());
                    
                    while(!found && j>0) // Get parent function
                    {
                        if(tokens.get(j).matches("Identifier:.*"))
                        {
                            if(tokens.get(j+1).equals("(") && (tokens.get(j-1).equals("Keyword:int") || tokens.get(j-1).equals("Keyword:float") || tokens.get(j-1).equals("Keyword:void")))
                            {
                                parent = tokens.get(j).substring(11);
                                
                                for(int k=0; k<SYMTAB.size(); k++)
                                {
                                    if(SYMTAB.get(k).id.equals(parent))
                                    {
                                        parentFunction = SYMTAB.get(k);
                                    }
                                }
                                
                                found = true;
                            }
                        }
                        
                        j--;
                    }
                    
                    ArrayList<SymTabElement> prevVars = new ArrayList<SymTabElement>();
                    
                    for(int k=0; k<parentFunction.parameters.size(); k++)
                    {
                        SymTabElement temp = new SymTabElement(parentFunction.parameters.get(k), parentFunction.parameterTypes.get(k), 0 , parentFunction.parameterIsArray.get(k),false,null,null,null);
                        prevVars.add(temp);
                    }

                    int depthCount = 0;
                    int parenCount = 0;
                    for(int g=i; g>0; g--) //Get all previous variables from valid scopes
                    {
                        if(tokens.get(g).equals("}"))
                        {
                            depthCount ++;
                        }
                        else if(tokens.get(g).equals("{"))
                        {
                            if(depthCount > 0)
                            {
                                depthCount --;
                            }
                        }
                        else if(tokens.get(g).equals(")"))
                        {
                            parenCount++;
                        }
                        else if(tokens.get(g).equals("("))
                        {
                            if(parenCount>0)
                            {
                                parenCount--;
                            }
                        }

                        if(depthCount == 0 && parenCount == 0)
                        {
                            if(tokens.get(g).matches("Identifier:.*") && (tokens.get(g-1).equals("Keyword:int") || tokens.get(g-1).equals("Keyword:float")))
                            {
                                if(!tokens.get(g+1).equals("(") || tokens.get(g+1).equals("("))
                                {
                                    boolean foundit = false;
                                    for(int p=0; p<prevVars.size(); p++)
                                    {
                                        if(prevVars.get(p).id.equals(tokens.get(g).substring(11)))
                                        {
                                            foundit = true;
                                        }
                                    }
                                    
                                    if(foundit == false)
                                    {
                                        if(tokens.get(g+1).equals("["))
                                        {
                                            SymTabElement temp = new SymTabElement(tokens.get(g).substring(11), tokens.get(g-1).substring(8),0,true,false,null,null,null);
                                            prevVars.add(temp);
                                        }
                                        else
                                        {
                                            if(!tokens.get(g+1).equals("("))
                                            {
                                                SymTabElement temp = new SymTabElement(tokens.get(g).substring(11), tokens.get(g-1).substring(8),0,false,false,null,null,null);
                                                prevVars.add(temp);
                                            }
                                            else
                                            {
                                                SymTabElement temp = new SymTabElement(tokens.get(g).substring(11), tokens.get(g-1).substring(8),0,false,true,null,null,null);
                                                prevVars.add(temp);
                                            }
                                        }
                                    }
                                }
                            }
                        }  
                    }
                //=======================================================================
                
                if(tokens.get(i+1).equals("(") && !tokens.get(i-1).equals(")"))
                {
                    int index = i+2;
                    int depth = 1;
                    String typeRHS = ""; 
                    boolean firstType = true;
                    boolean inFunc = false;
                    int funcDepth = 0;
                    boolean violation = false;
                    
                    while(depth != 0)
                    {
                        if(!inFunc)
                        {
                            if(tokens.get(index).equals("("))
                            {
                                depth++;
                            }
                            else if(tokens.get(index).equals(")"))
                            {
                                depth--;
                            }
                            else if(tokens.get(index).matches("Int:.*"))
                            {
                                if(firstType)
                                {
                                    typeRHS = "int";
                                    firstType = false;
                                }
                                else
                                {
                                    if(!typeRHS.equals("int"))
                                    {
                                        violation = true;
                                    }
                                }
                            }
                            else if(tokens.get(index).matches("Float:.*"))
                            {
                                if(firstType)
                                {
                                    typeRHS = "float";
                                    firstType = false;
                                }
                                else
                                {
                                    if(!typeRHS.equals("float"))
                                    {
                                        violation = true;
                                    }
                                }
                            }
                            else if(tokens.get(index).matches("Identifier:.*"))
                            {
                                int var = 0;
                                for(int p=0; p<prevVars.size(); p++)
                                {
                                    if(prevVars.get(p).id.equals(tokens.get(index).substring(11)))
                                    {
                                        var = p;
                                    }
                                }
                                
                                if(firstType)
                                {
                                    typeRHS = prevVars.get(var).type;
                                    firstType = false;
                                    
                                    if(prevVars.get(var).isFunction)
                                    {
                                        inFunc = true;
                                        funcDepth = depth;
                                    }
                                }
                                else
                                {
                                    if(!typeRHS.equals(prevVars.get(var).type))
                                    {
                                        violation = true;
                                    }
                                    
                                    if(prevVars.get(var).isFunction)
                                    {
                                        inFunc = true;
                                        funcDepth = depth;
                                    }
                                }
                            }
                        }
                        else
                        {
                            if(tokens.get(index).equals("("))
                            {
                                depth++;
                            }
                            else if(tokens.get(index).equals(")"))
                            {
                                depth--;
                                
                                if(depth == funcDepth)
                                {
                                    inFunc = false;
                                }
                            }
                        }
                        
                        index++;
                    }
                    if(tokens.get(i-1).matches("Int:.*"))
                    {
                        if(!typeRHS.equals("int"))
                        {
                            violation = true;
                        }
                    }
                    else if(tokens.get(i-1).matches("Float:.*"))
                    {
                        if(!typeRHS.equals("float"))
                        {
                            violation = true;
                        }
                    }
                    else if(tokens.get(i-1).matches("Identifier:.*"))
                    {
                        int var = 0;
                        for(int p=0; p<prevVars.size(); p++)
                        {
                            if(prevVars.get(p).id.equals(tokens.get(i-1).substring(11)))
                            {
                                var = p;
                            }
                        }

                        if(!prevVars.get(var).type.equals(typeRHS))
                        {
                            violation = true;
                        }
                    }

                    if(violation == true)
                    {
                        semantics = false;
                        //System.out.println("Operator/operand disagreement involving parenthesis");
                    }
                    
                }
                
                
                else if(tokens.get(i-1).equals(")"))
                {
                    int firstParen = i-1;
                    int lastParen = i-1;
                    int depth1 = 1;
                    boolean violation = false;
                        
                    while(depth1 > 0)
                    {
                        firstParen --;
                            
                        if(tokens.get(firstParen).equals(")"))
                        {
                            depth1 ++;
                        }
                        else if(tokens.get(firstParen).equals("("))
                        {
                            depth1 --;
                        }
                    }
                    
                    boolean firstType = true;
                    String type = "";
                    int index = firstParen + 1;
                    int depth = 1;
                    boolean inFunc = false;
                    int funcDepth = 0;
                    
                    while(index < lastParen)
                    {
                        if(!inFunc)
                        {
                            if(tokens.get(index).equals("("))
                            {
                                depth++;
                            }
                            else if(tokens.get(index).equals(")"))
                            {
                                depth--;
                            }
                            else if(tokens.get(index).matches("Int:.*")) 
                           {
                               if(firstType)
                               {
                                   type = "int";
                                   firstType = false;
                               }
                               else
                               {
                                   if(!type.equals("int"))
                                   {
                                       violation = true;
                                   }
                               }
                           }
                           else if(tokens.get(index).matches("Float:.*")) 
                           {
                               if(firstType)
                               {
                                   type = "float";
                                   firstType = false;
                               }
                               else
                               {
                                   if(!type.equals("float"))
                                   {
                                       violation = true;
                                   }
                               }
                           }
                           else if(tokens.get(index).matches("Identifier:.*")) 
                           {
                               int var = 0;
                               
                               for(int p=0; p<prevVars.size(); p++)
                               {
                                   if(prevVars.get(p).id.equals(tokens.get(index).substring(11)))
                                   {
                                       var = p;
                                   }
                               }
                               
                               if(firstType)
                               {
                                   type = prevVars.get(var).type;
                                   firstType = false;
                               }
                               else
                               {
                                   if(!type.equals(prevVars.get(var).type))
                                   {
                                       violation = true;
                                   }
                               }
                               
                               if(prevVars.get(var).isFunction)
                               {
                                   inFunc = true;
                                   funcDepth = depth;
                               }
                           }
                        }
                        else
                        {
                            if(tokens.get(index).equals("("))
                            {
                                depth++;
                            }
                            else if(tokens.get(index).equals(")"))
                            {
                                depth--;
                                
                                if(depth == funcDepth)
                                {
                                    inFunc = false;
                                }
                            }
                        }
                        
                        
                        index++;
                    }
                    
                    int index2 = i+1;
                    boolean found2 = false;
                    
                    while(!found2)
                    {
                        if(tokens.get(index2).matches("Int:.*"))
                        {
                            if(!type.equals("int"))
                            {
                                violation = true;
                            }
                            found2 = true;
                        }
                        else if(tokens.get(index2).matches("Float:.*"))
                        {
                            if(!type.equals("float"))
                            {
                                violation = true;
                            }
                            found2 = true;
                        }
                        else if(tokens.get(index2).matches("Identifier:.*"))
                        {
                            int var = 0;

                            for(int p=0; p<prevVars.size(); p++)
                            {
                                if(prevVars.get(p).id.equals(tokens.get(index2).substring(11)))
                                {
                                    var = p;
                                }
                            }

                            if(!type.equals(prevVars.get(var).type))
                            {
                                violation = true;
                            }
                            found2 = true;
                        }
                        
                        index2++;
                    }
                    
                    if(violation == true)
                    {
                        semantics = false;
                        //System.out.println("Operator/operand disagreement regarding parenthesis 8===D");
                    }
                    
                }
                    
            }
        }
        
        //End check for operand/operand agreement in parenthesis=========================================================
        
        
        return semantics;
    }
}

class SymTabElement
{
    public String id;
    public String type;
    public int depth;
    public boolean isArray;
    public boolean isFunction;
    public ArrayList<String> parameters;
    public ArrayList<String> parameterTypes;
    public ArrayList<Boolean> parameterIsArray;
    
    public SymTabElement(String i, String t, int d, boolean a, boolean f, ArrayList<String> p, ArrayList<String> pt, ArrayList<Boolean> pia)
    {
        this.id = i;
        this.type = t;
        this.depth = d;
        this.isArray = a;
        this.isFunction = f;
        this.parameters = p;
        this.parameterTypes = pt;
        this.parameterIsArray = pia;
    }
}
