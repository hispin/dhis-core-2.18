/*
 * Copyright (c) 2004-2009, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.patientchart;

import static org.hisp.dhis.chart.Chart.TYPE_BAR;

import java.awt.Color;
import java.awt.Font;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.math.stat.regression.SimpleRegression;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * @version $ DefaultPatientChartService.java Sep 4, 2011 7:13:19 PM $
 * 
 */
@Transactional
public class DefaultPatientChartService
    implements PatientChartService
{
    private static final Font titleFont = new Font( "Tahoma", Font.BOLD, 14 );

    private static final Color[] colors = { Color.decode( "#d54a4a" ), Color.decode( "#2e4e83" ),
        Color.decode( "#75e077" ), Color.decode( "#e3e274" ), Color.decode( "#e58c6d" ), Color.decode( "#df6ff3" ),
        Color.decode( "#88878e" ), Color.decode( "#6ff3e8" ), Color.decode( "#6fc3f3" ), Color.decode( "#aaf36f" ),
        Color.decode( "#9d6ff3" ), Color.decode( "#474747" ) };

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientChartStore patientChartStore;

    public void setPatientChartStore( PatientChartStore patientChartStore )
    {
        this.patientChartStore = patientChartStore;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    // -------------------------------------------------------------------------
    // ChartService implementation
    // -------------------------------------------------------------------------

    @Override
    public void deletePatientChart( PatientChart patientChart )
    {
        patientChartStore.delete( patientChart );
    }

    @Override
    public Collection<PatientChart> getAllPatientCharts()
    {
        return patientChartStore.getAll();
    }

    @Override
    public PatientChart getPatientChart( int id )
    {
        return patientChartStore.get( id );
    }

    @Override
    public PatientChart getPatientChartByTitle( String title )
    {
        return patientChartStore.getByTitle( title );
    }
    
    @Override
    public Collection<PatientChart> getPatientCharts( Collection<Program> programs )
    {
        return patientChartStore.getPatientCharts( programs );
    }

    @Override
    public int savePientChart( PatientChart patientChart )
    {
        return patientChartStore.save( patientChart );
    }

    @Override
    public void updatePatientChart( PatientChart patientChart )
    {
        patientChartStore.update( patientChart );
    }

    @Override
    public JFreeChart getJFreeChart( int id, I18nFormat format )
    {
        PatientChart patientChart = getPatientChart( id );

        patientChart.setFormat( format );

        return getJFreeChart( patientChart );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private JFreeChart getJFreeChart( PatientChart patientChart )
    {
        final BarRenderer barRenderer = getBarRenderer();
        final LineAndShapeRenderer lineRenderer = getLineRenderer();

        // ---------------------------------------------------------------------
        // Plot
        // ---------------------------------------------------------------------

        CategoryPlot plot = null;

        CategoryDataset[] dataSets = getCategoryDataSet( patientChart );

        if ( patientChart.isType( TYPE_BAR ) )
        {
            plot = new CategoryPlot( dataSets[0], new CategoryAxis(), new NumberAxis(), barRenderer );
        }
        else
        {
            plot = new CategoryPlot( dataSets[0], new CategoryAxis(), new NumberAxis(), lineRenderer );
        }

        if ( patientChart.isRegression() )
        {
            plot.setDataset( 1, dataSets[1] );
            plot.setRenderer( 1, lineRenderer );
        }

        JFreeChart jFreeChart = new JFreeChart( patientChart.getTitle(), titleFont, plot, true );

        // ---------------------------------------------------------------------
        // Plot orientation
        // ---------------------------------------------------------------------

        plot.setOrientation( PlotOrientation.VERTICAL );
        plot.setDatasetRenderingOrder( DatasetRenderingOrder.FORWARD );

        // ---------------------------------------------------------------------
        // Category label positions
        // ---------------------------------------------------------------------

        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryLabelPositions( CategoryLabelPositions.UP_45 );

        // ---------------------------------------------------------------------
        // Color & antialias
        // ---------------------------------------------------------------------

        jFreeChart.setBackgroundPaint( Color.WHITE );
        jFreeChart.setAntiAlias( true );

        return jFreeChart;
    }

    /**
     * Returns a bar renderer.
     */
    private BarRenderer getBarRenderer()
    {
        BarRenderer renderer = new BarRenderer();

        renderer.setMaximumBarWidth( 0.07 );

        for ( int i = 0; i < colors.length; i++ )
        {
            renderer.setSeriesPaint( i, colors[i] );
            renderer.setShadowVisible( false );
        }

        return renderer;
    }

    /**
     * Returns a line and shape renderer.
     */
    private LineAndShapeRenderer getLineRenderer()
    {
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();

        for ( int i = 0; i < colors.length; i++ )
        {
            renderer.setSeriesPaint( i, colors[i] );
        }

        return renderer;
    }

    /**
     * Returns a DefaultCategoryDataSet based on patient-data-values for the
     * patient-chart.
     */
    private CategoryDataset[] getCategoryDataSet( PatientChart patientChart )
    {
        final DefaultCategoryDataset regularDataSet = new DefaultCategoryDataset();
        final DefaultCategoryDataset regressionDataSet = new DefaultCategoryDataset();

        if ( patientChart != null )
        {           
            Collection<PatientDataValue> patientDataValues = patientDataValueService.getPatientDataValues( patientChart
                .getDataElement() );
            
            Iterator<PatientDataValue> iter = patientDataValues.iterator();

            while ( iter.hasNext() )
            {
                if ( !iter.next().getProgramStageInstance().getProgramInstance().getProgram().equals(
                    patientChart.getProgram() ) )
                {
                    iter.remove();
                }
            }

            // ---------------------------------------------------------
            // Regular dataset
            // ---------------------------------------------------------

            int columnIndex = 0;

            for ( PatientDataValue patientDataValue : patientDataValues )
            {
                final SimpleRegression regression = new SimpleRegression();

                regularDataSet.addValue( Double.parseDouble( patientDataValue.getValue() ), patientChart.getDataElement().getName(), patientChart.getFormat()
                    .formatDate( patientDataValue.getProgramStageInstance().getExecutionDate() ) );

                columnIndex++;

                regression.addData( columnIndex, Double.parseDouble( patientDataValue.getValue() ) );
            }
        }

        return new CategoryDataset[] { regularDataSet, regressionDataSet };
    }
}
