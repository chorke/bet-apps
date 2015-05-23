
package chorke.bet.apps.core.calculators;

import java.math.BigDecimal;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * Nastavenia pre počítanie ziskov.
 * 
 * @author Chorke
 */
public class YieldProperties {
    
    /**
     * Škála pre zisky. Podľa nej sa stávky zhlukujú spolu.
     */
    private List<BigDecimal> scale;
    /**
     * Názov stávkovej spoločnosti, od ktorej sa berú stávky.
     */
    private String betCompany;
    /**
     * Ostatné nastavenia.
     */
    private Map<String, Object> properties;
    
    /**
     * Vytvorí nové nastavenia zisku s prázdnými rozsahmi, stávkovou spoločnosťou
     * a prázdnymi properties.
     */
    public YieldProperties(){
        this(null, "", new HashMap<String, Object>());
    }

    /**
     * Vytvorí nové nastavenia zisku s rozsahmi scales a stávkovou spoločnosťou
     * betCompany.
     * 
     * @param scale
     * @param betCompany 
     */
    public YieldProperties(List<BigDecimal> scale, String betCompany) {
        this(scale, betCompany, new HashMap<String, Object>());
    }

    /**
     * Vytvorí nové nastavenia zisku s rozsahmi scales, stávkovou spoločnosťou
     * betCompany a nastaveniami properties.
     * 
     * @param scale
     * @param betCompany
     * @param properties 
     */
    public YieldProperties(List<BigDecimal> scale, String betCompany, Map<String, Object> properties) {
        setScale(scale);
        this.betCompany = betCompany;
        this.properties = properties;
    }

    /**
     * Vráti aktuálnu stávkovu spoločnosť.
     * @return 
     */
    public String getBetCompany() {
        return betCompany;
    }

    /**
     * Nastaví stávovú spoločnosť.
     * 
     * @param betCompany 
     */
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
     * Vráti neupraviteľný zoznam s aktuálne nastavenými rozsahmi (iba hraničné
     * hodnoty, nie celé dvojice rozsahov).
     * @return 
     */
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
        if(this.scale == null){
            this.scale = new LinkedList<>();
        } else {
            this.scale.clear();
        }
        if(scale != null){
            for(BigDecimal d : scale){
                addScale(d);
            }
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof YieldProperties)){
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

    @Override
    public String toString() {
        return new StringBuilder("Yield properties: ").append(System.lineSeparator())
                .append(scale).append(System.lineSeparator())
                .append(betCompany).append(System.lineSeparator())
                .append(properties).toString();
    }
}