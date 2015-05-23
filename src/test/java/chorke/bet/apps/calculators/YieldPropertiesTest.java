
package chorke.bet.apps.calculators;

import static chorke.bet.apps.BasicTests.assertEqualsBigDecimal;
import static chorke.bet.apps.BasicTests.collectionChecker;
import chorke.bet.apps.core.calculators.YieldProperties;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Chorke
 */
public class YieldPropertiesTest {
    
    private YieldProperties yieldProperties;
    private List<BigDecimal> scales;
    
    @Before
    public void init(){
        yieldProperties = new YieldProperties();
        scales = new LinkedList<>();
        scales.add(new BigDecimal("1.42"));
        scales.add(new BigDecimal("1.55"));
        scales.add(new BigDecimal("1.78"));
        scales.add(new BigDecimal("2.41"));
        yieldProperties.setScale(scales);
    }
    
    @After
    public void tearDown(){
        yieldProperties = null;
        scales.clear();
        scales = null;
    }
    
    @Test
    public void getSetScaleTest(){
        yieldProperties.setScale(scales);
        List<BigDecimal> result = yieldProperties.getScale();
        assertEqualsListBigDecimal(scales, result);
        
        yieldProperties.setScale(null);
        result = yieldProperties.getScale();
        assertEqualsListBigDecimal(result, new ArrayList<BigDecimal>());
    }
    
    @Test
    public void addRemoveScaleTest(){
        BigDecimal num = new BigDecimal("1.62");
        
        yieldProperties.addScale(new BigDecimal("1.55"));
        List<BigDecimal> result = yieldProperties.getScale();
        assertEqualsListBigDecimal(scales, result);
        
        yieldProperties.addScale(num);
        result = yieldProperties.getScale();
        scales.add(2, num);
        assertEqualsListBigDecimal(scales, result);
        
        yieldProperties.removeScale(new BigDecimal("1.78"));
        result = yieldProperties.getScale();
        scales.remove(3);
        assertEqualsListBigDecimal(scales, result);
    }
    
    @Test
    public void setGetClearPropertyTest(){
        String text = "some Property";
        yieldProperties.setProperty("prop1", text);
        Object o = yieldProperties.getProperty("prop1");
        assertEquals(o, text);
        
        o = yieldProperties.getProperty("prop2");
        assertNull(o);
        
        BigDecimal num = BigDecimal.ONE;
        yieldProperties.setProperty("prop1", num);
        o = yieldProperties.getProperty("prop1");
        assertEquals(o, num);
        
        yieldProperties.clearProperty("prop1");
        o = yieldProperties.getProperty("prop1");
        assertNull(o);
    }
    
    private void assertEqualsListBigDecimal(List<BigDecimal> l1, List<BigDecimal> l2){
        if(!collectionChecker(l1, l2)){
            return;
        }
        Iterator<BigDecimal> i1 = l1.iterator();
        Iterator<BigDecimal> i2 = l2.iterator();
        while(i1.hasNext() && i2.hasNext()){
            assertEqualsBigDecimal(i1.next(), i2.next());
        }
    }
}