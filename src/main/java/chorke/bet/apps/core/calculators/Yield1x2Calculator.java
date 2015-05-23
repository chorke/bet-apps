
package chorke.bet.apps.core.calculators;

import chorke.bet.apps.core.CoreUtils.BetPossibility;
import chorke.bet.apps.core.CoreUtils.Winner;
import chorke.bet.apps.core.Tuple;
import chorke.bet.apps.core.bets.Bet1x2;
import chorke.bet.apps.core.calculators.Yield1x2Calculator.Key1x2;
import chorke.bet.apps.core.match.Match;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Počítadlo zisku pre stávky na 1x2.
 * @author Chorke
 */
public class Yield1x2Calculator extends YieldCalculator<Yield1x2, Key1x2>{

    /**
     * Vypočíta a vráti zisk zo všetkých zápasov matches, podľa nastavení 
     * properties a pre zvolené stávkovú možnosť.
     * 
     * @param matches
     * @param properties
     * @param betPosibility
     * @return 
     */
    @Override
    protected BigDecimal getYield(Key1x2 key, Collection<Match> matches, YieldProperties properties, 
            BetPossibility betPosibility){
        BigDecimal yield = BigDecimal.ZERO;
        Iterator<Bet1x2> betIter;
        for(Match m : matches){
            betIter = m.getBet(properties.getBetCompany(), Bet1x2.class).iterator();
            if(betIter.hasNext()){ // there is only one bet for every bet company
                switch (betPosibility){
                    case Home: 
                        yield = yield.add(yieldHome(m.getRegularTimeWinner(), betIter.next()));
                        break;
                    case Guest:
                        yield = yield.add(yieldGuest(m.getRegularTimeWinner(), betIter.next()));
                        break;
                    case Favorit:
                        yield = yield.add(yieldFavorit(m.getRegularTimeWinner(), betIter.next()));
                        break;
                    case Loser:
                        yield = yield.add(yieldLooser(m.getRegularTimeWinner(), betIter.next()));
                        break;
                    case Tie:
                        yield = yield.add(yieldTie(m.getRegularTimeWinner(), betIter.next()));
                        break;
                }
            }
        }
        return yield;
    }
    
    /**
     * Vráti zisk za predpokladu stávky na domáceho. Je to buď čistý zisk
     * (kurz - 1) pri výhre, alebo -1 pri prehre.
     * @param winner
     * @param bet
     * @return 
     */
    private BigDecimal yieldHome(Winner winner, Bet1x2 bet){
        if(winner == Winner.Team1){
            return bet.getHomeBet().subtract(BigDecimal.ONE);
        }
        return MINUS_ONE;
    }
    
    /**
     * Vráti zisk za predpokladu stávky na hosťa. Je to buď čistý zisk
     * (kurz - 1) pri výhre, alebo -1 pri prehre.
     * @param winner
     * @param bet
     * @return 
     */
    private BigDecimal yieldGuest(Winner winner, Bet1x2 bet){
        if(winner == Winner.Team2){
            return bet.getGuestBet().subtract(BigDecimal.ONE);
        }
        return MINUS_ONE;
    }
    
    /**
     * Vráti zisk za predpokladu stávky na favorita. Je to buď čistý zisk
     * (kurz - 1) pri výhre, alebo -1 pri prehre.
     * @param winner
     * @param bet
     * @return 
     */
    private BigDecimal yieldFavorit(Winner winner, Bet1x2 bet){
        if((winner == Winner.Team1 && bet.bet1.compareTo(bet.bet2) == -1)
                || (winner == Winner.Team2 && bet.bet2.compareTo(bet.bet1) == -1)){
            return bet.getFavoritBet().subtract(BigDecimal.ONE);
        }
        return MINUS_ONE;
    }
    
