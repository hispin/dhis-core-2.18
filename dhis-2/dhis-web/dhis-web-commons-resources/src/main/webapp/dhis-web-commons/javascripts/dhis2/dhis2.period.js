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

dhis2.period.DATE_FORMAT = "yyyy-mm-dd";

dhis2.period.generateMonthlyPeriods = function( cal, offset ) {
  var year = cal.today().year() - offset;

  var periods = [];

  for( var month = 1; month <= cal.monthsInYear(year); month++ ) {
    var startDate = cal.newDate(year, month, 1);
    var endDate = cal.newDate(startDate).set(startDate.daysInMonth(month), 'd');

    if( startDate.year() != endDate.year() ) {
      break;
    }

    var period = {};
    period['startDate'] = startDate.formatDate(dhis2.period.DATE_FORMAT);
    period['endDate'] = endDate.formatDate(dhis2.period.DATE_FORMAT);
    period['name'] = startDate.formatDate("MM yyyy");
    period['id'] = 'Monthly_' + period['startDate'];
    period['iso'] = startDate.formatDate("yyyymm");

    periods.push(period);
  }

  return periods;
};

dhis2.period.generateBiMonthlyPeriods = function( cal, offset ) {
  var year = cal.today().year() - offset;

  var periods = [];

  for( var month = 1; month <= cal.monthsInYear(year); month += 2 ) {
    var startDate = cal.newDate(year, month, 1);
    var endDate = cal.newDate(startDate).set(month + 1, 'm');
    endDate.set(endDate.daysInMonth(month + 1), 'd');

    if( startDate.year() != endDate.year() ) {
      break;
    }

    var period = {};
    period['startDate'] = startDate.formatDate(dhis2.period.DATE_FORMAT);
    period['endDate'] = endDate.formatDate(dhis2.period.DATE_FORMAT);
    period['name'] = startDate.formatDate("MM") + '-' + endDate.formatDate('MM') + ' ' + year;
    period['id'] = 'BiMonthly_' + period['startDate'];
    period['iso'] = startDate.formatDate("yyyymm") + 'B';

    periods.push(period);
  }

  return periods;
};

dhis2.period.generateQuarterlyPeriods = function( cal, offset ) {
  var year = cal.today().year() - offset;

  var periods = [];

  for( var month = 1, idx = 1; month <= cal.monthsInYear(year); month += 3, idx++ ) {
    var startDate = cal.newDate(year, month, 1);
    var endDate = cal.newDate(startDate).set(month + 2, 'm');
    endDate.set(endDate.daysInMonth(month + 2), 'd');

    if( startDate.year() != endDate.year() ) {
      break;
    }

    var period = {};
    period['startDate'] = startDate.formatDate(dhis2.period.DATE_FORMAT);
    period['endDate'] = endDate.formatDate(dhis2.period.DATE_FORMAT);
    period['name'] = startDate.formatDate("MM") + '-' + endDate.formatDate('MM') + ' ' + year;
    period['id'] = 'Quarterly_' + period['startDate'];
    period['iso'] = startDate.formatDate("yyyy") + 'Q' + idx;

    periods.push(period);
  }

  return periods;
};

dhis2.period.generateSixMonthlyPeriods = function( cal, offset ) {
  var year = cal.today().year() - offset;

  var periods = [];

  var startDate = cal.newDate(year, 1, 1);
  var endDate = cal.newDate(startDate).set(6, 'm');
  endDate.set(endDate.daysInMonth(6), 'd');

  var period = {};
  period['startDate'] = startDate.formatDate(dhis2.period.DATE_FORMAT);
  period['endDate'] = endDate.formatDate(dhis2.period.DATE_FORMAT);
  period['name'] = startDate.formatDate("MM") + '-' + endDate.formatDate('MM') + ' ' + year;
  period['id'] = 'SixMonthly_' + period['startDate'];
  period['iso'] = startDate.formatDate("yyyy") + 'S' + 1;

  periods.push(period);

  startDate = cal.newDate(year, 7, 1);
  endDate = cal.newDate(startDate).set(cal.monthsInYear(year), 'm');
  endDate.set(endDate.daysInMonth(12), 'd');

  period = {};
  period['startDate'] = startDate.formatDate(dhis2.period.DATE_FORMAT);
  period['endDate'] = endDate.formatDate(dhis2.period.DATE_FORMAT);
  period['name'] = startDate.formatDate("MM") + '-' + endDate.formatDate('MM') + ' ' + year;
  period['id'] = 'SixMonthly_' + period['startDate'];
  period['iso'] = startDate.formatDate("yyyy") + 'S' + 2;

  periods.push(period);

  return periods;
};
