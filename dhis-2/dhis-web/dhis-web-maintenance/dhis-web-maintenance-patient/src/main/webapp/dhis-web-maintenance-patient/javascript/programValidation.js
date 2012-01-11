
jQuery(document).ready(	function(){
	validation( 'programValidationForm', function( form ){			
		form.submit();
	});
});

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showProgramValidationDetails ( programValidationId )
{
    jQuery.post( 'getProgramValidation.action', { validationId: programValidationId }, function ( json ) {
		setInnerHTML( 'idField', json.validation.id );
		setInnerHTML( 'descriptionField', json.validation.description );
		setInnerHTML( 'leftSideField', json.validation.leftSide );
		if( json.validation.rightSide != '1==1')
		{
			setInnerHTML( 'leftSideTitle', i18n_left_side );
			setInnerHTML( 'rightSideField', json.validation.rightSide );
			showById('rightSideDiv');
		}
		else
		{
			setInnerHTML( 'leftSideTitle', i18n_condition );
			hideById('rightSideDiv');
		}
		setInnerHTML( 'programField', json.validation.program );
		
		showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove ProgramValidation
// -----------------------------------------------------------------------------

function removeProgramValidation( programValidationId, name )
{
	removeItem( programValidationId, name, i18n_confirm_delete, 'removeProgramValidation.action' );	
}

//-----------------------------------------------------------------
// Insert items data-element
//-----------------------------------------------------------------

function insertDataElement( element, target, decriptionDiv )
{
	if( element.selectedIndex == -1)
		return;
	
	var value = " " + element.options[element.selectedIndex].value + " ";
	
	insertTextCommon( target, value );
	
	getValidationDescription( decriptionDiv, target );
}

function insertOperator( decriptionDiv, target, value )
{
	insertTextCommon( target, ' ' + value + ' ' );
	
	getValidationDescription( decriptionDiv, target );
}


function getValidationDescription( decriptionDiv, sideDiv )
{
	$.post("getCaseAggregationDescription.action",
		{
			condition: getFieldValue( sideDiv )
		},
		function (data)
		{
			setInnerHTML( decriptionDiv, data );
		},'html');
}

function clearValidation( target, decriptionDiv )
{
	setFieldValue( target,'' );
	setInnerHTML( decriptionDiv, '' );
}

//------------------------------------------------------------------------------
// Get DataElements of Program-Stage into left-side
//------------------------------------------------------------------------------

function getLeftPrgramStageDataElements()
{
	clearListById( 'leftSideDE' );
	
	var programStage = document.getElementById( 'leftStage' );
	var psId = programStage.options[ programStage.selectedIndex ].value;
	if( psId == '') return;
  
	$.ajax({
		url: 'getAggPSDataElements.action?psId=' + psId,
		dataType: "xml",
		success: getLeftPrgramStageDataElementsCompleted
	});  
}

function getLeftPrgramStageDataElementsCompleted( dataelementElement )
{
	var programstageDE = byId( 'leftSideDE' );
	var programstageDEList = $(dataelementElement).find( 'dataelement' );
 
	$( programstageDEList ).each( function( i, item )
        {
            var id = $( item ).find("id").text();
			var name = $( item ).find("name").text();
			var type = $( item ).find("type").text(); 

			var option = document.createElement("option");
			option.value = id;
			option.text = name;
			option.title = name;
			jQuery(option).attr({data:"{type:'"+type+"'}"});
			programstageDE.add(option, null);  			
        } );
}

//------------------------------------------------------------------------------
// Get DataElements of Program-Stage into right-side
//------------------------------------------------------------------------------

function getRightPrgramStageDataElements()
{
	clearListById( 'rightSideDE' );
  	
	var programStage = document.getElementById( 'rightStage' );
	var psId = programStage.options[ programStage.selectedIndex ].value;
	if( psId == '') return;
  
	$.ajax({
		url: 'getAggPSDataElements.action?psId=' + psId,
		dataType: "xml",
		success: getRightPrgramStageDataElementsCompleted
	});  
}

function getRightPrgramStageDataElementsCompleted( dataelementElement )
{
	var programstageDE = document.getElementById( 'rightSideDE' );
	var programstageDEList = $(dataelementElement).find( 'dataelement' );
 
	$( programstageDEList ).each( function( i, item )
    {
		var id = $( item ).find("id").text();
		var name = $( item ).find("name").text();
		var type = $( item ).find("type").text(); 
		
		var option = document.createElement("option");
		option.value = id;
		option.text = name;
		option.title = name;
		jQuery(option).attr({data:"{type:'"+type+"'}"});
		programstageDE.add(option, null); 
	});
}
