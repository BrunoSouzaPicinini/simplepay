package com.simplepay.domain.repository;

import com.simplepay.domain.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByDocument(String document);

    Optional<Seller> findByEmail(String email);

    boolean existsByDocument(String document);

    boolean existsByEmail(String email);

    @Query("SELECT s FROM Seller s WHERE s.document = :document OR s.email = :email")
    Optional<Seller> findByDocumentOrEmail(@Param("document") String document, @Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Seller s WHERE s.document = :document OR s.email = :email")
    boolean existsByDocumentOrEmail(@Param("document") String document, @Param("email") String email);
}
