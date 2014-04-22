
package chorke.proprietary.bet.apps.core.calculators;

import chorke.proprietary.bet.apps.StaticConstants.Periode;
import chorke.proprietary.bet.apps.core.match.Match;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Chorke
 */
public interface YieldCalculator<T extends Yield> {
    
    /**
     * Vypočíta celkový zisk. 
     * @param matches zápasy
     * @param properties nastavenie pre počítanie zisku
     * @return celkový zisk
     */
    T getOverallYield(Collection<Match> matches, YieldProperties properties);
    
    /**
     * Vypočíta zisk podľa období.
     * 
     * @param matches zápasy
     * @param properties vlastnosti zisku
     * @param periode obdobia, ktoré majú byť zlučované pri počítaní zisku
     * @return zisky podľa zvolených období.
     */
    Map<Calendar, T> getPeriodicYield(Collection<Match> matches,
            YieldProperties properties, Periode periode);
    
    /**
     * Vypočíta kumulatívny celkový zisk. 
     * 
     * @param matches zápasy
     * @param properties nastavenia pre počítanie zisku
     * @return celkový kumulatívny zisk
     */
    T getCumulativeYield(Collection<Match> matches, CumulativeYieldProperties properties);
    
    /**
     * Vypočíta kumulatívny zisk podľa obodbia.
     * 
     * @param matches zápasy
     * @param properties nastavenie pre počítanie zisku
     * @param period obdobie
     * @return kumulatvíny zisk podľa období
     */
    Map<Calendar, T> getCumulativePeriodicYield(Collection<Match> matches,
            CumulativeYieldProperties properties, Periode period);
}
