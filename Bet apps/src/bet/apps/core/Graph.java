package bet.apps.core;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private List<Double> values;
    
    public Graph(){
        values = new ArrayList<>();
    }
    
    public void addValue(double value){
        addValueAt(value, -1);
    }
    
    public void addValueAt(double value, int position){
        if(position < 0){
            values.add(value);
        } else {
            values.add(position, value);
        }
    }
    
    public List<Double> getValues() {
        return values;
    }
    
    public double getMaxAbsValue(){
        int i = 0;
        if(values.size() > 0){
            for(int j = 1; j < values.size(); j++){
                if(Math.abs(values.get(j)) > Math.abs(values.get(i))){
                    i = j;
                }
            }
            return Math.abs(values.get(i));
        } else {
            return 0.0;
        }
    }
}