// -----------------------------------------------------------------------------
// View chart
// -----------------------------------------------------------------------------

function viewChart( url, size )
{
    var width = size === 'wide' ? 1000 : 700;
    var height = size === 'tall' ? 800 : 500;

    $('#chartImage').attr('src', url);
    $('#chartView').dialog({
       autoOpen: true,
       modal: true,
       height: height + 35,
       width: width,
       resizable: false,
       title: 'Viewing Chart'
    });
}

// -----------------------------------------------------------------------------
// Remove chart
// -----------------------------------------------------------------------------

function removeChart( chartId, chartTitle )
{
    removeItem(chartId, chartTitle, i18n_confirm_delete, "removeChart.action");
}

// -----------------------------------------------------------------------------
// Show chart details
// -----------------------------------------------------------------------------

function showChartDetails( chartId )
{
    var request = new Request();
    request.setResponseTypeXML('chart');
    request.setCallbackSuccess(chartReceived);
    request.send('getChart.action?id=' + chartId);
}

function chartReceived( xmlObject )
{
    setInnerHTML('titleField', getElementValue(xmlObject, 'title'));
    setInnerHTML('dimensionField', getElementValue(xmlObject, 'dimension'));
    setInnerHTML('indicatorsField', getElementValue(xmlObject, 'indicators'));
    setInnerHTML('periodsField', getElementValue(xmlObject, 'periods'));
    setInnerHTML('organisationUnitsField', getElementValue(xmlObject, 'organisationUnits'));

    showDetails();
}

// -----------------------------------------------------------------------------
// Validate and save
// -----------------------------------------------------------------------------

function saveChart()
{
    if (validateTargetLine() && validateCollections())
    {
        var url = "validateChart.action?id=" + getFieldValue("id") + "&title=" + getFieldValue("title");

        var request = new Request();
        request.setResponseTypeXML('message');
        request.setCallbackSuccess(saveChartReceived);
        request.send(url);
    }
}

function saveChartReceived( messageElement )
{
    var type = messageElement.getAttribute('type');
    var message = messageElement.firstChild.nodeValue;
    var dimension = document.getElementById("dimension").value;

    if (type == "input")
    {
        setMessage(message);

        return false;
    } 
    else if (type == "success")
    {
        if ($("#selectedIndicators").attr('multiple') !== undefined)
        {
            $("#selectedIndicators").children().attr("selected", true);
        }

        if ($("#selectedDataElements").attr('multiple') !== undefined)
        {
            $("#selectedDataElements").children().attr("selected", true);
        }

        if ($("#selectedPeriods").attr('multiple') !== undefined)
        {
            $("#selectedPeriods").children().attr("selected", true);
        }

        if ($("#selectedOrganisationUnits").attr('multiple') !== undefined)
        {
            $("#selectedOrganisationUnits").children().attr("selected", true);
        }

        $('#chartForm').submit();
    }
}

function selectedChartType()
{
    return $("#type").val();
}

function selectedIndicatorsCount()
{
    return $("#selectedIndicators option").length;
}

function validateTargetLine()
{
    var targetLine = $("#targetLine").attr("checked");

    if (targetLine)
    {
        var targetLineValue = $("#targetLineValue").val();

        if (targetLineValue.length == 0)
        {
            setMessage(i18n_target_line_value_must_be_provided);

            return false;
        }

        if (isNaN(targetLineValue))
        {
            setMessage(i18n_target_line_value_must_be_number);

            return false;
        }
    }

    return true;
}

function validateCollections()
{
    if (!hasElements("selectedIndicators") && !hasElements("selectedDataElements"))
    {
        setMessage(i18n_must_select_at_least_one_indicator);

        return false;
    }

    if (!hasElements("selectedOrganisationUnits") && !isChecked("userOrganisationUnit"))
    {
        setMessage(i18n_must_select_at_least_one_unit);

        return false;
    }

    if (!hasElements("selectedPeriods") && !relativePeriodsChecked())
    {
        setMessage(i18n_must_select_at_least_one_period);

        return false;
    }

    return true;
}
