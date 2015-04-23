
package chorke.proprietary.bet.apps.gui;

import chorke.proprietary.bet.apps.core.bets.Bet;
import chorke.proprietary.bet.apps.core.bets.Bet1x2;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Array;
import java.util.ResourceBundle;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ToolTipManager;
import javax.swing.border.EtchedBorder;

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
     * Čakací dialóg.
     */
    private static JDialog waitingDialog;
    /**
     * Správa vo waitingDialog.
     */
    private static JLabel message;
    
    /**
     * Defaultná ikonka aplikácie pre okná.
     */
    private static Image defaultAppIcon = getIconImage();
    
    /**
     * Objekt pre čakanie, kým sa zobrazí waiting dialog.
     */
    private static final Object WAITING_DIALOG_OBJECT = new Object();
    
    /**
     * Udáva, či je potreba čakať, kým sa zobrazí čakacie okno. Resp. či okno
     * už je zobrazené (aktívne), alebo nie.
     */
    private static volatile boolean waitUntilShowed;
    
    /**
     * Zobrazí čakací dialóg so správou waitingMessage. Počas jeho viditeľnosti
     * nie je možné pracovať s aplikáciou. Ak je už zobrazený, nestane sa nič.
     * Dialóg spustí v novom vlákne. S ostatnými gui časťami teda nie je možné 
     * pracovať, ale samotné volajúce vlákno môže pokračovať vo svojej práci.
     * Volajúce vlákno je počas zviditeľnovania čakacieho dialógu uspané a čaká,
     * pokým nie je dialóg úspešne aktivovaný. Potom je jeho činnosť obnovená.
     * Metóda by nemala byť volaná z vlákna EDT (vlákno pre správu udalostí a.i.,
     * teda z metód ActionListnerov, WindowListnerov atď.) inak hrozí zamrznutie.
     * 
     * @param waitingMessage 
     */
    public static void showWaitingDialog(String waitingMessage, JButton... buttons){
//        if(waitingDialog == null){
//            initWaitingDialog();
//        } else if(waitingDialog.isVisible()){
//            return;
//        }
        if(waitingDialog != null && waitingDialog.isActive()){
            return;
        }
        initWaitingDialog(buttons);
        message.setText(waitingMessage);
//        waitingDialog.setResizable(true);
        waitingDialog.pack();
        waitingDialog.setLocationRelativeTo(null);
        waitUntilShowed = true;
        new Thread(new DialogThread()).start();
        synchronized(WAITING_DIALOG_OBJECT){
            try{
                while(waitUntilShowed){
                    WAITING_DIALOG_OBJECT.wait();
                }
            } catch (InterruptedException ex){
                System.err.println(ex);
            }
        }
    }
    
    /**
     * Skryje aktuálne zobrazený čakací dialóg. Ak nie je dialóg
     * viditeľný, nestane sa nič.
     */
    public static void hideWaitingDialog(){
        if(waitingDialog != null && waitingDialog.isVisible()){
            waitingDialog.dispose();
            waitingDialog = null;
        }
    }
    
    /**
     * Aktualizuje správu, ktorá je zobrazená na čakacom dialógu. 
     * 
     * @param newMessage nová správa pre čakací dialóg
     */
    public static void updateWaitingDialogMessage(String newMessage){
        if(waitingDialog != null && waitingDialog.isVisible()){
            message.setText(newMessage);
            waitingDialog.repaint();
        }
    }
    
    /**
     * Pripraví čakaci dialóg. Správu je potom možné jednoducho meniť nastavením
     * textu pre {@link #message}.
     */
    private static void initWaitingDialog(JButton... buttons){
        JPanel panel = new JPanel();
        panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        message = new JLabel();
        GroupLayout gl = new GroupLayout(panel);
        if(buttons == null || buttons.length == 0){
        gl.setVerticalGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGap(10, 40, 100)
                .addComponent(message)
                .addGap(10, 40, 100));
        gl.setHorizontalGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGap(10, 100, 200)
                .addComponent(message)
                .addGap(10, 100, 200));
        } else {
            Group buttVer = gl.createParallelGroup(GroupLayout.Alignment.CENTER);
            Group buttHor = gl.createSequentialGroup();
            for(JButton b : buttons){
                buttVer.addComponent(b);
                buttHor.addComponent(b);
            }
            gl.setVerticalGroup(gl.createSequentialGroup()
                    .addComponent(message)
                    .addGap(20)
                    .addGroup(buttVer));
            gl.setHorizontalGroup(gl.createSequentialGroup()
                    .addGap(40)
                    .addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(message)
                        .addGroup(buttHor))
                    .addGap(40));
        }
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);
        panel.setLayout(gl);

        waitingDialog = new JDialog();
        waitingDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowActivated(WindowEvent e) {
                synchronized(WAITING_DIALOG_OBJECT){
                    waitUntilShowed = false;
                    WAITING_DIALOG_OBJECT.notifyAll();
                }
            }
        });
        waitingDialog.setModal(true);
        waitingDialog.setUndecorated(true);
        waitingDialog.add(panel);
    }
    
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
    
    /**
     * Samostané vlákno pre spustenie čakacieho dialógu, aby nezamrzlo
     * vlákno vlajúce {@link #showWaitingDialog(java.lang.String)}.
     */
    private static class DialogThread implements Runnable {

        @Override
        public void run() {
            waitingDialog.setVisible(true);
        }
    }
}