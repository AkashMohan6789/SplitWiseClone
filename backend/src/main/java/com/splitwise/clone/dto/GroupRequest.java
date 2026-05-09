package com.splitwise.clone.dto;

import lombok.Data;
import java.util.List;

@Data
public class GroupRequest {
    private String name;
    private List<Long> memberIds;
}
