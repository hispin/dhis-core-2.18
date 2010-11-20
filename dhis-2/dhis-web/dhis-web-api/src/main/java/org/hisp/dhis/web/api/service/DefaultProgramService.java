package org.hisp.dhis.web.api.service;

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.web.api.model.DataElement;
import org.hisp.dhis.web.api.model.Model;
import org.hisp.dhis.web.api.model.ModelList;
import org.hisp.dhis.web.api.model.Program;
import org.hisp.dhis.web.api.model.ProgramStage;
import org.hisp.dhis.web.api.utils.LocaleUtil;
import org.springframework.beans.factory.annotation.Required;

public class DefaultProgramService
    implements IProgramService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private org.hisp.dhis.program.ProgramService programService;

    private org.hisp.dhis.i18n.I18nService i18nService;

    // -------------------------------------------------------------------------
    // ProgramService
    // -------------------------------------------------------------------------

    public List<Program> getPrograms( OrganisationUnit unit, String localeString )
    {
        List<Program> programs = new ArrayList<Program>();

        for ( org.hisp.dhis.program.Program program : programService.getPrograms( unit ) )
        {
            programs.add( getProgram( program.getId(), localeString ) );
        }

        return programs;
    }

    public Program getProgram( int programId, String localeString )
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
                de.setCompulsory( programStagedataElement.isCompulsory() );
                de.setCategoryOptionCombos( mobileCategpryOptCombos );

                des.add( de );
            }

            prStg.setDataElements( des );

            prStgs.add( prStg );

        }

        pr.setProgramStages( prStgs );

        return pr;
    }

    @Required
    public void setProgramService( org.hisp.dhis.program.ProgramService programService )
    {
        this.programService = programService;
    }

    @Required
    public void setI18nService( org.hisp.dhis.i18n.I18nService i18nService )
    {
        this.i18nService = i18nService;
    }

}
