package nl.iamit.edifact;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimePart;

import nl.iamit.io.exceptions.NotYetImplementedException;
import nl.iamit.io.exceptions.QueryBeforeSaveException;
import nl.iamit.io.exceptions.QueryNoDataException;
import nl.iamit.io.exceptions.UnknownModeException;
import nl.iamit.util.exceptions.InvalidArgumentException;
import nl.iamit.util.exceptions.InvalidProcedureCallException;
import nl.iamit.util.exceptions.ParseException;

import com.sun.mail.util.LineOutputStream;

public class EdifactMimePart implements MimePart {
	private static Logger log = Logger.getLogger(EdifactMimePart.class
			.getName());

	EdifactDataSource ds;

	public EdifactMimePart(EdifactDataSource ds){
		this.ds=ds;
	}
	
	public EdifactMimePart(DataSource ds) throws MessagingException {
		String tempDestinationFileName=null;
		try {
			tempDestinationFileName = IAmMailEdifact.getTempPath()+System.getProperty("file.separator")+"temp.edi";
		} catch (InvalidProcedureCallException e) {
			log.severe(e.getMessage());
			e.printStackTrace();
			throw new MessagingException(e.getMessage());
		}
		try {
			this.ds = new EdifactDataSource(ds,tempDestinationFileName);
		} catch (NotYetImplementedException e) {
			log.severe(e.getMessage());
			e.printStackTrace();
			throw new MessagingException(e.getMessage());
		} catch (IOException e) {
			log.severe(e.getMessage());
			e.printStackTrace();
			throw new MessagingException(e.getMessage());
		} catch (AccessControlException e) {
			log.severe(e.getMessage());
			e.printStackTrace();
			throw new MessagingException(e.getMessage());
		} catch (ParseException e) {
			log.severe(e.getMessage());
			e.printStackTrace();
			throw new MessagingException(e.getMessage());
		} catch (InvalidArgumentException e) {
			log.severe(e.getMessage());
			e.printStackTrace();
			throw new MessagingException(e.getMessage());
		} catch (AssertionError e) {
			log.severe(e.getMessage());
			e.printStackTrace();
			throw new MessagingException(e.getMessage());
		} catch (UnknownModeException e) {
			log.severe(e.getMessage());
			e.printStackTrace();
			throw new MessagingException(e.getMessage());
		} catch (ClassNotFoundException e) {
			log.severe(e.getMessage());
			e.printStackTrace();
			throw new MessagingException(e.getMessage());
		} catch (SQLException e) {
			log.severe(e.getMessage());
			e.printStackTrace();
			throw new MessagingException(e.getMessage());
		} catch (QueryBeforeSaveException e) {
			log.severe(e.getMessage());
			e.printStackTrace();
			throw new MessagingException(e.getMessage());
		} catch (QueryNoDataException e) {
			log.severe(e.getMessage());
			e.printStackTrace();
			throw new MessagingException(e.getMessage());
		} catch (InvalidProcedureCallException e) {
			log.severe(e.getMessage());
			e.printStackTrace();
			throw new MessagingException(e.getMessage());
		}
		log.fine("Constructor: class datasource=" + ds.getClass().getName());

	}

	public void addHeaderLine(String arg0) throws MessagingException {
		log.fine("addHeaderLine");
		 // TODO Auto-generated method stub
	}

	public Enumeration getAllHeaderLines() throws MessagingException {
		
		log.fine("getAllHeaderLines");
		// TODO Auto-generated method stub
		return null;
	}

	public String getContentID() throws MessagingException {
		log.fine("getContentID"); // TODO Auto-generated method stub
		return null;
	}

	public String[] getContentLanguage() throws MessagingException {
		log.fine("getContentLanguage"); // TODO Auto-generated method stub
	
		return null;
	}

	public String getContentMD5() throws MessagingException {
		log.fine("getContentMD5"); // TODO Auto-generated method stub
	
		return null;
	}

	public String getEncoding() throws MessagingException {
		log.fine("getEncoding"); // TODO Auto-generated method stub
	
		return null;
	}

	public String getHeader(String arg0, String arg1) throws MessagingException {
		log.fine("getHeader"); // TODO Auto-generated method stub
		
		return null;
	}

	public Enumeration getMatchingHeaderLines(String[] arg0)
			throws MessagingException {
		log.fine("getMatchingHeaderLines"); // TODO Auto-generated method stub
		
		return null;
	}

