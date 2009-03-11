package org.hisp.dhis.gis.action.dataanlysis;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.datamart.DataMartStore;
import org.hisp.dhis.gis.FeatureService;
import org.hisp.dhis.gis.GISConfiguration;
import org.hisp.dhis.gis.GISConfigurationService;
import org.hisp.dhis.gis.Legend;
import org.hisp.dhis.gis.LegendService;
import org.hisp.dhis.gis.LegendSet;
import org.hisp.dhis.gis.comparator.LegendComparator;
import org.hisp.dhis.gis.ext.BagSession;
import org.hisp.dhis.gis.ext.Feature;
import org.hisp.dhis.gis.ext.FeatureValueComparator;
import org.hisp.dhis.gis.state.SelectionManager;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.jdbc.StatementManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.aggregation.AggregationService;

import com.opensymphony.xwork.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id: FillMapByIndicatorAction.java 28-04-2008 16:06:00 $
 */
public class FillMapByIndicatorAction implements Action {
	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------

	private FeatureService featureService;

	private OrganisationUnitSelectionManager selectionManager;

	private SelectionManager selectionGISManager;

	private StatementManager statementManager;

	private IndicatorService indicatorService;

	private DataMartStore dataMartStore;

	private LegendService legendService;

	private PeriodService periodService;

	private I18nFormat format;

	private AggregationService aggregationService;

	private GISConfigurationService gisConfigurationService;

	// -------------------------------------------------------------------------
	// Input
	// -------------------------------------------------------------------------

	private Integer indicatorId;

	private String startDate;

	private String endDate;

	private Integer periodId;

	private NumberFormat formatter = new DecimalFormat("#0.00");

	// -------------------------------------------------------------------------
	// Output
	// -------------------------------------------------------------------------

	private List<Feature> features = new ArrayList<Feature>();

	private LegendSet legendSet;

	// -------------------------------------------------------------------------
	// Getter & setter
	// -------------------------------------------------------------------------

	public void setAggregationService(AggregationService aggregationService) {
		this.aggregationService = aggregationService;
	}

	public void setPeriodId(Integer periodId) {
		this.periodId = periodId;
	}

	public void setGisConfigurationService(
			GISConfigurationService gisConfigurationService) {
		this.gisConfigurationService = gisConfigurationService;
	}

	public void setStatementManager(StatementManager statementManager) {
		this.statementManager = statementManager;
	}

	public void setSelectionGISManager(SelectionManager selectionGISManager) {
		this.selectionGISManager = selectionGISManager;
	}

	public void setLegendService(LegendService legendService) {
		this.legendService = legendService;
	}

