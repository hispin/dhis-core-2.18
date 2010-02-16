OpenLayers.ProxyHost="";OpenLayers.nullHandler=function(request){OpenLayers.Console.userError(OpenLayers.i18n("unhandledRequest",{'statusText':request.statusText}));};OpenLayers.loadURL=function(uri,params,caller,onComplete,onFailure){if(typeof params=='string'){params=OpenLayers.Util.getParameters(params);}
var success=(onComplete)?onComplete:OpenLayers.nullHandler;var failure=(onFailure)?onFailure:OpenLayers.nullHandler;return OpenLayers.Request.GET({url:uri,params:params,success:success,failure:failure,scope:caller});};OpenLayers.parseXMLString=function(text){var index=text.indexOf('<');if(index>0){text=text.substring(index);}
var ajaxResponse=OpenLayers.Util.Try(function(){var xmldom=new ActiveXObject('Microsoft.XMLDOM');xmldom.loadXML(text);return xmldom;},function(){return new DOMParser().parseFromString(text,'text/xml');},function(){var req=new XMLHttpRequest();req.open("GET","data:"+"text/xml"+";charset=utf-8,"+encodeURIComponent(text),false);if(req.overrideMimeType){req.overrideMimeType("text/xml");}
req.send(null);return req.responseXML;});return ajaxResponse;};OpenLayers.Ajax={emptyFunction:function(){},getTransport:function(){return OpenLayers.Util.Try(function(){return new XMLHttpRequest();},function(){return new ActiveXObject('Msxml2.XMLHTTP');},function(){return new ActiveXObject('Microsoft.XMLHTTP');})||false;},activeRequestCount:0};OpenLayers.Ajax.Responders={responders:[],register:function(responderToAdd){for(var i=0;i<this.responders.length;i++){if(responderToAdd==this.responders[i]){return;}}
this.responders.push(responderToAdd);},unregister:function(responderToRemove){OpenLayers.Util.removeItem(this.reponders,responderToRemove);},dispatch:function(callback,request,transport){var responder;for(var i=0;i<this.responders.length;i++){responder=this.responders[i];if(responder[callback]&&typeof responder[callback]=='function'){try{responder[callback].apply(responder,[request,transport]);}catch(e){}}}}};OpenLayers.Ajax.Responders.register({onCreate:function(){OpenLayers.Ajax.activeRequestCount++;},onComplete:function(){OpenLayers.Ajax.activeRequestCount--;}});OpenLayers.Ajax.Base=OpenLayers.Class({initialize:function(options){this.options={method:'post',asynchronous:true,contentType:'application/xml',parameters:''};OpenLayers.Util.extend(this.options,options||{});this.options.method=this.options.method.toLowerCase();if(typeof this.options.parameters=='string'){this.options.parameters=OpenLayers.Util.getParameters(this.options.parameters);}}});OpenLayers.Ajax.Request=OpenLayers.Class(OpenLayers.Ajax.Base,{_complete:false,initialize:function(url,options){OpenLayers.Ajax.Base.prototype.initialize.apply(this,[options]);if(OpenLayers.ProxyHost&&OpenLayers.String.startsWith(url,"http")){url=OpenLayers.ProxyHost+encodeURIComponent(url);}
this.transport=OpenLayers.Ajax.getTransport();this.request(url);},request:function(url){this.url=url;this.method=this.options.method;var params=OpenLayers.Util.extend({},this.options.parameters);if(this.method!='get'&&this.method!='post'){params['_method']=this.method;this.method='post';}
this.parameters=params;if(params=OpenLayers.Util.getParameterString(params)){if(this.method=='get'){this.url+=((this.url.indexOf('?')>-1)?'&':'?')+params;}else if(/Konqueror|Safari|KHTML/.test(navigator.userAgent)){params+='&_=';}}
try{var response=new OpenLayers.Ajax.Response(this);if(this.options.onCreate){this.options.onCreate(response);}
OpenLayers.Ajax.Responders.dispatch('onCreate',this,response);this.transport.open(this.method.toUpperCase(),this.url,this.options.asynchronous);if(this.options.asynchronous){window.setTimeout(OpenLayers.Function.bind(this.respondToReadyState,this,1),10);}
this.transport.onreadystatechange=OpenLayers.Function.bind(this.onStateChange,this);this.setRequestHeaders();this.body=this.method=='post'?(this.options.postBody||params):null;this.transport.send(this.body);if(!this.options.asynchronous&&this.transport.overrideMimeType){this.onStateChange();}}catch(e){this.dispatchException(e);}},onStateChange:function(){var readyState=this.transport.readyState;if(readyState>1&&!((readyState==4)&&this._complete)){this.respondToReadyState(this.transport.readyState);}},setRequestHeaders:function(){var headers={'X-Requested-With':'XMLHttpRequest','Accept':'text/javascript, text/html, application/xml, text/xml, */*','OpenLayers':true};if(this.method=='post'){headers['Content-type']=this.options.contentType+
(this.options.encoding?'; charset='+this.options.encoding:'');if(this.transport.overrideMimeType&&(navigator.userAgent.match(/Gecko\/(\d{4})/)||[0,2005])[1]<2005){headers['Connection']='close';}}
if(typeof this.options.requestHeaders=='object'){var extras=this.options.requestHeaders;if(typeof extras.push=='function'){for(var i=0,length=extras.length;i<length;i+=2){headers[extras[i]]=extras[i+1];}}else{for(var i in extras){headers[i]=extras[i];}}}
for(var name in headers){this.transport.setRequestHeader(name,headers[name]);}},success:function(){var status=this.getStatus();return!status||(status>=200&&status<300);},getStatus:function(){try{return this.transport.status||0;}catch(e){return 0;}},respondToReadyState:function(readyState){var state=OpenLayers.Ajax.Request.Events[readyState];var response=new OpenLayers.Ajax.Response(this);if(state=='Complete'){try{this._complete=true;(this.options['on'+response.status]||this.options['on'+(this.success()?'Success':'Failure')]||OpenLayers.Ajax.emptyFunction)(response);}catch(e){this.dispatchException(e);}
var contentType=response.getHeader('Content-type');}
try{(this.options['on'+state]||OpenLayers.Ajax.emptyFunction)(response);OpenLayers.Ajax.Responders.dispatch('on'+state,this,response);}catch(e){this.dispatchException(e);}
if(state=='Complete'){this.transport.onreadystatechange=OpenLayers.Ajax.emptyFunction;}},getHeader:function(name){try{return this.transport.getResponseHeader(name);}catch(e){return null;}},dispatchException:function(exception){var handler=this.options.onException;if(handler){handler(this,exception);OpenLayers.Ajax.Responders.dispatch('onException',this,exception);}else{var listener=false;var responders=OpenLayers.Ajax.Responders.responders;for(var i=0;i<responders.length;i++){if(responders[i].onException){listener=true;break;}}
if(listener){OpenLayers.Ajax.Responders.dispatch('onException',this,exception);}else{throw exception;}}}});OpenLayers.Ajax.Request.Events=['Uninitialized','Loading','Loaded','Interactive','Complete'];OpenLayers.Ajax.Response=OpenLayers.Class({status:0,statusText:'',initialize:function(request){this.request=request;var transport=this.transport=request.transport,readyState=this.readyState=transport.readyState;if((readyState>2&&!(!!(window.attachEvent&&!window.opera)))||readyState==4){this.status=this.getStatus();this.statusText=this.getStatusText();this.responseText=transport.responseText==null?'':String(transport.responseText);}
if(readyState==4){var xml=transport.responseXML;this.responseXML=xml===undefined?null:xml;}},getStatus:OpenLayers.Ajax.Request.prototype.getStatus,getStatusText:function(){try{return this.transport.statusText||'';}catch(e){return'';}},getHeader:OpenLayers.Ajax.Request.prototype.getHeader,getResponseHeader:function(name){return this.transport.getResponseHeader(name);}});OpenLayers.Ajax.getElementsByTagNameNS=function(parentnode,nsuri,nsprefix,tagname){var elem=null;if(parentnode.getElementsByTagNameNS){elem=parentnode.getElementsByTagNameNS(nsuri,tagname);}else{elem=parentnode.getElementsByTagName(nsprefix+':'+tagname);}
return elem;};OpenLayers.Ajax.serializeXMLToString=function(xmldom){var serializer=new XMLSerializer();var data=serializer.serializeToString(xmldom);return data;};