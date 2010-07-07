
var operandToEliminate = 0;
var operandToKeep = 0;

function initLists()
{	
	$.getJSON( 
        "../dhis-web-commons-ajax-json/getOperands.action",
        {},
        function( json )
        {
        	var operands = json.operands;
        	
        	for ( var i = 0; i < operands.length; i++ )
        	{
        	   $( "#operandList" ).append( "<option value='" + 
        	       operands[i].operandId + "'>" + operands[i].operandName + "</option>" );
        	}
        }
    );
}

function operandSelected()
{
	var operandName = $( "#operandList :selected" ).text();
	
	if ( operandToEliminate == 0 ) // Step 1
    {
        $( "#eliminateNameField" ).html( operandName );
        $( "#confirmEliminateButton" ).removeAttr( "disabled" );
    }
    else // Step 2
    {
        $( "#keepNameField" ).html( operandName );
        $( "#confirmKeepButton" ).removeAttr( "disabled" );
    }	
}

function eliminateConfirmed()
{
	operandToEliminate = $( "#operandList" ).val();
	
	$( "#confirmEliminateButton" ).attr( "disabled", "disabled" );
	
	$( "#step1" ).css( "background-color", "white" );
	$( "#step2" ).css( "background-color", "#ccffcc" );
}

function keepConfirmed()
{
	operandToKeep = $( "#operandList" ).val();
	
	if ( operandToEliminate == operandToKeep )
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
	setWaitMessage( i18n_eliminating + "..." );
	
	$.ajax({ 
		"url": "eliminateDuplicateData.action", 
		"data": { 
			"dataElementToKeep": operandToKeep.split( "." )[0],
			"categoryOptionComboToKeep": operandToKeep.split( "." )[1],
			"dataElementToEliminate": operandToEliminate.split( "." )[0],
			"categoryOptionComboToEliminate": operandToEliminate.split( "." )[1] },
		"success": function()
		{
		    setMessage( i18n_elimination_done );
		} });
}
