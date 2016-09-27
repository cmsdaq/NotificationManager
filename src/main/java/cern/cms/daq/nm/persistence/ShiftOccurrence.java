package cern.cms.daq.nm.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "daq_shifts", schema = "cms_shiftlist")
public class ShiftOccurrence {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "shifter_id")
	private Long userId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "shift_start")
	private java.util.Date start;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "shift_end")
	private java.util.Date end;

	@Column(name = "shift_type")
	private String shiftType;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public java.util.Date getStart() {
		return start;
	}

	public void setStart(java.util.Date start) {
		this.start = start;
	}

	public java.util.Date getEnd() {
		return end;
	}

	public void setEnd(java.util.Date end) {
		this.end = end;
	}

	public String getShiftType() {
		return shiftType;
	}

	public void setShiftType(String shiftType) {
		this.shiftType = shiftType;
	}

	@Override
	public String toString() {
		return "ShiftOccurrence [userId=" + userId + ", start=" + start + ", end=" + end + ", shiftType=" + shiftType
				+ "]";
	}
}
