package com.puzzly.api.repository;

import com.puzzly.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //public List<User> getUserList();

    public User findByUserIdOrUserName(Long userId, String username);

    @Query("select this from User this where this.userName =:username")
    public User getUserByUserName(@Param("username") String username);

    // dev
    public User findByEmail(String email);
}
