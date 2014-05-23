/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.match;

import chorke.proprietary.bet.apps.core.bets.Bet;
import chorke.proprietary.bet.apps.StaticConstants.Winner;
import chorke.proprietary.bet.apps.core.match.score.PartialScore;
import chorke.proprietary.bet.apps.core.match.score.Score;
import chorke.proprietary.bet.apps.core.match.sports.Sport;
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

    /**
     * Ak {@code score == null}, potom je skóore resetované.
     * @param score 
     */
    public void setScore(Score score) {
        if(score == null){
            this.score = new Score();
        } else {
            this.score = score;
        }
    }
    
    /**
     * Nikdy {@code null}.
     * @return 
     */
    public Score getScore() {
        return score;
    }
    
    public void addPartialScore(PartialScore partialScore){
        sport.addPartialScore(partialScore, score);
    }
    
    public void addPartialScore(int firstParty, int secondParty){
        addPartialScore(new PartialScore(firstParty, secondParty));
    }
    
    public Winner getWinner(){
        return sport.getWinner(score);
    }
    
    public Winner getRegularTimeWinner(){
        return sport.getRegularTimeWinner(score);
    }
    
    /**
     * Ak {@code properties == null}, potom sú properties resetované.
     * @param properties 
     */
    public void setProperties(MatchProperties properties) {
        if(properties == null){
            this.properties = new MatchProperties();
        } else {
            this.properties = properties;
        }
    }

    /**
     * Nikdy {@code null}.
     * @return 
     */
    public MatchProperties getProperties() {
        return properties;
    }
    
    @SuppressWarnings("unchecked")
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[").append(sport).append(" {").append(id).append("}] ")
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

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !obj.getClass().equals(this.getClass())){
            return false;
        }
        Match m = (Match) obj;
        if(this.id == null){
            return false;
        }
        if(this.sport == null){
            return false;
        }
        return this.id.equals(m.id)
                && this.sport.equals(m.sport)
                && this.score.equals(m.score)
                && this.properties.equals(m.properties);
    }

    @Override
    public int hashCode() {
        return 31 * ((id == null ? 0 : id.hashCode())
                + (sport == null ? 0 : sport.hashCode())
                + properties.hashCode()
                + score.hashCode());
    }
}
