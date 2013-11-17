/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.match.score;

/**
 *
 * @author Chorke
 */
public class PartialScore {

    public final int firstParty;
    public final int secondParty;

    public PartialScore(int firstParty, int secondParty) {
        this.firstParty = firstParty;
        this.secondParty = secondParty;
    }

    @Override
    public String toString() {
        return firstParty + ":" + secondParty;
    }
}
