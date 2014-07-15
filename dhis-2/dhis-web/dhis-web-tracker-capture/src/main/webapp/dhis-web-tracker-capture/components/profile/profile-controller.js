trackerCapture.controller('ProfileController',
        function($rootScope,
                $scope,     
                CurrentSelection,
                TEIService,
                DialogService,
                AttributesFactory,
                TranslationService) {

    TranslationService.translate();
    
    //attributes for profile    
    $scope.attributes = {};    
    $scope.editProfile = false;    
    
    AttributesFactory.getAll().then(function(atts){
        angular.forEach(atts, function(att){
            $scope.attributes[att.id] = att;
        }); 
    }); 
    
    //listen for the selected entity       
    $scope.$on('dashboardWidgets', function(event, args) { 
        var selections = CurrentSelection.get();
        $scope.selectedTei = angular.copy(selections.tei);
        $scope.trackedEntity = selections.te;
        $scope.selectedProgram = selections.pr;   
        $scope.selectedEnrollment = selections.enrollment;  
        
        //display only those attributes that belong the selected program
        //if no program, display attributesInNoProgram
        TEIService.processAttributes($scope.selectedTei, $scope.selectedProgram, $scope.selectedEnrollment).then(function(tei){
            $scope.selectedTei = tei;            
        });
    });
    
    $scope.enableEdit = function(){
        $scope.entityAttributes = angular.copy($scope.selectedTei.attributes);
        $scope.editProfile = !$scope.editProfile; 
        $rootScope.profileWidget.expand = true;
    };
    
    $scope.save = function(){
        
        var tei = angular.copy($scope.selectedTei);
        tei.attributes = [];
        //prepare to update the tei on the server side 
        angular.forEach($scope.selectedTei.attributes, function(attribute){
            if(!angular.isUndefined(attribute.value)){
                tei.attributes.push({attribute: attribute.attribute, value: attribute.value});
            } 
        });
        
        TEIService.update(tei).then(function(updateResponse){
            
            if(updateResponse.status !== 'SUCCESS'){//update has failed
                var dialogOptions = {
                        headerText: 'update_error',
                        bodyText: updateResponse.description
                    };
                DialogService.showDialog({}, dialogOptions);
                return;
            }
            
            $scope.editProfile = !$scope.editProfile;
            CurrentSelection.set({tei: $scope.selectedTei, te: $scope.trackedEntity, pr: $scope.selectedProgram, enrollment: $scope.selectedEnrollment});   
        });       
    };
    
    $scope.cancel = function(){
        $scope.selectedTei.attributes = $scope.entityAttributes;  
        $scope.editProfile = !$scope.editProfile;
    };
});