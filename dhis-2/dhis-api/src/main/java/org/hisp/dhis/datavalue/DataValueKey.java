package org.hisp.dhis.datavalue;

/*
 * Copyright (c) 2004-2009, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;

/**
 * @author Latifov Murodillo Abdusamadovich
 * @version $Id: DataValueKey.java 1 2009-04-10
 */

@Embeddable 
public class DataValueKey implements Serializable {
	
	private static final long serialVersionUID = 1L; 
	
	private Integer dataElementid;
	
	private Integer periodid;
	
	private Integer sourceid;
	
	private Integer categoryoptioncomboid;

	public DataValueKey(){}

	public DataValueKey(Integer categoryoptioncomboid, Integer dataElementid,
			Integer periodid, Integer sourceid) {
		super();
		this.categoryoptioncomboid = categoryoptioncomboid;
		this.dataElementid = dataElementid;
		this.periodid = periodid;
		this.sourceid = sourceid;
	}

	public Integer getDataElementid() {
		return dataElementid;
	}

	public void setDataElementid(Integer dataElementid) {
		this.dataElementid = dataElementid;
	}

	public Integer getPeriodid() {
		return periodid;
	}

	public void setPeriodid(Integer periodid) {
		this.periodid = periodid;
	}

	public Integer getSourceid() {
		return sourceid;
	}

	public void setSourceid(Integer sourceid) {
		this.sourceid = sourceid;
	}

	public Integer getCategoryoptioncomboid() {
		return categoryoptioncomboid;
	}

	public void setCategoryoptioncomboid(Integer categoryoptioncomboid) {
		this.categoryoptioncomboid = categoryoptioncomboid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((categoryoptioncomboid == null) ? 0 : categoryoptioncomboid
						.hashCode());
		result = prime * result
				+ ((dataElementid == null) ? 0 : dataElementid.hashCode());
		result = prime * result
				+ ((periodid == null) ? 0 : periodid.hashCode());
		result = prime * result
				+ ((sourceid == null) ? 0 : sourceid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataValueKey other = (DataValueKey) obj;
		if (categoryoptioncomboid == null) {
			if (other.categoryoptioncomboid != null)
				return false;
		} else if (!categoryoptioncomboid.equals(other.categoryoptioncomboid))
			return false;
		if (dataElementid == null) {
			if (other.dataElementid != null)
				return false;
		} else if (!dataElementid.equals(other.dataElementid))
			return false;
		if (periodid == null) {
			if (other.periodid != null)
				return false;
		} else if (!periodid.equals(other.periodid))
			return false;
		if (sourceid == null) {
			if (other.sourceid != null)
				return false;
		} else if (!sourceid.equals(other.sourceid))
			return false;
		return true;
	}
	
} 