	public Enumeration getNonMatchingHeaderLines(String[] arg0)
			throws MessagingException {
		log.fine("getNonMatchingHeaderLines"); // TODO Auto-generated method stub
		
		return null;
	}

	public void setContentLanguage(String[] arg0) throws MessagingException {
		log.fine("setContentLanguage");
// TODO Auto-generated method stub

	}

	public void setContentMD5(String arg0) throws MessagingException {
		log.fine("setContentMD5"); // TODO Auto-generated method stub

	}

	public void setText(String arg0) throws MessagingException {
		log.fine("setText"); // TODO Auto-generated method stub

	}

	public void setText(String arg0, String arg1) throws MessagingException {
		log.fine("setText"); // TODO Auto-generated method stub

	}

	public void setText(String arg0, String arg1, String arg2)
			throws MessagingException {
		log.fine("setText"); // TODO Auto-generated method stub

	}

	public void addHeader(String arg0, String arg1) throws MessagingException {
		log.fine("addHeader"); // TODO Auto-generated method stub

	}

	public Enumeration getAllHeaders() throws MessagingException {
		log.fine("getAllHeaders"); // TODO Auto-generated method stub
		return null;
	}

	public Object getContent() throws IOException, MessagingException {
		log.fine("getContent"); // TODO Auto-generated method stub
		return null;
	}

	public String getContentType() throws MessagingException {
		log.fine("getContentType"); // TODO Auto-generated method stub
		return null;
	}

	public DataHandler getDataHandler() throws MessagingException {
		log.fine("getDataHandler"); // TODO Auto-generated method stub
		return null;
	}

	public String getDescription() throws MessagingException {
		log.fine("getDescription"); // TODO Auto-generated method stub
		return null;
	}

	public String getDisposition() throws MessagingException {
		log.fine("getting disposition");
		return MimePart.INLINE;

	}

	public String getFileName() throws MessagingException {
		log.fine("Getting fileName");
		if (ds == null) {
			return null;
		}
		return ds.getName();
	}

	public String[] getHeader(String arg0) throws MessagingException {
		log.fine("getHeader"); // TODO Auto-generated method stub
		return null;
	}

	public InputStream getInputStream() throws IOException, MessagingException {
		log.fine("getInputStream"); // TODO Auto-generated method stub
		return null;
	}

	public int getLineCount() throws MessagingException {
		log.fine("tmp"); // TODO Auto-generated method stub
		return 0;
	}

	public Enumeration getMatchingHeaders(String[] arg0)
			throws MessagingException {
		log.fine("tmp"); // TODO Auto-generated method stub
		return null;
	}

	public Enumeration getNonMatchingHeaders(String[] arg0)
			throws MessagingException {
		log.fine("tmp"); // TODO Auto-generated method stub
		return null;
	}

	public int getSize() throws MessagingException {
		log.fine("tmp"); // TODO Auto-generated method stub
		return 0;
	}

	public boolean isMimeType(String arg0) throws MessagingException {
		log.fine("tmp"); // TODO Auto-generated method stub
		return false;
	}

	public void removeHeader(String arg0) throws MessagingException {
		log.fine("tmp"); // TODO Auto-generated method stub

	}

	public void setContent(Multipart arg0) throws MessagingException {
		log.fine("tmp"); // TODO Auto-generated method stub

	}

	public void setContent(Object arg0, String arg1) throws MessagingException {
		log.fine("tmp"); // TODO Auto-generated method stub

	}

	public void setDataHandler(DataHandler arg0) throws MessagingException {
		log.fine("tmp"); // TODO Auto-generated method stub

	}

	public void setDescription(String arg0) throws MessagingException {
		log.fine("tmp"); // TODO Auto-generated method stub

	}

	public void setDisposition(String arg0) throws MessagingException {
		log.fine("tmp"); // TODO Auto-generated method stub
	}

	public void setFileName(String arg0) throws MessagingException {
		log.fine("tmp"); // TODO Auto-generated method stub

	}

	public void setHeader(String arg0, String arg1) throws MessagingException {
		log.fine("tmp"); // TODO Auto-generated method stub

	}

	public void writeTo(OutputStream os) throws IOException, MessagingException {
		// see if we already have a LOS
		LineOutputStream los = null;
		if (os instanceof LineOutputStream) {
			los = (LineOutputStream) os;
		} else {
			los = new LineOutputStream(os);
		}
		los.writeln(ds.getContent());
	}

}
