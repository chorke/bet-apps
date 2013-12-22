/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.calculators;

import chorke.proprietary.bet.apps.core.Tuple;
import chorke.proprietary.bet.apps.core.calculators.Yield.BetPossibility;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Chorke
 */
public class Yield1x2 extends Yield{
    
    private List<Tuple<Integer, BigDecimal>> home;
    private List<Tuple<Integer, BigDecimal>> guest;
    private List<Tuple<Integer, BigDecimal>> favorit;
    private List<Tuple<Integer, BigDecimal>> looser;
    private List<Tuple<Integer, BigDecimal>> tie;

    public Yield1x2() {
        super();
        home = new ArrayList<>();
        guest = new ArrayList<>();
        favorit = new ArrayList<>();
        looser = new ArrayList<>();
        tie = new ArrayList<>();
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
    public List<Tuple<Integer, BigDecimal>> getYields(BetPossibility betPossibility)
            throws IllegalArgumentException{
        switch(betPossibility){
            case Favorit:
                return Collections.unmodifiableList(favorit);
            case Guest:
                return Collections.unmodifiableList(guest);
            case Home:
                return Collections.unmodifiableList(home);
            case Looser:
                return Collections.unmodifiableList(looser);
            case Tie:
                return Collections.unmodifiableList(tie);
            default :
                throw new IllegalStateException("Unsupported enum value [" + betPossibility + "]");
        }
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
        switch(betPossibility){
            case Favorit:
                addYieldForScaleIndexInner(favorit, index, yield);
                break;
            case Guest:
                addYieldForScaleIndexInner(guest, index, yield);
                break;
            case Home:
                addYieldForScaleIndexInner(home, index, yield);
                break;
            case Looser:
                addYieldForScaleIndexInner(looser, index, yield);
                break;
            case Tie:
                addYieldForScaleIndexInner(tie, index, yield);
                break;
            default :
                throw new IllegalStateException("Unsupported enum value [" + betPossibility + "]");
        }
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
    private void addYieldForScaleIndexInner(List<Tuple<Integer, BigDecimal>> list, 
            int index, BigDecimal yield)
            throws IndexOutOfBoundsException {
        if(index > properties.getScale().size() || index < 0){
            throw new IndexOutOfBoundsException("Index out of bounds [" + index + "]");
        }
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).first.equals(index)){
                list.remove(i);
            }
        }
        list.add(new Tuple<>(index, yield));
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
    public BigDecimal getYieldForScaleIndex(BetPossibility betPossibility, int index)
            throws IllegalArgumentException, IllegalStateException {
        switch(betPossibility){
            case Favorit:
                return getYieldForScaleIndexInner(favorit, index);
            case Guest:
                return getYieldForScaleIndexInner(guest, index);
            case Home:
                return getYieldForScaleIndexInner(home, index);
            case Looser:
                return getYieldForScaleIndexInner(looser, index);
            case Tie:
                return getYieldForScaleIndexInner(tie, index);
            default :
                throw new IllegalStateException("Unsupported enum value [" + betPossibility + "]");
        }
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
            Tuple<BigDecimal, BigDecimal> range)
            throws IllegalArgumentException, IllegalStateException {
        try{
            return getYieldForScaleIndex(possibleWinner, properties.getIndexForRange(range));
        } catch (IllegalArgumentException ex){
            throw new IllegalArgumentException("No yield for range [" + range + "]", ex);
        }
    }
    
    /**
     * Vráti zisk pre zvolenú stávkovú kolekciu.
     * 
     * @param list 
     * @param index 
     * 
     * @throws IllegalArgumentException ak neexistuje zisk pre daný {@code index}
     */
    private BigDecimal getYieldForScaleIndexInner(List<Tuple<Integer, BigDecimal>> list, int index)
            throws IllegalArgumentException {
        for(Tuple<Integer, BigDecimal> t : list){
            if(t.first.equals(index)){
                return t.second;
            }
        }
        throw new IllegalArgumentException("No yield for index [" + index + "]");
    }

    /**
     * {@inheritDoc}
     * 
     * Ak sú nastavované nové properties 
     * (presnejšie {@code !this.properties.equals(properties)}),
     * vyčistí aktuálne hodnoty ziskov.
     * 
     * @param properties 
     */
    @Override
    public void setProperties(YieldProperties properties) {
        if(!this.properties.equals(properties)){
            super.setProperties(properties);
            home.clear();
            guest.clear();
            favorit.clear();
            looser.clear();
            tie.clear();
        }
    }
}