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

var organisationUnitTreePath = '../dhis-web-commons/ouwt/';
var organisationUnits = {};

var selection = new Selection();
var subtree = new Subtree();

var dhis2 = dhis2 || {};
dhis2.ou = dhis2.ou || {};

dhis2.ou.store = new dhis2.storage.Store( {
    name: 'dhis2',
    objectStores: [ {
        name: 'ou',
        adapters: [ /* dhis2.storage.IndexedDBAdapter, */ dhis2.storage.DomLocalStorageAdapter, dhis2.storage.InMemoryAdapter ]
    }, {
        name: 'ouPartial',
        adapters: [ /* dhis2.storage.IndexedDBAdapter, */ dhis2.storage.DomSessionStorageAdapter, dhis2.storage.InMemoryAdapter ]
    }, {
        name: 'ouConfig',
        adapters: [ /* dhis2.storage.IndexedDBAdapter, */ dhis2.storage.DomSessionStorageAdapter, dhis2.storage.InMemoryAdapter ]
    } ]
} );

$( document ).ready( function ()
{
    selection.load();
} );

// -----------------------------------------------------------------------------
// Selection
// -----------------------------------------------------------------------------

function Selection()
{
    var listenerFunction = undefined;
    var multipleSelectionAllowed = false;
    var unselectAllowed = false;
    var rootUnselectAllowed = false;
    var autoSelectRoot = true;
    var realRoot = true;
    var includeChildren = false;

    this.setListenerFunction = function( listenerFunction_, skipInitialCall ) {
        listenerFunction = listenerFunction_;

        if( !skipInitialCall ) {
            $( "#orgUnitTree" ).one( "ouwtLoaded", function() {
                selection.responseReceived();
            } );
        }
    };

    this.setMultipleSelectionAllowed = function( allowed ) {
        multipleSelectionAllowed = allowed;
    };

    this.setUnselectAllowed = function( allowed ) {
        unselectAllowed = allowed;
    };

    this.setRootUnselectAllowed = function( allowed ) {
        rootUnselectAllowed = allowed;
    };

    this.setAutoSelectRoot = function( autoSelect ) {
        autoSelectRoot = autoSelect;
    };

    this.setIncludeChildren = function( children ) {
        includeChildren = children;
    };

    this.getSelected = function() {
        var selected = sessionStorage[getTagId( "Selected" )];
        selected = selected ? JSON.parse( selected ) : [];
        selected = $.isArray( selected ) ? selected : [ selected ];

        return  selected;
    };

    this.clearSelected = function() {
        sessionStorage.removeItem( getTagId( "Selected" ) );
    };

    this.setSelected = function( selected ) {
        sessionStorage[getTagId( "Selected" )] = JSON.stringify( selected );
    };

    this.selectedExists = function() {
        return sessionStorage[getTagId( "Selected" )] != null;
    };

    this.getRoots = function() {
        var roots = localStorage[getTagId( "Roots" )];
        return roots ? JSON.parse( roots ) : [];
    };

    this.rootsExists = function() {
        return localStorage[getTagId( "Roots" )] != null;
    };

    this.setRoots = function(roots) {
        localStorage[getTagId( "Roots" )] = JSON.stringify( roots );
    };

    this.getVersion = function() {
        return localStorage[getTagId( "Version" )] ? localStorage[getTagId( "Version" )] : 0;
    };

    this.setVersion = function( version ) {
        localStorage[getTagId( "Version" )] = version;
    };

    this.clearVersion = function() {
        localStorage.removeItem( getTagId( "Version" ) );
    };

    this.versionExists = function() {
        return localStorage[getTagId( "Version" )] != null;
    };

    this.getOrganisationUnits = function() {
        var organisationUnits = localStorage["organisationUnits"];
        return organisationUnits ? JSON.parse( organisationUnits ) : {};
    };

    this.setOrganisationUnits = function( organisationUnits ) {
        if( organisationUnits ) {
            localStorage["organisationUnits"] = JSON.stringify( organisationUnits );
        } else {
            localStorage["organisationUnits"] = JSON.stringify( {} );
        }
    };

    this.getPartialOrganisationUnits = function() {
        return sessionStorage['organisationUnits'] ? JSON.parse( sessionStorage['organisationUnits'] ) : {};
    };

    this.setPartialOrganisationUnits = function( organisationUnits ) {
        if( organisationUnits ) {
            sessionStorage["organisationUnits"] = JSON.stringify( organisationUnits );
        } else {
            sessionStorage["organisationUnits"] = JSON.stringify( {} );
        }
    };

    this.organisationUnitsExists = function() {
        return localStorage["organisationUnits"] != null;
    };

    this.ajaxOrganisationUnits = function( versionOnly, format ) {
        format = format || "json";

        return $.ajax( {
            url: '../dhis-web-commons-ajax-json/getOrganisationUnitTree.action',
            data: {
                versionOnly: versionOnly
            },
            type: 'POST',
            dataType: format
        } );
    };

    this.load = function ()
    {
        function sync_and_reload()
        {
            var roots = selection.getRoots();

            if( !selection.selectedExists() && roots.length > 0 ) {
                if( autoSelectRoot ) {
                    if( multipleSelectionAllowed ) {
                        selection.setSelected( roots );
                    }
                    else {
                        selection.setSelected( roots[0] );
                    }
                }
                else {
                    selection.sync( true );
                }
            }

            organisationUnits = selection.getOrganisationUnits();
            $.extend( organisationUnits, selection.getPartialOrganisationUnits() );

            selection.sync();
            subtree.reloadTree();

            $( "#ouwt_loader" ).hide();
        }

        function update_required( remoteVersion, remoteRoots ) {
            var localVersion = selection.getVersion();
            var localRoots = selection.getRoots();

            if ( localVersion != remoteVersion ) {
                return true;
            }

            if ( localRoots == null || localRoots.length == 0 ) {
                return true;
            }

            localRoots.sort();
            remoteRoots.sort();

            for( var i in localRoots ) {
                if( localRoots.hasOwnProperty( i ) ) {
                    if( remoteRoots[i] == null || localRoots[i] != remoteRoots[i] ) {
                        return true;
                    }
                }
            }

            return false;
        }

        var should_update = false;

        selection.ajaxOrganisationUnits( true, 'text' ).done(function( data ) {
            if ( data.indexOf( "<!DOCTYPE" ) != 0 ) {
                data = JSON.parse( data );
                realRoot = data.realRoot;
                should_update = update_required( data.version, data.roots );
            }
        } ).always( function() {
            if( should_update ) {
                selection.ajaxOrganisationUnits( false ).done(function( data ) {
                    selection.setRoots( data.roots );
                    selection.setVersion( data.version );
                    selection.setOrganisationUnits( data.organisationUnits );
                } ).always( function() {
                        sync_and_reload();
                        $( "#orgUnitTree" ).trigger( "ouwtLoaded" );
                    } );
            }
            else {
                sync_and_reload();
                $( "#orgUnitTree" ).trigger( "ouwtLoaded" );
            }
        } );
    };

    // server = true : sync from server
    // server = false : sync to server
    this.sync = function ( server, fn )
    {
        if ( fn === undefined ) {
            fn = function () {};
        }

        if( server ) {
            selection.clearSelected();

            $.post( organisationUnitTreePath + "getselected.action", function( data ) {
                if( data["selectedUnits"].length < 1 ) {
                    return;
                }

                if( multipleSelectionAllowed ) {
                    var selected = [];

                    $.each( data["selectedUnits"], function( i, item ) {
                        selected.push( item.id );
                    } );

                    selection.setSelected( selected );
                }
                else {
                    var ou = data["selectedUnits"][0];
                    selection.setSelected( ou.id );
                }

                subtree.reloadTree();
            } );
        } else {
            $.post( organisationUnitTreePath + "clearselected.action", function() {
                var selected = selection.getSelected();

                if( multipleSelectionAllowed ) {
                    $.each( selected, function( i, item ) {
                        $.post( organisationUnitTreePath + "addorgunit.action", {
                            id: item
                        } );
                    } ).complete( fn );
                } else {
                    selected = $.isArray( selected ) ? selected[0] : selected;

                    $.post( organisationUnitTreePath + "setorgunit.action", {
                        id: selected
                    } ).complete( fn );
                }
            } );
        }
    };

    this.clear = function ()
    {
        selection.clearSelected();

        var roots = selection.getRoots();
        selection.getRoots().length > 1 ? selection.setSelected( roots ) : selection.setSelected( roots[0] );
        subtree.reloadTree();

        $.post( organisationUnitTreePath + "clearselected.action" ).complete( this.responseReceived );
    };

    this.select = function( unitId ) {
        var $linkTag = $( "#" + getTagId( unitId ) ).find( "a" ).eq( 0 );

        if( $linkTag.hasClass( "selected" ) && ( unselectAllowed || rootUnselectAllowed ) ) {
            var selected = selection.getSelected();

            if( rootUnselectAllowed && !unselectAllowed && !multipleSelectionAllowed ) {
                var roots = selection.getRoots();

                if( $.inArray( selected, roots ) == -1 ) {
                    return;
                }

                if( !realRoot ) {
                    return;
                }
            }

            if( !!selected && $.isArray( selected ) ) {
                var idx = undefined;

                $.each( selected, function( i, item ) {
                    if( +item === unitId ) {
                        idx = i;
                    }
                } );

                if( idx !== undefined ) {
                    dhis2.array.remove( selected, idx, idx );
                }

                selection.setSelected( selected );
            } else {
                selection.clearSelected();
            }

            $.post( organisationUnitTreePath + "removeorgunit.action", {
                id: unitId
            } ).complete( this.responseReceived );

            $linkTag.removeClass( "selected" );
        } else {
            if( multipleSelectionAllowed ) {
                var selected = selection.getSelected();

                if( selected.indexOf( unitId ) !== -1 ) {
                    return;
                }

                selected.push( unitId );
                selection.setSelected( selected );

                $.post( organisationUnitTreePath + "addorgunit.action", {
                    id: unitId
                } ).complete( this.responseReceived );

                $linkTag.addClass( "selected" );
            }
            else
            {
                selection.setSelected( unitId );

                $.ajax( {
                    url:organisationUnitTreePath + "setorgunit.action",
                    data:{
                        id:unitId
                    },
                    type:'POST',
                    timeout:10000,
                    complete:this.responseReceived
                } );

                $( "#orgUnitTree" ).find( "a" ).removeClass( "selected" );
                $linkTag.addClass( "selected" );
            }
        }
    };

    this.responseReceived = function() {
        if( !listenerFunction ) {
            return;
        }

        var children = [];
        var ids = [];
        var names = [];
        var selected = selection.getSelected();

        if( multipleSelectionAllowed ) {
            $.each( selected, function( i, item ) {
                var name = organisationUnits[item].n;
                ids.push( item );
                names.push( name );
            } );
        } else {
            // we only support includeChildren for single selects
            if( includeChildren ) {
                children = organisationUnits[selected].c;
            }

            $.extend( organisationUnits, selection.getPartialOrganisationUnits() );

            var name = organisationUnits[selected].n;
            ids.push( +selected );
            names.push( name );
        }

        if( !multipleSelectionAllowed ) {
            subtree.getChildren( selected ).done( function() {
                listenerFunction( ids, names, children );
            } );
        } else {
            listenerFunction( ids, names, children );
        }
    };

    function getTagId( unitId )
    {
        return 'orgUnit' + unitId;
    }

    this.findByName = function() {
        var name = $( '#searchField' ).val();
        var match;

        for( var ou in organisationUnits ) {
            if( organisationUnits.hasOwnProperty( ou ) ) {
                var value = organisationUnits[ou];
                if( value.n == name ) {
                    match = value;
                }
            }
        }

        if( match !== undefined ) {
            $( '#searchField' ).css( 'background-color', '#ffffff' );

            multipleSelectionAllowed ? selection.setSelected( [ match.id ] ) : selection.setSelected( match.id );

            subtree.reloadTree();
            selection.sync( false, selection.responseReceived );
        }
        else {
            $.ajax( {
                url: '../dhis-web-commons-ajax-json/getOrganisationUnitTree.action',
                data: { byName: name }
            } ).done(function( data ) {
                if( data.realRoot === undefined ) {
                    var partialOrganisationUnits = selection.getPartialOrganisationUnits();
                    $.extend( partialOrganisationUnits, data.organisationUnits );
                    selection.setPartialOrganisationUnits( partialOrganisationUnits );

                    $.extend( organisationUnits, data.organisationUnits );
                    selection.findByName();
                }

                $( '#searchField' ).css( 'background-color', '#ffc5c5' );
            } ).fail( function() {
                $( '#searchField' ).css( 'background-color', '#ffc5c5' );
            } );
        }
    };

    this.enable = function() {
        $( "#orgUnitTree" ).show();
    };

    this.unblock = function() {
        $( "#orgUnitTree" ).unblock();
    };

    this.disable = function() {
        $( "#orgUnitTree" ).hide();
    };

    this.block = function( message ) {
        if( message === undefined ) {
            $( "#orgUnitTree" ).block( {message: null} );
        }
        else {
            $( "#orgUnitTree" ).block( {
                message: message,
                css: {
                    border: '1px solid black',
                    margin: '4px 10px'
                }
            } );
        }
    };
}

