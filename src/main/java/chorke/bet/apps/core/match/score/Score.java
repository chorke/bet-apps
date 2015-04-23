
package chorke.bet.apps.core.match.score;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Score {

    private int scoreFirstParty = 0;
    private int scoreSecondParty = 0;
    private List<PartialScore> partialScore = new ArrayList<>();

    public Score(){}
    
    private Score(Score score){
        scoreFirstParty = score.scoreFirstParty;
        scoreSecondParty = score.scoreSecondParty;
        for(PartialScore ps : score.partialScore){
            this.partialScore.add(new PartialScore(ps.firstParty, ps.secondParty));
        }
    }
    
    public int getScoreFirstParty() {
        return scoreFirstParty;
    }

    public int getScoreSecondParty() {
        return scoreSecondParty;
    }

    public Score getScoreAfterPart(int part) {
        if (part >= partialScore.size()) {
            return new Score(this);
        }
        if (part <= 0) {
            return new Score();
        }
        Score s = new Score();
        for (int i = 0; i < part; i++) {
            PartialScore ps = partialScore.get(i);
            s.addPartialScore(ps.firstParty, ps.secondParty);
        }
        return s;
    }

    public int getSumScore() {
        return scoreFirstParty + scoreSecondParty;
    }
    
    public void setScoreFirstParty(int firstParty){
        this.scoreFirstParty = firstParty;
    }
    
    public void setScoreSecondParty(int secondParty){
        this.scoreSecondParty = secondParty;
    }
    
    /**
     * Pridá skóre. Neupravuje finálne skóre, a preto môže po zavolaní tejto metódy 
     * nastať nekonzistencia. Je lepšie použiť metódu {@link
     * chorke.proprietary.bet.apps.core.match.sports.Sport#addPartialScore(PartialScore)},
     * ktorá príslušne upraví aj výsledné skóre.
     * @param ps 
     * @see  chorke.proprietary.bet.apps.core.match.sports.Sport#addPartialScore(PartialScore) 
     */
    public void addPartialScore(PartialScore ps){
        partialScore.add(ps);
    }
    
    /**
     * Pozri metódu {@link #addPartialScore(PartialScore)}
     * @param firstParty
     * @param secondParty 
     * @see  #addPartialScore(PartialScore)
     * @see  chorke.proprietary.bet.apps.core.match.sports.Sport#addPartialScore(PartialScore) 
     */
    public void addPartialScore(int firstParty, int secondParty) {
        addPartialScore(new PartialScore(firstParty, secondParty));
    }

    public List<PartialScore> getPartialScore(){
        return Collections.unmodifiableList(partialScore);
    }
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder()
                .append(scoreFirstParty)
                .append(":")
                .append(scoreSecondParty)
                .append(" ( ");
        for (PartialScore ps : partialScore) {
            b.append(ps).append(" ");
        }
        b.append(")");
        return b.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !obj.getClass().equals(this.getClass())){
            return false;
        }
        Score sc = (Score) obj;
        if(sc.scoreFirstParty != this.scoreFirstParty
                || sc.scoreSecondParty != this.scoreSecondParty){
            return false;
        }
        if(sc.partialScore.size() != this.partialScore.size()){
            return false;
        }
        for(int i = 0; i < this.partialScore.size(); i++){
            if(!this.partialScore.get(i).equals(sc.partialScore.get(i))){
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hc = 0;
        for(PartialScore ps : partialScore){
            hc += ps.hashCode();
        }
        return hc * (scoreFirstParty * 1000 + scoreSecondParty);
    }
}