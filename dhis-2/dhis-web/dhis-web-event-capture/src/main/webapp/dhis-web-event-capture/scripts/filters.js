'use strict';

/* Filters */

var eventCaptureFilters = angular.module('eventCaptureFilters', [])

.filter('gridFilter', function(){    
    
    return function(data, filterText, filterObjectTypes){
        
        if(!data ){
            return;
        }
        
        if(!filterText){
            return data;
        }        
        else{            
            
            var keys = [];
            var filteredData = data;
            
            for(var key in filterText){
                keys.push(key);
                
                for(var i=0; i<filteredData.length; i++){
                    
                    var val = filteredData[i][key];
                    
                    if( filterObjectTypes[key].dataElement.type === 'date'){
                        
                        if( filterText[key].start || filterText[key].end){
                            var start = moment(filterText[key].start, 'YYYY-MM-DD');
                            var end = moment(filterText[key].end, 'YYYY-MM-DD');  
                            var date = moment(val, 'YYYY-MM-DD');                              
                            
                            if( ( Date.parse(date) > Date.parse(end) ) || (Date.parse(date) < Date.parse(start)) ){  
                                filteredData.splice(i,1);
                                i--;
                            }                                                        
                        }
                        
                    }
                    else{
                        if( filterObjectTypes[key].dataElement.type === 'int'){
                            val = val.toString();
                        }

                        val = val.toLowerCase();
                        if( val.indexOf(filterText[key].toLowerCase()) === -1 ){
                            filteredData.splice(i,1);
                            i--;
                        }                        
                    }
                                        
                }
            }            
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