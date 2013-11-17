package bet.apps.windows;

import bet.apps.BetApps;
import bet.apps.core.DateMiner;
import bet.apps.core.MyDate;
import bet.apps.filters.DateFilter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

class NewFilterWin extends JFrame{
    
    private String name;
    private final JButton OKButton;
    private DateFilter.FilterRange defaultRange;
    private DateFilter.FilterType defaultType;
    private final ButtonGroup typeGroup = new ButtonGroup();
    private final ButtonGroup rangeGroup = new ButtonGroup();
    private final JTextField startField = new JTextField();
    private final JTextField endField = new JTextField();
    private final JCheckBox isNotFilter = new JCheckBox(BetApps.LABLES.getString("notFilter"));

    public NewFilterWin(String name, JButton OKButton, DateFilter filter) {
        if(filter != null){
            this.name = filter.getName();
            this.defaultRange = filter.getRange();
            this.defaultType = filter.getType();
            startField.setText(dateToField(filter.getStart()));
            endField.setText(dateToField(filter.getEnd()));
            this.isNotFilter.setSelected(filter.isIsNotFilter());
        } else {
            if(name != null){
                this.name = name;
            } else {
                this.name = "Filter";
            }
            this.defaultRange = DateFilter.FilterRange.EVERY_SAME;
            this.defaultType = DateFilter.FilterType.DATE;
            startField.setText(dateToField(DateMiner.INITIAL_DATE));
            endField.setText(DateMiner.getTodayAsString("."));
            isNotFilter.setSelected(false);
        }
        this.OKButton = OKButton;
        init();
    }
    
    private void init(){
        JPanel background = new JPanel();
        background.setLayout(new FlowLayout());
        
        JPanel typePanel = new JPanel();
        typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.Y_AXIS));
        typePanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), BetApps.LABLES.getString("type")));
        typePanel.setPreferredSize(new Dimension(180, 200));
//        DateFilter.FilterType[] types = DateFilter.FilterType.values();
        String[] types = DateFilter.localeFilterType();
        for(int i = 0; i < types.length; i++){
            JRadioButton b = new JRadioButton(types[i]);
            if(defaultType != null && defaultType.toString().equals(types[i])){
                b.setSelected(true);
            }
            b.setActionCommand(types[i].toString());
            typeGroup.add(b);
            typePanel.add(b);
        }
        
        JPanel rangePanel = new JPanel();
        rangePanel.setLayout(new BoxLayout(rangePanel, BoxLayout.Y_AXIS));
        rangePanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), BetApps.LABLES.getString("range")));
        rangePanel.setPreferredSize(new Dimension(140, 200));
//        DateFilter.FilterRange[] ranges = DateFilter.FilterRange.values();
        String[] ranges = DateFilter.localeFilterRange();
        for(int i = 0; i < ranges.length; i++){
            JRadioButton b = new JRadioButton(ranges[i]);
            if(defaultRange != null && defaultRange.toString().equals(ranges[i])){
                b.setSelected(true);
            }
            b.setActionCommand(ranges[i].toString());
            rangeGroup.add(b);
            rangePanel.add(b);
        }
        
        JPanel valuesPanel = new JPanel();
        valuesPanel.setLayout(new BoxLayout(valuesPanel, BoxLayout.Y_AXIS));
        valuesPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), BetApps.LABLES.getString("values")));
        valuesPanel.setPreferredSize(new Dimension(200, 200));
        
        JPanel startDatePanel = new JPanel();
        JLabel startLabel = new JLabel(BetApps.LABLES.getString("start"));
        startField.setPreferredSize(new Dimension(100,20));
        startDatePanel.add(startLabel);
        startDatePanel.add(startField);
        
        JPanel endDatePanel = new JPanel();
        JLabel endLabel = new JLabel(BetApps.LABLES.getString("end"));
        endField.setPreferredSize(new Dimension(100,20));
        endDatePanel.add(endLabel);
        endDatePanel.add(endField);
        
//        final String filtersName = name;
        
//        OKButton = new JButton("OK");
//        OKButton.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                ButtonModel typeModel = typeGroup.getSelection();
//                ButtonModel rangeModel = rangeGroup.getSelection();
//                String start = toProperStringDate(startField.getText());
//                String end = toProperStringDate(endField.getText());
//                if(typeModel == null || rangeModel == null){
//                    JOptionPane.showMessageDialog(window, "Choose type and range.");
//                } else if(start.isEmpty() || end.isEmpty()){
//                    JOptionPane.showMessageDialog(window, "Set correct date.");
//                } else {
//                    DateFilter.FilterRange range = DateFilter.FilterRange.valueOf(rangeModel.getActionCommand());
//                    DateFilter.FilterType type = DateFilter.FilterType.valueOf(typeModel.getActionCommand());
//                    
//                    MyDate startDate = MainWin.dateMiner.getDate(start);
//                    MyDate endDate = MainWin.dateMiner.getDate(end);
//                    
//                    DateFilter filter = new DateFilter(filtersName);
//                    filter.setEnd(endDate);
//                    filter.setStart(startDate);
//                    filter.setRange(range);
//                    filter.setType(type);
//                    filters.add(filter);
//                    model.addElement(filters.get(filters.size()-1).getName());
//                    window.dispose();
//                }
//            }
//        });
        
        valuesPanel.add(startDatePanel);
        valuesPanel.add(endDatePanel);
        valuesPanel.add(isNotFilter);
        valuesPanel.add(OKButton);
        
        background.add(typePanel);
        background.add(rangePanel);
        background.add(valuesPanel);
        add(background);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle(name);
        pack();
//        setVisible(true);
    }

    private String dateToField(MyDate date){
        return date.getDay() + "." + date.getMonth() + "." + date.getYear();
    }
    
    public ButtonGroup getRangeGroup() {
        return rangeGroup;
    }

    public ButtonGroup getTypeGroup() {
        return typeGroup;
    }

    public JTextField getStartField() {
        return startField;
    }

    public JTextField getEndField() {
        return endField;
    }
    
    public boolean isNotFilter(){
        return this.isNotFilter.isSelected();
    }
}
