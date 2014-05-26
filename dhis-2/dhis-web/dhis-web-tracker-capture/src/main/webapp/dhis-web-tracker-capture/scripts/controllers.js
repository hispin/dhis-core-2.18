'use strict';

/* Controllers */
var trackerCaptureControllers = angular.module('trackerCaptureControllers', [])

//Controller for settings page
.controller('SelectionController',
        function($rootScope,
                $scope,
                $location,
                $modal,
                Paginator,
                TranslationService, 
                storage,
                AttributesFactory,
                EntityQueryFactory,
                TEIService) {   
   
    //Selection
    $scope.selectedOrgUnit = '';
    $scope.selectedProgram = '';
    $scope.ouModes = [
                    {name: 'SELECTED', id: 1}, 
                    {name: 'CHILDREN', id: 2}, 
                    {name: 'DESCENDANTS', id: 3}
                  ];                  
    $scope.ouMode = $scope.ouModes[0];
    
    //Paging
    $scope.rowsPerPage = 50;
    $scope.currentPage = Paginator.getPage() + 1;   
    
    //EntityList
    $scope.showTrackedEntityDiv = false;
    
    //Searching
    $scope.showSearchDiv = false;
    $scope.searchText = null;
    $scope.emptySearchText = false;
    $scope.searchFilterExists = false;    
    
    $scope.searchMode = { 
                            listAll: 'LIST_ALL', 
                            freeText: 'FREE_TEXT', 
                            attributeBased: 'ATTRIBUTE_BASED'
                        };
    
    //Registration
    $scope.showRegistrationDiv = false;
    
    //watch for selection of org unit from tree
    $scope.$watch('selectedOrgUnit', function() {
        
        $scope.attributes = AttributesFactory.getWithoutProgram();    
        
        if( angular.isObject($scope.selectedOrgUnit)){   
            
            storage.set('SELECTED_OU', $scope.selectedOrgUnit);
            
            $scope.trackedEntityList = [];
            $scope.selectedProgram = '';
            
            //apply translation - by now user's profile is fetched from server.
            TranslationService.translate();
            $scope.loadPrograms($scope.selectedOrgUnit);            
            $scope.search($scope.searchMode.listAll);
            
        }
    });
    
    //load programs associated with the selected org unit.
    $scope.loadPrograms = function(orgUnit) {        
                
        $scope.selectedOrgUnit = orgUnit;
        $scope.selectedProgram = null;
        $scope.selectedProgramStage = null;
        
        if (angular.isObject($scope.selectedOrgUnit)) {   

            $scope.programs = [];
            
            var programs = storage.get('TRACKER_PROGRAMS');
            
            if( programs && programs != 'undefined' ){
                for(var i=0; i<programs.length; i++){
                    var program = storage.get(programs[i].id);   
                    if(angular.isObject(program)){
                        if(program.organisationUnits.hasOwnProperty(orgUnit.id)){
                            $scope.programs.push(program);
                        }
                    }                    
                }
                
                if( !angular.isUndefined($scope.programs)){                    
                    if($scope.programs.length === 1){
                        $scope.selectedProgram = $scope.programs[0];
                        $scope.pr = $scope.selectedProgram;    
                        $scope.attributes = AttributesFactory.getByProgram($scope.selectedProgram);
                    }                    
                }
            }
        }        
    };
    
    $scope.getProgramAttributes = function(program){ 
        $scope.trackedEntityList = null; 
        $scope.selectedProgram = program;
        
        if($scope.selectedProgram){
            $scope.attributes = AttributesFactory.getByProgram($scope.selectedProgram);
        }
        else{
            $scope.attributes = AttributesFactory.getWithoutProgram();
        }

        $scope.search($scope.searchMode);
    };
    
    $scope.search = function(mode){ 
        
        $scope.emptySearchText = false;
        $scope.emptySearchAttribute = false;
        $scope.showSearchDiv = false;
        $scope.showRegistrationDiv = false;                          
        $scope.trackedEntityList = null; 
        
        var queryUrl = null, 
            programUrl = null, 
            attributeUrl = {url: null, hasValue: false};
    
        if($scope.selectedProgram){
            programUrl = 'program=' + $scope.selectedProgram.id;
        }        
        
        //check search mode
        if( mode === $scope.searchMode.freeText ){     
            if(!$scope.searchText){                
                $scope.emptySearchText = true;
                return;
            }       
            
            $scope.showTrackedEntityDiv = true;      
            queryUrl = 'query=' + $scope.searchText;                     
        }
        else if( mode === $scope.searchMode.attributeBased ){
            $scope.showTrackedEntityDiv = true;                  
            attributeUrl = EntityQueryFactory.getQueryForAttributes($scope.attributes);
            
            if(!attributeUrl.hasValue){
                $scope.emptySearchAttribute = true;
                $scope.showSearchDiv = true;
                return;
            }
        }
        else if( mode === $scope.searchMode.listAll ){   
            $scope.showTrackedEntityDiv = true;    
        }      
        
        $scope.gridColumns = $scope.generateGridColumns($scope.attributes);

        //get events for the specified parameters
        TEIService.search($scope.selectedOrgUnit.id, 
                                            $scope.ouMode.name,
                                            queryUrl,
                                            programUrl,
                                            attributeUrl.url).then(function(data){
            $scope.trackedEntityList = data;            
        });
    };
    
    //generate grid columns from teilist attributes
    $scope.generateGridColumns = function(attributes){
        var columns = angular.copy(attributes);  
       
        //also add extra columns which are not part of attributes (orgunit for example)
        columns.push({id: 'orgUnitName', name: 'Organisation unit', type: 'string', displayInListNoProgram: false});
        
        //generate grid column for the selected program/attributes
        angular.forEach(columns, function(column){
            if(column.id === 'orgUnitName' && $scope.ouMode.name === 'SELECTED'){
                column.show = false;    
            }
            if(column.displayInListNoProgram){
                column.show = true;
            }           
           
            if(column.type === 'date'){
                 $scope.filterText[column.id]= {start: '', end: ''};
            }
        });        
        return columns;        
    };
    
    $scope.clearEntities = function(){
        $scope.trackedEntityList = null;
    };
    
    $scope.showRegistration = function(){
        $scope.showRegistrationDiv = !$scope.showRegistrationDiv;
        $scope.showTrackedEntityDiv = false;
        $scope.showSearchDiv = false;
    };  
    
    $scope.showSearch = function(){        
        $scope.showSearchDiv = !$scope.showSearchDiv;
        $scope.showRegistrationDiv = false;
        //$scope.showTrackedEntityDiv = false;   
        $scope.selectedProgram = '';
        $scope.emptySearchAttribute = false;
    };
    
    $scope.hideSearch = function(){        
        $scope.showSearchDiv = false;
        $rootScope.showAdvancedSearchDiv = false;
    };
    
    $scope.closeSearch = function(){
        $scope.showSearchDiv = !$scope.showSearchDiv;
    };
    
    $scope.showHideColumns = function(){
        
        $scope.hiddenGridColumns = 0;
        
        angular.forEach($scope.gridColumns, function(gridColumn){
            if(!gridColumn.show){
                $scope.hiddenGridColumns++;
            }
        });
        
        var modalInstance = $modal.open({
            templateUrl: 'views/column-modal.html',
            controller: 'ColumnDisplayController',
            resolve: {
                gridColumns: function () {
                    return $scope.gridColumns;
                },
                hiddenGridColumns: function(){
                    return $scope.hiddenGridColumns;
                }
            }
        });

        modalInstance.result.then(function (gridColumns) {
            $scope.gridColumns = gridColumns;
        }, function () {
        });
    };
    
    $scope.showDashboard = function(currentEntity){
        
        $location.path('/dashboard').search({selectedEntityId: currentEntity.id,                                            
                                            selectedProgramId: $scope.selectedProgram ? $scope.selectedProgram.id: null});                                    
    };  
       
    $scope.getHelpContent = function(){
        console.log('I will get help content');
    };    
})

