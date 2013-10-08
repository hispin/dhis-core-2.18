function changeRuleType( ruleType )
{
	if (ruleType == 'validation')
	{
		hideById( 'organisationUnitLevelTR');
		hideById( 'sequentialSampleCountTR');
		hideById( 'annualSampleCountTR');
		hideById( 'highOutliersTR');
		hideById( 'lowOutliersTR');
	} else
	{
		showById( 'organisationUnitLevelTR');
		showById( 'sequentialSampleCountTR');
		showById( 'annualSampleCountTR');
		showById( 'highOutliersTR');
		showById( 'lowOutliersTR');
    }
}
