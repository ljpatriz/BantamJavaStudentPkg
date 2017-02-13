/* Bantam Java Compiler and Language Toolset.
   Copyright (C) 2009 by Marc Corliss (corliss@hws.edu) and 
                         David Furcy (furcyd@uwosh.edu) and
                         E Christopher Lewis (lewis@vmware.com).
   ALL RIGHTS RESERVED.
   The Bantam Java toolset is distributed under the following 
   conditions:
     You may make copies of the toolset for your own use and 
     modify those copies.
     All copies of the toolset must retain the author names and 
     copyright notice.
     You may not sell the toolset or distribute it in 
     conjunction with a commerical product or service without 
     the expressed written consent of the authors.
   THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS 
   OR IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE 
   IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
   PARTICULAR PURPOSE. 
*/
/* code below is copied to the file containing the bantam.lexer */
package bantam.lexer;
import bantam.parser.TokenIds;
/* import Symbol class, which represents the symbols that are passed
   from the bantam.lexer to the bantam.parser.  Each symbol consists of an ID
   and a token value, which is defined in Token.java */
import java_cup.runtime.Symbol;


public class Lexer implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

    /* code below is copied to the class containing the bantam.lexer */
    /** maximum string size allowed */
    private final int MAX_STRING_SIZE = 5000;
    /** boolean indicating whether debugging is enabled */
    private boolean debug = false;
    /** boolean indicating whether we're lexing multiple files or a single file */
    private boolean multipleFiles = false;
    /** array that holds the names of each file we're lexing 
      * (used only when multipleFiles is true)
      * */
    private String[] filenames;
    /** array that holds the reader for each file we're lexing 
      * (used only when multipleFiles is true)
      * */
    private java.io.BufferedReader[] fileReaders;
    /** current file number used to index filenames and fileReaders
      * (used only when multipleFiles is true)
      * */
    private int fileCnt = 0;
    /** Lexer constructor - defined in JLex specification file
      * Needed to handle lexing multiple files
      * @param filenames list of filename strings
      * @param debug boolean indicating whether debugging is enabled
      * */
    public Lexer(String[] filenames, boolean debug) {
	// call private constructor, which does some initialization
	this();
	this.debug = debug;
	// set the multipleFiles flag to true (provides compatibility
	// with the single file constructors)
	multipleFiles = true;
	// initialize filenames field to parameter filenames
	// used later for finding the name of the current file
	this.filenames = filenames;
	// check that there is at least one specified filename
	if (filenames.length == 0)
	    throw new RuntimeException("Must specify at least one filename to scan");
	// must initialize readers for each file (BufferedReader)
	fileReaders = new java.io.BufferedReader[filenames.length];
	for (int i = 0; i < filenames.length; i++) {
	    // try...catch checks if file is found
	    try {
		// create the ith file reader
		fileReaders[i] = new java.io.BufferedReader(new java.io.FileReader(filenames[i]));
	    }
	    catch(java.io.FileNotFoundException e) {
		// if file not found then report an error and exit
		System.err.println("Error: file '" + filenames[i] + "' not found");
		System.exit(1);
	    }
	}
	// set yy_reader (a JLex variable) to the first file reader
	yy_reader = fileReaders[0];
	// set yyline to 1 (as opposed to 0)
	yyline = 1;
    }
    /** holds the current string constant
      * note: we use StringBuffer so that appending does not require constructing a new object 
      * */
    private StringBuffer currStringConst;
    /** getter method for accessing the current line number
      * @return current line number
      * */
    public int getCurrLineNum() {
	return yyline;
    }
    /** getter method for accessing the current file name
      * @return current filename string
      * */
    public String getCurrFilename() {
	return filenames[fileCnt];
    }
    /** print tokens - used primarily for debugging the bantam.lexer
      * */
    public void printTokens() throws java.io.IOException {
	// prevFileCnt is used to determine when the filename has changed
	// every time an EOF is encountered fileCnt is incremented
	// by testing fileCnt with prevFileCnt, we can determine when the
	// filename has changed and print the filename along with the tokens
	int prevFileCnt = -1;
	// try...catch needed since next_token() can throw an IOException
	try {
	    // iterate through all tokens
	    while (true) {
		// get the next token
		Symbol symbol = next_token();
		// check if file has changed
		if (prevFileCnt != fileCnt) {
		    // if it has then print out the new filename
		    System.out.println("# " + filenames[fileCnt]);
		    // update prevFileCnt
		    prevFileCnt = fileCnt;
		}
		// print out the token
		System.out.println((Token)symbol.value);
		// if we've reached the EOF (EOF only returned for the last
		// file) then we break out of loop
		if (symbol.sym == TokenIds.EOF)
		    break;
	    }
	}
	catch (java.io.IOException e) {
	    // if an IOException occurs then print error and exit
	    System.err.println("Unexpected IO exception while scanning.");
	    throw e;
	}
    }
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yychar;
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	public Lexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	public Lexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Lexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yychar = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;

    // set yyline to 1 (as opposed to 0)
    yyline = 1;
	}

	private boolean yy_eof_done = false;
	private final int YYINITIAL = 0;
	private final int yy_state_dtrans[] = {
		0
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yychar = yychar
			+ yy_buffer_index - yy_buffer_start;
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NO_ANCHOR,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NOT_ACCEPT,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NOT_ACCEPT,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NOT_ACCEPT,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NOT_ACCEPT,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NOT_ACCEPT,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR,
		/* 139 */ YY_NO_ANCHOR,
		/* 140 */ YY_NO_ANCHOR,
		/* 141 */ YY_NO_ANCHOR,
		/* 142 */ YY_NO_ANCHOR,
		/* 143 */ YY_NO_ANCHOR,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NO_ANCHOR,
		/* 147 */ YY_NO_ANCHOR,
		/* 148 */ YY_NO_ANCHOR,
		/* 149 */ YY_NO_ANCHOR,
		/* 150 */ YY_NO_ANCHOR,
		/* 151 */ YY_NO_ANCHOR,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NO_ANCHOR,
		/* 154 */ YY_NO_ANCHOR,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR,
		/* 159 */ YY_NO_ANCHOR,
		/* 160 */ YY_NO_ANCHOR,
		/* 161 */ YY_NO_ANCHOR,
		/* 162 */ YY_NO_ANCHOR,
		/* 163 */ YY_NO_ANCHOR,
		/* 164 */ YY_NO_ANCHOR,
		/* 165 */ YY_NO_ANCHOR,
		/* 166 */ YY_NO_ANCHOR,
		/* 167 */ YY_NO_ANCHOR,
		/* 168 */ YY_NO_ANCHOR,
		/* 169 */ YY_NO_ANCHOR,
		/* 170 */ YY_NO_ANCHOR,
		/* 171 */ YY_NO_ANCHOR,
		/* 172 */ YY_NO_ANCHOR,
		/* 173 */ YY_NO_ANCHOR,
		/* 174 */ YY_NO_ANCHOR,
		/* 175 */ YY_NO_ANCHOR,
		/* 176 */ YY_NO_ANCHOR,
		/* 177 */ YY_NO_ANCHOR,
		/* 178 */ YY_NO_ANCHOR,
		/* 179 */ YY_NO_ANCHOR,
		/* 180 */ YY_NO_ANCHOR,
		/* 181 */ YY_NO_ANCHOR,
		/* 182 */ YY_NO_ANCHOR,
		/* 183 */ YY_NO_ANCHOR,
		/* 184 */ YY_NO_ANCHOR,
		/* 185 */ YY_NO_ANCHOR,
		/* 186 */ YY_NO_ANCHOR,
		/* 187 */ YY_NO_ANCHOR,
		/* 188 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"29:10,30,29:2,27,29:18,1,19,28,21,29,20,29:2,8,9,12,11,21,10,29,13,41,43,42" +
",47,44,50,48,45,46,49,29:2,17,16,18,21:2,51:26,14,31,15,21,52,21,4,36,2,40," +
"25,26,51,38,33,51,37,3,51,34,32,51:2,23,5,22,24,51,35,39,51:2,6,29,7,29:2,0" +
":2")[0];

	private int yy_rmap[] = unpackFromString(1,189,
"0,1:2,2,1:4,3,4,1,5,1:2,6,7,8,9,1:4,10,1:4,11,12,1,13:12,1:10,14,15,16,11,1" +
"2,1,17,18,1,19,11,1,20,21,19,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,3" +
"7,38,39,40,14,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,6" +
"1,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,8" +
"6,87,88,13,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107" +
",108,109,110,111,109,112,113,114,115,116,117,118,119,117,120,121,122,123,12" +
"4,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139")[0];

	private int yy_nxt[][] = unpackFromString(140,53,
"1,2,3,135:3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,109,138,135,111,87,-1" +
",55,60,-1,60,135,54,89,140,142,135:4,53,186,187,188:7,135,60,-1:55,135,113," +
"135:2,-1:16,135:5,-1:5,135:9,-1,135:11,-1:10,20,-1:53,21,-1:53,22,-1:56,23," +
"-1:52,24,-1:52,25,-1:52,26,-1:37,22:7,-1:2,22:2,-1:2,22:39,-1,27:7,56:2,27:" +
"17,61,28,27,29,65,27:21,-1,56:26,-1,56:2,29,56:22,-1:2,135:4,-1:16,135:5,-1" +
":5,135:9,-1,135:11,-1:41,52:10,-1:43,53,186,187,188:7,-1:4,135:4,-1:16,135:" +
"4,30,-1:5,135:2,119,135:6,-1,135:11,-1:41,42:5,-1,42:2,-1,42,-1:4,135:4,-1:" +
"16,135,31,135:3,-1:5,135:9,-1,135:11,-1,61:7,-1:2,61:18,57,61,-1,69,61:21,-" +
"1:2,135:4,-1:16,135:5,-1:5,135:3,32,135:5,-1,135:11,-1,27:26,61,62,27,29,65" +
",27:21,-1:41,43:10,-1:4,135:4,-1:16,135:3,33,135,-1:5,135:9,-1,135:11,-1,61" +
":27,66,61,-1,69,61:21,-1:41,44:10,-1:4,135:4,-1:16,135:3,34,135,-1:5,135:9," +
"-1,135:11,-1:41,45:10,-1:4,135:3,35,-1:16,135:5,-1:5,135:9,-1,135:11,-1:41," +
"42:5,46,42:2,46,42,-1:4,135:4,-1:16,135:3,36,135,-1:5,135:9,-1,135:11,-1:41" +
",47:10,-1:4,135:4,-1:16,135:3,37,135,-1:5,135:9,-1,135:11,-1:41,48:10,-1:4," +
"135:4,-1:16,135:5,-1:5,135:5,38,135:3,-1,135:11,-1:41,49:10,-1:4,135:4,-1:1" +
"6,135:5,-1:5,135:2,39,135:6,-1,135:11,-1:41,50:10,-1:4,135:3,40,-1:16,135:5" +
",-1:5,135:9,-1,135:11,-1:41,51:10,-1:4,135:4,-1:16,135:4,41,-1:5,135:9,-1,1" +
"35:11,-1:2,135:2,117,135,-1:16,135:5,-1:5,59,135:8,-1,135:11,-1:41,58:4,63:" +
"2,58,63:3,-1:4,135:4,-1:16,135:3,64,135,-1:5,135:9,-1,135:11,-1:41,63:10,-1" +
":4,135:4,-1:16,135:2,68,135:2,-1:5,135:9,-1,135:11,-1:41,67:10,-1:4,135:3,7" +
"1,-1:16,135:5,-1:5,135:9,-1,135:11,-1:41,70:10,-1:4,135:3,73,-1:16,135:5,-1" +
":5,135:9,-1,135:11,-1:41,72:10,-1:4,135:3,75,-1:16,135:5,-1:5,135:9,-1,135:" +
"11,-1:41,58:3,74,76:2,58,76:3,-1:4,135,77,135:2,-1:16,135:5,-1:5,135:9,-1,1" +
"35:11,-1:41,78:10,-1:4,135:2,79,135,-1:16,135:5,-1:5,135:9,-1,135:11,-1:41," +
"80:10,-1:4,135:4,-1:16,135,81,135:3,-1:5,135:9,-1,135:11,-1:41,82:10,-1:4,1" +
"35:4,-1:16,135:5,-1:5,135:8,83,-1,135:11,-1:41,84:10,-1:4,135:4,-1:16,135:5" +
",-1:5,85,135:8,-1,135:11,-1:41,86:10,-1:4,135:4,-1:16,135,91,135:3,-1:5,135" +
":9,-1,135:11,-1:41,88:4,90:2,88:2,90,88,-1:4,135,93,135:2,-1:16,135:5,-1:5," +
"135:7,137,135,-1,135:11,-1:41,90:10,-1:4,135:2,95,135,-1:16,135:5,-1:5,135:" +
"9,-1,135:11,-1:41,92:10,-1:4,135:4,-1:16,125,135:4,-1:5,135:9,-1,135:11,-1:" +
"41,94:10,-1:4,135,97,135:2,-1:16,135:5,-1:5,135:9,-1,135:11,-1:41,88:4,96:2" +
",88,98,96,88,-1:4,135:3,129,-1:16,135:5,-1:5,135:9,-1,135:11,-1:41,100:10,-" +
"1:4,135:4,-1:16,135:5,-1:5,135,99,135:7,-1,135:11,-1:41,102:10,-1:4,135:4,-" +
"1:16,135:3,101,135,-1:5,135:9,-1,135:11,-1:41,104:10,-1:4,135:4,-1:16,135:2" +
",103,135:2,-1:5,135:9,-1,135:11,-1:41,106:10,-1:4,135:4,-1:16,135:3,130,135" +
",-1:5,135:9,-1,135:11,-1:41,108:10,-1:4,135:4,-1:16,131,135:4,-1:5,135:9,-1" +
",135:11,-1:2,135:4,-1:16,135:5,-1:5,135:2,105,135:6,-1,135:11,-1:2,135:2,13" +
"2,135,-1:16,135:5,-1:5,135:9,-1,135:11,-1:2,135:4,-1:16,135:5,-1:5,135:2,13" +
"3,135:6,-1,135:11,-1:2,134,135:3,-1:16,135:5,-1:5,135:9,-1,135:11,-1:2,135:" +
"4,-1:16,135:3,107,135,-1:5,135:9,-1,135:11,-1:41,110:3,112:3,110,112:3,-1:4" +
",135:4,-1:16,127,135:4,-1:5,135:9,-1,135:11,-1:2,135:4,-1:16,135:3,115,135," +
"-1:5,135:9,-1,135:11,-1:41,112:10,-1:4,135:4,-1:16,135:5,-1:5,135:6,121,135" +
":2,-1,135:11,-1:41,114:10,-1:4,135:4,-1:16,135,123,135:3,-1:5,135:9,-1,135:" +
"11,-1:41,110:3,116:3,118,116:3,-1:43,120:10,-1:43,122:10,-1:43,124:10,-1:43" +
",126:10,-1:43,128:10,-1:43,136:8,139,136,-1:43,139:10,-1:43,141:10,-1:43,13" +
"6:5,143,136:2,144,136,-1:43,145:10,-1:43,146:10,-1:43,147:10,-1:43,150:10,-" +
"1:43,148:10,-1:43,149:4,150:2,149,150:3,-1:43,151:10,-1:43,149:3,152,153:2," +
"149,153:3,-1:43,154:10,-1:43,155:10,-1:43,156:4,159:2,156,159:3,-1:43,159:1" +
"0,-1:43,157:10,-1:43,158:5,159,158:2,159,158,-1:43,160:10,-1:43,158:4,161,1" +
"62,158:2,162,158,-1:43,163:10,-1:43,164:4,165,168,164,165,168,165,-1:43,165" +
":3,164,165,168,165,164,168,164,-1:43,168:10,-1:43,165:5,168,165:2,168,165,-" +
"1:43,164:4,165,168,164:2,168,164,-1:43,166:10,-1:43,167:4,168:2,167,168:3,-" +
"1:43,169:10,-1:43,167:3,170,171:2,167,171:3,-1:43,172:5,173,172:2,174,172,-" +
"1:43,175:4,173:2,175,173,174,173,-1:43,176:3,175,174:2,176,175,174,175,-1:4" +
"3,174:10,-1:43,177:10,-1:43,178,179,180,179:7,-1:43,181:4,182,183,181,182,1" +
"84,182,-1:43,185:10,-1:2");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

    /* code below is executed when the end-of-file is reached */
    switch(yy_lexical_state) {
    case YYINITIAL:
	// if in YYINITIAL when EOF occurs then no error
	break;
    // if defining other states then might want to add other cases here...
    }
    // if we reach here then we should either start lexing the next
    // file (if there are more files to lex) or return EOF (if we're
    // at the file)
    if (multipleFiles && fileCnt < fileReaders.length - 1) {
	// more files to lex so update yy_reader and yyline and then continue
	yy_reader = fileReaders[++fileCnt];
	yyline = 1;
	continue;
    }
    // if we reach here, then we're at the last file so we return EOF
    // to bantam.parser
    return new Symbol(TokenIds.EOF, new Token("EOF", yyline));
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 0:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -2:
						break;
					case 1:
						
					case -3:
						break;
					case 2:
						{}
					case -4:
						break;
					case 3:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -5:
						break;
					case 4:
						{ return new Symbol(TokenIds.LBRACE,
                            new Token("LBRACE", yyline)); }
					case -6:
						break;
					case 5:
						{ return new Symbol(TokenIds.RBRACE,
                            new Token("RBRACE", yyline)); }
					case -7:
						break;
					case 6:
						{ return new Symbol(TokenIds.LPAREN,
                            new Token("LPAREN", yyline)); }
					case -8:
						break;
					case 7:
						{ return new Symbol(TokenIds.RPAREN,
                            new Token("RPAREN", yyline)); }
					case -9:
						break;
					case 8:
						{ return new Symbol(TokenIds.MINUS,
                            new Token("MINUS", yyline)); }
					case -10:
						break;
					case 9:
						{ return new Symbol(TokenIds.PLUS,
                            new Token("PLUS", yyline)); }
					case -11:
						break;
					case 10:
						{ return new Symbol(TokenIds.TIMES,
                            new Token("TIMES", yyline)); }
					case -12:
						break;
					case 11:
						{ return new Symbol(TokenIds.DIVIDE,
                            new Token("DIVIDE", yyline)); }
					case -13:
						break;
					case 12:
						{ return new Symbol(TokenIds.LSQBRACE,
                            new Token("LSQBRACE", yyline)); }
					case -14:
						break;
					case 13:
						{ return new Symbol(TokenIds.RSQBRACE,
                            new Token("RSQBRACE", yyline)); }
					case -15:
						break;
					case 14:
						{ return new Symbol(TokenIds.ASSIGN,
                            new Token("ASSIGN", yyline)); }
					case -16:
						break;
					case 15:
						{ return new Symbol(TokenIds.LT,
                            new Token("LT", yyline)); }
					case -17:
						break;
					case 16:
						{ return new Symbol(TokenIds.GT,
                            new Token("GT", yyline)); }
					case -18:
						break;
					case 17:
						{ throw new RuntimeException("Unmatched lexeme " +
                            yytext() + " at line " + yyline); }
					case -19:
						break;
					case 18:
						{ return new Symbol(TokenIds.MODULUS,
                            new Token("MODULUS", yyline)); }
					case -20:
						break;
					case 19:
						{ return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Unsupported Character", yyline)); }
					case -21:
						break;
					case 20:
						{ return new Symbol(TokenIds.DECR,
                            new Token("DECR", yyline)); }
					case -22:
						break;
					case 21:
						{ return new Symbol(TokenIds.INCR,
                            new Token("INCR", yyline)); }
					case -23:
						break;
					case 22:
						{ return new Symbol(TokenIds.error, new Token("LEX_ERROR","unterminated multiline comment", yyline));}
					case -24:
						break;
					case 23:
						{ return new Symbol(TokenIds.EQ,
                            new Token("EQ", yyline)); }
					case -25:
						break;
					case 24:
						{ return new Symbol(TokenIds.LEQ,
                            new Token("LEQ", yyline)); }
					case -26:
						break;
					case 25:
						{ return new Symbol(TokenIds.GEQ,
                            new Token("GEQ", yyline)); }
					case -27:
						break;
					case 26:
						{ return new Symbol(TokenIds.NE,
                            new Token("NE", yyline)); }
					case -28:
						break;
					case 28:
						{ return new Symbol(TokenIds.STRING_CONST,
                                                               new Token("STRING_CONST",
                                                               yytext().substring(1,yytext().length()-1)
                                                               , yyline));}
					case -29:
						break;
					case 29:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token("LEX_ERROR","String Constant spans multiple lines"
                                                                                              , yyline));}
					case -30:
						break;
					case 30:
						{ return new Symbol(TokenIds.IF,
                            new Token("IF", yyline)); }
					case -31:
						break;
					case 31:
						{ return new Symbol(TokenIds.FOR,
                            new Token("FOR", yyline)); }
					case -32:
						break;
					case 32:
						{ return new Symbol(TokenIds.NEW,
                            new Token("NEW", yyline)); }
					case -33:
						break;
					case 33:
						{ return new Symbol(TokenIds.BOOLEAN_CONST,
                                               new Token("TRUE",yyline));}
					case -34:
						break;
					case 34:
						{ return new Symbol(TokenIds.ELSE,
                            new Token("ELSE", yyline)); }
					case -35:
						break;
					case 35:
						{ return new Symbol(TokenIds.CLASS,
						    new Token("CLASS", yyline)); }
					case -36:
						break;
					case 36:
						{ return new Symbol(TokenIds.BOOLEAN_CONST,
                             new Token("FALSE",yyline));}
					case -37:
						break;
					case 37:
						{ return new Symbol(TokenIds.WHILE,
                            new Token("WHILE", yyline)); }
					case -38:
						break;
					case 38:
						{ return new Symbol(TokenIds.BREAK,
                            new Token("BREAK", yyline)); }
					case -39:
						break;
					case 39:
						{ return new Symbol(TokenIds.RETURN,
                            new Token("RETURN", yyline)); }
					case -40:
						break;
					case 40:
						{ return new Symbol(TokenIds.EXTENDS,
                            new Token("EXTENDS", yyline)); }
					case -41:
						break;
					case 41:
						{ return new Symbol(TokenIds.INSTANCEOF,
                            new Token("INSTANCEOF", yyline)); }
					case -42:
						break;
					case 42:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -43:
						break;
					case 43:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token
                                                            ("LEX_ERROR", yytext() + " is too large to be an integer at line " + yyline, yyline)); }
					case -44:
						break;
					case 44:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token
                                        ("LEX_ERROR", yytext() + " is too large to be an integer at line " + yyline, yyline)); }
					case -45:
						break;
					case 45:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token
                                    ("LEX_ERROR", yytext() + " is too large to be an integer at line " + yyline, yyline)); }
					case -46:
						break;
					case 46:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token
