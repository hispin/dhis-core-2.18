'use strict';

/* Services */

var trackerCaptureServices = angular.module('trackerCaptureServices', ['ngResource'])


.factory('StorageService', function(){
    var store = new dhis2.storage.Store({
        name: "dhis2tc",
        adapters: [dhis2.storage.IndexedDBAdapter, dhis2.storage.DomSessionStorageAdapter, dhis2.storage.InMemoryAdapter],
        objectStores: ['trackerCapturePrograms', 'programStages', 'trackedEntities','attributes','optionSets']
    });
    return{
        currentStore: store
    };
})

/* factory for loading logged in user profiles from DHIS2 */
.factory('CurrentUserProfile', function($http) { 
           
    var profile, promise;
    return {
        get: function() {
            if( !promise ){
                promise = $http.get( '../api/me/profile').then(function(response){
                   profile = response.data;
                   return profile;
                });
            }
            return promise;         
        }
    };  
})

/* Factory to fetch relationships */
.factory('RelationshipFactory', function($q, $rootScope, StorageService) { 
    return {
        getAll: function(){
            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.getAll('relationshipTypes').done(function(relationshipTypes){
                    $rootScope.$apply(function(){
                        def.resolve(relationshipTypes);
                    });                    
                });
            });            
            
            return def.promise;            
        },
        get: function(uid){
            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.get('relationshipTypes', uid).done(function(relationshipType){                    
                    $rootScope.$apply(function(){
                        def.resolve(relationshipType);
                    });
                });
            });                        
            return def.promise;            
        }
    };
})


/* Factory to fetch programs */
.factory('ProgramFactory', function($q, $rootScope, StorageService) { 
    return {
        getAll: function(){
            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.getAll('trackerCapturePrograms').done(function(programs){
                    $rootScope.$apply(function(){
                        def.resolve(programs);
                    });                    
                });
            });            
            
            return def.promise;            
        },
        get: function(uid){
            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.get('trackerCapturePrograms', uid).done(function(pr){                    
                    $rootScope.$apply(function(){
                        def.resolve(pr);
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
                    angular.forEach(pst.programStageDataElements, function(pstDe){   
                        if(pstDe.dataElement.optionSet){
                            StorageService.currentStore.get('optionSets', pstDe.dataElement.optionSet.id).done(function(optionSet){
                                pstDe.dataElement.optionSet = optionSet;                                
                            });                            
                        }                        
                    });
                    $rootScope.$apply(function(){
                        def.resolve(pst);
                    });
                });
            });            
            return def.promise;
        },
        getByProgram: function(program){
            var def = $q.defer();
            var stageIds = [];
            var programStages = [];
            angular.forEach(program.programStages, function(stage){
                stageIds.push(stage.id);
            });
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.getAll('programStages').done(function(stages){   
                    angular.forEach(stages, function(stage){
                        if(stageIds.indexOf(stage.id) !== -1){
                            angular.forEach(stage.programStageDataElements, function(pstDe){   
                                if(pstDe.dataElement.optionSet){
                                    StorageService.currentStore.get('optionSets', pstDe.dataElement.optionSet.id).done(function(optionSet){
                                        pstDe.dataElement.optionSet = optionSet;                                
                                    });                            
                                }                            
                            });
                            programStages.push(stage);                               
                        }                        
                    });                    
                    $rootScope.$apply(function(){
                        def.resolve(programStages);
                    });
                });                
            });            
            return def.promise;
        }
    };    
})

/*Orgunit service for local db */
.service('OrgUnitService', function($window, $q){
    
    var indexedDB = $window.indexedDB;
    var db = null;
    
    var open = function(){
        var deferred = $q.defer();
        
        var request = indexedDB.open("dhis2ou");
        
        request.onsuccess = function(e) {
          db = e.target.result;
          deferred.resolve();
        };

        request.onerror = function(){
          deferred.reject();
        };

        return deferred.promise;
    };
    
    var get = function(uid){
        
        var deferred = $q.defer();
        
        if( db === null){
            deferred.reject("DB not opened");
        }
        else{
            var tx = db.transaction(["ou"]);
            var store = tx.objectStore("ou");
            var query = store.get(uid);
                
            query.onsuccess = function(e){
                deferred.resolve(e.target.result);
            };
        }
        return deferred.promise;
    };
    
    return {
        open: open,
        get: get
    };    
})

