/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.bets;

/**
 *
 * @author Chorke
 */
public class BetOverUnder extends Bet{
    public double total;
    public double over;
    public double under;
    public String description;

    public BetOverUnder(String betCompany, double total, double over, double under, String description) {
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
