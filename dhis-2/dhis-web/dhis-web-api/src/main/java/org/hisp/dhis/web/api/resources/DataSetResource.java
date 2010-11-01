package org.hisp.dhis.web.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Produces;

import org.hisp.dhis.web.api.model.AbstractModelList;
import org.hisp.dhis.web.api.model.DataSet;
import org.hisp.dhis.web.api.model.DataSetValue;
import org.hisp.dhis.web.api.service.IDataSetService;
import org.hisp.dhis.web.api.service.IDataValueService;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/mobile-datasets")
public class DataSetResource {

	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	@Autowired
	private IDataSetService idataSetService;
	
	@Autowired
	private IDataValueService idataValueService;

	
	// -------------------------------------------------------------------------
    // Resources
    // -------------------------------------------------------------------------
	
	@GET	
	@Produces( "application/vnd.org.dhis2.abstractmodellist+serialized" ) 
	public AbstractModelList getAllMobileDataSets(@HeaderParam("accept-language") String locale) 
	{	
		return null;
//	    return idataSetService.getAllMobileDataSetsForLocale( locale );
	}	
		
	@GET 
	@Path("{datasetid}")
	@Produces( "application/vnd.org.dhis2.dataset+serialized" )
	public DataSet getSelectedDataSet(
			@PathParam("datasetid")  int datasetid,
			@HeaderParam("accept-language") String locale
			) 
	{		 
		return null;
//		return idataSetService.getDataSetForLocale( datasetid, locale );
	}	
	
	@POST
	@Path( "values" )
	@Consumes( "application/vnd.org.dhis2.datasetvalue+serialized" )
	@Produces("application/xml")	
	public String  getValues(DataSetValue dataSetValue) 
	{		
		return idataValueService.saveValues(dataSetValue);		
	}
}