/* Service to deal with enrollment */
.service('EnrollmentService', function($http) {
    
    return {        
        get: function( enrollmentUid ){
            var promise = $http.get(  '../api/enrollments/' + enrollmentUid ).then(function(response){
                return response.data;
            });
            return promise;
        },
        getByEntity: function( entity ){
            var promise = $http.get(  '../api/enrollments?trackedEntityInstance=' + entity ).then(function(response){
                return response.data;
            });
            return promise;
        },
        getByEntityAndProgram: function( entity, program ){
            var promise = $http.get(  '../api/enrollments?trackedEntityInstance=' + entity + '&program=' + program ).then(function(response){
                return response.data;
            });
            return promise;
        },
        enroll: function( enrollment ){
            var promise = $http.post(  '../api/enrollments', enrollment ).then(function(response){
                return response.data;
            });
            return promise;
        },
        update: function( enrollment){
            var promise = $http.put( '../api/enrollments/' + enrollment.enrollment , enrollment).then(function(response){
                return response.data;
            });
            return promise;
        },
        cancel: function(enrollment){
            var promise = $http.put('../api/enrollments/' + enrollment.enrollment + '/cancelled').then(function(response){
                return response.data;               
            });
            return promise;           
        },
        complete: function(enrollment){
            var promise = $http.put('../api/enrollments/' + enrollment.enrollment + '/completed').then(function(response){
                return response.data;               
            });
            return promise;           
        }        
    };   
})

/* Service for getting tracked entity instances */
.factory('TEService', function(StorageService, $q, $rootScope) {

    return {
        
        getAll: function(){            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.getAll('trackedEntities').done(function(entities){
                    $rootScope.$apply(function(){
                        def.resolve(entities);
                    });                    
                });
            });            
            return def.promise;
        },
        get: function(uid){            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.get('trackedEntities', uid).done(function(te){                    
                    $rootScope.$apply(function(){
                        def.resolve(te);
                    });
                });
            });                        
            return def.promise;            
        }
    };
})

/* Service for getting tracked entity instances */
.factory('TEIService', function($http, $q, AttributesFactory, DateUtils) {

    return {
        
        get: function(entityUid) {
            var promise = $http.get(  '../api/trackedEntityInstances/' +  entityUid ).then(function(response){     
                var tei = response.data;
                
                angular.forEach(tei.attributes, function(attribute){                   
                   if(attribute.type && attribute.value){                       
                       if(attribute.type === 'date'){                           
                           attribute.value = DateUtils.format(attribute.value);
                       }
                   } 
                });
                return tei;
            });            
            return promise;
        },        
        getByOrgUnitAndProgram: function(orgUnitUid, programUid) {

            var url = '../api/trackedEntityInstances.json?ou=' + orgUnitUid + '&program=' + programUid;
            
            var promise = $http.get( url ).then(function(response){               
                //return EntityService.formatter(response.data);
                return response.data;
            });            
            return promise;
        },
        getByOrgUnit: function(orgUnitUid) {           
            
            var url =  '../api/trackedEntityInstances.json?ou=' + orgUnitUid;
            
            var promise = $http.get( url ).then(function(response){                                
                //return EntityService.formatter(response.data);
                return response.data;
            });            
            return promise;
        },        
        search: function(ouId, ouMode, queryUrl, programUrl, attributeUrl, pager, paging) {
                
            var url =  '../api/trackedEntityInstances.json?ou=' + ouId + '&ouMode='+ ouMode;
            
            if(queryUrl){
                url = url + '&'+ queryUrl;
            }
            if(programUrl){
                url = url + '&' + programUrl;
            }
            if(attributeUrl){
                url = url + '&' + attributeUrl;
            }
            
            if(paging){
                var pgSize = pager ? pager.pageSize : 50;
                var pg = pager ? pager.page : 1;
                url = url + '&pageSize=' + pgSize + '&page=' + pg;
            }
            else{
                url = url + '&paging=false';
            }
            
            var promise = $http.get( url ).then(function(response){                                
                return response.data;
            });            
            return promise;
        },                
        update: function(tei){
            
            var url = '../api/trackedEntityInstances';
            var promise = $http.put( url + '/' + tei.trackedEntityInstance , tei).then(function(response){
                return response.data;
            });
            return promise;
        },
        register: function(tei){
            
            var url = '../api/trackedEntityInstances';
            
            var promise = $http.post(url, tei).then(function(response){
                return response.data;
            });
            return promise;
        },
        processAttributes: function(selectedTei, selectedProgram, selectedEnrollment){
            var def = $q.defer();            
            if(selectedTei.attributes){
                if(selectedProgram && selectedEnrollment){
                    //show attribute for selected program and enrollment
                    AttributesFactory.getByProgram(selectedProgram).then(function(atts){    
                        selectedTei.attributes = AttributesFactory.showRequiredAttributes(atts,selectedTei.attributes, true);
                        def.resolve(selectedTei);
                    }); 
                }
                if(selectedProgram && !selectedEnrollment){
                    //show attributes for selected program            
                    AttributesFactory.getByProgram(selectedProgram).then(function(atts){    
                        selectedTei.attributes = AttributesFactory.showRequiredAttributes(atts,selectedTei.attributes, false);
                        def.resolve(selectedTei);
                    }); 
                }
                if(!selectedProgram && !selectedEnrollment){
                    //show attributes in no program            
                    AttributesFactory.getWithoutProgram().then(function(atts){                
                        selectedTei.attributes = AttributesFactory.showRequiredAttributes(atts,selectedTei.attributes, false);     
                        def.resolve(selectedTei);
                    });
                }
            }       
            return def.promise;
        }
    };
})

