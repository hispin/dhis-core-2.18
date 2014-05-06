"use strict";

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */

dhis2.util.namespace('dhis2.period');

dhis2.period.DEFAULT_DATE_FORMAT = "yyyy-mm-dd";

/**
 * A date picker class that allows for creating both simple date pickers, and ranged date pickers.
 *
 * There is probably no reason to use this directly, since on startup, a global variable have been made available:
 *  - dhis2.period.picker   DatePicker object created with system calendar and system date format
 *
 * @param {$.calendars.baseCalendar} calendar Calendar to use, this must come from $.calendars.instance(chronology).
 * @param {String} format Date format to use for formatting, will default to ISO 8601
 * @constructor
 * @see <a href="http://keith-wood.name/datepick.html">http://keith-wood.name/datepick.html</a>
 */
dhis2.period.DatePicker = function( calendar, format ) {
  if( typeof calendar === 'undefined' ) {
    if( typeof dhis2.period.calendar !== 'undefined' ) {
      calendar = dhis2.period.calendar;
    } else {
      throw new Error('calendar parameter is required');
    }
  }

  if( typeof format === 'undefined' ) {
    format = dhis2.period.DEFAULT_DATE_FORMAT;
  }

  $.extend(this, {
    calendar: calendar,
    format: format,
    defaults: {
      calendar: calendar,
      dateFormat: format,
      showAnim: '',
      maxDate: calendar.today(),
      yearRange: 'c-100:c+100'
    }
  });
};

/**
 * Creates a date picker.
 *
 * @param el Element to select on, can be any kind of jQuery selector, or a jqEl
 * @param fromIso Convert field from ISO 8601 to local calendar
 * @param options Additional options, will be merged with the defaults
 */
dhis2.period.DatePicker.prototype.createInstance = function( el, fromIso, options ) {
  var $el = $(el);

  if( fromIso ) {
    var iso8601 = $.calendars.instance('gregorian');
    var isoDate = iso8601.parseDate(this.format, $el.val());
    var cDateIsoDate = this.calendar.fromJD(isoDate.toJD());
    $el.val(this.calendar.formatDate(this.format, cDateIsoDate));
  }

  $el.calendarsPicker($.extend({}, this.defaults, options));
};

/**
 * Creates a ranged date picker, keeping two fields in sync.
 *
 * @param {*} fromEl From element to select on, can be any kind of jQuery selector, or a jqEl
 * @param {*} toEl To element to select on, can be any kind of jQuery selector, or a jqEl
 * @param {Boolean} fromIso Convert fields from ISO 8601 to local calendar
 * @param {Object} options Additional options, will be merged with the defaults
 */
dhis2.period.DatePicker.prototype.createRangedInstance = function( fromEl, toEl, fromIso, options ) {
  var mergedOptions = $.extend({}, this.defaults, options || {});

  var $fromEl = $(fromEl);
  var $toEl = $(toEl);

  if( fromIso ) {
    var iso8601 = $.calendars.instance('gregorian');
    var from = iso8601.parseDate(this.format, $fromEl.val());
    var to = iso8601.parseDate(this.format, $toEl.val());

    var cDateFrom = this.calendar.fromJD(from.toJD());
    var cDateTo = this.calendar.fromJD(to.toJD());

    $fromEl.val(this.calendar.formatDate(this.format, cDateFrom));
    $toEl.val(this.calendar.formatDate(this.format, cDateTo));
  }

  mergedOptions.onSelect = function( dates ) {
    if( this.id === $fromEl.attr('id') ) {
      $toEl.calendarsPicker("option", "minDate", dates[0] || null);
    }
    else if( this.id === $toEl.attr('id') ) {
      $fromEl.calendarsPicker("option", "maxDate", dates[0] || null);
    }
  };

  $fromEl.calendarsPicker(mergedOptions);

  $toEl.calendarsPicker($.extend({}, mergedOptions, {
    maxDate: null
  }));

  $fromEl.calendarsPicker("setDate", $fromEl.calendarsPicker("getDate"));
  $toEl.calendarsPicker("setDate", $toEl.calendarsPicker("getDate"));
};

/**
 * A period generator that uses a specified calendar chronology to generate DHIS 2 periods.
 *
 * There is probably no reason to use this directly, since on startup, two global variables have been made available:
 *  - dhis2.period.calendar   The currently selected system calendar
 *  - dhis2.period.generator  An instance of this class using the system calendar
 *
 * @param {$.calendars.baseCalendar} calendar Calendar to use, this must come from $.calendars.instance(chronology).
 * @param {String} format Date format to use for formatting, will default to ISO 8601
 * @constructor
 */
