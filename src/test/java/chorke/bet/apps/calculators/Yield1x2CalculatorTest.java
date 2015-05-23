
package chorke.bet.apps.calculators;

import static chorke.bet.apps.BasicTests.assertEqualsBigDecimal;
import chorke.bet.apps.MatchInitializer;
import chorke.bet.apps.core.CoreUtils.BetPossibility;
import chorke.bet.apps.core.CoreUtils.Periode;
import chorke.bet.apps.core.bets.Bet1x2;
import chorke.bet.apps.core.calculators.Yield1x2;
import chorke.bet.apps.core.calculators.Yield1x2Calculator;
import chorke.bet.apps.core.calculators.Yield1x2Calculator.Key1x2;
import chorke.bet.apps.core.calculators.YieldProperties;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Chorke
 */
public class Yield1x2CalculatorTest extends MatchInitializer{
    
    private YieldProperties properties;
    private Yield1x2Calculator calculator;
    private Key1x2 key1;
    private Key1x2 key2;
    
    @Override
    public void init(){
        super.init();
        m1_24_12_2013_Hoc.addBet(new Bet1x2("myCom", 
                new BigDecimal("1.21"), new BigDecimal("5.56"), new BigDecimal("2.89")));
        m1_26_12_2013_Hoc.addBet(new Bet1x2("myCom", 
                new BigDecimal("2.65"), new BigDecimal("4.32"), new BigDecimal("2.31")));
        m1_29_11_2013_Soc.addBet(new Bet1x2("myCom", 
                new BigDecimal("1.56"), new BigDecimal("3.64"), new BigDecimal("1.89")));
        m1_3_1_2014_Soc.addBet(new Bet1x2("myCom", 
                new BigDecimal("1.54"), new BigDecimal("4.56"), new BigDecimal("1.88")));
        m2_2_1_2014_Soc.addBet(new Bet1x2("myCom", 
                new BigDecimal("2.11"), new BigDecimal("4.00"), new BigDecimal("2.11")));
        m2_30_11_2013_Soc.addBet(new Bet1x2("anotherCom", 
                new BigDecimal("1.43"), new BigDecimal("3.77"), new BigDecimal("2.33")));
        m2_31_12_2013_Hoc.addBet(new Bet1x2("myCom", 
                new BigDecimal("1.33"), new BigDecimal("4.21"), new BigDecimal("1.67")));
        mX_1_1_2014_Hoc.addBet(new Bet1x2("myCom", 
                new BigDecimal("1.66"), new BigDecimal("4.65"), new BigDecimal("2.11")));
        mX_28_12_2013_Soc.addBet(new Bet1x2("myCom", 
                new BigDecimal("1.65"), new BigDecimal("3.45"), new BigDecimal("1.76")));
        
        List<BigDecimal> scales = new LinkedList<>();
        BigDecimal sc = new BigDecimal("1.58");
        scales.add(sc);
        properties = new YieldProperties(scales, "myCom");
        
        key1 = new Key1x2(BigDecimal.ONE, sc);
        key2 = new Key1x2(sc.add(Yield1x2Calculator.ZERO_POINT_ZERO_ONE), Yield1x2Calculator.MAX_VALUE);
        
        calculator = new Yield1x2Calculator();
    }
    
