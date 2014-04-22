
package chorke.proprietary.bet.apps.core.calculators;

import chorke.proprietary.bet.apps.StaticConstants.BetPossibility;
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
    private Map<Integer, BigDecimal> home;
    /**
     * Hostia. Index rozsahu a zisk.
     */
    private Map<Integer, BigDecimal> guest;
    /**
     * Favorit. Index rozsahu a zisk.
     */
    private Map<Integer, BigDecimal> favorit;
    /**
     * Looser. Index rozsahu a zisk.
     */
    private Map<Integer, BigDecimal> looser;
    /**
     * Remíza. Index rozsahu a zisk.
     */
    private Map<Integer, BigDecimal> tie;
    
    public Yield1x2() {
        super();
        home = new HashMap<>();
        guest = new HashMap<>();
        favorit = new HashMap<>();
        looser = new HashMap<>();
        tie = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     * 
     * Ak sú nastavované nové properties 
     * (presnejšie {@code !this.properties.equals(properties)}),
     * vyčistí aktuálne hodnoty ziskov.
     * 
     * @param properties 
     */
    @Override
    public void setProperties(YieldProperties properties) {
        if(!this.properties.equals(properties)){
            super.setProperties(properties);
            home.clear();
            guest.clear();
            favorit.clear();
            looser.clear();
            tie.clear();
        }
    }

    @Override
    public BetPossibility[] getSupportedBetPossibilities() {
        return new BetPossibility[]{BetPossibility.Guest, BetPossibility.Home, 
            BetPossibility.Favorit, BetPossibility.Looser, BetPossibility.Tie};
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
                .append(" looser: ").append(stringYield(looser))
                .append(System.lineSeparator())
                .append(" tie: ").append(stringYield(tie));
        return sb.toString();
    }

    @Override
    protected Map<Integer, BigDecimal> getRequiredMap(BetPossibility betPossibility) {
        switch(betPossibility){
            case Favorit:
                return favorit;
            case Guest:
                return guest;
            case Home:
                return home;
            case Looser:
                return looser;
            case Tie:
                return tie;
            default :
                throw new IllegalStateException("Unsupported bet possibility [" + betPossibility + "]");
        }
    }
}