dhis2.period.PeriodGenerator = function( calendar, format ) {
  calendar = calendar || dhis2.period.calendar;
  format = format || dhis2.period.DEFAULT_DATE_FORMAT;

  if( typeof calendar === 'undefined' ) {
    throw new Error('calendar parameter was not provided or is undefined, and no global dhis2.period.calendar instance exists.')
  }

  $.extend(this, {
    calendar: calendar,
    format: format,
    generators: {
      "Daily": dhis2.period.makeDailyPeriodGenerator(calendar, format),
      "Weekly": dhis2.period.makeWeeklyPeriodGenerator(calendar, format),
      "Monthly": dhis2.period.makeMonthlyPeriodGenerator(calendar, format),
      "BiMonthly": dhis2.period.makeBiMonthlyPeriodGenerator(calendar, format),
      "Quarterly": dhis2.period.makeQuarterlyPeriodGenerator(calendar, format),
      "SixMonthly": dhis2.period.makeSixMonthlyPeriodGenerator(calendar, format),
      "SixMonthlyApril": dhis2.period.makeSixMonthlyAprilPeriodGenerator(calendar, format),
      "Yearly": dhis2.period.makeYearlyPeriodGenerator(calendar, format),
      "FinancialApril": dhis2.period.makeFinancialAprilPeriodGenerator(calendar, format),
      "FinancialJuly": dhis2.period.makeFinancialJulyPeriodGenerator(calendar, format),
      "FinancialOct": dhis2.period.makeFinancialOctoberPeriodGenerator(calendar, format)
    }
  });
};

/**
 * @returns {Array} All available period generators
 */
dhis2.period.PeriodGenerator.prototype.getAll = function() {
  return this.generators;
};

/**
 * @returns {Object} The calendar chronology used for this period generator
 */
dhis2.period.PeriodGenerator.prototype.getCalendar = function() {
  return this.calendar;
};

/**
 * @returns {Object} The date format used for this period generator
 */
dhis2.period.PeriodGenerator.prototype.getDateFormat = function() {
  return this.format;
};

/**
 * @param {String} generator Generator to find
 * @returns {*} Wanted generator if it exists
 */
dhis2.period.PeriodGenerator.prototype.get = function( generator ) {
  return this.generators[generator];
};

/**
 * @param {String} generator Generator to use (String)
 * @param {int} offset Offset for generatePeriods
 * @returns {Array} Generated periods as array
 */
dhis2.period.PeriodGenerator.prototype.generatePeriods = function( generator, offset ) {
  return this.generators[generator].generatePeriods(offset);
};

/**
 * @param {String} generator Generator to use (String)
 * @param {int} offset Offset for generatePeriods
 * @returns {Array} Generated periods as array
 */
dhis2.period.PeriodGenerator.prototype.generateReversedPeriods = function( generator, offset ) {
  return this.reverse(this.generators[generator].generatePeriods(offset));
};

/**
 * Convenience method to get Daily generator
 */
dhis2.period.PeriodGenerator.prototype.daily = function( offset ) {
  return this.get('Daily').generatePeriods(offset);
};

/**
 * Convenience method to get Weekly generator
 */
dhis2.period.PeriodGenerator.prototype.weekly = function( offset ) {
  return this.get('Weekly').generatePeriods(offset);
};

/**
 * Convenience method to get Monthly generator
 */
dhis2.period.PeriodGenerator.prototype.monthly = function( offset ) {
  return this.get('Monthly').generatePeriods(offset);
};

/**
 * Convenience method to get BiMonthly generator
 */
dhis2.period.PeriodGenerator.prototype.biMonthly = function( offset ) {
  return this.get('BiMonthly').generatePeriods(offset);
};

/**
 * Convenience method to get Quarterly generator
 */
dhis2.period.PeriodGenerator.prototype.quarterly = function( offset ) {
  return this.get('Quarterly').generatePeriods(offset);
};

/**
 * Convenience method to get SixMonthly generator
 */
dhis2.period.PeriodGenerator.prototype.sixMonthly = function( offset ) {
  return this.get('SixMonthly').generatePeriods(offset);
};

/**
 * Convenience method to get SixMonthlyApril generator
 */
dhis2.period.PeriodGenerator.prototype.sixMonthlyApril = function( offset ) {
  return this.get('SixMonthlyApril').generatePeriods(offset);
};

