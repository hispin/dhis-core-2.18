package org.hisp.dhis.web.api.service;

import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.web.api.model.Program;

public interface IProgramService {
	
	public List<Program> getPrograms( OrganisationUnit unit, String localeString );
	
	public Program getProgram( int programId, String localeString );

}
