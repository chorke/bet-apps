/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.httpparsing;

import chorke.proprietary.bet.apps.StaticConstants.Sport;
import chorke.proprietary.bet.apps.core.bets.Bet1x2;
import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.core.match.MatchProperties;
import chorke.proprietary.bet.apps.core.Tuple;
import chorke.proprietary.bet.apps.core.bets.BetAsianHandicap;
import chorke.proprietary.bet.apps.core.bets.BetBothTeamsToScore;
import chorke.proprietary.bet.apps.core.bets.BetDoubleChance;
import chorke.proprietary.bet.apps.core.bets.BetDrawNoBet;
import chorke.proprietary.bet.apps.core.bets.BetOverUnder;
import chorke.proprietary.bet.apps.io.BetIOException;
import chorke.proprietary.bet.apps.io.BetIOManager;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Chorke
 */
public class BetexplorerComParser implements HTMLBetParser{

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
    
    private final String[] betsType = {BET_1X2, BET_OVER_UNDER,
                        BET_ASIAN_HANDICAP, BET_DRAW_NO_BET,
                        BET_DOUBLE_CHANCE, BET_BOTH_TEAMS_TO_SCORE};
    private final DocumentDownloader docDwnl = new DocumentDownloader();
    
    private String actualExploredSportString = SOCCER;
    private Sport actualExploredSport = Sport.Soccer;
    private Sport sport = Sport.All;
    
    private Document document;
    private Calendar proccessingDate;
    private Calendar startDate;
    private Calendar endDate;
    private Collection<String> undownloadedMatches;
    private Collection<Tuple<Sport, Calendar>> undownloadedSports;
    private Collection<Match> unsavedMatches;
    
    private BetIOManager manager;

    public BetexplorerComParser(BetIOManager manager) {
        proccessingDate = new GregorianCalendar();
        proccessingDate.set(Calendar.SECOND, 0);
        proccessingDate.set(Calendar.MILLISECOND, 0);
        startDate = new GregorianCalendar();
        endDate = new GregorianCalendar();
        startDate.setTimeInMillis(proccessingDate.getTimeInMillis());
        endDate.setTimeInMillis(proccessingDate.getTimeInMillis());
        undownloadedMatches = new LinkedList<>();
        undownloadedSports = new LinkedList<>();
        unsavedMatches = new LinkedList<>();
        this.manager = manager;
    }

    @Override
    public void setEndDate(Calendar end) {
        endDate.setTimeInMillis(end.getTimeInMillis());
    }

    @Override
    public void setStartDate(Calendar start) {
        startDate.setTimeInMillis(start.getTimeInMillis());
    }
    
    @Override
    public Collection<Match> getMatchesOfDay(Calendar cal) {
        setActualProccessingDate(cal);
        return getMatches();
    }

    public void setActualExploredSport(Sport actualExploredSport) {
        this.actualExploredSport = actualExploredSport;
        switch (actualExploredSport){
            case Baseball:
                actualExploredSportString = BASEBALL;
                break;
            case Basketball:
                actualExploredSportString = BASKETBALL;
                break;
            case Handball:
                actualExploredSportString = HANDBALL;
                break;
            case Hockey:
                actualExploredSportString = HOCKEY;
                break;
            case Soccer:
                actualExploredSportString = SOCCER;
                break;
            case Volleyball:
                actualExploredSportString = VOLLEYBALL;
                break;
        }
    }

    @Override
    public Collection<Match> nextDay() {
        proccessingDate.add(Calendar.DAY_OF_MONTH, 1);
        return getMatches();
    }

