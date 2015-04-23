
package chorke.proprietary.bet.apps;

import java.math.BigDecimal;
import java.util.Collection;
import static org.junit.Assert.fail;

/**
 *
 * @author Chorke
 */
public class BasicTests {

    /**
     * Vráti true, ak sú oba objekty null. Ak je iba jeden, skončí chybou.
     * Ak sú oba objekty rôzne od null, tak vráti false.
     * 
     * @param o1
     * @param o2
     * @return 
     */
    public static boolean assertAreBothNull(Object o1, Object o2){
        if(o1 == null && o2 == null){ return true; }
        if(o1 == null && o2 != null
                || o1 != null && o2 == null){ fail("Only one is null"); }
        return false;
    }
    
    /**
     * Vráti true, ak sú obe kolekcie vhodné na ďalšie spracovanie.
     * Teda nie sú null a majú rovnakú veľkosť. Inak buď skončí s chybou
     * (nemajú rovnakú veľkosť alebo je iba jedna null.)
     * alebo vráti false (sú obe null).
     * 
     * @param c1
     * @param c2
     * @return 
     */
    public static boolean collectionChecker(Collection c1, Collection c2){
        if(assertAreBothNull(c1, c2)){
            return false;
        }
        if(c1.size() != c2.size()){
            fail("Not same size");
        }
        return true;
    }
    
    /**
     * Porovná dve BigDecimal. Porovnáva pomocou compareTo().
     * @param b1
     * @param b2 
     */
    public static void assertEqualsBigDecimal(BigDecimal b1, BigDecimal b2){
        if(b1.compareTo(b2) != 0){
            fail(b1 + " <--> " + b2);
        }
    }
}
