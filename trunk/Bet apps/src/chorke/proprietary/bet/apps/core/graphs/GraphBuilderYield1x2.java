
package chorke.proprietary.bet.apps.core.graphs;

import chorke.proprietary.bet.apps.StaticConstants.BetPossibility;
import chorke.proprietary.bet.apps.core.Tuple;
import chorke.proprietary.bet.apps.core.calculators.Yield1x2;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Chorke
 */
public class GraphBuilderYield1x2 extends GraphBuilder<Yield1x2>{

    @Override
    public Graph getGraph(Map<Calendar, Yield1x2> yields, BetPossibility betPossibility, int index) {
        Map<Calendar, BigDecimal> yieldsForInner = new LinkedHashMap<>();
        List<Calendar> keys = new LinkedList<>(yields.keySet());
        Collections.sort(keys);
        for(Calendar c : keys){
            yieldsForInner.put(c, yields.get(c).getYieldForScaleIndex(betPossibility, index, true));
        }
        return getGraphInner(yieldsForInner);
    }

    @Override
    public Graph getGraph(Map<Calendar, Yield1x2> yields, BetPossibility betPossibility,
                Tuple<BigDecimal, BigDecimal> range) {
        Map<Calendar, BigDecimal> yieldsForInner = new LinkedHashMap<>();
        List<Calendar> keys = new LinkedList<>(yields.keySet());
        Collections.sort(keys);
        for(Calendar c : keys){
            yieldsForInner.put(c, yields.get(c).getYieldForScaleRange(betPossibility, range, true));
        }
        return getGraphInner(yieldsForInner);
    }
}
