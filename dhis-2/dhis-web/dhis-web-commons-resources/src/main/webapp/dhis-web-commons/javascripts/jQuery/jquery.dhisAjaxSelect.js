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

// -----------------------------------------------
// Support functions
// -----------------------------------------------
// Array Remove - By John Resig (MIT Licensed)
Array.remove = function(array, from, to)
{
    var rest = array.slice((to || from) + 1 || array.length);
    array.length = from < 0 ? array.length + from : from;
    return array.push.apply(array, rest);
};

// http://www.west-wind.com/weblog/posts/2008/Oct/24/Using-jQuery-to-search-Content-and-creating-custom-Selector-Filters
// adds :containsNoCase to filtering. $(sel).find(":containsNC(key)").do();
$.expr[":"].containsNC = function(el, i, m)
{
    var search = m[3];
    if (!search)
        return false;
    return eval("/" + search + "/i").test($(el).text());
};

/* perform dblclick action on the sourceId */
function dhisAjaxSelect_moveAllSelected(sourceId)
{
    $("#" + sourceId).dblclick();
}

/* select all options and perform dblclick action on the sourceId */
function dhisAjaxSelect_moveAll(sourceId)
{
    var jqSource = $("#" + sourceId);
    jqSource.find("option").attr("selected", "selected");
    jqSource.dblclick();
}

function dhisAjaxSelect_moveSorted($target, $array)
{
    if ($target.children().size() === 0) {
        $target.append($array);
    } else {
        var array = $array.get();
        var array_idx = 0;
        var current = array.shift();
        var $children = $target.children();

        while (current !== undefined) {
            var $current = $(current);

            if ($children.eq(array_idx).html() > $current.html()) {
                $(current).insertBefore($children.eq(array_idx));
                current = array.shift();
            } else {
                array_idx++;
            }

            if ($children.size() < array_idx) {
                break;
            }
        }

        if (current !== undefined) {
            $target.append(current);
        }

        $target.append(array);
    }
}

/**
 * Return ghost for a select. Creates it if necessary.
 * 
 * @param $target
 *            jQuery object to work on
 */
function get_ghost_for_select($target)
{
    var ghost_target_id = $target.attr("id") + '_ghost';
    var $ghost_target = $("#" + ghost_target_id);

    if ($ghost_target.size() === 0) {
        $ghost_target = $('<select id="' + ghost_target_id + '" multiple="multiple"></select>');
// $ghost_target.hide();
        $ghost_target.appendTo('body');
    }

    return $ghost_target;
}

/* filter a select-target with a given key */
function dhisAjaxSelect_filter($target, key)
{
    $ghost_target = get_ghost_for_select($target);
    key = key.toLowerCase();

    if (key.length === 0) {
        dhisAjaxSelect_moveSorted($target, $ghost_target.children());
    } else {
        var $target_options = $target.children();
        var $ghost_target_options = $ghost_target.children();

        var $ghost_target_matched = $ghost_target_options.filter(':containsNC(' + key + ')');
        var $target_not_matched = $target_options.filter(':not( :containsNC(' + key + ') )');

        dhisAjaxSelect_moveSorted($ghost_target, $target_not_matched);
        dhisAjaxSelect_moveSorted($target, $ghost_target_matched);
    }
}

/**
 * filter a selector on data-key = value
 */
function dhisAjaxSelect_filter_on_kv($target, key, value)
{
    $ghost_target = get_ghost_for_select($target);

    if (key.length === 0) {
        dhisAjaxSelect_moveSorted($target, $ghost_target.children());
        return;
    }

    // filter options that do not match on select
    var $options = $target.children();
    var array = []; // array of options to move to ghost

    $options.each(function() {
        var $this = $(this);

        if ( !compare_data_with_kv($this, key, value) ) {
            array.push($this[0]);
        }
    });

    dhisAjaxSelect_moveSorted($ghost_target, $(array));

    // filter options that match on ghost
    var $ghost_options = $ghost_target.children();
    var ghost_array = []; // array of options to move to ghost

    $ghost_options.each(function() {
        var $this = $(this);

        if ( compare_data_with_kv($this, key, value) ) {
            ghost_array.push($this[0]);
        }
    });

    dhisAjaxSelect_moveSorted($target, $(ghost_array));

    // TODO temporary hack...
    function comp(a, b) { return (a.innerHTML > b.innerHTML) ? 1 : -1; }
    $target.html( $target.children().sort(comp) );
    // end
}

/**
 * 
 * @param $target
 *            jQuery object to work on
 * @param key
 *            data-entry key, $target.data(key)
 * @param value
 *            value to compare to
 * @returns {Boolean} true or false after comparing $target.data(key) with value
 */
function compare_data_with_kv($target, key, value)
{
    var target_value = $target.data(key);

    if(! $.isArray(target_value) ) {
        var type = typeof(target_value);

        if(type === "number") {
            target_value = [ target_value.toString() ];
        } else {
            target_value = target_value.split(",");
        }
    }

    if (target_value) {
        if ($.inArray(value.toString(), target_value) !== -1) {
            return true;
        }
    }

    return false;
}

function dhisAjaxSelect_availableList_dblclick(sourceId, targetId)
{
    return function()
    {
        var jqAvailableList = $("#" + sourceId);
        var jqSelectedList = $("#" + targetId);

        dhisAjaxSelect_moveSorted(jqSelectedList, jqAvailableList.find(":selected"));
    }
}

