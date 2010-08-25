package org.hisp.dhis.web.api.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hisp.dhis.web.api.model.Form;
import org.hisp.dhis.web.api.service.ProgramStageService;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/forms")
public class FormsResource {

	@Autowired
	private ProgramStageService programStageService;
	
	@GET	
	@Produces(MediaType.APPLICATION_XML)	
	public List<Form> getAllForms() {
	    return programStageService.getAllForms();
	}	
		
	@GET 
	@Path("{formid}")
	@Produces(MediaType.APPLICATION_XML)	
	public Form getSelectedForm(@PathParam("formid")  int formid ) {
		return programStageService.getForm( formid );
	}
	  

}
