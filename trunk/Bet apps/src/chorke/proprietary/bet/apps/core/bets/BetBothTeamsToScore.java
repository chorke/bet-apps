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
public class BetBothTeamsToScore extends Bet{
    public final BigDecimal yes;
    public final BigDecimal no;

    public BetBothTeamsToScore(String betCompany, BigDecimal yes, BigDecimal no) {
        super(betCompany);
        this.yes = yes;
        this.no = no;
    }

    @Override
    public String toString() {
        return "BTtS: " + yes + " " + no;
    }
}
