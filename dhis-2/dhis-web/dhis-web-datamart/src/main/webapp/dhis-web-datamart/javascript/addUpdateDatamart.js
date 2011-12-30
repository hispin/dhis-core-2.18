// -----------------------------------------------------------------------------
// After the page loaded completely
// -----------------------------------------------------------------------------

jQuery( document ).ready( function() {

	/* Validates the required fields */
	validation2( 'exportForm', function ( form )
	{
		form.submit();
	}, {
		'beforeValidateHandler': function()
		{
			listValidator( 'dataelementsValidator', 'selectedDataElements' );
			listValidator( 'indicatorsValidator', 'selectedIndicators' );
			listValidator( 'organisationunitsValidator', 'selectedOrganisationUnits' );
			listValidator( 'periodsValidator', 'selectedPeriods' );
			
			if( byId('selectedPeriods').options.length == 0 )
			{
				if( $("input:checked").length > 0 )
				{
					$("#periodsValidator").rules("add",{required:false});
				}
				else
				{
					$("#periodsValidator").rules("add",{required:true});
				}
			}
		},
		'rules': getValidationRules( 'dataMart' )
	} );
	
	checkValueIsExist( "name", "validateDataMartExport.action", {
		id: getFieldValue( "id" )
	} );

	/* Loads the available data elements */
	jQuery("#availableDataElements").dhisAjaxSelect({
		source: "../dhis-web-commons-ajax-json/getDataElements.action",
		iterator: "dataElements",
		connectedTo: "selectedDataElements",
		handler: function(item) {
			var option = jQuery("<option data-id='" + item.groups + "'/>");
			option.text( item.name );
			option.attr( "value", item.id );
			
			return option;
		},
		filter: {
			source: "../dhis-web-commons-ajax-json/getDataElementGroups.action",
			label: 'dataelement group',
			iterator: "dataElementGroups",
			handler: function(item) {
				return "<option data-key='id' data-value='" + item.id + "'>" + item.name + "</option>";
			}
		}
	});
	
	/* Loads the available indicators */
	jQuery("#availableIndicators").dhisAjaxSelect({
		source: "../dhis-web-commons-ajax-json/getIndicators.action",
		iterator: "indicators",
		connectedTo: "selectedIndicators",
		handler: function(item) {
			var option = jQuery("<option data-id='" + item.groups + "'/>");
			option.text( item.name );
			option.attr( "value", item.id );
			
			return option;
		},
		filter: {
			source: "../dhis-web-commons-ajax-json/getIndicatorGroups.action",
			label: 'indicator group',
			iterator: 'indicatorGroups',
			handler: function(item) {
				return "<option data-key='id' data-value='"+ item.id +"'>" + item.name + "</option>";
			}
		}
	});
	
	/* Loads the available organisation units */
	jQuery("#availableOrganisationUnits").dhisAjaxSelect({
		source: "../dhis-web-commons-ajax-json/getOrganisationUnits.action?level=1",
		iterator: "organisationUnits",
		connectedTo: "selectedOrganisationUnits",
		handler: function(item) {
			var option = jQuery("<option/>");
			option.text( item.name );
			option.attr( "value", item.id );
			
			return option;
		}
	});
});

// -----------------------------------------------------------------------------
// Data retrieval methods
// -----------------------------------------------------------------------------

function getOrganisationUnitsAtLevel()
{
	var organisationUnitLevel = getFieldValue( 'organisationUnitLevel' );
	var filterInput = jQuery( "#availableOrganisationUnits_filter_input" );
	
	if ( organisationUnitLevel != null )
	{
		if ( organisationUnitLevel != 0 ) {
			filterInput.attr( 'disabled', 'disabled' );
		} else {
			filterInput.removeAttr( 'disabled' );
		}
		
		jQuery.postJSON( '../dhis-web-commons-ajax-json/getOrganisationUnits.action',
		{ level: organisationUnitLevel }, function ( json ) {
		
			var availableOrganisationUnits = byId( "availableOrganisationUnits" );
			var selectedOrganisationUnits = byId( "selectedOrganisationUnits" );
			
			clearList( availableOrganisationUnits );
			
			var units = json.organisationUnits;
			
			for ( var i = 0; i < units.length; i++ )
			{
				var id = units[ i ].id;
				var name = units[ i ].name;
				
				if ( listContains( selectedOrganisationUnits, id ) == false )
				{						
					var option = document.createElement( "option" );
					option.value = id;
					option.text = name;
					availableOrganisationUnits.add( option, null );
				}
			}
		});
	}
}

function getOrganisationUnitChildren()
{
	var organisationUnitId = getFieldValue( 'availableOrganisationUnits' );
	
	if ( organisationUnitId != null )
	{
		jQuery.postJSON( '../dhis-web-commons-ajax-json/getOrganisationUnitChildren.action',
		{ id: organisationUnitId }, function ( json ) {
		
			var selectedOrganisationUnits = byId( "selectedOrganisationUnits" );
			var units = json.organisationUnits;
			
			for ( var i = 0; i < units.length; i++ )
			{
				var id = units[ i ].id;
				var name = units[ i ].name;
				
				if ( listContains( selectedOrganisationUnits, id ) == false )
				{
					var option = document.createElement( "option" );
					option.value = id;
					option.text = name;
					selectedOrganisationUnits.add( option, null );
				}
			}
		});
	}
}

