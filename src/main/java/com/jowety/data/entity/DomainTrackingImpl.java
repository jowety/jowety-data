package com.jowety.data.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@MappedSuperclass
public abstract class DomainTrackingImpl implements Serializable, DomainTracking{

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false, updatable=false)
	protected Date createdDate;

	@Column(nullable=false, updatable=false)
	protected String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date modifiedDate;

	protected String modifiedBy;

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}


	@Override
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}


	@Override
	public String getCreatedBy() {
		return createdBy;
	}


	@Override
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}


	@Override
	public Date getModifiedDate() {
		return modifiedDate;
	}


	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}


	@Override
	public String getModifiedBy() {
		return modifiedBy;
	}


	@Override
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

}
