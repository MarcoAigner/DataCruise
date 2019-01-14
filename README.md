<h1 align="center"><img src="https://i.imgur.com/h4Gwpah.png" width="250" alt="data cruise"></h1>
<p align="center">An Android application providing real-time data & suggestions about car metrics.</p>
<br>

## About
This app was built as a university project for the course "Car IT" of studies "Information Management Automotive" at University of Applied Sciences Neu-Ulm.  
It has two modes:  
**Eco mode** gives you recommendations for when to shift, stats about remaining fuel and similiar information.  
**Sports mode** shows important information like engine load, temperature or revs per minute.  
Users can switch between these two modes seamlessly using the tabs at the top of their screen.
<p align="center"><img src="https://i.imgur.com/X3XliBz.jpg"></p>

## Implementation
*data cruise* works by connecting an Arduino computer to a car's on-board diagnostics port (OBD2).  
The Arduino then communicates with the app installed on the user's Android device via Bluetooth, transmitting diagnosis data in real-time. The application displays the received data to the user and gives driving recommendations based off of it.

## Team
Built by

  - [Marco Aigner](https://github.com/DerMarco/)
  - Laslo Welz
  - Dennis Hofmann
  - [Hanno Frenzel](https://github.com/HannoF/)
