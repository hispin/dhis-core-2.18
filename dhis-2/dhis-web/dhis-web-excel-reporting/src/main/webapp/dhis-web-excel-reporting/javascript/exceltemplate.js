// ========================================================================================================================
// EXCEL TEMPLATE MANAGER
// ========================================================================================================================

//----------------------------------------------------------
// Regular Expression using for checking excel's file name
//----------------------------------------------------------

//regPattern = /^[^0-9\s\W][a-zA-Z_. 0-9]{5,50}\.xl(?:sx|s)$/;
// or : 
regPattern = /^[a-zA-Z][\w\s\d.]{5,50}\.xl(?:sx|s)$/;

/*
*	Delete Excel Template
*/
function deleteExcelTemplate( fileName ) {

	if ( window.confirm(i18n_confirm_delete) ) {
	
		var request = new Request();
		request.setResponseTypeXML( 'xmlObject' );
		request.setCallbackSuccess( deleteExcelTemplateReceived );
		request.send( "deleteExcelTemplate.action?fileName=" + fileName );
	}
}

function deleteExcelTemplateReceived( xmlObject ) {

	var type = xmlObject.getAttribute( 'type' );
	
	if ( type == 'error' )
	{
		setMessage( xmlObject.firstChild.nodeValue );
	}
	else
	{
		window.location.href = 'listAllExcelTemplates.action';
	}
}

curTemplateName = '';
newTemplateName = '';

function openEditExcelTemplate( currentFileName ) {

	curTemplateName = currentFileName;
	$("#editExcelTemplateDiv").showAtCenter( true );
}

function validateRenamingExcelTemplate( fileName, columnIndex ) {
	
	var list = byId( 'list' );
	var rows = list.getElementsByTagName( 'tr' );
	
	for ( var i = 0 ; i < rows.length ; i++ )
	{
		var cell = rows[i].getElementsByTagName( 'td' )[columnIndex -1];
		var value = cell.firstChild.nodeValue;
		
		if ( value.toLowerCase() == fileName.toLowerCase() )
		{
			disable( "excelTemplateButtonRename" );
			setMessage( i18n_file_exists );
			break;
		}
		else if ( applyingPatternForFileName( fileName ) == null )
		{
			disable( "excelTemplateButtonRename" );
			setMessage
			(
				'<b>' + i18n_filename_wellformed  + '</b><ul><li>'
				+ i18n_length_filename_min5_max30 + '</li><li>'
				+ i18n_use_only_letters_numbers_dot_only + '</li></ul>'
			);
			break;
		}
		else
		{
			enable( "excelTemplateButtonRename" );
			hideMessage();
		}
	}
}

function applyingPatternForFileName( fileName ) {
	
	return fileName.match( regPattern );
}

/**
	param renamingMode::
	'RUS': Rename file name and update the system
	'RNUS': Rename file name but non-updating the system
*/

function checkingStatusExcelTemplate( newFileName, keyColumnIndex, statusColumnIndex ) {

    var list = byId( 'list' );
    var rows = list.getElementsByTagName( 'tr' );
    var flagRename = false;
	newTemplateName = newFileName;
	
	for ( var i = 0; i < rows.length; i++ )
    {
        var cell = rows[i].getElementsByTagName( 'td' )[keyColumnIndex-1];
        var value = cell.firstChild.nodeValue;
		cell = rows[i].getElementsByTagName( 'td' )[statusColumnIndex-1];
        var statusFile = cell.axis;
		
        if ( (value.toLowerCase() == curTemplateName.toLowerCase()) && (statusFile == "true") )
        {
            // File exists and being used
			if ( window.confirm(confirmRenamingMessage) )
			{
				renamingExcelTemplate( curTemplateName, newFileName, "RUS" );
			}
			else
			{
				hideById('editExcelTemplateDiv');
				deleteDivEffect();
			}
			return;
        }
		else
		{
			flagRename = true;
		}
	}
	
	// File exists and pending
	if ( flagRename )
	{
		renamingExcelTemplate( curTemplateName, newFileName, "RNUS" );
	}
	
}

function renamingExcelTemplate( curFileName, newFileName, renamingMode ) {
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( renamingExcelTemplateReceived );
	request.send( "renameExcelTemplate.action?newFileName=" + newFileName + "&curFileName=" + curFileName + "&renamingMode=" + renamingMode );
	
}

function renamingExcelTemplateReceived( xmlObject ) {

	var type = xmlObject.getAttribute( 'type' );
	var message = xmlObject.firstChild.nodeValue;
	
	if ( type == "success" )
	{
		hideById('editExcelTemplateDiv');
		deleteDivEffect();
	
		if ( window.confirm( confirmUpdateSysMessage ) )
		{
			//alert("update_system");
			updateReportExcelByTemplate();
		}
		else
		{
			window.location.href="listAllExcelTemplates.action?mode=" + mode + "&message=" + message;
		}
	}
	else if ( type == "none" )
	{
		window.location.href="listAllExcelTemplates.action?mode=" + mode + "&message=" + message;
	}
	else
	{
		setMessage( message );
	}
}

function updateReportExcelByTemplate() {

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( updateReportExcelByTemplateCompleted );
	request.send( "updateReportExcelByTemplate.action?curTemplateName=" + curTemplateName + "&newTemplateName=" + newTemplateName);
}

function updateReportExcelByTemplateCompleted( xmlObject ) {

	var type = xmlObject.getAttribute( 'type' );
	
	if ( type != "" )
	{
		var message = xmlObject.firstChild.nodeValue;
		window.location.href="listAllExcelTemplates.action?mode=" + mode + "&message=" + message;
	}
}

//----------------------------------------------------------
// Validate Upload Excel Template
//----------------------------------------------------------

function validateUploadExcelTemplate()
{
	$.ajaxFileUpload
	(
		{
			url:'validateUploadExcelTemplate.action',
			secureuri:false,
			fileElementId:'upload',
			dataType: 'xml',
			success: function (data, status)
			{
				data = data.getElementsByTagName('message')[0];
				var type = data.getAttribute("type");
				
				if ( type == 'error' )
				{                    
					setMessage(data.firstChild.nodeValue);
				}
				else if ( type == 'input' )
				{
					if ( window.confirm( i18n_confirm_override ) )
					{
						document.forms['uploadForm'].submit();
					}
					else
					{
						return;
					}
				}
				else
				{
					document.forms['uploadForm'].submit();
				}
			},
			error: function (data, status, e)
			{
			
			}
		}
	);
}