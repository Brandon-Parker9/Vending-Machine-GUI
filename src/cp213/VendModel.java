package cp213;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * Vending machine model. Contains the algorithms for vending products, handling
 * change and inventory, and working with credit. Should not perform any GUI
 * work of any kind.
 *
 * @author Brandon Parker
 * @version 2021-03-24
 */
public class VendModel {

	/**
	 * Constants to help make sense of indexing the different denomination arrays
	 */
	public final int TEN = 0, FIVE = 1, TOONIE = 2, LOONIE = 3, QUARTER = 4, DIME = 5, NICKEL = 6;

	/**
	 * count of each denomination within the machine
	 */
	public int[] denomination_count = { 1, 2, 20, 20, 20, 20, 20 };

	/**
	 * Value of each denomination as a double
	 */
	public double[] denomination_value = { 10.00, 5.00, 2.00, 1.00, 0.25, 0.10, 0.05 };

	/**
	 * Overall width of the items in the vending machine
	 */
	public int vend_item_width = 3;

	public JTextField cost_txt_field;
	public JTextField item_txt_field;
	public JTextField selected_item_txt_field;
	public JTextField total_cost_txt_field;

	public JButton btn_letter_a;
	public JButton btn_letter_b;
	public JButton btn_letter_c;
	public JButton btn_letter_d;
	public JButton btn_letter_e;
	public JButton btn_letter_f;
	public JButton btn_number_1;
	public JButton btn_number_2;
	public JButton btn_number_3;
	public JButton btn_number_4;
	public JButton btn_number_5;
	public JButton btn_number_6;
	public JButton btn_number_7;
	public JButton btn_number_8;
	public JButton btn_number_9;
	public JButton btn_symbol_asterisk;
	public JButton btn_number_0;
	public JButton btn_symbol_pound;
	public JButton cash_btn;
	public JButton credit_btn;

	public ArrayList<VendItem> item_array = new ArrayList<>();

	/**
	 * A function to calculate the total number of each denomination needed to get
	 * the correct change. The function looks at the total amount of change in the
	 * machine and determines the best configuration of change. After calling the
	 * function, you NEED to check the the count of nickel with the count in the
	 * machine as that is the indicator if the machine has enough change. All the
	 * other denomination counts are considered when this functions is executed.
	 * 
	 * For example, if denomination_array[NICKEL] is greater than
	 * denomination_count[NICKEL], then the machine currently does not have enough
	 * change for the customer.
	 * 
	 * @param change the amount of change the customer needs
	 * @return array with the total number of each coins needed to give correct
	 *         change
	 */
	private int[] calculate_change(double change) {
		int[] denomination_array = { 0, 0, 0, 0, 0, 0, 0 };

		// determines how many of each denomination is needed
		// for the optimal solution
		for (int i = 0; i < 7; i++) {
			denomination_array[i] = (int) (change / denomination_value[i]);
			change = Math.round((change - denomination_value[i] * denomination_array[i]) * 100.0) / 100.0;
		}

		// determines the amount of each denomination based on
		// the count of each in the machine. The only one that is
		// not considered is the NICKEL's, as that is used
		// to check if the machine has enough change or not
		for (int i = 0; i < 7; i++) {
			if (i != FIVE && i != NICKEL) {
				if (denomination_array[i] > denomination_count[i]) {
					int temp = (int) (((denomination_array[i] - denomination_count[i]) * denomination_value[i])
							/ (denomination_value[i + 1]));
					denomination_array[i + 1] += temp;
					denomination_array[i] = denomination_count[i];
				}
			} else if (i == FIVE) {
				if (denomination_array[i] > denomination_count[i]) {
					int remaining = (denomination_array[i] - denomination_count[i]);
					int toonie = remaining * 2;
					int loonie = remaining;

					denomination_array[TOONIE] += toonie;
					denomination_array[LOONIE] += loonie;

					denomination_array[i] = denomination_count[i];

				}
			}
		}

		return denomination_array;
	}