    @Test
    public void overallYieldTest(){
        Yield1x2 yield = calculator.getOverallYield(matches, properties);
        assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Favorit, key1, false),
                new BigDecimal("0.31"));
        assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Favorit, key2, false),
                new BigDecimal("-3"));
        assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Guest, key1, false),
                new BigDecimal("-2.33"));
        assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Guest, key2, false),
                new BigDecimal("-3"));
        assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Home, key1, false),
                new BigDecimal("0.31"));
        assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Home, key2, false),
                new BigDecimal("-0.35"));
        assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Loser, key1, false),
                new BigDecimal("-2.33"));
        assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Loser, key2, false),
                new BigDecimal("-0.35"));
        assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Tie, key1, false),
                new BigDecimal("-4"));
        assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Tie, key2, false),
                new BigDecimal("5.1"));
    }
    
    @Test
    public void getYieldYEAR(){
        Map<Calendar, Yield1x2> yields = calculator.getPeriodicYield(matches, properties, Periode.Year);
        assertEquals(yields.size(), 2);
        Yield1x2 yield;
        for(Calendar c : yields.keySet()){
            yield = yields.get(c);
            if(c.get(Calendar.YEAR) == 2013){
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Favorit, key1, false),
                        new BigDecimal("-0.23"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Favorit, key2, false),
                        new BigDecimal("-2"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Guest, key1, false),
                        new BigDecimal("-1.33"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Guest, key2, false),
                        new BigDecimal("-2"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Home, key1, false),
                        new BigDecimal("-0.23"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Home, key2, false),
                        new BigDecimal("0.65"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Loser, key1, false),
                        new BigDecimal("-1.33"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Loser, key2, false),
                        new BigDecimal("0.65"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Tie, key1, false),
                        new BigDecimal("-3"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Tie, key2, false),
                        new BigDecimal("1.45"));
            } else if(c.get(Calendar.YEAR) == 2014){
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Favorit, key1, false),
                        new BigDecimal("0.54"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Favorit, key2, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Guest, key1, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Guest, key2, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Home, key1, false),
                        new BigDecimal("0.54"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Home, key2, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Loser, key1, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Loser, key2, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Tie, key1, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Tie, key2, false),
                        new BigDecimal("3.65"));
            } else {
                fail("bad year " + c);
            }
        }
    }
    
    @Test
    public void getYieldMONTH(){
        Map<Calendar, Yield1x2> yields = calculator.getPeriodicYield(matches, properties, Periode.Month);
        assertEquals(yields.size(), 3);
        Yield1x2 yield;
        for(Calendar c : yields.keySet()){
            yield = yields.get(c);
            if(c.get(Calendar.MONTH) == 10 && c.get(Calendar.YEAR) == 2013){
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Favorit, key1, false),
                        new BigDecimal("0.56"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Favorit, key2, false),
                        new BigDecimal("0"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Guest, key1, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Guest, key2, false),
                        new BigDecimal("0"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Home, key1, false),
                        new BigDecimal("0.56"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Home, key2, false),
                        new BigDecimal("0"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Loser, key1, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Loser, key2, false),
                        new BigDecimal("0"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Tie, key1, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Tie, key2, false),
                        new BigDecimal("0"));
            } else if(c.get(Calendar.MONTH) == 11 && c.get(Calendar.YEAR) == 2013){
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Favorit, key1, false),
                        new BigDecimal("-0.79"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Favorit, key2, false),
                        new BigDecimal("-2"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Guest, key1, false),
                        new BigDecimal("-0.33"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Guest, key2, false),
                        new BigDecimal("-2"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Home, key1, false),
                        new BigDecimal("-0.79"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Home, key2, false),
                        new BigDecimal("0.65"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Loser, key1, false),
                        new BigDecimal("-0.33"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Loser, key2, false),
                        new BigDecimal("0.65"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Tie, key1, false),
                        new BigDecimal("-2"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Tie, key2, false),
                        new BigDecimal("1.45"));
            } else if(c.get(Calendar.MONTH) == 0 && c.get(Calendar.YEAR) == 2014){
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Favorit, key1, false),
                        new BigDecimal("0.54"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Favorit, key2, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Guest, key1, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Guest, key2, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Home, key1, false),
                        new BigDecimal("0.54"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Home, key2, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Loser, key1, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Loser, key2, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Tie, key1, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Tie, key2, false),
                        new BigDecimal("3.65"));
            } else {
                fail("bad calendar " + c);
            }
        }
    }
    
    @Test
    public void getYieldWEEK(){
        Map<Calendar, Yield1x2> yields = calculator.getPeriodicYield(matches, properties, Periode.Week);
        assertEquals(yields.size(), 6);
        Yield1x2 yield;
        for(Calendar c : yields.keySet()){
            yield = yields.get(c);
            if(c.get(Calendar.YEAR) == 2013 && c.get(Calendar.WEEK_OF_YEAR) == 48){
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Favorit, key1, false),
                        new BigDecimal("0.56"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Favorit, key2, false),
                        new BigDecimal("0"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Guest, key1, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Guest, key2, false),
                        new BigDecimal("0"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Home, key1, false),
                        new BigDecimal("0.56"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Home, key2, false),
                        new BigDecimal("0"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Loser, key1, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Loser, key2, false),
                        new BigDecimal("0"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Tie, key1, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Tie, key2, false),
                        new BigDecimal("0"));
            } else if(c.get(Calendar.YEAR) == 2013  && c.get(Calendar.WEEK_OF_YEAR) == 52){
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Favorit, key1, false),
                        new BigDecimal("0.21"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Favorit, key2, false),
                        new BigDecimal("-2"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Guest, key1, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Guest, key2, false),
                        new BigDecimal("-2"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Home, key1, false),
                        new BigDecimal("0.21"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Home, key2, false),
                        new BigDecimal("0.65"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Loser, key1, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Loser, key2, false),
                        new BigDecimal("0.65"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Tie, key1, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Tie, key2, false),
                        new BigDecimal("1.45"));
            } else if(c.get(Calendar.YEAR) == 2014  && c.get(Calendar.WEEK_OF_YEAR) == 1){
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Favorit, key1, false),
                        new BigDecimal("-0.46"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Favorit, key2, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Guest, key1, false),
                        new BigDecimal("-0.33"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Guest, key2, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Home, key1, false),
                        new BigDecimal("-0.46"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Home, key2, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Loser, key1, false),
                        new BigDecimal("-0.33"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Loser, key2, false),
                        new BigDecimal("-1"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Tie, key1, false),
                        new BigDecimal("-2"));
                assertEqualsBigDecimal(yield.getYieldForKey(BetPossibility.Tie, key2, false),
                        new BigDecimal("3.65"));
            } else {
                if(!((c.get(Calendar.YEAR) == 2013  && c.get(Calendar.WEEK_OF_YEAR) == 49)
                        || (c.get(Calendar.YEAR) == 2013  && c.get(Calendar.WEEK_OF_YEAR) == 50)
                        || (c.get(Calendar.YEAR) == 2013  && c.get(Calendar.WEEK_OF_YEAR) == 51))){
                    fail("bad calendar " + c);
                }
            }
        }
    }
}