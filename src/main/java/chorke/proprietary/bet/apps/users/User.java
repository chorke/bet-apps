
package chorke.proprietary.bet.apps.users;

import chorke.proprietary.bet.apps.core.CoreUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Užíateľ aplikácie
 * 
 * @author Chorke
 */
public class User {
    
    /**
     * Meno užívateľa.
     */
    private final String name;
    /**
     * Súbor pre ukladanie poznámok.
     */
    private File notesFile;
    /**
     * Zoznam stávkových kancelárií, ktoré sa nemajú sťahovať. [Pre korektný 
     * zoznam pred použitím zavolať {@link #setUpBetCompaniesBanList()}]
     */
    private Set<String> betCompaniesBanList;
    /**
     * Súbor pre ukladanie zakázaných stávkových kancelárií.
     */
    private File betCompaniesBanListFile;

    /**
     * Vytvorí novú inštanciu užívateľa s menom {@code name}.
     * 
     * @param name meno užívateľa
     * @throws IllegalArgumentException ak je meno {@code null} alebo prázdne
     */
    public User(String name) {
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("Users name cannot be empty.");
        }
        this.name = name;
    }
    
    /**
     * Vráti meno užívateľa.
     * 
     * @return meno užívateľa
     */
    public String getUserName(){
        return name;
    }
    
    /**
     * Vráti defaultný súbor, kde budúú ukladané užívateľové poznámky.
     * 
     * @return súbor s užívateľovými poznámkami.
     */
    public File getNotesFile(){
        if(notesFile == null){
            notesFile = new File(getUserDirectory(), name + "_notes");
        }
        return notesFile;
    }
    
    /**
     * Vráti zložku, v ktorej sa nachádza užívateľksá profil tohto užívateľa.
     * 
     * @return zložku s užívateľovým profilom
     */
    private File getUserDirectory(){
        File userDir = new File(CoreUtils.getBaseUsersDirectory(), name);
        if(!userDir.exists()){
            userDir.mkdirs();
        }
        return userDir;
    }
    
    /**
     * Vráti zoznam užívateľom zakázaných stávkových kancelárií.
     * 
     * @return zakázané stávkové kancelárie.
     */
    public Set<String> getBetCompniesBanList(){
        setUpBetCompaniesBanList();
        return betCompaniesBanList;
    }
    
    /**
     * Nastaví ban list pre stávkové spoločnosti.
     */
    private void setUpBetCompaniesBanList(){
        if(betCompaniesBanList == null){
            setUpBetCompaniesBanListFile();
            betCompaniesBanList = new HashSet<>();
            try(FileReader fr = new FileReader(betCompaniesBanListFile);
                    BufferedReader br = new BufferedReader(fr)){
                String line;
                while((line = br.readLine()) != null){
                    betCompaniesBanList.add(line);
                }
            } catch (IOException ex){
                System.out.println(ex);
            }
        }
    }
    
    /**
     * Nastaví súbor pre ukladanie zakázaných stávkových spoločností.
     */
    private void setUpBetCompaniesBanListFile() {
        if(betCompaniesBanListFile == null){
            betCompaniesBanListFile = new File(CoreUtils.getBaseUsersDirectory(),
                CoreUtils.FILE_SEP + name + CoreUtils.FILE_SEP + name + "_bcbanlist");
            if(!betCompaniesBanListFile.exists()){
                try{
                    betCompaniesBanListFile.createNewFile();
                } catch (IOException ex){
                    System.out.println(ex);
                }
            }
        }
    }
    
    /**
     * Pridá stávkovú spoločnosť ku zakázaným.
     * 
     * @param betCompany stávková spoločnosť
     * @throws IllegalArgumentException ak je {@code name null} alebo prázdne.
     */
    public void addBetCompanyToBanList(String betCompany) throws IllegalArgumentException{
        if(betCompany == null || betCompany.isEmpty()){
            throw new IllegalArgumentException("Bet company cannot be empty.");
        }
        setUpBetCompaniesBanList();
        if(betCompaniesBanList.add(betCompany)){
            setUpBetCompaniesBanListFile();
            try(FileWriter fw = new FileWriter(betCompaniesBanListFile, true);
                    BufferedWriter bw = new BufferedWriter(fw)){
                bw.append(betCompany);
                bw.newLine();
            } catch (IOException ex){
                System.out.println(ex);
            }
        }
    }
    
    /**
     * Odstráni stávkovú spoločnosť zo zakázaných.
     * 
     * @param betCompany stávková spoločnosť
     */
    public void removeBetCompanyFromBanList(String betCompany){
        setUpBetCompaniesBanList();
        if(betCompaniesBanList.remove(betCompany)){
            setUpBetCompaniesBanListFile();
            betCompaniesBanListFile.delete();
            try{
                betCompaniesBanListFile.createNewFile();
                try(FileWriter fw = new FileWriter(betCompaniesBanListFile, true);
                        BufferedWriter bw = new BufferedWriter(fw)){
                    for(String s : betCompaniesBanList){
                        bw.append(s);
                        bw.newLine();
                    }
                }
            } catch (IOException ex){
                System.out.println(ex);
            }
        }
    }
    /**
     * Vráti všetkých užívateľov, ktorí sú dostupní pre aplikáciu.
     * 
     * @return všetkých užívateľov dostupných pre aplikáciu
     */
    public static User[] getAllUsers(){
        File[] usersDirs = CoreUtils.getBaseUsersDirectory().listFiles();
        if(usersDirs == null || usersDirs.length == 0){
            return new User[0];
        }
        User[] users = new User[usersDirs.length];
        int i = 0;
        for(File usDir : usersDirs){
            users[i++] = new User(usDir.getName());
        }
        return users;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof User)){
            return false;
        }
        return name.equalsIgnoreCase(((User)obj).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public String toString() {
        return "User: " + name;
    }
}
