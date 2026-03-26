package lexer;

import lexer.readers.IReader;
import lexer.readers.SourceReader;
import tests.ILexer;

/**
 *  The Lexer class is responsible for scanning the source file
 *  which is a stream of characters and returning a stream of
 *  tokens; each token object will contain the string (or access
 *  to the string) that describes the token along with an
 *  indication of its location in the source program to be used
 *  for error reporting; we are tracking line numbers; white spaces
 *  are space, tab, newlines
 */
public class Lexer implements ILexer{
  private boolean atEOF = false;
  // next character to process
  private char ch;
  private IReader source;

  // positions in line of current token
  private int startPosition, endPosition, lineNumber;

  /**
   *  Lexer constructor
   * @param sourceFile is the name of the File to read the program source from
   */
  public Lexer( String sourceFile, boolean op) throws Exception {  //op = 0 will make constructor skip first read
    // init token table
    new TokenType();
    source = new SourceReader( sourceFile );
    if(op) {
      ch = source.read();
    }
  }

  public Lexer(IReader reader) throws Exception {
    // init token table
    new TokenType();
    this.source = reader;
    ch = source.read();
  }

  /**
   *  newIdTokens are either ids or reserved words; new id's will be inserted
   *  in the symbol table with an indication that they are id's
   *  @param id is the String just scanned - it's either an id or reserved word
   *  @param startPosition is the column in the source file where the token begins
   *  @param endPosition is the column in the source file where the token ends
   *  @return the Token; either an id or one for the reserved words
   */
  public Token newIdToken( String id, int startPosition, int endPosition, int lineNumber ) {
    //lineNumber = source.getLineno();
    return new Token(
      startPosition,
      endPosition,
      Symbol.symbol( id, Tokens.Identifier ) ,
      lineNumber
    );
  }
  /**
   *  number tokens are inserted in the symbol table; we don't convert the
   *  numeric strings to numbers until we load the bytecodes for interpreting;
   *  this ensures that any machine numeric dependencies are deferred
   *  until we actually run the program; i.e. the numeric constraints of the
   *  hardware used to compile the source program are not used
   *  @param number is the int String just scanned
   *  @param startPosition is the column in the source file where the int begins
   *  @param endPosition is the column in the source file where the int ends
   *  @return the int Token
   */
  public Token newNumberToken( String number, int startPosition, int endPosition, int lineNumber) {
    return new Token(
      startPosition,
      endPosition,
      Symbol.symbol( number, Tokens.INTeger ) ,
      lineNumber
    );
  }
  public Token newScientificToken( String number, int startPosition, int endPosition, int lineNumber) { 

    Symbol sym = Symbol.symbol( number, Tokens.BogusToken );
    return new Token(
      startPosition,
      endPosition,
      Symbol.symbol( number, Tokens.ScientificLit ) ,
      lineNumber
    );
  }
  public Token newStringLitToken( String stringLit, int startPosition, int endPosition, int lineNumber) {
    return new Token(
      startPosition,
      endPosition,
      Symbol.symbol( stringLit, Tokens.StringLit ),
      lineNumber
    );
  }
  /**
   *  build the token for operators (+ -) or separators (parens, braces)
   *  filter out comments which begin with two slashes
   *  @param tokenString is the String representing the token
   *  @param startPosition is the column in the source file where the token begins
   *  @param endPosition is the column in the source file where the token ends
   *  @return the Token just found
   */
  public Token makeToken( String tokenString, int startPosition, int endPosition, int lineNumber ) {
    // filter comments
    if( tokenString.equals("//") ) {
      try {
        int oldLine = source.getLineno();

        do {
          ch = source.read();
        } while( oldLine == source.getLineno() );
      } catch (Exception e) {
        atEOF = true;
      }

      return nextToken();
    }

    // ensure it's a valid token
    Symbol symbol = Symbol.symbol( tokenString, Tokens.BogusToken );

    if( symbol == null ) {
      System.out.println( "******** illegal character: " + tokenString );
      atEOF = true;
      return nextToken();
    }

    return new Token( startPosition, endPosition, symbol, lineNumber);
  }

