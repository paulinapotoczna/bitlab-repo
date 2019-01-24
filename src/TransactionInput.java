public class TransactionInput {
	public String transactionOutputId; //referencja do idoutputtransaction
	public TransactionOutput UTXO; //dostepne fundusze
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}
}