	public void setSelectionManager(
			OrganisationUnitSelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public LegendSet getLegendSet() {
		return legendSet;
	}

	public void setDataMartStore(DataMartStore dataMartStore) {
		this.dataMartStore = dataMartStore;
	}

	public void setIndicatorService(IndicatorService indicatorService) {
		this.indicatorService = indicatorService;
	}

	public void setIndicatorId(Integer indicatorId) {
		this.indicatorId = indicatorId;
	}

	public void setFeatureService(FeatureService featureService) {
		this.featureService = featureService;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public void setPeriodService(PeriodService periodService) {
		this.periodService = periodService;
	}

	public void setFormat(I18nFormat format) {
		this.format = format;
	}

	private BagSession bagSession = new BagSession();

	private void autoFixLegendSet(double maxValue) {

		Legend maxLegend = Collections.max(legendSet.getLegends(),
				new LegendComparator());

		if (maxValue > maxLegend.getMax()) {

			if (maxLegend.getAutoCreateMax() == Legend.AUTO_CREATE_MAX) {

				maxLegend
						.setMax(Double.parseDouble(formatter.format(maxValue)));

			} else {
				int red = new Integer((int) (Math.random() * 255)).intValue();
				int green = new Integer((int) (Math.random() * 255)).intValue();
				int blue = new Integer((int) (Math.random() * 255)).intValue();

				String color = (Integer.toHexString(red)
						+ Integer.toHexString(green) + Integer
						.toHexString(blue)).toUpperCase();

				Legend legendNew = new Legend("Fix", color, maxLegend.getMax(),
						Double.parseDouble(formatter.format(maxValue)));

				legendSet.addLegend(legendNew);
			}
		}

	}

	private LegendSet createLegendSet(double min, double max) {

		double section = (max - min) / 5;

		Legend l1 = new Legend("00FFFF", Double.parseDouble(formatter
				.format(min)), Double.parseDouble(formatter.format(min
				+ section)));
		min += section;
		Legend l2 = new Legend("00CCFF", Double.parseDouble(formatter
				.format(min)), Double.parseDouble(formatter.format(min
				+ section)));
		min += section;
		Legend l3 = new Legend("0066FF", Double.parseDouble(formatter
				.format(min)), Double.parseDouble(formatter.format(min
				+ section)));
		min += section;
		Legend l4 = new Legend("0000FF", Double.parseDouble(formatter
				.format(min)), Double.parseDouble(formatter.format(min
				+ section)));
		min += section;
		Legend l5 = new Legend("3300CC", Double.parseDouble(formatter
				.format(min)), Double.parseDouble(formatter.format(min
				+ section)));

		LegendSet legendSet = new LegendSet("Default");
		legendSet.addLegend(l1);
		legendSet.addLegend(l2);
		legendSet.addLegend(l3);
		legendSet.addLegend(l4);
		legendSet.addLegend(l5);

		return legendSet;

	}

	private double getIndicatorValue(Indicator indicator, Date startdate,
			Date enddate, OrganisationUnit organisationUnit, Period period) {

		if (gisConfigurationService.getValue(GISConfiguration.KEY_GETINDICATOR)
				.equalsIgnoreCase(GISConfiguration.AggregationService)) {

			return aggregationService.getAggregatedIndicatorValue(indicator,
					startdate, enddate, organisationUnit);
		}

		return dataMartStore.getAggregatedValue(indicator, period,
				organisationUnit);
	}

	public String execute() throws Exception {
		Period period = null;
		
		if (periodId != null) {
			period = periodService.getPeriod(periodId);
		}
		statementManager.initialise();

		OrganisationUnit organisationUnit = selectionManager
				.getSelectedOrganisationUnit();

		Indicator indicator = indicatorService.getIndicator(new Integer(
				indicatorId).intValue());

		selectionGISManager.setSelectedIndicator(indicator);

		Date startdate = format.parseDate(startDate);

		Date enddate = format.parseDate(endDate);

		for (OrganisationUnit org : organisationUnit.getChildren()) {

			double indicatorValue = getIndicatorValue(indicator, startdate,
					enddate, org, period);

			if (indicatorValue < 0.0) {

				indicatorValue = 0;
			}

			org.hisp.dhis.gis.Feature feature = featureService.get(org);

			features.add(new Feature(feature, indicatorValue, "#CCCCCC"));

		}

		double max = Collections.max(features, new FeatureValueComparator())
				.getAggregatedDataValue();

		double min = Collections.min(features, new FeatureValueComparator())
				.getAggregatedDataValue();

		legendSet = legendService.getLegendSet(indicator);

		if (legendSet == null) {
			legendSet = createLegendSet(min, max);
		} else {
			autoFixLegendSet(max);
		}

		legendSet.sortLegend(new LegendComparator());

		for (Feature feature : features) {
			for (Legend legend : legendSet.getLegends()) {

				if (feature.getAggregatedDataValue() >= legend.getMin()
						&& feature.getAggregatedDataValue() <= legend.getMax()) {
					feature.setColor("#" + legend.getColor());
				}
			}

			feature.setAggregatedDataValue(new Double(formatter.format(feature
					.getAggregatedDataValue())).doubleValue());

		}

		if (gisConfigurationService.getValue(GISConfiguration.KEY_GETINDICATOR)
				.equalsIgnoreCase(GISConfiguration.AggregationService)) {

			this.bagSession.setStartDate(format.formatDate(startdate));
			this.bagSession.setEndDate(format.formatDate(enddate));

		} else {
			this.bagSession.setStartDate(format.formatDate( period.getStartDate()));
			this.bagSession.setEndDate(format.formatDate( period.getStartDate()));
		}
		
		this.bagSession.setIndicator( indicator );
		this.bagSession.setFeatures( features );
		this.bagSession.setLegendSet( legendSet );
		
		selectionGISManager.setSeletedBagSession(this.bagSession);

		
		statementManager.destroy();

		return SUCCESS;
	}

}
