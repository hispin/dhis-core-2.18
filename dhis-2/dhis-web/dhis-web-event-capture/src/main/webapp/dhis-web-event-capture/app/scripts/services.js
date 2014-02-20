'use strict';

/* Services */

var eventCaptureServices = angular.module('eventCaptureServices', ['ngResource'])

/* Factory to fetch programs */
.factory('ProgramFactory', function($http, storage) {
    
    var dhis2Url = storage.get('CONFIG').activities.dhis.href;   
    
    var programUid, programPromise;
    var programs, programsPromise;
    var program;
    return {
        
        get: function(uid){
            if( programUid !== uid ){
                programPromise = $http.get(dhis2Url + '/api/programs/' + uid + '.json?viewClass=detailed&paging=false').then(function(response){
                    programUid = response.data.id; 
                    program = response.data;                     
                    return program;
                });
            }
            return programPromise;
        },       
        
        getMine: function(type){ 
            if( !programsPromise ){
                programsPromise = $http.get(dhis2Url + '/api/me/programs?includeDescendants=true&type='+type).then(function(response){
                   programs = response.data;
                   return programs;
                });
            }
            return programsPromise;    
        }
    };
})

/* Factory to fetch programStages */
.factory('ProgramStageFactory', function($http, storage) {  
    
    var dhis2Url = storage.get('CONFIG').activities.dhis.href;  
    
    var programStage, promise;   
    return {        
        get: function(uid){
            if( programStage !== uid ){
                promise = $http.get( dhis2Url + '/api/programStages/' + uid + '.json?viewClass=detailed&paging=false').then(function(response){
                   programStage = response.data.id;

                   //store locally - might need them for event data values
                   angular.forEach(response.data.programStageDataElements, function(prStDe){      
                       storage.set(prStDe.dataElement.id, prStDe);                       
                   });
                   
                   return response.data;
                });
            }
            return promise;
        }
    };    
})

/* factory for loading logged in user profiles from DHIS2 */
.factory('CurrentUserProfile', function($http, storage) { 
    
    var dhis2Url = storage.get('CONFIG').activities.dhis.href;      
           
    var profile, promise;
    return {
        get: function() {
            if( !promise ){
                promise = $http.get(dhis2Url + '/api/me/profile').then(function(response){
                   profile = response.data;
                   return profile;
                });
            }
            return promise;           
        }
    };    
})

/* Factory to enroll person in a program */
.service('EnrollmentFactory', function($http, storage) {
    
    var dhis2Url = storage.get('CONFIG').activities.dhis.href;      
    
    var EnrollmentFactory = {};

    EnrollmentFactory.enrollPerson = function( enrollment ) {
        return $http.post( dhis2Url + '/api/enrollments', enrollment );
    };
    
    EnrollmentFactory.getEnrollment = function( orgUnit, program, person, status ){
        return $http.get( dhis2Url + '/api/enrollments?orgunit=' + orgUnit + '&program=' + program + '&person=' + person + '&status=' + status);
    };

    return EnrollmentFactory;
})

/* Factory for loading OrgUnit */
.factory('OrgUnitFactory', function($http, storage) {
    
    var dhis2Url = storage.get('CONFIG').activities.dhis.href;     
    
    var orgUnit, orgUnitPromise, myOrgUnits, myOrgUnitsPromise, allOrgUnits, allOrgUnitsPromise;
    
    return {
        get: function(uid){
            
            if( orgUnit != uid ){
                orgUnitPromise = $http.get(dhis2Url + '/api/organisationUnits/' + uid + '.json').then(function(response){
                    orgUnit = response.data.id;
                    return response.data;
                });
            }
            return orgUnitPromise;
        },
        
        getMine: function(){
            if(!myOrgUnitsPromise){
                myOrgUnitsPromise = $http.get(dhis2Url + '/api/me/organisationUnits').then(function(response){
                    return response.data;
                });
            }
            return myOrgUnitsPromise;
        },
        
        getAll: function(){
            if(!allOrgUnitsPromise){
                allOrgUnitsPromise = http.get(dhis2Url + '/api/organisationUnits.json').then(function(response){
                    return response.data;
                });
            }
            return allOrgUnitsPromise;
        }
    }; 
})

