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
        var $linkTag = $( "#" + getTagId( unitId ) ).find( "a" ).eq( 0 );

        if ( $linkTag.hasClass( "selected" ) && unselectAllowed )
        {
            $.post( organisationUnitTreePath + "removeorgunit.action", {
                id : unitId
            }, function( data )
            {
                responseReceived( data.firstChild );
            }, 'xml' );

            $linkTag.removeClass( "selected" );
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

                $linkTag.addClass( "selected" );
            } else
            {
                $.ajax({
					type: "POST",
					url: organisationUnitTreePath + "setorgunit.action?id=" + unitId,
					dataType: "xml",
					success: responseReceived
				});
				
                $( "#orgUnitTree" ).find( "a" ).removeClass( "selected" );
                $linkTag.addClass( "selected" );
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

        $( rootElement ).find( "unitId" ).each( function( i, item )
        {
            unitIds[i] = $( item ).text()
        } );

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

        if ( children.length < 1 || !isVisible( children[0] ) )
        {
            $.ajax( {
                url : organisationUnitTreePath + 'expandSubtree.action',
                data : {
                    'parentId' : unitId
                },
                success : processExpand
            } );
        } else
        {
            $.ajax( {
                url : organisationUnitTreePath + 'collapseSubtree.action',
                data : {
                    'parentId' : unitId
                },
                success : processCollapse
            } );
        }
    };

    this.refreshTree = function()
    {
        var $treeTag = $( "#orgUnitTree" );
        $treeTag.children().eq( 0 ).remove();

        $.get( organisationUnitTreePath + "getExpandedTree.action", treeReceived );
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
                setVisible( $children.eq( 0 ), true );
                setToggle( $parentTag, true );
            }
        } );
    }

    function treeReceived( rootElement )
    {
        var $treeTag = $( "#orgUnitTree" );
        var $rootsTag = $( "<ul/>" );

        $( rootElement ).find( "roots > unit" ).each( function( i, item )
        {
            $rootsTag.append( createTreeElementTag( item ) );
        } );

        $treeTag.append( $rootsTag );

        $( rootElement ).find( "children > parent" ).each( function( i, item )
        {
            var parentId = $( item ).attr( "parentId" );
            var $parentTag = $( "#" + getTagId( parentId ) );

            createChildren( $parentTag, item );
        } );
    }

    function createChildren( parentTag, parentElement )
    {
        var $childrenTag = $( "<ul/>" );

        $( parentElement ).find( "child" ).each( function( i, item )
        {
            $childrenTag.append( createTreeElementTag( item ) );
        } )

        setVisible( $childrenTag, true );
        setToggle( parentTag, true );

        $( parentTag ).append( $childrenTag );
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

        return $childTag;
    }

    function setToggle( unitTag, expanded )
    {
        var $toggleTag = $( unitTag ).find( "span" );
        var toggleImg = expanded ? getToggleCollapse() : getToggleExpand();

        if ( $toggleTag.children().eq( 0 ) )
        {
            $toggleTag.children().eq( 0 ).replaceWith( toggleImg );
        } else
        {
            $toggleTag.append( toggleImg );
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
