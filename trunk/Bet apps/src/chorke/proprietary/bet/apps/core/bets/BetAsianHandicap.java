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
public class BetAsianHandicap extends Bet{
   
    public final BigDecimal bet1;
    public final BigDecimal bet2;
    public final BigDecimal handicap;
    public final String description;

    public BetAsianHandicap(String betCompany, BigDecimal bet1, BigDecimal bet2, BigDecimal handicap, String description) {
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
}
