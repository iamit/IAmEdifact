package test.nl.iamit.edifact;

import static org.junit.Assert.*;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.activation.DataContentHandler;
import javax.activation.MailcapCommandMap;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeUtility;

import nl.iamit.edifact.EdifactMimePart;
import nl.iamit.edifact.IAmMailEdifact;


import org.junit.Test;

public class TestIAmMailEdifact {
	private static Logger log = Logger.getLogger(TestIAmMailEdifact.class
			.getName());

	@Test
	public void setUp() throws Exception {
		// get the top Logger:
		Logger topLogger = java.util.logging.Logger.getLogger("");
		topLogger.setLevel(java.util.logging.Level.FINEST);

		Logger topLoggerIAmIT = java.util.logging.Logger.getLogger("nl.iamit");
		topLoggerIAmIT.setLevel(java.util.logging.Level.FINEST);
		Logger topLoggerTest = java.util.logging.Logger
				.getLogger("test.nl.iamit");
		topLoggerTest.setLevel(java.util.logging.Level.FINEST);
		Logger topLoggerExternal = java.util.logging.Logger.getLogger("com");
		topLoggerExternal.setLevel(java.util.logging.Level.INFO);
		// Handler for console (reuse it if it already exists)
		Handler consoleHandler = null;
		// see if there is already a console handler
		for (Handler handler : topLogger.getHandlers()) {
			if (handler instanceof ConsoleHandler) {
				// found the console handler
				consoleHandler = handler;
				break;
			}
		}

		if (consoleHandler == null) {
			// there was no console handler found, create a new one
			consoleHandler = new ConsoleHandler();
			topLogger.addHandler(consoleHandler);

		}
		// set the console handler to fine:
		consoleHandler.setLevel(java.util.logging.Level.FINEST);

	}

	@Test
	public void showMailCaps() {
		MailcapCommandMap mccm = new MailcapCommandMap();
		for (String mimetype : mccm.getMimeTypes()) {
			// log.fine(mimetype);
			System.out.println(mimetype);
		}

		String mailCapLine = "application/EDIFACT;; x-java-content-handler=nl.iamit.mail.handlers.ApplicationEdifact";
		mccm.addMailcap(mailCapLine);

		for (String mimetype : mccm.getMimeTypes()) {
			// log.fine(mimetype);
			System.out.println(mimetype);
		}

	}

	@Test
	public void showDataContentHandlers() {

		MailcapCommandMap mccm = new MailcapCommandMap();
		String mailCapLine = "application/EDIFACT;; x-java-content-handler=nl.iamit.mail.handlers.ApplicationEdifact";
		mccm.addMailcap(mailCapLine);
		for (String mimeType : mccm.getMimeTypes()) {
			System.out.println(mimeType);
			DataContentHandler dch = mccm.createDataContentHandler(mimeType);
			try {
				System.out.println(dch.getClass().getName());
			} catch (Exception e) {
				System.out.println(e.getMessage());
				// e.printStackTrace();
			}
		}
	}

	@Test
	public void testCharType() {
		IAmMailEdifact.addMailCapLineForEdifact();
		// private String getCharset(String type) {
		String type = "application/EDIFACT";
		String javaCharset = null;
		try {
			ContentType ct = new ContentType(type);
			String charset = ct.getParameter("charset");
			if (charset == null) {
				log.fine("No charsetfound!");
				// If the charset parameter is absent, use US-ASCII.
				charset = "us-ascii";
			}
			javaCharset = MimeUtility.javaCharset(charset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.fine("Charset  = "+javaCharset);
		assertTrue(javaCharset != null);
	}

	public static void doPause(int iTimeInSeconds) {
		long t0, t1;
		System.out.println("timer start");
		t0 = System.currentTimeMillis();
		t1 = System.currentTimeMillis() + (iTimeInSeconds * 1000);

		System.out.println("T0: " + t0);
		System.out.println("T1: " + t1);

		do {
			t0 = System.currentTimeMillis();

		} while (t0 < t1);

		System.out.println("timer end");

	}

}
