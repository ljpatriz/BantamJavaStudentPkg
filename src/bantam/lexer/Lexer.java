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
		/* 23 */ YY_NOT_ACCEPT,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
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
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NOT_ACCEPT,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NOT_ACCEPT,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NOT_ACCEPT,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NOT_ACCEPT,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NOT_ACCEPT,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NOT_ACCEPT,
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
		/* 197 */ YY_NO_ANCHOR,
		/* 198 */ YY_NO_ANCHOR,
		/* 199 */ YY_NO_ANCHOR,
		/* 200 */ YY_NO_ANCHOR,
		/* 201 */ YY_NO_ANCHOR,
		/* 202 */ YY_NO_ANCHOR,
		/* 203 */ YY_NO_ANCHOR,
		/* 204 */ YY_NO_ANCHOR,
		/* 205 */ YY_NO_ANCHOR,
		/* 206 */ YY_NO_ANCHOR,
		/* 207 */ YY_NO_ANCHOR,
		/* 208 */ YY_NO_ANCHOR,
		/* 209 */ YY_NO_ANCHOR,
		/* 210 */ YY_NO_ANCHOR,
		/* 211 */ YY_NO_ANCHOR,
		/* 212 */ YY_NO_ANCHOR,
		/* 213 */ YY_NO_ANCHOR,
		/* 214 */ YY_NO_ANCHOR,
		/* 215 */ YY_NO_ANCHOR,
		/* 216 */ YY_NO_ANCHOR,
		/* 217 */ YY_NO_ANCHOR,
		/* 218 */ YY_NO_ANCHOR,
		/* 219 */ YY_NO_ANCHOR,
		/* 220 */ YY_NO_ANCHOR,
		/* 221 */ YY_NO_ANCHOR,
		/* 222 */ YY_NO_ANCHOR,
		/* 223 */ YY_NO_ANCHOR,
		/* 224 */ YY_NO_ANCHOR,
		/* 225 */ YY_NO_ANCHOR,
		/* 226 */ YY_NO_ANCHOR,
		/* 227 */ YY_NO_ANCHOR,
		/* 228 */ YY_NO_ANCHOR,
		/* 229 */ YY_NO_ANCHOR,
		/* 230 */ YY_NO_ANCHOR,
		/* 231 */ YY_NO_ANCHOR,
		/* 232 */ YY_NO_ANCHOR,
		/* 233 */ YY_NO_ANCHOR,
		/* 234 */ YY_NO_ANCHOR,
		/* 235 */ YY_NO_ANCHOR,
		/* 236 */ YY_NO_ANCHOR,
		/* 237 */ YY_NO_ANCHOR,
		/* 238 */ YY_NO_ANCHOR,
		/* 239 */ YY_NO_ANCHOR,
		/* 240 */ YY_NO_ANCHOR,
		/* 241 */ YY_NO_ANCHOR,
		/* 242 */ YY_NO_ANCHOR,
		/* 243 */ YY_NO_ANCHOR,
		/* 244 */ YY_NO_ANCHOR,
		/* 245 */ YY_NO_ANCHOR,
		/* 246 */ YY_NO_ANCHOR,
		/* 247 */ YY_NO_ANCHOR,
		/* 248 */ YY_NO_ANCHOR,
		/* 249 */ YY_NO_ANCHOR,
		/* 250 */ YY_NO_ANCHOR,
		/* 251 */ YY_NO_ANCHOR,
		/* 252 */ YY_NO_ANCHOR,
		/* 253 */ YY_NO_ANCHOR,
		/* 254 */ YY_NO_ANCHOR,
		/* 255 */ YY_NO_ANCHOR,
		/* 256 */ YY_NO_ANCHOR,
		/* 257 */ YY_NO_ANCHOR,
		/* 258 */ YY_NO_ANCHOR,
		/* 259 */ YY_NO_ANCHOR,
		/* 260 */ YY_NO_ANCHOR,
		/* 261 */ YY_NO_ANCHOR,
		/* 262 */ YY_NO_ANCHOR,
		/* 263 */ YY_NO_ANCHOR,
		/* 264 */ YY_NO_ANCHOR,
		/* 265 */ YY_NO_ANCHOR,
		/* 266 */ YY_NO_ANCHOR,
		/* 267 */ YY_NO_ANCHOR,
		/* 268 */ YY_NO_ANCHOR,
		/* 269 */ YY_NO_ANCHOR,
		/* 270 */ YY_NO_ANCHOR,
		/* 271 */ YY_NO_ANCHOR,
		/* 272 */ YY_NO_ANCHOR,
		/* 273 */ YY_NO_ANCHOR,
		/* 274 */ YY_NO_ANCHOR,
		/* 275 */ YY_NO_ANCHOR,
		/* 276 */ YY_NO_ANCHOR,
		/* 277 */ YY_NO_ANCHOR,
		/* 278 */ YY_NO_ANCHOR,
		/* 279 */ YY_NO_ANCHOR,
		/* 280 */ YY_NO_ANCHOR,
		/* 281 */ YY_NO_ANCHOR,
		/* 282 */ YY_NO_ANCHOR,
		/* 283 */ YY_NO_ANCHOR,
		/* 284 */ YY_NO_ANCHOR,
		/* 285 */ YY_NO_ANCHOR,
		/* 286 */ YY_NO_ANCHOR,
		/* 287 */ YY_NO_ANCHOR,
		/* 288 */ YY_NO_ANCHOR,
		/* 289 */ YY_NO_ANCHOR,
		/* 290 */ YY_NO_ANCHOR,
		/* 291 */ YY_NO_ANCHOR,
		/* 292 */ YY_NO_ANCHOR,
		/* 293 */ YY_NO_ANCHOR,
		/* 294 */ YY_NO_ANCHOR,
		/* 295 */ YY_NO_ANCHOR,
		/* 296 */ YY_NO_ANCHOR,
		/* 297 */ YY_NO_ANCHOR,
		/* 298 */ YY_NO_ANCHOR,
		/* 299 */ YY_NO_ANCHOR,
		/* 300 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"6:9,2,3,6:2,33,6:18,1,23,34,11,6,11:2,6,14,15,5,17,24,16,11,4,45,47,46,51,4" +
"8,26,52,49,50,53,11:2,21,20,22,11:2,27:26,18,35,19,11,25,11,9,40,7,44,31,32" +
",27,42,37,27,41,8,27,38,36,27:2,29,10,28,30,27,39,43,27:2,12,11,13,6:2,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,301,
"0,1:4,2,1,3,1:6,4,5,6,1:6,7,1,8,9:3,10,9:8,11,9,1:10,12,1,13,14,15,16,8,1,1" +
"1,17,7,18,19,20,7,21,22,23,24,25,20,1,26,27,28,10,29,30,31,32,33,34,35,36,3" +
"7,38,39,4,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61" +
",62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86" +
",87,88,89,90,91,92,56,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107," +
"108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126" +
",127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,9,143,144," +
"145,146,147,148,149,150,151,152,153,135,154,155,131,156,157,158,159,160,161" +
",162,163,164,165,166,167,168,169,170,171,172,173,174,162,165,175,176,177,17" +
"8,179,180,181,182,183,184,185,186,187,188,189,190,176,191,192,193,178,194,1" +
"95,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,44,211,212,2" +
"13:2,214,215,216,217,199,218,195,219,220,221,209,222,223,208,224,225,226,22" +
"7,228,229,230,231,232,233:2,234,229,235,236")[0];

	private int yy_nxt[][] = unpackFromString(237,54,
"1,2,3,4,5,51,6,7,192:3,51,8,9,10,11,59,66,12,13,72,77,82,52,87,14,92,192,15" +
"5,196,192,158,115,-1,60,6,192,53,120,199,202,192:4,268,271,296,92:6,-1:58,1" +
"5,16,-1:55,192,161,192:2,-1:14,192:8,-1:3,192:9,-1,192:8,-1:7,14:4,-1:14,14" +
":8,-1:3,14:9,-1,14:8,-1,15:2,-1,15:29,-1,15:20,-1,116:2,16,75,80,116:8,75:2" +
",116:17,55,116:20,-1,23:2,24,23:10,56:2,23:17,63,25,69,23:18,-1,56:2,24,56:" +
"29,-1,56:20,-1:7,192:4,-1:14,192:8,-1:3,192:9,-1,192:8,-1,75:2,90,75:29,-1," +
"75:20,-1:26,38,-1:18,38:9,-1:26,266,-1:18,266:9,-1:20,22,-1:40,192:4,-1:14," +
"192:7,26,-1:3,192:2,170,192:6,-1,192:8,-1:7,14:4,-1:14,14,242,14:6,-1:3,14:" +
"9,266,242:8,-1,55:3,-1:2,55:8,-1:2,55:38,-1:16,17,-1:44,192:4,-1:14,192:4,2" +
"7,192:3,-1:3,192:9,-1,192:8,-1:7,14:4,-1:14,14,89,14:6,-1:3,14:9,58,89:8,-1" +
",63:2,-1,63:10,-1:2,63:18,57,85,63:18,-1:26,40,-1:18,40:9,-1:17,18,-1:43,19" +
"2:4,-1:14,192:8,-1:3,192:3,28,192:5,-1,192:8,-1:7,14:4,-1:14,14,94,14:6,-1:" +
"3,14:9,65,94:8,-1,23:2,24,23:29,63,64,69,23:18,-1:20,19,-1:40,192:4,-1:14,1" +
"92:6,30,192,-1:3,192:9,-1,192:8,-1:7,14:4,-1:14,14,97,14:6,-1:3,14:9,81,97:" +
"8,-1:26,41,-1:18,41:5,-1,41:2,-1:21,20,-1:40,192:4,-1:14,192:6,31,192,-1:3," +
"192:9,-1,192:8,-1:7,14:4,-1:14,14:8,-1:3,14:9,71,14:8,-1,75:2,90,29,75:28,-" +
"1,75:20,-1:26,42,-1:18,42:9,-1:20,21,-1:40,192:3,32,-1:14,192:8,-1:3,192:9," +
"-1,192:8,-1:7,14:4,-1:14,14:8,-1:3,14:9,76,100:3,14:2,100,14:2,-1,63:2,-1,6" +
"3:30,70,85,63:18,-1:26,43,-1:18,43:9,-1:7,192:4,-1:14,192:6,33,192,-1:3,192" +
":9,-1,192:8,-1:7,14:4,-1:14,14,89,14:6,-1:3,14:9,38,89:8,-1,75:2,90,75,80,7" +
"5:27,-1,75:20,-1:26,44,-1:18,44:9,-1:7,14:4,-1:14,14,54,14:6,-1:3,14:9,50,5" +
"4:8,-1:7,192:4,-1:14,192:6,34,192,-1:3,192:9,-1,192:8,-1:7,14:4,-1:14,14:8," +
"-1:3,14:9,40,14:8,-1:26,45,-1:18,45:9,-1:7,192:4,-1:14,192:8,-1:3,192:5,35," +
"192:3,-1,192:8,-1:7,14:4,-1:14,14:8,-1:3,14:9,42,14:8,-1:26,47,-1:18,47:9,-" +
"1:7,192:4,-1:14,192:8,-1:3,192:2,36,192:6,-1,192:8,-1:7,14:4,-1:14,14:8,-1:" +
"3,14:9,41,14:8,-1:26,48,-1:18,48:9,-1:7,192:3,37,-1:14,192:8,-1:3,192:9,-1," +
"192:8,-1:7,14:4,-1:14,14:8,-1:3,14:9,43,14:8,-1:26,91,-1:18,91:9,-1:7,192:4" +
",-1:14,192:7,39,-1:3,192:9,-1,192:8,-1:7,14:4,-1:14,14:8,-1:3,14:9,44,14:8," +
"-1:26,95,-1:18,95:9,-1:7,14:4,-1:14,14:8,-1:3,14:9,45,14:8,-1:26,111,-1:18," +
"76:3,113,111:2,76,111:2,-1:7,14:4,-1:14,14:8,-1:3,14:9,46,14:8,-1:26,46,-1:" +
"18,46:9,-1:7,14:4,-1:14,14:8,-1:3,14:9,47,14:8,-1:26,41,-1:18,41:5,49,41:2," +
"49,-1:7,14:4,-1:14,14:8,-1:3,14:9,48,14:8,-1:7,192:2,167,192,-1:14,192:8,-1" +
":3,61,192:8,-1,192:8,-1,116:2,16,75:2,116:8,75:2,116:17,55,116:20,-1:26,58," +
"-1:18,58:9,-1:7,14:4,-1:14,14,62,14:6,-1:3,14:9,117,62:8,-1:7,14:4,-1:14,14" +
",103,14:6,-1:3,14:9,86,103:8,-1:7,192:4,-1:14,192:6,67,192,-1:3,192:9,-1,19" +
"2:8,-1:26,65,-1:18,65:9,-1:7,14:4,-1:14,14,68,14:6,-1:3,14:9,121,68:8,-1:7," +
"14:4,-1:14,14,106,14:6,-1:3,14:9,91,106:8,-1:7,192:4,-1:14,192:5,73,192:2,-" +
"1:3,192:9,-1,192:8,-1:26,71,-1:18,71:9,-1:7,14:4,-1:14,14,74,14:6,-1:3,14:9" +
",133,74:8,-1:7,14:4,-1:14,14,108,14:6,-1:3,14:9,95,108:8,-1:7,192:3,78,-1:1" +
"4,192:8,-1:3,192:9,-1,192:8,-1:26,71,-1:18,76:4,71:2,76,71:2,-1:7,14:4,-1:1" +
"4,14,79,14:6,-1:3,14:9,125,79:8,-1:7,14:4,-1:14,14,110,14:6,-1:3,14:9,76,10" +
"0:3,110:2,100,110:2,-1:7,192:3,83,-1:14,192:8,-1:3,192:9,-1,192:8,-1:26,81," +
"-1:18,81:9,-1:7,14:4,-1:14,14,84,14:6,-1:3,14:9,129,84:3,79:2,84:2,79,-1:7," +
"14:4,-1:14,14,112,14:6,-1:3,14:9,98,112:8,-1:7,192:3,88,-1:14,192:8,-1:3,19" +
"2:9,-1,192:8,-1:26,86,-1:18,86:9,-1:7,14:4,-1:14,14,119,14:6,-1:3,14:9,137," +
"119:8,-1:7,14:4,-1:14,14,114,14:6,-1:3,14:9,101,114:8,-1:7,192,93,192:2,-1:" +
"14,192:8,-1:3,192:9,-1,192:8,-1:7,14:4,-1:14,14,123,14:6,-1:3,14:9,141,123:" +
"8,-1:7,192:2,96,192,-1:14,192:8,-1:3,192:9,-1,192:8,-1:26,98,-1:18,98:9,-1:" +
"7,14:4,-1:14,14,84,14:6,-1:3,14:9,129,84:3,127:2,84,131,127,-1:7,192:4,-1:1" +
"4,192:4,99,192:3,-1:3,192:9,-1,192:8,-1:26,101,-1:18,101:9,-1:7,14:4,-1:14," +
"14,135,14:6,-1:3,14:9,144,135:8,-1:7,192:4,-1:14,192:8,-1:3,192:8,102,-1,19" +
"2:8,-1:26,104,-1:18,141:4,104,141:2,104,141,-1:7,14:4,-1:14,14,139,14:6,-1:" +
"3,14:9,147,139:8,-1:7,192:4,-1:14,192:8,-1:3,105,192:8,-1,192:8,-1:26,141,-" +
"1:18,141:9,-1:26,129,-1:18,129:4,107:2,129,109,107,-1:7,192:4,-1:14,192:4,1" +
"24,192:3,-1:3,192:9,-1,192:8,-1:26,117,-1:18,117:9,-1:7,14:4,-1:14,14,118,1" +
"4:6,-1:3,14:9,156,118:8,-1:7,192,128,192:2,-1:14,192:8,-1:3,192:7,195,192,-" +
"1,192:8,-1:26,121,-1:18,121:9,-1:7,14:4,-1:14,14,122,14:6,-1:3,14:9,159,122" +
":8,-1:7,192:2,132,192,-1:14,192:8,-1:3,192:9,-1,192:8,-1:26,125,-1:18,125:9" +
",-1:7,14:4,-1:14,14,126,14:6,-1:3,14:9,168,126:8,-1:7,192:4,-1:14,192:3,179" +
",192:4,-1:3,192:9,-1,192:8,-1:26,129,-1:18,129:4,125:2,129:2,125,-1:7,14:4," +
"-1:14,14,130,14:6,-1:3,14:9,162,130:8,-1:7,192,136,192:2,-1:14,192:8,-1:3,1" +
"92:9,-1,192:8,-1:26,133,-1:18,133:9,-1:7,14:4,-1:14,14,130,14:6,-1:3,14:9,1" +
"65,134:2,130:3,134,130:2,-1:7,192:3,185,-1:14,192:8,-1:3,192:9,-1,192:8,-1:" +
"26,137,-1:18,137:9,-1:7,14:4,-1:14,14,138,14:6,-1:3,14:9,171,138:8,-1:7,192" +
":4,-1:14,192:8,-1:3,192,140,192:7,-1,192:8,-1:26,144,-1:18,144:9,-1:7,14:4," +
"-1:14,14,142,14:6,-1:3,14:9,165,134:2,142:3,145,142:2,-1:7,192:4,-1:14,192:" +
"6,143,192,-1:3,192:9,-1,192:8,-1:26,147,-1:18,147:9,-1:7,14:4,-1:14,14,148," +
"14:6,-1:3,14:9,174,148:8,-1:7,192:4,-1:14,192:5,146,192:2,-1:3,192:9,-1,192" +
":8,-1:26,171,-1:18,171:9,-1:7,14:4,-1:14,14,151,14:6,-1:3,14:9,177,151:8,-1" +
":7,192:4,-1:14,192:6,187,192,-1:3,192:9,-1,192:8,-1:26,150,-1:18,165:3,150," +
"153:2,154,150,153,-1:7,14:4,-1:14,14,166,14:6,-1:3,14:9,200,166:8,-1:7,192:" +
"4,-1:14,192:3,188,192:4,-1:3,192:9,-1,192:8,-1:26,174,-1:18,174:9,-1:7,192:" +
"4,-1:14,192:8,-1:3,192:2,149,192:6,-1,192:8,-1:7,192:2,189,192,-1:14,192:8," +
"-1:3,192:9,-1,192:8,-1:7,192:4,-1:14,192:8,-1:3,192:2,190,192:6,-1,192:8,-1" +
":7,191,192:3,-1:14,192:8,-1:3,192:9,-1,192:8,-1:7,192:4,-1:14,192:6,152,192" +
",-1:3,192:9,-1,192:8,-1:26,156,-1:18,156:9,-1:7,14:4,-1:14,14,157,14:6,-1:3" +
",14:9,193,157:8,-1:7,192:4,-1:14,192:3,182,192:4,-1:3,192:9,-1,192:8,-1:7,1" +
"92:4,-1:14,192:6,164,192,-1:3,192:9,-1,192:8,-1:26,159,-1:18,159:9,-1:7,14:" +
"4,-1:14,14,160,14:6,-1:3,14:9,197,160:8,-1:7,192:4,-1:14,192:8,-1:3,192:6,1" +
"73,192:2,-1,192:8,-1:26,162,-1:18,162:9,-1:7,14:4,-1:14,14,163,14:6,-1:3,14" +
":9,205,163:8,-1:7,192:4,-1:14,192:4,176,192:3,-1:3,192:9,-1,192:8,-1:26,162" +
",-1:18,165:3,162:3,165,162:2,-1:26,168,-1:18,168:9,-1:7,14:4,-1:14,14,169,1" +
"4:6,-1:3,14:9,203,169:7,166,-1:7,14:4,-1:14,14,172,14:6,-1:3,14:9,207,172:8" +
",-1:26,177,-1:18,177:9,-1:7,14:4,-1:14,14,169,14:6,-1:3,14:9,203,169:4,175," +
"169:2,178,-1:26,180,-1:18,207:3,180:2,207:2,180,207,-1:7,14:4,-1:14,14,181," +
"14:6,-1:3,14:9,209,181:8,-1:26,203,-1:18,203:5,183,203:2,186,-1:7,14:4,-1:1" +
"4,14,204,14:6,-1:3,14:9,222,204:8,-1:26,207,-1:18,207:9,-1:7,14:4,-1:14,14," +
"204,14:6,-1:3,14:9,222,184:3,204:2,184,204:2,-1:26,209,-1:18,209:9,-1:26,19" +
"3,-1:18,193:9,-1:7,14:4,-1:14,14,194,14:6,-1:3,14:9,218,194:8,-1:26,197,-1:" +
"18,197:9,-1:7,14:4,-1:14,14,198,14:6,-1:3,14:9,220,198:8,-1:26,200,-1:18,20" +
"0:9,-1:7,14:4,-1:14,14,201,14:6,-1:3,14:9,226,201:8,-1:26,203,-1:18,203:8,2" +
"00,-1:7,14:4,-1:14,14,204,14:6,-1:3,14:9,224,206:3,204:2,206,204:2,-1:26,20" +
"5,-1:18,205:9,-1:7,14:4,-1:14,14,208,14:6,-1:3,14:9,224,206:2,210,208:2,206" +
",208:2,-1:26,222,-1:18,222:9,-1:7,14:4,-1:14,14,212,14:6,-1:3,14:9,228,212:" +
"8,-1:26,226,-1:18,226:9,-1:7,14:4,-1:14,14,214,14:6,-1:3,14:9,230,214:4,227" +
",214:2,227,-1:26,211,-1:18,224:3,213,211:2,224,211,215,-1:7,14:4,-1:14,14,2" +
"16,14:6,-1:3,14:9,245,216:3,214,227,216:2,227,-1:26,217,-1:18,228:3,217:2,2" +
"28:2,217,228,-1:7,14:4,-1:14,14,227,14:6,-1:3,14:9,245,227:8,-1:26,228,-1:1" +
"8,228:9,-1:7,14:4,-1:14,14,216,14:6,-1:3,14:9,245,214:2,216,214,227,214,216" +
",227,-1:7,14:4,-1:14,14,214,14:6,-1:3,14:9,245,214:4,227,214:2,227,-1:26,21" +
"8,-1:18,218:9,-1:7,14:4,-1:14,14,219,14:6,-1:3,14:9,241,219:8,-1:26,220,-1:" +
"18,220:9,-1:7,14:4,-1:14,14,221,14:6,-1:3,14:9,243,221:8,-1:7,14:4,-1:14,14" +
",223,14:6,-1:3,14:9,249,223:8,-1:26,222,-1:18,224:4,222:2,224,222:2,-1:7,14" +
":4,-1:14,14,225,14:6,-1:3,14:9,247,225:4,227,225:2,227,-1:7,14:4,-1:14,14,2" +
"25,14:6,-1:3,14:9,247,225:3,229,231,225:2,231,-1:26,245,-1:18,245:9,-1:7,14" +
":4,-1:14,14,233,14:6,-1:3,14:9,251,235:2,233,237:2,235,233,237,-1:26,245,-1" +
":18,230:4,245:2,230,245:2,-1:7,14:4,-1:14,14,239,14:6,-1:3,14:9,251,239:7,2" +
"37,-1:26,243,-1:18,243:9,-1:7,14:4,-1:14,14,237,14:6,-1:3,14:9,278,237:8,-1" +
":26,232,-1:18,249:3,232:3,249,232,249,-1:7,14:4,-1:14,14,239,14:6,-1:3,14:9" +
",253,233:3,239:2,233,239,237,-1:26,247,-1:18,247:4,234,236,247:2,238,-1:7,1" +
"4:4,-1:14,14,237,14:6,-1:3,14:9,251,240:3,237:2,240,237:2,-1:26,232,-1:18,2" +
"49:3,232:2,249:2,232,249,-1:7,14:4,-1:14,14,233,14:6,-1:3,14:9,253,233:3,23" +
"7:2,233:2,237,-1:26,249,-1:18,249:9,-1:26,253,-1:18,251:3,253,251,278,251,2" +
"53,278,-1:26,251,-1:18,251:5,278,251:2,278,-1:26,241,-1:18,241:9,-1:7,14:4," +
"-1:14,14,267,14:6,-1:3,14:9,293,270,295,267:6,-1:26,50,-1:18,50:9,-1:7,14:4" +
",-1:14,14,273,14:6,-1:3,14:9,297,273,275,273:6,-1:26,287,-1:18,297,287,288," +
"287:2,289,287:2,300,-1:7,14:4,-1:14,14,244,14:6,-1:3,14:9,276,244:8,-1:26,2" +
"90,-1:18,298:3,291,290,292,298,290,294,-1:7,14:4,-1:14,14,246,14:6,-1:3,14:" +
"9,280,248:2,250,246:2,248,246:2,-1:7,14:4,-1:14,14,252,14:6,-1:3,14:9,282,2" +
"54:3,252:2,254,252,256,-1:7,14:4,-1:14,14,258,14:6,-1:3,14:9,284,258:4,252," +
"258:2,256,-1:26,247,-1:18,247:5,245,247:2,245,-1:7,14:4,-1:14,14,260,14:6,-" +
"1:3,14:9,286,262:2,260,256:2,262,260,256,-1:7,14:4,-1:14,14,256,14:6,-1:3,1" +
"4:9,299,256:8,-1:26,251,-1:18,253:4,251,278,253,251,278,-1:26,253,-1:18,253" +
":4,251,278,253:2,278,-1:26,255,-1:18,276:3,255:3,276,255,276,-1:26,257,-1:1" +
"8,280:3,259,257,261,280,257,263,-1:26,255,-1:18,276:3,255:2,276:2,255,276,-" +
"1:26,264,-1:18,282:3,265,264,285,282,264,299,-1:26,284,-1:18,284:5,285,284:" +
"2,299,-1:26,265,-1:18,286:3,265,299:2,286,265,299,-1:26,269,-1:18,293,272,2" +
"74,269:6,-1:26,299,-1:18,299:9,-1:7,14:4,-1:14,14,277,14:6,-1:3,14:9,298,27" +
"9:3,277,281,279,277,283,-1:26,278,-1:18,280:4,278:2,280,278:2,-1:26,278,-1:" +
"18,278:9,-1:26,276,-1:18,276:9");

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
						{ return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Unsupported Character", yyline)); }
					case -7:
						break;
					case 6:
						{ throw new RuntimeException("Unmatched lexeme " +
                            yytext() + " at line " + yyline); }
					case -8:
						break;
					case 7:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -9:
						break;
					case 8:
						{ return new Symbol(TokenIds.LBRACE,
                            new Token("LBRACE", yyline)); }
					case -10:
						break;
					case 9:
						{ return new Symbol(TokenIds.RBRACE,
                            new Token("RBRACE", yyline)); }
					case -11:
						break;
					case 10:
						{ return new Symbol(TokenIds.LPAREN,
                            new Token("LPAREN", yyline)); }
					case -12:
						break;
					case 11:
						{ return new Symbol(TokenIds.RPAREN,
                            new Token("RPAREN", yyline)); }
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
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -16:
						break;
					case 15:
						{}
					case -17:
						break;
					case 16:
						{ return new Symbol(TokenIds.error, new Token("LEX_ERROR","unterminated multiline comment", yyline));}
					case -18:
						break;
					case 17:
						{ return new Symbol(TokenIds.DECR,
                            new Token("DECR", yyline)); }
					case -19:
						break;
					case 18:
						{ return new Symbol(TokenIds.INCR,
                            new Token("INCR", yyline)); }
					case -20:
						break;
					case 19:
						{ return new Symbol(TokenIds.EQ,
                            new Token("EQ", yyline)); }
					case -21:
						break;
					case 20:
						{ return new Symbol(TokenIds.LEQ,
                            new Token("LEQ", yyline)); }
					case -22:
						break;
					case 21:
						{ return new Symbol(TokenIds.GEQ,
                            new Token("GEQ", yyline)); }
					case -23:
						break;
					case 22:
						{ return new Symbol(TokenIds.NE,
                            new Token("NE", yyline)); }
					case -24:
						break;
					case 24:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token("LEX_ERROR","String Constant spans multiple lines"
                                                                                              , yyline));}
					case -25:
						break;
					case 25:
						{ return new Symbol(TokenIds.STRING_CONST,
                                                               new Token("STRING_CONST",
                                                               yytext().substring(1,yytext().length()-1)
                                                               , yyline));}
					case -26:
						break;
					case 26:
						{ return new Symbol(TokenIds.IF,
                            new Token("IF", yyline)); }
					case -27:
						break;
					case 27:
						{ return new Symbol(TokenIds.FOR,
                            new Token("FOR", yyline)); }
					case -28:
						break;
					case 28:
						{ return new Symbol(TokenIds.NEW,
                            new Token("NEW", yyline)); }
					case -29:
						break;
					case 29:
						{}
					case -30:
						break;
					case 30:
						{ return new Symbol(TokenIds.BOOLEAN_CONST,
                                               new Token("TRUE",yyline));}
					case -31:
						break;
					case 31:
						{ return new Symbol(TokenIds.ELSE,
                            new Token("ELSE", yyline)); }
					case -32:
						break;
					case 32:
						{ return new Symbol(TokenIds.CLASS,
						    new Token("CLASS", yyline)); }
					case -33:
						break;
					case 33:
						{ return new Symbol(TokenIds.BOOLEAN_CONST,
                             new Token("FALSE",yyline));}
					case -34:
						break;
					case 34:
						{ return new Symbol(TokenIds.WHILE,
                            new Token("WHILE", yyline)); }
					case -35:
						break;
					case 35:
						{ return new Symbol(TokenIds.BREAK,
                            new Token("BREAK", yyline)); }
					case -36:
						break;
					case 36:
						{ return new Symbol(TokenIds.RETURN,
                            new Token("RETURN", yyline)); }
					case -37:
						break;
					case 37:
						{ return new Symbol(TokenIds.EXTENDS,
                            new Token("EXTENDS", yyline)); }
					case -38:
						break;
					case 38:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token
                                                                 ("LEX_ERROR", yytext() + " is too large to be an integer at line " + yyline, yyline)); }
					case -39:
						break;
					case 39:
						{ return new Symbol(TokenIds.INSTANCEOF,
                            new Token("INSTANCEOF", yyline)); }
					case -40:
						break;
					case 40:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token
                                                            ("LEX_ERROR", yytext() + " is too large to be an integer at line " + yyline, yyline)); }
					case -41:
						break;
					case 41:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -42:
						break;
					case 42:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token
                                                        ("LEX_ERROR", yytext() + " is too large to be an integer at line " + yyline, yyline)); }
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
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -51:
						break;
					case 51:
						{ return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Unsupported Character", yyline)); }
					case -52:
						break;
					case 52:
						{ throw new RuntimeException("Unmatched lexeme " +
                            yytext() + " at line " + yyline); }
					case -53:
						break;
					case 53:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -54:
						break;
					case 54:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -55:
						break;
					case 55:
						{ return new Symbol(TokenIds.error, new Token("LEX_ERROR","unterminated multiline comment", yyline));}
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
						{ return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Unsupported Character", yyline)); }
					case -59:
						break;
					case 60:
						{ throw new RuntimeException("Unmatched lexeme " +
                            yytext() + " at line " + yyline); }
					case -60:
						break;
					case 61:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -61:
						break;
					case 62:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -62:
						break;
					case 64:
						{ return new Symbol(TokenIds.STRING_CONST,
                                                               new Token("STRING_CONST",
                                                               yytext().substring(1,yytext().length()-1)
                                                               , yyline));}
					case -63:
						break;
					case 65:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -64:
						break;
					case 66:
						{ return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Unsupported Character", yyline)); }
					case -65:
						break;
					case 67:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -66:
						break;
					case 68:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -67:
						break;
					case 70:
						{ return new Symbol(TokenIds.STRING_CONST,
                                                               new Token("STRING_CONST",
                                                               yytext().substring(1,yytext().length()-1)
                                                               , yyline));}
					case -68:
						break;
					case 71:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -69:
						break;
					case 72:
						{ return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Unsupported Character", yyline)); }
					case -70:
						break;
					case 73:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -71:
						break;
					case 74:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -72:
						break;
					case 76:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -73:
						break;
					case 77:
						{ return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Unsupported Character", yyline)); }
					case -74:
						break;
					case 78:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -75:
						break;
					case 79:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -76:
						break;
					case 81:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -77:
						break;
					case 82:
						{ return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Unsupported Character", yyline)); }
					case -78:
						break;
					case 83:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -79:
						break;
					case 84:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -80:
						break;
					case 86:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -81:
						break;
					case 87:
						{ return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Unsupported Character", yyline)); }
					case -82:
						break;
					case 88:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -83:
						break;
					case 89:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -84:
						break;
					case 91:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -85:
						break;
					case 92:
						{ return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Unsupported Character", yyline)); }
					case -86:
						break;
					case 93:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -87:
						break;
					case 94:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -88:
						break;
					case 95:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -89:
						break;
					case 96:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -90:
						break;
					case 97:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -91:
						break;
					case 98:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -92:
						break;
					case 99:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -93:
						break;
					case 100:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
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
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -97:
						break;
					case 104:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -98:
						break;
					case 105:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -99:
						break;
					case 106:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -100:
						break;
					case 107:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -101:
						break;
					case 108:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -102:
						break;
					case 109:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -103:
						break;
					case 110:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -104:
						break;
					case 111:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -105:
						break;
					case 112:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -106:
						break;
					case 113:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -107:
						break;
					case 114:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -108:
						break;
					case 115:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -109:
						break;
					case 116:
						{ return new Symbol(TokenIds.error, new Token("LEX_ERROR","unterminated multiline comment", yyline));}
					case -110:
						break;
					case 117:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -111:
						break;
					case 118:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -112:
						break;
					case 119:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
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
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -116:
						break;
					case 123:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
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
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -120:
						break;
					case 127:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
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
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -124:
						break;
					case 131:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
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
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -128:
						break;
					case 135:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
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
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -132:
						break;
					case 139:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -133:
						break;
					case 140:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -134:
						break;
					case 141:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -135:
						break;
					case 142:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -136:
						break;
					case 143:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -137:
						break;
					case 144:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -138:
						break;
					case 145:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -139:
						break;
					case 146:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -140:
						break;
					case 147:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -141:
						break;
					case 148:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
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
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -145:
						break;
					case 152:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
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
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -149:
						break;
					case 156:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -150:
						break;
					case 157:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -151:
						break;
					case 158:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -152:
						break;
					case 159:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -153:
						break;
					case 160:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -154:
						break;
					case 161:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -155:
						break;
					case 162:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -156:
						break;
					case 163:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -157:
						break;
					case 164:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -158:
						break;
					case 165:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -159:
						break;
					case 166:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -160:
						break;
					case 167:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -161:
						break;
					case 168:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -162:
						break;
					case 169:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -163:
						break;
					case 170:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -164:
						break;
					case 171:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -165:
						break;
					case 172:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -166:
						break;
					case 173:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -167:
						break;
					case 174:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -168:
						break;
					case 175:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -169:
						break;
					case 176:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -170:
						break;
					case 177:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -171:
						break;
					case 178:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -172:
						break;
					case 179:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -173:
						break;
					case 180:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -174:
						break;
					case 181:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -175:
						break;
					case 182:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -176:
						break;
					case 183:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -177:
						break;
					case 184:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -178:
						break;
					case 185:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -179:
						break;
					case 186:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -180:
						break;
					case 187:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -181:
						break;
					case 188:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -182:
						break;
					case 189:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -183:
						break;
					case 190:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -184:
						break;
					case 191:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -185:
						break;
					case 192:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -186:
						break;
					case 193:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -187:
						break;
					case 194:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -188:
						break;
					case 195:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -189:
						break;
					case 196:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -190:
						break;
					case 197:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -191:
						break;
					case 198:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -192:
						break;
					case 199:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -193:
						break;
					case 200:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -194:
						break;
					case 201:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -195:
						break;
					case 202:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -196:
						break;
					case 203:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -197:
						break;
					case 204:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -198:
						break;
					case 205:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -199:
						break;
					case 206:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -200:
						break;
					case 207:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -201:
						break;
					case 208:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -202:
						break;
					case 209:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -203:
						break;
					case 210:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -204:
						break;
					case 211:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -205:
						break;
					case 212:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -206:
						break;
					case 213:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -207:
						break;
					case 214:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -208:
						break;
					case 215:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -209:
						break;
					case 216:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -210:
						break;
					case 217:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -211:
						break;
					case 218:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -212:
						break;
					case 219:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -213:
						break;
					case 220:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -214:
						break;
					case 221:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -215:
						break;
					case 222:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -216:
						break;
					case 223:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -217:
						break;
					case 224:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -218:
						break;
					case 225:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -219:
						break;
					case 226:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -220:
						break;
					case 227:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -221:
						break;
					case 228:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -222:
						break;
					case 229:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -223:
						break;
					case 230:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -224:
						break;
					case 231:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -225:
						break;
					case 232:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -226:
						break;
					case 233:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -227:
						break;
					case 234:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -228:
						break;
					case 235:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -229:
						break;
					case 236:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -230:
						break;
					case 237:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -231:
						break;
					case 238:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -232:
						break;
					case 239:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -233:
						break;
					case 240:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -234:
						break;
					case 241:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -235:
						break;
					case 242:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -236:
						break;
					case 243:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -237:
						break;
					case 244:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -238:
						break;
					case 245:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -239:
						break;
					case 246:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -240:
						break;
					case 247:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -241:
						break;
					case 248:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -242:
						break;
					case 249:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -243:
						break;
					case 250:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -244:
						break;
					case 251:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -245:
						break;
					case 252:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -246:
						break;
					case 253:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -247:
						break;
					case 254:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -248:
						break;
					case 255:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -249:
						break;
					case 256:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -250:
						break;
					case 257:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -251:
						break;
					case 258:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -252:
						break;
					case 259:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -253:
						break;
					case 260:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -254:
						break;
					case 261:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -255:
						break;
					case 262:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -256:
						break;
					case 263:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -257:
						break;
					case 264:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -258:
						break;
					case 265:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -259:
						break;
					case 266:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -260:
						break;
					case 267:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -261:
						break;
					case 268:
						{ return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Unsupported Character", yyline)); }
					case -262:
						break;
					case 269:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -263:
						break;
					case 270:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -264:
						break;
					case 271:
						{ return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Unsupported Character", yyline)); }
					case -265:
						break;
					case 272:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -266:
						break;
					case 273:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -267:
						break;
					case 274:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -268:
						break;
					case 275:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -269:
						break;
					case 276:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -270:
						break;
					case 277:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -271:
						break;
					case 278:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -272:
						break;
					case 279:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -273:
						break;
					case 280:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -274:
						break;
					case 281:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -275:
						break;
					case 282:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -276:
						break;
					case 283:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -277:
						break;
					case 284:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -278:
						break;
					case 285:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -279:
						break;
					case 286:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -280:
						break;
					case 287:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -281:
						break;
					case 288:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -282:
						break;
					case 289:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -283:
						break;
					case 290:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -284:
						break;
					case 291:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -285:
						break;
					case 292:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -286:
						break;
					case 293:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -287:
						break;
					case 294:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -288:
						break;
					case 295:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -289:
						break;
					case 296:
						{ return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Unsupported Character", yyline)); }
					case -290:
						break;
					case 297:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -291:
						break;
					case 298:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -292:
						break;
					case 299:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -293:
						break;
					case 300:
						{ return new Symbol(TokenIds.INT_CONST,
                                new Token("INT_CONST", yytext(), yyline)); }
					case -294:
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
