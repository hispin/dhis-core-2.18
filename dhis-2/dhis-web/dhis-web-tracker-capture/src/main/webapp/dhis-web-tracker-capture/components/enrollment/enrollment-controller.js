trackerCapture.controller('EnrollmentController',
        function($rootScope,
                $scope,  
                $filter,
                storage,
                ProgramFactory,
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
        
        angular.forEach(ProgramFactory.getAll(), function(program){
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
                
                $rootScope.$broadcast('dashboard', {selectedEntity: $scope.selectedEntity,
                                                    selectedOrgUnit: $scope.selectedOrgUnit,
                                                    selectedProgramId: $scope.selectedProgram.id,
                                                    selectedEnrollment: $scope.selectedEnrollment});
            });            
        }
        
        /*$rootScope.$broadcast('dashboard', {selectedEntity: $scope.selectedEntity,
                                                    selectedOrgUnit: $scope.selectedOrgUnit,
                                                    selectedProgramId: $scope.selectedProgram ? $scope.selectedProgram.id : null,
                                                    selectedEnrollment: $scope.selectedEnrollment ? $scope.selectedEnrollment : null});*/
        
    };
    
    
    
    $scope.showEnrollment = function(){        
        $scope.showEnrollmentDiv = !$scope.showEnrollmentDiv;
        
        console.log('Enrollment', $scope.selectedEntity, ' ', $scope.selectedProgram);
    };
    
    $scope.enroll = function(){        
        console.log('Enrollment', $scope.selectedEntity, ' ', $scope.selectedProgram);
    };
});