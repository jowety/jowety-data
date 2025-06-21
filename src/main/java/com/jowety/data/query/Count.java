package com.jowety.data.query;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.jowety.util.ObjectUtil;

/**
 * <p>This is used by the {@link SearchDaoIF#getCountsAsTree(List, List)} method
 * to hold a count associated with a grouping of values. Those values
 * are contained in the values Map.
 *
 * <p>A Count may contain a List<Count> of subcounts. This is to create a
 * tree structure where each successive layer will contain one additional
 * grouping value in the values Map.
 *
 * <p>For example, for a tree count of Employees, grouped first by department
 * and next by gender, the first layer of Counts would only contain the department
 * in the values Map. Each of these Counts would contain subcounts that have
 * both department and gender Counts.
 *
 * <p>Each Count also holds a percentage, and the higher total used to compute the percentage.
 *
 */
public class Count {

	Map<String, Object> values;
	Long count;
	Long total;
	Float percent;
	List<Count> subcounts;

	public Map<String, Object> getValues() {
		return values;
	}
	public void setValues(Map<String, Object> values) {
		this.values = values;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public Float getPercent() {
		return percent;
	}
	public void setPercent(Float percent) {
		this.percent = percent;
	}
	public List<Count> getSubcounts() {
		return subcounts;
	}
	public void setSubcounts(List<Count> subcounts) {
		this.subcounts = subcounts;
	}
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		if(values!=null) {
			tsb.append("values", ObjectUtil.objectToString(values));
		}
		tsb.append("count", this.count)
		.append("out of", this.total)
		.append("percent", this.percent);
		if(subcounts!=null) {
			for(Count sc: subcounts) {
				tsb.append("\nsubcount", sc);
			}
		}
		return tsb.toString();
	}




}
