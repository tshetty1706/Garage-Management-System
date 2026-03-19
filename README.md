# GO GARAGE - Garage-Management-System(GMS)
A backend-focused web application built with Spring Boot, MYSQL fro backend and a custom HTML/CSS/JavaScript frontend. This system allows customers to manage vehicles and book services, while administrators manage users, services, previously owned sales vehicles, and system activities..

The project is designed to practice real-world backend concepts such as REST APIs, database relationships, validation, and frontend–backend communication without relying on heavy frontend frameworks.

📌 Project Overview <br>
° User registration and login (Role-based authentication) <br>
° Vehicle registration & management linked to users using userId <br>
° Backend validation for avoiding duplicate entries <br>
° Service booking & tracking <br>
° Admin control panel operations <br>
° Activity logging <br>
° Used vehicle sales listing(provided with owner info) <br>

The system supports two main roles:<br>

👤 Customer<br>
🛠️ Admin & Super Admin<br>

👤 Customer Features<br>
  -> Login / Role-based access<br>
  -> Personal Dashboard (Active services, vehicle count, last service)<br>
  -> Add / Edit / Delete Vehicles<br>
  -> View Service History per vehicle<br>
  -> Book Services<br>
  -> Duplicate service booking prevention<br>
  -> Real-time success/error messaging<br>
  -> Profile management <br>
  -> Personal Activity Log tracking all actions<br>

🛠️ Admin Features
  -> Admin Dashboard (Users, Active services, Revenue, Pending requests)<br>
  -> User Management (Add, Delete, Search, Sort, Pagination)<br>
  -> Vehicle Registry Monitoring<br>
  -> Service Status Control (Pending / Active / Completed)<br>
  -> Previously owned vehicles Sales Management (Add, Edit, Delete sale vehicles)<br>
  -> Activity Logging for system operations<br>
  -> Validation-based deletion (Cannot delete user/vehicle with active services)<br>

🛠 Tech Stack <br>
Backend : Java | Spring Boot | MySQL<br>
Frontend : HTML5 | CSS | JavaScript<br>


