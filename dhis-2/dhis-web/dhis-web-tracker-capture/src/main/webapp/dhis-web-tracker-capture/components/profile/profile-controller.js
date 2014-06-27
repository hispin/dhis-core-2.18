trackerCapture.controller('ProfileController',
        function($rootScope,
                $scope,     
                orderByFilter,
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
    $scope.$on('selectedEntity', function(event, args) { 
        var selections = CurrentSelection.get();
        $scope.selectedTei = selections.tei;
        $scope.trackedEntity = selections.te;
        $scope.selectedProgram = selections.pr;       
        $scope.processTeiAttributes();
    });
    
    //display only those attributes that belong the selected program
    //if no program, display attributesInNoProgram
    $scope.processTeiAttributes = function(){        
        
        angular.forEach($scope.selectedTei.attributes, function(att){
            if(att.type === 'number' && !isNaN(parseInt(att.value))){
                att.value = parseInt(att.value);
            }            
        });        
        
        if($scope.selectedProgram){
            //show only those attributes in selected program            
            AttributesFactory.getByProgram($scope.selectedProgram).then(function(atts){    
                
                $scope.selectedTei.attributes = $scope.showRequiredAttributes(atts,$scope.selectedTei.attributes);   
                
            }); 
        }
        else{
            //show attributes in no program
            AttributesFactory.getWithoutProgram().then(function(atts){
                
                $scope.selectedTei.attributes = $scope.showRequiredAttributes(atts,$scope.selectedTei.attributes);
                
            });
        }
        
        $scope.selectedTei.attributes = orderByFilter($scope.selectedTei.attributes, '-order');
        $scope.selectedTei.attributes.reverse();
    };
    
    $scope.showRequiredAttributes = function(requiredAttributes, availableAttributes){
        
        var teiAttributes = availableAttributes;
        //first reset teiAttributes
        for(var j=0; j<teiAttributes.length; j++){
            teiAttributes[j].show = false;
        }
        
        //identify which ones to show
        for(var i=0; i<requiredAttributes.length; i++){
            var processed = false;
            for(var j=0; j<teiAttributes.length && !processed; j++){
                if(requiredAttributes[i].id === teiAttributes[j].attribute){
                    processed = true;
                    teiAttributes[j].show = true;
                    teiAttributes[j].order = i;
                }
            }

            if(!processed){//attribute was empty, so a chance to put some value
                teiAttributes.push({show: true, order: i, attribute: requiredAttributes[i].id, displayName: requiredAttributes[i].name, type: requiredAttributes[i].valueType, value: ''});
            }                   
        }
        
        return teiAttributes;
    };
    
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
        });
        $scope.editProfile = !$scope.editProfile;
    };
    
    $scope.cancel = function(){
        $scope.selectedTei.attributes = $scope.entityAttributes;  
        $scope.editProfile = !$scope.editProfile;
    };
});