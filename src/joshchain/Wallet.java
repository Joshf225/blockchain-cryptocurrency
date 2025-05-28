package joshchain;

/*
UTXOs = Unspent outputs = Wallet’s coins

Inputs = Claiming UTXOs to spend them

Total = Add up UTXOs until we have enough to send

Transactions = Proof of who sent how much to whom
*/

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

  public PrivateKey privateKey;
  public PublicKey publicKey;

  public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

  public ArrayList<Transaction> transactionHistory = new ArrayList<>();

  public Wallet(){
    generateKeyPair();
  }

  public void generateKeyPair() {
    try {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
      SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
      ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
      //Initialize the key generator and generate a KeyPair
      keyGen.initialize(ecSpec, random); //256 bytes provides an accesptable security level
      KeyPair keyPair = keyGen.generateKeyPair();
      //Set the public and private keys from the keyPair
      privateKey = keyPair.getPrivate();
      publicKey = keyPair.getPublic();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  //returns balance and stores the UTXO's owned by this wallet in this.UTXOs
  public float getBalance() {
    float total = 0;
    for (Map.Entry<String, TransactionOutput> item : JoshChain.UTXOs.entrySet()) {
      TransactionOutput UTXO = item.getValue();
      if(UTXO.isMine(publicKey)) { //if output belongs to me (if coins belong to me)
        total += UTXO.value; //add it to out list of unspent transactions.
      }
    }
    return total;
  }

  //Generates and returns a new transaction from this wallet.
  public Transaction sendFunds(PublicKey _reciepient, float value) {
    if(getBalance() < value) { //gather balance and check funds.
      System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
      return null;
    }

    ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

  // Looping through the wallet's unspent transaction outputs (UTXOs)
  // and accumulating value until we've gathered enough to cover the amount the user wants to send.
  float total = 0;
  for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
    TransactionOutput UTXO = item.getValue();

    // Add the UTXO's value to the running total.
    total += UTXO.value;

    // Create a TransactionInput using this UTXO's ID.
    // This input proves we own this output and intend to spend it.
    // Transactions serve as verifiable evidence of coin ownership and movement.
    inputs.add(new TransactionInput(UTXO.id));

    // Stop once we've gathered enough value (can be slightly more than needed; change will be handled later)
    if (total > value) break;
  }

    Transaction newTransaction = new Transaction(publicKey, _reciepient, value, inputs);
    newTransaction.generateSignature(privateKey);
    
    for(TransactionInput input: inputs) {
      UTXOs.remove(input.transactionOutputId);
    }
    //record the transaction to the transactionHistory ArrayList
    transactionHistory.add(newTransaction);
    
    return newTransaction;
  }

  public void printTransactionHistory() {
    System.out.println("Transaction history for wallet");
    for(Transaction tx: transactionHistory) {
      System.out.println("Sent "+tx.value+" to: "+StringUtil.getStringFromKey(tx.reciepient));
    }
  }

  //Tacks in array of transactions and returns a merkle root.
  public static String getMerkleRoot(ArrayList<Transaction> transactions) {
    int count = transactions.size();
    ArrayList<String> previousTreeLayer = new ArrayList<String>();
    for(Transaction transaction : transactions) {
      previousTreeLayer.add(transaction.transactionId);
    }
    ArrayList<String> treeLayer = previousTreeLayer;
    while(count > 1) {
      treeLayer = new ArrayList<String>();
      for(int i=1; i<previousTreeLayer.size(); i++) {
        treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
      }
      count = treeLayer.size();
      previousTreeLayer = treeLayer;
    }
    String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    return merkleRoot;
  }

}
