
// -----------------------------------------------------------------------------
// $Id: general.js 4486 2008-02-01 21:23:06Z larshelg $
// -----------------------------------------------------------------------------
// Selection
// -----------------------------------------------------------------------------

function organisationUnitSelected( orgUnits )
{
    window.location.href = 'select.action';
}

selection.setListenerFunction( organisationUnitSelected );

function changeOrder()
{
    window.open( 'getDataElementOrder.action', '_blank', 'width=700,height=500,scrollbars=yes' );
}

// -----------------------------------------------------------------------------
// Comments
// -----------------------------------------------------------------------------

function commentSelected( dataElementId )
{
    var commentSelector = document.getElementById( 'value[' + dataElementId + '].comments' );
    var commentField = document.getElementById( 'value[' + dataElementId + '].comment' );

    var value = commentSelector.options[commentSelector.selectedIndex].value;
    
    if ( value == 'custom' )
    {
        commentSelector.style.display = 'none';
        commentField.style.display = 'inline';
        
        commentField.select();
        commentField.focus();
    }
    else
    {
        commentField.value = value;
        
        saveComment( dataElementId, value );
    }
}

function commentLeft( dataElementId )
{
    var commentField = document.getElementById( 'value[' + dataElementId + '].comment' );
    var commentSelector = document.getElementById( 'value[' + dataElementId + '].comments' );

    saveComment( dataElementId, commentField.value );

    var value = commentField.value;
    
    if ( value == '' )
    {
        commentField.style.display = 'none';
        commentSelector.style.display = 'inline';

        commentSelector.selectedIndex = 0;
    }
}

// -----------------------------------------------------------------------------
// String Trim
// -----------------------------------------------------------------------------

function trim( stringToTrim ) 
{
  return stringToTrim.replace(/^\s+|\s+$/g,"");
}


// -----------------------------------------------------------------------------
// Date Validation for Linelisting
// -----------------------------------------------------------------------------

// Declaring valid date character, minimum year and maximum year
var dtCh= "-";
var minYear=1900;
var maxYear=2100;

function isInteger(s)
{
  var i;
  for (i = 0; i < s.length; i++)
  {   
    // Check that current character is number.
    var c = s.charAt(i);
    if (((c < "0") || (c > "9"))) return false;
  }
  // All characters are numbers.
  return true;
}

function stripCharsInBag(s, bag)
{
  var i;
  var returnString = "";
  
  // Search through string's characters one by one.
  // If character is not in bag, append to returnString.
  for (i = 0; i < s.length; i++)
  {   
    var c = s.charAt(i);
    if (bag.indexOf(c) == -1) returnString += c;
  }
  
  return returnString;
}