    @Override
    public Collection<Match> previousDay() {
        proccessingDate.add(Calendar.DAY_OF_MONTH, -1);
        return getMatches();
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
    public Collection<Match> getUnsavedMatches() {
        return Collections.unmodifiableCollection(unsavedMatches);
    }
    
    @Override
    public Collection<Match> getMatches() {
        undownloadedMatches.clear();
        undownloadedSports.clear();
        unsavedMatches.clear();
        Collection<Match> matches = new LinkedList<>();
        if(sport == Sport.All){
            setActualExploredSport(Sport.Baseball);
            matches.addAll(innerGetMatches());
            setActualExploredSport(Sport.Basketball);
            matches.addAll(innerGetMatches());
            setActualExploredSport(Sport.Handball);
            matches.addAll(innerGetMatches());
            setActualExploredSport(Sport.Hockey);
            matches.addAll(innerGetMatches());
            setActualExploredSport(Sport.Soccer);
            matches.addAll(innerGetMatches());
            setActualExploredSport(Sport.Volleyball);
            matches.addAll(innerGetMatches());
        } else {
            setActualExploredSport(sport);
            matches.addAll(innerGetMatches());
        }
        return matches;
    }
    
    private Collection<Match> innerGetMatches(){
        List<TextingMatch> list = getMatchesList();
        Collection<Match> matches = new LinkedList<>();
        Match match;
        for(TextingMatch m : list){
//            System.out.println("before parsing");
            match = parseMatchDetails(m);
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
//            break;
        }
        return matches;
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
        return proccessingDate;
    }

    @Override
    public void setActualProccessingDate(Calendar date) {
        this.proccessingDate.setTimeInMillis(date.getTimeInMillis());
    }
    
    private List<TextingMatch> getMatchesList(){
        List<TextingMatch> out = new LinkedList<>();
        document = docDwnl.getDocument(getURLForMatchesPageByDate());
        if(document == null){
            undownloadedSports.add(new Tuple(actualExploredSport, proccessingDate));
            return out;
        }
        String href;
        if(document.select("div.nr-nodata-info").isEmpty()){
            Elements tableElements = document.getElementsByTag("table");
            Document doc;
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
                            doc = docDwnl.getDocument(getURLForMatchScore(href));
                            if(doc == null){
                                undownloadedMatches.add(getURLForMatchScore(href));
                            } else {
                                tm.score = doc.select("table.tscore tr th.scorecell").text() 
                                            + doc.select("table.tscore tr td.center").text();
                                tm.matchID = getMatchID(href);
                                out.add(tm);
                            }
                        }
                    }
                }
            }
        }
        return out;
    }
    
    private String exploredDateTableAttributeString(){
        StringBuilder builder = new StringBuilder(27);
        builder.append(proccessingDate.get(Calendar.DAY_OF_MONTH))
                .append(",")
                .append(proccessingDate.get(Calendar.MONTH) + 1)
                .append(",")
                .append(proccessingDate.get(Calendar.YEAR));
        return builder.toString();
    }
    
    private String exploredDateURLString(){
        StringBuilder builder = new StringBuilder(26);
        builder.append("?year=")
                .append(proccessingDate.get(Calendar.YEAR))
                .append("&month=")
                .append(proccessingDate.get(Calendar.MONTH) + 1)
                .append("&day=")
                .append(proccessingDate.get(Calendar.DAY_OF_MONTH));
        return builder.toString();
    }
    
    private String getURLForMatchesPageByDate(){
        return STRING_URL 
                + RESULTS 
                + actualExploredSportString 
                + exploredDateURLString();
    }
    
    private String getURLForMatchScore(String matchURL){
        return STRING_URL + matchURL;
    }
    
    private String getURLForMatchDetails(String matchID, String betType){
        return STRING_URL 
                + "/gres/ajax-matchodds.php/?t=d&e="
                + matchID + "&b=" + betType;
    }

    private String getMatchID(String url){
        int startIdx = url.indexOf("matchid") + 8;
        return url.substring(startIdx, startIdx + 8);
    }
    
    private Match parseMatchDetails(TextingMatch textingMatch){
        Document doc;
        Elements tableRows;
        System.out.println(textingMatch);
        Match match = textingMatch.getMatch();
        for(String bt : betsType){
            doc = docDwnl.getDocument(getURLForMatchDetails(textingMatch.matchID, bt));
            if(doc == null){
                undownloadedMatches.add(getURLForMatchDetails(textingMatch.matchID, bt));
            } else {
                if(doc.select("div#no-odds-info").isEmpty()){
                    tableRows = doc.select("tbody tr");
                    processBetTable(tableRows, match, bt);
                }
            }
        }
//        System.out.println(match);
        return match;
    }
    
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
    
    private double parseDouble(String parse){
        try{
            return Double.parseDouble(parse);
        } catch (NumberFormatException ex){
            System.err.println("Double: " + ex);
            return 0.0;
        }
    }
    
    private int parseInt(String parse){
        try{
            return Integer.parseInt(parse);
        } catch (NumberFormatException ex){
            System.err.println("Integer: " + ex);
            return 0;
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
    
    private class TextingMatch{
        private String league;
        private String match;
        private String matchID;
        private String score;

        @Override
        public String toString() {
//            return league + " [" + matchID + "] " + match;
            return match + " " + score;
        }
        
        private Match getMatch(){
//            System.out.println(this);
            Match m = new Match(actualExploredSport);
            MatchProperties prop = new MatchProperties();
            /* league */
            String[] split = league.split("[:]+");
            prop.setCountry(split[0].trim());
            prop.setLeague(removeEmptyCharacters(split[1]).trim());
            /* match info */
            split = match.split("[ ]+");
            proccessingDate.set(Calendar.HOUR_OF_DAY, 
                    parseInt(split[0].substring(0, 2)));
            proccessingDate.set(Calendar.MINUTE, 
                    parseInt(split[0].substring(3, 5)));
            prop.setDate(proccessingDate);
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
    }
}