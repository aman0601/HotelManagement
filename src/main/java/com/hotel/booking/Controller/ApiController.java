package com.hotel.booking.Controller;


import com.hotel.booking.Config.JwtTokenProvider;
import com.hotel.booking.Model.Entity.*;
import com.hotel.booking.Model.Request.LoginRequest;
import com.hotel.booking.Model.Request.SignupRequest;
import com.hotel.booking.Model.Response.JwtResponse;
import com.hotel.booking.Model.Response.MessageResponse;
import com.hotel.booking.Repository.HotelRepository;
import com.hotel.booking.Repository.RoleRepository;
import com.hotel.booking.Repository.UserRepository;
import com.hotel.booking.Service.impl.BookingServiceImpl;
import com.hotel.booking.Service.impl.UserDetailsImpl;
import com.hotel.booking.Service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    BookingServiceImpl bookingService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenProvider jwtUtils;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    HotelRepository hotelRepository;

    // User Management Endpoints

    @PostMapping("/users/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        logger.info("Authentication Successfull, generating token : ");

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        logger.info("Token generation successfull, fetching user data : ");

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles));
    }

    @PostMapping("/users/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            logger.error("Username already exists");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if (role.equals("ROLE_ADMIN")) {
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        logger.info("Saving user data to database!");
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        User updatedUser = userService.updateUser(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Booking Management Endpoints

    @PostMapping("/bookings")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        Hotel hotel = hotelRepository.findByNameAndLocation(booking.getHotelName(), booking.getLocation());
        if (hotel == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Handle hotel not found
        }
        User user = userService.getUserByUsername(((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        Booking newBooking = bookingService.createBooking(user, booking, hotel);
        return new ResponseEntity<>(newBooking, HttpStatus.CREATED);
    }

    @GetMapping("/bookings/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @GetMapping("/bookings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @PutMapping("/bookings")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Booking> updateBooking(@RequestBody Booking booking) {
        Booking updatedBooking = bookingService.updateBooking(booking);
        return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
    }

    @DeleteMapping("/bookings/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/bookings/hotel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Hotel>> getHotelBookings(@RequestBody Hotel hotel) {
        List<Hotel> hotels;

        if (hotel != null) {
            // If hotel object is provided, check its properties to construct the query
            String name = hotel.getName();
            String location = hotel.getLocation();

            if (name != null && location != null) {
                // If both name and location are provided
                hotels = Collections.singletonList(hotelRepository.findByNameAndLocation(name, location));
            } else if (name != null) {
                // If only name is provided
                hotels = Collections.singletonList(hotelRepository.findByName(name));
            } else if (location != null) {
                // If only location is provided
                hotels = hotelRepository.findByLocation(location);
            } else {
                // If the hotel object is provided but no specific criteria, return all hotels
                hotels = hotelRepository.findAll();
            }
        } else {
            // If hotel object is not provided, return all hotels
            hotels = hotelRepository.findAll();
        }

        return new ResponseEntity<>(hotels, HttpStatus.OK);
    }
}