function daysInFebruary (year)
{
  // February has 29 days in any year evenly divisible by four,
  // EXCEPT for centurial years which are not also divisible by 400.
  
  return (((year % 4 == 0) && ( (!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28 );
}

function DaysArray(n) 
{
  for (var i = 1; i <= n; i++) 
  {
    this[i] = 31
    if (i==4 || i==6 || i==9 || i==11) {this[i] = 30}
    if (i==2) {this[i] = 29}
  } 
  
  return this
}

function isDate(dtStr)
{
  var daysInMonth = DaysArray(12)
  var pos1=dtStr.indexOf(dtCh)
  var pos2=dtStr.indexOf(dtCh,pos1+1)

  var strYear=dtStr.substring(0,pos1)
  var strMonth=dtStr.substring(pos1+1,pos2)
  var strDay=dtStr.substring(pos2+1)

  //var strMonth=dtStr.substring(0,pos1)
  //var strDay=dtStr.substring(pos1+1,pos2)
  //var strYear=dtStr.substring(pos2+1)
  strYr=strYear
  if (strDay.charAt(0)=="0" && strDay.length>1) strDay=strDay.substring(1)
  if (strMonth.charAt(0)=="0" && strMonth.length>1) strMonth=strMonth.substring(1)
  for (var i = 1; i <= 3; i++) 
  {
    if (strYr.charAt(0)=="0" && strYr.length>1) strYr=strYr.substring(1)
  }
  month=parseInt(strMonth)
  day=parseInt(strDay)
  year=parseInt(strYr)
  if (pos1==-1 || pos2==-1)
  {
    alert("The date format should be : yyyy-mm-dd")
    return false
  }
  
  if (strMonth.length<1 || month<1 || month>12)
  {
    alert("Please enter a valid month")
    return false
  }
  if (strDay.length<1 || day<1 || day>31 || (month==2 && day>daysInFebruary(year)) || day > daysInMonth[month])
  {
    alert("Please enter a valid day")
    return false
  }
  if (strYear.length != 4 || year==0 || year<minYear || year>maxYear)
  {
    alert("Please enter a valid 4 digit year between "+minYear+" and "+maxYear)
    return false
  }
  if (dtStr.indexOf(dtCh,pos2+1)!=-1 || isInteger(stripCharsInBag(dtStr, dtCh))==false)
  {
    alert("Please enter a valid date")
    return false
  }

  return true
}

function isInteger(s)
{
  var n = trim(s);
  return n.length > 0 && !(/[^0-9]/).test(n);
}

function validateWeightField( dataElementId, recordNo )
{
  var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );    
  var resVal = field.value;
  var resVal = resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '');

  if(resVal == null || resVal == "" )
  {    
    field.value = "";
    field.focus();
    
    return false;
  }

  if( isInteger( resVal) || resVal.toUpperCase() == "NK" )
  {
    saveLLbirthValue( dataElementId, recordNo );
  }
  else
  {
  	alert("Please enter weight in Grams");
  	field.value = "";
    field.focus();
    
    return false;
  }	
}

function validateNameField( dataElementId, recordNo )
{
  var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );    
  var resVal = field.value;

  if(resVal == null || resVal.replace(/^\s\s*/, '').replace(/\s\s*$/, '') == "" )
  {
    alert("Please enter Name");
    field.value = "";
    field.focus();
    
    return false;
  }
  
  saveLLbirthValue( dataElementId, recordNo );  	
}

function validateBirthDateField( dataElementId, recordNo )
{
	var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );    
  var resVal = field.value;
    
	if( isDate(resVal) )
	{	
    saveLLbirthValue( dataElementId, recordNo );
	}
	else
	{
	 field.value = "";
	 field.focus();
	}	
	
	return false; 
}


// -----------------------------------------------------------------------------
// Save
// -----------------------------------------------------------------------------

function saveLLbirthValue( dataElementId, recordNo )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value:value[' + recordNo + '].value' );    
    var resVal = '';
    
    field.style.backgroundColor = '#ffffcc';
    
    if(dataElementId == 1022 || dataElementId == 1025 || dataElementId == 1029 || dataElementId == 1030 || dataElementId == 1031 || dataElementId == 1035 || dataElementId == 1036 || dataElementId == 1037 || dataElementId == 1038 || dataElementId == 1039 || dataElementId == 1040 || dataElementId == 1043 || dataElementId == 1046 || dataElementId == 1050 || dataElementId == 1051 || dataElementId == 1052 || dataElementId == 1054)
    {
    	resVal = field.options[field.selectedIndex].value;
    	if(resVal == "NONE") return;
    }
    else
    	resVal = field.value; 
    
    if ( resVal != '' )
    {
    	var deIdRecordNo = dataElementId + ":" + recordNo;
	    var valueSaver = new ValueSaver( deIdRecordNo, resVal, '#ccffcc' );
    	valueSaver.save();        
    }

}





function saveValue( dataElementId, dataElementName )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value' );
    var type = document.getElementById( 'value[' + dataElementId + '].type' ).innerHTML;
    
    field.style.backgroundColor = '#ffffcc';
    
    if ( field.value != '' )
    {
        if ( type == 'int' )
        {
            if ( !isInt( field.value ))
            {
                field.style.backgroundColor = '#ffcc00';

                window.alert( i18n_value_must_integer + '\n\n' + dataElementName );

                field.select();
                field.focus();

                return;
            }
            else
            {
                var minString = document.getElementById( 'value[' + dataElementId + '].min' ).innerHTML;
                var maxString = document.getElementById( 'value[' + dataElementId + '].max' ).innerHTML;

                if ( minString.length != 0 && maxString.length != 0 )
                {
                    var value = new Number( field.value );
                    var min = new Number( minString );
                    var max = new Number( maxString );

                    if ( value < min )
                    {
                        var valueSaver = new ValueSaver( dataElementId, field.value, '#ffcccc' );
                        valueSaver.save();
                        
                        window.alert( i18n_value_of_data_element_less + '\n\n' + dataElementName );
                        
                        return;
                    }

                    if ( value > max )
                    {
                        var valueSaver = new ValueSaver( dataElementId, field.value, '#ffcccc' );
                        valueSaver.save();
                        
                        window.alert( i18n_value_of_data_element_greater + '\n\n' + dataElementName);
                        
                        return;
                    }
                }
            }
        }
    }

    var valueSaver = new ValueSaver( dataElementId, field.value, '#ccffcc' );
    valueSaver.save();

    if ( type == 'int')
    {
    	calculateCDE(dataElementId);
    }

}

