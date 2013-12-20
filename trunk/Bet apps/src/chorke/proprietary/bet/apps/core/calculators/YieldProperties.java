/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.calculators;

import chorke.proprietary.bet.apps.core.Tuple;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Chorke
 */
public class YieldProperties {
    private List<Double> scale;
    private String betCompany;
//    private double doubleNumber;
//    private String note;
    private Map<String, Object> properties;
    
    public YieldProperties(){
        this(null, "", new HashMap<String, Object>());
    }

    public YieldProperties(List<Double> scale, String betCompany) {
        this(scale, betCompany, new HashMap<String, Object>());
    }

    public YieldProperties(List<Double> scale, String betCompany, Map<String, Object> properties) {
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
//    /**
//     * Môže byť použité pre akúkoľvek pomocnú informáciu.
//     * Každá implementácia {@code YieldCalculator} môže 
//     * použiť túto hodnotu k iným účelom. Zároveň by mala 
//     * v javadocu popisovať, na čo túto hodnotu používa.
//     */
//    public double getDoubleNumber() {
//        return doubleNumber;
//    }
//
//    /**
//     * Môže byť použité pre akúkoľvek pomocnú informáciu.
//     * Každá implementácia {@code YieldCalculator} môže 
//     * použiť túto hodnotu k iným účelom. Zároveň by mala 
//     * v javadocu popisovať, na čo túto hodnotu používa.
//     * 
//     * @param doubleNumber 
//     */
//    public void setDoubleNumber(double doubleNumber) {
//        this.doubleNumber = doubleNumber;
//    }
//
//    /**
//     * Môže byť použité pre akúkoľvek pomocnú informáciu.
//     * Každá implementácia {@code YieldCalculator} môže 
//     * použiť túto hodnotu k iným účelom. Zároveň by mala 
//     * v javadocu popisovať, na čo túto hodnotu používa.
//     */
//    public String getNote() {
//        return note;
//    }
//
//    /**
//     * Môže byť použité pre akúkoľvek pomocnú informáciu.
//     * Každá implementácia {@code YieldCalculator} môže 
//     * použiť túto hodnotu k iným účelom. Zároveň by mala 
//     * v javadocu popisovať, na čo túto hodnotu používa.
//     * 
//     * @param note  
//     */
//    public void setNote(String note) {
//        this.note = note;
//    }
    
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
    public Tuple<Double, Double> getRangeForIndex(int index) throws IndexOutOfBoundsException{
        if(index > scale.size() || index < 0){
            throw new IndexOutOfBoundsException("Index out of bounds " + index);
        }
        if(index == 0){
            return new Tuple<>(1.00, scale.get(0));
        } else if(index == scale.size()){
            return new Tuple<>(scale.get(index - 1) + 0.01, Double.MAX_VALUE);
        } else {
            return new Tuple<>(scale.get(index - 1) + 0.01, scale.get(index));
        }
    }
    
    /**
     * Vráti všetky rozsahy podľa aktuálnych nastavení. 
     * Poradie zodpovedná poradiu indexov (0, 1, 2, ...)
     * 
     * @return rozsahy pre aktuálne nastavenie
     * 
     * @see addScale(Double)
     * @see removeScale(Double)
     * @see getScale()
     * @see setScale(List)
     */
    public List<Tuple<Double, Double>> getRanges(){
        List<Tuple<Double, Double>> out = new ArrayList<>(scale.size() + 1);
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
    public int getIndexForRange(Tuple<Double, Double> range) throws IllegalArgumentException{
        if(range.second == Double.MAX_VALUE){
            if(scale.get(scale.size() -1) != range.first){
                throw new IllegalArgumentException("Range not in scale " + range);
            }
            return scale.size();
        }
        if(range.first == 1.00){
            if(scale.get(0) != range.second){
                throw new IllegalArgumentException("Range not in scale " + range);
            }
        }
        int idx = scale.indexOf(range.second);
        if(idx <= 0){
            throw new IllegalArgumentException("Range not in scale " + range);
        }
        if(scale.get(idx - 1) + 0.01 != range.first){
            throw new IllegalArgumentException("Range not in scale " + range);
        }
        return idx;
    }
    
    public List<Double> getScale() {
        return Collections.unmodifiableList(scale);
    }
    
    public void addScale(Double d){
        if(!scale.contains(d)){
            for(int i = 0; i < scale.size(); i++){
                if(scale.get(i) < d){
                    scale.add(i, d);
                    return;
                }
            }
        }
    }
    
    public void removeScale(Double d){
        if(d != null){
            scale.remove(d);
        }
    }
    
    /**
     * Ak je {@code scale == null}, vytvorí sa prázdna vnútorná {@code scale}.
     * 
     * @param scale 
     */
    public final void setScale(List<Double> scale){
        if(scale == null){
            if(this.scale == null){
                this.scale = new ArrayList<>();
            } else {
                this.scale.clear();
            }
        } else {
            this.scale.clear();
            for(Double d : scale){
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