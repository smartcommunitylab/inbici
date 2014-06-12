package eu.iescities.pilot.rovereto.inbici.utils.frechetdistance.algo.envelope;
/**
 * Interface of the upper envelope data structure as required by the framework
 * of the frechetdistance.algo.FrechetDistance class.
 *
 * @author Wouter Meulemans (w.meulemans@tue.nl)
 */
public interface UpperEnvelope {
    
    public void add(int i, double[] P1, double[] P2, double[] Q);
    public void removeUpto(int i);
    public void clear();
    
    public double findMinimum(double... constants);
    
    public void truncateLast();
}
