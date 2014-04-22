
package chorke.proprietary.bet.apps.core.calculators;

import chorke.proprietary.bet.apps.StaticConstants.BetPossibility;
import chorke.proprietary.bet.apps.core.Tuple;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Chorke
 */
public abstract class Yield {
    
    /**
     * Požadované nastavenia pri počítaní zisku.
     */
    protected YieldProperties properties;

    /**
     * Počet zápasov. Index rozsahu a počet.
     */
    private Map<Integer, Integer> matches;
    
    public Yield() {
        properties = new YieldProperties();
        matches = new HashMap<>();
    }

    public YieldProperties getProperties() {
        return properties;
    }

    /**
     * Ak {@code properties == null}, tak vytvorí nové, prázdne properties.
     * 
     * @param properties 
     */
    public void setProperties(YieldProperties properties) {
        if(properties == null){
            this.properties = new YieldProperties();
        } else {
            this.properties = properties;
        }
    }
    
    /**
     * Vráti zisky podľa stávkových možností {@code betPossibility}. 
     * Podporované možnosti sú {@link BetPossibility#Favorit},
     * {@link BetPossibility#Guest}, {@link BetPossibility#Home}, 
     * {@link BetPossibility#Looser}, {@link BetPossibility#Tie}.
     * 
     * @param betPossibility
     * @return
     * @throws IllegalStateException ak {@code betPossibility} nie je podporovaná
     */
    public Map<Integer, BigDecimal> getYields(BetPossibility betPossibility)
            throws IllegalArgumentException{
        return Collections.unmodifiableMap(getRequiredMap(betPossibility));
    }
    
    /**
     * Pridá {@code yield} pre zvolenú možnosť stávky {@code betPossibility}.
     * Podporované možnosti sú {@link BetPossibility#Favorit},
     * {@link BetPossibility#Guest}, {@link BetPossibility#Home}, 
     * {@link BetPossibility#Looser}, {@link BetPossibility#Tie}.
     * 
     * @param betPossibility
     * @param index
     * @param yield
     * @throws IndexOutOfBoundsException ak {@code index} nie je v medziach pre aktuálne 
     *      {@code properties} ({@link Yield#getProperties()}, 
     *          {@link Yield#setProperties(YieldProperties)})
     * @throws IllegalStateException ak {@code betPossibility} nie je podporovaná
     */
    public void addYieldForScaleIndex(BetPossibility betPossibility, int index, BigDecimal yield)
            throws IndexOutOfBoundsException, IllegalStateException {
        addYieldForScaleIndexInner(getRequiredMap(betPossibility), index, yield);
    }
    
    /**
     * Pridá počet zápasov pre stanovený rozsah stávok (podľa index).
     * @param index
     * @param count 
     * @throws IllegalArgumentException Ak je {@code count <= 0};
     */
    public void addMatchesCountForScaleIndex(int index, int count){
        if(index > properties.getScale().size() || index < 0){
            throw new IndexOutOfBoundsException("Index out of bounds [" + index + "]");
        } if(count < 0){
            throw new IllegalArgumentException("Count cannot be negative [" + count + "]");
        }
        matches.put(index, count);
    }
    
    /**
     * Pridá počet zápasov pres stanovaný rozsah stávok (podľa range).
     * @param range
     * @param count 
     */
    public void addMatchesCountForScaleRange(Tuple<BigDecimal, BigDecimal> range, int count){
        try{
            addMatchesCountForScaleIndex(properties.getIndexForRange(range), count);
        } catch (IndexOutOfBoundsException ex){
            //should not happend
            throw new Error("Possible error in method YieldProperties.getIndexForRange(...).", ex);
        }
    }
    
    /**
     * Vráti počet zápasov, ktoré sú zahrnuté pri počítaní zisku. Je na 
     * programátorovi, aby zaručil konzistenciu. Počet nie je nijako 
     * kontrolovaný.
     * 
     * @param index
     * @return 
     */
    public int getMatchesCountForScaleIndex(int index){
        Integer i = matches.get(index);
        return i == null ? 0 : i.intValue();
    }
    
    /**
     * Vráti počet zápasov, ktoré sú zahrnuté pri počítaní zisku. Je na 
     * programátorovi, aby zaručil konzistenciu. Počet nie je nijako 
     * kontrolovaný.
     * 
     * @param range 
     * @return 
     */
    public int getMatchesCountForScaleRange(Tuple<BigDecimal, BigDecimal> range){
        return getMatchesCountForScaleIndex(properties.getIndexForRange(range));
    }
    
    /**
     * Pridá {@code yield} pre zvolenú možnosť stávky {@code betPossibility}.
     * Podporované možnosti sú {@link BetPossibility#Favorit},
     * {@link BetPossibility#Guest}, {@link BetPossibility#Home}, 
     * {@link BetPossibility#Looser}, {@link BetPossibility#Tie}.
     * 
     * @param betPossibility
     * @param range 
     * @param yield
     * 
     * @throws IllegalStateException ak {@code betPossibility} nie je podporovaná,
     *      alebo {@code range} nie je v danom rozsahu pre aktuálne 
     *      {@code properties} ({@link Yield#getProperties()}, 
     *          {@link Yield#setProperties(YieldProperties)})
     */
    public void addYieldForScaleRange(BetPossibility betPossibility,
            Tuple<BigDecimal, BigDecimal> range, BigDecimal yield)
            throws IllegalStateException{
        try{
            addYieldForScaleIndex(betPossibility, properties.getIndexForRange(range), yield);
        } catch (IllegalStateException ex){
            throw new IllegalStateException("Exception while adding", ex);
        } catch (IndexOutOfBoundsException ex){
            //should not happend
            throw new Error("Possible error in method YieldProperties.getIndexForRange(...).", ex);
        }
    }
    
