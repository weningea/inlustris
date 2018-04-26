package doorcard;

import javax.swing.JPanel;
import javax.swing.SpringLayout;
//import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.sql.Connection;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;

/*
 * Creates the doorcard Excel file using the information in the database
 */

public class Creator extends JPanel {

	private Starlight star;
	private JTextField textField;
	private JSpinner spinner;
	private JComboBox<String> comboBox;
	private String path = "";
	boolean success = false;
	
	/*
	 * TODO: Adjust the values in season[] to correspond better with school year
	 * Not necessary, but may provide a better user experience
	 */
	private static final String season[] = {"Winter", "Winter",
		    "Spring", "Spring", "Spring",
		    "Summer", "Summer", "Summer",
		    "Fall", "Fall", "Fall",
		    "Winter"};
	private static final int colwidth = 4100;
	private static final float rowheight = 15;
	
	private final JFileChooser fc = new JFileChooser();
	/**
	 * Create the panel.
	 */
	public Creator(Starlight s) {
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
		springLayout.putConstraint(SpringLayout.WEST, textField, 55, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, textField, -85, SpringLayout.SOUTH, this);
		textField.setEditable(false);
		add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton_1 = new JButton("Browse");
		springLayout.putConstraint(SpringLayout.EAST, textField, -6, SpringLayout.WEST, btnNewButton_1);
		springLayout.putConstraint(SpringLayout.WEST, btnNewButton_1, 355, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, btnNewButton_1, -83, SpringLayout.SOUTH, this);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //Browse to save file
				int returnVal = fc.showSaveDialog(Creator.this);
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
		
		JLabel lblNewLabel = new JLabel("Choose where to save your file");
		springLayout.putConstraint(SpringLayout.SOUTH, lblNewLabel, -111, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.NORTH, textField, 6, SpringLayout.SOUTH, lblNewLabel);
		springLayout.putConstraint(SpringLayout.WEST, lblNewLabel, 167, SpringLayout.WEST, this);
		add(lblNewLabel);
		
		JButton btnNewButton_2 = new JButton("Save");
		springLayout.putConstraint(SpringLayout.NORTH, btnNewButton_2, 6, SpringLayout.SOUTH, textField);
		springLayout.putConstraint(SpringLayout.WEST, btnNewButton_2, 221, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, btnNewButton_2, -59, SpringLayout.SOUTH, this);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					generate();
				}
				catch(SQLException sqle)
				{
					String msg = "An error has occurred!\nThe SQL SELECT statement failed.\nIf this message appears again, please consult the maintenance manual.";
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
		add(btnNewButton_2);
		
		ImagePanel imagePanel = new ImagePanel();
		springLayout.putConstraint(SpringLayout.NORTH, imagePanel, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, imagePanel, 98, SpringLayout.EAST, btnNewButton);
		springLayout.putConstraint(SpringLayout.SOUTH, imagePanel, -100, SpringLayout.NORTH, lblNewLabel);
		springLayout.putConstraint(SpringLayout.EAST, imagePanel, -171, SpringLayout.EAST, this);
		add(imagePanel);
		
		//Recommends the current season (Spring, Summer, Fall, Winter) to the user
		comboBox = new JComboBox<String>();
		springLayout.putConstraint(SpringLayout.WEST, comboBox, 166, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, comboBox, -271, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.NORTH, comboBox, 30, SpringLayout.SOUTH, imagePanel);
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Spring", "Summer", "Fall", "Winter"}));
		comboBox.setSelectedItem(season[java.time.LocalDate.now().getMonthValue()-1]);
		add(comboBox);
		
		//Recommends the current year to the user
		spinner = new JSpinner();
		springLayout.putConstraint(SpringLayout.SOUTH, spinner, 0, SpringLayout.SOUTH, comboBox);
		spinner.setValue(java.time.LocalDate.now().getYear());
		spinner.setEditor(new JSpinner.NumberEditor(spinner, "#"));
		springLayout.putConstraint(SpringLayout.NORTH, spinner, 30, SpringLayout.SOUTH, imagePanel);
		springLayout.putConstraint(SpringLayout.WEST, spinner, 6, SpringLayout.EAST, comboBox);
		springLayout.putConstraint(SpringLayout.EAST, spinner, 0, SpringLayout.EAST, lblNewLabel);
		add(spinner);
		star = s;
	}
	
