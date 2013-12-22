/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps;

import chorke.proprietary.bet.apps.core.bets.Bet1x2;
import chorke.proprietary.bet.apps.core.calculators.CumulativeYieldProperties;
import chorke.proprietary.bet.apps.core.calculators.Yield1x2;
import chorke.proprietary.bet.apps.core.calculators.Yield1x2Calculator;
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
public class Yield1x2CalculatorTest extends MatchInitializer{
    
    private YieldProperties properties;
    private Yield1x2Calculator calculator;
    
    @Override
    public void init(){
        super.init();
        System.out.println("after init");
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
        scales.add(new BigDecimal("1.58"));
        properties = new YieldProperties(scales, "myCom");
        
        calculator = new Yield1x2Calculator();
        System.out.println("end init");
    }
    
    @Test
    public void overallYieldTest(){
        Yield1x2 yield = calculator.getOverallYield(matches, properties);
        
    }
    
}