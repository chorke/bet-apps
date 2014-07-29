
package chorke.proprietary.bet.apps.matches;

import chorke.proprietary.bet.apps.MatchInitializer;
import chorke.proprietary.bet.apps.core.CoreUtils.Winner;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Chorke
 */
public class MatchTest extends MatchInitializer {
    
    @Test
    public void winnerTest(){
        assertEquals(m1_24_12_2013_Hoc.getWinner(), Winner.Team1);
        assertEquals(m2_31_12_2013_Hoc.getWinner(), Winner.Team2);
        assertEquals(m2_2_1_2014_Soc.getWinner(), Winner.Team2);
        assertEquals(m1_29_11_2013_Soc.getWinner(), Winner.Team1);
        assertEquals(mX_1_1_2014_Hoc.getWinner(), Winner.Team1);
        assertEquals(mX_28_12_2013_Soc.getWinner(), Winner.Tie);
    }
    
    @Test
    public void regularTimeWinnerTest(){
        assertEquals(m1_24_12_2013_Hoc.getRegularTimeWinner(), Winner.Team1);
        assertEquals(m2_31_12_2013_Hoc.getRegularTimeWinner(), Winner.Team2);
        assertEquals(m2_2_1_2014_Soc.getRegularTimeWinner(), Winner.Team2);
        assertEquals(m1_29_11_2013_Soc.getRegularTimeWinner(), Winner.Team1);
        assertEquals(mX_1_1_2014_Hoc.getRegularTimeWinner(), Winner.Tie);
        assertEquals(mX_28_12_2013_Soc.getRegularTimeWinner(), Winner.Tie);
    }
}