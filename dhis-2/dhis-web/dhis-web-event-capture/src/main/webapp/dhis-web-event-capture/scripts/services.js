'use strict';

/* Services */

var eventCaptureServices = angular.module('eventCaptureServices', ['ngResource'])

/* Factory to fetch programs */
.factory('ProgramFactory', function($http, DHIS2URL) {
    
    var programUid, programPromise;
    var programs, programsPromise;
    var program;
    return {
        
        get: function(uid){
            if( programUid !== uid ){
                programPromise = $http.get(DHIS2URL + '/api/programs/' + uid + '.json?viewClass=detailed&paging=false').then(function(response){
                    programUid = response.data.id; 
                    program = response.data;                     
                    return program;
                });
            }
            return programPromise;
        },       
        
        getMine: function(type){ 
            if( !programsPromise ){
                programsPromise = $http.get(DHIS2URL + '/api/me/programs?includeDescendants=true&type='+type).then(function(response){
                   programs = response.data;
                   return programs;
                });
            }
            return programsPromise;    
        },
        
        getEventProgramsByOrgUnit: function(orgUnit, type){
                       
            var promise = $http.get( DHIS2URL + '/api/programs.json?orgUnit=' + orgUnit + '&type=' + type ).then(function(response){
                programs = response.data;
                return programs;
            });            
            return promise;
        }
    };
})

/* Factory to fetch programStages */
.factory('ProgramStageFactory', function($http, DHIS2URL, storage) {  
    
    var programStage, promise;   
    return {        
        get: function(uid){
            if( programStage !== uid ){
                promise = $http.get( DHIS2URL + '/api/programStages/' + uid + '.json?viewClass=detailed&paging=false').then(function(response){
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
.factory('CurrentUserProfile', function($http, DHIS2URL) { 
           
    var profile, promise;
    return {
        get: function() {
            if( !promise ){
                promise = $http.get(DHIS2URL + '/api/me/profile').then(function(response){
                   profile = response.data;
                   return profile;
                });
            }
            return promise;         
        }
    };  
})

/* factory for handling events */
.factory('DHIS2EventFactory', function($http, DHIS2URL) {   
    
    return {
        
        getByPerson: function(person, orgUnit, program){   
            var promise = $http.get(DHIS2URL + '/api/events.json?' + 'person=' + person + '&orgUnit=' + orgUnit + '&program=' + program + '&paging=false').then(function(response){
                return response.data.eventList;
            });            
            return promise;
        },
        
        getByStage: function(orgUnit, programStage){
            var promise = $http.get(DHIS2URL + '/api/events.json?' + 'orgUnit=' + orgUnit + '&programStage=' + programStage + '&paging=false').then(function(response){
                //var dhis2Events = response.data.eventList;                
                
                return response.data.eventList;             
            });            
            return promise;
        },
        
        get: function(eventUID){
            
            var promise = $http.get(DHIS2URL + '/api/events/' + eventUID + '.json').then(function(response){               
                return response.data;
            });            
            return promise;
        },
        
        create: function(dhis2Event){
            var promise = $http.post(DHIS2URL + '/api/events.json?', dhis2Event).then(function(response){
                return response.data;
            });
            return promise;            
        },
        
        delete: function(dhis2Event){
           var promise = $http.delete(DHIS2URL + '/api/events/' + dhis2Event.event).then(function(response){
                return response.data;
            });
            return promise;           
        },
    
        update: function(dhis2Event){            
            var promise = $http.put(DHIS2URL + '/api/events/' + dhis2Event.event, dhis2Event).then(function(response){
                return response.data;
            });
            return promise;
        },
        
        updateSingleValue: function(dhis2Event){            
            var promise = $http.put(DHIS2URL + '/api/events/' + dhis2Event.event + '/' + dhis2Event.dataValues[0].dataElement, dhis2Event ).then(function(response){
                return response.data;
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

.service('ContextMenuSelectedItem', function(){
    this.selectedItem = '';
    
    this.setSelectedItem = function(selectedItem){  
        this.selectedItem = selectedItem;        
    };
    
    this.getSelectedItem = function(){
        return this.selectedItem;
    };
})

.service('TranslationService', function($translate, storage){
    
    this.translate = function(){
        var profile = storage.get('USER_PROFILE');
        profile = JSON.parse( profile );
        if( profile ){        
            $translate.uses(profile.settings.keyUiLocale);
        }
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