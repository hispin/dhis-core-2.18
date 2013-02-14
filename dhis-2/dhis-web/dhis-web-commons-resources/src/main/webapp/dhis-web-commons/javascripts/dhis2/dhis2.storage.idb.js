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
