
package chorke.proprietary.bet.apps;

import chorke.proprietary.bet.apps.core.Tuple;
import chorke.proprietary.bet.apps.core.calculators.Yield;
import chorke.proprietary.bet.apps.core.calculators.Yield.BetPossibility;
import chorke.proprietary.bet.apps.core.calculators.Yield1x2;
import chorke.proprietary.bet.apps.core.calculators.YieldProperties;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Chorke
 */
public class Yield1x2Test {
    
    private Yield1x2 yield;
    private static final BigDecimal NUM_1_45 = new BigDecimal("1.45");
    private static final BigDecimal NUM_1_46 = new BigDecimal("1.46");
    private static final BigDecimal NUM_1_47 = new BigDecimal("1.47");
    private static final BigDecimal NUM_1_48 = new BigDecimal("1.48");
    private static final BigDecimal NUM_1_49 = new BigDecimal("1.49");
    private static final BigDecimal NUM_1_50 = new BigDecimal("1.50");
    
    private static final BigDecimal NUM_1_82 = new BigDecimal("1.82");
    private static final BigDecimal NUM_1_83 = new BigDecimal("1.83");
    private static final BigDecimal NUM_2_56 = new BigDecimal("2.56");
    
    @Before
    public void init(){
        yield = new Yield1x2();
        YieldProperties properties = new YieldProperties();
        List<BigDecimal> ranges = new LinkedList<>();
        ranges.add(new BigDecimal("1.45"));
        ranges.add(new BigDecimal("1.82"));
        ranges.add(new BigDecimal("2.56"));
        properties.setScale(ranges);
        yield.setProperties(properties);
    }
    
    @Test
    public void addIndexGetIndexTest(){
        yield.addYieldForScaleIndex(BetPossibility.Favorit, 1, NUM_1_45);
        yield.addYieldForScaleIndex(BetPossibility.Guest, 1, NUM_1_46);
        yield.addYieldForScaleIndex(BetPossibility.Home, 1, NUM_1_47);
        yield.addYieldForScaleIndex(BetPossibility.Looser, 1, NUM_1_48);
        yield.addYieldForScaleIndex(BetPossibility.Tie, 1, NUM_1_49);
        
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Favorit), 1,
                yield.getYieldForScaleIndex(BetPossibility.Favorit, 1));
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Guest), 1,
                yield.getYieldForScaleIndex(BetPossibility.Guest, 1));
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Home), 1,
                yield.getYieldForScaleIndex(BetPossibility.Home, 1));
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Looser), 1,
                yield.getYieldForScaleIndex(BetPossibility.Looser, 1));
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Tie), 1,
                yield.getYieldForScaleIndex(BetPossibility.Tie, 1));
        
        yield.addYieldForScaleIndex(BetPossibility.Favorit, 1, NUM_1_50);
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Favorit), 1,
                yield.getYieldForScaleIndex(BetPossibility.Favorit, 1));
    }
    
    @Test
    public void addIndexGetRangeTest(){
        yield.addYieldForScaleIndex(BetPossibility.Favorit, 1, NUM_1_45);
        yield.addYieldForScaleIndex(BetPossibility.Guest, 1, NUM_1_45);
        yield.addYieldForScaleIndex(BetPossibility.Home, 1, NUM_1_47);
        yield.addYieldForScaleIndex(BetPossibility.Looser, 1, NUM_1_48);
        yield.addYieldForScaleIndex(BetPossibility.Tie, 1, NUM_1_49);
        
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Favorit), 1,
                yield.getYieldForScaleRange(BetPossibility.Favorit, new Tuple<>(NUM_1_46, NUM_1_82)));
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Guest), 1,
                yield.getYieldForScaleRange(BetPossibility.Guest, new Tuple<>(NUM_1_46, NUM_1_82)));
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Home), 1,
                yield.getYieldForScaleRange(BetPossibility.Home, new Tuple<>(NUM_1_46, NUM_1_82)));
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Looser), 1,
                yield.getYieldForScaleRange(BetPossibility.Looser, new Tuple<>(NUM_1_46, NUM_1_82)));
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Tie), 1,
                yield.getYieldForScaleRange(BetPossibility.Tie, new Tuple<>(NUM_1_46, NUM_1_82)));
    }
    
    @Test
    public void addRangeGetIndexTest(){
        yield.addYieldForScaleRange(BetPossibility.Favorit, new Tuple<>(NUM_1_83, NUM_2_56), NUM_1_45);
        yield.addYieldForScaleRange(BetPossibility.Guest, new Tuple<>(NUM_1_83, NUM_2_56), NUM_1_46);
        yield.addYieldForScaleRange(BetPossibility.Home, new Tuple<>(NUM_1_83, NUM_2_56), NUM_1_47);
        yield.addYieldForScaleRange(BetPossibility.Looser, new Tuple<>(NUM_1_83, NUM_2_56), NUM_1_48);
        yield.addYieldForScaleRange(BetPossibility.Tie, new Tuple<>(NUM_1_83, NUM_2_56), NUM_1_49);
        
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Favorit), 2,
                yield.getYieldForScaleIndex(BetPossibility.Favorit, 2));
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Guest), 2,
                yield.getYieldForScaleIndex(BetPossibility.Guest, 2));
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Home), 2,
                yield.getYieldForScaleIndex(BetPossibility.Home, 2));
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Looser), 2,
                yield.getYieldForScaleIndex(BetPossibility.Looser, 2));
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Tie), 2,
                yield.getYieldForScaleIndex(BetPossibility.Tie, 2));
    }
    
    @Test
    public void addRangeGetRangeTest(){
        yield.addYieldForScaleRange(BetPossibility.Favorit, new Tuple<>(NUM_1_83, NUM_2_56), NUM_1_45);
        yield.addYieldForScaleRange(BetPossibility.Guest, new Tuple<>(NUM_1_83, NUM_2_56), NUM_1_46);
        yield.addYieldForScaleRange(BetPossibility.Home, new Tuple<>(NUM_1_83, NUM_2_56), NUM_1_47);
        yield.addYieldForScaleRange(BetPossibility.Looser, new Tuple<>(NUM_1_83, NUM_2_56), NUM_1_48);
        yield.addYieldForScaleRange(BetPossibility.Tie, new Tuple<>(NUM_1_83, NUM_2_56), NUM_1_49);
        
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Favorit), 2,
                yield.getYieldForScaleRange(BetPossibility.Favorit, new Tuple<>(NUM_1_83, NUM_2_56)));
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Guest), 2,
                yield.getYieldForScaleRange(BetPossibility.Guest, new Tuple<>(NUM_1_83, NUM_2_56)));
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Home), 2,
                yield.getYieldForScaleRange(BetPossibility.Home, new Tuple<>(NUM_1_83, NUM_2_56)));
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Looser), 2,
                yield.getYieldForScaleRange(BetPossibility.Looser, new Tuple<>(NUM_1_83, NUM_2_56)));
        yieldEqualityIndexTest(yield.getYields(BetPossibility.Tie), 2,
                yield.getYieldForScaleRange(BetPossibility.Tie, new Tuple<>(NUM_1_83, NUM_2_56)));
    }
    
    private void yieldEqualityIndexTest(List<Tuple<Integer, BigDecimal>> allYields,
            int index, BigDecimal yield){
        for(Tuple<Integer, BigDecimal> tp : allYields){
            if(tp.first.equals(index)){
                if(yield.compareTo(tp.second) != 0){
                    fail();
                }
            }
        }
    }
}