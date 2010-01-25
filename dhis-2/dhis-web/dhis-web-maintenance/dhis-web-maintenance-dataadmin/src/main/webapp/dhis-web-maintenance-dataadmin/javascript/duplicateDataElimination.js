
var dataElementToEliminate = 0;
var categoryOptionComboToEliminate = 0;
var dataElementToKeep = 0;
var categoryOptionComboToKeep = 0;

function initLists()
{	
	$.getJSON( 
        "../dhis-web-commons-ajax-json/getDataElements.action",
        {},
        function( json )
        {
        	var elements = json.dataElements;
        	
        	for ( var i = 0; i < elements.length; i++ )
        	{
        	   $( "#dataElementList" ).append( "<option value='" + 
        	       elements[i].id + "'>" + elements[i].name + "</option>" );
        	}
        }
    );
}

function dataElementSelected()
{
	$( "#categoryOptionComboList" ).children().remove();
	
	var dataElementId = $( "#dataElementList" ).val();
	
	$.getJSON( 
        "../dhis-web-commons-ajax-json/getCategoryOptionCombos.action",
        {
        	"id": dataElementId
        },
        function( json )
        {
        	var cocs = json.categoryOptionCombos;
        	
        	for ( var i = 0; i < cocs.length; i++ )
        	{
        		$( "#categoryOptionComboList" ).append( "<option value='" +
        		    cocs[i].id + "'>" + cocs[i].name + "</option>" );
        	}
        }
    );
}

function categoryOptionComboSelected()
{
	$.getJSON( 
	    "../dhis-web-commons-ajax-json/getDataElementName.action",
	    {
	   	    "dataElementId": $( "#dataElementList" ).val(),
	   	    "categoryOptionComboId": $( "#categoryOptionComboList" ).val()
	    },
	    function( json )
	    {
	        if ( dataElementToEliminate == 0 && categoryOptionComboToEliminate == 0 ) // Step 1
		    {
		   	    $( "#eliminateNameField" ).html( json.name );
		   	    $( "#confirmEliminateButton" ).removeAttr( "disabled" );
		    }
		    else // Step 2
            {
                $( "#keepNameField" ).html( json.name );
                $( "#confirmKeepButton" ).removeAttr( "disabled" );
		    }
        }
    );
}

function eliminateConfirmed()
{
	dataElementToEliminate = $( "#dataElementList" ).val();
	categoryOptionComboToEliminate = $( "#categoryOptionComboList" ).val();
	
	$( "#confirmEliminateButton" ).attr( "disabled", "disabled" );
	
	$( "#step1" ).css( "background-color", "white" );
	$( "#step2" ).css( "background-color", "#ccffcc" );
}

function keepConfirmed()
{
	dataElementToKeep = $( "#dataElementList" ).val();
	categoryOptionComboToKeep = $( "#categoryOptionComboList" ).val();
	
	if ( dataElementToEliminate == dataElementToKeep && 
	   categoryOptionComboToEliminate == categoryOptionComboToKeep )
    {
   	    setMessage( i18n_select_different_data_elements );
   	    return;
    }
	
	$( "#confirmKeepButton" ).attr( "disabled", "disabled" );
	$( "#eliminateButton" ).removeAttr( "disabled" );
    
    $( "#step2" ).css( "background-color", "white" );
    $( "#step3" ).css( "background-color", "#ccffcc" );
}

function eliminate()
{
	setMessage( i18n_eliminating + "..." );
	
	$.ajax({ 
		"url": "eliminateDuplicateData.action", 
		"data": { 
			"dataElementToKeep": dataElementToKeep,
			"categoryOptionComboToKeep": categoryOptionComboToKeep,
			"dataElementToEliminate": dataElementToEliminate,
			"categoryOptionComboToEliminate": categoryOptionComboToEliminate },
		"success": function()
		{
		    setMessage( i18n_elimination_done );
		} });
}
