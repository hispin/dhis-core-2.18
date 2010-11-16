/**
 * 
 */
package org.hisp.dhis.web.api.service;

import java.util.List;

import org.hisp.dhis.web.api.model.ModelList;
import org.hisp.dhis.web.api.model.Program;

/**
 * @author abyotag_adm
 *
 */
public interface IProgramService {
	
	List<Program> getAllProgramsForLocale( String localeString );
	
	Program getProgramForLocale( int programId, String localeString );

}
