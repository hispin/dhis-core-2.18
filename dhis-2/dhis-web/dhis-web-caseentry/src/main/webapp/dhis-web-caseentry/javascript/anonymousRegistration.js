var DAO = DAO || {};

var DEFAULT_WAIT_CHECK_TIMEOUT = 10;

function initalizeProgramStages() {
    var programStagesInitialized = false;

    DAO.programStages = new dhis2.storage.Store( {name: 'programStages', adapter: 'dom-ss'}, function(store) {
        programStagesInitialized = true;
    });

    function waitCheck() {
        if(!programStagesInitialized) {
            setTimeout(waitCheck, DEFAULT_WAIT_CHECK_TIMEOUT);
        }
    }

    waitCheck();
}

function initializePrograms() {
    var programsInitialized = false;

    DAO.programs = new dhis2.storage.Store( {name: 'programs', adapter: 'dom-ss'}, function ( store ) {
        jQuery.getJSON( "getProgramMetaData.action", {},function ( data ) {
            var keys = _.keys( data.metaData.programs );
            var objs = _.values( data.metaData.programs );

            DAO.programs.addAll( keys, objs, function ( store ) {
                var deferred = $.Deferred();
                var promise = deferred.promise();

                _.each( _.values(data.metaData.programs), function(el, idx) {
                    var psid = el.programStages[0].id;

                    promise = promise.pipe(function () {
                        return loadProgramStage(psid);
                    });
                });

                deferred.resolve();

                selection.setListenerFunction( organisationUnitSelected );
                programsInitialized = true;
            } );
        } ).fail( function () {
            selection.setListenerFunction( organisationUnitSelected );
            programsInitialized = true;
        } );
    } );

    function waitCheck() {
        if(!programsInitialized) {
            setTimeout(waitCheck, DEFAULT_WAIT_CHECK_TIMEOUT);
        }
    }

    waitCheck();
}

$( document ).ready( function () {
    $.ajaxSetup( {
        type: 'POST',
        cache: false
    } );

    $( "#orgUnitTree" ).one( "ouwtLoaded", function () {
        // initialize the stores, and then try and add the data
        initalizeProgramStages();
        initializePrograms();
    } );

    $( document ).bind( 'dhis2.online', function ( event, loggedIn ) {
        if ( loggedIn ) {
            setHeaderDelayMessage( i18n_online_notification );
        }
        else {
            var form = [
                '<form style="display:inline;">',
                '<label for="username">Username</label>',
                '<input name="username" id="username" type="text" style="width: 70px; margin-left: 10px; margin-right: 10px" size="10"/>',
                '<label for="password">Password</label>',
                '<input name="password" id="password" type="password" style="width: 70px; margin-left: 10px; margin-right: 10px" size="10"/>',
                '<button id="login_button" type="button">Login</button>',
                '</form>'
            ].join( '' );

            setHeaderMessage( form );
            ajax_login();
        }
    } );

    $( document ).bind( 'dhis2.offline', function () {
        setHeaderMessage( i18n_offline_notification );
    } );

    dhis2.availability.startAvailabilityCheck();
} );

function ajax_login()
{
    $( '#login_button' ).bind( 'click', function()
    {
        var username = $( '#username' ).val();
        var password = $( '#password' ).val();

        $.post( '../dhis-web-commons-security/login.action', {
            'j_username' : username,
            'j_password' : password
        } ).success( function()
        {
            var ret = dhis2.availability.syncCheckAvailability();

            if ( !ret )
            {
                alert( i18n_ajax_login_failed );
            }
        } );
    } );
}

