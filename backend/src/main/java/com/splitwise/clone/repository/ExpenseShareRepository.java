package com.splitwise.clone.repository;

import com.splitwise.clone.model.ExpenseShare;
import com.splitwise.clone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Long> {
    List<ExpenseShare> findByUser(User user);
}
