/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.bets;

/**
 *
 * @author Chorke
 */
public class BetDrawNoBet extends Bet{
    public double bet1;
    public double bet2;

    public BetDrawNoBet(String betCompany, double bet1, double bet2) {
        super(betCompany);
        this.bet1 = bet1;
        this.bet2 = bet2;
    }

    @Override
    public String toString() {
        return "DNB: " + bet1 + " " + bet2;
    }
    
}
