
package chorke.proprietary.bet.apps.io;

import chorke.proprietary.bet.apps.core.Tuple;
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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;

/**
 *
 * Implementácia {@link BetIOManager} pre ukladanie stávok. Iniciálne je implementácia
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
            throw new BetIOException("Error by saving match" + ex, ex);
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
        try(Connection con = dataSource.getConnection();
                PreparedStatement matchesPS = con.prepareStatement("SELECT * FROM matches");
                PreparedStatement scoresPS = con.prepareStatement("SELECT * FROM scores");
                PreparedStatement bet1x2PS = con.prepareStatement("SELECT * FROM bet1x2");
                PreparedStatement betahPS = con.prepareStatement("SELECT * FROM betah");
                PreparedStatement betbttsPS = con.prepareStatement("SELECT * FROM betbtts");
                PreparedStatement betdcPS = con.prepareStatement("SELECT * FROM betdc");
                PreparedStatement betdnbPS = con.prepareStatement("SELECT * FROM betdnb");
                PreparedStatement betouPS = con.prepareStatement("SELECT * FROM betou");){
            return getMatchesFromPreparedStatements(matchesPS, scoresPS, 
                    bet1x2PS, betahPS, betbttsPS, betdcPS, betdnbPS, betouPS);
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
        Map<Long, Match> matches = getMatches(matchesPS.executeQuery());
        Map<Long, Score> scores = getScores(scoresPS.executeQuery());
        Map<Long, Collection<Bet>> bets = new HashMap<>();
        getBet1x2(bet1x2PS.executeQuery(), bets);
        getBetAsianHandicap(betahPS.executeQuery(), bets);
        getBetBothTeamsToScore(betbttsPS.executeQuery(), bets);
        getBetDoubleChance(betdcPS.executeQuery(), bets);
        getBetDrawNoBet(betdnbPS.executeQuery(), bets);
        getBetOverUnder(betouPS.executeQuery(), bets);
        return merge(matches, scores, bets);
    }
    
    /**
     * Priradí skóre a stávky ku zápasom. Predané mapy sú vo forme
     * {@code <idZápasu, zápas/skóre/stávky>}
     * @param matches
     * @param scores
     * @param bets
     * @return 
     */
    private Collection<Match> merge(Map<Long, Match> matches, 
            Map<Long, Score> scores, Map<Long, Collection<Bet>> bets){
        Collection<Match> merged = new LinkedList<>();
        Match match;
        Iterator<Long> iter = matches.keySet().iterator();
        Long l;
        Score score;
        while(iter.hasNext()){
            l = iter.next();
            match = matches.get(l);
            score = scores.get(l);
            match.setScore(null);
            if(score != null){
                for(PartialScore ps : score.getPartialScore()){
                    match.addPartialScore(ps);
                }
            }
            if(bets.containsKey(l)){
                for(Bet b : bets.get(l)){
                    match.addBet(b);
                }
            }
            merged.add(match);
            iter.remove();
            scores.remove(l);
            bets.remove(l);
        }
        Iterator<Match> cleanUp = merged.iterator();
        while(cleanUp.hasNext()){
            if(cleanUp.next().getBets().isEmpty()){
                cleanUp.remove();
            }
        }
        return merged;
    }
    
    /**
     * Pomocná metóda pre ukladanie stávok do mapy.
     * Obecné zásady sú, že ak už mapa obsahuje kľúč, je pridaná stávka ku 
     * príslušnej kolekcii, inak je vytvorená nová stávka a kolekcia.
     * @param bet
     * @param storage
     * @param idx 
     */
    private void putBetToStorage(Bet bet, Map<Long, Collection<Bet>> storage, Long idx){
        Collection<Bet> betsCollection;
        if(storage.containsKey(idx)){
            betsCollection = storage.get(idx);
        } else {
            betsCollection = new LinkedList<>();
            storage.put(idx, betsCollection);
        }
        betsCollection.add(bet);
    }
    
    /**
     * Z {@link ResultSet} {@code bet} získa všetky stávky {@link Bet1x2}
     * a uloží do {@code storage}. {@code bet} nie je nijako kontrolovaná
     * a mala by obsahovať iba korektná stávky {@link Bet1x2}.
     * @param bet
     * @param storage
     * @throws SQLException 
     */
    private void getBet1x2(ResultSet bet, Map<Long, Collection<Bet>> storage) throws SQLException{
        while(bet.next()){
            putBetToStorage(
                    new Bet1x2(bet.getString("betcompany"),
                        new BigDecimal(bet.getString("bet1")), 
                        new BigDecimal(bet.getString("betx")),
                        new BigDecimal(bet.getString("bet2"))),
                    storage, 
                    bet.getLong("matchid"));
        }
    }
    
    /**
     * Z {@link ResultSet} {@code bet} získa všetky stávky {@link BetAsianHandicap}
     * a uloží do {@code storage}. {@code bet} nie je nijako kontrolovaná
     * a mala by obsahovať iba korektné stávky {@link BetAsianHandicap}.
     * @param bet
     * @param storage
     * @throws SQLException 
     */
    private void getBetAsianHandicap(ResultSet bet, Map<Long, Collection<Bet>> storage) throws SQLException{
        while(bet.next()){
            putBetToStorage(
                    new BetAsianHandicap(bet.getString("betcompany"),
                        new BigDecimal(bet.getString("bet1")), 
                        new BigDecimal(bet.getString("bet2")),
                        new BigDecimal(bet.getString("handicap")),
                        bet.getString("description")),
                    storage, 
                    bet.getLong("matchid"));
        }
    }
    
    /**
     * Z {@link ResultSet} {@code bet} získa všetky stávky {@link BetBothTeamsToScore}
     * a uloží do {@code storage}. {@code bet} nie je nijako kontrolovaná
     * a mala by obsahovať iba korektná stávky {@link BetBothTeamsToScore}.
     * @param bet
     * @param storage
     * @throws SQLException 
     */
    private void getBetBothTeamsToScore(ResultSet bet, Map<Long, Collection<Bet>> storage) throws SQLException{
        while(bet.next()){
            putBetToStorage(
                    new BetBothTeamsToScore(bet.getString("betcompany"),
                        new BigDecimal(bet.getString("yesbet")), 
                        new BigDecimal(bet.getString("nobet"))),
                    storage, 
                    bet.getLong("matchid"));
        }
    }
    
    /**
     * Z {@link ResultSet} {@code bet} získa všetky stávky {@link BetDoubleChance}
     * a uloží do {@code storage}. {@code bet} nie je nijako kontrolovaná
     * a mala by obsahovať iba korektná stávky {@link BetDoubleChance}.
     * @param bet
     * @param storage
     * @throws SQLException 
     */
    private void getBetDoubleChance(ResultSet bet, Map<Long, Collection<Bet>> storage) throws SQLException{
        while(bet.next()){
            putBetToStorage(
                    new BetDoubleChance(bet.getString("betcompany"),
                        new BigDecimal(bet.getString("bet1x")), 
                        new BigDecimal(bet.getString("bet12")),
                        new BigDecimal(bet.getString("bet2x"))),
                    storage, 
                    bet.getLong("matchid"));
        }
    }
    
    /**
     * Z {@link ResultSet} {@code bet} získa všetky stávky {@link BetDrawNoBet}
     * a uloží do {@code storage}. {@code bet} nie je nijako kontrolovaná
     * a mala by obsahovať iba korektná stávky {@link BetDrawNoBet}.
     * @param bet
     * @param storage
     * @throws SQLException 
     */
    private void getBetDrawNoBet(ResultSet bet, Map<Long, Collection<Bet>> storage) throws SQLException{
        while(bet.next()){
            putBetToStorage(
                    new BetDrawNoBet(bet.getString("betcompany"),
                        new BigDecimal(bet.getString("bet1")), 
                        new BigDecimal(bet.getString("bet2"))),
                    storage, 
                    bet.getLong("matchid"));
        }
    }
    
    /**
     * Z {@link ResultSet} {@code bet} získa všetky stávky {@link BetOverUnder}
     * a uloží do {@code storage}. {@code bet} nie je nijako kontrolovaná
     * a mala by obsahovať iba korektná stávky {@link BetOverUnder}.
     * @param bet
     * @param storage
     * @throws SQLException 
     */
    private void getBetOverUnder(ResultSet bet, Map<Long, Collection<Bet>> storage) throws SQLException{
        while(bet.next()){
            putBetToStorage(
                    new BetOverUnder(bet.getString("betcompany"),
                        new BigDecimal(bet.getString("total")), 
                        new BigDecimal(bet.getString("overbet")),
                        new BigDecimal(bet.getString("underbet")),
                        bet.getString("description")),
                    storage, 
                    bet.getLong("matchid"));
        }
    }
    
    /**
     * Vráti skóre z {@link ResultSet} {@code scores}. {@code scores} nie je 
     * nijako kontrolovaná a mala by obsahovať iba korektné {@link Score} záznamy.
     * @param scores
     * @return
     * @throws SQLException 
     */
    private Map<Long, Score> getScores(ResultSet scores) throws SQLException{
        HashMap<Long, Score> out = new HashMap<>();
        HashMap<Tuple<Integer, Integer>, PartialScore> handles = new HashMap<>();
        Score score;
        while(scores.next()){
            if(scores.getInt("part") != -1){
                handles.put(new Tuple<>(scores.getInt("matchid"), scores.getInt("part")),
                        new PartialScore(scores.getInt("team1"), scores.getInt("team2")));
            }
        }
        Iterator<Tuple<Integer, Integer>> iter = handles.keySet().iterator();
        Tuple<Integer, Integer> tp;
        while(iter.hasNext()){
            tp = iter.next();
            if(tp.second.equals(1)){
                score = new Score();
                score.addPartialScore(handles.get(tp));
                out.put(new Long((long)tp.first), score);
                iter.remove();
            }
        }
        int part = 2;
        while(!handles.isEmpty()){
            iter = handles.keySet().iterator();
            while(iter.hasNext()){
                tp = iter.next();
                if(tp.second.equals(part)){
                    out.get(new Long((long)tp.first)).addPartialScore(handles.get(tp));
                    iter.remove();
                }
            }
            part++;
        }
        return out;
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
        try(Connection con = dataSource.getConnection();
                PreparedStatement matchesPS = prepareMatchStatement(con, 
                        properties.getStartDate(),
                        properties.getEndDate(),
                        properties.getLeagues());
                PreparedStatement scoresPS = con.prepareStatement("SELECT * FROM scores");
                PreparedStatement bet1x2PS = prepareBetStatement(con,
                        properties.getBetCompanies(), "bet1x2");
                PreparedStatement betahPS = prepareBetStatement(con,
                        properties.getBetCompanies(), "betah");
                PreparedStatement betbttsPS = prepareBetStatement(con,
                        properties.getBetCompanies(), "betbtts");
                PreparedStatement betdcPS = prepareBetStatement(con,
                        properties.getBetCompanies(), "betdc");
                PreparedStatement betdnbPS = prepareBetStatement(con,
                        properties.getBetCompanies(), "betdnb");
                PreparedStatement betouPS = prepareBetStatement(con,
                        properties.getBetCompanies(), "betou");){
            return getMatchesFromPreparedStatements(matchesPS, scoresPS, 
                    bet1x2PS, betahPS, betbttsPS, betdcPS, betdnbPS, betouPS);
        } catch (SQLException ex){
            throw new BetIOException("Error while finding all matches.", ex);
        }
    }

    /**
     * Bet - konkrétny typ stávky.
     * @param betCompanies
     * @param bet
     * @return
     * @throws SQLException 
     */
    private PreparedStatement prepareBetStatement(
            Connection con, Set<String> betCompanies, String bet) throws SQLException{
        StringBuilder out = new StringBuilder("SELECT * FROM ").append(bet);
        if(betCompanies == null || betCompanies.isEmpty()){
            return con.prepareStatement(out.toString());
        }
        out.append(" WHERE ");
        for(String betCompany : betCompanies){
            out.append("betcompany LIKE ? OR ");
        }
        out.delete(out.length() - 4, out.length());
        PreparedStatement ps = con.prepareStatement(out.toString());
        int i = 1;
        for(String betCompany : betCompanies){
            ps.setString(i, betCompany);
            i++;
        }
        return ps;
    }
    
    private PreparedStatement prepareMatchStatement(
            Connection con, Calendar start, Calendar end, Set<String> leagues) throws SQLException{
        StringBuilder out = new StringBuilder("SELECT * FROM matches");
        if(start == null && end == null && (leagues == null || leagues.isEmpty())){
            return con.prepareStatement(out.toString());
        }
        out.append(" WHERE ");
        if(start != null){
            out.append("matchdate >= ?");
            if(end != null){
                out.append(" AND ");
            }
        }
        if(end != null){
            out.append("matchdate <= ?");
            if(leagues != null && !leagues.isEmpty()){
                out.append(" AND ");
            }
        }
        if(leagues != null && !leagues.isEmpty()){
            out.append("(");
            for(String league : leagues){
                out.append("league LIKE ? OR ");
            }
            out.delete(out.length() - 4, out.length());
            out.append(")");
        }
        PreparedStatement ps = con.prepareStatement(out.toString());
        int i = 1;
        if(start != null){
            ps.setTimestamp(i, new Timestamp(start.getTimeInMillis()));
            i++;
        }
        if(end != null){
            ps.setTimestamp(i, new Timestamp(end.getTimeInMillis()));
            i++;
        }
        if(leagues != null && !leagues.isEmpty()){
            for(String league : leagues){
                ps.setString(i, league);
                i++;
            }
        }
        return ps;
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