//Controller for column show/hide
.controller('ColumnDisplayController', 
    function($scope, 
            $modalInstance, 
            hiddenGridColumns,
            gridColumns){
    
    $scope.gridColumns = gridColumns;
    $scope.hiddenGridColumns = hiddenGridColumns;
    
    $scope.close = function () {
      $modalInstance.close($scope.gridColumns);
    };
    
    $scope.showHideColumns = function(gridColumn){
       
        if(gridColumn.show){                
            $scope.hiddenGridColumns--;            
        }
        else{
            $scope.hiddenGridColumns++;            
        }      
    };    
})

.controller('RegistrationController', 
        function($scope,
                $location,
                AttributesFactory,
                TEIService,
                EnrollmentService,
                DialogService,
                storage,
                TranslationService) {
    
    //do translation of the registration page
    TranslationService.translate();   
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
    $scope.enrollment = {enrollmentDate: '', incidentDate: ''};    
    
    $scope.attributes = AttributesFactory.getWithoutProgram();
    $scope.trackedEntities = {available: storage.get('TRACKED_ENTITIES')};
    
    $scope.trackedEntities.selected = $scope.trackedEntities.available[0];
      
    //watch for selection of org unit from tree
    $scope.$watch('selectedProgram', function() {        
        if( angular.isObject($scope.selectedProgram)){
            $scope.trackedEntityList = [];
            $scope.attributes = AttributesFactory.getByProgram($scope.selectedProgram);
        }
    });    
    
    $scope.registerEntity = function(showDashboard){
        
        //get selected entity
        var selectedTrackedEntity = '';        
        if($scope.selectedProgram){
            selectedTrackedEntity = $scope.selectedProgram.trackedEntity.id;
        }
        else{
            selectedTrackedEntity = $scope.trackedEntities.selected.id;
        }
        
        //get tei attributes and their values
        var registrationAttributes = [];    
        angular.forEach($scope.attributes, function(attribute){
            if(!angular.isUndefined(attribute.value)){
                var att = {attribute: attribute.id, value: attribute.value};
                registrationAttributes.push(att);
            } 
        });       
        
        //prepare tei model and do registration
        $scope.tei = {trackedEntity: selectedTrackedEntity, orgUnit: $scope.selectedOrgUnit.id, attributes: registrationAttributes };   
        var teiId = '';
    
        TEIService.register($scope.tei).then(function(tei){
            
            if(tei.status === 'SUCCESS'){
                
                teiId = tei.reference;
                
                //registration is successful and check for enrollment
                if($scope.selectedProgram){    
                    //enroll TEI
                    var enrollment = {trackedEntityInstance: teiId,
                                program: $scope.selectedProgram.id,
                                status: 'ACTIVE',
                                dateOfEnrollment: $scope.enrollment.enrollmentDate,
                                dateOfIncident: $scope.enrollment.incidentDate
                            };
                    EnrollmentService.enroll(enrollment).then(function(data){
                        if(data.status != 'SUCCESS'){
                            //enrollment has failed
                            var dialogOptions = {
                                    headerText: 'enrollment_error',
                                    bodyText: data.description
                                };
                            DialogService.showDialog({}, dialogOptions);
                            return;
                        }
                    });
                }
            }
            else{
                //registration has failed
                var dialogOptions = {
                        headerText: 'registration_error',
                        bodyText: tei.description
                    };
                DialogService.showDialog({}, dialogOptions);
                return;
            }
            
            if(showDashboard){
                $location.path('/dashboard').search({selectedEntityId: teiId,
                                                selectedProgramId: $scope.selectedProgram ? $scope.selectedProgram.id : null});
            }
            else{            
                
                angular.forEach($scope.attributes, function(attribute){
                    attribute.value = ''; 
                });
                $scope.enrollment.enrollmentDate = '';
                $scope.enrollment.incidentDate =  '';
            }
        });
    };
})

