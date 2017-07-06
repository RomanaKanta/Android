package com.aircast.photobag.model;

import java.util.Comparator;

public class PBTimelineHistoryModel {
	public enum Type {
		ACORN, HONEY, MAPLE, GOLDACORN
	}
	
	public class TimeComparator implements Comparator<PBTimelineHistoryModel>{

		@Override
		public int compare(PBTimelineHistoryModel lhs,
				PBTimelineHistoryModel rhs) {
			if(lhs.createdAt < rhs.createdAt) return -1;
			if(lhs.createdAt == rhs.createdAt) return 0;
			return 1;
		}
	}

	private int id;
	private int createdAt;
	private Type type;
	private String description;
	private boolean isNew;

	public PBTimelineHistoryModel(int id, int createdAt, Type type,
			String description, boolean isNew) {
		super();
		this.id = id;
		this.createdAt = createdAt;
		this.type = type;
		this.description = description;
		this.isNew = isNew;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(int createdAt) {
		this.createdAt = createdAt;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	@Override
	public String toString() {
		return "[" + this.id + " " + this.description+"]"; 
	}

}
