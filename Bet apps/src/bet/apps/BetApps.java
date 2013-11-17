package bet.apps;

import bet.apps.windows.MainWin;
import chorke.proprietary.bet.apps.MainClass;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;

public class BetApps {

    public static final String LINE_SEPARATOR = System.lineSeparator();
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final Locale LOCALE = new Locale("sk-SK");
    public static final ResourceBundle LABLES = ResourceBundle.getBundle("bet.apps.properties.Labels",
            LOCALE);
    public static final ResourceBundle MESSAGES = ResourceBundle.getBundle("bet.apps.properties.Messages",
            LOCALE);
    public static String fileErrors = LABLES.getString("filesErrors") + ":" + LINE_SEPARATOR;
    
    public static void main(String[] args) {
        MainClass mc = new MainClass();
        mc.start();
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                MainWin mainWindows = new MainWin();
//                mainWindows.setVisible(true);
//            }
//        });
    }
}
