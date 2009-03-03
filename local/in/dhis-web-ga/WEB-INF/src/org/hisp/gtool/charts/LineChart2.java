package org.hisp.gtool.charts;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.VerticalAlignment;

public class LineChart2 {

	//	 Data Related Bar Chart
	//	Row Keys
	String[] series1;
	// Column Keys
	String[] categories1;
	// data...	
	double[][] data1;
	
	String chartTitle;
	String xAxis_Title;
	String yAxis_Title;
	
	// Data Related Line Chart

	/*
	String[] series2;
	// Column Keys
	String[] categories2;
	*/
	// data...	
	double[][] data2;
	
	//	constructor
	public LineChart2() { }

	// Function For Creating Dataset for Barchart
	private DefaultCategoryDataset  getDataset1() 
	{    
		
		//	create the dataset...
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
					
		for(int i=0;i<series1.length;i++)
		{
			for(int j=0;j<categories1.length;j++)
			{
				dataset.addValue(data1[i][j], series1[i], categories1[j]);
			}
		}
		
		return dataset;
	}
	
	// Function For Creating Dataset for Linechart
	private DefaultCategoryDataset  getDataset2() 
	{    
		
		//	create the dataset...
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
					
		for(int i=0;i<series1.length;i++)
		{
			series1[i]+="(Target)";	
			for(int j=0;j<categories1.length;j++)
			{
				dataset.addValue(data2[i][j], series1[i], categories1[j]);
			}
		}
		
		return dataset;
	}
public String getChartViewer(HttpServletRequest request, HttpServletResponse response) {
    
	 final CategoryDataset dataset1 = getDataset1();

    // create the first plot...
    final CategoryItemRenderer renderer = new LineAndShapeRenderer();
    renderer.setToolTipGenerator(new StandardCategoryToolTipGenerator());
    renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());    
    renderer.setItemLabelsVisible(true); 
    
    final CategoryPlot plot = new CategoryPlot();
    plot.setDataset(dataset1);
    plot.setRenderer(renderer);
    
    plot.setDomainAxis(new CategoryAxis(xAxis_Title));
    plot.setRangeAxis(new NumberAxis(yAxis_Title));
    
    plot.setOrientation(PlotOrientation.VERTICAL);
    plot.setRangeGridlinesVisible(true);
    plot.setDomainGridlinesVisible(true);
    
    // customise the range axis...
    final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    rangeAxis.setAutoRangeIncludesZero(true);
    rangeAxis.setUpperMargin(0.15);
    rangeAxis.setLowerMargin(0.15);
    
    final CategoryDataset dataset2 = getDataset2();
	 
    final CategoryItemRenderer renderer2 = new LineAndShapeRenderer();
    renderer2.setSeriesStroke(0, new BasicStroke(2.0f));
    renderer2.setSeriesStroke(1, new BasicStroke(2.0f));
    plot.setDataset(1, dataset2);
    plot.setRenderer(1, renderer2);
    plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    
 //   plot.setBackgroundPaint(Color.lightGray);
 //   plot.setRangeGridlinePaint(Color.white);
    
    final JFreeChart chart = new JFreeChart(plot);
    //chart.setTitle("Overlaid Bar Chart");
//    chart.setLegend(new StandardLegend());
//    chart.setBackgroundPaint(Color.white);

    /* TITLE */
    final TextTitle mainTitle = new TextTitle("Overlaid Bar Chart");
    mainTitle.setFont(new Font("times", Font.BOLD, 13));
    mainTitle.setPosition(RectangleEdge.TOP);     
    mainTitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    chart.addSubtitle(mainTitle);
    
    /* SUB TITLE */     
    final TextTitle subtitle = new TextTitle(chartTitle);
    subtitle.setFont(new Font("times", Font.BOLD, 13));
    subtitle.setPosition(RectangleEdge.TOP);     
    subtitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    chart.addSubtitle(subtitle);
    
    final CategoryAxis domainAxis = plot.getDomainAxis();
    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
    
    // OPTIONAL CUSTOMISATION COMPLETED.


  ChartRenderingInfo info = null;
  HttpSession session = request.getSession();
  try {

    //Create RenderingInfo object
    response.setContentType("text/html");
    info = new ChartRenderingInfo(new StandardEntityCollection());
    BufferedImage chartImage = chart.createBufferedImage(800, 500, info);

    // putting chart as BufferedImage in session, 
    // thus making it available for the image reading action Action.
    session.setAttribute("chartImage", chartImage);

    PrintWriter writer = new PrintWriter(response.getWriter());
    
    
    ChartUtilities.writeImageMap(writer,"imageMap",info,true);
    
    
    writer.flush();
  
  }
  catch (Exception e) {
     // handel your exception here
  }
 
  String pathInfo = "http://";
  pathInfo += request.getServerName();
  int port = request.getServerPort();
  pathInfo += ":"+String.valueOf(port);
  pathInfo += request.getContextPath();
  String chartViewer = pathInfo + "/servlet/ChartViewer";
  return chartViewer;
}




public String getChartTitle() {
	return chartTitle;
}

public void setChartTitle(String chartTitle) {
	this.chartTitle = chartTitle;
}

public String[] getCategories1() {
	return categories1;
}

public void setCategories1(String[] categories1) {
	this.categories1 = categories1;
}


public double[][] getData1() {
	return data1;
}

public void setData1(double[][] data1) {
	this.data1 = data1;
}

public double[][] getData2() {
	return data2;
}

public void setData2(double[][] data2) {
	this.data2 = data2;
}

public String[] getSeries1() {
	return series1;
}

public void setSeries1(String[] series1) {
	this.series1 = series1;
}

public String getXAxis_Title() {
	return xAxis_Title;
}

public void setXAxis_Title(String axis_Title) {
	xAxis_Title = axis_Title;
}

public String getYAxis_Title() {
	return yAxis_Title;
}

public void setYAxis_Title(String axis_Title) {
	yAxis_Title = axis_Title;
}


}
