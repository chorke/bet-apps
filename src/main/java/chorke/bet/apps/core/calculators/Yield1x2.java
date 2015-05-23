
package chorke.bet.apps.core.calculators;

import chorke.bet.apps.core.CoreUtils.BetPossibility;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Chorke
 */
public class Yield1x2 extends Yield{
    
    /**
     * Domáci. Index rozsahu a zisk.
     */
    private Map<Object, BigDecimal> home;
    /**
     * Hostia. Index rozsahu a zisk.
     */
    private Map<Object, BigDecimal> guest;
    /**
     * Favorit. Index rozsahu a zisk.
     */
    private Map<Object, BigDecimal> favorit;
    /**
     * Loser. Index rozsahu a zisk.
     */
    private Map<Object, BigDecimal> loser;
    /**
     * Remíza. Index rozsahu a zisk.
     */
    private Map<Object, BigDecimal> tie;
    
    public Yield1x2() {
        super();
        home = new HashMap<>();
        guest = new HashMap<>();
        favorit = new HashMap<>();
        loser = new HashMap<>();
        tie = new HashMap<>();
    }

    @Override
    public void clear() {
        home.clear();
        guest.clear();
        favorit.clear();
        loser.clear();
        tie.clear();
    }

    @Override
    public BetPossibility[] getSupportedBetPossibilities() {
        return new BetPossibility[]{BetPossibility.Home, BetPossibility.Tie, 
            BetPossibility.Guest, BetPossibility.Favorit, BetPossibility.Loser};
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("home: ").append(stringYield(home))
                .append(System.lineSeparator())
                .append(" guest: ").append(stringYield(guest))
                .append(System.lineSeparator())
                .append(" favorit: ").append(stringYield(favorit))
                .append(System.lineSeparator())
                .append(" looser: ").append(stringYield(loser))
                .append(System.lineSeparator())
                .append(" tie: ").append(stringYield(tie));
        return sb.toString();
    }

    @Override
    protected Map<Object, BigDecimal> getRequiredMap(BetPossibility betPossibility)
                throws IllegalArgumentException{
        switch(betPossibility){
            case Favorit:
                return favorit;
            case Guest:
                return guest;
            case Home:
                return home;
            case Loser:
                return loser;
            case Tie:
                return tie;
            default :
                throw new IllegalArgumentException(
                        "Unsupported bet possibility [" + betPossibility + "]");
        }
    }
}