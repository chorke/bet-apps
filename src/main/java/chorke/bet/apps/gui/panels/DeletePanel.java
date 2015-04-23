
package chorke.bet.apps.gui.panels;

import chorke.bet.apps.core.CoreUtils;
import chorke.bet.apps.gui.GuiUtils;
import chorke.bet.apps.gui.Season;
import chorke.bet.apps.io.BetIOManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel pre odstraňovanie stávok.
 * 
 * @author Chorke
 */
public class DeletePanel extends JPanel{
    
    private static final Logger log = LoggerFactory.getLogger(DeletePanel.class);
    
    /**
     * Aktuálne sedenie
     */
    private Season season;
    /**
     * Lokalizované názvy a hlášky
     */
    private ResourceBundle bundle;
    /**
     * Panel s krajinami a ligami
     */
    private CountriesAndLeaguesPanel countriesAndLeaguesPanel;
    /**
     * Panel so stávkovými spoločnosťami
     */
    private BetCompaniesListPanel betCompaniesPanel;
    /**
     * Tlačidlo pre vymazanie zvolených líg
     */
    private JButton deleteLeaguesButton;
    /**
     * Tlačidlo pre vymazanie zvolených spoločností
     */
    private JButton deleteBetCompaniesButton;
    /**
     * Manažér pre prácu s DB
     */
    private BetIOManager manager;
    
    /**
     * Vytvorí nový panel, ktorý ponúka možnsoť zmazať stávkové spoločnosti
     * a ligy.
     * 
     * @param season aktuálne sedeni
     */
    public DeletePanel(Season season){
        this.season = season;
        this.bundle = season.getDefaultBundle();
        this.manager = season.getManager();
        init();
    }
    
    /**
     * Inicializuje panel.
     */
    private void init(){
        betCompaniesPanel = new BetCompaniesListPanel(
                CoreUtils.getAllAvailableBetCompanies(manager, GuiUtils.SUPPORTED_BET_TYPES),
                null, null, season);
        countriesAndLeaguesPanel = new CountriesAndLeaguesPanel(
                manager.getAvailableCountriesAndLeagues(), null, season);
        deleteBetCompaniesButton = new JButton(bundle.getString("deleteBetCompanies"));
        deleteLeaguesButton = new JButton(bundle.getString("deleteLeagues"));
        deleteBetCompaniesButton.addActionListener(new DeleteBetCompaniesListener());
        deleteLeaguesButton.addActionListener(new DeleteLeaguesListener());
        GroupLayout gl = new GroupLayout(this);
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addComponent(betCompaniesPanel, 250, 250, 250)
                .addComponent(deleteBetCompaniesButton)
                .addComponent(countriesAndLeaguesPanel, 300, 300, 300)
                .addComponent(deleteLeaguesButton));
        gl.setHorizontalGroup(gl.createParallelGroup(Alignment.CENTER)
                .addComponent(betCompaniesPanel)
                .addComponent(deleteBetCompaniesButton)
                .addComponent(countriesAndLeaguesPanel)
                .addComponent(deleteLeaguesButton));
        gl.linkSize(deleteBetCompaniesButton, deleteLeaguesButton);
        setLayout(gl);
    }
    
    /**
     * Listener pre vymazanie zvolených stávkových spoločností
     */
    private class DeleteBetCompaniesListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int option = GuiUtils.showLocalizedConfirmDialog(bundle, "confirmDeleteBetCompanies",
                    "confirmDialogTitle", null);
            if(option == JOptionPane.YES_OPTION){
                new DeleteBetCompaniesWorker().execute();
                System.out.println("done listner");
            }
        }
        
    }
    
    /**
     * Listener pre vymazanie zvolených líg.
     */
    private class DeleteLeaguesListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int option = GuiUtils.showLocalizedConfirmDialog(bundle, "confirmDeleteLeagues",
                    "confirmDialogTitle", null);
            if(option == JOptionPane.YES_OPTION){
                new DeleteLeaguesWorker().execute();
            }
        }
    }
    
    /**
     * Worker pre vamazanie zvolených stávkových spoločností.
     */
    private class DeleteBetCompaniesWorker extends SwingWorker<Void, String>{

        @Override
        protected Void doInBackground() throws Exception {
            showWaitingDialogWithCancelOption(bundle.getString("deleting"));
            Set<String> betCompaniesToDelete = betCompaniesPanel.getActualChosenBetCompanies();
            for(String s : betCompaniesToDelete){
                GuiUtils.updateWaitingDialogMessage(s);
                System.out.println("now I delete bet company " + s);
                manager.deleteBetCompany(s);
            }
            return null;
        }

        @Override
        protected void done() {
            try{
                get();
            } catch (ExecutionException | InterruptedException | CancellationException ex){
                log.error("Error while deleting bet companies.", ex);
                JOptionPane.showMessageDialog(null, bundle.getString("deletingFail"));
            }
            GuiUtils.hideWaitingDialog();
        }
        
    }
    
    /**
     * Worker pre vymazanie zvolených líg.
     */
    private class DeleteLeaguesWorker extends SwingWorker<Void, String>{
        @Override
        protected Void doInBackground() throws Exception {
            showWaitingDialogWithCancelOption(
                    "_______________" + bundle.getString("deleting") + "_______________");
            Map<String, Set<String>> leaguesToDelete = countriesAndLeaguesPanel.getChoosenLeagues();
            if(leaguesToDelete.containsKey("")){
                GuiUtils.updateWaitingDialogMessage(bundle.getString("all"));
                System.out.println("Now I delete all matches");
                manager.deleteAll();
            } else {
                for(String country : leaguesToDelete.keySet()){
                    Set<String> leagues = leaguesToDelete.get(country);
                    if(leagues.contains("")){
                        GuiUtils.updateWaitingDialogMessage(country);
                        System.out.println("Now I deleting country " + country);
                        manager.deleteCountry(country);
                    } else {
                        for(String l : leagues){
                            GuiUtils.updateWaitingDialogMessage(country + " : " + l);
                            System.out.println("Now I deleting league " + country + " : " + l);
                            manager.deleteLeague(country, l);
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void done() {
            try{
                get();
            } catch (ExecutionException | InterruptedException | CancellationException ex){
                log.error("Error while deleting leagues.", ex);
                JOptionPane.showMessageDialog(null, bundle.getString("deletingFail"));
            }
            GuiUtils.hideWaitingDialog();
        }
    }
    
    /**
     * Spustí čakací dialóg pomocou 
     * {@link GuiUtils#showWaitingDialog(java.lang.String, javax.swing.JButton[])}
     * a nastaví mu správu {@code message} a jedno tlačidlo {@code cancel}, 
     * ktoré po stlačení preruší aktuálnu prácu manažéra {@link #manager}
     * pomocou metódy {@link BetIOManager#cancelActualQuery()}.
     * 
     * @param message správa, ktorá sa zobrazí v čakacom dialógu.
     */
    private void showWaitingDialogWithCancelOption(String message){
        JButton cancel = new JButton(bundle.getString("cancel"));
        cancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                manager.cancelActualQuery();
                ((JButton)e.getSource()).setEnabled(false);
            }
        });

        GuiUtils.showWaitingDialog(message, cancel);
    }
}