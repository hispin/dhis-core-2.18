// dom storage support (localStorage)
dhis2.storage.Store.adapter( 'dom', (function () {
    var storage = window.localStorage;

    var indexer = function ( dbname, name ) {
        return {
            key: dbname + '.' + name + '.__index__',

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
            this.indexer = indexer( this.dbname, this.name );
            if ( callback ) callback.call( this, this, options );
        },

        add: function ( key, obj, callback ) {
            var key = this.dbname + '.' + this.name + '.' + key;
            if ( this.indexer.find( key ) == -1 ) this.indexer.add( key );
            storage.setItem( key, JSON.stringify( obj ) );
            if ( callback ) callback.call( this, this, obj );

            return this;
        },

        remove: function ( key, callback ) {
            var key = this.dbname + '.' + this.name + '.' + key;
            this.indexer.remove( key );
            storage.removeItem( key );
            if ( callback ) callback.call( this, this );

            return this;
        },

        exists: function ( key, callback ) {
            var key = this.dbname + '.' + this.name + '.' + key;
            var success = storage.getItem( key ) != null;
            if ( callback ) callback.call( this, this, success );

            return this;
        },

        keys: function ( callback ) {
            var that = this;
            var keys = this.indexer.all().map( function ( r ) {
                return r.replace( that.dbname + '.' + that.name + '.', '' )
            } );

            if ( callback ) callback.call( this, this, keys );

            return this;
        },

        fetch: function ( key, callback ) {
            var key = this.dbname + '.' + this.name + '.' + key;
            var obj = storage.getItem( key );

            if ( obj ) {
                obj = JSON.parse( obj );
                if ( callback ) callback.call( this, this, obj );
            }

            return this;
        },

        fetchAll: function ( callback ) {
            var idx = this.indexer.all();
            var arr = [];

            for ( var k = 0; k < idx.length; k++ ) {
                arr.push( JSON.parse( storage.getItem( idx[k] ) ) );
            }

            if ( callback ) callback.call( this, this, arr );

            return this;
        },

        destroy: function () {
            this.keys( function ( store, keys ) {
                for ( var key in keys ) {
                    this.remove( key );
                }
            } );

            localStorage.removeItem( this.dbname + '.' + this.name + '.__index__' );
        }
    };
})() );
