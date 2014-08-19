trackerCapture.controller('NotesController',
        function($scope,
                $filter,
                storage,
                EnrollmentService,
                CurrentSelection,
                orderByFilter,
                TranslationService) {

    TranslationService.translate();
    
    var loginDetails = storage.get('LOGIN_DETAILS');
    var storedBy = '';
    if(loginDetails){
        storedBy = loginDetails.userCredentials.username;
    }
    
    var today = moment();
    today = Date.parse(today);
    today = $filter('date')(today, 'yyyy-MM-dd');
    
    $scope.$on('dashboardWidgets', function(event, args) {
        $scope.selectedEnrollment = null;
        var selections = CurrentSelection.get();
        if(selections.enrollment){
            EnrollmentService.get(selections.enrollment.enrollment).then(function(data){    
                $scope.selectedEnrollment = data;   
                if(!angular.isUndefined( $scope.selectedEnrollment.notes)){
                    $scope.selectedEnrollment.notes = orderByFilter($scope.selectedEnrollment.notes, '-storedDate');            
                    angular.forEach($scope.selectedEnrollment.notes, function(note){
                        note.storedDate = moment(note.storedDate).format('YYYY-MM-DD @ hh:mm A');
                    });
                }
            });
        }        
    });
   
    $scope.searchNoteField = false;
    $scope.addNoteField = false;    
    
    $scope.showAddNote = function() {
        $scope.addNoteField = !$scope.addNoteField;
    };
    
    $scope.addNote = function(){
        
        if(!angular.isUndefined($scope.note) && $scope.note != ""){
            
            var newNote = {value: $scope.note};

            if(angular.isUndefined( $scope.selectedEnrollment.notes) ){
                $scope.selectedEnrollment.notes = [{value: $scope.note, storedDate: today, storedBy: storedBy}];
                
            }
            else{
                $scope.selectedEnrollment.notes.splice(0,0,{value: $scope.note, storedDate: today, storedBy: storedBy});
            }

            var e = angular.copy($scope.selectedEnrollment);

            e.notes = [newNote];
            EnrollmentService.update(e).then(function(data){
                $scope.note = '';
                $scope.addNoteField = false; //note is added, hence no need to show note field.                
            });
        }        
    };
    
    $scope.closeAddNote = function(){
         $scope.addNoteField = false;
         $scope.note = '';           
    };
    
    $scope.searchNote = function(){        
        $scope.searchNoteField = $scope.searchNoteField === false ? true : false;
        $scope.noteSearchText = '';
    };
});