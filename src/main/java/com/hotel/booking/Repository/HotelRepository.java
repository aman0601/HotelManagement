package com.hotel.booking.Repository;

import com.hotel.booking.Model.Entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    Hotel findByName(String name);

    List<Hotel> findByLocation(String location);

    Hotel findByNameAndLocation(String name, String location);
}
