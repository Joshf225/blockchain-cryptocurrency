package joshchain;

import java.security.*;
import java.util.ArrayList;

public class Transaction {
  public String transactionId;
  public PublicKey sender;
  public PublicKey reciepient;
  public float value;
  public byte[] signature;

  public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
  public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

  private static int sequence = 0;

  public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
    this.sender = from;
    this.reciepient = to;
    this.value = value;
    this.inputs = inputs;
  }

  // This Calculates the transaction hash (which will be used as its Id)
	private String calulateHash() {
		sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
		return StringUtil.applySha256(
				StringUtil.getStringFromKey(sender) +
				StringUtil.getStringFromKey(reciepient) +
				Float.toString(value) + sequence
				);
	}

  //Signs all the data we dont wish to be tampered with.
  public void generateSignature(PrivateKey privateKey) {
    String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
    signature = StringUtil.applyECDSASig(privateKey,data);		
  }
  //Verifies the data we signed hasnt been tampered with
  public boolean verifySignature() {
    String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
    return StringUtil.verifyECDSASig(sender, data, signature);
  }

  //Returns true if new transaction could be created
  public boolean processTransaction() {
    if(verifySignature() == false) {
      System.out.println("#Transaction Signature failed to verify");
      return false;
    }

    //gather transaction inputs (Make sure they are unspent
    for(TransactionInput i : inputs) {
      i.UTXO = JoshChain.UTXOs.get(i.transactionOutputId);
    }

    //check if transaction is valid:
    if(getInputsValue() < JoshChain.minimumTransaction) {
      System.out.println("#Transaction Inputs to small: " + getInputsValue());
      return false;
    }
    
    float leftOver = getInputsValue() - value;
    transactionId = calulateHash();
    outputs.add(new TransactionOutput(this.reciepient, value, transactionId));
    outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

    //add outputs to Unspent list
    for(TransactionOutput o : outputs) {
      JoshChain.UTXOs.put(o.id , o);
    }

    //remove transaction inputs from UTXO lists as spent:
    for(TransactionInput i : inputs) {
      if(i.UTXO == null) continue; //if Transaction cant be found skip it
      JoshChain.UTXOs.remove(i.UTXO.id);
    }

    return true;
  }

//returns sum of inputs(UTXOs) values
	public float getInputsValue() {
		float total = 0;
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue; //if Transaction can't be found skip it 
			total += i.UTXO.value;
		}
		return total;
	}

//returns sum of outputs:
	public float getOutputsValue() {
		float total = 0;
		for(TransactionOutput o : outputs) {
			total += o.value;
		}
		return total;
	}
}
