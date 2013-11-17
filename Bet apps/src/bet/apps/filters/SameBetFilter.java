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
public class SameBetFilter extends AbstractBetFilter{
    
    private final String name;

    public SameBetFilter(String name) {
        this(name,0,0);
    }

    public SameBetFilter(String name, double lowerThreshold, double upperThreshold) {
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
        return  bet1 == match.getBet2() && bet1 > lowerThreshold && bet1 <= upperThreshold;
    }

    public String getName() {
        return name;
    }
}
