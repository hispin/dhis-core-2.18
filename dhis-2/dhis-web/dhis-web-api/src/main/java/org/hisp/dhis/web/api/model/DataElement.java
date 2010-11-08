package org.hisp.dhis.web.api.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DataElement extends AbstractModel {	

	private String type;

	private AbstractModelList categoryOptionCombos;
	
	public DataElement() {

	}	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

        public AbstractModelList getCategoryOptionCombos()
        {
            return categoryOptionCombos;
        }
    
        public void setCategoryOptionCombos( AbstractModelList categoryOptionCombos )
        {
            this.categoryOptionCombos = categoryOptionCombos;
        }
	
	
		
}
