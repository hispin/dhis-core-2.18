
//------------------------------------------------------------------------------
// Get Aggregated Dataelements
//------------------------------------------------------------------------------
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

//------------------------------------------------------------------------------
// Get Program Stages
//------------------------------------------------------------------------------
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
 
  for( var i = 0; i < programstageList.length; i++ )
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

//------------------------------------------------------------------------------
// Get Program Stage DataElements
//------------------------------------------------------------------------------
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
	if(holdFocus)
	{
		var programstageDE = document.getElementById( 'programstageDE' );
	
		var data = jQuery.metadata.get(holdFocus);
		
		var selDe = programstageDE.options[ programstageDE.selectedIndex ];
		var data1 = jQuery.metadata.get( selDe );
		var isMul = data1.type.split(":")[0];
		var dataType = data1.type.split(":")[1];
		
		programstageDEisMul=isMul;
		programstageDEdatatype=dataType;
		
		delRecNos=parseInt(holdFocus.name.slice(2,holdFocus.name.length));
		operatorSelected=document.getElementById("operator"+delRecNos);
		
		if( data.pos == "left" )
		{
			holdFocus.value += programstageDE.options[ programstageDE.selectedIndex ].value;
			selectOperatorOptions(operatorSelected, dataType);
		}	
		else if(data.pos=="right")
		{ 	 
			//alert(isMul +" : "+ dataType);
			 
			if( dataType == stringType && isMul == "1" && operatorSelected.options[operatorSelected.selectedIndex].value=='in')
			{
				var requestString = 'caseAggregationOptionCapture.action?id='+programstageDE.options[ programstageDE.selectedIndex ].value;
				var request = new Request();
		  		request.setResponseTypeXML( 'dataelement' );
		  		request.setCallbackSuccess(prepare_multioption_combo);
	  			request.send( requestString );
			}
			else
			{
				holdFocus.value += selDe.value;
			}
		}
	}
	
}

