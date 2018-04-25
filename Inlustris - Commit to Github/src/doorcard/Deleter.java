package doorcard;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;

public class Deleter extends JPanel {
	
	/*
	 * Contains functions for truncating the course data in the database
	 * Does not clear Professor information
	 * Does not clear Office Hour information
	 */

	/**
	 * Create the panel.
	 */
	private Starlight star;
	private VerifyDiagFactory factory;
	private boolean success = false;
	public Deleter(Starlight s) {
		star = s;
		factory = new VerifyDiagFactory();
		
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JButton btnBack = new JButton("Back");
		springLayout.putConstraint(SpringLayout.NORTH, btnBack, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, btnBack, 0, SpringLayout.WEST, this);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				star.swapPanel(0);
			}
		});
		add(btnBack);
		
		JButton btnClearDatabase = new JButton("Clear Database");
		btnClearDatabase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Verify v = factory.createDiag();
				if (v != null)
					v.setVisible(true);
			}
		});
		add(btnClearDatabase);
		
		JLabel lblCaution = new JLabel("CAUTION");
		springLayout.putConstraint(SpringLayout.NORTH, lblCaution, 140, SpringLayout.NORTH, this);
		lblCaution.setFont(new Font("Tahoma", Font.BOLD, 23));
		lblCaution.setBackground(Color.WHITE);
		lblCaution.setForeground(Color.RED);
		add(lblCaution);
		
		JLabel lblThisFunctionWill = new JLabel("This function will delete the current course information for EVERY PROFESSOR");
		springLayout.putConstraint(SpringLayout.WEST, lblThisFunctionWill, 33, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, lblCaution, -6, SpringLayout.NORTH, lblThisFunctionWill);
		springLayout.putConstraint(SpringLayout.NORTH, lblThisFunctionWill, 164, SpringLayout.NORTH, this);
		add(lblThisFunctionWill);
		
		JLabel lblNewLabel = new JLabel("This function should be used to clear each given semester");
		springLayout.putConstraint(SpringLayout.SOUTH, lblThisFunctionWill, -12, SpringLayout.NORTH, lblNewLabel);
		springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel, 194, SpringLayout.NORTH, this);
		add(lblNewLabel);
		
		JLabel lblNewLabel_3 = new JLabel("or for radical revisions of the database");
		springLayout.putConstraint(SpringLayout.SOUTH, lblNewLabel, -6, SpringLayout.NORTH, lblNewLabel_3);
		springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel_3, 214, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblNewLabel_3, 139, SpringLayout.WEST, this);
		add(lblNewLabel_3);
		
		JLabel lblNewLabel_1 = new JLabel("For minor changes, please use the POPULATE function instead");
		springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel_1, 4, SpringLayout.SOUTH, lblNewLabel_3);
		springLayout.putConstraint(SpringLayout.SOUTH, lblNewLabel_1, -236, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, lblNewLabel, 0, SpringLayout.EAST, lblNewLabel_1);
		springLayout.putConstraint(SpringLayout.WEST, lblNewLabel_1, 76, SpringLayout.WEST, this);
		add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Are you ABSOLUTELY CERTAIN you wish to drop the current course information?");
		springLayout.putConstraint(SpringLayout.NORTH, btnClearDatabase, 42, SpringLayout.SOUTH, lblNewLabel_2);
		springLayout.putConstraint(SpringLayout.EAST, lblNewLabel_2, -10, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel_2, 22, SpringLayout.SOUTH, lblNewLabel_1);
		springLayout.putConstraint(SpringLayout.WEST, lblNewLabel_2, 32, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, lblNewLabel_2, -200, SpringLayout.SOUTH, this);
		add(lblNewLabel_2);
		
		ImagePanel imagePanel = new ImagePanel(true);
		springLayout.putConstraint(SpringLayout.WEST, imagePanel, 132, SpringLayout.EAST, btnBack);
		springLayout.putConstraint(SpringLayout.EAST, imagePanel, -180, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.WEST, btnClearDatabase, 0, SpringLayout.WEST, imagePanel);
		springLayout.putConstraint(SpringLayout.NORTH, imagePanel, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.SOUTH, imagePanel, -6, SpringLayout.NORTH, lblCaution);
		springLayout.putConstraint(SpringLayout.WEST, lblCaution, 10, SpringLayout.WEST, imagePanel);
		add(imagePanel);

	}
	public void clearFactory()
	{
		factory.clear();
	}
	
	public void drop()
	{
		int lport=5656;
	    String rhost="athena.ecs.csus.edu";
	    String host="athena.ecs.csus.edu";
	    int rport=3306;
	    String user="*********";
	    String password="*******";
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
	    	int assinged_port=session.setPortForwardingL(lport, rhost, rport);
	      val+=5;
	      p.updateProgress("Connecting to database...", val);
	    	//mysql database connectivity
            Class.forName(driverName).newInstance();
            conn = DriverManager.getConnection (url, dbuserName, dbpassword);
            
            Statement st;
            //Statement st2;
    		try {
    			st = conn.createStatement();
    			String sql = "SELECT * from Course";
    	        ResultSet rs = st.executeQuery(sql);
    	        if (!rs.next())
    	        {
    	        	String msg = "There is no information to drop";
    	        	JOptionPane.showMessageDialog(new JFrame(), msg, "Notice: No information present",
    			        JOptionPane.WARNING_MESSAGE);
    	        	p.close();
    	        	success = true;
    	        }
    	        else
    	        {
    	        	rs.beforeFirst();
    	        	star.log(rs);
    	        
	    	        //st2 = conn.createStatement();
	    	        String sql2 = "TRUNCATE TABLE Course";
	    	        st.execute(sql2);
		        	String msg = "Successfully dropped course information";
		        	JOptionPane.showMessageDialog(new JFrame(), msg, "Success: Information has been dropped",
				        JOptionPane.INFORMATION_MESSAGE);
		        	p.finish();
		        	success = true;
    	        }
    		} catch (SQLException e) {
    			session.disconnect();
    	         String msg = "An error has occurred!\nThere was an SQL exception.\nIf this message appears again, please consult the maintenance manual.";
    				JOptionPane.showMessageDialog(new JFrame(), msg, "Error: SQL Exception",
    				        JOptionPane.ERROR_MESSAGE);
    				star.log(e);
    				p.dispose();
    		}
	    }catch(Exception e){
         session.disconnect();
         String msg = "An error has occurred!\nFailed to connect to the database. Please check your internet connection.\nIf this message appears again, please consult the maintenance manual.";
			JOptionPane.showMessageDialog(new JFrame(), msg, "Error: Could not connect",
			        JOptionPane.ERROR_MESSAGE);
			p.dispose();
			return;
	    }
        finally
		{
			try {
				if(conn != null && !conn.isClosed()){
					val+=5;
					//p.updateProgress("Closing connection",val);
					conn.close();
				}
			} catch (SQLException e) {
				session.disconnect();
   	         String msg = "An error has occurred!\nThere was an SQL exception.\nIf this message appears again, please consult the maintenance manual.";
   				JOptionPane.showMessageDialog(new JFrame(), msg, "Error: SQL Exception",
   				        JOptionPane.ERROR_MESSAGE);
   				star.log(e);
   				p.dispose();
			}
	    	if(session !=null && session.isConnected()){
	    		session.disconnect();
	    	}
		}
        
	}
	
	public boolean getSuccess()
	{
		return success;
	}
	public void done()
	{
		success=false;
	}
	
	private class VerifyDiagFactory
	{
		/*
		 * Ensures only one verification dialog at one time.
		 */
		private int numWindows;
		public VerifyDiagFactory()
		{
			numWindows=0;
		}
		public Verify createDiag()
		{
			if (numWindows == 0)
			{
				numWindows++;
				return new Verify(star);
			}
			else
				return null;
		}
		public void clear(){numWindows = 0;}
	}
}
