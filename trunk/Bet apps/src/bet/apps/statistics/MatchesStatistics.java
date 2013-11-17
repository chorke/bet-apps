package bet.apps.statistics;

import bet.apps.BetApps;
import bet.apps.core.BetErrorException;
import bet.apps.core.DateMiner;
import bet.apps.core.Match;
import bet.apps.filters.AbstractBetFilter;
import bet.apps.filters.ClearFavoritBetFilter;
import bet.apps.filters.Filter;
import bet.apps.windows.MainWin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MatchesStatistics {
    
    private List<Match> matches;
    private int numOfFields;
    private int numOfStats;
    private int[][] numOfMatchesInStats;
    private double[][] sumOfWins;
    private int[] numOfMatchesInFields;
    private DateMiner weekCalculator;
    private StatisticMiner statMiner;
    private AbstractBetFilter betFilter;

    public MatchesStatistics(){
        this.matches = new ArrayList<>();
        this.weekCalculator = MainWin.DATE_MINER;
        this.statMiner = new ClearFavoritStatisticMiner();
        this.betFilter = new ClearFavoritBetFilter("thresholds filter");
//        this.statMiner = new SameBetStatisticMiner();
//        this.betFilter = new SameBetFilter("thresholds filter");
    }
    
    public void makeStats(double[] bets, int numberOfStats){
        if(bets != null && bets.length > 1){
            if(numberOfStats < 5){
                numOfStats = 5;
            } else {
                numOfStats = numberOfStats;
            }
            this.numOfFields = bets.length-1;
            numOfMatchesInStats = new int[numOfFields][numOfStats];
            numOfMatchesInFields = new int[numOfFields];
            /*  0 - výhra favorit 
                1 - remíza
                2 - prehra favorit  
                3 - výhra domáci
                4 - výhra hosť  */
            sumOfWins = new double[numOfFields][numOfStats]; 
//            Iterator iter = matches.iterator();
//            while(iter.hasNext()){
            for(Match m : matches){
//                Match m = (Match)iter.next();
                int j = 0;
                while(j < numOfFields){
                    betFilter.setLowerThreshold(bets[j]);
                    betFilter.setUpperThreshold(bets[j+1]);
                    if(betFilter.passFilter(m)){
                        sumOfWins[j][0] += statMiner.getResultByBetingOnFavorit(m);
                        sumOfWins[j][1] += statMiner.getResultByBetingOnTie(m);
                        sumOfWins[j][2] += statMiner.getResultByBetingOnLoser(m);
                        sumOfWins[j][3] += statMiner.getResultByBetingOnHome(m);
                        sumOfWins[j][4] += statMiner.getResultByBetingOnGuest(m);
                        if(m.getWinner() == 1){
                            numOfMatchesInStats[j][3]++;
                        } else if(m.getWinner() == 2){
                            numOfMatchesInStats[j][4]++;
                        } else if(m.getWinner() == 0){
                            numOfMatchesInStats[j][1]++;
                        }
                        if(statMiner.isFavoritWinner(m)){
                            numOfMatchesInStats[j][0]++;
                        } else if(statMiner.isLoserWinner(m)){
                            numOfMatchesInStats[j][2]++;
                        }
                        numOfMatchesInFields[j]++;
                    }
                    j++;
                }
            }
        }
    }
    
    public void applyFilters(Collection<? extends Filter> filters){
        applyFilters(filters, 0);
    }
    
    public void applyFilters(Collection<? extends Filter> filters, int startIndex){
        if(filters != null && !filters.isEmpty()){
            List<Match> newMatches = new ArrayList<>(matches.subList(0, startIndex));
            for(int i = startIndex; i < matches.size(); i++){
                boolean pass = true;
                for(Filter f : filters){
                    pass = pass && f.passFilter(matches.get(i));
                }
                if(pass){
                    newMatches.add(matches.get(i));
                }
            }
            matches = newMatches;
        }
    }
    
    public final void addMatch(String match, String date) throws BetErrorException{
        String[] parse = match.split("[\t, ]+");
        parse = onlyNonEmpty(parse);
        try{
            if(parse.length == 4){
                double bet0 = Double.parseDouble(parse[1]);
                double bet1 = Double.parseDouble(parse[0]);
                double bet2 = Double.parseDouble(parse[2]);
                int winner = Integer.parseInt(parse[3]);
                matches.add(new Match(bet0, bet1, bet2, winner, weekCalculator.getDate(date)));
            } else if(parse.length == 3){
                double bet1 = Double.parseDouble(parse[0]);
                double bet2 = Double.parseDouble(parse[1]);
                int winner = Integer.parseInt(parse[2]);
                matches.add(new Match(1, bet1, bet2, winner, weekCalculator.getDate(date)));
            } else if(!match.trim().isEmpty()){
                throw new BetErrorException(BetApps.MESSAGES.getString("shortLongLine"));
            }
        } catch (NumberFormatException e){
            throw new BetErrorException(BetApps.MESSAGES.getString("shortLongLine") + 
                    ":   " + e.getMessage());
        }
    }
    
    private String[] onlyNonEmpty(String[] str){
        int j = 0;
        int i = 0;
        String[] out = new String[str.length];
        while(i < str.length){
            if(!str[i].isEmpty()){
                out[j] = str[i];
                j++;
            }
            i++;
        }
        if(i != j){
            return Arrays.copyOfRange(out, 0, j);
        }
        return out;
    }
    
    public final void addMatch(Match match){
        matches.add(match);
    }
    
    public final void addMatch(List<? extends Match> toAdd){
        this.matches.addAll(toAdd);
    }
    
    public double getYield(int i, int j){
        double output;
        try{
            output = Math.round(((sumOfWins[i][j] - numOfMatchesInFields[i])
                        / (double)numOfMatchesInFields[i])*10000.0)
                        / 100.0;
        } catch (IndexOutOfBoundsException | NullPointerException ex){
            output = 0.0;
        }
        return output;
    }

    public int getNumOfMatchesInFieldsAt(int i) {
        int output;
        try{
            output = numOfMatchesInFields[i];
        } catch (IndexOutOfBoundsException | NullPointerException ex){
            output = 0;
        }
        return output;
    }

    public int getNumOfMatchesInStatsAt(int i, int j) {
        int output;
        try{
            output = numOfMatchesInStats[i][j];
        } catch (IndexOutOfBoundsException | NullPointerException ex){
            output = 0;
        }
        return output;
    }

    public final int getNumberOfMatches(){
        return this.matches.size();
    }
    
    public final void clearMatches(){
        this.matches.clear();
    }

    public List<Match> getMatches() {
        return matches;
    }

    public int getNumOfFields() {
        return numOfFields;
    }

    public int getNumOfStats() {
        return numOfStats;
    }

    public StatisticMiner getStatMiner() {
        return statMiner;
    }

    public void setStatMiner(StatisticMiner statMiner) {
        this.statMiner = statMiner;
    }
    
    @Override
    public String toString(){
        String out = "";
//        Iterator iter = matches.iterator();
//        while(iter.hasNext()){
//            Match m = (Match)iter.next();
//            out += m.toString() + BetApps.LINE_SEPARATOR;
//        }
        for(Match m : matches){
            out += m.toString() + BetApps.LINE_SEPARATOR;
        }
        return "Matches stats" + BetApps.LINE_SEPARATOR + out;
    }
}