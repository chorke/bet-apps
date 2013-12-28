/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.httpparsing;

import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.core.Tuple;
import java.util.Calendar;
import java.util.Collection;

/**
 * Stiahne a rozparsuje stávky z web stránky.
 * @author Chorke
 */
public interface HTMLBetParser {
    
    public static enum BettingSports {Soccer, Hockey, Basketball, Handball, Volleyball, Baseball, All};
    
    Collection<Match> getMatchesOfDay(Calendar cal);
    
    Collection<Match> previousDay();
    
    Collection<Match> nextDay();
    
    Collection<Match> getMatches();
    
    void setExploredSport(BettingSports sport);
    
    BettingSports getExploredSport();
    
    Calendar getActualProccessingDate();
    
    void setActualProccessingDate(Calendar date);
    
    void setStartDate(Calendar start);
    
    void setEndDate(Calendar end);
    
    Collection<String> getUndownloadedMatches();
    
    Collection<Tuple<BettingSports, Calendar>> getUndownloadedSports();
    
    Collection<Match> getUnsavedMatches();
}
