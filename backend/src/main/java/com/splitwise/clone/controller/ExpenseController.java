package com.splitwise.clone.controller;

import com.splitwise.clone.dto.ExpenseRequest;
import com.splitwise.clone.model.Expense;
import com.splitwise.clone.model.ExpenseShare;
import com.splitwise.clone.model.Group;
import com.splitwise.clone.model.User;
import com.splitwise.clone.repository.ExpenseRepository;
import com.splitwise.clone.repository.GroupRepository;
import com.splitwise.clone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    ExpenseRepository expenseRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> addExpense(@RequestBody ExpenseRequest request) {
        Group group = groupRepository.findById(request.getGroupId()).orElseThrow();
        User payer = userRepository.findById(request.getPaidById()).orElseThrow();

        Expense expense = new Expense();
        expense.setGroup(group);
        expense.setPaidBy(payer);
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setDate(LocalDateTime.now());

        List<ExpenseShare> shares = new ArrayList<>();
        
        for (Map.Entry<Long, BigDecimal> entry : request.getSplits().entrySet()) {
            User user = userRepository.findById(entry.getKey()).orElseThrow();
            ExpenseShare share = new ExpenseShare();
            share.setExpense(expense);
            share.setUser(user);
            share.setAmountOwed(entry.getValue());
            shares.add(share);
        }
        
        expense.setShares(shares);
        expenseRepository.save(expense);

        return ResponseEntity.ok(expense);
    }
    
    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getGroupExpenses(@PathVariable Long groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow();
        List<Expense> expenses = expenseRepository.findByGroup(group);
        return ResponseEntity.ok(expenses);
    }
}
