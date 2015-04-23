package chorke.bet.apps.collections;

import chorke.bet.apps.core.collections.SwappingList;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SwappingListTest {

    private List<SimpleObject> tl;
    private static SimpleObject obj1;
    private static SimpleObject obj2;
    private static SimpleObject obj3;
    private static SimpleObject obj4;
    private static SimpleObject obj5;
    private static SimpleObject obj6;
    private static SimpleObject obj7;
    private static SimpleObject obj9;
    private static SimpleObject obj8;
    private static SimpleObject[] allOb;
    private static List<SimpleObject> allObAsList;

    @BeforeClass
    public static void setUpClass() {
        obj1 = new SimpleObject();
        obj2 = new SimpleObject();
        obj3 = new SimpleObject();
        obj4 = new SimpleObject();
        obj5 = new SimpleObject();
        obj6 = new SimpleObject();
        obj7 = new SimpleObject();
        obj8 = new SimpleObject();
        obj9 = new SimpleObject();
        allOb = new SimpleObject[]{obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9};
        allObAsList = Collections.unmodifiableList(Arrays.asList(allOb));
    }

    @AfterClass
    public static void tearDownClass() {
        obj1 = null;
        obj2 = null;
        obj3 = null;
        obj4 = null;
        obj5 = null;
        obj6 = null;
        obj7 = null;
        obj8 = null;
        obj9 = null;
        allOb = null;
        allObAsList = null;
    }

    @Before
    public void setUp() throws Exception {
//        tl = new SwappingList<>();
        tl = new LinkedList<>();
    }

    @After
    public void tearDown() throws Exception {
        tl = null;
    }

    /**
     * add, addAll, clear, remove are used too.
     */
    @Test
    public void testSize() {
        assertEquals("New instance do not have 0 size.", tl.size(), 0);
        tl.add(obj1);
        assertEquals("One object has been added and size is not 1.", tl.size(), 1);
        tl.addAll(allObAsList);
        assertEquals("Size after addAll shoul be 10", tl.size(), 10);
        tl.remove(0);
        assertEquals("Size after remove shoul be 9", tl.size(), 9);
        tl.clear();
        assertEquals("Size after clear shoul be 0", tl.size(), 0);
    }

    /**
     * add and clear are used too.
     */
    @Test
    public void testIsEmpty() {
        //start - nothing in it
        assertTrue("New instance is not empty.", tl.isEmpty());
        tl.add(obj1);
        assertFalse("An object has been added and list is still empty.", tl.isEmpty());
        tl.clear();
        assertTrue("Cleared instance is not empty.", tl.isEmpty());
    }

    /**
     * add, remove are used too
     */
    @Test
    public void testContains() {
        assertFalse("New Instance contains object.", tl.contains(obj1));
        tl.add(obj2);
        tl.add(obj1);
        assertTrue("Object not in list.", tl.contains(obj1));
        tl.remove(obj1);
        assertFalse("List contains rempoved object.", tl.contains(obj1));
        assertTrue("Second object after remove not in list.", tl.contains(obj2));
    }

    /**
     * addAll is used too
     */
    @Test
    public void testIterator() {
        Iterator<SimpleObject> iter = tl.iterator();
        assertFalse("Iterator of new instance has next element.", iter.hasNext());
        tl.addAll(allObAsList);
        testSimpleIterator(tl, allObAsList, null);
    }

    /**
     * addAll is used too
     */
    @Test
    public void testToArray() {
        assertArrayEquals("Not empty array.", tl.toArray(), new SimpleObject[]{});
        tl.addAll(allObAsList);
        assertArrayEquals("Not same arrays.", tl.toArray(), allOb);
    }

    /**
     * addAll is used too
     */
    @Test
    public void testToTypedArray() {
        assertArrayEquals("Not empty array.", tl.toArray(new SimpleObject[0]), new SimpleObject[]{});
        tl.addAll(allObAsList);
        assertArrayEquals("Not same arrays.", tl.toArray(new SimpleObject[0]), allOb);
        SimpleObject[] ar = new SimpleObject[20];
        System.arraycopy(allOb, 0, ar, 0, allOb.length);
        assertArrayEquals("Not same arrays.", tl.toArray(new SimpleObject[20]), ar);
    }

    /**
     * contains and size are used too
     */
    @Test
    public void testAdd() {
        assertEquals("New instance is not empty.", tl.size(), 0);
        assertFalse("List contains specific element before add operation.", tl.contains(obj9));
        tl.add(obj9);
        assertEquals("List size does not reflect add operation.", tl.size(), 1);
        assertTrue("List does not contain specific element.", tl.contains(obj9));
    }

    /**
     * add, contains are used too
     */
    @Test
    public void testRemoveObject() {
        assertFalse("List contains specific element", tl.contains(obj4));
        assertFalse("List contains specific element", tl.contains(obj6));
        tl.add(obj4);
        tl.add(obj6);
        assertTrue("List does not contain specific element", tl.contains(obj4));
        assertTrue("List does not contain specific element", tl.contains(obj6));
        assertTrue("Not removed", tl.remove(obj4));
        assertFalse("Element has not been removed.", tl.contains(obj4));
        assertTrue("More elements have been removed", tl.contains(obj6));
        assertFalse("Removed again.", tl.remove(obj4));
    }

    /**
     * add, get, contains are used too
     */
    @Test
    public void testRemoveIndex() {
        assertFalse("List contains specific element", tl.contains(obj4));
        assertFalse("List contains specific element", tl.contains(obj6));
        tl.add(obj4);
        tl.add(obj6);
        assertTrue("List does not contain specific element", tl.contains(obj4));
        assertTrue("List does not contain specific element", tl.contains(obj6));
        assertTrue("Bas object.", tl.remove(0) == obj4);
        assertFalse("Element has not been removed.", tl.contains(obj4));
        assertTrue("More elements have been removed", tl.contains(obj6));
    }

    /**
     * add is used too
     */
    @Test
    public void testContainsAll() {
        List<SimpleObject> objs = Arrays.asList(new SimpleObject[]{obj1, obj2, obj4});
        assertFalse("List should be empty.", tl.containsAll(objs));
        tl.add(obj9);
        assertFalse("List should be empty.", tl.containsAll(objs));
        tl.add(obj1);
        assertFalse("All elements have not yet been added.", tl.containsAll(objs));
        tl.add(obj2);
        assertFalse("All elements have not yet been added.", tl.containsAll(objs));
        tl.add(obj3);
        assertFalse("All elements have not yet been added.", tl.containsAll(objs));
        tl.add(obj4);
        assertTrue("All elements have been added.", tl.containsAll(objs));
        tl.add(obj5);
        assertTrue("All elements have been added.", tl.containsAll(objs));
    }

    @Test
    public void testAddAll() {
        tl.add(obj1);
        tl.addAll(Arrays.asList(new SimpleObject[]{obj8, obj7, obj2}));
        assertTrue("Bad object at specific position.", tl.get(0) == obj1);
        assertTrue("Bad object at specific position.", tl.get(1) == obj8);
        assertTrue("Bad object at specific position.", tl.get(2) == obj7);
        assertTrue("Bad object at specific position.", tl.get(3) == obj2);
    }

    @Test
    public void testAddAllToPosition() {
        tl.add(obj1);
        tl.add(obj4);
        tl.addAll(0, Arrays.asList(new SimpleObject[]{obj8, obj7, obj2}));
        assertTrue("Bad object at specific position.", tl.get(0) == obj8);
        assertTrue("Bad object at specific position.", tl.get(1) == obj7);
        assertTrue("Bad object at specific position.", tl.get(2) == obj2);
        assertTrue("Bad object at specific position.", tl.get(3) == obj1);
        assertTrue("Bad object at specific position.", tl.get(4) == obj4);
        tl.clear();

        tl.add(obj1);
        tl.add(obj4);
        tl.addAll(1, Arrays.asList(new SimpleObject[]{obj8, obj7, obj2}));
        assertTrue("Bad object at specific position.", tl.get(0) == obj1);
        assertTrue("Bad object at specific position.", tl.get(1) == obj8);
        assertTrue("Bad object at specific position.", tl.get(2) == obj7);
        assertTrue("Bad object at specific position.", tl.get(3) == obj2);
        assertTrue("Bad object at specific position.", tl.get(4) == obj4);
        tl.clear();

        tl.add(obj1);
        tl.add(obj4);
        tl.addAll(2, Arrays.asList(new SimpleObject[]{obj8, obj7, obj2}));
        assertTrue("Bad object at specific position.", tl.get(0) == obj1);
        assertTrue("Bad object at specific position.", tl.get(1) == obj4);
        assertTrue("Bad object at specific position.", tl.get(2) == obj8);
        assertTrue("Bad object at specific position.", tl.get(3) == obj7);
        assertTrue("Bad object at specific position.", tl.get(4) == obj2);
    }

    @Test
    public void testRemoveAll() {
        assertFalse("Element in list", tl.contains(obj3));
        assertFalse("Element in list", tl.contains(obj6));
        assertFalse("Element in list", tl.contains(obj4));
        assertFalse("Element in list", tl.contains(obj7));

        tl.add(obj3);
        tl.add(obj6);
        tl.add(obj4);
        tl.add(obj7);

        assertTrue("Element not in list", tl.contains(obj3));
        assertTrue("Element not in list", tl.contains(obj6));
        assertTrue("Element not in list", tl.contains(obj4));
        assertTrue("Element not in list", tl.contains(obj7));

        tl.removeAll(Arrays.asList(new SimpleObject[]{obj3, obj7}));

        assertFalse("Element in list", tl.contains(obj3));
        assertTrue("Element not in list", tl.contains(obj6));
        assertTrue("Element not in list", tl.contains(obj4));
        assertFalse("Element in list", tl.contains(obj7));
    }

    @Test
    public void testRetainAll() {
        assertFalse("Element in list", tl.contains(obj3));
        assertFalse("Element in list", tl.contains(obj6));
        assertFalse("Element in list", tl.contains(obj4));
        assertFalse("Element in list", tl.contains(obj7));

        tl.add(obj3);
        tl.add(obj6);
        tl.add(obj4);
        tl.add(obj7);

        assertTrue("Element not in list", tl.contains(obj3));
        assertTrue("Element not in list", tl.contains(obj6));
        assertTrue("Element not in list", tl.contains(obj4));
        assertTrue("Element not in list", tl.contains(obj7));

        tl.retainAll(Arrays.asList(new SimpleObject[]{obj3, obj7}));

        assertTrue("Element not in list", tl.contains(obj3));
        assertFalse("Element in list", tl.contains(obj6));
        assertFalse("Element in list", tl.contains(obj4));
        assertTrue("Element not in list", tl.contains(obj7));
    }

    @Test
    public void testClear() {
        assertFalse("Element in list", tl.contains(obj3));
        assertFalse("Element in list", tl.contains(obj6));
        assertFalse("Element in list", tl.contains(obj4));
        assertFalse("Element in list", tl.contains(obj7));

        tl.add(obj3);
        tl.add(obj6);
        tl.add(obj4);
        tl.add(obj7);

        assertTrue("Element not in list", tl.contains(obj3));
        assertTrue("Element not in list", tl.contains(obj6));
        assertTrue("Element not in list", tl.contains(obj4));
        assertTrue("Element not in list", tl.contains(obj7));

        tl.clear();

        assertFalse("Element in list", tl.contains(obj3));
        assertFalse("Element in list", tl.contains(obj6));
        assertFalse("Element in list", tl.contains(obj4));
        assertFalse("Element in list", tl.contains(obj7));
    }

    @Test
    public void testGet() {
        tl.addAll(allObAsList);
        assertEquals("Bad obeject returned.", tl.get(0), obj1);
        assertEquals("Bad obeject returned.", tl.get(8), obj9);
        assertEquals("Bad obeject returned.", tl.get(4), obj5);
        assertEquals("Bad obeject returned.", tl.get(3), obj4);
    }

    @Test
    public void testSet() {
        tl.addAll(allObAsList);
        assertEquals("Bad object returned", tl.get(0), obj1);
        assertEquals("Bad object returned", tl.get(3), obj4);
        assertEquals("Bad object returned", tl.get(8), obj9);

        tl.set(0, obj3);
        assertEquals("Bad object returned", tl.get(0), obj3);
        tl.set(8, obj4);
        assertEquals("Bad object returned", tl.get(8), obj4);
        tl.set(3, obj8);
        assertEquals("Bad object returned", tl.get(3), obj8);
    }

    @Test
    public void testAddToPosition() {
        tl.addAll(allObAsList);

        assertEquals("Bad object returned.", tl.get(0), obj1);
        assertEquals("Bad object returned.", tl.get(8), obj9);
        assertEquals("Bad object returned.", tl.get(4), obj5);

        tl.add(0, obj3);
        tl.add(4, obj2);
        tl.add(10, obj1);

        assertEquals("Bad object returned.", tl.get(0), obj3);
        assertEquals("Bad object returned.", tl.get(10), obj1);
        assertEquals("Bad object returned.", tl.get(4), obj2);
    }

    @Test
    public void testIndexOf() {
        tl.addAll(allObAsList);
        tl.addAll(allObAsList);

        assertEquals("Bad index.", tl.indexOf(obj1), 0);
        assertEquals("Bad index.", tl.indexOf(obj9), 8);
        assertEquals("Bad index.", tl.indexOf(obj2), 1);
        assertEquals("Bad index.", tl.indexOf(obj6), 5);

        tl.remove(0);
        assertEquals("Bad index.", tl.indexOf(obj9), 7);
        assertEquals("Bad index.", tl.indexOf(obj2), 0);
        assertEquals("Bad index.", tl.indexOf(obj6), 4);

        tl.add(3, obj7);
        assertEquals("Bad index.", tl.indexOf(obj9), 8);
        assertEquals("Bad index.", tl.indexOf(obj2), 0);
        assertEquals("Bad index.", tl.indexOf(obj6), 5);
    }

    @Test
    public void testLastIndexOf() {
        tl.add(obj1); //0
        tl.add(obj2);
        tl.add(obj3);
        tl.add(obj4);
        tl.add(obj1); //4
        tl.add(obj5);
        tl.add(obj1); //6
        tl.add(obj7);

        assertEquals("Bad index.", tl.lastIndexOf(obj1), 6);

        tl.remove(6);
        assertEquals("Bad index.", tl.lastIndexOf(obj1), 4);

        tl.remove(2);
        assertEquals("Bad index.", tl.lastIndexOf(obj1), 3);

    }

    @Test
    public void testListIterator() {
        tl.addAll(allObAsList);
        ListIterator<SimpleObject> iter = tl.listIterator();
        testSimpleIterator(tl, allObAsList, iter);
        tl.clear();
        tl.addAll(allObAsList);

        iter = tl.listIterator();
        assertFalse("Element before first element", iter.hasPrevious());
        assertEquals("Bad returned index.", iter.nextIndex(), 0);
        assertEquals("Bad returned index.", iter.previousIndex(), -1);
        assertEquals("Bad element returned", iter.next(), obj1);
        assertEquals("Bad returned index.", iter.nextIndex(), 1);
        assertEquals("Bad returned index.", iter.previousIndex(), 0);
        iter.remove();
        assertEquals("Bad returned index.", iter.nextIndex(), 0);
        assertEquals("Bad returned index.", iter.previousIndex(), -1);
        assertFalse("Element before first element", iter.hasPrevious());
        assertTrue("No next element", iter.hasNext());
        assertEquals("Bad element returned", iter.next(), obj2);
        assertEquals("Bad element returned", iter.next(), obj3);
        assertEquals("Bad element returned", iter.previous(), obj3);
        assertEquals("Bad element returned", iter.next(), obj3);
        assertEquals("Bad returned index.", iter.nextIndex(), 2);
        assertEquals("Bad returned index.", iter.previousIndex(), 1);
        iter.add(obj9);
        assertEquals("Bad returned index.", iter.nextIndex(), 3);
        assertEquals("Bad returned index.", iter.previousIndex(), 2);
        assertEquals("Bad element returned", iter.previous(), obj9);
        assertEquals("Bad returned index.", iter.nextIndex(), 2);
        assertEquals("Bad returned index.", iter.previousIndex(), 1);
        iter.add(obj6);
        assertEquals("Bad returned index.", iter.nextIndex(), 3);
        assertEquals("Bad returned index.", iter.previousIndex(), 2);
        assertEquals("Bad element returned", iter.previous(), obj6);
        assertEquals("Bad element returned", iter.next(), obj6);
        assertEquals("Bad element returned", iter.next(), obj9);
        assertEquals("Bad element returned", iter.next(), obj4);
        assertEquals("Bad element returned", iter.next(), obj5);
        assertEquals("Bad element returned", iter.next(), obj6);
        assertEquals("Bad returned index.", iter.nextIndex(), 7);
        assertEquals("Bad returned index.", iter.previousIndex(), 6);
        iter.remove();
        assertEquals("Bad returned index.", iter.nextIndex(), 6);
        assertEquals("Bad returned index.", iter.previousIndex(), 5);
        assertEquals("Bad element returned", iter.previous(), obj5);
        assertEquals("Bad element returned", iter.next(), obj5);
        assertEquals("Bad element returned", iter.next(), obj7);
        assertEquals("Bad returned index.", iter.nextIndex(), 7);
        assertEquals("Bad returned index.", iter.previousIndex(), 6);
        iter.set(obj1);
        assertEquals("Bad returned index.", iter.nextIndex(), 7);
        assertEquals("Bad returned index.", iter.previousIndex(), 6);
        assertEquals("Bad element returned", iter.previous(), obj1);
        assertEquals("Bad element returned", iter.next(), obj1);
        assertEquals("Bad element returned", iter.next(), obj8);
        assertEquals("Bad element returned", iter.next(), obj9);
        assertEquals("Bad element returned", iter.previous(), obj9);
        assertEquals("Bad element returned", iter.previous(), obj8);
        iter.set(obj2);
        assertEquals("Bad element returned", iter.next(), obj2);
        assertEquals("Bad element returned", iter.next(), obj9);
        assertTrue("Element before first element", iter.hasPrevious());
        assertFalse("Element after last element", iter.hasNext());

        assertArrayEquals("Not same arrays.", tl.toArray(new SimpleObject[0]),
                new SimpleObject[]{obj2, obj3, obj6, obj9, obj4, obj5, obj1, obj2, obj9});
    }

    @Test
    public void testListIteratorFromIndex() {
        tl.addAll(allObAsList);
        ListIterator<SimpleObject> iter = tl.listIterator(3);
        testSimpleIterator(tl, allObAsList.subList(3, allObAsList.size()), iter);
        
        tl.clear();
        tl.addAll(allObAsList);
        
        iter = tl.listIterator(3);
        assertTrue("No previous item.", iter.hasPrevious());
        assertTrue("No next item.", iter.hasNext());
        assertEquals("Bad returned index.", iter.nextIndex(), 3);
        assertEquals("Bad returned index.", iter.previousIndex(), 2);
        assertEquals("Bad returned element.", iter.previous(), obj3);
        assertEquals("Bad returned element.", iter.next(), obj3);
        assertEquals("Bad returned element.", iter.next(), obj4);
        assertEquals("Bad returned index.", iter.nextIndex(), 4);
        assertEquals("Bad returned index.", iter.previousIndex(), 3);
        iter.set(obj9);
        assertEquals("Bad returned index.", iter.nextIndex(), 4);
        assertEquals("Bad returned index.", iter.previousIndex(), 3);
        assertEquals("Bad returned element.", iter.previous(), obj9);
        assertEquals("Bad returned index.", iter.nextIndex(), 3);
        assertEquals("Bad returned index.", iter.previousIndex(), 2);
        assertEquals("Bad returned element.", iter.next(), obj9);
        assertEquals("Bad returned index.", iter.nextIndex(), 4);
        assertEquals("Bad returned index.", iter.previousIndex(), 3);
        assertEquals("Bad returned element.", iter.next(), obj5);
        assertEquals("Bad returned index.", iter.nextIndex(), 5);
        assertEquals("Bad returned index.", iter.previousIndex(), 4);
        iter.remove();
        assertEquals("Bad returned index.", iter.nextIndex(), 4);
        assertEquals("Bad returned index.", iter.previousIndex(), 3);
        assertEquals("Bad returned element.", iter.next(), obj6);
        assertEquals("Bad returned element.", iter.previous(), obj6);
        assertEquals("Bad returned element.", iter.previous(), obj9);
        assertEquals("Bad returned index.", iter.nextIndex(), 3);
        assertEquals("Bad returned index.", iter.previousIndex(), 2);
        iter.remove();
        assertEquals("Bad returned index.", iter.nextIndex(), 3);
        assertEquals("Bad returned index.", iter.previousIndex(), 2);
        assertEquals("Bad returned element.", iter.next(), obj6);
        assertEquals("Bad returned element.", iter.next(), obj7);
        assertEquals("Bad returned index.", iter.nextIndex(), 5);
        assertEquals("Bad returned index.", iter.previousIndex(), 4);
        iter.add(obj1);
        assertEquals("Bad returned index.", iter.nextIndex(), 6);
        assertEquals("Bad returned index.", iter.previousIndex(), 5);
        assertEquals("Bad returned element.", iter.previous(), obj1);
        assertEquals("Bad returned element.", iter.previous(), obj7);
        assertEquals("Bad returned element.", iter.next(), obj7);
        assertEquals("Bad returned element.", iter.next(), obj1);
        assertEquals("Bad returned element.", iter.next(), obj8);
        assertEquals("Bad returned element.", iter.next(), obj9);
        assertFalse("No previous item.", iter.hasNext());
        assertEquals("Bad returned index.", iter.nextIndex(), 8);
        
        assertArrayEquals("Not same arrays.", tl.toArray(new SimpleObject[0]),
                new SimpleObject[]{obj1, obj2, obj3, obj6, obj7, obj1, obj8, obj9});
    }

    @Test
    public void testSubList() {
        List<SimpleObject> list = new LinkedList<>(Arrays.asList(
                new SimpleObject[] {obj3, obj4, obj5, obj6}));
        tl.addAll(allObAsList);
        List<SimpleObject> subList = tl.subList(2, 6);
        testSimpleIterator(subList, list, null);
        
        tl.clear();
        tl.addAll(allObAsList);
        
        //listIterator(index)
        subList = tl.subList(2, 6);
        testSimpleIterator(subList, list.subList(1, list.size()), subList.listIterator(1));
        
        tl.clear();
        tl.addAll(allObAsList);
        
        //listIterator
        subList = tl.subList(2, 6);
        testSimpleIterator(subList, list, subList.listIterator());
        
        tl.clear();
        tl.addAll(allObAsList);
        
        subList = tl.subList(2, 6);
        //size
        assertEquals("Bad size.", subList.size(), 4);
        
        //containsAll
        assertFalse("List should not contains this elements.", subList.containsAll(
                Arrays.asList(new SimpleObject[] {obj2, obj3, obj4})));
        assertTrue("List should contains this elements.", subList.containsAll(
                Arrays.asList(new SimpleObject[] {obj3, obj4})));
        
        //add
        subList.add(obj4);
        list.add(obj4);
        assertTrue("Lists are not equals.", list.equals(subList));
        assertTrue("Lists are not equals.", tl.equals(
                Arrays.asList(new SimpleObject[] {  obj1, obj2, obj3, obj4, obj5, 
                                                    obj6, obj4, obj7, obj8, obj9})));
        //add(index, Object)
        subList.add(2, obj9);
        list.add(2, obj9);
        assertTrue("Lists are not equals.", list.equals(subList));
        assertTrue("Lists are not equals.", tl.equals(
                Arrays.asList(new SimpleObject[] {  obj1, obj2, obj3, obj4, obj9,
                                                    obj5, obj6, obj4, obj7, obj8, obj9})));
        
        //remove(index)
        assertEquals("Bad returned element.", subList.remove(3), obj5);
        list.remove(3);
        assertTrue("Lists are not equals.", list.equals(subList));
        assertTrue("Lists are not equals.", tl.equals(
                Arrays.asList(new SimpleObject[] {  obj1, obj2, obj3, obj4, obj9,
                                                    obj6, obj4, obj7, obj8, obj9})));
        //remove(Object)
        assertTrue("Element not removed.", subList.remove(obj3));
        assertFalse("Missing element removed.", subList.remove(obj1));
        list.remove(obj3);
        assertTrue("Lists are not equals.", list.equals(subList));
        assertTrue("Lists are not equals.", tl.equals(
                Arrays.asList(new SimpleObject[] {  obj1, obj2, obj4, obj9,
                                                    obj6, obj4, obj7, obj8, obj9})));
        //indexOf, lastIndexOf
        assertEquals("Bad returned index.", subList.indexOf(obj1), -1);
        assertEquals("Bad returned index.", subList.indexOf(obj4), 0);
        assertEquals("Bad returned index.", subList.lastIndexOf(obj4), 3);
        assertEquals("Bad returned index.", subList.indexOf(obj6), 2);
        //get
        assertEquals("Bad returned element.", subList.get(0), obj4);
        assertEquals("Bad returned element.", subList.get(1), obj9);
        //isEmpty
        assertFalse("List is empty.", subList.isEmpty());
        //contains, set
        assertFalse("List contains element.", subList.contains(obj8));
        assertEquals("Bad returned element.", subList.set(1, obj8), obj9);
        assertTrue("List does not contain element.", subList.contains(obj8));
        
        list.set(1, obj8);
        assertTrue("Lists are not equals.", list.equals(subList));
        assertTrue("Lists are not equals.", tl.equals(
                Arrays.asList(new SimpleObject[] {  obj1, obj2, obj4, obj8,
                                                    obj6, obj4, obj7, obj8, obj9})));
        //addAll
        subList.addAll(Arrays.asList(new SimpleObject[] {obj1, obj7}));
        list.addAll(Arrays.asList(new SimpleObject[] {obj1, obj7}));
        assertTrue("Lists are not equals.", list.equals(subList));
        assertTrue("Lists are not equals.", tl.equals(
                Arrays.asList(new SimpleObject[] {  obj1, obj2, obj4, obj8,
                                                    obj6, obj4, obj1, obj7,
                                                    obj7, obj8, obj9})));
        //addAll(index)
        subList.addAll(1, Arrays.asList(new SimpleObject[] {obj1, obj7}));
        list.addAll(1, Arrays.asList(new SimpleObject[] {obj1, obj7}));
        assertTrue("Lists are not equals.", list.equals(subList));
        assertTrue("Lists are not equals.", tl.equals(
                Arrays.asList(new SimpleObject[] {  obj1, obj2, obj4, obj1, obj7, obj8,
                                                    obj6, obj4, obj1, obj7,
                                                    obj7, obj8, obj9})));
        //removeAll
        subList.removeAll(Arrays.asList(new SimpleObject[] {obj1, obj7}));
        list.removeAll(Arrays.asList(new SimpleObject[] {obj1, obj7}));
        assertTrue("Lists are not equals.", list.equals(subList));
        assertTrue("Lists are not equals.", tl.equals(
                Arrays.asList(new SimpleObject[] {  obj1, obj2, obj4, obj8,
                                                    obj6, obj4,
                                                    obj7, obj8, obj9})));
        //retainAll
        subList.retainAll(Arrays.asList(new SimpleObject[] {obj4, obj6}));
        list.retainAll(Arrays.asList(new SimpleObject[] {obj4, obj6}));
        assertTrue("Lists are not equals.", list.equals(subList));
        assertTrue("Lists are not equals.", tl.equals(
                Arrays.asList(new SimpleObject[] {  obj1, obj2, obj4,
                                                    obj6, obj4,
                                                    obj7, obj8, obj9})));
        //sublist
        assertTrue("Lists are not equals.", 
                list.subList(1, list.size())
                    .equals(subList.subList(1, subList.size())));
        //toArray
        assertTrue("Not same arrays.", Arrays.equals(list.toArray(), subList.toArray()));
        //toArray typed
        assertTrue("Not same arrays.", Arrays.equals(
                list.toArray(new SimpleObject[0]),
                subList.toArray(new SimpleObject[0])));
        assertTrue("Not same arrays.", Arrays.equals(
                list.toArray(new SimpleObject[10]),
                subList.toArray(new SimpleObject[10])));
        
        //clear
        subList.clear();
        list.clear();
        assertTrue("Lists are not equals.", list.equals(subList));
        assertTrue("Lists are not equals.", tl.equals(
                Arrays.asList(new SimpleObject[] {  obj1, obj2, obj7, obj8, obj9})));
        assertTrue("List is not empty.", subList.isEmpty());
    }

    /**
     * Otestuje jednoduchý iterátor.
     * 
     * @param list testovaný zoznam a jeho iterátor
     * @param shouldContaint zoznam prvkov, ktoré majú byť prítomné v 
     *      kontorlovanom zozname
     * @param listIterator inicializovaný iterátor pre zoznam {@code list}.
     *      Ak je {@code null}, tak bude vytvorený nový iterátor. Ak nie, tak
     *      bude použitý tento iterátor.
     */
    private void testSimpleIterator(List<SimpleObject> list, List<SimpleObject> shouldContaint, Iterator<SimpleObject> listIterator) {
        Iterator<SimpleObject> iter = listIterator == null ? list.iterator() : listIterator;
        int i = Math.min(3, shouldContaint.size() - 1);
        SimpleObject toRemove = shouldContaint.get(i);
        for (Object o : shouldContaint) {
            assertTrue("Too few elements", iter.hasNext());
            Object tlo = iter.next();
            assertEquals("Bad object or bad order.", tlo, o);
            if (tlo.equals(toRemove)) {
                iter.remove();
            }
        }
        assertFalse("Too much elements", iter.hasNext());
        assertFalse("Iterator.remove did not remove object", list.contains(toRemove));
    }

    private static class SimpleObject {
    }
}
