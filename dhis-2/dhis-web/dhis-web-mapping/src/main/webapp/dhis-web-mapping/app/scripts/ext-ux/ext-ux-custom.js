/* ColorButton */

Ext.define('Ext.ux.button.ColorButton', {
	extend: 'Ext.button.Button',
	alias: 'widget.colorbutton',
	width: 109,
	height: 22,
	defaultValue: null,
	value: 'f1f1f1',
	getValue: function() {
		return this.value;
	},
	setValue: function(color) {
		this.value = color;
		if (Ext.isDefined(this.getEl())) {
			this.getEl().dom.style.background = '#' + color;
		}
	},
	reset: function() {
		this.setValue(this.defaultValue);
	},
	menu: {},
	menuHandler: function() {},
	initComponent: function() {
		var that = this;			
		this.defaultValue = this.value;			
		this.menu = Ext.create('Ext.menu.Menu', {
			showSeparator: false,
			items: {
				xtype: 'colorpicker',
				closeAction: 'hide',
				listeners: {
					select: function(cp, color) {
						that.setValue(color);
						that.menu.hide();
						that.menuHandler(cp, color);
					}
				}
			}
		});
		this.callParent();
	},
	listeners: {
		render: function() {
			this.setValue(this.value);
		}
	}
});

/* LayerItemPanel */

Ext.define('Ext.ux.panel.LayerItemPanel', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.layeritempanel',
	layout: 'column',
	layer: null,
	checkbox: null,
	numberField: null,
	imageUrl: null,
	text: null,
	width: 184,
	height: 22,
	value: false,
	opacity: 0.8,
	getValue: function() {
		return this.checkbox.getValue();
	},
	setValue: function(value, opacity) {
		this.checkbox.setValue(value);
		this.numberField.setDisabled(!value);
		this.layer.setVisibility(value);
		
		if (opacity === 0) {
			this.numberField.setValue(0);
			this.setOpacity(0.01);
		}
		else if (opacity > 0) {
			this.numberField.setValue(opacity * 100);
			this.setOpacity(opacity);
		}
		else {
			this.numberField.setValue(this.opacity * 100);
			this.setOpacity(this.opacity);
		}
	},
	getOpacity: function() {
		return this.opacity;
	},
	setOpacity: function(opacity) {
		this.opacity = opacity === 0 ? 0.01 : opacity;
		this.layer.setLayerOpacity(this.opacity);
	},
	disableItem: function() {
		this.checkbox.setValue(false);
		this.numberField.disable();
		this.layer.setVisibility(false);
	},
	updateItem: function(value) {
		this.numberField.setDisabled(!value);
		this.layer.setVisibility(value);
	},
	initComponent: function() {
		var that = this,
			image;
		
		this.checkbox = Ext.create('Ext.form.field.Checkbox', {
			width: 14,
			checked: this.value,
			listeners: {
				change: function(chb, value) {
					if (value && that.layer.layerType === GIS.conf.finals.layer.type_base) {
						var layers = GIS.util.map.getLayersByType(GIS.conf.finals.layer.type_base),
							layer;
						for (var i = 0; i < layers.length; i++) {
							layer = layers[i];
							if (layer !== that.layer) {
								layer.item.checkbox.suppressChange = true;
								layer.item.disableItem();
							}
						}
					}
					that.updateItem(value);
					
					GIS.cmp.downloadButton.xable();
				}
			}
		});
		
		image = Ext.create('Ext.Img', {
			width: 14,
			height: 14,
			src: this.imageUrl
		});
		
		this.numberField = Ext.create('Ext.form.field.Number', {
			cls: 'gis-numberfield',
			width: 47,
			height: 18,
			minValue: 0,
			maxValue: 100,
			value: this.opacity * 100,
			allowBlank: false,
			disabled: this.numberFieldDisabled,
			listeners: {
				change: function() {
					var value = this.getValue(),
						opacity = value === 0 ? 0.01 : value/100;
					
					that.setOpacity(opacity);
				}
			}
		});
		
		this.items = [
			{
				width: this.checkbox.width + 6,
				items: this.checkbox
			},
			{
				width: image.width + 5,
				items: image,
				bodyStyle: 'padding-top: 4px'
			},
			{
				width: 98,
				html: this.text,
				bodyStyle: 'padding-top: 4px'
			},
			{
				width: this.numberField.width,
				items: this.numberField
			}
		];		
		
		this.layer.setOpacity(this.opacity);
		
		this.callParent();
	}
});

/* CheckTextNumber */

