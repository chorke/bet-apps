
package chorke.proprietary.bet.apps.core.calculators;

import chorke.proprietary.bet.apps.StaticConstants.BetPossibility;
import chorke.proprietary.bet.apps.StaticConstants.Periode;
import chorke.proprietary.bet.apps.StaticConstants.Winner;
import chorke.proprietary.bet.apps.core.Tuple;
import chorke.proprietary.bet.apps.core.bets.Bet1x2;
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
 * Počítadlo zisku pre stávky na 1x2.
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
    public Map<Calendar, Yield1x2> getPeriodicYield(Collection<Match> matches,
                YieldProperties properties, Periode periode) {
        switch (periode) {
            case Day:
                return innerPeriodicYield(matches, properties, new PeriodHolderDAY());
            case Week:
                return innerPeriodicYield(matches, properties, new PeriodHolderWEEK());
            case Month:
                return innerPeriodicYield(matches, properties, new PeriodHolderMONTH());
            case Year:
                return innerPeriodicYield(matches, properties, new PeriodHolderYEAR());
        }
        throw new UnsupportedOperationException("Periode " + periode + "is not supported.");
    }

    @Override
    public Yield1x2 getCumulativeYield(Collection<Match> matches, 
                CumulativeYieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Calendar, Yield1x2> getCumulativePeriodicYield(Collection<Match> matches,
                CumulativeYieldProperties properties, Periode periode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Vypočíta periodické zisky zo zápasov matches a podľa nastavení properties.
     * Obdobia, ktoré sú zlučované sú rozhodované podľa periodHolder. 
     * 
     * @param matches
     * @param properties
     * @param periodHolder
     * @return 
     */
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
    
    /**
     * Vypočíta a vráti zisk zo všetkých zápasov matches, podľa nastavení 
     * properties a pre zvolené stávkovú možnosť.
     * 
     * @param matches
     * @param properties
     * @param betPosibility
     * @return 
     */
    private BigDecimal getYield(Collection<Match> matches, YieldProperties properties, 
            BetPossibility betPosibility){
        BigDecimal yield = new BigDecimal("0");
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
        return new BigDecimal("-1");
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
        return new BigDecimal("-1");
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
        return new BigDecimal("-1");
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
        return new BigDecimal("-1");
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
    
    /**
     * Rozhodne, či hodnota value je v zadanom rozsahu range. Range je braný
     * ako uzavretý (vrátane krajných bodov) interval.
     * @param value
     * @param range
     * @return 
     */
    protected boolean fitRange(BigDecimal value, Tuple<BigDecimal, BigDecimal> range){
        return range.first.compareTo(value) <= 0 && range.second.compareTo(value) >= 0;
    }

    @Override
    public BetPossibility[] getBetPossibilities() {
        return new BetPossibility[]{BetPossibility.Home, BetPossibility.Tie, 
            BetPossibility.Guest, BetPossibility.Favorit, BetPossibility.Loser};
    }
    
    /**
     * Trieda pre spravovanie období, ktoré sú zlučované.
     */
    private abstract class PeriodHolder{
        private Calendar date;
        
        /**
         * Posunie dátum na ďalšie obdobie.
         */
        abstract void nextPeriod();
        
        /**
         * Rozhodne, či zápas zapadá do aktuálne nastaveného obdobia.
         * @param m
         * @return 
         * @see #setDate(java.util.Calendar) 
         * @see #nextPeriod() 
         */
        abstract boolean suits(Match m);
        
        /**
         * Vráti posledný index zápasu v matches, ktorý ešte vyhovuje akutálne
         * nastavenému dátumu. Používa metódu suits, teda ak nevyhovuje už ani 
         * počiatočný index from, je vrátené číslo from.
         * 
         * @param from počiatočný index, od ktorého sa má hľadať
         * @param matches zápasy
         * @return posledný index, ktorý je v rovnakom období ako nastavený 
         * dátum
         * @see #setDate(java.util.Calendar) 
         * @see #suits(chorke.proprietary.bet.apps.core.match.Match)
         */
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
        
        /**
         * Nastaví dátum. Podľa neho sa bude určovať, či zápasy vyhovujú
         * obdobiu.
         * 
         * @param date 
         * @see #suits(chorke.proprietary.bet.apps.core.match.Match)
         */
        void setDate(Calendar date){
            this.date = date;
            this.date.set(Calendar.MILLISECOND, 0);
            this.date.set(Calendar.SECOND, 0);
            this.date.set(Calendar.MINUTE, 0);
            this.date.set(Calendar.HOUR_OF_DAY, 0);
        }
    }
    
    /**
     * Trieda pre spravovanie období, ktoré sú zlučované.
     * Za obdobie uvažuje jeden deň.
     */
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
    
    /**
     * Trieda pre spravovanie období, ktoré sú zlučované.
     * Za obdobie uvažuje jeden týždeň.
     */
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
    
    /**
     * Trieda pre spravovanie období, ktoré sú zlučované.
     * Za obdobie uvažuje jeden mesiac.
     */
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
    
    /**
     * Trieda pre spravovanie období, ktoré sú zlučované.
     * Za obdobie uvažuje jeden rok.
     */
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
            super.date.set(Calendar.MONTH, Calendar.JANUARY);
        }
    }
}