/**
 * Convenience method to get Yearly generator
 */
dhis2.period.PeriodGenerator.prototype.yearly = function( offset ) {
  return this.get('Yearly').generatePeriods(offset);
};

/**
 * Convenience method to get FinancialOct generator
 */
dhis2.period.PeriodGenerator.prototype.financialOct = function( offset ) {
  return this.get('FinancialOct').generatePeriods(offset);
};

/**
 * Convenience method to get FinancialJuly generator
 */
dhis2.period.PeriodGenerator.prototype.financialJuly = function( offset ) {
  return this.get('FinancialJuly').generatePeriods(offset);
};

/**
 * Convenience method to get FinancialApril generator
 */
dhis2.period.PeriodGenerator.prototype.financialApril = function( offset ) {
  return this.get('FinancialApril').generatePeriods(offset);
};

/**
 * Does out-of-place reversal of a list of periods
 * @param {Array} periods Periods to reverse
 * @returns {Array} Reversed array
 */
dhis2.period.PeriodGenerator.prototype.reverse = function( periods ) {
  return periods.slice(0).reverse();
};

/**
 * Out-of-place filtering of current + future periods
 * @param {Array} periods Periods to filter
 * @return {Array} Filtered periods array
 */
dhis2.period.PeriodGenerator.prototype.filterFuturePeriods = function( periods ) {
  var array = [];
  var today = this.calendar.today();

  $.each(periods, function() {
    if( this['_endDate'].compareTo(today) <= 0 ) {
      array.push(this);
    }
  });

  return array;
};

/**
 * Out-of-place filtering of future periods
 * @param {Array} periods Periods to filter
 * @return {Array} Filtered periods array
 */
dhis2.period.PeriodGenerator.prototype.filterFuturePeriodsExceptCurrent = function( periods ) {
  var array = [];
  var today = this.calendar.today();

  $.each(periods, function() {
    if( this['_startDate'].compareTo(today) <= 0 ) {
      array.push(this);
    }
  });

  return array;
};

dhis2.period.makeDailyPeriodGenerator = function( calendar, format ) {
  var self = {};
  self.generatePeriods = function( offset ) {
    offset = offset || 0;

    var year = offset + calendar.today().year();
    var periods = [];

    var startDate = calendar.newDate(year, 1, 1);

    for( var day = 1; day <= calendar.daysInYear(year); day++ ) {
      var period = {};
      period['startDate'] = startDate.formatDate(format);
      period['endDate'] = startDate.formatDate(format);
      period['name'] = startDate.formatDate(format);
      period['id'] = 'Daily_' + period['startDate'];
      period['iso'] = startDate.formatDate("yyyymmdd");

      period['_startDate'] = startDate;
      period['_endDate'] = startDate;

      periods.push(period);

      startDate.add(1, 'd');
    }

    return periods;
  };

  return self;
};

dhis2.period.makeWeeklyPeriodGenerator = function( calendar, format ) {
  var self = {};
  self.generatePeriods = function( offset ) {
    offset = offset || 0;

    var year = offset + calendar.today().year();
    var periods = [];

    var startDate = calendar.newDate(year, 1, 1);
    startDate.add(-(startDate.dayOfWeek() - 1), 'd'); // rewind to start of week, might cross year boundary

    // no reliable way to figure out number of weeks in a year (can differ in different calendars)
    // goes up to 200, but break when week is back to 1
    for( var week = 1; week < 200; week++ ) {
      var period = {};
      period['startDate'] = startDate.formatDate(format);

      // not very elegant, but seems to be best way to get week end, adds a week, then minus 1 day
      var endDate = calendar.newDate(startDate).add(1, 'w').add(-1, 'd');

      period['endDate'] = endDate.formatDate(format);
      period['name'] = 'W' + week + ' - ' + period['startDate'] + ' - ' + period['endDate'];
      period['id'] = 'Weekly_' + period['startDate'];
      period['iso'] = year + 'W' + week;

      period['_startDate'] = startDate;
      period['_endDate'] = endDate;

      periods.push(period);

      startDate.add(1, 'w');

      if( startDate.weekOfYear() == 1 ) {
        break;
      }
    }

    return periods;
  };

  return self;
};

