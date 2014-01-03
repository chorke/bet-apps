
package chorke.proprietary.bet.apps.core.httpparsing;

import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.core.match.MatchProperties;
import chorke.proprietary.bet.apps.core.Tuple;
import chorke.proprietary.bet.apps.core.bets.Bet1x2;
import chorke.proprietary.bet.apps.core.bets.BetAsianHandicap;
import chorke.proprietary.bet.apps.core.bets.BetBothTeamsToScore;
import chorke.proprietary.bet.apps.core.bets.BetDoubleChance;
import chorke.proprietary.bet.apps.core.bets.BetDrawNoBet;
import chorke.proprietary.bet.apps.core.bets.BetOverUnder;
import chorke.proprietary.bet.apps.core.match.sports.Sport;
import chorke.proprietary.bet.apps.io.BetIOException;
import chorke.proprietary.bet.apps.io.BetIOManager;
import chorke.proprietary.bet.apps.io.CloneableBetIOManager;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Stiahne, rozparsuje a uloží stávky z web stránky. 
 * Pre ukladanie používa konkrétnu implementáciu {@link BetIOManager}
 * @author Chorke
 */
public class BetexplorerComMultithreadParser implements HTMLBetParser{

    private static final String STRING_URL = "http://www.betexplorer.com";
    private static final String RESULTS = "/results/";
    private static final String SOCCER = "soccer/";
    private static final String HOCKEY = "hockey/";
    private static final String BASKETBALL = "basketball/";
    private static final String HANDBALL = "handball/";
    private static final String VOLLEYBALL = "volleyball/";
    private static final String BASEBALL = "baseball/";
    
    private static final String BET_1X2 = "1x2";
    private static final String BET_OVER_UNDER = "ou";
    private static final String BET_ASIAN_HANDICAP = "ah";
    private static final String BET_DRAW_NO_BET = "ha";
    private static final String BET_DOUBLE_CHANCE = "dc";
    private static final String BET_BOTH_TEAMS_TO_SCORE = "bts";
    
    private static final String[] betsType = {BET_1X2, BET_OVER_UNDER,
                        BET_ASIAN_HANDICAP, BET_DRAW_NO_BET,
                        BET_DOUBLE_CHANCE, BET_BOTH_TEAMS_TO_SCORE};
    
    private final Object LOCK_FOR_TO_DOWNLOAD = new Object();
    private final Object LOCK_FOR_TO_PARSE = new Object();
    private List<Tuple<BettingSports, Calendar>> toDownload;
    private ListIterator<Tuple<BettingSports, Calendar>> toDownloadIterator;
    private List<TextingMatch> toParse;
    private ListIterator<TextingMatch> toParseIterator;  
    
    private final Object WAITING_OBJECK = new Object();
    private final boolean[] TMCThreadsDoneStatus = new boolean[NUM_OF_TMCTHREAD];
    private final boolean[] MFTMCThreadsDoneStatus = new boolean[NUM_OF_MFTMCTHREAD];
    private boolean areAllTMCDone;
    private boolean areAllMFTMCDone;
    
    private static final int NUM_OF_TMCTHREAD = 12;
    private static final int NUM_OF_MFTMCTHREAD = 20;
    
    private Calendar startDate;
    private Calendar endDate;
    
    private BettingSports sport = BettingSports.All;
    
    private Collection<String> undownloadedMatches;
    private Collection<Tuple<BettingSports, Calendar>> undownloadedSports;
    private Collection<Match> unsavedMatches;
    
    private CloneableBetIOManager IOManager;
    
