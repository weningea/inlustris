package doorcard;

import java.awt.EventQueue;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.CardLayout;
import java.awt.Toolkit;

/*
 * The main class and application
 * 
 * Initializes menus and handles panel swapping
 * mm = MainMenu panel
 * create = Doorcard Creator panel
 * populator = Database Populator panel
 * deleter = Database clear panel
 * activeFrame = The currently active frame (used for swapping)
 * 
 * UIs generated using Eclipse IDE WindowBuilder
 */

public class Starlight {

	private JFrame frame;
	private JPanel mm, create, populator, activeFrame;
	private Deleter deleter;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Starlight window = new Starlight();
					window.frame.setTitle("Starlight");
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Starlight() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Starlight.class.getResource("/assets/star.png")));
		frame.setBounds(100, 100, 540, 520);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new CardLayout(0, 0));
		
		mm = new MainMenu(this);
		frame.getContentPane().add(mm, "name_1128642529155246");
		activeFrame = mm;
		
		create = new Creator(this);
		frame.getContentPane().add(create, "name_1129253038282910");
		
		populator = new Populator(this);
		frame.getContentPane().add(populator, "name_1135754477243692");
		
		deleter = new Deleter(this);
		frame.getContentPane().add(deleter, "name_162410256368964");
	
	}
	
	public void swapPanel(int p) //Swaps to the desired panel
	{
		/*
		 * 0: Main Menu
		 * 1: Creator
		 * 2: Populator
		 * 3: Deleter
		 */
		activeFrame.setVisible(false);
		switch (p)
		{
		case 0:
			mm.setVisible(true);
			activeFrame = mm;
			break;
		case 1:
			create.setVisible(true);
			activeFrame = create;
			break;
		case 2:
			populator.setVisible(true);
			activeFrame = populator;
			break;
		case 3:
			deleter.setVisible(true);
			activeFrame = deleter;
		}
	}
	public void log(Exception e) //Logs debgug file
	{
		/*
		 * To use this method, simply pass the exception
		 */
		try {
			Writer outputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("debug "+java.time.LocalDate.now()+"-"
					+java.time.LocalTime.now().getHour()+"-"+java.time.LocalTime.now().getMinute()+".txt")));
			if (System.getProperty("os.name").contains("Windows"))
			{
				outputStream.write("Begin debug log (stack trace):\r\nCurrent time: "+java.time.LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)
						+" "+java.time.ZoneId.systemDefault().getDisplayName(TextStyle.FULL, Locale.US)+"\r\n\r\n");
				outputStream.write(e.getMessage()+"\r\n");
				for (StackTraceElement s : e.getStackTrace())
					outputStream.write(s.toString()+"\r\n");
				outputStream.write("\r\nEnd debug log\r\n");
			}
			else
			{
				outputStream.write("Begin debug log (stack trace):\nCurrent time: "+java.time.LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)
						+" "+java.time.ZoneId.systemDefault().getDisplayName(TextStyle.FULL, Locale.US)+"\n\n");
				outputStream.write(e.getMessage()+"\n");
				for (StackTraceElement s : e.getStackTrace())
					outputStream.write(s.toString()+"\n");
				outputStream.write("\nEnd debug log\n");
			}
			outputStream.close();
		} catch (FileNotFoundException e1) {
			String msg = "An error has occurred!\nCould not find debug file.\nIf this message appears again, please consult the maintenance manual.";
			JOptionPane.showMessageDialog(new JFrame(), msg, "Error: File not found",
			        JOptionPane.ERROR_MESSAGE);
		} catch (IOException e1) {
			String msg = "An error has occurred!\nFailed to write to debug file.\nIf this message appears again, please consult the maintenance manual.";
			JOptionPane.showMessageDialog(new JFrame(), msg, "Error: Failed to write to file",
			        JOptionPane.ERROR_MESSAGE);
		}
	}
	public void log(ResultSet rs) //Logs the previous semester's course information as a txt file
	{
		if (rs != null)
		{
			try {
				Writer outputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Archive "+java.time.LocalDate.now()+"-"
						+java.time.LocalTime.now().getHour()+"-"+java.time.LocalTime.now().getMinute()+".txt")));
				if (System.getProperty("os.name").contains("Windows"))
				{
					outputStream.write("Begin archive\r\n\r\n");
					while(rs.next())
					{
						String tname = rs.getString("TeacherName");
						  String cname = rs.getString("CourseName");
				          cname=cname.replaceAll("\\s+"," ");
				          String sec = rs.getString("Section");
				          String dow = rs.getString("DayOfWeek");
				          String start = rs.getString("Start");
				          String end = rs.getString("End");
				          String building = rs.getString("Building");
				          String room = rs.getString("Room");
				          String start2 = start.substring(0, start.length()-3);
				          String end2 = end.replaceAll("\\s+","");
				          String time = start2+"-"+end2;
				          String place = building+" "+room;
				          outputStream.write(tname+"\t"+cname+" section "+sec+"\t"+dow+" "+time+"\t"+place+"\r\n");
					}
					outputStream.write("\r\nEnd archive");
				}
				else
				{
					outputStream.write("Begin archive\n\n");
					while (rs.next())
					{
						String tname = rs.getString("TeacherName");
						  String cname = rs.getString("CourseName");
				          cname=cname.replaceAll("\\s+"," ");
				          String sec = rs.getString("Section");
				          String dow = rs.getString("DayOfWeek");
				          String start = rs.getString("Start");
				          String end = rs.getString("End");
				          String building = rs.getString("Building");
				          String room = rs.getString("Room");
				          String start2 = start.substring(0, start.length()-3);
				          String end2 = end.replaceAll("\\s+","");
				          String time = start2+"-"+end2;
				          String place = building+" "+room;
				          outputStream.write(tname+"\t"+cname+" section "+sec+"\t"+dow+" "+time+"\t"+place+"\n");
					}
					outputStream.write("\nEnd archive");
				}
				outputStream.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Null set: empty");
		}
		
	}
	public Deleter getDeleter(){return deleter;}

}
