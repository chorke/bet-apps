
package chorke.proprietary.bet.apps.io;

import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.core.match.MatchProperties;
import java.util.Collection;

/**
 * Ukladá stávky.
 * @author Chorke
 */
public interface BetIOManager{
    
    void saveMatch(Match match) throws BetIOException;
    
    Collection<Match> loadAllMatches() throws BetIOException;
    
    Collection<Match> loadMatches(MatchProperties properties) throws BetIOException;
    
    void deleteMatch(Match match) throws BetIOException;
}