("LEX_ERROR", yytext() + " is too large to be an integer at line " + yyline, yyline)); }
					case -47:
						break;
					case 47:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token
                                ("LEX_ERROR", yytext() + " is too large to be an integer at line " + yyline, yyline)); }
					case -48:
						break;
					case 48:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token
                                        ("LEX_ERROR", yytext() + " is too large to be an integer at line " + yyline, yyline)); }
					case -49:
						break;
					case 49:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token
                                                ("LEX_ERROR", yytext() + " is too large to be an integer at line " + yyline, yyline)); }
					case -50:
						break;
					case 50:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token
                                                   ("LEX_ERROR", yytext() + " is too large to be an integer at line " + yyline, yyline)); }
					case -51:
						break;
					case 51:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token
                                                        ("LEX_ERROR", yytext() + " is too large to be an integer at line " + yyline, yyline)); }
					case -52:
						break;
					case 52:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token
                                                                 ("LEX_ERROR", yytext() + " is too large to be an integer at line " + yyline, yyline)); }
					case -53:
						break;
					case 53:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -54:
						break;
					case 54:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -55:
						break;
					case 55:
						{ throw new RuntimeException("Unmatched lexeme " +
                            yytext() + " at line " + yyline); }
					case -56:
						break;
					case 57:
						{ return new Symbol(TokenIds.STRING_CONST,
                                                               new Token("STRING_CONST",
                                                               yytext().substring(1,yytext().length()-1)
                                                               , yyline));}
					case -57:
						break;
					case 58:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -58:
						break;
					case 59:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -59:
						break;
					case 60:
						{ throw new RuntimeException("Unmatched lexeme " +
                            yytext() + " at line " + yyline); }
					case -60:
						break;
					case 62:
						{ return new Symbol(TokenIds.STRING_CONST,
                                                               new Token("STRING_CONST",
                                                               yytext().substring(1,yytext().length()-1)
                                                               , yyline));}
					case -61:
						break;
					case 63:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -62:
						break;
					case 64:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -63:
						break;
					case 66:
						{ return new Symbol(TokenIds.STRING_CONST,
                                                               new Token("STRING_CONST",
                                                               yytext().substring(1,yytext().length()-1)
                                                               , yyline));}
					case -64:
						break;
					case 67:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -65:
						break;
					case 68:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -66:
						break;
					case 70:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -67:
						break;
					case 71:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -68:
						break;
					case 72:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -69:
						break;
					case 73:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -70:
						break;
					case 74:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -71:
						break;
					case 75:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -72:
						break;
					case 76:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -73:
						break;
					case 77:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -74:
						break;
					case 78:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -75:
						break;
					case 79:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -76:
						break;
					case 80:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -77:
						break;
					case 81:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -78:
						break;
					case 82:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -79:
						break;
					case 83:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -80:
						break;
					case 84:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -81:
						break;
					case 85:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -82:
						break;
					case 86:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -83:
						break;
					case 87:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -84:
						break;
					case 88:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -85:
						break;
					case 89:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -86:
						break;
					case 90:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -87:
						break;
					case 91:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -88:
						break;
					case 92:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -89:
						break;
					case 93:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -90:
						break;
					case 94:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -91:
						break;
					case 95:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -92:
						break;
					case 96:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -93:
						break;
					case 97:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -94:
						break;
					case 98:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -95:
						break;
					case 99:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -96:
						break;
					case 100:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -97:
						break;
					case 101:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -98:
						break;
					case 102:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -99:
						break;
					case 103:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -100:
						break;
					case 104:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -101:
						break;
					case 105:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -102:
						break;
					case 106:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -103:
						break;
					case 107:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -104:
						break;
					case 108:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -105:
						break;
					case 109:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -106:
						break;
					case 110:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -107:
						break;
					case 111:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -108:
						break;
					case 112:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -109:
						break;
					case 113:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -110:
						break;
					case 114:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -111:
						break;
					case 115:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -112:
						break;
					case 116:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -113:
						break;
					case 117:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -114:
						break;
					case 118:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -115:
						break;
					case 119:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -116:
						break;
					case 120:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -117:
						break;
					case 121:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -118:
						break;
					case 122:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -119:
						break;
					case 123:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -120:
						break;
					case 124:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -121:
						break;
					case 125:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -122:
						break;
					case 126:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -123:
						break;
					case 127:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -124:
						break;
					case 128:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -125:
						break;
					case 129:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -126:
						break;
					case 130:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -127:
						break;
					case 131:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -128:
						break;
					case 132:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -129:
						break;
					case 133:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -130:
						break;
					case 134:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -131:
						break;
					case 135:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -132:
						break;
					case 136:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -133:
						break;
					case 137:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -134:
						break;
					case 138:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -135:
						break;
					case 139:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -136:
						break;
					case 140:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -137:
						break;
					case 141:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -138:
						break;
					case 142:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -139:
						break;
					case 143:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -140:
						break;
					case 144:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -141:
						break;
					case 145:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -142:
						break;
					case 146:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -143:
						break;
					case 147:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -144:
						break;
					case 148:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -145:
						break;
					case 149:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -146:
						break;
					case 150:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -147:
						break;
					case 151:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -148:
						break;
					case 152:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -149:
						break;
					case 153:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -150:
						break;
					case 154:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -151:
						break;
					case 155:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -152:
						break;
					case 156:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -153:
						break;
					case 157:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -154:
						break;
					case 158:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -155:
						break;
					case 159:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -156:
						break;
					case 160:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -157:
						break;
					case 161:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -158:
						break;
					case 162:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -159:
						break;
					case 163:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -160:
						break;
					case 164:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -161:
						break;
					case 165:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -162:
						break;
					case 166:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -163:
						break;
					case 167:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -164:
						break;
					case 168:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -165:
						break;
					case 169:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -166:
						break;
					case 170:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -167:
						break;
					case 171:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -168:
						break;
					case 172:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -169:
						break;
					case 173:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -170:
						break;
					case 174:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -171:
						break;
					case 175:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -172:
						break;
					case 176:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -173:
						break;
					case 177:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -174:
						break;
					case 178:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -175:
						break;
					case 179:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -176:
						break;
					case 180:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -177:
						break;
					case 181:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -178:
						break;
					case 182:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -179:
						break;
					case 183:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -180:
						break;
					case 184:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -181:
						break;
					case 185:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -182:
						break;
					case 186:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -183:
						break;
					case 187:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -184:
						break;
					case 188:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -185:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