function saveBoolean( dataElementId )
{
    var select = document.getElementById( 'value[' + dataElementId + '].boolean' );
    
    select.style.backgroundColor = '#ffffcc';
    
    var valueSaver = new ValueSaver( dataElementId, select.options[select.selectedIndex].value, '#ccffcc' );
    valueSaver.save();
}

function saveComment( dataElementId, commentValue )
{
    var field = document.getElementById( 'value[' + dataElementId + '].comment' );
    var select = document.getElementById( 'value[' + dataElementId + '].comments' );
    
    field.style.backgroundColor = '#ffffcc';
    select.style.backgroundColor = '#ffffcc';
    
    var commentSaver = new CommentSaver( dataElementId, commentValue );
    commentSaver.save();
}

function isInt( value )
{
    var number = new Number( value );
    
    if ( isNaN( number ))
    {
        return false;
    }
    
    return true;
}

// -----------------------------------------------------------------------------
// Saver objects
// -----------------------------------------------------------------------------

function ValueSaver( dataElementId_, value_, resultColor_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ccccff';

    var dataElementId = dataElementId_;
    var value = value_;
    var resultColor = resultColor_;
    
    this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );
        request.send( 'saveValue.action?dataElementId=' +
                dataElementId + '&value=' + value );
    };
    
    function handleResponse( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
        
        if ( code == 0 )
        {            
            nextFlag = 0;

            markValue( resultColor );
            
            //var textNode;
            
            //var timestampElement = rootElement.getElementsByTagName( 'timestamp' )[0];
            //var timestampField = document.getElementById( 'value[' + dataElementId + '].timestamp' );
            //textNode = timestampElement.firstChild;
            
            //timestampField.innerHTML = ( textNode ? textNode.nodeValue : '' );
            
            //var storedByElement = rootElement.getElementsByTagName( 'storedBy' )[0];
            //var storedByField = document.getElementById( 'value[' + dataElementId + '].storedBy' );
            //textNode = storedByElement.firstChild;

            //storedByField.innerHTML = ( textNode ? textNode.nodeValue : '' );
        }
        else
        {
            markValue( ERROR );
            window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
        }
    }
    
    function handleHttpError( errorCode )
    {
        markValue( ERROR );
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }
    
    function markValue( color )
    {
        //var type = document.getElementById( 'value[' + dataElementId + '].type' ).innerText;
        var element;
        
/*        if ( type == 'bool' )
        {
            element = document.getElementById( 'value[' + dataElementId + '].boolean' );
        }
        else
        {
            element = document.getElementById( 'value[' + dataElementId + '].value' );
        }
*/
		var temp = new Array();
		temp = dataElementId.split(":");
				
        element = document.getElementById( 'value[' + temp[0] + '].value:value['+ temp[1] +'].value' );
        element.style.backgroundColor = color;
    }
}

