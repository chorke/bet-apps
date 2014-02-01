
package chorke.proprietary.bet.apps.core.calculators;

import chorke.proprietary.bet.apps.StaticConstants.Winner;
import chorke.proprietary.bet.apps.core.Tuple;
import chorke.proprietary.bet.apps.core.bets.Bet1x2;
import chorke.proprietary.bet.apps.core.calculators.Yield.BetPossibility;
import chorke.proprietary.bet.apps.core.match.Match;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * @author Chorke
 */
public class Yield1x2Calculator implements YieldCalculator<Yield1x2>{

   private Map<Integer, List<Match>> splittedMatches;
    
    @Override
    public Yield1x2 getOverallYield(Collection<Match> matches, YieldProperties properties) {
        splitToScales(matches, properties);
        Yield1x2 yield = new Yield1x2();
        yield.setProperties(properties);
        for(Integer i : splittedMatches.keySet()){
            for(BetPossibility bp : BetPossibility.values()){
                yield.addYieldForScaleIndex(bp, i, 
                        getYield(splittedMatches.get(i), properties, bp));
                yield.addMatchesCountForScaleIndex(i, splittedMatches.get(i).size());
            }
        }
        return yield;
    }

    @Override
    public Map<Calendar, Yield1x2> getPeriodicYieldDAY(Collection<Match> matches,
                YieldProperties properties) {
        return innerPeriodicYield(matches, properties, new PeriodHolderDAY());
    }

    @Override
    public Map<Calendar, Yield1x2> getPeriodicYieldWEEK(Collection<Match> matches, 
                YieldProperties properties) {
        return innerPeriodicYield(matches, properties, new PeriodHolderWEEK());
    }

    @Override
    public Map<Calendar, Yield1x2> getPeriodicYieldMONTH(Collection<Match> matches, 
                YieldProperties properties) {
        return innerPeriodicYield(matches, properties, new PeriodHolderMONTH());
    }

    @Override
    public Map<Calendar, Yield1x2> getPeriodicYieldYEAR(Collection<Match> matches, 
                YieldProperties properties) {
        return innerPeriodicYield(matches, properties, new PeriodHolderYEAR());
    }

