//
// Created by Nguyễn Sơn on 22/12/2021.
//

#ifndef SOCKETSERVER_HANDLE_CLIENT_H
#define SOCKETSERVER_HANDLE_CLIENT_H

#endif //SOCKETSERVER_HANDLE_CLIENT_H

#include <stdio.h>
#include <unistd.h>
#include <stdio.h>
#include <arpa/inet.h>

//set of socket descriptors
fd_set readfds;
#define MAXCLIENT 100

typedef struct{
    int user_id;
    int socket_id;
} Client;

typedef struct{
    int user_id;
    char username[32];
    char password[64];
} User;

Client listUser[MAXCLIENT]; // Luu tru client login thanh cong -> User
int count_user;

int client_socket[MAXCLIENT]; // Luu tru client dang ket noi voi server

int AddClient(int userID, int socketID, int *countUser);
int SearchClientUserID(int userID, int countUser);
int DeleteClientUserID(int userID, int *countUser);
int SearchClientSocketID(int socketID, int countUser);
int DeleteClientSocketID(int socketID, int *countUser);
void CloseSocket(int socketID);