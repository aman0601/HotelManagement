# HotelManagement

# User and Booking Management API

This Postman collection provides a set of API endpoints for managing users and bookings.

## Authentication

To authenticate, you can use the "Authenticate User" request, which requires a username and password. After successful authentication, you'll receive an access token that you can use in subsequent requests by setting it in the Authorization header.

## Endpoints

### User Management

- **Create User**
  - Endpoint: POST /api/users/signup
  - Description: Create a new user with the provided username, password, and roles.

- **Create Admin**
  - Endpoint: POST /api/users/signup
  - Description: Create a new admin user with admin privileges.

- **Get User by Username**
  - Endpoint: GET /api/users/{username}
  - Description: Retrieve user information by username.

- **Get All Users**
  - Endpoint: GET /api/users
  - Description: Retrieve information for all users.

- **Update User**
  - Endpoint: PUT /api/users
  - Description: Update user information, including username, password, and roles.

- **Delete User**
  - Endpoint: DELETE /api/users/{id}
  - Description: Delete a user by their ID.

### Booking Management

- **Create Booking**
  - Endpoint: POST /api/bookings
  - Description: Create a new booking with details such as hotel name, check-in/out dates, and number of guests.

- **Get Booking by ID**
  - Endpoint: GET /api/bookings/{id}
  - Description: Retrieve booking information by its ID.

- **Get All Bookings**
  - Endpoint: GET /api/bookings
  - Description: Retrieve information for all bookings.

- **Update Booking**
  - Endpoint: PUT /api/bookings
  - Description: Update booking information, including hotel ID, check-in/out dates, and number of guests.

- **Cancel Booking**
  - Endpoint: DELETE /api/bookings/{id}
  - Description: Cancel a booking by its ID.

### Hotel Search

- **Hotel Search**
  - Endpoint: GET /api/bookings/hotel
  - Description: Search for hotels by name.

## Usage

1. Import this Postman collection into your Postman application.
2. Update the base URL in each request to match your API server's URL (default is http://localhost:8080).
3. For requests requiring authentication, obtain an access token using the "Authenticate User" request and set it in the Authorization header of subsequent requests.
4. Execute the desired requests to interact with the API.
