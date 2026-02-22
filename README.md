# Garage-Management-System(GMS)
A backend-focused web application built with Spring Boot, MYSQL fro backend and a custom HTML/CSS/JavaScript frontend. This system allows customers to manage vehicles and book services, while administrators manage users, services, previously owned sales vehicles, and system activities..

The project is designed to practice real-world backend concepts such as REST APIs, database relationships, validation, and frontend–backend communication — without relying on heavy frontend frameworks.

📌 Project Overview
° User registration and login (Role-based authentication)
° Vehicle registration & management linked to users using userId
° Backend validation for avoiding duplicate entries
° Service booking & tracking
° Admin control panel operations
° Activity logging
° Used vehicle sales listing(provided with owner info)

The system supports two main roles:

👤 Customer
🛠️ Admin & Super Admin

👤 Customer Features
  -> Login / Role-based access
  -> Personal Dashboard (Active services, vehicle count, last service)
  -> Add / Edit / Delete Vehicles
  -> View Service History per vehicle
  -> Book Services
  -> Duplicate service booking prevention
  -> Real-time success/error messaging
  -> Profile management 
  -> Personal Activity Log tracking all actions

🛠️ Admin Features
  -> Admin Dashboard (Users, Active services, Revenue, Pending requests)
  -> User Management (Add, Delete, Search, Sort, Pagination)
  -> Vehicle Registry Monitoring
  -> Service Status Control (Pending / Active / Completed)
  -> Previously owned vehicles Sales Management (Add, Edit, Delete sale vehicles)
  -> Activity Logging for system operations
  -> Validation-based deletion (Cannot delete user/vehicle with active services)

🛠 Tech Stack 
Backend : Java | Spring Boot | MySQL
Frontend : HTML5 | CSS | JavaScript


