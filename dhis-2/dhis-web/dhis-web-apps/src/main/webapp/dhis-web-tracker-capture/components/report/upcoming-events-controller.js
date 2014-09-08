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
            $scope.reportFinished = false;
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
        
        $scope.reportFinished = false;
        $scope.dataReady = false;
        
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
                $scope.reportFinished = true;
                $scope.dataReady = true;                
            });
        });
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
    
    $scope.sortTEIGrid = function(gridHeader){
        if ($scope.sortHeader === gridHeader.id){
            $scope.reverse = !$scope.reverse;
            return;
        }        
        $scope.sortHeader = gridHeader.id;
        $scope.reverse = false;    
    };
});