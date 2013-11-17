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
public class SameBetStatisticMiner implements StatisticMiner{

    @Override
    public double getResultByBetingOnFavorit(Match match) {
        return 0.0;
    }

    @Override
    public double getResultByBetingOnLoser(Match match) {
        return 0.0;
    }

    @Override
    public double getResultByBetingOnHome(Match match) {
        if(match.getBet1() == match.getBet2() && match.getWinner() == 1){
            return match.getBet1();
        }
        return 0.0;
    }

    @Override
    public double getResultByBetingOnGuest(Match match) {
        if(match.getBet1() == match.getBet2() && match.getWinner() == 2){
            return match.getBet2();
        }
        return 0.0;
    }

    @Override
    public double getResultByBetingOnTie(Match match) {
        if(match.getBet1() == match.getBet2() && match.getWinner() == 0){
            return match.getBet0();
        }
        return 0.0;
    }

    @Override
    public double getBetOnFavorit(Match match) {
        return 0.0;
    }

    @Override
    public boolean isFavoritWinner(Match match) {
        return false;
    }

    @Override
    public boolean isLoserWinner(Match match) {
        return false;
    }
    
}
