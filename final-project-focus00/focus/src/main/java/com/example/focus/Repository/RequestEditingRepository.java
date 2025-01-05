package com.example.focus.Repository;

import com.example.focus.Model.RequestEditing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestEditingRepository extends JpaRepository<RequestEditing, Integer> {

    List<RequestEditing> findByStatus(String status);
    List<RequestEditing> findRequestEditingsByEditor_Id(Integer id);
}
