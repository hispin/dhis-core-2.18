trackerCapture.controller('ReportController',
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
        
        if (angular.isObject($scope.selectedOrgUnit)) {   

            ProgramFactory.getAll().then(function(programs){
                $scope.programs = [];
                angular.forEach(programs, function(program){                            
                    if(program.organisationUnits.hasOwnProperty($scope.selectedOrgUnit.id)){                                
                        $scope.programs.push(program);
                    }
                });

                if($scope.programs.length === 0){
                    $scope.selectedProgram = null;
                }
                else{
                    if($scope.selectedProgram){
                        angular.forEach($scope.programs, function(program){                            
                            if(program.id === $scope.selectedProgram.id){                                
                                $scope.selectedProgram = program;
                            }
                        });
                    }
                    else{                        
                        if($scope.programs.length === 1){
                            $scope.selectedProgram = $scope.programs[0];
                        }                        
                    }
                } 
            });
        }        
    };
    
    //watch for selection of org unit from tree
    $scope.$watch('selectedProgram', function() {        
        if( angular.isObject($scope.selectedProgram)){            
            $scope.dataReady = false;
        }
    });
    
    $scope.generateReport = function(){
        
        $scope.dataReady = false;
        
        //check for form validity
        $scope.outerForm.submitted = true;        
        if( $scope.outerForm.$invalid || !$scope.selectedProgram){
            return false;
        }
        
        $scope.programStages = [];
        angular.forEach($scope.selectedProgram.programStages, function(stage){
            $scope.programStages[stage.id] = stage;
        });
            
        AttributesFactory.getByProgram($scope.selectedProgram).then(function(atts){            
            $scope.gridColumns = TEIGridService.generateGridColumns(atts, $scope.selectedOuMode.name);      
        });  
        
        //fetch TEIs for the selected program and orgunit/mode
        TEIService.search($scope.selectedOrgUnit.id, 
                            $scope.selectedOuMode.name,
                            null,
                            'program=' + $scope.selectedProgram.id,
                            null,
                            $scope.pager,
                            false).then(function(data){
            if(data.rows){
                $scope.teiCount = data.rows.length;
                $scope.dataReady = true;
            }
            
            
            //process tei grid
            $scope.teiList = TEIGridService.format(data);          
            
            DHIS2EventFactory.getByOrgUnitAndProgram($scope.selectedOrgUnit.id, $scope.selectedOuMode.name, $scope.selectedProgram.id).then(function(eventList){
                $scope.dhis2Events = [];
                angular.forEach(eventList, function(ev){
                    if(ev.trackedEntityInstance){
                        ev.name = $scope.programStages[ev.programStage].name;
                        ev.programName = $scope.selectedProgram.name;
                        ev.statusColor = EventUtils.getEventStatusColor(ev); 
                        ev.eventDate = DateUtils.format(ev.eventDate);
                        
                        if($scope.dhis2Events[ev.trackedEntityInstance]){
                            $scope.dhis2Events[ev.trackedEntityInstance].push(ev);
                        }
                        else{
                            $scope.dhis2Events[ev.trackedEntityInstance] = [ev];
                        }
                        ev = EventUtils.setEventOrgUnitName(ev);
                    }
                });
                
            });
        });
    };
    
    $scope.showEventDetails = function(dhis2Event, selectedTei){
        
        var modalInstance = $modal.open({
            templateUrl: 'components/report/event-details.html',
            controller: 'EventDetailsController',
            resolve: {
                dhis2Event: function () {
                    return dhis2Event;
                },
                gridColumns: function(){
                    return $scope.gridColumns;
                },
                selectedTei: function(){
                    return selectedTei;
                },
                entityName: function(){
                    return $scope.selectedProgram.trackedEntity.name;
                },
                reportMode: function(){
                    return 'PROGRAM';
                }
            }
        });

        modalInstance.result.then({
        });
    };   
    
})