//Controller for dashboard
.controller('DashboardController',
        function($rootScope,
                $scope,
                $location,
                $modal,
                $timeout,
                storage,
                TEIService,      
                CurrentSelection,
                TranslationService) {

    //do translation of the dashboard page
    TranslationService.translate();    
    
    //dashboard items   
    $rootScope.dashboardWidgets = {bigger: [], smaller: []};       
    $rootScope.enrollmentWidget = {title: 'enrollment', view: "views/enrollment.html", show: true};
    $rootScope.dataentryWidget = {title: 'dataentry', view: "views/dataentry.html", show: true};
    $rootScope.selectedWidget = {title: 'current_selections', view: "views/selected.html", show: false};
    $rootScope.profileWidget = {title: 'profile', view: "views/profile.html", show: true};
    $rootScope.relationshipWidget = {title: 'relationship', view: "views/relationship.html", show: true};
    $rootScope.notesWidget = {title: 'notes', view: "views/notes.html", show: true};    
   
    $rootScope.dashboardWidgets.bigger.push($rootScope.enrollmentWidget);
    $rootScope.dashboardWidgets.bigger.push($rootScope.dataentryWidget);
    $rootScope.dashboardWidgets.smaller.push($rootScope.selectedWidget);
    $rootScope.dashboardWidgets.smaller.push($rootScope.profileWidget);
    $rootScope.dashboardWidgets.smaller.push($rootScope.relationshipWidget);
    $rootScope.dashboardWidgets.smaller.push($rootScope.notesWidget);
    
    //selections
    $scope.selectedEntityId = null;
    $scope.selectedProgramId = null;
    
    $scope.selectedEntityId = ($location.search()).selectedEntityId; 
    $scope.selectedProgramId = ($location.search()).selectedProgramId; 
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
    
    if($scope.selectedProgramId && storage.get($scope.selectedProgramId)){
        $scope.selectedProgram = storage.get($scope.selectedProgramId);
    }
    else{
        $scope.selectedProgram = null;
    }
        
    if( $scope.selectedEntityId ){
        
        //Fetch the selected entity
        TEIService.get($scope.selectedEntityId).then(function(data){              
            CurrentSelection.set({tei: data, pr: $scope.selectedProgram});
         
            //broadcast selected entity for dashboard controllers
            $timeout(function() { 
                $rootScope.$broadcast('selectedEntity', {});
            }, 100);
        });       
    }   
    
    $scope.back = function(){
        $location.path('/');
    };
    
    $scope.displayEnrollment = false;
    $scope.showEnrollment = function(){
        $scope.displayEnrollment = true;
    };
    
    $scope.removeWidget = function(widget){        
        widget.show = false;
    };
    
    $scope.showHideWidgets = function(){
        var modalInstance = $modal.open({
            templateUrl: "views/widgets.html",
            controller: "DashboardWidgetsController"
        });

        modalInstance.result.then(function () {
        });
    };
})

