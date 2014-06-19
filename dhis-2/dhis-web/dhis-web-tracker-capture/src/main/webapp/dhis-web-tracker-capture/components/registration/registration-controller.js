trackerCapture.controller('RegistrationController', 
        function($scope,
                $location,
                AttributesFactory,
                TEService,
                TEIService,
                EnrollmentService,
                DialogService,
                storage,
                TranslationService) {

    //do translation of the registration page
    TranslationService.translate();   
    
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
    $scope.enrollment = {enrollmentDate: '', incidentDate: ''};   
    
    AttributesFactory.getWithoutProgram().then(function(atts){
        $scope.attributes = atts;
    });
            
    $scope.trackedEntities = {available: []};
    TEService.getAll().then(function(entities){
        $scope.trackedEntities.available = entities;   
        $scope.trackedEntities.selected = $scope.trackedEntities.available[0];
    });
    
    //watch for selection of program
    $scope.$watch('selectedProgram', function() {        
        if( angular.isObject($scope.selectedProgram)){
            $scope.trackedEntityList = [];
            AttributesFactory.getByProgram($scope.selectedProgram).then(function(atts){
                $scope.attributes = atts;
            });
        }
    });    
    
    $scope.registerEntity = function(showDashboard){
        
        //check for form validity
        $scope.outerForm.submitted = true;        
        if( $scope.outerForm.$invalid ){
            return false;
        }
        
        //form is valid, continue the registration
        //get selected entity
        var selectedTrackedEntity = $scope.trackedEntities.selected.id; 
        if($scope.selectedProgram){
            selectedTrackedEntity = $scope.selectedProgram.trackedEntity.id;
        }
        
        //get tei attributes and their values
        var registrationAttributes = [];    
        angular.forEach($scope.attributes, function(attribute){
            if(!angular.isUndefined(attribute.value)){
                var att = {attribute: attribute.id, value: attribute.value};
                registrationAttributes.push(att);
            } 
        });       
        
        //prepare tei model and do registration
        $scope.tei = {trackedEntity: selectedTrackedEntity, orgUnit: $scope.selectedOrgUnit.id, attributes: registrationAttributes };   
        var teiId = '';
    
        TEIService.register($scope.tei).then(function(tei){
            
            if(tei.status === 'SUCCESS'){
                
                teiId = tei.reference;
                
                //registration is successful and check for enrollment
                if($scope.selectedProgram){    
                    //enroll TEI
                    var enrollment = {trackedEntityInstance: teiId,
                                program: $scope.selectedProgram.id,
                                status: 'ACTIVE',
                                dateOfEnrollment: $scope.enrollment.enrollmentDate,
                                dateOfIncident: $scope.enrollment.incidentDate
                            };
                    EnrollmentService.enroll(enrollment).then(function(data){
                        if(data.status != 'SUCCESS'){
                            //enrollment has failed
                            var dialogOptions = {
                                    headerText: 'enrollment_error',
                                    bodyText: data.description
                                };
                            DialogService.showDialog({}, dialogOptions);
                            return;
                        }
                    });
                }
            }
            else{
                //registration has failed
                var dialogOptions = {
                        headerText: 'registration_error',
                        bodyText: tei.description
                    };
                DialogService.showDialog({}, dialogOptions);
                return;
            }
            
            //reset form
            angular.forEach($scope.attributes, function(attribute){
                attribute.value = ''; 
            });
            $scope.enrollment.enrollmentDate = '';
            $scope.enrollment.incidentDate =  '';
            $scope.outerForm.submitted = false; 
            
            if(showDashboard){
                $location.path('/dashboard').search({selectedEntityId: teiId,
                                                selectedProgramId: $scope.selectedProgram ? $scope.selectedProgram.id : null});
            }            
        });
    };
});