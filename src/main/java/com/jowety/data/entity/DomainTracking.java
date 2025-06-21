package com.jowety.data.entity;

import java.util.Date;

public interface DomainTracking {
	
	public String getIdString();

	/**
	 * @return the createdDate
	 */
	public abstract Date getCreatedDate();

	/**
	 * @param createdDate
	 */
	public abstract void setCreatedDate(Date createdDate);

	/**
	 * @return the createdBy
	 */
	public abstract String getCreatedBy();

	/**
	 * @param createdBy
	 */
	public abstract void setCreatedBy(String createdBy);

	/**
	 * @return the modifiedDate
	 */
	public abstract Date getModifiedDate();

	/**
	 * @param modifiedDate
	 */
	public abstract void setModifiedDate(Date modifiedDate);

	/**
	 * @return the modifiedBy
	 */
	public abstract String getModifiedBy();

	/**
	 * @param modifiedBy
	 */
	public abstract void setModifiedBy(String modifiedBy);



}