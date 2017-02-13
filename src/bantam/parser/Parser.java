//----------------------------------------------------
// The following code was generated by CUP v0.10k
// Sun Jan 08 12:59:43 EST 2017
//----------------------------------------------------

package bantam.parser;

import bantam.ast.ClassList;
import bantam.ast.Class_;
import bantam.ast.MemberList;
import bantam.ast.Program;
import bantam.lexer.Lexer;
import bantam.lexer.Token;
import bantam.util.ErrorHandler;
import java_cup.runtime.Symbol;

/**
 * CUP v0.10k generated parser.
 *
 * @version Sun Jan 08 12:59:43 EST 2017
 */
public class Parser extends java_cup.runtime.lr_parser
{

    /**
     * Default constructor.
     */
    public Parser() {super();}

    /**
     * Constructor which sets the default scanner.
     */
    public Parser(java_cup.runtime.Scanner s) {super(s);}

    /**
     * Production table.
     */
    protected static final short _production_table[][] = unpackFromStrings(new
            String[]{"\000\007\000\002\003\003\000\002\002\004\000\002\004" +
            "\003\000\002\004\004\000\002\005\007\000\002\005\011" + "\000\002\006\002"});

    /**
     * Access to production table.
     */
    public short[][] production_table() {return _production_table;}

    /**
     * Parse-action table.
     */
    protected static final short[][] _action_table = unpackFromStrings(new
            String[]{"\000\020\000\004\005\007\001\002\000\006\002\001\005" +
            "\007\001\002\000\004\002\021\001\002\000\006\002\uffff" +
            "\005\uffff\001\002\000\004\051\010\001\002\000\006\006" +
            "\012\042\011\001\002\000\004\043\ufffb\001\002\000\004" +
            "\051\013\001\002\000\004\042\014\001\002\000\004\043" +
            "\ufffb\001\002\000\004\043\016\001\002\000\006\002\ufffc" +
            "\005\ufffc\001\002\000\004\043\020\001\002\000\006\002" +
            "\ufffd\005\ufffd\001\002\000\004\002\000\001\002\000\006" +
            "\002\ufffe\005\ufffe\001\002"});

    /**
     * Access to parse-action table.
     */
    public short[][] action_table() {return _action_table;}

    /**
     * <code>reduce_goto</code> table.
     */
    protected static final short[][] _reduce_table = unpackFromStrings(new
            String[]{"\000\020\000\010\003\004\004\003\005\005\001\001\000" +
            "\004\005\021\001\001\000\002\001\001\000\002\001\001" +
            "\000\002\001\001\000\002\001\001\000\004\006\016\001" +
            "\001\000\002\001\001\000\002\001\001\000\004\006\014" +
            "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
            "\001\000\002\001\001\000\002\001\001\000\002\001\001" + ""});

    /**
     * Access to <code>reduce_goto</code> table.
     */
    public short[][] reduce_table() {return _reduce_table;}

    /**
     * Instance of action encapsulation class.
     */
    protected CUP$Parser$actions action_obj;

    /**
     * Action encapsulation object initializer.
     */
    protected void init_actions() {
        action_obj = new CUP$Parser$actions(this);
    }

    /**
     * Invoke a user supplied parse action.
     */
    public java_cup.runtime.Symbol do_action(int act_num, java_cup.runtime.lr_parser
            parser, java.util.Stack stack, int top) throws java.lang.Exception {
    /* call code in generated class */
        return action_obj.CUP$Parser$do_action(act_num, parser, stack, top);
    }

    /**
     * Indicates start state.
     */
    public int start_state() {return 0;}

    /**
     * Indicates start production.
     */
    public int start_production() {return 1;}

    /**
     * <code>EOF</code> Symbol index.
     */
    public int EOF_sym() {return 0;}

    /**
     * <code>error</code> Symbol index.
     */
    public int error_sym() {return 1;}


    /**
     * overrides done_parsing in inherited class, called when parsing finishes
     * either after accepting or because of errors
     */
    public void done_parsing() {
        super.done_parsing();
        // if lexing and/or parsing errors then exit
        action_obj.getErrorHandler().checkErrors();
    }

    /**
     * overrides syntax_error in inherited class and handles syntax errors
     *
     * @param curr current symbol from the bantam.lexer
     */
    public void syntax_error(Symbol curr) {
        // get token value from the symbol
        Token t = (Token) curr.value;

        // get the error handler object
        ErrorHandler errorHandler = action_obj.getErrorHandler();

        // may want to modify and augment the code below for more
        // accurate error reporting
        errorHandler.register(errorHandler.PARSE_ERROR, action_obj.getCurrFilename(),
                action_obj.getCurrLineNum(), "unexpected input near '" + t.getLexeme()
                        + "'");
    }

    /**
     * override error_sync_size in inherited class and
     * change the minimum number of tokens to skip on an error from 3 to 2
     *
     * @return minimum number of tokens to skip on an error
     */
    protected int error_sync_size() {
        return 2;
    }

}

/**
 * Cup generated class to encapsulate user supplied action code.
 */
class CUP$Parser$actions
{


