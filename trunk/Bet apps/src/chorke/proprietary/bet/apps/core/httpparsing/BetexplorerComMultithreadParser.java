/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.httpparsing;

import chorke.proprietary.bet.apps.StaticConstants.Sport;
import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.core.match.MatchProperties;
import chorke.proprietary.bet.apps.core.Tuple;
import chorke.proprietary.bet.apps.core.bets.Bet1x2;
import chorke.proprietary.bet.apps.core.bets.BetAsianHandicap;
import chorke.proprietary.bet.apps.core.bets.BetBothTeamsToScore;
import chorke.proprietary.bet.apps.core.bets.BetDoubleChance;
import chorke.proprietary.bet.apps.core.bets.BetDrawNoBet;
import chorke.proprietary.bet.apps.core.bets.BetOverUnder;
import chorke.proprietary.bet.apps.io.BetIOException;
import chorke.proprietary.bet.apps.io.BetIOManager;
import chorke.proprietary.bet.apps.io.CloneableBetIOManager;
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
    
    private static final int NUM_OF_TMCTHREAD = 12;
    private static final int NUM_OF_MFTMCTHREAD = 20;
    
    private Calendar startDate;
    private Calendar endDate;
    private Calendar actualProcessingDate;
    private Sport sport = Sport.All;
