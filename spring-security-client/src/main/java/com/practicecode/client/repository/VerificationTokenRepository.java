package com.practicecode.client.repository;

import com.practicecode.client.entity.VerficationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerficationToken, Long> {
}
