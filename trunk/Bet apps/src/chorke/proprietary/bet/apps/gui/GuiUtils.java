
package chorke.proprietary.bet.apps.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

/**
 * Trieda s často používanými metódami v aplikácii v GUI časti.
 * @author Chorke
 */
public class GuiUtils {

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
     * Zobrazí čakací dialóg so správou waitingMessage. Počas jeho viditeľnosti
     * nie je možné pracovať s aplikáciou. Ak je už zobrazený, nestane sa nič.
     * Dialóg spustí v novom vlákne. S ostatnými gui časťami teda nie je možné 
     * pracovať, ale samotné volajúce vlákno môže pokračovať vo svojej práci.
     * 
     * @param waitingMessage 
     */
    public static void showWaitingDialog(String waitingMessage){
        if(waitingDialog == null){
            initWaitingDialog();
        } else if(waitingDialog.isVisible()){
            return;
        }
        message.setText(waitingMessage);
        waitingDialog.pack();
        waitingDialog.setLocationRelativeTo(null);
        new Thread(new DialogThread()).start();
    }
    
    /**
     * Skryje aktuálne zobrazený čakací dialóg. Ak nie je dialóg
     * viditeľný, nestane sa nič.
     */
    public static void hideWaitingDialog(){
        if(waitingDialog != null && waitingDialog.isVisible()){
            waitingDialog.setVisible(false);
        }
    }
    
    /**
     * Pripraví čakaci dialóg. Správu je potom možné jednoducho meniť nastavením
     * textu pre {@link #message}.
     */
    private static void initWaitingDialog(){
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 100));
        panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        message = new JLabel();
        GroupLayout gl = new GroupLayout(panel);
        gl.setVerticalGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGap(10, 40, 100)
                .addComponent(message)
                .addGap(10, 40, 100));
        gl.setHorizontalGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGap(10, 100, 200)
                .addComponent(message)
                .addGap(10, 100, 200));
        gl.setAutoCreateContainerGaps(true);
        panel.setLayout(gl);

        waitingDialog = new JDialog();
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
    
    private static class DialogThread implements Runnable {

        @Override
        public void run() {
            waitingDialog.setVisible(true);
        }
        
    }
}
