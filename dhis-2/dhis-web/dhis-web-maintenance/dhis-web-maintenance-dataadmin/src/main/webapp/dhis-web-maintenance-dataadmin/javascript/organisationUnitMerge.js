
var organisationUnitToEliminate = 0;
var organisationUnitToKeep = 0;
var organisationUnitSelected = 0;

function treeSelected( unitIds )
{
	organisationUnitSelected = unitIds[0];
	
	$.getJSON( 
        "../dhis-web-commons-ajax-json/getOrganisationUnit.action",
        {
            "id": organisationUnitSelected
        },
        function( json )
        {
        	if ( organisationUnitToEliminate == 0 ) // Step 1
        	{
	            document.getElementById( "eliminateNameField" ).innerHTML = json.organisationUnit.name;
	            document.getElementById( "confirmOrganisationUnitToEliminateButton" ).disabled = false;        
        	}
        	else // Step 2
        	{
        		document.getElementById( "keepNameField" ).innerHTML = json.organisationUnit.name;
                document.getElementById( "confirmOrganisationUnitToKeepButton" ).disabled = false; 
        	}   
        }
    );
}

function organisationUnitToEliminateConfirmed()
{
	organisationUnitToEliminate = organisationUnitSelected;	
    document.getElementById( "confirmOrganisationUnitToEliminateButton" ).disabled = true;
                
	document.getElementById( 'step1' ).style.backgroundColor = 'white';
	document.getElementById( 'step2' ).style.backgroundColor = '#ccffcc';
}

function organisationUnitToKeepConfirmed()
{
	organisationUnitToKeep = organisationUnitSelected;	
	document.getElementById( "confirmOrganisationUnitToKeepButton" ).disabled = true;	
    document.getElementById( "mergeButton" ).disabled = false;
                
    document.getElementById( 'step2' ).style.backgroundColor = 'white';
    document.getElementById( 'step3' ).style.backgroundColor = '#ccffcc';              
}

function mergeOrganisationUnits()
{
	setMessage( i18n_merging + "..." );
	
	$.getJSON( 
	    "mergeOrganisationUnits.action",
        {
	   	    "organisationUnitToEliminate": organisationUnitToEliminate,
	   	    "organisationUnitToKeep": organisationUnitToKeep
	    },
	    function( json )
	    {
	    }
	);
}
