// -----------------------------------------------------------------------------
// Change indicator group and data dictionary
// -----------------------------------------------------------------------------

function criteriaChanged()
{
    var indicatorGroupId = getListValue( "indicatorGroupList" );
    var dataDictionaryId = getListValue( "dataDictionaryList" );
    
    var url = "indicator.action?&dataDictionaryId=" + dataDictionaryId + "&indicatorGroupId=" + indicatorGroupId;
    
    window.location.href = url;
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showIndicatorDetails( indicatorId )
{
    var request = new Request();
    request.setResponseTypeXML( 'indicator' );
    request.setCallbackSuccess( indicatorReceived );
    request.send( 'getIndicator.action?id=' + indicatorId );
}

function indicatorReceived( indicatorElement )
{
    setFieldValue( 'nameField', getElementValue( indicatorElement, 'name' ) );
    
    setFieldValue( 'shortNameField', getElementValue( indicatorElement, 'shortName' ) );
    
    var alternativeName = getElementValue( indicatorElement, 'alternativeName' );
    setFieldValue( 'alternativeNameField', alternativeName ? alternativeName : '[' + i18n_none + ']' );
    
    var description = getElementValue( indicatorElement, 'description' );
    setFieldValue( 'descriptionField', description ? description : '[' + i18n_none + ']' );
    
    var annualized = getElementValue( indicatorElement, 'annualized' );
    setFieldValue( 'annualizedField', annualized == "true" ? i18n_yes : i18n_no );
    
    setFieldValue( 'indicatorTypeNameField', getElementValue( indicatorElement, 'indicatorTypeName' ) );
    
    var numeratorDescription = getElementValue( indicatorElement, 'numeratorDescription' );
    setFieldValue( 'numeratorDescriptionField', numeratorDescription ? numeratorDescription : '[' + i18n_none + ']' );

    var denominatorDescription = getElementValue( indicatorElement, 'denominatorDescription' );
    setFieldValue( 'denominatorDescriptionField', denominatorDescription ? denominatorDescription : '[' + i18n_none + ']' );

    var url = getElementValue( indicatorElement, 'url' );
    setFieldValue( 'urlField', url ? '<a href="' + url + '">' + url + '</a>' : '[' + i18n_none + ']' );
    
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove indicator
// -----------------------------------------------------------------------------

function removeIndicator( indicatorId, indicatorName )
{
    var result = window.confirm( i18n_confirm_delete + '\n\n' + indicatorName );
    
    if ( result )
    {
        var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( removeIndicatorCompleted );
        request.send( 'removeIndicator.action?id=' + indicatorId );
    }
}

function removeIndicatorCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        window.location.href = 'indicator.action';
    }
    else if ( type = 'error' )
    {
        setFieldValue( 'warningField', message );
        
        showWarning();
    }
}

// -----------------------------------------------------------------------------
// Add indicator
// -----------------------------------------------------------------------------

function validateAddIndicator()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );
    request.send( 'validateIndicator.action?name=' + getFieldValue( 'name' ) +
    	'&shortName=' + getFieldValue( 'shortName' ) +
    	'&alternativeName=' + getFieldValue( 'alternativeName' ) +
    	'&code=' + getFieldValue( 'code' ) +
        '&indicatorTypeId=' + getListValue( 'indicatorTypeId' ) +
        '&numerator=' + getFieldValue( 'numerator' ) +
        '&numeratorDescription=' + getFieldValue( 'numeratorDescription' ) +
        '&numeratorAggregationOperator=' + getFieldValue( 'numeratorAggregationOperator' ) +
        '&denominator=' + getFieldValue( 'denominator' ) +
        '&denominatorDescription=' + getFieldValue( 'denominatorDescription' ) +
        '&denominatorAggregationOperator=' + getFieldValue( 'denominatorAggregationOperator' ) );

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var form = document.getElementById( 'addIndicatorForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_indicator_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

// -----------------------------------------------------------------------------
// Update indicator
// -----------------------------------------------------------------------------

function validateUpdateIndicator()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );
    request.send( 'validateIndicator.action?id=' + getFieldValue( 'id' ) +
        '&name=' + getFieldValue( 'name' ) +
        '&shortName=' + getFieldValue( 'shortName' ) +
        '&alternativeName=' + getFieldValue( 'alternativeName' ) +
    	'&code=' + getFieldValue( 'code' ) +
        '&indicatorTypeId=' + getListValue( 'indicatorTypeId' ) +
        '&numerator=' + getFieldValue( 'numerator' ) +
        '&numeratorDescription=' + getFieldValue( 'numeratorDescription' ) +
        '&numeratorAggregationOperator=' + getFieldValue( 'numeratorAggregationOperator' ) +
        '&denominator=' + getFieldValue( 'denominator' ) +
        '&denominatorDescription=' + getFieldValue( 'denominatorDescription' ) +
        '&denominatorAggregationOperator=' + getFieldValue( 'denominatorAggregationOperator' ) );

    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var form = document.getElementById( 'updateIndicatorForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_saving_indicator_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}
