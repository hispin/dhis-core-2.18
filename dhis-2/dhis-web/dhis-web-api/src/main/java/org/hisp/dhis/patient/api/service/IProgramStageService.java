package org.hisp.dhis.patient.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.patient.api.model.Form;
import org.hisp.dhis.patient.api.model.IDataElement;
import org.hisp.dhis.patient.api.model.IProgramStage;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageService;
import org.springframework.beans.factory.annotation.Autowired;

public class IProgramStageService {

	@Autowired
	private ProgramStageService programStageService;

	public List<IProgramStage> getAllForms() {

		List<IProgramStage> forms = new ArrayList<IProgramStage>();

		List<ProgramStage> programStages = (List<ProgramStage>) programStageService.getAllProgramStages();

		for (ProgramStage programStage : programStages) {

			IProgramStage form = new IProgramStage();

			form.setId(programStage.getId());
			form.setName(programStage.getName());

			forms.add(form);

		}

		return forms;
	}
	
	public Form getForm(int programStageId)
	{		
		ProgramStage programStage = programStageService.getProgramStage( programStageId );
		
		Collection<ProgramStageDataElement> dataElements = programStage.getProgramStageDataElements();
		
		Form form = new Form();
		List<IDataElement> des = new ArrayList<IDataElement>();
		form.setFormId( programStage.getId() );
		form.setFormName( programStage.getName() );
		
		for( ProgramStageDataElement programStageDataElement : dataElements )
		{
			IDataElement de = new IDataElement();
			de.setDeId( programStageDataElement.getDataElement().getId() );
			de.setDeName( programStageDataElement.getDataElement().getName() );
			de.setDeType( programStageDataElement.getDataElement().getType() );
			des.add( de );
		}
		
		form.setDataElements(des);	
		
		return form;
	}
}
