package com.splitwise.clone.repository;

import com.splitwise.clone.model.Expense;
import com.splitwise.clone.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByGroup(Group group);
}
