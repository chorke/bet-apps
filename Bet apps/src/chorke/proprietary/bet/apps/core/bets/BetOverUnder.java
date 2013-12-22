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
}
