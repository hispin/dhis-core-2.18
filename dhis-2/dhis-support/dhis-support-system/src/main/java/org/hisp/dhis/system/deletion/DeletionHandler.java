package org.hisp.dhis.system.deletion;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.datamart.DataMartExport;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.gis.Legend;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.mapping.MapLegend;
import org.hisp.dhis.mapping.MapLegendSet;
import org.hisp.dhis.mapping.MapView;
import org.hisp.dhis.minmax.MinMaxDataElement;
import org.hisp.dhis.olap.OlapURL;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.programattributevalue.ProgramAttributeValue;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserSetting;
import org.hisp.dhis.validation.ValidationCriteria;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;

/**
 * A DeletionHandler should override methods for objects that, when deleted,
 * will affect the current object in any way. Eg. a DeletionHandler for
 * DataElementGroup should override the deleteDataElement(..) method which
 * should remove the DataElement from all DataElementGroups. Also, it should
 * override the allowDeleteDataElement() method and return false if there exists
 * objects that are dependent on the DataElement and are considered not be
 * deleted.
 * 
 * @author Lars Helge Overland
 * @version $Id$
 */
public abstract class DeletionHandler
{
    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    protected abstract String getClassName();

    // -------------------------------------------------------------------------
    // Public methods
    // -------------------------------------------------------------------------

    public void deleteChart( Chart chart )
    {
    }

    public boolean allowDeleteChart( Chart chart )
    {
        return true;
    }

    public void deleteDataDictionary( DataDictionary dataDictionary )
    {
    }

    public boolean allowDeleteDataDictionary( DataDictionary dataDictionary )
    {
        return true;
    }

    public void deleteDataElement( DataElement dataElement )
    {
    }

    public boolean allowDeleteDataElement( DataElement dataElement )
    {
        return true;
    }

    public void deleteCalculatedDataElement( CalculatedDataElement dataElement )
    {
    }

    public boolean allowDeleteCalculatedDataElement( CalculatedDataElement dataElement )
    {
        return true;
    }

    public void deleteDataElementGroup( DataElementGroup dataElementGroup )
    {
    }

    public boolean allowDeleteDataElementGroup( DataElementGroup dataElementGroup )
    {
        return true;
    }

    public void deleteDataElementGroupSet( DataElementGroupSet dataElementGroupSet )
    {
    }

    public boolean allowDeleteDataElementGroupSet( DataElementGroupSet dataElementGroupSet )
    {
        return true;
    }

    public void deleteDataElementCategory( DataElementCategory category )
    {
    }

    public boolean allowDeleteDataElementCategory( DataElementCategory category )
    {
        return true;
    }

    public void deleteDataElementCategoryOption( DataElementCategoryOption categoryOption )
    {
    }

    public boolean allowDeleteDataElementCategoryOption( DataElementCategoryOption categoryOption )
    {
        return true;
    }

    public void deleteDataElementCategoryCombo( DataElementCategoryCombo categoryCombo )
    {
    }

    public boolean allowDeleteDataElementCategoryCombo( DataElementCategoryCombo categoryCombo )
    {
        return true;
    }

    public void deleteDataElementCategoryOptionCombo( DataElementCategoryOptionCombo categoryOptionCombo )
    {
    }

    public boolean allowDeleteDataElementCategoryOptionCombo( DataElementCategoryOptionCombo categoryOptionCombo )
    {
        return true;
    }

    public void deleteDataSet( DataSet dataSet )
    {
    }

    public boolean allowDeleteDataSet( DataSet dataSet )
    {
        return true;
    }

    public void deleteSection( Section section )
    {
    }

    public boolean allowDeleteSection( Section section )
    {
        return true;
    }

    public void deleteCompleteDataSetRegistration( CompleteDataSetRegistration registration )
    {
    }

    public boolean allowDeleteCompleteDataSetRegistration( CompleteDataSetRegistration registration )
    {
        return true;
    }

    public void deleteDataValue( DataValue dataValue )
    {
    }

    public boolean allowDeleteDataValue( DataValue dataValue )
    {
        return true;
    }

    public void deleteExpression( Expression expression )
    {
    }

