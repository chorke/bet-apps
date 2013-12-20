/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.calculators;

import chorke.proprietary.bet.apps.core.Tuple;
import chorke.proprietary.bet.apps.core.calculators.Yield.BetPossibility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Chorke
 */
public class Yield1x2 extends Yield{
    
    private List<Tuple<Integer, Double>> home;
    private List<Tuple<Integer, Double>> guest;
    private List<Tuple<Integer, Double>> favorit;
    private List<Tuple<Integer, Double>> looser;
    private List<Tuple<Integer, Double>> tie;

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
     * @throws IllegalArgumentException ak {@code betPossibility} nie je podporovaná
     */
    public List<Tuple<Integer, Double>> getYields(BetPossibility betPossibility)
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
                throw new IllegalArgumentException("Unsupported enum value [" + betPossibility + "]");
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
     * @throws IllegalArgumentException ak {@code betPossibility} nie je podporovaná
     */
    public void addYieldForScaleIndex(BetPossibility betPossibility, int index, double yield)
            throws IndexOutOfBoundsException, IllegalArgumentException {
        if(index > properties.getScale().size() || index < 0){
            throw new IndexOutOfBoundsException("Index out of bounds [" + index + "]");
        }
        switch(betPossibility){
            case Favorit:
                favorit.add(new Tuple<>(index, yield));
            case Guest:
                guest.add(new Tuple<>(index, yield));
            case Home:
                home.add(new Tuple<>(index, yield));
            case Looser:
                looser.add(new Tuple<>(index, yield));
            case Tie:
                tie.add(new Tuple<>(index, yield));
            default :
                throw new IllegalArgumentException("Unsupported enum value [" + betPossibility + "]");
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
     * @throws IllegalArgumentException ak {@code betPossibility} nie je podporovaná,
     *      alebo {@code range} nie je v danom rozsahu pre aktuálne 
     *      {@code properties} ({@link Yield#getProperties()}, 
     *          {@link Yield#setProperties(YieldProperties)})
     */
    public void addYieldForScaleRange(BetPossibility betPossibility,
            Tuple<Double, Double> range, double yield)
            throws IllegalArgumentException{
        try{
            addYieldForScaleIndex(betPossibility, properties.getIndexForRange(range), yield);
        } catch (IllegalArgumentException ex){
            throw new IllegalArgumentException("Exception while adding", ex);
        } catch (IndexOutOfBoundsException ex){
            //should not happend
            throw new Error("Possible error in method YieldProperties.getIndexForRange(...).", ex);
        }
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
     * @throws IllegalArgumentException ak {@code betPossibility} nie je podporovaná,
     *      {@code index} nie je v danom rozsahu pre aktuálne 
     *      {@code properties} ({@link Yield#getProperties()}, 
     *          {@link Yield#setProperties(YieldProperties)}), alebo neexistuje
     *      zisk pre daný {@code index}
     */
    public Double getYieldForScaleIndex(BetPossibility betPossibility, int index)
            throws IllegalArgumentException {
        switch(betPossibility){
            case Favorit:
                return getYieldForScaleIndex(favorit, index);
            case Guest:
                return getYieldForScaleIndex(guest, index);
            case Home:
                return getYieldForScaleIndex(home, index);
            case Looser:
                return getYieldForScaleIndex(looser, index);
            case Tie:
                return getYieldForScaleIndex(tie, index);
            default :
                throw new IllegalArgumentException("Unsupported enum value [" + betPossibility + "]");
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
     */
    public Double getYieldForScaleRange(BetPossibility possibleWinner, Tuple<Double, Double> range)
            throws IllegalArgumentException {
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
    private Double getYieldForScaleIndex(List<Tuple<Integer, Double>> list, int index)
            throws IllegalArgumentException {
        for(Tuple<Integer, Double> t : list){
            if(t.first == index){
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