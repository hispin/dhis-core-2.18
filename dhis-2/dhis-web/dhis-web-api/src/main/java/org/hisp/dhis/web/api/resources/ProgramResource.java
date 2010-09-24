package org.hisp.dhis.web.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Produces;

import org.hisp.dhis.web.api.model.AbstractModelList;
import org.hisp.dhis.web.api.model.Program;
import org.hisp.dhis.web.api.service.IProgramService;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/programs")
public class ProgramResource {

	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	@Autowired
	private IProgramService iprogramService;
	
	// -------------------------------------------------------------------------
    // Resources
    // -------------------------------------------------------------------------
	
	@GET	
	@Produces( "application/vnd.org.dhis2.abstractmodellist+serialized" ) 
	public AbstractModelList getAllMobileDataSets(@HeaderParam("accept-language") String locale) 
	{	
	    return iprogramService.getAllProgramsForLocale( locale );
	}	
		
	@GET 
	@Path("{programid}")
	@Produces( "application/vnd.org.dhis2.program+serialized" )
	public Program getSelectedProgram(
			@PathParam("programid")  int programid,
			@HeaderParam("accept-language") String locale
			) 
	{		 
		return iprogramService.getProgramForLocale( programid, locale );
	}	
}
