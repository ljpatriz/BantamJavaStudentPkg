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
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NOT_ACCEPT,
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
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NOT_ACCEPT,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NOT_ACCEPT,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NOT_ACCEPT,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NOT_ACCEPT,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NOT_ACCEPT,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NOT_ACCEPT,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NOT_ACCEPT,
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
		/* 188 */ YY_NO_ANCHOR,
		/* 189 */ YY_NO_ANCHOR,
		/* 190 */ YY_NO_ANCHOR,
		/* 191 */ YY_NO_ANCHOR,
		/* 192 */ YY_NO_ANCHOR,
		/* 193 */ YY_NO_ANCHOR,
		/* 194 */ YY_NO_ANCHOR,
		/* 195 */ YY_NO_ANCHOR,
		/* 196 */ YY_NO_ANCHOR,
		/* 197 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"6:9,2,3,6:2,30,6:18,1,22,31,24,6,23,6:2,13,14,5,16,24,15,6,4,42,44,43,48,45" +
",51,49,46,47,50,6:2,20,19,21,24:2,52:26,17,32,18,24,53,24,9,37,7,41,28,29,5" +
"2,39,34,52,38,8,52,35,33,52:2,26,10,25,27,52,36,40,52:2,11,6,12,6:2,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,198,
"0,1:4,2,1:2,3,1:4,4,5,1:2,6,7,8,1:2,9,10,1:6,11,1,12,13:3,14,13:9,1:10,15,1" +
"6,17,18,19,12,1,20,11,21,22,11,1,23,24,22,25,26,14,27,28,29,30,31,32,33,34," +
"35,36,37,38,39,40,41,42,43,44,45,15,46,47,48,49,50,51,52,53,54,55,56,57,58," +
"59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83," +
"84,85,86,87,88,89,90,91,92,93,94,13,95,96,97,98,99,100,101,102,103,104,105," +
"106,107,108,109,110,111,112,113,114,115,116,117,115,118,119,120,121,122,123" +
",124,125,123,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,14" +
"1,142,143,144,145")[0];

	private int yy_nxt[][] = unpackFromString(146,54,
"1,2,3,4,5,6,7,8,144:3,9,10,11,12,13,14,15,16,17,18,19,58,20,21,118,147,144," +
"120,95,-1,64,7,144,59,98,149,151,144:4,57,195,196,197:7,144,7,-1:58,22,23,-" +
"1:55,144,122,144:2,-1:14,144:5,-1:3,144:9,-1,144:11,-1:15,24,-1:54,25,-1:56" +
",26,-1:53,27,-1:53,28,-1:35,22:2,-1,22:26,-1,22:23,-1,96:2,23,74,77,96:7,74" +
":2,96:15,60,96:23,-1,30:2,31,30:9,61:2,30:15,66,32,70,30:21,-1,61:2,31,61:2" +
"6,-1,61:23,-1:7,144:4,-1:14,144:5,-1:3,144:9,-1,144:11,-1,74:2,83,74:26,-1," +
"74:23,-1:42,56:10,-1:44,57,195,196,197:7,-1:21,29,-1:41,144:4,-1:14,144:4,3" +
"3,-1:3,144:2,128,144:6,-1,144:11,-1,60:3,-1:2,60:7,-1:2,60:39,-1:42,46:5,-1" +
",46:2,-1,46,-1:9,144:4,-1:14,144,34,144:3,-1:3,144:9,-1,144:11,-1,66:2,-1,6" +
"6:9,-1:2,66:16,62,80,66:21,-1:7,144:4,-1:14,144:5,-1:3,144:3,35,144:5,-1,14" +
"4:11,-1,30:2,31,30:26,66,67,70,30:21,-1:42,47:10,-1:9,144:4,-1:14,144:3,37," +
"144,-1:3,144:9,-1,144:11,-1:42,48:10,-1:9,144:4,-1:14,144:3,38,144,-1:3,144" +
":9,-1,144:11,-1,74:2,83,36,74:25,-1,74:23,-1:42,49:10,-1:9,144:3,39,-1:14,1" +
"44:5,-1:3,144:9,-1,144:11,-1,66:2,-1,66:27,71,80,66:21,-1:42,46:5,50,46:2,5" +
"0,46,-1:9,144:4,-1:14,144:3,40,144,-1:3,144:9,-1,144:11,-1,74:2,83,74,77,74" +
":24,-1,74:23,-1:42,51:10,-1:9,144:4,-1:14,144:3,41,144,-1:3,144:9,-1,144:11" +
",-1:42,52:10,-1:9,144:4,-1:14,144:5,-1:3,144:5,42,144:3,-1,144:11,-1:42,53:" +
"10,-1:9,144:4,-1:14,144:5,-1:3,144:2,43,144:6,-1,144:11,-1:42,54:10,-1:9,14" +
"4:3,44,-1:14,144:5,-1:3,144:9,-1,144:11,-1:42,55:10,-1:9,144:4,-1:14,144:4," +
"45,-1:3,144:9,-1,144:11,-1:7,144:2,126,144,-1:14,144:5,-1:3,65,144:8,-1,144" +
":11,-1,96:2,23,74:2,96:7,74:2,96:15,60,96:23,-1:42,63:4,68:2,63,68:3,-1:9,1" +
"44:4,-1:14,144:3,69,144,-1:3,144:9,-1,144:11,-1:42,68:10,-1:9,144:4,-1:14,1" +
"44:2,73,144:2,-1:3,144:9,-1,144:11,-1:42,72:10,-1:9,144:3,76,-1:14,144:5,-1" +
":3,144:9,-1,144:11,-1:42,75:10,-1:9,144:3,79,-1:14,144:5,-1:3,144:9,-1,144:" +
"11,-1:42,78:10,-1:9,144:3,82,-1:14,144:5,-1:3,144:9,-1,144:11,-1:42,63:3,81" +
",84:2,63,84:3,-1:9,144,85,144:2,-1:14,144:5,-1:3,144:9,-1,144:11,-1:42,86:1" +
"0,-1:9,144:2,87,144,-1:14,144:5,-1:3,144:9,-1,144:11,-1:42,88:10,-1:9,144:4" +
",-1:14,144,89,144:3,-1:3,144:9,-1,144:11,-1:42,90:10,-1:9,144:4,-1:14,144:5" +
",-1:3,144:8,91,-1,144:11,-1:42,92:10,-1:9,144:4,-1:14,144:5,-1:3,93,144:8,-" +
"1,144:11,-1:42,94:10,-1:9,144:4,-1:14,144,100,144:3,-1:3,144:9,-1,144:11,-1" +
":42,97:4,99:2,97:2,99,97,-1:9,144,102,144:2,-1:14,144:5,-1:3,144:7,146,144," +
"-1,144:11,-1:42,99:10,-1:9,144:2,104,144,-1:14,144:5,-1:3,144:9,-1,144:11,-" +
"1:42,101:10,-1:9,144:4,-1:14,134,144:4,-1:3,144:9,-1,144:11,-1:42,103:10,-1" +
":9,144,106,144:2,-1:14,144:5,-1:3,144:9,-1,144:11,-1:42,97:4,105:2,97,107,1" +
"05,97,-1:9,144:3,138,-1:14,144:5,-1:3,144:9,-1,144:11,-1:42,109:10,-1:9,144" +
":4,-1:14,144:5,-1:3,144,108,144:7,-1,144:11,-1:42,111:10,-1:9,144:4,-1:14,1" +
"44:3,110,144,-1:3,144:9,-1,144:11,-1:42,113:10,-1:9,144:4,-1:14,144:2,112,1" +
"44:2,-1:3,144:9,-1,144:11,-1:42,115:10,-1:9,144:4,-1:14,144:3,139,144,-1:3," +
"144:9,-1,144:11,-1:42,117:10,-1:9,144:4,-1:14,140,144:4,-1:3,144:9,-1,144:1" +
"1,-1:7,144:4,-1:14,144:5,-1:3,144:2,114,144:6,-1,144:11,-1:7,144:2,141,144," +
"-1:14,144:5,-1:3,144:9,-1,144:11,-1:7,144:4,-1:14,144:5,-1:3,144:2,142,144:" +
"6,-1,144:11,-1:7,143,144:3,-1:14,144:5,-1:3,144:9,-1,144:11,-1:7,144:4,-1:1" +
"4,144:3,116,144,-1:3,144:9,-1,144:11,-1:42,119:3,121:3,119,121:3,-1:9,144:4" +
",-1:14,136,144:4,-1:3,144:9,-1,144:11,-1:7,144:4,-1:14,144:3,124,144,-1:3,1" +
"44:9,-1,144:11,-1:42,121:10,-1:9,144:4,-1:14,144:5,-1:3,144:6,130,144:2,-1," +
"144:11,-1:42,123:10,-1:9,144:4,-1:14,144,132,144:3,-1:3,144:9,-1,144:11,-1:" +
"42,119:3,125:3,127,125:3,-1:44,129:10,-1:44,131:10,-1:44,133:10,-1:44,135:1" +
"0,-1:44,137:10,-1:44,145:8,148,145,-1:44,148:10,-1:44,150:10,-1:44,145:5,15" +
"2,145:2,153,145,-1:44,154:10,-1:44,155:10,-1:44,156:10,-1:44,159:10,-1:44,1" +
"57:10,-1:44,158:4,159:2,158,159:3,-1:44,160:10,-1:44,158:3,161,162:2,158,16" +
"2:3,-1:44,163:10,-1:44,164:10,-1:44,165:4,168:2,165,168:3,-1:44,168:10,-1:4" +
"4,166:10,-1:44,167:5,168,167:2,168,167,-1:44,169:10,-1:44,167:4,170,171,167" +
":2,171,167,-1:44,172:10,-1:44,173:4,174,177,173,174,177,174,-1:44,174:3,173" +
",174,177,174,173,177,173,-1:44,177:10,-1:44,174:5,177,174:2,177,174,-1:44,1" +
"73:4,174,177,173:2,177,173,-1:44,175:10,-1:44,176:4,177:2,176,177:3,-1:44,1" +
"78:10,-1:44,176:3,179,180:2,176,180:3,-1:44,181:5,182,181:2,183,181,-1:44,1" +
"84:4,182:2,184,182,183,182,-1:44,185:3,184,183:2,185,184,183,184,-1:44,183:" +
"10,-1:44,186:10,-1:44,187,188,189,188:7,-1:44,190:4,191,192,190,191,193,191" +
",-1:44,194:10,-1:2");

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
						{}
					case -5:
						break;
					case 4:
						{}
					case -6:
						break;
					case 5:
						{ return new Symbol(TokenIds.DIVIDE,
                            new Token("DIVIDE", yyline)); }
					case -7:
						break;
					case 6:
						{ return new Symbol(TokenIds.TIMES,
                            new Token("TIMES", yyline)); }
					case -8:
						break;
					case 7:
						{ throw new RuntimeException("Unmatched lexeme " +
                            yytext() + " at line " + yyline); }
					case -9:
						break;
					case 8:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -10:
						break;
					case 9:
						{ return new Symbol(TokenIds.LBRACE,
                            new Token("LBRACE", yyline)); }
					case -11:
						break;
					case 10:
						{ return new Symbol(TokenIds.RBRACE,
                            new Token("RBRACE", yyline)); }
					case -12:
						break;
					case 11:
						{ return new Symbol(TokenIds.LPAREN,
                            new Token("LPAREN", yyline)); }
					case -13:
						break;
					case 12:
						{ return new Symbol(TokenIds.RPAREN,
                            new Token("RPAREN", yyline)); }
					case -14:
						break;
					case 13:
						{ return new Symbol(TokenIds.MINUS,
                            new Token("MINUS", yyline)); }
					case -15:
						break;
					case 14:
						{ return new Symbol(TokenIds.PLUS,
                            new Token("PLUS", yyline)); }
					case -16:
						break;
					case 15:
						{ return new Symbol(TokenIds.LSQBRACE,
                            new Token("LSQBRACE", yyline)); }
					case -17:
						break;
					case 16:
						{ return new Symbol(TokenIds.RSQBRACE,
                            new Token("RSQBRACE", yyline)); }
					case -18:
						break;
					case 17:
						{ return new Symbol(TokenIds.ASSIGN,
                            new Token("ASSIGN", yyline)); }
					case -19:
						break;
					case 18:
						{ return new Symbol(TokenIds.LT,
                            new Token("LT", yyline)); }
					case -20:
						break;
					case 19:
						{ return new Symbol(TokenIds.GT,
                            new Token("GT", yyline)); }
					case -21:
						break;
					case 20:
						{ return new Symbol(TokenIds.MODULUS,
                            new Token("MODULUS", yyline)); }
					case -22:
						break;
					case 21:
						{ return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Unsupported Character", yyline)); }
					case -23:
						break;
					case 22:
						{}
					case -24:
						break;
					case 23:
						{ return new Symbol(TokenIds.error, new Token("LEX_ERROR","unterminated multiline comment", yyline));}
					case -25:
						break;
					case 24:
						{ return new Symbol(TokenIds.DECR,
                            new Token("DECR", yyline)); }
					case -26:
						break;
					case 25:
						{ return new Symbol(TokenIds.INCR,
                            new Token("INCR", yyline)); }
					case -27:
						break;
					case 26:
						{ return new Symbol(TokenIds.EQ,
                            new Token("EQ", yyline)); }
					case -28:
						break;
					case 27:
						{ return new Symbol(TokenIds.LEQ,
                            new Token("LEQ", yyline)); }
					case -29:
						break;
					case 28:
						{ return new Symbol(TokenIds.GEQ,
                            new Token("GEQ", yyline)); }
					case -30:
						break;
					case 29:
						{ return new Symbol(TokenIds.NE,
                            new Token("NE", yyline)); }
					case -31:
						break;
					case 31:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token("LEX_ERROR","String Constant spans multiple lines"
                                                                                              , yyline));}
					case -32:
						break;
					case 32:
						{ return new Symbol(TokenIds.STRING_CONST,
                                                               new Token("STRING_CONST",
                                                               yytext().substring(1,yytext().length()-1)
                                                               , yyline));}
					case -33:
						break;
					case 33:
						{ return new Symbol(TokenIds.IF,
                            new Token("IF", yyline)); }
					case -34:
						break;
					case 34:
						{ return new Symbol(TokenIds.FOR,
                            new Token("FOR", yyline)); }
					case -35:
						break;
					case 35:
						{ return new Symbol(TokenIds.NEW,
                            new Token("NEW", yyline)); }
					case -36:
						break;
					case 36:
						{}
					case -37:
						break;
					case 37:
						{ return new Symbol(TokenIds.BOOLEAN_CONST,
                                               new Token("TRUE",yyline));}
					case -38:
						break;
					case 38:
						{ return new Symbol(TokenIds.ELSE,
                            new Token("ELSE", yyline)); }
					case -39:
						break;
					case 39:
						{ return new Symbol(TokenIds.CLASS,
						    new Token("CLASS", yyline)); }
					case -40:
						break;
					case 40:
						{ return new Symbol(TokenIds.BOOLEAN_CONST,
                             new Token("FALSE",yyline));}
					case -41:
						break;
					case 41:
						{ return new Symbol(TokenIds.WHILE,
                            new Token("WHILE", yyline)); }
					case -42:
						break;
					case 42:
						{ return new Symbol(TokenIds.BREAK,
                            new Token("BREAK", yyline)); }
					case -43:
						break;
					case 43:
						{ return new Symbol(TokenIds.RETURN,
                            new Token("RETURN", yyline)); }
					case -44:
						break;
					case 44:
						{ return new Symbol(TokenIds.EXTENDS,
                            new Token("EXTENDS", yyline)); }
					case -45:
						break;
					case 45:
						{ return new Symbol(TokenIds.INSTANCEOF,
                            new Token("INSTANCEOF", yyline)); }
					case -46:
						break;
					case 46:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
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
						{ return new Symbol(TokenIds.LEX_ERROR, new Token
                                                ("LEX_ERROR", yytext() + " is too large to be an integer at line " + yyline, yyline)); }
					case -54:
						break;
					case 54:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token
                                                   ("LEX_ERROR", yytext() + " is too large to be an integer at line " + yyline, yyline)); }
					case -55:
						break;
					case 55:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token
                                                        ("LEX_ERROR", yytext() + " is too large to be an integer at line " + yyline, yyline)); }
					case -56:
						break;
					case 56:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token
                                                                 ("LEX_ERROR", yytext() + " is too large to be an integer at line " + yyline, yyline)); }
					case -57:
						break;
					case 57:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -58:
						break;
					case 58:
						{ throw new RuntimeException("Unmatched lexeme " +
                            yytext() + " at line " + yyline); }
					case -59:
						break;
					case 59:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -60:
						break;
					case 60:
						{ return new Symbol(TokenIds.error, new Token("LEX_ERROR","unterminated multiline comment", yyline));}
					case -61:
						break;
					case 62:
						{ return new Symbol(TokenIds.STRING_CONST,
                                                               new Token("STRING_CONST",
                                                               yytext().substring(1,yytext().length()-1)
                                                               , yyline));}
					case -62:
						break;
					case 63:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -63:
						break;
					case 64:
						{ throw new RuntimeException("Unmatched lexeme " +
                            yytext() + " at line " + yyline); }
					case -64:
						break;
					case 65:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -65:
						break;
					case 67:
						{ return new Symbol(TokenIds.STRING_CONST,
                                                               new Token("STRING_CONST",
                                                               yytext().substring(1,yytext().length()-1)
                                                               , yyline));}
					case -66:
						break;
					case 68:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -67:
						break;
					case 69:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -68:
						break;
					case 71:
						{ return new Symbol(TokenIds.STRING_CONST,
                                                               new Token("STRING_CONST",
                                                               yytext().substring(1,yytext().length()-1)
                                                               , yyline));}
					case -69:
						break;
					case 72:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -70:
						break;
					case 73:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -71:
						break;
					case 75:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -72:
						break;
					case 76:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -73:
						break;
					case 78:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -74:
						break;
					case 79:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -75:
						break;
					case 81:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -76:
						break;
					case 82:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -77:
						break;
					case 84:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -78:
						break;
					case 85:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -79:
						break;
					case 86:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -80:
						break;
					case 87:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -81:
						break;
					case 88:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -82:
						break;
					case 89:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -83:
						break;
					case 90:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -84:
						break;
					case 91:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -85:
						break;
					case 92:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -86:
						break;
					case 93:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -87:
						break;
					case 94:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -88:
						break;
					case 95:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -89:
						break;
					case 96:
						{ return new Symbol(TokenIds.error, new Token("LEX_ERROR","unterminated multiline comment", yyline));}
					case -90:
						break;
					case 97:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -91:
						break;
					case 98:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -92:
						break;
					case 99:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -93:
						break;
					case 100:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -94:
						break;
					case 101:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -95:
						break;
					case 102:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -96:
						break;
					case 103:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -97:
						break;
					case 104:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -98:
						break;
					case 105:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -99:
						break;
					case 106:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -100:
						break;
					case 107:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -101:
						break;
					case 108:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -102:
						break;
					case 109:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -103:
						break;
					case 110:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -104:
						break;
					case 111:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -105:
						break;
					case 112:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -106:
						break;
					case 113:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -107:
						break;
					case 114:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -108:
						break;
					case 115:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -109:
						break;
					case 116:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -110:
						break;
					case 117:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -111:
						break;
					case 118:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -112:
						break;
					case 119:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -113:
						break;
					case 120:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -114:
						break;
					case 121:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -115:
						break;
					case 122:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -116:
						break;
					case 123:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -117:
						break;
					case 124:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -118:
						break;
					case 125:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -119:
						break;
					case 126:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -120:
						break;
					case 127:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -121:
						break;
					case 128:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -122:
						break;
					case 129:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -123:
						break;
					case 130:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -124:
						break;
					case 131:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -125:
						break;
					case 132:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -126:
						break;
					case 133:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -127:
						break;
					case 134:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -128:
						break;
					case 135:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -129:
						break;
					case 136:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -130:
						break;
					case 137:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -131:
						break;
					case 138:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -132:
						break;
					case 139:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -133:
						break;
					case 140:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -134:
						break;
					case 141:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -135:
						break;
					case 142:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -136:
						break;
					case 143:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -137:
						break;
					case 144:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -138:
						break;
					case 145:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -139:
						break;
					case 146:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -140:
						break;
					case 147:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -141:
						break;
					case 148:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -142:
						break;
					case 149:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -143:
						break;
					case 150:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -144:
						break;
					case 151:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -145:
						break;
					case 152:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -146:
						break;
					case 153:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -147:
						break;
					case 154:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -148:
						break;
					case 155:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -149:
						break;
					case 156:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -150:
						break;
					case 157:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -151:
						break;
					case 158:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -152:
						break;
					case 159:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -153:
						break;
					case 160:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -154:
						break;
					case 161:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -155:
						break;
					case 162:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -156:
						break;
					case 163:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -157:
						break;
					case 164:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -158:
						break;
					case 165:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -159:
						break;
					case 166:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -160:
						break;
					case 167:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -161:
						break;
					case 168:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -162:
						break;
					case 169:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -163:
						break;
					case 170:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -164:
						break;
					case 171:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -165:
						break;
					case 172:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -166:
						break;
					case 173:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -167:
						break;
					case 174:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -168:
						break;
					case 175:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -169:
						break;
					case 176:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -170:
						break;
					case 177:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -171:
						break;
					case 178:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -172:
						break;
					case 179:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -173:
						break;
					case 180:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -174:
						break;
					case 181:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -175:
						break;
					case 182:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -176:
						break;
					case 183:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -177:
						break;
					case 184:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -178:
						break;
					case 185:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -179:
						break;
					case 186:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -180:
						break;
					case 187:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -181:
						break;
					case 188:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -182:
						break;
					case 189:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -183:
						break;
					case 190:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -184:
						break;
					case 191:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -185:
						break;
					case 192:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -186:
						break;
					case 193:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -187:
						break;
					case 194:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -188:
						break;
					case 195:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -189:
						break;
					case 196:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -190:
						break;
					case 197:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -191:
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
