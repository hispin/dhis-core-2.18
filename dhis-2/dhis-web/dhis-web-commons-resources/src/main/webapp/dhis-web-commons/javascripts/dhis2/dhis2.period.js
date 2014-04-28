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

dhis2.util.namespace('dhis2.period');

dhis2.period.DEFAULT_DATE_FORMAT = "yyyy-mm-dd";

dhis2.period.PeriodGenerator = function( calendar ) {
  if( typeof calendar === 'undefined' ) {
    calendar = dhis2.period.calendar;
  }

  var periodTypes = {
    "Daily": dhis2.period.makeDailyPeriodGenerator(calendar),
    "Weekly": dhis2.period.makeWeeklyPeriodGenerator(calendar),
    "Monthly": dhis2.period.makeMonthlyPeriodGenerator(calendar),
    "BiMonthly": dhis2.period.makeBiMonthlyPeriodGenerator(calendar),
    "Quarterly": dhis2.period.makeQuarterlyPeriodGenerator(calendar),
    "SixMonthly": dhis2.period.makeSixMonthlyPeriodGenerator(calendar),
    "SixMonthlyApril": dhis2.period.makeSixMonthlyAprilPeriodGenerator(calendar),
    "Yearly": dhis2.period.makeYearlyPeriodGenerator(calendar),
    "FinancialOct": dhis2.period.makeMonthlyPeriodGenerator(calendar),
    "FinancialJuly": dhis2.period.makeMonthlyPeriodGenerator(calendar),
    "FinancialApril": dhis2.period.makeMonthlyPeriodGenerator(calendar)
  };

  this.getAll = function() {
    return periodTypes;
  };

  this.getCalendar = function() {
    return calendar;
  };

  this.get = function( generator ) {
    return periodTypes[generator];
  };

  this.daily = function( offset ) {
    return this.get('Daily').generatePeriods(offset);
  };

  this.weekly = function( offset ) {
    return this.get('Weekly').generatePeriods(offset);
  };

  this.monthly = function( offset ) {
    return this.get('Monthly').generatePeriods(offset);
  };

  this.biMonthly = function( offset ) {
    return this.get('BiMonthly').generatePeriods(offset);
  };

  this.quarterly = function( offset ) {
    return this.get('Quarterly').generatePeriods(offset);
  };

  this.sixMonthly = function( offset ) {
    return this.get('SixMonthly').generatePeriods(offset);
  };

  this.sixMonthlyApril = function( offset ) {
    return this.get('SixMonthlyApril').generatePeriods(offset);
  };

  this.financialOct = function( offset ) {
    return this.get('FinancialOct').generatePeriods(offset);
  };

  this.financialJuly = function( offset ) {
    return this.get('FinancialJuly').generatePeriods(offset);
  };

  this.financialApril = function( offset ) {
    return this.get('FinancialApril').generatePeriods(offset);
  };
};

dhis2.period.makeDailyPeriodGenerator = function( cal ) {
  var self = {};
  self.generatePeriods = function( offset ) {
    if( typeof offset === 'undefined' ) {
      offset = 0;
    }

    var year = cal.today().year() - offset;
    var periods = [];

    var startDate = cal.newDate(year, 1, 1);

    for( var day = 1; day <= cal.daysInYear(year); day++ ) {
      var period = {};
      period['startDate'] = startDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
      period['endDate'] = startDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
      period['name'] = startDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
      period['id'] = 'Daily_' + period['startDate'];
      period['iso'] = startDate.formatDate("yyyymmdd");

      periods.push(period);

      startDate.add(1, 'd');
    }

    return periods;
  };

  return self;
};

