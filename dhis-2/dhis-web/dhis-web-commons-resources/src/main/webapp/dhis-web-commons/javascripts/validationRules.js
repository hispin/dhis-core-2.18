var validationRules = {
	/* dhis-web-maintenance-user */
	"user" : {
		"name" : {
			"length" : [ 2, 140 ]
		},
		"username" : {
			"length" : [ 2, 140 ],
			"firstletteralphabet" : true,
			"alphanumeric" : true
		},
		"password" : {
			"length" : [ 8, 35 ]
		},
		"email" : {
			"length" : [ 0, 160 ]
		},
		"phone" : {
			"length" : [ 0, 80 ]
		}
	},
	"role" : {
		"name" : {
			"length" : [ 2, 140 ]
		},
		"description" : {
			"length" : [ 2, 210 ]
		}
	},
	"userGroup" : {
		"name" : {
			"length" : [ 2, 210 ],
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : true
		}
	},

	/* dhis-web-maintenance-organisationunit */
	"organisationUnit": {
		"name": {
			"length": [2, 160]
		},
		"shortName": {
			"length": [2, 25]
		},
		"code": {
			"length": [0, 25]
		},
		"url": {
			"length": [0, 255]			
		},
		"contactPerson": {
			"length": [0, 255]
		},
		"address": {
			"length": [0, 255]
		},
		"email": {
			"length": [0, 250]
		},
		"phoneNumber": {
			"length": [0, 255]
		}
	},
	"organisationUnitGroup": {
		"name": {
			"length": [2, 160]
		}
	},
	"organisationUnitGroupSet": {
		"name": {
			"length": [2, 230]
		},
		"description": {
			"length": [2, 255]
		}
	},
	
	/* dhis-web-maintenance-dataset */
	"dataEntry": {
		"name": {
			"length": [4, 100]
		}
	},
	"section": {
		"name": {
			"length": [2, 160]
		},
		"selectedList": {
			
		}
	},
	"dataSet": {
		"name": {
			"alphanumericwithbasicpuncspaces": true,
			"firstletteralphabet": false,
			"length": [4, 150]
		},
		"shortName": {
			"alphanumericwithbasicpuncspaces": true,
			"firstletteralphabet": false,
			"length": [2, 20]
		},
		"code": {
			"alphanumericwithbasicpuncspaces": true,
			"notOnlyDigits": false,
			"length": [4, 40]
		}
	}
}
