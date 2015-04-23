
package chorke.bet.apps.core.bets;

/**
 * Trieda reprezentujúca stávku.
 * @author Chorke
 */
public abstract class Bet {
    
    /**
     * Názov stávkovej spoločnosti.
     */
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
