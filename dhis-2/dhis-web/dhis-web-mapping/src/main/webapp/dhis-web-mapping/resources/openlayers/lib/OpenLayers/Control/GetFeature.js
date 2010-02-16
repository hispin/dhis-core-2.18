OpenLayers.Control.GetFeature=OpenLayers.Class(OpenLayers.Control,{protocol:null,multipleKey:null,toggleKey:null,modifiers:null,multiple:false,click:true,clickout:true,toggle:false,clickTolerance:5,hover:false,box:false,maxFeatures:10,features:null,hoverFeature:null,handlerOptions:null,handlers:null,hoverResponse:null,EVENT_TYPES:["featureselected","featureunselected","clickout","beforefeatureselected","hoverfeature","outfeature"],initialize:function(options){this.EVENT_TYPES=OpenLayers.Control.GetFeature.prototype.EVENT_TYPES.concat(OpenLayers.Control.prototype.EVENT_TYPES);options.handlerOptions=options.handlerOptions||{};OpenLayers.Control.prototype.initialize.apply(this,[options]);this.features={};this.handlers={};if(this.click){this.handlers.click=new OpenLayers.Handler.Click(this,{click:this.selectSingle},this.handlerOptions.click||{})};if(this.box){this.handlers.box=new OpenLayers.Handler.Box(this,{done:this.selectBox},OpenLayers.Util.extend(this.handlerOptions.box,{boxDivClassName:"olHandlerBoxSelectFeature"}));}
if(this.hover){this.handlers.hover=new OpenLayers.Handler.Hover(this,{'move':this.cancelHover,'pause':this.selectHover},OpenLayers.Util.extend(this.handlerOptions.hover,{'delay':250}));}},activate:function(){if(!this.active){for(var i in this.handlers){this.handlers[i].activate();}}
return OpenLayers.Control.prototype.activate.apply(this,arguments);},deactivate:function(){if(this.active){for(var i in this.handlers){this.handlers[i].deactivate();}}
return OpenLayers.Control.prototype.deactivate.apply(this,arguments);},unselectAll:function(options){var feature;for(var i=this.features.length-1;i>=0;--i){feature=this.features[i];if(!options||options.except!=feature){this.unselect(feature);}}},selectSingle:function(evt){OpenLayers.Element.addClass(this.map.viewPortDiv,"olCursorWait");var bounds=this.pixelToBounds(evt.xy);this.setModifiers(evt);this.request(bounds,{single:true});},selectBox:function(position){if(position instanceof OpenLayers.Bounds){var minXY=this.map.getLonLatFromPixel(new OpenLayers.Pixel(position.left,position.bottom));var maxXY=this.map.getLonLatFromPixel(new OpenLayers.Pixel(position.right,position.top));var bounds=new OpenLayers.Bounds(minXY.lon,minXY.lat,maxXY.lon,maxXY.lat);this.setModifiers(this.handlers.box.dragHandler.evt);this.request(bounds);}},selectHover:function(evt){var bounds=this.pixelToBounds(evt.xy);this.request(bounds,{single:true,hover:true});},cancelHover:function(){if(this.hoverResponse){this.protocol.abort(this.hoverResponse);this.hoverResponse=null;}},request:function(bounds,options){options=options||{};var filter=new OpenLayers.Filter.Spatial({type:OpenLayers.Filter.Spatial.BBOX,value:bounds});var response=this.protocol.read({maxFeatures:options.single==true?this.maxFeatures:undefined,filter:filter,callback:function(result){if(result.code==1){if(result.features.length){if(options.single==true){this.selectBestFeature(result.features,bounds.getCenterLonLat(),options);}else{this.select(result.features);}}else if(options.hover){this.hoverSelect();}else{this.events.triggerEvent("clickout");if(this.clickout){this.unselectAll();}}}
OpenLayers.Element.removeClass(this.map.viewPortDiv,"olCursorWait");},scope:this});if(options.hover==true){this.hoverResponse=response;}},selectBestFeature:function(features,clickPosition,options){options=options||{};if(features.length){var point=new OpenLayers.Geometry.Point(clickPosition.lon,clickPosition.lat);var feature,resultFeature,dist;var minDist=Number.MAX_VALUE;for(var i=0;i<features.length;++i){feature=features[i];if(feature.geometry){dist=point.distanceTo(feature.geometry,{edge:false});if(dist<minDist){minDist=dist;resultFeature=feature;if(minDist==0){break;}}}}
if(options.hover==true){this.hoverSelect(resultFeature);}else{this.select(resultFeature||features);}};},setModifiers:function(evt){this.modifiers={multiple:this.multiple||(this.multipleKey&&evt[this.multipleKey]),toggle:this.toggle||(this.toggleKey&&evt[this.toggleKey])}},select:function(features){if(!this.modifiers.multiple&&!this.modifiers.toggle){this.unselectAll();}
if(!(features instanceof Array)){features=[features];}
var feature;for(var i=0,len=features.length;i<len;++i){feature=features[i];if(this.features[feature.fid||feature.id]){if(this.modifiers.toggle){this.unselect(this.features[feature.fid||feature.id]);}}else{cont=this.events.triggerEvent("beforefeatureselected",{feature:feature});if(cont!==false){this.features[feature.fid||feature.id]=feature;this.events.triggerEvent("featureselected",{feature:feature});}}}},hoverSelect:function(feature){var fid=feature?feature.fid||feature.id:null;var hfid=this.hoverFeature?this.hoverFeature.fid||this.hoverFeature.id:null;if(hfid&&hfid!=fid){this.events.triggerEvent("outfeature",{feature:this.hoverFeature});this.hoverFeature=null;}
if(fid&&fid!=hfid){this.events.triggerEvent("hoverfeature",{feature:feature});this.hoverFeature=feature;}},unselect:function(feature){delete this.features[feature.fid||feature.id];this.events.triggerEvent("featureunselected",{feature:feature});},unselectAll:function(){for(var fid in this.features){this.unselect(this.features[fid]);}},setMap:function(map){for(var i in this.handlers){this.handlers[i].setMap(map);}
OpenLayers.Control.prototype.setMap.apply(this,arguments);},pixelToBounds:function(pixel){var llPx=pixel.add(-this.clickTolerance/2,this.clickTolerance/2);var urPx=pixel.add(this.clickTolerance/2,-this.clickTolerance/2);var ll=this.map.getLonLatFromPixel(llPx);var ur=this.map.getLonLatFromPixel(urPx);return new OpenLayers.Bounds(ll.lon,ll.lat,ur.lon,ur.lat);},CLASS_NAME:"OpenLayers.Control.GetFeature"});