/* Factory for getting tracked entity attributes */
.factory('AttributesFactory', function($q, $rootScope, StorageService, orderByFilter) {      

    return {
        getAll: function(){
            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.getAll('attributes').done(function(attributes){
                    angular.forEach(attributes, function(att){
                        if(att.optionSet){
                           StorageService.currentStore.get('optionSets', att.optionSet.id).done(function(optionSet){
                                att.optionSet = optionSet;
                            });
                        }
                        $rootScope.$apply(function(){
                            def.resolve(attributes);
                        });
                    });                    
                });
            });            
            return def.promise;            
        }, 
        getByProgram: function(program){
            
            var attributes = [];
            var programAttributes = [];

            var def = $q.defer();
            this.getAll().then(function(atts){
                angular.forEach(atts, function(attribute){
                    attributes[attribute.id] = attribute;
                });

                angular.forEach(program.programTrackedEntityAttributes, function(pAttribute){
                    var att = attributes[pAttribute.trackedEntityAttribute.id];
                    att.mandatory = pAttribute.mandatory;
                    if(pAttribute.displayInList){
                        att.displayInListNoProgram = true;
                    }                    
                    programAttributes.push(att);                
                });
                def.resolve(programAttributes);                                  
            });
            return def.promise;    
        },
        getWithoutProgram: function(){   
            
            var def = $q.defer();
            this.getAll().then(function(atts){
                var attributes = [];
                angular.forEach(atts, function(attribute){
                    if (attribute.displayInListNoProgram) {
                        attributes.push(attribute);
                    }
                });     
                def.resolve(attributes);             
            });     
            return def.promise;
        },        
        getMissingAttributesForEnrollment: function(tei, program){
            var def = $q.defer();
            this.getByProgram(program).then(function(atts){
                var programAttributes = atts;
                var existingAttributes = tei.attributes;
                var missingAttributes = [];
                
                for(var i=0; i<programAttributes.length; i++){
                    var exists = false;
                    for(var j=0; j<existingAttributes.length && !exists; j++){
                        if(programAttributes[i].id === existingAttributes[j].attribute){
                            exists = true;
                        }
                    }
                    if(!exists){
                        missingAttributes.push(programAttributes[i]);
                    }
                }
                def.resolve(missingAttributes);
            });            
            return def.promise();            
        },
        showRequiredAttributes: function(requiredAttributes, teiAttributes, fromEnrollment){        

            //first reset teiAttributes
            for(var j=0; j<teiAttributes.length; j++){
                teiAttributes[j].show = false;
                if(teiAttributes[j].type === 'number' && !isNaN(parseInt(teiAttributes[j].value))){
                    teiAttributes[j].value = parseInt(teiAttributes[j].value);
                }
            }

            //identify which ones to show
            for(var i=0; i<requiredAttributes.length; i++){
                var processed = false;
                for(var j=0; j<teiAttributes.length && !processed; j++){
                    if(requiredAttributes[i].id === teiAttributes[j].attribute){                    
                        processed = true;
                        teiAttributes[j].show = true;
                        teiAttributes[j].order = i;
                        teiAttributes[j].mandatory = requiredAttributes[i].mandatory ? requiredAttributes[i].mandatory : false;
                    }
                }

                if(!processed && fromEnrollment){//attribute was empty, so a chance to put some value
                    teiAttributes.push({show: true, order: i, mandatory: requiredAttributes[i].mandatory ? requiredAttributes[i].mandatory : false, attribute: requiredAttributes[i].id, displayName: requiredAttributes[i].name, type: requiredAttributes[i].valueType, value: ''});
                }                   
            }

            teiAttributes = orderByFilter(teiAttributes, '-order');
            teiAttributes.reverse();

            return teiAttributes;
        }
    };
})

