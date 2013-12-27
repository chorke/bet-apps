/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.match;

import chorke.proprietary.bet.apps.core.bets.Bet;
import chorke.proprietary.bet.apps.StaticConstants.Sport;
import chorke.proprietary.bet.apps.StaticConstants.Winner;
import chorke.proprietary.bet.apps.core.match.score.Score;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author Chorke
 */
public class Match implements Comparable<Match>{
    
    private Long id = null;
    private MatchProperties properties;
    private Score score;
    private final Sport sport;
    
    private Map<String, Collection<Bet>> bets;

    public Match(Sport sport) {
        bets = new HashMap<>();
        score = new Score();
        properties = new MatchProperties();
        this.sport = sport;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sport getSport() {
        return sport;
    }

    public void setScore(Score score) {
        this.score = score;
    }
    
    public Score getScore() {
        return score;
    }
    
    public void addPartialScore(int firstParty, int secondParty){
        score.addPartialScore(firstParty, secondParty);
    }
    
    public Winner getWinner(Score sc){
        if(sc.getScoreFirstParty() > sc.getScoreSecondParty()){
            return Winner.Team1;
        } else if(sc.getScoreSecondParty() > sc.getScoreFirstParty()){
            return Winner.Team2;
        }
        return Winner.Tie;
    }
    
    public Winner getWinner(){
        return getWinner(score);
    }
    
    public Winner getRegularTimeWinner(){
        switch (sport){
            case Soccer:
            case Handball:
                return getWinner(score.getScoreAfterPart(2));
            case Basketball:
                return getWinner(score.getScoreAfterPart(4));
            case Hockey:
                return getWinner(score.getScoreAfterPart(3));
            case Volleyball:
            case Baseball:
            default: 
                return getWinner();
        }
    }
    
    public void setProperties(MatchProperties properties) {
        if(properties == null){
            this.properties = new MatchProperties();
        } else {
            this.properties = properties;
        }
    }

    public MatchProperties getProperties() {
        return properties;
    }
    
    public <T extends Bet> Collection<T> getBet(String betCompany, Class<T> clazz){
        Collection<T> out = new LinkedList<>();
        if(bets.containsKey(betCompany)){
            for(Bet b : bets.get(betCompany)){
                if(b.getClass().equals(clazz)){
                    out.add((T)b);
                }
            }
        }
        return out;
    }

    public Map<String, Collection<Bet>> getBets() {
        return Collections.unmodifiableMap(bets);
    }
    
    public void addBet(Bet bet){
        if(bets.containsKey(bet.betCompany)){
            bets.get(bet.betCompany).add(bet);
        } else {
            Collection<Bet> c = new LinkedList<>();
            c.add(bet);
            bets.put(bet.betCompany, c);
        }
    }
    
//    public void removeBet(String betCompany, Bet bet){
//        if(bets.containsKey(betCompany)){
//            bets.get(betCompany).remove(bet);
//        }
//    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[").append(sport).append("] ")
                .append(properties).append(" ").append(score);
        builder.append(System.lineSeparator());
        for(String com : bets.keySet()){
            builder.append(com).append(System.lineSeparator());
            for(Bet b : bets.get(com)){
                builder.append(b).append(",");
            }
            builder.deleteCharAt(builder.length()-1)
                    .append(System.lineSeparator());
        }
        return builder.toString();
    }

    @Override
    public int compareTo(Match o) {
       return this.properties.getDate().compareTo(o.properties.getDate());
    }
}
