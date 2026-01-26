package com.gms.demo.repository;

import com.gms.demo.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepo extends JpaRepository<Activity,Integer> {
    List<Activity> findByUserIdOrderByDateDesc(int userId);
}
