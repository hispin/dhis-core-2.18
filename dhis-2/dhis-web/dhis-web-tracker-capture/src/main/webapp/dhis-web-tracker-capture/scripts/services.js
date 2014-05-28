'use strict';

/* Services */

var trackerCaptureServices = angular.module('trackerCaptureServices', ['ngResource'])

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

/* Factory to fetch programs */
.factory('ProgramFactory', function($http, storage) {
    
    var programUid, programPromise;
    var programs, programsPromise;
    var program;
    return {
        
        get: function(uid){
            if( programUid !== uid ){
                programPromise = $http.get( '../api/programs/' + uid + '.json?viewClass=detailed&paging=false').then(function(response){
                    programUid = response.data.id; 
                    program = response.data;                     
                    return program;
                });
            }
            return programPromise;
        },       
        
        getMine: function(type){ 
            if( !programsPromise ){
                programsPromise = $http.get( '../api/me/programs?includeDescendants=true&type='+type).then(function(response){
                   programs = response.data;
                   return programs;
                });
            }
            return programsPromise;    
        },
        
        getEventProgramsByOrgUnit: function(orgUnit, type){
                       
            var promise = $http.get(  '../api/programs.json?orgUnit=' + orgUnit + '&type=' + type ).then(function(response){
                programs = response.data;
                return programs;
            });            
            return promise;
        },
        getAll: function(){
            var programs = [];
            angular.forEach(storage.get('TRACKER_PROGRAMS'), function(p){
                programs.push(storage.get(p.id));
            });
            return programs;
        }
    };
})

/* Factory to fetch programStages */
.factory('ProgramStageFactory', function($http, storage) {  
    
    var programStage, promise;   
    return {        
        get: function(uid){
            if( programStage !== uid ){
                promise = $http.get(  '../api/programStages/' + uid + '.json?viewClass=detailed&paging=false').then(function(response){
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
        get: function( entity ){
            var promise = $http.get(  '../api/enrollments?trackedEntityInstance=' + entity ).then(function(response){
                return response.data;
            });
            return promise;
        },
        enroll: function( enrollment ){
            var promise = $http.post(  '../api/enrollments', enrollment ).then(function(response){
                return response.data;
            });
            return promise;
        }        
    };   
})

/* Service for getting tracked entity instances */
.factory('TEIService', function($http, $filter, EntityService) {
    
    var promise;
    return {
        
        get: function(entityUid) {
            promise = $http.get(  '../api/trackedEntityInstances/' +  entityUid ).then(function(response){     
                var tei = response.data;
                
                angular.forEach(tei.attributes, function(attribute){                   
                   if(attribute.type && attribute.value){                       
                       if(attribute.type === 'date'){                           
                           attribute.value = moment(attribute.value, 'YYYY-MM-DD')._d;
                           attribute.value = Date.parse(attribute.value);
                           attribute.value = $filter('date')(attribute.value, 'yyyy-MM-dd');                           
                       }
                   } 
                });
                return tei;
            });            
            return promise;
        },
        
        getByOrgUnitAndProgram: function(orgUnitUid, programUid) {

            var url = '../api/trackedEntityInstances.json?ou=' + orgUnitUid + '&program=' + programUid;
            
            promise = $http.get( url ).then(function(response){               
                return EntityService.formatter(response.data);
            });            
            return promise;
        },
        getByOrgUnit: function(orgUnitUid) {           
            
            var url =  '../api/trackedEntityInstances.json?ou=' + orgUnitUid;
            
            promise = $http.get( url ).then(function(response){                                
                return EntityService.formatter(response.data);
            });            
            return promise;
        },        
        search: function(ouId, ouMode, queryUrl, programUrl, attributeUrl) {           
            
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
            
            promise = $http.get( url ).then(function(response){                                
                return EntityService.formatter(response.data);
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
        }
    };
})

/* Factory for getting tracked entity attributes */
.factory('AttributesFactory', function(storage) { 
    
    return {
        getAll: function(){  
            return storage.get('ATTRIBUTES');
        }, 
        getByProgram: function(program){
            
            if(program){
                var attributes = [];
                var programAttributes = [];

                angular.forEach(this.getAll(), function(attribute){
                    attributes[attribute.id] = attribute;
                });

                angular.forEach(program.programTrackedEntityAttributes, function(pAttribute){
                   programAttributes.push(attributes[pAttribute.attribute.id]);                
                }); 
                
                return programAttributes;            
            }
            return this.getWithoutProgram();           
        },
        getWithoutProgram: function(){            
            var attributes = [];
            
            angular.forEach(this.getAll(), function(attribute) {
                if (attribute.displayInListNoProgram) {
                    attributes.push(attribute);
                }
            });           

            return attributes;
        },
        convertListingForToQuery: function(){
            var param = '';
            angular.forEach(this.getForListing(), function(attribute) {
                param +=  '&' + 'attribute=' + attribute.id;
            });
            
            return param;
        },
        getMissingAttributesForEnrollment: function(tei, program){
            var programAttributes = this.getByProgram(program);
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
            return missingAttributes;
        },
        hideAttributesNotInProgram: function(tei, program){
            var programAttributes = this.getByProgram(program);
            var teiAttributes = tei.attributes;
            
            for(var i=0; i<teiAttributes.length; i++){
                teiAttributes[i].show = true;
                var inProgram = false;
                for(var j=0; j<programAttributes.length && !inProgram; j++){
                    if(teiAttributes[i].attribute === programAttributes[j].id){
                        inProgram = true;
                    }
                }
                if(!inProgram){
                    teiAttributes[i].show = false;
                }                
            }            
            return tei.attributes;
        }
    };
})

/* factory for handling events */
.factory('DHIS2EventFactory', function($http) {   
    
    return {
        
        getByEntity: function(entity, orgUnit, program){   
            var promise = $http.get( '../api/events.json?' + 'trackedEntityInstance=' + entity + '&orgUnit=' + orgUnit + '&program=' + program + '&paging=false').then(function(response){
                return response.data.events;
            });            
            return promise;
        }
    };    
})

.service('EntityQueryFactory', function(){  
    
    this.getQueryForAttributes = function(attributes){
        
        var query = {url: null, hasValue: false};
        
        angular.forEach(attributes, function(attribute){           

            if(attribute.value && attribute.value !== ''){                    
                query.hasValue = true;                
                if(angular.isArray(attribute.value)){
                    var index = 0, q = '';
                    
                    angular.forEach(attribute.value, function(val){
                        
                        if(index < attribute.value.length-1){
                            q = q + val + ';';
                        }
                        else{
                            q = q + val;
                        }                        
                        index++;
                    });
                    
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
        });
        return query;
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
    
    this.set = function(currentSelection){  
        this.currentSelection = currentSelection;        
    };
    
    this.get = function(){
        return this.currentSelection;
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
            
})

.service('EntityService', function(OrgUnitService, $filter){
    
    return {
        formatter: function(grid){
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
                    rDate = moment(rDate, 'YYYY-MM-DD')._d;
                    rDate = Date.parse(rDate);
                    rDate = $filter('date')(rDate, 'yyyy-MM-dd');                           
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
                        entityList.push(entity);
                    }        
                });                
            });
            return {headers: attributes, rows: entityList};                                    
        }        
    };
});