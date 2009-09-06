/*reference local blank image*/
Ext.BLANK_IMAGE_URL = '../resources/ext/resources/images/default/s.gif';

Ext.onReady(function()
{
    Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
    
    Ext.override(Ext.layout.FormLayout, {
        renderItem : function(c, position, target) {
            if (c && !c.rendered && c.isFormField && c.inputType != 'hidden') {
                var args = [
                    c.id, c.fieldLabel,
                    c.labelStyle||this.labelStyle||'',
                    this.elementStyle||'',
                    typeof c.labelSeparator == 'undefined' ? this.labelSeparator : c.labelSeparator,
                    (c.itemCls||this.container.itemCls||'') + (c.hideLabel ? ' x-hide-label' : ''),
                    c.clearCls || 'x-form-clear-left' 
                ];
                
                if (typeof position == 'number') {
                    position = target.dom.childNodes[position] || null;
                }
                
                if (position) {
                    c.formItem = this.fieldTpl.insertBefore(position, args, true);
                }
                else {
                    c.formItem = this.fieldTpl.append(target, args, true);
                }
                c.actionMode = 'formItem';
                c.render('x-form-el-'+c.id);
                c.container = c.formItem;
                c.actionMode = 'container';
            }
            else {
                Ext.layout.FormLayout.superclass.renderItem.apply(this, arguments);
            }
        }
    });

    Ext.override(Ext.form.TriggerField, {
        actionMode: 'wrap',
        onShow: Ext.form.TriggerField.superclass.onShow,
        onHide: Ext.form.TriggerField.superclass.onHide
    });
    
    Ext.override(Ext.form.Checkbox, {
        onRender: function(ct, position) {
            Ext.form.Checkbox.superclass.onRender.call(this, ct, position);
            if(this.inputValue !== undefined) {
                this.el.dom.value = this.inputValue;
            }
            /*this.el.addClass('x-hidden');*/
            this.innerWrap = this.el.wrap({
                /*tabIndex: this.tabIndex,*/
                cls: this.baseCls+'-wrap-inner'
            });
            
            this.wrap = this.innerWrap.wrap({cls: this.baseCls+'-wrap'});
            
            this.imageEl = this.innerWrap.createChild({
                tag: 'img',
                src: Ext.BLANK_IMAGE_URL,
                cls: this.baseCls
            });
            
            if(this.boxLabel){
                this.labelEl = this.innerWrap.createChild({
                    tag: 'label',
                    htmlFor: this.el.id,
                    cls: 'x-form-cb-label',
                    html: this.boxLabel
                });
            }
            /*this.imageEl = this.innerWrap.createChild({
                tag: 'img',
                src: Ext.BLANK_IMAGE_URL,
                cls: this.baseCls
            }, this.el);*/
            if(this.checked) {
                this.setValue(true);
            }
            else {
                this.checked = this.el.dom.checked;
            }
            this.originalValue = this.checked;
        },
        afterRender: function() {
            Ext.form.Checkbox.superclass.afterRender.call(this);
            /*this.wrap[this.checked ? 'addClass' : 'removeClass'](this.checkedCls);*/
            this.imageEl[this.checked ? 'addClass' : 'removeClass'](this.checkedCls);
        },
        initCheckEvents: function() {
            /*this.innerWrap.removeAllListeners();*/
            this.innerWrap.addClassOnOver(this.overCls);
            this.innerWrap.addClassOnClick(this.mouseDownCls);
            this.innerWrap.on('click', this.onClick, this);
            /*this.innerWrap.on('keyup', this.onKeyUp, this);*/
        },
        onFocus: function(e) {
            Ext.form.Checkbox.superclass.onFocus.call(this, e);
            /*this.el.addClass(this.focusCls);*/
            this.innerWrap.addClass(this.focusCls);
        },
        onBlur: function(e) {
            Ext.form.Checkbox.superclass.onBlur.call(this, e);
            /*this.el.removeClass(this.focusCls);*/
            this.innerWrap.removeClass(this.focusCls);
        },
        onClick: function(e) {
            if (e.getTarget().htmlFor != this.el.dom.id) {
                if (e.getTarget() !== this.el.dom) {
                    this.el.focus();
                }
                if (!this.disabled && !this.readOnly) {
                    this.toggleValue();
                }
            }
            /*e.stopEvent();*/
        },
        onEnable: Ext.form.Checkbox.superclass.onEnable,
        onDisable: Ext.form.Checkbox.superclass.onDisable,
        onKeyUp: undefined,
        setValue: function(v) {
            var checked = this.checked;
            this.checked = (v === true || v === 'true' || v == '1' || String(v).toLowerCase() == 'on');
            if (this.rendered) {
                this.el.dom.checked = this.checked;
                this.el.dom.defaultChecked = this.checked;
                /*this.wrap[this.checked ? 'addClass' : 'removeClass'](this.checkedCls);*/
                this.imageEl[this.checked ? 'addClass' : 'removeClass'](this.checkedCls);
            }
            if (checked != this.checked) {
                this.fireEvent("check", this, this.checked);
                if(this.handler){
                    this.handler.call(this.scope || this, this, this.checked);
                }
            }
        },
        getResizeEl: function() {
            /*if(!this.resizeEl){
                this.resizeEl = Ext.isSafari ? this.wrap : (this.wrap.up('.x-form-element', 5) || this.wrap);
            }
            return this.resizeEl;*/
            return this.wrap;
        }
    });
    
    Ext.override(Ext.form.Radio, {
        checkedCls: 'x-form-radio-checked'
    });

    myMap: null;
	map = new OpenLayers.Map({
		controls: [
			new OpenLayers.Control.Navigation(),
			new OpenLayers.Control.ArgParser(),
			new OpenLayers.Control.Attribution(),
		]
	});
	this.myMap = map;
    
    MAPDATA = null;
    URL = null;
    ACTIVEPANEL = 'choropleth';
    MAPVIEW = false;
    MAPVIEWACTIVE = false;    
    URLACTIVE = false;
    PARAMETER = null;
    BOUNDS = 0;
    MAP_SOURCE_TYPE_DATABASE = 'database';
    MAP_SOURCE_TYPE_SHAPEFILE = 'shapefile';
    MAPSOURCE = null;
    
    Ext.Ajax.request({
        url: path + 'getMapSourceTypeUserSetting' + type,
        method: 'GET',
        
        success: function( responseObject ) {
            MAPSOURCE = Ext.util.JSON.decode( responseObject.responseText ).mapSource;
        },
        failure: function() {
            alert( 'Status', 'Error while saving data' );
        }
    });
	
	MASK = new Ext.LoadMask(Ext.getBody(), {msg: 'Loading...', msgCls: 'x-mask-loading2'});
	
    function getUrlParam(strParamName) {
        var output = "";
        var strHref = window.location.href;
        if ( strHref.indexOf("?") > -1 ) {
            var strQueryString = strHref.substr(strHref.indexOf("?")).toLowerCase();
            var aQueryString = strQueryString.split("&");
            for ( var iParam = 0; iParam < aQueryString.length; iParam++ ) {
                if (aQueryString[iParam].indexOf(strParamName.toLowerCase() + "=") > -1 ) {
                    var aParam = aQueryString[iParam].split("=");
                    output = aParam[1];
                    break;
                }
            }
        }
        return unescape(output);
    }
    
    if (getUrlParam('view') != '') {
        PARAMETER = getUrlParam('view');
        URLACTIVE = true;
    }
    
    function validateInput(name) {
        if (name.length > 25) {
            return false;
        }
        else {
            return true;
        }
    }
    
    function getMultiSelectHeight() {
        var h = screen.height;
        
        if (h <= 800) {
            return 120;
        }
        else if (h <= 1050) {
            return 310;
        }
        else if (h <= 1200) {
            return 530;
        }
        else {
            return 850;
        }
    }
    
    function isNumber(x) 
    { 
        return ( (typeof x === typeof 1) && (null !== x) && isFinite(x) );
    }
    
    var vmap0 = new OpenLayers.Layer.WMS(
        "World WMS",
        "http://labs.metacarta.com/wms/vmap0", 
        {layers: 'basic'}
    );
                                   
    var choroplethLayer = new OpenLayers.Layer.Vector(CHOROPLETH_LAYERNAME, {
        'visibility': false,
        'displayInLayerSwitcher': false,
        'styleMap': new OpenLayers.StyleMap({
            'default': new OpenLayers.Style(
                OpenLayers.Util.applyDefaults(
                    {'fillOpacity': 1, 'strokeColor': '#222222', 'strokeWidth': 1},
                    OpenLayers.Feature.Vector.style['default']
                )
            ),
            'select': new OpenLayers.Style(
                {'strokeColor': '#000000', 'strokeWidth': 2, 'cursor': 'pointer'}
            )
        })
    });
    
    map.addLayers([ vmap0, choroplethLayer ]);
        
    Ext.Ajax.request({
        url: path + 'getAllMapLayers' + type,
        method: 'GET',
        success: function(responseObject) {
            var mapLayers = Ext.util.JSON.decode(responseObject.responseText).mapLayers;
            
            for (var i = 0; i < mapLayers.length; i++) {
                var fillColor = mapLayers[i].fillColor;
                var fillOpacity = parseFloat(mapLayers[i].fillOpacity);
                var strokeColor = mapLayers[i].strokeColor;
                var strokeWidth = parseFloat(mapLayers[i].strokeWidth);
                
                var treeLayer = new OpenLayers.Layer.Vector(mapLayers[i].name, {
                    'visibility': false,
                    'styleMap': new OpenLayers.StyleMap({
                        'default': new OpenLayers.Style(
                            OpenLayers.Util.applyDefaults(
                                {'fillColor': fillColor, 'fillOpacity': fillOpacity, 'strokeColor': strokeColor, 'strokeWidth': strokeWidth},
                                OpenLayers.Feature.Vector.style['default']
                            )
                        )
                    }),
                    'strategies': [new OpenLayers.Strategy.Fixed()],
                    'protocol': new OpenLayers.Protocol.HTTP({
                        'url': GEOJSON_URL + mapLayers[i].mapSource,
                        'format': new OpenLayers.Format.GeoJSON()
                    })
                });
                
                treeLayer.events.register('loadstart', null, function() {
					MASK.msg = 'Loading...';
                    MASK.show();
                });
                
                treeLayer.events.register('loadend', null, function() {
                    MASK.hide();
                });
                    
                map.addLayer(treeLayer);
            }
        },
        failure: function() {}
    });
    
    map.events.on({
        changelayer: function(e) {
            if (e.property == 'visibility' && e.layer != choroplethLayer) {
                if (e.layer.visibility) {
                    selectFeatureChoropleth.deactivate();
                }
                else {
                    selectFeatureChoropleth.activate();
                }
            }
        }
    });

    var selectFeatureChoropleth = new OpenLayers.Control.newSelectFeature(
        choroplethLayer,
        {
            onClickSelect: onClickSelectChoropleth,
            onClickUnselect: onClickUnselectChoropleth,
            onHoverSelect: onHoverSelectChoropleth,
            onHoverUnselect: onHoverUnselectChoropleth
        }
    );
    
    map.addControl(selectFeatureChoropleth);
    selectFeatureChoropleth.activate();

    map.setCenter(new OpenLayers.LonLat(init_longitude, init_latitude), init_zoom);
    
    /*REGISTER MAPS PANEL*/
    
    var organisationUnitLevelStore = new Ext.data.JsonStore({
        url: path + 'getOrganisationUnitLevels' + type,
        baseParams: { format: 'json' },
        root: 'organisationUnitLevels',
        fields: ['id', 'level', 'name'],
        autoLoad: true
    });

    var organisationUnitStore = new Ext.data.JsonStore({
        url: path + 'getOrganisationUnitsAtLevel' + type,
        baseParams: { level: 1, format: 'json' },
        root: 'organisationUnits',
        fields: ['id', 'name'],
        sortInfo: { field: 'name', direction: 'ASC' },
        autoLoad: false
    });
    
    var existingMapsStore = new Ext.data.JsonStore({
        url: path + 'getAllMaps' + type,
        baseParams: { format: 'jsonmin' },
        root: 'maps',
        fields: ['id', 'name', 'mapLayerPath', 'organisationUnitLevel'],
        autoLoad: true
    });

    var organisationUnitComboBox = new Ext.form.ComboBox({
        id: 'organisationunit_cb',
        fieldLabel: 'Organisation unit',
        typeAhead: true,
        editable: false,
        valueField: 'id',
        displayField: 'name',
        emptyText: MENU_EMPTYTEXT,
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        selectOnFocus: true,
        width: combo_width,
        minListWidth: combo_width + 26,
        store: organisationUnitStore
    });
    
    var organisationUnitLevelComboBox = new Ext.form.ComboBox({
        id: 'organisationunitlevel_cb',
        fieldLabel: 'Level',
        typeAhead: true,
        editable: false,
        valueField: 'id',
        displayField: 'name',
        emptyText: MENU_EMPTYTEXT,
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        selectOnFocus: true,
        width: combo_width,
        minListWidth: combo_width + 26,
        store: organisationUnitLevelStore,
        listeners: {
            'select': {
                fn: function() {
                    var level1 = Ext.getCmp('newmap_cb').getValue();
                    var level2 = Ext.getCmp('organisationunitlevel_cb').getValue();
                    var orgunit = Ext.getCmp('organisationunit_cb').getValue();

                    if (level1 >= level2) { /*CURRENTLY NOT WORKING BECAUSE OF valuefield: 'id'*/
                        organisationUnitLevelComboBox.reset();
                        Ext.messageRed.msg('New map', 'The organisation unit selected above must be divided into a lower level than itself.');
                        return;
                    }
                },
                scope: this
            }
        }
    });

    var newNameTextField = new Ext.form.TextField({
        id: 'newname_tf',
        emptyText: MENU_EMPTYTEXT,
        width: combo_width
    });
    
    var editNameTextField = new Ext.form.TextField({
        id: 'editname_tf',
        emptyText: MENU_EMPTYTEXT,
        width: combo_width
    });
    
    var mapLayerPathTextField = new Ext.form.TextField({
        id: 'maplayerpath_tf',
        emptyText: MENU_EMPTYTEXT,
        width: combo_width
    });
    
    var typeComboBox = new Ext.form.ComboBox({
        id: 'type_cb',
        editable: false,
        displayField: 'name',
        valueField: 'name',
		emptyText: MENU_EMPTYTEXT,
        width: combo_width,
        minListWidth: combo_width + 26,
        triggerAction: 'all',
        mode: 'local',
        value: 'Polygon',
        store: new Ext.data.SimpleStore({
            fields: ['name'],
            data: [['Polygon']]
        })
    });
    
    var newNameColumnTextField = new Ext.form.TextField({
        id: 'newnamecolumn_tf',
        emptyText: MENU_EMPTYTEXT,
        width: combo_width
    });
    
    var editNameColumnTextField = new Ext.form.TextField({
        id: 'editnamecolumn_tf',
        emptyText: MENU_EMPTYTEXT,
        width: combo_width
    });
    
    var newLongitudeTextField = new Ext.form.TextField({
        id: 'newlongitude_tf',
        emptyText: MENU_EMPTYTEXT,
        width: combo_width
    });
    
    var editLongitudeTextField = new Ext.form.TextField({
        id: 'editlongitude_tf',
        emptyText: MENU_EMPTYTEXT,
        width: combo_width
    });
    
    var newLatitudeTextField = new Ext.form.TextField({
        id: 'newlatitude_tf',
        emptyText: MENU_EMPTYTEXT,
        width: combo_width
    });
    
    var editLatitudeTextField = new Ext.form.TextField({
        id: 'editlatitude_tf',
        emptyText: MENU_EMPTYTEXT,
        width: combo_width
    });
    
    var newZoomComboBox = new Ext.form.ComboBox({
        id: 'newzoom_cb',
        editable: false,
        emptyText: 'Required',
        displayField: 'value',
        valueField: 'value',
        width: combo_width,
        minListWidth: combo_width + 26,
        triggerAction: 'all',
        mode: 'local',
        value: 7,
        store: new Ext.data.SimpleStore({
            fields: ['value'],
            data: [[5], [6], [7], [8], [9]]
        })
    });
    
    var editZoomComboBox = new Ext.form.ComboBox({
        id: 'editzoom_cb',
        editable: false,
        emptyText: '',
        displayField: 'value',
        valueField: 'value',
        width: combo_width,
        minListWidth: combo_width + 26,
        triggerAction: 'all',
        mode: 'local',
        value: 5,
        store: new Ext.data.SimpleStore({
            fields: ['value'],
            data: [[5], [6], [7], [8], [9]]
        })
    });
    
    var newMapButton = new Ext.Button({
        id: 'newmap_b',
        text: 'Register new map',
        handler: function()
        {
            /*var nm = Ext.getCmp('newmap_cb').getValue();
            var oui = Ext.getCmp('organisationunit_cb').getValue();*/
    
            Ext.Ajax.request({
                url: path + 'getOrganisationUnitsAtLevel' + type,
                method: 'POST',
                params: { level: 1, format: 'json' },

                success: function( responseObject ) {
                    var oui = Ext.util.JSON.decode( responseObject.responseText ).organisationUnits[0].id;
                    var ouli = Ext.getCmp('organisationunitlevel_cb').getValue();
                    var nn = Ext.getCmp('newname_tf').getValue();
                    var mlp = Ext.getCmp('maplayerpath_tf').getValue();
                    var t = Ext.getCmp('type_cb').getValue();
                    var nc = Ext.getCmp('newnamecolumn_tf').getValue();
                    var lon = Ext.getCmp('newlongitude_tf').getValue();
                    var lat = Ext.getCmp('newlatitude_tf').getValue();
                    var zoom = Ext.getCmp('newzoom_cb').getValue();
                     
                    if (!nn || !mlp || !oui || !ouli || !nc || !lon || !lat) {
                        Ext.messageRed.msg('New map', 'Form is not complete.');
                        return;
                    }
                    
                    if (validateInput(nn) == false) {
                        Ext.messageRed.msg('New map', msg_highlight_start + 'Map name' + msg_highlight_end + ' cannot have more than 25 characters.');
                        return;
                    }
                    
                    /*if (!isNumber(lon)) {
                        Ext.messageRed.msg('New map', msg_highlight_start + 'Longitude' + msg_highlight_end + ' must be a number.');
                        return;
                    }
                    else {
                        if (lon < -180 || lon > 180) {
                            Ext.messageRed.msg('New map', msg_highlight_start + 'Longitude' + msg_highlight_end + ' must be between -180 and 180.');
                            return;
                        }
                    }
                    
                    if (!isNumber(lat)) {
                        Ext.messageRed.msg('New map', msg_highlight_start + 'Latitude' + msg_highlight_end + ' must be a number.');
                        return;
                    }
                    else {
                        if (lat < -90 || lat > 90) {
                            Ext.messageRed.msg('New map', msg_highlight_start + 'Latitude' + msg_highlight_end + ' must be between -90 and 90.');
                            return;
                        }
                    }*/

                    Ext.Ajax.request({
                        url: path + 'getAllMaps' + type,
                        method: 'GET',
                        success: function( responseObject ) {
                            var maps = Ext.util.JSON.decode(responseObject.responseText).maps;
                            for (var i = 0; i < maps.length; i++) {
                                if (maps[i].name == nn) {
                                    Ext.messageRed.msg('New map', 'There is already a map called ' + msg_highlight_start + nn + msg_highlight_end + '.');
                                    return;
                                }
                                else if (maps[i].mapLayerPath == mlp) {
                                    Ext.messageRed.msg('New map', 'There is already a map with ' + msg_highlight_start + mlp + msg_highlight_end + ' as source file.');
                                    return;
                                }
                            }
                            
                            Ext.Ajax.request({
                                url: path + 'addOrUpdateMap' + type,
                                method: 'GET',
                                params: { name: nn, mapLayerPath: mlp, type: t, organisationUnitId: oui, organisationUnitLevelId: ouli, nameColumn: nc, longitude: lon, latitude: lat, zoom: zoom},

                                success: function( responseObject ) {
                                    Ext.messageBlack.msg('New map', 'The map ' + msg_highlight_start + nn + msg_highlight_end + ' was registered.');
                                    
                                    Ext.getCmp('map_cb').getStore().reload();
                                    Ext.getCmp('maps_cb').getStore().reload();
                                    Ext.getCmp('editmap_cb').getStore().reload();
                                    Ext.getCmp('deletemap_cb').getStore().reload();
                                    
                                    Ext.getCmp('organisationunitlevel_cb').reset();
                                    Ext.getCmp('newname_tf').reset();
                                    Ext.getCmp('maplayerpath_tf').reset();
                                    Ext.getCmp('newnamecolumn_tf').reset();
                                    Ext.getCmp('newlongitude_tf').reset();
                                    Ext.getCmp('newlatitude_tf').reset();
                                    Ext.getCmp('newzoom_cb').reset();                                    
                                },
                                failure: function() {
                                    alert( 'Error: addOrUpdateMap' );
                                }
                            });
                        },
                        failure: function() {
                            alert( 'Error: getAllMaps' );
                        }
                    });
                },
                failure: function() {
                    alert( 'Error: getOrganisationUnitsAtLevel' );
                }
            });
        }
    });
    
    var editMapButton = new Ext.Button({
        id: 'editmap_b',
        text: 'Save changes',
        handler: function() {
            var en = Ext.getCmp('editname_tf').getValue();
            var em = Ext.getCmp('editmap_cb').getValue();
            var nc = Ext.getCmp('editnamecolumn_tf').getValue();
            var lon = Ext.getCmp('editlongitude_tf').getValue();
            var lat = Ext.getCmp('editlatitude_tf').getValue();
            var zoom = Ext.getCmp('editzoom_cb').getValue();
            
            if (!en || !em || !nc || !lon || !lat) {
                Ext.messageRed.msg('New map', 'Form is not complete.');
                return;
            }
            
            if (validateInput(en) == false) {
                Ext.messageRed.msg('New map', 'Map name cannot be longer than 25 characters.');
                return;
            }
           
            Ext.Ajax.request({
                url: path + 'addOrUpdateMap' + type,
                method: 'GET',
                params: { name: en, mapLayerPath: em, nameColumn: nc, longitude: lon, latitude: lat, zoom: zoom },

                success: function( responseObject ) {
                    Ext.messageBlack.msg('Edit map', 'The map ' + msg_highlight_start + en + msg_highlight_end + ' was updated.');
                    
                    Ext.getCmp('map_cb').getStore().reload();
                    Ext.getCmp('maps_cb').getStore().reload();
                    Ext.getCmp('editmap_cb').getStore().reload();
                    Ext.getCmp('editmap_cb').reset();
                    Ext.getCmp('deletemap_cb').getStore().reload();
                    Ext.getCmp('deletemap_cb').reset();
                    
                    Ext.getCmp('editmap_cb').reset();
                    Ext.getCmp('editname_tf').reset();
                    Ext.getCmp('editnamecolumn_tf').reset();
                    Ext.getCmp('editlongitude_tf').reset();
                    Ext.getCmp('editlatitude_tf').reset();
                    Ext.getCmp('editzoom_cb').reset();
                },
                failure: function() {
                    alert( 'Status', 'Error while saving data' );
                }
            });
        }
    });
    
    var deleteMapButton = new Ext.Button({
        id: 'deletemap_b',
        text: 'Delete map',
        handler: function() {
            var mlp = Ext.getCmp('deletemap_cb').getValue();
            
            if (!mlp) {
                Ext.messageRed.msg('Delete map', 'Please select a map.');
                return;
            }
            
            Ext.Ajax.request({
                url: path + 'deleteMap' + type,
                method: 'GET',
                params: { mapLayerPath: mlp },

                success: function( responseObject ) {
                    Ext.messageBlack.msg('Edit map', 'The map ' + msg_highlight_start + mlp + msg_highlight_end + ' was deleted.');
                    
                    Ext.getCmp('map_cb').getStore().reload();
					
					if (Ext.getCmp('map_cb').getValue() == mlp) {
						Ext.getCmp('map_cb').reset();
					}
					
                    Ext.getCmp('maps_cb').getStore().reload();
                    Ext.getCmp('editmap_cb').getStore().reload();
                    Ext.getCmp('editmap_cb').reset();
                    Ext.getCmp('deletemap_cb').getStore().reload();
                    Ext.getCmp('deletemap_cb').reset();
                    Ext.getCmp('mapview_cb').getStore().reload();
                    Ext.getCmp('mapview_cb').reset();
                },
                failure: function() {
                    alert( 'Status', 'Error while saving data' );
                }
            });
        }
    });
    
    var newMapComboBox = new Ext.form.ComboBox({
        id: 'newmap_cb',
        typeAhead: true,
        editable: false,
        valueField: 'level',
        displayField: 'name',
        emptyText: MENU_EMPTYTEXT,
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        selectOnFocus: true,
        width: combo_width,
        minListWidth: combo_width + 26,
        store: organisationUnitLevelStore,
        listeners: {
            'select': {
                fn: function() {
                    var level = Ext.getCmp('newmap_cb').getValue();
                    organisationUnitStore.baseParams = { level: level, format: 'json' };
                    organisationUnitStore.reload();
                },
                scope: this
            }
        }
    });
    
    var editMapComboBox = new Ext.form.ComboBox({
        id: 'editmap_cb',
        typeAhead: true,
        editable: false,
        valueField: 'mapLayerPath',
        displayField: 'name',
        emptyText: MENU_EMPTYTEXT,
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        selectOnFocus: true,
        width: combo_width,
        minListWidth: combo_width + 26,
        store: existingMapsStore,
        listeners: {
            'select': {
                fn: function() {
                    var mlp = Ext.getCmp('editmap_cb').getValue();
                    
                    Ext.Ajax.request({
                        url: path + 'getMapByMapLayerPath' + type,
                        method: 'GET',
                        params: { mapLayerPath: mlp, format: 'json' },

                        success: function( responseObject ) {
                            var map = Ext.util.JSON.decode( responseObject.responseText ).map[0];
                            
                            Ext.getCmp('editname_tf').setValue(map.name);
                            Ext.getCmp('editnamecolumn_tf').setValue(map.nameColumn);
                            Ext.getCmp('editlongitude_tf').setValue(map.longitude);
                            Ext.getCmp('editlatitude_tf').setValue(map.latitude);
                            Ext.getCmp('editzoom_cb').setValue(map.zoom);
                        },
                        failure: function() {
                            alert( 'Error while retrieving data: getAssignOrganisationUnitData' );
                        } 
                    });
                },
                scope: this
            }
        }
    });
    
    var deleteMapComboBox = new Ext.form.ComboBox({
        xtype: 'combo',
        id: 'deletemap_cb',
        typeAhead: true,
        editable: false,
        valueField: 'mapLayerPath',
        displayField: 'name',
        emptyText: MENU_EMPTYTEXT,
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        selectOnFocus: true,
        width: combo_width,
        minListWidth: combo_width + 26,
        store: existingMapsStore
    });
    
    var newMapPanel = new Ext.Panel({   
        id: 'newmap_p',
        items:
        [   
            /*{ html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Map type</p>' }, typeComboBox, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Organisation unit level</p>' }, newMapComboBox, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Organisation unit</p>' }, multi, { html: '<br>' },*/
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Organisation unit level</p>' }, organisationUnitLevelComboBox, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Map source file</p>' }, mapLayerPathTextField, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Display name</p>' }, newNameTextField, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Name column</p>' }, newNameColumnTextField, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Longitude (x)</p>' }, newLongitudeTextField, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Latitude (y)</p>' }, newLatitudeTextField, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Zoom</p>' }, newZoomComboBox
        ]
    });
    
    var editMapPanel = new Ext.Panel({
        id: 'editmap_p',
        items: [
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Map</p>' }, editMapComboBox, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Display name</p>' }, editNameTextField, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Name column</p>' }, editNameColumnTextField, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Longitude</p>' }, editLongitudeTextField, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Latitude</p>' }, editLatitudeTextField, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Zoom</p>' }, editZoomComboBox
        ]
    });
    
    var deleteMapPanel = new Ext.Panel({
        id: 'deletemap_p',
        items: [
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Map</p>' }, deleteMapComboBox
        ]
    });

    shapefilePanel = new Ext.Panel({
        id: 'shapefile_p',
        title: '<font style="font-family:tahoma; font-weight:normal; font-size:11px; color:' + MENU_TITLECOLOR_LIGHT + ';">Register maps</font>',
        items:
        [
            {
                xtype: 'tabpanel',
                activeTab: 0,
                deferredRender: false,
                plain: true,
                defaults: {layout: 'fit', bodyStyle: 'padding:8px'},
                listeners: {
                    tabchange: function(panel, tab) {
                        var nm_b = Ext.getCmp('newmap_b');
                        var em_b = Ext.getCmp('editmap_b');
                        var dm_b = Ext.getCmp('deletemap_b');
                        
                        if (tab.id == 'map0')
                        { 
                            nm_b.setVisible(true);
                            em_b.setVisible(false);
                            dm_b.setVisible(false);
                        }
                        
                        else if (tab.id == 'map1')
                        {
                            nm_b.setVisible(false);
                            em_b.setVisible(true);
                            dm_b.setVisible(false);
                        }
                        
                        else if (tab.id == 'map2')
                        {
                            nm_b.setVisible(false);
                            em_b.setVisible(false);
                            dm_b.setVisible(true);
                        }
                    }
                },
                items:
                [
                    {
                        title:'New map',
                        id: 'map0',
                        items:
                        [
                            newMapPanel
                        ]
                    },
                    {
                        title:'Edit map',
                        id: 'map1',
                        items:
                        [
                            editMapPanel
                        ]
                    },
                    {
                        title:'Delete map',
                        id: 'map2',
                        items:
                        [
                            deleteMapPanel
                        ]
                    }
                ]
            },
            { html: '<br>' },
            
            newMapButton,
            
            editMapButton,
            
            deleteMapButton
        ]
    });
    
    /*LEGEND SET PANEL*/
    
    var legendSetNameTextField = new Ext.form.TextField({
        id: 'legendsetname_tf',
        emptyText: MENU_EMPTYTEXT,
        width: combo_width
    });
    
    var legendSetMethodComboBox = new Ext.form.ComboBox({
        id: 'legendsetmethod_cb',
        editable: false,
        valueField: 'value',
        displayField: 'text',
        mode: 'local',
        emptyText: MENU_EMPTYTEXT,
        triggerAction: 'all',
        width: combo_width,
        minListWidth: combo_width + 26,
        store: new Ext.data.SimpleStore({
            fields: ['value', 'text'],
            data: [[2, 'Distributed values'], [1, 'Equal intervals']]
        })
    });
    
    var legendSetClassesComboBox = new Ext.form.ComboBox({
        id: 'legendsetclasses_cb',
        editable: false,
        valueField: 'value',
        displayField: 'value',
        mode: 'local',
        emptyText: MENU_EMPTYTEXT,
        triggerAction: 'all',
        width: combo_width,
        minListWidth: combo_width + 26,
        store: new Ext.data.SimpleStore({
            fields: ['value'],
            data: [[1], [2], [3], [4], [5], [6], [7], [8]]
        })
    });
    
    var legendSetLowColorColorPalette = new Ext.ux.ColorField({
        id: 'legendsetlowcolor_cp',
        allowBlank: false,
        width: combo_width,
        minListWidth: combo_width + 26,
        value: "#FFFF00"
    });
    
    var legendSetHighColorColorPalette = new Ext.ux.ColorField({
        id: 'legendsethighcolor_cp',
        allowBlank: false,
        width: combo_width,
        minListWidth: combo_width + 26,
        value: "#FF0000"
    });

    var legendSetIndicatorStore = new Ext.data.JsonStore({
        url: path + 'getAllIndicators' + type,
        root: 'indicators',
        fields: ['id', 'name', 'shortName'],
        sortInfo: { field: 'name', direction: 'ASC' },
        autoLoad: true
    });
    
    var legendSetIndicatorMultiSelect = new Ext.ux.Multiselect({
        id: 'legendsetindicator_ms',
        dataFields: ['id', 'name', 'shortName'], 
        valueField: 'id',
        displayField: 'shortName',
        width: gridpanel_width - 25,
        height: getMultiSelectHeight(),
        store: legendSetIndicatorStore
    });
    
    var legendSetStore = new Ext.data.JsonStore({
        url: path + 'getAllMapLegendSets' + type,
        root: 'mapLegendSets',
        fields: ['id', 'name'],
        sortInfo: { field: 'name', direction: 'ASC' },
        autoLoad: true
    });
    
    var legendSetComboBox = new Ext.form.ComboBox({
        id: 'legendset_cb',
        typeAhead: true,
        editable: false,
        valueField: 'id',
        displayField: 'name',
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        emptyText: MENU_EMPTYTEXT,
        selectOnFocus: true,
        width: combo_width,
        minListWidth: combo_width + 26,
        store: legendSetStore
    });
    
    var newLegendSetButton = new Ext.Button({
        id: 'newlegendset_b',
        text: 'Register new legend set',
        handler: function() {
            var ln = Ext.getCmp('legendsetname_tf').getValue();
/*            var lm = Ext.getCmp('legendsetmethod_cb').getValue();*/
            var lc = Ext.getCmp('legendsetclasses_cb').getValue();            
            var llc = Ext.getCmp('legendsetlowcolor_cp').getValue();
            var lhc = Ext.getCmp('legendsethighcolor_cp').getValue();
/*            var li = Ext.getCmp('legendsetindicator_cb').getValue();*/
            var lims = Ext.getCmp('legendsetindicator_ms').getValue();
            
            if (!lc || !ln || !lims) {
                Ext.messageRed.msg('New legend set', 'Form is not complete.');
                return;
            }
            
            if (validateInput(ln) == false) {
                Ext.messageRed.msg('New legend set', 'Legend set name cannot be longer than 25 characters.');
                return;
            }
            
            var array = new Array();
            array = lims.split(',');
            var params = '?indicators=' + array[0];
            
            for (var i = 1; i < array.length; i++) {
                array[i] = '&indicators=' + array[i];
                params += array[i];
            }
            
            Ext.Ajax.request({
                url: path + 'addOrUpdateMapLegendSet.action' + params,
                method: 'POST',
                params: { name: ln, method: 2, classes: lc, colorLow: llc, colorHigh: lhc },

                success: function( responseObject ) {
                    Ext.messageBlack.msg('New legend set', 'The legend set ' + msg_highlight_start + ln + msg_highlight_end + ' was registered.');
                    Ext.getCmp('legendset_cb').getStore().reload();
                },
                failure: function() {
                    alert( 'Status', 'Error while saving data' );
                }
            });
        }
    });
    
    var deleteLegendSetButton = new Ext.Button({
        id: 'deletelegendset_b',
        text: 'Delete legend set',
        handler: function() {
            var ls = Ext.getCmp('legendset_cb').getValue();
            var lsrw = Ext.getCmp('legendset_cb').getRawValue();
            
            if (!ls) {
                Ext.messageRed.msg('Delete legend set', 'Please select a legend set.');
                return;
            }
            
            Ext.Ajax.request({
                url: path + 'deleteMapLegendSet' + type,
                method: 'GET',
                params: { id: ls },

                success: function( responseObject ) {
                    Ext.messageBlack.msg('Delete legend set', 'The legend set ' + msg_highlight_start + lsrw + msg_highlight_end + ' was deleted.');
                    
                    Ext.getCmp('legendset_cb').getStore().reload();
                    Ext.getCmp('legendset_cb').reset();
                },
                failure: function() {
                    alert( 'Status', 'Error while saving data' );
                }
            });
        }
    });
    
    var newLegendSetPanel = new Ext.Panel({   
        id: 'newlegendset_p',
        items:
        [   
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Display name</p>' }, legendSetNameTextField, { html: '<br>' },
/*            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Method</p>' }, legendSetMethodComboBox, { html: '<br>' },*/
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Classes</p>' }, legendSetClassesComboBox, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Lowest value color</p>' }, legendSetLowColorColorPalette, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Highest value color</p>' }, legendSetHighColorColorPalette, { html: '<br>' },
/*            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Indicator group</p>' }, legendSetIndicatorGroupComboBox, { html: '<br>' },*/
/*            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Indicator</p>' }, legendSetIndicatorComboBox*/
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Indicators</p>' }, legendSetIndicatorMultiSelect
        ]
    });
    
    var deleteLegendSetPanel = new Ext.Panel({   
        id: 'deletelegendset_p',
        items:
        [   
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Legend set</p>' }, legendSetComboBox
        ]
    });

    var legendsetPanel = new Ext.Panel({
        id: 'legendset_p',
        title: '<font style="font-family:tahoma; font-weight:normal; font-size:11px; color:' + MENU_TITLECOLOR_LIGHT + ';">Register legend sets</font>',
        items:
        [
            {
                xtype: 'tabpanel',
                activeTab: 0,
                deferredRender: false,
                plain: true,
                defaults: {layout: 'fit', bodyStyle: 'padding:8px'},
                listeners: {
                    tabchange: function(panel, tab) {
                        var nl_b = Ext.getCmp('newlegendset_b');
                        var dl_b = Ext.getCmp('deletelegendset_b');
                        
                        if (tab.id == 'legendset0') { 
                            nl_b.setVisible(true);
                            dl_b.setVisible(false);
                        }
                        else if (tab.id == 'legendset1') {
                            nl_b.setVisible(false);
                            dl_b.setVisible(true);
                        }
                    }
                },
                items:
                [
                    {
                        title:'New legend set',
                        id: 'legendset0',
                        items:
                        [
                            newLegendSetPanel
                        ]
                    },
                    {
                        title:'Delete legend set',
                        id: 'legendset1',
                        items:
                        [
                            deleteLegendSetPanel
                        ]
                    }
                ]
            },
            { html: '<br>' },
            
            newLegendSetButton,
            
            deleteLegendSetButton
        ]
    });
    
    /*VIEW PANEL*/
    
    var viewNameTextField = new Ext.form.TextField({
        id: 'viewname_tf',
        emptyText: '',
        width: combo_width
    });
    
    var viewStore = new Ext.data.JsonStore({
        url: path + 'getAllMapViews' + type,
        root: 'mapViews',
        fields: ['id', 'name'],
        id: 'id',
        sortInfo: { field: 'name', direction: 'ASC' },
        autoLoad: true
    });
    
    var viewComboBox = new Ext.form.ComboBox({
        id: 'view_cb',
        typeAhead: true,
        editable: false,
        valueField: 'id',
        displayField: 'name',
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        emptyText: MENU_EMPTYTEXT,
        selectOnFocus: true,
        width: combo_width,
        minListWidth: combo_width + 26,
        store: viewStore
    });
    
    var view2ComboBox = new Ext.form.ComboBox({
        id: 'view2_cb',
        typeAhead: true,
        editable: false,
        valueField: 'id',
        displayField: 'name',
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        emptyText: MENU_EMPTYTEXT,
        selectOnFocus: true,
        width: combo_width,
        minListWidth: combo_width + 26,
        store: viewStore
    });
    
    var newViewButton = new Ext.Button({
        id: 'newview_b',
        text: 'Register new view',
        handler: function() {
            var vn = Ext.getCmp('viewname_tf').getValue();
            var ig = Ext.getCmp('indicatorgroup_cb').getValue();
            var ii = Ext.getCmp('indicator_cb').getValue();
            var pt = Ext.getCmp('periodtype_cb').getValue();
            var p = Ext.getCmp('period_cb').getValue();
            var mst = MAPSOURCE;
            var ms = Ext.getCmp('map_cb').getValue();
            var c = Ext.getCmp('numClasses').getValue();
            var ca = Ext.getCmp('colorA_cf').getValue();
            var cb = Ext.getCmp('colorB_cf').getValue();
            
			if (!vn) {
                Ext.messageRed.msg('New map view', 'Map view form is not complete.');
                return;
            }
			
            if (!ig || !ii || !pt || !p || !mst || !ms || !c ) {
                Ext.messageRed.msg('New map view', 'Thematic map form is not complete.');
                return;
            }
            
            if (validateInput(vn) == false) {
                Ext.messageRed.msg('New map view', 'Map view name cannot be longer than 25 characters.');
                return;
            }
            
            Ext.Ajax.request({
                url: path + 'getAllMapViews' + type,
                method: 'GET',
                success: function( responseObject ) {
                    var mapViews = Ext.util.JSON.decode( responseObject.responseText ).mapViews;
                    
                    for (var i = 0; i < mapViews.length; i++) {
                        if (mapViews[i].name == vn) {
                            Ext.messageRed.msg('New map view', 'There is already a map view called ' + msg_highlight_start + vn + msg_highlight_end + '.');
                            return;
                        }
                    }
            
                    Ext.Ajax.request({
                        url: path + 'addOrUpdateMapView' + type,
                        method: 'POST',
                        params: { name: vn, indicatorGroupId: ig, indicatorId: ii, periodTypeId: pt, periodId: p, mapSourceType: mst, mapSource: ms, method: 2, classes: c, colorLow: ca, colorHigh: cb },

                        success: function( responseObject ) {
                            Ext.messageBlack.msg('New map view', 'The view ' + msg_highlight_start + vn + msg_highlight_end + ' was registered.');
                            Ext.getCmp('view_cb').getStore().reload();
                            Ext.getCmp('mapview_cb').getStore().reload();
                            Ext.getCmp('viewname_tf').reset();
                        },
                        failure: function() {
                            alert( 'Error: addOrUpdateMapView' );
                        }
                    });
                },
                failure: function() {
                            alert( 'Error: getAllMapViews' );
                }
            });
        }
    });
    
    var deleteViewButton = new Ext.Button({
        id: 'deleteview_b',
        text: 'Delete view',
        handler: function() {
            var v = Ext.getCmp('view_cb').getValue();
            var name = Ext.getCmp('view_cb').getStore().getById(v).get('name');
            
            if (!v) {
                Ext.messageRed.msg('Delete map view', 'Please select a map view.');
                return;
            }
            
            Ext.Ajax.request({
                url: path + 'deleteMapView' + type,
                method: 'POST',
                params: { id: v },

                success: function( responseObject ) {
                    Ext.messageBlack.msg('Delete map view', 'The map view ' + msg_highlight_start + name + msg_highlight_end + ' was deleted.');
                    Ext.getCmp('view_cb').getStore().reload();
                    Ext.getCmp('view_cb').reset();
                    Ext.getCmp('mapview_cb').getStore().reload();
                },
                failure: function() {
                    alert( 'Status', 'Error while saving data' );
                }
            });
        }
    });
    
    var dashboardViewButton = new Ext.Button({
        id: 'dashboardview_b',
        text: 'Add view to DHIS 2 dashboard',
        handler: function() {
            var v2 = Ext.getCmp('view2_cb').getValue();
			var nv = Ext.getCmp('view2_cb').getRawValue();
            
            if (!v2) {
                Ext.messageRed.msg('Dashboard map view', 'Please select a map view.');
                return;
            }
            
            Ext.Ajax.request({
                url: path + 'addMapViewToDashboard' + type,
                method: 'POST',
                params: { id: v2 },

                success: function( responseObject ) {
                    Ext.messageBlack.msg('Dashboard map view', 'The view ' + msg_highlight_start + nv + msg_highlight_end + ' was added to dashboard.');
                    
                    Ext.getCmp('view_cb').getStore().reload();
                    Ext.getCmp('view_cb').reset();
                    Ext.getCmp('mapview_cb').getStore().reload();
                },
                failure: function() {
                    alert( 'Status', 'Error while saving data' );
                }
            });
        }
    });
    
    var newViewPanel = new Ext.Panel({   
        id: 'newview_p',
        items:
        [
            { html: '<font color="' + MENU_TEXTCOLOR_INFO + '">Saving current thematic map selection.</font>' }, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Display name</p>' }, viewNameTextField
        ]
    });
    
    var deleteViewPanel = new Ext.Panel({   
        id: 'deleteview_p',
        items:
        [   
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;View</p>' }, viewComboBox
        ]
    });
    
    var dashboardViewPanel = new Ext.Panel({   
        id: 'dashboardview_p',
        items:
        [   
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;View</p>' }, view2ComboBox
        ]
    });
    
    var viewPanel = new Ext.Panel({
        id: 'view_p',
        title: '<font style="font-family:tahoma; font-weight:normal; font-size:11px; color:' + MENU_TITLECOLOR_LIGHT + ';">Register views</font>',
        items:
        [
            {
                xtype: 'tabpanel',
                activeTab: 0,
                deferredRender: false,
                plain: true,
                defaults: {layout: 'fit', bodyStyle: 'padding:8px'},
                listeners: {
                    tabchange: function(panel, tab)
                    {
                        var nv_b = Ext.getCmp('newview_b');
                        var dv_b = Ext.getCmp('deleteview_b');
                        var dbv_b = Ext.getCmp('dashboardview_b');
                        
                        if (tab.id == 'view0')
                        { 
                            nv_b.setVisible(true);
                            dv_b.setVisible(false);
                            dbv_b.setVisible(false);
                        }
                        
                        else if (tab.id == 'view1')
                        {
                            nv_b.setVisible(false);
                            dv_b.setVisible(true);
                            dbv_b.setVisible(false);
                        }
                        
                        else if (tab.id == 'view2')
                        {
                            nv_b.setVisible(false);
                            dv_b.setVisible(false);
                            dbv_b.setVisible(true);
                        }
                    }
                },
                items:
                [
                    {
                        title:'New view',
                        id: 'view0',
                        items:
                        [
                            newViewPanel
                        ]
                    },
                    
                    {
                        title:'Delete view',
                        id: 'view1',
                        items:
                        [
                            deleteViewPanel
                        ]
                    },
                    
                    {
                        title:'Dashboard view',
                        id: 'view2',
                        items:
                        [
                            dashboardViewPanel
                        ]
                    }
                ]
            },
            
            { html: '<br>' },
            
            newViewButton,
            
            deleteViewButton,
            
            dashboardViewButton
        ]
    });
    
    /*OVERLAY PANEL*/
    
    var mapLayerNameTextField = new Ext.form.TextField({
        id: 'maplayername_tf',
        emptyText: MENU_EMPTYTEXT,
        width: combo_width
    });
    
    var mapLayerMapSourceFileTextField = new Ext.form.TextField({
        id: 'maplayermapsourcefile_tf',
        emptyText: MENU_EMPTYTEXT,
        width: combo_width
    });
    
    var mapLayerFillColorColorField = new Ext.ux.ColorField({
        id: 'maplayerfillcolor_cf',
        allowBlank: false,
        width: combo_width,
        value: '#FF0000'
    });
    
    var mapLayerFillOpacityComboBox = new Ext.form.ComboBox({
        id: 'maplayerfillopacity_cb',
        editable: true,
        valueField: 'value',
        displayField: 'value',
        mode: 'local',
        triggerAction: 'all',
        width: combo_width,
        minListWidth: combo_width + 26,
        value: 0.5,
        store: new Ext.data.SimpleStore({
            fields: ['value'],
            data: [[0.0], [0.1], [0.2], [0.3], [0.4], [0.5], [0.6], [0.7], [0.8], [0.9], [1.0]]
        })
    });
    
    var mapLayerStrokeColorColorField = new Ext.ux.ColorField({
        id: 'maplayerstrokecolor_cf',
        allowBlank: false,
        width: combo_width,
        value: '#222222'
    });
    
    var mapLayerStrokeWidthComboBox = new Ext.form.ComboBox({
        id: 'maplayerstrokewidth_cb',
        editable: true,
        valueField: 'value',
        displayField: 'value',
        mode: 'local',
        triggerAction: 'all',
        width: combo_width,
        minListWidth: combo_width + 26,
        value: 2,
        store: new Ext.data.SimpleStore({
            fields: ['value'],
            data: [[0], [1], [2], [3], [4]]
        })
    });
    
    var mapLayerStore = new Ext.data.JsonStore({
        url: path + 'getAllMapLayers' + type,
        root: 'mapLayers',
        fields: ['id', 'name'],
        sortInfo: { field: 'name', direction: 'ASC' },
        autoLoad: true
    });
    
    var mapLayerComboBox = new Ext.form.ComboBox({
        id: 'maplayer_cb',
        typeAhead: true,
        editable: false,
        valueField: 'id',
        displayField: 'name',
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        emptyText: MENU_EMPTYTEXT,
        selectOnFocus: true,
        width: combo_width,
        minListWidth: combo_width + 26,
        store: mapLayerStore
    });
    
    var newMapLayerButton = new Ext.Button({
        id: 'newmaplayer_b',
        text: 'Register new overlay',
        handler: function() {
            var mln = Ext.getCmp('maplayername_tf').getRawValue();
            var mlmsf = Ext.getCmp('maplayermapsourcefile_tf').getValue();
            var mlfc = Ext.getCmp('maplayerfillcolor_cf').getValue();
            var mlfo = Ext.getCmp('maplayerfillopacity_cb').getValue();
            var mlsc = Ext.getCmp('maplayerstrokecolor_cf').getValue();
            var mlsw = Ext.getCmp('maplayerstrokewidth_cb').getValue();
            
            if (!mln || !mlmsf ) {
                Ext.messageRed.msg('New overlay', 'Overlay form is not complete.');
                return;
            }
            
            if (validateInput(mln) == false) {
                Ext.messageRed.msg('New overlay', 'Overlay name cannot be longer than 25 characters.');
                return;
            }
            
            Ext.Ajax.request({
                url: path + 'addOrUpdateMapLayer' + type,
                method: 'POST',
                params: { name: mln, type: 'overlay', mapSource: mlmsf, fillColor: mlfc, fillOpacity: mlfo, strokeColor: mlsc, strokeWidth: mlsw },

                success: function( responseObject ) {
                    Ext.messageBlack.msg('New overlay', 'The overlay ' + msg_highlight_start + mln + msg_highlight_end + ' was registered.');
                    Ext.getCmp('maplayer_cb').getStore().reload();
                },
                failure: function() {
                    alert( 'Status', 'Error while saving data' );
                }
            });
            
            map.addLayer(
                new OpenLayers.Layer.Vector(mln, {
                    'visibility': false,
                    'styleMap': new OpenLayers.StyleMap({
                        'default': new OpenLayers.Style(
                            OpenLayers.Util.applyDefaults(
                                {'fillColor': mlfc, 'fillOpacity': mlfo, 'strokeColor': mlsc, 'strokeWidth': mlsw},
                                OpenLayers.Feature.Vector.style['default']
                            )
                        )
                    }),
                    'strategies': [new OpenLayers.Strategy.Fixed()],
                    'protocol': new OpenLayers.Protocol.HTTP({
                        'url': GEOJSON_URL + mlmsf,
                        'format': new OpenLayers.Format.GeoJSON()
                    })
                })
            );
            
            Ext.getCmp('maplayername_tf').reset();
            Ext.getCmp('maplayermapsourcefile_tf').reset();
        }
    });
    
    var deleteMapLayerButton = new Ext.Button({
        id: 'deletemaplayer_b',
        text: 'Delete overlay',
        handler: function() {
            var ml = Ext.getCmp('maplayer_cb').getValue();
            var mln = Ext.getCmp('maplayer_cb').getRawValue();
            
            if (!ml) {
                Ext.messageRed.msg('Delete overlay', 'Please select an overlay.');
                return;
            }
            
            Ext.Ajax.request({
                url: path + 'deleteMapLayer' + type,
                method: 'POST',
                params: { id: ml },

                success: function( responseObject ) {
                    Ext.messageBlack.msg('Delete overlay', 'The overlay ' + msg_highlight_start + mln + msg_highlight_end + ' was deleted.');
                    Ext.getCmp('maplayer_cb').getStore().reload();
                    Ext.getCmp('maplayer_cb').reset();
                },
                failure: function() {
                    alert( 'Status', 'Error while saving data' );
                }
            });
            
            map.getLayersByName(mln)[0].destroy();
        }
    });
    
    var newMapLayerPanel = new Ext.Panel({   
        id: 'newmaplayer_p',
        items:
        [
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Display name</p>' }, mapLayerNameTextField, { html: '<br>' }, 
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Map source file</p>' }, mapLayerMapSourceFileTextField, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Fill color</p>' }, mapLayerFillColorColorField, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Fill opacity</p>' }, mapLayerFillOpacityComboBox, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Stroke color</p>' }, mapLayerStrokeColorColorField, { html: '<br>' },
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Stroke width</p>' }, mapLayerStrokeWidthComboBox, { html: '<br>' }
        ]
    });
    
    var deleteMapLayerPanel = new Ext.Panel({   
        id: 'deletemaplayer_p',
        items:
        [   
            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Overlay</p>' }, mapLayerComboBox
        ]
    });
    
    var mapLayerPanel = new Ext.Panel({
        id: 'maplayer_p',
        title: '<font style="font-family:tahoma; font-weight:normal; font-size:11px; color:' + MENU_TITLECOLOR_LIGHT + ';">Register overlays</font>',
        items:
        [
            {
                xtype: 'tabpanel',
                activeTab: 0,
                deferredRender: false,
                plain: true,
                defaults: {layout: 'fit', bodyStyle: 'padding:8px'},
                listeners: {
                    tabchange: function(panel, tab)
                    {
                        var nml_b = Ext.getCmp('newmaplayer_b');
                        var dml_b = Ext.getCmp('deletemaplayer_b');
                        
                        if (tab.id == 'maplayer0') { 
                            nml_b.setVisible(true);
                            dml_b.setVisible(false);
                        }
                        else if (tab.id == 'maplayer1') {
                            nml_b.setVisible(false);
                            dml_b.setVisible(true);
                        }
                    }
                },
                items:
                [
                    {
                        title:'New overlay',
                        id: 'maplayer0',
                        items:
                        [
                            newMapLayerPanel
                        ]
                    },
                    {
                        title:'Delete overlay',
                        id: 'maplayer1',
                        items:
                        [
                            deleteMapLayerPanel
                        ]
                    }
                ]
            },
            
            { html: '<br>' },
            
            newMapLayerButton,
            
            deleteMapLayerButton
        ]
    });
    
    /*ADMIN PANEL*/
    
    var adminPanel = new Ext.form.FormPanel({
        id: 'admin_p',
        title: '<font style="font-family:tahoma; font-weight:normal; font-size:11px; color:' + MENU_TITLECOLOR_ADMIN + ';">Administrator</font>',
        items:
        [   
            {
                xtype: 'combo',
                fieldLabel: 'Map source',
				labelSeparator: MENU_LABELSEPARATOR,
                id: 'mapsource_cb',
                editable: false,
                valueField: 'id',
                displayField: 'text',
                mode: 'local',
                triggerAction: 'all',
                width: 133,
                minListWidth: combo_width,
                store: new Ext.data.SimpleStore({
                    fields: ['id', 'text'],
                    data: [['database', 'DHIS database'], ['shapefile', 'Shapefile']]
                }),
                listeners:{
                    'select': {
                        fn: function() {
                            var msv = Ext.getCmp('mapsource_cb').getValue();
                            var msrw = Ext.getCmp('mapsource_cb').getRawValue();
                            
                            Ext.Ajax.request({
                                url: path + 'getMapSourceTypeUserSetting' + type,
                                method: 'POST',

                                success: function( responseObject ) {
                                    if (Ext.util.JSON.decode(responseObject.responseText).mapSource == msv) {
                                        Ext.messageRed.msg('Map source', msg_highlight_start + msrw + msg_highlight_end + ' is already selected.');
                                    }
                                    else {
                                        Ext.Ajax.request({
                                            url: path + 'setMapSourceTypeUserSetting' + type,
                                            method: 'POST',
                                            params: { mapSourceType: msv },

                                            success: function( responseObject ) {
                                                Ext.messageBlack.msg('Map source', msg_highlight_start + msrw + msg_highlight_end + ' is saved as map source.');

                                                MAPSOURCE = msv;
                                                
                                                Ext.getCmp('map_cb').getStore().reload();
                                                Ext.getCmp('maps_cb').getStore().reload();
                                                Ext.getCmp('mapview_cb').getStore().reload();

                                                Ext.getCmp('map_cb').reset();
                                                Ext.getCmp('mapview_cb').reset();
                                                
                                                if (MAPSOURCE == 'shapefile') {
                                                    Ext.getCmp('register_chb').enable();
                                                }
                                                else if (MAPSOURCE == 'database') {
                                                    Ext.getCmp('register_chb').disable();
                                                }
                                            },
                                            failure: function() {
                                                alert( 'Status', 'Error while saving data' );
                                            }
                                        });
                                    }
                                },
                                failure: function() {
                                    alert( 'Status', 'Error while saving data' );
                                }
                            });
                        }
                    }
                }
            },
            {
                xtype: 'checkbox',
                id: 'register_chb',
                fieldLabel: 'Admin panels',
				labelSeparator: MENU_LABELSEPARATOR,
                isFormField: true,
                listeners: {
                    'check': {
                        fn: function(checkbox,checked) {
                            if (checked) {
                                mapping.show();
                                shapefilePanel.show();
                                mapLayerPanel.show();
                                Ext.getCmp('west').doLayout();
                            }
                            else {
                                mapping.hide();
                                shapefilePanel.hide();
                                mapLayerPanel.hide();
                                Ext.getCmp('west').doLayout();
                            }
                        },
                        scope: this
                    }
                }
            }
        ],
        listeners: {
            expand: {
                fn: function() {
                    Ext.getCmp('mapsource_cb').setValue(MAPSOURCE);
                    
                    if (MAPSOURCE == 'shapefile') {
                        Ext.getCmp('register_chb').enable();
                    }
                    else if (MAPSOURCE == 'database') {
                        Ext.getCmp('register_chb').disable();
                    }
                }
            }
        }
    });
    
    /*WIDGETS*/
    
    choropleth = new mapfish.widgets.geostat.Choropleth({
        id: 'choropleth',
        map: map,
        layer: choroplethLayer,
		title: '<font style="font-family:tahoma; font-weight:normal; font-size:11px; color:' + MENU_TITLECOLOR_LIGHT + ';">Thematic map </font>',
        url: INIT_URL,
        featureSelection: false,
        legendDiv: 'choroplethLegend',
        defaults: {width: 130},
        listeners: {
            expand: {
                fn: function() {
                    choroplethLayer.setVisibility(false);
                    choropleth.classify(false);
                    
                    ACTIVEPANEL = 'choropleth';
                }
            }
        }
    });
    
    mapping = new mapfish.widgets.geostat.Mapping({
        id: 'mapping',
        map: map,
        layer: choroplethLayer,
        title: '<font style="font-family:tahoma; font-weight:normal; font-size:11px; color:' + MENU_TITLECOLOR_LIGHT + ';">Assign organisation units to map</font>',
        url: INIT_URL,
        featureSelection: false,
        legendDiv: 'choroplethLegend',
        defaults: {width: 130},
        listeners: {
            expand: {
                fn: function() {
                    choroplethLayer.setVisibility(false);
                    mapping.classify(false);
                    
                    ACTIVEPANEL = 'mapping';
                }
            }
        }
    });	
    
	map.events.on({
        changelayer: function(e) {
            if (e.property == 'visibility' && e.layer != choroplethLayer) {
                if (e.layer.visibility) {
                    selectFeatureChoropleth.deactivate();
                }
                else {
                    selectFeatureChoropleth.activate();
                }
            }
        }
    });
    
    mapping.hide();
    shapefilePanel.hide();
    mapLayerPanel.hide();
    
    var layerTreeConfig = [{
        nodeType: 'gx_baselayercontainer',
        singleClickExpand: true,
        text: 'Backgrounds'
    }, {
        nodeType: 'gx_overlaylayercontainer',
        singleClickExpand: true
    }, {
        nodeType: 'gx_layer',
        layer: 'Thematic map'
    }];       
    
    var layerTree = new Ext.tree.TreePanel({
        title: 'Map layers',
        enableDD: true,
        bodyStyle: 'padding-bottom:5px;',
        rootVisible: false,
        root: {
            nodeType: 'async',
            children: layerTreeConfig
        }
    });
	
	var zoomInButton = new Ext.Button({
		text: 'Zoom in',
		cls: 'x-btn-text-icon',
		icon: '../images/zoom_in.png',
		handler:function() {
			this.map.zoomIn();
		},
		scope:this,
	});
	
	var zoomOutButton = new Ext.Button({
		text: 'Zoom out',
		cls: 'x-btn-text-icon',
		icon: '../images/zoom_out.png',
		handler:function() {
			this.map.zoomOut();
		},
		scope:this,
	});
	
	var zoomMaxExtentButton = new Ext.Button({
		text: 'Max extent',
		cls: 'x-btn-text-icon',
		icon: '../images/zoom_min.png',
		handler:function() {
			this.map.zoomToMaxExtent();
		},
		scope:this,
	});
	
	var exitButton = new Ext.Button({
		text: 'Exit GIS',
		cls: 'x-btn-text-icon',
		icon: '../images/exit.png',
		handler: function() {
			window.location.href = '../dhis-web-portal/redirect.action'
		}
	});

	var mapToolbar = new Ext.Toolbar({
		id: 'map_tb',
		items: [
			zoomInButton, '-',
			zoomOutButton, '-',
			zoomMaxExtentButton,
			'->',
			exitButton
		]
	});
    
    viewport = new Ext.Viewport({
        id: 'viewport',
        layout: 'border',
        margins: '0 0 5 0',
        items:
        [
            new Ext.BoxComponent(
            {
                region: 'north',
                id: 'north',
                el: 'north',
                height: north_height
            }),
            {
                region: 'east',
                id: 'east',
                collapsible: true,
                width: 200,
                margins: '0 5 0 5',
                defaults: {
                    border: true,
                    frame: true
                },
                layout: 'anchor',
                items:
                [
                    layerTree,
                    {
                        title: 'Overview',
                        html:'<div id="overviewmap" style="height:97px; padding-top:2px;"></div>'
                    },
                    {
                        title: 'Cursor position',
                        height: 65,
                        contentEl: 'position',
                        anchor: '100%',
                        bodyStyle: 'padding-left: 4px;'
                    },
                    {
                        title: 'Map legend',
                        minHeight: 65,
                        autoHeight: true,
                        contentEl: 'legend',
                        anchor: '100%'
                    }
                ]
            },
            {
                region: 'west',
                id: 'west',
                split: true,
				title: 'Map panels',
                collapsible: true,
                width: west_width,
                minSize: 175,
                maxSize: 500,
                margins: '0 0 0 5',
                layout: 'accordion',
                defaults: {
                    border: true,
                    frame: true
                },
                items: [
                    choropleth,
                    viewPanel,
                    legendsetPanel,
                    shapefilePanel,
                    mapping,
                    mapLayerPanel,
                    adminPanel
                ]
            },
            {
                xtype: 'gx_mappanel',
                region: 'center',
                id: 'center',
                height: 1000,
                width: 800,
                map: map,
                title: '',
                zoom: 3,
				tbar: mapToolbar
            }
        ]
    });
    
    map.addControl(new OpenLayers.Control.MousePosition({
        displayClass: 'void', 
        div: $('mouseposition'), 
        prefix: '<font color="' + MENU_TITLECOLOR_LIGHT + '">x: </font>',
        separator: '<br/><font color="' + MENU_TITLECOLOR_LIGHT + '">y: </font>'
    }));

    map.addControl(new OpenLayers.Control.OverviewMap({
        div: $('overviewmap'),
        size: new OpenLayers.Size(180, 95),
        minRectSize: 0
    }));
    
    map.addControl(new OpenLayers.Control.ZoomBox());
	
    Ext.get('loading').fadeOut({remove: true});
});