	//Uses SELECT SQL statements and pulls the necessary information from the database, then saves it to the given file
	private void generate() throws SQLException
	{
		if (path.equals(""))
		{
			String msg = "Cannot save to a blank file.\nPlease choose where you want to save the file.";
      	  JOptionPane.showMessageDialog(new JFrame(), msg, "Error: Save file not chosen",
      		        JOptionPane.ERROR_MESSAGE);
      	  return;
		}
		int lport=5656;
	    String rhost="athena.ecs.csus.edu";
	    String host="athena.ecs.csus.edu";
	    int rport=3306;
	    String user="********";
	    String password="********";
	    String dbuserName = "*********";
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
	    	p.updateProgress("Connecting...",val);
	    	//Set StrictHostKeyChecking property to no to avoid UnknownHostKey issue
	    	java.util.Properties config = new java.util.Properties(); 
	    	config.put("StrictHostKeyChecking", "no");
	    	JSch jsch = new JSch();
	    	session=jsch.getSession(user, host, 22);
	    	session.setPassword(password);
	    	session.setConfig(config);
	    	session.connect();
	    	val+=5;
	    	p.updateProgress("Connected.", val);
	    	//int assinged_port=
	    			session.setPortForwardingL(lport, rhost, rport);
	    	val+=5;
	    	p.updateProgress("Port Forwarded", val);
	    	//mysql database connectivity
            Class.forName(driverName).newInstance();
            conn = DriverManager.getConnection(url, dbuserName, dbpassword);
            val+=5;
            p.updateProgress("Connected to Database", val);
	    }catch(Exception e){
	    	String msg = "An error has occurred!\nFailed to connect to the database. Please check your internet connection.\nIf this error continues please consult the maintenance manual.";
	      	  JOptionPane.showMessageDialog(new JFrame(), msg, "Error: Could not connect",
	      		        JOptionPane.ERROR_MESSAGE);
	      	  p.dispose();
	      	  return;
	    }
          try{
          Statement st = conn.createStatement();
          String sql = "SELECT * from Teacher";
          ResultSet rs = st.executeQuery(sql);  
          int rowNum=2;
          int colNum=3;
          FileOutputStream outputStream = new FileOutputStream(path);  
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Faculty Door Cards");
        
        sheet.setColumnWidth(1,colwidth);
        sheet.setColumnWidth(2,colwidth);
        sheet.setColumnWidth(3,colwidth);
        sheet.setColumnWidth(4,colwidth);
        sheet.setColumnWidth(5,colwidth);
        sheet.setDefaultRowHeightInPoints(rowheight);
        
         while(rs.next()){
        	 val+=5;
        	 p.updateProgress("Building doorcards",val);
         //Retrieve by column name
         String name = rs.getString("Name");
         String email = rs.getString("Email");
         String phone = rs.getString("Phone");
         String id = rs.getString("ID");
         String fbuild = rs.getString("Building");
         String froom = rs.getString("Room");
         //Display values
         
         //Write to xlxs
        int[] pos = header(name,email,phone,id,fbuild,froom,rowNum,colNum,workbook,sheet);
        rowNum = pos[0];
        colNum = pos[1];
        rowNum++;
        
        Statement st2 = conn.createStatement();
          String sql2 = "SELECT * FROM Course WHERE `TeacherName` = '"+id+"';";
          ResultSet rs2 = st2.executeQuery(sql2);
          int numclasses=0;
          
          while(rs2.next())
          { //Generates class list
	        numclasses++;
	        String cname = rs2.getString("CourseName");
	        cname=cname.replaceAll("\\s+"," ");
	        String sec = rs2.getString("Section");
	        String dow = rs2.getString("DayOfWeek");
	        String start = rs2.getString("Start");
	        String end = rs2.getString("End");
	        String building = rs2.getString("Building");
	        String room = rs2.getString("Room");
	        String start2 = start.substring(0, start.length()-3);
	        String end2 = end.replaceAll("\\s+","");
	        String time = start2+"-"+end2;
	        String place = building+" "+room;
	
	        Row row7 = sheet.createRow(rowNum);
	        Cell cell7a = row7.createCell(colNum);
	        colNum++;
	        Cell cell7b = row7.createCell(colNum);
	        colNum++;
	        Cell cell7c = row7.createCell(colNum);
	        colNum++;
	        Cell cell7d = row7.createCell(colNum);
	        colNum++;
	        Cell cell7e = row7.createCell(colNum);
	        colNum--;
	        colNum--;
	        colNum--;
	        colNum--;
	        CellStyle cellStyle7 = row7.getSheet().getWorkbook().createCellStyle();
	        CellStyle cellStyle7a = row7.getSheet().getWorkbook().createCellStyle();
	        CellStyle cellStyle7b = row7.getSheet().getWorkbook().createCellStyle();
	        cellStyle7.setAlignment(HorizontalAlignment.CENTER);
	        //cellStyle7.setBorderTop(BorderStyle.THICK);
	        cellStyle7a.setAlignment(HorizontalAlignment.CENTER);
	        //cellStyle7a.setBorderTop(BorderStyle.THICK);
	        cellStyle7a.setBorderLeft(BorderStyle.THICK);
	        cellStyle7b.setAlignment(HorizontalAlignment.CENTER);
	        //cellStyle7b.setBorderTop(BorderStyle.THICK);
	        cellStyle7b.setBorderRight(BorderStyle.THICK); 
	        for(int j = 1; j<=5; j++)
	        row7.createCell(j);   
	        for(int j = 1; j<=5; j++)
	        row7.getCell(j).setCellStyle(cellStyle7);
	        row7.getCell(1).setCellStyle(cellStyle7a);
	        row7.getCell(5).setCellStyle(cellStyle7b);
	        XSSFFont font7 = workbook.createFont();
	        font7.setFontHeightInPoints((short) 11);
	        //font7.setBold(true);
	        cellStyle7.setFont(font7);
	        cellStyle7a.setFont(font7);
	        cellStyle7b.setFont(font7);
	
	        cell7a.setCellValue((String) cname);
	        cell7b.setCellValue((String) sec);
	        cell7c.setCellValue((String) dow);
	        cell7d.setCellValue((String) time);
	        cell7e.setCellValue((String) place);
	        rowNum++;
	        if (numclasses == 8)
	        {
	        	rowNum = genBlankLines(rowNum,workbook,sheet);
	        	rowNum = genBlankLines(rowNum,workbook,sheet);
	        	pos = footer("",rowNum,colNum,workbook,sheet);
	        	rowNum = pos[0];
	        	colNum = pos[1];
	        	pos = header(name,email,phone,id,fbuild,froom,rowNum,colNum,workbook,sheet);
	            rowNum = pos[0]+1;
	            colNum = pos[1];
	            numclasses = 0;
	        }
        }
        if(numclasses<8){ //Generates blank lines
	        for(;numclasses<8;numclasses++){
	        	rowNum = genBlankLines(rowNum,workbook,sheet);
	        }
        }
             
       
       
       
       Statement st3 = conn.createStatement();
          String sql3 = "SELECT * FROM Office WHERE `TeacherName` = '"+name+"';";
          ResultSet rs3 = st3.executeQuery(sql3);
          String officehours="";
          String officehours2="";
          int numoff=0;
          
         while(rs3.next()){
         numoff++;
         String oday = rs3.getString("DayOfWeek");
         String ostart = rs3.getString("Start");
         String oend = rs3.getString("End");
         if(oday.equals("Monday")){
         oday="M";
          }
         else if(oday.equals("Tuesday")){
         oday="T";
          }
         else if(oday.equals("Wednesday")){
         oday="W";
          }
         else if(oday.equals("Thursday")){
         oday="R";
          }
         else if(oday.equals("Friday")){
         oday="F";
          }
         else if(oday.equals("Saturday")){
         oday="S";
          }else{
          oday="???";
          }
          
          String ohr1 = ostart.substring(0,2);
          String omin1 = ostart.substring(3,5);
          int ohr1i = Integer.parseInt(ohr1);
          //int omin1i = Integer.parseInt(omin1);
          //System.out.println("\n\nSDSDSDS\n"+ohr1i+"\n\n");
          if(ohr1i>12){
          ohr1i=ohr1i-12;
          }
          ostart=ohr1i+":"+omin1+"-";
          //System.out.println(ohr1i+":"+omin1i);
          
          String ohr2 = oend.substring(0,2);
          String omin2 = oend.substring(3,5);
          int ohr2i = Integer.parseInt(ohr2);
          //int omin2i = Integer.parseInt(omin2);
          //System.out.println("\n\nSDSDSDS\n"+ohr2i+"\n\n");
          String oendxm="";
          if(ohr2i>12){
          ohr2i=ohr2i-12;
          oendxm="PM";
          }else if(ohr2i==12){
          oendxm="PM";
          }else{
          oendxm="AM";
          }
          oend=ohr2i+":"+omin2+oendxm+", ";
          //System.out.println(ohr2i+":"+omin2i);
          
          if(numoff<5){
          officehours=officehours+oday+" "+ostart+oend;
          }else{
          officehours2=officehours2+oday+" "+ostart+oend;
          }
          
          
          
          
          }
       if(officehours.length()>5){
        officehours=officehours.substring(0,officehours.length()-2);
        }
        
        
       if(officehours2.length()>5){
        officehours2=officehours2.substring(0,officehours2.length()-2);
        }
        
        Row rowz = sheet.createRow(rowNum);
        Cell cellz = rowz.createCell(colNum);
        colNum++;
        Cell cellza = rowz.createCell(colNum);
        colNum--;
        CellStyle cellStylez = rowz.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyleza = rowz.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStylezb = rowz.getSheet().getWorkbook().createCellStyle();
        cellStylez.setAlignment(HorizontalAlignment.LEFT);
        //cellStylez.setBorderTop(BorderStyle.THICK);
        cellStyleza.setAlignment(HorizontalAlignment.RIGHT);
        //cellStyleza.setBorderTop(BorderStyle.THICK);
        cellStyleza.setBorderLeft(BorderStyle.THICK);
        cellStylezb.setAlignment(HorizontalAlignment.LEFT);
        //cellStylezb.setBorderTop(BorderStyle.THICK);
        cellStylezb.setBorderRight(BorderStyle.THICK);
        cellz.setCellStyle(cellStylez);  
        for(int j = 1; j<=5; j++)
        rowz.createCell(j);   
        for(int j = 1; j<=5; j++)
        rowz.getCell(j).setCellStyle(cellStylez);
        rowz.getCell(1).setCellStyle(cellStyleza);
        rowz.getCell(5).setCellStyle(cellStylezb);
        XSSFFont fontz = workbook.createFont();
        fontz.setFontHeightInPoints((short) 11);
        //fontz.setBold(true);
        XSSFFont fontza = workbook.createFont();
        fontza.setFontHeightInPoints((short) 11);
        fontza.setBold(true);
        cellStylez.setFont(fontz); 
        cellStyleza.setFont(fontza);
        cellz.setCellValue((String) "Office Location:");
        if (fbuild != null && froom != null)
        	cellza.setCellValue((String) fbuild+" "+froom);
        else
        	cellza.setCellValue("No available office");
        rowNum++; 
        
        
        
        
        
        
        Row row9 = sheet.createRow(rowNum);
        Cell cell9 = row9.createCell(colNum);
        colNum++;
        Cell cell9a = row9.createCell(colNum);
        colNum--;
        CellStyle cellStyle9 = row9.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle9a = row9.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle9b = row9.getSheet().getWorkbook().createCellStyle();
        cellStyle9.setAlignment(HorizontalAlignment.LEFT);
        //cellStyle9.setBorderTop(BorderStyle.THICK);
        cellStyle9a.setAlignment(HorizontalAlignment.RIGHT);
        //cellStyle9a.setBorderTop(BorderStyle.THICK);
        cellStyle9a.setBorderLeft(BorderStyle.THICK);
        cellStyle9b.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle9b.setBorderTop(BorderStyle.THICK);
        cellStyle9b.setBorderRight(BorderStyle.THICK);
        cell9.setCellStyle(cellStyle9);  
        for(int j = 1; j<=5; j++)
        row9.createCell(j);   
        for(int j = 1; j<=5; j++)
        row9.getCell(j).setCellStyle(cellStyle9);
        row9.getCell(1).setCellStyle(cellStyle9a);
        row9.getCell(5).setCellStyle(cellStyle9b);
        XSSFFont font9 = workbook.createFont();
        font9.setFontHeightInPoints((short) 11);
        //font9.setBold(true);
        XSSFFont font9a = workbook.createFont();
        font9a.setFontHeightInPoints((short) 11);
        font9a.setBold(true);
        cellStyle9.setFont(font9); 
        cellStyle9a.setFont(font9a);
        cell9.setCellValue((String) "Office Hours:");
        cell9a.setCellValue((String) officehours);
        rowNum++; 
        
        
        pos = footer(officehours2,rowNum,colNum,workbook,sheet);
        rowNum = pos[0];
        colNum = pos[1];
          }
                         try {
            workbook.write(outputStream);
            workbook.close();
            success = true;
        }  catch (IOException e) {
        	String msg = "An error has occurred!\nSomething went wrong with the OuputFileStream.\nIf this error continues please consult the maintenance manual.";
      	  JOptionPane.showMessageDialog(new JFrame(), msg, "Error: Could not open output stream",
      		        JOptionPane.ERROR_MESSAGE);
      	  star.log(e);
      	  p.dispose();
        }
          rs.close();
          }
          catch (SQLException s){
        	  p.dispose();
        	  throw s;
          }
          catch (FileNotFoundException e)
          {
        	  String msg = "An error has occurred!\nThe selected file could not be found.\nIf this error continues please consult the maintenance manual.";
        	  JOptionPane.showMessageDialog(new JFrame(), msg, "Error: Could not find file",
        		        JOptionPane.ERROR_MESSAGE);
        	  star.log(e);
        	  p.dispose();
          }
          finally{
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
	private int[] header(String name, String email, String phone, String id, String fbuild, String froom, int rowNum, int colNum, XSSFWorkbook workbook, XSSFSheet sheet)
	{
		Row row0 = sheet.createRow(rowNum);
        Cell cell0 = row0.createCell(colNum);
        CellStyle cellStyle0 = row0.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle0a = row0.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle0b = row0.getSheet().getWorkbook().createCellStyle();
        cellStyle0.setAlignment(HorizontalAlignment.CENTER);
        cellStyle0.setBorderTop(BorderStyle.THICK);
        cellStyle0a.setAlignment(HorizontalAlignment.CENTER);
        cellStyle0a.setBorderTop(BorderStyle.THICK);
        cellStyle0a.setBorderLeft(BorderStyle.THICK);
        cellStyle0b.setAlignment(HorizontalAlignment.CENTER);
        cellStyle0b.setBorderTop(BorderStyle.THICK);
        cellStyle0b.setBorderRight(BorderStyle.THICK);
        cell0.setCellStyle(cellStyle0);  
        for(int j = 1; j<=5; j++)
        row0.createCell(j);   
        for(int j = 1; j<=5; j++)
        row0.getCell(j).setCellStyle(cellStyle0);
        row0.getCell(1).setCellStyle(cellStyle0a);
        row0.getCell(5).setCellStyle(cellStyle0b);
        XSSFFont font0 = workbook.createFont();
        font0.setFontHeightInPoints((short) 14);
        font0.setBold(true);
        cellStyle0.setFont(font0); 
        cell0.setCellValue((String) "CALIFORNIA STATE UNIVERSITY, SACRAMENTO");
        rowNum++;
        
        
        
        
        Row row1 = sheet.createRow(rowNum);
        Cell cell1 = row1.createCell(colNum);
        CellStyle cellStyle1 = row1.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle1a = row1.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle1b = row1.getSheet().getWorkbook().createCellStyle();
        cellStyle1.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle1.setBorderTop(BorderStyle.THICK);
        cellStyle1a.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle1a.setBorderTop(BorderStyle.THICK);
        cellStyle1a.setBorderLeft(BorderStyle.THICK);
        cellStyle1b.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle1b.setBorderTop(BorderStyle.THICK);
        cellStyle1b.setBorderRight(BorderStyle.THICK);
        cell1.setCellStyle(cellStyle1);  
        for(int j = 1; j<=5; j++)
        row1.createCell(j);   
        for(int j = 1; j<=5; j++)
        row1.getCell(j).setCellStyle(cellStyle1);
        row1.getCell(1).setCellStyle(cellStyle1a);
        row1.getCell(5).setCellStyle(cellStyle1b);
        XSSFFont font1 = workbook.createFont();
        font1.setFontHeightInPoints((short) 14);
        font1.setBold(true);
        cellStyle1.setFont(font1); 
        cell1.setCellValue((String) "DEPARTMENT OF COMPUTER SCIENCE");
        rowNum++;        
        
        
        
        Row row2 = sheet.createRow(rowNum);
        Cell cell2 = row2.createCell(colNum);
        CellStyle cellStyle2 = row2.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle2a = row2.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle2b = row2.getSheet().getWorkbook().createCellStyle();
        cellStyle2.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle2.setBorderTop(BorderStyle.THICK);
        cellStyle2a.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle2a.setBorderTop(BorderStyle.THICK);
        cellStyle2a.setBorderLeft(BorderStyle.THICK);
        cellStyle2b.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle2b.setBorderTop(BorderStyle.THICK);
        cellStyle2b.setBorderRight(BorderStyle.THICK);
        cell2.setCellStyle(cellStyle2);  
        for(int j = 1; j<=5; j++)
        row2.createCell(j);   
        for(int j = 1; j<=5; j++)
        row2.getCell(j).setCellStyle(cellStyle2);
        row2.getCell(1).setCellStyle(cellStyle2a);
        row2.getCell(5).setCellStyle(cellStyle2b);
        XSSFFont font2 = workbook.createFont();
        font2.setFontHeightInPoints((short) 14);
        font2.setBold(true);
        cellStyle2.setFont(font2); 
        cell2.setCellValue(((String) comboBox.getSelectedItem())+" "+spinner.getValue());
        rowNum++;         
         
         
         Row rowqbl = sheet.createRow(rowNum);
        CellStyle cellStyleqbl = rowqbl.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyleqbla = rowqbl.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyleqblb = rowqbl.getSheet().getWorkbook().createCellStyle();
        cellStyleqbl.setAlignment(HorizontalAlignment.CENTER);
        //cellStyleqbl.setBorderTop(BorderStyle.THICK);
        cellStyleqbla.setAlignment(HorizontalAlignment.CENTER);
        //cellStyleqbla.setBorderTop(BorderStyle.THICK);
        cellStyleqbla.setBorderLeft(BorderStyle.THICK);
        cellStyleqblb.setAlignment(HorizontalAlignment.CENTER);
        //cellStyleqblb.setBorderTop(BorderStyle.THICK);
        cellStyleqblb.setBorderRight(BorderStyle.THICK);  
        for(int j = 1; j<=5; j++)
        rowqbl.createCell(j);   
        for(int j = 1; j<=5; j++)
        rowqbl.getCell(j).setCellStyle(cellStyleqbl);
        rowqbl.getCell(1).setCellStyle(cellStyleqbla);
        rowqbl.getCell(5).setCellStyle(cellStyleqblb);
        XSSFFont fontqbl = workbook.createFont();
        fontqbl.setFontHeightInPoints((short) 13);
        fontqbl.setBold(true);
        cellStyleqbl.setFont(fontqbl);
        cellStyleqbla.setFont(fontqbl);
        cellStyleqblb.setFont(fontqbl); 
        rowNum++;
         
         
         
         
        Row row3 = sheet.createRow(rowNum);
        Cell cell3 = row3.createCell(colNum);
        CellStyle cellStyle3 = row3.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle3a = row3.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle3b = row3.getSheet().getWorkbook().createCellStyle();
        cellStyle3.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle3.setBorderTop(BorderStyle.THICK);
        cellStyle3a.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle3a.setBorderTop(BorderStyle.THICK);
        cellStyle3a.setBorderLeft(BorderStyle.THICK);
        cellStyle3b.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle3b.setBorderTop(BorderStyle.THICK);
        cellStyle3b.setBorderRight(BorderStyle.THICK);
        cell3.setCellStyle(cellStyle3);  
        for(int j = 1; j<=5; j++)
        row3.createCell(j);   
        for(int j = 1; j<=5; j++)
        row3.getCell(j).setCellStyle(cellStyle3);
        row3.getCell(1).setCellStyle(cellStyle3a);
        row3.getCell(5).setCellStyle(cellStyle3b);
        XSSFFont font3 = workbook.createFont();
        font3.setFontHeightInPoints((short) 13);
        font3.setBold(true);
        cellStyle3.setFont(font3); 
        cell3.setCellValue((String) name);
        rowNum++;        
        
        Row row4 = sheet.createRow(rowNum);
        Cell cell4 = row4.createCell(colNum);
        CellStyle cellStyle4 = row4.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle4a = row4.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle4b = row4.getSheet().getWorkbook().createCellStyle();
        cellStyle4.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle4.setBorderTop(BorderStyle.THICK);
        cellStyle4a.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle4a.setBorderTop(BorderStyle.THICK);
        cellStyle4a.setBorderLeft(BorderStyle.THICK);
        cellStyle4b.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle4b.setBorderTop(BorderStyle.THICK);
        cellStyle4b.setBorderRight(BorderStyle.THICK);
        cell4.setCellStyle(cellStyle4);  
        for(int j = 1; j<=5; j++)
        row4.createCell(j);   
        for(int j = 1; j<=5; j++)
        row4.getCell(j).setCellStyle(cellStyle4);
        row4.getCell(1).setCellStyle(cellStyle4a);
        row4.getCell(5).setCellStyle(cellStyle4b);
        XSSFFont font4 = workbook.createFont();
        font4.setFontHeightInPoints((short) 12);
        font4.setBold(true);
        cellStyle4.setFont(font4); 
        cell4.setCellValue((String) email);
        rowNum++; 

        Row row5 = sheet.createRow(rowNum);
        Cell cell5 = row5.createCell(colNum);
        CellStyle cellStyle5 = row5.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle5a = row5.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle5b = row5.getSheet().getWorkbook().createCellStyle();
        cellStyle5.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle5.setBorderTop(BorderStyle.THICK);
        cellStyle5a.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle5a.setBorderTop(BorderStyle.THICK);
        cellStyle5a.setBorderLeft(BorderStyle.THICK);
        cellStyle5b.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle5b.setBorderTop(BorderStyle.THICK);
        cellStyle5b.setBorderRight(BorderStyle.THICK);
        cell5.setCellStyle(cellStyle5);  
        for(int j = 1; j<=5; j++)
        row5.createCell(j);   
        for(int j = 1; j<=5; j++)
        row5.getCell(j).setCellStyle(cellStyle5);
        row5.getCell(1).setCellStyle(cellStyle5a);
        row5.getCell(5).setCellStyle(cellStyle5b);
        XSSFFont font5 = workbook.createFont();
        font5.setFontHeightInPoints((short) 12);
        font5.setBold(true);
        cellStyle5.setFont(font5); 
        if(phone != null){
        cell5.setCellValue((String) "(916) "+phone);
        } else {
        cell5.setCellValue((String) "<No Phone Number Given>");
        }
        rowNum++;
        
        Row row5bl = sheet.createRow(rowNum);
        CellStyle cellStyle5bl = row5bl.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle5bla = row5bl.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle5blb = row5bl.getSheet().getWorkbook().createCellStyle();
        cellStyle5bl.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle5bl.setBorderTop(BorderStyle.THICK);
        cellStyle5bla.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle5bla.setBorderTop(BorderStyle.THICK);
        cellStyle5bla.setBorderLeft(BorderStyle.THICK);
        cellStyle5blb.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle5blb.setBorderTop(BorderStyle.THICK);
        cellStyle5blb.setBorderRight(BorderStyle.THICK);  
        for(int j = 1; j<=5; j++)
        	row5bl.createCell(j);   
        for(int j = 1; j<=5; j++)
        	row5bl.getCell(j).setCellStyle(cellStyle5bl);
        row5bl.getCell(1).setCellStyle(cellStyle5bla);
        row5bl.getCell(5).setCellStyle(cellStyle5blb);
        XSSFFont font5bl = workbook.createFont();
        font5bl.setFontHeightInPoints((short) 13);
        font5bl.setBold(true);
        cellStyle5bl.setFont(font5bl);
        cellStyle5bla.setFont(font5bl);
        cellStyle5blb.setFont(font5bl); 
        rowNum++;
        
        
        colNum--;
        colNum--;
        Row row6 = sheet.createRow(rowNum);
        Cell cell6 = row6.createCell(colNum);
        Cell cell6a = row6.createCell(colNum);
        colNum++;
        Cell cell6b = row6.createCell(colNum);
        colNum++;
        Cell cell6c = row6.createCell(colNum);
        colNum++;
        Cell cell6d = row6.createCell(colNum);
        colNum++;
        Cell cell6e = row6.createCell(colNum);

        CellStyle cellStyle6 = row6.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle6a = row6.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle6b = row6.getSheet().getWorkbook().createCellStyle();
        cellStyle6.setAlignment(HorizontalAlignment.CENTER);
        cellStyle6.setBorderBottom(BorderStyle.THIN);
        cellStyle6a.setAlignment(HorizontalAlignment.CENTER);
        cellStyle6a.setBorderBottom(BorderStyle.THIN);
        cellStyle6a.setBorderLeft(BorderStyle.THICK);
        cellStyle6b.setAlignment(HorizontalAlignment.CENTER);
        cellStyle6b.setBorderBottom(BorderStyle.THIN);
        cellStyle6b.setBorderRight(BorderStyle.THICK);
        cell6.setCellStyle(cellStyle6);  
        for(int j = 1; j<=5; j++)
        	row6.createCell(j);   
        for(int j = 1; j<=5; j++)
        	row6.getCell(j).setCellStyle(cellStyle6);
        row6.getCell(1).setCellStyle(cellStyle6a);
        row6.getCell(5).setCellStyle(cellStyle6b);
        XSSFFont font6 = workbook.createFont();
        font6.setFontHeightInPoints((short) 13);
        font6.setBold(true);
        cellStyle6.setFont(font6);
        cellStyle6a.setFont(font6);
        cellStyle6b.setFont(font6);

        colNum--;
        colNum--;
        colNum--;
        colNum--;
        cell6a.setCellValue((String) "COURSE");
        cell6b.setCellValue((String) "SECTION");
        cell6c.setCellValue((String) "DAYS");
        cell6d.setCellValue((String) "TIME");
        cell6e.setCellValue((String) "ROOM");
        
        int[] pos = {rowNum,colNum};
        return pos;
	}
	private int[] footer(String officehours2, int rowNum, int colNum, XSSFWorkbook workbook, XSSFSheet sheet)
	{
		Row row10 = sheet.createRow(rowNum);
        Cell cell10 = row10.createCell(colNum);
        colNum++;
        Cell cell10a = row10.createCell(colNum);
        colNum--;
        CellStyle cellStyle10 = row10.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle10a = row10.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle10b = row10.getSheet().getWorkbook().createCellStyle();
        cellStyle10.setAlignment(HorizontalAlignment.LEFT);
        cellStyle10.setBorderBottom(BorderStyle.THICK);
        cellStyle10a.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle10a.setBorderBottom(BorderStyle.THICK);
        cellStyle10a.setBorderLeft(BorderStyle.THICK);
        cellStyle10b.setAlignment(HorizontalAlignment.CENTER);
        cellStyle10b.setBorderBottom(BorderStyle.THICK);
        cellStyle10b.setBorderRight(BorderStyle.THICK);
        cell10.setCellStyle(cellStyle10);  
        for(int j = 1; j<=5; j++)
        row10.createCell(j);   
        for(int j = 1; j<=5; j++)
        row10.getCell(j).setCellStyle(cellStyle10);
        row10.getCell(1).setCellStyle(cellStyle10a);
        row10.getCell(5).setCellStyle(cellStyle10b);
        XSSFFont font10 = workbook.createFont();
        font10.setFontHeightInPoints((short) 11);
        //font10.setBold(true);
        XSSFFont font10a = workbook.createFont();
        font10a.setFontHeightInPoints((short) 11);
        font10a.setBold(true);
        cellStyle10.setFont(font10); 
        cellStyle10a.setFont(font10a);
        cell10.setCellValue((String) "");
        cell10a.setCellValue((String) officehours2);
        rowNum++;
        
        
        
        
        
        
        colNum++;
        colNum++;
        
        
        
        rowNum++;
        rowNum++;
        int[] pos = {rowNum,colNum};
        return pos;
	}
	private int genBlankLines(int rowNum, XSSFWorkbook workbook, XSSFSheet sheet)
	{
		Row row8bl = sheet.createRow(rowNum);
        CellStyle cellStyle8bl = row8bl.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle8bla = row8bl.getSheet().getWorkbook().createCellStyle();
        CellStyle cellStyle8blb = row8bl.getSheet().getWorkbook().createCellStyle();
        cellStyle8bl.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle8bl.setBorderTop(BorderStyle.THICK);
        cellStyle8bla.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle8bla.setBorderTop(BorderStyle.THICK);
        cellStyle8bla.setBorderLeft(BorderStyle.THICK);
        cellStyle8blb.setAlignment(HorizontalAlignment.CENTER);
        //cellStyle8blb.setBorderTop(BorderStyle.THICK);
        cellStyle8blb.setBorderRight(BorderStyle.THICK);  
        for(int j = 1; j<=5; j++)
        row8bl.createCell(j);   
        for(int j = 1; j<=5; j++)
        row8bl.getCell(j).setCellStyle(cellStyle8bl);
        row8bl.getCell(1).setCellStyle(cellStyle8bla);
        row8bl.getCell(5).setCellStyle(cellStyle8blb);
        XSSFFont font8bl = workbook.createFont();
        font8bl.setFontHeightInPoints((short) 11);
        font8bl.setBold(true);
        cellStyle8bl.setFont(font8bl);
        cellStyle8bla.setFont(font8bl);
        cellStyle8blb.setFont(font8bl); 
        rowNum++;
        return rowNum;
	}
}
