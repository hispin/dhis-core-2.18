/* Pagination service */
var d2Services = angular.module('d2Services', ['ngResource'])

/* Factory to fetch geojsons */
.factory('GeoJsonFactory', function($q, $rootScope, StorageService) { 
    return {
        getAll: function(){

            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.getAll('geoJsons').done(function(geoJsons){
                    $rootScope.$apply(function(){
                        def.resolve(geoJsons);
                    });                    
                });
            });
            
            return def.promise;            
        },
        get: function(level){
            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.get('geoJsons', level).done(function(geoJson){                    
                    $rootScope.$apply(function(){
                        def.resolve(geoJson);
                    });
                });
            });                        
            return def.promise;            
        }
    };
})

/* Factory to fetch optioSets */
.factory('OptionSetService', function($q, $rootScope, StorageService) { 
    return {
        getAll: function(){
            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.getAll('optionSets').done(function(optionSets){
                    $rootScope.$apply(function(){
                        def.resolve(optionSets);
                    });                    
                });
            });            
            
            return def.promise;            
        },
        get: function(uid){
            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.get('optionSets', uid).done(function(optionSet){                    
                    $rootScope.$apply(function(){
                        def.resolve(optionSet);
                    });
                });
            });                        
            return def.promise;            
        },
        getNameOrCode: function(options, key){
            var val = key;            

            if(options){
                for(var i=0; i<options.length; i++){
                    if( key === options[i].name){
                        val = options[i].code;
                        break;
                    }
                    if( key === options[i].code){
                        val = options[i].name;
                        break;
                    }
                }
            }            
            return val;
        }
    };
})

/* Factory for loading external data */
.factory('ExternalDataFactory', function($http) {

    return {        
        get: function(fileName) {
            var promise = $http.get( fileName ).then(function(response){
                return response.data;
            });            
            return promise;
        }
    };
})


/* service for getting calendar setting */
.service('CalendarService', function(storage, $rootScope){    

    return {
        getSetting: function() {
            
            var dhis2CalendarFormat = {keyDateFormat: 'yyyy-MM-dd', keyCalendar: 'gregorian', momentFormat: 'YYYY-MM-DD'};                
            var storedFormat = storage.get('CALENDAR_SETTING');
            if(angular.isObject(storedFormat) && storedFormat.keyDateFormat && storedFormat.keyCalendar){
                if(storedFormat.keyCalendar === 'iso8601'){
                    storedFormat.keyCalendar = 'gregorian';
                }

                if(storedFormat.keyDateFormat === 'dd-MM-yyyy'){
                    dhis2CalendarFormat.momentFormat = 'DD-MM-YYYY';
                }
                
                dhis2CalendarFormat.keyCalendar = storedFormat.keyCalendar;
                dhis2CalendarFormat.keyDateFormat = storedFormat.keyDateFormat;
            }
            $rootScope.dhis2CalendarFormat = dhis2CalendarFormat;
            return dhis2CalendarFormat;
        }
    };            
})

