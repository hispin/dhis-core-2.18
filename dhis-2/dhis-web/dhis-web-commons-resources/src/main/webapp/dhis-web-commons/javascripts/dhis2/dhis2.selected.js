/**
 * Simple plugin for keeping two <select /> elements in sync.
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */

!(function( $, window, document, undefined ) {
  var methods = {
    create: function( options ) {
      var context = {};
      $.extend(context, $.fn.selected.defaults, options);

      if( context.target === undefined ) {
        $.error('selected: Missing options.target, please add your target box either as a jqEl or as a query.');
      } else if( context.url === undefined ) {
        $.error('selected: Missing options.url, please give URL of where to find the source data.');
      } else if( !$.isFunction(context.handler) ) {
        $.error('selected: Invalid options.handler.');
      }

      // pass-through if jqEl, query if string
      context.source = this;
      context.target = $(context.target);
      context.search = $(context.search);

      if( !(context.source instanceof $) ) {
        $.error('selected: Invalid source.');
      } else if( !(context.target instanceof $) ) {
        $.error('selected: Invalid target.');
      }

      context.source.data('selected', context);
      context.target.data('selected', context);

      context.page = 1;
      context.defaultProgressiveLoader(context);

      context.source.on('dblclick', 'option', context.defaultSourceDblClickHandler);
      context.target.on('dblclick', 'option', context.defaultTargetDblClickHandler);
      context.source.on('scroll', context.makeScrollHandler(context));

      if( context.search instanceof $ ) {
        context.search.on('keypress', context.makeSearchHandler(context));
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
    makeSearchHandler: function( context ) {
      return function( e ) {
        if( e.keyCode == 13 ) {
          context.page = 1;
          context.like = $(this).val();
          context.defaultProgressiveLoader(context);
          e.preventDefault();
        }
      }
    },
    makeScrollHandler: function( context ) {
      return function( e ) {
        if( context.source[0].offsetHeight + context.source.scrollTop() >= context.source[0].scrollHeight ) {
          context.defaultProgressiveLoader(context);
        }
      }
    },
    defaultProgressiveLoader: function( context ) {
      if( context.page === undefined ) {
        return;
      }

      var request = {
        url: context.url,
        data: {
          paging: true,
          pageSize: 50,
          page: context.page
        },
        dataType: 'json'
      };

      if( context.like !== undefined && context.like.length > 0 ) {
        request.data.filter = 'name:like:' + context.like;
      }

      return $.ajax(request).done(function( data ) {
        if( data.pager.page == 1 ) {
          context.source.children().remove();
        }

        context.page++;

        if( context.page > data.pager.pageCount ) {
          delete context.page;
        }

        if( data[context.iterator] === undefined ) {
          return;
        }

        $.each(data[context.iterator], function( idx ) {
          if( context.target.find('option[value=' + this.id + ']').length == 0 ) {
            context.source.append(context.handler(this));
          }
        });
      }).fail(function() {
        context.source.children().remove();
      });
    }
  };

})(jQuery, window, document);