/*SELECT FEATURES*/

function onHoverSelectChoropleth(feature) {
    var east_panel = Ext.getCmp('east');
    var x = east_panel.x - 210;
    var y = east_panel.y + 41;
    style = '<p style="margin-top: 5px; padding-left:5px;">';
    space = '&nbsp;&nbsp;';
    bs = '<b>';
    be = '</b>';
    lf = '<br>';
    pe = '</p>';
    
    if (MAPDATA != null) {
        if (ACTIVEPANEL == 'choropleth') {
            popup_feature = new Ext.Window({
                title: 'Organisation unit',
                width: 190,
                height: 84,
                layout: 'fit',
                plain: true,
                bodyStyle: 'padding:5px',
                x: x,
                y: y
            });    

            var html = style + feature.attributes[MAPDATA.nameColumn] + pe;
            html += style + bs + 'Value:' + be + space + feature.attributes.value + pe;
            
            popup_feature.html = html;
            popup_feature.show();
        }
        else if (ACTIVEPANEL == 'mapping') {
            popup_feature = new Ext.Window({
                title: 'Organisation unit',
                width: 190,
                height: 84,
                layout: 'fit',
                plain: true,
                bodyStyle: 'padding:5px',
                x: x,
                y: y
            });    

            var html = style + feature.attributes[MAPDATA.nameColumn] + pe;
            
            popup_feature.html = html;
            popup_feature.show();
        }
    }
}

