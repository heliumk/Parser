package ast;

import visitor.*;

public class RangeExpTree extends AST{
    public RangeExpTree() {}

    public Object accept(ASTVisitor visitor) {
        return visitor.visitRangeExpTree(this);
    }
}