    /**
     * Vráti zisk za predpokladu stávky na stávkovo slabšieho. Je to buď 
     * čistý zisk (kurz - 1) pri výhre, alebo -1 pri prehre.
     * @param winner
     * @param bet
     * @return 
     */
    private BigDecimal yieldLooser(Winner winner, Bet1x2 bet){
        if((winner == Winner.Team1 && bet.bet1.compareTo(bet.bet2) == 1)
                || (winner == Winner.Team2 && bet.bet2.compareTo(bet.bet1) == 1)){
            return bet.getLooserBet().subtract(BigDecimal.ONE);
        }
        return MINUS_ONE;
    }
    
    /**
     * Vráti zisk za predpokladu stávky na remízu. Je to buď čistý zisk
     * (kurz - 1) pri výhre, alebo -1 pri prehre.
     * @param winner
     * @param bet
     * @return 
     */
    private BigDecimal yieldTie(Winner winner, Bet1x2 bet){
        if(winner == Winner.Tie){
            return bet.getTieBet().subtract(BigDecimal.ONE);
        }
        return MINUS_ONE;
    }
    
    @Override
    protected Yield1x2 getNewYieldInstance() {
        return new Yield1x2();
    }

    @Override
    protected void splitToScales(Collection<Match> matches, YieldProperties properties) {
        if(splittedMatches != null){
            splittedMatches.clear();
        }
        splittedMatches = new HashMap<>();
        List<BigDecimal> scales = properties.getScale();
        if(scales.isEmpty()){
            splittedMatches.put(new Key1x2(BigDecimal.ONE, MAX_VALUE), new LinkedList<Match>());
        } else if(scales.size() == 1){
            splittedMatches.put(new Key1x2(BigDecimal.ONE, scales.get(0)), new LinkedList<Match>());
            splittedMatches.put(new Key1x2(scales.get(0).add(ZERO_POINT_ZERO_ONE), MAX_VALUE),
                    new LinkedList<Match>());
        } else {
            BigDecimal last = BigDecimal.ONE;
            for(int i = 0; i < scales.size(); i++){
                BigDecimal actual = scales.get(i);
                splittedMatches.put(new Key1x2(last, actual), new LinkedList<Match>());
                last = actual.add(ZERO_POINT_ZERO_ONE);
            }
            splittedMatches.put(new Key1x2(last, MAX_VALUE), new LinkedList<Match>());
        }
        
        if(matches == null){
            return;
        }
        for(Match m : matches){
            Collection<Bet1x2> bets = m.getBet(properties.getBetCompany(), Bet1x2.class);
            if(!bets.isEmpty()){
                BigDecimal bet = bets.iterator().next().getFavoritBet();
                for(Entry<Key1x2, List<Match>> entry : splittedMatches.entrySet()){
                    if(entry.getKey().fitRange(bet)){
                        entry.getValue().add(m);
                        break;
                    }
                }
            }
        }
        for(List<Match> l : splittedMatches.values()){
            Collections.sort(l);
        }
    }
    
    public static class Key1x2{
        private final BigDecimal lower;
        private final BigDecimal upper;
        
        public Key1x2(BigDecimal lower, BigDecimal upper) {
            this.lower = lower;
            this.upper = upper;
        }

        @Override
        public int hashCode() {
            return (lower == null ? 0 : lower.hashCode())
                    + (upper == null ? 0 : upper.hashCode());
        }
        
        @Override
        public boolean equals(Object obj) {
            if(this == obj){
                return true;
            }
            if(!(obj instanceof Key1x2)){
                return false;
            }
            Key1x2 other = (Key1x2) obj;
            return (this.lower == null ? other.lower == null : this.lower.compareTo(other.lower) == 0)
                    && (this.upper == null ? other.upper == null : this.upper.compareTo(other.upper) == 0);
        }

        @Override
        public String toString() {
            return lower + " - " + upper;
        }
        
        private boolean fitRange(BigDecimal value){
            return lower.compareTo(value) <= 0 && upper.compareTo(value) >= 0;
        }
    }
}
