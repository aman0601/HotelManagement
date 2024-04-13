package com.hotel.booking.Repository;

import com.hotel.booking.Model.Entity.ERole;
import com.hotel.booking.Model.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