    public boolean allowDeleteExpression( Expression expression )
    {
        return true;
    }

    public void deleteMinMaxDataElement( MinMaxDataElement minMaxDataElement )
    {
    }

    public boolean allowDeleteMinMaxDataElement( MinMaxDataElement minMaxDataElement )
    {
        return true;
    }

    public void deleteIndicator( Indicator indicator )
    {
    }

    public boolean allowDeleteIndicator( Indicator indicator )
    {
        return true;
    }

    public void deleteIndicatorGroup( IndicatorGroup indicatorGroup )
    {
    }

    public boolean allowDeleteIndicatorGroup( IndicatorGroup indicatorGroup )
    {
        return true;
    }

    public void deleteIndicatorType( IndicatorType indicatorType )
    {
    }

    public boolean allowDeleteIndicatorType( IndicatorType indicatorType )
    {
        return true;
    }

    public void deleteIndicatorGroupSet( IndicatorGroupSet indicatorGroupSet )
    {
    }

    public boolean allowDeleteIndicatorGroupSet( IndicatorGroupSet indicatorGroupSet )
    {
        return true;
    }

    public void deletePeriod( Period period )
    {
    }

    public boolean allowDeletePeriod( Period period )
    {
        return true;
    }

    public void deleteSource( Source source )
    {
    }

    public boolean allowDeleteSource( Source source )
    {
        return true;
    }

    public void deleteValidationRule( ValidationRule validationRule )
    {
    }

    public boolean allowDeleteValidationRule( ValidationRule validationRule )
    {
        return true;
    }

    public void deleteValidationRuleGroup( ValidationRuleGroup validationRuleGroup )
    {
    }

    public boolean allowDeleteValidationRuleGroup( ValidationRuleGroup validationRuleGroup )
    {
        return true;
    }

    public void deleteDataEntryForm( DataEntryForm form )
    {
    }

    public boolean allowDeleteDataEntryForm( DataEntryForm form )
    {
        return true;
    }

    public void deleteOrganisationUnit( OrganisationUnit unit )
    {
    }

    public boolean allowDeleteOrganisationUnit( OrganisationUnit unit )
    {
        return true;
    }

    public void deleteOrganisationUnitGroup( OrganisationUnitGroup group )
    {
    }

    public boolean allowDeleteOrganisationUnitGroup( OrganisationUnitGroup group )
    {
        return true;
    }

    public void deleteOrganisationUnitGroupSet( OrganisationUnitGroupSet groupSet )
    {
    }

    public boolean allowDeleteOrganisationUnitGroupSet( OrganisationUnitGroupSet groupSet )
    {
        return true;
    }

    public void deleteOrganisationUnitLevel( OrganisationUnitLevel level )
    {
    }

    public boolean allowDeleteOrganisationUnitLevel( OrganisationUnitLevel level )
    {
        return true;
    }

    public void deleteReport( Report report )
    {
    }

    public boolean allowDeleteReport( Report report )
    {
        return true;
    }

    public void deleteReportTable( ReportTable reportTable )
    {
    }

    public boolean allowDeleteReportTable( ReportTable reportTable )
    {
        return true;
    }

    public void deleteUser( User user )
    {
    }

    public boolean allowDeleteUser( User user )
    {
        return true;
    }

    public void deleteUserCredentials( UserCredentials credentials )
    {
    }

    public boolean allowDeleteUserCredentials( UserCredentials credentials )
    {
        return true;
    }

    public void deleteUserAuthorityGroup( UserAuthorityGroup authorityGroup )
    {
    }

    public boolean allowDeleteUserAuthorityGroup( UserAuthorityGroup authorityGroup )
    {
        return true;
    }

    public void deleteUserSetting( UserSetting userSetting )
    {
    }

    public boolean allowDeleteUserSetting( UserSetting userSetting )
    {
        return true;
    }

    public void deleteDataMartExport( DataMartExport dataMartExport )
    {
    }

    public boolean allowDeleteDataMartExport( DataMartExport dataMartExport )
    {
        return true;
    }

    public void deleteOlapURL( OlapURL olapURL )
    {
    }

