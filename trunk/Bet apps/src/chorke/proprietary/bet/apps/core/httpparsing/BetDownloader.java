/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.httpparsing;

import chorke.proprietary.bet.apps.StaticConstants.Sport;
import chorke.proprietary.bet.apps.core.match.Match;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Chorke
 */
public class BetDownloader {
    
    private final Object LOCK_Parser_Freedom = new Object();
    private final Object LOCK_Output_Matches = new Object();
    
    private final int numberOfParsers = 10; 
    
    private final HTMLBetParser[] parsers = new HTMLBetParser[numberOfParsers];
    private final boolean[] parsersFreedom = new boolean[numberOfParsers];
    private final List<Tuple> needToDownload = new LinkedList<>();
    private final List<Match> outpuMatches = new LinkedList<>();
    
    private Calendar startDate;
    private Calendar endDate;
    
    public BetDownloader() {
        for(int i = 0; i < numberOfParsers; i++){
            parsers[i] = new BetexplorerComParser(null);
        }
        startDate = new GregorianCalendar();
        endDate = new GregorianCalendar();
    }
    
    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate.setTimeInMillis(startDate.getTimeInMillis());
        this.startDate.set(Calendar.MILLISECOND, 1);
        this.startDate.set(Calendar.SECOND, 0);
        this.startDate.set(Calendar.MINUTE, 0);
        this.startDate.set(Calendar.HOUR_OF_DAY, 0);
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate.setTimeInMillis(endDate.getTimeInMillis());
        this.endDate.set(Calendar.MILLISECOND, 1);
        this.endDate.set(Calendar.SECOND, 0);
        this.endDate.set(Calendar.MINUTE, 0);
        this.endDate.set(Calendar.HOUR_OF_DAY, 0);
    }
    
    public Collection<Match> getMatches(){
        prepareListToDownload();
        Arrays.fill(parsersFreedom, true);
        ListIterator iter = needToDownload.listIterator();
        int freeParser;
        Tuple toParse;
        while(iter.hasNext()){
            freeParser = getFreeParser();
            if(freeParser >= 0){
                parsersFreedom[freeParser] = false;
                toParse = (Tuple)iter.next();
                SeparateThreadDownloader std = new SeparateThreadDownloader(
                        freeParser, toParse.date, toParse.sport);
                Thread t = new Thread(std);
                t.start();
            } else {
                try {
                    Thread.sleep(30);
                } catch (InterruptedException ex) {}
            }
        }
        return outpuMatches;
    }
    
    private void addOutputMatches(Collection<Match> m){
        synchronized(LOCK_Output_Matches){
            outpuMatches.addAll(m);
            //TODO - save keď je viac záznamov v outputMatches ako stanovená hranica
        }
    }
    
    private int getFreeParser(){
        int free = -1;
        synchronized(LOCK_Parser_Freedom){
            for(int i = 0; i < numberOfParsers; i++){
                if(parsersFreedom[i]){ 
                    free = i;
                    break;
                }
            }
        }
        return free;
    }
    
    private void prepareListToDownload(){
        Calendar c;
        for(long i = startDate.getTimeInMillis(); 
                i <= endDate.getTimeInMillis();
                i += 86_400_000){
            c = new GregorianCalendar();
            c.setTimeInMillis(i);
            needToDownload.add(new Tuple(c, Sport.Soccer));
            needToDownload.add(new Tuple(c, Sport.Baseball));
            needToDownload.add(new Tuple(c, Sport.Basketball));
            needToDownload.add(new Tuple(c, Sport.Handball));
            needToDownload.add(new Tuple(c, Sport.Hockey));
            needToDownload.add(new Tuple(c, Sport.Volleyball));
        }
    }
    
    private class SeparateThreadDownloader implements Runnable{
        
        private int freeParser;
        private Calendar date;
        private Sport sport;

        public SeparateThreadDownloader(int freeParser, Calendar date, Sport sport) {
            this.freeParser = freeParser;
            this.date = date;
            this.sport = sport;
        }

        @Override
        public void run() {
            parsers[freeParser].setExploredSport(sport);
            addOutputMatches(parsers[freeParser].getMatchesOfDay(date));
            synchronized(LOCK_Parser_Freedom){
                parsersFreedom[freeParser] = true;
            }
        }
    }
    
    private class Tuple {
        private final Calendar date;
        private final Sport sport;

        public Tuple(Calendar date, Sport sport) {
            this.date = date;
            this.sport = sport;
        }
    }
    
}