function onHoverUnselectChoropleth(feature) {
    if (MAPDATA != null) {
        popup_feature.hide();
    }
}

function onClickSelectChoropleth(feature) {
    if (ACTIVEPANEL == 'mapping') {
        if (!Ext.getCmp('grid_gp').getSelectionModel().getSelected()) {
            Ext.messageRed.msg('Assign organisation units', 'Please first select an organisation unit in the list.');
            return;
        }
        
        var selected = Ext.getCmp('grid_gp').getSelectionModel().getSelected();
        var organisationUnitId = selected.data['organisationUnitId'];
        var organisationUnit = selected.data['organisationUnit'];
        
        var nameColumn = MAPDATA.nameColumn;
        var mlp = MAPDATA.mapLayerPath;
        var featureId = feature.attributes[nameColumn];
        var name = feature.attributes[nameColumn];

        Ext.Ajax.request({
            url: path + 'addOrUpdateMapOrganisationUnitRelation' + type,
            method: 'GET',
            params: { mapLayerPath: mlp, organisationUnitId: organisationUnitId, featureId: featureId },

            success: function( responseObject ) {
                Ext.messageBlack.msg('Assign organisation units', msg_highlight_start + organisationUnit + msg_highlight_end + ' (database) assigned to ' + msg_highlight_start + name + msg_highlight_end + ' (shapefile).');
                
                Ext.getCmp('grid_gp').getStore().reload();
                loadMapData('assignment');
            },
            failure: function() {
                alert( 'Status', 'Error while retrieving data' );
            } 
        });
        
        popup_feature.hide();
    }
}

