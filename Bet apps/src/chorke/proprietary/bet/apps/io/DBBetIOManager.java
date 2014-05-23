
package chorke.proprietary.bet.apps.io;

import chorke.proprietary.bet.apps.core.bets.Bet;
import chorke.proprietary.bet.apps.core.bets.Bet1x2;
import chorke.proprietary.bet.apps.core.bets.BetAsianHandicap;
import chorke.proprietary.bet.apps.core.bets.BetBothTeamsToScore;
import chorke.proprietary.bet.apps.core.bets.BetDoubleChance;
import chorke.proprietary.bet.apps.core.bets.BetDrawNoBet;
import chorke.proprietary.bet.apps.core.bets.BetOverUnder;
import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.core.match.MatchProperties;
import chorke.proprietary.bet.apps.core.match.score.PartialScore;
import chorke.proprietary.bet.apps.core.match.score.Score;
import chorke.proprietary.bet.apps.core.match.sports.Sport;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;

/**
 *
 * Implementácia {@link CloneableBetIOManager} pre ukladanie stávok. Iniciálne je implementácia
 * robená pre PostgreSQL databázu. Obsahuje však iba jednoduché SELECT, INSERT, DELETE
 * SQL príkazy, ktoré by mali podporovať všetky databázi.
 * 
 * @author Chorke
 */
public class DBBetIOManager implements CloneableBetIOManager{

    private DataSource dataSource;
    
    private volatile boolean isCanceled;
    
    private Set<PreparedStatement> actualPreparedStatements = new HashSet<>();
    
