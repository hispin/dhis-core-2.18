var validationRules = {
	/* dhis-web-maintenance-user */
	"user" : {
		"name" : {
			"rangelength" : [ 2, 140 ]
		},
		"username" : {
			"rangelength" : [ 2, 140 ],
			"firstletteralphabet" : true,
			"alphanumeric" : true
		},
		"password" : {
			"rangelength" : [ 8, 35 ]
		},
		"email" : {
			"rangelength" : [ 0, 160 ]
		},
		"phone" : {
			"rangelength" : [ 0, 80 ]
		}
	},
	"role" : {
		"name" : {
			"rangelength" : [ 2, 140 ]
		},
		"description" : {
			"rangelength" : [ 2, 210 ]
		}
	},
	"userGroup" : {
		"name" : {
			"rangelength" : [ 2, 210 ],
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : true
		}
	},

	/* dhis-web-maintenance-organisationunit */
	"organisationUnit" : {
		"name" : {
			"rangelength" : [ 2, 160 ]
		},
		"shortName" : {
			"rangelength" : [ 2, 25 ]
		},
		"code" : {
			"rangelength" : [ 0, 25 ]
		},
		"url" : {
			"rangelength" : [ 0, 255 ]
		},
		"contactPerson" : {
			"rangelength" : [ 0, 255 ]
		},
		"address" : {
			"rangelength" : [ 0, 255 ]
		},
		"email" : {
			"rangelength" : [ 0, 250 ]
		},
		"phoneNumber" : {
			"rangelength" : [ 0, 255 ]
		}
	},
	"organisationUnitGroup" : {
		"name" : {
			"rangelength" : [ 2, 160 ]
		}
	},
	"organisationUnitGroupSet" : {
		"name" : {
			"rangelength" : [ 2, 230 ]
		},
		"description" : {
			"rangelength" : [ 2, 255 ]
		}
	},

	/* dhis-web-maintenance-dataset */
	"dataEntry" : {
		"name" : {
			"rangelength" : [ 4, 100 ]
		}
	},
	"section" : {
		"name" : {
			"rangelength" : [ 2, 160 ]
		},
		"selectedList" : {

		}
	},
	"dataSet" : {
		"name" : {
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : false,
			"rangelength" : [ 4, 150 ]
		},
		"shortName" : {
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : false,
			"rangelength" : [ 2, 20 ]
		},
		"code" : {
			"alphanumericwithbasicpuncspaces" : true,
			"notOnlyDigits" : false,
			"rangelength" : [ 4, 40 ]
		}
	},

	/* dhis-web-maintenance-dataadmin */
	"sqlView" : {
		"name" : {
			"rangelength" : [ 2, 50 ]
		},
		"description" : {
			"rangelength" : [ 2, 255 ]
		},
		"sqlquery" : {
			"rangelength" : [ 1, 255 ]
		}
	},
	"dataLocking" : {},
	"dataBrowser" : {},
	"minMax" : {},

	/* dhis-web-validationrule */
	"validationRule" : {
		"name" : {
			"rangelength" : [ 2, 160 ]
		},
		"description" : {
			"rangelength" : [ 2, 160 ]
		}
	},
	"validationRuleGroup" : {
		"name" : {
			"rangelength" : [ 2, 160 ]
		},
		"description" : {
			"rangelength" : [ 2, 160 ]
		}
	},

	/* dhis-web-maintenance-datadictionary */
	"concept" : {
		"name" : {
			"rangelength" : [ 3, 10 ]
		}
	},
	"dateElementCategoryCombo" : {
		"name" : {
			"rangelength" : [ 2, 160 ]
		}
	},
	"dateElementCategory" : {
		"name" : {
			"rangelength" : [ 2, 160 ]
		}
	},
	"dataElementGroup" : {
		"name" : {
			"rangelength" : [ 3, 150 ]
		}
	},
	"dataElementGroupSet" : {
		"name" : {
			"rangelength" : [ 2, 230 ]
		}
	},
	"indicator" : {
		"name" : {
			"rangelength" : [ 3, 150 ],
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : true
		},
		"shortName" : {
			"rangelength" : [ 2, 20 ],
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : true
		},
		"alternativeName" : {
			"rangelength" : [ 3, 150 ],
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : true
		},
		"code" : {
			"rangelength" : [ 3, 25 ],
			"alphanumericwithbasicpuncspaces" : true,
			"notOnlyDigits" : true
		},
		"description" : {
			"rangelength" : [ 3, 250 ],
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : true
		},
		"url" : {
			"rangelength" : [ 0, 255 ]
		}
	},
	"indicatorGroup" : {
		"name" : {
			"rangelength" : [ 3, 150 ],
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : true
		}
	},
	"indicatorGroupSet" : {
		"name" : {
			"rangelength" : [ 2, 230 ]
		}
	},
	"indicatorType" : {
		"name" : {
			"rangelength" : [ 3, 150 ],
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : true
		},
		"factor" : {
			"rangelength" : [ 1, 10 ]
		}
	}
}
