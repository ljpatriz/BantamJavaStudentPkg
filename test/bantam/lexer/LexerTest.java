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


    @Test
    public void forToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("for"));
        Symbol token = lexer.next_token();
        String name = ((Token)token.value).getName();
        assertEquals("FOR",name);
    }

    @Test
    public void instanceofToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("instanceof"));
        Symbol token = lexer.next_token();
        String name = ((Token)token.value).getName();
        assertEquals("INSTANCEOF",name);
    }

    @Test
    public void ifToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("if"));
        Symbol token = lexer.next_token();
        String name = ((Token)token.value).getName();
        assertEquals("IF",name);
    }

    @Test
    public void returnToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("return"));
        Symbol token = lexer.next_token();
        String name = ((Token)token.value).getName();
        assertEquals("RETURN",name);
    }

    @Test
    public void newToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("new"));
        Symbol token = lexer.next_token();
        String name = ((Token)token.value).getName();
        assertEquals("NEW",name);
    }

    @Test
    public void breakToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("break"));
        Symbol token = lexer.next_token();
        String name = ((Token)token.value).getName();
        assertEquals("BREAK",name);
    }

    @Test
    public void elseToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("else"));
        Symbol token = lexer.next_token();
        String name = ((Token)token.value).getName();
        assertEquals("ELSE",name);
    }

    @Test
    public void whileToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("while"));
        Symbol token = lexer.next_token();
        String name = ((Token)token.value).getName();
        assertEquals("WHILE",name);
    }

    @Test
    public void extendsToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("extends"));
        Symbol token = lexer.next_token();
        String name = ((Token)token.value).getName();
        assertEquals("EXTENDS",name);
    }

    @Test
    public void intToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("5236 001 003294"));
        Symbol token = lexer.next_token();
        String name = ((Token)token.value).getName();
        String attribute = ((Token)token.value).getAttribute();
        assertEquals("5236",attribute);
        assertEquals("INT_CONST",name);

        token = lexer.next_token();
        name = ((Token)token.value).getName();
        attribute = ((Token)token.value).getAttribute();
        assertEquals("001", attribute);
        assertEquals("INT_CONST",name);
    }

    @Test
    public void invalidIntToken() throws Exception {
        Lexer lexer = new Lexer(new StringReader("5236001003294 2147484000"));
        Symbol token = lexer.next_token();
        String name = ((Token)token.value).getName();
        assertEquals("LEX_ERROR",name);

        token = lexer.next_token();
        name = ((Token)token.value).getName();
        assertEquals("LEX_ERROR",name);
    }

}