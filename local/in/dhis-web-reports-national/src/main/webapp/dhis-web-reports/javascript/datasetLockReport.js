
	
// function for displaying OrgUnit
function getSelectedOrgUnit( orgUnitIds )
{
    if( orgUnitIds != "" )
    {
		$.post("getOrgUnitName.action",
			{
				selectedOrgUnitId : orgUnitIds
			},
			function (data)
			{
				 responseGetSelectedOrgUnitName(data);
			},'xml');
			
	}
}

function responseGetSelectedOrgUnitName( dataelement )
{
    var element = dataelement.getElementsByTagName("dataelement");
    document.reportForm.ouNameTB.value=element[0].getElementsByTagName("OugUnitName")[0].firstChild.nodeValue;
}



//function getAllPeriods() {
	//var periodTypeList = document.getElementById("periodTypeId");
//	var periodTypeId = periodTypeList.options[periodTypeList.selectedIndex].value;

//	if (periodTypeId != null) {
	//	var url = "getPeriodsForLock.action?name=" + periodTypeId;
	//	$.ajax( {
		//	url :url,
		//	cache :false,
		//	success : function(response) {
				/* dom = parseXML(response);
				$('#periodIds >option').remove();
				$(dom).find('period').each(
						function() {
							$('#periodIds').append(
									"<option value="
											+ $(this).find('id').text() + ">"
											+ $(this).find('name').text()
											+ "</option>");
						});
				enable("periodIds"); */
				
			//	getAllPeriodsReceived( response );
				
	//			getDataSets();
		//	}
	//	});
	//}
//}



/*
function getAllPeriodsReceived(xmlObject) {

	var periodList = byId("periodIds");

	clearList(periodList);

	var periods = xmlObject.getElementsByTagName("period");
	for ( var i = 0; i < periods.length; i++) {
		var id = periods[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = periods[i].getElementsByTagName("name")[0].firstChild.nodeValue;;

		/* var option = document.createElement("option");
		option.value = id;
		option.text = name;
		reportsList.add(option, null); */
		
	//	$("#periodIds").append("<option value='"+ id +"'>" + name + "</option>");
	//}
	//$("#periodIds").attr('disabled', false);
//}

// Functions for get availabe periods
function getAllPeriods() {
    var periodTypeList = document.getElementById( "periodTypeId" );
    var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;

    if ( periodTypeId != null ) {
        var url = "getPeriodsForLock.action?name=" + periodTypeId;
        $.ajax({
            url: url,
            cache: false,
            success: function(response){
                dom = parseXML(response);
                $( '#periodIds >option' ).remove();
                $(dom).find('period').each(function(){
                    $('#periodIds').append("<option value="+$(this).find('id').text()+">" +$(this).find('name').text()+ "</option>");
                });
                enable( "periodIds" );
                getDataSets();
            }
        });
    }
}


function parseXML(xml) {
	if (window.ActiveXObject && window.GetObject) {
		var dom = new ActiveXObject('Microsoft.XMLDOM');
		dom.loadXML(xml);
		return dom;
	}
	if (window.DOMParser)
		return new DOMParser().parseFromString(xml, 'text/xml');
	throw new Error('No XML parser available');
}

// functions for get all corresponding dataSets
function getDataSets() {
	var periodTypeList = document.getElementById("periodTypeId");
	var periodType = periodTypeList.options[periodTypeList.selectedIndex].value;

	if (periodType != null) {
		var url = "getDataSetsForLockAction.action?periodType=" + periodType;
		$.ajax( {
			url :url,
			cache :false,
			success : function(response) {
				$('#dataSets >option').remove();
				// $( '#lockedDataSets >option' ).remove();
			$(response).find('dataSet').each(
					function() {
						$('#dataSets').append(
								"<option value=" + $(this).find('id').text()
										+ ">" + $(this).find('name').text()
										+ "</option>");
					});
			enable("dataSets");
			// enable( "lockedDataSets" );
			enable("generate");
			// loadEmptyOrgUnitTree();
		}
		});
	}
}