// big chunks of this is based on code from:
// http://brian.io/lawnchair

dhis2.util.namespace( 'dhis2.storage' );

dhis2.storage.Store = function ( options, callback ) {
    var Store = dhis2.storage.Store;

    this.name = options.name || 'records';
    this.record = options.record || 'record';

    if ( arguments.length <= 2 && arguments.length > 0 ) {
        callback = (typeof arguments[0] === 'function') ? arguments[0] : arguments[1];
        options = (typeof arguments[0] === 'function') ? {} : arguments[0];
    } else {
        throw 'Incorrect # of ctor args!'
    }

    if ( !JSON ) throw 'JSON unavailable! Include http://www.json.org/json2.js to fix.';
    if ( typeof callback !== 'function' ) throw 'No callback was provided.';
    if ( Store.adapters.length == 0 ) throw 'No adapters was provided.';

    var adapter;

    if ( options.adapter ) {
        for ( var i = 0, l = Store.adapters.length; i < l; i++ ) {
            if ( options.adapter === Store.adapters[i].id ) {
                adapter = Store.adapters[i].valid() ? Store.adapters[i] : undefined;
                break;
            }
        }
    } else {
        for ( var i = 0, l = Store.adapters.length; i < l; i++ ) {
            adapter = Store.adapters[i].valid() ? Store.adapters[i] : undefined;
            if ( adapter ) break;
        }
    }

    if ( !adapter ) throw 'No valid adapter.';

    // mixin adapter functions
    for ( var i in adapter ) {
        if ( adapter.hasOwnProperty( i ) ) {
            this[i] = adapter[i];
        }
    }

    this.init( options, callback );
};

dhis2.storage.Store.adapters = [];

dhis2.storage.Store.adapter = function ( id, obj ) {
    var Store = dhis2.storage.Store;
    var adapter_interface = "init save remove exists load all".split( ' ' );

    var missing_functions = [];

    // verify adapter
    for ( var i in adapter_interface ) {
        if ( !obj.hasOwnProperty( adapter_interface[i] ) || typeof obj[adapter_interface[i]] !== 'function' ) {
            missing_functions.push( adapter_interface[i] );
        }
    }

    if ( missing_functions.length > 0 ) {
        throw 'Adapter \'' + id + '\' does not meet interface requirements, missing: ' + missing_functions.join( ' ' );
    }

    // for now just assume that all adapters follow the interface requirements
    obj['id'] = id;
    Store.adapters.splice( 0, 0, obj );
};

// web storage support (localStorage)
dhis2.storage.Store.adapter( 'dom', (function () {
    var storage = window.localStorage;

    var indexer = function ( name ) {
        return {
            key: name + '.__index__',

            all: function () {
                var a = storage.getItem( this.key );

                if ( a ) {
                    try {
                        a = JSON.parse( a );
                    } catch ( e ) {
                        a = null;
                    }
                }

                if ( a == null ) {
                    storage.setItem( this.key, JSON.stringify( [] ) );
                }

                return JSON.parse( storage.getItem( this.key ) );
            },

            add: function ( key ) {
                var a = this.all();
                a.push( key );
                storage.setItem( this.key, JSON.stringify( a ) );
            },

            remove: function ( key ) {
                var a = this.all();

                if ( a.indexOf( key ) != -1 ) {
                    dhis2.array.remove( a, a.indexOf( key ), a.indexOf( key ) );
                    storage.setItem( this.key, JSON.stringify( a ) );
                }
            },

            find: function ( key ) {
                var a = this.all();
                return a.indexOf( key );
            }
        }
    }

    return {
        valid: function () {
            return !!storage;
        },

        init: function ( options, callback ) {
            this.indexer = indexer( this.name );
            if ( callback ) callback.call( this, this, options );
        },

        save: function ( key, obj, callback ) {
            var key = this.name + '.' + key;
            if ( this.indexer.find( key ) == -1 ) this.indexer.add( key );
            storage.setItem( key, JSON.stringify( obj ) );
            if ( callback ) callback.call( this, this, obj );

            return this;
        },

        remove: function ( key, callback ) {
            var key = this.name + '.' + key;
            this.indexer.remove( key );
            storage.removeItem( key );
            if ( callback ) callback.call( this, this );

            return this;
        },

        exists: function ( key, callback ) {
            key = this.name + '.' + key;
            var success = storage.getItem( key ) != null;
            if ( callback ) callback.call( this, this, success );

            return this;
        },

        keys: function ( callback ) {
            var that = this;
            var keys = this.indexer.all().map( function ( r ) {
                return r.replace( that.name + '.', '' )
            } );

            if ( callback ) callback.call( this, this, keys );

            return this;
        },

        load: function ( key, callback ) {
            key = this.name + '.' + key;
            var obj = storage.getItem( key );

            if ( obj ) {
                obj = JSON.parse( obj );
                if ( callback ) callback.call( this, this, obj );
            }

            return this;
        },

        all: function ( callback ) {
            var idx = this.indexer.all();
            var arr = [];

            for ( var k = 0; k < idx.length; k++ ) {
                arr.push( JSON.parse( storage.getItem( idx[k] ) ) );
            }

            if ( callback ) callback.call( this, this, arr );

            return this;
        }
    };
})() );

// web storage support (indexedDb)
dhis2.storage.Store.adapter( 'indexed-db', (function () {
    function getIDB() {
        return window.indexedDB || window.webkitIndexedDB || window.mozIndexedDB || window.oIndexedDB || window.msIndexedDB;
    }

    return {
        valid: function () {
            return false;
            // return !!getIDB();
        },

        init: function ( options, callback ) {
            throw 'Init not implemented'
        },

        save: function ( key, obj, callback ) {
            throw 'Save not implemented'
        },

        remove: function ( key, callback ) {
            throw 'Remove not implemented'
        },

        exists: function ( key, callback ) {
            throw 'Exists not implemented'
        },

        load: function ( key, callback ) {
            throw 'Load not implemented'
        },

        all: function ( callback ) {
            throw 'All not implemented'
        }
    };
})() );