//Controller for the profile section
.controller('ProfileController',
        function($scope,                
                storage,
                CurrentSelection,
                TranslationService) {

    TranslationService.translate();
    
    //attributes for profile    
    $scope.attributes = {};    
    $scope.editProfile = false;    
    
    angular.forEach(storage.get('ATTRIBUTES'), function(attribute){
        $scope.attributes[attribute.id] = attribute;
    }); 
    
    //listen for the selected entity
    $scope.$on('selectedEntity', function(event, args) { 
        var selections = CurrentSelection.get();
        $scope.selectedEntity = selections.tei;        
      
        angular.forEach(storage.get('TRACKED_ENTITIES'), function(te){
            if($scope.selectedEntity.trackedEntity === te.id){
                $scope.trackedEntity = te;
            }
        }); 
        
        $scope.entityAttributes = angular.copy($scope.selectedEntity.attributes);
    });
    
    $scope.showEdit = function(){
      $scope.editProfile = !$scope.editProfile; 
    };
    
    $scope.save = function(){
        
        $scope.editProfile = !$scope.editProfile;
    };
    
    $scope.cancel = function(){
        $scope.selectedEntity.attributes = $scope.entityAttributes;  
        $scope.editProfile = !$scope.editProfile;
    };
})

//Controller for the enrollment section
.controller('EnrollmentController',
        function($rootScope,
                $scope,  
                $filter,
                storage,
                CurrentSelection,
                EnrollmentService,
                TranslationService) {

    TranslationService.translate();
    
    //programs for enrollment
    $scope.enrollments = [];
    $scope.programs = []; 
    $scope.showEnrollmentDiv = false;
    
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
    
    //listen for the selected items
    $scope.$on('selectedEntity', function(event, args) {
        
        var selections = CurrentSelection.get();
        $scope.selectedEntity = selections.tei;    
        
        $scope.selectedOrgUnit = storage.get('SELECTED_OU');
        
        angular.forEach(storage.get('TRACKER_PROGRAMS'), function(program){
            program = storage.get(program.id);
            if(program.organisationUnits.hasOwnProperty($scope.selectedOrgUnit.id) &&
               program.trackedEntity.id === $scope.selectedEntity.trackedEntity){
                $scope.programs.push(program);
            }
        });
        
        if(selections.pr){       
            angular.forEach($scope.programs, function(program){
                if(selections.pr.id === program.id){
                    $scope.selectedProgram = program;
                    $scope.loadEvents();
                }
            });
        }
    }); 
    
    $scope.loadEvents = function() {
        
        if($scope.selectedProgram){
            
            $scope.selectedEnrollment = '';           
            
            EnrollmentService.get($scope.selectedEntity.trackedEntityInstance).then(function(data){
                $scope.enrollments = data.enrollmentList;                
                
                angular.forEach($scope.enrollments, function(enrollment){
                    if(enrollment.program === $scope.selectedProgram.id ){
                        $scope.selectedEnrollment = enrollment;
                    }
                }); 
            
                $scope.programStages = [];        
                angular.forEach($scope.selectedProgram.programStages, function(stage){
                   $scope.programStages.push(storage.get(stage.id));               
                });

                if($scope.selectedEnrollment){
                    $scope.selectedEnrollment.dateOfIncident = $filter('date')($scope.selectedEnrollment.dateOfIncident, 'yyyy-MM-dd');
                }
                
                $rootScope.$broadcast('dataentry', {selectedEntity: $scope.selectedEntity,
                                                    selectedOrgUnit: $scope.selectedOrgUnit,
                                                    selectedProgramId: $scope.selectedProgram.id,
                                                    selectedEnrollment: $scope.selectedEnrollment});
            });            
        }
        
        $rootScope.$broadcast('dataentry', {selectedEntity: $scope.selectedEntity,
                                                    selectedOrgUnit: $scope.selectedOrgUnit,
                                                    selectedProgramId: $scope.selectedProgram ? $scope.selectedProgram.id : null,
                                                    selectedEnrollment: $scope.selectedEnrollment ? $scope.selectedEnrollment : null});
        
    };
    
    $scope.showEnrollment = function(){        
        console.log('Enrollment', $scope.selectedEntity, ' ', $scope.selectedProgram);
    };
    
    $scope.enroll = function(){        
        console.log('Enrollment', $scope.selectedEntity, ' ', $scope.selectedProgram);
    };
})

