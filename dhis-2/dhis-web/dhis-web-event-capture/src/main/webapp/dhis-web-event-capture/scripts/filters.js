'use strict';

/* Filters */

var eventCaptureFilters = angular.module('eventCaptureFilters', [])

.filter('gridFilter', function($filter){    
    
    return function(data, filterText, filterTypes){
        
        if(!data ){
            return;
        }
        
        if(!filterText){
            return data;
        }        
        else{            
            
            var dateFilter = {}, nonDateFilter = {}, filteredData = data;
            
            for(var key in filterText){
                
                if(filterTypes[key] === 'date'){
                    if( filterText[key].start || filterText[key].end){
                        dateFilter[key] = filterText[key];
                    }
                }
                else{
                    nonDateFilter[key] = filterText[key];
                }
            }           
                      
            filteredData = $filter('filter')(filteredData, nonDateFilter);             
                        
            return filteredData;
        } 
    };   
})

.filter('paginate', function(Paginator) {
    return function(input, rowsPerPage) {
        if (!input) {
            return input;
        }

        if (rowsPerPage) {
            Paginator.rowsPerPage = rowsPerPage;
        }       
        
        Paginator.itemCount = input.length;

        return input.slice(parseInt(Paginator.page * Paginator.rowsPerPage), parseInt((Paginator.page + 1) * Paginator.rowsPerPage + 1) - 1);
    };
})

.filter('forLoop', function() {
    return function(input, start, end) {
        input = new Array(end - start);
        for (var i = 0; start < end; start++, i++) {
            input[i] = start;
        }
        return input;
    };
});