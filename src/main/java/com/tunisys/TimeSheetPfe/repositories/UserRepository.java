package com.tunisys.TimeSheetPfe.repositories;

import com.tunisys.TimeSheetPfe.models.ERole;
import com.tunisys.TimeSheetPfe.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel,Long> {
    @Query("select (count(u) > 0) from UserModel u where u.email = ?1")
    Boolean existsByEmail(String email);
    @Query("SELECT u FROM UserModel u JOIN u.roles r WHERE r.name = :roleName")
    List<UserModel> findByRole(@Param("roleName") ERole roleName);

    @Query("select u from UserModel u where u.email = ?1")
    Optional<UserModel> findByEmail(String email);
    @Query("SELECT COUNT(u) FROM UserModel u JOIN u.roles r WHERE r.name = :roleName")
    Long countByRole(@Param("roleName") ERole roleName);

}
