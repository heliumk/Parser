package ast;

import lexer.Symbol;
import lexer.Token;
import visitor.*;

public class ScientificTree extends AST {

    private Symbol symbol;

    public ScientificTree(Token token) {
        this.symbol = token.getSymbol();
    }
  
    public Object accept(ASTVisitor visitor) {
        return visitor.visitScientificTree(this);
    }
    
    public Symbol getSymbol() {
        return symbol;
    }
}
