package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ast.*;
import parser.Parser;
import tests.helpers.Helpers;
import tests.helpers.TestVisitor;
import visitor.ASTVisitor;

public class IfStatementTests {
    
    @ParameterizedTest
    @MethodSource("provideIfStatements")
    void testIfStatement(ILexer lexer, List<AST> expectedAst) throws Exception {
        final Parser parser = new Parser(lexer);
        AST ast = parser.execute();

        // Helpful for debugging (please remember to comment before submission!):
        // PrintVisitor printer = new PrintVisitor();
        // ast.accept(printer);

        ASTVisitor visitor = new TestVisitor(expectedAst);
        Object result = ast.accept(visitor);

        assertEquals(null, result);     
    }

    private static Stream<Arguments> provideIfStatements() throws Exception {
        return Stream.of(
            Arguments.of(Helpers.lexerFromPseudoProgram(IF_ELSE_TEST_PROGRAM), IF_ELSE_TEST_AST)  ,
            Arguments.of(Helpers.lexerFromPseudoProgram(IF_TEST_PROGRAM), IF_TEST_AST)
        );
    }

    private static final String IF_ELSE_TEST_PROGRAM = """
        program { int <id>
            if ( <int> == <int> ) then {
                <id> = <int>
            } else {
                <id> = <int>
            }
        }
        """;
    
    private static final List<AST> IF_ELSE_TEST_AST = Arrays.asList(
        new ProgramTree(),
        new BlockTree(),
        new DeclTree(),
        new IntTypeTree(),
        new IdTree(Helpers.tt("<id>")),
        new IfTree(),
        new RelOpTree(Helpers.tt("==")),
        new IntTree(Helpers.tt("<int>")),
        new IntTree(Helpers.tt("<int>")),
        new BlockTree(),
        new AssignTree(),
        new IdTree(Helpers.tt("<id>")),
        new IntTree(Helpers.tt("<int>")),
        new BlockTree(),
        new AssignTree(),
        new IdTree(Helpers.tt("<id>")),
        new IntTree(Helpers.tt("<int>"))
    );

    private static final String IF_TEST_PROGRAM = """
            program { int <id>
                if ( <int> == <int> ) then {
                    <id> = <int>
                }
            }
            """;

    private static final List<AST> IF_TEST_AST = Arrays.asList(
        new ProgramTree(),
        new BlockTree(),
        new DeclTree(),
        new IntTypeTree(),
        new IdTree(Helpers.tt("<id>")),
        new IfTree(),
        new RelOpTree(Helpers.tt("==")),
        new IntTree(Helpers.tt("<int>")),
        new IntTree(Helpers.tt("<int>")),
        new BlockTree(),
        new AssignTree(),
        new IdTree(Helpers.tt("<id>")),
        new IntTree(Helpers.tt("<int>"))
    );
}