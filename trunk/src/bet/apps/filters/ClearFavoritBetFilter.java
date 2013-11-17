
package bet.apps.filters;

import bet.apps.core.Match;

/**
 * Represents filter, that determines, whether specific {@code Match} pass
 * according to bet need. It is considered {@code lowerThreshold}, 
 * {@code upperThreshold} and bet to favorit. If bet to favorit equals to
 * bet to loser, then {@link #passFilter(bet.apps.classes.Match)} return false.
 * 
 * @author Chorke
 */
public class ClearFavoritBetFilter extends AbstractBetFilter{

    private final String name;

    public ClearFavoritBetFilter(String name){
        this(name,0,0);
    }
    
    public ClearFavoritBetFilter(String name, double lowerThreshold, double upperThreshold){
        this.lowerThreshold = lowerThreshold;
        this.upperThreshold = upperThreshold;
        if(name != null){
            this.name = name;
        } else {
            this.name = "";
        }
    }
            
    @Override
    public boolean passFilter(Match match) {
        double bet1 = match.getBet1();
        double bet2 = match.getBet2();
        if(bet1 == bet2){
            return false;
        }
        double min = bet1;
        if(bet2 < min){
            min = bet2;
        }
        return min > lowerThreshold && min <= upperThreshold;
//        return  (
//                (match.getBet1() < match.getBet2() && match.getBet1() > lowerThreshold && match.getBet1() <= upperThreshold)
//                ||
//                (match.getBet1() > match.getBet2() && match.getBet2() > lowerThreshold && match.getBet2() <= upperThreshold)
//                );
    }

    public String getName() {
        return name;
    }
}
