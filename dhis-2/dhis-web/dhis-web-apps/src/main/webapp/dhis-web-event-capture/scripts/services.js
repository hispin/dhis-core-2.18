'use strict';

/* Services */

var eventCaptureServices = angular.module('eventCaptureServices', ['ngResource'])

.factory('StorageService', function(){
    var store = new dhis2.storage.Store({
        name: 'dhis2ec',
        adapters: [dhis2.storage.IndexedDBAdapter, dhis2.storage.DomSessionStorageAdapter, dhis2.storage.InMemoryAdapter],
        objectStores: ['ecPrograms', 'programStages', 'geoJsons', 'optionSets', 'events']
    });
    return{
        currentStore: store
    };
})

.factory('OfflineStorageService', function($http, $q, $rootScope, StorageService){
    return {        
        hasLocalData: function() {
            var def = $q.defer();
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.getKeys('events').done(function(events){
                    $rootScope.$apply(function(){
                        def.resolve( events.length > 0 );
                    });                    
                });
            });            
            return def.promise;
        },
        getLocalData: function(){
            var def = $q.defer();            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.getAll('events').done(function(events){
                    $rootScope.$apply(function(){
                        def.resolve({events: events});
                    });                    
                });
            });            
            return def.promise;
        },
        uploadLocalData: function(){            
            var def = $q.defer();
            this.getLocalData().then(function(localData){                
                var evs = {events: []};
                angular.forEach(localData.events, function(ev){
                    ev.event = ev.id;
                    delete ev.id;
                    evs.events.push(ev);
                });

                $http.post('../api/events', evs).then(function(evResponse){                            
                    def.resolve();
                });                      
            });
            return def.promise;
        }
    };
})

.service('DateUtils', function($filter, CalendarService){
    
    return {
        format: function(dateValue) {            
            if(!dateValue){
                return;
            }            
            var calendarSetting = CalendarService.getSetting();
            dateValue = $filter('date')(dateValue, calendarSetting.keyDateFormat);            
            return dateValue;
        },
        formatToHrsMins: function(dateValue) {
            var calendarSetting = CalendarService.getSetting();
            var dateFormat = 'YYYY-MM-DD @ hh:mm A';
            if(calendarSetting.keyDateFormat === 'dd-MM-yyyy'){
                dateFormat = 'DD-MM-YYYY @ hh:mm A';
            }            
            return moment(dateValue).format(dateFormat);
        },
        getToday: function(){  
            var calendarSetting = CalendarService.getSetting();
            var tdy = $.calendars.instance(calendarSetting.keyCalendar).newDate();            
            var today = moment(tdy._year + '-' + tdy._month + '-' + tdy._day, 'YYYY-MM-DD')._d;            
            today = Date.parse(today);     
            today = $filter('date')(today,  calendarSetting.keyDateFormat);
            return today;
        },
        formatFromUserToApi: function(dateValue){            
            if(!dateValue){
                return;
            }
            var calendarSetting = CalendarService.getSetting();            
            dateValue = moment(dateValue, calendarSetting.momentFormat)._d;
            dateValue = Date.parse(dateValue);     
            dateValue = $filter('date')(dateValue, 'yyyy-MM-dd'); 
            return dateValue;            
        },
        formatFromApiToUser: function(dateValue){            
            if(!dateValue){
                return;
            }            
            var calendarSetting = CalendarService.getSetting();
            dateValue = moment(dateValue, 'YYYY-MM-DD')._d;
            dateValue = Date.parse(dateValue);     
            dateValue = $filter('date')(dateValue, calendarSetting.keyDateFormat); 
            return dateValue;
        }
    };
})

/* Factory to fetch programs */
.factory('ProgramFactory', function($q, $rootScope, StorageService) {  
        
    return {
        
        getAll: function(){
            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.getAll('ecPrograms').done(function(programs){                    
                    $rootScope.$apply(function(){
                        def.resolve(programs);
                    });                    
                });
            });
            return def.promise;
        }        
    };
})

/* Factory to fetch programStages */
.factory('ProgramStageFactory', function($q, $rootScope, StorageService) {  
    
    return {        
        get: function(uid){
            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.get('programStages', uid).done(function(pst){                    
                    $rootScope.$apply(function(){
                        def.resolve(pst);
                    });
                });
            });                        
            return def.promise;            
        }        
    };        
})