/* service for dealing with dates */
.service('DateUtils', function($filter, CalendarService){
    
    return {        
        getDate: function(dateValue){
            if(!dateValue){
                return;
            }            
            var calendarSetting = CalendarService.getSetting();
            dateValue = moment(dateValue, calendarSetting.momentFormat)._d;
            return Date.parse(dateValue);
        },
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
                        attributes[this.nodeName] = this.value;                       
                    });
                    
                    var deId = '', newInputField;     
                    if(attributes.hasOwnProperty('id')){
                        deId = attributes['id'].substring(4, attributes['id'].length-1).split("-")[1]; 
                        
                        //name needs to be unique so that it can be used for validation in angularjs
                        if(attributes.hasOwnProperty('name')){
                            attributes['name'] = deId;
                        }
                        
                        var maxDate = programStageDataElements[deId].allowFutureDate ? '' : 0;
                        
                        //check data element type and generate corresponding angular input field
                        if(programStageDataElements[deId].dataElement.type == "int"){
                            newInputField = '<input type="number" ' +
                                            this.getAttributesAsString(attributes) +
                                            ' ng-model="currentEvent.' + deId + '"' +
                                            ' ng-required="prStDes.' + deId + '.compulsory"> ' + 
                                            '<span ng-show="outerForm.submitted && outerForm.'+ deId +'.$invalid" class="required">{{\'int_required\'| translate}}</span>';                                     
                        }
                        if(programStageDataElements[deId].dataElement.type == "string"){
                            if(programStageDataElements[deId].dataElement.optionSet){
                                var optionSetId = programStageDataElements[deId].dataElement.optionSet.id;
                        		newInputField = '<input type="text" ' +
                                            this.getAttributesAsString(attributes) +
                                            ' ng-model="currentEvent.' + deId + '" ' +
                                            ' ng-disabled="currentEvent[uid] == \'uid\'" ' +
                                            ' ng-required="prStDes.' + deId + '.compulsory"' +
                                            ' typeahead="option.name as option.name for option in optionSets.'+optionSetId+'.options | filter:$viewValue | limitTo:20"' +
                                            ' typeahead-editable="false" ' +
                                            ' d2-typeahead-validation ' +
                                            ' class="typeahead" ' +
                                            ' placeholder="&#xf0d7;&nbsp;&nbsp;" ' +
                                            ' typeahead-open-on-focus ng-required="prStDes.'+deId+'.compulsory"> ' +
                                            '<span ng-show="outerForm.submitted && outerForm.'+ deId +'.$invalid" class="required">{{\'option_required\'| translate}}</span>';
                        	}
                        	else{
                        		newInputField = '<input type="text" ' +
                                            this.getAttributesAsString(attributes) +
                                            ' ng-model="currentEvent.' + deId + '" ' +
                                            ' ng-disabled="currentEvent[uid] == \'uid\'" ' +
                                            ' ng-required="prStDes.' + deId + '.compulsory"> ' +
                                            '<span ng-show="outerForm.submitted && outerForm.'+ deId +'.$invalid" class="required">{{\'string_required\'| translate}}</span>';                                     
                        	}
                        }
                        if(programStageDataElements[deId].dataElement.type == "bool"){
                            newInputField = '<select ' +
                                            this.getAttributesAsString(attributes) +
                                            ' ng-model="currentEvent.' + deId + '" ' +
                                            ' ng-required="prStDes.' + deId + '.compulsory">' + 
                                            '<option value="">{{\'please_select\'| translate}}</option>' +
                                            '<option value="false">{{\'no\'| translate}}</option>' + 
                                            '<option value="true">{{\'yes\'| translate}}</option>' +
                                            '</select> ' +
                                            '<span ng-show="outerForm.submitted && outerForm.'+ deId +'.$invalid" class="required">{{\'bool_required\'| translate}}</span>';                                     
                        }
                        if(programStageDataElements[deId].dataElement.type == "date"){
                            newInputField = '<input type="text" ' +
                                            this.getAttributesAsString(attributes) +
                                            ' ng-model="currentEvent.' + deId + '"' +
                                            ' d2-date ' +
                                            ' max-date="' + maxDate + '"' + '\'' +
                                            ' ng-required="prStDes.' + deId + '.compulsory"> ' +
                                            '<span ng-show="outerForm.submitted && outerForm.'+ deId +'.$invalid" class="required">{{\'date_required\'| translate}}</span>'; 
                        }
                        if(programStageDataElements[deId].dataElement.type == "trueOnly"){
                            newInputField = '<input type="checkbox" ' +
                                            this.getAttributesAsString(attributes) +
                                            ' ng-model="currentEvent.' + deId + '"' +
                                            ' ng-required="prStDes.' + deId + '.compulsory"> ' +
                                            '<span ng-show="outerForm.submitted && outerForm.'+ deId +'.$invalid" class="required">{{\'required\'| translate}}</span>';
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
});