dhis2.period.makeWeeklyPeriodGenerator = function( cal ) {
  var self = {};
  self.generatePeriods = function( offset ) {
    if( typeof offset === 'undefined' ) {
      offset = 0;
    }

    var year = cal.today().year() - offset;

    var periods = [];

    var startDate = cal.newDate(year, 1, 1);
    startDate.add(-(startDate.dayOfWeek() - 1), 'd'); // rewind to start of week, might cross year boundary

    // no reliable way to figure out number of weeks in a year (can differ in different calendars)
    // goes up to 200, but break when week is back to 1
    for( var week = 1; week < 200; week++ ) {
      var period = {};
      period['startDate'] = startDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);

      // not very elegant, but seems to be best way to get week end, adds a week, then minus 1 day
      var endDate = cal.newDate(startDate).add(1, 'w').add(-1, 'd');

      period['endDate'] = endDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
      period['name'] = 'W' + week + ' - ' + period['startDate'] + ' - ' + period['endDate'];
      period['id'] = 'Weekly_' + period['startDate'];
      period['iso'] = year + 'W' + week;

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

dhis2.period.makeMonthlyPeriodGenerator = function( cal ) {
  var self = {};
  self.generatePeriods = function( offset ) {
    if( typeof offset === 'undefined' ) {
      offset = 0;
    }

    var year = cal.today().year() - offset;

    var periods = [];

    for( var month = 1; month <= cal.monthsInYear(year); month++ ) {
      var startDate = cal.newDate(year, month, 1);
      var endDate = cal.newDate(startDate).set(startDate.daysInMonth(month), 'd');

      var period = {};
      period['startDate'] = startDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
      period['endDate'] = endDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
      period['name'] = startDate.formatDate("MM yyyy");
      period['id'] = 'Monthly_' + period['startDate'];
      period['iso'] = startDate.formatDate("yyyymm");

      periods.push(period);
    }

    return periods;
  };

  return self;
};

dhis2.period.makeBiMonthlyPeriodGenerator = function( cal ) {
  var self = {};
  self.generatePeriods = function( offset ) {
    if( typeof offset === 'undefined' ) {
      offset = 0;
    }

    var year = cal.today().year() - offset;
    var periods = [];

    for( var month = 1; month <= cal.monthsInYear(year); month += 2 ) {
      var startDate = cal.newDate(year, month, 1);
      var endDate = cal.newDate(startDate).set(month + 1, 'm');
      endDate.set(endDate.daysInMonth(month + 1), 'd');

      var period = {};
      period['startDate'] = startDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
      period['endDate'] = endDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
      period['name'] = startDate.formatDate("MM") + ' - ' + endDate.formatDate('MM') + ' ' + year;
      period['id'] = 'BiMonthly_' + period['startDate'];
      period['iso'] = startDate.formatDate("yyyymm") + 'B';

      periods.push(period);
    }

    return periods;
  };

  return self;
};

dhis2.period.makeQuarterlyPeriodGenerator = function( cal ) {
  var self = {};
  self.generatePeriods = function( offset ) {
    if( typeof offset === 'undefined' ) {
      offset = 0;
    }

    var year = cal.today().year() - offset;
    var periods = [];

    for( var month = 1, idx = 1; month <= cal.monthsInYear(year); month += 3, idx++ ) {
      var startDate = cal.newDate(year, month, 1);
      var endDate = cal.newDate(startDate).set(month + 2, 'm');
      endDate.set(endDate.daysInMonth(month + 2), 'd');

      var period = {};
      period['startDate'] = startDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
      period['endDate'] = endDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
      period['name'] = startDate.formatDate("MM") + ' - ' + endDate.formatDate('MM') + ' ' + year;
      period['id'] = 'Quarterly_' + period['startDate'];
      period['iso'] = startDate.formatDate("yyyy") + 'Q' + idx;

      periods.push(period);
    }

    return periods;
  };

  return self;
};

dhis2.period.makeSixMonthlyPeriodGenerator = function( cal ) {
  var self = {};
  self.generatePeriods = function( offset ) {
    if( typeof offset === 'undefined' ) {
      offset = 0;
    }

    var year = cal.today().year() - offset;

    var periods = [];

    var startDate = cal.newDate(year, 1, 1);
    var endDate = cal.newDate(startDate).set(6, 'm');
    endDate.set(endDate.daysInMonth(6), 'd');

    var period = {};
    period['startDate'] = startDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
    period['endDate'] = endDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
    period['name'] = startDate.formatDate("MM") + ' - ' + endDate.formatDate('MM') + ' ' + year;
    period['id'] = 'SixMonthly_' + period['startDate'];
    period['iso'] = startDate.formatDate("yyyy") + 'S1';

    periods.push(period);

    startDate = cal.newDate(year, 7, 1);
    endDate = cal.newDate(startDate).set(cal.monthsInYear(year), 'm');
    endDate.set(endDate.daysInMonth(12), 'd');

    period = {};
    period['startDate'] = startDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
    period['endDate'] = endDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
    period['name'] = startDate.formatDate("MM") + ' - ' + endDate.formatDate('MM') + ' ' + year;
    period['id'] = 'SixMonthly_' + period['startDate'];
    period['iso'] = startDate.formatDate("yyyy") + 'S2';

    periods.push(period);

    return periods;
  };

  return self;
};

dhis2.period.makeSixMonthlyAprilPeriodGenerator = function( cal ) {
  var self = {};
  self.generatePeriods = function( offset ) {
    if( typeof offset === 'undefined' ) {
      offset = 0;
    }

    var year = cal.today().year() - offset;
    var periods = [];

    var startDate = cal.newDate(year, 4, 1);
    var endDate = cal.newDate(startDate).set(9, 'm');
    endDate.set(endDate.daysInMonth(9), 'd');

    var period = {};
    period['startDate'] = startDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
    period['endDate'] = endDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
    period['name'] = startDate.formatDate("MM") + ' - ' + endDate.formatDate('MM') + ' ' + year;
    period['id'] = 'SixMonthlyApril_' + period['startDate'];
    period['iso'] = startDate.formatDate("yyyy") + 'AprilS1';

    periods.push(period);

    startDate = cal.newDate(year, 10, 1);
    endDate = cal.newDate(startDate).set(startDate.year() + 1, 'y').set(2, 'm');
    endDate.set(endDate.daysInMonth(endDate.month()), 'd');

    period = {};
    period['startDate'] = startDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
    period['endDate'] = endDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
    period['name'] = startDate.formatDate("MM yyyy") + ' - ' + endDate.formatDate('MM yyyy');
    period['id'] = 'SixMonthlyApril_' + period['startDate'];
    period['iso'] = startDate.formatDate("yyyy") + 'AprilS2';

    periods.push(period);

    return periods;
  };

  return self;
};

dhis2.period.makeYearlyPeriodGenerator = function( cal ) {
  var self = {};
  self.generatePeriods = function( offset ) {
    if( typeof offset === 'undefined' ) {
      offset = 0;
    }

    var year = cal.today().year() - offset;
    var periods = [];

    // generate 11 years, thisYear +/- 5 years
    for( var i = -5; i < 6; i++ ) {
      var startDate = cal.newDate(year + i, 1, 1);
      var endDate = cal.newDate(startDate).set(cal.monthsInYear(year + i), 'm');
      endDate.set(endDate.daysInMonth(endDate.month()), 'd');

      var period = {};
      period['startDate'] = startDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
      period['endDate'] = endDate.formatDate(dhis2.period.DEFAULT_DATE_FORMAT);
      period['name'] = startDate.formatDate("yyyy");
      period['id'] = 'Yearly_' + period['startDate'];
      period['iso'] = startDate.formatDate("yyyy");

      periods.push(period);
    }

    return periods;
  };

  return self;
};
