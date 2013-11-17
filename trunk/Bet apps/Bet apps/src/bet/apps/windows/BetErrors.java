
package bet.apps.windows;

import bet.apps.BetApps;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Chorke
 */
public class BetErrors extends JFrame{
    
    public BetErrors(){
        init();
    }
    
    private void init(){
        
        JTextArea text = new JTextArea(BetApps.fileErrors);
        text.setEditable(false);
        JScrollPane pane = new JScrollPane(text);
        add(pane);
        
        setSize(500,300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle(BetApps.LABLES.getString("filesErrors"));
    }
}
