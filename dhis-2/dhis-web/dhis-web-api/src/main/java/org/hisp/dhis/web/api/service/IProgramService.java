/**
 * 
 */
package org.hisp.dhis.web.api.service;

import org.hisp.dhis.web.api.model.AbstractModelList;
import org.hisp.dhis.web.api.model.Program;

/**
 * @author abyotag_adm
 *
 */
public interface IProgramService {
	
	AbstractModelList getAllProgramsForLocale( String localeString );
	
	Program getProgramForLocale( int programId, String localeString );

}
