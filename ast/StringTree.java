package ast;

import lexer.Symbol;
import lexer.Token;
import visitor.*;

public class StringTree extends AST {
  
    private Symbol symbol;

    public StringTree(Token token) {
        this.symbol = token.getSymbol();
    }

    public Object accept(ASTVisitor visitor) {
        return visitor.visitStringTree(this);
    }
    
    public Symbol getSymbol() {
        return symbol;
    }
}
