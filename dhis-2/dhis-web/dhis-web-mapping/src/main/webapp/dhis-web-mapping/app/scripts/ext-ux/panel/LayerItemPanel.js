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
			
		
		
		//opacity = opacity === 0 ? 0.01 : (opacity || this.opacity);
		//this.checkbox.setValue(value);
		//this.numberField.setValue(opacity * 100);
		//this.numberField.setDisabled(!value);
		//this.layer.setVisibility(value);
		//this.setOpacity(opacity);
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
