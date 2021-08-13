package com.nikowis.kalah.repository;

import com.nikowis.kalah.model.Kalah;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface KalahRepository extends MongoRepository<Kalah, String> {
}
