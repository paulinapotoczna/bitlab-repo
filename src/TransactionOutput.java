public class TransactionOutput {
	public String id;
	public PublicKey reciepient;//klucz publiczny odbiorcy
	public float value; 
	public String parentTransactionId; //id transakcji z ktorej pochodzi 
	
	public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = StringUtil.toHex(reciepient.key+Float.toString(value)+parentTransactionId);
	}
	
	//czy bitcoiny naleza do danego klucza publicznego
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == reciepient);
	}
	
	
}