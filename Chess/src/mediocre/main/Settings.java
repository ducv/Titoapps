package mediocre.main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import mediocre.def.Definitions;
import mediocre.transtable.TranspositionTable;

public class Settings implements Definitions {

	/* Transposition tables */
	private TranspositionTable transpositionTable;
	private TranspositionTable repTable;
	private TranspositionTable evalHash;
	private TranspositionTable pawnHash;
	private int tt_size;
	private static final int REP_SIZE = 16384; // 2^14
	private int eval_size;
	private int pawn_size;

	/* Book */
	private Book book;
	private boolean useOwnBook;
	private String bookLocation;

	private static Settings instance = null;

	/**
	 * Singleton (with private constructor and the getInstance method, only one
	 * instance of the class can ever be created)
	 */
	private Settings() {
		initSettings();

		if (useOwnBook) {
			book = Book.getInstance(bookLocation); // Initialize the opening
			// book
			if (book == null)
				useOwnBook = false;
		}

		repTable = new TranspositionTable(REP_SIZE, true); // Initialize the
		// history
		transpositionTable = new TranspositionTable(tt_size, 6);
		evalHash = new TranspositionTable(eval_size, 2);
		pawnHash = new TranspositionTable(pawn_size, 3);
	}

	/**
	 * Creates the singleton of the Book and returns it
	 * 
	 * @return The instance of the settings
	 */
	public static Settings getInstance() {
		if (instance == null) {
			instance = new Settings();
		}
		return instance;
	}

	/**
	 * Tries to find mediocre.ini and use the settings from there, if
	 * mediocre.ini is not found default settings will be used.
	 * 
	 * If mediocre.ini is found but missing some setting the default value will
	 * be used for that setting
	 */
	public void initSettings() {
		BufferedReader in;
		boolean ub = false;
		boolean mts = false;
		boolean ets = false;
		boolean pts = false;
		String str = null;
		/**
		 * Note: i changed the default value of variable: useDefault to true The
		 * original value is false
		 */
		boolean useDefault = true;
		for (int i = 0; i < 3; i++) {
			try {
				if (i == 0)
					in = new BufferedReader(new FileReader("mediocre.ini"));
				else if (i == 1)
					in = new BufferedReader(new FileReader("bin/mediocre.ini"));
				else
					in = new BufferedReader(new FileReader("../mediocre.ini"));

				while ((str = in.readLine()) != null) {
					if (str.startsWith("ub none")) {
						useOwnBook = false;
						ub = true;
					} else if (str.startsWith("ub ")) {
						bookLocation = str.substring(3);
						useOwnBook = true;
						ub = true;
					} else if (str.startsWith("mts ")) {
						tt_size = Integer.parseInt(str.substring(4));
						tt_size = tt_size * 1024 * 1024 * 8 / 32 / 6;
						mts = true;
					} else if (str.startsWith("ets ")) {
						eval_size = Integer.parseInt(str.substring(4));
						eval_size = eval_size * 1024 * 1024 * 8 / 32 / 2;
						ets = true;
					} else if (str.startsWith("pts ")) {
						pawn_size = Integer.parseInt(str.substring(4));
						pawn_size = pawn_size * 1024 * 1024 * 8 / 32 / 3;
						pts = true;
					}
				}
				in.close();

				break;
			} catch (NullPointerException ex) {
				System.out.println("Error: Can't recognize; " + str);
				break;
			} catch (NumberFormatException ex) {
				System.out.println("Error: Can't recognize; " + str);
				break;
			} catch (FileNotFoundException e) {
				if (i == 2) {
					System.out
							.println("Error: mediocre.ini not found; using default setup");
					useDefault = true;
				}

			} catch (IOException e) {
				System.out
						.println("Error: mediocre.ini was found but an error occured while reading it; using default setup");
				useDefault = true;
			}
		}

		if (!useDefault && (!ub || !mts || !ets || !pts)) {
			if (!ub)
				System.out
						.println("Error: missing \"ub [true/false]\" in mediocre.ini; using default setup");
			if (!mts)
				System.out
						.println("Error: missing \"mts [size]\" in mediocre.ini; using default setup");
			if (!ets)
				System.out
						.println("Error: missing \"ets [size]\" in mediocre.ini; using default setup");
			if (!pts)
				System.out
						.println("Error: missing \"pts [size]\" in mediocre.ini; using default setup");
			useDefault = true;
		}

		if (useDefault) {
			// Own book will not be used by default
			useOwnBook = false;
			/**
			 * Also note: change the default of these variables; The original of
			 * them are: tt_size = 1048576; eval_size = 524288; pawn_size =
			 * 524288;
			 */
			tt_size = 65536; // 2^20, will translate to 24mb hash
			eval_size = 32768; // 2^19, will translate to 6mb hash with two
			// slots per entry
			pawn_size = 32768; // 2^19, will translate to 6mb hash with two
			// slots per entry (subject to change)
		}
	}

	public TranspositionTable getTranspositionTable() {
		return transpositionTable;
	}

	public TranspositionTable getRepTable() {
		return repTable;
	}

	public TranspositionTable getEvalHash() {
		return evalHash;
	}

	public TranspositionTable getPawnHash() {
		return pawnHash;
	}

	public int getTt_size() {
		return tt_size;
	}

	public static int getREP_SIZE() {
		return REP_SIZE;
	}

	public int getEval_size() {
		return eval_size;
	}

	public int getPawn_size() {
		return pawn_size;
	}

	public Book getBook() {
		return book;
	}

	public boolean isUseOwnBook() {
		return useOwnBook;
	}

	public String getBookLocation() {
		return bookLocation;
	}
}
