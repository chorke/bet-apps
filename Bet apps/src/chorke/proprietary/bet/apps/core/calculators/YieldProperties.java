
package chorke.proprietary.bet.apps.core.calculators;

import chorke.proprietary.bet.apps.core.Tuple;
import java.math.BigDecimal;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 *
 * @author Chorke
 */
public class YieldProperties {
    
    private static final BigDecimal ZERO_POINT_ZERO_ONE = new BigDecimal("0.01");
    private static final BigDecimal MAX_VALUE = new BigDecimal(Double.MAX_VALUE);
    
    private List<BigDecimal> scale;
    private String betCompany;
    private Map<String, Object> properties;
    
    public YieldProperties(){
        this(null, "", new HashMap<String, Object>());
    }

    public YieldProperties(List<BigDecimal> scale, String betCompany) {
        this(scale, betCompany, new HashMap<String, Object>());
    }

    public YieldProperties(List<BigDecimal> scale, String betCompany, Map<String, Object> properties) {
        setScale(scale);
        this.betCompany = betCompany;
        this.properties = properties;
    }

    
    public String getBetCompany() {
        return betCompany;
    }

    public void setBetCompany(String betCompany) {
        this.betCompany = betCompany;
    }

    /**
     * Vráti property so špecifickým názvom alebo {@code null} ak neexistuje 
     * {@code name}.
     * 
     * @param name
     * @return 
     */
    public Object getProperty(String name){
        return properties.get(name);
    }
    
    /**
     * Uloží property s názvom {@code name} a hodnotou {@code property}.
     * 
     * @param name
     * @param property 
     */
    public void setProperty(String name, Object property){
        properties.put(name, property);
    }
    
    /**
     * Odstráni property s názvom {@code name}.
     * @param name 
     */
    public void clearProperty(String name){
        properties.remove(name);
    }
    
    /**
     * Vráti hranice pre rozsahy stávok podľa {@code index}. 
     * Rozsahy sú indexované od 0.
     * 
     * Napríklad, ak {@code scale} obsahuje prvky [1.45, 1.67, 1.99, 2.05],
     * tak:
     * <ul>
     * <li>{@code getScale(0)} vráti {@code <1.00, 1.45>} </li>
     * <li>{@code getScale(2)} vráti {@code <1.68, 1.99>} </li>
     * <li>{@code getScale(4)} vráti {@code <2.06, Double.MAX_VALUE>} </li>
     * </ul>
     * 
     * Ak je {@code scale} prázdna, tak {@code getScale(0)} vráti {@code <1.00, Double.MAX_VALUE>}
     * 
     * @param index
     * @return 
     * @throws IndexOutOfBoundsException Ak je 
     * {@code index > scale.size() || index < 0}.
     */
    public Tuple<BigDecimal, BigDecimal> getRangeForIndex(int index) throws IndexOutOfBoundsException{
        if(index > scale.size() || index < 0){
            throw new IndexOutOfBoundsException("Index out of bounds " + index);
        }
        if(index == 0){
            return new Tuple<>(BigDecimal.ONE, scale.isEmpty() ? BigDecimal.ONE : scale.get(0));
        } else if(index == scale.size()){
            return new Tuple<>(scale.get(index - 1).add(ZERO_POINT_ZERO_ONE), MAX_VALUE);
        } else {
            return new Tuple<>(scale.get(index - 1).add(ZERO_POINT_ZERO_ONE), scale.get(index));
        }
    }
    
    /**
     * Vráti všetky rozsahy podľa aktuálnych nastavení. 
     * Poradie zodpovedná poradiu indexov (0, 1, 2, ...)
     * 
     * @return rozsahy pre aktuálne nastavenie
     * 
     * @see addScale(BigDecimal)
     * @see removeScale(BigDecimal)
     * @see getScale()
     * @see setScale(List)
     */
    public List<Tuple<BigDecimal, BigDecimal>> getRanges(){
        List<Tuple<BigDecimal, BigDecimal>> out = new LinkedList<>();
        for(int i = 0; i <= scale.size(); i++){
            out.add(getRangeForIndex(i));
        }
        return out;
    }