    public BetexplorerComMultithreadParser(CloneableBetIOManager IOManager) {
        startDate = new GregorianCalendar();
        endDate = new GregorianCalendar();
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 1);
        endDate.setTimeInMillis(startDate.getTimeInMillis());
        undownloadedMatches = new LinkedList<>();
        undownloadedSports = new LinkedList<>();
        unsavedMatches = new LinkedList<>();
        this.IOManager = IOManager;
    }
    
    @Override
    public Collection<Match> getMatchesOfDay(Calendar cal) {
        startDate.setTimeInMillis(cal.getTimeInMillis());
        endDate.setTimeInMillis(cal.getTimeInMillis());
        return getMatches();
    }

    @Override
    public Collection<Match> previousDay() {
        startDate.add(Calendar.DAY_OF_MONTH, -1);
        endDate.setTimeInMillis(startDate.getTimeInMillis());
        return getMatches();
   }

    @Override
    public Collection<Match> nextDay() {
        endDate.add(Calendar.DAY_OF_MONTH, 1);
        startDate.setTimeInMillis(endDate.getTimeInMillis());
        return getMatches();
    }

    @Override
    public Collection<Match> getMatches() {
        undownloadedMatches.clear();
        undownloadedSports.clear();
        unsavedMatches.clear();
        toParse = getAllTextingMatches();
        toParseIterator = toParse.listIterator();
        MFTMCThread[] mftmcThreads = new MFTMCThread[NUM_OF_MFTMCTHREAD];
        areAllMFTMCDone = false;
        Arrays.fill(MFTMCThreadsDoneStatus, false);
        for(int i = 0; i < NUM_OF_MFTMCTHREAD; i++){
            mftmcThreads[i] = new MFTMCThread(getCloneOfIOManager(), i);
            Thread t = new Thread(mftmcThreads[i]);
            t.start();
        }
        synchronized(WAITING_OBJECK){
            while(!areAllMFTMCDone){
                try{
                    WAITING_OBJECK.wait();
                } catch (InterruptedException ex){}
            }
        }
        List<Match> matches = new LinkedList<>();
        for(MFTMCThread th : mftmcThreads){
            matches.addAll(th.matches);
        }
        toParseIterator = null;
        return matches;
    }

    /**
     * Vytovrí klon {@link CloneableBetIOManager}, aby každé vlákno mohlo 
     * používať vlastného manažéra.
     * @return 
     */
   
    private CloneableBetIOManager getCloneOfIOManager(){
        if(IOManager == null){
            return null;
        } else {
            return IOManager.cloneBetIOManager();
        }
    }
    
    @Override
    public Collection<String> getUndownloadedMatches() {
        return Collections.unmodifiableCollection(undownloadedMatches);
    }

    @Override
    public Collection<Tuple<BettingSports, Calendar>> getUndownloadedSports() {
        return Collections.unmodifiableCollection(undownloadedSports);
    }
    
    @Override
    public Collection<Match> getUnsavedMatches(){
        return Collections.unmodifiableCollection(unsavedMatches);
    }
    
    private List<TextingMatch> getAllTextingMatches(){
        toDownload = prepareSportsAndDates();
        toDownloadIterator = toDownload.listIterator();
        TMCThread[] tmcThreads = new TMCThread[NUM_OF_TMCTHREAD];
        Arrays.fill(TMCThreadsDoneStatus, false);
        areAllTMCDone = false;
        for(int i = 0; i < NUM_OF_TMCTHREAD; i++){
            tmcThreads[i] = new TMCThread(i);
            Thread t = new Thread(tmcThreads[i]);
            t.start();
        }
        synchronized(WAITING_OBJECK){
            while(!areAllTMCDone){
                try{
                    WAITING_OBJECK.wait();
                } catch (InterruptedException ex){}
            }
        }
        List<TextingMatch> matches = new LinkedList<>();
        for(TMCThread th : tmcThreads){
            matches.addAll(th.matches);
        }
        toDownloadIterator = null;
        return matches;
    }
    
    private void TMCThreadIsDone(int threadId){
        synchronized(TMCThreadsDoneStatus){
            TMCThreadsDoneStatus[threadId] = true;
            areAllTMCDone = true;
            for(boolean b : TMCThreadsDoneStatus){
                if(!b){
                    areAllTMCDone = false;
                    break;
                }
            }
        }
        if(areAllTMCDone){
            synchronized(WAITING_OBJECK){
                WAITING_OBJECK.notifyAll();
            }
        }
    }
    
    private void MFTMCThreadIsDone(int threadiId){
        synchronized(MFTMCThreadsDoneStatus){
            MFTMCThreadsDoneStatus[threadiId] = true;
            areAllMFTMCDone = true;
            for(boolean b : MFTMCThreadsDoneStatus){
                if(!b){
                    areAllMFTMCDone = false;
                    break;
                }
            }
        }
        if(areAllMFTMCDone){
            synchronized(WAITING_OBJECK){
                WAITING_OBJECK.notifyAll();
            }
        }
    }
    
    /**
     * Vráti ďalší šport a deň, ktorý sa má stiahnuť. Metóda je vláknovo bezpečná,
     * môže byť teda volaná viacerými {@link TMCThread}
     * Vracia {@code null} ak nie je ďalší šport a deň.
     * @return 
     * @see TMCThread
     */
    private Tuple<BettingSports, Calendar> getNextSportForDownload(){
        synchronized(LOCK_FOR_TO_DOWNLOAD){
            if(toDownloadIterator.hasNext()){
                return toDownloadIterator.next();
            } else {
                return null;
            }
        }
    }
    
    /**
     * Vráti ďalší zápas, ktorý sa má stiahnuť. Metóda je vláknovo bezpečná,
     * môže byť teda volaná viacerými {@link MFTMCThread}.
     * Vracia {@code null} ak nie je ďalší zápas.
     * @return 
     * @see MFTMCThread
     */
    private TextingMatch getNextTextingMatch(){
        synchronized(LOCK_FOR_TO_PARSE){
            if(toParseIterator.hasNext()){
                return toParseIterator.next();
            } else {
                return null;
            }
        }
    }
    
    /**
     * Pripraví potrebné športy a dátumy na stiahnutie podľa zvoleného
     * športu a začiatočného a koncového dátumu
     * @return 
     */
    private List<Tuple<BettingSports, Calendar>> prepareSportsAndDates(){
        List<Tuple<BettingSports, Calendar>> out = new LinkedList<>();
        Calendar c;
        for(long i = startDate.getTimeInMillis(); 
                i <= endDate.getTimeInMillis();
                i += 86_400_000){
            c = new GregorianCalendar();
            c.setTimeInMillis(i);
            if(sport == BettingSports.All) {
                out.add(new Tuple(BettingSports.Soccer, c));
                out.add(new Tuple(BettingSports.Baseball, c));
                out.add(new Tuple(BettingSports.Basketball, c));
                out.add(new Tuple(BettingSports.Handball, c));
                out.add(new Tuple(BettingSports.Hockey, c));
                out.add(new Tuple(BettingSports.Volleyball, c));
            } else {
                out.add(new Tuple(sport, c));
            }
        }
        return out;
    }
    
    @Override
    public void setExploredSport(BettingSports sport) {
        this.sport = sport;
    }

    @Override
    public BettingSports getExploredSport() {
        return sport;
    }

    @Override
    public void setStartDate(Calendar start) {
        this.startDate.setTimeInMillis(start.getTimeInMillis());
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 1);
    }
    
    @Override
    public void setEndDate(Calendar end) {
        this.endDate.setTimeInMillis(end.getTimeInMillis());
        endDate.set(Calendar.HOUR_OF_DAY, 0);
        endDate.set(Calendar.MINUTE, 0);
        endDate.set(Calendar.SECOND, 0);
        endDate.set(Calendar.MILLISECOND, 1);
    }

    /**
     * TMC - Texting Match Collector. Vlákno pre získavanie {@link TextingMatch}
     * zo stránky. Podľa zvoleného športu a dňa stiahne a pripravý všetky zápasy.
     * Pre získanie {@link TextingMatch} používa {@link TextingMatchCollector}.
     * Šport a deň získava metódou {@link #getNextSportForDownload()} pokiaľ 
     * táto metóda nevráti null. Jej vykonávanie potom končí a výsledky sú 
     * dostupné v premennej {@code matches}.
     * 
     * @see TextingMatch
     * @see TextingMatchCollector
     * @see #getNextSportForDownload()
     */
    private class TMCThread implements Runnable{
        
        private final int id;
        private List<TextingMatch> matches;

        public TMCThread(int id) {
            this.id = id;
        }
        
        @Override
        public void run() {
            matches = new LinkedList<>();
            Tuple<BettingSports, Calendar> tuple = getNextSportForDownload();
            TextingMatchCollector tmc;
            while(tuple != null){
                tmc = new TextingMatchCollector(tuple.first, tuple.second);
                matches.addAll(tmc.getMatchesList());
                tuple = getNextSportForDownload();
            }
            TMCThreadIsDone(id);
            System.err.println("Done TMC(" + id + ")");
        }
    }
    
    /**
     * MFTMC - Match From Texting Match Collector. Vlákno pre získanie zápasov
     * z {@link TextingMatch}. Stihane všetky infomácie o zápase zo stránky.
     * Pre získanie ďalšieho {@link TextingMatch} používa metódu {@link #getNextTextingMatch()}.
     * Ak metóda vráti {@code null}, metóda končí a výsledok je prístupný
     * v premennej {@code matches}. Pre získanie zápasu používa triedu
     * {@link MatchFromTextingMatchCollector}.
     * 
     * @see MatchFromTextingMatchCollector
     * @see TextingMatch
     * @see #getNextTextingMatch()
     * 
     */
    private class MFTMCThread implements Runnable{

        private List<Match> matches;
        private final int id;
        private BetIOManager manager;

        public MFTMCThread(BetIOManager manager, int id) {
            this.manager = manager;
            this.id = id;
        }

        @Override
        public void run() {
            matches = new LinkedList<>();
            TextingMatch tm = getNextTextingMatch();
            MatchFromTextingMatchCollector mftmc;
            Match match;
            while(tm != null){
                mftmc = new MatchFromTextingMatchCollector(tm);
                match = mftmc.parseMatchDetails();
                if(!match.getBets().isEmpty()){
                    if(manager != null){
                        try{
                            manager.saveMatch(match);
                        } catch (BetIOException ex){
                            unsavedMatches.add(match);
                        }
                    }
                    matches.add(match);
                }
                tm = getNextTextingMatch();
            }
            MFTMCThreadIsDone(id);
            System.err.println("Done MFTMC (" + id + ")");
        }
        
    }
    
    /**
     * Stiahne stránku so zvoleným dňom a zápasom a vytvorí z nich {@link TextingMatch}.
     */
    private class TextingMatchCollector{
        private final BettingSports exploredSport;
        private final String exploredSportString;
        private final Calendar date;
        private final DocumentDownloader docDwnl = new DocumentDownloader();

        public TextingMatchCollector(BettingSports exploredSport, Calendar date) {
            this.exploredSport = exploredSport;
            switch (exploredSport){
                case Baseball:
                    exploredSportString = BASEBALL;
                    break;
                case Basketball:
                    exploredSportString = BASKETBALL;
                    break;
                case Handball:
                    exploredSportString = HANDBALL;
                    break;
                case Hockey:
                    exploredSportString = HOCKEY;
                    break;
                case Volleyball:
                    exploredSportString = VOLLEYBALL;
                    break;
                default:
                    exploredSportString = SOCCER;
            }
            this.date = date;
        }

        /**
         * Stiahne a vráti všetky zápasy športu vo zvolenom dni vo forme 
         * {@link TextingMatch}.
         * @return 
         */
        private List<TextingMatch> getMatchesList(){
            List<TextingMatch> out = new LinkedList<>();
            Document document = docDwnl.getDocument(getURLForMatchesPageByDate());
            if(document == null){
                undownloadedSports.add(new Tuple(exploredSport, date));
                return out;
            }
            String href;
            if(document.select("div.nr-nodata-info").isEmpty()){
                Elements tableElements = document.getElementsByTag("table");
                for(Element table : tableElements){
                    Elements rtitle = table.select("tr.rtitle");
                    if(!rtitle.isEmpty()){
                        Elements trElements = table.select("tr[data-dt^= " 
                                + exploredDateTableAttributeString() + "]");
                        TextingMatch tm;
                        if(!trElements.isEmpty()){
                            for(Element el : trElements){
                                tm = new TextingMatch();
                                tm.league = rtitle.get(0).text();
                                tm.match = el.text();
                                href = el.select("a").attr("href");
                                document = docDwnl.getDocument(getURLForMatchScore(href));
                                if(document == null){
                                    undownloadedMatches.add(getURLForMatchScore(href));
                                } else {
                                    tm.score = document.select("table.tscore tr th.scorecell").text() 
                                                + document.select("table.tscore tr td.center").text();
                                    tm.matchID = getMatchID(href);
                                    tm.date.setTimeInMillis(date.getTimeInMillis());
                                    tm.sport = exploredSport;
                                    out.add(tm);
                                }
                            }
                        }
                    }
                }
            }
            return out;
        }
        
        /**
         * Pripraví url adresu pre zvolený deň a šport.
         * @return 
         */
        private String getURLForMatchesPageByDate(){
            return STRING_URL 
                    + RESULTS 
                    + exploredSportString 
                    + exploredDateURLString();
        }
        
        /**
         * Pripraví argument v url adrese zodpovedajúci zvolenému dátumu.
         * @return 
         */
        private String exploredDateURLString(){
            StringBuilder builder = new StringBuilder(26);
            builder.append("?year=")
                    .append(date.get(Calendar.YEAR))
                    .append("&month=")
                    .append(date.get(Calendar.MONTH) + 1)
                    .append("&day=")
                    .append(date.get(Calendar.DAY_OF_MONTH));
            return builder.toString();
        }
        
        /**
         * Pripraví argument dátumu vo forme používanej na stránke v tabuľke.
         * @return 
         */
        private String exploredDateTableAttributeString(){
            StringBuilder builder = new StringBuilder(27);
            builder.append(date.get(Calendar.DAY_OF_MONTH))
                    .append(",")
                    .append(date.get(Calendar.MONTH) + 1)
                    .append(",")
                    .append(date.get(Calendar.YEAR));
            return builder.toString();
        }
        
        /**
         * Vytvorí url odkaz pre konkrétny zápas.
         * @param matchURL
         * @return 
         */
        private String getURLForMatchScore(String matchURL){
            return STRING_URL + matchURL;
        }
        
        /**
         * Získa id zápasu na stránke z odkazu v texte.
         * @param url
         * @return 
         */
        private String getMatchID(String url){
            int startIdx = url.indexOf("matchid") + 8;
            return url.substring(startIdx, startIdx + 8);
        }
        
    }
    
    /**
     * Z príslušného {@link TextingMatch} vytvorí {@link Match}. 
     */
    private class MatchFromTextingMatchCollector{
        private final TextingMatch textingMatch;
        private final DocumentDownloader docDwnl = new DocumentDownloader();

        public MatchFromTextingMatchCollector(TextingMatch textingMatch) {
            this.textingMatch = textingMatch;
        }
        
        /**
         * Stiahne detail zápasu a vytovrí z neho {@link Match}.
         * @return 
         */
        private Match parseMatchDetails(){
            Document doc;
            Elements tableRows;
            System.out.println(textingMatch);
            Match outputMatch = textingMatch.getMatch();
            for(String bt : betsType){
                doc = docDwnl.getDocument(getURLForMatchDetails(textingMatch.matchID, bt));
                if(doc == null){
                    undownloadedMatches.add(getURLForMatchDetails(textingMatch.matchID, bt));
                } else {
                    if(doc.select("div#no-odds-info").isEmpty()){
                        tableRows = doc.select("tbody tr");
                        processBetTable(tableRows, outputMatch, bt);
                    }
                }
            }
            return outputMatch;
        }
        
        /**
         * Pripraví adresu pre stiahnutie detailu zápasu podľa id zápasu a typu stávky.
         * 
         * @param matchID
         * @param betType
         * @return 
         */
        private String getURLForMatchDetails(String matchID, String betType){
            return STRING_URL 
                    + "/gres/ajax-matchodds.php/?t=d&e="
                    + matchID + "&b=" + betType;
        }
        
        /**
         * Spracuje tabuľku stávok a pridá ich ku {@code match}
         * @param tableRows
         * @param match
         * @param betType 
         */
        private void processBetTable(Elements tableRows, Match match, String betType){
            switch (betType){
                case BET_1X2:
                    process1x2Bet(tableRows, match);
                    return;
                case BET_ASIAN_HANDICAP:
                    processAsianHandicapBet(tableRows, match);
                    return;
                case BET_BOTH_TEAMS_TO_SCORE:
                    processBothTeamsToScoreBet(tableRows, match);
                    return;
                case BET_DOUBLE_CHANCE:
                    processDoubleChanceBet(tableRows, match);
                    return;
                case BET_DRAW_NO_BET:
                    processDrawNoBetBet(tableRows, match);
                    return;
                case BET_OVER_UNDER:
                    processOverUnderBet(tableRows, match);
            }
        }
        
        /**
         * Spracuje tabuľku stávok 1x2
         * @param tableRows
         * @param match 
         */
        private void process1x2Bet(Elements tableRows, Match match){
            String name;
            Elements bets;
            for(Element row : tableRows){
                name = row.select("th").first().text();
                bets = row.select("td");
                match.addBet(new Bet1x2(name,
                        parseNumber(bets.get(0).attr("data-odd")),
                        parseNumber(bets.get(1).attr("data-odd")), 
                        parseNumber(bets.get(2).attr("data-odd"))));
            }
        }
        
        /**
         * Spracuje tabuľku stávok Over Under
         * @param tableRows
         * @param match 
         */
        private void processOverUnderBet(Elements tableRows, Match match){
            String name;
            Elements bets;
            for(Element row : tableRows){
                name = row.select("th").first().text();
                bets = row.select("td");
                if(bets.get(0).select(".doublepar").isEmpty()){
                    String[] s = bets.get(0).select(".doublepar-w").first().text().split("[ ]+");
                    match.addBet(new BetOverUnder(name, 
                        parseNumber(s[0]),
                        parseNumber(bets.get(1).attr("data-odd")),
                        parseNumber(bets.get(2).attr("data-odd")),
                            s[1]));
                } else {
                    match.addBet(new BetOverUnder(name, 
                            parseNumber(bets.get(0).select(".doublepar").first().text()),
                            parseNumber(bets.get(1).attr("data-odd")),
                            parseNumber(bets.get(2).attr("data-odd")),
                            ""));
                }
            }
        }
        
        /**
         * Spracuje tabuľku stávok Asian Handicap
         * @param tableRows
         * @param match 
         */
        private void processAsianHandicapBet(Elements tableRows, Match match){
            String name;
            Elements bets;
            String[] handicaps;
            for(Element row : tableRows){
                name = row.select("th").first().text();
                bets = row.select("td");
                if(!bets.get(0).select(".doublepar").isEmpty()){
                    handicaps = bets.get(0).select(".doublepar").first().text().split("[ ,]+");
                    if(handicaps.length == 1){
                        match.addBet(new BetAsianHandicap(name,
                                parseNumber(bets.get(1).attr("data-odd")),
                                parseNumber(bets.get(2).attr("data-odd")),
                                parseNumber(handicaps[0]),
                                ""));
                    } else {
                        match.addBet(new BetAsianHandicap(name,
                                parseNumber(bets.get(1).attr("data-odd")),
                                parseNumber(bets.get(2).attr("data-odd")),
                                parseNumber(handicaps[0]).add(parseNumber(handicaps[1])).divide(new BigDecimal("2")),
                                ""));
                    }
                } else {
                    handicaps = bets.get(0).select(".doublepar-w").first().text().split("[ ,]+");
                    if(handicaps.length == 2){
                        match.addBet(new BetAsianHandicap(name,
                                parseNumber(bets.get(1).attr("data-odd")),
                                parseNumber(bets.get(2).attr("data-odd")),
                                parseNumber(handicaps[0]),
                                handicaps[1]));
                    } else {
                        match.addBet(new BetAsianHandicap(name,
                                parseNumber(bets.get(1).attr("data-odd")),
                                parseNumber(bets.get(2).attr("data-odd")),
                                parseNumber(handicaps[0]).add(parseNumber(handicaps[1])).divide(new BigDecimal("2")),
                                handicaps[2]));
                    }
                }
            }
        }
        
        /**
         * Spracuje tabuľku stávok Draw No Bet
         * @param tableRows
         * @param match 
         */
        private void processDrawNoBetBet(Elements tableRows, Match match){
            String name;
            Elements bets;
            for(Element row : tableRows){
                name = row.select("th").first().text();
                bets = row.select("td");
                match.addBet(new BetDrawNoBet(name,
                        parseNumber(bets.get(0).attr("data-odd")),
                        parseNumber(bets.get(1).attr("data-odd"))));
            }
        }
        
        /**
         * Spracuje tabuľku stávok Double Chance
         * @param tableRows
         * @param match 
         */
        private void processDoubleChanceBet(Elements tableRows, Match match){
            String name;
            Elements bets;
            for(Element row : tableRows){
                name = row.select("th").first().text();
                bets = row.select("td");
                match.addBet(new BetDoubleChance(name,
                        parseNumber(bets.get(0).attr("data-odd")),
                        parseNumber(bets.get(1).attr("data-odd")), 
                        parseNumber(bets.get(2).attr("data-odd"))));
            }
        }
        
        /**
         * Spracuje tabuľku stávok Both Teams To Score
         * @param tableRows
         * @param match 
         */
        private void processBothTeamsToScoreBet(Elements tableRows, Match match){
            String name;
            Elements bets;
            for(Element row : tableRows){
                name = row.select("th").first().text();
                bets = row.select("td");
                match.addBet(new BetBothTeamsToScore(name,
                        parseNumber(bets.get(0).attr("data-odd")),
                        parseNumber(bets.get(1).attr("data-odd"))));
            }
        }
        
        /**
         * Vráti {@link BigDecimal} zo stringu, alebo {@link BigDecimal#ZERO}
         * v prípade chyby.
         * @param parse
         * @return 
         */
        private BigDecimal parseNumber(String parse){
            try{
                return new BigDecimal(parse);
            } catch (NumberFormatException ex){
                return BigDecimal.ZERO;
            }
        }
    }
    
    /**
     * Trieda pre udržiavanie informácií pre stiahnutie detailu zápasu.
     * Obsahuje ligu, id zápasu, zápas, skóre, šport a deň.
     */
    private class TextingMatch{
        private String league;
        private String match;
        private String matchID;
        private String score;
        private BettingSports sport;
        private Calendar date = new GregorianCalendar();

        @Override
        public String toString() {
            return match + " " + score;
        }
        
        /**
         * Vytvorí zápas podľa aktuálnych nastavení parametrov.
         * @return 
         */
        private Match getMatch(){
            Match m = new Match(Sport.getSport(sport));
            MatchProperties prop = new MatchProperties();
            /* league */
            String[] split = league.split("[:]+");
            prop.setCountry(split[0].trim());
            prop.setLeague(removeEmptyCharacters(split[1]).trim());
            /* match info */
            split = match.split("[ ]+");
            date.set(Calendar.HOUR_OF_DAY, 
                    parseInt(split[0].substring(0, 2)));
            date.set(Calendar.MINUTE, 
                    parseInt(split[0].substring(3, 5)));
            prop.setDate(date);
            m.setProperties(prop);
            /* match score */
            split = score.split("[, ()]+");
            if(split.length > 1){
                String[] scoreSplit;
                for(int i = 2; i < split.length; i++){
                    scoreSplit = split[i].split(":");
                    m.addPartialScore(parseInt(removeEmptyCharacters(scoreSplit[0])),
                        parseInt(removeEmptyCharacters(scoreSplit[1])));
                }
            }
            return m;
        }
        
        /**
         * Odstráni prázdne znaky, ktoré sú používané na stránke a nie sú zachytené
         * metódou {@link String#trim()}.
         * @param s
         * @return 
         * @see String#trim()
         */
        private String removeEmptyCharacters(String s){
            byte[] newB = new byte[s.length()];
            int i = 0;
            for(byte b : s.getBytes()){
                if(b > 0){ newB[i++] = b;}
            }
            StringBuilder builder = new StringBuilder();
            for(int j = 0; j < i; j++){
                builder.append((char)newB[j]);
            }
            return builder.toString();
        }
        
        /**
         * Parsuje {@code int} hodnotu príslušného {@code parse}. V prípade chyby
         * vracia 0.
         * @param parse
         * @return 
         */
        private int parseInt(String parse){
            try{
                return Integer.parseInt(parse);
            } catch (NumberFormatException ex){
                return 0;
            }
        }
    }
}