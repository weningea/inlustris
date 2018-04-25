package doorcard;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/*
 * Progress dialog should display progress bar during loading situations
 * 
 * Does not work entirely. Not such a big issue
 */

public class Progress extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JButton okButton;
	private JLabel lblLoading;
	private JProgressBar progressBar;

	/**
	 * Create the dialog.
	 */
	public Progress() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setBounds(100, 100, 236, 165);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);
		{
			lblLoading = new JLabel("Loading");
			sl_contentPanel.putConstraint(SpringLayout.SOUTH, lblLoading, -30, SpringLayout.SOUTH, contentPanel);
			sl_contentPanel.putConstraint(SpringLayout.EAST, lblLoading, -90, SpringLayout.EAST, contentPanel);
			contentPanel.add(lblLoading);
			lblLoading.setVisible(true);
		}
		{
			progressBar = new JProgressBar();
			sl_contentPanel.putConstraint(SpringLayout.NORTH, progressBar, 6, SpringLayout.SOUTH, lblLoading);
			sl_contentPanel.putConstraint(SpringLayout.EAST, progressBar, -35, SpringLayout.EAST, contentPanel);
			contentPanel.add(progressBar);
			progressBar.setVisible(true);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						close();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.setVisible(false);
			}
		}
	}
	public void updateProgress(String s, int v)
	{
		lblLoading.setText(s);
		progressBar.setValue(v);
		this.repaint();
	}
	public void prepBar(int max)
	{
		progressBar.setMaximum(max);
	}
	public void finish()
	{
		lblLoading.setText("Finished");
		progressBar.setVisible(false);
		okButton.setVisible(true);
	}
	public void close()
	{
		this.dispose();
	}
}
