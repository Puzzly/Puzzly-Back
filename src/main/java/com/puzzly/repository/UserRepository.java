package com.puzzly.repository;

import com.puzzly.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //public List<User> getUserList();

    public User findByUserIdOrUserName(Long userId, String userName);

    @Query("select this from User this where this.userName =:userName")
    public User getUserByName(@Param("userName") String userName);
}