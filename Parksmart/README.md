# ParkSmart

ParkSmart is a Java Swing smart parking management project built for academic DSA implementation. It simulates parking slot management, vehicle tracking, reservations, route finding, analytics, and role-based access for admin and staff users.

## Features

- Admin and staff login
- Dashboard with live parking metrics
- Slot management by block and slot group
- Vehicle management with history tracking
- Reservation management
- Route visualizer for shortest path guidance
- Analytics for occupancy, block ranking, and estimated revenue
- Java Swing GUI with a dark blue theme

## DSA Concepts Used

- Graphs for parking route modeling
- Shortest path algorithms for navigation
- Searching and filtering for slots, vehicles, and reservations
- Sorting for record ordering
- Greedy and optimization concepts in analytics
- Modular service-model architecture for clean separation of logic

## Tech Stack

- Java
- Java Swing
- Object-Oriented Programming
- DSA-based service layer
- Git and GitHub
- GitHub Pages for project presentation

## Project Structure

```text
src/
  app/        -> entry point
  gui/        -> Swing panels and UI
  model/      -> domain models
  service/    -> business logic
  dsa/        -> graph and algorithms
screenshots/  -> project screenshots
docs/         -> GitHub Pages site files
```

## How to Run

1. Clone the repository:

```bash
   git clone https://github.com/TAditya007/SEM-3-Project-ParkSmart-.git
   ```

1. Open the project in Eclipse or IntelliJ IDEA.

2. Compile and run:

   ```bash
   javac --release 8 -d out -sourcepath src src/app/Main.java
   java -cp out app.Main
   ```

## Screenshots

Add screenshots in the `screenshots/` folder and reference them here.

- Login page
- Dashboard
- Slot management
- Vehicle management
- Route panel
- Analytics panel

Example markdown once screenshots are added:

```md



```

## GitHub Pages

A simple project website can be hosted from the `docs/` folder using GitHub Pages. In repository settings, set the Pages source to the `main` branch and `/docs` folder so GitHub serves `docs/index.html` as the project site.

## Future Improvements

- Database integration
- Real-time parking sensors / IoT support
- Better route animation
- Exportable reports
- Better active staff tracking

## Author

K Bhargava Aditya  
Engineering Student(KLH-B)  
Academic Project: ParkSmart
