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
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.Rotation;

public class PieChart3D1 {

	//	 categories...
	String[] section;
	// data...
	double[] data;

	String chartTitle;
	String xAxis_Title;
	String yAxis_Title;
	
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


	//	constructor
	public PieChart3D1() { }

	
	private DefaultPieDataset getDataset() 
	{    
		// create the dataset...
		DefaultPieDataset dataset = new DefaultPieDataset();
		for (int i = 0; i < data.length; i++) 
			{
				dataset.setValue(section[i], data[i]);
			}
		return dataset;
	}

 public String getChartViewer(HttpServletRequest request, HttpServletResponse response) {
   DefaultPieDataset dataset = getDataset();
   // create the chart...
   final JFreeChart chart = ChartFactory.createPieChart3D(
           "",  // chart title
           dataset,                // data
           true,                   // include legend
           true,
           false
       );

   /* TITLE */
   final TextTitle mainTitle = new TextTitle("Pie Chart 3D");
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
   
       final PiePlot3D plot = (PiePlot3D) chart.getPlot();
       plot.setStartAngle(290);
       plot.setDirection(Rotation.CLOCKWISE);
       plot.setForegroundAlpha(0.5f);
       plot.setNoDataMessage("No data to display");

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


public double[] getData() {
	return data;
}


public void setData(double[] data) {
	this.data = data;
}


public String[] getSection() {
	return section;
}


public void setSection(String[] section) {
	this.section = section;
}

}
