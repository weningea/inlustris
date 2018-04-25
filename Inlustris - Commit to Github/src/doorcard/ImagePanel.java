package doorcard;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/*
 * Image Panel stores Sac State logo for main menu
 * 
 * Could be made more generic and less hard-coded
 */

public class ImagePanel extends JPanel {

	private BufferedImage image;
	/**
	 * Create the panel.
	 */
	public ImagePanel() {
		try {
			URL url = Starlight.class.getResource("/assets/logo.png");
			if (url == null)
				image = ImageIO.read(new File("src/assets/logo.png"));
			else
				image = ImageIO.read(url);
	       } catch (IOException ex) {
	    	   /*String msg = "An error occurred!\nSomething went wrong loading the following image:\n"+image.toString();
	       	  JOptionPane.showMessageDialog(new JFrame(), msg, "Dialog",
	       		        JOptionPane.ERROR_MESSAGE);*/
	    	   System.out.println("An error occurred loading the image");
	       }
	}
	public ImagePanel(boolean flag)
	{
		try {
			URL url = Starlight.class.getResource("/assets/caution.png");
			if (url == null)
				image = ImageIO.read(new File("src/assets/caution.png"));
			else
				image = ImageIO.read(url);
	       } catch (IOException ex) {
	    	   /*String msg = "An error occurred!\nSomething went wrong loading the following image:\n"+image.toString();
	       	  JOptionPane.showMessageDialog(new JFrame(), msg, "Dialog",
	       		        JOptionPane.ERROR_MESSAGE);*/
	    	   System.out.println("An error occurred loading the image");
	       }
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this); // see javadoc for more info on the parameters            
    }


}
