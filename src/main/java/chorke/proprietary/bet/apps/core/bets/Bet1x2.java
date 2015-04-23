
package chorke.proprietary.bet.apps.core.bets;

import java.math.BigDecimal;

/**
 * Trieda pre stávky 1x2.
 * @author Chorke
 */
public class Bet1x2 extends Bet{
    public final BigDecimal bet1;
    public final BigDecimal betX;
    public final BigDecimal bet2;

    /**
     * Vytvorí novú stávku so spoločnousťou betCompany, kurzom na domáceho bet1, 
     * kurzom na remízu betX a kurzom na hosťa bet2.
     * @param betCompany
     * @param bet1
     * @param betX
     * @param bet2 
     */
    public Bet1x2(String betCompany, BigDecimal bet1, BigDecimal betX, BigDecimal bet2) {
        super(betCompany);
        this.bet1 = bet1;
        this.betX = betX;
        this.bet2 = bet2;
    }
    
    /**
     * Vráti stávkový kurz pre domáceho.
     * @return 
     */
    public BigDecimal getHomeBet(){
        return bet1;
    }
    
    /**
     * Vráti stávkový kurz pre hosťa.
     * @return 
     */
    public BigDecimal getGuestBet(){
        return bet2;
    }
    
    /**
     * Vráti stávkový kurz pre remízu.
     * @return 
     */
    public BigDecimal getTieBet(){
        return betX;
    }
    
    /**
     * Vráti stávkový kurz na favorita stretnutia.
     * Vráti 0.0, ak neexistuje favoritná stávka (domáci a hostia majú rovnaké stávky)
     * @return 
     */
    public BigDecimal getFavoritBet(){
        if(bet1.compareTo(bet2) == 1){
            return bet2;
        } else if(bet2.compareTo(bet1) == 1){
            return bet1;
        } else {
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Vráti stávkový kurz proti favoritovi stretnutia.
     * Vráti 0.0, ak neexistuje favoritná stávka (domáci a hostia majú rovnaké stávky)
     * @return 
     */
    public BigDecimal getLooserBet(){
        if(bet1.compareTo(bet2) == 1){
            return bet1;
        } else if(bet2.compareTo(bet1) == 1){
            return bet2;
        } else {
            return BigDecimal.ZERO;
        }
    }
    
    @Override
    public String toString() {
        return "1x2: " + bet1 + " " + betX + " " + bet2;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !obj.getClass().equals(this.getClass())){
            return false;
        }
        Bet1x2 b = (Bet1x2)obj;
        return this.betCompany != null && this.betCompany.equals(b.betCompany)
                && (this.bet1 != null && this.bet1.compareTo(b.bet1) == 0)
                && (this.bet2 != null && this.bet2.compareTo(b.bet2) == 0)
                && (this.betX != null && this.betX.compareTo(b.betX) == 0);
    }

    @Override
    public int hashCode() {
        return 31 * (betCompany == null ? 0 : betCompany.hashCode()
                + (bet1 == null ? 0 : bet1.hashCode())
                + (bet2 == null ? 0 : bet2.hashCode())
                + (betX == null ? 0 : betX.hashCode()));
    }
}
