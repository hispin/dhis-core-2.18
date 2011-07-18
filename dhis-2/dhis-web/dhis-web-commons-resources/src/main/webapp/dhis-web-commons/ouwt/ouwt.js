// -----------------------------------------------------------------------------
// Author:   Torgeir Lorange Ostby
// -----------------------------------------------------------------------------

/*
 * Usage:
 *
 * Use the selection.setListenerFunction function to register a callback
 * function to be called when an organisation unit is selected. The callback
 * function must have one argument, an array with the ids of the selected
 * organisation units.
 *
 * Multiple selection is by default turned off. Use the
 * selection.setMultipleSelectionAllowed function to change this.
 */

var selection = new Selection();
var subtree = new Subtree();
var organisationUnitTreePath = '../dhis-web-commons/ouwt/';

// -----------------------------------------------------------------------------
// Selection
// -----------------------------------------------------------------------------

function Selection()
{
    var listenerFunction;
    var multipleSelectionAllowed = false;
    var unselectAllowed = false;

    this.setListenerFunction = function( listenerFunction_ )
    {
        listenerFunction = listenerFunction_;
    };

    this.setMultipleSelectionAllowed = function( allowed )
    {
        multipleSelectionAllowed = allowed;
    };

    this.setUnselectAllowed = function( allowed )
    {
        unselectAllowed = allowed;
    }

    this.select = function( unitId )
    {
        var linkTag = $( "#" + getTagId( unitId ) ).find( "a" ).eq( 0 );

        if ( linkTag.hasClass( "selected" ) && unselectAllowed )
        {
            $.post( organisationUnitTreePath + "removeorgunit.action", {
                id : unitId
            }, function( data )
            {
                responseReceived( data.firstChild );
            }, 'xml' );

        } else
        {
            if ( multipleSelectionAllowed )
            {
                $.post( organisationUnitTreePath + "addorgunit.action", {
                    id : unitId
                }, function( data )
                {
                    responseReceived( data.firstChild );
                }, 'xml' );

                linkTags[0].className = 'selected';
            } else
            {
                $.post( organisationUnitTreePath + "setorgunit.action", {
                    id : unitId
                }, function( data )
                {
                    responseReceived( data.firstChild );
                }, 'xml' );

                $( "#orgUnitTree" ).find( "a" ).removeClass( "selected" );
                $( "#" + getTagId( unitId ) ).find( "a" ).eq( 0 ).addClass( "selected" );

            }
        }
    };

    function responseReceived( rootElement )
    {
        if ( !listenerFunction )
        {
            return;
        }

        var unitIds = new Array();

        var unitIdElements = rootElement.getElementsByTagName( 'unitId' );
        for ( var i = 0, unitIdElement; ( unitIdElement = unitIdElements[i] ); ++i )
        {
            unitIds[i] = unitIdElement.firstChild.nodeValue;
        }

        listenerFunction( unitIds );
    }

    function getTagId( unitId )
    {
        return 'orgUnit' + unitId;
    }

    this.findByCode = function()
    {
        $.getJSON( organisationUnitTreePath + 'getOrganisationUnitByCode.action?code='
                + encodeURI( $( '#searchField' ).val() ), function( data )
        {
            var unitId = data.message;
            if ( data.response == "success" )
            {
                $( '#orgUnitTreeContainer' ).load( organisationUnitTreePath + 'loadOrganisationUnitTree.action',
                        function()
                        {

                            if ( !listenerFunction )
                            {
                                return false;
                            }
                            var unitIds = [ unitId ];
                            listenerFunction( unitIds );
                        } );
            } else
            {
                $( '#searchField' ).css( 'background-color', '#ffc5c5' );
            }
        } );
    }
}

// -----------------------------------------------------------------------------
// Subtree
// -----------------------------------------------------------------------------

