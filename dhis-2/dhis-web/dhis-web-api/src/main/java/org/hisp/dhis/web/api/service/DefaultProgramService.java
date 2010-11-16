/**
 * 
 */
package org.hisp.dhis.web.api.service;

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.web.api.model.Model;
import org.hisp.dhis.web.api.model.ModelList;
import org.hisp.dhis.web.api.model.DataElement;
import org.hisp.dhis.web.api.model.Program;
import org.hisp.dhis.web.api.model.ProgramStage;
import org.hisp.dhis.web.api.utils.LocaleUtil;

/**
 * @author abyotag_adm
 * 
 */
public class DefaultProgramService
    implements IProgramService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private org.hisp.dhis.program.ProgramService programService;

    public org.hisp.dhis.program.ProgramService getProgramService()
    {
        return programService;
    }

    public void setProgramService( org.hisp.dhis.program.ProgramService programService )
    {
        this.programService = programService;
    }

    private org.hisp.dhis.i18n.I18nService i18nService;

    public org.hisp.dhis.i18n.I18nService getI18nService()
    {
        return i18nService;
    }

    public void setI18nService( org.hisp.dhis.i18n.I18nService i18nService )
    {
        this.i18nService = i18nService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // ProgramService
    // -------------------------------------------------------------------------
    public List<Program> getAllProgramsForLocale( String localeString )
    {
        List<Program> programs = new ArrayList<Program>();

        Collection<OrganisationUnit> units = currentUserService.getCurrentUser().getOrganisationUnits();
        OrganisationUnit unit = null;

        if ( units.size() > 0 )
        {
            unit = units.iterator().next();
        }
        else
        {
            return null;
        }
        for ( org.hisp.dhis.program.Program program : programService.getPrograms( unit ) )
        {
            programs.add( getProgramForLocale( program.getId(), localeString ) );
        }

        return programs;
    }

    public Program getProgramForLocale( int programId, String localeString )
    {
        Locale locale = LocaleUtil.getLocale( localeString );

        org.hisp.dhis.program.Program program = programService.getProgram( programId );

        program = i18n( i18nService, locale, program );

        Program pr = new Program();

        pr.setId( program.getId() );
        pr.setName( program.getName() );

        List<ProgramStage> prStgs = new ArrayList<ProgramStage>();

        for ( org.hisp.dhis.program.ProgramStage programStage : program.getProgramStages() )
        {
            programStage = i18n( i18nService, locale, programStage );

            ProgramStage prStg = new ProgramStage();
            prStg.setId( programStage.getId() );
            prStg.setName( programStage.getName() );

            List<DataElement> des = new ArrayList<DataElement>();

            for ( org.hisp.dhis.program.ProgramStageDataElement programStagedataElement : programStage
                .getProgramStageDataElements() )
            {
                ModelList mobileCategpryOptCombos = new ModelList();
                mobileCategpryOptCombos.setModels( new ArrayList<Model>() );
                programStagedataElement = i18n( i18nService, locale, programStagedataElement );
                Set<DataElementCategoryOptionCombo> deCatOptCombs = programStagedataElement.getDataElement()
                    .getCategoryCombo().getOptionCombos();

                for ( DataElementCategoryOptionCombo categoryOptCombo : deCatOptCombs )
                {
                    Model mobileCategpryOptCombo = new Model();
                    mobileCategpryOptCombo.setId( categoryOptCombo.getId() );
                    mobileCategpryOptCombo.setName( categoryOptCombo.getName() );
                    mobileCategpryOptCombos.getAbstractModels().add( mobileCategpryOptCombo );
                }

                DataElement de = new DataElement();
                de.setId( programStagedataElement.getDataElement().getId() );
                de.setName( programStagedataElement.getDataElement().getName() );
                de.setType( programStagedataElement.getDataElement().getType() );
                de.setCategoryOptionCombos( mobileCategpryOptCombos );

                des.add( de );
            }

            prStg.setDataElements( des );

            prStgs.add( prStg );

        }

        pr.setProgramStages( prStgs );

        return pr;
    }


}
