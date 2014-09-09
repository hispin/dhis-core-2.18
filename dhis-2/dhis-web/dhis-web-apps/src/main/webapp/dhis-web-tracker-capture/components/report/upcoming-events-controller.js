trackerCapture.controller('UpcomingEventsController',
         function($scope,
                $modal,
                $location,
                orderByFilter,
                DateUtils,
                EventUtils,
                TEIService,
                TEIGridService,
                TranslationService,
                AttributesFactory,
                ProgramFactory,
                DHIS2EventFactory,
                ContextMenuSelectedItem,
                storage) {

    TranslationService.translate();
    
    $scope.today = DateUtils.format(moment());
    
    $scope.ouModes = [{name: 'SELECTED'}, {name: 'CHILDREN'}, {name: 'DESCENDANTS'}, {name: 'ACCESSIBLE'}];         
    $scope.selectedOuMode = $scope.ouModes[0];
    $scope.report = {};
    $scope.displayMode = {};
    
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
            $scope.reportStarted = false;
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
        $scope.reportStarted = true;        
        $scope.programStages = [];
        $scope.filterTypes = {};
        $scope.filterText = {};
        
        angular.forEach($scope.selectedProgram.programStages, function(stage){
            $scope.programStages[stage.id] = stage;
        });
            
        AttributesFactory.getByProgram($scope.selectedProgram).then(function(atts){            
            $scope.gridColumns = TEIGridService.generateGridColumns(atts, $scope.selectedOuMode.name);

            $scope.gridColumns.push({name: 'event_name', id: 'event_name', type: 'string', displayInListNoProgram: false, showFilter: false, show: true});
            $scope.filterTypes['event_name'] = 'string';                

            $scope.gridColumns.push({name: 'due_date', id: 'due_date', type: 'date', displayInListNoProgram: false, showFilter: false, show: true});
            $scope.filterTypes['due_date'] = 'date';
            $scope.filterText['due_date']= {};                
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
                
                //incase a TEI happens to have more than one overdue, sort using duedate
                for(var tei in $scope.dhis2Events){                    
                    $scope.dhis2Events[tei] = orderByFilter($scope.dhis2Events[tei], '-dueDate');
                    $scope.dhis2Events[tei].reverse();
                }
                
                //make upcoming event name and its due date part of the grid column
                for(var i=0; i<$scope.teiList.length; i++){
                    $scope.teiList[i].event_name = $scope.dhis2Events[$scope.teiList[i].id][0].name;
                    $scope.teiList[i].due_date = $scope.dhis2Events[$scope.teiList[i].id][0].dueDate;
                    $scope.teiList[i].followup = $scope.dhis2Events[$scope.teiList[i].id][0].followup;
                }
               
                $scope.reportFinished = true;
                $scope.reportStarted = false;                
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
    
    $scope.searchInGrid = function(gridColumn){
        
        $scope.currentFilter = gridColumn;
       
        for(var i=0; i<$scope.gridColumns.length; i++){
            
            //toggle the selected grid column's filter
            if($scope.gridColumns[i].id === gridColumn.id){
                $scope.gridColumns[i].showFilter = !$scope.gridColumns[i].showFilter;
            }            
            else{
                $scope.gridColumns[i].showFilter = false;
            }
        }
    };    
    
    $scope.removeStartFilterText = function(gridColumnId){
        $scope.filterText[gridColumnId].start = undefined;
    };
    
    $scope.removeEndFilterText = function(gridColumnId){
        $scope.filterText[gridColumnId].end = undefined;
    };
    
    $scope.showDashboard = function(tei){
        $location.path('/dashboard').search({tei: tei.id,                                            
                                            program: $scope.selectedProgram ? $scope.selectedProgram.id: null});
    };
});