	/**
	 * Takes are of all inputs from the main JFrame keypad
	 * 
	 * @author Brandon Parker
	 *
	 */
	public class control_panel_button_listener implements ActionListener {

		/**
		 * This action listener is used for the keypad buttons on the main JFrame. It
		 * updates the Item and Cost text box accordingly base on the input.
		 *
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			String str = e.getActionCommand();

			if (item_txt_field.getText().length() >= 2) {
				item_txt_field.setText(str);
				cost_txt_field.setText("$0.00");

			} else {
				item_txt_field.setText(item_txt_field.getText() + str);
			}

			if (item_txt_field.getText().length() == 2) {
				char[] item_str = item_txt_field.getText().toCharArray();
				int index = (int) (item_str[0] - 65) * 3 + Character.getNumericValue(item_str[1]);

				if (item_txt_field.getText().length() == 2 && Character.isLetter(item_str[0])
						&& Character.isDigit(item_str[1]) && index < item_array.size()
						&& Character.getNumericValue(item_str[1]) <= vend_item_width) {

					String amount = String.format("$ %.2f", item_array.get(index).getCost());
					cost_txt_field.setText(amount);
				}
			}
		}
	}

	/**
	 * Takes care of when the Cash button is clicked
	 * 
	 * @author Brandon Parker
	 *
	 */
	public class cash_button_listener implements ActionListener {

		JTextField cost_txt;
		JTextField input_txt;
		JTextField change_txt;
		JFrame payment_frame;

		String added_denominations = "";

		/**
		 * Takes care of when the Cash button is pushed on the main JFrame
		 * 
		 * @param e ActionEvent
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			if (selected_item_txt_field.getText().length() > 0) {
				get_payment();
			} else {
				JOptionPane.showMessageDialog(null, "Please choose an item first!");
			}

		}

		/**
		 * Sets up and opens a new JFrame to collect payment from the user. It has all
		 * the buttons and text boxes need to input cash amount and show what is being
		 * inputed.
		 * 
		 */
		private void get_payment() {
			// set up new frame
			payment_frame = new JFrame("Cash Payment");
			payment_frame.setLayout(new FlowLayout());
			payment_frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			payment_frame.setMinimumSize(new Dimension(400, 200));

			// set frame opening position to the middle
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			payment_frame.setLocation(dim.width / 2 - payment_frame.getSize().width / 2,
					dim.height / 2 - payment_frame.getSize().height / 2);

			// setup and add the two panels
			this.button_panel_setup(payment_frame);
			this.text_box_panel_setup(payment_frame);

			// set background and visibility
			payment_frame.getContentPane().setBackground(Color.WHITE);
			payment_frame.setVisible(true);

		}

		/**
		 * Sets up the buttons needed so the user can input the amount of cash they
		 * would like to put in the machine.
		 * 
		 * @param frame JFrame where you would like the panel added
		 */
		private void button_panel_setup(JFrame frame) {
			JPanel button_panel = new JPanel();
			button_panel.setLayout(new GridBagLayout());
			button_panel.setBackground(Color.WHITE);

			// string full of denominations allowed
			String[] dollar_amounts = { "$0.05", "$0.10", "$0.25", "$1.00", "$2.00", "$5.00" };

			// add denomination value buttons
			for (int y = 0; y < 2; y++) {
				for (int x = 0; x < 3; x++) {
					JButton btn = new JButton(dollar_amounts[(y * 3) + x]);
					btn.addActionListener(new denomination_button_listener());
					btn.setActionCommand(dollar_amounts[(y * 3) + x].substring(1));
					this.add_button_gbc(x, y, btn, button_panel);
				}
			}

			// add $10 button
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.gridwidth = 3;
			gbc.fill = GridBagConstraints.HORIZONTAL;

			JButton btn = new JButton("$10.00");
			btn.addActionListener(new denomination_button_listener());
			btn.setActionCommand("10.00");
			button_panel.add(btn, gbc);

			// Add confirm button
			gbc.gridy = 5;
			gbc.insets = new Insets(10, 0, 0, 0);
			JButton confirm_btn = new JButton("Confirm");
			confirm_btn.addActionListener(new confirm_cancel_clear_button_listener());
			button_panel.add(confirm_btn, gbc);

			// Add cancel button
			gbc.gridy = 6;
			JButton clear_btn = new JButton("Clear");
			clear_btn.addActionListener(new confirm_cancel_clear_button_listener());
			button_panel.add(clear_btn, gbc);

			frame.add(button_panel);

		}

