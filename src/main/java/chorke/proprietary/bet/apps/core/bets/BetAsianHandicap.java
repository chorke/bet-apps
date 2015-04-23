
package chorke.proprietary.bet.apps.core.bets;

import java.math.BigDecimal;

/**
 * Trieda pre sátvky azijského hendikepu.
 * 
 * @author Chorke
 */
public class BetAsianHandicap extends Bet{
   
    public final BigDecimal bet1;
    public final BigDecimal bet2;
    public final BigDecimal handicap;
    public final String description;

    /**
     * Vytvorí novú stávku so spoločnousťou betCompany, kurzom na domáceho
     * bet1, kurzom na hosťa bet2, handikepom handicap a popisom handikapu 
     * description.
     * @param betCompany
     * @param bet1
     * @param bet2
     * @param handicap
     * @param description 
     */
    public BetAsianHandicap(String betCompany, BigDecimal bet1, BigDecimal bet2,
            BigDecimal handicap, String description) {
        super(betCompany);
        this.bet1 = bet1;
        this.bet2 = bet2;
        this.handicap = handicap;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Asian Han. [" + handicap + "] " + bet1 + " " + bet2;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null || !obj.getClass().equals(this.getClass())){
            return false;
        }
        BetAsianHandicap b = (BetAsianHandicap)obj;
        return (this.betCompany != null && this.betCompany.equals(b.betCompany))
                && (this.description != null && this.description.equals(b.description))
                && (this.bet1 != null && this.bet1.compareTo(b.bet1) == 0)
                && (this.bet2 != null && this.bet2.compareTo(b.bet2) == 0)
                && (this.handicap != null && this.handicap.compareTo(b.handicap) == 0);
    }

    @Override
    public int hashCode() {
        return 31 * (betCompany == null ? 0 : betCompany.hashCode()
                + (description == null ? 0 : description.hashCode())
                + (bet1 == null ? 0 : bet1.hashCode())
                + (bet2 == null ? 0 : bet2.hashCode())
                + (handicap == null ? 0 : handicap.hashCode()));
    }
}
