package parser;

import ast.*;
import java.util.*;
import lexer.*;
import tests.ILexer;

/**
 * The Parser class performs recursive-descent parsing; as a by-product it will
 * build the <b>Abstract Syntax Tree</b> representation for the source
 * program
 * Following is the Grammar we are using:
 *
 *  PROGRAM -> 'program' BLOCK ==> program
 *
 *  BLOCK -> '{' D* S* '}'  ==> block
 *
 *  D -> TYPE NAME                    ==> decl
 *    -> TYPE NAME FUNHEAD BLOCK      ==> functionDecl
 *
 *  TYPE  ->  'int'
 *        ->  'boolean'
 *
 *  FUNHEAD  -> '(' (D list ',')? ')'  ==> formals
 *
 *  S -> 'if' E 'then' BLOCK 'else' BLOCK  ==> if
 *    -> 'while' E BLOCK               ==> while
 *    -> 'return' E                    ==> return
 *    -> BLOCK
 *    -> NAME '=' E                    ==> assign
 *
 *  E -> SE
 *    -> SE '==' SE   ==> =
 *    -> SE '!=' SE   ==> !=
 *    -> SE '<'  SE   ==> <
 *    -> SE '<=' SE   ==> <=
 *
 *  SE  ->  T
 *      ->  SE '+' T  ==> +
 *      ->  SE '-' T  ==> -
 *      ->  SE '|' T  ==> or
 *
 *  T  -> F
 *     -> T '*' F  ==> *
 *     -> T '/' F  ==> /
 *     -> T '&' F  ==> and
 *
 *  F  -> '(' E ')'
 *     -> NAME
 *     -> <int>
 *     -> NAME '(' (E list ',')? ')' ==> call
 *
 *  NAME  -> <id>
 */
public class Parser {

  private Token currentToken;
  private ILexer lex;
  private EnumSet<Tokens> relationalOps = EnumSet.of(
    Tokens.Equal,
    Tokens.NotEqual,
    Tokens.Less,
    Tokens.LessEqual,
    Tokens.Greater,
    Tokens.GreaterEqual
  );
  private EnumSet<Tokens> addingOps = EnumSet.of(
    Tokens.Plus,
    Tokens.Minus,
    Tokens.Or
  );
  private EnumSet<Tokens> multiplyingOps = EnumSet.of(
    Tokens.Multiply,
    Tokens.Divide,
    Tokens.And
  );

  /**
   * Construct a new Parser;
   *
   * @param sourceProgram - source file name
   * @exception Exception - thrown for any problems at startup (e.g. I/O)
   */
  public Parser(String sourceProgram) throws Exception {
    try {
      lex = new Lexer(sourceProgram, true);
      scan();
    } catch (Exception e) {
      System.out.println("********exception*******" + e.toString());
      throw e;
    }
  }

  // Constructor used for testing
  public Parser(ILexer lexer) throws Exception {
    new TokenType();
    lex = lexer;
    scan();
  }

  public Lexer getLex() {
    return (Lexer)lex;
  }

  /**
   * Execute the parse command
   *
   * @return the AST for the source program
   * @exception Exception - pass on any type of exception raised
   */
  public AST execute() throws Exception {
    try {
      return rProgram();
    } catch (SyntaxError e) {
      e.print();
      throw e;
    }
  }

  /**
   * Program -> 'program' block ==> program
   *
   * @return the program tree
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rProgram() throws SyntaxError {
    // note that rProgram actually returns a ProgramTree; we use the
    // principle of substitutability to indicate it returns an AST
    AST t = new ProgramTree();
    expect(Tokens.Program);
    t.addKid(rBlock());
    return t;
  }

  /**
   * block -> '{' d* s* '}'       ==> block
   *
   * @return block tree
   * @exception SyntaxError - thrown for any syntax error e.g. an expected
   * left brace isn't found
   */
  public AST rBlock() throws SyntaxError {
    expect(Tokens.LeftBrace);
    AST t = new BlockTree();

    // Get declarations until there are no more matches for declarations
    while (startingDecl()) {
      t.addKid(rDecl());
    }

    // Get statements until there are no more matches for statements
    while (startingStatement()) {
      t.addKid(rStatement());
    }

    expect(Tokens.RightBrace);

    return t;
  }

  boolean startingDecl() {
    return isNextTok(Tokens.Int) || isNextTok(Tokens.BOOLean) ||
    isNextTok(Tokens.StringType) || isNextTok(Tokens.Scientific);
  }

  boolean startingStatement() {
    return (
      isNextTok(Tokens.If) ||
      isNextTok(Tokens.While) ||
      isNextTok(Tokens.Return) ||
      isNextTok(Tokens.LeftBrace) ||
      isNextTok(Tokens.Identifier) ||
      isNextTok(Tokens.Forall)          
    );
  }

