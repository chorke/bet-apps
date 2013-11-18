/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.bets;

/**
 *
 * @author Chorke
 */
public class Bet1x2 extends Bet{
    public final double bet1;
    public final double betX;
    public final double bet2;

    public Bet1x2(String betCompany, double bet1, double betX, double bet2) {
        super(betCompany);
        this.bet1 = bet1;
        this.betX = betX;
        this.bet2 = bet2;
    }
    
    public double getHomeBet(){
        return bet1;
    }
    
    public double getGuestBet(){
        return bet2;
    }
    
    public double getTieBet(){
        return betX;
    }
    
    /**
     * Vráti 0.0, ak neexistuje favoritná stávka (domáci a hostia majú rovnaké stávky)
     * @return 
     */
    public double getFavoritBet(){
        if(bet1 > bet2){
            return bet2;
        } else if(bet2 > bet1){
            return bet1;
        } else {
            return 0.0;
        }
    }
    
    /**
     * Vráti 0.0, ak neexistuje favoritná stávka (domáci a hostia majú rovnaké stávky)
     * @return 
     */
    public double getLooserBet(){
        if(bet1 > bet2){
            return bet1;
        } else if(bet2 > bet1){
            return bet2;
        } else {
            return 0.0;
        }
    }
    
    @Override
    public String toString() {
        return "1x2: " + bet1 + " " + betX + " " + bet2;
    }
}
