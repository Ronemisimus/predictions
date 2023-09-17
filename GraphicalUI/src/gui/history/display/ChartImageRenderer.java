package gui.history.display;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChartImageRenderer {
    private final int width, height;
    private final String title;
    private final String xLabel, yLabel;

    private JFreeChart chart;

    public ChartImageRenderer(int width, int height, String title, String xLabel, String yLabel) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.chart = null;
    }

    public void buildXYChart(Map<Integer, Integer> data) {
        DefaultXYDataset dataset = new DefaultXYDataset();

        double[][] dataMap = new double[2][data.size()];
        List<Integer> sortedKeys = data.keySet().stream()
                .sorted().collect(Collectors.toList());
        IntStream.range(0, data.size()).forEach(i -> {
            dataMap[0][i] = sortedKeys.get(i);
            dataMap[1][i] = data.get(sortedKeys.get(i));
        });
        dataset.addSeries(title, dataMap);

        this.chart = ChartFactory.createXYAreaChart(
                title,     // Chart title
                xLabel,  // X-axis label
                yLabel,  // Y-axis label
                dataset,         // Your XYDataset
                PlotOrientation.VERTICAL,
                false,            // Show legend
                false,            // Show tooltips
                false            // Show URLs
        );
        XYPlot plot = chart.getXYPlot();
        plot.setRangeGridlinePaint(Color.gray);
        plot.setDomainGridlinePaint(Color.gray);
    }

    public BufferedImage createGraphImage() {

        // Customize the chart's appearance (optional)
        this.chart.setBackgroundPaint(Color.white);

        // Create a BufferedImage from the chart
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Paint the chart onto the image
        chart.draw(g2d, new Rectangle2D.Double(0, 0, width, height));

        // Dispose of the graphics context to free resources
        g2d.dispose();

        return image;
    }
}
