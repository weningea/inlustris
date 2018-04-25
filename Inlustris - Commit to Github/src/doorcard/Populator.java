package doorcard;

import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.text.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.*;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.sql.Connection;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import java.util.Date;

/*
 * Populates the database with the given Excel file containing course data
 */

public class Populator extends JPanel {

	private Starlight star;
	private JTextField textField;
	private String path = "";
	private boolean success = false;
	
	private final JFileChooser fc = new JFileChooser();
	/**
	 * Create the panel.
	 */
	public Populator(Starlight s) {
		star = s;
		fc.setFileFilter(new FileNameExtensionFilter("Microsoft Excel File", "xlsx"));
		
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JButton btnNewButton = new JButton("Back");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				star.swapPanel(0);
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btnNewButton, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, btnNewButton, 0, SpringLayout.WEST, this);
		add(btnNewButton);
		
		textField = new JTextField();
		springLayout.putConstraint(SpringLayout.WEST, textField, 77, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, textField, -80, SpringLayout.SOUTH, this);
		textField.setEditable(false);
		add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton_1 = new JButton("Browse");
		springLayout.putConstraint(SpringLayout.EAST, textField, -6, SpringLayout.WEST, btnNewButton_1);
		springLayout.putConstraint(SpringLayout.SOUTH, btnNewButton_1, -78, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, btnNewButton_1, -68, SpringLayout.EAST, this);
		btnNewButton_1.addActionListener(new ActionListener() { //Browse for file
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(Populator.this);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					path = fc.getSelectedFile().getPath();
					if (!path.contains(".xlsx"))
						path = path+".xlsx";
					textField.setText(path);
				}
			}
		});
		add(btnNewButton_1);
		
		JButton btnLoad = new JButton("Load");
		springLayout.putConstraint(SpringLayout.NORTH, btnLoad, 8, SpringLayout.SOUTH, textField);
		springLayout.putConstraint(SpringLayout.WEST, btnLoad, 214, SpringLayout.WEST, this);
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					generate();
				}
				catch(SQLException sqle)
				{
					String msg = "An error has occurred!\nThe SQL statement failed.\nIf this message appears again, please consult the maintenance manual.";
					JOptionPane.showMessageDialog(new JFrame(), msg, "Error: SQL Exception",
					        JOptionPane.ERROR_MESSAGE);
					star.log(sqle);
				}
				finally
				{
					if (success)
					{
						success = false;
						path = "";
						textField.setText(path);
						star.swapPanel(0);
					}
				}
			}
		});
		add(btnLoad);
		
		JLabel lblPleaseSelectA = new JLabel("Please select a file to load");
		springLayout.putConstraint(SpringLayout.WEST, lblPleaseSelectA, 182, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, lblPleaseSelectA, -6, SpringLayout.NORTH, textField);
		add(lblPleaseSelectA);
		
		ImagePanel imagePanel = new ImagePanel();
		springLayout.putConstraint(SpringLayout.NORTH, imagePanel, -300, SpringLayout.NORTH, lblPleaseSelectA);
		springLayout.putConstraint(SpringLayout.WEST, imagePanel, 159, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, imagePanel, -37, SpringLayout.NORTH, lblPleaseSelectA);
		springLayout.putConstraint(SpringLayout.EAST, imagePanel, 25, SpringLayout.EAST, lblPleaseSelectA);
		add(imagePanel);

	}
	
	//Generates and submits SQL statements to populate database
	public void generate() throws SQLException
	{
		if (path.equals(""))
		{
			String msg = "Cannot read from a blank file.\nPlease choose the file you want to open.";
	      	  JOptionPane.showMessageDialog(new JFrame(), msg, "Dialog",
	      		        JOptionPane.ERROR_MESSAGE);
	      	  return;
		}
		 int lport=5656;
		    String rhost="athena.ecs.csus.edu";
		    String host="athena.ecs.csus.edu";
		    int rport=3306;
		    String user="*******";
		    String password="******";
		    String dbuserName = "********";
	        String dbpassword = "*******";
	        String url = "jdbc:mysql://localhost:"+lport+"/test";
	        String driverName="com.mysql.jdbc.Driver";
	        Connection conn = null;
	        Session session= null;
	        Progress p = new Progress();
	        int val = 0;
	        p.prepBar(100);
	try{
				p.setVisible(true);
				p.updateProgress("Connecting...", val);
		    	//Set StrictHostKeyChecking property to no to avoid UnknownHostKey issue
		    	java.util.Properties config = new java.util.Properties(); 
		    	config.put("StrictHostKeyChecking", "no");
		    	JSch jsch = new JSch();
		    	session=jsch.getSession(user, host, 22);
		    	session.setPassword(password);
		    	session.setConfig(config);
	         //session.disconnect();
		    	session.connect();
		    	//int assinged_port=
		    	session.setPortForwardingL(lport, rhost, rport);
		      val+=5;
		      p.updateProgress("Connecting to database...", val);
		    	//mysql database connectivity
	            Class.forName(driverName).newInstance();
	            conn = DriverManager.getConnection (url, dbuserName, dbpassword);
		    }catch(Exception e){
	         session.disconnect();
	         String msg = "An error has occurred!\nFailed to connect to the database. Please check your internet connection.\nIf this message appears again, please consult the maintenance manual.";
				JOptionPane.showMessageDialog(new JFrame(), msg, "Error: Could not connect",
				        JOptionPane.ERROR_MESSAGE);
				p.dispose();
				return;
		    }



	    
	        try {
	        	val+=5;
	        	p.updateProgress("Opening file...",val);
	            FileInputStream excelFile = new FileInputStream(new File(path));
	            Workbook workbook = new XSSFWorkbook(excelFile);
	            Sheet datatypeSheet = workbook.getSheetAt(0);
	            Iterator<Row> iterator = datatypeSheet.iterator();
	            String section="";
	            String days="";
	            String faculty="";
	            String start="";
	            String end="";
	            String coursename="";
	            String cid="";
	            String building="";
	            String room="";
	            
	            while (iterator.hasNext()) {
	                Row currentRow = iterator.next();
	                Iterator<Cell> cellIterator = currentRow.iterator();

	                while (cellIterator.hasNext()) {
	                	val+=5;
		            	p.updateProgress("Reading from file...",val);
	                    Cell currentCell = cellIterator.next();
	                    
	                    if (currentCell.getColumnIndex() == 0) {
	                    //CellType type = currentCell.getCellTypeEnum();
	                    if(currentCell.getCellTypeEnum() == CellType.STRING) {
	                    String s=currentCell.getStringCellValue();
	                    if(s.equals("CSC Total")) {
	                    session.disconnect();
	                    }}}
	                    
	                    
	                    if (currentCell.getColumnIndex() == 0) {
	                    CellType type = currentCell.getCellTypeEnum();
	                    if (type == CellType.NUMERIC) {
	                    double cidd = currentCell.getNumericCellValue();
	                    int ciddd = (int)cidd;
	                    cid=Integer.toString(ciddd);
	                        //System.out.print(currentCell.getNumericCellValue() + "  ");
	                        }}
	                    
	                    if (currentCell.getColumnIndex() == 1) {
	                    if (currentCell.getStringCellValue()!= null) {
	                    coursename = currentCell.getStringCellValue();
	                        //System.out.print(currentCell.getStringCellValue() + "  ");
	                        }} 
	                    if (currentCell.getColumnIndex() == 2) {
	                    if (currentCell.getStringCellValue()!= null) {
	                    section = currentCell.getStringCellValue();
	                        //System.out.print(currentCell.getStringCellValue() + "  ");
	                        }} 
	                    if (currentCell.getColumnIndex() == 25) {
	                    if (currentCell.getStringCellValue()!= null) {
	                    faculty = currentCell.getStringCellValue();
	                        //System.out.print(currentCell.getStringCellValue() + "  ");
	                        }} 
	                    if (currentCell.getColumnIndex() == 26) {
	                    if (currentCell.getStringCellValue()!= null) {
	                    days = currentCell.getStringCellValue();
	                        //System.out.print(currentCell.getStringCellValue() + "  ");
	                        }} 
	                    if (currentCell.getColumnIndex() == 29) {
	                    CellType type = currentCell.getCellTypeEnum();
	                    if (type == CellType.NUMERIC) {
	                    Date date = currentCell.getDateCellValue();
	                    SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm aa");
	                    String time=formatTime.format(date);
	                    //System.out.println("\n\n"+time+"\n\n");
	                    String hr = time.substring(0, 2);
	                    String min = time.substring(3, 5);
	                    String xm = time.substring(6, 8);
	                    int hri = Integer.parseInt(hr);
	                    int mini = Integer.parseInt(min);
	                    
	                    String pm="AM";
	                    if(mini==9){
	                    mini++;
	                    pm="PM";
	                    }
	                    else if(mini==19){
	                    mini++;
	                    pm="PM";
	                    }
	                    else if(mini==29){
	                    mini++;
	                    pm="PM";
	                    }
	                    else if(mini==39){
	                    mini++;
	                    pm="PM";
	                    }
	                    else if(mini==49){
	                    mini++;
	                    pm="PM";
	                    }
	                    else if(mini==59 && hri==12){
	                    hri=1;
	                    mini=0;
	                    pm="PM";
	                    }
	                    else if(mini==59 && hri!=12){
	                    mini=0;
	                    hri++;
	                    xm="PM";
	                    }
	                    
	                    
	                    
	                    String minii = "";
	                    if (mini == 0){
	                    minii = "00"; 
	                    }
	                    else{
	                    minii = Integer.toString(mini);
	                    }

	                    
	                    time = hri+":"+minii+" "+xm;
	                    
	                    start = time;
	                        //System.out.print(start + "  ");
	                        }} 
	                    if (currentCell.getColumnIndex() == 30) {
	                    CellType type2 = currentCell.getCellTypeEnum();
	                    if (type2 == CellType.NUMERIC) {
	                    Date date2 = currentCell.getDateCellValue();
	                    SimpleDateFormat formatTime2 = new SimpleDateFormat("hh:mm aa");
	                    String time2=formatTime2.format(date2);
	                    String hr2 = time2.substring(0, 2);
	                    String min2 = time2.substring(3, 5);
	                    String xm2 = time2.substring(6, 8);
	                    int hri2 = Integer.parseInt(hr2);
	                    int mini2 = Integer.parseInt(min2);
	                    
	                    String pm2="AM";
	                    if(mini2==9){
	                    mini2++;
	                    pm2="PM";
	                    }
	                    else if(mini2==19){
	                    mini2++;
	                    pm2="PM";
	                    }
	                    else if(mini2==29){
	                    mini2++;
	                    pm2="PM";
	                    }
	                    else if(mini2==39){
	                    mini2++;
	                    pm2="PM";
	                    }
	                    else if(mini2==49){
	                    mini2++;
	                    pm2="PM";
	                    }
	                    else if(mini2==59 && hri2==12){
	                    hri2=1;
	                    mini2=0;
	                    pm2="PM";
	                    }
	                    else if(mini2==59 && hri2!=12){
	                    mini2=0;
	                    hri2++;
	                    xm2="PM";
	                    }

	                    
	                    
	                    
	                    
	                    String minii2 = "";
	                    if (mini2 == 0){
	                    minii2 = "00"; 
	                    }
	                    else{
	                    minii2 = Integer.toString(mini2);
	                    }
	                   
	                    time2 = hri2+":"+minii2+" "+xm2;
	                    
	                    end = time2;
	                        //System.out.print(end + "  ");
	                        }}
	                    if (currentCell.getColumnIndex() == 31) {
	                    if (currentCell.getStringCellValue()!= null) {
	                    if (currentCell.getStringCellValue().length()>=4) {
	                    building = currentCell.getStringCellValue();
	                    room = building.substring(3, building.length());
	                    building = building.substring(0, 3);
	                        //System.out.print(currentCell.getStringCellValue() + "  ");
	                        }}}
	                        
	                }
	                //System.out.println(""+"");
	                //SQL WORK HERE
	                if(cid.equals("CSC Total")){
	                session.disconnect();
	                }
	                if (!faculty.equals(""))
	                {
			          Statement st = conn.createStatement();
			          String sql = "SELECT ID from Teacher WHERE ID='"+faculty+"'";
			          ResultSet rs = st.executeQuery(sql);
			          if(rs.next())
			          {
			        	  //String sql9 = "SELECT * from Course WHERE CourseID='"+cid+"', CourseName='"+coursename+"'";
			        	  String sql9 = "SELECT * from Course WHERE CourseID='"+cid+"'";
				          ResultSet rs9 = st.executeQuery(sql9); 
	                      
		                   if(!rs9.next())
		                   {
						          String sql2 = "INSERT INTO `test`.`Course` (`TeacherName`, `Start`, `End`, `DayOfWeek`, `CourseID`, `CourseName`, `Section`, `Building`, `Room`) VALUES  ('"+faculty+"', '"+start+"', '"+end+"', '"+days+"', '"+cid+"', '"+coursename+"', '"+section+"', '"+building+"', '"+room+"')";
						          int rs2 = st.executeUpdate(sql2); 
		                   } else {
		                      String sql8 = "UPDATE Course SET `TeacherName`='"+faculty+"', `Start`='"+start+"', `End`='"+end+"', `DayOfWeek`='"+days+"', `Building`='"+building+"', `Room`='"+room+"' WHERE CourseID='"+cid+"'";
		                      int rs8 = st.executeUpdate(sql8);
		                   }
			          }
	                }
	            }
	            success = true;
	            workbook.close();
	        
	        } catch (SQLException s){
	        session.disconnect();
	        p.dispose();
	            throw s;
	        } catch (FileNotFoundException e) {
	        String msg = "An error has occurred!\nThe selected file could not be found.\nIf this message appears again, please consult the maintenance manual.";
			JOptionPane.showMessageDialog(new JFrame(), msg, "Error: File could not be found",
			        JOptionPane.ERROR_MESSAGE);
			star.log(e);
			p.dispose();
			return;
	        } catch (IOException e) {
	        String msg = "An error has occurred!\nSomething went wrong with the FileReader.\nIf this message appears again, please consult the maintenance manual.";
			JOptionPane.showMessageDialog(new JFrame(), msg, "Error: Could not read from file",
			        JOptionPane.ERROR_MESSAGE);
			star.log(e);
			p.dispose();
			return;
	        }
	        finally
	        {
	        	if(conn != null && !conn.isClosed()){
		    		val+=5;
		    		p.updateProgress("Closing connection",val);
		    		conn.close();
		    	}
		    	if(session !=null && session.isConnected()){
		    		session.disconnect();
		    	}
	        }
	        p.finish();
	}
}
