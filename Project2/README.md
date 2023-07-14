# CPD Project 2 - Typing Racer

## Work Done By: 

| GROUP  | 14 |
| ------------- | ------------- |
| Vicente Salvador Martínez Lora  | up202006950@up.pt  |
| Giulio Guarino | up202211853@fe.up.pt  |
| Luis Filipe Pinto Cunha | up201709375@up.pt
_________________________________________________________________________________________

## Description:

A directory with the source code for project 2 of the CPD course. This is part of the distributed computing topic.

The goal of this project is to build a service capable of matching players and put them together in a lobby managed by a server through multiple threads.
Once the lobby is created, the service should allow the users to play a game and then collect the results to update their ranks based on them.

_________________________________________________________________________________________

## How it works:

1. A server application starts by running on the local machine, listening on port 8000 waiting for client connections.
2. Several client applications run on the local machine, connecting to the server at the same port.
3. Each client is asked to either log in or register. 
4. If authenticated, a menu shows up allowing users to check their ranks, join a game queue, or exit the app. 
5. If the player chooses to queue, the service will keep the player locked in a queue waiting for a game to begin. 
6. Once the right amount of players is achieved, the game starts.
7. Once the game ends, the players are again presented with the main menu.

---------------------------------------------------------------

### Further explanation:

These are the classes that were created for this project and their descriptions:

- **Client**: Represents a client that connects to the server. It establishes a socket connection to the server, sets up input and output streams for communication, and creates separate threads for input and output operations.

- **Game**: Represents a game instance. It implements the Callable interface and manages the game flow. It keeps track of the players, their scores, and the game results. It also communicates with players and handles the game's logic. In this version, it works just as a simulation that gives random scores to each player. 

- **GameResult**: Represents all the possible results of a game: First place, second place, third place or forth place.

- **Menu** Responsible for a simple menu system that displays options to the user and reads their choice from the input. It provides a way to interact with the user and navigate through the different functionalities of the application.

- **Server** Represents the game server that listens for incoming client connections. It manages the game lobbies, handles client connections, starts game threads and cleans up finished games. Furthermore, the server manages multiple games simultaneously and checks the connectivity of players.

- **SocketReception**: Accepts incoming client connections in the server application and creates the instances of objects needed to manage each new user.

- **User**: Represents a user connected to the server. It encapsulates the client socket, input/output streams, and handles communication with the server. This class interacts with the lobby, game, and performs actions based on user input.

- **UserManager**: Manages the users, their credentials and their data, in other words, it handles user authentication, registration, points management, user rankings and provides methods to validate user credentials.
_________________________________________________________________________________________

## How to use:

### To run on windows:
#### Setup with a server and 4 clients (players):
- Just double click the file:
```run_windows.bat```
#### Setup with a server and 8 clients (players):
- Just double click the file:
```run_windows_2games.bat```
---------------------------------------------------------------
#### Manual setup:
- Erase all the files .class inside of the folder src / run ```0_cleanproject.bat```
- Compile all the files .java with the comand ```javac nameofthefile.java``` / run ```1_buildproject.bat```
- Run the file Server in one Terminal and the Client(s) in another(s) terminal(s) ```java Server 8000``` ```java Client localhost 8000``` / run ```2_startproject.bat```
---------------------------------------------------------------
### To run on Linux:

- Run the command:
```make```
- Run the file Server in one Terminal and the Client(s) in another(s) terminal:
```java Server 8000```
```java Client localhost 8000```

_________________________________________________________________________________________

## Important Notes:
- The game is just a simulation, as the focus of the project was on the service that connects clients to a server and not the game itself.
- To easily add 4 player, run the file ```3_add4players.bat``` in the 'src' folder
- When starting the server and the client, you will have to authenticate yourself, or log in or register, all the data created will be deposited in the file "credentials.txt" inside the folder "data"

_________________________________________________________________________________________

## Requirements
- java framework
