package nl.iamit.edifact;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.activation.DataSource;
import javax.mail.internet.MimePartDataSource;

import nl.iamit.edifact.message.EdifactMessage;
import nl.iamit.edifact.parsers.EdifactParser;
import nl.iamit.io.exceptions.NotYetImplementedException;
import nl.iamit.io.exceptions.QueryBeforeSaveException;
import nl.iamit.io.exceptions.QueryNoDataException;
import nl.iamit.io.exceptions.UnknownModeException;
import nl.iamit.util.FileFunctions;
import nl.iamit.util.exceptions.InvalidArgumentException;
import nl.iamit.util.exceptions.InvalidProcedureCallException;
import nl.iamit.util.exceptions.ParseException;

public class EdifactDataSource implements DataSource{
	private static Logger log = Logger.getLogger(EdifactDataSource.class.getName());
	private String fileName;
	private String path;
	private File file;
	private EdifactMessage edifactMessage;

	public EdifactDataSource(String path,String fileName) throws AccessControlException, ParseException, IOException, InvalidArgumentException, AssertionError, UnknownModeException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException, InvalidProcedureCallException{
		log.fine("Constructor fileName and path");
		this.fileName=fileName;
		this.path=path;
		file=new File(fileName);
		parseFile();
	}
	
	public EdifactDataSource(DataSource dataSource){
		log.fine("Constructor DataSource");
		String className=dataSource.getClass().getName();
		log.fine(className);
		if(className.equals("javax.mail.internet.MimePartDataSource")){
			javax.mail.internet.MimePartDataSource mpds=(MimePartDataSource)dataSource;
			InputStream is=null;
			try {
				is=mpds.getInputStream();
			} catch (IOException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				return;
			}
			String fileNameTemp=null;
			try {
				//log.fine(dataSource.getName());
				//System.exit(0);
				fileNameTemp=IAmMailEdifact.getTempPath()+ System.getProperty("file.separator") +"temp.edi";
			} catch (InvalidProcedureCallException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				return;
			}
			FileFunctions.copy(is, fileNameTemp, 32);
			
			this.file=new File(fileNameTemp);
			String fileNameAndPath=file.getAbsolutePath();
			this.fileName=file.getName();
			this.path=fileNameAndPath.substring(0,fileNameAndPath.length()-fileName.length());
			try {
				parseFile();
			} catch (AccessControlException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				return;
			} catch (ParseException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				return;
			} catch (IOException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				return;
			} catch (InvalidArgumentException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				return;
			} catch (AssertionError e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				return;
			} catch (UnknownModeException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				return;
			} catch (ClassNotFoundException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				return;
			} catch (SQLException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				return;
			} catch (QueryBeforeSaveException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				return;
			} catch (QueryNoDataException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				return;
			} catch (InvalidProcedureCallException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				return;
			}
		}
	}
	
	public EdifactDataSource(DataSource dataSource,String destinationFileName) throws NotYetImplementedException, IOException, AccessControlException, ParseException, InvalidArgumentException, AssertionError, UnknownModeException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException, InvalidProcedureCallException{
		log.fine("Constructor DataSource and destinationFileName");
		String className=dataSource.getClass().getName();
		log.fine(className);
		if(className.equals("javax.mail.internet.MimePartDataSource")){
			javax.mail.internet.MimePartDataSource mpds=(MimePartDataSource)dataSource;
			InputStream is=mpds.getInputStream();
			
			FileFunctions.copy(is, destinationFileName, 32);
			
			this.file=new File(destinationFileName);
			String fileNameAndPath=file.getAbsolutePath();
			this.fileName=file.getName();
			this.path=fileNameAndPath.substring(0,fileNameAndPath.length()-fileName.length());
			parseFile();

		}else{
			throw new NotYetImplementedException("Cannot cast from "+className+" to EdifactDataSource");
		}
	}

	public EdifactDataSource(File file) throws AccessControlException, ParseException, IOException, InvalidArgumentException, AssertionError, UnknownModeException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException, InvalidProcedureCallException{
		log.fine("Constructor File");
		this.file=file;
		String fileNameAndPath=file.getAbsolutePath();
		this.fileName=file.getName();
		this.path=fileNameAndPath.substring(0,fileNameAndPath.length()-fileName.length());
		//FileFunctions.getPath(fileNameAndPath);
		parseFile();
	}
	
	private void parseFile() throws AccessControlException, ParseException, IOException, InvalidArgumentException, AssertionError, UnknownModeException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException, InvalidProcedureCallException{
		log.fine("Parsing file");
		edifactMessage=EdifactParser.parseFileToBericht(path+fileName);
	}

	public EdifactDataSource(EdifactMessage edifactMessage){
		log.fine("Constructor EdifactMessage");
		this.edifactMessage=edifactMessage;
	}

	
	public String getContentType() {
		log.fine("Getting contentType");
		return "application/EDIFACT; name="+fileName;
	}
	
	public File getFile(){
		log.fine("Getting file");
		return file;
	}
	
	public String getContent(){
		log.fine("Getting content (String)");
		return edifactMessage.getMessageContent();
	}

	public InputStream getInputStream() throws IOException {
		log.fine("Getting input stream");
		if(file!=null){
			return new FileInputStream(file);
		}else{
			return null;
		}
	}

	public String getName() {
		log.fine("Getting name");
		return fileName;
	}

	public OutputStream getOutputStream() throws IOException {
		log.fine("Getting outputStream");
		if(file!=null){ 
			return new FileOutputStream(file);
		}else{
			return null;
		}
	}

}
