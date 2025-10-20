package com.tickea.repository;

import com.tickea.jpa.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

	@Query("""
		         select distinct t.fechaTicket
		         from Ticket t
		         where t.firebaseUid = :uid
		         order by t.fechaTicket
		         """)
		  List<LocalDate> findFechasRegistradas(@Param("uid") String uid);


}