function selectOperatorOptions(operatorSelected, dataType)
{
	switch (dataType)
	{
		case stringType: 
			var disabled_options=[1,2,3,4,5,8];
			break;
		case intType:
			var disabled_options=new Array();
			break;
		case dateType:
			var disabled_options=new Array();
			break;
		case boolType:
			var disabled_options=[1,2,3,4,7,8];
			break;
		default:
			var disabled_options=new Array();
	}
	
	var options=operatorSelected.options;
	options[0].selected=true;
	for( i = 0; i < options.length; i++ )
	{
		var flag = 0;
		for( j = 0; j < disabled_options.length; j++ )
		{
			if( i == disabled_options[j] )
			{
				flag=1;
				break;
			}
		}
		if( flag == 1 )
		{
			options[i].disabled=true;	
		}
		else
		{
			options[i].disabled=false;
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

function removeRecord( obj, delRecNo )	//edit :ankit gangal 9/6
{
	delRowNo=obj.parentNode.parentNode.rowIndex;
	noofrows=obj.parentNode.parentNode.parentNode.getElementsByTagName('tr').length;
	
	if(noofrows!=2)
	{
		var tbl = document.getElementById("tblGrid");
		tbl.deleteRow(delRowNo);
		
		var delRecTB = document.getElementById("delRecTB");
		delRecTB.value += delRecNo + ",";
	} 
	else
	{
		alert("last box cannot be deleted");
	}
}

function addNewRow()
{
		var tbl = document.getElementById("tblGrid");
		var lastRow = tbl.rows.length;
		var newRow = tbl.insertRow(lastRow);
		tableRowCount++;
	            
	    var oCell = newRow.insertCell(0);
	    oCell.innerHTML = "<input type='text' id='le"+ tableRowCount +"' name='le"+ tableRowCount +"' data='{pos:\"left\"}' onfocus='updateFocus(this)'>";
	
	    oCell = newRow.insertCell(1);
	    oCell.innerHTML = "<select id='operator"+ tableRowCount +"' name='operator"+ tableRowCount +"'><option value='NA'>Select</option><option value='\<'>&lt;</option><option value='\>'>&gt;</option><option value='\<='>&le;</option><option value='\>='>&ge;</option><option value='\='>=</option><option value='!='>!=</option><option value='in'>IN</option><option value='diff'>DIFF</option></select>";
	        
	    oCell = newRow.insertCell(2);
	    oCell.innerHTML = "<input type='text' id='re"+ tableRowCount +"' name='re"+ tableRowCount +"' data='{pos:\"right\"}' onfocus='updateFocus(this)'>";
	    
	    oCell = newRow.insertCell(3);    	
		oCell.innerHTML = "<select id='andor"+ tableRowCount +"' name='andor"+ tableRowCount +"' onchange='addNewRow()'><option value='NA'>Select</option><option value='AND'>AND</option><option value='OR'>OR</option></select>";
	
	    oCell = newRow.insertCell(4);
		oCell.innerHTML = "<a href='#Delete table row' onclick='removeRecord(this,"+tableRowCount+")' title='remove' )><img src='../images/delete.png' alt='remove'></a>";
}

function prepareExpression()
{	
	var i = 0;
	var delRecTB = document.getElementById("delRecTB");
	delRecTBvalue=delRecTB.value;
	//delRecTBvalue.slice(0,delRecTBvalue.length-1);
	var delRecNos = delRecTBvalue.split(",");
	
	var finalExp="NOTHING";
	
	//var csRadio = document.getElementById("csRadio");
	var csRadio=document.getElementsByName("csRadio");
	
	if(csRadio[1].checked == true)	//sumRadio is checked
		finalExp = "SUM@";
	else if(csRadio[0].checked==true)	//countRadio is checked
		finalExp = "COUNT@"; 
	
	for( i = 0; i <= tableRowCount; i++ )
	{				
		var flag = 0;
		for( var j=0; j< delRecNos.length; j++ )
		{
			if(delRecNos[j]=="") {break;}
			if( delRecNos[j] == i) { flag =1; break; }
		}
		if(flag == 0 )
		{
			if(and_orCell)
				if( and_orCell.options[ and_orCell.selectedIndex ].value != "NA" ) finalExp += and_orCell.options[ and_orCell.selectedIndex ].value;
			var lCell = document.getElementById("le"+i);
			var opeCell = document.getElementById("operator"+i);
			var rCell = document.getElementById("re"+i);
			var and_orCell = document.getElementById("andor"+i);
		
			if( opeCell.options[ opeCell.selectedIndex ].value == "NA" || ( rCell.value == null || trim(rCell.value) == "" )  || ( lCell.value == null || trim(lCell.value) == "" ) )
			{
				finalExp += " SCOND{ ( " + lCell.value + " ) } ";
			}
			else
			{
				finalExp += " COND{ ( " + lCell.value + " ) " + opeCell.options[ opeCell.selectedIndex].value + " ( [" + rCell.value + "] ) } ";	
			}
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

function prepare_multioption_combo(dataelement)
{
	var data = holdFocus;
	var option_list = dataelement.getElementsByTagName( 'dataelement' );
	
	var multipleoptioncombobox= document.getElementById('multiple_option_combo_id_box');
	clearList( multipleoptioncombobox );
	for ( var i = 0; i < option_list.length; i++ )
	{
		var id = option_list[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = option_list[i].getElementsByTagName("name")[0].firstChild.nodeValue;
		name=name.slice(1,name.length);
		name=name.slice(0,name.length-1);
		var option = document.createElement("option");
		option.value = id;
		option.text = name;
		option.title = name;
		option.ondblclick=select_click;
		multipleoptioncombobox.add(option, null);
	}
	multipleoptioncombo=document.getElementById('multiple_option_combo_id');
	multipleoptioncombo.style.top=(find_positionY(data)+22)+"px";
	multipleoptioncombo.style.left=find_positionX(data)+"px";
	darkout(true);
	multipleoptioncombo.style.display='';
}

function select_click()
{
	var data = holdFocus;
	var multipleoptioncombobox= document.getElementById('multiple_option_combo_id_box');
	var options=multipleoptioncombobox.options;
	var option_string="(";
	for(i=0; i<options.length; i++)
	{
		if(options[i].selected==true)
		{
			option_string+="'"+options[i].text+"', ";
		}
	}
	option_string=option_string.slice(0,option_string.length-2);
	option_string+=")";
	data.value=option_string;
	multipleoptioncombobox.parentNode.style.display="none";
}


function find_positionY(obj)
{
	var curtop = 0;
	if (obj.offsetParent) 
	{
		curtop = obj.offsetTop;
		while (obj = obj.offsetParent) 
		{
			curtop += obj.offsetTop;
		}
	}
	
	return curtop;
}

function find_positionX(obj)
{
	var curleft = 0;
	if (obj.offsetParent) 
	{
		curleft = obj.offsetLeft;
		while (obj = obj.offsetParent) 
		{
			curleft += obj.offsetLeft;
		}
	}
	return curleft;
}
function getTableFromQuery()
{
	var str=trim(document.getElementById("expression").value);
	if(str!="")
	{
		var tbl = document.getElementById("tblGrid");
		tblrows=tbl.getElementsByTagName("tr");
		while(tblrows.length!=1)
		{
			tbl.deleteRow(1);
			tblrows=tbl.getElementsByTagName("tr");
		}
		
		tableRowCount=-1;
		document.getElementById("delRecTB").value="";
	    var str1=str.split("@");
	    
	    var csRadio=document.getElementsByName("csRadio");
	    
	    if(trim(str1[0])=="COUNT")
	    	csRadio[0].checked = true;
	    else if(trim(str1[0])=="SUM")
	    	csRadio[1].checked=true;
	    start_pos=0;
	    str1[1]=trim(str1[1]);
	    first_pos=str1[1].indexOf("COND{", 0);
	    
	    while(first_pos!=-1)
	    {
	    	start_pos=first_pos+5;
	        first_condition=trim(str1[1].slice(str1[1].indexOf("(", start_pos)+1,str1[1].indexOf(")", start_pos)));

	        start_pos=str1[1].indexOf(")", start_pos);
	        if(start_pos==-1)
	        {
	            break;
	        }
	        constraint=trim(str1[1].slice(start_pos+1, str1[1].indexOf("(", start_pos)));
	        start_pos=str1[1].indexOf("(", start_pos);
	        second_condition=trim(str1[1].slice(start_pos+1, str1[1].indexOf("}", start_pos)));
	        second_condition=trim(second_condition.slice(1,second_condition.length-1));
	        second_condition=trim(second_condition.slice(0,second_condition.length-1));
	        start_pos=str1[1].indexOf("}", start_pos);
	                    
	        if(start_pos==-1)
	        	break;
	        if(start_pos<first_pos)
	        	break;
	        
	         addNewRow();
	        document.getElementById("le"+tableRowCount).value=first_condition;
			operator=document.getElementById("operator"+tableRowCount);
			for(var i=0; i<operator.options.length;i++)
			{
				if(operator.options[i].value==constraint)
				{
					operator.options[i].selected=true;
					break;
				}
			}
			document.getElementById("re"+tableRowCount).value=second_condition;
	                    
	        first_pos1=str1[1].indexOf("SCOND{", start_pos);
	        first_pos=str1[1].indexOf("COND{", start_pos);
	                    
			while(first_pos==(first_pos1+1))
	        {
	        	start_pos=first_pos+11;
	            first_pos=str1[1].indexOf("COND{", start_pos);
	            first_pos1=str1[1].indexOf("SCOND{", start_pos);
			}
	        if(first_pos==-1)
	        	break;
			andor=trim(str1[1].slice(start_pos+1,first_pos-1));
	        
			operator=document.getElementById("andor"+tableRowCount);
			for(var i=0; i<operator.options.length;i++)
			{
				if(operator.options[i].value==andor)
				{
					operator.options[i].selected=true;
					break;
				}
			}
		}
	}
}

function darkout(visible)
{
	var dark=document.getElementById('darkenScreenObject');
    if (!dark)
    {
    	var tbody = document.getElementsByTagName("body")[0];
		var tnode = document.createElement('div');
		tnode.style.position='absolute';
		tnode.style.top='0px';
		tnode.style.left='0px';
		tnode.style.overflow='hidden';
		tnode.style.display='none';
		tnode.id='darkenScreenObject';
		tbody.appendChild(tnode);
		dark=document.getElementById('darkenScreenObject');
    }
    if (visible) 
    {
    	var pageWidth='100%';
		var pageHeight='100%';
		dark.style.zIndex=89;
		dark.style.width= pageWidth;
		dark.style.height= pageHeight;
		dark.style.display="";
		dark.onclick = function() //attach a event handler to hide both div
		{
		  document.getElementById("multiple_option_combo_id").style.display="none";
		  dark.style.display="none";
		};
    }
    else
    	dark.style.display="none";
}