//Controller for the data entry section
.controller('DataEntryController',
        function($scope, 
                $filter,
                orderByFilter,
                storage,
                DHIS2EventFactory,
                OrgUnitService,
                TranslationService) {

    TranslationService.translate();
     
    //listen for the selected items
    $scope.$on('dataentry', function(event, args) {  
        
        var today = moment();
        today = Date.parse(today);
        today = $filter('date')(today, 'yyyy-MM-dd');

        $scope.currentEvent = null;
        $scope.allowEventCreation = false;
        $scope.repeatableStages = [];        
        $scope.dhis2Events = [];       
    
        $scope.selectedEntity = args.selectedEntity;
        $scope.selectedOrgUnit = args.selectedOrgUnit;
        $scope.selectedProgramId = args.selectedProgramId;        
        $scope.selectedEnrollment = args.selectedEnrollment;        
        
        if($scope.selectedOrgUnit && 
                $scope.selectedProgramId && 
                $scope.selectedEntity && 
                $scope.selectedEnrollment){
            
            DHIS2EventFactory.getByEntity($scope.selectedEntity.trackedEntityInstance, $scope.selectedOrgUnit.id, $scope.selectedProgramId).then(function(data){
                $scope.dhis2Events = data;
                
                if(angular.isUndefined($scope.dhis2Events)){
                    
                    $scope.dhis2Events = [];
                    
                    console.log('need to create new ones:  ', $scope.selectedEnrollment);
                    
                    if($scope.selectedEnrollment.status === 'ACTIVE'){
                        //create events for the selected enrollment
                        var program = storage.get($scope.selectedProgramId);
                        var programStages = [];
                        
                        angular.forEach(program.programStages, function(ps){  
                            ps = storage.get(ps.id);
                            programStages.push(ps);
                            
                            var eventDate = moment(moment().add('d', ps.minDaysFromStart), 'YYYY-MM-DD')._d;
                            eventDate = Date.parse(eventDate);
                            eventDate = $filter('date')(eventDate, 'yyyy-MM-dd');
                            var dhis2Event = {programStage: ps.id, 
                                              orgUnit: $scope.selectedOrgUnitId, 
                                              eventDate: eventDate,
                                              name: ps.name,
                                              status: 'ACTIVE'};
                            
                            $scope.dhis2Events.push(dhis2Event);    
                            
                        });
                    }
                }
                
                angular.forEach($scope.dhis2Events, function(dhis2Event){
                    
                    var ps = storage.get(dhis2Event.programStage);
                    //check if a stage is repeatable
                    if(ps.repeatable){
                        $scope.allowEventCreation = true;
                        if($scope.repeatableStages.indexOf(ps) === -1){
                            $scope.repeatableStages.push(ps);
                        }
                    }
                            
                    dhis2Event.name = ps.name;
                    dhis2Event.eventDate = moment(dhis2Event.eventDate, 'YYYY-MM-DD')._d;
                    dhis2Event.eventDate = Date.parse(dhis2Event.eventDate);
                    dhis2Event.eventDate = $filter('date')(dhis2Event.eventDate, 'yyyy-MM-dd');
                    
                    if(dhis2Event.status === 'COMPLETED'){
                        dhis2Event.statusColor = 'stage-completed';
                    }
                    else{
                        dhis2Event.statusColor = 'stage-on-time';

                        if(moment(today).isAfter(dhis2Event.eventDate)){
                            dhis2Event.statusColor = 'stage-overdue';
                        }                        
                    } 
                    
                    if(dhis2Event.orgUnit){
                        OrgUnitService.open().then(function(){
                            OrgUnitService.get(dhis2Event.orgUnit).then(function(ou){
                                if(ou){
                                    dhis2Event.orgUnitName = ou.n;
                                }                                                       
                            });                            
                        }); 
                    }                                                             
                });

                $scope.dhis2Events = orderByFilter($scope.dhis2Events, '-eventDate');
                $scope.dhis2Events.reverse();              
            });          
        }
    });
    
    $scope.createNewEvent = function(){
        console.log('need to create new event:  ', $scope.repeatableStages);        
    };
    
    $scope.showDataEntry = function(event){
        
        if(event){
            
            $scope.currentEvent = event;
            $scope.currentEvent.providedElsewhere = [];
            $scope.currentEvent.dataValues = [];
            $scope.currentStage = storage.get($scope.currentEvent.programStage); 
            
            angular.forEach($scope.currentStage.programStageDataElements, function(prStDe){                
                $scope.currentStage.programStageDataElements[prStDe.dataElement.id] = prStDe.dataElement;
                if(prStDe.allowProvidedElsewhere){
                    $scope.currentEvent.providedElsewhere[prStDe.dataElement.id] = '';   
                }                
            });
            
            angular.forEach($scope.currentEvent.dataValues, function(dataValue){
                var val = dataValue.value;
                var de = $scope.currentStage.programStageDataElements[dataValue.dataElement];
                if( de && de.type === 'int' && val){
                    val = parseInt(val);
                    dataValue.value = val;
                }                   
            });     
        }     
    };    
})

