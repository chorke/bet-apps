
package chorke.proprietary.bet.apps.gui.panels;

import chorke.proprietary.bet.apps.StaticConstants;
import chorke.proprietary.bet.apps.core.Tuple;
import chorke.proprietary.bet.apps.core.httpparsing.HTMLBetParser;
import chorke.proprietary.bet.apps.StaticConstants.BettingSports;
import chorke.proprietary.bet.apps.core.httpparsing.MultithreadHTMLBetParser;
import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.gui.GuiUtils;
import chorke.proprietary.bet.apps.gui.Season;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
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
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Panel pre sťahovanie stávok do DB
 * @author Chorke
 */
public class DownloadPanel extends JPanel {

    private JComboBox<String> sportToDownload;
    private DateChooser from;
    private DateChooser by;
    private JButton downloadButton;
    
    private Season season;
    
    private ResourceBundle bundle;
    
    public DownloadPanel(Season season) {
        if(season == null){
            throw new IllegalArgumentException("Season cannot be null.");
        }
        this.season = season;
        bundle = this.season.getDefaultBundle();
        init();
    }
    
    private void init(){
        from = new DateChooser(season.getDefaultLocale());
        by = new DateChooser(season.getDefaultLocale());
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
                    BettingSports.values()[sportToDownload.getSelectedIndex()], season.getParser());
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
        private JTextArea infoTextArea;
        /**
         * Defaultný výstupný stream JVM pred začatím sťahovania.
         */
        private PrintStream defaultPrintStream;
        
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
            PrintStream ps = new PrintStream(new TextAreaOutputStream(infoTextArea));
            defaultPrintStream = System.out;
            System.setOut(ps);
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
            infoTextArea = new JTextArea();
            infoTextArea.setEditable(false);
            infoTextArea.setDocument(new LinesLimitedDocument(6000));
            JScrollPane pane = new JScrollPane(infoTextArea);
            pane.setPreferredSize(new Dimension(500, 500));
            JPanel mainInfoPanel = new JPanel();
            mainInfoPanel.setPreferredSize(new Dimension(500, 550));
            JButton stopButtom = new JButton(bundle.getString("stop"));
            stopButtom.addActionListener(new StopDownloading(this));
            
            mainInfoPanel.add(pane);
            mainInfoPanel.add(stopButtom);
            
            return GuiUtils.getDefaultFrame(null, JFrame.DO_NOTHING_ON_CLOSE,
                    false, null, mainInfoPanel);
        }
        
        @Override
        protected void done() {
            if(downloadInfoWin != null){
                downloadInfoWin.dispose();
            }
            GuiUtils.hideWaitingDialog();
            int i = 0;
            for(Window w : windows){
                w.setVisible(windowsVisibility[i++]);
            }
            System.setOut(defaultPrintStream);
            getDownloadResultsInfoWin().setVisible(true);
            System.err.println("unsaved {" + betParser.getUnsavedMatches().size()
                + "}: " + betParser.getUnsavedMatches());
            System.err.println("matches {" + betParser.getUndownloadedMatches().size()
                + "}: " + betParser.getUndownloadedMatches());
            System.err.println("sports {" + betParser.getUndownloadedSports().size()
                + "}: " + betParser.getUndownloadedSports());
            
        }
        
        /**
         * Vráti okno, ktoré zobrazí záverečné výsledky sťahovania.
         * @return 
         */
        private JFrame getDownloadResultsInfoWin(){
            JScrollPane pane = new JScrollPane(infoTextArea);
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
                        DateFormat.SHORT, StaticConstants.getDefaultLocale());
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
                new Thread(new ShowWaitingDialog()).start();
            }
        }
    }
    
    /**
     * Spustí waiting dialóg, aby sa nespúšťal v EDT.
     */
    private class ShowWaitingDialog implements Runnable{

        @Override
        public void run() {
            GuiUtils.showWaitingDialog(bundle.getString("stopping"));
        }
    }
    
    /**
     * OutputStream, ktorý svoj výstup zapisuje do JTextArea.
     */
    private class TextAreaOutputStream extends OutputStream{

        private JTextArea textArea;

        /**
         * Vytvorí OutputStream, ktorý bude zapisovať výstup do textArea.
         * @param textArea 
         */
        public TextAreaOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }
        
        @Override
        public void write(int b) throws IOException {
            boolean isCaretAtEnd = textArea.getCaretPosition() == textArea.getDocument().getLength();
            textArea.append(new Character((char)b).toString());
            if(isCaretAtEnd){
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            boolean isCaretAtEnd = textArea.getCaretPosition() == textArea.getDocument().getLength();
            textArea.append(new String(b, off, len, Charset.forName("UTF-8")));
            if(isCaretAtEnd){
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
        }
    }
    
    /**
     * Dokument s obmedzeným počtom riadkov. Každý nový pridaný riadok nad limit
     * ostráni najstarší (teda prvý) riadok v dokumente.
     * 
     */
    private class LinesLimitedDocument extends PlainDocument{

        private int linesLimit;
        private int linesCount = 0;
        private List<String> linesShowed = new LinkedList<>();

        public LinesLimitedDocument(int linesLimit) {
            super();
            this.linesLimit = linesLimit;
        }
        
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            String lineSep = System.lineSeparator();
            String[] lines = str.split("[" + lineSep + "]+");
            if(lines.length > linesLimit){
                linesShowed.clear();
                remove(0, getLength());
                int off = 0;
                for(int i = lines.length - linesLimit; i < lines.length; i++){
                    super.insertString(off, lines[i] + lineSep, a);
                    off += lines[i].length();
                }
                linesCount = linesLimit;
            } else {
                for(String l : lines){
                    if(linesCount >= linesLimit){
                        remove(0, linesShowed.remove(0).length() + lineSep.length());
                        linesCount--;
                    }
                    linesShowed.add(l);
                    super.insertString(getLength(), l + lineSep, a);
                    linesCount++;
                }
            }
        }
    }
}
