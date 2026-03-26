package visitor;

/*
created to store coordinate information from offset visitor

*/
public class Position {
    Integer x = null;
    Integer y = null;

    Position(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer i) {
        this.x = i;
    }

    public void updateX(Integer i) {
        this.x = x + i; 
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer i) {
        this.y = i;
    }
}
