# Final project for cs5010 from group SEN YAN & HAOYU YANG
## How to use?
- First Step: Enter server name and server port number in the terminal: "localhost\n" and "8000"
- We defined 8000 as the port number.

- Users are allowed to enter ? for printing instructions anytime.

- login <username> : Command used to log into the chat room.
- Chat room client would require every user to login before they could move forward.
- Users will get a response from server like: "Hello, XXX. There are X other connected clients." or
- "You're the only client in the chat room"

- logoff : Command used to log off from the server.
- Client is no longer connected to the server after executing this command. However, clients are able to login again anytime.
- Client receives a response from server: "You are no longer connected"

- who : Command used to query active clients in chat room.
- The client who sends this request would be deducted from the list of active clients.
- Client receives a list of other active clients.

- @user <username> message : Command used to send a direct message to someone.
- Server will check whether names of sender and recipient are valid. If not, a failure message will be sent back to client.
- Sender receives a message saying "message sent successfully"
- Recipient receives a message saying "You got a message from XXX: XXX message bodyXXXX".

- @all message : Command used to send a broadcast message.
- Server will check whether name of the sender is valid.
- Sender receives a message saying "message send successfully"
- All active users in the chat room except the sender will receive the message.

- !user <username> : Command used to send a insult to someone.
- Server will check whether name of the recipient is valid.
- Sender receives a message saying "message send successfully"
- Recipient receives the insult message.