function onClickUnselectChoropleth(feature) {}


/*MAP DATA*/

function loadMapData(redirect) {
    Ext.Ajax.request({
        url: path + 'getMapByMapLayerPath' + type,
        method: 'POST',
        params: { mapLayerPath: URL, format: 'json' },

        success: function( responseObject ) {
		
            MAPDATA = Ext.util.JSON.decode(responseObject.responseText).map[0];
            
            if (MAPSOURCE == 'database') {
                MAPDATA.name = Ext.getCmp('map_cb').getRawValue();
                MAPDATA.organisationUnit = 'Country';
                MAPDATA.organisationUnitLevel = Ext.getCmp('map_cb').getValue();
                MAPDATA.unqiueColumn = 'name';
                MAPDATA.nameColumn = 'name';
                MAPDATA.longitude = COUNTRY_LONGITUDE;
                MAPDATA.latitude = COUNTRY_LATITUDE;
                MAPDATA.zoom = COUNTRY_ZOOM;
            }
            else if (MAPSOURCE == 'shapefile') {
                MAPDATA.organisationUnitLevel = parseFloat(MAPDATA.organisationUnitLevel);
                MAPDATA.longitude = parseFloat(MAPDATA.longitude);
                MAPDATA.latitude = parseFloat(MAPDATA.latitude);
                MAPDATA.zoom = parseFloat(MAPDATA.zoom);
            }
           
            map.panTo(new OpenLayers.LonLat(MAPDATA.longitude, MAPDATA.latitude));
			
			if (MAPDATA.zoom != map.getZoom()) {
				map.zoomTo(MAPDATA.zoom);
			}

            if (redirect == 'choropleth') {
                getChoroplethData(); }
            else if (redirect == 'point') {
                getPointData(); }
            else if (redirect == 'assignment') {
                getAssignOrganisationUnitData(); }
            else if (redirect == 'auto-assignment') {
                getAutoAssignOrganisationUnitData(); }
        },
        failure: function() {
            alert( 'Error while retrieving map data: loadMapData' );
        } 
    });
}

