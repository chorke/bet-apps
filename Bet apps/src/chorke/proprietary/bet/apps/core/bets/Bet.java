/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.bets;

/**
 *
 * @author Chorke
 */
public abstract class Bet {
    
    public String betCompany;

    public Bet(String betCompany) {
        this.betCompany = betCompany;
    }

    @Override
    public String toString() {
        return betCompany;
    }
}
