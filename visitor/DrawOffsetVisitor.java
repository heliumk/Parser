package visitor;

import ast.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 *
 * @author Lowell Milliken
 */
public class DrawOffsetVisitor extends ASTVisitor {


  private final int nodew = 100;
  private final int nodeh = 30;
  private final int vertSep = 50;
  private final int horizSep = 30;

  private int width;
  private int height;

  private int[] nCount;
  private int[] progress;
  private int depth = 0;
  private BufferedImage bimg;
  private Graphics2D g2;

  public static HashMap<Integer,Position> offsetHashMap = new HashMap<Integer,Position>();

  public DrawOffsetVisitor(int[] nCount, HashMap<Integer, Position> offsetMap, Position dimensions) {
    this.nCount = nCount;
    progress = new int[nCount.length];
    offsetHashMap = offsetMap;

    width = dimensions.getX() * (nodew + horizSep);
    height = dimensions.getY() * (nodeh + vertSep);

    g2 = createGraphics2D(width, height);
  }

  public void draw(String s, AST t) {
    int hstep = nodew + horizSep;
    int vstep = nodeh + vertSep;

    //

    int x = offsetHashMap.get(t.getNodeNum()).getX() * hstep/2 + hstep*5;
    int y = offsetHashMap.get(t.getNodeNum()).getY() * vstep/2;
    g2.setColor(Color.black);
    g2.drawOval(x, y, nodew, nodeh);
    g2.setColor(Color.BLACK);
    g2.drawString(s, x + 10, y + 2 * nodeh / 3);

    
    int startx = x + nodew / 2;
    int starty = y + nodeh;
    int endx;
    int endy;
    g2.setColor(Color.black);
    
    for (AST kid : t.getKids()) {
      endx = offsetHashMap.get(kid.getNodeNum()).getX() * hstep/2  + hstep * 5 + nodew / 2;
      endy = (depth + 1) * vstep/2;
      g2.drawLine(startx, starty, endx, endy);
    }
    
    progress[depth]++;
    depth++;
    visitKids(t);
    depth--;
  }

  private Graphics2D createGraphics2D(int w, int h) {
    Graphics2D g2;

    if (bimg == null || bimg.getWidth() != w || bimg.getHeight() != h) {
      bimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    }

    g2 = bimg.createGraphics();
    g2.setBackground(Color.WHITE);
    g2.setRenderingHint(
      RenderingHints.KEY_RENDERING,
      RenderingHints.VALUE_RENDER_QUALITY
    );
    g2.clearRect(0, 0, w, h);

    return g2;
  }

  public BufferedImage getImage() {
    return bimg;
  }

  public Object visitProgramTree(AST t) {
    draw("Program", t);
    return null;
  }

  public Object visitBlockTree(AST t) {
    draw("Block", t);
    return null;
  }

  public Object visitFunctionDeclTree(AST t) {
    draw("FunctionDecl", t);
    return null;
  }

  public Object visitCallTree(AST t) {
    draw("Call", t);
    return null;
  }

  public Object visitDeclTree(AST t) {
    draw("Decl", t);
    return null;
  }

  public Object visitIntTypeTree(AST t) {
    draw("IntType", t);
    return null;
  }

  public Object visitBoolTypeTree(AST t) {
    draw("BoolType", t);
    return null;
  }

  public Object visitFormalsTree(AST t) {
    draw("Formals", t);
    return null;
  }

  public Object visitActualArgsTree(AST t) {
    draw("ActualArgs", t);
    return null;
  }

  public Object visitIfTree(AST t) {
    draw("If", t);
    return null;
  }

  public Object visitWhileTree(AST t) {
    draw("While", t);
    return null;
  }

  public Object visitReturnTree(AST t) {
    draw("Return", t);
    return null;
  }

  public Object visitAssignTree(AST t) {
    draw("Assign", t);
    return null;
  }

  public Object visitIntTree(AST t) {
    draw("Int: " + ((IntTree) t).getSymbol().toString(), t);
    return null;
  }

  public Object visitIdTree(AST t) {
    draw("Id: " + ((IdTree) t).getSymbol().toString(), t);
    return null;
  }

  public Object visitRelOpTree(AST t) {
    draw("RelOp: " + ((RelOpTree) t).getSymbol().toString(), t);
    return null;
  }

  public Object visitAddOpTree(AST t) {
    draw("AddOp: " + ((AddOpTree) t).getSymbol().toString(), t);
    return null;
  }

  public Object visitMultOpTree(AST t) {
    draw("MultOp: " + ((MultOpTree) t).getSymbol().toString(), t);
    return null;
  }

  public Object visitStringTree(AST t) {
    draw("String: " + ((StringTree) t).getSymbol().toString(), t);
    return null;
  }

  public Object visitStringTypeTree(AST t) {
    draw("StringType", t);
    return null;
  }

  public Object visitScientificTree(AST t) {
    draw("Scientific: " + ((ScientificTree) t).getSymbol().toString(), t);
    return null;
  }

  public Object visitScientificTypeTree(AST t) {
    draw("ScientificType", t);
    return null;
  }

  public Object visitForAllTree(AST t) {
    draw("ForAll", t);
    return null;
  }

  public Object visitRangeExpTree(AST t) {
    draw("RangeExp", t);
    return null;
  }
}