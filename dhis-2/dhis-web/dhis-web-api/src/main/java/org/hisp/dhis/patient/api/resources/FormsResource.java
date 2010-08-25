package org.hisp.dhis.patient.api.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hisp.dhis.patient.api.model.Form;
import org.hisp.dhis.patient.api.model.IProgramStage;
import org.hisp.dhis.patient.api.service.IProgramStageService;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/forms")
public class FormsResource {

	@Autowired
	private IProgramStageService iProgramStageService;
	
	@GET	
	@Produces(MediaType.TEXT_XML)	
	public List<IProgramStage> getAllFormsXML() {

		List<IProgramStage> forms = new ArrayList<IProgramStage>();

		forms.addAll(iProgramStageService.getAllForms());

		return forms;
	}	
		
	@GET 
	@Path("{formid}")
	@Produces(MediaType.TEXT_XML)	
	public Form getSelectedFormXML(@PathParam("formid")  int formid ) {
		return iProgramStageService.getForm( formid );
	}
	  

}