//Controller for the dashboard widgets
.controller('DashboardWidgetsController', 
    function($scope, 
            $modalInstance,
            TranslationService){
    
    TranslationService.translate();
    
    $scope.close = function () {
        $modalInstance.close($scope.eventGridColumns);
    };       
})

//Controller for the relationship section
.controller('RelationshipController',
        function($scope,                
                storage,
                TranslationService) {

    TranslationService.translate();    
    $scope.attributes = storage.get('ATTRIBUTES');    
})

//Controller for the notes section
.controller('NotesController',
        function($scope,                
                storage,
                TranslationService) {

    TranslationService.translate();
    
    $scope.attributes = storage.get('ATTRIBUTES');
    
})

//Controller for the selected section
.controller('SelectedInfoController',
        function($scope,                
                storage,
                TranslationService) {

    TranslationService.translate();
    
    //listen for the selected items
    $scope.$on('selectedEntity', function(event, args) {
        
        $scope.selectedEntity = args.selectedEntity;
        $scope.selectedProgramId = args.selectedProgramId;        
        $scope.selectedOrgUnitId = args.selectedOrgUnitId;        
        
        if($scope.selectedProgramId){
            $scope.selectedProgram = storage.get($scope.selectedProgramId);        
        }
        
        $scope.selectedOrgUnit = storage.get('SELECTED_OU');
        $scope.selections = [];
        
        $scope.selections.push({title: 'registering_unit', value: $scope.selectedOrgUnit ? $scope.selectedOrgUnit.name : 'not_selected'});
        $scope.selections.push({title: 'program', value: $scope.selectedProgram ? $scope.selectedProgram.name : 'not_selected'});               
        
    });     
})

//Controller for the header section
.controller('HeaderController',
        function($scope,                
                DHIS2URL,
                TranslationService) {

    TranslationService.translate();
    
    $scope.home = function(){        
        window.location = DHIS2URL;
    };
    
});