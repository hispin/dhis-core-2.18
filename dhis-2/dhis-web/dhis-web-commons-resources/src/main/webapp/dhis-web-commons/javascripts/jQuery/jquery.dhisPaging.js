/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * @author mortenoh
 */

(function($) {
  var templates = {
    button: "<button id='${id}' style='width: 80px; margin-bottom: 4px; margin-top: 4px;'>${text}</button>",
    option: "<option>${text}</option>",
    option_selected: "<option selected='selected'>${text}</option>",
    wrapper: "<div id='${id}' style='padding: 0; margin: 0;' />",
    pagesize_input: "<input id='${id}' type='text' style='width: 100px;'/>",
    filter_input: "<input id='${id}'></input>",
    select_page: "<select id='${id}'></select>"
  }

  var methods = {
    load: function(select_id) {
        var $select = $("#" + select_id);
        var settings = $select.data("settings");
        var params = settings.params;

        var id = $select.attr("id");
        var wrapper_id = id + "_wrapper";
        $wrapper = $("#" + wrapper_id);
        var select_page_id = id + "_select_page";
        var $select_page = $("#" + select_page_id);
        var next_button_id = id + "_next_button";
        var $next_button = $("#" + next_button_id);
        var previous_button_id = id + "_previous_button";
        var $previous_button = $("#" + previous_button_id);
        var filter_input_id = id + "_filter_input";
        var pagesize_input_id = id + "_pagesize_input";
        var $pagesize_input = $("#" + pagesize_input_id);

        $.getJSON(settings.url, $.param( settings.params ), function(json) {
            $select.empty();
            $select_page.empty();

            params.currentPage = json.paging.currentPage;
            params.numberOfPages = json.paging.numberOfPages;
            params.pageSize = json.paging.pageSize;
            params.startPage = json.paging.startPage;

            settings.handler($select, json);

            for(var j=1; j<=params.numberOfPages; j++) {
                if(params.currentPage == j) {
                  $select_page.append( $.tmpl(templates.option_selected, {"text": j}) );
                } else {
                  $select_page.append( $.tmpl(templates.option, {"text": j}) );
                }
            }

            $("#" + pagesize_input_id).val( params.pageSize );

            $previous_button.removeAttr("disabled");
            $next_button.removeAttr("disabled");

            if(params.currentPage == params.startPage) {
                $previous_button.attr("disabled", "disabled");
            }

            if(params.currentPage == params.numberOfPages) {
                $next_button.attr("disabled", "disabled");
            }

            settings.params = params;
            $select.data("settings", settings);
         });
    },
    init: function(options) {
      var settings = { }
      var params = { }

      $.extend(settings, options);
      $.extend(params, options.params);
      params.usePaging = true;

      var $select = $(this);
      var id = $(this).attr("id");
      var wrapper_id = id + "_wrapper";
      var select_page_id = id + "_select_page";
      var next_button_id = id + "_next_button";
      var previous_button_id = id + "_previous_button";
      var filter_input_id = id + "_filter_input";
      var pagesize_input_id = id + "_pagesize_input";

      $select.wrap( $.tmpl(templates.wrapper, { "id": wrapper_id }) );
      $wrapper = $("#" + wrapper_id);

      $wrapper.append( $.tmpl(templates.select_page, { "id": select_page_id }) )
      $select_page = $("#" + select_page_id);

      $wrapper.append( $.tmpl(templates.button, { "id": previous_button_id, "text":"previous" }) );
      $previous_button = $("#" + previous_button_id);

      $wrapper.append( $.tmpl(templates.button, { "id": next_button_id, "text":"next" }) );
      $next_button = $("#" + next_button_id);

      $wrapper.append( $.tmpl(templates.pagesize_input, { "id": pagesize_input_id }) );
      $pagesize_input = $("#" + pagesize_input_id);

      settings.params = params;
      $select.data("settings", settings);
      methods.load("" + id);

      $next_button.click(function() {
          params.currentPage = +params.currentPage + 1;
          settings.params = params;
          $select.data("settings", settings);

          methods.load("" + id);
      });

      $previous_button.click(function() {
          params.currentPage = +params.currentPage - 1;
          settings.params = params;
          $select.data("settings", settings);

          methods.load("" + id);
      });

      $select_page.change(function() {
          params.currentPage = +$(this).find(":selected").val();
          settings.params = params;
          $select.data("settings", settings);

          methods.load("" + id);
      });

      $pagesize_input.change(function() {
         params.pageSize = +$(this).val();
         params.currentPage = 1;
         settings.params = params;
         $select.data("settings", settings);

         methods.load("" + id);
      });
    }
  }

  $.fn.dhisPaging = function(method) {
    if(methods[method]) {
      return methods[ method ].apply( this, Array.prototype.slice.call( arguments, 1 ));
    } else if (typeof method === 'object' || !method) {
      return methods.init.apply(this, arguments);
    } else {
      $.error('Method ' +  method + ' does not exist on jQuery.dhisPaging' );
    }  
  };
})(jQuery);
