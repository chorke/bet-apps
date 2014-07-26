
package chorke.proprietary.bet.apps.core.graphs;

import chorke.proprietary.bet.apps.core.CoreUtils.BetPossibility;
import chorke.proprietary.bet.apps.core.Tuple;
import chorke.proprietary.bet.apps.core.calculators.Yield;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Map;

/**
 *
 * @author Chorke
 */
public abstract class GraphBuilder <T extends Yield> {
    
    /**
     * Vytvorí graf pre zvolenú možnosť stávky a rozsah podľa indexu.
     * 
     * @param yields
     * @param betPossibility
     * @param index
     * @return 
     */
    public abstract Graph getGraph(Map<Calendar, T> yields, BetPossibility betPossibility, int index);
    
    /**
     * Vytvorí graf pre zvolenú možnosť stávky a podľa rozsahu range.
     * 
     * @param yields
     * @param betPossibility
     * @param range
     * @return 
     */
    public abstract Graph getGraph(Map<Calendar, T> yields, BetPossibility betPossibility,
            Tuple<BigDecimal, BigDecimal> range);
    
    /**
     * Vytvorí graf z predaných argumentov. Poradie hodnôt v grafe je rovnaké
     * ako poradie vracané iterátorom {@code yields.keySet().iterator}.
     * 
     * @param yields
     * @return 
     */
    protected Graph getGraphInner(Map<Calendar, BigDecimal> yields){
        Graph g = new Graph();
        for(Calendar c : yields.keySet()){
            g.add(yields.get(c));
        }
        return g;
    }
}