dhis2.period.makeMonthlyPeriodGenerator = function( calendar, format ) {
  var self = {};
  self.generatePeriods = function( offset ) {
    offset = offset || 0;

    var year = offset + calendar.today().year();
    var periods = [];

    for( var month = 1; month <= calendar.monthsInYear(year); month++ ) {
      var startDate = calendar.newDate(year, month, 1);
      var endDate = calendar.newDate(startDate).set(startDate.daysInMonth(month), 'd');

      var period = {};
      period['startDate'] = startDate.formatDate(format);
      period['endDate'] = endDate.formatDate(format);
      period['name'] = startDate.formatDate("MM yyyy");
      period['id'] = 'Monthly_' + period['startDate'];
      period['iso'] = startDate.formatDate("yyyymm");

      period['_startDate'] = startDate;
      period['_endDate'] = endDate;

      periods.push(period);
    }

    return periods;
  };

  return self;
};

dhis2.period.makeBiMonthlyPeriodGenerator = function( calendar, format ) {
  var self = {};
  self.generatePeriods = function( offset ) {
    offset = offset || 0;

    var year = offset + calendar.today().year();
    var periods = [];

    for( var month = 1; month <= calendar.monthsInYear(year); month += 2 ) {
      var startDate = calendar.newDate(year, month, 1);
      var endDate = calendar.newDate(startDate).set(month + 1, 'm');
      endDate.set(endDate.daysInMonth(month + 1), 'd');

      var period = {};
      period['startDate'] = startDate.formatDate(format);
      period['endDate'] = endDate.formatDate(format);
      period['name'] = startDate.formatDate("MM") + ' - ' + endDate.formatDate('MM') + ' ' + year;
      period['id'] = 'BiMonthly_' + period['startDate'];
      period['iso'] = startDate.formatDate("yyyymm") + 'B';

      period['_startDate'] = startDate;
      period['_endDate'] = endDate;

      periods.push(period);
    }

    return periods;
  };

  return self;
};

dhis2.period.makeQuarterlyPeriodGenerator = function( calendar, format ) {
  var self = {};
  self.generatePeriods = function( offset ) {
    offset = offset || 0;

    var year = offset + calendar.today().year();
    var periods = [];

    for( var month = 1, idx = 1; month <= calendar.monthsInYear(year); month += 3, idx++ ) {
      var startDate = calendar.newDate(year, month, 1);
      var endDate = calendar.newDate(startDate).set(month + 2, 'm');
      endDate.set(endDate.daysInMonth(month + 2), 'd');

      var period = {};
      period['startDate'] = startDate.formatDate(format);
      period['endDate'] = endDate.formatDate(format);
      period['name'] = startDate.formatDate("MM") + ' - ' + endDate.formatDate('MM') + ' ' + year;
      period['id'] = 'Quarterly_' + period['startDate'];
      period['iso'] = startDate.formatDate("yyyy") + 'Q' + idx;

      period['_startDate'] = startDate;
      period['_endDate'] = endDate;

      periods.push(period);
    }

    return periods;
  };

  return self;
};

dhis2.period.makeSixMonthlyPeriodGenerator = function( calendar, format ) {
  var self = {};
  self.generatePeriods = function( offset ) {
    offset = offset || 0;

    var year = offset + calendar.today().year();
    var periods = [];

    var startDate = calendar.newDate(year, 1, 1);
    var endDate = calendar.newDate(startDate).set(6, 'm');
    endDate.set(endDate.daysInMonth(6), 'd');

    var period = {};
    period['startDate'] = startDate.formatDate(format);
    period['endDate'] = endDate.formatDate(format);
    period['name'] = startDate.formatDate("MM") + ' - ' + endDate.formatDate('MM') + ' ' + year;
    period['id'] = 'SixMonthly_' + period['startDate'];
    period['iso'] = startDate.formatDate("yyyy") + 'S1';

    period['_startDate'] = startDate;
    period['_endDate'] = endDate;

    periods.push(period);

    startDate = calendar.newDate(year, 7, 1);
    endDate = calendar.newDate(startDate).set(calendar.monthsInYear(year), 'm');
    endDate.set(endDate.daysInMonth(12), 'd');

    period = {};
    period['startDate'] = startDate.formatDate(format);
    period['endDate'] = endDate.formatDate(format);
    period['name'] = startDate.formatDate("MM") + ' - ' + endDate.formatDate('MM') + ' ' + year;
    period['id'] = 'SixMonthly_' + period['startDate'];
    period['iso'] = startDate.formatDate("yyyy") + 'S2';

    period['_startDate'] = startDate;
    period['_endDate'] = endDate;

    periods.push(period);

    return periods;
  };

  return self;
};