/*CHOROPLETH*/
function getChoroplethData() {
	MASK.msg = 'Creating choropleth...';
	MASK.show();
	
    var indicatorId = Ext.getCmp('indicator_cb').getValue();
    var periodId = Ext.getCmp('period_cb').getValue();
    var level = MAPDATA.organisationUnitLevel;

    Ext.Ajax.request({
        url: path + 'getMapValues' + type,
        method: 'POST',
        params: { indicatorId: indicatorId, periodId: periodId, level: level, format: 'json' },

        success: function( responseObject ) {
            dataReceivedChoropleth( responseObject.responseText );
        },
        failure: function() {
            alert( 'Error: getMapValues' );
        } 
    });
}

function dataReceivedChoropleth( responseText ) {
    var layers = this.myMap.getLayersByName(CHOROPLETH_LAYERNAME);
    var features = layers[0].features;
    
    var mapvalues = Ext.util.JSON.decode(responseText).mapvalues;
    
    if (MAPSOURCE == 'database') {
        for (var i=0; i < features.length; i++) {
            for (var j=0; j < mapvalues.length; j++) {
                if (features[i].attributes.value == null) {
                    features[i].attributes.value = 0;
                }

                if (features[i].attributes.name == mapvalues[j].orgUnit) {
                    features[i].attributes.value = parseFloat(mapvalues[j].value);
                }
            }
        }
        
        var options = {};
        
        /*hidden*/
        choropleth.indicator = 'value';
        choropleth.indicatorText = 'Indicator';
        options.indicator = choropleth.indicator;
        
        options.method = Ext.getCmp('method').getValue();
        options.numClasses = Ext.getCmp('numClasses').getValue();
        options.colors = choropleth.getColors();

        choropleth.coreComp.updateOptions(options);
        choropleth.coreComp.applyClassification();
        choropleth.classificationApplied = true;
        
        MASK.hide();
    }
    else {
        var mlp = MAPDATA.mapLayerPath;
        var nameColumn = MAPDATA.nameColumn;
        
        Ext.Ajax.request({
            url: path + 'getAvailableMapOrganisationUnitRelations' + type,
            method: 'POST',
            params: { mapLayerPath: mlp, format: 'json' },

            success: function( responseObject ) {
                var relations = Ext.util.JSON.decode(responseObject.responseText).mapOrganisationUnitRelations;
                
                for (var i=0; i < relations.length; i++) {
                    var orgunitid = relations[i].organisationUnitId;
                    var featureid = relations[i].featureId;
                    
                    for (var j=0; j < mapvalues.length; j++) {
                        if (orgunitid == mapvalues[j].organisationUnitId) {
                            for (var k=0; k < features.length; k++) {
                                if (features[k].attributes['value'] == null) {
                                    features[k].attributes['value'] = 0;
                                }
                                
                                if (featureid == features[k].attributes[nameColumn]) {
                                    features[k].attributes['value'] = mapvalues[j].value;
                                }
                            }
                        }
                    }
                }
                
                var options = {};
                
                /*hidden*/
                choropleth.indicator = 'value';
                choropleth.indicatorText = 'Indicator';
                options.indicator = choropleth.indicator;
                
                options.method = Ext.getCmp('method').getValue();
                options.numClasses = Ext.getCmp('numClasses').getValue();
                options.colors = choropleth.getColors();
                
                choropleth.coreComp.updateOptions(options);
                choropleth.coreComp.applyClassification();
                choropleth.classificationApplied = true;
                
                MASK.hide();
            },
            failure: function() {
                alert( 'Error while retrieving data: dataReceivedChoropleth' );
            } 
        });
    }
}

