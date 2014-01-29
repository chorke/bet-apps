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

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !obj.getClass().equals(this.getClass())){
            return false;
        }
        PartialScore ps = (PartialScore) obj;
        return ps.firstParty == this.firstParty
                && ps.secondParty == this.secondParty;
    }

    @Override
    public int hashCode() {
        return firstParty * 100 + secondParty;
    }
}
