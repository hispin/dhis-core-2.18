function getPeriods() {
    var periodTypeList = document.getElementById( "periodTypeId" );
    var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;

    if ( periodTypeId != null ) {
        var url = "../dhis-web-commons-ajax/getPeriods.action?name=" + periodTypeId;
        $.ajax({
            url: url,
            cache: false,
            success: function(response){
                dom = parseXML(response);
                $( '#periodId >option' ).remove();
                $(dom).find('period').each(function(){
                    $('#periodId').append("<option value="+$(this).find('id').text()+">" +$(this).find('name').text()+ "</option>");
                });
                document.getElementById( "periodId" ).disabled = false;
            }
        });
    }
}

function parseXML( xml ) {
    if( window.ActiveXObject && window.GetObject ) {
        var dom = new ActiveXObject( 'Microsoft.XMLDOM' );
        dom.loadXML( xml );
        return dom;
    }
    if( window.DOMParser )
        return new DOMParser().parseFromString( xml, 'text/xml' );
    throw new Error( 'No XML parser available' );
}

function getDataSets() {
    var periodList = document.getElementById( "periodId" );
    var periodId = periodList.options[ periodList.selectedIndex ].value;

    if ( periodId != null ) {
        var url = "getDataSets.action?periodId=" + periodId;
        $.ajax({
            url:url,
            cache: false,
            success: function(response){
                $( '#unlockedDataSets >option' ).remove();
                $( '#lockedDataSets >option' ).remove();
                $(response).find('dataSet').each(function(){
                    if($(this).find('locked').text() == 'false'){
                        $('#unlockedDataSets').append("<option value="+$(this).find('id').text()+">" +$(this).find('name').text()+ "</option>");
                    }
                    else {
                        $('#lockedDataSets').append("<option value="+$(this).find('id').text()+">" +$(this).find('name').text()+ "</option>");
                    }
                });
                document.getElementById( "unlockedDataSets" ).disabled = false;
                document.getElementById( "lockedDataSets" ).disabled = false;
            }
        });
    }
}

function updateDataSets() {
    if ( validateLocking() )  {
        selectAllById( "lockedDataSets" );
        selectAllById( "unlockedDataSets" );

        document.getElementById( "lockingForm" ).submit();
    }
}

function validateLocking() {
    if ( getListValue( "periodTypeId" ) == "null" ) {
        setMessage( i18n_select_a_period_type );
        return false;
    }

    if ( getListValue( "periodId" ) == "null" ) {
        setMessage( i18n_select_a_period );
        return false;
    }
    return true;
}