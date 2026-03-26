package lexer.readers;

import java.io.*;

/**
 *  This class is used to manage the source program input stream;
 *  each read request will return the next usable character; it
 *  maintains the source column position of the character
*/
public class SourceReader implements IReader {
    public static final String READ_PREFIX = "READLINE:   ";

    private BufferedReader source;
    // line number of source program
    private int lineNumber = 0;
    // position of last character processed
    private int position;
    // if true then last character read was newline so read in the next line
    private boolean isPriorEndLine = true;
    private String nextLine;

  /**
   *  Construct a new SourceReader
   *  @param sourceFile the String describing the user's source file
   *  @exception IOException is thrown if there is an I/O problem
   */
  public SourceReader( String sourceFile ) throws IOException {
    //System.out.println( "Source file: " + sourceFile );
    //System.out.println( "user.dir: " + System.getProperty( "user.dir" ));
    source = new BufferedReader( new FileReader( sourceFile ));
  }

  public SourceReader(BufferedReader reader) throws IOException {
    this.source = reader;
  }

  public void close() {
    try {
      source.close();
    } catch( Exception e ) { /* no-op */ }
  }

  /**
   *  read next char; track line #, character position in line<br>
   *  return space for newline
   *  @return the character just read in
   *  @IOException is thrown for IO problems such as end of file
   */
  public char read() throws IOException {
    if( isPriorEndLine ) {
      lineNumber++;
      position = -1;
      nextLine = source.readLine();

      if( nextLine != null) {
        if("".equals(nextLine)){
          System.out.println( String.format("%3d:", lineNumber) );
          } else {
          System.out.println( String.format("%3d: %s", lineNumber, nextLine) );
          }
        
        
      }

      isPriorEndLine = false;
    }

    if( nextLine == null ) {
      // hit eof or some I/O problem
      throw new IOException();
    }

    if( nextLine.length() == 0 ) {
      isPriorEndLine = true;
      return ' ';
    }

    position++;
    if( position >= nextLine.length() ) {
      isPriorEndLine = true;
      return ' ';
    }

    return nextLine.charAt( position );
  }

  public void printFile () {
    try{
      lineNumber++;
      nextLine = source.readLine();
      do {
        System.out.println("  " + lineNumber + ": " + nextLine.toString());
        lineNumber++;
        nextLine = source.readLine();
      } while( nextLine != null );
    } catch (Exception e) {

    }
  }

  /**
   *  @return the position of the character just read in
   */
  public int getPosition() {
    return position;
  }

  /**
   *  @return the line number of the character just read in
   */
  public int getLineno() {
    return lineNumber;
  }

  public String getNextLine() {
    return nextLine;
  }

  public boolean getIsPrioEnd() {
    return isPriorEndLine;
  } 

/*
  public static void main( String args[] ) {
    SourceReader s = null;

    try {
      s = new SourceReader( "t" );

      while( true ) {
        char ch = s.read();
        System.out.println(
           "Char: " + ch + " Line: " + s.lineno + "position: " + s.position
        );
      }
    } catch( Exception e ) {}

    if( s != null ) {
      s.close();
    }
  }
*/
}