dhis2.period.makeSixMonthlyAprilPeriodGenerator = function( calendar, format ) {
  var self = {};
  self.generatePeriods = function( offset ) {
    offset = offset || 0;

    var year = offset + calendar.today().year();
    var periods = [];

    var startDate = calendar.newDate(year, 4, 1);
    var endDate = calendar.newDate(startDate).set(9, 'm');
    endDate.set(endDate.daysInMonth(9), 'd');

    var period = {};
    period['startDate'] = startDate.formatDate(format);
    period['endDate'] = endDate.formatDate(format);
    period['name'] = startDate.formatDate("MM") + ' - ' + endDate.formatDate('MM') + ' ' + year;
    period['id'] = 'SixMonthlyApril_' + period['startDate'];
    period['iso'] = startDate.formatDate("yyyy") + 'AprilS1';

    period['_startDate'] = startDate;
    period['_endDate'] = endDate;

    periods.push(period);

    startDate = calendar.newDate(year, 10, 1);
    endDate = calendar.newDate(startDate).set(startDate.year() + 1, 'y').set(2, 'm');
    endDate.set(endDate.daysInMonth(endDate.month()), 'd');

    period = {};
    period['startDate'] = startDate.formatDate(format);
    period['endDate'] = endDate.formatDate(format);
    period['name'] = startDate.formatDate("MM yyyy") + ' - ' + endDate.formatDate('MM yyyy');
    period['id'] = 'SixMonthlyApril_' + period['startDate'];
    period['iso'] = startDate.formatDate("yyyy") + 'AprilS2';

    period['_startDate'] = startDate;
    period['_endDate'] = endDate;

    periods.push(period);

    return periods;
  };

  return self;
};

dhis2.period.makeYearlyPeriodGenerator = function( calendar, format ) {
  var self = {};
  self.generatePeriods = function( offset ) {
    offset = offset || 0;

    var year = offset + calendar.today().year();
    var periods = [];

    // generate 11 years, thisYear +/- 5 years
    for( var i = -5; i < 6; i++ ) {
      var startDate = calendar.newDate(year + i, 1, 1);
      var endDate = calendar.newDate(startDate).set(calendar.monthsInYear(year + i), 'm');
      endDate.set(endDate.daysInMonth(endDate.month()), 'd');

      var period = {};
      period['startDate'] = startDate.formatDate(format);
      period['endDate'] = endDate.formatDate(format);
      period['name'] = startDate.formatDate("yyyy");
      period['id'] = 'Yearly_' + period['startDate'];
      period['iso'] = startDate.formatDate("yyyy");

      period['_startDate'] = startDate;
      period['_endDate'] = endDate;

      periods.push(period);
    }

    return periods;
  };

  return self;
};

dhis2.period.makeFinancialAprilPeriodGenerator = function( calendar, format ) {
  return dhis2.period.makeYearlyPeriodGeneratorWithMonthOffset(calendar, 4, 'April', format);
};

dhis2.period.makeFinancialJulyPeriodGenerator = function( calendar, format ) {
  return dhis2.period.makeYearlyPeriodGeneratorWithMonthOffset(calendar, 7, 'July', format);
};

dhis2.period.makeFinancialOctoberPeriodGenerator = function( calendar, format ) {
  return dhis2.period.makeYearlyPeriodGeneratorWithMonthOffset(calendar, 10, 'Oct', format);
};

dhis2.period.makeYearlyPeriodGeneratorWithMonthOffset = function( calendar, monthStart, monthShortName, format ) {
  var self = {};
  self.generatePeriods = function( offset ) {
    offset = offset || 0;

    var year = offset + calendar.today().year();
    var periods = [];

    var startDate = calendar.newDate(year - 5, monthStart, 1);

    // generate 11 years, thisYear +/- 5 years
    for( var i = 1; i < 12; i++ ) {
      var endDate = calendar.newDate(startDate).add(1, 'y').add(-1, 'd');

      var period = {};
      period['startDate'] = startDate.formatDate(format);
      period['endDate'] = endDate.formatDate(format);
      period['name'] = startDate.formatDate("MM yyyy") + ' - ' + endDate.formatDate("MM yyyy");
      period['id'] = 'Financial' + monthShortName + '_' + period['startDate'];
      period['iso'] = startDate.formatDate("yyyy") + monthShortName;

      period['_startDate'] = startDate;
      period['_endDate'] = endDate;

      periods.push(period);
      startDate.add(1, 'y');
    }

    return periods;
  };

  return self;
};
