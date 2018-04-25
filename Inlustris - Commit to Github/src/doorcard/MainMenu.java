package doorcard;

import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/*
 * The main menu
 * 
 * Contains two buttons to switch between creator and populator
 */

public class MainMenu extends JPanel {

	private Starlight star;
	/**
	 * Create the panel.
	 */
	public MainMenu(Starlight s) {
		star = s;
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JButton btnNewButton = new JButton("Create Doorcards");
		springLayout.putConstraint(SpringLayout.NORTH, btnNewButton, 411, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, btnNewButton, 44, SpringLayout.WEST, this);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				star.swapPanel(1);
			}
		});
		add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Populate Database");
		springLayout.putConstraint(SpringLayout.NORTH, btnNewButton_1, 0, SpringLayout.NORTH, btnNewButton);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				star.swapPanel(2);
			}
		});
		add(btnNewButton_1);
		
		ImagePanel imagePanel = new ImagePanel();
		springLayout.putConstraint(SpringLayout.NORTH, imagePanel, 81, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, imagePanel, 170, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, imagePanel, -52, SpringLayout.NORTH, btnNewButton_1);
		springLayout.putConstraint(SpringLayout.EAST, imagePanel, -153, SpringLayout.EAST, this);
		add(imagePanel);
		
		JButton btnClearDatabase = new JButton("Clear Database");
		springLayout.putConstraint(SpringLayout.EAST, btnNewButton_1, -21, SpringLayout.WEST, btnClearDatabase);
		springLayout.putConstraint(SpringLayout.WEST, btnClearDatabase, 357, SpringLayout.WEST, this);
		btnClearDatabase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				star.swapPanel(3);
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btnClearDatabase, 0, SpringLayout.NORTH, btnNewButton);
		add(btnClearDatabase);
	}
}
