package com.hotel.booking.Config;

import com.hotel.booking.Model.Entity.ERole;
import com.hotel.booking.Model.Entity.Hotel;
import com.hotel.booking.Model.Entity.Role;
import com.hotel.booking.Repository.HotelRepository;
import com.hotel.booking.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final HotelRepository hotelRepository;

    @Autowired
    public DataInitializer(RoleRepository roleRepository, HotelRepository hotelRepository) {
        this.roleRepository = roleRepository;
        this.hotelRepository = hotelRepository;
    }

    @Override
    public void run(String... args) {
        if (!roleRepository.findByName(ERole.ROLE_USER).isPresent()) {
            roleRepository.save(new Role(ERole.ROLE_USER));
        }
        if (!roleRepository.findByName(ERole.ROLE_ADMIN).isPresent()) {
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
        }
        if (hotelRepository.findByName("Taj Hotel") == null) {
            hotelRepository.save(new Hotel("Taj Hotel", "Delhi"));
        }
        if (hotelRepository.findByName("Leela Hotel") == null) {
            hotelRepository.save(new Hotel("Leela Hotel", "Delhi"));
        }
        if (hotelRepository.findByName("Radisson Hotel") == null) {
            hotelRepository.save(new Hotel("Radisson Hotel", "Mumbai"));
        }
        if (hotelRepository.findByName("Ramada Hotel") == null) {
            hotelRepository.save(new Hotel("Ramada Hotel", "Jaipur"));
        }
    }
}
