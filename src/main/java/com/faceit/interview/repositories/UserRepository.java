package com.faceit.interview.repositories;

import com.faceit.interview.repositories.models.UserDbModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<UserDbModel, UUID> {
}
