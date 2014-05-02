
package chorke.proprietary.bet.apps.core.bets;

import java.math.BigDecimal;

/**
 *
 * @author Chorke
 */
public class BetBothTeamsToScore extends Bet{
    public final BigDecimal yes;
    public final BigDecimal no;

    /**
     * Vytvorí stávku so spoločnosťou betCompany, kurzom na áno yes a kurzom
     * na nie no.
     * @param betCompany
     * @param yes
     * @param no 
     */
    public BetBothTeamsToScore(String betCompany, BigDecimal yes, BigDecimal no) {
        super(betCompany);
        this.yes = yes;
        this.no = no;
    }

    @Override
    public String toString() {
        return "BTtS: " + yes + " " + no;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null || !obj.getClass().equals(this.getClass())){
            return false;
        }
        BetBothTeamsToScore b = (BetBothTeamsToScore)obj;
        return this.betCompany != null && this.betCompany.equals(b.betCompany)
                && (this.yes != null && this.yes.compareTo(b.yes) == 0)
                && (this.no != null && this.no.compareTo(b.no) == 0);
    }

    @Override
    public int hashCode() {
        return 31 * (betCompany == null ? 0 : betCompany.hashCode()
                + (yes == null ? 0 : yes.hashCode())
                + (no == null ? 0 : no.hashCode()));
    }
}
