
package chorke.bet.apps.gui;

import chorke.bet.apps.core.bets.Bet;
import chorke.bet.apps.core.bets.Bet1x2;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ResourceBundle;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.ToolTipManager;

/**
 * Trieda s často používanými metódami v aplikácii v GUI časti.
 * @author Chorke
 */
public class GuiUtils {

    public static final Class<? extends Bet>[] SUPPORTED_BET_TYPES = initSupportedTypes();
    
    
    private static Class<? extends  Bet>[] initSupportedTypes() {
        @SuppressWarnings("unchecked")
        Class<? extends Bet>[] bets = (Class<? extends Bet>[])Array.newInstance(Class.class, 1);
        bets[0] = Bet1x2.class;
        return bets;
    }
    
    private GuiUtils(){}
        
    /**
     * Defaultná ikonka aplikácie pre okná.
     */
    private static Image defaultAppIcon = getIconImage();
    
    /**
     * Vráti defaultnú ikonku pre aplikáciu.
     * @return 
     */
    private static Image getIconImage(){
//        try{
//            return ImageIO.read(GuiUtils.class.getResource(
//                    "/chorke/proprietary/bet/apps/gui/icons/icon.png"));
//        } catch (IOException | IllegalArgumentException ex){
//            return null;
//        }
        return new JFrame().getIconImage();
    }
    
    /**
     * Vytvorí z buttons jednu ButtonGroup.
     * @param buttons 
     */
    public static void mergeToOneButtonGroup(JRadioButton... buttons){
        ButtonGroup group = new ButtonGroup();
        for(JRadioButton b : buttons){
            group.add(b);
        }
    }
    
    /**
     * Vráti okno aplikácie. Metóda predpokladá, že okno má práve jednu 
     * komponentu (je možné všetko dať na jeden JPanel). Zavolá 
     * {@link JFrame#pack()} po pridaní komponenty component. Ak je 
     * komponenta zmenená, bude možno potreba znova zavolať metódu.
     * Rovnako tak {@link JFrame#setLocationRelativeTo(java.awt.Component)},
     * ktorá je volaná po {@link JFrame#pack()}.
     * 
     * @param title titulok okna, null pre prázdny titulok.
     * @param defaultCloseOperation defaultná operácia pri zatvorení
     * @param resizable možnosť zmeniť veľkosť okna
     * @param locationRelativComp komponenta, vzhľadum ku ktorej bude 
     *      vrátené okno umiestnené
     * @param component komponenta, ktorú má obsahovať okno, ak je null, nepridá
     *      sa nič
     * @return 
     */
    public static  JFrame getDefaultFrame(String title, int defaultCloseOperation,
            boolean resizable, Component locationRelativComp, Component component){
        JFrame f = new JFrame(title == null ? "" : title);
        f.setIconImage(defaultAppIcon);
        f.setDefaultCloseOperation(defaultCloseOperation);
        f.setResizable(resizable);
        if(component != null){
            f.add(component);
        }
        f.pack();
        f.setLocationRelativeTo(locationRelativComp);
        return f;
    }
    
    /**
     * Vytvorí novú inštanciu radio button s textom text a ActionListener
     * listener.
     * @param text
     * @param listener 
     * @return 
     */
    public static JRadioButton getRadioButton(String text, ActionListener listener){
        JRadioButton butt = new JRadioButton(text);
        butt.addActionListener(listener);
        return butt;
    }
    
    /**
     * Vráti prvý index prvku v buttons, ktorý je zvolený. Ak nie je vybraný
     * žiaden prvok, je vrátená -1. Ak sú buttons v jednej ButtonsGroup, potom
     * je zvolený práve jeden index.
     * 
     * @param buttons 
     * @return 
     */
    public static int getSelectedIdx(JRadioButton[] buttons){
        for(int i = 0; i < buttons.length; i++){
            if(buttons[i].isSelected()){
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Nastaví tool tip text pre komponentu com a zaregistruje ju 
     * pri ToolTipManager.
     * 
     * @param com
     * @param toolTipText 
     */
    public static void addToolTipForComponent(JComponent com, String toolTipText){
        com.setToolTipText(toolTipText);
        ToolTipManager.sharedInstance().registerComponent(com);
    }
    
    /**
     * Vráti index, ktorý má predaná trieda c v poly {@link #SUPPORTED_BET_TYPES}.
     * Ak sa v poly nenachádza, je vrátená -1.
     * @param c
     * @return 
     */
    public static int getIndexOfClassInSupportedClasses(Class c){
        int i = 0;
        for(Class sup : SUPPORTED_BET_TYPES){
            if(c.equals(sup)){  return i; }
            i++;
        }
        return -1;
    }
    
    /**
     * Vypočíta potrebný počet riadkov pre počet elementov elemCount a 
     * pevný počet stĺpcov col.
     * 
     * @param col požadovaný počet stĺpcov
     * @param elemCount počet elementov
     * @return potrebný počet riadkov.
     */
    public static int getNumOfRows(int col, int elemCount){
        return elemCount % col == 0 ? elemCount / col : (elemCount / col) + 1;
    }
    
    /**
     * Zobrazí potvrdzovací dialóg, ktorý bude mať lokalizované hlášku, názov
     * a možnosti. 
     * 
     * @param bundle {@link ResourceBundle}, ktoré budú použité pre lokalizované texty
     * @param messageKey kľúč pre {@code bundle} pre správu
     * @param titleKey kľúč pre {@code bundle} pre názov
     * @param parentComponent rodičovská komponenta
     * @return podľa zvolenej možnosti to bude buď {@link JOptionPane#YES_OPTION},
     *          {@link JOptionPane#NO_OPTION},
     *          {@link JOptionPane#CANCEL_OPTION} alebo {@link JOptionPane#CLOSED_OPTION}
     *      ak užívateľ zatvoril dialóg.
     */
    public static int showLocalizedConfirmDialog(ResourceBundle bundle,
            String messageKey, String titleKey, Component parentComponent){
        Object[] options = {bundle.getString("yes"), bundle.getString("no"),
                            bundle.getString("cancel")};
        int option = JOptionPane.showOptionDialog(
                parentComponent,
                bundle.getString(messageKey),
                bundle.getString(titleKey),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]);
        if(option == 0){
            return JOptionPane.YES_OPTION;
        }
        if(option == 1){
            return JOptionPane.NO_OPTION;
        }
        if(option == 2){
            return JOptionPane.CANCEL_OPTION;
        }
        return option;
    }
}