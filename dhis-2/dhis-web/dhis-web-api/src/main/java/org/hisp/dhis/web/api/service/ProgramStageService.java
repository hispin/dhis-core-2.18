package org.hisp.dhis.web.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.web.api.model.Form;
import org.hisp.dhis.web.api.model.DataElement;
import org.springframework.beans.factory.annotation.Autowired;

public class ProgramStageService {

	@Autowired
	private org.hisp.dhis.program.ProgramStageService programStageService;

	public List<Form> getAllForms() {

		List<Form> forms = new ArrayList<Form>();

		for (org.hisp.dhis.program.ProgramStage programStage : programStageService.getAllProgramStages()) {

			Form modelProgramStage = new Form();

			modelProgramStage.setId( programStage.getId());
			modelProgramStage.setName( programStage.getName());

			forms.add(modelProgramStage);

		}

		return forms;
	}
	
	public Form getForm(int programStageId)
	{		
		org.hisp.dhis.program.ProgramStage programStage = programStageService.getProgramStage( programStageId );
		
		Collection<ProgramStageDataElement> dataElements = programStage.getProgramStageDataElements();
		
		Form modelProgramStage = new Form();
		List<DataElement> des = new ArrayList<DataElement>();
		modelProgramStage.setId( programStage.getId() );
		modelProgramStage.setName( programStage.getName() );
		
		for( ProgramStageDataElement programStageDataElement : dataElements )
		{
			DataElement de = new DataElement();
			de.setId( programStageDataElement.getDataElement().getId() );
			de.setName( programStageDataElement.getDataElement().getName() );
			de.setType( programStageDataElement.getDataElement().getType() );
			des.add( de );
		}
		
		modelProgramStage.setDataElements(des);	
		
		return modelProgramStage;
	}
}