/* factory for handling events */
.factory('DHIS2EventFactory', function($http, $q) {   
    
    return {     
        
        getEventsByStatus: function(entity, orgUnit, program, programStatus){   
            var promise = $http.get( '../api/events.json?' + 'trackedEntityInstance=' + entity + '&orgUnit=' + orgUnit + '&program=' + program + '&programStatus=' + programStatus  + '&paging=false').then(function(response){
                return response.data.events;
            });            
            return promise;
        },
        getEventsByProgram: function(entity, orgUnit, program){   
            var promise = $http.get( '../api/events.json?' + 'trackedEntityInstance=' + entity + '&orgUnit=' + orgUnit + '&program=' + program + '&paging=false').then(function(response){
                return response.data.events;
            });            
            return promise;
        },
        getByOrgUnitAndProgram: function(orgUnit, ouMode, program, startDate, endDate){
            var url;
            if(startDate && endDate){
                url = '../api/events.json?' + 'orgUnit=' + orgUnit + '&ouMode='+ ouMode + '&program=' + program + '&startDate=' + startDate + '&endDate=' + endDate + '&paging=false';
            }
            else{
                url = '../api/events.json?' + 'orgUnit=' + orgUnit + '&ouMode='+ ouMode + '&program=' + program + '&paging=false';
            }
            var promise = $http.get( url ).then(function(response){
                return response.data.events;
            });            
            return promise;
        },
        get: function(eventUid){            
            var promise = $http.get('../api/events/' + eventUid + '.json').then(function(response){               
                return response.data;
            });            
            return promise;
        },        
        create: function(dhis2Event){    
            var promise = $http.post('../api/events.json', dhis2Event).then(function(response){
                return response.data;           
            });
            return promise;            
        },
        delete: function(dhis2Event){
            var promise = $http.delete('../api/events/' + dhis2Event.event).then(function(response){
                return response.data;               
            });
            return promise;           
        },
        update: function(dhis2Event){   
            var promise = $http.put('../api/events/' + dhis2Event.event, dhis2Event).then(function(response){
                return response.data;         
            });
            return promise;
        },        
        updateForSingleValue: function(singleValue){   
            var promise = $http.put('../api/events/' + singleValue.event + '/' + singleValue.dataValues[0].dataElement, singleValue ).then(function(response){
                return response.data;
            });
            return promise;
        },
        updateForNote: function(dhis2Event){   
            var promise = $http.put('../api/events/' + dhis2Event.event + '/addNote', dhis2Event).then(function(response){
                return response.data;         
            });
            return promise;
        },
        updateForEventDate: function(dhis2Event){
            var promise = $http.put('../api/events/' + dhis2Event.event + '/updateEventDate', dhis2Event).then(function(response){
                return response.data;         
            });
            return promise;
        }
    };    
})

/* factory for handling event reports */
.factory('EventReportService', function($http, $q) {   
    
    return {        
        getEventReport: function(orgUnit, ouMode, program, startDate, endDate, programStatus, eventStatus, pager){ 
            var pgSize = pager ? pager.pageSize : 50;
        	var pg = pager ? pager.page : 1;
            var url = '../api/events/overdue.json?' + 'orgUnit=' + orgUnit + '&ouMode='+ ouMode + '&program=' + program + '&programStatus=' + programStatus + '&eventStatus='+ eventStatus + '&pageSize=' + pgSize + '&page=' + pg;
            if(startDate && endDate){
                url = url + '&startDate=' + startDate + '&endDate=' + endDate ;
            }
            var promise = $http.get( url ).then(function(response){
                return response.data;
            });            
            return promise;
        }
    };    
})

.factory('OperatorFactory', function(){
    
    var defaultOperators = ['IS', 'RANGE' ];
    var boolOperators = ['yes', 'no'];
    return{
        defaultOperators: defaultOperators,
        boolOperators: boolOperators
    };  
})

.service('EntityQueryFactory', function(OperatorFactory){  
    
    this.getAttributesQuery = function(attributes, enrollment){

        var query = {url: null, hasValue: false};
        
        angular.forEach(attributes, function(attribute){           

            if(attribute.valueType === 'date' || attribute.valueType === 'number'){
                var q = '';
                
                if(attribute.operator === OperatorFactory.defaultOperators[0]){
                    if(attribute.exactValue && attribute.exactValue !== ''){
                        query.hasValue = true;    
                        q += 'EQ:' + attribute.exactValue + ':';
                    }
                }                
                if(attribute.operator === OperatorFactory.defaultOperators[1]){
                    if(attribute.startValue && attribute.startValue !== ''){
                        query.hasValue = true;    
                        q += 'GT:' + attribute.startValue + ':';
                    }
                    if(attribute.endValue && attribute.endValue !== ''){
                        query.hasValue = true;    
                        q += 'LT:' + attribute.endValue + ':';
                    }
                }                
                if(query.url){
                    if(q){
                        q = q.substr(0,q.length-1);
                        query.url = query.url + '&filter=' + attribute.id + ':' + q;
                    }
                }
                else{
                    if(q){
                        q = q.substr(0,q.length-1);
                        query.url = 'filter=' + attribute.id + ':' + q;
                    }
                }
            }
            else{
                if(attribute.value && attribute.value !== ''){                    
                    query.hasValue = true;                

                    if(angular.isArray(attribute.value)){
                        var q = '';
                        angular.forEach(attribute.value, function(val){                        
                            q += val + ';';
                        });

                        q = q.substr(0,q.length-1);

                        if(query.url){
                            if(q){
                                query.url = query.url + '&filter=' + attribute.id + ':IN:' + q;
                            }
                        }
                        else{
                            if(q){
                                query.url = 'filter=' + attribute.id + ':IN:' + q;
                            }
                        }                    
                    }
                    else{                        
                        if(query.url){
                            query.url = query.url + '&filter=' + attribute.id + ':LIKE:' + attribute.value;
                        }
                        else{
                            query.url = 'filter=' + attribute.id + ':LIKE:' + attribute.value;
                        }
                    }
                }
            }            
        });
        
        if(enrollment){
            var q = '';
            if(enrollment.operator === OperatorFactory.defaultOperators[0]){
                if(enrollment.programExactDate && enrollment.programExactDate !== ''){
                    query.hasValue = true;
                    q += '&programStartDate=' + enrollment.programExactDate + '&programEndDate=' + enrollment.programExactDate;
                }
            }
            if(enrollment.operator === OperatorFactory.defaultOperators[1]){
                if(enrollment.programStartDate && enrollment.programStartDate !== ''){                
                    query.hasValue = true;
                    q += '&programStartDate=' + enrollment.programStartDate;
                }
                if(enrollment.programEndDate && enrollment.programEndDate !== ''){
                    query.hasValue = true;
                    q += '&programEndDate=' + enrollment.programEndDate;
                }
            }            
            if(q){
                if(query.url){
                    query.url = query.url + q;
                }
                else{
                    query.url = q;
                }
            }            
        }
        return query;
        
    };   
    
    this.resetAttributesQuery = function(attributes, enrollment){
        
        angular.forEach(attributes, function(attribute){
            attribute.exactValue = '';
            attribute.startValue = '';
            attribute.endValue = '';
            attribute.value = '';           
        });
        
        if(enrollment){
            enrollment.programStartDate = '';
            enrollment.programEndDate = '';          
        }        
        return attributes;        
    }; 
})

