import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
	//do podpisywania naszych transakcji
	public PrivateKey privateKey;
	
	//adres do udostepniania 
	public PublicKey publicKey;
	
	//Wysy³amy równie¿ nasz klucz publiczny wraz z transakcj¹ i mo¿emy go u¿yæ do
	//sprawdzenia, czy nasz podpis jest wa¿ny, a dane nie zosta³y zmienione.
	
	//Hashmapa do przechowywania wychodzacych transakcji
	public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

	public Wallet(String publicKey, String privateKey) {

		this.privateKey = new PrivateKey(privateKey);
		this.publicKey = new PublicKey(publicKey);
	}

	public Wallet() {
		generateKeyPair();
	}

	public void generateKeyPair() {
		try {
			Thread.sleep((long) 100);// odczekanie 100 milisekund by klucze sie roznily
			int data = (int) new Date().getTime();
			privateKey = new PrivateKey(Integer.toString(data));
			publicKey = new PublicKey(Integer.toString(data + 123));
			// System.out.println("Prywatny klucz: " + privateKey.key + " Publiczny klucz: " + publicKey.key);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	//pobranie danych dotyczacych salda // sprawdzenie czy dana transakcja nalezy do nas
	public float getBalance() {
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item : Manage.UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			if (UTXO.isMine(publicKey)) { //sprawdzanie na podstawie klucza publicznego
				UTXOs.put(UTXO.id, UTXO); //dodanie do niewydanych transakcjiS
				total += UTXO.value;
			}
		}
		return total;
	}

	public Transaction sendFunds(PublicKey recipient, float value) {
		if (getBalance() < value) {
			System.out.println("Zbyt ma³o funduszy. Transakcja odrzucona.");
			return null;
		}
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			total += UTXO.value;
			inputs.add(new TransactionInput(UTXO.id));
			if (total > value)
				break;
		}
		Transaction newTransaction = new Transaction(publicKey, recipient, value, inputs);

		for (TransactionInput input : inputs) {
			UTXOs.remove(input.transactionOutputId);
		}
		return newTransaction;
	}
}