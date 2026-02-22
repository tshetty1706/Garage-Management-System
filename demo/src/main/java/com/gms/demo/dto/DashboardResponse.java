package com.gms.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DashboardResponse {
    String name;
    String userName;

    private int totalUsers;
    private int activeServices;
    private int pendingRequests;
    private double totalRevenue;

    List<RecentActivityDto> recentActivity;
}
