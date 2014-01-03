
package chorke.proprietary.bet.apps.core.match.score;

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

    public void addPartialScore(PartialScore ps){
        partialScore.add(ps);
        scoreFirstParty += ps.firstParty;
        scoreSecondParty += ps.secondParty;
    }
    
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
}