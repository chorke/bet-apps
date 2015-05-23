
package chorke.bet.apps.core.calculators;

import chorke.bet.apps.core.CoreUtils.BetPossibility;
import chorke.bet.apps.core.CoreUtils.Periode;
import static chorke.bet.apps.core.CoreUtils.Periode.Day;
import static chorke.bet.apps.core.CoreUtils.Periode.Month;
import static chorke.bet.apps.core.CoreUtils.Periode.Week;
import static chorke.bet.apps.core.CoreUtils.Periode.Year;
import chorke.bet.apps.core.Tuple;
import chorke.bet.apps.core.bets.Bet1x2;
import chorke.bet.apps.core.match.Match;
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
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface pre počítanie zisku zo stávok.
 * 
 * @author Chorke
 */
public abstract class YieldCalculator<Y extends Yield, K> {
    
    protected static final Logger LOG = LoggerFactory.getLogger(YieldCalculator.class);
    
    protected Map<K, List<Match>> splittedMatches;
    protected static final BigDecimal MINUS_ONE = new BigDecimal("-1");
    public static final BigDecimal ZERO_POINT_ZERO_ONE = new BigDecimal("0.01");
    public static final BigDecimal MAX_VALUE = new BigDecimal(Double.MAX_VALUE);
    
    private final BetPossibility[] betPossibilities = getBetPossibilities();

    /**
     * Vypočíta celkový zisk. 
     * @param matches zápasy
     * @param properties nastavenie pre počítanie zisku
     * @return celkový zisk
     */
    public Y getOverallYield(Collection<Match> matches, YieldProperties properties){
        splitToScales(matches, properties);
        Y yield = getNewYieldInstance();
        yield.setProperties(properties);
        for(Entry<K, List<Match>> entry : splittedMatches.entrySet()){
            for(BetPossibility bp : betPossibilities){
                LOG.debug("Getting yield for {}, for bet {}", entry.getKey(), bp);
                yield.addYieldForKey(bp, entry.getKey(), 
                        getYield(entry.getKey(), entry.getValue(), properties, bp));
                yield.addMatchesCountForKey(entry.getKey(), entry.getValue().size());
            }
        }
        return yield;
    }
    
    /**
     * Vypočíta zisk podľa období.
     * 
     * @param matches zápasy
     * @param properties vlastnosti zisku
     * @param periode obdobia, ktoré majú byť zlučované pri počítaní zisku
     * @return zisky podľa zvolených období.
     */
    public Map<Calendar, Y> getPeriodicYield(Collection<Match> matches,
            YieldProperties properties, Periode periode) {
        LOG.debug("Getting yield for {}.", periode);
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
        throw new IllegalArgumentException("Periode " + periode + " is not supported.");
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
    private Map<Calendar, Y> innerPeriodicYield(Collection<Match> matches,
                YieldProperties properties, PeriodHolder periodHolder) {
        splitToScales(matches, properties);
        Map<Calendar, Y> yields = new HashMap<>();
        Y yield;
        for(Entry<K, List<Match>> entry : splittedMatches.entrySet()){
            LOG.debug("Getting yield for key {}.", entry.getKey());
            List<Match> rangeMatches = entry.getValue();
            if(rangeMatches.isEmpty()){
                continue;
            }
            int from = 0, to = 0;
            periodHolder.setDate((Calendar)rangeMatches.get(0).getProperties().getDate().clone());
            while(from < rangeMatches.size()){
                LOG.trace("Period from idx {} to idx {}.", from, to);
                if(yields.containsKey(periodHolder.date)){
                    yield = yields.get(periodHolder.date);
                } else {
                    yield = getNewYieldInstance();
                    yield.setProperties(properties);
                    yields.put((Calendar)periodHolder.date.clone(), yield);
                }
                to = periodHolder.lastSuitedMatchIndex(from, rangeMatches);
                List<Match> sublist = rangeMatches.subList(from, to);
                for(BetPossibility bp : betPossibilities){
                    LOG.debug("Getting yield for bet {}.", bp);
                    yield.addYieldForKey(bp, entry.getKey(), getYield(entry.getKey(), sublist, properties, bp));
                    yield.addMatchesCountForKey(entry.getKey(), sublist.size());
                }
                periodHolder.nextPeriod();
                from = to;
            }
        }
        return yields;
    }
    
    /**
     * Returns a new instace of {@link Yield}.
     * 
     * @return new intance of subclass of {@link Yield}
     */
    protected abstract Y getNewYieldInstance();
    
    /**
     * Returns yield for all matches in the collection.
     * 
     * @param key 
     * @param matches matches
     * @param properties properties
     * @param betPosibility bet posibility
     * @return 
     */
    protected abstract BigDecimal getYield(K key, Collection<Match> matches,
            YieldProperties properties, BetPossibility betPosibility);
    
    /**
     * Vypočíta kumulatívny celkový zisk. 
     * 
     * @param matches zápasy
     * @param properties nastavenia pre počítanie zisku
     * @return celkový kumulatívny zisk
     */
    public Y getCumulativeYield(K key, Collection<Match> matches, CumulativeYieldProperties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Vypočíta kumulatívny zisk podľa obodbia.
     * 
     * @param matches zápasy
     * @param properties nastavenie pre počítanie zisku
     * @param period obdobie
     * @return kumulatvíny zisk podľa období
     */
    public Map<Calendar, Y> getCumulativePeriodicYield(Collection<Match> matches,
            CumulativeYieldProperties properties, Periode period) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Vráti podporované možnosti stávok.
     * 
     * @return 
     */
    public BetPossibility[] getBetPossibilities(){
        return getNewYieldInstance().getSupportedBetPossibilities();
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
    protected abstract void splitToScales(Collection<Match> matches, YieldProperties properties);
    
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
            if(LOG.isTraceEnabled()){
                LOG.trace("Old date {}", super.date);
            }
            super.date.add(Calendar.DATE, 1);
            if(LOG.isTraceEnabled()){
                LOG.trace("New date {}", super.date);
            }
        }

        @Override
        boolean suits(Match m) {
            Calendar c = m.getProperties().getDate();
            boolean out = c.get(Calendar.DATE) == super.date.get(Calendar.DATE)
                    && c.get(Calendar.MONTH) == super.date.get(Calendar.MONTH)
                    && c.get(Calendar.YEAR) == super.date.get(Calendar.YEAR);
            if(LOG.isTraceEnabled()){
                LOG.trace("Actual date {}, date in match {} [suites = {}]", super.date, c, out);
            }
            return out;
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
