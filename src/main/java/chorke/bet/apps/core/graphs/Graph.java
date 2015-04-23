
package chorke.bet.apps.core.graphs;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Graf hodnôt. Hodnoty sú reprezentované ako {@link BigDecimal}.
 * 
 * @author Chorke
 */
public class Graph implements Cloneable{
    
    private List<BigDecimal> values = new LinkedList<>();
    private BigDecimal min = null;
    private BigDecimal max = null;

    /**
     * Aktuálne minimum v grafe. Ak je graf prázdny, je vrátené null.
     * 
     * @return 
     */
    public BigDecimal getMin() {
        return min;
    }

    /**
     * Aktuálne maximum v grafe. Ak je graf prázdny, je vrátené null.
     * 
     * @return 
     */
    public BigDecimal getMax() {
        return max;
    }

    /**
     * Aktuálne hodnoty v grafe. Ak je graf prázdny, je vrátený prázdny zoznam.
     * 
     * @return nemodifikovateľnú kolekciu s aktuálnymi hodnotami.
     */
    public List<BigDecimal> getValues() {
        return Collections.unmodifiableList(values);
    }
    
    /**
     * Pridá hodnotu value ako ďalšiu hodnotu v grafe. Je pridaná na koniec
     * zoznamu.
     * Ak je value rovné null, tak je vyhodená {@link IllegalArgumentException}.
     * 
     * @param value 
     */
    public void add(BigDecimal value){
        add(value, values.size());
    }
    
    /**
     * Pridá hodnotu value ako ďalšiu hodnotu v grafe. Je pridaná na pozíciu
     * idx. Ak je idx menšia ako 0 resp. väčšia ako aktuálny počet
     * hodnôt, je vyhodná výnimka {@link IndexOutOfBoundsException}.
     * Ak je value rovné null, tak je vyhodená {@link IllegalArgumentException}.
     * 
     * @param value 
     * @param idx 
     */
    public void add(BigDecimal value, int idx){
        if(value == null){
            throw new IllegalArgumentException("Value cannot be null.");
        }
        checkMax(value);
        checkMin(value);
        checkPosition(idx);
        values.add(idx, value);
    }
    
    /**
     * Odstráni hodnotu z pozície idx. Ak je idx menšie ako 0 alebo väčšie
     * ako aktuálny počet prvkov, je vyhodená výnimka {@link IndexOutOfBoundsException}.
     * 
     * @param idx
     * @return 
     */
    public BigDecimal remove(int idx){
        checkPosition(idx);
        if(idx == values.size()){
            throw new IndexOutOfBoundsException("Index cannot be equal to actual values length "
                    + "(length = " + values.size() + ", index = " + idx + ")");
        }
        BigDecimal value = values.remove(idx);
        if(value.compareTo(min) == 0){
            findMin();
        }
        if(value.compareTo(max) == 0){
            findMax();
        }
        return value;
    }
    
    /**
     * Nájden a nastaví nové minimum. Ak je values prázdna, potom min bude null.
     */
    private void findMin(){
        min = null;
        for(BigDecimal val : values){
            checkMin(val);
        }
    }
    
    /**
     * Nájden a nastaví nové maximum. Ak je values prázdna, potom min bude null.
     */
    private void findMax(){
        max = null;
        for(BigDecimal val : values){
            checkMax(val);
        }
    }
    
    /**
     * Odstráni všetky hodnoty.
     */
    public void clear(){
        values.clear();
        min = null;
        max = null;
    }
    
    /**
     * Skontroluje pozíciu. Ak je pozícia menšia ako 0 alebo väčšia ako 
     * počet hodnôt, je vyhodená výnimka {@link IndexOutOfBoundsException}.
     * Ak idx rovné values.size(), potom nie je vyhodná výnimka.
     * @param idx 
     */
    private void checkPosition(int idx){
        if(idx < 0){
            throw new IndexOutOfBoundsException("Index cannot be less then 0");
        }
        if(idx > values.size()){
            throw new IndexOutOfBoundsException("Index cannot be greater then actual values length "
                    + "(length = " + values.size() + ", index = " + idx + ")");
        }
    }
    
    /**
     * Skontroluje, či je hodnota value menšia ako aktuálne minimum. Ak áno,
     * tak upraví aktuálne minimum na novú hodnotu. 
     * 
     * @param value 
     */
    private void checkMin(BigDecimal value){
        if(min == null || value.compareTo(min) <= -1){
            min = value;
        }
    }
    
    /**
     * Skontroluje, či je hodnota value menšia ako aktuálne maximum. Ak áno,
     * tak upraví aktuálne maximum na novú hodnotu. 
     * 
     * @param value 
     */
    private void checkMax(BigDecimal value){
        if(max == null || value.compareTo(max) >= 1){
            max = value;
        }
    }
    
    /**
     * Vráti klon. Obzvlášť zoznam values je nový a teda sa klon nebude
     * ovplyvňovať s pôvodným objektom pri pridávaní a odoberaní hodnôt.
     * @return 
     */
    public Graph getDeepClone(){
        try{
            Graph clone = (Graph)super.clone();
            clone.values = new LinkedList<>();
            for(BigDecimal val : values){
                clone.values.add(val);
            }
            return clone;
        } catch (CloneNotSupportedException ex){}
        return null;
    }
    
    /**
     * Vráti aktálny počet hodnôt.
     * 
     * @return 
     */
    public int valuesCount(){
        return values.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Graph:  ");
        for(BigDecimal bd : values){
            sb.append(bd).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }
}
