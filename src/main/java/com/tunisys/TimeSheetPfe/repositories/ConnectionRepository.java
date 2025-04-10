package com.tunisys.TimeSheetPfe.repositories;

import com.tunisys.TimeSheetPfe.models.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ConnectionRepository extends JpaRepository<Connection,Long> {

    @Query("select c from Connection c where c.userId = ?1")
    List<Connection> findByUserId(Long userId);

    @Query("select (count(c) > 0) from Connection c where c.userId = ?1 and c.token = ?2")
    Boolean existsByUserIdAndToken(Long userId, String token);

    @Transactional
    @Modifying
    @Query("delete from Connection c where c.userId = ?1 and c.token = ?2")
    void deleteByUserIdAndToken(Long userId, String token);
}
