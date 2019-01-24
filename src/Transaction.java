import java.util.ArrayList;

public class Transaction {
	
	public String transactionId; //zawiera hash transakcji
	public PublicKey sender; 
	public PublicKey reciepient;
	public float value; 
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	private static int sequence = 0; //przybli�ona liczba transakcji
	
	public Transaction(PublicKey from, PublicKey to, float value,  ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}
	//przetwarzanie transakcji
	public boolean processTransaction() 
	{	
		//pobiera dane wej�ciowe transakcji (upewniaj�c, �e s� niewydane)
		for(TransactionInput i : inputs) {
			i.UTXO = Manage.UTXOs.get(i.transactionOutputId);
		}

		if(getInputsValue() < Manage.minimumTransaction) {
			System.out.println("Zbyt ma�a warto��: " + getInputsValue());
			System.out.println("Warto�� musi by� wi�ksza ni�: " + Manage.minimumTransaction);
			return false;
		}
		
		//generowanie wynik�w transakcji
		float leftOver = getInputsValue() - value; //pobierz warto�� inputs, a nast�pnie zostaw reszt�
		transactionId = calculateHash();
		outputs.add(new TransactionOutput( this.reciepient, value,transactionId)); //wyslanie wartosci do odbiorcy
		outputs.add(new TransactionOutput( this.sender, leftOver,transactionId)); //zmiana wartosci o leftover		
				
		//niewydane bitcoiny
		for(TransactionOutput o : outputs) {
			Manage.UTXOs.put(o.id , o);
		}
		
		//usuwanie wydanych bitcoinow
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue; //jesli nie ma transakcji to pomin
			Manage.UTXOs.remove(i.UTXO.id);
		}
		
		return true;
	}
	
	public float getInputsValue() {
		float total = 0;
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue; //jesli nie ma transakcji to pomin
			total += i.UTXO.value;
		}
		return total;
	}
	
	public float getOutputsValue() {
		float total = 0;
		for(TransactionOutput o : outputs) {
			total += o.value;
		}
		return total;
	}
	
	private String calculateHash() {
		sequence++;//zwieksza o jedna wartosc jesli hashe transakcji sa identyczne
		return StringUtil.toHex(sender.key +reciepient.key +Float.toString(value) + sequence);
	}
}