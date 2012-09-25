var COLOR_GREEN = '#b9ffb9';
var COLOR_WHITE = '#ffffff'

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
    jQuery.getJSON( 'getProgramValidation.action', { validationId: programValidationId }, function ( json ) {
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
	$.postUTF8("getProgramValidationDescription.action",
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
	var programStageId = programStage.options[ programStage.selectedIndex ].value;
	if( programStageId == '') return;
	
	jQuery.getJSON( "getPatientDataElements.action", {
		programStageId:programStageId
	}, function(json){
		for ( i in json.dataElements ) {
			var id = '[DE:' + programStageId + '.' + json.dataElements[i].id + ']';
			jQuery( '#leftSideDE').append( '<option value="' + id + '">' + json.dataElements[i].name + '</option>' );
		}
	});   
}

//------------------------------------------------------------------------------
// Get DataElements of Program-Stage into right-side
//------------------------------------------------------------------------------

function getRightPrgramStageDataElements()
{
	clearListById( 'rightSideDE' );
  	
	var programStage = document.getElementById( 'rightStage' );
	var programStageId = programStage.options[ programStage.selectedIndex ].value;
	if( programStageId == '') return;
  
	jQuery.getJSON( "getPatientDataElements.action", {
		programStageId:programStageId
	}, function(json){
		for ( i in json.dataElements ) {
			var id = '[DE:' + programStageId + '.' + json.dataElements[i].id + ']';
			jQuery( '#rightSideDE').append( '<option value="' + id + '">' + json.dataElements[i].name + '</option>' );
		}
	}); 
	
}

function getDateDataElements()
{
	hideById('dataElementValidation');
	showLoader();
	var programStageId = getFieldValue('programStage');
	
	if( programStageId == '')
	{
		hideById('loaderDiv');
		hideById('dataElementValidation');
		return;
	}
	
	$( '#dataElementValidation' ).load( "getDateDataElements.action", 
		{ 
			psId:programStageId
		},function( )
		{	
			hideById('loaderDiv');
			showById('dataElementValidation');
		});
}

function dateValidation( id, fieldId, dataElementName ) 
{
	var expression = $("#" + fieldId + ' option:selected').val();
	var validationid = $("#" + fieldId ).attr('validationid');
	
	if( expression != -5)
	{
		hideById( 'div' + fieldId );
		setFieldValue('days' + fieldId, '' );
		saveDateValidation( id, fieldId, dataElementName );
	}
	else
	{
		byId( fieldId ).style.backgroundColor = COLOR_WHITE;
		byId( 'days' + fieldId ).style.backgroundColor = COLOR_WHITE;
		showById( 'div' + fieldId );
	}
	
} 

function saveDateValidation( id, fieldId, dataElementName )
{
	var expression = $("#" + fieldId + ' option:selected').val();
	if( expression == -5 )
	{
		expression += "D" + getFieldValue( 'days' + fieldId );
	}
	var validationid = $("#" + fieldId ).attr('validationid');
	
	if( validationid == '' )
	{
		var description =  $('#programStage option:selected').text() + ' - ' + dataElementName;
		jQuery.post( "addDateProgramValidation.action", {
			description: description,
			leftSide: id,
			rightSide: expression,
			programId: getFieldValue('programId'),
			dateType: getFieldValue('dateType')
		}, function( json )
		{
			byId( fieldId ).style.backgroundColor = COLOR_GREEN;
			byId( 'days' + fieldId ).style.backgroundColor = COLOR_GREEN;
			$("#" + fieldId ).attr('validationid', json.message );
		});
	}
	else
	{
		var rightSide = $("#" + fieldId + ' option:selected').val();
		if( rightSide == '')
		{
			jQuery.post( "removeProgramValidation.action", {
				id:validationid
			}, function( json )
			{
				byId( fieldId ).style.backgroundColor = COLOR_GREEN;
				byId( 'days' + fieldId ).style.backgroundColor = COLOR_GREEN;
				$("#" + fieldId ).attr('validationid', '' );
			});
		}
		else
		{
			var description =  $('#programStage option:selected').text() + ' - ' + dataElementName;
			jQuery.post( "updateDateProgramValidation.action", {
				id:validationid,
				description: description,
				leftSide: id,
				rightSide: expression,
				programId: getFieldValue('programId')
			}, function( json )
			{
				byId( fieldId ).style.backgroundColor = COLOR_GREEN;
				byId( 'days' + fieldId ).style.backgroundColor = COLOR_GREEN;
			});
		}
	}
}

function parseRightSide( dataElementId, rightSide )
{
	var index = rightSide.indexOf('D');
	if( index < 0 )
	{
		$('#' + dataElementId ).val( rightSide );
	}
	else
	{
		var selectorValue = rightSide.substr( 0,index );
		var daysValue = rightSide.substr( index + 1,rightSide.length);
		$('#' + dataElementId).val(selectorValue);
		$('#days' + dataElementId ).val(daysValue);
		showById('div' + dataElementId );
	}
}
