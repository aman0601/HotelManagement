package com.hotel.booking.Service.impl;

import com.hotel.booking.Model.Entity.Booking;
import com.hotel.booking.Model.Entity.Hotel;
import com.hotel.booking.Model.Entity.User;
import com.hotel.booking.Repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl {

    @Autowired
    BookingRepository bookingRepository;

    public List<Booking> getUserBookings(User user) {
        return bookingRepository.findByUser(user);
    }

    public Booking createBooking(User user, Booking booking, Hotel hotel) {
        booking.setUser(user);
        booking.setHotel(hotel);
        return bookingRepository.save(booking);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }

    public void cancelBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking updateBooking(Booking booking) {
        // Perform validation or business logic if needed
        return bookingRepository.save(booking);
    }
}