/* Factory for getting person */
.factory('PersonFactory', function($http, storage, PersonAttributesFactory, LocalAttributesFactory, orderByFilter) {
    
    var dhis2Url = storage.get('CONFIG').activities.dhis.href;      
    
    return {
        getPregnantWoman: function(uid){
            var promise = $http.get(dhis2Url + '/api/persons/' + uid + '.json').then(function(response){                
                var person = response.data;
                
                //This is is to have consistent display of person and attributes - because every person might not have value for every attribute. 
                //But we need to show all attributes in any way.                   
                PersonAttributesFactory.getPregnantWomanRegistrationAttributes().then(function(data){
                    var pregnantWomanRegistrationAttributes = data.personAttributeTypes;
                    
                    //assume every person has values for the attributes - initially all are empty values
                    angular.forEach(pregnantWomanRegistrationAttributes, function(attribute){                                        
                       attribute.value = '';
                       var loop = true;
                       for(var i=0; i<person.attributes.length && loop; i++){                           
                           if(person.attributes[i].attribute == attribute.id){
                               attribute.value = person.attributes[i].value;
                           }
                       }
                    });  

                    pregnantWomanRegistrationAttributes = orderByFilter(pregnantWomanRegistrationAttributes, '-order');
                    pregnantWomanRegistrationAttributes.reverse();
                    
                    person.attributes = pregnantWomanRegistrationAttributes;                    
                });                
                return person;
            });            
            return promise;
        },
        getContactPerson: function(uid){
            var promise = $http.get(dhis2Url + '/api/persons/' + uid + '.json').then(function(response){                
                var person = response.data;
                
                //This is is to have consistent display of person and attributes - because every person might not have value for every attribute. 
                //But we need to show all attributes in any way.                   
                PersonAttributesFactory.getContactPersonRegistrationAttributes().then(function(data){
                    var contactPersonRegistrationAttributes = data.personAttributeTypes;
                    
                    //assume every person has values for the attributes - initially all are empty values
                    angular.forEach(contactPersonRegistrationAttributes, function(attribute){                                        
                       attribute.value = '';
                       var loop = true;
                       for(var i=0; i<person.attributes.length && loop; i++){                           
                           if(person.attributes[i].attribute == attribute.id){
                               attribute.value = person.attributes[i].value;
                           }
                       }
                    });  

                    contactPersonRegistrationAttributes = orderByFilter(contactPersonRegistrationAttributes, '-order');
                    contactPersonRegistrationAttributes.reverse();
                    
                    person.attributes = contactPersonRegistrationAttributes;                    
                });
                
                return person;
            });            
            return promise;
        },        
        register: function(person){
            var promise = $http.post(dhis2Url + '/api/persons', person).then(function(response){
                return response.data;
            });
            return promise;
        },        
        update: function(person){
            var promise = $http.put(dhis2Url + '/api/persons/' + person.person , person).then(function(response){
                return response.data;
            });
            return promise;
        }
    };
})

