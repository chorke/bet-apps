/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.match.score;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Score {

    private int scoreFirstParty = 0;
    private int scoreSecondParty = 0;
    private List<PartialScore> partialScore = new ArrayList<>();

    public int getScoreFirstParty() {
        return scoreFirstParty;
    }

    public void setScoreFirstParty(int scoreFirstParty) {
        this.scoreFirstParty = scoreFirstParty;
    }

    public int getScoreSecondParty() {
        return scoreSecondParty;
    }

    public void setScoreSecondParty(int scoreSecondParty) {
        this.scoreSecondParty = scoreSecondParty;
    }

    public Score getScoreAfterPart(int part) {
        if (part > partialScore.size()) {
            return this;
        }
        if (part <= 0) {
            return new Score();
        }
        Score s = new Score();
        for (int i = 0; i < part; i++) {
            PartialScore ps = partialScore.get(i);
            s.scoreFirstParty += ps.firstParty;
            s.scoreSecondParty += ps.secondParty;
            s.partialScore.add(ps);
        }
        return s;
    }

    public int getSumScore() {
        return scoreFirstParty + scoreSecondParty;
    }

    public void addPartialScore(int firstParty, int secondParty) {
        partialScore.add(new PartialScore(firstParty, secondParty));
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