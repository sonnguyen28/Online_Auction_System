//
// Created by Nguyễn Sơn on 22/12/2021.
//

#ifndef SOCKETSERVER_HANDLE_MESSAGE_H
#define SOCKETSERVER_HANDLE_MESSAGE_H

#endif //SOCKETSERVER_HANDLE_MESSAGE_H

#include <stdio.h>
#include "cJSON.h"
#include "handle_database.h"
char *responseMess;

int readCommand(char *messageFormClient);
void handleRequest(int command, char *messageFromClient, int socketID);
int checkUserRunning(int userID);
Lot readInfoLot(char *messageFormClient);
void printClient();
void sendALL();
void sendOne(int socketID);