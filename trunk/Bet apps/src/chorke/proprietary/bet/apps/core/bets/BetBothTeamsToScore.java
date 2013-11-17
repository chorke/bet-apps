/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.bets;

/**
 *
 * @author Chorke
 */
public class BetBothTeamsToScore extends Bet{
    public double yes;
    public double no;

    public BetBothTeamsToScore(String betCompany, double yes, double no) {
        super(betCompany);
        this.yes = yes;
        this.no = no;
    }

    @Override
    public String toString() {
        return "BTtS: " + yes + " " + no;
    }
}