// -----------------------------------------------------------------------------
// Subtree
// -----------------------------------------------------------------------------

function Subtree() {
    this.toggle = function( unitId ) {
        var children = $( "#" + getTagId( unitId ) ).find( "ul" );
        var ou = organisationUnits[unitId];

        if( children.length < 1 || !isVisible( children[0] ) ) {
            processExpand( ou );
        }
        else {
            processCollapse( ou );
        }
    };

    var selectOrgUnits = function( ous ) {
        $.each( ous, function( i, item ) {
            selectOrgUnit( item );
        } );
    };

    var selectOrgUnit = function( ou ) {
        $( "#" + getTagId( ou ) + " > a" ).addClass( "selected" );
    };

    var expandTreeAtOrgUnits = function( ous ) {
        $.each( ous, function( i, item ) {
            expandTreeAtOrgUnit( item );
        } );
    };

    var expandTreeAtOrgUnit = function( ou ) {
        if( organisationUnits[ou] == null ) {
            return;
        }

        var ouEl = organisationUnits[ou];

        var $rootsTag = $( "#orgUnitTree > ul" );

        if( $rootsTag.length < 1 ) {
            $( "#orgUnitTree" ).append( "<ul/>" );
            $rootsTag = $( "#orgUnitTree > ul" );
        }

        var array = [];

        if( ouEl.pid !== undefined ) {
            while( ouEl.pid !== undefined ) {
                if( organisationUnits[ouEl.pid] != null ) {
                    array.push( ouEl.pid );
                }
                else {
                    break;
                }

                ouEl = organisationUnits[ouEl.pid];
            }

            array.reverse();
        }

        var rootId = array.length < 1 ? ou : array[0];

        if( $( "#" + getTagId( rootId ) ).length < 1 ) {
            var expand = organisationUnits[rootId];
            // var $parentTag = $( "#" + getTagId( rootId ) );
            $rootsTag.append( createTreeElementTag( expand ) );
        }

        $.each( array, function( i, item ) {
            var expand = organisationUnits[item];
            processExpand( expand );
        } );
    };

    this.reloadTree = function() {
        var $treeTag = $( "#orgUnitTree" );
        $treeTag.children().eq( 0 ).remove();

        var roots = selection.getRoots();
        var selected = selection.getSelected();

        expandTreeAtOrgUnits( roots );
        expandTreeAtOrgUnits( selected );

        selectOrgUnits( selected );
    };

    // force reload
    this.refreshTree = function() {
        selection.clearVersion();
        selection.load();
    };

    function processCollapse( parent ) {
        var $parentTag = $( "#" + getTagId( parent.id ) );
        var child = $parentTag.find( "ul" ).eq( 0 );
        setVisible( child, false );
        setToggle( $parentTag, false );
    }

    function processExpand( parent ) {
        var $parentTag = $( "#" + getTagId( parent.id ) );
        var $children = $parentTag.find( "ul" );

        if( $children.length < 1 ) {
            getAndCreateChildren( $parentTag, parent );
        }
        else {
            setVisible( $children.eq( 0 ), true );
            setToggle( $parentTag, true );
        }
    }

    this.getChildren = function( parentId ) {
        return $.post( '../dhis-web-commons-ajax-json/getOrganisationUnitTree.action?parentId=' + parentId, function( data ) {
                var partialOrganisationUnits = selection.getPartialOrganisationUnits();
                $.extend( partialOrganisationUnits, data.organisationUnits );
                selection.setPartialOrganisationUnits( partialOrganisationUnits );

                $.extend( organisationUnits, data.organisationUnits );
            }
        );
    };

    function getAndCreateChildren( parentTag, parent ) {
        if( parent.c !== undefined ) {
            if( organisationUnits[parent.c[0]] !== undefined ) {
                createChildren( parentTag, parent );
            }
            else {
                subtree.getChildren( parent.id ).done( function() {
                    createChildren( parentTag, parent );
                } );
            }
        }

    }

    function createChildren( parentTag, parent )
    {
        var $childrenTag = $( "<ul/>" );

        $.each( parent.c, function ( i, item )
        {
            var ou = organisationUnits[item];

            if ( ou !== undefined )
            {
                $childrenTag.append( createTreeElementTag( ou ) );
            }
        } );

        setVisible( $childrenTag, true );
        setToggle( parentTag, true );

        $( parentTag ).append( $childrenTag );
    }

    function createTreeElementTag( ou ) {
        var $toggleTag = $( "<span/>" );
        $toggleTag.addClass( "toggle" );

        if( ou.c.length > 0 ) {
            $toggleTag.bind( "click", new Function( 'subtree.toggle( ' + ou.id + ' )' ) );
            $toggleTag.append( getToggleExpand() );
        }
        else {
            $toggleTag.append( getToggleBlank() );
        }

        var $linkTag = $( "<a/>" );
        $linkTag.attr( "href", "javascript:void selection.select( " + ou.id + ")" );
        $linkTag.append( ou.n );

        var $childTag = $( "<li/>" );

        $childTag.attr( "id", getTagId( ou.id ) );
        $childTag.append( " " );
        $childTag.append( $toggleTag );
        $childTag.append( " " );
        $childTag.append( $linkTag );

        return $childTag;
    }

    function setToggle( unitTag, expanded ) {
        var $toggleTag = $( unitTag ).find( "span" );
        var toggleImg = expanded ? getToggleCollapse() : getToggleExpand();

        if( $toggleTag.children().eq( 0 ) ) {
            $toggleTag.children().eq( 0 ).replaceWith( toggleImg );
        }
        else {
            $toggleTag.append( toggleImg );
        }
    }

    function setVisible( tag, visible ) {
        visible ? $( tag ).show() : $( tag ).hide();
    }

    function isVisible( tag ) {
        return $( tag ).is( ":visible" );
    }

    function getTagId( unitId ) {
        return 'orgUnit' + unitId;
    }

    function getToggleExpand() {
        return getToggleImage().attr( "src", "../images/colapse.png" ).attr( "alt", "[+]" );
    }

    function getToggleCollapse() {
        return getToggleImage().attr( "src", "../images/expand.png" ).attr( "alt", "[-]" );
    }

    function getToggleBlank() {
        return getToggleImage().attr( "src", "../images/transparent.gif" ).removeAttr( "alt" );
    }

    function getToggleImage() {
        return $( "<img/>" ).attr( "width", 9 ).attr( "height", 9 );
    }
}