    /**
     * Vráti index pre rozsahy stávok podľa rozsahu {@code range}. 
     * Rozsahy sú indexované od 0.
     * 
     * Napríklad, ak {@code scale} obsahuje prvky [1.45, 1.67],
     * tak:
     * <ul>
     * <li>{@code getScale(<1.00, 1.45>)} vráti {@code 0} </li>
     * <li>{@code getScale(<1.46, 1.67>)} vráti {@code 1} </li>
     * <li>{@code getScale(<1.68, Double.MAX_VALUE>)} vráti {@code 2} </li>
     * <li>iná kombinácia spôsobí {@link IllegalArgumentException}</li>
     * </ul>
     * 
     * Ak je {@code scale} prázdna, tak {@code getScale(0)} vráti {@code <1.00, Double.MAX_VALUE>}
     * 
     * @param index
     * @return 
     * @throws IllegalArgumentException Ak {@code range} nie je platný rozsah.
     */
    public int getIndexForRange(Tuple<BigDecimal, BigDecimal> range) throws IllegalArgumentException{
        if(range.second.equals(MAX_VALUE)){
            if(!scale.get(scale.size() - 1).add(ZERO_POINT_ZERO_ONE).equals(range.first)){
                throw new IllegalArgumentException("Range not in scale " + range);
            }
            return scale.size();
        }
        if(range.first.equals(BigDecimal.ONE)){
            if(!scale.get(0).equals(range.second)){
                throw new IllegalArgumentException("Range not in scale " + range);
            }
            return 0;
        }
        int idx = scale.indexOf(range.second);
        if(idx <= 0){
            throw new IllegalArgumentException("Range not in scale " + range);
        }
        if(!scale.get(idx - 1).add(ZERO_POINT_ZERO_ONE).equals(range.first)){
            throw new IllegalArgumentException("Range not in scale " + range);
        }
        return idx;
    }
    
    public List<BigDecimal> getScale() {
        return Collections.unmodifiableList(scale);
    }
    
    /**
     * Pridá ďalšiu hranicu ne škálu. Podľa nich sú následne počítané štatistiky.
     * @param d 
     */
    public void addScale(BigDecimal d){
        if(!scale.contains(d)){
            for(int i = 0; i < scale.size(); i++){
                if(scale.get(i).compareTo(d) > 0){
                    scale.add(i, d);
                    return;
                }
            }
            scale.add(d);
        }
    }
    
    /**
     * Odstráni hranicu zo škály.
     * @param d 
     */
    public void removeScale(BigDecimal d){
        if(d != null){
            for(int i = 0; i < scale.size(); i++){
                if(scale.get(i).compareTo(d) == 0){
                    scale.remove(i);
                    return;
                }
            }
        }
    }
    
    /**
     * Ak je {@code scale == null}, vytvorí sa prázdna vnútorná {@code scale}.
     * 
     * @param scale 
     */
    public final void setScale(List<BigDecimal> scale){
        if(scale == null){
            if(this.scale == null){
                this.scale = new LinkedList<>();
            } else {
                this.scale.clear();
            }
        } else {
            if(this.scale == null){
                this.scale = new LinkedList<>();
            } else {
                this.scale.clear();
            }
            for(BigDecimal d : scale){
                addScale(d);
            }
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null
                || !(obj instanceof YieldProperties)){
            return false;
        }
        YieldProperties yp = (YieldProperties) obj;
        if(!this.betCompany.equals(yp.betCompany)
                || !this.properties.equals(yp.properties)){
            return false;
        }
        return this.scale.equals(yp.scale);
    }

    @Override
    public int hashCode() {
        return 31 * this.betCompany.hashCode() 
                * this.properties.hashCode()
                * this.scale.hashCode();
    }
}