function dhisAjaxSelect_selectedList_dblclick(sourceId, targetId)
{
    return function()
    {
        var jqAvailableList = $("#" + targetId);
        var jqSelectedList = $("#" + sourceId);

        dhisAjaxSelect_moveSorted(jqAvailableList, jqSelectedList.find(":selected"));
    }
}

// -----------------------------------------------
// Plugin
// -----------------------------------------------

(function($)
{
    var templates = {
        wrapper : "<div id='${id}' style='padding: 0; margin: 0; background-color: #fefefe; border: 1px solid gray;' />",
        button : "<button id='${id}' type='button' style='width: 70px; margin: 4px;'>${text}</button>",
        option : "<option>${text}</option>",
        option_selected : "<option selected='selected'>${text}</option>",
        filter_input : "<input id='${id}' placeholder='Filter' type='text' style='width: 100%; height: 18px; border: 1px inset gray;' />",
        filter_select : "<select id='${id}' style='width: 100%; margin-bottom: 4px; margin-top: 0;'></select>"
    }

    var methods = {
        load : function(select_id)
        {
            var $select = $("#" + select_id);
            var settings = $select.data("settings");
            var params = settings.params;

            var id = $select.attr("id");
            var wrapper_id = id + "_wrapper";
            $wrapper = $("#" + wrapper_id);
            var filter_input_id = id + "_filter_input";
            var $filter_input = $("#" + filter_input_id);
            var filter_select_id = id + "_filter_select";

            $.post(settings.source, $.param(settings.params), function(json)
            {
                $select.empty();

                $.each(json[settings.iterator], function(i, item)
                {
                    var option = $(settings.handler(item));
                    $select.append(option);
                });

                if (settings.connectedTo) {
                    var $connectedTo = $('#' + settings.connectedTo);

                    if ($connectedTo) {
                        $connectedTo.children().each(function()
                        {
                            var value = $(this).attr("value");
                            $select.find("option[value=" + value + "]").remove();
                        });
                    }
                }
            });
        },
        init : function(options)
        {
            var settings = {}
            var params = {}

            $.extend(settings, options);
            $.extend(params, options.params);

            var $select = $(this);
            $select.css("border", "none");
            var id = $(this).attr("id");
            var wrapper_id = id + "_wrapper";
            var filter_input_id = id + "_filter_input";
            var filter_button_id = id + "_filter_button";
            var filter_select_id = id + "_filter_select";

            $select.wrap($.tmpl(templates.wrapper, {
                "id" : wrapper_id
            }));

            $select.css("border-top", "1px solid gray");

            var $wrapper = $("#" + wrapper_id);

            if (settings.filter !== undefined) {
                $wrapper.prepend($.tmpl(templates.filter_select, {
                    "id" : filter_select_id
                }));

                if (settings.filter.label !== undefined) {
                    $wrapper.prepend("<div style='width: 100%; padding-left: 4px;'>Filter by " + settings.filter.label
                            + ":</div>");
                } else {
                    $wrapper.prepend("<div style='width: 100%; padding-left: 4px;'>Filter by:</div>");
                }

                var $filter_select = $("#" + filter_select_id);

                $.getJSON(settings.filter.source, function(json)
                {
                    $filter_select.empty();
                    $filter_select.append("<option>All</option>");

                    $.each(json[settings.filter.iterator], function(i, item)
                    {
                        var option = $(settings.filter.handler(item));
                        $filter_select.append(option);
                    });
                });

                $filter_select.bind("change", {
                    "id" : id
                }, function(event)
                {
                    var $option = $(this).find(":selected");
                    var key = $option.data("key");
                    var value = $option.data("value");

                    key = !!key ? key : "";
                    value = !!value ? value : "";

                    var settings = $("#" + event.data.id).data("settings");

                    dhisAjaxSelect_filter_on_kv($("#" + event.data.id), key, value);
                });
            }

            var $filter_table = $("<table/>");

            $filter_table.css({
                "padding" : "1px",
                "width" : "100%"
            });

            var $filter_tr = $("<tr/>");

            var $filter_td1 = $("<td/>")
            var $filter_td2 = $("<td/>")

            $filter_td2.css("width", "70px");

            $filter_td1.append($.tmpl(templates.filter_input, {
                "id" : filter_input_id
            }))
            $filter_td2.append($.tmpl(templates.button, {
                "id" : filter_button_id,
                "text" : "filter"
            }));

            $filter_tr.append($filter_td1);
            $filter_tr.append($filter_td2);

            $filter_table.append($filter_tr);

            $wrapper.prepend($filter_table);

            var $filter_input = $("#" + filter_input_id);
            var $filter_button = $("#" + filter_button_id);

            settings.params = params;
            $select.data("settings", settings);
            methods.load("" + id);

            $filter_button.click(function()
            {
                key = $filter_input.val();
                dhisAjaxSelect_filter($select, key);
            });

            $filter_input.keypress(function(e)
            {
                if (e.keyCode == 13) {
                    $filter_button.click();
                    e.preventDefault();
                }
            });

            if (settings.connectedTo) {
                $select.dblclick(dhisAjaxSelect_availableList_dblclick($select.attr("id"), settings.connectedTo));
                $('#' + settings.connectedTo).dblclick(
                        dhisAjaxSelect_selectedList_dblclick(settings.connectedTo, $select.attr('id')));
            }
        }
    }

    $.fn.dhisAjaxSelect = function(method)
    {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error('Method ' + method + ' does not exist on jQuery.dhisAjaxSelect');
        }
    };
})(jQuery, undefined);
