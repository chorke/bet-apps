
package bet.apps.statistics;

import bet.apps.core.Graph;
import bet.apps.core.Match;
import bet.apps.core.PaintableGraph;
import bet.apps.filters.Filter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Chorke
 */
public abstract class AbstractPeriodicStatistics implements PeriodicStatistics{

    
    private Map<Integer, MatchesStatistics> periodicMatches;
    private double[] actualBets;
    private int actualNumOfStats;
    private boolean needMakeStats;
    private boolean needMakeGraphs;
    private List<List<Graph>> graphs;

    public AbstractPeriodicStatistics() {
        periodicMatches = new TreeMap<>();
        actualBets = new double[0];
        actualNumOfStats = 0;
        needMakeStats = true;
        needMakeGraphs = true;
    }
    
    /**
     * Method does nothing with parameter {@code matches}, but sets some inner
     * boolean private fields for good performance. Hence every class extending this
     * class should call {@code super.split(matches)}. Otherwise proper function is
     * not guaranteed.
     * 
     * @param matches {@docRoot}
     */
    @Override
    public void split(Collection<Match> matches) {
        needMakeStats = true;
        needMakeGraphs = true;
    }

    @Override
    public void clearMatches() {
        periodicMatches.clear();
        needMakeStats = true;
        needMakeGraphs = true;
    }

    @Override
    public void makePeriodicStatistics(double[] bets, int numberOfStats) {
        checkIfNeedMakeStats(bets, numberOfStats);
        if(needMakeStats){
            for(Integer i : periodicMatches.keySet()){
                periodicMatches.get(i).makeStats(bets, numberOfStats);
            }
            needMakeStats = false;
        }
    }
    
    private void checkIfNeedMakeStats(double[] bets, int numberOfStats){
        if(!needMakeStats){
            if(numberOfStats != actualNumOfStats){
                needMakeStats = true;
            } else if(bets == null){
                needMakeStats = false;
            } else if(bets.length != actualBets.length){
                needMakeStats = true;
            } else {
                for(int i = 0; i < bets.length; i++){
                    needMakeStats = needMakeStats || (bets[i] != actualBets[i]);
                }
            }
        }
    }
    
    @Override
    public List<List<Graph>> getGraphs(double[] bets, int numberOfStats){
        makeGraphs(bets, numberOfStats);
        return graphs;
    }
    
    private void makeGraphs(double[] bets, int numberOfStats){
        makePeriodicStatistics(bets, numberOfStats);
        checkIfNeedMakeGraphs();
        if(needMakeGraphs){
            List<List<Graph>> out = new ArrayList<>(bets.length - 1);
            for(int fields = 0; fields < bets.length - 1; fields++){
                ArrayList<Graph> list = new ArrayList<>(numberOfStats);
                for(int stats = 0; stats < numberOfStats; stats++){
                    Graph g = new Graph();
                    for(Integer i : periodicMatches.keySet()){
                        g.addValue(periodicMatches.get(i).getYield(fields, stats));
                    }
                    list.add(g);
                }
                out.add(list);
            }
            graphs = out;
        }
        needMakeGraphs = false;
    }
    
    private void checkIfNeedMakeGraphs(){
        if(needMakeStats){
            needMakeGraphs = true;
        } else if(graphs == null){
            needMakeGraphs = true;
        }
    }

    @Override
    public List<List<PaintableGraph>> getPaintableGraphs(double[] bets, int numberOfStats){
        makeGraphs(bets, numberOfStats);
        int fields = graphs.size();
        List<List<PaintableGraph>> out = new ArrayList<>(fields);
        for(int i = 0; i < fields; i++){
            int stats = graphs.get(i).size();
            List<PaintableGraph> list = new ArrayList<>(stats);
            for(int j = 0; j < stats; j++){
                PaintableGraph pg = new PaintableGraph();
                pg.setGraph(graphs.get(i).get(j));
                list.add(pg);
            }
            out.add(list);
        }
        return out;
    }

    @Override
    public double getYield(int periodIndex, int field, int stats) {
        double output = 0.0;
        if(periodicMatches.containsKey(periodIndex)){
            output = periodicMatches.get(periodIndex).getYield(field, stats);
        }
        return output;
    }

    @Override
    public int getNumOfMatchesInFieldForPeriod(int periodIndex, int field) {
        int output = 0;
        if(periodicMatches.containsKey(periodIndex)){
            output = periodicMatches.get(periodIndex).getNumOfMatchesInFieldsAt(field);
        }
        return output;
    }

    @Override
    public int getNumOfMatchesInStastForPeriod(int periodIndex, int field, int stats) {
        int output = 0;
        if(periodicMatches.containsKey(periodIndex)){
            output = periodicMatches.get(periodIndex).getNumOfMatchesInStatsAt(field, stats);
        }
        return output;
    }

    @Override
    public void applyFilters(Collection<? extends Filter> filters) {
        applyFilters(filters, 0);
    }

    @Override
    public void applyFilters(Collection<? extends Filter> filters, int startSize) {
        for(Integer i : periodicMatches.keySet()){
            periodicMatches.get(i).applyFilters(filters, startSize);
        }
    }
    
    public Map<Integer, MatchesStatistics> getPeriodicMatches() {
        return periodicMatches;
    }

    public void setNeedMakeStats(boolean needMakeStats) {
        this.needMakeStats = needMakeStats;
    }

    public void setNeedMakeGraphs(boolean needMakeGraphs) {
        this.needMakeGraphs = needMakeGraphs;
    }
}
