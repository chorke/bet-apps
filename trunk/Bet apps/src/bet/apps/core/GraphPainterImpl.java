package bet.apps.core;

import java.awt.Dimension;
import java.util.List;
import javax.swing.JPanel;

public class GraphPainterImpl implements GraphPainter{
    
    private Dimension dimOfGraph;
    private Dimension dimOfLegend;
    private int widthOfLeftMargin = 50;
    
    public GraphPainterImpl(Dimension d) {
        int h = Math.round(d.height * 0.8f);
        this.dimOfGraph = new Dimension(d.width, h);
        this.dimOfLegend = new Dimension(d.width, d.height - h);
    }
    
    @Override
    public JPanel paintGraph(List<PaintableGraph> graph){
        GraphPanel panel = new GraphPanel();
        panel.setDimOfGraph(dimOfGraph);
        panel.setDimOfLegend(dimOfLegend);
        panel.setWidthOfLeftMargin(widthOfLeftMargin);
        panel.setPaintableGraphs(graph);
        //panel.repaint();
        return panel;
    }
}