  /**
   *  @return the next Token found in the source file
   */
  public Token nextToken() {
    // ch is always the next char to process
    if( atEOF ) {     //
      if( source != null ) {
        source.close();
        source = null;
      }

      return null;
    }

    try {
      // scan past whitespace
      while( Character.isWhitespace( ch )) {
        ch = source.read();
      }
    } catch( Exception e ) {
      atEOF = true;
      return nextToken();
    }

    startPosition = source.getPosition();
    endPosition = startPosition-1;
    lineNumber = source.getLineno();

    if( Character.isJavaIdentifierStart( ch )) {
      // return tokens for ids and reserved words
      String id = "";

      try {
        do {
          endPosition++;
          id += ch;
          ch = source.read();
        } while( Character.isJavaIdentifierPart( ch ));
      } catch( Exception e ) {
        atEOF = true;
      }

      return newIdToken( id, startPosition, endPosition, lineNumber );
    }
    
    if( ch == '"') {    
      // return string literal tokens
      String stringLit = "";
      
      endPosition++;

      try {

        ch = source.read();

        while(ch != '"' && source.getIsPrioEnd() == false ) {//set false when not end of line
          endPosition++;
          stringLit += ch;
          ch = source.read();
        }

        if (ch == '"') {  //valid ending
          ch = source.read();
          endPosition++;
          return newStringLitToken( stringLit, startPosition, endPosition, lineNumber );
        } else {        

          Symbol symbol = Symbol.symbol( stringLit, Tokens.BogusToken );

          if( symbol == null ) {
            System.out.println( "******** illegal character: " + stringLit );
            return nextToken();
          }

        }
       
      } catch( Exception e ) {
        atEOF = true;
      }
    }

    if( Character.isDigit( ch )) { //catch integer literals and scientific notation literals
      // return number tokens
      String number = "";

      try {   

        do {
          endPosition++;
          number += ch;
          ch = source.read();
        } while (Character.isDigit( ch ));

        if(ch == '.') {   //catches . in scientific notation
          endPosition++;
          number += ch;
          ch = source.read();
        } else {
          return newNumberToken( number, startPosition, endPosition, lineNumber );
        }

        if( Character.isDigit( ch )) {//checks first + second char after .
          endPosition++;
          number += ch;
          ch = source.read();
        } else{  
          Symbol symbol = Symbol.symbol( number, Tokens.BogusToken );

          if( symbol == null ) {
            System.out.println( "******** illegal character: " + ch );
            return nextToken();
          }
        }

        if( Character.isDigit( ch )) {//checks for second + second char after .
          endPosition++;
          number += ch;
          ch = source.read();
        }

        if(ch == 'e' || ch == 'E') {
          endPosition++;
          number += ch;
          ch = source.read();
        } else {
          Symbol symbol = Symbol.symbol( number, Tokens.BogusToken );

          if( symbol == null ) {
            System.out.println( "******** illegal character: " + ch );
            return nextToken();
          }
        }
        
        if(ch == '+' || ch == '-') {
          endPosition++;
          number += ch;
          ch = source.read();
        } else {
          Symbol symbol = Symbol.symbol( number, Tokens.BogusToken );

          if( symbol == null ) {
            System.out.println( "******** illegal character: " + ch );
            return nextToken();
          }
        }

        if (Character.isWhitespace( ch )) {
          Symbol symbol = Symbol.symbol( number, Tokens.BogusToken );
  
            if( symbol == null ) {
              System.out.println( "******** illegal character: " + ch );
              return nextToken();
            }
        }

        while( !Character.isWhitespace( ch )) {
          if (!Character.isDigit( ch )) {
            Symbol symbol = Symbol.symbol( number, Tokens.BogusToken );
  
            if( symbol == null ) {
              System.out.println( "******** illegal character: " + ch );
              return nextToken();
            }
          }
          endPosition++;
          number += ch;
          ch = source.read();
        }

      return newScientificToken( number, startPosition, endPosition, lineNumber );
      }  catch( Exception e ) {
        atEOF = true;
      }
    }

    // At this point the only tokens to check for are one or two
    // characters; we must also check for comments that begin with
    // 2 slashes
    String charOld = "" + ch;
    String op = charOld;
    Symbol sym;

    try {
      endPosition++;
      ch = source.read();
      op += ch;

      // check if valid 2 char operator; if it's not in the symbol
      // table then don't insert it since we really have a one char
      // token
      sym = Symbol.symbol( op, Tokens.BogusToken );
      
      if (sym == null) {
        // it must be a one char token
        return makeToken( charOld, startPosition, endPosition, lineNumber );
      }

      endPosition++;
      ch = source.read();
      return makeToken( op, startPosition, endPosition, lineNumber );
    } catch( Exception e ) { /* no-op */ }

    atEOF = true;

    if( startPosition == endPosition ) {
      op = charOld;
    }

    return makeToken( op, startPosition, endPosition, lineNumber );
  }
  public static void main(String args[]) {
    Token token;
    String filename = "";
    Lexer lex;

    try {
      if (args.length == 0) {
        filename = "simple.x";
        System.out.println("usage: java simple.x");
        lex = new Lexer( filename, true);
      } else {
        filename = args[0];
        lex = new Lexer( filename, true);
      }

      while( true ) {
        token = lex.nextToken();
        String s = String.format("%-8s  left: %-8d    right: %-8d    line: %-8d    %-8s",
        token.toString() , token.getLeftPosition(), token.getRightPosition(), token.getLineNumber(), token.getKind());
        System.out.println(s);
      }

    } catch (Exception e) {/*no-op */}

    System.out.println();

    try {   
      lex = new Lexer(filename, false);
      lex.source.printFile();
    } catch (Exception e) {}
  }
}