/* Service for getting person profile - including program related attribtues */
.factory('PersonService', function($http, storage, LocalAttributesFactory, orderByFilter){
    
    var dhis2Url = storage.get('CONFIG').activities.dhis.href;      

    return {        
        getPregnantWoman: function(uid){
            var promise = $http.get(dhis2Url + '/api/persons/' + uid + '.json').then(function(response){                
                var person = response.data;
                
                //This is is to have consistent display of person and attributes - because every person might not have value for every attribute. 
                //But we need to show all attributes in any way.                   
                LocalAttributesFactory.get().then(function(data){

                    //assume every person has values for the attributes - initially all are empty values
                    var newAttributes = [];
                    angular.forEach(data.pregnantWoman, function(localAttribute){                                        
                        var newAttribute = {displayName: localAttribute.name, 
                                            code: localAttribute.code, 
                                            order: localAttribute.order,
                                            mandatoryToDisplay: localAttribute.mandatoryToDisplay,
                                            value: ''};
                        angular.forEach(person.attributes, function(attribute){
                            if(attribute.code == newAttribute.code){
                                newAttribute.value = attribute.value;
                            }                               
                        });   
                        
                        newAttributes.push(newAttribute);
                        
                    });  
                    
                    newAttributes= orderByFilter(newAttributes, '-order');
                    newAttributes.reverse();

                    person.attributes = newAttributes;                   
                });
                
                return person;
            });            
            return promise;
        },
        getContactPerson: function(uid){
            var promise = $http.get(dhis2Url + '/api/persons/' + uid + '.json').then(function(response){                
                var person = response.data;
                
                //This is is to have consistent display of person and attributes - because every person might not have value for every attribute. 
                //But we need to show all attributes in any way.                   
                LocalAttributesFactory.get().then(function(data){

                    //assume every person has values for the attributes - initially all are empty values
                    var newAttributes = [];
                    angular.forEach(data.contactPerson, function(localAttribute){                                        
                        var newAttribute = {displayName: localAttribute.name, 
                                            code: localAttribute.code, 
                                            order: localAttribute.order,
                                            mandatoryToDisplay: localAttribute.mandatoryToDisplay,
                                            value: ''};
                        angular.forEach(person.attributes, function(attribute){
                            if(attribute.code == newAttribute.code){
                                newAttribute.value = attribute.value;
                            }                               
                        });   
                        
                        newAttributes.push(newAttribute);
                        
                    });  
                    
                    newAttributes= orderByFilter(newAttributes, '-order');
                    newAttributes.reverse();

                    person.attributes = newAttributes;                   
                });                
                return person;
            });            
            return promise;
        },        
        getAllPregnantWomen: function(orgUnitUid, programUid){
            var promise = $http.get(dhis2Url + '/api/persons.json?orgUnit=' + orgUnitUid + '&program=' + programUid + '&paging=false').then(function(response){                

                var personList = response.data.personList;
                
                //This is is to have consistent display of person and attributes - because every person might not have value for every attribute. 
                //But we need to show all attributes in any way.                   
                LocalAttributesFactory.get().then(function(data){
                    
                    angular.forEach(personList, function(person){   
                         
                        //assume every person has values for the attributes - initially all are empty values
                        var newAttributes = [];
                        angular.forEach(data.pregnantWoman, function(localAttribute){                                        
                            var newAttribute = {displayName: localAttribute.name, 
                                                code: localAttribute.code, 
                                                order: localAttribute.order,
                                                mandatoryToDisplay: localAttribute.mandatoryToDisplay,
                                                value: ''};
                            angular.forEach(person.attributes, function(attribute){
                                if(attribute.code == newAttribute.code){
                                    newAttribute.value = attribute.value;
                                }                               
                            });                                            
                            newAttributes.push(newAttribute);
                        });            
                        
                        newAttributes= orderByFilter(newAttributes, '-order');
                        newAttributes.reverse();
                        
                        person.attributes = newAttributes;
                        
                    });
                });

                return personList;
            }); 
            
            return promise;
        }
    };            
})