/*PROPORTIONAL SYMBOL*/
function getPointData() {
    var indicatorId = Ext.getCmp('indicator_cb').getValue();
    var periodId = Ext.getCmp('period_cb').getValue();
    var level = parseFloat(MAPDATA.organisationUnitLevel);

    Ext.Ajax.request({
        url: path + 'getMapValues' + type,
        method: 'GET',
        params: { indicatorId: indicatorId, periodId: periodId, level: level, format: 'json' },

        success: function( responseObject ) {
            dataReceivedPoint( responseObject.responseText );
        },
        failure: function() {
            alert( 'Status', 'Error while retrieving data' );
        } 
    });
}

function dataReceivedPoint( responseText ) {
    var layers = this.myMap.getLayersByName(choroplethLayerName);
    var features = layers[0]['features'];

    var mapvalues = Ext.util.JSON.decode(responseText).mapvalues;
    
    var mlp = MAPDATA.mapLayerPath;
    var nameColumn = MAPDATA.nameColumn;
    
    Ext.Ajax.request({
        url: path + 'getAvailableMapOrganisationUnitRelations' + type,
        method: 'GET',
        params: { mapLayerPath: mlp, format: 'json' },

        success: function( responseObject ) {
            var relations = Ext.util.JSON.decode(responseObject.responseText).mapOrganisationUnitRelations;

            for (var i=0; i < relations.length; i++) {
                var orgunitid = relations[i].organisationUnitId;
                var featureid = relations[i].featureId;
                
                for (var j=0; j < mapvalues.length; j++) {
                    if (orgunitid == mapvalues[j].organisationUnitId) {
                        for (var k=0; k < features.length; k++) {
                            if (features[k].attributes['value'] == null) {
                                features[k].attributes['value'] = 0;
                            }
                            
                            if (featureid == features[k].attributes[nameColumn]) {
                                features[k].attributes['value'] = mapvalues[j].value;
                            }
                        }
                    }
                }
            }
            
            var minSize = Ext.getCmp('minSize').getValue();
            var maxSize = Ext.getCmp('maxSize').getValue();
            proportionalsymbol.coreComp.updateOptions({
                'indicator': this.indicator,
                'minSize': minSize,
                'maxSize': maxSize
            });
            
            proportionalsymbol.coreComp.applyClassification();
            proportionalsymbol.classificationApplied = true;
        },
        failure: function() {
            alert( 'Error while retrieving data: dataReceivedChoropleth' );
        } 
    });
}

