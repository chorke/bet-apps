
package bet.apps.statistics;

import bet.apps.core.Graph;
import bet.apps.core.Match;
import bet.apps.core.PaintableGraph;
import bet.apps.filters.Filter;
import java.util.Collection;
import java.util.List;

/**
 * Every implementation must store matches for creating statistics and graphs.
 * 
 * 
 * @author Chorke
 */
public interface PeriodicStatistics {
    /**
     * Creates periodic groups based on {@code matches} and specific preriod.
     * Period depends on implementation of this interface.
     * 
     * @param matches matches to be splited
     */
    public void split(Collection<Match> matches);
    
    public void clearMatches();
    
    /**
     * Makes statistics for matches according to periodic groups and thresholds 
     * for bets. For every region between two thresholds is created one field and
     * every fiel contains {@code numberOfStats} statistics. Some implementations
     * could ignore this number.
     * 
     * @param bets thresholds - each field is one threshold
     */
    public void makePeriodicStatistics(double[] bets, int numberOfStats);
    
    /**
     * Creates graphs for periodic statistics.
     * @return 
     */
    public List<List<Graph>> getGraphs(double[] bets, int numberOfStats);
    
    public List<List<PaintableGraph>> getPaintableGraphs(double[] bets, int numberOfStats);
    
    public double getYield(int periodIndex, int field, int stats);
    
    public int getNumOfMatchesInFieldForPeriod(int periodIndex, int field);
    
    public int getNumOfMatchesInStastForPeriod(int periodIndex, int field, int stats);
    
    public void applyFilters(Collection<? extends Filter> filters);
    
    public void applyFilters(Collection<? extends Filter> filters, int startSize);
}