/* service for dealing with custom form */
.service('CustomFormService', function(){
    
    return {
        getForProgramStage: function(programStage){
            
            if(!programStage){
                return null;
            }
            
            var htmlCode = programStage.dataEntryForm ? programStage.dataEntryForm.htmlCode : null;  
            
            if(htmlCode){                
            
                var programStageDataElements = [];

                angular.forEach(programStage.programStageDataElements, function(prStDe){
                    programStageDataElements[prStDe.dataElement.id] = prStDe;
                });

                var inputRegex = /<input.*?\/>/g,
                    match,
                    inputFields = [];                

                while (match = inputRegex.exec(htmlCode)) {                
                    inputFields.push(match[0]);
                }
                
                for(var i=0; i<inputFields.length; i++){                    
                    var inputField = inputFields[i];                    
                    var inputElement = $.parseHTML( inputField );
                    var attributes = {};
                                       
                    $(inputElement[0].attributes).each(function() {
                        attributes[this.nodeName] = this.nodeValue;                       
                    });
                    
                    var deId = '', newInputField;     
                    if(attributes.hasOwnProperty('id')){
                        deId = attributes['id'].substring(4, attributes['id'].length-1).split("-")[1]; 
                     
                        //name needs to be unique so that it can be used for validation in angularjs
                        if(attributes.hasOwnProperty('name')){
                            attributes['name'] = deId;
                        }
                        
                        //check data element type and generate corresponding angular input field
                        if(programStageDataElements[deId].dataElement.type == "int"){
                            newInputField = '<input type="number" ' +
                                            this.getAttributesAsString(attributes) +
                                            ' ng-model="currentEvent.' + deId + '"' +
                                            ' ng-class="getInputNotifcationClass(' + deId + ',true)"' +
                                            ' ng-blur="saveDatavalue(programStageDataElements.'+ deId + ')"' + 
                                            ' ng-required="programStageDataElements.' + deId + '.compulsory">';
                        }
                        if(programStageDataElements[deId].dataElement.type == "string"){
                            newInputField = '<input type="text" ' +
                                            this.getAttributesAsString(attributes) +
                                            ' ng-model="currentEvent.' + deId + '" ' +
                                            ' ng-class="getInputNotifcationClass(' + deId + ',true)"' +
                                            ' ng-required="programStageDataElements.' + deId + '.compulsory"' +
                                            ' ng-blur="saveDatavalue(programStageDataElements.'+ deId + ')"' + 
                                            ' typeahead="option.code as option.name for option in programStageDataElements.'+deId+'.dataElement.optionSet.options | filter:$viewValue | limitTo:20"' +
                                            ' typeahead-open-on-focus ng-required="programStageDataElements.'+deId+'.compulsory">';
                        }
                        if(programStageDataElements[deId].dataElement.type == "bool"){
                            newInputField = '<select ' +
                                            this.getAttributesAsString(attributes) +
                                            ' ng-model="currentEvent.' + deId + '" ' +
                                            ' ng-class="getInputNotifcationClass(' + deId + ',true)"' +
                                            ' ng-change="saveDatavalue(programStageDataElements.'+ deId + ')"' + 
                                            ' ng-required="programStageDataElements.' + deId + '.compulsory">' + 
                                            '<option value="">{{\'please_select\'| translate}}</option>' +
                                            '<option value="false">{{\'no\'| translate}}</option>' + 
                                            '<option value="true">{{\'yes\'| translate}}</option>' +
                                            '</select>';
                        }
                        if(programStageDataElements[deId].dataElement.type == "date"){
                            newInputField = '<input type="text" ' +
                                            this.getAttributesAsString(attributes) +
                                            ' ng-model="currentEvent.' + deId + '"' +
                                            ' ng-class="getInputNotifcationClass(' + deId + ',true)"' +
                                            ' ng-date' +
                                            ' blur-or-change="saveDatavalue(programStageDataElements.'+ deId + ')"' + 
                                            ' ng-required="programStageDataElements.' + deId + '.compulsory">';
                        }
                        if(programStageDataElements[deId].dataElement.type == "trueOnly"){
                            newInputField = '<input type="checkbox" ' +
                                            this.getAttributesAsString(attributes) +
                                            ' ng-model="currentEvent.' + deId + '"' +
                                            ' ng-class="getInputNotifcationClass(' + deId + ',true)"' +
                                            ' ng-change="saveDatavalue(programStageDataElements.'+ deId + ')"' + 
                                            ' ng-required="programStageDataElements.' + deId + '.compulsory">';
                        }
                        htmlCode = htmlCode.replace(inputField, newInputField);
                    }
                }
                
                return htmlCode;
                
            }
            
            return null;
        },
        getAttributesAsString: function(attributes){
            if(attributes){
                var attributesAsString = '';                
                for(var prop in attributes){
                    if(prop != 'value'){
                        attributesAsString += prop + '="' + attributes[prop] + '" ';
                    }
                }
                return attributesAsString;
            }
            return null;
        }
    };            
})