function organisationUnitSelected( orgUnits, orgUnitNames ) {
    showById( 'dataEntryMenu' );
    hideById( 'eventActionMenu' );
    hideById( 'dataEntryInfor' );
    hideById( 'advanced-search' );
    hideById( 'minimized-advanced-search' );
    hideById( 'listDiv' );
    hideById( 'programName' );
    setFieldValue( "listAll", true );
    setFieldValue( "startDate", '' );
    setFieldValue( "endDate", '' );
    jQuery( '#advancedSearchTB [name=searchText]' ).val( '' );

    setFieldValue( 'orgunitId', orgUnits[0] );
    setFieldValue( 'orgunitName', orgUnitNames[0] );
    hideById( 'listDiv' );
    hideById( 'dataEntryInfor' );

    DAO.programs.fetchAll( function ( store, arr ) {
        var programs = [];

        $.each( arr, function ( idx, item ) {
            if ( item.programAssociations.indexOf( orgUnits[0] ) != -1 ) {
                programs.push( item );
            }
        } );

        updateProgramList( programs );
    } );
}

function updateProgramList( arr ) {
    jQuery( '#searchingAttributeIdTD [id=searchObjectId] option' ).remove();
    jQuery( '#advancedSearchTB [id=searchObjectId] option' ).remove();
    clearListById( 'displayInReports' );
    clearListById( 'programId' );

    jQuery( '#programId' ).append( '<option value="" psid="" reportDateDes="' + i18n_report_date + '">[' + i18n_please_select + ']</option>' );

    for ( var i = 0; i < arr.length; i++ ) {
        jQuery( '#programId' ).append( '<option value="' + arr[i].key + '" psid="' + arr[i].programStages[0].id + '" reportDateDes="' +
            arr[i].programStages[0].reportDateDescription + '">' + arr[i].name + '</option>' );
    }

    disableCriteriaDiv();
    showById( 'selectDiv' );
}

function disableCriteriaDiv() {
    disable( 'listBtn' );
    disable( 'addBtn' );
    disable( 'filterBtn' );
    disable( 'removeBtn' );
    jQuery( '#criteriaDiv :input' ).each( function ( idx, item ) {
        disable( this.id );
    } );
}

function enableCriteriaDiv() {
    enable( 'listBtn' );
    enable( 'addBtn' );
    enable( 'filterBtn' );
    enable( 'removeBtn' );
    jQuery( '#criteriaDiv :input' ).each( function ( idx, item ) {
        enable( this.id );
    } );
}

function getDataElements() {
    hideById( 'dataEntryInfor' );
    hideById( 'listDiv' );
    jQuery( '#searchingAttributeIdTD [id=searchObjectId] option' ).remove();
    jQuery( '#advancedSearchTB [id=searchObjectId] option' ).remove();
    programStageId = jQuery( '#programId option:selected' ).attr( 'psid' );
    setFieldValue( 'programStageId', programStageId );
    setInnerHTML( 'reportDateDescriptionField', jQuery( '#programId option:selected' ).attr( 'reportDateDes' ) );
    setInnerHTML( 'reportDateDescriptionField2', jQuery( '#programId option:selected' ).attr( 'reportDateDes' ) );

    if ( programStageId == '' ) {
        removeAllAttributeOption();
        disableCriteriaDiv();
        enable( 'orgunitName' );
        enable( 'programId' );
        hideById( 'listDiv' );
        setFieldValue( 'searchText' );
        return;
    }

    jQuery.getJSON( "getProgramStageDataElementList.action",
        {
            programStageId: getFieldValue( 'programStageId' )
        },
        function ( json ) {
            jQuery( '#advancedSearchTB [name=searchText]' ).val( '' );
            jQuery( '.stage-object-selected' ).attr( 'psid', jQuery( '#programId option:selected' ).attr( "psid" ) );

            clearListById( 'searchObjectId' );
            clearListById( 'displayInReports' );

            jQuery( '[name=searchObjectId]' ).append( '<option value="" >[' + i18n_please_select + ']</option>' );
            for ( i in json.programStageDataElements ) {
                jQuery( '[name=searchObjectId]' ).append( '<option value="' + json.programStageDataElements[i].id + '" type="' + json.programStageDataElements[i].type + '">' + json.programStageDataElements[i].name + '</option>' );
                if ( json.programStageDataElements[i].displayInReports == 'true' ) {
                    jQuery( '#displayInReports' ).append( '<option value="' + json.programStageDataElements[i].id + '"></option>' );
                }
            }

            enableCriteriaDiv();
            validateSearchEvents( true );
        } ).fail(function() {
            enable( 'addBtn' );
        });
}

