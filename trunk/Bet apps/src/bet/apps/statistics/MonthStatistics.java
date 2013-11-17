/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bet.apps.statistics;

import bet.apps.core.Match;
import java.util.Collection;

/**
 *
 * @author Chorke
 */
public class MonthStatistics extends AbstractPeriodicStatistics{
 
    @Override
    public void split(Collection<Match> matches) {
        for (Match m : matches) {
            int hash = m.getHashCodeByMonth();
            if (getPeriodicMatches().containsKey(hash)) {
                getPeriodicMatches().get(hash).addMatch(m);
            } else {
                MatchesStatistics ms = new MatchesStatistics();//NUM_OF_FIELDS, NUM_OF_STATS);
                ms.addMatch(m);
                getPeriodicMatches().put(hash, ms);
            }
        }
        super.split(matches);
    }
}
