
package chorke.proprietary.bet.apps.gui.panels;

import chorke.proprietary.bet.apps.core.httpparsing.HTMLBetParser;
import chorke.proprietary.bet.apps.core.httpparsing.HTMLBetParser.BettingSports;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

/**
 * Panel pre sťahovanie stávok do DB
 * @author Chorke
 */
public class DownloadPanel extends JPanel {

    private HTMLBetParser parser;
    private JComboBox<BettingSports> sportToDownload;
    private DateChooser from;
    private DateChooser till;
    private JButton downloadButton;
    
    public DownloadPanel(HTMLBetParser parser) {
        this.parser = parser;
        init();
    }
    
    private void init(){
        setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Download"));
        from = new DateChooser();
        till = new DateChooser();
        from.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "From"));
        till.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Till"));
        Dimension dim = new Dimension(150, 50);
        from.setPreferredSize(dim);
        till.setPreferredSize(dim);
        sportToDownload = new JComboBox<>(BettingSports.values());
        downloadButton = new JButton("Download");
        downloadButton.addActionListener(new DownloadSports());
        
        GroupLayout gl = new GroupLayout(this);
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);
        gl.setHorizontalGroup(gl.createParallelGroup(Alignment.CENTER, true)
                .addGroup(Alignment.CENTER, gl.createSequentialGroup()
                    .addGap(10, 40, 200)
                    .addComponent(from, 120, 150, 250)
                    .addGap(10, 40, 200)
                    .addComponent(till)
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
                    .addComponent(till)
                    .addGap(10, 40, 200)
                    .addComponent(downloadButton)
                    .addGap(10, 40, 200)));
        gl.linkSize(sportToDownload, downloadButton);
        gl.linkSize(from, till);
        setLayout(gl);
    }
    
    /**
     * Akcia pre download button. Stiahne všetky zápasy podľa aktuálne
     * zvoleného počiatočného (from) a koncového (till) dátumu a podľa
     * športu (sportToDownload). Ku sťahovaniu používa HTMLBetParser
     * parser. 
     */
    private class DownloadSports implements ActionListener{
        
        @Override
        public void actionPerformed(ActionEvent e) {
            parser.setStartDate(from.getActualDate());
            parser.setEndDate(till.getActualDate());
            parser.setExploredSport((BettingSports)sportToDownload.getSelectedItem());
            //parsing
        }
        
    }
}
