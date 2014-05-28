trackerCapture.controller('ProfileController',
        function($scope,                
                storage,
                CurrentSelection,
                TEIService,
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
        
        angular.forEach($scope.selectedEntity.attributes, function(att){
            if(att.type === 'number' && !isNaN(parseInt(att.value))){
                att.value = parseInt(att.value);
            }
        });
        $scope.entityAttributes = angular.copy($scope.selectedEntity.attributes);
    });
    
    $scope.showEdit = function(){
      $scope.editProfile = !$scope.editProfile; 
    };
    
    $scope.save = function(){
        
        var tei = angular.copy($scope.selectedEntity);
        tei.attributes = [];
        //prepare to update the tei on the server side 
        angular.forEach($scope.selectedEntity.attributes, function(attribute){
            if(!angular.isUndefined(attribute.value)){
                tei.attributes.push({attribute: attribute.attribute, value: attribute.value});
            } 
        });
        
        TEIService.update(tei).then(function(updateResponse){
            
            if(updateResponse.status !== 'SUCCESS'){//update has failed
                var dialogOptions = {
                        headerText: 'registration_error',
                        bodyText: updateResponse.description
                    };
                DialogService.showDialog({}, dialogOptions);
                return;
            }            
        });
        
        console.log('the tei is:  ', tei);
        $scope.editProfile = !$scope.editProfile;
    };
    
    $scope.cancel = function(){
        $scope.selectedEntity.attributes = $scope.entityAttributes;  
        $scope.editProfile = !$scope.editProfile;
    };
});
