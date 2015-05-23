
package chorke.bet.apps.gui.panels;

import chorke.bet.apps.core.CoreUtils;
import chorke.bet.apps.core.CoreUtils.BettingSports;
import chorke.bet.apps.core.Tuple;
import chorke.bet.apps.core.httpparsing.HTMLBetParser;
import chorke.bet.apps.core.httpparsing.MultithreadHTMLBetParser;
import chorke.bet.apps.core.match.Match;
import chorke.bet.apps.gui.GuiUtils;
import chorke.bet.apps.gui.Session;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.chorke.gui.utils.logging.GUIAppender;
import org.chorke.gui.utils.panels.DateChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel pre sťahovanie stávok do DB
 * @author Chorke
 */
public class DownloadPanel extends JPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadPanel.class);
    
    private JComboBox<String> sportToDownload;
    private DateChooser from;
    private DateChooser by;
    private JButton downloadButton;
    
    private Session session;
    
    private ResourceBundle bundle;
    
    public DownloadPanel(Session session) {
        if(session == null){
            throw new IllegalArgumentException("Session cannot be null.");
        }
        this.session = session;
        bundle = this.session.getDefaultBundle();
        init();
    }
    
    private void init(){
        from = new DateChooser(session.getDefaultLocale());
        by = new DateChooser(session.getDefaultLocale());
        from.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED),
                bundle.getString("from")));
        by.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED),
                bundle.getString("by")));
        Dimension dim = new Dimension(150, 50);
        from.setPreferredSize(dim);
        by.setPreferredSize(dim);
        sportToDownload = new JComboBox<>(getLocalizedBettingSportsArray());
        sportToDownload.setBackground(Color.WHITE);
        downloadButton = new JButton(bundle.getString("download"));
        downloadButton.addActionListener(new DownloadSportsListener());
        
        GroupLayout gl = new GroupLayout(this);
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);
        gl.setHorizontalGroup(gl.createParallelGroup(Alignment.CENTER, true)
                .addGroup(Alignment.CENTER, gl.createSequentialGroup()
                    .addGap(10, 40, 200)
                    .addComponent(from, 120, 150, 250)
                    .addGap(10, 40, 200)
                    .addComponent(by)
                    .addGap(10, 40, 200))
                .addGroup(Alignment.CENTER, gl.createSequentialGroup()
                    .addGap(10, 40, 200)
                    .addComponent(sportToDownload)
                    .addGap(10, 40, 200)
                    .addComponent(downloadButton)
                    .addGap(10, 40, 200)));
        gl.setVerticalGroup(gl.createParallelGroup(Alignment.CENTER, true)
                .addGroup(Alignment.CENTER, gl.createSequentialGroup()
                    .addGap(10, 40, 200)
                    .addComponent(from, 20, 30, 40)
                    .addGap(10, 40, 200)
                    .addComponent(sportToDownload, 20, 30, 40)
                    .addGap(10, 40, 200))
                .addGroup(Alignment.CENTER, gl.createSequentialGroup()
                    .addGap(10, 40, 200)
                    .addComponent(by)
                    .addGap(10, 40, 200)
                    .addComponent(downloadButton)
                    .addGap(10, 40, 200)));
        gl.linkSize(sportToDownload, downloadButton);
        gl.linkSize(from, by);
        setLayout(gl);
    }
    
    /**
     * Vráti lokalizované názvy športov v poradí, ako ich vráti 
     * {@link BettingSports#values()}.
     * @return 
     */
    private String[] getLocalizedBettingSportsArray(){
        BettingSports[] sports = BettingSports.values();
        String[] out = new String[sports.length];
        int i = 0;
        for(BettingSports bs : sports){
            out[i++] = bundle.getString(bs.toString());
        }
        return out;
    }
    
    /**
     * Akcia pre download button. Stiahne všetky zápasy podľa aktuálne
     * zvoleného počiatočného (from) a koncového (by) dátumu a podľa
     * športu (sportToDownload). Ku sťahovaniu používa HTMLBetParser
     * parser. 
     */
    private class DownloadSportsListener implements ActionListener{
        
        @Override
        public void actionPerformed(ActionEvent e) {
            Downloader d = new Downloader(from.getActualDate(), by.getActualDate(),
                    BettingSports.values()[sportToDownload.getSelectedIndex()], session.getParser());
            d.execute();
        }
    }
    
    /**
     * Stiahne všetky zápasy podľa zadaného dátumu. Zneviditeľní všetky
     * aktívne okná aplikácie a otvorí iba jedno s informáciami o sťahovaní.
     * Po skončení sťahovania sa znova všetky vrátia do pôvodného stavu.
     * Počas sťahovania je nastavený nový OutputStream pre systém.
     * Po skončení je obnovený pôvodný.
     * 
     * Relevanté sú iba dni. Menšie jednotky ako celé dni neuvažuje.
     */
    private class Downloader extends SwingWorker<Void, Void>{

        private Calendar startDate;
        private Calendar endDate;
        private BettingSports sport;
        private HTMLBetParser betParser;
        
        /**
         * Počet stiahnutých zápasov.
         */
        private int matchesCount;
        /**
         * Informácie o viditeľnosti aktuálnych okien pre znovuobnovenie stavu
         * pred začatím sťahovania.
         */
        private boolean[] windowsVisibility;
        /**
         * Okná aplikácie pred začatím sťahovania. Do pôvodnej viditeľnosti sa 
         * po skončení sťahovania budú dávať iba tieto okná.
         */
        private Window[] windows;
        /**
         * Okno pre výpis informácii o sťahovaní.
         */
        private JFrame downloadInfoWin;
        /**
         * Textová oblasť pre výpis informácií o sťahovaní.
         */
        private JTextPane infoTextPane;
//        /**
//         * Defaultný výstupný stream JVM pred začatím sťahovania.
//         */
//        private PrintStream defaultPrintStream;
        
        /**
         * Indikácia, že sa má zastaviť sťahovanie.
         */
        private volatile boolean stop;

        /**
         * Nestiahnuté športy.
         */
        private Collection<Tuple<BettingSports, Calendar>> allUndownloadedSports;
        
        /**
         * Vytvorí no downloader, ktorý sťiahne zápasy od startDate do endDate.
         * Pre sťahovanie používa parser.
         * @param startDate
         * @param endDate
         * @param parser 
         */
        public Downloader(Calendar startDate, Calendar endDate, BettingSports sport, HTMLBetParser parser) {
            this.startDate = (Calendar)startDate.clone();
            this.endDate = (Calendar)endDate.clone();
            this.sport = sport;
            this.betParser = parser;
            eraseDate(this.startDate);
            eraseDate(this.endDate);
        }
        
        @Override
        protected Void doInBackground() throws Exception {
            stop = false;
            hideWindows();
            downloadInfoWin = getDownloadInfoWindow();
            downloadInfoWin.setVisible(true);
            allUndownloadedSports = new LinkedList<>();
            
            Calendar partStartDate = (Calendar)startDate.clone();
            betParser.setExploredSport(sport);
            boolean end = false;
            matchesCount = 0;
            while(!end && !stop){
                Calendar partEndDate = getNextPartEndDate(partStartDate);
                betParser.setStartDate(partStartDate);
                betParser.setEndDate(partEndDate);
                betParser.setBetCompaniesBanList(session.getUser().getBetCompniesBanList());
                Collection<Match> matches = betParser.getMatches();
                matchesCount += matches.size();
                matches.clear();
                allUndownloadedSports.addAll(betParser.getUndownloadedSports());
                end = lastIteration(partEndDate);
                partStartDate = partEndDate;
                partStartDate.add(Calendar.DATE, 1);
            }
            return null;
        }
        
        /**
         * Vráti okno, v ktorom sa budú zobrazovať informácie o sťahovaní.
         * @return 
         */
        private JFrame getDownloadInfoWindow(){
            infoTextPane = GUIAppender.getTextPane("DOWNLOAD_GUI");
            infoTextPane.setText("");
            JScrollPane pane = new JScrollPane(infoTextPane);
            pane.setPreferredSize(new Dimension(500, 500));
            JPanel mainInfoPanel = new JPanel();
            mainInfoPanel.setPreferredSize(new Dimension(500, 550));
            JButton stopButton = new JButton(bundle.getString("stop"));
            stopButton.addActionListener(new StopDownloading(this));
            
            mainInfoPanel.add(pane);
            mainInfoPanel.add(stopButton);
            
            return GuiUtils.getDefaultFrame(null, JFrame.DO_NOTHING_ON_CLOSE,
                    false, null, mainInfoPanel);
        }
        
        @Override
        protected void done() {
            try{
                get();
            } catch (ExecutionException | InterruptedException | CancellationException ex){
                LOGGER.error("Error while downloadind sports.", ex);
            }
            if(downloadInfoWin != null){
                downloadInfoWin.dispose();
            }
            int i = 0;
            for(Window w : windows){
                w.setVisible(windowsVisibility[i++]);
            }
            getDownloadResultsInfoWin().setVisible(true);
            LOGGER.warn("unsaved {" + betParser.getUnsavedMatches().size()
                + "}: " + betParser.getUnsavedMatches());
            LOGGER.warn("matches {" + betParser.getUndownloadedMatches().size()
                + "}: " + betParser.getUndownloadedMatches());
            LOGGER.warn("sports {" + betParser.getUndownloadedSports().size()
                + "}: " + betParser.getUndownloadedSports());
            
        }
        
        /**
         * Vráti okno, ktoré zobrazí záverečné výsledky sťahovania.
         * @return 
         */
        private JFrame getDownloadResultsInfoWin(){
            JScrollPane pane = new JScrollPane(infoTextPane);
            pane.setPreferredSize(new Dimension(500, 400));
            int collSize = betParser.getUndownloadedSports().size();
            JLabel undwnSportsLable = new JLabel(bundle.getString("undwnSports") + ": " + collSize);
            JLabel matchesCountLabel = new JLabel(bundle.getString("dwnMatches") + ": " + matchesCount);
            JLabel emptyLabel = new JLabel();
            JButton showButton = new JButton(bundle.getString("show"));
            showButton.addActionListener(new ShowInfoAboutUndownloadedSports(allUndownloadedSports));
            showButton.setEnabled(collSize != 0);
            JPanel mainPanel = new JPanel();
            GroupLayout gl = new GroupLayout(mainPanel);
            gl.setHorizontalGroup(gl.createParallelGroup(Alignment.CENTER)
                    .addComponent(pane)
                    .addGroup(gl.createSequentialGroup()
                        .addGroup(gl.createParallelGroup()
                            .addComponent(matchesCountLabel)
                            .addComponent(undwnSportsLable))
                        .addGroup(gl.createParallelGroup()
                            .addComponent(emptyLabel)
                            .addComponent(showButton))));
            gl.setVerticalGroup(gl.createSequentialGroup()
                    .addComponent(pane)
                    .addGroup(gl.createParallelGroup()
                        .addGroup(gl.createSequentialGroup()
                            .addComponent(matchesCountLabel)
                            .addComponent(undwnSportsLable))
                        .addGroup(gl.createSequentialGroup()
                            .addComponent(emptyLabel)
                            .addComponent(showButton))));
            gl.setAutoCreateContainerGaps(true);
            gl.setAutoCreateGaps(true);
            gl.linkSize(SwingConstants.VERTICAL, undwnSportsLable, showButton);
            gl.linkSize(SwingConstants.VERTICAL, matchesCountLabel, emptyLabel);
            mainPanel.setLayout(gl);            
            
            return GuiUtils.getDefaultFrame(null, JFrame.DISPOSE_ON_CLOSE,
                    true, null, mainPanel);
        }
        
        /**
         * Stiahne znovu nestiahnuté športy.
         */
        private class ShowInfoAboutUndownloadedSports implements ActionListener{
            
            private Collection<Tuple<BettingSports, Calendar>> undownloadedSports;

            public ShowInfoAboutUndownloadedSports(
                    Collection<Tuple<BettingSports, Calendar>> undownloadedMatches) {
                this.undownloadedSports = undownloadedMatches;
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder builder = new StringBuilder();
                DateFormat df = DateFormat.getDateInstance(
                        DateFormat.SHORT, CoreUtils.getDefaultLocale());
                for(Tuple<BettingSports, Calendar> sports : undownloadedSports){
                    builder.append(df.format(sports.second.getTime()))
                            .append("   ")
                            .append(bundle.getString(sports.first.toString()))
                            .append(System.lineSeparator());
                }
                JTextArea text = new JTextArea(builder.toString());
                text.setEditable(false);
                JScrollPane pane = new JScrollPane(text);
                pane.setPreferredSize(new Dimension(300, 200));
                GuiUtils.getDefaultFrame(null, JFrame.DISPOSE_ON_CLOSE,
                    true, null, pane).setVisible(true);
            }
        }
        
        /**
         * Zneviditeľní všetky okná aplikácie. Zaznamená ich pôvodné hodnoty
         * viditeľnosti pre opätovné znovunastavenie do windowsVisibility.
         */
        private void hideWindows(){
            windows = Window.getWindows();
            windowsVisibility = new boolean[windows.length];
            int i = 0;
            for(Window w : windows){
                windowsVisibility[i++] = w.isVisible();
                w.setVisible(false);
            }
        }
        
        /**
         * Rozhodne či zadaný partEndDate je už posledný požadovaný dátum,
         * ktorý má byť stiahnutý.
         * 
         * @param partEndDate
         * @return 
         */
        private boolean lastIteration(Calendar partEndDate){
            return endDate.compareTo(partEndDate) == 0;
        }
        
        /**
         * Vráti najbližší dátum, ktorý je menší alebo rovný koncovému požadovanému
         * dátumu a nie je viac ako 4 dní od start dátumu.
         * @param start
         * @return 
         */
        private Calendar getNextPartEndDate(Calendar start){
            Calendar end = (Calendar)start.clone();
            int i = 0;
            while(i++ < 4 && end.before(endDate)){
                end.add(Calendar.DATE, 1);
            }
            return end;
        }
        
        /**
         * Vymaže všetky polia menšie ako dni v dátume date. Resp. nastaví dátum
         * na prvú milisekundu dňa.
         * @param date 
         */
        private void eraseDate(Calendar date){
            date.set(Calendar.MILLISECOND, 1);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.HOUR, 0);
        }
    }
    
    /**
     * Action listener pre zastavenie sťahovania.
     */
    private class StopDownloading implements ActionListener{

        private Downloader downloader;
        
        /**
         * Downloader, ktorý by mal byť zastavený. 
         * @param downloader 
         */
        public StopDownloading(Downloader downloader) {
            this.downloader = downloader;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] options = {bundle.getString("immediately"),
                bundle.getString("afterIteration"),
                bundle.getString("cancel")};
            int option = JOptionPane.showOptionDialog(null,
                    bundle.getString("interruptQuestion"), "",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, 
                    options, options[2]);
            if(option == 2 || option == -1){
                return;
            }
            downloader.stop = true;
            if(option == 0){
                if(downloader.betParser instanceof MultithreadHTMLBetParser){
                    ((MultithreadHTMLBetParser)downloader.betParser).stopThreads();
                }
            }
        }
    }
}
