/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.httpparsing;

import chorke.proprietary.bet.apps.StaticConstants.Sport;
import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.core.Tuple;
import java.util.Calendar;
import java.util.Collection;

/**
 * Stiahne a rozparsuje stávky z web stránky.
 * @author Chorke
 */
public interface HTMLBetParser {
    
    Collection<Match> getMatchesOfDay(Calendar cal);
    
    Collection<Match> previousDay();
    
    Collection<Match> nextDay();
    
    Collection<Match> getMatches();
    
    void setExploredSport(Sport sport);
    
    Sport getExploredSport();
    
    Calendar getActualProccessingDate();
    
    void setActualProccessingDate(Calendar date);
    
    void setStartDate(Calendar start);
    
    void setEndDate(Calendar end);
    
    Collection<String> getUndownloadedMatches();
    
    Collection<Tuple<Sport, Calendar>> getUndownloadedSports();
    
    Collection<Match> getUnsavedMatches();
}