    @Override
    public Yield1x2 getCumulativeYield(Collection<Match> matches, 
                CumulativeYieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Calendar, Yield1x2> getCumulativePeriodicYieldDAY(Collection<Match> matches,
                CumulativeYieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Calendar, Yield1x2> getCumulativePeriodicYieldWEEK(Collection<Match> matches, 
                CumulativeYieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Calendar, Yield1x2> getCumulativePeriodicYieldMONTH(Collection<Match> matches,
                CumulativeYieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Calendar, Yield1x2> getCumulativePeriodicYieldYEAR(Collection<Match> matches, 
                CumulativeYieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private Map<Calendar, Yield1x2> innerPeriodicYield(Collection<Match> matches,
                YieldProperties properties, PeriodHolder periodHolder) {
        splitToScales(matches, properties);
        Map<Calendar, Yield1x2> yields = new HashMap<>();
        List<Match> rangeMatches;
        List<Match> sublist;
        Yield1x2 yield;
        for(Integer i : splittedMatches.keySet()){
            rangeMatches = splittedMatches.get(i);
            if(rangeMatches.isEmpty()){ continue; }
            int from = 0, to;
            periodHolder.setDate((Calendar)rangeMatches.get(0).getProperties().getDate().clone());
            while(from < rangeMatches.size()){
                if(yields.containsKey(periodHolder.date)){
                    yield = yields.get(periodHolder.date);
                } else {
                    yield = new Yield1x2();
                    yield.setProperties(properties);
                    yields.put((Calendar)periodHolder.date.clone(), yield);
                }
                to = periodHolder.lastSuitedMatchIndex(from, rangeMatches);
                sublist = rangeMatches.subList(from, to);
                for(BetPossibility bp : BetPossibility.values()){
                    yield.addYieldForScaleIndex(bp, i, getYield(sublist, properties, bp));
                    yield.addMatchesCountForScaleIndex(i, sublist.size());
                }
                periodHolder.nextPeriod();
                from = to;
            }
        }
        return yields;
    }
    
    private BigDecimal getYield(Collection<Match> matches, YieldProperties properties, 
            BetPossibility betPosibility){
        BigDecimal yield = new BigDecimal("0");
        Iterator<Bet1x2> betIter;
        for(Match m : matches){
            betIter = m.getBet(properties.getBetCompany(), Bet1x2.class).iterator();
            if(betIter.hasNext()){
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
                    case Looser:
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
    
    private BigDecimal yieldHome(Winner winner, Bet1x2 bet){
        if(winner == Winner.Team1){
            return bet.getHomeBet().subtract(BigDecimal.ONE);
        }
        return new BigDecimal("-1");
    }
    
    private BigDecimal yieldGuest(Winner winner, Bet1x2 bet){
        if(winner == Winner.Team2){
            return bet.getGuestBet().subtract(BigDecimal.ONE);
        }
        return new BigDecimal("-1");
    }
    
    private BigDecimal yieldFavorit(Winner winner, Bet1x2 bet){
        if((winner == Winner.Team1 && bet.bet1.compareTo(bet.bet2) == -1)
                || (winner == Winner.Team2 && bet.bet2.compareTo(bet.bet1) == -1)){
            return bet.getFavoritBet().subtract(BigDecimal.ONE);
        }
        return new BigDecimal("-1");
    }
    
    private BigDecimal yieldLooser(Winner winner, Bet1x2 bet){
        if((winner == Winner.Team1 && bet.bet1.compareTo(bet.bet2) == 1)
                || (winner == Winner.Team2 && bet.bet2.compareTo(bet.bet1) == 1)){
            return bet.getLooserBet().subtract(BigDecimal.ONE);
        }
        return new BigDecimal("-1");
    }
    
    private BigDecimal yieldTie(Winner winner, Bet1x2 bet){
        if(winner == Winner.Tie){
            return bet.getTieBet().subtract(BigDecimal.ONE);
        }
        return new BigDecimal("-1");
    }
    
    /**
     * Rozdelí zápasy do skupín podľa rozsahov, ktoré má akutálne nastavené
     * {@code properties}. Zápasy zároveň v jednotlivých skupinách zoradí.
     * <br/>
     * <br/>
     * Metóda je protected - pre iné rozdelenie stačí vytvoriť rozširujúcu
     * triedu a prepísať metódu a tým upraviť akceptujúce hodnoty stávok
     * (vhodné prepísať aj 
     * {@link #getIndex(Match, List, YieldProperties)} a 
     * {@link #fitRange(BigDecimal, Tuple)}). Táto metóda má základné chovanie a 
     * neberie do úvahy zápasy s rovnakými stávkami na oba tými.
     * 
     * @param matches
     * @param properties
     * @return 
     */
    protected void splitToScales(Collection<Match> matches,
                 YieldProperties properties){
        splittedMatches = new HashMap<>();
        List<Tuple<BigDecimal, BigDecimal>> ranges = properties.getRanges();
        int i;
        for(i = 0; i < ranges.size(); i++){
            splittedMatches.put(i, new LinkedList<Match>());
        }
        for(Match m : matches){
            i = getIndex(m, ranges, properties);
            if(i != -1){
                splittedMatches.get(i).add(m);
            }
        }
        for(Integer in : splittedMatches.keySet()){
            Collections.sort(splittedMatches.get(in));
        }
    }

    /**
     * Vráti index, ktorý vyhovuje zápasu vzhľadom ku {@code ranges} (kategória,
     * do ktorej patrí zápas vzhľadom ku svojím kurzom).
     * Ak nevyhovuje žiaden index, vráti -1 
     * <br/>
     * <br/>
     * Metóda je protected - pre iné rozdelenie stačí vytvoriť rozširujúcu
     * triedu a prepísať metódu a tým upraviť akceptujúce hodnoty stávok
     * (vhodné prepísať aj 
     * {@link #splitToScales(Collection, YieldProperties)} a 
     * {@link #fitRange(BigDecimal, Tuple)}). Táto metóda má základné chovanie a 
     * neberie do úvahy zápasy s rovnakými stávkami na oba tými.
     * 
     * @param m
     * @param ranges
     * @return 
     * @see Bet1x2#getFavoritBet()
     */
    protected int getIndex(Match m, List<Tuple<BigDecimal, BigDecimal>> ranges,
            YieldProperties properties) {
        Iterator<Bet1x2> iter = m.getBet(properties.getBetCompany(), Bet1x2.class).iterator();
        if(!iter.hasNext()){
            return -1;
        }
        Bet1x2 bet = iter.next();
        for(int i = 0; i < ranges.size(); i++){
            if(fitRange(bet.getFavoritBet(), ranges.get(i))){
                return i;
            }
        }
        return -1;
    }
    
    
    protected boolean fitRange(BigDecimal value, Tuple<BigDecimal, BigDecimal> range){
        return range.first.compareTo(value) <= 0 && range.second.compareTo(value) >= 0;
    }
    
    private abstract class PeriodHolder{
        private Calendar date;
        
        abstract void nextPeriod();
        abstract boolean suits(Match m);
        
        int lastSuitedMatchIndex(int from, List<Match> matches) {
            ListIterator<Match> iter = matches.listIterator(from);
            int last = from;
            while(iter.hasNext()){
                if(suits(iter.next())){
                    last++;
                } else {
                    break;
                }
            }
            return last;
        }
        
        void setDate(Calendar date){
            this.date = date;
            this.date.set(Calendar.MILLISECOND, 0);
            this.date.set(Calendar.SECOND, 0);
            this.date.set(Calendar.MINUTE, 0);
            this.date.set(Calendar.HOUR_OF_DAY, 0);
        }
    }
    
    private class PeriodHolderDAY extends PeriodHolder{

        @Override
        void nextPeriod() {
            super.date.add(Calendar.DATE, 1);
        }

        @Override
        boolean suits(Match m) {
            Calendar c = m.getProperties().getDate();
            return c.get(Calendar.DATE) == super.date.get(Calendar.DATE)
                    && c.get(Calendar.MONTH) == super.date.get(Calendar.MONTH)
                    && c.get(Calendar.YEAR) == super.date.get(Calendar.YEAR);
        }
    }
    
    private class PeriodHolderWEEK extends PeriodHolder{

        @Override
        void nextPeriod() {
            super.date.add(Calendar.WEEK_OF_YEAR, 1);
        }

        @Override
        boolean suits(Match m) {
            Calendar c = m.getProperties().getDate();
            if(c.get(Calendar.MONTH) == 11 && c.get(Calendar.WEEK_OF_YEAR) == 1){
                return c.get(Calendar.YEAR) == super.date.get(Calendar.YEAR) - 1;
            }
            return c.get(Calendar.WEEK_OF_YEAR) == super.date.get(Calendar.WEEK_OF_YEAR)
                    && c.get(Calendar.YEAR) == super.date.get(Calendar.YEAR);
        }

        @Override
        void setDate(Calendar date) {
            super.setDate(date);
            super.date.set(Calendar.DAY_OF_WEEK, 1);
        }
        
    }
        
    private class PeriodHolderMONTH extends PeriodHolder{

        @Override
        void nextPeriod() {
            super.date.add(Calendar.MONTH, 1);
        }

        @Override
        boolean suits(Match m) {
            Calendar c = m.getProperties().getDate();
            return c.get(Calendar.MONTH) == super.date.get(Calendar.MONTH)
                    && c.get(Calendar.YEAR) == super.date.get(Calendar.YEAR);
        }

        @Override
        void setDate(Calendar date) {
            super.setDate(date);
            super.date.set(Calendar.DATE, 1);
        }
    }
    
    private class PeriodHolderYEAR extends PeriodHolder{

        @Override
        void nextPeriod() {
            super.date.add(Calendar.YEAR, 1);
        }

        @Override
        boolean suits(Match m) {
            Calendar c = m.getProperties().getDate();
            return c.get(Calendar.YEAR) == super.date.get(Calendar.YEAR);
        }

        @Override
        void setDate(Calendar date) {
            super.setDate(date);
            super.date.set(Calendar.DATE, 1);
            super.date.set(Calendar.MONTH, 0);
        }
    }
}
