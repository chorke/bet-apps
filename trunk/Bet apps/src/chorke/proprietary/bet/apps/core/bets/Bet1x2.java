/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.bets;

import java.math.BigDecimal;

/**
 *
 * @author Chorke
 */
public class Bet1x2 extends Bet{
    public final BigDecimal bet1;
    public final BigDecimal betX;
    public final BigDecimal bet2;

    public Bet1x2(String betCompany, BigDecimal bet1, BigDecimal betX, BigDecimal bet2) {
        super(betCompany);
        this.bet1 = bet1;
        this.betX = betX;
        this.bet2 = bet2;
    }
    
    public BigDecimal getHomeBet(){
        return bet1;
    }
    
    public BigDecimal getGuestBet(){
        return bet2;
    }
    
    public BigDecimal getTieBet(){
        return betX;
    }
    
    /**
     * Vráti 0.0, ak neexistuje favoritná stávka (domáci a hostia majú rovnaké stávky)
     * @return 
     */
    public BigDecimal getFavoritBet(){
        if(bet1.compareTo(bet2) == 1){
            return bet2;
        } else if(bet2.compareTo(bet1) == -1){
            return bet1;
        } else {
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Vráti 0.0, ak neexistuje favoritná stávka (domáci a hostia majú rovnaké stávky)
     * @return 
     */
    public BigDecimal getLooserBet(){
        if(bet1.compareTo(bet2) == 1){
            return bet1;
        } else if(bet2.compareTo(bet1) == -1){
            return bet2;
        } else {
            return BigDecimal.ZERO;
        }
    }
    
    @Override
    public String toString() {
        return "1x2: " + bet1 + " " + betX + " " + bet2;
    }
}
