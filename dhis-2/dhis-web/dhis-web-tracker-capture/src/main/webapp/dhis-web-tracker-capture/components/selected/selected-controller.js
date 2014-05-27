trackerCapture.controller('SelectedInfoController',
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
});