package chorke.bet.apps;

import chorke.bet.apps.core.graphs.Graph;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import static chorke.bet.apps.BasicTests.assertAreBothNull;
import static chorke.bet.apps.BasicTests.collectionChecker;

/**
 *
 * @author Chorke
 */
public class GraphTest {
    
    /**
     * Test of getValues method, of class Graph.
     */
    @Test
    public void testGetAddValuesGetMinMax() {
        List<BigDecimal> list = new LinkedList<>();
        list.add(new BigDecimal("10.531"));
        list.add(new BigDecimal("83.179"));
        list.add(new BigDecimal("0.123"));
        list.add(new BigDecimal("-8.872"));
        list.add(new BigDecimal("8.872"));
        list.add(new BigDecimal("95.36"));
        list.add(new BigDecimal("-0.0"));
        list.add(new BigDecimal("-1.543"));
        
        Graph g = new Graph();
        
        assertAreBothNull(g.getMin(), null);
        assertAreBothNull(g.getMax(), null);
        assertEqualsBigDecimalLists(g.getValues(), new LinkedList<BigDecimal>());
        
        for(BigDecimal bd : list){
            g.add(bd);
        }
        
        try{
            g.add(null);
            fail("Add value null: no exception");
        } catch (IllegalArgumentException ex){
        } catch (Exception e){
            fail("Add value null: bad exception " + e);
        }
        
        assertEqualsBigDecimal(g.getMax(), new BigDecimal("95.36"));
        assertEqualsBigDecimal(g.getMin(), new BigDecimal("-8.872"));
        assertEqualsBigDecimalLists(g.getValues(), list);
    }

    /**
     * Test of add method, of class Graph.
     */
    @Test
    public void testAddToPosition() {
        List<BigDecimal> list = new LinkedList<>();
        list.add(new BigDecimal("10.531"));
        list.add(new BigDecimal("0.123"));
        list.add(new BigDecimal("-1.543"));
        
        Graph g = new Graph();
        for(BigDecimal bd : list){
            g.add(bd);
        }
        assertEqualsBigDecimalLists(g.getValues(), list);
        
        try{
            g.add(null, 0);
            fail("Add to position value null: no exception");
        } catch (IllegalArgumentException ex){
        } catch (Exception e){
            fail("Add to position value null: bad exception " + e);
        }
        
        try{
            g.add(BigDecimal.ONE, -1);
            fail("Add to position idx < 0: no exception");
        } catch (IndexOutOfBoundsException ex){
        } catch (Exception e){
            fail("Add to position idx < 0: bad exception " + e);
        }
        
        try{
            g.add(BigDecimal.ONE, 4);
            fail("Add to position idx > size: no exception");
        } catch (IndexOutOfBoundsException ex){
        } catch (Exception e){
            fail("Add to position idx > size: bad exception " + e);
        }
        
        g.add(new BigDecimal("-0.34"), 1);
        list.add(1, new BigDecimal("-0.34"));
        assertEqualsBigDecimalLists(g.getValues(), list);
        assertEqualsBigDecimal(g.getMin(), new BigDecimal("-1.543"));
        
        g.add(new BigDecimal("-2.34"), 3);
        list.add(3, new BigDecimal("-2.34"));
        assertEqualsBigDecimalLists(g.getValues(), list);
        assertEqualsBigDecimal(g.getMin(), new BigDecimal("-2.34"));
        
        g.add(new BigDecimal("11.34"), 2);
        list.add(2, new BigDecimal("11.34"));
        assertEqualsBigDecimalLists(g.getValues(), list);
        assertEqualsBigDecimal(g.getMax(), new BigDecimal("11.34"));
        
        g.add(new BigDecimal("2.34"), 2);
        list.add(2, new BigDecimal("2.34"));
        assertEqualsBigDecimalLists(g.getValues(), list);
        assertEqualsBigDecimal(g.getMax(), new BigDecimal("11.34"));
    }

    /**
     * Test of remove method, of class Graph.
     */
    @Test
    public void testRemoveGetMinMax() {
        List<BigDecimal> list = new LinkedList<>();
        list.add(new BigDecimal("10.531"));
        list.add(new BigDecimal("0.123"));
        list.add(new BigDecimal("-1.543"));
        
        Graph g = new Graph();
        for(BigDecimal bd : list){
            g.add(bd);
        }
        assertEqualsBigDecimalLists(g.getValues(), list);
        
        try{
            g.remove(-1);
            fail("Remove idx < 0: no exception");
        } catch (IndexOutOfBoundsException ex){
        } catch (Exception e){
            fail("Remove idx < 0: bad exception " + e);
        }
        
        try{
            g.remove(3);
            fail("Remove idx > size: no exception");
        } catch (IndexOutOfBoundsException ex){
        } catch (Exception e){
            fail("Remove idx > size: bad exception " + e);
        }
        
        g.remove(0);
        assertEqualsBigDecimal(g.getMax(), new BigDecimal("0.123"));
        assertEqualsBigDecimal(g.getMin(), new BigDecimal("-1.543"));
        list.remove(0);
        assertEqualsBigDecimalLists(g.getValues(), list);
        
        g.remove(1);
        assertEqualsBigDecimal(g.getMax(), new BigDecimal("0.123"));
        assertEqualsBigDecimal(g.getMin(), new BigDecimal("0.123"));
        list.remove(1);
        assertEqualsBigDecimalLists(g.getValues(), list);
        
        g.remove(0);
        assertEqualsBigDecimal(g.getMax(), null);
        assertEqualsBigDecimal(g.getMin(), null);
        list.remove(0);
        assertEqualsBigDecimalLists(g.getValues(), list);
    }

    /**
     * Test of clear method, of class Graph.
     */
    @Test
    public void testClear() {
        List<BigDecimal> list = new LinkedList<>();
        list.add(new BigDecimal("10.531"));
        list.add(new BigDecimal("0.123"));
        list.add(new BigDecimal("-1.543"));
        
        Graph g = new Graph();
        for(BigDecimal bd : list){
            g.add(bd);
        }
        assertEqualsBigDecimalLists(g.getValues(), list);
        
        g.clear();
        assertEqualsBigDecimalLists(g.getValues(), new LinkedList<BigDecimal>());
        assertEqualsBigDecimal(g.getMax(), null);
        assertEqualsBigDecimal(g.getMin(), null);
    }
    
    
    
    private void assertEqualsBigDecimal(BigDecimal val1, BigDecimal val2){
        if(!assertAreBothNull(val1, val2)){
            if(val1.compareTo(val2) != 0){
                fail("BigDecimal values are not same: " + val1 + ", " + val2);
            }
        }
    }
    
    private void assertEqualsBigDecimalLists(List<BigDecimal> list1, List<BigDecimal> list2){
        if(collectionChecker(list1, list2)){
            for(int i = 0; i < list1.size(); i++){
                assertEqualsBigDecimal(list1.get(i), list2.get(i));
            }
        }
//        if(!assertAreBothNull(list1, list2)){
//            if(list1.size() != list2.size()){ fail("Not same size {list1: " 
//                    + list1.size() + ", list2: " + list2.size() + "}"); }
//            for(int i = 0; i < list1.size(); i++){
//                assertEqualsBigDecimal(list1.get(i), list2.get(i));
//            }
//        }
    }
}