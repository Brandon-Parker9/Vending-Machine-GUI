package cp213;

/**
 * VendItem object to help keep track of each items name, cost and stock
 * 
 * @author Brandon Parker
 *
 */
public class VendItem {

	private String item_name;
	private double cost;
	private int stock;

	/**
	 * Constructor
	 * 
	 * @param item_name String of the item name
	 * @param cost      double regarding the cost of item
	 * @param stock     int regarding the quantity in the machine
	 */
	public VendItem(String item_name, double cost, int stock) {
		this.item_name = item_name;
		this.cost = cost;
		this.stock = stock;

	}

	/**
	 * Getter
	 * 
	 * @return item_name, the name of the item
	 */
	public String getItem_name() {
		return item_name;
	}

	@Override
	public String toString() {
		return "VendItem [item_name=" + item_name + ", cost=" + cost + ", stock=" + stock + "]";
	}

	/**
	 * Getter
	 * 
	 * @return cost, the cost of the item
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * Setter - set the cost of the item
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}

	/**
	 * Getter
	 * 
	 * @return stock, the stock of the item
	 */
	public int getStock() {
		return stock;
	}

	/**
	 * Setter - set the stock of the item
	 */
	public void setStock(int stock) {
		this.stock = stock;
	}

	/**
	 * @return boolean if the the stock of the item is 0
	 */
	public boolean stock_available() {
		return this.stock > 0;
	}

	/**
	 * Set the item stock to sock - 1
	 */
	public void remove_one_item() {
		this.stock--;
	}
}
