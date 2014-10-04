
$( document ).ready( function()
{
	showLoader();
	
    $.getJSON( "getDataIntegrity.action", {}, populateIntegrityItems );
} );

function populateIntegrityItems( json )
{
	hideLoader();
	
    displayViolationList( json.dataElementsWithoutDataSet, "dataElementsWithoutDataSet", false );
    displayViolationList( json.dataElementsWithoutGroups, "dataElementsWithoutGroups", false );
	displayViolationList( json.dataElementsViolatingExclusiveGroupSets, "dataElementsViolatingExclusiveGroupSets", true );
	displayViolationList( json.dataElementsInDataSetNotInForm, "dataElementsInDataSetNotInForm", true );
    displayViolationList( json.dataElementsAssignedToDataSetsWithDifferentPeriodTypes, "dataElementsAssignedToDataSetsWithDifferentPeriodTypes", true );
    displayViolationList( json.dataSetsNotAssignedToOrganisationUnits, "dataSetsNotAssignedToOrganisationUnits", false );
    displayViolationList( json.sectionsWithInvalidCategoryCombinations, "sectionsWithInvalidCategoryCombinations", false );
    displayViolationList( json.indicatorsWithIdenticalFormulas, "indicatorsWithIdenticalFormulas", false );
    displayViolationList( json.indicatorsWithoutGroups, "indicatorsWithoutGroups", false );
    displayViolationList( json.invalidIndicatorNumerators, "invalidIndicatorNumerators", true );
    displayViolationList( json.invalidIndicatorDenominators, "invalidIndicatorDenominators", true );
	displayViolationList( json.indicatorsViolatingExclusiveGroupSets, "indicatorsViolatingExclusiveGroupSets", true );
    displayViolationList( json.organisationUnitsWithCyclicReferences, "organisationUnitsWithCyclicReferences", false );
    displayViolationList( json.orphanedOrganisationUnits, "orphanedOrganisationUnits", false );
    displayViolationList( json.organisationUnitsWithoutGroups, "organisationUnitsWithoutGroups", false );
    displayViolationList( json.organisationUnitsViolatingExclusiveGroupSets, "organisationUnitsViolatingExclusiveGroupSets", true );
    displayViolationList( json.organisationUnitGroupsWithoutGroupSets, "organisationUnitGroupsWithoutGroupSets", false );
    displayViolationList( json.duplicatePeriods, "duplicatePeriods", false );
    displayViolationList( json.validationRulesWithoutGroups, "validationRulesWithoutGroups", false );
    displayViolationList( json.invalidValidationRuleLeftSideExpressions, "invalidValidationRuleLeftSideExpressions", true );
    displayViolationList( json.invalidValidationRuleRightSideExpressions, "invalidValidationRuleRightSideExpressions", true );
}

function displayViolationList( list, id, lineBreak )
{
    if ( list.length > 0 )
    {
    	// Display image "drop-down" button
    	
        $( "#" + id + "Button" )
           .attr({ src: "../images/down.png", title: "View violations" })
           .css({ cursor: "pointer" })
           .click( function() { $( "#" + id + "Div" ).slideToggle( "fast" ); } );

        // Populate violation div

        var violations = "";
        
        for ( var i = 0; i < list.length; i++ )
        {
            violations += list[i] + "<br>";
            violations += !!lineBreak ? "<br>" : "";
        }
        
        $( "#" + id + "Div" ).html( violations );
    }
    else
    {
    	// Display image "check" button
    	
        $( "#" + id + "Button" ).attr({ src: "../images/check.png", title: "No violations" });
    }
        
    $( "#" + id + "Div" ).hide();
}