
//--------------------------------------------------------------
// Get Aggregated Dataelements
//--------------------------------------------------------------
function getAggDataElements( )
{
  var degroup = document.getElementById( 'degroup' );
  var degId = degroup.options[ degroup.selectedIndex ].value;

  var requestString = 'getAggDataElements.action?degId=' + degId;

  var request = new Request();
  request.setResponseTypeXML( 'dataelement' );
  request.setCallbackSuccess( getAggDataElementsCompleted );

  request.send( requestString );
}

function getAggDataElementsCompleted( dataelementElement )
{
  var de = document.getElementById( 'aggde' );
  
  clearList( de );
  	
  var dataElementList = dataelementElement.getElementsByTagName( 'dataelement' );
 
  for ( var i = 0; i < dataElementList.length; i++ )
    {
        var id = dataElementList[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var name = dataElementList[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;

        var option = document.createElement("option");
        option.value = id;
        option.text = name;
        option.title = name;
        
        de.add(option, null);       	
    }	    
}

//--------------------------------------------------------------
// Get Program Stages
//--------------------------------------------------------------
function getProgramStages()
{
  var program = document.getElementById( 'program' );
  var programId = program.options[ program.selectedIndex ].value;
    

  var requestString = 'getPrgramStages.action?programId=' + programId;

  var request = new Request();
  request.setResponseTypeXML( 'programstage' );
  request.setCallbackSuccess( getProgramStagesCompleted );

  request.send( requestString );	
}

function getProgramStagesCompleted( programstageElement )
{
  var programstage = document.getElementById( 'programstage' );
  
  clearList( programstage );
  	
  var programstageList = programstageElement.getElementsByTagName( 'programstage' );
 
  for ( var i = 0; i < programstageList.length; i++ )
    {
        var id = programstageList[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var name = programstageList[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;

        var option = document.createElement("option");
        option.value = id;
        option.text = name;
        option.title = name;
        
        programstage.add(option, null);       	
    }
    if( programstage.options.length > 0 )
    {
    	programstage.options[0].selected = true;
    	
    	getPrgramStageDataElements();
    }
    else
    {
    	var programstageDE = document.getElementById( 'programstageDE' );
  
  		clearList( programstageDE );
    }	    
}

//--------------------------------------------------------------
// Get Program Stage DataElements
//--------------------------------------------------------------
function getPrgramStageDataElements()
{
  var programStage = document.getElementById( 'programstage' );
  var psId = programStage.options[ programStage.selectedIndex ].value;

  var requestString = 'getPSDataElements.action?psId=' + psId;

  var request = new Request();
  request.setResponseTypeXML( 'dataelement' );
  request.setCallbackSuccess( getPrgramStageDataElementsCompleted );

  request.send( requestString );	
}

function getPrgramStageDataElementsCompleted( dataelementElement )
{
  var programstageDE = document.getElementById( 'programstageDE' );
  
  clearList( programstageDE );
  	
  var programstageDEList = dataelementElement.getElementsByTagName( 'dataelement' );
 
  for ( var i = 0; i < programstageDEList.length; i++ )
    {
        var id = programstageDEList[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var name = programstageDEList[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		var type = programstageDEList[ i ].getElementsByTagName("type")[0].firstChild.nodeValue;

        var option = document.createElement("option");
        option.value = id;
        option.text = name;
        option.title = name;
        jQuery(option).attr({data:"{type:'"+type+"'}"});
        programstageDE.add(option, null);       	
    }	    
}

//--------------------------------------------------------------
// Get CaseAgg Expression
//--------------------------------------------------------------
function getCaseAggExpression( )
{
  var aggde = document.getElementById( 'aggde' );
  var aggdeId = aggde.options[ aggde.selectedIndex ].value;

  var requestString = 'getCaseAggExpression.action?aggdeId=' + aggdeId;

  var request = new Request();
  request.setResponseTypeXML( 'expression' );
  request.setCallbackSuccess( getCaseAggExpressionCompleted );

  request.send( requestString );
}

function getCaseAggExpressionCompleted( expressionElement )
{
  var expressionTA = document.getElementById( 'expression' );
  
  var expression = expressionElement.firstChild.nodeValue;
 
  expressionTA.value = expression;
}

//-----------------------------------------------------------------
//
//-----------------------------------------------------------------
function updateFocus(x)
{
	holdFocus = x;	
}

function displayPSDEInfo()
{
	var programstageDE = document.getElementById( 'programstageDE' );
	
	var data = jQuery.metadata.get(holdFocus);
	
	if( data.pos == "left" )
	{
		holdFocus.value += programstageDE.options[ programstageDE.selectedIndex ].value;
	}	
	else
	{
		var selDe = programstageDE.options[ programstageDE.selectedIndex ];
		var data1 = jQuery.metadata.get( selDe );
		var isMul = data1.type.split(":")[0];
		var dataType = data1.type.split(":")[1]; 
		 
		//alert(isMul +" : "+ dataType);
		 
		if( dataType == stringType && isMul == "1" )
		{
			var result = window.confirm( 'Do you want to select from optioncombo(s)?' );

  			if ( result )
  			{
    			window.location.href = 'delDataSet.action?dataSetId=' + dataSetId;
  			}
		}
		else
		{
			holdFocus.value += selDe.value;
		}
	}
}

function displayCPInfo()
{
	var caseProperty = document.getElementById( 'caseProperty' );
	
	holdFocus.value += caseProperty.options[ caseProperty.selectedIndex ].value;	
}

function displayCAInfo()
{
	var caseAttribute = document.getElementById( 'caseAttribute' );
	
	holdFocus.value += caseAttribute.options[ caseAttribute.selectedIndex ].value;	
}

function removeRecord( delRecNo )
{
	var tbl = document.getElementById("tblGrid");
	tbl.deleteRow(delRecNo);
	
	var delRecTB = document.getElementById("delRecTB");
	delRecTB.value += delRecNo + ",";
}

function addNewRow( )
{
	var tbl = document.getElementById("tblGrid");
	var lastRow = tbl.rows.length;
	var newRow = tbl.insertRow(lastRow);
	tableRowCount++;
            
    var oCell = newRow.insertCell(0);
    oCell.innerHTML = "<input type='text' id='le"+ tableRowCount +"' name='le"+ tableRowCount +"' data='{pos:\"left\"}' onfocus='updateFocus(this)'>";

    oCell = newRow.insertCell(1);
    oCell.innerHTML = "<select id='operator"+ tableRowCount +"' name='operator"+ tableRowCount +"' onchange=''><option value='NA'>Select</option><option value='less_than'><</option><option value='greater_than'>></option><option value='less_than_equal'><=</option><option value='greater_than_equal'>>=</option><option value='equal'>=</option><option value='not_equal'>!=</option><option value='in'>IN</option><option value='diff'>DIFF</option></select>";
        
    oCell = newRow.insertCell(2);
    oCell.innerHTML = "<input type='text' id='re"+ tableRowCount +"' name='re"+ tableRowCount +"' data='{pos:\"right\"}' onfocus='updateFocus(this)'>";
    
    oCell = newRow.insertCell(3);    	
	oCell.innerHTML = "<select id='andor"+ tableRowCount +"' name='andor"+ tableRowCount +"' onchange='addNewRow()'><option value='NA'>Select</option><option value='and'>AND</option><option value='or'>OR</option></select>";

    oCell = newRow.insertCell(4);
	oCell.innerHTML = "<a href='javascript:removeRecord("+ tableRowCount +")' title='remove' )><img src='../images/delete.png' alt='remove'></a>";
}

function prepareExpression()
{	
	var i = 0;
	var delRecTB = document.getElementById("delRecTB");
	var delRecNos = delRecTB.value.split(",");
	
	var finalExp="NOTHING";
	
	var csRadio = document.getElementById("csRadio");
	
	if(csRadio.value == "sumRadio")
		finalExp = "SUM@";
	else
		finalExp = "COUNT@"; 
	
	for( i = 0; i <= tableRowCount; i++ )
	{				
		var flag = 0;
		for( var j=0; j< delRecNos.lenght; j++ )
		{
			if( delRecNos[j] == i ) { falg =1; break; }
		}
		if(flag == 0 )
		{
			var lCell = document.getElementById("le"+i);
			var opeCell = document.getElementById("operator"+i);
			var rCell = document.getElementById("re"+i);
			var and_orCell = document.getElementById("andor"+i);
		
			if( opeCell.options[ opeCell.selectedIndex ].value == "NA" && ( rCell.value == null || trim(rCell.value) == "" ) )
			{
				finalExp += " SCOND{ ( " + lCell.value + " ) } ";
			}
			else
			{
				finalExp += " COND{ ( " + lCell.value + " ) " + opeCell.options[ opeCell.selectedIndex].value + " ( [" + rCell.value + "] ) } ";	
			}
			
			if( and_orCell.options[ and_orCell.selectedIndex ].value != "NA" ) finalExp += and_orCell.options[ and_orCell.selectedIndex ].value;
		}
	}
	
	var expTA = document.getElementById("expression");
	expTA.value = finalExp;	
}

// -----------------------------------------------------------------------------
// String Trim
// -----------------------------------------------------------------------------

function trim( stringToTrim ) 
{
  return stringToTrim.replace(/^\s+|\s+$/g,"");
}

