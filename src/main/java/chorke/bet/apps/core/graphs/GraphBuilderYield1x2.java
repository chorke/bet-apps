
package chorke.bet.apps.core.graphs;

import chorke.bet.apps.core.CoreUtils.BetPossibility;
import chorke.bet.apps.core.calculators.Yield1x2;
import chorke.bet.apps.core.calculators.Yield1x2Calculator.Key1x2;
import chorke.bet.apps.core.calculators.YieldCalculator;
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
    public Graph getGraph(Map<Calendar, Yield1x2> yields, BetPossibility betPossibility,
            int idxOnScale, List<BigDecimal> scales) {
        LOG.debug("Getting graph for index {} from scales {}.", idxOnScale, scales);
        Key1x2 key;
        if(scales.isEmpty()){
            key = new Key1x2(BigDecimal.ONE, YieldCalculator.MAX_VALUE);
        } else if(scales.size() == idxOnScale){
            key = new Key1x2(scales.get(idxOnScale - 1), YieldCalculator.MAX_VALUE);
        } else if(idxOnScale == 0){
            key = new Key1x2(BigDecimal.ONE, scales.get(0));
        } else {
            key = new Key1x2(scales.get(idxOnScale - 1).add(YieldCalculator.ZERO_POINT_ZERO_ONE),
                    scales.get(idxOnScale));
        }
        LOG.debug("Key for graph {}.", key);
        Map<Calendar, BigDecimal> yieldsForInner = new LinkedHashMap<>();
        List<Calendar> keys = new LinkedList<>(yields.keySet());
        Collections.sort(keys);
        for(Calendar c : keys){
            yieldsForInner.put(c, yields.get(c).getYieldForKey(betPossibility, key, true));
        }
        return getGraphInner(yieldsForInner);
    }

//    @Override
//    public Graph getGraph(Map<Calendar, Yield1x2> yields, BetPossibility betPossibility,
//                Tuple<BigDecimal, BigDecimal> range) {
//        Map<Calendar, BigDecimal> yieldsForInner = new LinkedHashMap<>();
//        List<Calendar> keys = new LinkedList<>(yields.keySet());
//        Collections.sort(keys);
//        for(Calendar c : keys){
//            yieldsForInner.put(c, yields.get(c).getYieldForScaleRange(betPossibility, range, true));
//        }
//        return getGraphInner(yieldsForInner);
//    }
}
