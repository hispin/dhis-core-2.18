trackerCapture.controller('NotesController',
        function($scope,
                storage,
                DateUtils,
                EnrollmentService,
                CurrentSelection,
                orderByFilter) {
    
    var loginDetails = storage.get('LOGIN_DETAILS');
    var storedBy = '';
    if(loginDetails && loginDetails.userCredentials){
        storedBy = loginDetails.userCredentials.username;
    }
    
    var today = DateUtils.getToday();
    
    $scope.showMessagingDiv = false;
    $scope.showNotesDiv = true;
    
    $scope.$on('dashboardWidgets', function() {
        $scope.selectedEnrollment = null;
        var selections = CurrentSelection.get();                    
        $scope.selectedTei = angular.copy(selections.tei);
        $scope.selectedProgram = selections.pr;
        
        var selections = CurrentSelection.get();
        if(selections.selectedEnrollment){
            EnrollmentService.get(selections.selectedEnrollment.enrollment).then(function(data){    
                $scope.selectedEnrollment = data;   
                if(!angular.isUndefined( $scope.selectedEnrollment.notes)){
                    $scope.selectedEnrollment.notes = orderByFilter($scope.selectedEnrollment.notes, '-storedDate');            
                    angular.forEach($scope.selectedEnrollment.notes, function(note){
                        note.storedDate = DateUtils.formatToHrsMins(note.storedDate);
                    });
                }
            });
        }
        
        if($scope.selectedTei){
            //check if the selected TEI has any of the contact attributes
            //that can be used for communication
            var continueLoop = true;
            for(var i=0; i<$scope.selectedTei.attributes.length && continueLoop; i++){
                if( ($scope.selectedTei.attributes[i].type === 'phoneNumber' && $scope.selectedTei.attributes[i].show) || 
                    ($scope.selectedTei.attributes[i].type === 'email' && $scope.selectedTei.attributes[i].show) ){
                    $scope.messagingPossible = true;
                    continueLoop = false;
                }
            }
        }
    });
   
    $scope.searchNoteField = false;
    
    $scope.addNote = function(){
        
        if(!angular.isUndefined($scope.note) && $scope.note != ""){
            
            var newNote = {value: $scope.note};

            if(angular.isUndefined( $scope.selectedEnrollment.notes) ){
                $scope.selectedEnrollment.notes = [{value: $scope.note, storedDate: DateUtils.formatFromUserToApi(today), storedBy: storedBy}];
                
            }
            else{
                $scope.selectedEnrollment.notes.splice(0,0,{value: $scope.note, storedDate: DateUtils.formatFromUserToApi(today), storedBy: storedBy});
            }

            var e = angular.copy($scope.selectedEnrollment);

            e.notes = [newNote];
            EnrollmentService.update(e).then(function(){
                $scope.note = '';
                $scope.addNoteField = false; //note is added, hence no need to show note field.                
            });
        }        
    };
    
    $scope.clearNote = function(){
         $scope.note = '';           
    };
    
    $scope.searchNote = function(){        
        $scope.searchNoteField = $scope.searchNoteField === false ? true : false;
        $scope.noteSearchText = '';
    };
    
    $scope.showNotes = function(){
        $scope.showNotesDiv = !$scope.showNotesDiv;
        $scope.showMessagingDiv = !$scope.showMessagingDiv;
    };
    
    $scope.showMessaging = function(){
        $scope.showNotesDiv = !$scope.showNotesDiv;
        $scope.showMessagingDiv = !$scope.showMessagingDiv;
    };
});