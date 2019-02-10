import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TxHandler {
    public UTXOPool UP;
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        // IMPLEMENT THIS
        UP = utxoPool;
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        // IMPLEMENT THIS
        //(1)
        for (Transaction.Input in: tx.getInputs()) {
            UTXO utxo = new UTXO(in.prevTxHash, in.outputIndex);
            if (!UP.contains(utxo)) {
                return false;
            }
        }
        //(2)
        int index = 0;
        for (Transaction.Input in: tx.getInputs()) {
            UTXO utxo = new UTXO(in.prevTxHash, in.outputIndex);
            PublicKey adr = UP.getTxOutput(utxo).address;
            if (!Crypto.verifySignature(adr ,tx.getRawDataToSign(index++),in.signature)) {
                return false;
            }
        }

        //(3)
        ArrayList<UTXO> utxos = UP.getAllUTXO();

        for (Transaction.Input in: tx.getInputs()) {
            UTXO utxo = new UTXO(in.prevTxHash, in.outputIndex);
            if (utxos.contains(utxo)) {
                utxos.remove(utxo);
            } else {
                return false;
            }
        }

        //(4)
        for (Transaction.Output op: tx.getOutputs()) {
            if (op.value < 0) {
                return false;
            }
        }
        //(5)
        double sumInputs = 0, sumOutputs = 0;
        for (Transaction.Output op: tx.getOutputs()) {
            sumOutputs += op.value;
        }

        for (Transaction.Input in: tx.getInputs()) {
            UTXO utxo = new UTXO(in.prevTxHash, in.outputIndex);
            double val = UP.getTxOutput(utxo).value;
            sumInputs += val;
        }

        if (sumInputs < sumOutputs) {
            return false;
        }

        return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
        ArrayList<Transaction> txs = new ArrayList<Transaction>();
        for (Transaction tx: possibleTxs) {
            if (isValidTx(tx)) {
                txs.add(tx);

                for (Transaction.Input in: tx.getInputs()) {
                    UTXO utxo = new UTXO(in.prevTxHash, in.outputIndex);
                    UP.removeUTXO(utxo);

                }
                int index = 0;
                for (Transaction.Output op: tx.getOutputs()) {
                    UTXO utxo = new UTXO(tx.getHash(), index++);
                    UP.addUTXO(utxo, op);
                }
            }
        }

        Transaction[] accepted;
        accepted = new Transaction[txs.size()];

        for (int i = 0; i < txs.size(); ++i) {
            accepted[i] = txs.get(i);
        }

        return accepted;
    }

}