  /**
   * d -> type name                ==> decl
   * d -> type name funcHead block ==> functionDecl
   *
   * @return either the decl tree or the functionDecl tree
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rDecl() throws SyntaxError {
    AST t, t1;
    t = rType();
    t1 = rName();

    // A LeftParen indicates that this must be a function
    // (this is the beginning of the formal parameters list)
    if (isNextTok(Tokens.LeftParen)) {
      t = (new FunctionDeclTree()).addKid(t).addKid(t1);
      t.addKid(rFuncHead());
      t.addKid(rBlock());
      return t;
    }
    t = (new DeclTree()).addKid(t).addKid(t1);

    return t;
  }

  /**
   * type -> 'int'
   * type -> 'bool'
   *
   * @return either the intType or boolType tree
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rType() throws SyntaxError {
    AST t;

    if (isNextTok(Tokens.Int)) {
      t = new IntTypeTree();
      scan();
    } else if(isNextTok(Tokens.BOOLean)){
      t = new BoolTypeTree();
      scan();
    } else if(isNextTok(Tokens.StringType)){
      t = new StringTypeTree();
      scan();
    } else {                           
      expect(Tokens.Scientific);
      t = new ScientificTypeTree();
    }
    return t;
  }

  /**
   * funcHead -> '(' (decl list ',')? ')' ==> formals
   *
   * note a funchead is a list of zero or more decl's,
   * separated by commas, all in parens
   *
   * @return the formals tree describing this list of formals
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rFuncHead() throws SyntaxError {
    AST t = new FormalsTree();
    expect(Tokens.LeftParen);

    if (!isNextTok(Tokens.RightParen)) {
      do {
        t.addKid(rDecl());
        if (isNextTok(Tokens.Comma)) {
          scan();
        } else {
          break;
        }
      } while (true);
    }

    expect(Tokens.RightParen);

    return t;
  }

  /**
   * S -> 'if' e 'then' block 'else' block  ==> if
   *   -> 'if' e 'then' block               ==> if no else  //
   *   -> 'while' e block                   ==> while
   *   -> 'return' e                        ==> return
   *   -> block                             ==> block
   *   -> name '=' e                        ==> assign
   *   -> 'forall' decl 'in' rangexp lock   ==> forall
   *
   * @return the tree corresponding to the statement found
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rStatement() throws SyntaxError {
    AST t;
    if (isNextTok(Tokens.If)) {
      scan();
      t = new IfTree();

      t.addKid(rExpr());

      expect(Tokens.Then);
      t.addKid(rBlock());

      if (isNextTok(Tokens.Else)) {
        scan();
        t.addKid(rBlock());
      }

      return t;
    } else if (isNextTok(Tokens.While)) {
      scan();
      t = new WhileTree();

      t.addKid(rExpr());
      t.addKid(rBlock());

      return t;
    } else if (isNextTok(Tokens.Return)) {
      scan();
      t = new ReturnTree();

      t.addKid(rExpr());

      return t;
    } else if (isNextTok(Tokens.LeftBrace)) {
      return rBlock();
    } else if (isNextTok(Tokens.Forall)) {
      scan();
      t = new ForAllTree();

      t.addKid(rDecl());

      expect(Tokens.In);
      t.addKid(rRangeExp());

      t.addKid(rBlock());
      return t;
    }

    t = rName();                         
    t = (new AssignTree()).addKid(t);

    expect(Tokens.Assign);

    t.addKid(rExpr());

    return t;
  }

  /**
   * e -> se
   *   -> se '==' se ==> =
   *   -> se '!=' se ==> !=
   *   -> se '<' se  ==> <
   *   -> se '<=' se ==> <=
   *   -> se '>' se  ==> >
   *   -> se '>=' se ==> >=
   *
   * @return the tree corresponding to the expression
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rExpr() throws SyntaxError {
    AST t, kid = rSimpleExpr();

    t = getRelationTree();
    if (t == null) {
      return kid;
    }

    t.addKid(kid);
    t.addKid(rSimpleExpr());

    return t;
  }

  /**
   * se -> t
   *    -> se '+' t ==> +
   *    -> se '-' t ==> -
   *    -> se '|' t ==> or
   *
   * This rule indicates we should pick up as many <i>t</i>'s as
   * possible; the <i>t</i>'s will be left associative
   *
   * @return the tree corresponding to the adding expression
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rSimpleExpr() throws SyntaxError {
    AST t, kid = rTerm();

    while ((t = getAddOperTree()) != null) {
      t.addKid(kid);
      t.addKid(rTerm());

      kid = t;
    }

    return kid;
  }

  /**
   * t -> f
   *   -> t '*' f ==> *
   *   -> t '/' f ==> /
   *   -> t '&' f ==> and
   *
   * This rule indicates we should pick up as many <i>f</i>'s as
   * possible; the <i>f</i>'s will be left associative
   *
   * @return the tree corresponding to the multiplying expression
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rTerm() throws SyntaxError {
    AST t, kid = rFactor();

    while ((t = getMultOperTree()) != null) {
      t.addKid(kid);
      t.addKid(rFactor());

      kid = t;
    }

    return kid;
  }

  /**
   * f -> '(' e ')'
   *   -> name
   *   -> <int>
   *   -> <string>
   *   -> <scientific> 
   *   -> name '(' (e list ',')? ')'     ==> call
   *
   * @return the tree corresponding to the factor expression
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rFactor() throws SyntaxError {
    AST t;

    // -> (e)
    if (isNextTok(Tokens.LeftParen)) {
      scan();
      t = rExpr();
      expect(Tokens.RightParen);
      return t;
    }
    // -> <int>
    else if (isNextTok(Tokens.INTeger)) {
      t = new IntTree(currentToken);
      scan();
      return t;
    }
    // -> <string>
    else if (isNextTok(Tokens.StringLit)) {
      t = new StringTree(currentToken);
      scan();
      return t;
    }
    // -> <scientific>
    else if (isNextTok(Tokens.ScientificLit)) {
      t = new ScientificTree(currentToken);
      scan();
      return t;
    }

    t = rName();
    //  -> name (not a function call)
    if (!isNextTok(Tokens.LeftParen)) {
      return t;
    }

    // -> name '(' (e list ',')? ) ==> call
    scan();
    t = (new CallTree()).addKid(t);

    if (!isNextTok(Tokens.RightParen)) {
      do {
        t.addKid(rExpr());
        if (isNextTok(Tokens.Comma)) {
          scan();
        } else {
          break;
        }
      } while (true);
    }
    expect(Tokens.RightParen);

    return t;
  }

  /**
   * name -> <id> //identifier
   *
   * @return the id tree
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rName() throws SyntaxError {
    AST t;

    if (isNextTok(Tokens.Identifier)) {
      t = new IdTree(currentToken);
      scan();

      return t;
    }
    throw new SyntaxError(currentToken, Tokens.Identifier);
  }

  /**
   * 
   *
   * @return the RangeExp tree
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rRangeExp() throws SyntaxError {   
    AST t = new RangeExpTree();

    expect(Tokens.LeftBracket);
    t.addKid(rExpr());
    expect(Tokens.Range);
    t.addKid(rExpr());
    expect(Tokens.RightBracket);

    return t;
  }

  // build tree with current token's relation
  private AST getRelationTree() {
    Tokens kind = currentToken.getKind();

    if (relationalOps.contains(kind)) {
      AST t = new RelOpTree(currentToken);
      scan();

      return t;
    } else {
      return null;
    }
  }

  private AST getAddOperTree() {
    Tokens kind = currentToken.getKind();

    if (addingOps.contains(kind)) {
      AST t = new AddOpTree(currentToken);
      scan();

      return t;
    } else {
      return null;
    }
  }

  private AST getMultOperTree() {
    Tokens kind = currentToken.getKind();

    if (multiplyingOps.contains(kind)) {
      AST t = new MultOpTree(currentToken);
      scan();

      return t;
    } else {
      return null;
    }
  }

  private boolean isNextTok(Tokens kind) {
    return currentToken != null && currentToken.getKind() == kind;
  }

  private void expect(Tokens kind) throws SyntaxError {
    if (isNextTok(kind)) {
      scan();

      return;
    }
    throw new SyntaxError(currentToken, kind);
  }

  private void scan() {
    currentToken = lex.nextToken();

    // This is debug printout (and should not appear in final submission)
   /* if (currentToken != null) {
      currentToken.print();
    }*/

    return;
  }
}

class SyntaxError extends Exception {

  private static final long serialVersionUID = 1L;
  private Token tokenFound;
  private Tokens kindExpected;

  /**
   * record the syntax error just encountered
   *
   * @param tokenFound is the token just found by the parser
   * @param kindExpected is the token we expected to find based on the current
   * context
   */
  public SyntaxError(Token tokenFound, Tokens kindExpected) {
    this.tokenFound = tokenFound;
    this.kindExpected = kindExpected;
  }

  void print() {
    System.out.println("Expected: " + kindExpected);
    return;
  }

  @Override
  public String toString() {
      return String.format("Expected [%s], found [%s]", kindExpected, tokenFound);
  }
}