function dataElementOnChange( this_ ) {
    var container = jQuery( this_ ).parent().parent().attr( 'id' );
    var element = jQuery( '#' + container + ' [id=searchText]' );
    var valueType = jQuery( '#' + container + ' [id=searchObjectId] option:selected' ).attr( 'type' );

    if ( valueType == 'date' ) {
        element.replaceWith( getDateField( container ) );
        datePickerValid( 'searchText_' + container );
        return;
    }
    else {
        $( '#searchText_' + container ).datepicker( "destroy" );
        $( '#' + container + ' [id=dateOperator]' ).replaceWith( "" );

        if ( valueType == 'bool' ) {
            element.replaceWith( getTrueFalseBox() );
        }
        else if ( valueType == 'optionset' ) {
            element.replaceWith( searchTextBox );
            autocompletedFilterField( container + " [id=searchText]", jQuery( this_ ).val() );
        }
        else if ( valueType == 'username' ) {
            autocompletedUsernameField( jQuery( this ).attr( 'id' ) );
        }
        else {
            element.replaceWith( searchTextBox );
        }
    }
}

function autocompletedFilterField( idField, searchObjectId ) {
    var input = jQuery( "#" + idField );
    input.css( "width", "237px" );
    input.autocomplete( {
        delay: 0,
        minLength: 0,
        source: function ( request, response ) {
            $.ajax( {
                url: "getOptions.action?id=" + searchObjectId + "&query=" + input.val(),
                dataType: "json",
                success: function ( data ) {
                    response( $.map( data.options, function ( item ) {
                        return {
                            label: item.o,
                            id: item.o
                        };
                    } ) );
                }
            } );
        },
        select: function ( event, ui ) {
            input.val( ui.item.value );
            input.autocomplete( "close" );
        }
    } )
        .addClass( "ui-widget" );

    input.data( "autocomplete" )._renderItem = function ( ul, item ) {
        return $( "<li></li>" )
            .data( "item.autocomplete", item )
            .append( "<a>" + item.label + "</a>" )
            .appendTo( ul );
    };

    var wrapper = this.wrapper = $( "<span style='width:200px'>" )
        .addClass( "ui-combobox" )
        .insertAfter( input );

    var button = $( "<a style='width:20px; margin-bottom:-5px;height:20px;'>" )
        .attr( "tabIndex", -1 )
        .attr( "title", i18n_show_all_items )
        .appendTo( wrapper )
        .button( {
            icons: {
                primary: "ui-icon-triangle-1-s"
            },
            text: false
        } )
        .addClass( 'small-button' )
        .click( function () {
            if ( input.autocomplete( "widget" ).is( ":visible" ) ) {
                input.autocomplete( "close" );
                return;
            }
            $( this ).blur();
            input.autocomplete( "search", "" );
            input.focus();
        } );
}

