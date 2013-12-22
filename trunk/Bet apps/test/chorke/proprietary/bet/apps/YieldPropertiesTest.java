/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps;

import chorke.proprietary.bet.apps.core.Tuple;
import chorke.proprietary.bet.apps.core.calculators.YieldProperties;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

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
    
    @Test
    public void getRangesTest(){
        List<Tuple<BigDecimal, BigDecimal>> list = new LinkedList<>();
        list.add(new Tuple<>(new BigDecimal("1.00"), new BigDecimal("1.42")));
        list.add(new Tuple<>(new BigDecimal("1.43"), new BigDecimal("1.55")));
        list.add(new Tuple<>(new BigDecimal("1.56"), new BigDecimal("1.78")));
        list.add(new Tuple<>(new BigDecimal("1.79"), new BigDecimal("2.41")));
        list.add(new Tuple<>(new BigDecimal("2.42"), new BigDecimal(Double.MAX_VALUE)));
        
        assertEqualsListTupleBigDecimal(yieldProperties.getRanges(), list);
    }
    
    @Test
    public void rangeForIndexTest(){
        Tuple<BigDecimal, BigDecimal> range = yieldProperties.getRangeForIndex(0);
        assertEqualsTupleBigDecimal(range, new Tuple<>(new BigDecimal("1.0"), new BigDecimal("1.42")));
        
        range = yieldProperties.getRangeForIndex(2);
        assertEqualsTupleBigDecimal(range, new Tuple<>(new BigDecimal("1.56"), new BigDecimal("1.78")));
        
        range = yieldProperties.getRangeForIndex(4);
        assertEqualsTupleBigDecimal(range, new Tuple<>(new BigDecimal("2.42"), new BigDecimal(Double.MAX_VALUE)));
        try{
            yieldProperties.getRangeForIndex(-1);
            fail("No exception for index -1");
        } catch (IndexOutOfBoundsException ex){}
        try{
            yieldProperties.getRangeForIndex(10);
            fail("No exception for index 10");
        } catch (IndexOutOfBoundsException ex){}
    }
    
    @Test
    public void indexForRangeTest(){
        int index = yieldProperties.getIndexForRange(new Tuple<>(BigDecimal.ONE, 
                new BigDecimal("1.42")));
        assertEquals(index, 0);
        
        index = yieldProperties.getIndexForRange(new Tuple<>(new BigDecimal("1.79"),
                new BigDecimal("2.41")));
        assertEquals(index, 3);
        
        index = yieldProperties.getIndexForRange(new Tuple<>(new BigDecimal("2.42"),
                new BigDecimal(Double.MAX_VALUE)));
        assertEquals(index, 4);
        
        try{
            yieldProperties.getIndexForRange(new Tuple<>(new BigDecimal("1.43"), 
                    new BigDecimal("1.54")));
            fail("No exception for range <1.43, 1.54>");
        } catch (IllegalArgumentException ex){}
        
        try{
            yieldProperties.getIndexForRange(new Tuple<>(new BigDecimal("1.55"), 
                    new BigDecimal("1.78")));
            fail("No exception for range <1.55, 1.78>");
        } catch (IllegalArgumentException ex){}
        
        try{
            yieldProperties.getIndexForRange(new Tuple<>(new BigDecimal("0.99"), 
                    new BigDecimal("1.42")));
            fail("No exception for range <0.99, 1.42>");
        } catch (IllegalArgumentException ex){}
        
        try{
            yieldProperties.getIndexForRange(new Tuple<>(new BigDecimal("2.42"), 
                    new BigDecimal("10.0")));
            fail("No exception for range <2.42, 10.0>");
        } catch (IllegalArgumentException ex){}
        
        try{
            yieldProperties.getIndexForRange(new Tuple<>(new BigDecimal("1.68"), 
                    new BigDecimal("1.78")));
            fail("No exception for range <1.68, 1.78>");
        } catch (IllegalArgumentException ex){}
        
        try{
            yieldProperties.getIndexForRange(new Tuple<>(new BigDecimal("1.68"), 
                    new BigDecimal("1.99")));
            fail("No exception for range <1.68, 1.99>");
        } catch (IllegalArgumentException ex){}
        
        try{
            yieldProperties.getIndexForRange(new Tuple<>(new BigDecimal("4.5"), 
                    new BigDecimal(Double.MAX_VALUE)));
            fail("No exception for range <4.5, Double.MAX_VALUE>");
        } catch (IllegalArgumentException ex){}
    }
    
    private void assertEqualsListBigDecimal(List<BigDecimal> l1, List<BigDecimal> l2){
        if(!checkNullList(l1, l2)){ fail(); }
        Iterator<BigDecimal> i1 = l1.iterator();
        Iterator<BigDecimal> i2 = l2.iterator();
        while(i1.hasNext() && i2.hasNext()){
            if(i1.next().compareTo(i2.next()) != 0){ fail(); }
        }
    }
    
    private void assertEqualsListTupleBigDecimal(List<Tuple<BigDecimal, BigDecimal>> l1, 
            List<Tuple<BigDecimal, BigDecimal>> l2){
        if(!checkNullList(l1, l2)){ fail(); }
        Iterator<Tuple<BigDecimal, BigDecimal>> i1 = l1.iterator();
        Iterator<Tuple<BigDecimal, BigDecimal>> i2 = l2.iterator();
        Tuple<BigDecimal, BigDecimal> tp1, tp2;
        while(i1.hasNext() && i2.hasNext()){
            tp1 = i1.next();
            tp2 = i2.next();
            if(tp1.first.compareTo(tp2.first) != 0
                    || tp1.second.compareTo(tp2.second) != 0){
                fail();
            }
        }
    }
    
    private void assertEqualsTupleBigDecimal(Tuple<BigDecimal, BigDecimal> tp1, 
            Tuple<BigDecimal, BigDecimal> tp2){
        if(tp1.first.compareTo(tp2.first) != 0
                || tp1.second.compareTo(tp2.second) != 0){
            fail();
        }
    }
    
    private boolean checkNullList(List l1, List l2){
        if(l1 == null && l2 == null){ return true; }
        if(l1 == null && l2 != null){ return false; }
        if(l1 != null && l2 == null){ return false; }
        return l1.size() == l2.size();
    }
}