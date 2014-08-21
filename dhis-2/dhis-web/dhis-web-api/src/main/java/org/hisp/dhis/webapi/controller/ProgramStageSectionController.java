/**
 * 
 */
package org.hisp.dhis.webapi.controller;

import org.hisp.dhis.program.ProgramStageSection;
import org.hisp.dhis.schema.descriptors.ProgramStageSectionSchemaDescriptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Abyot Asalefew Gizaw <abyota@gmail.com>
 *
 */
@Controller
@RequestMapping( value = ProgramStageSectionSchemaDescriptor.API_ENDPOINT )
public class ProgramStageSectionController
    extends AbstractCrudController<ProgramStageSection>
{
}