/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.bets;

/**
 *
 * @author Chorke
 */
public class BetAsianHandicap extends Bet{
   
    public double bet1;
    public double bet2;
    public double handicap;
    public String description;

    public BetAsianHandicap(String betCompany, double bet1, double bet2, double handicap, String description) {
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
