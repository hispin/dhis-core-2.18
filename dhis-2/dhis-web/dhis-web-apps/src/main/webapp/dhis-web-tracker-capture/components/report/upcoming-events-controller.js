trackerCapture.controller('UpcomingEventsController',
         function($scope,
                $modal,
                DateUtils,
                EventUtils,
                TEIService,
                TEIGridService,
                TranslationService,
                AttributesFactory,
                ProgramFactory,
                DHIS2EventFactory,
                storage) {

    TranslationService.translate();
    
    $scope.today = DateUtils.format(moment());
    
    $scope.ouModes = [{name: 'SELECTED'}, {name: 'CHILDREN'}, {name: 'DESCENDANTS'}, {name: 'ACCESSIBLE'}];         
    $scope.selectedOuMode = $scope.ouModes[0];
    $scope.report = {};
    
    //watch for selection of org unit from tree
    $scope.$watch('selectedOrgUnit', function() {        
        if( angular.isObject($scope.selectedOrgUnit)){            
            storage.set('SELECTED_OU', $scope.selectedOrgUnit);            
            $scope.loadPrograms($scope.selectedOrgUnit);
        }
    });
    
    //load programs associated with the selected org unit.
    $scope.loadPrograms = function(orgUnit) {
        $scope.selectedOrgUnit = orgUnit;        
        if (angular.isObject($scope.selectedOrgUnit)){
            ProgramFactory.getAll().then(function(programs){
                $scope.programs = programs;                
                if($scope.programs.length === 1){
                    $scope.selectedProgram = $scope.programs[0];
                } 
            });
        }        
    };
    
    //watch for selection of program
    $scope.$watch('selectedProgram', function() {   
        if( angular.isObject($scope.selectedProgram)){            
            $scope.reportStarted = false;
            $scope.dataReady = false;
        }
    });
    
    $scope.generateReport = function(program, report, ouMode){

        $scope.selectedProgram = program;
        $scope.report = report;
        $scope.selectedOuMode = ouMode;
        
        //check for form validity
        $scope.outerForm.submitted = true;        
        if( $scope.outerForm.$invalid || !$scope.selectedProgram){
            return false;
        }
        
        $scope.reportStarted = true;
        $scope.dataReady = false;
        
        $scope.programStages = [];
        angular.forEach($scope.selectedProgram.programStages, function(stage){
            $scope.programStages[stage.id] = stage;
        });
            
        AttributesFactory.getByProgram($scope.selectedProgram).then(function(atts){            
            $scope.attributes = TEIGridService.generateGridColumns(atts, $scope.selectedOuMode.name);   
        });  
        
        //fetch TEIs for the selected program and orgunit/mode
        TEIService.search($scope.selectedOrgUnit.id, 
                            $scope.selectedOuMode.name,
                            null,
                            'program=' + $scope.selectedProgram.id,
                            null,
                            $scope.pager,
                            false).then(function(data){                     
            
            //process tei grid
            var teis = TEIGridService.format(data,true);     
            $scope.teiList = [];            
            DHIS2EventFactory.getByOrgUnitAndProgram($scope.selectedOrgUnit.id, $scope.selectedOuMode.name, $scope.selectedProgram.id, null, null).then(function(eventList){
                $scope.dhis2Events = [];   
                angular.forEach(eventList, function(ev){
                    if(ev.dueDate){
                        ev.dueDate = DateUtils.format(ev.dueDate);
                        
                        if( ev.trackedEntityInstance && 
                            !ev.eventDate && 
                            ev.dueDate >= report.startDate && 
                            ev.dueDate <= report.endDate){
                        
                            ev.name = $scope.programStages[ev.programStage].name;
                            ev.programName = $scope.selectedProgram.name;
                            ev.statusColor = EventUtils.getEventStatusColor(ev); 
                            ev.dueDate = DateUtils.format(ev.dueDate);

                            if($scope.dhis2Events[ev.trackedEntityInstance]){
                                if(teis.rows[ev.trackedEntityInstance]){
                                    $scope.teiList.push(teis.rows[ev.trackedEntityInstance]);
                                    delete teis.rows[ev.trackedEntityInstance];
                                }                     
                                $scope.dhis2Events[ev.trackedEntityInstance].push(ev);
                            }
                            else{
                                if(teis.rows[ev.trackedEntityInstance]){
                                    $scope.teiList.push(teis.rows[ev.trackedEntityInstance]);
                                    delete teis.rows[ev.trackedEntityInstance];
                                }  
                                $scope.dhis2Events[ev.trackedEntityInstance] = [ev];
                            }
                            ev = EventUtils.setEventOrgUnitName(ev);
                        }                        
                    }
                });
                $scope.reportStarted = false;
                $scope.dataReady = true;                
            });
        });
    };
    
    $scope.showReschedule = function(dhis2Event, selectedTei){
        
        var modalInstance = $modal.open({
            templateUrl: 'components/report/rescheduling.html',
            controller: 'ReschedulingController',
            resolve: {
                dhis2Event: function () {
                    return dhis2Event;
                },
                attributes: function(){
                    return $scope.attributes;
                },
                selectedTei: function(){
                    return selectedTei;
                },
                entityName: function(){
                    return $scope.selectedProgram.trackedEntity.name;
                }
            }
        });

        modalInstance.result.then({
        });
    };    
})

//Controller for event details
.controller('ReschedulingController', 
    function($scope, 
            $modalInstance,            
            DHIS2EventFactory,
            dhis2Event,
            selectedTei,
            attributes,
            entityName){
    
    $scope.selectedTei = selectedTei;
    $scope.attributes = attributes;
    $scope.entityName = entityName;    
    $scope.currentEvent = dhis2Event;
    
    
    $scope.save = function(){
        
        
        if($scope.currentEvent.dueDate == ''){
            $scope.invalidDate = true;
            return false;
        }
        else{
            var rawDate = $filter('date')($scope.currentEvent.dueDate, 'yyyy-MM-dd'); 
            var convertedDate = moment($scope.currentEvent.dueDate, 'YYYY-MM-DD')._d;
            convertedDate = $filter('date')(convertedDate, 'yyyy-MM-dd'); 

            if(rawDate !== convertedDate){
                $scope.invalidDate = true;
                return false;
            } 

            var e = {event: $scope.currentEvent.event,
                 enrollment: $scope.currentEvent.enrollment,
                 dueDate: $scope.currentEvent.dueDate,
                 status: $scope.currentEvent.status,
                 program: $scope.currentEvent.program,
                 programStage: $scope.currentEvent.programStage,
                 orgUnit: $scope.currentEvent.orgUnit,
                 trackedEntityInstance: $scope.currentEvent.trackedEntityInstance
                };

            DHIS2EventFactory.update(e).then(function(data){            
                $scope.invalidDate = false;
                $scope.dueDateSaved = true;
                $scope.currentEvent.sortingDate = $scope.currentEvent.dueDate;                
                var statusColor = EventUtils.getEventStatusColor($scope.currentEvent);  
                var continueLoop = true;
                for(var i=0; i< $scope.dhis2Events.length && continueLoop; i++){
                    if($scope.dhis2Events[i].event === $scope.currentEvent.event ){
                        $scope.dhis2Events[i].statusColor = statusColor;
                        continueLoop = false;
                    }
                } 
            });
        }
        
    };
    
    $scope.close = function () {
        $modalInstance.close();
    };
});