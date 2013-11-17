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
public class GeneralStatisticMiner implements StatisticMiner {

    @Override
    public double getResultByBetingOnFavorit(Match match) {
        int winner = match.getWinner();
        double bet1 = match.getBet1();
        double bet2 = match.getBet2();
        if(winner == 1 && bet1 < bet2){
            return bet1;
        } else if (winner == 2 && bet2 < bet1){
            return bet2;
        } else {
            return 0.0;
        }
    }

    @Override
    public double getResultByBetingOnLoser(Match match) {
        int winner = match.getWinner();
        double bet1 = match.getBet1();
        double bet2 = match.getBet2();
        if(winner == 2 && bet1 < bet2){
            return bet2;
        } else if (winner == 1 && bet2 < bet1){
            return bet1;
        } else {
            return 0.0;
        }
    }

    @Override
    public double getResultByBetingOnHome(Match match) {
        if(match.getWinner() == 1){
            return match.getBet1();
        }
        return 0.0;
    }

    @Override
    public double getResultByBetingOnGuest(Match match) {
        if(match.getWinner() == 2){
            return match.getBet2();
        }
        return 0.0;
    }

    @Override
    public double getResultByBetingOnTie(Match match) {
        if(match.getWinner() == 0){
            return match.getBet0();
        }
        return 0.0;
    }

    @Override
    public double getBetOnFavorit(Match match) {
        double bet1 = match.getBet1();
        double bet2 = match.getBet2();
        if(bet1 < bet2){
            return bet1;
        } else if(bet2 < bet1){
            return bet2;
        } else {
            return 0.0;
        }
    }

    @Override
    public boolean isFavoritWinner(Match match) {
        int winner = match.getWinner();
        double bet1 = match.getBet1();
        double bet2 = match.getBet2();
        return (winner == 1 && bet1 < bet2) || (winner == 2 && bet2 < bet1);
    }

    @Override
    public boolean isLoserWinner(Match match) {
        int winner = match.getWinner();
        double bet1 = match.getBet1();
        double bet2 = match.getBet2();
        return (winner == 2 && bet1 < bet2) || (winner == 1 && bet2 < bet1);
    }
    
}
