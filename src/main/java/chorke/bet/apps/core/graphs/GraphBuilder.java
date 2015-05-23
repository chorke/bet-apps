
package chorke.bet.apps.core.graphs;

import chorke.bet.apps.core.CoreUtils.BetPossibility;
import chorke.bet.apps.core.calculators.Yield;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chorke
 */
public abstract class GraphBuilder <T extends Yield> {
    
    protected static final Logger LOG = LoggerFactory.getLogger(GraphBuilder.class);
    
//    /**
//     * Vytvorí graf pre zvolenú možnosť stávky a rozsah podľa indexu.
//     * 
//     * @param yields
//     * @param betPossibility
//     * @param index
//     * @return 
//     */
//    public abstract Graph getGraph(Map<Calendar, T> yields, BetPossibility betPossibility, int index);
    
    /**
     * Vytvorí graf pre zvolenú možnosť stávky a kľúča.
     * 
     * @param yields
     * @param betPossibility
     * @param idxOnScale 
     * @param scales  
     * @return 
     */
    public abstract Graph getGraph(Map<Calendar, T> yields, BetPossibility betPossibility,
            int idxOnScale, List<BigDecimal> scales);
    
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
