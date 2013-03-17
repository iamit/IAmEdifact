package nl.iamit.edifact.handlers;

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;

import nl.iamit.edifact.EdifactMimePart;

public class ApplicationEdifact implements DataContentHandler {
	private static Logger log = Logger.getLogger(ApplicationEdifact.class
			.getName());

	ActivationDataFlavor edifactDataFlavor = new ActivationDataFlavor(
			nl.iamit.edifact.EdifactMimePart.class, "application/EDIFACT",
			"EDIFACT Message");
	
	
	

	public Object getContent(DataSource ds) throws IOException {
		log.fine("getContent");
		try {
			return new EdifactMimePart(ds);
		} catch (MessagingException e) {
			IOException ioex = new IOException(
					"Exception while constructing MimeMultipart");
			ioex.initCause(e);
			throw ioex;
		}
	}

	public Object getTransferData(DataFlavor df, DataSource ds)
			throws IOException {
		log.fine("getTransferData");
		// make sure the datasource is of the type edifactDataFlavor
		if (edifactDataFlavor.equals(df)) {
			return getContent(ds);
		} else {
			return null;
		}
	}

	public DataFlavor[] getTransferDataFlavors() {
		log.fine("getTransferDataFlavors");
		return new DataFlavor[] { edifactDataFlavor };
	}

	public void writeTo(Object obj, String mimeType, OutputStream os)
			throws IOException {
		log.fine("writeTo");
		if (obj instanceof EdifactMimePart) {
			try {
				((EdifactMimePart) obj).writeTo(os);
			} catch (MessagingException e) {
				throw new IOException(e.toString());
			}
		}
	}

}

