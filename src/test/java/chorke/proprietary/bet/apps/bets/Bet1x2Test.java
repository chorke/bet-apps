package chorke.proprietary.bet.apps.bets;

import chorke.proprietary.bet.apps.core.bets.Bet1x2;
import static chorke.proprietary.bet.apps.BasicTests.assertEqualsBigDecimal;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.Before;

/**
 *
 * @author Chorke
 */
public class Bet1x2Test {
    
    private Bet1x2 bet;
    private Bet1x2 betGuest;
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
        betGuest = new Bet1x2("myCom", BET_2, BET_X, BET_1);
    }
    
    @Test
    public void getHomeBetTest(){
        assertEqualsBigDecimal(bet.getHomeBet(), BET_1);
        assertEqualsBigDecimal(betSame.getHomeBet(), BET_1_SAME);
        assertEqualsBigDecimal(betGuest.getHomeBet(), BET_2);
    }
    
    @Test
    public void getGuestBetTest(){
        assertEqualsBigDecimal(bet.getGuestBet(), BET_2);
        assertEqualsBigDecimal(betSame.getGuestBet(), BET_2_SAME);
        assertEqualsBigDecimal(betGuest.getGuestBet(), BET_1);
    }
    
    @Test
    public void getFavoritBetTest(){
        assertEqualsBigDecimal(bet.getFavoritBet(), BET_2);
        assertEqualsBigDecimal(betSame.getFavoritBet(), BigDecimal.ZERO);
        assertEqualsBigDecimal(betGuest.getFavoritBet(), BET_2);
    }
    
    @Test
    public void getLooserBetTest(){
        assertEqualsBigDecimal(bet.getLooserBet(), BET_1);
        assertEqualsBigDecimal(betSame.getLooserBet(), BigDecimal.ZERO);
        assertEqualsBigDecimal(betGuest.getLooserBet(), BET_1);
    }
    
    @Test
    public void getTieBetTest(){
        assertEqualsBigDecimal(bet.getTieBet(), BET_X);
        assertEqualsBigDecimal(betSame.getTieBet(), BET_X_SAME);
        assertEqualsBigDecimal(betGuest.getTieBet(), BET_X);
    }
}