package visitor;

import ast.AST;
import java.util.*;

public class OffsetVisitor extends ASTVisitor {

    public static HashMap<Integer,Position> offsetMap = new HashMap<Integer,Position>();

    private int[] tCount = new int[100];
    private int[] nCount = new int[100];
    private int depth = 0;
    private int maxDepth = 0;
    private int maxOffset = 0;
    private int result = 0;              //holds update offset
    private Position offset;

    private void countOff( AST t ) {

        tCount[depth]++;

        if (depth > maxDepth) {
            maxDepth = depth;
          }

        if (hasKids(t)) {
            depth++;
            visitKids(t);
            depth--;
            calcParentOffSet(t);
        } else {
            offset = new Position(nCount[depth], depth);

            if (nCount[depth] > maxOffset) {
                maxOffset = nCount[depth];
              }

            nCount[depth] = nCount[depth] + 2;
            offsetMap.put(t.getNodeNum(), offset);
        }
      }

    public HashMap<Integer, Position> getOffHashMap(){
        return offsetMap;
    }

    public void printCoordinates () {
        int i = 1;
        while (offsetMap.get(i) != null) {
            System.out.println(i+": Offset: " + offsetMap.get(i).getX() + ": Depth: " +offsetMap.get(i).getY());
            i++;
        }
    }

    public void calcParentOffSet(AST t) {

        int leftMost = 0;
        int rightMost = 0;
        ArrayList<AST> kids= t.getKids();

        leftMost = offsetMap.get(kids.get(0).getNodeNum()).getX();
        
        for (AST kid : kids) {
            if (offsetMap.get(kid.getNodeNum()).getX() > rightMost) {
                rightMost = offsetMap.get(kid.getNodeNum()).getX();
            }
          }

        result = (leftMost + rightMost)/2;
        offset = new Position(result, depth);
        offsetMap.put(t.getNodeNum(), offset);

        if (result < nCount[depth]) {
            int g = nCount[depth] - result;
            updateKids(t, g);
        }

        nCount[depth] = offsetMap.get(t.getNodeNum()).getX() + 2;
    }

    public void updateKids(AST t, int off) {

        for (AST kid : t.getKids()) {
            depth++;
            updateKids(kid, off);
            depth--;
          }

          offsetMap.get(t.getNodeNum()).updateX(off);

          if (offsetMap.get(t.getNodeNum()).getX() >= maxOffset) {
            maxOffset = offsetMap.get(t.getNodeNum()).getX();
          }
    }

    public Position getDimensions () {
        Position dimensions = new Position(maxOffset, maxDepth);
        return dimensions;
      }

      public int[] getTCount () {
        return tCount;
      }  

    public int[] getCount() {
        int[] count = new int[maxDepth + 1];
    
        for (int i = 0; i <= maxDepth; i++) {
          count[i] = nCount[i];
        }
    
        return count;
      }
    
    public int getMaxDepth() {
        return maxDepth;
      }

    public int getMaxOffset() {
        return maxOffset;
      }
    
    public Boolean hasKids(AST t){
        if (t.kidCount() > 0) {
            return true;     
        } else {
            return false;
        }
    }

    public Object visitProgramTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitBlockTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitFunctionDeclTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitCallTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitDeclTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitIntTypeTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitBoolTypeTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitFormalsTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitActualArgsTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitIfTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitWhileTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitForTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitReturnTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitAssignTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitIntTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitFloatTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitIdTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitRelOpTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitAddOpTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitMultOpTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitStringTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitStringTypeTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitScientificTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitScientificTypeTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitForAllTree(AST t) {
        countOff(t);
        return null;
    }

    public Object visitRangeExpTree(AST t) {
        countOff(t);
        return null;
    }
}    
