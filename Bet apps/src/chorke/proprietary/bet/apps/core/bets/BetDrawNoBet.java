/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.bets;

import java.math.BigDecimal;

/**
 *
 * @author Chorke
 */
public class BetDrawNoBet extends Bet{
    public final BigDecimal bet1;
    public final BigDecimal bet2;

    public BetDrawNoBet(String betCompany, BigDecimal bet1, BigDecimal bet2) {
        super(betCompany);
        this.bet1 = bet1;
        this.bet2 = bet2;
    }

    @Override
    public String toString() {
        return "DNB: " + bet1 + " " + bet2;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null || !obj.getClass().equals(this.getClass())){
            return false;
        }
        BetDrawNoBet b = (BetDrawNoBet)obj;
        return this.betCompany != null && this.betCompany.equals(b.betCompany)
                &&(this.bet1 != null && this.bet1.compareTo(b.bet1) == 0)
                && (this.bet2 != null && this.bet2.compareTo(b.bet2) == 0);
    }

    @Override
    public int hashCode() {
        return 31 * (betCompany == null ? 0 : betCompany.hashCode()
                + (bet1 == null ? 0 : bet1.hashCode())
                + (bet2 == null ? 0 : bet2.hashCode()));
    }
}
