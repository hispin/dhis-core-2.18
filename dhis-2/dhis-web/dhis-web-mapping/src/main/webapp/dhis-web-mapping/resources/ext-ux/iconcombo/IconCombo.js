/* @author  Ing. Jozef Sakalos */
Ext.namespace('Ext.ux.plugins');

Ext.ux.plugins.IconCombo = function(config) {
    Ext.apply(this, config);
};
 
Ext.extend(Ext.ux.plugins.IconCombo, Ext.util.Observable, {
    init: function(combo) {
        Ext.apply(combo, {
            tpl:  '<tpl for=".">'
                + '<div class="x-combo-list-item ux-icon-combo-item '
                + '{' + combo.iconClsField + '}">'
                + '{' + combo.displayField + '}'
                + '</div></tpl>',
 
            onRender: combo.onRender.createSequence(function(ct, position) {
                this.wrap.applyStyles({position:'relative'});
                this.el.addClass('ux-icon-combo-input');
 
                this.icon = Ext.DomHelper.append(this.el.up('div.x-form-field-wrap'), {
                    tag: 'div', style:'position:absolute'
                });
            }),
 
            setIconCls: function() {
                var rec = this.store.query(this.valueField, this.getValue()).itemAt(0);
                if (rec) {
                    this.icon.className = 'ux-icon-combo-icon ' + rec.get(this.iconClsField);
                }
            },
             
            setValue: combo.setValue.createSequence(function(value) {
                this.setIconCls();
            })
        });
    }
});
