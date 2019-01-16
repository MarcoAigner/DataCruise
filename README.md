<h1 align="center"><img src="https://i.imgur.com/h4Gwpah.png" width="250" alt="data cruise"></h1>
<p align="center">An Android application providing real-time data & suggestions about car metrics.</p>
<br>

## About
This app was built as a university project for the course "Car IT" of studies "Information Management Automotive" at University of Applied Sciences Neu-Ulm.  
It provides two modes:  

**Eco mode** aids in driving more fuel efficently by providing information about the current rpm, shift advice and similar information.

**Sports mode** provides information useful for a sporty driving style. Keeps track of the engine load, (max-) speed, rpm and other.

Users can switch between these two modes seamlessly using the tabs at the top of their screen.
<p align="center"><img src="https://i.imgur.com/WMihwCF.jpg"></p>

## Implementation
*data cruise* works by connecting an Arduino computer to a car's on-board diagnostics port (OBD2).
The Arduino then communicates with the app installed on the user's Android device via Bluetooth, transmitting diagnosis data in real-time. The application displays the received data to the user and provides driving recommendations based off of it.
If the driver continues to drive with either too high or too low rpm for a certain amount of time, this event will be saved on a thingspeak server.

As the OBD Port is a standardized diagnostics interface, this app can be used with any car (programmed arduino computer required).

## Team
Built by

  - [Marco Aigner](https://github.com/DerMarco/)
  - [Laslo Welz](https://github.com/LasHarry/)
  - Dennis Hofmann
  - [Hanno Frenzel](https://github.com/HannoF/)
  
  ## Links
  YouTube Video showcasing the app:
  
 <a href="http://www.youtube.com/watch?feature=player_embedded&v=pH8dUQXGjCs
" target="_blank"><img src="http://img.youtube.com/vi/pH8dUQXGjCs/0.jpg" 
alt="Data Cruise YouTube Preview" width="240" height="180" border="10" /></a>
  
  Thingspeak server: 
  
  https://thingspeak.com/channels/676477-
