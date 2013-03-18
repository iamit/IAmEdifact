package nl.iamit.edifact.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AccessControlException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import nl.iamit.edifact.message.EdifactMessage;
import nl.iamit.edifact.message.EdifactMessageGeneric;
import nl.iamit.edifact.message.elements.EdifactLineElement;
import nl.iamit.edifact.message.segments.EdifactSegmentEndOfMessage;
import nl.iamit.edifact.message.segments.EdifactSegmentInterchangeHeader;
import nl.iamit.edifact.message.segments.EdifactSegmentMessageEndTrailer;
import nl.iamit.edifact.message.segments.EdifactSegmentMessageHeader;
import nl.iamit.edifact.parsers.EdifactParser;
import nl.iamit.io.IAmFileWriter;
import nl.iamit.io.exceptions.QueryBeforeSaveException;
import nl.iamit.io.exceptions.QueryNoDataException;
import nl.iamit.io.exceptions.UnknownModeException;
import nl.iamit.mail.IAmMail;
import nl.iamit.mail.IAmMailFetchServerSettings;
import nl.iamit.mail.IAmMailMessageSettings;
import nl.iamit.mail.IAmMailSendServerSettings;
import nl.iamit.mail.IAmMailStore;
import nl.iamit.util.FileFunctions;
import nl.iamit.util.StringStandardRegex;
import nl.iamit.util.exceptions.DuplicateException;
import nl.iamit.util.exceptions.InvalidArgumentException;
import nl.iamit.util.exceptions.InvalidProcedureCallException;
import nl.iamit.util.exceptions.ParseException;

public class EdifactDemo extends JFrame {

	public static boolean DEBUG=true;
	
	public static String DEFAULT_SMTP_SERVER = "smtp.gmail.com";
	public static String DEFAULT_IMAP_SERVER = "imap.gmail.com";
	public static int DEFAULT_IMAP_PORT = 993;
	public static int DEFAULT_SMTP_PORT = 465; // 587
	public static String DEFAULT_EMAIL_ADDRESS = "somebody@gmail.com";
	public static String DEFAULT_USERNAME = "somebody@gmail.com";
	public static String DEFAULT_PASSWORD = "unknown";

	private static EdifactDemo gui;
	private static Logger log = Logger.getLogger(EdifactDemo.class.getName());
	private int version_main = 1;
	private int version_sub = 0;
	private static String rootPath;
	private static String logPath;
	private static String messagesPath;
	private static Color bgColorPanel;
	private static Color bgColor;

	private DemoPanel demoPanel;
	
	