function Subtree()
{
    this.toggle = function( unitId )
    {
        var children = $( "#" + getTagId( unitId ) ).find( "ul" );

        var request = new Request();
        request.setResponseTypeXML( 'units' );

        if ( children.length < 1 || !isVisible( children[0] ) )
        {
            request.setCallbackSuccess( processExpand );
            request.send( organisationUnitTreePath + 'expandSubtree.action?parentId=' + unitId );
        } else
        {
            request.setCallbackSuccess( processCollapse );
            request.send( organisationUnitTreePath + 'collapseSubtree.action?parentId=' + unitId );
        }
    };

    this.refreshTree = function()
    {
        var treeTag = document.getElementById( 'orgUnitTree' );

        var children = treeTag.getElementsByTagName( 'ul' );
        treeTag.removeChild( children[0] );

        var request = new Request();
        request.setResponseTypeXML( 'units' );
        request.setCallbackSuccess( treeReceived );
        request.send( organisationUnitTreePath + 'getExpandedTree.action' );
    };

    function processCollapse( rootElement )
    {
        $( rootElement ).find( "unit" ).each( function( i, item )
        {
            var parentId = $( item ).eq( 0 ).text();
            var $parentTag = $( "#" + getTagId( parentId ) );
            var child = $parentTag.find( "ul" ).eq( 0 );

            setVisible( child, false );
            setToggle( $parentTag, false );
        } );
    }

    function processExpand( rootElement )
    {
        $( rootElement ).find( "parent" ).each( function( i, item )
        {
            var parentId = $( item ).attr( "parentId" );
            var $parentTag = $( "#" + getTagId( parentId ) );
            var $children = $parentTag.find( "ul" );

            if ( $children.length < 1 )
            {
                createChildren( $parentTag.get( 0 ), item );
            } else
            {
                setVisible( $children.eq( 0 ), false );
                setToggle( $parentTag, false );
            }
        } );
    }

    function treeReceived( rootElement )
    {
        var rootsElement = rootElement.getElementsByTagName( 'roots' )[0];
        var unitElements = rootsElement.getElementsByTagName( 'unit' );

        var treeTag = document.getElementById( 'orgUnitTree' );
        var rootsTag = document.createElement( 'ul' );

        for ( var i = 0; i < unitElements.length; ++i )
        {
            var unitTag = createTreeElementTag( unitElements[i] );

            rootsTag.appendChild( unitTag );
        }

        treeTag.appendChild( rootsTag );

        var childrenElement = rootElement.getElementsByTagName( 'children' )[0];
        var parentElements = childrenElement.getElementsByTagName( 'parent' );

        for ( var i = 0, parentElement; ( parentElement = parentElements[i] ); ++i )
        {
            var parentId = parentElement.getAttribute( 'parentId' );
            var parentTag = document.getElementById( getTagId( parentId ) );

            createChildren( parentTag, parentElement );
        }
    }

    function createChildren( parentTag, parentElement )
    {
        var children = parentElement.getElementsByTagName( 'child' );
        var childrenTag = document.createElement( 'ul' );

        for ( var i = 0, child; ( child = children[i] ); ++i )
        {
            var childTag = createTreeElementTag( child );

            childrenTag.appendChild( childTag );
        }

        setVisible( childrenTag, true );
        setToggle( parentTag, true );

        $( parentTag ).get( 0 ).appendChild( childrenTag );
    }

    function createTreeElementTag( child )
    {
        var $child = $( child );
        var childId = $child.attr( "id" );
        var hasChildren = $child.attr( "hasChildren" ) != 0;

        var $toggleTag = $( "<span/>" );
        $toggleTag.addClass( "toggle" );

        if ( hasChildren )
        {
            $toggleTag.bind( "click", new Function( 'subtree.toggle( ' + childId + ' )' ) );
            $toggleTag.append( getToggleExpand() );
        } else
        {
            $toggleTag.append( getToggleBlank() );
        }

        var $linkTag = $( "<a/>" );
        $linkTag.attr( "href", "javascript:void selection.select( " + childId + ")" );
        $linkTag.append( $child.eq( 0 ).text() );

        if ( $child.attr( "select" ) )
        {
            $linkTag.addClass( "selected" );
        }

        var $childTag = $( "<li/>" );
        $childTag.attr( "id", getTagId( childId ) );
        $childTag.append( " " );
        $childTag.append( $toggleTag )
        $childTag.append( " " );
        $childTag.append( $linkTag )

        return $childTag.get( 0 );
    }

    function setToggle( unitTag, expanded )
    {
        var toggleTag = $( unitTag ).find( "span" ).get( 0 );
        var toggleImg = expanded ? getToggleCollapse() : getToggleExpand();

        if ( toggleTag.firstChild )
        {
            toggleTag.replaceChild( toggleImg, toggleTag.firstChild );
        } else
        {
            toggleTag.appendChild( toggleImg );
        }
    }

    function setVisible( tag, visible )
    {
        if ( visible )
        {
            $( tag ).show();
        } else
        {
            $( tag ).hide();
        }
    }

    function isVisible( tag )
    {
        return $( tag ).is( ":visible" );
    }

    function getTagId( unitId )
    {
        return 'orgUnit' + unitId;
    }

    function getToggleExpand()
    {
        var imgTag = getToggleImage();
        imgTag.src = '../images/colapse.png';
        imgTag.alt = '[+]';
        return imgTag;
    }

    function getToggleCollapse()
    {
        var imgTag = getToggleImage();
        imgTag.src = '../images/expand.png';
        imgTag.width = '9';
        imgTag.height = '9';
        imgTag.alt = '[-]';
        return imgTag;
    }

    function getToggleBlank()
    {
        var imgTag = getToggleImage();
        imgTag.src = '../images/transparent.gif';
        imgTag.width = '9';
        imgTag.height = '9';
        imgTag.alt = '';
        return imgTag;
    }

    function getToggleImage()
    {
        var imgTag = document.createElement( 'img' );
        imgTag.width = '9';
        imgTag.height = '9';
        return imgTag;
    }
}
