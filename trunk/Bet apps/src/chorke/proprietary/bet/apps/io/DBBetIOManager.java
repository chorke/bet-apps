/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.io;

import chorke.proprietary.bet.apps.StaticConstants.Sport;
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
import javax.sql.DataSource;

/**
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
        try(Connection con = dataSource.getConnection()){
            Collection<Match> matches = getMatches(con);
            Map<Long, Score> scores = getScores(con);
            //TODO - stávky a spojiť to do zápasu
            //TODO - get metódy pre jednotlivé časti zmeniť tak, aby brali ResultSet
            //budú potom použiteľné pre loadMatches(MatchProperties)
            throw new UnsupportedOperationException("Unfinished");
        } catch (SQLException ex){
            throw new BetIOException("Error while finding all matches.", ex);
        }
    }

    private Map<Long, Score> getScores(Connection con) throws SQLException{
        try(PreparedStatement ps = con.prepareStatement("SELECT * FROM scores")){
            HashMap<Long, Score> out = new HashMap<>();
            HashMap<Tuple<Integer, Integer>, PartialScore> handles = new HashMap<>();
            ResultSet scores = ps.executeQuery();
            Score score;
            while(scores.next()){
                if(scores.getInt("part") != -1){
                    handles.put(new Tuple<>(scores.getInt("matchid"), scores.getInt("part")),
                            new PartialScore(scores.getInt("team2"), scores.getInt("part")));
                }
            }
            for(Tuple<Integer, Integer> tp : handles.keySet()){
                if(tp.second.equals(1)){
                    score = new Score();
                    score.addPartialScore(handles.get(tp));
                    out.put(new Long((long)tp.first), score);
                    handles.remove(tp);
                }
            }
            int part = 2;
            while(!handles.isEmpty()){
                Tuple<Integer, Integer> tp;
                Iterator<Tuple<Integer, Integer>> iter = handles.keySet().iterator();
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
        } catch (SQLException ex){
            throw new SQLException("Error while loading scores.", ex);
        }
    }
    
    private Collection<Match> getMatches(Connection con) throws SQLException{
        try(PreparedStatement ps = con.prepareCall("SELECT * FROM matches")){
            ResultSet matches = ps.executeQuery();
            Collection<Match> out = new LinkedList<>();
            Match match;
            MatchProperties prop;
            Calendar cal;
            while(matches.next()){
                match = new Match(Sport.valueOf(matches.getString("sport")));
                match.setId(matches.getLong("id"));
                prop = new MatchProperties();
                prop.setCountry(matches.getString("country"));
                prop.setLeague(matches.getString("league"));
                cal = new GregorianCalendar();
                cal.setTimeInMillis(matches.getTimestamp("matchdate").getTime());
                prop.setDate(cal);
                match.setProperties(prop);
                out.add(match);
            }
            return out;
        } catch (SQLException ex){
            throw new SQLException("Error while getting matches", ex);
        }
    }
    
    
    @Override
    public Collection<Match> loadMatches(MatchProperties properties) throws BetIOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteMatch(Match match) throws BetIOException {
        throw new UnsupportedOperationException("Not supported yet.");
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
