trackerCapture.controller('ProfileController',
        function($scope,                
                CurrentSelection,
                TEService,
                TEIService,
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
    $scope.$on('dashboard', function(event, args) { 
        var selections = CurrentSelection.get();
        $scope.selectedEntity = selections.tei; 
        $scope.selectedProgram = selections.pr; 
        
        if($scope.selectedEntity){
            TEService.get($scope.selectedEntity.trackedEntity).then(function(te){
                $scope.trackedEntity = te;
            });
            
            $scope.processTeiAttributes();
        }
        
    });
    
    //display only those attributes that belong the selected program
    //if no program, display attributesInNoProgram
    $scope.processTeiAttributes = function(){        
        
        angular.forEach($scope.selectedEntity.attributes, function(att){
            if(att.type === 'number' && !isNaN(parseInt(att.value))){
                att.value = parseInt(att.value);
            }
        });
        
        if($scope.selectedProgram){
            //show only those attributes in selected program            
            AttributesFactory.getByProgram($scope.selectedProgram).then(function(atts){
                for(var i=0; i<$scope.selectedEntity.attributes.length; i++){
                    $scope.selectedEntity.attributes[i].show = false;
                    var processed = false;
                    for(var j=0; j<atts.length && !processed; j++){
                        if($scope.selectedEntity.attributes[i].attribute === atts[j].id){
                            processed = true;
                            $scope.selectedEntity.attributes[i].show = true;
                        }
                    }                                   
                }
            }); 
        }
        else{
            //show attributes in no program
            AttributesFactory.getWithoutProgram().then(function(atts){
                for(var i=0; i<$scope.selectedEntity.attributes.length; i++){
                    $scope.selectedEntity.attributes[i].show = false;
                    var processed = false;
                    for(var j=0; j<atts.length && !processed; j++){
                        if($scope.selectedEntity.attributes[i].attribute === atts[j].id){
                            processed = true;
                            $scope.selectedEntity.attributes[i].show = true;
                        }
                    }                                   
                }
            });
        }              
    };
    
    $scope.enableEdit = function(){
        $scope.entityAttributes = angular.copy($scope.selectedEntity.attributes);
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
                        headerText: 'update_error',
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
