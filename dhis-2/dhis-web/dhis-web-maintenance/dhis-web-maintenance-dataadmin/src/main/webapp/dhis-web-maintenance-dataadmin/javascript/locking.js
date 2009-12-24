function getPeriods() {
    var periodTypeList = document.getElementById( "periodTypeId" );
    var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;

    if ( periodTypeId != null ) {
        var url = "getPeriodsForLock.action?name=" + periodTypeId;
        $.ajax({
            url: url,
            cache: false,
            success: function(response){
                dom = parseXML(response);
                $( '#periodId >option' ).remove();
                $(dom).find('period').each(function(){
                    $('#periodId').append("<option value="+$(this).find('id').text()+">" +$(this).find('name').text()+ "</option>");
                });
                enable( "periodId" );
                getDataSets();
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
                enable( "unlockedDataSets" );
                enable( "lockedDataSets" );
                loadEmptyOrgUnitTree();
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

function loadEmptyOrgUnitTree(){  
	 desableLockComponents(); 
	 enableParent( "periodTypeId" );
   enableParent( "periodId" );
	 enableParent( "unlockedDataSets" );
   enableParent( "lockedDataSets" );  
   enableParent( "button1" );
	enableParent( "button2" );
	enableParent( "button3" );
  	enableParent( "button4" ); 
   
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

function enableLockComponents(){
  enableParent( "periodTypeId" );
  enableParent( "periodId" );
  enableParent( "unlockedDataSets" );
  enableParent( "lockedDataSets" );
	enableParent( "button1" );
	enableParent( "button2" );
	enableParent( "button3" );
  enableParent( "button4" );
  enableParent( "button5" );
	enableParent( "button6" );
	enableParent( "button7" );
	enableParent( "button8" );
	enableParent( "button9" );	 
	enableParent( "levelList" );
	enableParent( "submitButton" );
}
		
function desableLockComponents(){
	disableParent( "periodTypeId" );
  disableParent( "periodId" );
  disableParent( "unlockedDataSets" );
  disableParent( "lockedDataSets" );
	disableParent( "button1" );
	disableParent( "button2" );
	disableParent( "button3" );
  disableParent( "button4" );
  disableParent( "button5" );
	disableParent( "button6" );
	disableParent( "button7" );
	disableParent( "button8" );
	//disableParent( "button9" );	 
	disableParent( "levelList" );
	disableParent( "submitButton" );
}

function LoadOrgUnitTree(){
	 Reload();
	 desableLockComponents();
	 var periodList = document.getElementById( "periodId" );
   var periodId = periodList.options[ periodList.selectedIndex ].value;
   var lockedDataSetList = document.getElementById('lockedDataSets');
   var selectedLockedDataSetId = lockedDataSetList.options[ lockedDataSetList.selectedIndex ].value;       
   iframeForOUTree.location.href='orgunitWiseSetupAssociationsTree.action?selectedLockedDataSetId=' + selectedLockedDataSetId + '&periodId=' + periodId;		 
}

function ApplyAll(){ 
	 desableLockComponents();  
   var lockedDataSetList = document.getElementById('lockedDataSets');
   var selectedLockedDataSetId = lockedDataSetList.options[ lockedDataSetList.selectedIndex ].value;           
   iframeForOUTree.location.href ='selectAll.action?selectedLockedDataSetId=' + selectedLockedDataSetId;
}
    
function RemoveAll(){
	 desableLockComponents();       
   var periodList = document.getElementById( "periodId" );
   var periodId = periodList.options[ periodList.selectedIndex ].value;
   var lockedDataSetList = document.getElementById('lockedDataSets');
   var selectedLockedDataSetId = lockedDataSetList.options[ lockedDataSetList.selectedIndex ].value;       
   iframeForOUTree.location.href='unselectAll.action?selectedLockedDataSetId=' + selectedLockedDataSetId + '&periodId=' + periodId;
}
    
function lockAllAtLevel(){
	 desableLockComponents();
   var periodList = document.getElementById( "periodId" );
   var periodId = periodList.options[ periodList.selectedIndex ].value;
   var list = document.getElementById( 'levelList' );         
   var level = list.options[ list.selectedIndex ].value; 
   var lockedDataSetList = document.getElementById('lockedDataSets');
   var selectedLockedDataSetId = lockedDataSetList.options[ lockedDataSetList.selectedIndex ].value;           
   iframeForOUTree.location.href ='selectLevel.action?level=' + level + '&selectedLockedDataSetId=' + selectedLockedDataSetId + '&periodId=' + periodId;
}

function unLockAllAtLevel(){
	 desableLockComponents();
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
        desableLockComponents();            
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