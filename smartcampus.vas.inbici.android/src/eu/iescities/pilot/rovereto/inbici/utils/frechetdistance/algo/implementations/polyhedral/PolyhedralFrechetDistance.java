package eu.iescities.pilot.rovereto.inbici.utils.frechetdistance.algo.implementations.polyhedral;
import eu.iescities.pilot.rovereto.inbici.utils.frechetdistance.algo.FrechetDistance;
import eu.iescities.pilot.rovereto.inbici.utils.frechetdistance.algo.envelope.UpperEnvelope;
import eu.iescities.pilot.rovereto.inbici.utils.frechetdistance.algo.util.PolyhedralDistanceFunction;

/**
 * Computing the Frechet distance under polyhedral distances.
 * Constructor requires a polyhedral distance function to be specified.
 * 
 * @author Wouter Meulemans (w.meulemans@tue.nl)
 */
public class PolyhedralFrechetDistance extends FrechetDistance {

    protected PolyhedralDistanceFunction distfunc;

    public PolyhedralFrechetDistance(PolyhedralDistanceFunction distfunc) {
        this.distfunc = distfunc;
    }

    @Override
    public double distance(double[] p, double[] q) {
        return distfunc.getDistance(p, q);
    }

    @Override
    protected UpperEnvelope initializeRowUpperEnvelope(int row) {
        return new PolyhedralUpperEnvelope(distfunc, Q[row], Q[row + 1]);
    }

    @Override
    protected UpperEnvelope initializeColumnUpperEnvelope(int column) {
        return new PolyhedralUpperEnvelope(distfunc, P[column], P[column + 1]);
    }
}