/* Modal service for user interaction */
.service('ModalService', ['$modal', function($modal) {

    var modalDefaults = {
        backdrop: true,
        keyboard: true,
        modalFade: true,
        templateUrl: 'views/modal.html'
    };

    var modalOptions = {
        closeButtonText: 'Close',
        actionButtonText: 'OK',
        headerText: 'Proceed?',
        bodyText: 'Perform this action?'
    };

    this.showModal = function(customModalDefaults, customModalOptions) {
        if (!customModalDefaults)
            customModalDefaults = {};
        customModalDefaults.backdrop = 'static';
        return this.show(customModalDefaults, customModalOptions);
    };

    this.show = function(customModalDefaults, customModalOptions) {
        //Create temp objects to work with since we're in a singleton service
        var tempModalDefaults = {};
        var tempModalOptions = {};

        //Map angular-ui modal custom defaults to modal defaults defined in service
        angular.extend(tempModalDefaults, modalDefaults, customModalDefaults);

        //Map modal.html $scope custom properties to defaults defined in service
        angular.extend(tempModalOptions, modalOptions, customModalOptions);

        if (!tempModalDefaults.controller) {
            tempModalDefaults.controller = function($scope, $modalInstance) {
                $scope.modalOptions = tempModalOptions;
                $scope.modalOptions.ok = function(result) {
                    $modalInstance.close(result);
                };
                $scope.modalOptions.close = function(result) {
                    $modalInstance.dismiss('cancel');
                };
            };
        }

        return $modal.open(tempModalDefaults).result;
    };

}])

/* Dialog service for user interaction */
.service('DialogService', ['$modal', function($modal) {

        var dialogDefaults = {
            backdrop: true,
            keyboard: true,
            backdropClick: true,
            modalFade: true,            
            templateUrl: 'views/dialog.html'
        };

        var dialogOptions = {
            closeButtonText: 'close',
            actionButtonText: 'ok',
            headerText: 'dhis2_tracker',
            bodyText: 'Perform this action?'
        };

        this.showDialog = function(customDialogDefaults, customDialogOptions) {
            if (!customDialogDefaults)
                customDialogDefaults = {};
            customDialogDefaults.backdropClick = false;
            return this.show(customDialogDefaults, customDialogOptions);
        };

        this.show = function(customDialogDefaults, customDialogOptions) {
            //Create temp objects to work with since we're in a singleton service
            var tempDialogDefaults = {};
            var tempDialogOptions = {};

            //Map angular-ui modal custom defaults to modal defaults defined in service
            angular.extend(tempDialogDefaults, dialogDefaults, customDialogDefaults);

            //Map modal.html $scope custom properties to defaults defined in service
            angular.extend(tempDialogOptions, dialogOptions, customDialogOptions);

            if (!tempDialogDefaults.controller) {
                tempDialogDefaults.controller = function($scope, $modalInstance) {
                    $scope.dialogOptions = tempDialogOptions;
                    $scope.dialogOptions.ok = function(result) {
                        $modalInstance.close(result);
                    };                           
                };
            }

            return $modal.open(tempDialogDefaults).result;
        };

    }])

