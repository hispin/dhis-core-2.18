'use strict';

/* Controllers */
var cacheCleanerControllers = angular.module('cacheCleanerControllers', [])

//Controller for settings page
.controller('MainController', function($scope, storage, $window, idbStorageService, ModalService) {
    
    $scope.afterClearing = false;
    
    var getItemsToClear = function(){
        
        $scope.lsCacheExists = false;
        $scope.idxCacheExists = false;
        
        $scope.lsKeys = [], $scope.dbKeys = [];
        
        var reservedLocalStorageKeys = ['key', 'getItem', 'setItem', 'removeItem', 'clear', 'length'];
    
        for(var key in $window.localStorage){
            if(reservedLocalStorageKeys.indexOf(key) === -1)
            {
                $scope.lsKeys.push({id: key, remove: false});
                $scope.lsCacheExists = true;
            }
        }

        var idxDBs = ['dhis2ou', 'dhis2', 'dhis2tc', 'dhis2ec', 'dhis2de'];    
        angular.forEach(idxDBs, function(db){
            idbStorageService.dbExists(db).then(function(res){
                if( res ){
                    $scope.dbKeys.push({id: db, remove: false});
                    $scope.idxCacheExists = true;
                }
            });
        });        
    };     
    
    getItemsToClear();
    
    $scope.clearCache = function(){
        
        var modalOptions = {
            closeButtonText: 'cancel',
            actionButtonText: 'proceed',
            headerText: 'clearing_cache',
            bodyText: 'proceed_cleaning'
        };

        ModalService.showModal({}, modalOptions).then(function(){
            angular.forEach($scope.lsKeys, function(lsKey){
                if(lsKey.remove){
                    storage.remove(lsKey.id);
                    console.log('removed from local storage:  ', lsKey.id);
                }
            });

            angular.forEach($scope.dbKeys, function(dbKey){
                if(dbKey.remove){
                    idbStorageService.deleteDb(dbKey.id).then(function(res){
                        if(res){
                            console.log('removed from local indexeddb:  ', dbKey.id);
                        }
                        else{
                            console.log('failed to remove from local indexeddb:  ', dbKey.id);
                        }
                        
                    });
                }
            });   
            $scope.afterClearing = true;
            getItemsToClear();            
        });
    };
    
    $scope.selectAll = function(){
        angular.forEach($scope.lsKeys, function(lsKey){
            lsKey.remove = true;
        });

        angular.forEach($scope.dbKeys, function(dbKey){
            dbKey.remove = true;
        });
    };
});