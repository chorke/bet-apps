/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.calculators;

import chorke.proprietary.bet.apps.core.Tuple;
import chorke.proprietary.bet.apps.core.match.Match;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Nepoužíva {@code note} a {@code doubleNumber} v {@code YieldProperties}
 * 
 * @author Chorke
 */
public class Yield1x2Calculator implements YieldCalculator<Yield1x2>{

    @Override
    public Yield1x2 getOverallYield(Collection<Match> matches, YieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Calendar, Yield1x2> getPeriodicYieldDAY(Collection<Match> matches,
                YieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Calendar, Yield1x2> getPeriodicYieldWEEK(Collection<Match> matches, 
                YieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Calendar, Yield1x2> getPeriodicYieldMONTH(Collection<Match> matches, 
                YieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Calendar, Yield1x2> getPeriodicYieldYEAR(Collection<Match> matches, 
                YieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Yield1x2 getCumulativeYield(Collection<Match> matches, 
                CumulativeYieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Calendar, Yield1x2> getCumulativePeriodicYieldDAY(Collection<Match> matches,
                CumulativeYieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Calendar, Yield1x2> getCumulativePeriodicYieldWEEK(Collection<Match> matches, 
                CumulativeYieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Calendar, Yield1x2> getCumulativePeriodicYieldMONTH(Collection<Match> matches,
                CumulativeYieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Calendar, Yield1x2> getCumulativePeriodicYieldYEAR(Collection<Match> matches, 
                CumulativeYieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Rozdelí zápasy do skupín podľa rozsahov, ktoré má akutálne nastavené
     * {@code properties}. Zápasy zároveň v jednotlivých skupinách zoradí.
     * 
     * @param matches
     * @param properties
     * @return 
     */
    private Map<Integer, List<Match>> splitToScales(Collection<Match> matches,
                 YieldProperties properties){
        //TODO - musí brať do úvahy properties - betCompany
        Map<Integer, List<Match>> out = new HashMap<>();
        List<Tuple<Double, Double>> ranges = properties.getRanges();
        int i;
        for(i = 0; i < ranges.size(); i++){
            out.put(i, new LinkedList<Match>());
        }
        for(Match m : matches){
            i = getIndex(m, ranges);
            if(i != -1){
                out.get(i).add(m);
            }
        }
        for(Integer in : out.keySet()){
            Collections.sort(out.get(in));
        }
        return out;
    }

    /**
     * Vráti index, ktorý vyhovuje zápasu vzhľadom ku {@code ranges} (kategória,
     * do ktorej patrí zápas vzhľadom ku svojím kurzom).
     * Ak nevyhovuje žiaden index, vráti -1.
     * 
     * @param m
     * @param ranges
     * @return 
     */
    private int getIndex(Match m, List<Tuple<Double, Double>> ranges) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
