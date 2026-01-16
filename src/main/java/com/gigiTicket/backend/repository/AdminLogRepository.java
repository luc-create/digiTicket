package com.gigiTicket.backend.repository;

import com.gigiTicket.backend.model.AdminLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminLogRepository extends JpaRepository<AdminLog, Integer> {
	List<AdminLog> findByAdminIdOrderByDateActionDesc(Integer adminId);
}

