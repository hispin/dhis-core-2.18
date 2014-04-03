
// -----------------------------------------------------------------------------
// Page init
// -----------------------------------------------------------------------------

$( document ).ready( function() { pageInit(); } );

function pageInit()
{
	setTableStyles();
	
    // Hover on rightbar close image
    
    $( "#hideRightBarImg" ).mouseover( function()
    {
    	$( this ).attr( "src", "../images/hide_active.png" );
    } );
    $( "#hideRightBarImg" ).mouseout( function()
    {
        $( this ).attr( "src", "../images/hide.png" );
    } );
	
	// Set show and hide drop down events on top menu
	
	$( "#menuLink1" ).hover( function() 
	{
		showDropDown( "menuDropDown1" );
	}, 
	function() 
	{
		hideDropDownTimeout();
	} );

	$( "#menuLink2" ).hover( function() 
	{
		showDropDown( "menuDropDown2" );
	}, 
	function() 
	{
		hideDropDownTimeout();
	} );

	$( "#menuLink3" ).hover( function() 
	{
		showDropDown( "menuDropDown3" );
	}, 
	function() 
	{
		hideDropDownTimeout();
	} );

	$( "#menuDropDown1, #menuDropDown2, #menuDropDown3" ).hover( function() 
	{
		cancelHideDropDownTimeout();
	}, 
	function() 
	{
		hideDropDownTimeout();
	} );


    $('.drop-down-menu-link').get().forEach(function (element, index, elements) {
        var id = $(element).parent().attr('id'),
            dropdown_menu = $('div#' + id.split('_')[0]);

        function closeAllDropdowns() {
            $('.app-menu-dropdown').each(function () {
                $(this).attr('data-clicked-open', 'false');
                $(this).hide();
            });
            hideDropDown();
        }

        $(element).click(function () {
            return function () {
                var thisDropDownStatus = $(dropdown_menu).attr('data-clicked-open');
                closeAllDropdowns();

                if (thisDropDownStatus === 'true') {
                    $(dropdown_menu).attr('data-clicked-open', 'false');
                } else {
                    $(dropdown_menu).attr('data-clicked-open', 'true');
                    showDropDown(dropdown_menu.attr('id'));
                }
            }
        }());
    });

    $(window).resize(function () {
        $('.app-menu-dropdown').get().forEach(function (element, index, elements) {
            var newDropDownId = '#' + $(element).attr('id'),
                position = $(newDropDownId + '_button').position();

            $(newDropDownId).css('position', 'absolute');
            $(newDropDownId).css('top', '55px');
            $(newDropDownId).css('left', Math.ceil(position.left - Math.ceil(parseInt($(newDropDownId).innerWidth(), 10) - 108)) + 'px');
        });
    });
}

function setTableStyles()
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
}

// -----------------------------------------------------------------------------
// Menu functions
// -----------------------------------------------------------------------------

var menuTimeout = 500;
var closeTimer = null;
var dropDownId = null;

function showDropDown( id )
{
    var newDropDownId = "#" + id,
        position = $(newDropDownId + '_button').position();

    cancelHideDropDownTimeout();

    $(newDropDownId).css('position', 'absolute');
    $(newDropDownId).css('top', '55px');
    $(newDropDownId).css('left', Math.ceil(position.left - Math.ceil(parseInt($(newDropDownId).innerWidth(), 10) - 108)) + 'px');


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
        if ($( dropDownId ).attr( 'data-clicked-open' ) === 'true') {
            return;
        }
	    $( dropDownId ).hide();
	    
	    dropDownId = null;
	}
}

function hideDropDownTimeout()
{
    closeTimer = window.setTimeout( "hideDropDown()", menuTimeout );
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

dhis2.util.namespace( 'dhis2.leftBar' );

dhis2.leftBar.setLinks = function( showLeftBarLink, showExtendMenuLink )
{
	$( '#showLeftBar' ).css( 'display', ( showLeftBarLink ? 'inline' : 'none' ) );
	$( '#extendMainMenuLink' ).css( 'display', ( showExtendMenuLink ? 'inline' : 'none' ) );
};

dhis2.leftBar.showAnimated = function()
{
	dhis2.leftBar.setMenuVisible();
	dhis2.leftBar.setLinks( false, true );
	$( '#leftBar, #orgUnitTree' ).css( 'width', '' ).show( 'slide', { direction: 'left', duration: 200 } );
	$( '#mainPage' ).css( 'margin-left', '' );
};

dhis2.leftBar.extendAnimated = function()
{
	dhis2.leftBar.setMenuExtended();
	dhis2.leftBar.setLinks( false, false );
    $( '#leftBar, #orgUnitTree' ).show().animate( { direction: 'left', width: '+=150px', duration: 20 } );
    $( '#mainPage' ).animate( { direction: 'left', marginLeft: '+=150px', duration: 20 } );
    $( '#hideMainMenuLink' ).attr( 'href', 'javascript:dhis2.leftBar.retract()' );
};

dhis2.leftBar.extend = function()
{
	dhis2.leftBar.setMenuExtended();
	dhis2.leftBar.setLinks( false, false );
    $( '#leftBar, #orgUnitTree' ).show().css( "width", "+=150px" );
    $( '#mainPage' ).css( "margin-left", "+=150px" );
    $( '#hideMainMenuLink' ).attr( 'href', 'javascript:dhis2.leftBar.retract()' );
};

dhis2.leftBar.retract = function()
{
	dhis2.leftBar.setMenuVisible();
	dhis2.leftBar.setLinks( false, true );
    $( '#leftBar, #orgUnitTree' ).show().animate( { direction: 'right', width: '-=150px', duration: 20 } );
    $( '#mainPage' ).animate( { direction: 'right', marginLeft: '-=150px', duration: 20 } );
    $( '#hideMainMenuLink' ).attr( 'href', 'javascript:javascript:dhis2.leftBar.hideAnimated()' );
}

dhis2.leftBar.hideAnimated = function()
{
	dhis2.leftBar.setMenuHidden();
	dhis2.leftBar.setLinks( true, false );
    $( '#leftBar' ).hide( 'slide', { direction: 'left', duration: 200 } );
    $( '#mainPage' ).animate( { direction: 'right', marginLeft: '20px', duration: 200 } );
};

dhis2.leftBar.hide = function()
{
	dhis2.leftBar.setMenuHidden();
	dhis2.leftBar.setLinks( true, false );
    $( '#leftBar' ).hide();
    $( '#mainPage' ).css( 'margin-left', '20px' );
};

dhis2.leftBar.setMenuVisible = function()
{
    $.get( '../dhis-web-commons/menu/setMenuState.action?state=VISIBLE' );        
};
    
dhis2.leftBar.setMenuExtended = function()
{
	$.get( '../dhis-web-commons/menu/setMenuState.action?state=EXTENDED' );
};
    
dhis2.leftBar.setMenuHidden = function()
{        
    $.get( '../dhis-web-commons/menu/setMenuState.action?state=HIDDEN' );
};
    
dhis2.leftBar.openHelpForm = function( id )
{
	window.open( "../dhis-web-commons/help/viewDynamicHelp.action?id=" + id, "Help", "width=800,height=600,scrollbars=yes" );
};
