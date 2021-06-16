package cp213;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * Main class for the Vending Machine.
 *
 * @author Brandon Parker
 * @version 2021-03-24
 */
public class A05Main {

	/**
	 * Driver Code
	 * 
	 * @param args NOT NEEDED
	 */
	public static void main(String[] args) {
		VendModel model = new VendModel();
		VendView vv = new VendView(model);
		final JFrame frame = new JFrame();
		frame.setContentPane(vv);

		frame.setMinimumSize(new Dimension(100, 100));

		// i used the code below for centering the frame
		// you can use it if you want, I also change the
		// minimum size when i used the code below

//		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);

		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
