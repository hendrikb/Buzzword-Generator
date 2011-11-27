import java.io.FileNotFoundException;

import com.plan2beta.lib.buzzwords.BuzzWordFactory;
import com.plan2beta.lib.buzzwords.exceptions.DatabaseException;
import com.plan2beta.lib.buzzwords.exceptions.NoWordsLeftException;


public class BuzzWordApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			BuzzWordFactory bwf = new BuzzWordFactory();
			System.out.println(bwf.createPhrase(2));
			System.out.println(bwf.createPhrase(3));
			System.out.println(bwf.createPhrase(4));
			System.out.println(bwf.createPhrase(5));
		}
		catch (NoWordsLeftException e) {
			System.err.println("Out of words");
		}
		catch (DatabaseException e) {
			System.err.println("couldnt load database: " + e.getMessage());
		}
		catch (FileNotFoundException e) {
			System.err.println("couldnt load database files: " + e.getMessage());
		}
	}

}
