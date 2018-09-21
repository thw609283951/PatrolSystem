package com.supersit.patrol.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.supersit.common.interfaces.IFlowTagName;
import com.supersit.patrol.db.PatrolDatabase;

import java.util.List;

@Table(database = PatrolDatabase.class)
public class EventTypeEntity extends BaseModel implements IFlowTagName,Parcelable{

	@PrimaryKey
	@SerializedName("workpastTypeId")
	private Long id;

	@Column
	@SerializedName("workpastType")
	private String name;

	@SerializedName("workpastTitles")
	private List<EventTitleEntity> eventTitleEntities;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(methods = {OneToMany.Method.ALL}, variableName = "eventTitleEntities")
	public List<EventTitleEntity> getEventTitleEntities() {
		if (eventTitleEntities == null || eventTitleEntities.isEmpty()) {
			eventTitleEntities = SQLite.select()
					.from(EventTitleEntity.class)
					.where(EventTitleEntity_Table.eventTypeId.eq(id))
					.queryList();
		}
		return eventTitleEntities;
	}

	public void setEventTitleEntities(List<EventTitleEntity> eventTitleEntities) {
		this.eventTitleEntities = eventTitleEntities;
	}

	@Override
	public String getFlowTagName() {
		return name;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(this.id);
		dest.writeString(this.name);
		dest.writeTypedList(this.eventTitleEntities);
	}

	public EventTypeEntity() {
	}

	protected EventTypeEntity(Parcel in) {
		this.id = (Long) in.readValue(Long.class.getClassLoader());
		this.name = in.readString();
		this.eventTitleEntities = in.createTypedArrayList(EventTitleEntity.CREATOR);
	}

	public static final Creator<EventTypeEntity> CREATOR = new Creator<EventTypeEntity>() {
		@Override
		public EventTypeEntity createFromParcel(Parcel source) {
			return new EventTypeEntity(source);
		}

		@Override
		public EventTypeEntity[] newArray(int size) {
			return new EventTypeEntity[size];
		}
	};
}