/* Factory for getting person attribute types*/
.factory('PersonAttributesFactory', function($http, storage, LocalAttributesFactory, orderByFilter) { 
    
    var dhis2Url = storage.get('CONFIG').activities.dhis.href; 

    return {
        getEnrollmentAttributes: function(uid){
            var attributes, promise;
            if( !attributes && !promise ){                
                rPromise = $http.get(dhis2Url + '/api/personAttributeTypes.json?program=' + uid + '&viewClass=detailed&paging=false').then(function(response){                    
                    angular.forEach(response.data.personAttributeTypes, function(registrationAttribute) {
                        if (angular.isObject(registrationAttribute.personAttributeOptions)) {
                            angular.forEach(registrationAttribute.personAttributeOptions, function(attributeOption) {
                                attributeOption.value = attributeOption.name;
                            });
                        }
                    });                     
                   attributes = response.data;
                   return attributes;
                });
            }
            return promise;
        },       
        getPregnantWomanRegistrationAttributes: function(){
            var attributes, promise;
            if(!attributes && !promise){
                promise = $http.get(dhis2Url + '/api/personAttributeTypes.json?withoutPrograms=true&viewClass=detailed&paging=false').then(function(response){                    
                    attributes = response.data;
                    var pAttributes = [];
                    LocalAttributesFactory.get().then(function(data){     

                        for(var j=0; j<response.data.personAttributeTypes.length; j++){
                            if (angular.isObject(response.data.personAttributeTypes[j].personAttributeOptions)) {
                                angular.forEach(response.data.personAttributeTypes[j].personAttributeOptions, function(attributeOption) {
                                    attributeOption.value = attributeOption.name;
                                });
                            }
                            
                            var pLoop = true;
                            for(var i=0; i<data.pregnantWoman.length && pLoop; i++){
                                if(response.data.personAttributeTypes[j].code == data.pregnantWoman[i].code){
                                    response.data.personAttributeTypes[j].order = data.pregnantWoman[i].order;
                                    pAttributes.push(response.data.personAttributeTypes[j]);
                                    pLoop = false;
                                }
                            }

                            if(pLoop){                                
                                response.data.personAttributeTypes.splice(j, 1 );
                                j--;
                            }
                        }             
                    });
                    return response.data;
                });
            }
            return promise;
        },
        getContactPersonRegistrationAttributes: function(){
            var attributes, promise;
            if(!attributes && !promise){
                promise = $http.get(dhis2Url + '/api/personAttributeTypes.json?withoutPrograms=true&viewClass=detailed&paging=false').then(function(response){                    
                    
                    var cAttributes = [];
                    LocalAttributesFactory.get().then(function(data){     

                        for(var j=0; j<response.data.personAttributeTypes.length; j++){
                            if (angular.isObject(response.data.personAttributeTypes[j].personAttributeOptions)) {
                                angular.forEach(response.data.personAttributeTypes[j].personAttributeOptions, function(attributeOption) {
                                    attributeOption.value = attributeOption.name;
                                });
                            }
                            
                            var pLoop = true;
                            for(var i=0; i<data.contactPerson.length && pLoop; i++){
                                if(response.data.personAttributeTypes[j].code == data.contactPerson[i].code){
                                    response.data.personAttributeTypes[j].order = data.contactPerson[i].order;
                                    cAttributes.push(response.data.personAttributeTypes[j]);
                                    pLoop = false;
                                }
                            }

                            if(pLoop){                                
                                response.data.personAttributeTypes.splice(j, 1 );
                                j--;
                            }
                        }             
                    });
                    return response.data;
                });
            }
            return promise;
        },
        getRegistrationAttributes: function(){
            var promise, attributes;
            if(!attributes && !promise){
                promise = $http.get(dhis2Url + '/api/personAttributeTypes.json?withoutPrograms=true&viewClass=detailed&paging=false').then(function(response){                                                            
                    attributes = response.data.personAttributeTypes;
                    /*var pAttributes = [], cAttributes = [];                    
                    LocalAttributesFactory.get().then(function(data){     

                        angular.forEach(attributes, function(attribute){
                            if (angular.isObject(attribute.personAttributeOptions)) {
                                angular.forEach(attribute.personAttributeOptions, function(attributeOption) {
                                    attributeOption.value = attributeOption.name;
                                });
                            }
                            
                            var loop = true;                                                     
                            for(var i=0; i<data.pregnantWoman.length && loop; i++){
                                if(attribute.code == data.pregnantWoman[i].code){
                                    var att = {code: attribute.code,
                                                     id: attribute.id,
                                                     mandatory: attribute.mandatory,
                                                     name: attribute.name,
                                                     valueType: attribute.valueType,
                                                     personAttributeOptions: attribute.personAttributeOptions,
                                                     order: data.pregnantWoman[i].order,                                          
                                                     mandatoryToDisplay: data.pregnantWoman[i].mandatoryToDisplay
                                                 }; 
                                    pAttributes.push(att);
                                    loop = false;
                                }
                            }

                            loop = true;                                                     
                            for(var i=0; i<data.contactPerson.length && loop; i++){
                                if(attribute.code == data.contactPerson[i].code){
                                    var att = {code: attribute.code,
                                                     id: attribute.id,
                                                     mandatory: attribute.mandatory,
                                                     name: attribute.name,
                                                     valueType: attribute.valueType,
                                                     personAttributeOptions: attribute.personAttributeOptions,
                                                     order: data.contactPerson[i].order, 
                                                     mandatoryToDisplay: data.contactPerson[i].mandatoryToDisplay
                                                 };                        
                                    cAttributes.push(att);
                                    loop = false;
                                }
                            }                         
                        });
                    
                        pAttributes= orderByFilter(pAttributes, '-order');
                        pAttributes.reverse();

                        cAttributes= orderByFilter(cAttributes, '-order');
                        cAttributes.reverse(); 
                    
                        attributes.pregnantWoman = pAttributes;  
                        attributes.contactPerson = cAttributes;     
                        
                    });*/                    
                    return attributes;
                });
            }
            return promise;
        }
    };
})

