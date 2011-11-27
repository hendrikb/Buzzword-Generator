package com.plan2beta.lib.buzzwords;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Test;

import com.plan2beta.lib.buzzwords.exceptions.DatabaseException;
import com.plan2beta.lib.buzzwords.exceptions.NoWordsLeftException;


public class BuzzWordFactoryTest {

	@Test
	public final void testGeneralMalfunction() {
		BuzzWordFactory bs;
		try {
			bs = new BuzzWordFactory();
			assertNotNull(bs.createPhrase(5));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("This may never fail");
		}
		catch (DatabaseException e) {
			e.printStackTrace();
			fail("This may never fail");
		}
		catch (NoWordsLeftException e) {
			e.printStackTrace();
			fail("This may never fail");
		}

	}

	@Test
	public final void testBuzzWordFactory() {
		BuzzWordFactory bs = null;
		try {
			bs = new BuzzWordFactory();
		}
		catch (FileNotFoundException e) {
			fail("May not fail generally");
		}
		catch (DatabaseException e) {
			fail("May not fail generally");
		}
		try {
			assertTrue(bs.createPhrase(1).indexOf(" ") == -1);
			assertTrue(bs.createPhrase(2).indexOf(" ") > 1);
			assertTrue(bs.createPhrase(3).indexOf(" ") > 1);
			assertTrue(bs.createPhrase(10).indexOf(" ") > 1);
		}
		catch (NoWordsLeftException e) {
			fail("these are generally working principles, they may not fail. Error: " + e.getMessage());
		}

	}

	@Test
	public final void testFaultyStreams() {
		try {
			BuzzWordFactory bs = new BuzzWordFactory(new FileInputStream("nonexistant"), new FileInputStream("nonexistant"), new FileInputStream("nonexistant"));
			fail("This may not work because the stream files are non existant");
			bs.createPhrase(0);
		}
		catch (FileNotFoundException e) {
			assertNotNull(e);
		}
		catch (DatabaseException e) {
			fail("This databaseException may never be thrown because the FileNotFound check had to be triggered earlier");
		}
		catch (NoWordsLeftException e) {
			fail("This may never happen.");
		}

	}

	@Test
	public final void testBuzzWordFactoryFromScratch() {
		InputStream nouns = new ByteArrayInputStream("noun\nnoun2".getBytes());
		InputStream adjs = new ByteArrayInputStream("super".getBytes());
		InputStream nExts = new ByteArrayInputStream("driven".getBytes());

		BuzzWordFactory bs = null;
		try {
			bs = new BuzzWordFactory(nouns, adjs, nExts);
		}
		catch (DatabaseException e) {
			fail("database must be able to load automatically");
			e.printStackTrace();
		}

		try {
			assertEquals(null, bs.createPhrase(0));
			String s = bs.createPhrase(1);
			assertTrue("The string was " + s + " but it should have been either noun or noun2", s.equals("noun") || s.equals("noun2"));

		}
		catch (NoWordsLeftException e) {
			fail("This may not fail as we have enough words in the stream.");
		}

		try {
			bs.createPhrase(20);
			fail("This must fail because we only have a couple of words in the DB, not 20");
		}
		catch (NoWordsLeftException e) {
			assertNotNull(e);
		}

	}
}