function autocompletedUsernameField( idField ) {
    var input = jQuery( "#" + idField );
    input.parent().width( input.width() + 200 );
    var dataElementId = input.attr( 'id' ).split( '-' )[1];

    input.autocomplete( {
        delay: 0,
        minLength: 0,
        source: function ( request, response ) {
            $.ajax( {
                url: "getUsernameList.action?query=" + input.val(),
                dataType: "json",
                cache: true,
                success: function ( data ) {
                    response( $.map( data.usernames, function ( item ) {
                        return {
                            label: item.u,
                            id: item.u
                        };
                    } ) );
                }
            } );
        },
        minLength: 0,
        select: function ( event, ui ) {
            var fieldValue = ui.item.value;

            if ( !dhis2.trigger.invoke( "caseentry-value-selected", [dataElementId, fieldValue] ) ) {
                input.val( "" );
                return false;
            }

            input.val( fieldValue );
            if ( !unSave ) {
                saveVal( dataElementId );
            }
            input.autocomplete( "close" );
        },
        change: function ( event, ui ) {
            if ( !ui.item ) {
                var matcher = new RegExp( "^" + $.ui.autocomplete.escapeRegex( $( this ).val() ) + "$", "i" ),
                    valid = false;
                if ( !valid ) {
                    $( this ).val( "" );
                    if ( !unSave )
                        saveVal( dataElementId );
                    input.data( "autocomplete" ).term = "";
                    return false;
                }
            }
        }
    } )
        .addClass( "ui-widget" );

    input.data( "autocomplete" )._renderItem = function ( ul, item ) {
        return $( "<li></li>" )
            .data( "item.autocomplete", item )
            .append( "<a>" + item.label + "</a>" )
            .appendTo( ul );
    };

    var wrapper = this.wrapper = $( "<span style='width:200px'>" )
        .addClass( "ui-combobox" )
        .insertAfter( input );

    var button = $( "<a style='width:20px; margin-bottom:-5px;height:20px;'>" )
        .attr( "tabIndex", -1 )
        .attr( "title", i18n_show_all_items )
        .appendTo( wrapper )
        .button( {
            icons: {
                primary: "ui-icon-triangle-1-s"
            },
            text: false
        } )
        .addClass( 'small-button' )
        .click( function () {
            if ( input.autocomplete( "widget" ).is( ":visible" ) ) {
                input.autocomplete( "close" );
                return;
            }
            $( this ).blur();
            input.autocomplete( "search", "" );
            input.focus();
        } );
}

function removeAllAttributeOption() {
    jQuery( '#advancedSearchTB tbody tr' ).each( function ( i, item ) {
        if ( i > 0 ) {
            jQuery( item ).remove();
        }
    } )
}

function validateSearchEvents( listAll ) {
    listAll = eval( listAll );
    setFieldValue( 'listAll', listAll );

    var flag = true;
    if ( !listAll ) {
        if ( getFieldValue( 'startDate' ) == "" || getFieldValue( 'endDate' ) == "" ) {
            showWarningMessage( i18n_specify_a_date );
            flag = false;
        }

        if ( flag && !listAll && jQuery( '#filterBtn' ).attr( "disabled" ) == "disabled" ) {
            jQuery( '#advancedSearchTB tr' ).each( function ( index, row ) {
                if ( index > 1 ) {
                    jQuery( row ).find( ':input' ).each( function ( idx, item ) {
                        var input = jQuery( item );
                        if ( input.attr( 'type' ) != 'button' && idx == 0 && input.val() == '' ) {
                            showWarningMessage( i18n_specify_data_element );
                            flag = false;
                        }
                    } )
                }
            } );
        }
    }

    if ( flag ) {
        searchEvents( listAll );
    }
}

function searchEvents( listAll ) {
    hideById( 'dataEntryInfor' );
    hideById( 'listDiv' );

    var params = '';
    jQuery( '#displayInReports option' ).each( function ( i, item ) {
        var input = jQuery( item );
        params += '&searchingValues=de_' + input.val() + '_false_';
    } );

    if ( listAll ) {
        params += '&startDate=';
        params += '&endDate=';
    }
    else {
        var value = '';
        var searchingValue = '';
        params += '&startDate=' + getFieldValue( 'startDate' );
        params += '&endDate=' + getFieldValue( 'endDate' );
        if ( byId( "incompleted" ).checked ) {
            params += '&useCompletedEvents=false';
        }
        jQuery( '#advancedSearchTB tr' ).each( function ( index, row ) {
            if ( index > 1 ) {
                jQuery( row ).find( ':input' ).each( function ( idx, item ) {
                    var input = jQuery( item );
                    if ( input.attr( 'type' ) != 'button' ) {
                        if ( idx == 0 && input.val() != '' ) {
                            searchingValue = 'de_' + input.val() + '_false_';
                        }
                        else if ( input.val() != '' ) {
                            value += jQuery.trim( input.val() ).toLowerCase();
                        }
                    }
                } );

                if ( value != '' ) {
                    searchingValue += getValueFormula( value );
                    params += '&searchingValues=' + searchingValue;
                }
                searchingValue = '';
                value = '';
            }
        } )
    }

    params += '&facilityLB=selected';
    params += '&level=0';
    params += '&orgunitIds=' + getFieldValue( 'orgunitId' );
    params += '&programStageId=' + jQuery( '#programId option:selected' ).attr( 'psid' );
    params += '&orderByOrgunitAsc=false';
    params += '&userOrganisationUnit=false';
    params += '&userOrganisationUnitChildren=false';

    contentDiv = 'listDiv';
    showLoader();

    $.ajax( {
        type: "POST",
        url: 'searchProgramStageInstances.action',
        data: params,
        success: function ( html ) {
            hideById( 'dataEntryInfor' );
            setInnerHTML( 'listDiv', html );

            var searchInfor = (listAll) ? i18n_list_all_events : i18n_search_events_by_dataelements;
            setInnerHTML( 'searchInforTD', searchInfor );

            if ( !listAll && jQuery( '#filterBtn' ).attr( "disabled" ) == "disabled" ) {
                showById( 'minimized-advanced-search' );
                hideById( 'advanced-search' );
            }
            else {
                hideById( 'minimized-advanced-search' );
                hideById( 'advanced-search' );
                showById( 'filterBtn' );
            }

            showById( 'listDiv' );
            hideById( 'loaderDiv' );
        }
    } );
}

