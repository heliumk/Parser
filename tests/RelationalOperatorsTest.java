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
import visitor.*;

public class RelationalOperatorsTest {

    @ParameterizedTest
    @MethodSource("provideRelationalTokenPrograms")
    void testRelationalOperators(ILexer lexer, List<AST> expectedAst) throws Exception {
        Parser parser = new Parser(lexer);
        AST ast = parser.execute();

        // Helpful for debugging (please remember to comment before submission!):
        // PrintVisitor printer = new PrintVisitor();
        // ast.accept(printer);

        ASTVisitor visitor = new TestVisitor(expectedAst);
        Object result = ast.accept(visitor);

        assertEquals(null, result);
    }

    private static Stream<Arguments> provideRelationalTokenPrograms() throws Exception {
        return Stream.of(
            Arguments.of(lexerForRelop("=="), expectedAstForRelop("==")),
            Arguments.of(lexerForRelop("!="), expectedAstForRelop("!=")),
            Arguments.of(lexerForRelop("<"), expectedAstForRelop("<")),
            Arguments.of(lexerForRelop("<="), expectedAstForRelop("<=")),
            Arguments.of(lexerForRelop(">"), expectedAstForRelop(">")),
            Arguments.of(lexerForRelop(">="), expectedAstForRelop(">="))
        );
    }

    private static ILexer lexerForRelop(String relop) throws Exception {
        return Helpers.lexerFromPseudoProgram(
            String.format("""
                    program { 
                        return <int> %s <int>
                    }
                    """, relop)
        );

    }

    private static List<AST> expectedAstForRelop(String relop) {
        return Arrays.asList(
            new ProgramTree(),
            new BlockTree(),
            new ReturnTree(),
            new RelOpTree(Helpers.tt(relop)),
            new IntTree(Helpers.tt("<int>")),
            new IntTree(Helpers.tt("<int>"))
        );
    }
}