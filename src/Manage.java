import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class Manage {

	public static Map<String, User> users_ = new HashMap<String, User>();

	//cd C:\Users\Paulina\eclipse-workspace\Bitlab\src                     --> Bitlab/src 	
	//javac Bitlab.java
	//java Bitlab 239.0.0.0 1234 <--przyklad
	
	public static Transaction genesisTransaction;
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
	public static int difficulty = 3;// tutaj
	public static float minimumTransaction = 0.1f;// w klasie transaction
	public static Wallet walletA;
	public static Wallet walletB;
	public static Wallet walletC;
	static Scanner sc = new Scanner(System.in);

//tworzenie na sztywno uzytkownikow i ich portfeli
	public static void addUsers() {

		walletA = new Wallet("123", "321");
		walletB = new Wallet("456", "654");
		walletC = new Wallet("789", "987");
		//System.out.println("Prywatny: "+walletA.privateKey.getPrivateKey());
		//System.out.println("Publiczny: "+walletA.publicKey.getPublicKey());
		Wallet coinbase = new Wallet();

		//pocz¹tkowa transakcja, ktora odpowiedzialna jest za wyslanie 100 bitcoinow
		genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);

		genesisTransaction.transactionId = "0"; //reczne ustawienie id transakcji
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value,
				genesisTransaction.transactionId)); // reczne dodanie do wyjsciowych operacji
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); 
		//konieczne jest przechowywanie hasha pierwszej transakcji

		System.out.println("Tworzenie pocz¹tkowej transakcji i bloku...");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);

		users_.put("jan123", new User("jan123", "jan123", walletA));
		users_.put("tom123", new User("tom123", "tom123", walletB));
		users_.put("kasia123", new User("kasia123", "kasia123", walletC));

		// testing
		System.out.println(sendFunds2(users_.get("jan123"), users_.get("tom123"), 40f));
		//System.out.println(sendFunds2(users_.get("tom123"), users_.get("jan123"), 20f));
		//System.out.println(sendFunds2(users_.get("jan123"), users_.get("tom123"), 5f));

		System.out.println("\n\n\n\n\n");
	}

	public static String doSomething() {
		String[] command = new String[3];
		System.out.println("Funkcja('w' - wyslij bitcoiny innemu uzytkownikowi | 's' - sprawdz swoje saldo"
				+ " | 'wt' - sprawdz wyslane transakcje | 'wth' - sprawdz hash'e wyslanych transakcji |  'ot' - sprawdz otrzymane transakcje | 'q' - wyjdz z programu): ");
		command[0] = sc.nextLine();
		if (command[0].equals("w")) {
			System.out.println("Nazwa uzytkownika: \n");
			command[1] = sc.nextLine();
			System.out.println("Kwota: \n");
			command[2] = sc.nextLine();
			Bitlab.flag_send = true;
			return sendFunds(Bitlab.user_logged_in, users_.get(command[1]), Float.parseFloat(command[2]));
		}
		if (command[0].equals("s")) {
			checkBalance(Bitlab.user_logged_in);
		}

		if (command[0].equals("q")) {
			return "Exit";
		}
		if (command[0].equals("wt")) {
			Set<Entry<Float, String>> hashSet = Bitlab.user_logged_in.send_transaction.entrySet();
			System.out.println("Wys³ane transakcje");
			if(hashSet.isEmpty() == true) System.out.println("Brak.");
			for (Entry entry : hashSet) {

				System.out.println("Kwota operacji: " + entry.getKey() + " Odbiorca: " + entry.getValue());
			}
		}
		if (command[0].equals("ot")) {
			Set<Entry<Float, String>> hashSet = Bitlab.user_logged_in.get_transaction.entrySet();
			System.out.println("Otrzymane transakcje");
			if(hashSet.isEmpty() == true) System.out.println("Brak.");
			for (Entry entry : hashSet) {

				System.out.println("Kwota operacji: " + entry.getKey() + " Nadawca: " + entry.getValue());
			}
		}
		if (command[0].equals("wth")) {
			Set<Entry<Float, String>> hashSet = Bitlab.user_logged_in.send_transaction_hash.entrySet();
			System.out.println("Wys³ane transakcje i ich hash");
			if(hashSet.isEmpty() == true) System.out.println("Brak.");
			for (Entry entry : hashSet) {

				System.out.println("Kwota operacji: " + entry.getKey() + " Hash: " + entry.getValue());
			}
			/*Set<Entry<String, TransactionOutput>> hashSet2 = Bitlab.user_logged_in.getWallet().UTXOs.entrySet();
			System.out.println("Wys³ane transakcje i ich hash");
			if(hashSet2.isEmpty() == true) System.out.println("Brak.");
			for (Entry entry : hashSet2) {

				System.out.println("Hash: " + entry.getKey() + " Transakcja: " + entry.getValue());
			}*/
		}
		return "";
	}

	public static void updateBalance(String command) {
		int[] positions = new int[3];
		int counter = 0;
		// wyslal user1.login user2.login value
		if (command.contains("wyslal")) {
			// szukanie wszystkich 3 spacji w lancuchu command
			for (int i = 0; i < command.length(); i++) {
				if (command.charAt(i) == ' ' && counter != 3) {
					positions[counter] = i;
					counter++;
				}
			}
			String login_send = command.substring(positions[0] + 1, positions[1]);// pomiedzy 1 spacja a 2 spacja
			String login_receiver = command.substring(positions[1] + 1, positions[2]);// pomiedzy 1 spacja a 2 spacja
			String value = command.substring(positions[2] + 1, command.length());
			if (Bitlab.flag_send == false) {
				sendFunds(users_.get(login_send), users_.get(login_receiver), Float.parseFloat(value));
				Bitlab.user_logged_in.get_transaction.put(Float.parseFloat(value), users_.get(login_send).getLogin());
			} else {
				Bitlab.flag_send = false;
			}
		}
	}

	public static String sendFunds2(User user1, User user2, float value) {
		int size = blockchain.size();
		Block blockOld = blockchain.get(size - 1);
		Block blockNew = new Block(blockOld.hash);
		// System.out.println("STARY: " + blockOld.hash);
		// System.out.println("NOWY: " + blockNew.hash);
		//System.out.println(user1.login + " wysy³a " + value + " do " + user2.login);
		// wyslal user1.login user2.login value
		String s = "wyslal " + user1.getLogin() + " " + user2.getLogin() + " " + value;
		blockNew.addTransaction(user1.getWallet().sendFunds(user2.getWallet().publicKey, value));
		addBlock(blockNew);
	//	public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId)
	//	UTXOs.put(blockNew.hash, new TransactionOutput(user2.getWallet().publicKey, value, blockOld.hash)); 		
		return s;
	}
	
	public static String sendFunds(User user1, User user2, float value) {
		int size = blockchain.size();
		Block blockOld = blockchain.get(size - 1);
		Block blockNew = new Block(blockOld.hash);
		// System.out.println("STARY: " + blockOld.hash);
		// System.out.println("NOWY: " + blockNew.hash);
		//System.out.println(user1.login + " wysy³a " + value + " do " + user2.login);
		// wyslal user1.login user2.login value
		String s = "wyslal " + user1.getLogin() + " " + user2.getLogin() + " " + value;
		blockNew.addTransaction(user1.getWallet().sendFunds(user2.getWallet().publicKey, value));
		addBlock(blockNew);
		// poprawic zapisywanie transakcji wysylanych! tylko dla jednego uzytkownika
		if(Bitlab.user_logged_in.getLogin() != user2.getLogin()) 
		{
			Bitlab.user_logged_in.send_transaction.put(value, user2.getLogin());
			Bitlab.user_logged_in.send_transaction_hash.put(value, blockOld.hash);
		//	UTXOs.put(blockNew.hash, new TransactionOutput(user2.getWallet().publicKey, value, blockOld.hash)); 
		}
		return s;
	}

	public static void checkBalance(User user1) {
		System.out.println("\nTwoje saldo " + user1.getLogin() + ": " + user1.getWallet().getBalance());
	}

	public static boolean isExistLogin(String login) {
		User u;
		u = users_.get(login);// get user by login

		if (u != null)
			return true;
		System.out.print("Nie istnieje taki u¿ytkownik!");
		return false;
	}

	public static boolean isValidPassword(String login, String password) {
		User u;
		u = users_.get(login);// get user by login

		if (u.getPassword().equals(password) == false) {
			System.out.print("Z³e has³o u¿ytkownika! Spróbuj jeszcze raz.");
			return false;
		}
		System.out.println("Zalogowano. Witaj " + u.getLogin() + "!");
		return true;
	}

	public static User getUser(String login) {
		return users_.get(login);
	}

	public static void addBlock(Block newBlock) {
		newBlock.doBlock(difficulty);
		blockchain.add(newBlock);
	}
}