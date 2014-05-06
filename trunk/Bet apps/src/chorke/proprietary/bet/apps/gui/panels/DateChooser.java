
package chorke.proprietary.bet.apps.gui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
    
/**
 * Panel pre výber dátumu.
 * @author Chorke
 */
public class DateChooser extends JPanel{
    
    /**
     * Okno so zobrazeným dátumom.
     */
    private JDialog dateWin;
    /**
     * JComboBox pre mesiace
     */
    private JComboBox<String> monthChooser;
    /**
     * JSpiner pre mesaic.
     */
    private JSpinner yearChooser;
    /**
     * Tlačítko, ktoré vyvolá zobrazenie dateWin pre výber dátumu.
     */
    private JButton dateCall;
    /**
     * Panel, ktorý obsahuje všetky súčasti pre výber dátumu. Je nastavený
     * pre dateWin.
     */
    private JPanel datePanel;
    /**
     * Panel, ktorý obsahuje mriežku pre dni.
     */
    private JPanel daysPanel;
    /**
     * WindowFosucListener, ktorý je nastavený pre dateWin. 
     */
    private WindowFocusListener focus = new Focus();
    /**
     * Aktuálne zvolený dátum.
     */
    private Calendar calendar;
    /**
     * Dočasný dátum pri výbere. Obzvlášť pri výbere mesiaca a roku.
     */
    private Calendar tmpCal;
    /**
     * Dnešný deň.
     */
    private Calendar today;
    /**
     * Tlačítka pre dni od 1 do 31.
     */
    private JButton[] days;
    /**
     * Lokalizované názvy dní
     */
    private JLabel[] daysName;
    /**
     * Oblasť pre textovú podobu aktuálne zvoleného dátumu.
     */
    private JTextArea dateString;
    /**
     * Defaultné Locale pre aplikáciu
     */
    private Locale defaulLocale;
    
    /**
     * Farba tlačítka, ktorou je zobrazený dnešný dátum.
     */
    private Color todayColor = Color.ORANGE;
    /**
     * Farba tlačítok dní, ktoré nemajú dnešný dátum.
     */
    private Color otherDaysColor = new JButton().getBackground();
    
    /**
     * Vytvorí novú inštanciu DateChooser s počiatočným dnešným dátumom.
     */
    public DateChooser(Locale locale) {
        if(locale == null){
            throw new IllegalArgumentException("Locale cannot be null.");
        }
        this.defaulLocale = locale;
        today = new GregorianCalendar(defaulLocale);
        calendar = new GregorianCalendar(defaulLocale);
        tmpCal = new GregorianCalendar(defaulLocale);
        
        calendar.setTimeInMillis(today.getTimeInMillis());
        tmpCal.setTimeInMillis(today.getTimeInMillis());
        
        init();
    }

