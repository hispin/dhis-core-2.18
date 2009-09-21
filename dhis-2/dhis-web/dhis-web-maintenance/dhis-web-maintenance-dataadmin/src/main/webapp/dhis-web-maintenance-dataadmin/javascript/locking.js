function getPeriods() {
    var periodTypeList = document.getElementById( "periodTypeId" );
    var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;

    if ( periodTypeId != null ) {
        var url = "../dhis-web-commons-ajax/getPeriodsForLock.action?name=" + periodTypeId;
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
        var url = "getDataSetsForOrguntwiseLocking.action?periodId=" + periodId;
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
                LoadEmptyOrgUnitTree();
            }
        });
    }
}
    
function cancilSelection() {    
     window.location.href = "displayLockingForm.action";   
}

function updateDataSets() {
    if ( validateLocking() )  {
        selectAllById( "lockedDataSets" );
        selectAllById( "unlockedDataSets" );      
        document.getElementById( "lockingForm" ).submit();
     }
}

function LoadOrgUnitTree(){
	 Reload();
     desableLockGeneralComponenets(); 
	 desableLockOptionButtons();
	 var periodList = document.getElementById( "periodId" );
     var periodId = periodList.options[ periodList.selectedIndex ].value;
     var lockedDataSetList = document.getElementById('lockedDataSets');
     var selectedLockedDataSetId = lockedDataSetList.options[ lockedDataSetList.selectedIndex ].value;
          
     iframeForOUTree.location.href='orgunitWiseSetupAssociationsTree.action?selectedLockedDataSetId=' + selectedLockedDataSetId + '&periodId=' + periodId;		 
}

function LoadEmptyOrgUnitTree(){  
	 desableLockOptionButtons();              
     iframeForOUTree.location.href='emptyOrgunitSetupAssociationsTree.action';		 
}

function clearFrame(){
	x = window.frames["iframeForOUTree"];
	//x.document.open();
	x.document.close(); 
}

function Reload() {
	var f = document.getElementById('iframeForOUTree');
	f.contentWindow.location.reload(true);
}

function enableLockGeneralComponenets(){
    parent.document.getElementById( "periodTypeId" ).disabled = false;
	parent.document.getElementById( "periodId" ).disabled = false;
	parent.document.getElementById( "unlockedDataSets" ).disabled = false;
	parent.document.getElementById( "lockedDataSets" ).disabled = false;
	parent.document.getElementById( "button5" ).disabled = false;
	parent.document.getElementById( "button6" ).disabled = false;
	parent.document.getElementById( "button7" ).disabled = false;
	parent.document.getElementById( "button8" ).disabled = false;	
}

function desableLockGeneralComponenets(){
	parent.document.getElementById( "periodTypeId" ).disabled = true;
	parent.document.getElementById( "periodId" ).disabled = true;
	parent.document.getElementById( "unlockedDataSets" ).disabled = true;
	parent.document.getElementById( "lockedDataSets" ).disabled = true;
	parent.document.getElementById( "button5" ).disabled = true;
	parent.document.getElementById( "button6" ).disabled = true;
	parent.document.getElementById( "button7" ).disabled = true;
	parent.document.getElementById( "button8" ).disabled = true;
}

function enableLockOptionButtons(){
	parent.document.getElementById( "submitButton1" ).disabled = false;
	parent.document.getElementById( "submitButton2" ).disabled = false;
	parent.document.getElementById( "submitButton3" ).disabled = false;
    parent.document.getElementById( "submitButton4" ).disabled = false; 
	parent.document.getElementById( "levelList" ).disabled = false;
	parent.document.getElementById( "submitButton9" ).disabled = false;
	parent.document.getElementById( "submitButton" ).disabled = false;
}
		
function desableLockOptionButtons(){
	parent.document.getElementById( "submitButton1" ).disabled = true;
    parent.document.getElementById( "submitButton2" ).disabled = true;
    parent.document.getElementById( "submitButton3" ).disabled = true;
	parent.document.getElementById( "submitButton4" ).disabled = true; 
	parent.document.getElementById( "levelList" ).disabled = true;
	//parent.document.getElementById( "submitButton9" ).disabled = true;
	parent.document.getElementById( "submitButton" ).disabled = true; 
}
		
function desableLockOptionButtonsForApplyLockOnAll(){
	parent.document.getElementById( "submitButton2" ).disabled = true;
	parent.document.getElementById( "submitButton3" ).disabled = true;
	parent.document.getElementById( "submitButton4" ).disabled = true; 
	parent.document.getElementById( "levelList" ).disabled = true;
    //parent.document.getElementById( "submitButton9" ).disabled = true;
	parent.document.getElementById( "submitButton" ).disabled = true; 
}
		
