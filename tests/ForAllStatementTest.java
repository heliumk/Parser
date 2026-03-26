package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ast.*;
import parser.Parser;
import tests.helpers.Helpers;
import tests.helpers.TestVisitor;
import visitor.ASTVisitor;

public class ForAllStatementTest {
    
    @Test
    public void testForAllStatement() throws Exception {
        final Parser parser = new Parser(Helpers.lexerFromPseudoProgram(FOR_ALL_PROGRAM));
        AST ast = parser.execute();

        // Helpful for debugging (please remember to comment before submission!):
        // PrintVisitor printer = new PrintVisitor();
        // ast.accept(printer);

        ASTVisitor visitor = new TestVisitor(FOR_ALL_AST);
        Object result = ast.accept(visitor);

        assertEquals(null, result);     

    }

    private static final String FOR_ALL_PROGRAM = """
            program { int <id>
                forall int <id> in [ <int> .. <int> ] {
                    <id> = <int>
                }
            }
            """;

    private static final List<AST> FOR_ALL_AST = Arrays.asList(
        new ProgramTree(),
        new BlockTree(),
        new DeclTree(),
        new IntTypeTree(),
        new IdTree(Helpers.tt("<id>")),
        new ForAllTree(),
        new DeclTree(),
        new IntTypeTree(),
        new IdTree(Helpers.tt("<id>")),
        new RangeExpTree(),
        new IntTree(Helpers.tt("<int>")),
        new IntTree(Helpers.tt("<int>")),
        new BlockTree(),
        new AssignTree(),
        new IdTree(Helpers.tt("<id>")),
        new IntTree(Helpers.tt("<int>"))
    );
}