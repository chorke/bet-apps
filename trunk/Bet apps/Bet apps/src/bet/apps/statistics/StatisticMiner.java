/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bet.apps.statistics;

import bet.apps.core.Match;

/**
 *
 * @author Chorke
 */
public interface StatisticMiner {
    
    public double getResultByBetingOnFavorit(Match match);
    
    public double getResultByBetingOnLoser(Match match);
    
    public double getResultByBetingOnHome(Match match);
    
    public double getResultByBetingOnGuest(Match match);
    
    public double getResultByBetingOnTie(Match match);
    
    public double getBetOnFavorit(Match match);
    
    public boolean isFavoritWinner(Match match);
    
    public boolean isLoserWinner(Match match);
}