/*MAPPING*/
function getAssignOrganisationUnitData() {
	MASK.msg = 'Creating map...';
	MASK.show();
	
    var mlp = MAPDATA.mapLayerPath;
    
    Ext.Ajax.request({
        url: path + 'getAvailableMapOrganisationUnitRelations' + type,
        method: 'GET',
        params: { mapLayerPath: mlp, format: 'json' },

        success: function( responseObject ) {
            dataReceivedAssignOrganisationUnit( responseObject.responseText );
        },
        failure: function() {
            alert( 'Error while retrieving data: getAssignOrganisationUnitData' );
        } 
    });
}

function dataReceivedAssignOrganisationUnit( responseText ) {
    var layers = this.myMap.getLayersByName(CHOROPLETH_LAYERNAME);
    features = layers[0]['features'];
    
    var relations = Ext.util.JSON.decode(responseText).mapOrganisationUnitRelations;
    
    var nameColumn = MAPDATA.nameColumn;   
    
    for (var i=0; i < features.length; i++) {
        var featureId = features[i].attributes[nameColumn];
        features[i].attributes['value'] = 0;
        
        for (var j=0; j < relations.length; j++) {
            if (relations[j].featureId == featureId) {
                features[i].attributes['value'] = 1;
            }
        }
    }
    
    var options = {};
        
    /*hidden*/
    mapping.indicator = 'value';
    mapping.indicatorText = 'Indicator';
    options.indicator = mapping.indicator;
    
    options.method = 1;
    options.numClasses = 2;
    
    var colorA = new mapfish.ColorRgb();
    colorA.setFromHex('#FFFFFF');
    var colorB = new mapfish.ColorRgb();
    colorB.setFromHex('#72FF63');
    options.colors = [colorA, colorB]; 
    
    mapping.coreComp.updateOptions(options);
    mapping.coreComp.applyClassification();
    mapping.classificationApplied = true;
    
    MASK.hide();
}

