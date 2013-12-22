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
    
}
