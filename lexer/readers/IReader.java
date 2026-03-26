package lexer.readers;

import java.io.IOException;

public interface IReader {
    public char read() throws IOException;
    public int getPosition();
    public int getLineno();
    public void close();
    public String toString();
    public void printFile ();
    public boolean getIsPrioEnd();
    public String getNextLine();
}