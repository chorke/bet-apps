
package chorke.proprietary.bet.apps.gui.panels;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;
import javax.swing.undo.UndoManager;



/**
 * Panel pre poznámky.
 * Podporuje undo a redo operácie. Neobsahuje ale tlačítka pre tieto operácie.
 * Pri použití nutné vytvoriť tieto tlačítka.
 * 
 * @author Chorke
 */
public class NotesPanel extends JPanel{

    private final File propertiesFile;
    private Properties prop = new Properties();
    private JTextArea noteText;
    private NoteSaver saver;
    private UndoManager redoUndo;
    private Timer saveTimer;
    
    /**
     * Vytvorí nový panel s poznámkami. Poznámky budú ukladané do súbori 
     * {@code notes}. Ak je {@code notes == null}, poznámky nebudú ukladané.
     * Ak súbor existuje poznámky budú z neho načítané a nastavené ako iniciálny
     * text.
     * 
     * @param notes súbor pre ukladanie a načítanie poznámok
     */
    public NotesPanel(File notes) {
        propertiesFile = notes;
        init();
    }
    
    /**
     * Inicializuje panel s poznámkami.
     */
    private void init(){
        noteText = new JTextArea();
        noteText.setLineWrap(true);
        noteText.setBorder(new LineBorder(Color.BLACK));
        noteText.setWrapStyleWord(true);
        noteText.setBackground(new Color(255,255,200));
        noteText.setTabSize(1);
        noteText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(saveTimer == null && saver == null){
                    initTimer();
                }
            }
        });
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(noteText);
        try{
            if(propertiesFile != null && !propertiesFile.exists()){
                propertiesFile.createNewFile();
            }
            if(propertiesFile != null){
                prop.load(new FileInputStream(propertiesFile));
            }
            noteText.setText(prop.getProperty("note"));
            noteText.setCaretPosition(0);
        } catch (IOException ex){}
        redoUndo = new UndoManager();
        noteText.getDocument().addUndoableEditListener(redoUndo);
    }
    
    /**
     * Uloží text poznámok do súboru.
     */
    public void saveNote(){
        if(saveTimer != null){
            saveTimer.cancel();
            saveTimer = null;
        }
        saver = new NoteSaver();
        saver.execute();
    }
    
    /**
     * Inicializuje časovač, ktorý po 5 sekundách uloží poznámky.
     */
    private void initTimer(){
        saveTimer = new Timer();
        saveTimer.schedule(new SaveTimerTask(), 5000);
    }
    
    /**
     * Redo operácie pre poznámky.
     */
    public void redoEdit(){
        if(redoUndo.canRedo()){
            redoUndo.redo();
        }
    }
    
    /**
     * Undo operácia pre poznámky.
     */
    public void undoEdit(){
        if(redoUndo.canUndo()){
            redoUndo.undo();
        }
    }
    
    /**
     * Úloha, ktorá uloží poznámky.
     */
    private class SaveTimerTask extends TimerTask {

        @Override
        public void run() {
            saveTimer.cancel();
            saveTimer = null;
            saveNote();
        }
    }
    
    /**
     * Worker, ktorý uloží poznámky do súboru.
     */
    private class NoteSaver extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            if(propertiesFile != null){
                prop.setProperty("note", noteText.getText());
                prop.store(new FileOutputStream(propertiesFile), "Automatically saved.");
            }
            return null;
        }

        @Override
        protected void done() {
            try{
                saver = null;
                get();
            } catch (ExecutionException | InterruptedException | CancellationException ex){}
        }
    }
}

