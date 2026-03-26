package tests.helpers;

import java.util.HashMap;
import java.util.StringTokenizer;

import ast.*;
import lexer.Symbol;
import lexer.Token;
import lexer.Tokens;
import tests.ILexer;


public class Helpers {
    private static HashMap<String, Token> testTokens = new HashMap<>();
    static {    
        // Reserved words
        testTokens.put("program", new Token(0, 0, Symbol.symbol("program", Tokens.Program)));
        testTokens.put("if", new Token(0, 0, Symbol.symbol("if", Tokens.If)));
        testTokens.put("then", new Token(0, 0, Symbol.symbol("then", Tokens.Then)));
        testTokens.put("else", new Token(0, 0, Symbol.symbol("else", Tokens.Else)));
        testTokens.put("while", new Token(0, 0, Symbol.symbol("while", Tokens.While)));
        testTokens.put("function", new Token(0, 0, Symbol.symbol("function", Tokens.Function)));
        testTokens.put("return", new Token(0, 0, Symbol.symbol("return", Tokens.Return)));
        testTokens.put("forall", new Token(0, 0, Symbol.symbol("forall", Tokens.Forall)));
        testTokens.put("in", new Token(0, 0, Symbol.symbol("in", Tokens.In)));
        
        testTokens.put("{", new Token(0, 0, Symbol.symbol("{", Tokens.LeftBrace)));
        testTokens.put("}", new Token(0, 0, Symbol.symbol("}", Tokens.RightBrace)));
        testTokens.put("(", new Token(0, 0, Symbol.symbol("(", Tokens.LeftParen)));
        testTokens.put(")", new Token(0, 0, Symbol.symbol(")", Tokens.RightParen)));
        testTokens.put("[", new Token(0, 0, Symbol.symbol("[", Tokens.LeftBracket)));
        testTokens.put("]", new Token(0, 0, Symbol.symbol("]", Tokens.RightBracket)));
        testTokens.put(",", new Token(0, 0, Symbol.symbol(",", Tokens.Comma)));
        testTokens.put("..", new Token(0, 0, Symbol.symbol("..", Tokens.Range)));

        // Operators
        testTokens.put("=", new Token(0, 0, Symbol.symbol("=", Tokens.Assign)));
        testTokens.put("+", new Token(0, 0, Symbol.symbol("+", Tokens.Plus)));
        testTokens.put("-", new Token(0, 0, Symbol.symbol("-", Tokens.Minus)));
        testTokens.put("|", new Token(0, 0, Symbol.symbol("|", Tokens.Or)));
        testTokens.put("&", new Token(0, 0, Symbol.symbol("&", Tokens.And)));
        testTokens.put("*", new Token(0, 0, Symbol.symbol("*", Tokens.Multiply)));
        testTokens.put("/", new Token(0, 0, Symbol.symbol("/", Tokens.Divide)));

        // Relational Operators
        testTokens.put("==", new Token(0, 0, Symbol.symbol("==", Tokens.Equal)));
        testTokens.put("!=", new Token(0, 0, Symbol.symbol("!=", Tokens.NotEqual)));
        testTokens.put("<", new Token(0, 0, Symbol.symbol("<", Tokens.Less)));
        testTokens.put("<=", new Token(0, 0, Symbol.symbol("<=", Tokens.LessEqual)));
        testTokens.put(">", new Token(0, 0, Symbol.symbol(">", Tokens.Greater)));
        testTokens.put(">=", new Token(0, 0, Symbol.symbol(">=", Tokens.GreaterEqual)));

        // Types
        testTokens.put("int", new Token(0, 0, Symbol.symbol("int", Tokens.Int)));
        testTokens.put("<int>", new Token(0, 0, Symbol.symbol("42", Tokens.INTeger)));
        testTokens.put("boolean", new Token(0, 0, Symbol.symbol("boolean", Tokens.BOOLean)));
        testTokens.put("<boolean>", new Token(0, 0, Symbol.symbol("42", Tokens.INTeger)));
        testTokens.put("string", new Token(0, 0, Symbol.symbol("string", Tokens.StringType)));
        testTokens.put("<string>", new Token(0, 0, Symbol.symbol("This is a string", Tokens.StringLit)));
        testTokens.put("scientific", new Token(0, 0, Symbol.symbol("scientific", Tokens.Scientific)));
        testTokens.put("<scientific>", new Token(0, 0, Symbol.symbol("1.23e+456", Tokens.ScientificLit)));
        
        // Identifiers
        testTokens.put("<id>", new Token(0, 0, Symbol.symbol("x", Tokens.Identifier)));
    }

    public static Token tt(String token) {
        return testTokens.get(token);
    }

    private static HashMap<String, AST> treeMappings = new HashMap<>();
    static {
        treeMappings.put("int", new IntTypeTree());
        treeMappings.put("<int>", new IntTree(Helpers.tt("<int>")));
        treeMappings.put("boolean", new BoolTypeTree());
        treeMappings.put("<boolean>", new IntTree(Helpers.tt("<int>")));
        treeMappings.put("string", new StringTypeTree());
        treeMappings.put("<string>", new StringTree(Helpers.tt("<string>")));
        treeMappings.put("scientific", new ScientificTypeTree());
        treeMappings.put("<scientific>", new ScientificTree(Helpers.tt("<scientific>")));

    }

    public static AST at(String token) {
        return treeMappings.get(token);
    }

    public static ILexer lexerFromPseudoProgram(String program) throws Exception {
        StringTokenizer tokenizer = new StringTokenizer(program);

        TestLexer lexer = new TestLexer();
        
        while(tokenizer.hasMoreTokens()) {
            String tokenString = tokenizer.nextToken();
            Token token = Helpers.tt(tokenString);
            
            if(token == null) {
                // This should only happen if I have created an invalid test case
                throw new Exception(String.format("Unrecognized token: %s", tokenString));
            }

            lexer.addToken(token);
        }

        return lexer;
    }
}