	public EdifactDemo() {
		bgColor = createColor(206, 255, 206);
		bgColorPanel = createColor(255, 255, 175);
		this.setBackground(bgColor);
		rootPath = System.getProperty("user.dir")
				+ System.getProperty("file.separator");
		logPath = rootPath+"logs"+ System.getProperty("file.separator");
		messagesPath=rootPath+"messages"+ System.getProperty("file.separator");
		initLogger();
		this.setSize(800, 650);
		this.setTitle("Edifact demo");

		demoPanel = new DemoPanel();
		this.setContentPane(demoPanel);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				gui = new EdifactDemo();
				gui.setVisible(true);
			}

		});
	}

	private Color createColor(int r, int g, int b) {
		float tmpFloat[] = new float[3];
		Color.RGBtoHSB(r, g, b, tmpFloat);
		return Color.getHSBColor(tmpFloat[0], tmpFloat[1], tmpFloat[2]);

	}

	private void initLogger() {

		try {
			// get the top Logger:
			Logger topLogger = java.util.logging.Logger.getLogger("");
			if (topLogger == null) {
				log.warning("Logger empty top not found, setting to nl");
				topLogger = java.util.logging.Logger.getLogger("nl");
			}

			// Handler for console (reuse it if it already exists)
			Handler consoleHandler = null;
			// see if there is already a console handler
			if (topLogger != null) {
				for (Handler handler : topLogger.getHandlers()) {
					if (handler instanceof ConsoleHandler) {
						// found the console handler
						consoleHandler = handler;
						break;
					}
				}
			}

			if (consoleHandler == null) {
				// there was no console handler found, create a new one
				consoleHandler = new ConsoleHandler();
				if (topLogger != null) {
					topLogger.addHandler(consoleHandler);
				}
			}
			// set the console handler to fine:
			consoleHandler.setLevel(java.util.logging.Level.FINE);

			// get the toplogger for nl.iamit
			Logger topLoggerIAmIT = Logger.getLogger("nl.iamit");
			topLoggerIAmIT.setLevel(Level.FINEST);

			// the gui logfile handler.
			FileHandler handler = new FileHandler(
					logPath + "IAmEdifactDemo.log", 1024 * 1024, 8, true);
			if(DEBUG){
				handler.setLevel(Level.FINEST);
			}else{
				handler.setLevel(Level.INFO);
			}
			handler.setFormatter(new SingleLineFormatter());
			if (topLoggerIAmIT != null) {
				topLoggerIAmIT.addHandler(handler);
			}

			// the thirdparty (org, com) logfile handler.
			FileHandler handlerThirdparty = new FileHandler(logPath
					+ "thirdparty.log", 1024 * 1024, 8, true);
			if(DEBUG){
				handlerThirdparty.setLevel(Level.FINEST);
			}else{
			handlerThirdparty.setLevel(Level.INFO);
			}
			handlerThirdparty.setFormatter(new SingleLineFormatter());

			// the overall logfile handler.
			// FileHandler handlerAll = new FileHandler(rootPath
			// + "all.log", 1024 * 1024, 8, true);
			// handlerAll.setLevel(Level.INFO);
			// handlerAll.setFormatter(new SingleLineFormatter());

			// the top logger for org classes
			Logger topLoggerOrg = Logger.getLogger("org");
			if (topLoggerOrg != null) {
				topLoggerOrg.setLevel(Level.FINEST);
				topLoggerOrg.addHandler(handlerThirdparty);
			}

			// the top logger for com classes
			Logger topLoggerCom = Logger.getLogger("com");
			if (topLoggerCom != null) {
				topLoggerCom.setLevel(Level.FINEST);
				topLoggerCom.addHandler(handlerThirdparty);
			}

		} catch (SecurityException e1) {
			log.severe(e1.getMessage());
			e1.printStackTrace();
		} catch (IOException e1) {
			log.severe(e1.getMessage());
			e1.printStackTrace();
		}

		log.info("**** Starting IAmEdifact Demo version " + version_main + "."
				+ version_sub + " ****");
		log.fine("paths and logging initialized...");
		try {
			// log information about system, java and user
			log.info("OS: " + System.getProperty("os.name") + " ("
					+ System.getProperty("os.version") + " - "
					+ System.getProperty("os.arch") + ")");
			log.info("Java: " + System.getProperty("java.runtime.version"));
			log.info("Java home: " + System.getProperty("java.home"));
			log.info("User: " + System.getProperty("user.name")
					+ " ( country: " + System.getProperty("user.country")
					+ " language: " + System.getProperty("user.language") + ")");

		} catch (Exception e) {
			// log.severe(e.getMessage());
			e.printStackTrace();
		}

	}

	public void receiveEdifactMessages() throws InvalidProcedureCallException, MessagingException {
		log.fine("Receiving messages");
		
		IAmMailFetchServerSettings inServer= demoPanel.settingsPanel.getInServer();
		IAmMailStore store = new IAmMailStore(inServer);
		store.setPropertyDebug(true);
		store.connect();
		//note if you want to delete a message, use:
		//store.openFolder(IAmMailStore.FOLDER_OPEN_OPTION_READ_WRITE);
		store.openFolder();
		Message[] messages = store.getMessages();
		store.getMessageHeaders(messages);
		String messagePath = messagesPath+"in"+ System.getProperty("file.separator");
		for (Message message : messages) {
			
			String subject = message.getSubject();
			if (subject == null) {
				continue;
			}
			if (subject.length() <= 0) {
				continue;
			}
			int id = message.getMessageNumber();
			if (!subject.toLowerCase().startsWith("iamedifact")
					&& !subject.toLowerCase().startsWith("fwd: iamedifact")) {
				log.fine("message "+id+" not the correct message: "+subject);
				continue;
			}
			log.fine("message found: "+subject);
			Date messageDate = message.getSentDate();
			
			String from = message.getFrom()[0].toString();
			//here you can do all kind of checks to make sure you have the right message
			//if not the right message; continue
			

			FileFunctions.pathExist(messagePath, true);
			ArrayList<String> savedFilenames = null;
			try {
				savedFilenames = IAmMail.saveAttachmentsInMessageToPath(
						message, messagePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			} catch (InvalidArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			if (savedFilenames == null) {
				continue;
			}
			if (savedFilenames.isEmpty()) {
				continue;
			}
			
			for (String savedFileName : savedFilenames) {
				String pathFile = messagePath
						+ System.getProperty("file.separator") + savedFileName;
				//EdiFecMessageEkt messageEkt = null; //this is my extended message
				EdifactMessageGeneric messageGeneric =null;
				try {
					messageGeneric =(EdifactMessageGeneric)EdifactParser.parseFileToBericht(pathFile);
				
					//messageEkt = new EdiFecMessageEkt(); 
					//messageEkt
						//	.createFromGenericMessage((EdifactMessageGeneric) EdiFecParser
							//		.parseFileToBericht(pathFile));
					
				} catch (AccessControlException e) {
					log.severe(e.getMessage());
					e.printStackTrace();
					continue;
				} catch (ParseException e) {
					log.severe(e.getMessage());
					e.printStackTrace();
					continue;
				} catch (IOException e) {
					log.severe(e.getMessage());
					e.printStackTrace();
					continue;
				} catch (InvalidArgumentException e) {
					log.severe(e.getMessage());
					e.printStackTrace();
					continue;
				} catch (AssertionError e) {
					log.severe(e.getMessage());
					e.printStackTrace();
					continue;
				} catch (UnknownModeException e) {
					log.severe(e.getMessage());
					e.printStackTrace();
					continue;
				} catch (ClassNotFoundException e) {
					log.severe(e.getMessage());
					e.printStackTrace();
					continue;
				} catch (SQLException e) {
					log.severe(e.getMessage());
					e.printStackTrace();
					continue;
				} catch (QueryBeforeSaveException e) {
					log.severe(e.getMessage());
					e.printStackTrace();
					continue;
				} catch (QueryNoDataException e) {
					log.severe(e.getMessage());
					e.printStackTrace();
					continue;
				}
				if (messageGeneric == null) {
					continue;
				}
				
				//now you have the message you can do some more checks on the message itselve,
				// or rename it to the message id or something
				
			}
			
		}
		store.close();
		//by the way, you can also backup messages to another imap folder, or just delete the messages using the store.
		//than use :
		//store.close(true);
		
	}
	
	protected void processWindowEvent(WindowEvent ev) {
		super.processWindowEvent(ev);
		if (ev.getID() == WindowEvent.WINDOW_CLOSING) {
			// Overridden so we can exit when window is closed
			exitDemo();
		} else if (ev.getID() == WindowEvent.WINDOW_ICONIFIED) {
			// minimized
		} else if (ev.getID() == WindowEvent.WINDOW_ACTIVATED) {
			// and maximized again
		}

	}
	
	public void exitDemo() {

		try {
			// unregisterListeners();
		} catch (Exception e) {
			log.severe(e.getMessage());
			e.printStackTrace();
		}
		log.info("Closing IAmEdifact demo");
		log.info("--------------------------------------------------");
		dispose();
		System.exit(0);
	}
	
	

	public void sendEdifactMessages() throws AccessControlException,
			InvalidProcedureCallException, InvalidArgumentException,
			ParseException, AssertionError, UnknownModeException,
			ClassNotFoundException, SQLException, QueryBeforeSaveException,
			QueryNoDataException, DuplicateException, FileNotFoundException {
		log.fine("Sending message");
		
		IAmMailSendServerSettings sendServerSettings = demoPanel.settingsPanel
				.getOutServer();
		//create an EdifactMessageGeneric
		EdifactMessageGeneric edifactMessage = new EdifactMessageGeneric();
		
		// Edifact interchange header
		String interchangeTransactionId="TransactionId";
		EdifactSegmentInterchangeHeader interchangeHeader=new EdifactSegmentInterchangeHeader();
		interchangeHeader.createFromData("SENDER", "RECEIVER",interchangeTransactionId , EdifactMessage.EDIFACT_MESSAGE_TYPE_CLOCKT);
		// Edifactmessage header
		EdifactSegmentMessageHeader messageHeader=new EdifactSegmentMessageHeader();
		String versionNr="1";
		String releaseNr="0";
		String messageId= "messageId";
		messageHeader.createFromData(EdifactMessage.EDIFACT_MESSAGE_TYPE_CLOCKT,messageId, versionNr, releaseNr, "organisationId", "messageIdentificationId");
		
		ArrayList<EdifactLineElement> edfifactLines=new ArrayList<EdifactLineElement>();
		edfifactLines.add(interchangeHeader.getMainLine());
		edfifactLines.add(messageHeader.getMainLine());
		//you should add extra lines here (the content) I normally make an extended class of EdifactMessage in stead of using the EdifactMessageGeneric
		//so you create your own interface to add lines, if you let me know how a message looks like, I can help with this
	
		//end of trailer:
		EdifactSegmentMessageEndTrailer unt=new EdifactSegmentMessageEndTrailer();
		int nrSegments=edfifactLines.size()+2;
		unt.createFromData(nrSegments, messageId);
		edfifactLines.add(unt.getMainLine());
		//end of message:
		EdifactSegmentEndOfMessage unz =new EdifactSegmentEndOfMessage();
		int nrOfMessages=1;
		unz.createFromData(nrOfMessages, interchangeTransactionId);
		edfifactLines.add(unz.getMainLine());
		
		//create the message 
		edifactMessage.createFromEdifactLines(interchangeHeader, messageHeader, edfifactLines);
		//get the lines from this message:
		ArrayList<String> lines = edifactMessage.getLines();
		if (lines.isEmpty()) {
			log.fine("No lines");
			return;
		}
		//save the edifact message to a file:
		String fileName = messagesPath+"out"+ System.getProperty("file.separator")+"testEdifact.edi";
		try {
			log.fine("Saving file to "+fileName);
			saveLinesToFile(lines,fileName);
		} catch (IOException e) {
			log.severe(e.getMessage());
			e.printStackTrace();
			showError("Error","Error saving edifact message, check logs");
			return;
		}
		
		// for now: use the replyto address as recipient 
		String recipient = sendServerSettings.getReplyToAddress();
		ArrayList<String> recipients = new ArrayList<String>();
		recipients.add(recipient);
		String subject = "IAmEdifact demo";
		IAmMailMessageSettings mailSettings = new IAmMailMessageSettings(
				recipients, sendServerSettings.getReplyToAddress(), subject);
	
		
		mailSettings.addFileToAttach(fileName);
		IAmMail.sendMail(sendServerSettings, mailSettings, DEBUG);

	}
	
	public static boolean saveLinesToFile(ArrayList<String> lines,
			String fileName) throws InvalidArgumentException, IOException {

		StringBuffer content = new StringBuffer();
		int nrLines = lines.size();
		int cntr = 0;
		for (String line : lines) {
			content.append(line);
			content.append("'");
			cntr++;
			if (cntr < nrLines) {
				content.append("\r\n");
			}
		}
		IAmFileWriter.writeToFile(fileName, content.toString());
		return true;
	}
	
	private void testMailIn(){
		IAmMailFetchServerSettings inServer= demoPanel.settingsPanel.getInServer();
		if(IAmMail.testFetchServerConnection(inServer)){
			showInfo("Test incomming mail settings","Succes, Settings OK!");
		}else{
			showError("Test incomming mail settings","Failed: Settings not OK! (see logs)");
		}
	}

	private void testMailOut(){
		IAmMailSendServerSettings sendServerSettings = demoPanel.settingsPanel
				.getOutServer();
		if(IAmMail.testSmtpServerConnection(sendServerSettings, true)){
			showInfo("Test outgoing mail settings","Succes, Settings OK!");
		}else{
			showError("Test outgoing mail settings","Failed: Settings not OK! (see logs)");
		}
	}

	
	private class DemoButtonPanel extends JPanel implements ActionListener {
		private JButton butSend;
		private JButton butReceive;
		private JButton butCancel;
		private JButton butTestMailIn;
		private JButton butTestMailOut;
		
		
		public DemoButtonPanel() {
			this.setBackground(bgColor);

			butSend = new JButton("Send");
			butReceive = new JButton("Receive");
			butCancel=new JButton("Cancel");
			butTestMailIn=new JButton("Test in");
			butTestMailOut=new JButton("Test out");

			butSend.addActionListener(this);
			butReceive.addActionListener(this);
			butCancel.addActionListener(this);
			butTestMailIn.addActionListener(this);
			butTestMailOut.addActionListener(this);
			
			this.add(butTestMailOut);
			this.add(butSend);
			this.add(butTestMailIn);
			this.add(butReceive);
			this.add(butCancel);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			Object eventSource = event.getSource();
			try {
				if (eventSource.equals(butSend)) {

					sendEdifactMessages();
				} else if (eventSource.equals(butReceive)) {
					receiveEdifactMessages();
				} else if(eventSource.equals(butTestMailIn)){
					testMailIn();
				} else if(eventSource.equals(butTestMailOut)){
					testMailOut();
				}
			} catch (AccessControlException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				showError("Error", "Something went wrong, check log");
			} catch (InvalidProcedureCallException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				showError("Error", "Something went wrong, check log");
			} catch (InvalidArgumentException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				showError("Error", "Something went wrong, check log");
			} catch (ParseException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				showError("Error", "Something went wrong, check log");
			} catch (AssertionError e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				showError("Error", "Something went wrong, check log");
			} catch (UnknownModeException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				showError("Error", "Something went wrong, check log");
			} catch (ClassNotFoundException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				showError("Error", "Something went wrong, check log");
			} catch (SQLException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				showError("Error", "Something went wrong, check log");
			} catch (QueryBeforeSaveException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				showError("Error", "Something went wrong, check log");
			} catch (QueryNoDataException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				showError("Error", "Something went wrong, check log");
			} catch (DuplicateException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				showError("Error", "Something went wrong, check log");
			} catch (FileNotFoundException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				showError("Error", "Something went wrong, check log");
			} catch (MessagingException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
				showError("Error", "Something went wrong, check log");
			}

		}
	}

	private class DemoSettingsPanel extends JPanel {

		private JTextField tfInServer = new JTextField("");
		private JTextField tfInPort = new JTextField("");
		private JTextField tfInboxName = new JTextField("");
		private JTextField tfOutServer = new JTextField("");
		private JTextField tfOutPort = new JTextField("");
		private JTextField tfEmailAddress = new JTextField("");
		private JTextField tfUserName = new JTextField("");
		private JPasswordField pwdPassword = new JPasswordField("");
		private JCheckBox chUseTLS=new JCheckBox("Use TLS");
		private JCheckBox chUseSSL=new JCheckBox("Use SSL");
		private JCheckBox chUselogin=new JCheckBox("Use Login");
		private JComboBox cbFetchProtocol =new JComboBox();

		public DemoSettingsPanel() {

			this.setBackground(bgColor);

			GridBagLayout gridBag = new GridBagLayout();
			this.setLayout(gridBag);
			GridBagConstraints c = new GridBagConstraints();
			initGridBagConstraints(c);

			initDefaults();

			int row = 0;
			int col = 0;
			JLabel lblEmail = new JLabel("Email Address:");
			addComponents(this, lblEmail, gridBag, c, col, row, 1, 1);
			col++;
			addComponents(this, tfEmailAddress, gridBag, c, col, row, 1, 1);

			row++;
			col=0;
			JLabel lblUserName = new JLabel("UserName:");
			addComponents(this, lblUserName, gridBag, c, col, row, 1, 1);
			col++;
			addComponents(this, tfUserName, gridBag, c, col, row, 1, 1);
			
			row++;
			col=0;
			addComponents(this, chUseTLS, gridBag, c, col, row, 1, 1);
			row++;
			col=0;
			addComponents(this, chUseSSL, gridBag, c, col, row, 1, 1);
			row++;
			col=0;
			addComponents(this, chUselogin, gridBag, c, col, row, 1, 1);
			
			row++;
			col=0;
			JLabel lblPassword = new JLabel("Password:");
			addComponents(this, lblPassword, gridBag, c, col, row, 1, 1);
			col++;
			addComponents(this, pwdPassword, gridBag, c, col, row, 1, 1);
			
			row++;
			col=0;
			JLabel lblServerIn = new JLabel("Server in:");
			addComponents(this, lblServerIn, gridBag, c, col, row, 1, 1);
			col++;
			addComponents(this, tfInServer, gridBag, c, col, row, 1, 1);
			col++;
			JLabel lblPortIn = new JLabel("port");
			addComponents(this, lblPortIn, gridBag, c, col, row, 1, 1);
			col++;
			addComponents(this, tfInPort, gridBag, c, col, row, 1, 1);
			row++;
			col=0;
			JLabel lblInbox = new JLabel("Inbox name:");
			addComponents(this, lblInbox, gridBag, c, col, row, 1, 1);
			col++;
			addComponents(this,tfInboxName, gridBag, c, col, row, 1, 1);
			row++;
			col=0;
			JLabel lblProtocolIn = new JLabel("Protocol:");
			addComponents(this, lblProtocolIn, gridBag, c, col, row, 1, 1);
			col++;
			addComponents(this,cbFetchProtocol, gridBag, c, col, row, 1, 1);

			row++;
			col = 0;
			JLabel lblServerOut = new JLabel("Server out:");
			addComponents(this, lblServerOut, gridBag, c, col, row, 1, 1);
			col++;
			addComponents(this, tfOutServer, gridBag, c, col, row, 1, 1);
			col++;
			JLabel lblPortOut = new JLabel("port");
			addComponents(this, lblPortOut, gridBag, c, col, row, 1, 1);
			col++;
			addComponents(this, tfOutPort, gridBag, c, col, row, 1, 1);

		}

		private void initDefaults() {

			tfEmailAddress.setText(DEFAULT_EMAIL_ADDRESS);
			tfUserName.setText(DEFAULT_USERNAME);
			pwdPassword.setText(DEFAULT_PASSWORD);

			tfInServer.setText(DEFAULT_IMAP_SERVER);
			tfInPort.setText("" + DEFAULT_IMAP_PORT);
			tfOutServer.setText(DEFAULT_SMTP_SERVER);
			tfOutPort.setText("" + DEFAULT_SMTP_PORT);
			tfInboxName.setText("INBOX");
			
			cbFetchProtocol.addItem("POP3 ("+IAmMailFetchServerSettings.FETCH_PROTOCOL_POP3+")");
			cbFetchProtocol.addItem("POP3s ("+IAmMailFetchServerSettings.FETCH_PROTOCOL_POP3S+")");
			cbFetchProtocol.addItem("IMAP ("+IAmMailFetchServerSettings.FETCH_PROTOCOL_IMAP+")");
			cbFetchProtocol.addItem("IMAPS ("+IAmMailFetchServerSettings.FETCH_PROTOCOL_IMAPS+")");
			
			cbFetchProtocol.setSelectedItem("IMAPS ("+IAmMailFetchServerSettings.FETCH_PROTOCOL_IMAPS+")");
			
			this.chUselogin.setSelected(true);
			this.chUseSSL.setSelected(false);
			this.chUseSSL.setSelected(true);
		}

		public IAmMailFetchServerSettings getInServer() {
			String selectedFetchProtocol=cbFetchProtocol.getSelectedItem().toString();
			int fetchProtocol =IAmMailFetchServerSettings.FETCH_PROTOCOL_IMAPS;
			try{
				fetchProtocol=Integer.parseInt(StringStandardRegex.getNameIdSplitted(selectedFetchProtocol).getSecond());
			}catch(Exception e){
				log.severe(e.getMessage());
				e.printStackTrace();
			}
			String inboxName = tfInboxName.getText();
			// from settings panel:
			String fetchServerName = this.tfInServer.getText();
			int fetchPort = Integer.parseInt(this.tfInPort.getText());
			String password = this.pwdPassword.getText();
			if (password.equals(DEFAULT_PASSWORD)) {
				log.severe("Fill in password");
				showError("Default password",
						"Still the default password, fill in a usefull password!");
				return null;

			}
			String userName = this.tfUserName.getText();
			if (userName.equals(DEFAULT_USERNAME)) {
				log.severe("Fill in username");
				showError("Default user name",
						"Still the default user name, fill in a usefull usrename!");
				return null;

			}
			boolean useSsl = this.chUseSSL.isSelected();
			IAmMailFetchServerSettings servSet = new IAmMailFetchServerSettings(
					fetchProtocol, fetchServerName, inboxName, fetchPort,
					userName, password,useSsl);

			return servSet;
		}

		public IAmMailSendServerSettings getOutServer() {
			// from settings panel:
			boolean useTLS = this.chUseTLS.isSelected();
			boolean useSSL = this.chUseSSL.isSelected();
			boolean useLoginName = this.chUselogin.isSelected();
			
			String sendServerName = this.tfOutServer.getText();
			int sendPort = Integer.parseInt(this.tfOutPort.getText());
			String password = this.pwdPassword.getText();

			if (password.equals(DEFAULT_PASSWORD)) {
				log.severe("Fill in password");
				showError("Default password",
						"Still the default password, fill in a usefull password!");
				return null;

			}
			String userName = this.tfUserName.getText();
			if (userName.equals(DEFAULT_USERNAME)) {
				log.severe("Fill in username");
				showError("Default user name",
						"Still the default user name, fill in a usefull username!");
				return null;

			}
			String email = this.tfEmailAddress.getText();
			if (email.equals(DEFAULT_EMAIL_ADDRESS)) {
				log.severe("Fill in email address");
				showError("Default email address",
						"Still the default email address, fill in a usefull email address!");
				return null;

			}

			IAmMailSendServerSettings servSet = new IAmMailSendServerSettings(
					sendServerName, sendPort, userName, password, useTLS,
					useSSL, useLoginName, email);
			return servSet;
		}

	}

	private void addComponents(JPanel panel, JComponent jc,
			GridBagLayout gridBag, GridBagConstraints c, int x, int y,
			int xwidth, int ywidth) {
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = xwidth;
		c.gridheight = ywidth;
		gridBag.setConstraints(jc, c);
		panel.add(jc);
	}

	private void initGridBagConstraints(GridBagConstraints c) {
		c.insets = new Insets(2, 2, 2, 2);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.ipadx = 0;
		c.ipadx = 0;
	}

	/**
	 * Show an error message, baseframe as parent
	 * 
	 * @param title
	 * @param message
	 */
	public void showError(String title, String message) {
		JOptionPane.showMessageDialog(this, message, title,
				JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Show an error message, baseframe as parent
	 * 
	 * @param title
	 * @param message
	 */
	public void showInfo(String title, String message) {
		JOptionPane.showMessageDialog(this, message, title,
				JOptionPane.INFORMATION_MESSAGE);
	}

	private class DemoPanel extends JPanel {

		private DemoSettingsPanel settingsPanel;

		public DemoPanel() {
			setLayout(new BorderLayout());
			this.setBackground(bgColor);

			DemoButtonPanel buttonPanel = new DemoButtonPanel();
			settingsPanel = new DemoSettingsPanel();

			this.add(settingsPanel, BorderLayout.CENTER);
			this.add(buttonPanel, BorderLayout.SOUTH);

		}

	}
}

// This custom formatter formats parts of a log record to a single line
class SingleLineFormatter extends Formatter {
	static private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	// This method is called for every log records
	public String format(LogRecord rec) {
		Date timestamp = new Date(rec.getMillis());

		return (dateFormat.format(timestamp) + ' ' + rec.getLevel() + ' '
				+ rec.getSourceClassName() + '.' + rec.getSourceMethodName()
				+ " : " + formatMessage(rec) + '\n');
	}

	// This method is called just after the handler using this
	// formatter is created
	public String getHead(Handler h) {
		return "" + (new Date()) + "\n";
	}

	// This method is called just after the handler using this
	// formatter is closed
	public String getTail(Handler h) {
		return "\n";
	}

}