/* current selections */
.service('CurrentSelection', function(){
    this.currentSelection = '';
    this.relationshipInfo = '';
    
    this.set = function(currentSelection){  
        this.currentSelection = currentSelection;        
    };
    
    this.get = function(){
        return this.currentSelection;
    };
    
    this.setRelationshipInfo = function(relationshipInfo){  
        this.relationshipInfo = relationshipInfo;        
    };
    
    this.getRelationshipInfo = function(){
        return this.relationshipInfo;
    };
})

/* Context menu for grid*/
.service('ContextMenuSelectedItem', function(){
    this.selectedItem = '';
    
    this.setSelectedItem = function(selectedItem){  
        this.selectedItem = selectedItem;        
    };
    
    this.getSelectedItem = function(){
        return this.selectedItem;
    };
})

/* Translation service - gets logged in user profile for the server, 
 * and apply user's locale to translation
 */
.service('TranslationService', function($translate, storage){
    
    this.translate = function(){
        var profile = storage.get('USER_PROFILE');        
        if( profile ){        
            $translate.uses(profile.settings.keyUiLocale);
        }
    };
})

/* Pagination service */
/* Pagination service */
.service('Paginator', function () {
    this.page = 1;
    this.pageSize = 50;
    this.itemCount = 0;
    this.pageCount = 0;
    this.toolBarDisplay = 5;

    this.setPage = function (page) {
        if (page > this.getPageCount()) {
            return;
        }

        this.page = page;
    };
    
    this.getPage = function(){
        return this.page;
    };
    
    this.setPageSize = function(pageSize){
      this.pageSize = pageSize;
    };
    
    this.getPageSize = function(){
        return this.pageSize;
    };
    
    this.setItemCount = function(itemCount){
      this.itemCount = itemCount;
    };
    
    this.getItemCount = function(){
        return this.itemCount;
    };
    
    this.setPageCount = function(pageCount){
        this.pageCount = pageCount;
    };

    this.getPageCount = function () {
        return this.pageCount;
    };

    this.lowerLimit = function() { 
        var pageCountLimitPerPageDiff = this.getPageCount() - this.toolBarDisplay;

        if (pageCountLimitPerPageDiff < 0) { 
            return 0; 
        }

        if (this.getPage() > pageCountLimitPerPageDiff + 1) { 
            return pageCountLimitPerPageDiff; 
        } 

        var low = this.getPage() - (Math.ceil(this.toolBarDisplay/2) - 1); 

        return Math.max(low, 0);
    };
})


/*this is just a hack - there should be better way */
.service('ValidDate', function(){    
    var dateValidation;    
    return {
        get: function(dt) {
            dateValidation = dt;
        },
        set: function() {    
            return dateValidation;
        }
    };
            
})

.service('TEIGridService', function(OrgUnitService, DateUtils, $translate){
    
    return {
        format: function(grid, map){
            if(!grid || !grid.rows){
                return;
            }
            
            //grid.headers[0-4] = Instance, Created, Last updated, Org unit, Tracked entity
            //grid.headers[5..] = Attribute, Attribute,.... 
            var attributes = [];
            for(var i=5; i<grid.headers.length; i++){
                attributes.push({id: grid.headers[i].name, name: grid.headers[i].column, type: grid.headers[i].type});
            }

            var entityList = [];

            OrgUnitService.open().then(function(){

                angular.forEach(grid.rows, function(row){
                    var entity = {};
                    var isEmpty = true;

                    entity.id = row[0];
                    var rDate = row[1];
                    rDate = DateUtils.format(rDate);
                    entity.created = rDate;
                    entity.orgUnit = row[3];                              
                    entity.type = row[4];  

                    OrgUnitService.get(row[3]).then(function(ou){
                        if(ou){
                            entity.orgUnitName = ou.n;
                        }                                                       
                    });

                    for(var i=5; i<row.length; i++){
                        if(row[i] && row[i] !== ''){
                            isEmpty = false;
                            entity[grid.headers[i].name] = row[i];
                        }
                    }

                    if(!isEmpty){
                        if(map){
                            entityList[entity.id] = entity;
                        }
                        else{
                            entityList.push(entity);
                        }
                    }        
                });                
            });
            return {headers: attributes, rows: entityList, pager: grid.metaData.pager};                                    
        },
        generateGridColumns: function(attributes, ouMode){
            
            var columns = attributes ? angular.copy(attributes) : [];
       
            //also add extra columns which are not part of attributes (orgunit for example)
            columns.push({id: 'orgUnitName', name: $translate('registering_unit'), type: 'string', displayInListNoProgram: false});
            columns.push({id: 'created', name: $translate('registration_date'), type: 'date', displayInListNoProgram: false});

            //generate grid column for the selected program/attributes
            angular.forEach(columns, function(column){
                if(column.id === 'orgUnitName' && ouMode !== 'SELECTED'){
                    column.show = true;    
                }

                if(column.displayInListNoProgram || column.displayInList){
                    column.show = true;
                }  
                column.showFilter = false;
            });     
            
            return columns;  
        },
        getData: function(rows, columns){
            var data = [];
            angular.forEach(rows, function(row){
                var d = {};
                angular.forEach(columns, function(col){
                    if(col.show){
                        d[col.name] = row[col.id];
                    }                
                });
                data.push(d);            
            });
            return data;
        },
        getHeader: function(columns){
            var header = []; 
            angular.forEach(columns, function(col){
                if(col.show){
                    header.push($translate(col.name));
                }
            });        
            return header;
        }
    };
})