/*AUTO MAPPING*/

function getAutoAssignOrganisationUnitData() {
	MASK.msg = 'Loading data...';
	MASK.show();

    var level = MAPDATA.organisationUnitLevel;

    Ext.Ajax.request({
        url: path + 'getOrganisationUnitsAtLevel' + type,
        method: 'POST',
        params: { level: level, format: 'json' },

        success: function( responseObject ) {
            dataReceivedAutoAssignOrganisationUnit( responseObject.responseText );
        },
        failure: function() {
            alert( 'Status', 'Error while retrieving data' );
        } 
    });
}

function dataReceivedAutoAssignOrganisationUnit( responseText ) {
    var layers = this.myMap.getLayersByName(CHOROPLETH_LAYERNAME);
    var features = layers[0]['features'];
    var organisationUnits = Ext.util.JSON.decode(responseText).organisationUnits;
    var nameColumn = MAPDATA.nameColumn;
    var mlp = MAPDATA.mapLayerPath;
    var count_features = 0;
    var count_orgunits = 0;
    var count_match = 0;
    var relations = '';

    for ( var j=0; j < features.length; j++ ) {
        count_features++;
        
        for ( var i=0; i < organisationUnits.length; i++ ) {
            count_orgunits++;
            
            if (features[j].attributes[nameColumn] == organisationUnits[i].name) {
            
                count_match++;
                var organisationUnitId = organisationUnits[i].id;
                var featureId = features[j].attributes[nameColumn];

                relations += organisationUnitId + '::' + featureId + ';;';
            }
        }
    }
	
	if (count_match == 0) {
		MASK.msg = 'No organisation units assigned';
	}
	else {
		MASK.msg = 'Assigning ' + count_match + ' organisation units...';
	}
	MASK.show();
    
    Ext.Ajax.request({
        url: path + 'addOrUpdateMapOrganisationUnitRelations' + type,
        method: 'POST',
        params: { mapLayerPath: mlp, relations: relations },

        success: function( responseObject ) {
			MASK.msg = 'Applying organisation units relations...';
			MASK.show();
			
            Ext.messageBlack.msg('Assign organisation units', '' + msg_highlight_start + count_match + msg_highlight_end + ' organisation units assigned.<br><br>Database: ' + msg_highlight_start + count_orgunits/count_features + msg_highlight_end + '<br>Shapefile: ' + msg_highlight_start + count_features + msg_highlight_end);
            
            Ext.getCmp('grid_gp').getStore().reload();
            loadMapData('assignment');
        },
        failure: function() {
            alert( 'Error: addOrUpdateMapOrganisationUnitRelations' );
        } 
    });
                
}