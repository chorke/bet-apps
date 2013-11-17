/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.calculators;

import chorke.proprietary.bet.apps.core.Tuple;
import java.util.ArrayList;
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
    
    public Double getHomeYieldForScaleIndex(int index) throws IllegalArgumentException{
        for(Tuple<Integer, Double> t : home){
            if(t.first == index){
                return t.second;
            }
        }
        throw new IllegalArgumentException("No yield for index [" + index + "]");
    }
    
    public Double getHomeYieldForScaleRange(Tuple<Double, Double> range) throws IllegalArgumentException{
        try{
            return getHomeYieldForScaleIndex(properties.getIndexForRange(range));
        } catch (IllegalArgumentException ex){
            throw new IllegalArgumentException("No yield for range [" + range + "]", ex);
        }
    }
    
}
