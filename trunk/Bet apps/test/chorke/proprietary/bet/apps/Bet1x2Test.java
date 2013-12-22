package chorke.proprietary.bet.apps;

import chorke.proprietary.bet.apps.core.bets.Bet1x2;
import java.math.BigDecimal;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Chorke
 */
public class Bet1x2Test {
    
    private Bet1x2 bet;
    private Bet1x2 betSame;
    private static final BigDecimal BET_1 = new BigDecimal("2.37");
    private static final BigDecimal BET_X = new BigDecimal("4.65");
    private static final BigDecimal BET_2 = new BigDecimal("1.98");
    
    private static final BigDecimal BET_1_SAME = new BigDecimal("2.11");
    private static final BigDecimal BET_X_SAME = new BigDecimal("4.57");
    private static final BigDecimal BET_2_SAME =  new BigDecimal("2.11");
    
    @Before
    public void init(){
        bet = new Bet1x2("myCom", BET_1, BET_X, BET_2);
        betSame = new Bet1x2("myCom", BET_1_SAME, BET_X_SAME, BET_2_SAME);
    }
    
    @Test
    public void getHomeBetTest(){
        assertEquals(bet.getHomeBet(), BET_1);
        assertEquals(betSame.getHomeBet(), BET_1_SAME);
    }
    
    @Test
    public void getGuestBetTest(){
        assertEquals(bet.getGuestBet(), BET_2);
        assertEquals(betSame.getGuestBet(), BET_2_SAME);
    }
    
    @Test
    public void getFavoritBetTest(){
        assertEquals(bet.getFavoritBet(), BET_2);
        assertEquals(betSame.getFavoritBet(), BigDecimal.ZERO);
    }
    
    @Test
    public void getLooserBetTest(){
        assertEquals(bet.getLooserBet(), BET_1);
        assertEquals(betSame.getLooserBet(), BigDecimal.ZERO);
    }
    
    @Test
    public void getTieBetTest(){
        assertEquals(bet.getTieBet(), BET_X);
        assertEquals(betSame.getTieBet(), BET_X_SAME);
    }
    
    private void assertEquals(BigDecimal b1, BigDecimal b2){
        if(b1.compareTo(b2) != 0){
            fail();
        }
    }
}