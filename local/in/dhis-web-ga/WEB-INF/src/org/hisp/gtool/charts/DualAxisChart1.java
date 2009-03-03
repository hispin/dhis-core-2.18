package org.hisp.gtool.charts;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.VerticalAlignment;

public class DualAxisChart1 {

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
	
	String[] series2;
	// Column Keys
	String[] categories2;

	// data...	
	double[][] data2;
	
	//	constructor
	public DualAxisChart1() { }

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
					
		for(int i=0;i<series2.length;i++)
		{
			for(int j=0;j<categories2.length;j++)
			{
				dataset.addValue(data2[i][j], series2[i], categories2[j]);
			}
		}
		
		return dataset;
	}
 public String getChartViewer(HttpServletRequest request, HttpServletResponse response) {
     final CategoryDataset dataset1 = getDataset1();

     // create the chart...
     final JFreeChart chart = ChartFactory.createBarChart(
         "",        // chart title
         "Category",               // domain axis label
         "Value",                  // range axis label
         dataset1,                 // data
         PlotOrientation.VERTICAL,
         true,                     // include legend
         true,                     // tooltips?
         false                     // URL generator?  Not required...
     );

     // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
 //    chart.setBackgroundPaint(Color.white);
//     chart.getLegend().setAnchor(Legend.SOUTH);

     /* TITLE */
     final TextTitle mainTitle = new TextTitle("Dual Axis Chart");
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
     
     // get a reference to the plot for further customisation...
     final CategoryPlot plot = chart.getCategoryPlot();
 //    plot.setBackgroundPaint(new Color(0xEE, 0xEE, 0xFF));
     plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);

     // customise the range axis...
     final NumberAxis rangeAxis1 = (NumberAxis) plot.getRangeAxis();
     rangeAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
     rangeAxis1.setAutoRangeIncludesZero(true);
     rangeAxis1.setUpperMargin(0.15);
     rangeAxis1.setLowerMargin(0.15);
     rangeAxis1.setLabel(yAxis_Title);
     
     final CategoryDataset dataset2 = getDataset2();
     plot.setDataset(1, dataset2);
     plot.mapDatasetToRangeAxis(1, 1);
     // customise the range axis...
     
     
     
     final CategoryAxis domainAxis = plot.getDomainAxis();
     domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
     domainAxis.setLabel(xAxis_Title);
     
     //final ValueAxis axis2 = new NumberAxis("Secondary");
     
     
     final NumberAxis rangeAxis2 = (NumberAxis) plot.getRangeAxis();
     rangeAxis2.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
     rangeAxis2.setAutoRangeIncludesZero(true);
     rangeAxis2.setUpperMargin(0.30);
     rangeAxis2.setLowerMargin(0.15);
     rangeAxis2.setLabel(yAxis_Title);
     plot.setRangeAxis(1, rangeAxis2);
     
     final LineAndShapeRenderer renderer2 = new LineAndShapeRenderer();
     renderer2.setToolTipGenerator(new StandardCategoryToolTipGenerator());
     renderer2.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());    
     renderer2.setItemLabelsVisible(true);
     
     plot.setRenderer(1, renderer2);
     plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
     
     
    
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

public String[] getCategories2() {
	return categories2;
}

public void setCategories2(String[] categories2) {
	this.categories2 = categories2;
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

public String[] getSeries2() {
	return series2;
}

public void setSeries2(String[] series2) {
	this.series2 = series2;
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
