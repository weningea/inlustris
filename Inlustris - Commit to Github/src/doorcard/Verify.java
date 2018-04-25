package doorcard;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Verify extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */

	/**
	 * Create the dialog.
	 */
	private Starlight star;
	public Verify(Starlight s) {
		star = s;
		setTitle("Just checking...");
		setBounds(100, 100, 322, 215);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);
		{
			JLabel lblAreYouAbsolutely = new JLabel("Are you ABSOLUTELY SURE?");
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblAreYouAbsolutely, 50, SpringLayout.WEST, contentPanel);
			sl_contentPanel.putConstraint(SpringLayout.SOUTH, lblAreYouAbsolutely, -54, SpringLayout.SOUTH, contentPanel);
			lblAreYouAbsolutely.setFont(new Font("Tahoma", Font.PLAIN, 15));
			contentPanel.add(lblAreYouAbsolutely);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						star.getDeleter().drop();
						star.getDeleter().clearFactory();
						if (star.getDeleter().getSuccess())
						{
							star.getDeleter().done();
							star.swapPanel(0);
						}
						close();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						star.getDeleter().clearFactory();
						close();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public void close()
	{
		this.dispose();
	}
}