		/**
		 * @param x      Column location
		 * @param y      Row location
		 * @param button JButton you would like added to the JPanel
		 * @param panel  JPanel you would like the button added to
		 */
		private void add_button_gbc(int x, int y, JButton button, JPanel panel) {
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = x;
			gbc.gridy = y;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			panel.add(button, gbc);

		}

		/**
		 * Sets up all the text boxes needed so the user can see what they are
		 * inputting.
		 * 
		 * @param frame JFrame where you would like the panel added
		 */
		private void text_box_panel_setup(JFrame frame) {

			// setup new panel for text boxes to be added to
			JPanel text_box_panel = new JPanel();
			text_box_panel.setLayout(new GridLayout(3, 2, 0, 10));
			text_box_panel.setBackground(Color.WHITE);

			// create the text boxes
			this.cost_txt = new JTextField(7);
			this.input_txt = new JTextField(7);
			this.change_txt = new JTextField(7);

			// initialize text box parameters
			cost_txt.setEditable(false);
			cost_txt.setText("$ 0.00");
			cost_txt.setHorizontalAlignment(JTextField.RIGHT);

			input_txt.setEditable(false);
			input_txt.setText("$ 0.00");
			input_txt.setHorizontalAlignment(JTextField.RIGHT);

			change_txt.setEditable(false);
			change_txt.setText("$ 0.00");
			change_txt.setHorizontalAlignment(JTextField.RIGHT);

			// add labels and text boxes
			text_box_panel.add(new JLabel("Cost:"));
			text_box_panel.add(cost_txt);
			text_box_panel.add(new JLabel("Input:"));
			text_box_panel.add(input_txt);
			text_box_panel.add(new JLabel("Change:"));
			text_box_panel.add(change_txt);

			cost_txt.setText(total_cost_txt_field.getText());

			// add new panel to the frame
			frame.add(text_box_panel);
		}

		/**
		 * Takes care of all the denomination buttons
		 * 
		 * @author Brandon Parker
		 *
		 */
		public class denomination_button_listener implements ActionListener {

			/**
			 * Updates the total input text box based on the denomination button the user
			 * clicks. It also stores which button they pushed for later
			 */
			@Override
			public void actionPerformed(ActionEvent e) {

				double amount = Double.parseDouble(e.getActionCommand());
				double prev_amount = Double.parseDouble(input_txt.getText().substring(1));
				amount += prev_amount;
				input_txt.setText(String.format("$ %.2f", amount));

				// adds the denomination chosen to the string for processing later
				// adds a comma if needed
				if (added_denominations.length() == 0) {
					added_denominations += e.getActionCommand();
				} else {
					added_denominations = added_denominations + "," + e.getActionCommand();
				}
			}
		}

		/**
		 * Takes care of the Confirm and Cancel Buttons
		 * 
		 * 
		 * @author Brandon Parker
		 *
		 */
		public class confirm_cancel_clear_button_listener implements ActionListener {

