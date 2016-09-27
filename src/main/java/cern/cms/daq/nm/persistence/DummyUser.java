package cern.cms.daq.nm.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

@Entity
public class DummyUser {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	private String username;
	private String email;
	private String phone;
	
	@Column(nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean useCustomEmail;
	
	@Column(nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean useCustomPhone;
	
	@Transient
	private String cernEmail;
	@Transient
	private String cernPhone;

	public boolean isUseCustomEmail() {
		return useCustomEmail;
	}

	public void setUseCustomEmail(boolean useCustomEmail) {
		this.useCustomEmail = useCustomEmail;
	}

	public boolean isUseCustomPhone() {
		return useCustomPhone;
	}

	public void setUseCustomPhone(boolean useCustomPhone) {
		this.useCustomPhone = useCustomPhone;
	}

	public String getCernEmail() {
		return cernEmail;
	}

	public void setCernEmail(String cernEmail) {
		this.cernEmail = cernEmail;
	}

	public String getCernPhone() {
		return cernPhone;
	}

	public void setCernPhone(String cernPhone) {
		this.cernPhone = cernPhone;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return "DummyUser [id=" + id + ", username=" + username + ", email=" + email + ", phone=" + phone
				+ ", cernEmail(transient)=" + cernEmail + ", cernPhone(transient)=" + cernPhone + "]";
	}



}
