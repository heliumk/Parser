package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import compiler.Compiler;

public class OutputTest {
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @Before
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @After
    public void tearDown() {
        System.setOut(standardOut);
    }
    
    @Test
    public void testOutput() throws Exception {
        Path temp = Files.createTempFile("fall-2022", ".x");
        String absolutePath = temp.toString();

        BufferedWriter writer = new BufferedWriter(new FileWriter(absolutePath));
        writer.write(TEST_FILE);
        writer.close();

        Compiler compiler = new Compiler(absolutePath);
        compiler.compileProgram();

        outputStreamCaptor.flush();
        String result = outputStreamCaptor.toString();

        String[] outputLines = result.split("\n");
        String[] expectLines = EXPECTED_OUTPUT.split("\n");

        for(int i = 0; i < outputLines.length && i < expectLines.length; i++) {
            // This is hacky af but it seems to get rid of whitespace mismatches, and I'm tired of looking at it
            for(int j = 0; j < expectLines[i].length(); j++) {
              assertEquals(expectLines[i].charAt(j), outputLines[i].charAt(j));
            }

            assertEquals(expectLines[i], outputLines[i]);
        }
    }

    private static final String TEST_FILE = 
"""
program { int i boolean b string s scientific sc
  // Check function declaration works,
  //   if without else
  //   relops
  //   ops
  string checkOldRelops(int x, boolean bb) {
    if(1 < 2) then {
      if(1 <=2) then {
        if(1== 2) then {
          if(1 != 2) then {
            return 42
          }
        }
      } else {
        // Ops
        return i + i - 1 / i * i
      }
    }
  }

  // Check assignment with new types
  s = "hi"
  sc = 1.23e+45

  // Check forall and function invocation
  forall int j in [ 2 .. 42 ] {
    i = write(j)
  }
}
""";

    private static final String EXPECTED_OUTPUT = 
"""
  1: program { int i boolean b string s scientific sc
  2:   // Check function declaration works,
  3:   //   if without else
  4:   //   relops
  5:   //   ops
  6:   string checkOldRelops(int x, boolean bb) {
  7:     if(1 < 2) then {
  8:       if(1 <=2) then {
  9:         if(1== 2) then {
 10:           if(1 != 2) then {
 11:             return 42
 12:           }
 13:         }
 14:       } else {
 15:         // Ops
 16:         return i + i - 1 / i * i
 17:       }
 18:     }
 19:   }
 20:
 21:   // Check assignment with new types
 22:   s = "hi"
 23:   sc = 1.23e+45
 24:
 25:   // Check forall and function invocation
 26:   forall int j in [ 2 .. 42 ] {
 27:     i = write(j)
 28:   }
 29: }

---------------AST-------------
1:  Program
2:    Block
5:      Decl
3:        IntType
4:        Id: i
8:      Decl
6:        BoolType
7:        Id: b
11:     Decl
9:        StringType
10:       Id: s
14:     Decl
12:       ScientificType
13:       Id: sc
17:     FunctionDecl
15:       StringType
16:       Id: checkOldRelops
18:       Formals
21:         Decl
19:           IntType
20:           Id: x
24:         Decl
22:           BoolType
23:           Id: bb
25:       Block
26:         If
28:           RelOp: <
27:             Int: 1
29:             Int: 2
30:           Block
31:             If
33:               RelOp: <=
32:                 Int: 1
34:                 Int: 2
35:               Block
36:                 If
38:                   RelOp: ==
37:                     Int: 1
39:                     Int: 2
40:                   Block
41:                     If
43:                       RelOp: !=
42:                         Int: 1
44:                         Int: 2
45:                       Block
46:                         Return
47:                           Int: 42
48:               Block
49:                 Return
53:                   AddOp: -
51:                     AddOp: +
50:                       Id: i
52:                       Id: i
57:                     MultOp: *
55:                       MultOp: /
54:                         Int: 1
56:                         Id: i
58:                       Id: i
60:     Assign
59:       Id: s
61:       String: hi
63:     Assign
62:       Id: sc
64:       Scientific: 1.23e+45
65:     Forall
68:       Decl
66:         IntType
67:         Id: j
69:       RangeExp
70:         Int: 2
71:         Int: 42
72:       Block
74:         Assign
73:           Id: i
76:           Call
75:             Id: write
77:             Id: j
""";
}