    public boolean allowDeleteOlapURL( OlapURL olapURL )
    {
        return true;
    }

    public void deleteDocument( Document document )
    {
    }

    public boolean allowDeleteDocument( Document document )
    {
        return true;
    }

    public void deleteMapLegend( MapLegend mapLegend )
    {
    }

    public boolean allowDeleteMapLegend( MapLegend mapLegend )
    {
        return true;
    }

    public void deleteMapLegendSet( MapLegendSet mapLegendSet )
    {
    }

    public boolean allowDeleteMapLegendSet( MapLegendSet mapLegendSet )
    {
        return true;
    }

    public void deleteMapView( MapView mapView )
    {
    }

    public boolean allowDeleteMapView( MapView mapView )
    {
        return true;
    }

    public boolean allowDeleteLegend( Legend legend )
    {
        return true;
    }

    public void deleteConcept( Concept concept )
    {
    }

    public boolean allowDeleteConcept( Concept concept )
    {
        return true;
    }

    public boolean allowDeletePatient( Patient patient )
    {
        return true;
    }

    public void deletePatient( Patient patient )
    {
    }

    public boolean allowDeletePatientAttribute( PatientAttribute patientAttribute )
    {
        return true;
    }

    public void deletePatientAttribute( PatientAttribute patientAttribute )
    {

    }

    public boolean allowDeletePatientAttributeValue( PatientAttributeValue patientAttributeValue )
    {
        return true;
    }

    public void deletePatientAttributeValue( PatientAttributeValue patientAttributeValue )
    {
    }

    public boolean allowDeletePatientAttributeGroup( PatientAttributeGroup patientAttributeGroup )
    {
        return true;
    }

    public void deletePatientAttributeGroup( PatientAttributeGroup patientAttributeGroup )
    {
    }

    public boolean allowDeletePatientIdentifier( PatientIdentifier patientIdentifier )
    {
        return true;
    }

    public void deletePatientIdentifier( PatientIdentifier patientIdentifier )
    {
    }

    public boolean allowDeletePatientIdentifierType( PatientIdentifierType patientIdentifierType )
    {
        return true;
    }

    public void deletePatientIdentifierType( PatientIdentifierType patientIdentifierType )
    {
    }

    public boolean allowDeleteRelationship( Relationship relationship )
    {
        return true;
    }

    public void deleteRelationship( Relationship relationship )
    {
    }

    public boolean allowDeleteRelationshipType( RelationshipType relationshipType )
    {
        return true;
    }

    public void deleteRelationshipType( RelationshipType relationshipType )
    {
    }
    
    public boolean allowDeleteProgram( Program program )
    {
        return true;
    }

    public void deleteProgram( Program program )
    {
    }

    public boolean allowDeleteProgramInstance( ProgramInstance programInstance )
    {
        return true;
    }

    public void deleteProgramInstance( ProgramInstance programInstance )
    {
    }
    
    public boolean allowDeleteProgramStage( ProgramStage programStage )
    {
        return true;
    }

    public void deleteProgramStage( ProgramStage programStage )
    {
    }
    
    public boolean allowDeleteProgramStageInstance( ProgramStageInstance programStageInstance )
    {
        return true;
    }

    public void deleteProgramStageInstance( ProgramStageInstance programStageInstance )
    {
    }
    
    public boolean allowDeleteProgramStageDataElement( ProgramStageDataElement programStageDataElement )
    {
        return true;
    }

    public void deleteProgramStageDataElement( ProgramStageDataElement programStageDataElement )
    {
    }
    
    
    public boolean allowDeletePatientDataValue( PatientDataValue patientDataValue )
    {
        return true;
    }

    public void deletePatientDataValue( PatientDataValue patientDataValue )
    {
    }
    
    public boolean allowDeleteValidationCriteria( ValidationCriteria validationCriteria )
    {
        return true;
    }

    public void deleteValidationCriteria( ValidationCriteria validationCriteria )
    {
    }
    
    public boolean allowDeleteProgramAttributeValue( ProgramAttributeValue programAttributeValue )
    {
        return true;
    }

    public void deleteProgramAttributeValue( ProgramAttributeValue programAttributeValue )
    {
    }
}
