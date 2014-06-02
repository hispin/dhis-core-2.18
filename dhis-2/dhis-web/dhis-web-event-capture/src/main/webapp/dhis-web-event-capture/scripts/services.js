'use strict';

/* Services */

var eventCaptureServices = angular.module('eventCaptureServices', ['ngResource'])

/* factory for loading logged in user profiles from DHIS2 */
.factory('CurrentUserProfile', function($http) { 
           
    var profile, promise;
    return {
        get: function() {
            if( !promise ){
                promise = $http.get('../api/me/profile').then(function(response){
                   profile = response.data;
                   return profile;
                });
            }
            return promise;         
        }
    };  
})

/* Factory to fetch programs */
.factory('ProgramFactory', function($http) {
    
    var programUid, programPromise;
    var programs, programsPromise;
    var program;
    return {
        
        getAll: function(){
            if( !programsPromise ){
                programsPromise = $http.get('../api/programs.json?filter=type:eq:3&include=id,name,version,dateOfEnrollmentDescription,dateOfIncidentDescription,displayIncidentDate,ignoreOverdueEvents,organisationUnits[id,name],programStages[id,name]').then(function(response){
                //programsPromise = $http.get('../api/programs.json?filter=type:eq:3&include=id,name,version').then(function(response){
                   programs = response.data.programs;
                   
                   angular.forEach(programs, function ( program ) {
                       var ou = {};
                       angular.forEach(program.organisationUnits, function(o){
                           ou[o.id] = o.name;
                       });
                       
                       program.organisationUnits = ou;
                       var ur = {};
                       angular.forEach(program.userRoles, function(u){
                           ur[u.id] = u.name;
                       });                       
                       program.userRoles = ur;
                   });
                   return programs;
                });                        
            }
            return programsPromise;
        },
        
        get: function(uid){
            if( programUid !== uid ){
                programPromise = $http.get('../api/programs.json?filter=id:eq:' + uid +'&include=id,name,dateOfEnrollmentDescription,dateOfIncidentDescription,displayIncidentDate,ignoreOverdueEvents,organisationUnits[id,name],programStages[id,name]').then(function(response){
                    programUid = response.data.id; 
                    program = response.data;                     
                    return program;
                });
            }
            return programPromise;
        }
    };
})

/* Factory to fetch programStages */
.factory('ProgramStageFactory', function($http) {  
    
    var programStageUid, promise;   
    return {        
        get: function(uid){
            if( programStageUid !== uid ){
                promise = $http.get( '../api/programStages.json?filter=id:eq:' + uid +'&include=id,name,version,description,minDaysFromStart,repeatable,dataEntryForm,programStageDataElements[displayInReports,allowProvidedElsewhere,allowDateInFuture,compulsory,dataElement[id,name,type,optionSet[id,name,options]]]').then(function(response){
                   programStageUid = response.data.programStages[0].id;
                   return response.data.programStages[0];
                });
            }
            return promise;
        }
    };    
})

