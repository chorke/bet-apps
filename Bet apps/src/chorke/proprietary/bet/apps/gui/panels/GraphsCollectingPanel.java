
package chorke.proprietary.bet.apps.gui.panels;

import chorke.proprietary.bet.apps.core.calculators.YieldCalculator;
import javax.swing.JPanel;

/**
 * Vytvorí panel so grafmi všetkých možností a podľa všetkých rozsahov.
 * Grafy budú zahŕňať všetky možné obdobia.
 * 
 * @author Chorke
 */
public class GraphsCollectingPanel extends JPanel{
    
    private YieldCalculator calculator;

    public GraphsCollectingPanel(YieldCalculator calculator) {
        this.calculator = calculator;
    }
    
    
}
