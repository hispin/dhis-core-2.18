var validationRules = {
	/* dhis-web-maintenance-user */
	"user" : {
		"name" : {
			"range" : [ 2, 140 ]
		},
		"username" : {
			"range" : [ 2, 140 ],
			"firstletteralphabet" : true,
			"alphanumeric" : true
		},
		"password" : {
			"range" : [ 8, 35 ]
		},
		"email" : {
			"range" : [ 0, 160 ]
		},
		"phone" : {
			"range" : [ 0, 80 ]
		}
	},
	"role" : {
		"name" : {
			"range" : [ 2, 140 ]
		},
		"description" : {
			"range" : [ 2, 210 ]
		}
	},
	"userGroup" : {
		"name" : {
			"range" : [ 2, 210 ],
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : true
		}
	},

	/* dhis-web-maintenance-organisationunit */
	"organisationUnit": {
		"name": {
			"range": [2, 160]
		},
		"shortName": {
			"range": [2, 25]
		},
		"code": {
			"range": [0, 25]
		},
		"url": {
			"range": [0, 255]			
		},
		"contactPerson": {
			"range": [0, 255]
		},
		"address": {
			"range": [0, 255]
		},
		"email": {
			"range": [0, 250]
		},
		"phoneNumber": {
			"range": [0, 255]
		}
	},
	"organisationUnitGroup": {
		"name": {
			"range": [2, 160]
		}
	},
	"organisationUnitGroupSet": {
		"name": {
			"range": [2, 230]
		},
		"description": {
			"range": [2, 255]
		}
	},
	
	/* dhis-web-maintenance-dataset */
	"dataEntry": {
		"name": {
			"range": [4, 100]
		}
	},
	"section": {
		"name": {
			"range": [2, 160]
		},
		"selectedList": {
			
		}
	},
	"dataSet": {
		"name": {
			"alphanumericwithbasicpuncspaces": true,
			"firstletteralphabet": false,
			"range": [4, 150]
		},
		"shortName": {
			"alphanumericwithbasicpuncspaces": true,
			"firstletteralphabet": false,
			"range": [2, 20]
		},
		"code": {
			"alphanumericwithbasicpuncspaces": true,
			"notOnlyDigits": false,
			"range": [4, 40]
		}
	}
}
