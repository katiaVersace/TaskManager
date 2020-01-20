package com.alten.springboot.taskmanager.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "team")
public class Team {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "name", nullable = false)
	private String name;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "employees_teams", joinColumns = { @JoinColumn(name = "team_id") }, inverseJoinColumns = {
			@JoinColumn(name = "employee_id") }, uniqueConstraints = @UniqueConstraint(columnNames = { "team_id",
					"employee_id" }))
	private Set<Employee> employees;

	@Column(name = "version", nullable = false)
	private int version;

	public Team() {
		super();
	}

	public Team(String name) {
		super();
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Employee> getEmployees() {
		if (employees == null)
			employees = new HashSet<Employee>();
		return employees;
	}

	public void setEmployees(Set<Employee> employees) {
		this.employees = employees;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "Team [id=" + id + ", name=" + name + ", employees=" + employees + ", version=" + version + "]";
	}

}
