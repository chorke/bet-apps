
package chorke.proprietary.bet.apps.users;

import chorke.proprietary.bet.apps.StaticConstants;
import java.io.File;

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
     * Vytvorí novú inštanciu užívateľa s menom {@code name}.
     * 
     * @param name meno užívateľa
     * @throws IllegalArgumentException ak je meno {@code null} alebo prázdne
     */
    public User(String name) {
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("users name cannot be empty.");
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
            notesFile = new File(StaticConstants.getUsersDirectory(),
                    StaticConstants.FILE_SEP + name + StaticConstants.FILE_SEP + name + "_notes");
        }
        return notesFile;
    }
    
    /**
     * Vráti všetkých užívateľov, ktorí sú dostupní pre aplikáciu.
     * 
     * @return všetkých užívateľov dostupných pre aplikáciu
     */
    public static User[] getAllUsers(){
        File[] usersDirs = StaticConstants.getUsersDirectory().listFiles();
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
