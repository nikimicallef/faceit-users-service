package com.faceit.interview.repositories.models;

import com.bol.secure.Encrypted;
import com.mongodb.lang.Nullable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document("users")
public class UserDbModel implements Persistable<UUID> {
	@Id
	private UUID id;
	private String firstName;
	private String lastName;
	@Nullable
	private String nickname;
	@Encrypted
	private String password;
	@Indexed(unique = true)
	private String email;
	private String country;
	@CreatedDate
	private LocalDateTime createdAt;
	@LastModifiedDate
	private LocalDateTime updatedAt;
	@Transient
	private Boolean isNew = null;

	public UserDbModel() {
	}

	public UserDbModel(String firstName, String lastName, @Nullable String nickname, String password, String email, String country) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.nickname = nickname;
		this.password = password;
		this.email = email;
		this.country = country;
	}

	@Override
	public boolean isNew() {
		if (isNew == null && id == null) {
			isNew = true;
		} else if (isNew == null && id != null) {
			isNew = false;
		}
		return isNew;
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

	@Nullable
	public String getNickname() {
		return nickname;
	}

	public void setNickname(@Nullable String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		UserDbModel that = (UserDbModel) o;

		return new EqualsBuilder().append(id, that.id).append(firstName, that.firstName).append(lastName, that.lastName).append(nickname, that.nickname).append(password, that.password).append(email, that.email).append(country, that.country).append(createdAt, that.createdAt).append(updatedAt, that.updatedAt).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(id).append(firstName).append(lastName).append(nickname).append(password).append(email).append(country).append(createdAt).append(updatedAt).toHashCode();
	}

	@Override
	public String toString() {
		return "UserDbModel{" +
		"id=" + id +
		", firstName='" + firstName + '\'' +
		", lastName='" + lastName + '\'' +
		", nickname='" + nickname + '\'' +
		", password='" + password + '\'' +
		", email='" + email + '\'' +
		", country='" + country + '\'' +
		", createdAt=" + createdAt +
		", updatedAt=" + updatedAt +
		'}';
	}
}