/* factory for handling events */
.factory('DHIS2EventFactory', function($http) {   
    
    return {
        getByStage: function(orgUnit, programStage, pager){
        	var pgSize = pager ? pager.pageSize : 50;
        	var pg = pager ? pager.page : 1;
                var url = '../api/events.json?' + 'orgUnit=' + orgUnit + '&programStage=' + programStage + '&pageSize=' + pgSize + '&page=' + pg;            

                var promise = $http.get( url ).then(function(response){                        
                return response.data;        
            }, function(){     
                return dhis2.ec.storageManager.getEvents(orgUnit, programStage);                
            });            
            
            return promise;
        },
        
        get: function(eventUid){            
            var promise = $http.get('../api/events/' + eventUid + '.json').then(function(response){               
                return response.data;                
            }, function(){
                return dhis2.ec.storageManager.getEvent(eventUid);
            });            
            return promise;
        },
        
        create: function(dhis2Event){            
            var e = angular.copy(dhis2Event);            
            dhis2.ec.storageManager.saveEvent(e);            
        
            var promise = $http.post('../api/events.json', dhis2Event).then(function(response){
                dhis2.ec.storageManager.clearEvent(e);
                return response.data;
            }, function(){
                return {importSummaries: [{status: 'SUCCESS', reference: e.event}]};
            });
            return promise;            
        },
        
        delete: function(dhis2Event){
            dhis2.ec.storageManager.clearEvent(dhis2Event);
            var promise = $http.delete('../api/events/' + dhis2Event.event).then(function(response){
                return response.data;
            }, function(){                
            });
            return promise;           
        },
    
        update: function(dhis2Event){   
            dhis2.ec.storageManager.saveEvent(dhis2Event);
            var promise = $http.put('../api/events/' + dhis2Event.event, dhis2Event).then(function(response){
                dhis2.ec.storageManager.clearEvent(dhis2Event);
                return response.data;
            }, function(){
            });
            return promise;
        },
        
        updateForSingleValue: function(singleValue, fullValue){            
            dhis2.ec.storageManager.saveEvent(fullValue);            
            var promise = $http.put('../api/events/' + singleValue.event + '/' + singleValue.dataValues[0].dataElement, singleValue ).then(function(response){
                dhis2.ec.storageManager.clearEvent(fullValue);
                return response.data;
            }, function(){                
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

            return e;  
        }        
    };
})

/* service for dealing with custom form */
.service('CustomFormService', function(){
    
    return {
        getForProgramStage: function(programStage){
            
            var htmlCode = programStage.dataEntryForm ? programStage.dataEntryForm.htmlCode : null;  
            
            if(htmlCode){                
            
                var programStageDataElements = [];

                angular.forEach(programStage.programStageDataElements, function(prStDe){
                    programStageDataElements[prStDe.dataElement.id] = prStDe;
                });

                var inputRegex = /<input(.*)\/>/g,
                    styleRegex = /style="[^"]*"/,
                    idRegex = /id="[^"]*"/,
                    match,
                    inputFields = [];

                while (match = inputRegex.exec(htmlCode)) {                
                    inputFields.push(match[0]);        
                }

                for(var i=0; i<inputFields.length; i++){
                    var inputField = inputFields[i];
                    var deId = '', style = '', newInputField;

                    if(match = idRegex.exec(inputFields[i])){                    
                        deId = match[0].substring(4, match[0].length-1).split("-")[1];

                        if(match = styleRegex.exec(inputFields[i]) ){
                            style = match[0];                          
                        }

                        if(programStageDataElements[deId].dataElement.type == "int"){
                            newInputField = '<input type="number" name="'+ deId +'"' + style + 'ng-model="currentEvent.' + deId + '"' +
                                            'ng-required="programStageDataElements.' + deId + '.compulsory">';
                        }
                        if(programStageDataElements[deId].dataElement.type == "string"){
                            newInputField = '<input type="text" name="'+ deId +'"' + style + 'ng-model="currentEvent.' + deId + '"' +
                                            'ng-required="programStageDataElements.' + deId + '.compulsory"' +
                                            'typeahead="option for option in programStageDataElements.'+deId+'.dataElement.optionSet.options | filter:$viewValue | limitTo:20"' +
                                            'typeahead-open-on-focus ng-required="programStageDataElements.'+deId+'.compulsory" name="foo" style="width:99%;">';
                        }
                        if(programStageDataElements[deId].dataElement.type == "bool"){
                            newInputField = '<select name="'+ deId +'"' + style + 'ng-model="currentEvent.' + deId + '"' +
                                            'ng-required="programStageDataElements.' + deId + '.compulsory">' + 
                                            'option value="">{{\'please_select\'| translate}}</option>' +
                                            '<option value="0">{{\'no\'| translate}}</option>' + 
                                            '<option value="1">{{\'yes\'| translate}}</option>';
                        }
                        if(programStageDataElements[deId].dataElement.type == "date"){
                            newInputField = '<input type="text" name="'+ deId +'"' + style + ' ng-model="currentEvent.' + deId + '"' +
                                            'ng-date' +
                                            'ng-required="programStageDataElements.' + deId + '.compulsory">';
                        }
                        if(programStageDataElements[deId].dataElement.type == "trueOnly"){
                            newInputField = '<input type="checkbox" name="'+ deId +'"' + style + 'ng-model="currentEvent.' + deId + '"' +
                                            'ng-required="programStageDataElements.' + deId + '.compulsory">';
                        }

                        newInputField = //'<ng-form name="innerForm">' + 
                                        newInputField + 
                                        '<span ng-show="outerForm.submitted && outerForm.'+ deId +'.$invalid" class="required">{{\'required\'| translate}}</span>';                                     
                                        //'</ng-form>';                                    

                        htmlCode = htmlCode.replace(inputField, newInputField);
                    }                
                }
                
                return htmlCode;
                
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
            
});
