package org.hisp.dhis.de.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryComboService;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueKey;
import org.hisp.dhis.hibernate.HibernateSessionManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;


/**
 * @author Latifov Murodillo Abdusamadovich
 * @version $Id: ViewDataValueAuditPopupForm.java 15-04-2009 $
 */

public class ViewDataValueAuditPopupAction
implements Action
{
	private Integer organisationUnitId;
	private Integer dataElementComboId;
	private Integer dataElementId;
	private Integer periodId;
	private List<DataValue> dataValues;
	private DataValueKey dvk;
	private DataValue dataValue;
	private OrganisationUnit organisationUnit;
	private Period period;
	private DataElementCategoryOptionCombo dataElementCategoryOptionCombo;
	private DataElement dataElement;
	
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

	public DataElementCategoryOptionCombo getDataElementCategoryOptionCombo() {
		return dataElementCategoryOptionCombo;
	}

	public void setDataElementCategoryOptionCombo(
			DataElementCategoryOptionCombo dataElementCategoryOptionCombo) {
		this.dataElementCategoryOptionCombo = dataElementCategoryOptionCombo;
	}

	private HibernateSessionManager sessionManager;

    public void setSessionManager( HibernateSessionManager sessionManager )
    {
        this.sessionManager = sessionManager;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private DataElementCategoryOptionComboService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(
			DataElementCategoryOptionComboService dataElementCategoryOptionComboService) {
		this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
	}

	private PeriodService periodService;

	public void setPeriodService(PeriodService periodService) {
		this.periodService = periodService;
	}
	
    private OrganisationUnitService organisationUnitService;

	public void setOrganisationUnitService(
			OrganisationUnitService organisationUnitService) {
		this.organisationUnitService = organisationUnitService;
	}


	public DataElement getDataElement() {
		return dataElement;
	}

	public void setDataElement(DataElement dataElement) {
		this.dataElement = dataElement;
	}

	public OrganisationUnit getOrganisationUnit() {
		return organisationUnit;
	}

	public void setOrganisationUnit(OrganisationUnit organisationUnit) {
		this.organisationUnit = organisationUnit;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}
	
	public DataValue getDataValue() {
		return dataValue;
	}

	public void setDataValue(DataValue dataValue) {
		this.dataValue = dataValue;
	}


    
	public List<DataValue> getDataValues() {
		return dataValues;
	}


	public void setDataValues(List<DataValue> dataValues) {
		this.dataValues = dataValues;
	}


	public DataValueKey getDvk() {
		return dvk;
	}


	public void setDvk(DataValueKey dvk) {
		this.dvk = dvk;
	}

    public Integer getOrganisationUnitId() {
		return organisationUnitId;
	}


	public void setOrganisationUnitId(Integer organisationUnitId) {
		this.organisationUnitId = organisationUnitId;
	}


	public Integer getDataElementComboId() {
		return dataElementComboId;
	}


	public void setDataElementComboId(Integer dataElementComboId) {
		this.dataElementComboId = dataElementComboId;
	}


	public Integer getDataElementId() {
		return dataElementId;
	}


	public void setDataElementId(Integer dataElementId) {
		this.dataElementId = dataElementId;
	}


	public Integer getPeriodId() {
		return periodId;
	}


	public void setPeriodId(Integer periodId) {
		this.periodId = periodId;
	}


	public HibernateSessionManager getSessionManager() {
		return sessionManager;
	}


	public String execute()
        throws Exception
    {
		dataElement = dataElementService.getDataElement(getDataElementId());
		organisationUnit = organisationUnitService.getOrganisationUnit(getOrganisationUnitId());
		period = periodService.getPeriod(getPeriodId());
		dataElementCategoryOptionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo(getDataElementComboId());
		
		Session session = sessionManager.getCurrentSession();
		
		AuditReader reader = AuditReaderFactory.get(session);
		dvk = new DataValueKey();
		dvk.setCategoryoptioncomboid(getDataElementComboId());
		dvk.setDataElementid(getDataElementId());
		dvk.setPeriodid(getPeriodId());
		dvk.setSourceid(getOrganisationUnitId());
		
		List<Number> revs = reader.getRevisions(DataValue.class, dvk );
		dataValues = new ArrayList<DataValue>();
		
		for(int i=0; i<revs.size();i++){
			dataValues.add(reader.find(DataValue.class, dvk, revs.get(i)));
		}
		
		return SUCCESS;
    }
}
