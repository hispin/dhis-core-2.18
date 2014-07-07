trackerCapture.controller('ReportController',
        function($scope,
                CurrentSelection,
                storage,
                DateUtils,
                EventUtils,
                TranslationService,
                DHIS2EventFactory) {

    TranslationService.translate();
    
    $scope.ouModes = [{name: 'SELECTED'}, 
                    {name: 'CHILDREN'}, 
                    {name: 'DESCENDANTS'},
                    {name: 'ACCESSIBLE'}
                  ];         
    $scope.selectedOuMode = $scope.ouModes[0];
    
    $scope.$on('dashboard', function(event, args) {
        var selections = CurrentSelection.get();
        $scope.selectedOrgUnit = storage.get('SELECTED_OU');
        $scope.selectedEntity = selections.tei;      
        $scope.selectedProgram = selections.pr;        
        $scope.selectedEnrollment = selections.enrollment; 
        
        if($scope.selectedOrgUnit && 
                $scope.selectedProgram && 
                $scope.selectedEntity && 
                $scope.selectedEnrollment){
            
            $scope.getEvents();
        }       
    });
    
    $scope.getEvents = function(){
        
        $scope.dhis2Events = '';
        $scope.dataExists = false;
        DHIS2EventFactory.getEventsByProgram($scope.selectedEntity.trackedEntityInstance, $scope.selectedOrgUnit.id, $scope.selectedProgram.id).then(function(data){
            $scope.dhis2Events = data;     
            if(!angular.isUndefined($scope.dhis2Events) ){                
                for(var i=0; i<$scope.dhis2Events.length && !$scope.dataExists; i++){
                    if(!angular.isUndefined($scope.dhis2Events[i].dataValues)){
                        $scope.dataExists = true;
                    }                    
                }                    
            }
        });          
    };
})

//conroller for tei report
.controller('TeiReportController',
        function($scope,
                CurrentSelection,
                storage,
                DateUtils,
                EventUtils,
                TranslationService,
                DHIS2EventFactory) {

    TranslationService.translate();
    
    $scope.$on('dashboard', function(event, args) {
        var selections = CurrentSelection.get();
        $scope.selectedOrgUnit = storage.get('SELECTED_OU');
        $scope.selectedEntity = selections.tei;      
        $scope.selectedProgram = selections.pr;        
        $scope.selectedEnrollment = selections.enrollment; 
        
        if($scope.selectedOrgUnit && 
                $scope.selectedProgram && 
                $scope.selectedEntity && 
                $scope.selectedEnrollment){
            
            $scope.getEvents();
        }       
    });
    
    $scope.getEvents = function(){
        
        $scope.dhis2Events = '';
        $scope.dataExists = false;
        DHIS2EventFactory.getEventsByProgram($scope.selectedEntity.trackedEntityInstance, $scope.selectedOrgUnit.id, $scope.selectedProgram.id).then(function(data){
            $scope.dhis2Events = data;     
            if(!angular.isUndefined($scope.dhis2Events) ){                
                for(var i=0; i<$scope.dhis2Events.length && !$scope.dataExists; i++){
                    if(!angular.isUndefined($scope.dhis2Events[i].dataValues)){
                        $scope.dataExists = true;
                    }                    
                }                    
            }
        });          
    };
});