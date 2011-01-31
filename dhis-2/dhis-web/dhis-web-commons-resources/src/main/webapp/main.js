
// -----------------------------------------------------------------------------
// Page init
// -----------------------------------------------------------------------------

$( document ).ready( function() { pageInit(); } );

function pageInit()
{
	// Zebra stripes in lists
	
	$( "table.listTable tbody tr:odd" ).addClass( "listAlternateRow" );
    $( "table.listTable tbody tr:even" ).addClass( "listRow" );

    // Hover rows in lists
    
    $( "table.listTable tbody tr" ).mouseover( function()
    {
    	$( this ).addClass( "listHoverRow" );
    } );
    $( "table.listTable tbody tr" ).mouseout( function()
    {
        $( this ).removeClass( "listHoverRow" );
    } );
    
    // Hover on rightbar close image
    
    $( "#hideRightBarImg" ).mouseover( function()
    {
    	$( this ).attr( "src", "../images/hide_active.png" );
    } );
    $( "#hideRightBarImg" ).mouseout( function()
    {
        $( this ).attr( "src", "../images/hide.png" );
    } );
    
    // Resize UI in case of 800 x 600 screen

    if( $( window ).width() <= 800 ) 
    {
        $( 'img#menuSeparator1').css('left','400px' );
        $( 'img#menuSeparator2').css('left','490px' );
        $( 'img#menuSeparator3').css('left','580px' );
        $( 'img#menuSeparator4').css('left','670px' );
        $( 'img#menuSeparator5').css('left','750px' );
        $( 'div#menuLink1').css('left','398px' );
        $( 'div#menuLink2').css('left','485px' );
        $( 'div#menuLink3').css('left','580px' );
        $( 'div#menuLink4').css('left','665px' );
        $( 'div#menuDropDown1').css('left','400px' );
        $( 'div#menuDropDown2').css('left','490px' );
        $( 'div#menuDropDown3').css('left','580px' );

        $( '#leftBar').css('width','175px' );
        $( '#leftBarContents h2').css('margin-right','10px' );
        $( '#leftBarContents h2').css('margin-left','10px' );
        $( '#leftBarContents ul').css('margin-left','30px' );
        $( '#leftBarContents img').css('margin-left','10px' );

        $( '#orgUnitTree').css('width','172px' );

        $( '#mainPage').css('margin-left','200px' );
        $( 'div#orgUnitTree ul').css('margin-left','10px' );
    }
    
    // Set dynamic back URLs for about page links
        
	var currentPath = '../dhis-web-commons-about/';
	var backURL = '?backUrl=' + window.location;

	$( "#menuDropDownHelpCenter" ).click(
		function()
		{
			window.location.href = currentPath + 'help.action' + backURL;
		});
		
	$( "#menuDropDownFeedBack" ).click(
		function()
		{
			window.location.href = currentPath + 'displayFeedbackForm.action' + backURL;
		});
		
	$( "#menuDropDownChangeLog" ).click(
		function()
		{
			window.location.href = currentPath + 'displayChangeLog.action' + backURL;
		});
		
	$( "#menuDropDownSupportiveSoftware" ).click(
		function()
		{
			window.location.href= currentPath + 'displaySupportiveSoftware.action' + backURL;
		});
	
	$( "#menuDropDownUserAccount" ).click(
		function()
		{
			window.location.href = currentPath + 'showUpdateUserAccountForm.action' + backURL;
		});
		
	$( "#menuDropDownAboutDHIS2" ).click(
		function()
		{
			window.location.href = currentPath + 'about.action' + backURL;
		});
	
	// Intro fade in
	
	$( "#introList" ).fadeIn();
}

// -----------------------------------------------------------------------------
// Menu functions
// -----------------------------------------------------------------------------

var menuTimeout = 500;
var closeTimer = null;
var dropDownId = null;

function showDropDown( id )
{
    cancelHideDropDownTimeout();
    
    var newDropDownId = "#" + id;
  
    if ( dropDownId != newDropDownId )
    {   
        hideDropDown();

        dropDownId = newDropDownId;
        
        $( dropDownId ).show();
    }
}

function hideDropDown()
{
	if ( dropDownId )
	{
	    $( dropDownId ).hide();
	    
	    dropDownId = null;
	}
}

function hideDropDownTimeout()
{
    closeTimer = window.setTimeout( hideDropDown, menuTimeout );
}

function cancelHideDropDownTimeout()
{
    if ( closeTimer )
    {
        window.clearTimeout( closeTimer );
        
        closeTimer = null;
    }
}

// -----------------------------------------------------------------------------
// Leftbar
// -----------------------------------------------------------------------------

var leftBar = new LeftBar();

function LeftBar()
{    
    this.showAnimated = function()
    {
        setMenuVisible();        
        setMainPageNormal( '270px' ); // Delegated to dom.js for each style
        $( 'div#leftBar' ).show( 'fast' );
        $( 'span#showLeftBar' ).hide( 'fast' );
    };
    
    this.hideAnimated = function()
    {
        setMenuHidden();
        setMainPageFullscreen( '20px' );
        $( 'div#leftBar' ).hide( 'fast' );
        $( 'span#showLeftBar' ).show( 'fast' );
    };
    
    this.hide = function()
    {
        setMenuHidden();
        setMainPageFullscreen( '20px' );
        document.getElementById( 'leftBar' ).style.display = 'none';
        document.getElementById( 'showLeftBar' ).style.display = 'block';
    } 

	function setMainPageFullscreen()
	{
		document.getElementById( 'mainPage' ).style.marginLeft = '20px';
	}

    function setMenuVisible()
    {
        var request = new Request();
        request.send( '../dhis-web-commons/menu/setMenuVisible.action' );        
    }
    
    function setMenuHidden()
    {        
        var request = new Request();
        request.send( '../dhis-web-commons/menu/setMenuHidden.action' );        
    }    
    
    this.openHelpForm = function( id )
    {
		window.open ("../dhis-web-commons/help/viewDynamicHelp.action?id=" + id,"Help", 'width=800,height=600,scrollbars=yes');
    }
}
