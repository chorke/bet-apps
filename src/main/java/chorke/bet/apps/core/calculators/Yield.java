
package chorke.bet.apps.core.calculators;

import chorke.bet.apps.core.CoreUtils.BetPossibility;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Trieda reprezentujúca zisk.
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
    private Map<Object, Integer> matches;
    
    /**
     * Vytvorí novú inštanciu.
     */
    protected Yield() {
        properties = new YieldProperties();
        matches = new HashMap<>();
    }

    /**
     * Vráti aktuálne nastavené properties.
     */
    public YieldProperties getProperties() {
        return properties;
    }

    /**
     * Nastaví properties.
     * Ak {@code properties == null}, tak vytvorí nové, prázdne properties.
     * 
     * @param properties properties pre nastavenie
     */
    public void setProperties(YieldProperties properties) {
        if(!this.properties.equals(properties)){
            if(properties == null){
                this.properties = new YieldProperties();
            } else {
                this.properties = properties;
            }
            clear();
        }
    }
    
    /**
     * Vyčistí aktuálne hodnoty ziskov
     */
    public abstract void clear();
    
    /**
     * Vráti zisky podľa stávkových možností {@code betPossibility}. 
     * 
     * @param betPossibility
     * @return
     * @throws IllegalArgumentException ak {@code betPossibility} nie je podporovaná
     * @see #getSupportedBetPossibilities() 
     */
    public Map<Object, BigDecimal> getYields(BetPossibility betPossibility)
            throws IllegalArgumentException{
        return Collections.unmodifiableMap(getRequiredMap(betPossibility));
    }
    
    /**
     * Pridá {@code yield} pre zvolenú možnosť stávky {@code betPossibility}.
     * 
     * @param betPossibility
     * @param index
     * @param yield
     * @throws IndexOutOfBoundsException ak {@code index} nie je v medziach pre aktuálne 
     *      {@code properties}.
     * @throws IllegalArgumentException ak {@code betPossibility} nie je podporovaná
     * @see #getSupportedBetPossibilities() 
     * @see #Yield#getProperties()
     * @see #Yield#setProperties(YieldProperties)
     */
    public void addYieldForKey(BetPossibility betPossibility, Object key, BigDecimal yield)
            throws IllegalArgumentException {
        getRequiredMap(betPossibility).put(key, yield);
    }
    
    /**
     * Pridá počet zápasov pre stanovený rozsah stávok (podľa index).
     * @param key
     * @param count
     * @throws IndexOutOfBoundsException ak je index menší ako 0 alebo väčší
     * ako počet rozsahov.
     */
    public void addMatchesCountForKey(Object key, int count){
        if(count < 0){
            throw new IllegalArgumentException("Count cannot be negative [" + count + "]");
        }
        matches.put(key, count);
    }
    
    /**
     * Vráti počet zápasov, ktoré sú zahrnuté pri počítaní zisku. Je na 
     * programátorovi, aby zaručil konzistenciu. Počet nie je nijako 
     * kontrolovaný.
     * 
     * Ak nie je nastavený počet pre zadaný index, alebo ak je index
     * mimo rozsah, je vrátená 0.
     * 
     * @param index
     * @return 
     */
    public int getMatchesCountForKey(Object key){
        Integer i = matches.get(key);
        return i == null ? 0 : i.intValue();
    }
    
    /**
     * Pridá zisk do kolekcie. Ak kolekcia už obsahuje index, tak ho nahradí.
     * 
     * @param list
     * @param index
     */
//    private void addYieldForKeyInner(Map<Object, BigDecimal> list, 
//            Object key, BigDecimal yield) {
//        list.put(key, yield);
//    }
    
    
    /**
     * Vráti zisk pre zvolenú možnosť stávky {@code betPossibility}.
     * 
     * @param betPossibility
     * @param index 
     * 
     * @throws IllegalArgumentException ak {@code betPossibility} nie je 
     *      podporovaná
     */
    public BigDecimal getYieldForKey(BetPossibility betPossibility,
            Object key, boolean percentage)
            throws IllegalArgumentException {
        BigDecimal yield = getRequiredMap(betPossibility).get(key);
        if(yield == null){
            return BigDecimal.ZERO;
        }
        if(percentage){
            return percentage(yield, getMatchesCountForKey(key));
        } else {
            return yield;
        }
    }
    
    private BigDecimal percentage(BigDecimal yield, int matchesCount){
        if(matchesCount <= 0){
            return BigDecimal.ZERO;
        }
        return yield
            .divide(new BigDecimal(matchesCount), new MathContext(2))
            .multiply(new BigDecimal("100"));
    }
    
    /**
     * String, pre zvolenú kolekciu ziskov.
     * @param toString
     * @return 
     */
    protected String stringYield(Map<Object, BigDecimal> toString){
        StringBuilder sb = new StringBuilder();
        for(Entry<Object, BigDecimal> entry : toString.entrySet()){
            Object key = entry.getKey();
            sb.append("{index=").append(key)
                    .append(", yield=").append(entry.getValue())
                    .append(", percentage=").append(percentage(entry.getValue(),
                            getMatchesCountForKey(key)))
                    .append("%, count=").append(getMatchesCountForKey(key))
                .append("}");
        }
        return sb.toString();
    }
    
    /**
     * Vráti podporované možnosti stávky pre tento zisk.
     * 
     * @return 
     */
    public abstract BetPossibility[] getSupportedBetPossibilities();
    
    /**
     * Vráti požadovanú mapu, podľa betPossibillity a akutálne nastaveným 
     * YieldProperties.
     * 
     * @param betPossibility
     * @return 
     * @throws IllegalArgumentException ak nie je betPossibility podporovaná
     */
    protected abstract Map<Object, BigDecimal> getRequiredMap(BetPossibility betPossibility)
            throws IllegalArgumentException;
}
