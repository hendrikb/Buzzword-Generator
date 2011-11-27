package com.plan2beta.lib.buzzwords;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import com.plan2beta.lib.buzzwords.exceptions.DatabaseException;
import com.plan2beta.lib.buzzwords.exceptions.NoWordsLeftException;


/***
 * This BuzzWord Factory delivers randomly generated buzz word combinations.
 * 
 * @author Hendrik Bergunde <hbergunde@gmx.de>
 */
public class BuzzWordFactory {

	private final InputStream nounStream;
	private final InputStream nounExtsStream;
	private final InputStream adjectiveStream;
	private final Hashtable<String, ArrayList<String>> wordsDb;

	/***
	 * Build up a BuzzWord Factory which will give you randomly generated buzz
	 * words, combined from the "databases" you provide in nounStream,
	 * adjectiveStream, nounExtsStream
	 * 
	 * @param stream_of_nouns
	 *            A stream of nouns, one by line. The BuzzwordFactory will use
	 *            these to generate the longer buzzwords. Maybe you could use
	 *            FileStreams?
	 * @param stream_of_adjectives
	 *            A stream of adjectives, one by line. The BuzzwordFactory will
	 *            use these to generate the longer buzzwords. Maybe you could
	 *            use FileStreams?
	 * @param stream_of_noun_extensions
	 *            A stream of noun extensions (i.e. -noun--xxx, where xxx is the
	 *            noun extensions, e.g. test-driven, where driven is the noun
	 *            extension), one by line. The BuzzwordFactory will use these to
	 *            generate the longer buzzwords. Maybe you could use
	 *            FileStreams?
	 * @throws DatabaseException
	 *             Thrown if generally something is not alright while loading
	 *             the database.
	 * @see BuzzWordFactory.createPhrase
	 */
	public BuzzWordFactory(InputStream stream_of_nouns, InputStream stream_of_adjectives, InputStream stream_of_noun_extensions) throws DatabaseException {
		this.nounStream = stream_of_nouns;
		this.adjectiveStream = stream_of_adjectives;
		this.nounExtsStream = stream_of_noun_extensions;

		wordsDb = new Hashtable<String, ArrayList<String>>();

		try {
			wordsDb.put("nouns", LoadDatabase(this.nounStream));
			wordsDb.put("adjectives", LoadDatabase(this.adjectiveStream));
			wordsDb.put("nounexts", LoadDatabase(this.nounExtsStream));
		}
		catch (IOException e) {
			throw new DatabaseException("Something went wrong while loading the words database.", e);
		}
		catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	/***
	 * Create a Buzzword Factory with an example default database.
	 * 
	 * @see BuzzWordFactory
	 * @throws DatabaseException
	 *             Thrown if generally something is not alright while loading
	 *             the database.
	 * @throws FileNotFoundException
	 *             Thrown if the default database could not be loaded.
	 */
	public BuzzWordFactory() throws DatabaseException, FileNotFoundException {
		this(new FileInputStream(new File("db/it/nouns.txt")), new FileInputStream(new File("db/it/adjectives.txt")), new FileInputStream(new File("db/it/nounexts.txt")));
	}

	private final static int NOUN = 0;
	private final static int ADJECTIVE = 1;
	private final static int NOUNEXTS = 2;

	/***
	 * Create an award winning bingo owning awesomeness enabled buzzword phrase
	 * 
	 * @param length
	 *            The buzzword phrase's length
	 * @return the buzzword phrase
	 * @throws NoWordsLeftException
	 *             Thrown if the database ran out of words. The desired length
	 *             of the phrase is bigger than the provided list of words.
	 */
	public String createPhrase(int length) throws NoWordsLeftException {

		Hashtable<String, ArrayList<String>> db = new Hashtable<String, ArrayList<String>>(wordsDb);

		if (length > 1) {
			String beginningAdjective = popAdjective(db);
			return beginningAdjective + " " + createPhrase(length - 1, db);
		}
		else return createPhrase(length, db);

	}

	private String createPhrase(int length, Hashtable<String, ArrayList<String>> db) throws NoWordsLeftException {

		if (length == 0) return null;

		Random r = new Random();
		int index;
		if (length == 1) {
			index = r.nextInt(db.get("nouns").size());
			return db.get("nouns").remove(index);
		}
		else {
			int useWhat = r.nextInt(3); // 3 is EXCLUSIVE, so 3 will never come! So we have 0 = Nouns, 1 = adjectives, 2 = nounextensions
			try {
				switch (useWhat) {
					default:
						index = r.nextInt(db.get("nouns").size());
						return db.get("nouns").remove(index) + " " + createPhrase(length - 1, db);
					case ADJECTIVE:
						index = r.nextInt(db.get("adjectives").size());
						return db.get("adjectives").remove(index) + " " + createPhrase(length - 1, db);
					case NOUNEXTS:
						index = r.nextInt(db.get("nounexts").size());
						return db.get("nouns").remove(r.nextInt(db.get("nouns").size())) + "-" + db.get("nounexts").remove(index) + " " + createPhrase(length - 1, db);
				}
			}
			catch (IllegalArgumentException e) {
				throw new NoWordsLeftException(e);
			}
			catch (IndexOutOfBoundsException e) {
				throw new NoWordsLeftException(e);
			}
		}
	}

	private String popAdjective(Hashtable<String, ArrayList<String>> db) {
		if (db.size() > 0) {
			int index = new Random().nextInt(db.get("adjectives").size());
			return db.get("adjectives").remove(index);
		}
		else return null;
	}

	private ArrayList<String> LoadDatabase(InputStream nounStream2) throws IOException, DatabaseException {
		InputStreamReader isr = new InputStreamReader(nounStream2);
		BufferedReader br = new BufferedReader(isr);
		ArrayList<String> list = new ArrayList<String>();
		String line;
		while ((line = br.readLine()) != null) {
			if (line.length() > 2 && line.matches("^\\w+")) {
				list.add(line);
			}
		}

		br.close();
		isr.close();
		nounStream2.close();

		if (list.size() == 0) {
			throw new DatabaseException("tried to load the list, but the list file seems to be empty.");
		}

		return list;
	}
}
