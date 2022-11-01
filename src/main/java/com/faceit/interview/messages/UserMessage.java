package com.faceit.interview.messages;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

public class UserMessage {
	private UserMessageOperationEnum operation;
	private UUID id;
	private String firstName;
	private String lastName;
	private String nickname;
	private String email;
	private String country;

	public UserMessage(UserMessageOperationEnum operation, UUID id, String firstName, String lastName, String nickname, String email, String country) {
		this.operation = operation;
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.nickname = nickname;
		this.email = email;
		this.country = country;
	}

	public UserMessageOperationEnum getOperation() {
		return operation;
	}

	public void setOperation(UserMessageOperationEnum operation) {
		this.operation = operation;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		UserMessage that = (UserMessage) o;

		return new EqualsBuilder().append(operation, that.operation).append(id, that.id).append(firstName, that.firstName).append(lastName, that.lastName).append(nickname, that.nickname).append(email, that.email).append(country, that.country).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(operation).append(id).append(firstName).append(lastName).append(nickname).append(email).append(country).toHashCode();
	}

	@Override
	public String toString() {
		return "UserMessage{" +
		"operation=" + operation +
		", id=" + id +
		", firstName='" + firstName + '\'' +
		", lastName='" + lastName + '\'' +
		", nickname='" + nickname + '\'' +
		", email='" + email + '\'' +
		", country='" + country + '\'' +
		'}';
	}
}