/* factory for handling events */
.factory('DHIS2EventFactory', function($http, $q, StorageService, $rootScope) {   
    
    return {
        getByStage: function(orgUnit, programStage, pager){
        	var pgSize = pager ? pager.pageSize : 50;
        	var pg = pager ? pager.page : 1;
            var url = '../api/events.json?' + 'orgUnit=' + orgUnit + '&programStage=' + programStage + '&pageSize=' + pgSize + '&page=' + pg;
            
            var promise = $http.get( url ).then(function(response){                    
                return response.data;        
            }, function(){     
                //return dhis2.ec.storageManager.getEvents(orgUnit, programStage);                

                var def = $q.defer();
                StorageService.currentStore.open().done(function(){
                    StorageService.currentStore.getAll('events').done(function(evs){
                        var result = {events: [], pager: {pageSize: '', page: 1, toolBarDisplay: 5, pageCount: 1}};
                        angular.forEach(evs, function(ev){                            
                            if(ev.programStage === programStage && ev.orgUnit === orgUnit){
                                ev.event = ev.id;
                                result.events.push(ev);
                            }
                        }); 
                        $rootScope.$apply(function(){
                            def.resolve( result );
                        });                    
                    });
                });            
                return def.promise;
            });            
            
            return promise;
        },        
        get: function(eventUid){            
            var promise = $http.get('../api/events/' + eventUid + '.json').then(function(response){               
                return response.data;                
            }, function(){
                //return dhis2.ec.storageManager.getEvent(eventUid);
                var p = dhis2.ec.store.get('events', eventUid).then(function(ev){
                    ev.event = eventUid;
                    return ev;
                });
                return p;
            });            
            return promise;
        },        
        create: function(dhis2Event){
            var promise = $http.post('../api/events.json', dhis2Event).then(function(response){
                return response.data;
            }, function(){            
                dhis2Event.id = dhis2.util.uid();  
                dhis2Event.event = dhis2Event.id;
                dhis2.ec.store.set( 'events', dhis2Event );                
                return {importSummaries: [{status: 'SUCCESS', reference: dhis2Event.id}]};
            });
            return promise;            
        },        
        delete: function(dhis2Event){
            var promise = $http.delete('../api/events/' + dhis2Event.event).then(function(response){
                return response.data;
            }, function(){
                dhis2.ec.store.remove( 'events', dhis2Event.event );
            });
            return promise;           
        },    
        update: function(dhis2Event){
            var promise = $http.put('../api/events/' + dhis2Event.event, dhis2Event).then(function(response){              
                return response.data;
            }, function(){
                dhis2.ec.store.remove('events', dhis2Event.event);
                dhis2Event.id = dhis2Event.event;
                dhis2.ec.store.set('events', dhis2Event);
            });
            return promise;
        },        
        updateForSingleValue: function(singleValue, fullValue){        
            var promise = $http.put('../api/events/' + singleValue.event + '/' + singleValue.dataValues[0].dataElement, singleValue ).then(function(response){
                 return response.data;
            }, function(){
                dhis2.ec.store.remove('events', fullValue.event);
                fullValue.id = fullValue.event;
                dhis2.ec.store.set('events', fullValue);
            });
            return promise;
        }
    };    
})

/* service for dealing with events */
.service('DHIS2EventService', function(){
    return {     
        //for simplicity of grid display, events were changed from
        //event.datavalues = [{dataElement: dataElement, value: value}] to
        //event[dataElement] = value
        //now they are changed back for the purpose of storage.   
        reconstructEvent: function(event, programStageDataElements){
            var e = {};
        
            e.event         = event.event;
            e.status        = event.status;
            e.program       = event.program;
            e.programStage  = event.programStage;
            e.orgUnit       = event.orgUnit;
            e.eventDate     = event.eventDate;

            var dvs = [];
            angular.forEach(programStageDataElements, function(prStDe){
                if(event.hasOwnProperty(prStDe.dataElement.id)){
                    dvs.push({dataElement: prStDe.dataElement.id, value: event[prStDe.dataElement.id]});
                }
            });

            e.dataValues = dvs;
            
            if(event.coordinate){
                e.coordinate = {latitude: event.coordinate.latitude ? event.coordinate.latitude : '',
                                     longitude: event.coordinate.longitude ? event.coordinate.longitude : ''};
            }

            return e;
        }        
    };
});