			/**
			 * After the confirm button is clicked, this function checks to make sure the
			 * user has entered enough money and the machine has enough change. Then it
			 * displays a message describing how much change you will receive.
			 * 
			 * After the cancel button is clicked, it will reset all the values and text
			 * boxes and the user can restart their input.
			 *
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand() == "Confirm") {

					double cost_amount = Double.parseDouble(cost_txt.getText().substring(1));
					double input_amount = Double.parseDouble(input_txt.getText().substring(1));
					double change_amount = input_amount - cost_amount;

					int[] change_array = calculate_change(change_amount);

					if (input_amount >= cost_amount && change_array[NICKEL] <= denomination_count[NICKEL]) {
						change_txt.setText(String.format("$ %.2f", change_amount));

						// update item stock
						String[] str_arr = selected_item_txt_field.getText().split(",");

						for (String str : str_arr) {
							char[] item_str = str.toCharArray();
							int index = (int) (item_str[0] - 65) * 3 + Character.getNumericValue(item_str[1]);
							item_array.get(index).remove_one_item();
						}

						// update the change left in the machine
						for (int i = 0; i < 7; i++) {
							denomination_count[i] -= change_array[i];
						}

						update_current_denomination_count(added_denominations);

						String message = "Thank you for your purchase! \n\n You recived the following change:"
								+ "\nTens: " + change_array[TEN] + "\nFives: " + change_array[FIVE] + "\nToonies: "
								+ change_array[TOONIE] + "\nLoonies: " + change_array[LOONIE] + "\nQuarters: "
								+ change_array[QUARTER] + "\nDimes: " + change_array[DIME] + "\nNickels: "
								+ change_array[NICKEL];

						JOptionPane.showMessageDialog(null, message);

						// small pause before removing the cash JFrame
						try {
							TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}

						payment_frame.dispose();

						cost_txt_field.setText("$0.00");
						item_txt_field.setText("");
						selected_item_txt_field.setText("");
						total_cost_txt_field.setText("$0.00");
						added_denominations = "";

					} else if (input_amount < cost_amount) {

						JOptionPane.showMessageDialog(null, "Sorry, Not enough funds.");
						change_txt.setText("$0.00");
						added_denominations = "";
					} else {

						JOptionPane.showMessageDialog(null, "Sorry, Not enough change in machine.");
						change_txt.setText("$0.00");
						added_denominations = "";
					}

					input_txt.setText("$0.00");

				} else if (e.getActionCommand() == "Clear") {
					input_txt.setText("$0.00");
					change_txt.setText("$0.00");
					added_denominations = "";
				}

			}

			/**
			 * Helper function to update the denomination counts within the machine when
			 * they customer inputs money
			 * 
			 * @param string string of denominations separated by a comma
			 */
			private void update_current_denomination_count(String string) {
				for (String str : string.split(",")) {
					double value = Double.parseDouble(str);

					if (value == denomination_value[TEN]) {
						denomination_count[TEN]++;

					} else if (value == denomination_value[FIVE]) {
						denomination_count[FIVE]++;

					} else if (value == denomination_value[TOONIE]) {
						denomination_count[TOONIE]++;

					} else if (value == denomination_value[LOONIE]) {
						denomination_count[LOONIE]++;

					} else if (value == denomination_value[QUARTER]) {
						denomination_count[QUARTER]++;

					} else if (value == denomination_value[DIME]) {
						denomination_count[DIME]++;

					} else if (value == denomination_value[NICKEL]) {
						denomination_count[NICKEL]++;

					}

				}
			}

		}
	}

	/**
	 * Takes care of when the Credit button is clicked
	 * 
	 * @author Brandon Parker
	 *
	 */
	public class credit_button_listener implements ActionListener {

		boolean transaction = true;

