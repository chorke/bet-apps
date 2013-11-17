/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.calculators;

import chorke.proprietary.bet.apps.core.match.Match;
import java.util.Calendar;
import java.util.Collection;
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
    public Map<Calendar, Yield1x2> getPeriodicYieldDAY(Collection<Match> matches, YieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Calendar, Yield1x2> getPeriodicYieldWEEK(Collection<Match> matches, YieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Calendar, Yield1x2> getPeriodicYieldMONTH(Collection<Match> matches, YieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Calendar, Yield1x2> getPeriodicYieldYEAR(Collection<Match> matches, YieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Yield1x2 getCumulativeYield(Collection<Match> matches, CumulativeYieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
