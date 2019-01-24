import java.util.ArrayList;

public class Transaction {
	
	public String transactionId; //zawiera hash transakcji
	public PublicKey sender; 
	public PublicKey reciepient;
	public float value; 
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	private static int sequence = 0; //przybli¿ona liczba transakcji
	
	public Transaction(PublicKey from, PublicKey to, float value,  ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}
	//przetwarzanie transakcji
	public boolean processTransaction() 
	{	
		//pobiera dane wejœciowe transakcji (upewniaj¹c, ¿e s¹ niewydane)
		for(TransactionInput i : inputs) {
			i.UTXO = Manage.UTXOs.get(i.transactionOutputId);
		}

		if(getInputsValue() < Manage.minimumTransaction) {
			System.out.println("Zbyt ma³a wartoœæ: " + getInputsValue());
			System.out.println("Wartoœæ musi byæ wiêksza ni¿: " + Manage.minimumTransaction);
			return false;
		}
		
		//generowanie wyników transakcji
		float leftOver = getInputsValue() - value; //pobierz wartoœæ inputs, a nastêpnie zostaw resztê
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