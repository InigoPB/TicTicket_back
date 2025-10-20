package com.tickea.repository;

import com.tickea.jpa.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketItemRepository extends JpaRepository<TicketItem, Long> {
    @Query("SELECT item FROM TicketItem item " +
           "JOIN item.ticket t " +
           "WHERE t.fechaTicket = :fecha AND t.firebaseUid = :uid")
    List<TicketItem> findItemsByFechaAndUid(@Param("fecha") LocalDate fecha,
                                            @Param("uid") String uid);


    @Query("SELECT item FROM TicketItem item " +
           "JOIN item.ticket t " +
           "WHERE t.fechaTicket BETWEEN :fechaInicio AND :fechaFin " +
           "AND t.firebaseUid = :uid")
    List<TicketItem> findItemsByFechaRangeAndUid(@Param("fechaInicio") LocalDate fechaInicio,
                                                 @Param("fechaFin") LocalDate fechaFin,
                                                 @Param("uid") String uid);                                       
}

