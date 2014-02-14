(function ($) {

    function init(plot) {
        plot.hooks.drawSeries.push(drawSeries);
        plot.hooks.shutdown.push(shutdown);
        if (plot.hooks.processOffset) {         // skip if we're using 0.7 - just add the labelClass explicitly.
            plot.hooks.processOffset.push(processOffset);
        }
    }

    function processOffset(plot, offset) {
        // Check to see if each series has a labelClass defined. If not, add a default one.
        // processOptions gets called before the data is loaded, so we can't do this there.
        var series = plot.getData();
        for (var i = 0; i < series.length; i++) {
            if (!series[i].canvasRender && series[i].showLabels && !series[i].labelClass) {
                series[i].labelClass = "seriesLabel" + (i + 1);
            }
        }
    }

    function drawSeries(plot, ctx, series) {
        if (!series.showLabels || !(series.labelClass || series.canvasRender) || !series.labels || series.labels.length == 0) {
            return;
        }
        ctx.save();
        if (series.canvasRender) {
            ctx.fillStyle = series.cColor;
            ctx.font = series.cFont;
        }

        for (i = 0; i < series.data.length; i++) {
            if (series.labels[i]) {
                var loc = plot.pointOffset({ x: series.data[i][0], y: series.data[i][1] });
                var offset = plot.getPlotOffset();
                //if (loc.left > 0 && loc.left < plot.width() && loc.top > 0 && loc.top < plot.height()) //for some reason my label couldn't work, hence turned this off
                    drawLabel(series.labels[i], loc.left, loc.top);
            }
        }
        ctx.restore();

        function drawLabel(contents, x, y) {
            var radius = series.points.radius;
            if (!series.canvasRender) {
                var elem = $('<div class="' + series.labelClass + '">' + contents + '</div>').css({ position: 'absolute' }).appendTo(plot.getPlaceholder());
                switch (series.labelPlacement) {
                    case "above":
                        elem.css({
                            top: y - (elem.height() + radius),
                            left: x - elem.width() / 2
                        });
                        break;
                    case "left":
                        elem.css({
                            top: y - elem.height() / 2,
                            left: x - (elem.width() + radius)
                        });
                        break;
                    case "right":
                        elem.css({
                            top: y - elem.height() / 2,
                            left: x + radius /*+ 15 */
                        });
                        break;
                    default:
                        elem.css({
                            top: y + radius/*+ 10*/,
                            left: x - elem.width() / 2
                        });
                }
            }
            else {
                //TODO: check boundaries
                var tWidth = ctx.measureText(contents).width;
                switch (series.labelPlacement) {
                    case "above":
                        x = x - tWidth / 2;
                        y -= (series.cPadding + radius);
                        ctx.textBaseline = "bottom";
                        break;
                    case "left":
                        x -= tWidth + series.cPadding + radius;
                        ctx.textBaseline = "middle";
                        break;
                    case "right":
                        x += series.cPadding + radius;
                        ctx.textBaseline = "middle";
                        break;
                    default:
                        ctx.textBaseline = "top";
                        y += series.cPadding + radius;
                        x = x - tWidth / 2;

                }
                ctx.fillText(contents, x, y);
            }
        }

    }

    function shutdown(plot, eventHolder) {
        var series = plot.getData();
        for (var i = 0; i < series.length; i++) {
            if (!series[i].canvasRender && series[i].labelClass) {
                $("." + series[i].labelClass).remove();
            }
        }
    }

    // labelPlacement options: below, above, left, right
    var options = {
        series: {
            showLabels: false,
            labels: [],
            labelClass: null,
            labelPlacement: "below",
            canvasRender: false,
            cColor: "#000",
            cFont: "9px, san-serif",
            cPadding: 4
        }
    };

    $.plot.plugins.push({
        init: init,
        options: options,
        name: "seriesLabels",
        version: "0.2"
    });
})(jQuery);