    /**
     * object for error handling
     */
    private ErrorHandler errorHandler = new ErrorHandler();

    /* sets the error handler */
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /* returns the current line number from the scanner */
    public int getCurrLineNum() {
        return ((Lexer) parser.getScanner()).getCurrLineNum();
    }

    /* returns the current file name from the scanner */
    public String getCurrFilename() {
        return ((Lexer) parser.getScanner()).getCurrFilename();
    }

    private final Parser parser;

    /**
     * Constructor
     */
    CUP$Parser$actions(Parser parser) {
        this.parser = parser;
    }

    /**
     * Method with the actual generated action code.
     */
    public final java_cup.runtime.Symbol CUP$Parser$do_action(int CUP$Parser$act_num,
                       java_cup.runtime.lr_parser CUP$Parser$parser,
                       java.util.Stack CUP$Parser$stack, int CUP$Parser$top) throws java.lang.Exception {
      /* Symbol object for return from actions */
        java_cup.runtime.Symbol CUP$Parser$result;

      /* select the action based on the action number */
        switch (CUP$Parser$act_num) {
          /*. . . . . . . . . . . . . . . . . . . .*/
            case 6: // member_list ::=
            {
                MemberList RESULT = null;
                RESULT = new MemberList(getCurrLineNum());
                CUP$Parser$result = new java_cup.runtime.Symbol(4/*member_list*/, RESULT);
            }
            return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
            case 5: // class ::= CLASS ID EXTENDS ID LBRACE member_list RBRACE
            {
                Class_ RESULT = null;
                Token t = (Token) ((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt
                        (CUP$Parser$top - 6)).value;
                Token name = (Token) ((java_cup.runtime.Symbol) CUP$Parser$stack
                        .elementAt(CUP$Parser$top - 5)).value;
                Token parent = (Token) ((java_cup.runtime.Symbol) CUP$Parser$stack
                        .elementAt(CUP$Parser$top - 3)).value;
                MemberList ml = (MemberList) ((java_cup.runtime.Symbol)
                        CUP$Parser$stack.elementAt(CUP$Parser$top - 1)).value;
                RESULT = new Class_(t.getLineNum(), getCurrFilename(), name
                        .getAttribute(), parent.getAttribute(), ml);
                CUP$Parser$result = new java_cup.runtime.Symbol(3/*class*/, RESULT);
            }
            return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
            case 4: // class ::= CLASS ID LBRACE member_list RBRACE
            {
                Class_ RESULT = null;
                Token t = (Token) ((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt
                        (CUP$Parser$top - 4)).value;
                Token name = (Token) ((java_cup.runtime.Symbol) CUP$Parser$stack
                        .elementAt(CUP$Parser$top - 3)).value;
                MemberList ml = (MemberList) ((java_cup.runtime.Symbol)
                        CUP$Parser$stack.elementAt(CUP$Parser$top - 1)).value;
                RESULT = new Class_(t.getLineNum(), getCurrFilename(), name
                        .getAttribute(), "Object", ml);
                CUP$Parser$result = new java_cup.runtime.Symbol(3/*class*/, RESULT);
            }
            return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
            case 3: // class_list ::= class_list class
            {
                ClassList RESULT = null;
                ClassList cl = (ClassList) ((java_cup.runtime.Symbol) CUP$Parser$stack
                        .elementAt(CUP$Parser$top - 1)).value;
                Class_ c = (Class_) ((java_cup.runtime.Symbol) CUP$Parser$stack
                        .elementAt(CUP$Parser$top - 0)).value;
                RESULT = (ClassList) cl.addElement(c);
                CUP$Parser$result = new java_cup.runtime.Symbol(2/*class_list*/, RESULT);
            }
            return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
            case 2: // class_list ::= class
            {
                ClassList RESULT = null;
                Class_ c = (Class_) ((java_cup.runtime.Symbol) CUP$Parser$stack
                        .elementAt(CUP$Parser$top - 0)).value;
                RESULT = (ClassList) (new ClassList(0)).addElement(c);
                CUP$Parser$result = new java_cup.runtime.Symbol(2/*class_list*/, RESULT);
            }
            return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
            case 1: // $START ::= program EOF
            {
                Object RESULT = null;
                Program start_val = (Program) ((java_cup.runtime.Symbol)
                        CUP$Parser$stack.elementAt(CUP$Parser$top - 1)).value;
                RESULT = start_val;
                CUP$Parser$result = new java_cup.runtime.Symbol(0/*$START*/, RESULT);
            }
          /* ACCEPT */
            CUP$Parser$parser.done_parsing();
            return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
            case 0: // program ::= class_list
            {
                Program RESULT = null;
                ClassList cl = (ClassList) ((java_cup.runtime.Symbol) CUP$Parser$stack
                        .elementAt(CUP$Parser$top - 0)).value;
                RESULT = new Program(0, cl);
                CUP$Parser$result = new java_cup.runtime.Symbol(1/*program*/, RESULT);
            }
            return CUP$Parser$result;

          /* . . . . . .*/
            default:
                throw new Exception("Invalid action number found in internal parse table");

        }
    }
}