/* Factory to fetch relationships */
.factory('RelationshipFactory', function($http, storage) {
    
    var dhis2Url = storage.get('CONFIG').activities.dhis.href;  
    var relationships, promise;        
    return {        
        get: function(){
            if( !relationships && !promise ){
                promise = $http.get(dhis2Url + '/api/relationshipTypes.json?paging=false').then(function(response){                                                                            
                    relationships = response.data.relationshipTypes;                    
                    return relationships;
                });
            }
            return promise;
        }
    };
})

/* factory for getting data elements */
.factory('DataElementFactory', function($http, storage) {
    
    var dhis2Url = storage.get('CONFIG').activities.dhis.href;      
   
    var DataElemmentFactory = {};

    DataElemmentFactory.getDataElement = function(uid) {
        return $http.get(dhis2Url + '/api/dataElements/' + uid + '.json');
    };

    DataElemmentFactory.getAllDataElements = function() {
        return $http.get(dhis2Url + '/api/dataElements');
    };

    DataElemmentFactory.getDataElementGroup = function(uid) {
        return $http.get(dhis2Url + '/api/dataElementGroups/' + uid + '.json');
    };

    DataElemmentFactory.getAllDataElementGroups = function() {
        return $http.get(dhis2Url + '/api/dataElementGroups');
    };

    return DataElemmentFactory;
})

/* factory for handling events */
.factory('DHIS2EventFactory', function($http, storage, ProgramStageFactory) {   
    
    var dhis2Url = storage.get('CONFIG').activities.dhis.href;      
    
    return {
        
        getByPerson: function(person, orgUnit, program){   
            var promise = $http.get(dhis2Url + '/api/events.json?' + 'person=' + person + '&orgUnit=' + orgUnit + '&program=' + program + '&paging=false').then(function(response){
                return response.data.eventList;
            });            
            return promise;
        },
        
        getByStage: function(orgUnit, programStage){
            var promise = $http.get(dhis2Url + '/api/events.json?' + 'orgUnit=' + orgUnit + '&programStage=' + programStage + '&paging=false').then(function(response){
                //var dhis2Events = response.data.eventList;                
                
                return response.data.eventList;             
            });            
            return promise;
        },
        
        get: function(eventUID){
            
            var promise = $http.get(dhis2Url + '/api/events/' + eventUID + '.json').then(function(response){               
                return response.data;
            });            
            return promise;
        },
        
        create: function(dhis2Event){
            var promise = $http.post(dhis2Url + '/api/events.json?', dhis2Event).then(function(response){
                return response.data;
            });
            return promise;            
        },
    
        update: function(dhis2Event){            
            var promise = $http.put(dhis2Url + '/api/events/' + dhis2Event.event, dhis2Event).then(function(response){
                return response.data;
            });
            return promise;
        },
        
        updateSingleValue: function(dhis2Event){            
            var promise = $http.put(dhis2Url + '/api/events/' + dhis2Event.event + '/' + dhis2Event.dataValues[0].dataElement, dhis2Event ).then(function(response){
                return response.data;
            });
            return promise;
        }
    };    
})

