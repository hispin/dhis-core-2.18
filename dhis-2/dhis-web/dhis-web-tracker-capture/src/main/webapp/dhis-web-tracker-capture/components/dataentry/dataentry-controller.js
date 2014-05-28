trackerCapture.controller('DataEntryController',
        function($scope, 
                $filter,
                orderByFilter,
                storage,
                DHIS2EventFactory,
                OrgUnitService,
                TranslationService) {

    TranslationService.translate();
     
    //listen for the selected items
    $scope.$on('dashboard', function(event, args) {  
        
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
                    
                    if($scope.selectedEnrollment.status === 'ACTIVE'){
                        //create events for the selected enrollment
                        var program = storage.get($scope.selectedProgramId);
                        //var programStages = [];
                        
                        angular.forEach(program.programStages, function(ps){  
                            ps = storage.get(ps.id);
                            //programStages.push(ps);
                            
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
});