Ext.define('Ext.ux.panel.CheckTextNumber', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.checktextnumber',
	layout: 'column',
	layer: null,
	checkbox: null,
	text: null,
	numberField: null,
	width: 184,
	height: 22,
	value: false,
	number: 5,
	getValue: function() {
		return this.checkbox.getValue();
	},
	getNumber: function() {
		return this.numberField.getValue();
	},
	setValue: function(value, number) {
		if (value) {
			this.checkbox.setValue(value);
		}
		if (number) {
			this.numberField.setValue(number);
		}
	},
	enable: function() {
		this.numberField.enable();
	},
	disable: function() {
		this.numberField.disable();
	},
	reset: function() {
		this.checkbox.setValue(false);
		this.numberField.setValue(this.number);
		this.numberField.disable();
	},
	initComponent: function() {
		var that = this;
		
		this.checkbox = Ext.create('Ext.form.field.Checkbox', {
			width: 196,
			boxLabel: this.text,
			checked: this.value,
			disabled: this.disabled,
			listeners: {
				change: function(chb, value) {
					if (value) {
						that.enable();
					}
					else {
						that.disable();
					}
				}
			}
		});
		
		this.numberField = Ext.create('Ext.form.field.Number', {
			cls: 'gis-numberfield',
			fieldStyle: 'border-top-left-radius: 1px; border-bottom-left-radius: 1px',
			style: 'padding-bottom: 3px',
			width: 60,
			height: 21,
			minValue: 0,
			maxValue: 10000,
			value: this.number,
			allowBlank: false,
			disabled: true
		});
		
		this.items = [
			{
				width: this.checkbox.width + 6,
				items: this.checkbox
			},
			{
				width: this.numberField.width,
				items: this.numberField
			}
		];
		
		this.callParent();
	}
});

/* MultiSelect */

Ext.define("Ext.ux.layout.component.form.MultiSelect",{extend:"Ext.layout.component.field.Field",alias:["layout.multiselectfield"],type:"multiselectfield",defaultHeight:200,sizeBodyContents:function(a,b){var c=this;if(!Ext.isNumber(b)){b=c.defaultHeight}c.owner.panel.setSize(a,b)}});

/*
This file is part of Ext JS 4
Copyright (c) 2011 Sencha Inc
Contact: http://www.sencha.com/contact
GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file. Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
If you are unsure which license is appropriate for your use, please contact the sales department at http://www.sencha.com/contact.
*/
/**
 * @class Ext.ux.form.MultiSelect
 * @extends Ext.form.field.Base
 * A control that allows selection and form submission of multiple list items.
 *
 * @history
 * 2008-06-19 bpm Original code contributed by Toby Stuart (with contributions from Robert Williams)
 * 2008-06-19 bpm Docs and demo code clean up
 *
 * @constructor
 * Create a new MultiSelect
 * @param {Object} config Configuration options
 * @xtype multiselect
 */
