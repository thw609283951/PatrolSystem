package com.supersit.patrol.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.supersit.common.interfaces.IFlowTagName;
import com.supersit.patrol.db.PatrolDatabase;

@Table(database = PatrolDatabase.class)
public class EventTitleEntity extends BaseModel implements IFlowTagName,Parcelable{

	@PrimaryKey
	@SerializedName("workpastTitleId")
	private Long id;

	@Column
	@SerializedName("workpastTitle")
	private String name;

	@Column
	@SerializedName("workpastTypeHint")
	private String hint;
//
//	@ForeignKey(
//			onUpdate = ForeignKeyAction.CASCADE,
//			onDelete = ForeignKeyAction.CASCADE,
//			stubbedRelationship = true,
//			references = @ForeignKeyReference(
//					columnName = "eventType_id",
//					foreignKeyColumnName = "id"
//			)
//	)
	@Column
	@SerializedName("workpastTypeId")
	private Long eventTypeId;

	public Long getEventTypeId() {
		return eventTypeId;
	}

	public void setEventTypeId(Long eventTypeId) {
		this.eventTypeId = eventTypeId;
	}

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

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
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
		dest.writeString(this.hint);
		dest.writeValue(this.eventTypeId);
	}

	public EventTitleEntity() {
	}

	protected EventTitleEntity(Parcel in) {
		this.id = (Long) in.readValue(Long.class.getClassLoader());
		this.name = in.readString();
		this.hint = in.readString();
		this.eventTypeId = (Long) in.readValue(Long.class.getClassLoader());
	}

	public static final Creator<EventTitleEntity> CREATOR = new Creator<EventTitleEntity>() {
		@Override
		public EventTitleEntity createFromParcel(Parcel source) {
			return new EventTitleEntity(source);
		}

		@Override
		public EventTitleEntity[] newArray(int size) {
			return new EventTitleEntity[size];
		}
	};
}
