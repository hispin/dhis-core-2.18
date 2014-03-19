'use strict';

/* Services */

var eventCaptureServices = angular.module('eventCaptureServices', ['ngResource'])

/* Factory to fetch programs */
.factory('ProgramFactory', function($http) {
    
    var programUid, programPromise;
    var programs, programsPromise;
    var program;
    return {
        
        get: function(uid){
            if( programUid !== uid ){
                programPromise = $http.get('../api/programs/' + uid + '.json?viewClass=detailed&paging=false').then(function(response){
                    programUid = response.data.id; 
                    program = response.data;                     
                    return program;
                });
            }
            return programPromise;
        },       
        
        getMine: function(type){ 
            if( !programsPromise ){
                programsPromise = $http.get('../api/me/programs?includeDescendants=true&type='+type).then(function(response){
                   programs = response.data;
                   return programs;
                });
            }
            return programsPromise;    
        },
        
        getEventProgramsByOrgUnit: function(orgUnit, type){
                       
            var promise = $http.get( '../api/programs.json?orgUnit=' + orgUnit + '&type=' + type ).then(function(response){
                programs = response.data;
                return programs;
            });            
            return promise;
        }
    };
})

/* Factory to fetch programStages */
.factory('ProgramStageFactory', function($http, storage) {  
    
    var programStage, promise;   
    return {        
        get: function(uid){
            if( programStage !== uid ){
                promise = $http.get( '../api/programStages/' + uid + '.json?viewClass=detailed&paging=false').then(function(response){
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

/* factory for handling events */
.factory('DHIS2EventFactory', function($http) {   
    
    return {
        getByStage: function(orgUnit, programStage, pager){
            var url = '../api/events.json?' + 'orgUnit=' + orgUnit + '&programStage=' + programStage + '&pageSize=' + pager.pageSize + '&page=' + pager.page;            
            
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