		/**
		 * Runs the logic for when the credit button is click. Since the Delay class
		 * used actually stops the thread, i am unable to make a message appear to say
		 * something like processing or connecting, but overall it does work.
		 *
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			// checks if an item has been chosen yet
			if (selected_item_txt_field.getText().length() > 0) {

				// checks if another transaction is already happening
				if (credit_btn.getText().compareTo("Connecting...") != 0) {

					credit_btn.setText("Connecting...");

					// stops user from being able to choose cash while credit transaction
					// is being processed
					cash_btn.setEnabled(false);

					// starts new thread to run the delay class so the GUI still works
					Thread runnable = new Thread(new my_runnable());
					runnable.start();

				} else {
					// if another transaction is being processed
					JOptionPane.showMessageDialog(null,
							"Please wait, another transaction is being processed!\nTry again when not connecting.");
				}
			} else {
				// if no items are chosen yet
				JOptionPane.showMessageDialog(null, "Please choose an item first!");
			}
		}

		private class my_runnable implements Runnable {

			@Override
			public void run() {
				pause();

			}

			/**
			 * Calling the delayed class and depending on whether or not the transaction is
			 * approved, updates the item stock, or displays the appropriate message
			 */
			private synchronized void pause() {
				Delay delay = new Delay();
				try {
					transaction = delay.call();

					if (transaction) {
						JOptionPane.showMessageDialog(null,
								"Approved! Thank you for your purchase!\nTotal amount paid: "
										+ total_cost_txt_field.getText());

						if (selected_item_txt_field.getText().length() > 1) {
							// update item stock
							String[] str_arr = selected_item_txt_field.getText().split(",");

							for (String str : str_arr) {
								char[] item_str = str.toCharArray();
								int index = (int) (item_str[0] - 65) * 3 + Character.getNumericValue(item_str[1]);
								item_array.get(index).remove_one_item();
							}

							cost_txt_field.setText("$0.00");
							item_txt_field.setText("");
							selected_item_txt_field.setText("");
							total_cost_txt_field.setText("$0.00");
						}

					} else {
						// if the transaction fails
						JOptionPane.showMessageDialog(null, "Declined! Please try again!");
					}
					credit_btn.setText("Credit");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Sorry! Unknown Error, please try again!");
				}

				cash_btn.setEnabled(true);
			}
		}
	}

	/**
	 * Takes care of when the add item button is clicked
	 * 
	 * @author Brandon Parker
	 *
	 */
	public class add_item_button_listener implements ActionListener {

		/**
		 * After the add item button is click, this function checks if the item chosen
		 * is valid and if there are either any in stock or if they have they have the
		 * last of the items already selected.
		 *
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (item_txt_field.getText().length() == 2) {
				char[] item_str = item_txt_field.getText().toCharArray();
				int index = (int) (item_str[0] - 65) * 3 + Character.getNumericValue(item_str[1]);

				// check if its a valid choice

				if (Character.isLetter(item_str[0]) && Character.isDigit(item_str[1]) && index < item_array.size()
						&& Character.getNumericValue(item_str[1]) <= vend_item_width) {
					String[] str_arr = selected_item_txt_field.getText().split(",");
					int total = 1;

					for (String str : str_arr) {

						if (str.compareTo(item_txt_field.getText()) == 0) {
							total++;
						}
					}
					// checks if the item is either in stock or the user has selected them all
					// already
					if (item_array.get(index).stock_available() && item_array.get(index).getStock() >= total) {

						// decides whether or not to add a comma for visual appeal
						if (selected_item_txt_field.getText().length() == 0) {
							selected_item_txt_field.setText(item_txt_field.getText());
						} else {
							String str = selected_item_txt_field.getText();
							selected_item_txt_field.setText(str + "," + item_txt_field.getText());
						}

						// updates the total cost after adding a new item
						double cost = Double.parseDouble(cost_txt_field.getText().substring(1));
						double total_cost = Double.parseDouble(total_cost_txt_field.getText().substring(1));
						total_cost += cost;

						total_cost_txt_field.setText(String.format("$ %.2f", total_cost));

					} else if (item_array.get(index).getStock() == 0) {
						// if the user selects and item that is currently our of stock
						JOptionPane.showMessageDialog(null, "Sorry, Out of stock!");
					} else {
						// if the user already has selected the remaining items in stock
						JOptionPane.showMessageDialog(null,
								"You already have the remiaing amount of this item selected.");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Invalid Input");
				}
			} else {
				JOptionPane.showMessageDialog(null, "Invalid Input");
			}
		}
	}

	/**
	 * Takes care of when the Cancel button is clicked
	 * 
	 * @author Brandon Parker
	 *
	 */
	public class cancel_button_listener implements ActionListener {

		/**
		 * Resets everything if the user clicks cancel
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			cost_txt_field.setText("$0.00");
			item_txt_field.setText("");
			selected_item_txt_field.setText("");
			total_cost_txt_field.setText("$0.00");

		}
	}

}