//Controller for event details
.controller('EventDetailsController', 
    function($scope, 
            $modalInstance,
            orderByFilter,
            ProgramStageFactory,
            dhis2Event,
            selectedTei,
            gridColumns,
            entityName,
            reportMode){
    
    $scope.selectedTei = selectedTei;
    $scope.gridColumns = gridColumns;
    $scope.entityName = entityName;
    $scope.reportMode = reportMode;
    $scope.currentEvent = dhis2Event;
    $scope.currentEvent.providedElsewhere = [];
    
    if(!angular.isUndefined( $scope.currentEvent.notes)){
        $scope.currentEvent.notes = orderByFilter($scope.currentEvent.notes, '-storedDate');            
        angular.forEach($scope.currentEvent.notes, function(note){
            note.storedDate = moment(note.storedDate).format('YYYY-MM-DD @ hh:mm A');
        });
    }
    
    ProgramStageFactory.get($scope.currentEvent.programStage).then(function(stage){
        $scope.currentStage = stage;

        $scope.allowProvidedElsewhereExists = false;
        angular.forEach($scope.currentStage.programStageDataElements, function(prStDe){
            $scope.currentStage.programStageDataElements[prStDe.dataElement.id] = prStDe.dataElement;
            if(prStDe.allowProvidedElsewhere){
                $scope.allowProvidedElsewhereExists = true;
                $scope.currentEvent.providedElsewhere[prStDe.dataElement.id] = '';   
            }                
        });
        angular.forEach($scope.currentEvent.dataValues, function(dataValue){
            var val = dataValue.value;
            if(val){
                var de = $scope.currentStage.programStageDataElements[dataValue.dataElement];
                if( de && de.type === 'int' && val){
                    val = parseInt(val);
                    dataValue.value = val;
                }
                $scope.currentEvent[dataValue.dataElement] = val;
            }                    
        });
    });
    
    $scope.close = function () {
        $modalInstance.close();
    };
})


