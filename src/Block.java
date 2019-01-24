import java.util.ArrayList;
import java.util.Date;

public class Block {

	public String hash;
	public String previousHash;
	public String merkleRoot;//drzewo hash
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	public long timeStamp; // milisekundy 1/1/1970.
	public int nonce;

	public Block(String previousHash) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash(); 
	}
	
	public String toString() {
		return hash;
	}

	// Obliczanie nowego hash'a na podstawie zawartoœci bloków
	public String calculateHash() {
		String calculatedhash = StringUtil
				.toHex(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
		//System.out.println(calculatedhash);
		return calculatedhash;
	}

	public void doBlock(int difficulty) {
		merkleRoot = StringUtil.getMerkleRoot(transactions);
		nonce++;
		hash = calculateHash();
		//System.out.println("Blok: " + hash);
	}


	//dodanie transakcji do bloku
	public boolean addTransaction(Transaction transaction) 
	{
		if (transaction == null)
			return false;//false jest nie zostala pomyslne dodana do bloku
		if ((!"0".equals(previousHash))) {
			if ((transaction.processTransaction() != true)) {
				System.out.println("Transakcja siê nie powiod³a. Odrzucono.");
				return false;
			}
		}
		transactions.add(transaction);
		System.out.println("Transakcja zosta³a dodana do bloku.");
		return true;
	}
}