.service('DateUtils', function($filter){
    
    return {
        format: function(dateValue) {            
            dateValue = moment(dateValue, 'YYYY-MM-DD')._d;
            dateValue = Date.parse(dateValue);
            dateValue = $filter('date')(dateValue, 'yyyy-MM-dd');
            return dateValue;
        },
        formatToHrsMins: function(dateValue) {            
            return moment(dateValue).format('YYYY-MM-DD @ hh:mm A');
        }
    };            
})

.service('EventUtils', function($filter, DateUtils, OrgUnitService){
    return {
        createDummyEvent: function(programStage, orgUnit, enrollment){
            
            var today = DateUtils.format(moment());
    
            var dueDate = this.getEventDueDate(programStage, enrollment);
            var dummyEvent = {programStage: programStage.id, 
                              orgUnit: orgUnit.id,
                              orgUnitName: orgUnit.name,
                              dueDate: dueDate,
                              sortingDate: dueDate,
                              name: programStage.name,
                              reportDateDescription: programStage.reportDateDescription,
                              status: 'SCHEDULED'};
            dummyEvent.statusColor = 'alert alert-warning';//'stage-on-time';
            if(moment(today).isAfter(dummyEvent.dueDate)){
                dummyEvent.statusColor = 'alert alert-danger';//'stage-overdue';
            }
            return dummyEvent;        
        },
        getEventStatusColor: function(dhis2Event){    
            var today = DateUtils.format(moment());
            var eventDate = today;
            
            if(dhis2Event.eventDate){
                eventDate = dhis2Event.eventDate;
            }
    
            if(dhis2Event.status === 'COMPLETED'){
                return 'alert alert-success';//'stage-completed';
            }
            else if(dhis2Event.status === 'SKIPPED'){
                return 'alert alert-default'; //'stage-skipped';
            }
            else{                
                if(dhis2Event.eventDate){
                    return 'alert alert-info'; //'stage-executed';
                }
                else{
                    if(moment(eventDate).isAfter(dhis2Event.dueDate)){
                        return 'alert alert-danger';//'stage-overdue';
                    }                
                    return 'alert alert-warning';//'stage-on-time';
                }               
            }            
        },
        getEventDueDate: function(programStage, enrollment){
            var dueDate = DateUtils.format(enrollment.dateOfIncident);
            dueDate = moment(dueDate).add('d', programStage.minDaysFromStart)._d;
            dueDate = DateUtils.format(dueDate);
            return dueDate;
        },
        getEventOrgUnitName: function(orgUnitId){            
            if(orgUnitId){
                OrgUnitService.open().then(function(){
                    OrgUnitService.get(orgUnitId).then(function(ou){
                        if(ou){
                            return ou.n;             
                        }                                                       
                    });                            
                }); 
            }
        },
        setEventOrgUnitName: function(dhis2Event){            
            if(dhis2Event.orgUnit){
                OrgUnitService.open().then(function(){
                    OrgUnitService.get(dhis2Event.orgUnit).then(function(ou){
                        if(ou){
                            dhis2Event.eventOrgUnitName = ou.n;
                            return dhis2Event;                            
                        }                                                       
                    });                            
                }); 
            }
        },
        reconstruct: function(dhis2Event, programStage){
            
            var e = {dataValues: [], 
                    event: dhis2Event.event, 
                    program: dhis2Event.program, 
                    programStage: dhis2Event.programStage, 
                    orgUnit: dhis2Event.orgUnit, 
                    trackedEntityInstance: dhis2Event.trackedEntityInstance,
                    status: dhis2Event.status,
                    dueDate: dhis2Event.dueDate
                };
                
            angular.forEach(programStage.programStageDataElements, function(prStDe){
                if(dhis2Event[prStDe.dataElement.id]){
                    var val = {value: dhis2Event[prStDe.dataElement.id], dataElement: prStDe.dataElement.id};
                    if(dhis2Event.providedElsewhere[prStDe.dataElement.id]){
                        val.providedElsewhere = dhis2Event.providedElsewhere[prStDe.dataElement.id];
                    }
                    e.dataValues.push(val);
                }                                
            });
                     
            return e;
        }
    }; 
});