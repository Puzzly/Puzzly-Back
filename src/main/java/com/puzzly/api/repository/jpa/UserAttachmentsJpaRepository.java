package com.puzzly.api.repository.jpa;

import com.puzzly.api.entity.User;
import com.puzzly.api.entity.UserAttachments;
import com.puzzly.api.repository.jpa.querydsl.UserAttachmentsJpaRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserAttachmentsJpaRepository extends JpaRepository<UserAttachments, Long>, UserAttachmentsJpaRepositoryCustom {

    public Optional<UserAttachments> findByUserAndIsDeleted(User user, Boolean isDeleted);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserAttachments SET isDeleted =:after, deleteDateTime =:deleteDateTime, deleteUser=:user where isDeleted=:before and user =:user")
    public void bulkUpdateIsDeleted(User user, Boolean before, Boolean after, LocalDateTime deleteDateTime);

}
