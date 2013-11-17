/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.io;

import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.core.match.MatchProperties;
import java.util.Collection;

/**
 *
 * @author Chorke
 */
public interface BetIOManager extends Cloneable{
    
    void saveMatch(Match match) throws BetIOException;
    
    void saveMatches(Collection<Match> matches) throws BetIOException;
    
    Collection<Match> loadAllMatches() throws BetIOException;
    
    Collection<Match> loadMatches(MatchProperties properties) throws BetIOException;
    
    void deleteMatch(Match match) throws BetIOException;
    
    void deleteMatches(Collection<Match> matches);
    
    BetIOManager cloneBetIOManager();
}
