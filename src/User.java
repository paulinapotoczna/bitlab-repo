import java.util.HashMap;
import java.util.Map;

public class User 
{
	public static Map<Float, String> get_transaction = new HashMap<Float, String>();
	public static Map<Float, String> send_transaction = new HashMap<Float, String>();
	public static Map<Float, String> get_transaction_hash = new HashMap<Float, String>();
	public static Map<Float, String> send_transaction_hash = new HashMap<Float, String>();

	private String login;
	private String password;
	private Wallet wallet;

	public User(String login, String password, Wallet wallet) {
		this.login = login;
		this.password = password;
		this.wallet = wallet;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Wallet getWallet() {
		return wallet;
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}

	public static void addSend(String user, float value) {
		send_transaction.put(value, user);
	}

	public static void addGet(String user, float value) {
		get_transaction.put(value, user);
	}
}
