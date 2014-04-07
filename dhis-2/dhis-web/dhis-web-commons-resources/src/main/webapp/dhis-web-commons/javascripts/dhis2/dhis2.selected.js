/**
 * Simple plugin for keeping two <select /> elements in sync.
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */

!(function( $, window, document, undefined ) {
  var methods = {
    create: function( options ) {
      var settings = {};
      $.extend(settings, $.fn.selected.defaults, options);

      if( settings.target === undefined ) {
        $.error('selected: Missing options.target, please add your target box either as a jqEl or as a query.');
      } else if( settings.url === undefined ) {
        $.error('selected: Missing options.url, please give URL of where to find the source data.');
      } else if( !$.isFunction(settings.handler) ) {
        $.error('selected: Invalid options.handler.');
      }

      // pass-through if jqEl, query if string
      settings.source = this;
      settings.target = $(settings.target);
      settings.search = $(settings.search);

      if( !(settings.source instanceof $) ) {
        $.error('selected: Invalid source.');
      } else if( !(settings.target instanceof $) ) {
        $.error('selected: Invalid target.');
      }

      settings.source.data('selected', settings);
      settings.target.data('selected', settings);

      settings.page = 1;
      settings.defaultProgressiveLoader(settings);

      settings.source.on('dblclick', 'option', settings.defaultSourceDblClickHandler);
      settings.target.on('dblclick', 'option', settings.defaultTargetDblClickHandler);
      settings.source.on('scroll', settings.makeScrollHandler(settings));

      if( settings.search instanceof $ ) {
        settings.search.on('keypress', settings.makeSearchHandler(settings));
      }
    }
  };

  methods.defaultMethod = methods.create;

  // method dispatcher
  $.fn.selected = function( method ) {
    var args = Array.prototype.slice.call(arguments, 1);

    if( $.isFunction(methods[method]) ) {
      return methods[method].apply(this, args);
    } else if( $.isPlainObject(method) || $.type(method) === 'undefined' ) {
      return methods.defaultMethod.apply(this, arguments);
    } else {
      $.error('selected: Unknown method');
    }
  };

  $.fn.selected.defaults = {
    iterator: 'objects',
    handler: function( item ) {
      return $('<option/>').val(item.id).text(item.name);
    },
    defaultMoveSelected: function( sel ) {
      $(sel).find(':selected').trigger('dblclick');
    },
    defaultMoveAll: function( sel ) {
      $(sel).find('option').attr('selected', 'selected').trigger('dblclick');
    },
    defaultSourceDblClickHandler: function() {
      var $this = $(this);
      var $selected = $this.parent().data('selected');

      if( $selected === undefined ) {
        $.error('selected: Invalid source.parent, does not contain selected object.');
      }

      $this.removeAttr('selected');
      $selected.target.append($this);
    },
    defaultTargetDblClickHandler: function() {
      var $this = $(this);
      var $selected = $this.parent().data('selected');

      if( $selected === undefined ) {
        $.error('selected: Invalid target.parent, does not contain selected object.');
      }

      $this.removeAttr('selected');
      $selected.source.append($this);
    },
    makeSearchHandler: function( settings ) {
      return function( e ) {
        if( e.keyCode == 13 ) {
          settings.defaultProgressiveLoader(settings, $(this).val());
          e.preventDefault();
        }
      }
    },
    makeScrollHandler: function( settings ) {
      return function( e ) {
        if( settings.source[0].offsetHeight + settings.source.scrollTop() >= settings.source[0].scrollHeight ) {
          settings.defaultProgressiveLoader(settings);
        }
      }
    },
    defaultProgressiveLoader: function( settings, search ) {
      if( settings.page === undefined ) {
        return;
      }

      var request = {
        url: settings.url,
        data: {
          paging: true,
          pageSize: 50,
          page: settings.page
        },
        dataType: 'json'
      };

      if( search !== undefined && search.length > 0 ) {
        request.data.filter = 'name:like:' + search;
      }

      return $.ajax(request).done(function( data ) {
        if( data.pager.page == 1 ) {
          settings.source.children().remove();
        }

        settings.page++;

        if( settings.page > data.pager.pageCount ) {
          delete settings.page;
        }

        if( data[settings.iterator] === undefined ) {
          $.error('selected: Invalid iterator for source url: ' + settings.iterator);
        }

        $.each(data[settings.iterator], function( idx ) {
          if( settings.target.find('option[value=' + this.id + ']').length == 0 ) {
            settings.source.append(settings.handler(this));
          }
        });
      }).fail(function() {
        settings.source.children().remove();
      });
    }
  };

})(jQuery, window, document);