//conroller for tei report
.controller('TeiReportController',
        function($scope,
                $filter,
                CurrentSelection,
                storage,
                DateUtils,
                EventUtils,
                TEIService,
                TranslationService,
                ProgramFactory,
                ProgramStageFactory,
                EnrollmentService,
                DHIS2EventFactory) {

    TranslationService.translate();    
    $scope.showProgramReportDetailsDiv = false;
    $scope.programs = [];  
    $scope.programNames = [];  
    $scope.programStageNames = [];
    ProgramFactory.getAll().then(function(programs){     
        $scope.programs = programs;
        angular.forEach($scope.programs, function(pr){
            delete pr.organisationUnits;
            $scope.programNames[pr.id] = {id: pr.id, name: pr.name};
            angular.forEach(pr.programStages, function(stage){                
                $scope.programStageNames[stage.id] = {id: stage.id, name: stage.name};
            });
        });
    });
        
    $scope.$on('dashboardWidgets', function(event, args) {
        $scope.showProgramReportDetailsDiv = false;
        var selections = CurrentSelection.get();
        $scope.selectedOrgUnit = storage.get('SELECTED_OU');
        $scope.selectedTei = selections.tei;  
        $scope.selectedEntity = selections.te;
        $scope.selectedProgram = selections.pr;        
        $scope.selectedEnrollment = selections.enrollment; 
    
        if($scope.selectedTei){            
            $scope.getEvents();
        }       
    });
    
    $scope.getEvents = function(){
        
        $scope.dataFetched = false;
        $scope.dataExists = false;
        var programId = null, orgUnitId = null;
        
        if($scope.selectedProgram){
            programId = $scope.selectedProgram.id;
        }
        
        $scope.report = [];
        angular.forEach($scope.programs, function(pr){
            $scope.report[pr.id] = {};
        });
        
        DHIS2EventFactory.getEventsByProgram($scope.selectedTei.trackedEntityInstance, orgUnitId, programId).then(function(eventList){
            angular.forEach(eventList, function(ev){
                if(ev.program){       
                    ev.visited = true;
                    ev.dueDate = DateUtils.format(ev.dueDate);  
                    ev.sortingDate = ev.dueDate;
                    ev.name = $scope.programStageNames[ev.programStage].name;
                    ev.programName = $scope.programNames[ev.program].name;
                    if(angular.isUndefined($scope.report[ev.program].enrollments)){
                        $scope.report[ev.program] = {enrollments: {}};
                    }
                    ev.statusColor = EventUtils.getEventStatusColor(ev); 
                    
                    if(ev.eventDate){
                        ev.eventDate = DateUtils.format(ev.eventDate);
                        ev.sortingDate = ev.eventDate;
                    }
                    else{
                        ev.visited = false;
                    }                 

                    if(ev.enrollment){
                        if($scope.report[ev.program].enrollments[ev.enrollment]){
                            $scope.report[ev.program].enrollments[ev.enrollment].push(ev);
                        }
                        else{
                            $scope.report[ev.program].enrollments[ev.enrollment]= [ev];
                        }
                    }
                    ev = EventUtils.setEventOrgUnitName(ev);
                }                
            });

            if(eventList){
                $scope.dataExists = true;
            }
            $scope.dataFetched = true;
        });
    };
    
    $scope.showProgramReportDetails = function(pr){
        
        $scope.showProgramReportDetailsDiv = !$scope.showProgramReportDetailsDiv;
        $scope.selectedProgram = pr;
        $scope.selectedReport = $scope.report[pr.id];
        
        //today as report date
        $scope.today = moment();
        $scope.today = Date.parse($scope.today);
        $scope.today = $filter('date')($scope.today, 'yyyy-MM-dd');

        //process tei attributes, this is to have consistent display so that the tei 
        //contains program attributes whether it has value or not
        TEIService.processAttributes($scope.selectedTei, $scope.selectedProgram, null).then(function(tei){
            $scope.tei = tei;  
        });
        
        //get program stage for the selected program
        //they are needed assign data element names for event data values
        $scope.programStages = [];  
        $scope.allowProvidedElsewhereExists = [];
        angular.forEach($scope.selectedProgram.programStages, function(st){
            ProgramStageFactory.get(st.id).then(function(stage){
                $scope.programStages[stage.id] = stage;
                var providedElsewhereExists = false;
                for(var i=0; i<stage.programStageDataElements.length && !providedElsewhereExists; i++){                
                    if(stage.programStageDataElements[i].allowProvidedElsewhere){
                        providedElsewhereExists = true;
                        $scope.allowProvidedElsewhereExists[st.id] = true;
                    }                
                }            
            });
        });
        
        //program reports come grouped in enrollment, process for each enrollment
        $scope.enrollments = [];        
        angular.forEach(Object.keys($scope.selectedReport.enrollments), function(enr){        
            //format report data values
            angular.forEach($scope.selectedReport.enrollments[enr], function(ev){
                angular.forEach(ev.notes, function(note){
                    note.storedDate = moment(note.storedDate).format('DD.MM.YYYY @ hh:mm A');
                }); 

                if(ev.dataValues){
                    angular.forEach(ev.dataValues, function(dv){
                        if(dv.dataElement){
                            ev[dv.dataElement] = dv;
                        }                    
                    });
                }
            });

            //get enrollment details
            EnrollmentService.get(enr).then(function(enrollment){
                enrollment.dateOfEnrollment = DateUtils.format(enrollment.dateOfEnrollment);
                enrollment.dateOfIncident = DateUtils.format(enrollment.dateOfIncident);            
                angular.forEach(enrollment.notes, function(note){
                    note.storedDate = moment(note.storedDate).format('DD.MM.YYYY @ hh:mm A');
                });            
                $scope.enrollments.push(enrollment);               
            });
        });    
    };
    
    $scope.close = function(){
        $scope.showProgramReportDetailsDiv = false;
    };
    
    $scope.print = function(){
        $scope.showProgramReportDetailsDiv = false;
    };
});