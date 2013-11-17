/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.bets;

/**
 *
 * @author Chorke
 */
public class Bet1x2 extends Bet{
    public double bet1 = 0.0;
    public double betX = 0.0;
    public double bet2 = 0.0;

    public Bet1x2(String betCompany, double bet1, double betX, double bet2) {
        super(betCompany);
        this.bet1 = bet1;
        this.betX = betX;
        this.bet2 = bet2;
    }

    @Override
    public String toString() {
        return "1x2: " + bet1 + " " + betX + " " + bet2;
    }
}