    /**
     * Pridá zisk do kolekcie. Ak kolekcia už obsahuje index, tak ho nahradí.
     * 
     * @param list
     * @param index
     * @throws IndexOutOfBoundsException
     */
    private void addYieldForScaleIndexInner(Map<Integer, BigDecimal> list, 
            int index, BigDecimal yield)
            throws IndexOutOfBoundsException {
        if(index > properties.getScale().size() || index < 0){
            throw new IndexOutOfBoundsException("Index out of bounds [" + index + "]");
        }
        list.put(index, yield);
    }
    
    
    /**
     * Vráti zisk pre zvolenú možnosť stávky {@code betPossibility}.
     * Podporované možnosti sú {@link BetPossibility#Favorit},
     * {@link BetPossibility#Guest}, {@link BetPossibility#Home}, 
     * {@link BetPossibility#Looser}, {@link BetPossibility#Tie}.
     * 
     * @param betPossibility
     * @param index 
     * 
     * @throws IllegalArgumentException ak 
     *      {@code index} nie je v danom rozsahu pre aktuálne 
     *      {@code properties} ({@link Yield#getProperties()}, 
     *          {@link Yield#setProperties(YieldProperties)}), alebo neexistuje
     *      zisk pre daný {@code index}
     * @throws IllegalStateException ak {@code betPossibility} nie je podporovaná
     */
    public BigDecimal getYieldForScaleIndex(BetPossibility betPossibility,
            int index, boolean percentage)
            throws IllegalArgumentException, IllegalStateException {
        return getYieldForScaleIndexInner(getRequiredMap(betPossibility), index, percentage);
    }
    
    /**
     * Vráti zisk pre zvolenú možnosť stávky {@code betPossibility}.
     * Podporované možnosti sú {@link BetPossibility#Favorit},
     * {@link BetPossibility#Guest}, {@link BetPossibility#Home}, 
     * {@link BetPossibility#Looser}, {@link BetPossibility#Tie}.
     * 
     * @param betPossibility
     * @param range 
     * 
     * @throws IllegalArgumentException ak {@code betPossibility} nie je podporovaná,
     *      {@code range} nie je v danom rozsahu pre aktuálne 
     *      {@code properties} ({@link Yield#getProperties()}, 
     *          {@link Yield#setProperties(YieldProperties)}), alebo neexistuje
     *      zisk pre daný {@code range}
     * @throws IllegalStateException ak {@code betPossibility} nie je podporovaná
     */
    public BigDecimal getYieldForScaleRange(BetPossibility possibleWinner, 
            Tuple<BigDecimal, BigDecimal> range, boolean percentage)
            throws IllegalArgumentException, IllegalStateException {
        try{
            return getYieldForScaleIndex(
                    possibleWinner, properties.getIndexForRange(range), percentage);
        } catch (IllegalArgumentException ex){
            throw new IllegalArgumentException("No yield for range [" + range + "]", ex);
        }
    }
    
    /**
     * Vráti zisk pre zvolenú stávkovú kolekciu.
     * 
     * @param list 
     * @param index 
     */
    private BigDecimal getYieldForScaleIndexInner(
            Map<Integer, BigDecimal> list, int index, boolean percentage){
        BigDecimal yield = list.get(index);
        if(yield == null){
            return BigDecimal.ZERO;
        }
        if(percentage){
            int count = getMatchesCountForScaleIndex(index);
            if(count <= 0){
                return BigDecimal.ZERO;
            }
            return yield
                .divide(new BigDecimal(count), new MathContext(2))
                .multiply(new BigDecimal("100"));
        } else {
            return yield;
        }
    }
    
    /**
     * String, pre zvolenú kolekciu ziskov.
     * @param toString
     * @return 
     */
    protected String stringYield(Map<Integer, BigDecimal> toString){
        StringBuilder sb = new StringBuilder();
        for(Integer key : toString.keySet()){
            BigDecimal bd = toString.get(key);
            sb.append("{index=").append(key)
                    .append(", yield=").append(bd)
                    .append(", percentage=").append(getYieldForScaleIndexInner(toString,
                            key, true))
                    .append("%, count=").append(getMatchesCountForScaleIndex(key))
                .append("}");
        }
        return sb.toString();
    }
    
    /**
     * Vráti podporované možnosti stávky pre tento zisk.
     * 
     * @return 
     */
    abstract BetPossibility[] getSupportedBetPossibilities();
    
    /**
     * Vráti požadovanú mapu, podľa betPossibillity a akutálne nastaveným 
     * YieldProperties.
     * 
     * @param betPossibility
     * @return 
     */
    protected abstract Map<Integer, BigDecimal> getRequiredMap(BetPossibility betPossibility);
}
