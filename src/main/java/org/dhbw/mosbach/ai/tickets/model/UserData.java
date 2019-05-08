package org.dhbw.mosbach.ai.tickets.model;

import javax.persistence.*;

@Entity
@Table(indexes = { @Index(columnList = "user_id", unique = true) })
public class UserData {
	@Id
	@GeneratedValue
	private long id;

	@OneToOne(optional = false, fetch = FetchType.EAGER)
	private User user;

	public UserData() {
		super();
	}

	public UserData(User user) {
		this.user = user;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
