
package chorke.proprietary.bet.apps;

import chorke.proprietary.bet.apps.core.httpparsing.BetexplorerComMultithreadParser;
import chorke.proprietary.bet.apps.gui.GuiUtils;
import chorke.proprietary.bet.apps.gui.Season;
import chorke.proprietary.bet.apps.gui.panels.MainPanel;
import chorke.proprietary.bet.apps.io.DBBetIOManager;
import chorke.proprietary.bet.apps.users.User;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;



/**
 * Spúšťa aplikáciu.
 * 
 * @author Chorke
 */
public class MainClass implements Runnable{
    
    public static void main(String... args){
        SwingUtilities.invokeLater(new MainClass());
    }
    
    @Override
    public void run(){
        DBBetIOManager man = new DBBetIOManager(StaticConstants.DATA_SOURCE);
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
    }
}
