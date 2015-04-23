
package chorke.bet.apps;

import chorke.bet.apps.core.CoreUtils;
import chorke.bet.apps.core.httpparsing.BetexplorerComMultithreadParser;
import chorke.bet.apps.gui.GuiUtils;
import chorke.bet.apps.gui.Season;
import chorke.bet.apps.gui.panels.LoadingPanel;
import chorke.bet.apps.gui.panels.MainPanel;
import chorke.bet.apps.io.DBBetIOManager;
import chorke.bet.apps.users.User;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.Thread.UncaughtExceptionHandler;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spúšťa aplikáciu.
 * 
 * @author Chorke
 */
public class MainClass implements Runnable{
    
    /**
     * Logger pre main triedu. Zaznamenáva aj nezachytené výnimky.
     */
    private static final Logger log = LoggerFactory.getLogger(MainClass.class);
    
    public static void main(String... args){
        log.debug("Setting default uncought exception handler.");
        Thread.setDefaultUncaughtExceptionHandler(new DefaultUncoughtExceptionHandler());
        log.debug("Starting application.");
        SwingUtilities.invokeLater(new MainClass());
        log.debug("Application has been started.");
    }
    
    /**
     * Defaultné spracovanie nechytených výnimiek. Zaloguje ich a vypíše
     * stacktrace na štandardný chybový výstup.
     */
    private static class DefaultUncoughtExceptionHandler implements UncaughtExceptionHandler{

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.error("Error in thread " + t + ": uncought exception.", e);
            e.printStackTrace(System.err);
        }
    }
    
    @Override
    public void run(){
        DBBetIOManager man = new DBBetIOManager(CoreUtils.DATA_SOURCE);
        Season season = new Season();
        season.setManager(man);
        season.setParser(new BetexplorerComMultithreadParser(man));
        season.setUser(new User("default_user"));
        
        final MainPanel panel = new MainPanel(season);
        
        JScrollPane pane = new JScrollPane(panel);
        JFrame f = GuiUtils.getDefaultFrame(null, JFrame.DO_NOTHING_ON_CLOSE,
                true, null, pane);
        f.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                panel.destroy();
                System.exit(0);
            }
        });
        f.setVisible(true);
//        testMethod(season);
    }
    
    private void testMethod(Season season){
//        DeletePanel panel = new DeletePanel(season);
        LoadingPanel panel = new LoadingPanel(season);
        JFrame f = GuiUtils.getDefaultFrame("Test", JFrame.DISPOSE_ON_CLOSE, false, null,
                panel);
        f.setVisible(true);
    }
}
