
package chorke.proprietary.bet.apps.core.bets;

import java.math.BigDecimal;

/**
 *
 * @author Chorke
 */
public class BetOverUnder extends Bet{
    public final BigDecimal total;
    public final BigDecimal over;
    public final BigDecimal under;
    public final String description;

    public BetOverUnder(String betCompany, BigDecimal total, BigDecimal over, BigDecimal under, String description) {
        super(betCompany);
        this.total = total;
        this.over = over;
        this.under = under;
        this.description = description;
    }

    @Override
    public String toString() {
        return "O/U ["+ total + "]: " + over + "/" + under;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null || !obj.getClass().equals(this.getClass())){
            return false;
        }
        BetOverUnder b = (BetOverUnder)obj;
        return this.betCompany != null && this.betCompany.equals(b.betCompany)
                && (this.description != null && this.description.equals(b.description))
                && (this.over != null && this.over.compareTo(b.over) == 0)
                && (this.under != null && this.under.compareTo(b.under) == 0)
                && (this.total != null && this.total.compareTo(b.total) == 0);
    }

    @Override
    public int hashCode() {
        return 31 * (betCompany == null ? 0 : betCompany.hashCode()
                + (description == null ? 0 : description.hashCode())
                + (over == null ? 0 : over.hashCode())
                + (under == null ? 0 : under.hashCode())
                + (total == null ? 0 : total.hashCode()));
    }
}
