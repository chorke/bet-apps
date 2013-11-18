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
 *
 * @author Chorke
 */
public interface YieldCalculator<T extends Yield> {
    
    T getOverallYield(Collection<Match> matches, YieldProperties properties);
    
    Map<Calendar, T> getPeriodicYieldDAY(Collection<Match> matches, YieldProperties properties);
    
    Map<Calendar, T> getPeriodicYieldWEEK(Collection<Match> matches, YieldProperties properties);
    
    Map<Calendar, T> getPeriodicYieldMONTH(Collection<Match> matches, YieldProperties properties);
    
    Map<Calendar, T> getPeriodicYieldYEAR(Collection<Match> matches, YieldProperties properties);
    
    T getCumulativeYield(Collection<Match> matches, CumulativeYieldProperties properties);
    
    Map<Calendar, T> getCumulativePeriodicYieldDAY(Collection<Match> matches, CumulativeYieldProperties properties);
    
    Map<Calendar, T> getCumulativePeriodicYieldWEEK(Collection<Match> matches, CumulativeYieldProperties properties);
    
    Map<Calendar, T> getCumulativePeriodicYieldMONTH(Collection<Match> matches, CumulativeYieldProperties properties);
    
    Map<Calendar, T> getCumulativePeriodicYieldYEAR(Collection<Match> matches, CumulativeYieldProperties properties);
}
