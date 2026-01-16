package com.gigiTicket.backend.repository;

import com.gigiTicket.backend.model.Notification;
import com.gigiTicket.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
	List<Notification> findByUserOrderByDateCreationDesc(User user);
	List<Notification> findByUserAndLueFalseOrderByDateCreationDesc(User user);
	List<Notification> findByTicketId(Integer ticketId);
}

