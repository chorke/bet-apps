
package chorke.proprietary.bet.apps.core.bets;

import java.math.BigDecimal;

/**
 *
 * @author Chorke
 */
public class BetDoubleChance extends Bet{
    public final BigDecimal bet1X;
    public final BigDecimal betX2;
    public final BigDecimal bet12;

    public BetDoubleChance(String betCompany, BigDecimal bet1X, BigDecimal bet12, BigDecimal betX2) {
        super(betCompany);
        this.bet1X = bet1X;
        this.betX2 = betX2;
        this.bet12 = bet12;
    }

    @Override
    public String toString() {
        return "DC: " + bet1X + " " + bet12 + " " + betX2;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null || !obj.getClass().equals(this.getClass())){
            return false;
        }
        BetDoubleChance b = (BetDoubleChance)obj;
        return this.betCompany != null && this.betCompany.equals(b.betCompany)
                && (this.bet1X != null && this.bet1X.compareTo(b.bet1X) == 0)
                && (this.betX2 != null && this.betX2.compareTo(b.betX2) == 0)
                && (this.bet12 != null && this.bet12.compareTo(b.bet12) == 0);
    }

    @Override
    public int hashCode() {
        return 31 * (betCompany == null ? 0 : betCompany.hashCode()
                + (bet1X == null ? 0 : bet1X.hashCode())
                + (bet12 == null ? 0 : bet12.hashCode())
                + (betX2 == null ? 0 : betX2.hashCode()));
    }
}
