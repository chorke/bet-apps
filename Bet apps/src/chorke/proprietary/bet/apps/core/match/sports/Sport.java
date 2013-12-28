
package chorke.proprietary.bet.apps.core.match.sports;

import chorke.proprietary.bet.apps.StaticConstants.Winner;
import chorke.proprietary.bet.apps.core.httpparsing.HTMLBetParser.BettingSports;
import chorke.proprietary.bet.apps.core.match.score.Score;

/**
 *
 * @author Chorke
 */
public abstract class Sport {
    
    private static final String SOCCER_STRING = "soccer";
    private static final String HOCKEY_STRING = "hockey";
    private static final String BASKETBALL_STRING = "basketball";
    private static final String HANDBALL_STRING = "handball";
    private static final String VOLLEYBALL_STRING = "volleyball";
    private static final String BASEBALL_STRING = "baseball";
    
    public Winner getWinner(Score score){
        if(score.getScoreFirstParty() > score.getScoreSecondParty()){
            return Winner.Team1;
        } else if(score.getScoreSecondParty() > score.getScoreFirstParty()){
            return Winner.Team2;
        }
        return Winner.Tie;
    }
    
    public abstract Winner getRegularTimeWinner(Score score);
    
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
    }
}