//    private String exploredSportString = SOCCER;
    private List<Tuple<Sport, Calendar>> toDownload;
    private List<TextingMatch> toParse;
    private ListIterator<Tuple<Sport, Calendar>> toDownloadIterator;
    private ListIterator<TextingMatch> toParseIterator;  
    private Collection<String> undownloadedMatches;
    private Collection<Tuple<Sport, Calendar>> undownloadedSports;
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
        actualProcessingDate = new GregorianCalendar();
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
    @SuppressWarnings("SleepWhileInLoop")
    public Collection<Match> getMatches() {
        undownloadedMatches.clear();
        undownloadedSports.clear();
        unsavedMatches.clear();
        toParse = getAllTextingMatches();
        toParseIterator = toParse.listIterator();
        MFTMCThread[] mftmcThreads = new MFTMCThread[NUM_OF_MFTMCTHREAD];
        int i;
        for(i = 0; i < NUM_OF_MFTMCTHREAD; i++){
            mftmcThreads[i] = new MFTMCThread(getCloneOfIOManager());
            Thread t = new Thread(mftmcThreads[i]);
            t.start();
        }
        List<Match> matches = new LinkedList<>();
        i = 0;
        while(i < NUM_OF_MFTMCTHREAD){
            if(mftmcThreads[i].done){
                matches.addAll(mftmcThreads[i].matches);
                i++;
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {}
            }
        }
        toParseIterator = null;
        return matches;
    }

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
    public Collection<Tuple<Sport, Calendar>> getUndownloadedSports() {
        return Collections.unmodifiableCollection(undownloadedSports);
    }
    
    @Override
    public Collection<Match> getUnsavedMatches(){
        return Collections.unmodifiableCollection(unsavedMatches);
    }
    
    @SuppressWarnings("SleepWhileInLoop")
    private List<TextingMatch> getAllTextingMatches(){
        toDownload = prepareSportsAndDates();
        toDownloadIterator = toDownload.listIterator();
        TMCThread[] tmcThreads = new TMCThread[NUM_OF_TMCTHREAD];
        for(int i = 0; i < NUM_OF_TMCTHREAD; i++){
            tmcThreads[i] = new TMCThread();
            Thread t = new Thread(tmcThreads[i]);
            t.start();
        }
        List<TextingMatch> matches = new LinkedList<>();
        int i = 0;
        while(i < NUM_OF_TMCTHREAD){
            if(tmcThreads[i].done){
                matches.addAll(tmcThreads[i].matches);
                i++;
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {}
            }
        }
        toDownloadIterator = null;
        return matches;
    }
    
    private Tuple<Sport, Calendar> getNextTuple(){
        synchronized(LOCK_FOR_TO_DOWNLOAD){
            if(toDownloadIterator.hasNext()){
                return toDownloadIterator.next();
            } else {
                return null;
            }
        }
    }
    
    private TextingMatch getNextTextingMatch(){
        synchronized(LOCK_FOR_TO_PARSE){
            if(toParseIterator.hasNext()){
                return toParseIterator.next();
            } else {
                return null;
            }
        }
    }
    
    private List<Tuple<Sport, Calendar>> prepareSportsAndDates(){
        List<Tuple<Sport, Calendar>> out = new LinkedList<>();
        Calendar c;
        for(long i = startDate.getTimeInMillis(); 
                i <= endDate.getTimeInMillis();
                i += 86_400_000){
            c = new GregorianCalendar();
            c.setTimeInMillis(i);
            if(sport == Sport.All) {
                out.add(new Tuple(Sport.Soccer, c));
                out.add(new Tuple(Sport.Baseball, c));
                out.add(new Tuple(Sport.Basketball, c));
                out.add(new Tuple(Sport.Handball, c));
                out.add(new Tuple(Sport.Hockey, c));
                out.add(new Tuple(Sport.Volleyball, c));
            } else {
                out.add(new Tuple(sport, c));
            }
        }
        return out;
    }
    
    @Override
    public void setExploredSport(Sport sport) {
        this.sport = sport;
    }

    @Override
    public Sport getExploredSport() {
        return sport;
    }

    @Override
    public Calendar getActualProccessingDate() {
        return actualProcessingDate;
    }

    @Override
    public void setActualProccessingDate(Calendar date) {
        this.actualProcessingDate.setTimeInMillis(date.getTimeInMillis());
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

//    private static synchronized Connection getConnection(String url){
//        return Jsoup.connect(url).timeout(5_000);
//    }
    
    private class TMCThread implements Runnable{

        private List<TextingMatch> matches;
        private volatile boolean done = false;
        
        @Override
        public void run() {
            done = false;
            matches = new LinkedList<>();
            Tuple<Sport, Calendar> tuple = getNextTuple();
            TextingMatchCollector tmc;
            while(tuple != null){
                tmc = new TextingMatchCollector(tuple.first, tuple.second);
                matches.addAll(tmc.getMatchesList());
                tuple = getNextTuple();
            }
            done = true;
            System.err.println("Done TMC");
        }
    }
    
    private class MFTMCThread implements Runnable{

        private List<Match> matches;
        private volatile boolean done = false;
        private BetIOManager manager;

        public MFTMCThread(BetIOManager manager) {
            this.manager = manager;
        }

        @Override
        public void run() {
            done = false;
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
            done = true;
            System.err.println("Done MFTMC");
        }
        
    }
    
    private class TextingMatchCollector{
        private final Sport exploredSport;
        private final String exploredSportString;
        private final Calendar date;
        private final DocumentDownloader docDwnl = new DocumentDownloader();

        public TextingMatchCollector(Sport exploredSport, Calendar date) {
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
    //            Document doc;
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
        
        private String getURLForMatchesPageByDate(){
            return STRING_URL 
                    + RESULTS 
                    + exploredSportString 
                    + exploredDateURLString();
        }
        
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
        
        private String exploredDateTableAttributeString(){
            StringBuilder builder = new StringBuilder(27);
            builder.append(date.get(Calendar.DAY_OF_MONTH))
                    .append(",")
                    .append(date.get(Calendar.MONTH) + 1)
                    .append(",")
                    .append(date.get(Calendar.YEAR));
            return builder.toString();
        }
        
        private String getURLForMatchScore(String matchURL){
            return STRING_URL + matchURL;
        }
        
        private String getMatchID(String url){
            int startIdx = url.indexOf("matchid") + 8;
            return url.substring(startIdx, startIdx + 8);
        }
        
//        private Document getDocument(String url){
//            /* vyhnutie sa NullPointerException */
//            Document doc = new Document(url);
//            for(int i = 1; i <= CONNECTION_TRIES; i++){
//                try{
//                    doc = getConnection(url).get();
//                    return doc;
//                } catch (IOException ex){
//                    System.err.println("Time out [" + i + "] by connecting: " + url);
//                    System.err.println(ex);
//                }
//            }
//            return doc;
//        }
    }
    
//    private class DocumentDownloader {
//        
//        private Document getDocument(String url){
//            Document doc = null;
//            for(int i = 1; i <= CONNECTION_TRIES; i++){
//                try{
//                    doc = getConnection(url).get();
//                    return doc;
//                } catch (IOException ex){
//                    System.err.println("Time out [" + i + "] by connecting: " + url);
//                    System.err.println(ex);
//                }
//            }
//            return doc;
//        }
//    }
    
    private class MatchFromTextingMatchCollector{
        private final TextingMatch textingMatch;
        private final DocumentDownloader docDwnl = new DocumentDownloader();

        public MatchFromTextingMatchCollector(TextingMatch textingMatch) {
            this.textingMatch = textingMatch;
        }
        
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
    //        System.out.println(match);
            return outputMatch;
        }
        
        private String getURLForMatchDetails(String matchID, String betType){
            return STRING_URL 
                    + "/gres/ajax-matchodds.php/?t=d&e="
//                    + "ribex9H5&b=" + betType;
                    + matchID + "&b=" + betType;
        }
        
//        private Document getDocument(String url){
//            /* vyhnutie sa NullPointerException */
//            Document doc = new Document(url);
//            for(int i = 1; i <= CONNECTION_TRIES; i++){
//                try{
//                    doc = getConnection(url).get();
//                    return doc;
//                } catch (IOException ex){
//                    System.err.println("Time out [" + i + "] by connecting: " + url);
//                    System.err.println(ex);
//                }
//            }
//            return doc;
//        }
        
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
        
        private void process1x2Bet(Elements tableRows, Match match){
            String name;
            Elements bets;
            for(Element row : tableRows){
                name = row.select("th").first().text();
                bets = row.select("td");
                match.addBet(new Bet1x2(name,
                        parseDouble(bets.get(0).attr("data-odd")),
                        parseDouble(bets.get(1).attr("data-odd")), 
                        parseDouble(bets.get(2).attr("data-odd"))));
            }
        }

        private void processOverUnderBet(Elements tableRows, Match match){
            String name;
            Elements bets;
            for(Element row : tableRows){
                name = row.select("th").first().text();
                bets = row.select("td");
                if(bets.get(0).select(".doublepar").isEmpty()){
                    String[] s = bets.get(0).select(".doublepar-w").first().text().split("[ ]+");
                    match.addBet(new BetOverUnder(name, 
                        parseDouble(s[0]),
                        parseDouble(bets.get(1).attr("data-odd")),
                        parseDouble(bets.get(2).attr("data-odd")),
                            s[1]));
                } else {
                    match.addBet(new BetOverUnder(name, 
                            parseDouble(bets.get(0).select(".doublepar").first().text()),
                            parseDouble(bets.get(1).attr("data-odd")),
                            parseDouble(bets.get(2).attr("data-odd")),
                            ""));
                }
            }
        }

        private void processAsianHandicapBet(Elements tableRows, Match match){
            String name;
            Elements bets;
            String[] handicaps;
//            System.out.println(match.getProperties().getLeague() + " - in");
            for(Element row : tableRows){
                name = row.select("th").first().text();
                bets = row.select("td");
                if(!bets.get(0).select(".doublepar").isEmpty()){
                    handicaps = bets.get(0).select(".doublepar").first().text().split("[ ,]+");
                    if(handicaps.length == 1){
                        match.addBet(new BetAsianHandicap(name,
                                parseDouble(bets.get(1).attr("data-odd")),
                                parseDouble(bets.get(2).attr("data-odd")),
                                parseDouble(handicaps[0]),
                                ""));
                    } else {
                        match.addBet(new BetAsianHandicap(name,
                                parseDouble(bets.get(1).attr("data-odd")),
                                parseDouble(bets.get(2).attr("data-odd")),
                                ((parseDouble(handicaps[0]) + parseDouble(handicaps[1])) / 2.0),
                                ""));
                    }
                } else {
                    handicaps = bets.get(0).select(".doublepar-w").first().text().split("[ ,]+");
                    if(handicaps.length == 2){
                        match.addBet(new BetAsianHandicap(name,
                                parseDouble(bets.get(1).attr("data-odd")),
                                parseDouble(bets.get(2).attr("data-odd")),
                                parseDouble(handicaps[0]),
                                handicaps[1]));
                    } else {
                        match.addBet(new BetAsianHandicap(name,
                                parseDouble(bets.get(1).attr("data-odd")),
                                parseDouble(bets.get(2).attr("data-odd")),
                                ((parseDouble(handicaps[0]) + parseDouble(handicaps[1])) / 2.0),
                                handicaps[2]));
                    }
                }
            }
//            System.out.println(match.getProperties().getLeague() + " - out");
        }

        private void processDrawNoBetBet(Elements tableRows, Match match){
            String name;
            Elements bets;
            for(Element row : tableRows){
                name = row.select("th").first().text();
                bets = row.select("td");
                match.addBet(new BetDrawNoBet(name,
                        parseDouble(bets.get(0).attr("data-odd")),
                        parseDouble(bets.get(1).attr("data-odd"))));
            }
        }

        private void processDoubleChanceBet(Elements tableRows, Match match){
            String name;
            Elements bets;
            for(Element row : tableRows){
                name = row.select("th").first().text();
                bets = row.select("td");
                match.addBet(new BetDoubleChance(name,
                        parseDouble(bets.get(0).attr("data-odd")),
                        parseDouble(bets.get(1).attr("data-odd")), 
                        parseDouble(bets.get(2).attr("data-odd"))));
            }
        }

        private void processBothTeamsToScoreBet(Elements tableRows, Match match){
            String name;
            Elements bets;
            for(Element row : tableRows){
                name = row.select("th").first().text();
                bets = row.select("td");
                match.addBet(new BetBothTeamsToScore(name,
                        parseDouble(bets.get(0).attr("data-odd")),
                        parseDouble(bets.get(1).attr("data-odd"))));
            }
        }
        
        private double parseDouble(String parse){
            try{
                return Double.parseDouble(parse);
            } catch (NumberFormatException ex){
//                System.err.println("Double: " + ex);
                return 0.0;
            }
        }
    }
    
    private class TextingMatch{
        private String league;
        private String match;
        private String matchID;
        private String score;
        private Sport sport;
        private Calendar date = new GregorianCalendar();

        @Override
        public String toString() {
            return match + " " + score;
        }
        
        private Match getMatch(){
            Match m = new Match(sport);
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
                m.setScore(parseInt(removeEmptyCharacters(split[0])),
                        parseInt(removeEmptyCharacters(split[1])));
                String[] scoreSplit;
                for(int i = 2; i < split.length; i++){
                    scoreSplit = split[i].split(":");
                    m.addPartialScore(parseInt(removeEmptyCharacters(scoreSplit[0])),
                        parseInt(removeEmptyCharacters(scoreSplit[1])));
                }
            }
            return m;
        }
        
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
        private int parseInt(String parse){
            try{
                return Integer.parseInt(parse);
            } catch (NumberFormatException ex){
//                System.err.println("Integer: " + ex);
                return 0;
            }
        }
    }
}