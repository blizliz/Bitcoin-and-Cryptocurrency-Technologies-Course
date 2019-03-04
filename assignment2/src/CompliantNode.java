import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {
    private boolean[] followees;
    private double p_graph; // parameter for random graph: prob. that an edge will exist
    private double p_malicious; // prob. that a node will be set to be malicious
    private double p_txDistribution; // probability of assigning an initial transaction to each node
    private int numRounds;
    private Set<Transaction> allHeard;
    private int startIgnore;

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        // IMPLEMENT THIS
        this.p_graph = p_graph;
        this.p_malicious = p_malicious;
        this.p_txDistribution = p_txDistribution;
        this.numRounds = numRounds;
    }

    public void setFollowees(boolean[] followees) {
        this.followees = Arrays.copyOf(followees, followees.length);
        // IMPLEMENT THIS
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        // IMPLEMENT THIS
        allHeard = new HashSet<Transaction>(pendingTransactions);
    }

    public Set<Transaction> sendToFollowers() {
        // IMPLEMENT THIS
        return allHeard;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        // IMPLEMENT THIS
        for (Candidate candidate: candidates) {
            if (!allHeard.contains(candidate.tx)) {
                allHeard.add(candidate.tx);
            }
        }
        --numRounds;
    }
}
