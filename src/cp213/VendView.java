package cp213;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Handles the GUI portion of the vending machine. May use other GUI classes for
 * individual elements of the vending machine. Should use the VendModel for all
 * control logic.
 *
 * @author Brandon Parker
 * @version 2021-03-24
 */
@SuppressWarnings("serial")
public class VendView extends JPanel {

	private VendModel model = null;

	private JPanel item_panel = new JPanel();
	private JPanel control_panel = new JPanel();

	private JLabel image;

	/**
	 * Constructor that sets up the main JFrame
	 * 
	 * @param model the logic to the vending machine
	 */
	public VendView(VendModel model) {
		super();
		this.model = model;
		// your code here
		// this.setBackground(Color.blue);
		this.setBackground(Color.WHITE);

		this.setLayout(new GridBagLayout());
		this.item_panel_setup();
		this.control_panel_setup();

	}

	/**
	 * Sets up the panel that shows all the different items available from the
	 * vending machine
	 */
	private void item_panel_setup() {

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;

		// set the item_panel layout to a gridbaglyout
		// item_panel.setBackground(Color.red);
		item_panel.setBackground(Color.WHITE);
		item_panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 5));
		item_panel.setOpaque(true);

		item_panel.setLayout(new GridBagLayout());
		this.add(item_panel, gbc);

		// add title to the vending machine
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.ipady = 0;
		gbc.ipadx = 0;

		JLabel title = new JLabel("Vend-O-Matic");
		title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

		item_panel.add(title, gbc);

		// get item detail strings from a file
		String[] str_arr = this.file_contents_to_array("src\\item_details.txt");

		// create all item objects
		for (String str : str_arr) {
			this.create_VendItem(str);
		}

		// adds all the images and their labels
		for (int y = 1; y < 11; y += 2) {
			gbc.gridy = y;
			gbc.ipadx = 5;
			gbc.ipady = 2;

			// add images
			for (int x = 0; x < model.vend_item_width; x++) {

				gbc.gridx = x;
				this.add_image(item_panel, gbc, x + (y / 2 * 3));
			}

			gbc.gridy = (y + 1);

			// add labels
			for (int x = 0; x < model.vend_item_width; x++) {

				gbc.gridx = x;
				this.add_label(item_panel, gbc, y / 2, x);
			}
		}

		JLabel vend_hole = new JLabel();

		vend_hole.setBackground(Color.WHITE);
		vend_hole.setOpaque(true);
		vend_hole.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));

		gbc.gridy = 12;
		gbc.gridx = 0;
		gbc.ipadx = 0;
		gbc.ipady = 50;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		item_panel.add(vend_hole, gbc);

	}

	/**
	 * Helper function to add the images of the items to the desired panel
	 * 
	 * @param panel JPanel you would like the image added to
	 * @param gbc   Constraints for the image being added
	 * @param index index of image you would like added
	 */
	private void add_image(JPanel panel, GridBagConstraints gbc, int index) {
		this.image = new JLabel();

		String path = "vend_images/" + model.item_array.get(index).getItem_name() + ".jpg";

		image.setIcon(new ImageIcon(path));

		image.setSize(new Dimension(80, 80));
		panel.add(image, gbc);
	}

	/**
	 * Creates a VendItem Object from the given string. String must be in the
	 * following form:
	 * 
	 * name,cost,stock
	 * 
	 * where name is the name of the VendItem, cost is a double of what the cost
	 * should be and stock is an int regarding the amount of items available
	 * 
	 * @param str parameters of the object separated by a comma
	 */
	private void create_VendItem(String str) {
		String[] str_array = str.split(",");
		double cost = Double.parseDouble(str_array[1]);
		int stock = Integer.parseInt(str_array[2]);

		VendItem vend_item = new VendItem(str_array[0], cost, stock);
		model.item_array.add(vend_item);

	}

	/**
	 * @param panel JPanel you would like the image added to
	 * @param gbc   Constraints for the label being added
	 * @param row   Row you would like the label added based on the item
	 * @param col   Column you would like the label added based on the item
	 */
	private void add_label(JPanel panel, GridBagConstraints gbc, int row, int col) {

		String vend_label = "" + ((char) (65 + row)) + col;

		JLabel label = new JLabel(vend_label);
		panel.add(label, gbc);
	}

	/**
	 * Creates the control panel for the vending machine. All the buttons and text
	 * boxes are added and setup.
	 */
	private void control_panel_setup() {

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.ipadx = 50;

		// control_panel.setBackground(Color.green);
		control_panel.setBackground(Color.WHITE);

		control_panel.setLayout(new GridBagLayout());

		// add the frame to the main frame
		this.add(control_panel, gbc);

		// add the selection buttons
		this.add_selection_buttons(control_panel);

		// add item label
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.ipady = 10;
		gbc.ipadx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JLabel cost_label = new JLabel("Cost:");
		JLabel item_label = new JLabel("Item: ");

		control_panel.add(item_label, gbc);

		// add item text box
		model.item_txt_field = new JTextField(7);
		model.item_txt_field.setEditable(false);
		model.item_txt_field.setHorizontalAlignment(JTextField.RIGHT);

		gbc.gridx = 1;
		gbc.gridy = 6;
		gbc.gridwidth = 2;
		gbc.ipady = 0;

		control_panel.add(model.item_txt_field, gbc);

		// add cost label
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.ipady = 10;
		gbc.ipadx = 0;

		control_panel.add(cost_label, gbc);

		// add cost text box
		model.cost_txt_field = new JTextField(7);
		model.cost_txt_field.setEditable(false);
		model.cost_txt_field.setText("$0.00");
		model.cost_txt_field.setHorizontalAlignment(JTextField.RIGHT);

		gbc.gridx = 1;
		gbc.gridy = 7;
		gbc.gridwidth = 2;
		gbc.ipady = 0;

		control_panel.add(model.cost_txt_field, gbc);

		// add button

		JButton add_item = new JButton("Add Item");
		add_item.addActionListener(model.new add_item_button_listener());

		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.gridwidth = 3;

		control_panel.add(add_item, gbc);

		// add items and total text boxes

		gbc.gridx = 0;
		gbc.gridy = 9;
		gbc.ipady = 10;
		gbc.gridwidth = 1;

		control_panel.add(new JLabel("Items:"), gbc);

		gbc.gridx = 1;
		gbc.ipady = 0;
		gbc.gridwidth = 2;

		model.selected_item_txt_field = new JTextField(7);
		model.selected_item_txt_field.setEditable(false);
		model.selected_item_txt_field.setHorizontalAlignment(JTextField.RIGHT);

		control_panel.add(model.selected_item_txt_field, gbc);

		gbc.gridx = 0;
		gbc.gridy = 10;
		gbc.ipady = 10;
		gbc.gridwidth = 1;

		control_panel.add(new JLabel("Total:"), gbc);

		gbc.gridx = 1;
		gbc.ipady = 0;
		gbc.gridwidth = 2;

		model.total_cost_txt_field = new JTextField(7);
		model.total_cost_txt_field.setEditable(false);
		model.total_cost_txt_field.setText("$0.00");
		model.total_cost_txt_field.setHorizontalAlignment(JTextField.RIGHT);

		control_panel.add(model.total_cost_txt_field, gbc);

		// add cash and credit buttons
		model.cash_btn = new JButton("Cash");
		model.credit_btn = new JButton("Credit");

		gbc.gridx = 0;
		gbc.gridy = 11;
		gbc.gridwidth = 3;

		control_panel.add(model.cash_btn, gbc);
		model.cash_btn.addActionListener(model.new cash_button_listener());

		gbc.gridy = 12;

		control_panel.add(model.credit_btn, gbc);
		model.credit_btn.addActionListener(model.new credit_button_listener());

		// cancel button

		JButton cancel_btn = new JButton("Cancel");
		cancel_btn.addActionListener(model.new cancel_button_listener());

		gbc.gridx = 0;
		gbc.gridy = 13;
		gbc.gridwidth = 3;

		control_panel.add(cancel_btn, gbc);

	}

	/**
	 * Helper function to add all the standard keypad button to the desired panel
	 * 
	 * @param panel JPanel you would like the buttons added to
	 */
	private void add_selection_buttons(JPanel panel) {

		model.btn_letter_a = new JButton("A");
		model.btn_letter_b = new JButton("B");
		model.btn_letter_c = new JButton("C");
		model.btn_letter_d = new JButton("D");
		model.btn_letter_e = new JButton("E");
		model.btn_letter_f = new JButton("F");
		model.btn_number_1 = new JButton("1");
		model.btn_number_2 = new JButton("2");
		model.btn_number_3 = new JButton("3");
		model.btn_number_4 = new JButton("4");
		model.btn_number_5 = new JButton("5");
		model.btn_number_6 = new JButton("6");
		model.btn_number_7 = new JButton("7");
		model.btn_number_8 = new JButton("8");
		model.btn_number_9 = new JButton("9");
		model.btn_symbol_asterisk = new JButton("*");
		model.btn_number_0 = new JButton("0");
		model.btn_symbol_pound = new JButton("#");

		this.add_button_gbc(0, 0, model.btn_letter_a, panel);
		this.add_button_gbc(1, 0, model.btn_letter_b, panel);
		this.add_button_gbc(2, 0, model.btn_letter_c, panel);
		this.add_button_gbc(0, 1, model.btn_letter_d, panel);
		this.add_button_gbc(1, 1, model.btn_letter_e, panel);
		this.add_button_gbc(2, 1, model.btn_letter_f, panel);
		this.add_button_gbc(0, 2, model.btn_number_1, panel);
		this.add_button_gbc(1, 2, model.btn_number_2, panel);
		this.add_button_gbc(2, 2, model.btn_number_3, panel);
		this.add_button_gbc(0, 3, model.btn_number_4, panel);
		this.add_button_gbc(1, 3, model.btn_number_5, panel);
		this.add_button_gbc(2, 3, model.btn_number_6, panel);
		this.add_button_gbc(0, 4, model.btn_number_7, panel);
		this.add_button_gbc(1, 4, model.btn_number_8, panel);
		this.add_button_gbc(2, 4, model.btn_number_9, panel);
		this.add_button_gbc(0, 5, model.btn_symbol_asterisk, panel);
		this.add_button_gbc(1, 5, model.btn_number_0, panel);
		this.add_button_gbc(2, 5, model.btn_symbol_pound, panel);

	}

	/**
	 * Helper function to add buttons
	 * 
	 * @param x      Column you would like the button added
	 * @param y      Row you would like the button added
	 * @param button JButton you would like added to the panel
	 * @param panel  JPanel you would like the button added to
	 */
	private void add_button_gbc(int x, int y, JButton button, JPanel panel) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		button.addActionListener(model.new control_panel_button_listener());

		panel.add(button, gbc);

	}

	/**
	 * Takes a file and puts each line into an array, the String array is a max of
	 * 15 lines.
	 * 
	 * @param file_path location of data
	 * @return
	 */
	private String[] file_contents_to_array(String file_path) {

		Scanner fileIn;
		String[] str_array = new String[15];
		try {
			fileIn = new Scanner(new FileInputStream(file_path));
			int i = 0;
			while (i < 15 && fileIn.hasNextLine()) {

				str_array[i] = fileIn.nextLine();
				// System.out.println(str_array[i]);
				i++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		}

		return str_array;
	}

}
