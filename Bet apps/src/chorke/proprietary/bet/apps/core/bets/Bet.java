
package chorke.proprietary.bet.apps.core.bets;

/**
 * Trieda reprezentujúca stávku.
 * @author Chorke
 */
public abstract class Bet {
    
    public String betCompany;

    /**
     * Vytvorí novú stávku so spoločnosťou betCompany.
     * @param betCompany 
     */
    public Bet(String betCompany) {
        this.betCompany = betCompany;
    }

    @Override
    public String toString() {
        return betCompany;
    }
}
