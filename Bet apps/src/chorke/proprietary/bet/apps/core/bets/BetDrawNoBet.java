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
    
}
