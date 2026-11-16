package com.hotel.hotel_management.auth;

import com.hotel.hotel_management.user.Role;

public record LoginResponse(String username, Role role, boolean mustChangePassword) {
}
