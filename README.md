# 💰 JoshChain – A Java-Based Blockchain & Cryptocurrency Prototype

Welcome to **JoshChain**, a minimal blockchain implementation in Java designed to help understand how cryptocurrencies like Bitcoin work — from transactions and wallets to blocks and proof-of-work mining.

This project was built by following a hands-on tutorial series and customizing key components along the way to deepen understanding of blockchain internals using core Java.

---

## 🚀 Features

- ✅ Blockchain structure with linked `Block` objects
- ⛓ SHA-256 hashing for block integrity
- ⛏ Proof-of-work mining (adjustable difficulty)
- 🔐 Wallets with ECDSA key pairs (via Bouncy Castle)
- ✍️ Digitally signed transactions using private keys
- 🔁 Unspent Transaction Output (UTXO) model
- 💳 Realistic transaction creation, signing, and verification
- 🧾 Transaction inputs & outputs for verifiable coin movement
- 📦 Blocks hold multiple transactions (Merkle root concept simulated)
- 🧪 Full blockchain validation: hash consistency, signature verification, and input legitimacy

---

## 🛠️ Technologies & Tools Used

| Tool/Library       | Purpose                                 |
|--------------------|------------------------------------------|
| Java (JDK 11+)      | Core language used to build the system   |
| Bouncy Castle       | ECDSA cryptographic signatures           |
| GSON (Google)       | For JSON serialization (debugging view) |
| Eclipse / IntelliJ  | Java IDE (optional)                     |

---

## 📁 Folder Structure

/src
├── NoobChain.java // Main blockchain driver
├── Block.java // Block structure and hash logic
├── Wallet.java // Wallet class with ECDSA keys
├── Transaction.java // Main transaction logic
├── TransactionInput.java // References to past outputs (UTXOs)
├── TransactionOutput.java // Output of a transaction (like coins)
└── StringUtil.java // Cryptographic helper functions
---

## 📚 What I Learned

### 🔸 Java Fundamentals:
- OOP principles (encapsulation, constructors, static methods)
- Collections API: `HashMap`, `ArrayList`, `Map.Entry`
- Java Cryptography API (`KeyPair`, `Signature`, `MessageDigest`)
- Custom utility methods and byte array manipulation

### 🔸 Blockchain Concepts:
- Hash chaining and data immutability
- Proof-of-work mining using nonce and SHA-256
- UTXO model for coin ownership
- Public/private key cryptography
- Signature generation and verification
- Transaction flow: inputs, outputs, and balance tracking
- Blockchain validation and tamper detection

---

## 🧠 How It Works (In Short)

1. **Wallets** generate ECDSA key pairs and can sign transactions.
2. Transactions contain:
   - Sender & recipient public keys
   - Amount to transfer
   - Inputs referencing previous outputs
   - Outputs defining new UTXOs
   - Digital signature
3. Blocks store validated transactions and are mined using a proof-of-work algorithm.
4. Each block contains:
   - A cryptographic hash
   - A reference to the previous block’s hash
   - A simulated Merkle root of transactions
5. Blockchain validity is checked by:
   - Recalculating and comparing hashes
   - Verifying digital signatures
   - Ensuring UTXOs are correctly spent and updated

---

## 🧪 Example Usage (In `NoobChain.java`)

```java
Wallet walletA = new Wallet();
Wallet walletB = new Wallet();

Transaction genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
Block genesis = new Block("0");
genesis.addTransaction(genesisTransaction);
blockchain.add(genesis);

Block block1 = new Block(genesis.hash);
block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
blockchain.add(block1);

✅ To Run the Project

    Clone or download the repo

    Ensure Java (JDK 11+) is installed

    Add BouncyCastle and GSON to your project classpath

    Run NoobChain.java as your entry point

📌 Notes

    This is a proof-of-concept, not a production-ready cryptocurrency.

    The goal is education — to learn how blockchain works under the hood.

    Many features (e.g., peer-to-peer networking, mempool, Merkle trees) are simplified or mocked.

🙌 Acknowledgements

    Based on the "NoobChain" tutorial series by Kass

    Extended, documented, and explained with detailed Java learning focus