function getValueFormula( value ) {
    if ( value.indexOf( '"' ) != value.lastIndexOf( '"' ) ) {
        value = value.replace( /"/g, "'" );
    }
    // if key is [xyz] && [=xyz]
    if ( value.indexOf( "'" ) == -1 ) {
        var flag = value.match( /[>|>=|<|<=|=|!=]+[ ]*/ );

        if ( flag == null ) {
            value = "='" + value + "'";
        }
        else {
            value = value.replace( flag, flag + "'" );
            value += "'";
        }
    }
    // if key is ['xyz'] && [='xyz']
    // if( value.indexOf("'") != value.lastIndexOf("'") )
    else {
        var flag = value.match( /[>|>=|<|<=|=|!=]+[ ]*/ );

        if ( flag == null ) {
            value = "=" + value;
        }
    }

    return value;
}

function removeEvent( programStageId ) {
    removeItem( programStageId, '', i18n_comfirm_delete_event, 'removeCurrentEncounter.action' );
}

function showUpdateEvent( programStageInstanceId ) {
    hideById( 'dataEntryMenu' );
    showById( 'eventActionMenu' );
    jQuery( "[name=eventActionLink]" ).hide();
    hideById( 'selectDiv' );
    hideById( 'searchDiv' );
    hideById( 'listDiv' );
    setFieldValue( 'programStageInstanceId', programStageInstanceId );
    setInnerHTML( 'dataEntryFormDiv', '' );
    showLoader();

    service.displayProgramStage( getFieldValue( 'programStageId' ), programStageInstanceId, getFieldValue( 'orgunitId' ) );
}

function backEventList() {
    showById( 'dataEntryMenu' );
    hideById( 'eventActionMenu' );
    hideById( 'dataEntryInfor' );
    hideById( 'programName' );
    showById( 'selectDiv' );
    showById( 'searchDiv' );
    showById( 'listDiv' );
    searchEvents( eval( getFieldValue( 'listAll' ) ) );
}

function showAddEventForm() {
    showById( 'eventActionMenu' );
    jQuery( "[name=eventActionLink]" ).hide();
    hideById( 'dataEntryMenu' );
    setInnerHTML( 'dataEntryFormDiv', '' );
    setFieldValue( 'executionDate', '' );
    hideById( 'selectDiv' );
    hideById( 'searchDiv' );
    hideById( 'listDiv' );
    showById( 'programName' );
    hideById( 'actionDiv' );
    showById( 'dataEntryInfor' );
    setFieldValue( 'programStageInstanceId', '0' );
    byId( 'executionDate' ).style.backgroundColor = "#ffffff";
    setInnerHTML( 'programName', jQuery( '#programId option:selected' ).text() );
}

function addNewEvent() {
    var programStageInstanceId = getFieldValue( 'programStageInstanceId' );
    var programId = jQuery( '#programId option:selected' ).val();
    var executionDate = getFieldValue( 'executionDate' );
    var orgunitId = getFieldValue( 'orgunitId' );

    jQuery( "#executionDate" ).css( 'background-color', SAVING_COLOR );

    service.saveExecutionDate( programId, programStageInstanceId, executionDate, orgunitId );
}

function completedAndAddNewEvent() {
    doComplete( true );
}

function removeEmptyEvents() {
    var result = window.confirm( i18n_confirm_remove_empty_events );

    if ( result ) {
        jQuery.getJSON( "removeEmptyEvents.action",
            {
                programStageId: jQuery( '#selectDiv [id=programId] option:selected' ).attr( 'psid' )
            },
            function ( json ) {
                if ( json.response == 'success' ) {
                    showSuccessMessage( i18n_remove_empty_events_success );
                    validateSearchEvents( true )
                }
            } );
    }
}

function removeCurrentEvent() {
    var result = window.confirm( i18n_comfirm_delete_event );
    if ( result ) {
        $.postJSON(
            "removeCurrentEncounter.action",
            {
                "id": getFieldValue( 'programStageInstanceId' )
            },
            function ( json ) {
                if ( json.response == "success" ) {
                    backEventList();
                }
                else if ( json.response == "error" ) {
                    showWarningMessage( json.message );
                }
            } );
    }
}

function showFilterForm() {
    showById( 'advanced-search' );
    hideById( 'minimized-advanced-search' );
    disable( 'filterBtn' );
    setFieldValue( 'listAll', false );
}

function removeAllOption() {
    enable( 'filterBtn' );
    jQuery( '#advancedSearchTB tr' ).each( function ( i, row ) {
        if ( i > 2 ) {
            jQuery( this ).remove();
        }
        else if ( i == 2 ) {
            jQuery( this ).find( ':input' ).each( function ( idx, item ) {
                var input = jQuery( item );
                if ( input.attr( 'type' ) != 'button' ) {
                    input.val( '' );
                }
            } );
        }
    } );
    jQuery( '#searchObjectId' ).val( "" );
    jQuery( '#searchText' ).val( "" );
    searchEvents( eval( getFieldValue( "listAll" ) ) );
}

// execution date module
var service = (function () {
    var anonymousExecutionDatesInitialized = false;
    var anonymousDataValuesInitialized = false;

    var anonymousExecutionDates = new dhis2.storage.Store( {name: 'anonymousExecutionDates', adapter: 'dom' }, function ( store ) {
        anonymousExecutionDatesInitialized = true;
    } );

    var anonymousDataValues = new dhis2.storage.Store( {name: 'anonymousDataValues', adapter: 'dom' }, function ( store ) {
        anonymousDataValuesInitialized = true;
    } );

    return {
        saveExecutionDate: function( programId, programStageInstanceId, executionDate, organisationUnitId ) {
            $.ajax( {
                url: 'saveExecutionDate.action',
                data: createExecutionDate(programId, programStageInstanceId, executionDate, organisationUnitId),
                type: 'POST',
                dataType: 'json'
            } ).done(function ( json ) {
                if ( json.response == 'success' ) {
                    jQuery( "#executionDate" ).css( 'background-color', SUCCESS_COLOR );
                    setFieldValue( 'programStageInstanceId', json.message );

                    if ( programStageInstanceId != json.message ) {
                        showUpdateEvent( json.message );
                    }
                }
                else {
                    jQuery( "#executionDate" ).css( 'background-color', ERROR_COLOR );
                    showWarningMessage( json.message );
                }
            } ).fail( function () {
                function waitForExecutionDateStore() {
                    if(!anonymousExecutionDatesInitialized) {
                        setTimeout(waitForExecutionDateStore, 50);
                    }
                }

                waitForExecutionDateStore();

                var data = createExecutionDate(programId, programStageInstanceId, executionDate, organisationUnitId);

                if(programStageInstanceId == 0) {
                    anonymousExecutionDates.keys(function(store, keys) {
                        var i = 100;

                        for(; i<10000; i++) {
                            if( keys.indexOf(i) == -1 ) break;
                        }

                        programStageInstanceId = "local"+i;

                        setFieldValue( 'programStageInstanceId', programStageInstanceId );
                        jQuery( "#executionDate" ).css( 'background-color', SUCCESS_COLOR );
                        showUpdateEvent( programStageInstanceId );
                    });
                } else {
                    // if we have a programStageInstanceId, just reuse that one
                    setFieldValue( 'programStageInstanceId', programStageInstanceId );
                    jQuery( "#executionDate" ).css( 'background-color', SUCCESS_COLOR );
                    showUpdateEvent( programStageInstanceId );
                }
            } );
        },

        displayProgramStage: function( programStageId, programStageInstanceId, organisationUnitId ) {
            loadProgramStage( programStageId, programStageInstanceId, organisationUnitId, function ( data ) {
                $( '#dataEntryFormDiv' ).html( data );
                updateDataForm();
            },function () {
                $( '#dataEntryFormDiv' ).html( "<div class='message message-info'>Unable to load form.</div>" );
                hideById( 'loaderDiv' );
            } );
        }
    }
})();

function updateDataForm() {
    jQuery( '#inputCriteriaDiv' ).remove();
    showById( 'programName' );
    showById( 'actionDiv' );
    var programName = jQuery( '#programId option:selected' ).text();
    var programStageId = jQuery( '#programId option:selected' ).attr( 'psid' );
    jQuery( '.stage-object-selected' ).attr( 'psid', programStageId );
    setInnerHTML( 'programName', programName );
    jQuery('#executionDate').css('width',430);
    jQuery('#executionDate').css('margin-right',30);

    if ( getFieldValue( 'completed' ) == 'true' ) {
        disable( "completeBtn" );
        enable( "uncompleteBtn" );
    }
    else {
        enable( "completeBtn" );
        disable( "uncompleteBtn" );
    }
    hideById( 'loaderDiv' );
    showById( 'dataEntryInfor' );
    showById( 'entryFormContainer' );

    jQuery( "#entryForm :input" ).each( function () {
        if ( ( jQuery( this ).attr( 'options' ) != null && jQuery( this ).attr( 'options' ) == 'true' )
            || ( jQuery( this ).attr( 'username' ) != null && jQuery( this ).attr( 'username' ) == 'true' ) ) {
            var input = jQuery( this );
            input.parent().width( input.width() + 200 );
        }
    } );
}

function createExecutionDate( programId, programStageInstanceId, executionDate, organisationUnitId ) {
    return {
        programId: programId,
        programStageInstanceId: programStageInstanceId,
        executionDate: executionDate,
        organisationUnitId: organisationUnitId
    }
}

function loadProgramStage( programStageId, programStageInstanceId, organisationUnitId, success, fail ) {
    /*
    DAO.programStages.fetch(programStageId, function(store, arr) {
        if ( arr.length > 0 ) {
            if(success) success(arr[0]);
        } else {
            var data = {};

            if(programStageId)
                data.programStageId = programStageId;

            if(programStageInstanceId)
                data.programStageInstanceId = programStageInstanceId;

            if(organisationUnitId)
                data.organisationUnitId = organisationUnitId;

            $.ajax( {
                url: 'dataentryform.action',
                data: data,
                dataType: 'html'
            } ).done(function(data) {
                DAO.programStages.add(programStageId, data);
                if(success) success(data);
            } ).fail(function() {
                if(fail) fail();
            });
        }
    });
    */

    var data = {};

    if(programStageId)
        data.programStageId = programStageId;

    if(programStageInstanceId)
        data.programStageInstanceId = programStageInstanceId;

    if(organisationUnitId)
        data.organisationUnitId = organisationUnitId;

    $.ajax( {
        url: 'dataentryform.action',
        data: data,
        dataType: 'html'
    } ).done(function(data) {
        DAO.programStages.add(programStageId, data);
        if(success) success(data);
    } ).fail(function() {
        if(fail) fail();
    });
}
