
package chorke.bet.apps.core.match.sports;

import chorke.bet.apps.core.CoreUtils.Winner;
import chorke.bet.apps.core.CoreUtils.BettingSports;
import chorke.bet.apps.core.match.score.PartialScore;
import chorke.bet.apps.core.match.score.Score;

/**
 *
 * @author Chorke
 */
public abstract class Sport {
    
    public static final String SOCCER_STRING = "soccer";
    public static final String HOCKEY_STRING = "hockey";
    public static final String BASKETBALL_STRING = "basketball";
    public static final String HANDBALL_STRING = "handball";
    public static final String VOLLEYBALL_STRING = "volleyball";
    public static final String BASEBALL_STRING = "baseball";
    
    /**
     * Vráti výhercu celého zápasu vrátane nadstavenia atď.
     * @param score
     * @return 
     */
    public Winner getWinner(Score score){
        if(score.getScoreFirstParty() > score.getScoreSecondParty()){
            return Winner.Team1;
        } else if(score.getScoreSecondParty() > score.getScoreFirstParty()){
            return Winner.Team2;
        }
        return Winner.Tie;
    }
    
    /**
     * Vráti výhercu zápasu v riadnom hracom čase podľa skóre score.
     * @param score
     * @return 
     */
    public abstract Winner getRegularTimeWinner(Score score);
    
    /**
     * Upraví skóre tak, aby zodpovedalo skutočnému výsledku (napríklad
     * po nájazdoch v hokeji sa pridáva iba jeden bod,...)
     * @param partialScore
     * @param score 
     */
    abstract void updateScore(PartialScore partialScore, Score score);
    
    /**
     * Pridá nové čiastočné skóre ku celkovému skóre zápasu score. 
     * 
     * @param firstParty
     * @param secondParty
     * @param score 
     */
    public void addPartialScore(PartialScore partialScore, Score score){
        score.addPartialScore(partialScore);
        updateScore(partialScore, score);
    }
    
    /**
     * Pridá nové čiastočné skóre ku celkovému skóre zápasu score. 
     * 
     * @param firstParty
     * @param secondParty
     * @param score 
     */
    public void addPartialScore(int firstParty, int secondParty, Score score){
        addPartialScore(new PartialScore(firstParty, secondParty), score);
    }
    
    /**
     * Vráti implementáciu triedy Sport podľa požadovaného športu sport.
     * Možnosti sú {@link BettingSports#Baseball}, {@link BettingSports#Basketball},
     * {@link BettingSports#Handball}, {@link BettingSports#Hockey},
     * {@link BettingSports#Soccer}, {@link BettingSports#Volleyball}.
     * @param sport
     * @return 
     */
    public static Sport getSport(BettingSports sport){
        switch (sport){
            case Soccer:
                return new Soccer();
            case Handball:
                return new Handball();
            case Basketball:
                return new Basketball();
            case Hockey:
                return new Hockey();
            case Volleyball:
                return new Volleyball();
            case Baseball:
                return new Baseball();
            default: 
                throw new IllegalArgumentException("Unsupported sport");
        }
    }
    
