/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bet.apps.filters;

import bet.apps.core.Match;

/**
 *
 * @author Chorke
 */
public class GeneralBetFilter extends AbstractBetFilter{
    
    private final String name;

    public GeneralBetFilter(String name) {
        this(name,0,0);
    }

    public GeneralBetFilter(String name, double lowerThreshold, double upperThreshold) {
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
            return bet1 > lowerThreshold && bet1 <= upperThreshold;
        }
        double min = bet1;
        if(bet2 < min){
            min = bet2;
        }
        return min > lowerThreshold && min <= upperThreshold;
    }

    public String getName() {
        return name;
    }
}
