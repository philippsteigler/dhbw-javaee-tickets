package org.dhbw.mosbach.ai.tickets.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Klasse zur Modellierung einer konkreten Rolle.
 *
 * Jede Rolle besitzt eine eindeituge ID als Primärschlüssel, eine Bezeichnung und eine Beschreibung zur besseren
 * Verwendbarkeit.
 */
@Entity
public class Role {

	// Attribute analog zu Spalten ein der Role-Tabelle.
	@Id
	@GeneratedValue
	private long id;

	@Column(nullable = false, length = 64, unique = true)
	private String name;

	@Column(length = 1024)
	private String description;

	public Role() {
		super();
	}

	// Für jede Rolle wird der Name und die Bezeichnung beim Erzeugen gesetzt.
	// Der Primärschlüssel wird automatisch vergeben.
	public Role(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}

	// Getter und Setter
	@XmlTransient
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@XmlAttribute(required = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	// hashCode(), equals() und toString() werden Aufgrund von Vererbung implementiert.
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Role))
			return false;
		final Role other = (Role) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			return other.name == null;
		} else return name.equals(other.name);
	}

	@Override
	public String toString() {
		return String.format("Role [id=%s, name=%s, description=%s]", id, name, description);
	}
}
