/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.bets;

/**
 *
 * @author Chorke
 */
public class BetDoubleChance extends Bet{
    public double bet1X;
    public double betX2;
    public double bet12;

    public BetDoubleChance(String betCompany, double bet1X, double bet12, double betX2) {
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
