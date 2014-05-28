trackerCapture.controller('ProfileController',
        function($scope,                
                storage,
                CurrentSelection,
                TEIService,
                AttributesFactory,
                TranslationService) {

    TranslationService.translate();
    
    //attributes for profile    
    $scope.attributes = {};    
    $scope.editProfile = false;    
    
    angular.forEach(storage.get('ATTRIBUTES'), function(attribute){
        $scope.attributes[attribute.id] = attribute;
    }); 
    
    //listen for the selected entity
    /*$scope.$on('selectedEntity', function(event, args) { 
        var selections = CurrentSelection.get();
        $scope.selectedEntity = selections.tei; 
        $scope.selectedProgram = selections.pr ? storage.get(selections.pr) : null;        
        $scope.getTei();
        
    });*/
    
    $scope.$on('dashboard', function(event, args) { 
        var selections = CurrentSelection.get();
        $scope.selectedEntity = selections.tei; 
        $scope.selectedProgram = selections.pr ? storage.get(selections.pr) : null; 
        $scope.processTeiAttributes();
        
    });
    
    //display only those attributes that belong the selected program
    //if no program, display attributesInNoProgram
    $scope.processTeiAttributes = function(){
        
        $scope.entityAttributes = angular.copy($scope.selectedEntity.attributes);
        
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
        
        $scope.selectedEntity.attributes = AttributesFactory.hideAttributesNotInProgram($scope.selectedEntity, $scope.selectedProgram);
    };
    
    $scope.enableEdit = function(){
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
        $scope.editProfile = !$scope.editProfile;
    };
    
    $scope.cancel = function(){
        $scope.selectedEntity.attributes = $scope.entityAttributes;  
        $scope.editProfile = !$scope.editProfile;
    };
});