    /**
     * Vráti implementáciu triedy Sport podľa požadovaného športu sport.
     * Možnosti sú {@link #BASEBALL_STRING}, {@link #BASKETBALL_STRING},
     * {@link #HANDBALL_STRING}, {@link #HOCKEY_STRING}, {@link #SOCCER_STRING}
     * {@link #VOLLEYBALL_STRING}.
     * @param sport
     * @return 
     */
    public static Sport getSport(String sport){
        switch (sport){
            case SOCCER_STRING:
                return new Soccer();
            case HANDBALL_STRING:
                return new Handball();
            case BASKETBALL_STRING:
                return new Basketball();
            case HOCKEY_STRING:
                return new Hockey();
            case VOLLEYBALL_STRING:
                return new Volleyball();
            case BASEBALL_STRING:
                return new Baseball();
            default: 
                throw new IllegalArgumentException("Unsupported sport");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !obj.getClass().equals(this.getClass())){
            return false;
        }
        return toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
    
    // implementácie
    private static class Soccer extends Sport{
        @Override
        public Winner getRegularTimeWinner(Score score) {
            return getWinner(score.getScoreAfterPart(2));
        }

        @Override
        public String toString() {
            return SOCCER_STRING;
        }

        @Override
        void updateScore(PartialScore partialScore, Score score) {
            if(score.getPartialScore().size() <= 4){
                score.setScoreFirstParty(score.getScoreFirstParty() + partialScore.firstParty);
                score.setScoreSecondParty(score.getScoreSecondParty()+ partialScore.secondParty);
            } else {
                if(partialScore.firstParty < partialScore.secondParty){
                    score.setScoreSecondParty(score.getScoreSecondParty() + 1);
                } else {
                    score.setScoreFirstParty(score.getScoreFirstParty() + 1);
                }
            }
        }
    }
    
    private static class Hockey extends Sport{
        @Override
        public Winner getRegularTimeWinner(Score score) {
            return getWinner(score.getScoreAfterPart(3));
        }

        @Override
        public String toString() {
            return HOCKEY_STRING;
        }

        @Override
        void updateScore(PartialScore partialScore, Score score) {
            if(score.getPartialScore().size() <= 4){
                score.setScoreFirstParty(score.getScoreFirstParty() + partialScore.firstParty);
                score.setScoreSecondParty(score.getScoreSecondParty()+ partialScore.secondParty);
            } else {
                if(partialScore.firstParty < partialScore.secondParty){
                    score.setScoreSecondParty(score.getScoreSecondParty() + 1);
                } else if(partialScore.firstParty > partialScore.secondParty) {
                    score.setScoreFirstParty(score.getScoreFirstParty() + 1);
                }
            }
        }
        
    }
    
    private static class Basketball extends Sport{
        @Override
        public Winner getRegularTimeWinner(Score score) {
            return getWinner(score.getScoreAfterPart(4));
        }

        @Override
        public String toString() {
            return BASKETBALL_STRING;
        }

        @Override
        void updateScore(PartialScore partialScore, Score score) {
            score.setScoreFirstParty(score.getScoreFirstParty() + partialScore.firstParty);
            score.setScoreSecondParty(score.getScoreSecondParty()+ partialScore.secondParty);
        }
    }
    
    private static class Handball extends Sport{
        @Override
        public Winner getRegularTimeWinner(Score score) {
            return getWinner(score.getScoreAfterPart(2));
        }

        @Override
        public String toString() {
            return HANDBALL_STRING;
        }
        
        @Override
        void updateScore(PartialScore partialScore, Score score) {
            if(score.getPartialScore().size() <= 4){
                score.setScoreFirstParty(score.getScoreFirstParty() + partialScore.firstParty);
                score.setScoreSecondParty(score.getScoreSecondParty()+ partialScore.secondParty);
            } else {
                if(partialScore.firstParty < partialScore.secondParty){
                    score.setScoreSecondParty(score.getScoreSecondParty() + 1);
                } else {
                    score.setScoreFirstParty(score.getScoreFirstParty() + 1);
                }
            }
        }
    }
    
    private static class Volleyball extends Sport{
        @Override
        public Winner getRegularTimeWinner(Score score) {
            return getWinner(score);
        }

        @Override
        public String toString() {
            return VOLLEYBALL_STRING;
        }

        @Override
        void updateScore(PartialScore partialScore, Score score) {
            if(partialScore.firstParty < partialScore.secondParty){
                score.setScoreSecondParty(score.getScoreSecondParty() + 1);
            } else {
                score.setScoreFirstParty(score.getScoreFirstParty() + 1);
            }
        }
    }
    
    private static class Baseball extends Sport{
        @Override
        public Winner getRegularTimeWinner(Score score) {
            return getWinner(score);
        }

        @Override
        public String toString() {
            return BASEBALL_STRING;
        }
        
        @Override
        void updateScore(PartialScore partialScore, Score score) {
            score.setScoreFirstParty(score.getScoreFirstParty() + partialScore.firstParty);
            score.setScoreSecondParty(score.getScoreSecondParty()+ partialScore.secondParty);
        }
    }
}
