/**MINI-PROJECT : Lexical Analyser/Scanner
 * Comments, Identifiers, Keywords, Separators, Operators, Literals 
 * Achieved : Recognition of strings, comments
 */
import java.io.*;
import java.util.*;
import java.util.regex.*;

class LexicalAnalysis4 {
   final static int INIT=0;
   final static int SLASH=1;
   final static int LINE=2;
   final static int STAR=3;
   final static int BLOCK=4;
   final static int QUOTE=5;
   final static int IDENTIFIER_OR_KWD=6;
   final static int OPERATOR=7;
   final static int APOSTROPHE=8;
   final static int DOT=9;
   final static int INTEGER=10;
   final static int DOUBLE=11;
   
   public static void main()throws IOException {
      //Inputting the name of the file
      InputStreamReader read=new InputStreamReader(System.in);
      BufferedReader ab=new BufferedReader(read);
      System.out.print("Enter the path/name of the file to compile: ");
      String fileName=ab.readLine();
      lexAnalysis(fileName);
   }
   static void lexAnalysis(String fileName){
      FileReader fileReader=null;
      LineNumberReader lnr=null;
      int c;
      int state;
      try {
         //Declarations
         fileReader=new FileReader(fileName);
         lnr=new LineNumberReader(fileReader);
         lnr.setLineNumber(1);
         System.out.println("\nProcessing...");
         
         //Creating the file to be passed to the parser
         File file=new File("Lexed.txt");
         if(!file.exists()) {
            file.createNewFile();
         }
         FileWriter fw=new FileWriter(file.getAbsoluteFile());
         BufferedWriter bw=new BufferedWriter(fw);
         //Declarations and Initialisations to be used in recognition
         state=INIT;
         String lString="";            //to store the string literal
         String identifierOrKwd="";
         String operator="";
         int lChar;
         String lNumeric="";
         Pattern pKwd=Pattern.compile("abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|double|do|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throws|throw|transient|try|void|volatile|while");
         Matcher mKwd;
         
         while((c=lnr.read())!=-1) {
               switch(state) {
                  case INIT:
                     if(c=='/') {state=SLASH; }
                     else if(c=='"') {state=QUOTE; }
                     else if(c=='.') {state=DOT; lnr.mark(0); }
                     else if(Character.isDigit(c)) { lNumeric+=(char)c; state=INTEGER; lnr.mark(0); }
                     else if(Character.isJavaIdentifierStart(c)) {state=IDENTIFIER_OR_KWD; identifierOrKwd+=(char)c; lnr.mark(0);}
                     else if(c==';'||c==','||c=='('||c==')'||c=='{'||c=='}'||c=='['||c==']') {System.out.print("Separator"); bw.write("SEPARATOR ");}
                     else if(c=='='||c=='>'||c=='<'||c=='!'||c=='~'||c=='?'||c==':'||c=='&'||c=='|'||c=='+'||c=='-'||c=='*'||c=='^'||c=='%') {
                        state=OPERATOR; operator+=(char)c; lnr.mark(0);
                     }
                     else if(c=='\'') {state=APOSTROPHE; }
                     else {System.out.print((char)c); bw.write((char)c); }
                     break;
                  case SLASH:
                     if(c=='/') {state=LINE; }
                     else if(c=='*') {state=BLOCK; }
                     else {state=INIT; System.out.print("Operator"); bw.write("OPERATOR "); }
                     break;
                  case LINE:
                     if(c=='\n') {state=INIT;}
                     break;
                  case STAR:
                     if(c=='*') {state=STAR;}
                     else if(c=='/') {state=INIT; }
                     else {state=BLOCK; }
                  case BLOCK:
                     if(c=='*') {state=STAR; }
                     break;
                  case QUOTE:
                     if(c=='"') {System.out.print("String");state=INIT; lString=""; bw.write("STRING ");break;}
                     else if(c=='\\') {c=lnr.read(); }
                     lString+=(char)c;
                     break;
                  case IDENTIFIER_OR_KWD:
                     if(!(Character.isJavaIdentifierPart(c))) {
                        mKwd=pKwd.matcher(identifierOrKwd);
                        lnr.reset();
                        state=INIT;   
                        if(mKwd.matches()) {System.out.print("Keyword"); bw.write("KEYWORD "); }
                        else {System.out.print("Identifier"); bw.write("IDENTIFIER "); }
                        identifierOrKwd="";
                     }
                     else { identifierOrKwd+=(char)c; lnr.mark(0); }
                     break;
                  case OPERATOR:
                     if(!(c=='='||c=='&'||c=='|'||c=='+'||c=='-'||c=='>'||c=='<')) {
                        lnr.reset();
                        state=INIT;
                        System.out.print("Operator");
                        bw.write("OPERATOR ");
                        operator="";
                     }
                     else {operator+=(char)c; lnr.mark(0); }
                     break; 
                  case APOSTROPHE:
                     lChar=(char)c;
                     if(lChar!=-1 && lnr.read()=='\''){
                        state=INIT;
                        System.out.print("Character");
                        bw.write("CHARACTER ");
                     }
                     //else generate exception
                     break;
                  case INTEGER:
                     if(Character.isDigit(c)) { lNumeric+=(char)c; lnr.mark(0); }
                     else if(c=='.') { lNumeric+="."; state=DOUBLE; }
                     else if(c=='l') { lNumeric+="l"; state=INIT; System.out.print("Integer"); bw.write("INTEGER "); }
                     else { lnr.reset(); lNumeric=""; state=INIT; System.out.print("Integer"); bw.write("INTEGER"); }
                     break;
                  case DOUBLE:
                     if(Character.isDigit(c)) { lNumeric+=(char)c; lnr.mark(0); }
                     else if(c=='f') { System.out.print("Float"); bw.write("FLOAT "); state=INIT; }
                     else if(c=='d') { System.out.print("Double"); bw.write("DOUBLE "); state=INIT; }
                     else {System.out.print("Double"); bw.write("DOUBLE "); lnr.reset(); lNumeric=""; state=INIT; }
                     break;
                  case DOT:
                     if(Character.isJavaIdentifierStart(c)) { System.out.print("Separator"); bw.write("SEPARATOR "); state=INIT; }
                     else if(Character.isDigit(c)) { state=DOUBLE; }
                     else if(c=='*') { state=OPERATOR; }
                     lnr.reset();
                     break;
               }            //switch ends
         }                  //while closes
         bw.close();
      }
      catch(FileNotFoundException fnfe) {
         System.out.println("The requested file could NOT be found");
      }
      catch(IOException ioe) {
         System.out.println("An error occurred while reading from/writing to the file");
      }
   }
}