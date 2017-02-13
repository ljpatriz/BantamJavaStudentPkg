package bantam.lexer;

import java_cup.runtime.Symbol;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;

/*
 * File: LexerTest.java
 * Author: djskrien
 * Date: 1/8/17
 */
public class LexerTest
{
    @BeforeClass
    public static void begin() {
        System.out.println("begin");
    }
    @Test
    public void classToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("class"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("CLASS",s);
    }

    @Test
    public void lbraceToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("{"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("LBRACE",s);
    }

    @Test
    public void rbraceToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("}"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("RBRACE",s);
    }

    @Test
    public void lparenToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("("));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("LPAREN",s);
    }

    @Test
    public void rparenToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader(")"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("RPAREN",s);
    }

    @Test
    public void minusToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("-"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("MINUS",s);
    }

    @Test
    public void plusToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("+"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("PLUS",s);
    }

    @Test
    public void timesToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("*"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("TIMES",s);
    }

    @Test
    public void lsqbraceToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("["));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("LSQBRACE",s);
    }

    @Test
    public void rsqbraceToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("]"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("RSQBRACE",s);
    }

    @Test
    public void divideToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("/"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("DIVIDE",s);
    }

    @Test
    public void assignToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("="));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("ASSIGN",s);
    }

    @Test
    public void incrToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("++"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("INCR",s);
    }

    @Test
    public void decrToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("--"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("DECR",s);
    }

    @Test
    public void ltToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("<"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("LT",s);
    }

    @Test
    public void gtToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader(">"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("GT",s);
    }

    @Test
    public void eqToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("=="));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("EQ",s);
    }

    @Test
    public void neToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("!="));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("NE",s);
    }

    @Test
    public void leqToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("<="));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("LEQ",s);
    }

    @Test
    public void geqToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader(">="));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("GEQ",s);
    }

    @Test
    public void modulusToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("%"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("MODULUS",s);
    }


    @Test
    public void unsupportedCharacterToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("#"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("LEX_ERROR",s);
//        token = lexer.next_token();
//        s = ((Token)token.value).getName();
//        assertEquals("LEX_ERROR",s);
    }

    @Test
    public void trueToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("true"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("TRUE",s);
    }

    @Test
    public void falseToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("false"));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("FALSE",s);
    }

    @Test
    public void stringToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("\"this is a string\""));
        Symbol token = lexer.next_token();
        String name = ((Token)token.value).getName();
        String attribute = ((Token)token.value).getAttribute();
        assertEquals("this is a string",attribute);
        assertEquals("STRING_CONST",name);
    }

    @Test
    public void unterminatedMultilineComment() throws Exception {
        Lexer lexer = new Lexer(new StringReader("/*unterminated multiline comment"));
        Symbol token = lexer.next_token();
        String name = ((Token)token.value).getName();
        String attribute = ((Token)token.value).getAttribute();
        assertEquals("unterminated multiline comment",attribute);
        assertEquals("LEX_ERROR",name);
    }

    @Test
    public void multilineStringToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("\"this is a multiline \n string\""));
        Symbol token = lexer.next_token();
        String name = ((Token)token.value).getName();
        String attribute = ((Token)token.value).getAttribute();
        assertEquals("LEX_ERROR",name);
        assertEquals("String Constant spans multiple lines",attribute);

    }

    @Test
    public void EOFToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader(""));
        Symbol token = lexer.next_token();
        String s = ((Token)token.value).getName();
        assertEquals("EOF",s);
    }
}