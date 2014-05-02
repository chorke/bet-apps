
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
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    
    public DBBetIOManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public void saveMatch(Match match) throws BetIOException {
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
            
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            match.setId(rs.getLong(1));
      
            ps = con.prepareStatement("INSERT INTO scores (matchid,team1,team2,part) VALUES(?,?,?,?)");
            ps.setLong(1, match.getId());
            ps.setInt(2, match.getScore().getScoreFirstParty());
            ps.setInt(3, match.getScore().getScoreSecondParty());
            ps.setInt(4, -1);
            ps.executeUpdate();
            List<PartialScore> listPs = match.getScore().getPartialScore();
            int i = 0;
            while(i < listPs.size()){
                ps = con.prepareStatement(
                        "INSERT INTO scores (matchid,team1,team2,part) VALUES(?,?,?,?)");
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
                    ps.executeUpdate();
                }
            }
        } catch (SQLException ex){
            if(match.getId() != null){
                deleteMatch(match);
            }
            throw new BetIOException("Error by saving match. ", ex);
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
        System.out.println("getting matches");
        Map<Long, Match> matches = getMatches(matchesPS.executeQuery());
        matchesPS.close();
        System.out.println("getting scores");
        getScores(scoresPS.executeQuery(), matches);
        scoresPS.close();
        if(bet1x2PS != null){
            System.out.println("getting 1x2");
            getBet1x2(bet1x2PS.executeQuery(), matches);
            bet1x2PS.close();
        }
        if(betahPS != null){
            System.out.println("getting ah");
            getBetAsianHandicap(betahPS.executeQuery(), matches);
            betahPS.close();
        }
        if(betbttsPS != null){
            System.out.println("getting btts");
            getBetBothTeamsToScore(betbttsPS.executeQuery(), matches);
            betbttsPS.close();
        }
        if(betdcPS != null){
            System.out.println("getting dc");
            getBetDoubleChance(betdcPS.executeQuery(), matches);
            betdcPS.close();
        }
        if(betdnbPS != null){
            System.out.println("getting dnb");
            getBetDrawNoBet(betdnbPS.executeQuery(), matches);
            betdnbPS.close();
        }
        if(betouPS != null){
            System.out.println("getting ou");
            getBetOverUnder(betouPS.executeQuery(), matches);
            betouPS.close();
        }
        Collection<Match> outputMatches = new LinkedList<>();
        Iterator<Long> iter = matches.keySet().iterator();
        Match match;
        while(iter.hasNext()){
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
            
            return getMatchesFromPreparedStatements(matchesPS, scoresPS, 
                    bet1x2PS, betahPS, betbttsPS, betdcPS, betdnbPS, betouPS);
        } catch (SQLException ex){
            throw new BetIOException("Error while finding all matches.", ex);
        }
    }

    
    
    @Override
    public void deleteMatch(Match match) throws BetIOException {
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
            
            matchesPS.executeUpdate();
            scoresPS.executeUpdate();
            bet1x2PS.executeUpdate();
            betahPS.executeUpdate();
            betbttsPS.executeUpdate();
            betdcPS.executeUpdate();
            betdnbPS.executeUpdate();
            betouPS.executeUpdate();
        } catch (SQLException ex){
            throw new BetIOException("Error while deleting match.", ex);
        }
    }

    @Override
    public <T extends Bet> Collection<String> getAvailableBetCompanies(Class<T> clazz) 
            throws BetIOException, IllegalArgumentException{
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
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
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
    @SuppressWarnings("possibleNullPointerException")
    public Map<String, Collection<String>> getAvailableCountriesAndLeagues() 
            throws BetIOException {
        if(dataSource == null){
            throw new BetIOException("No data source");
        }
        try(Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(
                        "SELECT DISTINCT country, league FROM matches ORDER BY country")){
            ResultSet rs = ps.executeQuery();
            Map<String, Collection<String>> ctryAndlgs = new HashMap<>();
            String previousCtr = "";
            Collection<String> leagues = null;
            while(rs.next()){
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
