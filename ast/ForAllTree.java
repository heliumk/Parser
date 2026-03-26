package ast;

import visitor.*;

public class ForAllTree extends AST{
    public ForAllTree() {}

    public Object accept(ASTVisitor visitor) {
        return visitor.visitForAllTree(this);
    }
}