function CommentSaver( dataElementId_, value_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ccccff';

    var dataElementId = dataElementId_;
    var value = value_;
    
    this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );
        request.send( 'saveComment.action?dataElementId=' +
                dataElementId + '&comment=' + value );
    };
    
    function handleResponse( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
        
        if ( code == 0 )
        {
            markComment( SUCCESS );
            
            //var textNode;
            
            //var timestampElement = rootElement.getElementsByTagName( 'timestamp' )[0];
            //var timestampField = document.getElementById( 'value[' + dataElementId + '].timestamp' );
            //textNode = timestampElement.firstChild;
            
            //timestampField.innerHTML = ( textNode ? textNode.nodeValue : '' );
            
            //var storedByElement = rootElement.getElementsByTagName( 'storedBy' )[0];
            //var storedByField = document.getElementById( 'value[' + dataElementId + '].storedBy' );
            //textNode = storedByElement.firstChild;

            //storedByField.innerHTML = ( textNode ? textNode.nodeValue : '' );
        }
        else
        {
            markComment( ERROR );
            window.alert( i18n_saving_comment_failed_status_code + '\n\n' + code );
        }
    }
    
    function handleHttpError( errorCode )
    {
        markComment( ERROR );
        window.alert( i18n_saving_comment_failed_error_code + '\n\n' + errorCode );
    }
    
    function markComment( color )
    {
        var field = document.getElementById( 'value[' + dataElementId + '].comment' );
        var select = document.getElementById( 'value[' + dataElementId + '].comments' );

        field.style.backgroundColor = color;
        select.style.backgroundColor = color;
    }
}

// -----------------------------------------------------------------------------
// View history
// -----------------------------------------------------------------------------
/*
function viewHistory( dataElementId )
{
	
    window.open( 'viewHistory.action?dataElementId=' + dataElementId, '_blank', 'width=560,height=550,scrollbars=yes' );
}*/

// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function validate()
{
    window.open( 'validate.action', '_blank', 'width=800, height=400, scrollbars=yes, resizable=yes' );
}

// -----------------------------------------------------------------------------
// CalculatedDataElements
// -----------------------------------------------------------------------------

/**
 * Calculate and display the value of any CDE the given data element is a part of.
 * @param dataElementId  id of the data element to calculate a CDE for
 */
function calculateCDE( dataElementId )
{
    var cdeId = getCalculatedDataElement(dataElementId);
  
    if ( ! cdeId )
    {
  	    return;
    }
    
    var factorMap = calculatedDataElementMap[cdeId];
    var value = 0;
    var dataElementValue;
    
    for ( dataElementId in factorMap )
    {
    	dataElementValue = document.getElementById( 'value[' + dataElementId + '].value' ).value;
    	value += ( dataElementValue * factorMap[dataElementId] );
    }
    
    document.getElementById( 'value[' + cdeId + '].value' ).value = value;
}

/**
 * Returns the id of the CalculatedDataElement this DataElement id is a part of.
 * @param dataElementId id of the DataElement
 * @return id of the CalculatedDataElement this DataElement id is a part of,
 *     or null if the DataElement id is not part of any CalculatedDataElement
 */
function getCalculatedDataElement( dataElementId )
{
    for ( cdeId in calculatedDataElementMap )
    {
  	    var factorMap = calculatedDataElementMap[cdeId];

  	    if ( deId in factorMap )
  	    {
  	    	return cdeId;
  	    }

    }

    return null;
}

function calculateAndSaveCDEs()
{
    var request = new Request();
    request.setCallbackSuccess( dataValuesReceived );
    request.setResponseTypeXML( 'dataValues' );
    request.send( 'calculateCDEs.action' );
}

function dataValuesReceived( node )
{
	var values = node.getElementsByTagName('dataValue');
    var dataElementId;
    var value;

	for ( var i = 0, value; value = values[i]; i++ )
	{
		dataElementId = value.getAttribute('dataElementId');
		value = value.firstChild.nodeValue;		
		document.getElementById( 'value[' + dataElementId + '].value' ).value = value;
	}
}



function saveLineListingAggData()
{
    var request = new Request();
    request.setCallbackSuccess( saveLineListingAggDataReceived );
    request.setResponseTypeXML( 'dataValues' );
    request.send( 'saveLineListingAggData.action' );
}

function saveLineListingAggDataReceived( node )
{
	alert("Aggregated DataElements Saved");
	
	var values = node.getElementsByTagName('dataValue');
    var dataElementId;
    var value;

	for ( var i = 0, value; value = values[i]; i++ )
	{
		dataElementId = value.getAttribute('dataElementId');
		optionComboId = value.getAttribute('optionComboId');
		
		value = value.firstChild.nodeValue;
		//document.getElementById( 'value[' + dataElementId + '].value' + ':' +  'value[' + optionComboId + '].value').value = value;		
		document.getElementById( 'value[' + dataElementId + '].value' ).value = value;
	}
}