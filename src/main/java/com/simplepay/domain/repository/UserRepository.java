package com.simplepay.domain.repository;

import com.simplepay.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByDocument(String document);

    Optional<User> findByMail(String mail);

    boolean existsByDocument(String document);

    boolean existsByMail(String mail);

    @Query("SELECT u FROM User u WHERE u.document = :document OR u.mail = :mail")
    Optional<User> findByDocumentOrMail(@Param("document") String document, @Param("mail") String mail);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.document = :document OR u.mail = :mail")
    boolean existsByDocumentOrMail(@Param("document") String document, @Param("mail") String mail);
}
