package eu.iescities.pilot.rovereto.inbici.utils.frechetdistance.algo;

import eu.iescities.pilot.rovereto.inbici.utils.frechetdistance.algo.deque.Deque;
import eu.iescities.pilot.rovereto.inbici.utils.frechetdistance.algo.deque.DequeItem;
import eu.iescities.pilot.rovereto.inbici.utils.frechetdistance.algo.envelope.UpperEnvelope;

/**
 * Generic class for the computation of the Frechet distance.
 * Abstracts from the upper envelope, which is distance-measure specific.
 *
 * @author Wouter Meulemans (w.meulemans@tue.nl)
 */
public abstract class FrechetDistance {

    protected int N, M;
    protected double[][] P, Q;

    public double computeDistance(double[][] P, double[][] Q) {

        this.P = P;
        this.Q = Q;

        this.N = P.length - 1;
        this.M = Q.length - 1;

        double dist = compute();

        this.P = null;
        this.Q = null;

        return dist;
    }

    private double compute() {
    	
    	
        Deque<DInt>[] column_queues = new Deque[N];
        UpperEnvelope[] column_envelopes = new UpperEnvelope[N];
        for (int i = 0; i < N; i++) {
            column_queues[i] = new Deque<DInt>();
            column_envelopes[i] = initializeColumnUpperEnvelope(i);
        }

        Deque<DInt>[] row_queues = new Deque[M];
        UpperEnvelope[] row_envelopes = new UpperEnvelope[M];
        for (int j = 0; j < M; j++) {
            row_queues[j] = new Deque<DInt>();
            row_envelopes[j] = initializeRowUpperEnvelope(j);
        }

        double[][] L_opt = new double[N][M];
        L_opt[0][0] = distance(P[0], Q[0]);
        for (int j = 1; j < M; j++) {
            L_opt[0][j] = Double.POSITIVE_INFINITY;
        }

        double[][] B_opt = new double[N][M];
        B_opt[0][0] = L_opt[0][0];
        for (int i = 1; i < N; i++) {
            B_opt[i][0] = Double.POSITIVE_INFINITY;
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {

                //System.out.println("Computing cell [" + i + "," + j + "]");
                if (i < N - 1) {
                    // compute L_opt[i+1][j]
                    //System.out.println("  L_opt[i+1][j]");

                    Deque<DInt> queue = row_queues[j];
                    UpperEnvelope upperenv = row_envelopes[j];

                    while (queue.getSize() > 0 && B_opt[queue.getLast().value][j] > B_opt[i][j]) {
                        queue.removeLast();
                    }
                    queue.addLast(new DInt(i));

                    if (queue.getSize() == 1) {
                        upperenv.clear();
                    }

                    upperenv.add(i + 1, Q[j], Q[j + 1], P[i + 1]);

                    int h = queue.getFirst().value;
                    double min = h < i ? upperenv.findMinimum(L_opt[i][j], B_opt[h][j]) : upperenv.findMinimum(B_opt[h][j]);

                    while (queue.getSize() > 1 && B_opt[queue.getFirst().getNext().value][j] <= min) {
                        queue.removeFirst();

                        h = queue.getFirst().value;
                        assert h <= i;
                        upperenv.removeUpto(h);

                        min = h < i ? upperenv.findMinimum(L_opt[i][j], B_opt[h][j]) : upperenv.findMinimum(B_opt[h][j]);
                    }

                    L_opt[i + 1][j] = min;
                    upperenv.truncateLast();
                }

                if (j < M - 1) {
                    // compute B_opt[i][j+1]
                    //System.out.println("  B_opt[i][j+1]");

                    Deque<DInt> queue = column_queues[i];
                    UpperEnvelope upperenv = column_envelopes[i];

                    while (queue.getSize() > 0 && L_opt[i][queue.getLast().value] >= L_opt[i][j]) {
                        queue.removeLast();
                    }
                    queue.addLast(new DInt(j));

                    if (queue.getSize() == 1) {
                        upperenv.clear();
                    }

                    upperenv.add(j + 1, P[i], P[i + 1], Q[j + 1]);

                    int h = queue.getFirst().value;
                    double min = h < j ? upperenv.findMinimum(B_opt[i][j], L_opt[i][h]) : upperenv.findMinimum(L_opt[i][h]);

                    while (queue.getSize() > 1 && L_opt[i][queue.getFirst().getNext().value] <= min) {
                        queue.removeFirst();

                        h = queue.getFirst().value;
                        assert h <= j;
                        upperenv.removeUpto(h);

                        min = h < j ? upperenv.findMinimum(B_opt[i][j], L_opt[i][h]) : upperenv.findMinimum(L_opt[i][h]);
                    }

                    B_opt[i][j + 1] = min;
                    upperenv.truncateLast();
                }
            }
        }

        return Math.max(distance(P[N], Q[M]), Math.min(L_opt[N - 1][M - 1], B_opt[N - 1][M - 1]));
    }

    public abstract double distance(double[] p, double[] q);

    protected abstract UpperEnvelope initializeRowUpperEnvelope(int row);

    protected abstract UpperEnvelope initializeColumnUpperEnvelope(int column);

    private class DInt extends DequeItem<DInt> {

        int value;

        public DInt(int value) {
            this.value = value;
        }
    }
}
