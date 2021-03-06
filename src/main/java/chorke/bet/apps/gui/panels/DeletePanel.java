
package chorke.bet.apps.gui.panels;

import chorke.bet.apps.core.CoreUtils;
import chorke.bet.apps.gui.GuiUtils;
import chorke.bet.apps.gui.Session;
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
import org.chorke.gui.utils.worker.SwingWorkerWithWaitingDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel pre odstraňovanie stávok.
 * 
 * @author Chorke
 */
public class DeletePanel extends JPanel{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DeletePanel.class);
    
    /**
     * Aktuálne sedenie
     */
    private Session session;
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
     * @param session aktuálne sedeni
     */
    public DeletePanel(Session session){
        this.session = session;
        this.bundle = session.getDefaultBundle();
        this.manager = session.getManager();
        init();
    }
    
    /**
     * Inicializuje panel.
     */
    private void init(){
        betCompaniesPanel = new BetCompaniesListPanel(
                CoreUtils.getAllAvailableBetCompanies(manager, GuiUtils.SUPPORTED_BET_TYPES),
                null, null, session);
        countriesAndLeaguesPanel = new CountriesAndLeaguesPanel(
                manager.getAvailableCountriesAndLeagues(), null, session);
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
                DeleteBetCompaniesWorker worker = new DeleteBetCompaniesWorker();
                worker.setCancelString(bundle.getString("cancel"));
                worker.setDialogMessage(bundle.getString("deleting"));
                worker.execute(true, SwingWorkerWithWaitingDialog.HIDE_AFTER_DONE);
                LOGGER.info("done listner");
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
                DeleteLeaguesWorker worker = new DeleteLeaguesWorker();
                worker.setCancelString(bundle.getString("cancel"));
                worker.setDialogMessage(
                        "_______________" + bundle.getString("deleting") + "_______________");
                worker.execute(true, SwingWorkerWithWaitingDialog.HIDE_AFTER_DONE);
            }
        }
    }
    
    /**
     * Worker pre vamazanie zvolených stávkových spoločností.
     */
    private class DeleteBetCompaniesWorker extends SwingWorkerWithWaitingDialog<Void, String>{

        @Override
        public void cancelInvoked() {}

        @Override
        public Void doYourJob() throws Exception {
            Set<String> betCompaniesToDelete = betCompaniesPanel.getActualChosenBetCompanies();
            for(String s : betCompaniesToDelete){
                LOGGER.info("Deleting bet company " + s);
                manager.deleteBetCompany(s);
            }
            return null;
        }

        @Override
        public void jobIsDone() {
            try{
                get();
            } catch (ExecutionException | InterruptedException | CancellationException ex){
                LOGGER.error("Error while deleting bet companies.", ex);
                JOptionPane.showMessageDialog(null, bundle.getString("deletingFail"));
            }
        }   
    }
    
    /**
     * Worker pre vymazanie zvolených líg.
     */
    private class DeleteLeaguesWorker extends SwingWorkerWithWaitingDialog<Void, String>{
        
        @Override
        public Void doYourJob() throws Exception {
            Map<String, Set<String>> leaguesToDelete = countriesAndLeaguesPanel.getChoosenLeagues();
            if(leaguesToDelete.containsKey("")){
                LOGGER.info("Deleting all matches");
                manager.deleteAll();
            } else {
                for(String country : leaguesToDelete.keySet()){
                    Set<String> leagues = leaguesToDelete.get(country);
                    if(leagues.contains("")){
                        LOGGER.info("Now I deleting country " + country);
                        manager.deleteCountry(country);
                    } else {
                        for(String l : leagues){
                            LOGGER.info("Now I deleting league " + country + " : " + l);
                            manager.deleteLeague(country, l);
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public void jobIsDone() {
            try{
                get();
            } catch (ExecutionException | InterruptedException | CancellationException ex){
                LOGGER.error("Error while deleting leagues.", ex);
                JOptionPane.showMessageDialog(null, bundle.getString("deletingFail"));
            }
        }

        @Override
        public void cancelInvoked() {}
    }
}