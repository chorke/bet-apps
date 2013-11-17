/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bet.apps.windows;

import bet.apps.BetApps;
import bet.apps.core.DateMiner;
import bet.apps.core.MyDate;
import bet.apps.filters.DateFilter;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Chorke
 */
public class FilterWin extends JFrame{
    
    private final List<DateFilter> filters;
    private DefaultListModel model = new DefaultListModel();
    @SuppressWarnings("unchecked")
    private JList listOfFilters = new JList(model);
    private final JButton removeButton = new JButton(BetApps.LABLES.getString("remove"));
    private final JButton newButton = new JButton(BetApps.LABLES.getString("new"));
    private final JButton alterButton = new JButton(BetApps.LABLES.getString("alter"));
    
    public FilterWin(List<DateFilter> filters) {
        this.filters = filters;
        init();
    }
    
    private void init(){
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1,2,20,30));
        mainPanel.setBorder(new EmptyBorder(10,10,10,10));
        
        JScrollPane textScrollPane = new JScrollPane(listOfFilters);
        
        setListOfFilters();
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), 
                BetApps.LABLES.getString("options")));
        
        MainWin.addToPanelWithGlue(buttonsPanel, newButton, removeButton, alterButton);
        
        newButton.setFocusable(false);
        newButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String name = "";
                boolean exist = true;
                while(exist){
                    name = (String)JOptionPane.showInputDialog(null,
                            BetApps.LABLES.getString("name"),
                            BetApps.LABLES.getString("name"),
                            JOptionPane.PLAIN_MESSAGE);
                    exist = exist(name);
                    if(exist){
                        JOptionPane.showMessageDialog(null, BetApps.MESSAGES.getString("existingName"));
                    }
                }
                if(name != null && !name.isEmpty()){
                    createNewFilter(name);
                }
            }
        });
        
        removeButton.setFocusable(false);
        removeButton.setEnabled(false);
        removeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selected = listOfFilters.getSelectedIndices();
                if(selected != null){
                    for(int i = selected.length - 1; i >= 0; i--){
                        model.remove(selected[i]);
                        filters.remove(selected[i]);
                    }
                }
            }
        });
        
        alterButton.setFocusable(false);
        alterButton.setEnabled(false);
        alterButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selected = listOfFilters.getSelectedIndices();
                if(selected != null){
                    for(int i = selected.length - 1; i >= 0; i--){
                        alterOldFilter(filters.get(selected[i]));
                    }
                }
            }
        });
        
        listOfFilters.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(listOfFilters.getSelectedIndex() == -1){
                    removeButton.setEnabled(false);
                    alterButton.setEnabled(false);
                } else {
                    removeButton.setEnabled(true);
                    alterButton.setEnabled(true);
                }
            }
        });
        
        mainPanel.add(textScrollPane);
        mainPanel.add(buttonsPanel);
        
        add(mainPanel);
        
        setResizable(false);
        setLocationRelativeTo(null);
        setSize(500,300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(BetApps.LABLES.getString("filters"));
    }
    
    @SuppressWarnings("unchecked")
    private void setListOfFilters(){
        //model.removeAllElements();
        for(int i = 0; i < filters.size(); i++){
            model.addElement(filters.get(i).getName());
        }
    }
    @SuppressWarnings("unchecked")
    private void createNewFilter(String name){
        JButton OKButton = new JButton(BetApps.LABLES.getString("ok"));
        final NewFilterWin window = new NewFilterWin(name, OKButton, null);
        
        final String filtersName = name;
        OKButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ButtonModel typeModel = window.getTypeGroup().getSelection();
                ButtonModel rangeModel = window.getRangeGroup().getSelection();
                String start = toProperStringDate(window.getStartField().getText());
                String end = toProperStringDate(window.getEndField().getText());
                if(typeModel == null || rangeModel == null){
                    JOptionPane.showMessageDialog(window, BetApps.MESSAGES.getString("typeAndRange"));
                } else if(start.isEmpty() || end.isEmpty()){
                    JOptionPane.showMessageDialog(window, BetApps.MESSAGES.getString("correctDate"));
                } else {
//                    DateFilter.FilterRange range = DateFilter.FilterRange.valueOf(rangeModel.getActionCommand());
//                    DateFilter.FilterType type = DateFilter.FilterType.valueOf(typeModel.getActionCommand());
                    
                    DateFilter.FilterRange range = DateFilter.localeValueOfRange(rangeModel.getActionCommand());
                    DateFilter.FilterType type = DateFilter.localeValueOfType(typeModel.getActionCommand());
                    
                    MyDate startDate = MainWin.DATE_MINER.getDate(start);
                    MyDate endDate = MainWin.DATE_MINER.getDate(end);
                    
                    boolean isNotFilter = window.isNotFilter();
                    
                    DateFilter filter = new DateFilter(filtersName);
                    filter.setEnd(endDate);
                    filter.setStart(startDate);
                    filter.setRange(range);
                    filter.setType(type);
                    filter.setIsNotFilter(isNotFilter);
                    
                    filters.add(filter);
                    model.addElement(filters.get(filters.size()-1).getName());
                    window.dispose();
                }
            }
        });
        window.setVisible(true);
    }
    
    private void alterOldFilter(DateFilter filter){
        JButton OKButton = new JButton(BetApps.LABLES.getString("ok"));
        final NewFilterWin window = new NewFilterWin(filter.getName(), OKButton, filter);
        final DateFilter finalFilter = filter;
        OKButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ButtonModel typeModel = window.getTypeGroup().getSelection();
                ButtonModel rangeModel = window.getRangeGroup().getSelection();
                String start = toProperStringDate(window.getStartField().getText());
                String end = toProperStringDate(window.getEndField().getText());
                if(typeModel == null || rangeModel == null){
                    JOptionPane.showMessageDialog(window, BetApps.MESSAGES.getString("typeAndRange"));
                } else if(start.isEmpty() || end.isEmpty()){
                    JOptionPane.showMessageDialog(window, BetApps.MESSAGES.getString("correctDate"));
                } else {
//                    DateFilter.FilterRange range = DateFilter.FilterRange.valueOf(rangeModel.getActionCommand());
//                    DateFilter.FilterType type = DateFilter.FilterType.valueOf(typeModel.getActionCommand());
                    
                    DateFilter.FilterRange range = DateFilter.localeValueOfRange(rangeModel.getActionCommand());
                    DateFilter.FilterType type = DateFilter.localeValueOfType(typeModel.getActionCommand());
                    
                    MyDate startDate = MainWin.DATE_MINER.getDate(start);
                    MyDate endDate = MainWin.DATE_MINER.getDate(end);
                    
                    boolean isNotFilter = window.isNotFilter();
                    
                    finalFilter.setEnd(endDate);
                    finalFilter.setStart(startDate);
                    finalFilter.setRange(range);
                    finalFilter.setType(type);
                    finalFilter.setIsNotFilter(isNotFilter);
                    window.dispose();
                }
            }
        });
        
        window.setVisible(true);
    }
    
    private String toProperStringDate(String date){
        if(date.isEmpty()){
            return DateMiner.getTodayAsString("_");
        }
        String[] splited = date.split("[ .]+");
        if(splited.length != 3){
            return "";
        }
        int day, month, year;
        try{
            day = Integer.valueOf(splited[0]);
            month = Integer.valueOf(splited[1]);
            year = Integer.valueOf(splited[2]);
        } catch (NumberFormatException ex){
            return "";
        }
        return day + "_" + month + "_" + year;
    }
    
    private boolean exist(String name){
        if(name == null || name.isEmpty()){
            return false;
        }
        for(int i = 0; i < filters.size(); i++){
            if(filters.get(i).getName().equals(name)){
                return true;
            }
        }
        return false;
    }
 }
