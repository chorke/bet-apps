package bet.apps.core;

import bet.apps.BetApps;
import bet.apps.statistics.MatchesStatistics;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JFileChooser;

public class DataReader {
    
    private static File lastFile = null;
    
    /**
     * Load matches and bets from file or directory {@code lastFile} and store them in 
     * {@code statistics}. {@code JFileChooser} is opened according to {@code lastFile}
     * and returned file is last selected file from {@code JFileChooser}.
     * 
     * @param file      started file to choose for JFileChooser
     * @param statistics    {@code MatchesStatistics} for storing matches.
     * @return              parent directory of last selected file
     */
    
    public static MatchesStatistics loadBets(File file){
        JFileChooser chooser = new JFileChooser(file);
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int ret = chooser.showDialog(null, BetApps.LABLES.getString("add"));
        File[] files = null;
        MatchesStatistics matches = new MatchesStatistics();
        if(ret == JFileChooser.APPROVE_OPTION){
            files = chooser.getSelectedFiles();
            getMatches(files, matches);
        }
        if(files == null || files.length == 0){
            lastFile = file;
        } else {
            lastFile =  files[0].getParentFile();
        }
        return matches;
    }
    
    private static void getMatches(File[] files, MatchesStatistics statistics){
        if(files != null && files.length > 0){
            for(File f: files){//int i = 0; i < files.length; i++){
                if(f.isDirectory()){
                    getMatches(f.listFiles(), statistics);
                } else {
                    readBetsFromFile(f, statistics);
                }
            }
        }
    }
    
    private static void readBetsFromFile(File file, MatchesStatistics statistics){
        try(FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr)){
            String fileName = file.getName();
            String line = br.readLine();
            int i = 1;
            while(line != null){
                try{
                    statistics.addMatch(line, fileName);
                }catch(BetErrorException ex){
                    BetApps.fileErrors += BetApps.LABLES.getString("line") + i + 
                            BetApps.LABLES.getString("InFile")
                            + file.getPath() 
                            + BetApps.LINE_SEPARATOR
                            + ex.getMessage() + "\t" + line
                            + BetApps.LINE_SEPARATOR;
                }
                line = br.readLine();
                i++;
            }
        } catch (IOException e){}
    }
    
    public static File getLastFile(){
        return lastFile;
    }
    
    /**
     * Store data as *.arff file for Weka. Default file is D:\kurzy.arff.
     * 
     * @param stats Matches to be stored.
     */
    public static void writeToArff(MatchesStatistics stats){
        Collection<Match> toWrite= stats.getMatches();
        Iterator iter = toWrite.iterator();
        try(FileOutputStream fos = new FileOutputStream(new File("D:" + BetApps.FILE_SEPARATOR + "kurzy.arff"));
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                BufferedWriter bw = new BufferedWriter(osw)) {
            bw.write("@relation kurzy"
                    + BetApps.LINE_SEPARATOR
                    + BetApps.LINE_SEPARATOR
                    + "@attribute bet1 numeric"
                    + BetApps.LINE_SEPARATOR
                    + "@attribute bet0 numeric"
                    + BetApps.LINE_SEPARATOR
                    + "@attribute bet2 numeric"
                    + BetApps.LINE_SEPARATOR
                    + "@attribute class {1,2,3}"
                    + BetApps.LINE_SEPARATOR
                    + BetApps.LINE_SEPARATOR
                    + "@data"
                    + BetApps.LINE_SEPARATOR
                    );
            while(iter.hasNext()){
                Match m = (Match)iter.next();
                bw.write(m.getBet1()
                        + ","
                        + m.getBet0()
                        + ","
                        + m.getBet2()
                        + ","
                        + (m.getWinner() + 1)
                        + BetApps.LINE_SEPARATOR);
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Store matches as file for MultiLayerPercepron network for NeuralNetworks apps.
     * 
     * @param stats matches to be stored
     */
    public static void writeToMLP(MatchesStatistics stats){
        Collection<Match> toWrite= stats.getMatches();
        Iterator iter = toWrite.iterator();
        try(FileOutputStream fos = new FileOutputStream(new File("D:" + BetApps.FILE_SEPARATOR + "kurzy.nnts"));
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                BufferedWriter bw = new BufferedWriter(osw)) {
            bw.write("#Multi Layer Perceptron"
                    + BetApps.LINE_SEPARATOR
                    );
            while(iter.hasNext()){
                Match m = (Match)iter.next();
                bw.write(m.getBet1()
                        + " "
                        + m.getBet0()
                        + " "
                        + m.getBet2()
                        + BetApps.LINE_SEPARATOR);
                String s = "";
                if(m.getWinner() == 0){ s = "0.1 0 0"; }
                if(m.getWinner() == 1){ s = "0 0.1 0"; }
                if(m.getWinner() == 2){ s = "0 0 0.1"; }
                bw.write(s + BetApps.LINE_SEPARATOR);
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
