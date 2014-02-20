'use strict';

/* Filters */

var eventCaptureFilters = angular.module('eventCaptureFilters', [])

.filter('gridFilter', function(){
    
    /* array is first argument, each addiitonal argument is prefixed by a ":" in filter markup*/
    return function(dataArray, searchTerm){
        
        if(!dataArray ) return;
        
        /* when term is cleared, return full array*/        
        if( !searchTerm){
            return dataArray;
        }
        else{
            
            /* otherwise filter the array */
            var term = searchTerm.toLowerCase();
            return dataArray.filter(function( item){
                return item.id.toLowerCase().indexOf(term) > -1 || item.name.toLowerCase().indexOf(term) > -1;    
            });
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