/* service to communicate current event with controllers */
.service('DHIS2EventService', function() {
    var currentEventUid;
    return {
        setCurrentEventUid: function(uid) {
            currentEventUid = uid;
        },
        getCurrentEventUid: function() {    
            return currentEventUid;
        }
    };
})

/* Service for loading app configurations */
.factory('TrackerApp', function($http) {
    
    var configuration, configurationPromise;

    return {
        getConfiguration: function() {
            
            if(!configuration || !configurationPromise){
                configurationPromise = $http.get('manifest.webapp').then(function(response){
                   configuration = response.data;
                   //configuration.activities.dhis.href = 'http://localhost:8080';
                   return configuration;
                });
            }
            
            return configurationPromise; 
        }
    };
})

/* Service for evaluating intervention rules */
.service('ExpressionService', function(storage) {
    
    return {
        getDataElementExpression: function(val, dhis2Events) {
          
            var regex = /#[^#]*#/g,
                    match,
                    m,
                    mDe,
                    matches = [];
            
            //first collect all variables that need data value from the expression
            while (match = regex.exec(val)) {               
                m = match.toString();
                mDe = m.substring(1,m.length-1);
                matches.push(mDe);             
                
            }
            
            //replace variables with actuall data values - here I am trusing the order of entry
            //if the expression requires a value not yet recorded - this will fail! 
            for(var k=0; k<matches.length; k++){
                var loopThrough = true;
                var de = storage.get(matches[k]);
                for(var i=0; i<dhis2Events.length && loopThrough; i++){
                    for(var j=0; j<dhis2Events[i].dataValues.length && loopThrough; j++){
                        if( de.id == dhis2Events[i].dataValues[j].dataElement ){                            
                            var dv = dhis2Events[i].dataValues[j].value;
                            if( de.type == 'string'){                                
                                dv = '"' + dv + '"';                                
                            }
                            val = val.replace(new RegExp('#'+de.code+'#','g'), dv);
                            
                            loopThrough = false;
                        }                            
                    }
                }
            }
            
            val = val.replace(/#[^#]*#/g, null);
            return val;
        }
    };
})

/* Service for evaluating intervention rules */
.service('InterventionService', function(storage,
                                         $rootScope,
                                         ExpressionService, 
                                         TransferHandler,
                                         DialogService) {
    
    return {
        
        getResults: function(dhis2Events){      

            var checked = [];
            
            var dep = [], con = [], smr = [], rem = [], mes = [], sch = [];
            //Fetch available events for the selected person
            angular.forEach(dhis2Events, function(dhis2Event){         

                angular.forEach(dhis2Event.dataValues, function(dataValue){                    
                    
                    var de = storage.get(dataValue.dataElement);  
                    
                    if(angular.isObject(de)){ 
                        
                        var actualValue = dataValue.value;  
                        
                        if( de.type == 'string'){                            
                            actualValue = '"' + actualValue + '"';                            
                        }   
                        
                        //if(checked.indexOf(de.code) != -1 ){
                            
                            angular.forEach(de.actions, function(eiAction) {                           

                                var val = eiAction.value.replace(new RegExp('#' + de.code + '#', 'g'), actualValue);

                                //check if the expression contains some varibales
                                if (val.indexOf('#') != -1) {
                                    //format the expression, replace varibales with actual value
                                    //when replacing value, track back from the latest one.
                                    val = ExpressionService.getDataElementExpression(val, dhis2Events);
                                }              

                                //make sure the expression has no variables - but values
                                if (val.indexOf('#') != -1) {
                                    //if the expression still contains some varibales - this means 
                                    //the expression requires values which are not yet collected.
                                    var dialogOptions = {
                                        headerText: 'intervention_error',
                                        bodyText: 'intervention_error_text'
                                    };
                                    DialogService.showDialog({}, dialogOptions);
                                    return;                            
                                }
                                else{

                                    if ($rootScope.$eval(val)) {
                                        TransferHandler.store(eiAction.task.dependencies, de.code, dataValue.value, dep);
                                        TransferHandler.store(eiAction.task.conditionsComplications, de.code, dataValue.value, con);                                
                                        TransferHandler.store(eiAction.task.reminders, de.code, dataValue.value, rem);
                                        TransferHandler.store(eiAction.task.messaging, de.code, dataValue.value, mes);
                                        TransferHandler.store(eiAction.task.scheduling, de.code, dataValue.value, sch);
                                        TransferHandler.store(eiAction.task.summary, de.code, dataValue.value, smr);
                                    }                        
                                }
                            });                     
                            
                            //no need to check it again in case
                            //it is collected in subsequent visits
                            checked.push(de.code); 
                        //}                                            
                    }                                            
                });                              
            });    
            var results = { depResult: dep,
                            conResult: con,
                            remResult: rem,
                            mesResult: mes,
                            schResult: sch,
                            smrResult: smr
                          };
            return results;
        }        
    };    
})

/* Service for handling outcomes of interventions */
.factory('TransferHandler', function() {

    return {
        store: function(input, code, value, output) {            
            for (var i = 0; i < input.length; i++) {
                if (input[i]) {
                    input[i] = input[i].replace(new RegExp('#' + code + '#', 'g'), value);
                    if (output.indexOf(input[i]) == -1) {
                        output.push(input[i]);
                    }
                }
            }
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

/* Popup dialog for displaying notes */
.service('NotesDialogService', ['$modal', function($modal) {

    var dialogDefaults = {
        backdrop: true,
        keyboard: true,
        backdropClick: true,
        modalFade: true,
        templateUrl: 'views/anc/note.html'
    };

    var dialogOptions = {
        closeButtonText: 'close',
        actionButtonText: 'ok',
        headerText: 'dhis2_tracker',
        bodyText: 'Perform this action?',
        note: 'note',
        created_by: 'created_by',
        date: 'date'
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

.service('ContextMenuSelectedItem', function(){
    this.selectedItem = '';
    
    this.setSelectedItem = function(selectedItem){  
        this.selectedItem = selectedItem;        
    };
    
    this.getSelectedItem = function(){
        return this.selectedItem;
    };
})

/* Pagination service */
.service('Paginator', function () {
    this.page = 0;
    this.rowsPerPage = 50;
    this.itemCount = 0;
    this.limitPerPage = 5;

    this.setPage = function (page) {
        if (page > this.pageCount()) {
            return;
        }

        this.page = page;
    };
    
    this.getPage = function(){
        return this.page;
    };
    
    this.getRowsPerPage = function(){
        return this.rowsPerPage;
    };

    this.nextPage = function () {
        if (this.isLastPage()) {
            return;
        }

        this.page++;
    };

    this.perviousPage = function () {
        if (this.isFirstPage()) {
            return;
        }

        this.page--;
    };

    this.firstPage = function () {
        this.page = 0;
    };

    this.lastPage = function () {
        this.page = this.pageCount() - 1;
    };

    this.isFirstPage = function () {
        return this.page == 0;
    };

    this.isLastPage = function () {
        return this.page == this.pageCount() - 1;
    };

    this.pageCount = function () {
        var count = Math.ceil(parseInt(this.itemCount, 10) / parseInt(this.rowsPerPage, 10)); 
        if (count === 1) { this.page = 0; } return count;
    };

    this.lowerLimit = function() { 
        var pageCountLimitPerPageDiff = this.pageCount() - this.limitPerPage;

        if (pageCountLimitPerPageDiff < 0) { 
            return 0; 
        }

        if (this.page > pageCountLimitPerPageDiff + 1) { 
            return pageCountLimitPerPageDiff; 
        } 

        var low = this.page - (Math.ceil(this.limitPerPage/2) - 1); 

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
            
});