Ext.define('Ext.ux.form.MultiSelect', {
    extend: 'Ext.form.field.Base',
    alternateClassName: 'Ext.ux.Multiselect',
    alias: ['widget.multiselect', 'widget.multiselectfield'],
    uses: ['Ext.view.BoundList', 'Ext.form.FieldSet', 'Ext.ux.layout.component.form.MultiSelect', 'Ext.view.DragZone', 'Ext.view.DropZone'],
    /**
     * @cfg {String} listTitle An optional title to be displayed at the top of the selection list.
     */
    /**
     * @cfg {String/Array} dragGroup The ddgroup name(s) for the MultiSelect DragZone (defaults to undefined).
     */
    /**
     * @cfg {String/Array} dropGroup The ddgroup name(s) for the MultiSelect DropZone (defaults to undefined).
     */
    /**
     * @cfg {Boolean} ddReorder Whether the items in the MultiSelect list are drag/drop reorderable (defaults to false).
     */
    ddReorder: false,
    /**
     * @cfg {Object/Array} tbar An optional toolbar to be inserted at the top of the control's selection list.
     * This can be a {@link Ext.toolbar.Toolbar} object, a toolbar config, or an array of buttons/button configs
     * to be added to the toolbar. See {@link Ext.panel.Panel#tbar}.
     */
    /**
     * @cfg {String} appendOnly True if the list should only allow append drops when drag/drop is enabled
     * (use for lists which are sorted, defaults to false).
     */
    appendOnly: false,
    /**
     * @cfg {String} displayField Name of the desired display field in the dataset (defaults to 'text').
     */
    displayField: 'text',
    /**
     * @cfg {String} valueField Name of the desired value field in the dataset (defaults to the
     * value of {@link #displayField}).
     */
    /**
     * @cfg {Boolean} allowBlank False to require at least one item in the list to be selected, true to allow no
     * selection (defaults to true).
     */
    allowBlank: true,
    /**
     * @cfg {Number} minSelections Minimum number of selections allowed (defaults to 0).
     */
    minSelections: 0,
    /**
     * @cfg {Number} maxSelections Maximum number of selections allowed (defaults to Number.MAX_VALUE).
     */
    maxSelections: Number.MAX_VALUE,
    /**
     * @cfg {String} blankText Default text displayed when the control contains no items (defaults to 'This field is required')
     */
    blankText: 'This field is required',
    /**
     * @cfg {String} minSelectionsText Validation message displayed when {@link #minSelections} is not met (defaults to 'Minimum {0}
     * item(s) required'). The {0} token will be replaced by the value of {@link #minSelections}.
     */
    minSelectionsText: 'Minimum {0} item(s) required',
    /**
     * @cfg {String} maxSelectionsText Validation message displayed when {@link #maxSelections} is not met (defaults to 'Maximum {0}
     * item(s) allowed'). The {0} token will be replaced by the value of {@link #maxSelections}.
     */
    maxSelectionsText: 'Maximum {0} item(s) allowed',
    /**
     * @cfg {String} delimiter The string used to delimit the selected values when {@link #getSubmitValue submitting}
     * the field as part of a form. Defaults to ','. If you wish to have the selected values submitted as separate
     * parameters rather than a single delimited parameter, set this to <tt>null</tt>.
     */
    delimiter: ',',
    /**
     * @cfg {Ext.data.Store/Array} store The data source to which this MultiSelect is bound (defaults to <tt>undefined</tt>).
     * Acceptable values for this property are:
     * <div class="mdetail-params"><ul>
     * <li><b>any {@link Ext.data.Store Store} subclass</b></li>
     * <li><b>an Array</b> : Arrays will be converted to a {@link Ext.data.ArrayStore} internally.
     * <div class="mdetail-params"><ul>
     * <li><b>1-dimensional array</b> : (e.g., <tt>['Foo','Bar']</tt>)<div class="sub-desc">
     * A 1-dimensional array will automatically be expanded (each array item will be the combo
     * {@link #valueField value} and {@link #displayField text})</div></li>
     * <li><b>2-dimensional array</b> : (e.g., <tt>[['f','Foo'],['b','Bar']]</tt>)<div class="sub-desc">
     * For a multi-dimensional array, the value in index 0 of each item will be assumed to be the combo
     * {@link #valueField value}, while the value at index 1 is assumed to be the combo {@link #displayField text}.
     * </div></li></ul></div></li></ul></div>
     */
    componentLayout: 'multiselectfield',
    fieldBodyCls: Ext.baseCSSPrefix + 'form-multiselect-body',
    // private
    initComponent: function () {
        var me = this;
        me.bindStore(me.store, true);
        if (me.store.autoCreated) {
            me.valueField = me.displayField = 'field1';
            if (!me.store.expanded) {
                me.displayField = 'field2';
            }
        }
        if (!Ext.isDefined(me.valueField)) {
            me.valueField = me.displayField;
        }
        me.callParent();
    },
    bindStore: function (store, initial) {
        var me = this,
            oldStore = me.store,
            boundList = me.boundList;
        if (oldStore && !initial && oldStore !== store && oldStore.autoDestroy) {
            oldStore.destroy();
        }
        me.store = store ? Ext.data.StoreManager.lookup(store) : null;
        if (boundList) {
            boundList.bindStore(store || null);
        }
    },
    // private
    onRender: function (ct, position) {
        var me = this,
            panel, boundList, selModel;
        me.callParent(arguments);
        boundList = me.boundList = Ext.create('Ext.view.BoundList', {
            multiSelect: true,
            store: me.store,
            displayField: me.displayField,
            border: false
        });
        selModel = boundList.getSelectionModel();
        me.mon(selModel, {
            selectionChange: me.onSelectionChange,
            scope: me
        });        
        panel = me.panel = Ext.create('Ext.panel.Panel', {
            title: me.listTitle,
            tbar: me.tbar,
            items: [boundList],
            renderTo: me.bodyEl,
            layout: 'fit'
        });
        // Must set upward link after first render
        panel.ownerCt = me;
        // Set selection to current value
        me.setRawValue(me.rawValue);
    },
    // No content generated via template, it's all added components
    getSubTplMarkup: function () {
        return '';
    },
    // private
    afterRender: function () {
        var me = this;
        me.callParent();
        if (me.ddReorder && !me.dragGroup && !me.dropGroup) {
            me.dragGroup = me.dropGroup = 'MultiselectDD-' + Ext.id();
        }
        if (me.draggable || me.dragGroup) {
            me.dragZone = Ext.create('Ext.view.DragZone', {
                view: me.boundList,
                ddGroup: me.dragGroup,
                dragText: '{0} Item{1}'
            });
        }
        if (me.droppable || me.dropGroup) {
            me.dropZone = Ext.create('Ext.view.DropZone', {
                view: me.boundList,
                ddGroup: me.dropGroup,
                handleNodeDrop: function (data, dropRecord, position) {
                    var view = this.view,
                        store = view.getStore(),
                        records = data.records,
                        index;
                    // remove the Models from the source Store
                    data.view.store.remove(records);
                    index = store.indexOf(dropRecord);
                    if (position === 'after') {
                        index++;
                    }
                    store.insert(index, records);
                    view.getSelectionModel().select(records);
                }
            });
        }
    },
    onSelectionChange: function () {
        this.checkChange();
    },
    /**
     * Clears any values currently selected.
     */
    clearValue: function () {
        this.setValue([]);
    },
    /**
     * Return the value(s) to be submitted for this field. The returned value depends on the {@link #delimiter}
     * config: If it is set to a String value (like the default ',') then this will return the selected values
     * joined by the delimiter. If it is set to <tt>null</tt> then the values will be returned as an Array.
     */
    getSubmitValue: function () {
        var me = this,
            delimiter = me.delimiter,
            val = me.getValue();
        return Ext.isString(delimiter) ? val.join(delimiter) : val;
    },
    // inherit docs
    getRawValue: function () {
        var me = this,
            boundList = me.boundList;
        if (boundList) {
            me.rawValue = Ext.Array.map(boundList.getSelectionModel().getSelection(), function (model) {
                return model.get(me.valueField);
            });
        }
        return me.rawValue;
    },
    // inherit docs
    setRawValue: function (value) {
        var me = this,
            boundList = me.boundList,
            models;
        value = Ext.Array.from(value);
        me.rawValue = value;
        if (boundList) {
            models = [];
            Ext.Array.forEach(value, function (val) {
                var undef, model = me.store.findRecord(me.valueField, val, undef, undef, true, true);
                if (model) {
                    models.push(model);
                }
            });
            boundList.getSelectionModel().select(models, false, true);
        }
        return value;
    },
    // no conversion
    valueToRaw: function (value) {
        return value;
    },
    // compare array values
    isEqual: function (v1, v2) {
        var fromArray = Ext.Array.from,
            i, len;
        v1 = fromArray(v1);
        v2 = fromArray(v2);
        len = v1.length;
        if (len !== v2.length) {
            return false;
        }
        for (i = 0; i < len; i++) {
            if (v2[i] !== v1[i]) {
                return false;
            }
        }
        return true;
    },
    getErrors: function (value) {
        var me = this,
            format = Ext.String.format,
            errors = me.callParent(arguments),
            numSelected;
        value = Ext.Array.from(value || me.getValue());
        numSelected = value.length;
        if (!me.allowBlank && numSelected < 1) {
            errors.push(me.blankText);
        }
        if (numSelected < this.minSelections) {
            errors.push(format(me.minSelectionsText, me.minSelections));
        }
        if (numSelected > this.maxSelections) {
            errors.push(format(me.maxSelectionsText, me.maxSelections));
        }
        return errors;
    },
    onDisable: function () {
        this.callParent();
        this.disabled = true;
        this.updateReadOnly();
    },
    onEnable: function () {
        this.callParent();
        this.disabled = false;
        this.updateReadOnly();
    },
    setReadOnly: function (readOnly) {
        this.readOnly = readOnly;
        this.updateReadOnly();
    },
    /**
     * @private Lock or unlock the BoundList's selection model to match the current disabled/readonly state
     */
    updateReadOnly: function () {
        var me = this,
            boundList = me.boundList,
            readOnly = me.readOnly || me.disabled;
        if (boundList) {
            boundList.getSelectionModel().setLocked(readOnly);
        }
    },
    onDestroy: function () {
        Ext.destroyMembers(this, 'panel', 'boundList', 'dragZone', 'dropZone');
        this.callParent();
    }
});

