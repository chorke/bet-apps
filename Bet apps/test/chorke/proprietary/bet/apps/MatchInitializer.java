
package chorke.proprietary.bet.apps;

import chorke.proprietary.bet.apps.core.CoreUtils.BettingSports;
import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.core.match.sports.Sport;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import org.junit.Before;

/**
 *
 * @author Chorke
 */
public abstract class MatchInitializer {
    
    protected Match m1_29_11_2013_Soc;
    protected Match m2_30_11_2013_Soc;
    
    protected Match m1_24_12_2013_Hoc;
    protected Match m1_26_12_2013_Hoc;
    protected Match mX_28_12_2013_Soc;
    
    protected Match m2_31_12_2013_Hoc;
    protected Match mX_1_1_2014_Hoc;
    protected Match m2_2_1_2014_Soc;
    protected Match m1_3_1_2014_Soc;
    
    protected List<Match> matches;
    
    
    @Before
    public void init(){
        mX_1_1_2014_Hoc = new Match(Sport.getSport(BettingSports.Hockey));
        m1_24_12_2013_Hoc = new Match(Sport.getSport(BettingSports.Hockey));
        m1_26_12_2013_Hoc = new Match(Sport.getSport(BettingSports.Hockey));
        mX_28_12_2013_Soc = new Match(Sport.getSport(BettingSports.Soccer));
        m1_29_11_2013_Soc = new Match(Sport.getSport(BettingSports.Soccer));
        m2_2_1_2014_Soc = new Match(Sport.getSport(BettingSports.Soccer));
        m2_30_11_2013_Soc = new Match(Sport.getSport(BettingSports.Soccer));
        m2_31_12_2013_Hoc = new Match(Sport.getSport(BettingSports.Hockey));
        m1_3_1_2014_Soc = new Match(Sport.getSport(BettingSports.Soccer));
        
        setDates();
        setScores();
        
        matches = new LinkedList<>();
        matches.add(m1_24_12_2013_Hoc);
        matches.add(m1_26_12_2013_Hoc);
        matches.add(m1_29_11_2013_Soc);
        matches.add(m1_3_1_2014_Soc);
        matches.add(m2_2_1_2014_Soc);
        matches.add(m2_2_1_2014_Soc);
        matches.add(m2_31_12_2013_Hoc);
        matches.add(mX_1_1_2014_Hoc);
        matches.add(mX_28_12_2013_Soc);
    }
    
    private void setDates(){
        mX_1_1_2014_Hoc.getProperties().setDate(new GregorianCalendar(2014, Calendar.JANUARY, 1));
        m1_24_12_2013_Hoc.getProperties().setDate(new GregorianCalendar(2013, Calendar.DECEMBER, 24));
        m1_26_12_2013_Hoc.getProperties().setDate(new GregorianCalendar(2013, Calendar.DECEMBER, 26));
        mX_28_12_2013_Soc.getProperties().setDate(new GregorianCalendar(2013, Calendar.DECEMBER, 28));
        m1_29_11_2013_Soc.getProperties().setDate(new GregorianCalendar(2013, Calendar.NOVEMBER, 29));
        m2_2_1_2014_Soc.getProperties().setDate(new GregorianCalendar(2014, Calendar.JANUARY, 2));
        m2_30_11_2013_Soc.getProperties().setDate(new GregorianCalendar(2013, Calendar.NOVEMBER, 30));
        m2_31_12_2013_Hoc.getProperties().setDate(new GregorianCalendar(2013, Calendar.DECEMBER, 31));
        m1_3_1_2014_Soc.getProperties().setDate(new GregorianCalendar(2014, Calendar.JANUARY, 3));
    }
    
    private void setScores(){
        m1_24_12_2013_Hoc.addPartialScore(1, 3);
        m1_24_12_2013_Hoc.addPartialScore(2, 0);
        m1_24_12_2013_Hoc.addPartialScore(1, 0);
        
        m1_26_12_2013_Hoc.addPartialScore(0, 0);
        m1_26_12_2013_Hoc.addPartialScore(2, 0);
        m1_26_12_2013_Hoc.addPartialScore(1, 1);
        
        m1_29_11_2013_Soc.addPartialScore(1, 0);
        m1_29_11_2013_Soc.addPartialScore(1, 0);
        
        m1_3_1_2014_Soc.addPartialScore(0, 0);
        m1_3_1_2014_Soc.addPartialScore(2, 1);
        
        m2_2_1_2014_Soc.addPartialScore(2, 1);
        m2_2_1_2014_Soc.addPartialScore(0, 2);
        
        m2_30_11_2013_Soc.addPartialScore(0, 1);
        m2_30_11_2013_Soc.addPartialScore(0, 1);
        
        m2_31_12_2013_Hoc.addPartialScore(1, 3);
        m2_31_12_2013_Hoc.addPartialScore(0, 0);
        m2_31_12_2013_Hoc.addPartialScore(1, 0);
        
        mX_1_1_2014_Hoc.addPartialScore(1, 0);
        mX_1_1_2014_Hoc.addPartialScore(1, 0);
        mX_1_1_2014_Hoc.addPartialScore(0, 2);
        mX_1_1_2014_Hoc.addPartialScore(1, 0);
        
        mX_28_12_2013_Soc.addPartialScore(1, 0);
        mX_28_12_2013_Soc.addPartialScore(0, 1);
    }
    
}