    public DBBetIOManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public void saveMatch(Match match) throws BetIOException {
        isCanceled = false;
        if(dataSource == null){
            throw new BetIOException("No data source");
        }
        if(match == null){
            throw new IllegalArgumentException("Match can no be null.");
        }
        if(match.getId() != null){
            throw new IllegalArgumentException("Matchs ID already set");
        }
        if(match.getProperties() == null){
            throw new IllegalArgumentException("Match properties can no be null.");
        }
        if(match.getProperties().getCountry() == null
                || match.getProperties().getCountry().isEmpty()
                || match.getProperties().getLeague() == null
                || match.getProperties().getLeague().isEmpty()){
            throw new IllegalArgumentException("Invalid match properties.");
        }
        if(match.getSport() == null){
            throw new IllegalArgumentException("Sport can not be null");
        }
        try(Connection con = dataSource.getConnection()){
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO matches "
                    + "(sport,country,league,matchdate) VALUES(?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, match.getSport().toString());
            ps.setString(2, match.getProperties().getCountry());
            ps.setString(3, match.getProperties().getLeague());
            ps.setTimestamp(4, new Timestamp(match.getProperties().getDate().getTimeInMillis()));
            
            addToActualPS(ps);
            checkCancelation();
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            match.setId(rs.getLong(1));
      
            removeFromActualPS(ps);
            ps = con.prepareStatement("INSERT INTO scores (matchid,team1,team2,part) VALUES(?,?,?,?)");
            addToActualPS(ps);
            ps.setLong(1, match.getId());
            ps.setInt(2, match.getScore().getScoreFirstParty());
            ps.setInt(3, match.getScore().getScoreSecondParty());
            ps.setInt(4, -1);
            checkCancelation();
            ps.executeUpdate();
            List<PartialScore> listPs = match.getScore().getPartialScore();
            int i = 0;
            while(i < listPs.size()){
                checkCancelation();
                removeFromActualPS(ps);
                ps = con.prepareStatement(
                        "INSERT INTO scores (matchid,team1,team2,part) VALUES(?,?,?,?)");
                addToActualPS(ps);
                ps.setLong(1, match.getId());
                ps.setInt(2, listPs.get(i).firstParty);
                ps.setInt(3, listPs.get(i).secondParty);
                i++;
                ps.setInt(4, i);
                ps.executeUpdate();
            }
            Map<String, Collection<Bet>> bets = match.getBets();
            for(String s : bets.keySet()){
                for(Bet b : bets.get(s)){
                    checkCancelation();
                    removeFromActualPS(ps);
                    if(b instanceof Bet1x2){
                        ps = prepareStatementBet1x2(
                                con, (Bet1x2)b, match.getId());
                    } else if(b instanceof BetAsianHandicap){
                        ps = prepareStatementBetAsianHandicap(
                                con, (BetAsianHandicap)b, match.getId());
                    } else if(b instanceof BetBothTeamsToScore){
                        ps = prepareStatementBetBothTeamsToScore(
                                con, (BetBothTeamsToScore)b, match.getId());
                    } else if(b instanceof BetDoubleChance){
                        ps = prepareStatementBetDoubleChance(
                                con, (BetDoubleChance)b, match.getId());
                    } else if(b instanceof BetDrawNoBet){
                        ps = prepareStatementBetDrawNoBet(
                                con, (BetDrawNoBet)b, match.getId());
                    } else if(b instanceof BetOverUnder){
                        ps = prepareStatementBetOverUnder(
                                con, (BetOverUnder)b, match.getId());
                    } else {
                        throw new BetIOException("Unsuported Bet class");
                    }
                    addToActualPS(ps);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException ex){
            if(match.getId() != null){
                boolean cancel = isCanceled;
                deleteMatch(match);
                isCanceled = cancel;
            }
            throw new BetIOException("Error by saving match. ", ex);
        }
    }
    
    private void removeFromActualPS(PreparedStatement... ps){
        for(PreparedStatement p : ps){
            actualPreparedStatements.remove(p);
        }
    }
    
    private void addToActualPS(PreparedStatement... ps){
        actualPreparedStatements.addAll(Arrays.asList(ps));
    }
    
    /**
     * Skontorluje, či má byť aktuálny beh prerušený. Ak áno, vyhodí výnimku
     * SQLException.
     * 
     * @throws SQLException 
     */
    private void checkCancelation() throws SQLException {
        if(isCanceled){
            throw new SQLException("Query was canceled.");
        }
    }
    
    /**
     * Vytvorí {@link PreparedStatement} pre {@link Bet1x2} stávku.
     * @param con
     * @param bet
     * @param matchID
     * @return
     * @throws SQLException 
     */
    private PreparedStatement prepareStatementBet1x2(Connection con, Bet1x2 bet, Long matchID)
            throws SQLException{
        PreparedStatement ps = con.prepareStatement("INSERT INTO bet1x2 "
                + "(matchid,betcompany,bet1,betx,bet2) "
                + "VALUES (?,?,?,?,?)");
        ps.setLong(1, matchID);
        ps.setString(2, bet.betCompany);
        ps.setString(3, bet.bet1.toPlainString());
        ps.setString(4, bet.betX.toPlainString());
        ps.setString(5, bet.bet2.toPlainString());
        return ps;
    }
    
    /**
     * Vytvorí {@link PreparedStatement} pre {@link BetAsianHandicap} stávku.
     * @param con
     * @param bet
     * @param matchID
     * @return
     * @throws SQLException 
     */
    private PreparedStatement prepareStatementBetAsianHandicap(
            Connection con, BetAsianHandicap bet, Long matchID)
            throws SQLException{
       PreparedStatement ps = con.prepareStatement("INSERT INTO betah "
                + "(matchid,betcompany,bet1,bet2,handicap,description) "
                + "VALUES (?,?,?,?,?,?)");
        ps.setLong(1, matchID);
        ps.setString(2, bet.betCompany);
        ps.setString(3, bet.bet1.toPlainString());
        ps.setString(4, bet.bet2.toPlainString());
        ps.setString(5, bet.handicap.toPlainString());
        ps.setString(6, bet.description);
        return ps; 
    }
    
    /**
     * Vytvorí {@link PreparedStatement} pre {@link BetBothTeamsToScore} stávku.
     * @param con
     * @param bet
     * @param matchID
     * @return
     * @throws SQLException 
     */
    private PreparedStatement prepareStatementBetBothTeamsToScore(
            Connection con, BetBothTeamsToScore bet, Long matchID)
            throws SQLException{
        PreparedStatement ps = con.prepareStatement("INSERT INTO betbtts "
                + "(matchid,betcompany,yesbet,nobet) "
                + "VALUES (?,?,?,?)");
        ps.setLong(1, matchID);
        ps.setString(2, bet.betCompany);
        ps.setString(3, bet.yes.toPlainString());
        ps.setString(4, bet.no.toPlainString());
        return ps;
    }
    
    /**
     * Vytvorí {@link PreparedStatement} pre {@link BetDoubleChance} stávku.
     * @param con
     * @param bet
     * @param matchID
     * @return
     * @throws SQLException 
     */
    private PreparedStatement prepareStatementBetDoubleChance(
            Connection con, BetDoubleChance bet, Long matchID)
            throws SQLException{
        PreparedStatement ps = con.prepareStatement("INSERT INTO betdc "
                + "(matchid,betcompany,bet1x,bet2x,bet12) "
                + "VALUES (?,?,?,?,?)");
        ps.setLong(1, matchID);
        ps.setString(2, bet.betCompany);
        ps.setString(3, bet.bet1X.toPlainString());
        ps.setString(4, bet.betX2.toPlainString());
        ps.setString(5, bet.bet12.toPlainString());
        return ps;
    }
    
    /**
     * Vytvorí {@link PreparedStatement} pre {@link BetDrawNoBet} stávku.
     * @param con
     * @param bet
     * @param matchID
     * @return
     * @throws SQLException 
     */
    private PreparedStatement prepareStatementBetDrawNoBet(
            Connection con, BetDrawNoBet bet, Long matchID)
            throws SQLException{
        PreparedStatement ps = con.prepareStatement("INSERT INTO betdnb "
                + "(matchid,betcompany,bet1,bet2) "
                + "VALUES (?,?,?,?)");
        ps.setLong(1, matchID);
        ps.setString(2, bet.betCompany);
        ps.setString(3, bet.bet1.toPlainString());
        ps.setString(4, bet.bet2.toPlainString());
        return ps;
    }
    
    /**
     * Vytvorí {@link PreparedStatement} pre {@link BetOverUnder} stávku.
     * @param con
     * @param bet
     * @param matchID
     * @return
     * @throws SQLException 
     */
    private PreparedStatement prepareStatementBetOverUnder(
            Connection con, BetOverUnder bet, Long matchID)
            throws SQLException{
        PreparedStatement ps = con.prepareStatement("INSERT INTO betou "
                + "(matchid,betcompany,total,overbet,underbet,description) "
                + "VALUES (?,?,?,?,?,?)");
        ps.setLong(1, matchID);
        ps.setString(2, bet.betCompany);
        ps.setString(3, bet.total.toPlainString());
        ps.setString(4, bet.over.toPlainString());
        ps.setString(5, bet.under.toPlainString());
        ps.setString(6, bet.description);
        return ps;
    }
    
    @Override
    public Collection<Match> loadAllMatches() throws BetIOException {
        isCanceled = false;
        if(dataSource == null){
            throw new BetIOException("No data source");
        }
        LoadProperties prop = new LoadProperties();
        try(Connection con = dataSource.getConnection();
                PreparedStatement matchesPS = prop.prepareMatchStatement(con);
                PreparedStatement scoresPS = prop.prepareScoresStatement(con);
                PreparedStatement bet1x2PS = prop.prepareBetStatement(con, "bet1x2");
                PreparedStatement betahPS = prop.prepareBetStatement(con, "betah");
                PreparedStatement betbttsPS = prop.prepareBetStatement(con, "betbtts");
                PreparedStatement betdcPS = prop.prepareBetStatement(con, "betdc");
                PreparedStatement betdnbPS = prop.prepareBetStatement(con, "betdnb");
                PreparedStatement betouPS = prop.prepareBetStatement(con, "betou");){
            checkCancelation();
            return getMatchesFromPreparedStatements(matchesPS,
                    scoresPS, bet1x2PS, betahPS, betbttsPS, betdcPS, betdnbPS, betouPS);
        } catch (SQLException ex){
            throw new BetIOException("Error while finding all matches.", ex);
        }
    }
    
    /**
     * Obecná metóda, ktorá vráti zápasy pre vhodne pripravené {@link PreparedStatement}.
     * Load metódam preto stačí vytvoriť príslušné príkazy.
     * @param matchesPS
     * @param scoresPS
     * @param bet1x2PS
     * @param betahPS
     * @param betbttsPS
     * @param betdcPS
     * @param betdnbPS
     * @param betouPS
     * @return
     * @throws SQLException 
     */
    private Collection<Match> getMatchesFromPreparedStatements(
            PreparedStatement matchesPS,
            PreparedStatement scoresPS,
            PreparedStatement bet1x2PS,
            PreparedStatement betahPS,
            PreparedStatement betbttsPS,
            PreparedStatement betdcPS,
            PreparedStatement betdnbPS,
            PreparedStatement betouPS) throws SQLException {
        addToActualPS(matchesPS, scoresPS, bet1x2PS, betahPS, betbttsPS, betdcPS, betdnbPS, betouPS);
        System.out.println("getting matches");
        Map<Long, Match> matches = getMatches(matchesPS.executeQuery());
        matchesPS.close();
        checkCancelation();
        System.out.println("getting scores");
        getScores(scoresPS.executeQuery(), matches);
        scoresPS.close();
        checkCancelation();
        if(bet1x2PS != null){
            System.out.println("getting 1x2");
            getBet1x2(bet1x2PS.executeQuery(), matches);
            bet1x2PS.close();
            checkCancelation();
        }
        if(betahPS != null){
            System.out.println("getting ah");
            getBetAsianHandicap(betahPS.executeQuery(), matches);
            betahPS.close();
            checkCancelation();
        }
        if(betbttsPS != null){
            System.out.println("getting btts");
            getBetBothTeamsToScore(betbttsPS.executeQuery(), matches);
            betbttsPS.close();
            checkCancelation();
        }
        if(betdcPS != null){
            System.out.println("getting dc");
            getBetDoubleChance(betdcPS.executeQuery(), matches);
            betdcPS.close();
            checkCancelation();
        }
        if(betdnbPS != null){
            System.out.println("getting dnb");
            getBetDrawNoBet(betdnbPS.executeQuery(), matches);
            betdnbPS.close();
            checkCancelation();
        }
        if(betouPS != null){
            System.out.println("getting ou");
            getBetOverUnder(betouPS.executeQuery(), matches);
            betouPS.close();
            checkCancelation();
        }
        removeFromActualPS(matchesPS, scoresPS, bet1x2PS, betahPS, betbttsPS, betdcPS, betdnbPS, betouPS);
        Collection<Match> outputMatches = new LinkedList<>();
        Iterator<Long> iter = matches.keySet().iterator();
        Match match;
        while(iter.hasNext()){
            checkCancelation();
            match = matches.get(iter.next());
            if(!match.getBets().isEmpty()){
                outputMatches.add(match);
            }
            iter.remove();
        }
        return outputMatches;
    }
   
    /**
     * Z {@link ResultSet} {@code bet} získa všetky stávky {@link Bet1x2}
     * a uloží do {@code storage}. {@code bet} nie je nijako kontrolovaná
     * a mala by obsahovať iba korektná stávky {@link Bet1x2}.
     * @param bet
     * @param matches
     * @throws SQLException 
     */
    private void getBet1x2(ResultSet bet, Map<Long, Match> matches) throws SQLException{
        Match match;
        while(bet.next()){
            match = matches.get(bet.getLong("matchid"));
            if(match == null){ continue; }
            match.addBet(
                new Bet1x2(bet.getString("betcompany"),
                    new BigDecimal(bet.getString("bet1")), 
                    new BigDecimal(bet.getString("betx")),
                    new BigDecimal(bet.getString("bet2"))));
        }
        bet.close();
    }
    
    /**
     * Z {@link ResultSet} {@code bet} získa všetky stávky {@link BetAsianHandicap}
     * a uloží do {@code storage}. {@code bet} nie je nijako kontrolovaná
     * a mala by obsahovať iba korektné stávky {@link BetAsianHandicap}.
     * @param bet
     * @param storage
     * @throws SQLException 
     */
    private void getBetAsianHandicap(ResultSet bet, Map<Long, Match> matches) throws SQLException{
        Match match;
        while(bet.next()){
            match = matches.get(bet.getLong("matchid"));
            if(match == null){ continue; }
            match.addBet(
                    new BetAsianHandicap(bet.getString("betcompany"),
                        new BigDecimal(bet.getString("bet1")), 
                        new BigDecimal(bet.getString("bet2")),
                        new BigDecimal(bet.getString("handicap")),
                        bet.getString("description")));
        }
        bet.close();
    }
    
    /**
     * Z {@link ResultSet} {@code bet} získa všetky stávky {@link BetBothTeamsToScore}
     * a uloží do {@code storage}. {@code bet} nie je nijako kontrolovaná
     * a mala by obsahovať iba korektná stávky {@link BetBothTeamsToScore}.
     * @param bet
     * @param storage
     * @throws SQLException 
     */
    private void getBetBothTeamsToScore(ResultSet bet, Map<Long, Match> matches) throws SQLException{
        Match match;
        while(bet.next()){
            match = matches.get(bet.getLong("matchid"));
            if(match == null){ continue; }
            match.addBet(
                    new BetBothTeamsToScore(bet.getString("betcompany"),
                        new BigDecimal(bet.getString("yesbet")), 
                        new BigDecimal(bet.getString("nobet"))));
        }
        bet.close();
    }
    
    /**
     * Z {@link ResultSet} {@code bet} získa všetky stávky {@link BetDoubleChance}
     * a uloží do {@code storage}. {@code bet} nie je nijako kontrolovaná
     * a mala by obsahovať iba korektná stávky {@link BetDoubleChance}.
     * @param bet
     * @param storage
     * @throws SQLException 
     */
    private void getBetDoubleChance(ResultSet bet, Map<Long, Match> matches) throws SQLException{
        Match match;
        while(bet.next()){
            match = matches.get(bet.getLong("matchid"));
            if(match == null){ continue; }
            match.addBet(
                    new BetDoubleChance(bet.getString("betcompany"),
                        new BigDecimal(bet.getString("bet1x")), 
                        new BigDecimal(bet.getString("bet12")),
                        new BigDecimal(bet.getString("bet2x"))));
        }
        bet.close();
    }
    
    /**
     * Z {@link ResultSet} {@code bet} získa všetky stávky {@link BetDrawNoBet}
     * a uloží do {@code storage}. {@code bet} nie je nijako kontrolovaná
     * a mala by obsahovať iba korektná stávky {@link BetDrawNoBet}.
     * @param bet
     * @param storage
     * @throws SQLException 
     */
    private void getBetDrawNoBet(ResultSet bet, Map<Long, Match> matches) throws SQLException{
        Match match;
        while(bet.next()){
            match = matches.get(bet.getLong("matchid"));
            if(match == null){ continue; }
            match.addBet(
                    new BetDrawNoBet(bet.getString("betcompany"),
                        new BigDecimal(bet.getString("bet1")), 
                        new BigDecimal(bet.getString("bet2"))));
        }
        bet.close();
    }
    
    /**
     * Z {@link ResultSet} {@code bet} získa všetky stávky {@link BetOverUnder}
     * a uloží do {@code storage}. {@code bet} nie je nijako kontrolovaná
     * a mala by obsahovať iba korektná stávky {@link BetOverUnder}.
     * @param bet
     * @param storage
     * @throws SQLException 
     */
    private void getBetOverUnder(ResultSet bet, Map<Long, Match> matches) throws SQLException{
        Match match;
        while(bet.next()){
            match = matches.get(bet.getLong("matchid"));
            if(match == null){ continue; }
            match.addBet(
                    new BetOverUnder(bet.getString("betcompany"),
                        new BigDecimal(bet.getString("total")), 
                        new BigDecimal(bet.getString("overbet")),
                        new BigDecimal(bet.getString("underbet")),
                        bet.getString("description")));
        }
        bet.close();
    }
    
    /**
     * Vráti skóre z {@link ResultSet} {@code scores}. {@code scores} nie je 
     * nijako kontrolovaná a mala by obsahovať iba korektné {@link Score} záznamy.
     * @param scores
     * @return
     * @throws SQLException 
     */
    private void getScores(ResultSet scores, Map<Long, Match> matches)
            throws SQLException{
        int maxPart = 1;
        int exploredPart = 1;
        int acualPart;
        Match match;
        while(exploredPart <= maxPart){
            while(scores.next()){
                acualPart = scores.getInt("part");
                if(acualPart == exploredPart){
                    match = matches.get(scores.getLong("matchid"));
                    if(match == null){
                        continue; 
                    }
                    match.addPartialScore(
                            new PartialScore(scores.getInt("team1"), scores.getInt("team2")));
                }
                if(acualPart > maxPart){ 
                    maxPart = acualPart; 
                }
            }
            exploredPart++;
            scores.beforeFirst();
        }
    }
    
    /**
     * Vráti zápasy z {@link ResultSet} {@code matches}. {@code matches} nie je 
     * nijako kontrolovaná a mala by obsahovať iba korektné {@link Match} záznamy.
     * @param scores
     * @return
     * @throws SQLException 
     */
    private Map<Long, Match> getMatches(ResultSet matches) throws SQLException{
        Map<Long, Match> out = new HashMap<>();
        Match match;
        MatchProperties prop;
        Calendar cal;
        while(matches.next()){
            match = new Match(Sport.getSport(matches.getString("sport")));
            match.setId(matches.getLong("id"));
            prop = new MatchProperties();
            prop.setCountry(matches.getString("country"));
            prop.setLeague(matches.getString("league"));
            cal = new GregorianCalendar();
            cal.setTimeInMillis(matches.getTimestamp("matchdate").getTime());
            prop.setDate(cal);
            match.setProperties(prop);
            out.put(match.getId(), match);
        }
        matches.close();
        return out;
    }
    
    
    @Override
    public Collection<Match> loadMatches(LoadProperties properties) throws BetIOException {
        isCanceled = false;
        if(dataSource == null){
            throw new BetIOException("No data source");
        }
        if(properties == null){
            throw new IllegalArgumentException("Load properties can not be null");
        }
        if(properties.isEmpty()){
            return loadAllMatches();
        }
        try(Connection con = dataSource.getConnection()){
            PreparedStatement matchesPS = properties.prepareMatchStatement(con);
            PreparedStatement scoresPS = properties.prepareScoresStatement(con);
            PreparedStatement bet1x2PS = properties.prepareBetStatement(con, "bet1x2");
            PreparedStatement betahPS = properties.prepareBetStatement(con, "betah");
            PreparedStatement betbttsPS = properties.prepareBetStatement(con, "betbtts");
            PreparedStatement betdcPS = properties.prepareBetStatement(con, "betdc");
            PreparedStatement betdnbPS = properties.prepareBetStatement(con, "betdnb");
            PreparedStatement betouPS = properties.prepareBetStatement(con, "betou");
            
            bet1x2PS = properties.checkClass(Bet1x2.class, bet1x2PS);
            betahPS = properties.checkClass(BetAsianHandicap.class, betahPS);
            betbttsPS = properties.checkClass(BetBothTeamsToScore.class, betbttsPS);
            betdcPS = properties.checkClass(BetDoubleChance.class, betdcPS);
            betdnbPS = properties.checkClass(BetDrawNoBet.class, betdnbPS);
            betouPS = properties.checkClass(BetOverUnder.class, betouPS);
            
            checkCancelation();
            return getMatchesFromPreparedStatements(matchesPS, scoresPS, 
                    bet1x2PS, betahPS, betbttsPS, betdcPS, betdnbPS, betouPS);
        } catch (SQLException ex){
            throw new BetIOException("Error while finding all matches.", ex);
        }
    }

    
    
    @Override
    public void deleteMatch(Match match) throws BetIOException {
        isCanceled = false;
        if(dataSource == null){
            throw new BetIOException("No data source");
        }
        if(match == null){
            throw new IllegalArgumentException("Match can no be null.");
        }
        if(match.getId() == null){
            throw new IllegalArgumentException("Matchs ID is null");
        }
        try(Connection con = dataSource.getConnection();
                PreparedStatement matchesPS = 
                        con.prepareStatement("DELETE FROM matches WHERE id=?");
                PreparedStatement scoresPS = 
                        con.prepareStatement("DELETE FROM scores WHERE matchid=?");
                PreparedStatement bet1x2PS = 
                        con.prepareStatement("DELETE FROM bet1x2 WHERE matchid=?");
                PreparedStatement betahPS = 
                        con.prepareStatement("DELETE FROM betah WHERE matchid=?");
                PreparedStatement betbttsPS = 
                        con.prepareStatement("DELETE FROM betbtts WHERE matchid=?");
                PreparedStatement betdcPS = 
                        con.prepareStatement("DELETE FROM betdc WHERE matchid=?");
                PreparedStatement betdnbPS = 
                        con.prepareStatement("DELETE FROM betdnb WHERE matchid=?");
                PreparedStatement betouPS = 
                        con.prepareStatement("DELETE FROM betou WHERE matchid=?");){
            matchesPS.setLong(1, match.getId());
            scoresPS.setLong(1, match.getId());
            bet1x2PS.setLong(1, match.getId());
            betahPS.setLong(1, match.getId());
            betbttsPS.setLong(1, match.getId());
            betdcPS.setLong(1, match.getId());
            betdnbPS.setLong(1, match.getId());
            betouPS.setLong(1, match.getId());
            
            addToActualPS(matchesPS, scoresPS, bet1x2PS, betahPS, betbttsPS, betdcPS, betdnbPS, betouPS);
            checkCancelationAndExecuteUpdate(matchesPS);
            checkCancelationAndExecuteUpdate(scoresPS);
            checkCancelationAndExecuteUpdate(bet1x2PS);
            checkCancelationAndExecuteUpdate(betahPS);
            checkCancelationAndExecuteUpdate(betbttsPS);
            checkCancelationAndExecuteUpdate(betdcPS);
            checkCancelationAndExecuteUpdate(betdnbPS);
            checkCancelationAndExecuteUpdate(betouPS);
            removeFromActualPS(matchesPS, scoresPS, bet1x2PS, betahPS, betbttsPS, betdcPS, betdnbPS, betouPS);
        } catch (SQLException ex){
            throw new BetIOException("Error while deleting match.", ex);
        }
    }
    
    /**
     * Skontroluje, či má byť aktuálny dotaz prerušený a potom zavolá
     * metódu {@link PreparedStatement#executeUpdate()}.
     * 
     * @param ps
     * @throws SQLException 
     */
    private void checkCancelationAndExecuteUpdate(PreparedStatement ps) throws SQLException {
        checkCancelation();
        ps.executeUpdate();
    }

    @Override
    public <T extends Bet> Collection<String> getAvailableBetCompanies(Class<T> clazz) 
            throws BetIOException, IllegalArgumentException{
        isCanceled = false;
        if(dataSource == null){
            throw new BetIOException("No data source");
        }
        if(clazz == null){
            throw new IllegalArgumentException("Class argument cannot be null.");
        }
        String betTable;
        if(Bet1x2.class.isAssignableFrom(clazz)){
            betTable = "bet1x2";
        } else if(BetAsianHandicap.class.isAssignableFrom(clazz)){
            betTable = "betah";
        } else if(BetBothTeamsToScore.class.isAssignableFrom(clazz)){
            betTable = "betbtts";
        } else if(BetDoubleChance.class.isAssignableFrom(clazz)){
            betTable = "betdc";
        } else if(BetDrawNoBet.class.isAssignableFrom(clazz)){
            betTable = "betdnb";
        } else if(BetOverUnder.class.isAssignableFrom(clazz)){
            betTable = "betou";
        } else {
            betTable = "";
        }
        Collection<String> companies = new HashSet<>();
        if(!betTable.isEmpty()){
            try(Connection con = dataSource.getConnection();
                    PreparedStatement ps = con.prepareStatement(
                            "SELECT DISTINCT betcompany FROM " + betTable)){
                addToActualPS(ps);
                ResultSet rs = ps.executeQuery();
                removeFromActualPS(ps);
                while(rs.next()){
                    checkCancelation();
                    companies.add(rs.getString("betcompany"));
                }
            } catch (SQLException ex){
                throw new BetIOException("Exception while getting available bet companies "
                        + "(class: " + clazz + ")", ex);
            }
        }
        return companies;
    }

    @Override
    public Map<String, Collection<String>> getAvailableCountriesAndLeagues() 
            throws BetIOException {
        isCanceled = false;
        if(dataSource == null){
            throw new BetIOException("No data source");
        }
        try(Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(
                        "SELECT DISTINCT country, league FROM matches ORDER BY country")){
            addToActualPS(ps);
            ResultSet rs = ps.executeQuery();
            removeFromActualPS(ps);
            Map<String, Collection<String>> ctryAndlgs = new HashMap<>();
            String previousCtr = "";
            Collection<String> leagues = null;
            while(rs.next()){
                checkCancelation();
                String country = rs.getString("country");
                if(!previousCtr.equals(country)){
                    if(!previousCtr.isEmpty()){
                        ctryAndlgs.put(previousCtr, leagues);
                    }
                    leagues = new HashSet<>();
                }
                leagues.add(rs.getString("league"));
                previousCtr = country;
            }
            if(!previousCtr.isEmpty()){ //adding last country
                ctryAndlgs.put(previousCtr, leagues);
            }
            return ctryAndlgs;
        } catch (SQLException ex){
            throw new BetIOException("Exception while getting available leagues.", ex);
        }
    }

    @Override
    public Calendar getFirstDate() throws BetIOException {
        return getDate("MIN");
    }
    
    @Override
    public Calendar getLastDate() throws BetIOException {
        return getDate("MAX");
    }
    
    /**
     * Vráti časový údaj z DB, ktorý je špecifický pre modifier.
     * Konkrétne podľa SQL príkazu SELECT {@code <modifier>}(matchdate) FROM matches.
     * Vracia prvý nájdený údaj, alebo dnešný dátum, ak nie je nájdený žiaden.
     * 
     * @param modifier
     * @return
     * @throws BetIOException 
     */
    private Calendar getDate(String modifier) throws BetIOException{
        isCanceled = false;
        if(dataSource == null){
            throw new BetIOException("No data source");
        }
        try(Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(
                        "SELECT " + modifier + "(matchdate) AS mdate FROM matches")){
            checkCancelation();
            addToActualPS(ps);
            ResultSet rs = ps.executeQuery();
            removeFromActualPS(ps);
            Calendar c = new GregorianCalendar();
            if(!rs.next()){
                return c;
            }
            Timestamp ts = rs.getTimestamp("mdate");
            if(ts == null){
                return c;
            }
            c.setTimeInMillis(ts.getTime());
            return c;
        } catch (SQLException ex){
            throw new BetIOException("Exception while getting " + modifier + " date.", ex);
        }
    }

    @Override
    public void deleteBetCompany(String betCompany) throws BetIOException {
        if(dataSource == null){
            throw new BetIOException("No data source");
        }
        if(betCompany == null || betCompany.isEmpty()){
            throw new BetIOException("Bet company cannot be null or empty.");
        }
        try(Connection con = dataSource.getConnection();
                PreparedStatement bet1x2 = con.prepareStatement(getDeleteQuery("bet1x2", "betCompany LIKE ? "));
                PreparedStatement betah = con.prepareStatement(getDeleteQuery("betah", "betCompany LIKE ? "));
                PreparedStatement betbtts = con.prepareStatement(getDeleteQuery("betbtts", "betCompany LIKE ? "));
                PreparedStatement betdc = con.prepareStatement(getDeleteQuery("betdc", "betCompany LIKE ? "));
                PreparedStatement betdnb = con.prepareStatement(getDeleteQuery("betdnb", "betCompany LIKE ? "));
                PreparedStatement betou = con.prepareStatement(getDeleteQuery("betou", "betCompany LIKE ? "));
                PreparedStatement scores = con.prepareStatement("DELETE FROM scores "
                        + "WHERE matchid NOT IN (SELECT id FROM matches)");
                PreparedStatement matches = con.prepareStatement("DELETE FROM matches WHERE id NOT IN "
                    + "((SELECT DISTINCT matchid FROM bet1x2) "
                    + "UNION (SELECT DISTINCT matchid FROM betah) "
                    + "UNION (SELECT DISTINCT matchid FROM betbtts) "
                    + "UNION (SELECT DISTINCT matchid FROM betdc) "
                    + "UNION (SELECT DISTINCT matchid FROM betdnb) "
                    + "UNION (SELECT DISTINCT matchid FROM betou))")){
            addToActualPS(bet1x2, betah, betbtts, betdc, betdnb, betou, matches, scores);
            bet1x2.setString(1, betCompany);
            betah.setString(1, betCompany);
            betbtts.setString(1, betCompany);
            betdc.setString(1, betCompany);
            betdnb.setString(1, betCompany);
            betou.setString(1, betCompany);
            checkCancelationAndExecuteUpdate(bet1x2);
            checkCancelationAndExecuteUpdate(betah);
            checkCancelationAndExecuteUpdate(betbtts);
            checkCancelationAndExecuteUpdate(betdc);
            checkCancelationAndExecuteUpdate(betdnb);
            checkCancelationAndExecuteUpdate(betou);
            //matches najskôr -- scores berie NOT IN matches.id
            checkCancelationAndExecuteUpdate(matches);
            checkCancelationAndExecuteUpdate(scores);
            removeFromActualPS(bet1x2, betah, betbtts, betdc, betdnb, betou, matches, scores);
        } catch (SQLException ex){
            throw new BetIOException("Exception while deleting bet company.", ex);
        }
    }

    @Override
    public void deleteLeague(String country, String league) throws BetIOException {
        if(dataSource == null){
            throw new BetIOException("No data source");
        }
        if(country == null || country.isEmpty()){
            throw new BetIOException("Country cannot be null or empty.");
        }
        if(league == null || league.isEmpty()){
            throw new BetIOException("League cannot be null or empty.");
        }
        String whereforIds = "country LIKE ? AND league LIKE ?";
        try(Connection con = dataSource.getConnection();
                PreparedStatement scores = con.prepareStatement(getDeleteQuery("scores", whereforIds));
                PreparedStatement bet1x2 = con.prepareStatement(getDeleteQuery("bet1x2", whereforIds));
                PreparedStatement betah = con.prepareStatement(getDeleteQuery("betah", whereforIds));
                PreparedStatement betbtts = con.prepareStatement(getDeleteQuery("betbtts", whereforIds));
                PreparedStatement betdc = con.prepareStatement(getDeleteQuery("betdc", whereforIds));
                PreparedStatement betdnb = con.prepareStatement(getDeleteQuery("betdnb", whereforIds));
                PreparedStatement betou = con.prepareStatement(getDeleteQuery("betou", whereforIds));
                PreparedStatement matches = con.prepareStatement(
                        "DELETE FROM matches WHERE " + whereforIds)){
            addToActualPS(bet1x2, betah, betbtts, betdc, betdnb, betou, matches, scores);
            scores.setString(1, country);
            scores.setString(2, league);
            bet1x2.setString(1, country);
            bet1x2.setString(2, league);
            betah.setString(1, country);
            betah.setString(2, league);
            betbtts.setString(1, country);
            betbtts.setString(2, league);
            betdc.setString(1, country);
            betdc.setString(2, league);
            betdnb.setString(1, country);
            betdnb.setString(2, league);
            betou.setString(1, country);
            betou.setString(2, league);
            matches.setString(1, country);
            matches.setString(2, league);
            checkCancelationAndExecuteUpdate(scores);
            checkCancelationAndExecuteUpdate(bet1x2);
            checkCancelationAndExecuteUpdate(betah);
            checkCancelationAndExecuteUpdate(betbtts);
            checkCancelationAndExecuteUpdate(betdc);
            checkCancelationAndExecuteUpdate(betdnb);
            checkCancelationAndExecuteUpdate(betou);
            checkCancelationAndExecuteUpdate(matches);
            removeFromActualPS(bet1x2, betah, betbtts, betdc, betdnb, betou, matches, scores);
        } catch (SQLException ex){
            throw new BetIOException("Exception while deleting league.", ex);
        }
    }
    
    /**
     * Vráti delete dotaz do DB. Dotaz bude pre tabuľku {@code table} a 
     * mazať sa budú záznamy, ktoré majú matchID v rozsahu, ktorý vymedzí 
     * {@code whereForIds}.
     * 
     * @param table rabuľka
     * @param whereForIds where, ktorý vymedzí rozsah pre matchId
     * @return DELETE FROM {@code <table>} WHERE matchid IN (SELECT id FROM matches WHERE 
     * {@code <whereForIds>} );
     */
    private String getDeleteQuery(String table, String whereForIds){
        return "DELETE FROM " + table + 
                " WHERE matchid IN (SELECT id FROM matches WHERE " + whereForIds + ")";
    }

    @Override
    public void deleteSport(Sport sport) throws BetIOException {
        if(dataSource == null){
            throw new BetIOException("No data source");
        }
        if(sport == null){
            throw new BetIOException("Sport cannot be null.");
        }
        String whereforIds = "sport LIKE ?";
        try(Connection con = dataSource.getConnection();
                PreparedStatement scores = con.prepareStatement(getDeleteQuery("scores", whereforIds));
                PreparedStatement bet1x2 = con.prepareStatement(getDeleteQuery("bet1x2", whereforIds));
                PreparedStatement betah = con.prepareStatement(getDeleteQuery("betah", whereforIds));
                PreparedStatement betbtts = con.prepareStatement(getDeleteQuery("betbtts", whereforIds));
                PreparedStatement betdc = con.prepareStatement(getDeleteQuery("betdc", whereforIds));
                PreparedStatement betdnb = con.prepareStatement(getDeleteQuery("betdnb", whereforIds));
                PreparedStatement betou = con.prepareStatement(getDeleteQuery("betou", whereforIds));
                PreparedStatement matches = con.prepareStatement(
                        "DELETE FROM matches WHERE " + whereforIds)){
            addToActualPS(bet1x2, betah, betbtts, betdc, betdnb, betou, matches, scores);
            scores.setString(1, sport.toString());
            bet1x2.setString(1, sport.toString());
            betah.setString(1, sport.toString());
            betbtts.setString(1, sport.toString());
            betdc.setString(1, sport.toString());
            betdnb.setString(1, sport.toString());
            betou.setString(1, sport.toString());
            matches.setString(1, sport.toString());
            checkCancelationAndExecuteUpdate(scores);
            checkCancelationAndExecuteUpdate(bet1x2);
            checkCancelationAndExecuteUpdate(betah);
            checkCancelationAndExecuteUpdate(betbtts);
            checkCancelationAndExecuteUpdate(betdc);
            checkCancelationAndExecuteUpdate(betdnb);
            checkCancelationAndExecuteUpdate(betou);
            checkCancelationAndExecuteUpdate(matches);
            removeFromActualPS(bet1x2, betah, betbtts, betdc, betdnb, betou, matches, scores);
        } catch (SQLException ex){
            throw new BetIOException("Exception while deleting sport.", ex);
        }
    }
    
    @Override
    public void cancelActualQuery() {
        isCanceled = true;
        for(PreparedStatement ps : actualPreparedStatements){
            try{
                if(ps != null){
                    ps.cancel();
                    ps.close();
                }
            } catch (SQLException ex){
                System.out.println(ex);
            }
        }
    }
    
    @Override
    public DBBetIOManager cloneBetIOManager() {
        try{
            DBBetIOManager man = (DBBetIOManager)super.clone();
            man.dataSource = this.dataSource;
            return man;
        } catch (CloneNotSupportedException ex){
            return null;
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return cloneBetIOManager();
    }
}
