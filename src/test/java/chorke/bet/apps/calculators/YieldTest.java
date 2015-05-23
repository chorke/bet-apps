
package chorke.bet.apps.calculators;

import chorke.bet.apps.core.CoreUtils.BetPossibility;
import chorke.bet.apps.core.calculators.Yield1x2;
import chorke.bet.apps.core.calculators.Yield1x2Calculator.Key1x2;
import chorke.bet.apps.core.calculators.YieldProperties;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Chorke
 */
public class YieldTest {
    
    private static final BigDecimal NUM_1_45 = new BigDecimal("1.45");
    private static final BigDecimal NUM_1_46 = new BigDecimal("1.46");
    private static final BigDecimal NUM_1_47 = new BigDecimal("1.47");
    private static final BigDecimal NUM_1_48 = new BigDecimal("1.48");
    private static final BigDecimal NUM_1_49 = new BigDecimal("1.49");
    private static final BigDecimal NUM_1_50 = new BigDecimal("1.50");
    private static final BigDecimal NUM_1_82 = new BigDecimal("1.82");
    private static final BigDecimal NUM_2_56 = new BigDecimal("2.56");
    
    private static YieldProperties yieldProps;
    
    @BeforeClass
    public static void init(){
        yieldProps = new YieldProperties();
        List<BigDecimal> ranges = new LinkedList<>();
        ranges.add(NUM_1_45);
        ranges.add(NUM_1_82);
        ranges.add(NUM_2_56);
        yieldProps.setScale(ranges);
    }
    
    @Test
    public void addGetYieldForKeyTest(){
        Yield1x2 yield = new Yield1x2();
        Key1x2 key = new Key1x2(NUM_1_46, NUM_1_82);
        yield.addYieldForKey(BetPossibility.Favorit, key, NUM_1_45);
        yield.addYieldForKey(BetPossibility.Guest, key, NUM_1_46);
        yield.addYieldForKey(BetPossibility.Home, key, NUM_1_47);
        yield.addYieldForKey(BetPossibility.Loser, key, NUM_1_48);
        yield.addYieldForKey(BetPossibility.Tie, key, NUM_1_49);
        
        yieldEqualityKeyTest(yield.getYields(BetPossibility.Favorit), key,
                yield.getYieldForKey(BetPossibility.Favorit, key, false));
        yieldEqualityKeyTest(yield.getYields(BetPossibility.Guest), key,
                yield.getYieldForKey(BetPossibility.Guest, key, false));
        yieldEqualityKeyTest(yield.getYields(BetPossibility.Home), key,
                yield.getYieldForKey(BetPossibility.Home, key, false));
        yieldEqualityKeyTest(yield.getYields(BetPossibility.Loser), key,
                yield.getYieldForKey(BetPossibility.Loser, key, false));
        yieldEqualityKeyTest(yield.getYields(BetPossibility.Tie), key,
                yield.getYieldForKey(BetPossibility.Tie, key, false));
        
        yield.addYieldForKey(BetPossibility.Favorit, key, NUM_1_50);
        yieldEqualityKeyTest(yield.getYields(BetPossibility.Favorit), key,
                yield.getYieldForKey(BetPossibility.Favorit, key, false));
    }
    
    private void yieldEqualityKeyTest(Map<Object, BigDecimal> allYields,
            Object key, BigDecimal yield){
        if(yield.compareTo(allYields.get(key)) != 0){
            fail();
        }
    }
}