function desableLockOptionButtonsForRemoveAllLocks(){
	parent.document.getElementById( "submitButton1" ).disabled = true;
	parent.document.getElementById( "submitButton3" ).disabled = true;
	parent.document.getElementById( "submitButton4" ).disabled = true; 
    parent.document.getElementById( "levelList" ).disabled = true;
	//parent.document.getElementById( "submitButton9" ).disabled = true;
	parent.document.getElementById( "submitButton" ).disabled = true; 
}
		
function desableLockOptionButtonsForLockAtLevel(){
	parent.document.getElementById( "submitButton1" ).disabled = true;
	parent.document.getElementById( "submitButton2" ).disabled = true;
	parent.document.getElementById( "submitButton4" ).disabled = true; 
	parent.document.getElementById( "levelList" ).disabled = true;
	//parent.document.getElementById( "submitButton9" ).disabled = true;
	parent.document.getElementById( "submitButton" ).disabled = true; 
}
		
function desableLockOptionButtonsForUnlockAtLevel(){
	parent.document.getElementById( "submitButton1" ).disabled = true;
	parent.document.getElementById( "submitButton2" ).disabled = true;
	parent.document.getElementById( "submitButton3" ).disabled = true;
	parent.document.getElementById( "levelList" ).disabled = true;
	//parent.document.getElementById( "submitButton9" ).disabled = true;
	parent.document.getElementById( "submitButton" ).disabled = true; 
}
    
function ApplyAll(){
	 desableLockGeneralComponenets();
	 desableLockOptionButtons();
     //desableLockOptionButtonsForApplyLockOnAll();
     var lockedDataSetList = document.getElementById('lockedDataSets');
     var selectedLockedDataSetId = lockedDataSetList.options[ lockedDataSetList.selectedIndex ].value;           
     iframeForOUTree.location.href ='selectAll.action?selectedLockedDataSetId=' + selectedLockedDataSetId;
}
    
function RemoveAll(){
	 desableLockGeneralComponenets();
	 desableLockOptionButtons();
	 //desableLockOptionButtonsForRemoveAllLocks();       
     var periodList = document.getElementById( "periodId" );
     var periodId = periodList.options[ periodList.selectedIndex ].value;
     var lockedDataSetList = document.getElementById('lockedDataSets');
     var selectedLockedDataSetId = lockedDataSetList.options[ lockedDataSetList.selectedIndex ].value;       
     iframeForOUTree.location.href='unselectAll.action?selectedLockedDataSetId=' + selectedLockedDataSetId + '&periodId=' + periodId;
}
    
function lockAllAtLevel(){
	 desableLockGeneralComponenets();
	 desableLockOptionButtons();
	 //desableLockOptionButtonsForLockAtLevel();
     var periodList = document.getElementById( "periodId" );
     var periodId = periodList.options[ periodList.selectedIndex ].value;
     var list = document.getElementById( 'levelList' );         
     var level = list.options[ list.selectedIndex ].value; 
     var lockedDataSetList = document.getElementById('lockedDataSets');
     var selectedLockedDataSetId = lockedDataSetList.options[ lockedDataSetList.selectedIndex ].value;           
     iframeForOUTree.location.href ='selectLevel.action?level=' + level + '&selectedLockedDataSetId=' + selectedLockedDataSetId + '&periodId=' + periodId;
}

function unLockAllAtLevel(){
     desableLockGeneralComponenets();
	 desableLockOptionButtons();
     //desableLockOptionButtonsForUnlockAtLevel();
     var periodList = document.getElementById( "periodId" );
     var periodId = periodList.options[ periodList.selectedIndex ].value;
     var list = document.getElementById( 'levelList' );         
     var level = list.options[ list.selectedIndex ].value;
     var lockedDataSetList = document.getElementById('lockedDataSets');
     var selectedLockedDataSetId = lockedDataSetList.options[ lockedDataSetList.selectedIndex ].value;
     iframeForOUTree.location.href = 'unselectLevel.action?level=' + level + '&selectedLockedDataSetId=' + selectedLockedDataSetId + '&periodId=' + periodId;
}

function updateDataSetsOrgunitwise(){
     if ( validateLocking() )  {
        selectAllById( "unlockedDataSets" );       
        document.getElementById( "lockingForm" ).submit();            
     }
}
    
function validateLocking(){
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