    /**
     * Inicializuje panel pre výber kalendáru.
     */
    private void init(){
        String[] s = new String[12];
        Calendar c = new GregorianCalendar(2013, Calendar.JANUARY, Calendar.MONDAY);
        for(int i = 0; i < s.length; i++){
            s[i] = c.getDisplayName(Calendar.MONTH, Calendar.LONG, defaulLocale);
            c.roll(Calendar.MONTH, true);
        }
        monthChooser = new JComboBox<>(s);
        monthChooser.addItemListener(new MonthChoosen());
        yearChooser = new JSpinner();
        yearChooser.setModel(new SpinnerNumberModel(calendar.get(Calendar.YEAR),
                calendar.getMinimum(Calendar.YEAR), 
                calendar.getMaximum(Calendar.YEAR), 
                1));
        yearChooser.addChangeListener(new YearChoosen());
        initButtons();
        initPanelForDateChooser();
        dateString = new JTextArea();
        dateString.setEditable(false);
        dateString.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        setActualDate(calendar);
        ImageIcon icon = new ImageIcon(getClass().getResource(
                "/chorke/proprietary/bet/apps/gui/icons/calendar.png"));
        
        dateCall = new JButton(icon);
        dateCall.setMargin(new Insets(0, 0, 0, 0));
        dateCall.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                initDateWin();
                Point p = ((JButton)e.getSource()).getLocationOnScreen();
                Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                Dimension date = datePanel.getSize();
                if(p.x < 0){
                    p.x = 0;
                }else if(p.x + date.width > screen.width){
                    p.x = screen.width - date.width;
                }
                if(p.y < 0){
                    p.y = 0;
                } else if(p.y + date.height > screen.height){
                    p.y = screen.height - date.height;
                }
                monthChooser.setSelectedIndex(calendar.get(Calendar.MONTH));
                yearChooser.setValue(calendar.get(Calendar.YEAR));
                dateWin.setLocation(p.x, p.y + dateCall.getHeight());
                dateWin.setVisible(true);
                tmpCal.setTimeInMillis(calendar.getTimeInMillis());
            }
        });
        GroupLayout gl = new GroupLayout(this);
        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addComponent(dateString, 100, 120, 2000)
                .addComponent(dateCall, 25, 25, 25));
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup()
                .addComponent(dateString, 20, 20, 2000)
                .addComponent(dateCall, 20, 20, 2000)));
        setLayout(gl);
    }
    
    /**
     * Inicializuje okno pre výber dátumu.
     */
    private void initDateWin(){
        dateWin = new JDialog();
        dateWin.setUndecorated(true);
        dateWin.add(datePanel);
        dateWin.addWindowFocusListener(focus);
        dateWin.pack();
        dateWin.setFocusable(false);
    }
    
    /**
     * Inicializuje panel pre výber dátumu.
     */
    private void initPanelForDateChooser(){
        datePanel = new JPanel();
        datePanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        initDaysPanel();
        JLabel leftArrow = getArrowForMonthFeed("<<", -1);
        JLabel rightArrow = getArrowForMonthFeed(">>", 1);
        
        GroupLayout gl = new GroupLayout(datePanel);
        gl.setHorizontalGroup(gl.createParallelGroup()
                .addGroup(gl.createSequentialGroup()
                    .addComponent(monthChooser, 140, 140, 140)
                    .addComponent(yearChooser, 100, 100, 120))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(leftArrow)
                    .addComponent(daysPanel, 200, 200, 220)
                    .addComponent(rightArrow)));
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup()
                    .addComponent(monthChooser)
                    .addComponent(yearChooser))
                .addGroup(gl.createParallelGroup()
                    .addGroup(Alignment.CENTER, gl.createSequentialGroup()
                        .addComponent(leftArrow))
                    .addComponent(daysPanel, 150, 150, 200)
                    .addGroup(Alignment.CENTER, gl.createSequentialGroup()
                        .addComponent(rightArrow))));
        datePanel.setLayout(gl);
    }
    
    /**
     * Pripraví JLabel ktorá bude mať text label a po kliknutí na ňu
     * posunie mesiac o monthFeed.
     * 
     * @param label text pre JLabel
     * @param monthFeed požadovaný posun po kliknutí.
     * @return 
     */
    private JLabel getArrowForMonthFeed(String label, int monthFeed){
        JLabel arrow = new JLabel(label);
        arrow.setFont(new Font(arrow.getFont().getName(), Font.PLAIN, 20));
        arrow.addMouseListener(new ArrowMouseListener(monthFeed));
        return arrow;
    }
    
    /**
     * Inicializuje panel, ktorý obsahuje mriežku s dňami.
     */
    private void initDaysPanel(){
        daysPanel = new JPanel();
        daysPanel.setMaximumSize(new Dimension(100,100));
        daysPanel.setLayout(new GridLayout(7, 7));
        daysName = new JLabel[7];
        Calendar c = (Calendar)calendar.clone();
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        for(int i = 0; i < 7; i++){
            daysName[i] = new JLabel(c.getDisplayName(Calendar.DAY_OF_WEEK, 
                    Calendar.SHORT, 
                    defaulLocale));
            daysName[i].setHorizontalAlignment(SwingConstants.CENTER);
            c.roll(Calendar.DAY_OF_WEEK, true);
        }
        putProperDays(calendar);
    }
    
    /**
     * Nastaví správne dni a na správne miesta pre daysPanel podľa kalendára cal.
     * Ak je {@link #putProperDaysNow} false, tak dni nedáva. 
     * @param cal 
     */
    private void putProperDays(Calendar cal){
        int start = whereIsStart(cal);
        today.setTimeInMillis(System.currentTimeMillis());
        boolean thisMonth = today.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
                && today.get(Calendar.MONTH) == cal.get(Calendar.MONTH);
        int todayDate = today.get(Calendar.DATE) - 1;
        daysPanel.removeAll();
        for(int i = 0; i < 7; i++){
            daysPanel.add(daysName[i]);
        }
        for(int i = 0; i < start; i++){
            daysPanel.add(new JLabel());
        }
        for(int i = 0; i < cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++){
            daysPanel.add(days[i]);
            days[i].setBackground(thisMonth && i == todayDate ?
                    todayColor : otherDaysColor);
        }
        for(int i = (cal.getActualMaximum(Calendar.DAY_OF_MONTH) 
                + start + 7); i < 49 ;i++){
            daysPanel.add(new JLabel());
        }
        daysPanel.paintAll(daysPanel.getGraphics());
    }
    
    /**
     * Vráti pozíciu, na ktorej by mal byť v mriežke zobrazený 1 deň
     * v mesiaci pre {@code cal}. Indexuje sa od 0.
     * @param cal
     * @return 
     */
    private static int whereIsStart(Calendar cal){
        int i;
        Calendar c = (Calendar)cal.clone();
        c.set(Calendar.DAY_OF_MONTH, 1);
        switch(c.get(Calendar.DAY_OF_WEEK)){
            case (Calendar.MONDAY):     i = 0; break;
            case (Calendar.TUESDAY):    i = 1; break;
            case (Calendar.WEDNESDAY):  i = 2; break;
            case (Calendar.THURSDAY):   i = 3; break;
            case (Calendar.FRIDAY):     i = 4; break;
            case (Calendar.SATURDAY):   i = 5; break;
            default:                    i = 6; break;
        }
        i += startedDay(cal);
        if(i > 6){ i -= 7; }
        return i;
    }
    
    /**
     * Vráti počiatočný deň v kalendári, resp. číslo jemu zodpovedajúce tak
     * aby z neho mohol byť určený index počiatočného dňa v mriežke.
     * @param cal
     * @return 
     */
    private static int startedDay(Calendar cal){
        switch(cal.getFirstDayOfWeek()){
            case (Calendar.MONDAY):     return 0;
            case (Calendar.TUESDAY):    return 6;
            case (Calendar.WEDNESDAY):  return 5;
            case (Calendar.THURSDAY):   return 4;
            case (Calendar.FRIDAY):     return 3;
            case (Calendar.SATURDAY):   return 2;
            default:                    return 1;
        }
    }
    
    /**
     * Nastaví dátum pre tento DateChooser. Nastaví príslušne podľa neho aj
     * deň mesiac a rok v príslušnych poliach.
     * @param cal 
     */
    public void setActualDate(Calendar cal){
        calendar = cal;
        monthChooser.setSelectedIndex(calendar.get(Calendar.MONTH));
        yearChooser.setValue(calendar.get(Calendar.YEAR));
        dateString.setText(simpleDateString());
    }
    
    /**
     * Vráti akutálne nastavený dátum.
     * Vrátený dátum obsahuje aj hodiny minúty a sekundy
     * ak nebola použitá metóda {@link setCalnedar(Calendar)}, tak
     * čas je nastavený podľa času inicializácie {@code DateChooser}
     * 
     * @return inštanciu {@link Calendar}, ktorá reprezentuje zvolený dátum
     */
    public Calendar getActualDate(){
        return calendar;
    }
    
    /**
     * Vráti jednoduchý string reťazec pre aktuálne nastavený dátum.
     * @return 
     */
    private String simpleDateString(){
        return DateFormat.getDateInstance(DateFormat.SHORT, defaulLocale)
                .format(calendar.getTime());
    }
    
    /**
     * Inicializuje tlačítka pre dni. 
     */
    private void initButtons(){
        int max = calendar.getMaximum(Calendar.DAY_OF_MONTH);
        days = new JButton[max];
        Font f = new Font("arial", Font.PLAIN, 10);
        for(int i = 0; i < max; i++){
            days[i] = new JButton(Integer.toString(i + 1));
            days[i].addActionListener(new DayChoosen(i + 1));
            days[i].setFont(f);
            days[i].setMargin(new Insets(0, 0, 0, 0));
        }
    }
    
    /**
     * Akcie, ktoré sa majú vykonať pri zvolení dňa. Nastaví sa dátum,
     * nastaví sa reťazec do poľa pre aktuálny dátum a zruší sa výberové okno.
     */
    private class DayChoosen implements ActionListener {
        private final int day;

        public DayChoosen(int day) {
            this.day = day;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.MONTH, monthChooser.getSelectedIndex());
            calendar.set(Calendar.YEAR, (Integer)yearChooser.getValue());
            if(dateWin != null){
                dateWin.dispose();
                dateWin = null;
            }
            dateString.setText(simpleDateString());
        }
    }
    
    /**
     * Akcia, ktorá bude vykonaná pri nastavení mesiaca. Nastaví sa dočasná
     * hodnota mesiaca, deň sa nastav na 1 a nastavia sa správne dni pre 
     * zobrazenie.
     */
    private class MonthChoosen implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            tmpCal.set(Calendar.DAY_OF_MONTH, 1);
            tmpCal.set(Calendar.MONTH, monthChooser.getSelectedIndex());
            putProperDays(tmpCal);
        }
    }
    
    /**
     * Akcia, ktorá bude vykonaná pri nastavení roku.
     * Nastaví sa dočasná hodnota roku a vložia sa správne dni do mriežky.
     * Mesiac ani deň nie sú upravované.
     */
    private class YearChoosen implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            tmpCal.set(Calendar.YEAR, (Integer)yearChooser.getValue());
            putProperDays(tmpCal);
        }
    }
    
    /**
     * Akcia pre výberové okno pri získaní/stratení zamerania.
     * Pri stratení sa okno zruší. Pri získaní sa nedeje nič.
     */
    private class Focus implements WindowFocusListener {

        @Override
        public void windowLostFocus(WindowEvent e) {
            if(dateWin != null){
                dateWin.dispose();
                dateWin = null;
            }
        }
        
        @Override
        public void windowGainedFocus(WindowEvent e) {}
    }
    
    /**
     * Po kliknutí posunie zobrazenie dátumu o stanovaný počet mesiacov.
     */
    private class ArrowMouseListener extends MouseAdapter{

        private int monthFeed;

        /**
         * Po kliknutí bude posúvať mesiace o monthFeed.
         * @param monthFeed 
         */
        public ArrowMouseListener(int monthFeed) {
            this.monthFeed = monthFeed;
        }
        
        @Override
        public void mouseClicked(MouseEvent e) {
            int reqMonth = monthChooser.getSelectedIndex() + monthFeed;
            int reqYear = (int)yearChooser.getModel().getValue();
            if(reqMonth > Calendar.DECEMBER){
                while(reqMonth > Calendar.DECEMBER){
                    reqMonth -= 12;
                    reqYear++;
                }
            } else if(reqMonth < Calendar.JANUARY){
                while(reqMonth < Calendar.JANUARY){
                    reqMonth += 12;
                    reqYear--;
                }
            }
            yearChooser.getModel().setValue(reqYear);
            monthChooser.setSelectedIndex(reqMonth);
        }
    }
}
