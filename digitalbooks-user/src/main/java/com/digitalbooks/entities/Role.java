package com.digitalbooks.entities;

import javax.persistence.*;

@Entity
@Table(name="Roles")
public class Role {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int id;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	Roles role;
	
	public Role() {
	}

	public Role(int id, Roles role) {
		super();
		this.id = id;
		this.role = role;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "Role [id=" + id + ", role=" + role + "]";
	}

}
