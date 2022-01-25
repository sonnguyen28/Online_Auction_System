
//
// Created by Nguyễn Sơn on 23/12/2021.
//
#include <sys/socket.h>
#include <stdio.h>
#include "cJSON.h"
#include "handle_message.h"
#include "handle_client.h"
#include "string.h"

#define MAXLINE 4096

int readCommand(char *messageFromClient){
    cJSON *messJson = cJSON_Parse(messageFromClient);
    cJSON *command = cJSON_GetObjectItemCaseSensitive(messJson, "command");
    /*if (cJSON_IsNumber(command))
    {
        printf("Command: \"%d\"\n", command->valueint);
    }*/
    return command->valueint;
}

User readInfoClient(char *messageFromClient){
    User newUser;
    cJSON *messJson = cJSON_Parse(messageFromClient);
    cJSON *username = cJSON_GetObjectItemCaseSensitive(messJson, "username");
    cJSON *password = cJSON_GetObjectItemCaseSensitive(messJson, "password");
    cJSON *user_id = cJSON_GetObjectItemCaseSensitive(messJson, "user_id");
    if(user_id != NULL){
        newUser.user_id = user_id->valueint;
    }
    if(username != NULL){
        strcpy(newUser.username, username->valuestring);
    }
    if(password != NULL){
        strcpy(newUser.password, password->valuestring);
    }

    return newUser;
}

void printClient(){
    for (int i = 0; i < count_user; ++i) {
        printf("|%d|-|%d|-|%d|\n", i, listUser[i].user_id, listUser[i].socket_id);
    }
}

int checkUserRunning(int userID){
    for (int i = 0; i < count_user; ++i) {
        if(listUser[i].user_id == userID) return 0; // Phat hien user da login o noi khac
    }
    return 1;
}

Lot readInfoLot(char *messageFromClient) // Dung de doc message cua chuc nang create Lot
{
    Lot newLot;
    cJSON *messJson = cJSON_Parse(messageFromClient);
    cJSON *title = cJSON_GetObjectItemCaseSensitive(messJson, "title");
    cJSON *description = cJSON_GetObjectItemCaseSensitive(messJson, "description");
    cJSON *min_price = cJSON_GetObjectItemCaseSensitive(messJson, "min_price");
    //cJSON *time_start = cJSON_GetObjectItemCaseSensitive(messJson, "time_start");
    cJSON *time_stop = cJSON_GetObjectItemCaseSensitive(messJson, "time_stop");
    cJSON *image_link = cJSON_GetObjectItemCaseSensitive(messJson, "image_link");

    if(title != NULL){
        strcpy(newLot.title,title->valuestring);
    }
    if(description != NULL){
        strcpy(newLot.description, description->valuestring);
    }
    if(min_price != NULL){
        newLot.min_price = (float )min_price->valuedouble;
    }
    if(time_stop != NULL){
        //newLot.start_time = UnixTimeFromMysqlString(time_start->valuestring);
        newLot.stop_time = convert(time_stop->valuestring);
    }
    if(image_link != NULL){
        //printf("{%s\n%s}\n", convertTimeToString(newLot.start_time), convertTimeToString(newLot.stop_time));
        strcpy(newLot.image_link, image_link->valuestring);
    }
    return newLot;
}

Bid readInfoBid(char *messageFromClient)
{
    Bid newBid;
    cJSON *messJson = cJSON_Parse(messageFromClient);
    cJSON *user_id = cJSON_GetObjectItemCaseSensitive(messJson, "user_id");
    cJSON *lot_id = cJSON_GetObjectItemCaseSensitive(messJson, "lot_id");
    cJSON *bid_amount = cJSON_GetObjectItemCaseSensitive(messJson, "bid_amount");

    newBid.lot_id = lot_id->valueint;
    newBid.bidder_user_id = user_id->valueint;
    newBid.bid_amount = (float )bid_amount->valuedouble;

    return newBid;
}

void sendALL(){
    responseMess[strlen(responseMess)+1] = '\0';
    responseMess[strlen(responseMess)]='\n';
    for (int i = 0; i < count_user; i++) {
        // response message to client
        printf("Send to user %d: %s\n",listUser[i].user_id, responseMess);
        //FD_SET(listUser[i].socket_id, &readfds);
        send(listUser[i].socket_id , responseMess , strlen(responseMess) , 0 );
        //FD_CLR(listUser[i].socket_id, &readfds);
    }
    //memset(responseMess, '\0', MAXLINE);
    free(responseMess);
}

void sendOne(int socketID){
    //printf("Send to user %d: %s\n",socketID, responseMess);
    // response message to client
    responseMess[strlen(responseMess)+1] = '\0';
    responseMess[strlen(responseMess)]='\n';
    send(socketID , responseMess , strlen(responseMess) , 0 );
    free(responseMess);
    //memset(responseMess, '\0', MAXLINE);
}

void handleRequest(int command, char *messageFromClient, int socketID){
    cJSON *responseMessJson = cJSON_CreateObject();
    cJSON *commandJson = NULL;
    int response_command;
    int result;
    int isUserRunning;
    User newUser;
    Bid newBid;
    Lot newLot;
    int indexLot;
    switch (command) {
        //SIGN UP
        case 1:
            newUser = readInfoClient(messageFromClient);
            printf("SIGN UP (command: %d)\n", command);
            printf("Username: \"%s\"\n", newUser.username);
            printf("Password: \"%s\"\n", newUser.password);
            result = register_user(newUser.username, newUser.password);
            if(result > 0){
                newUser.user_id = result;
                printf("Sign up success !!!\n");
                /*AddClient(newUser.user_id, socketID, &count_user);*/ // Dung trong truong hop login luon sau khi dang ky thanh cong
                printClient();
                response_command = 1;
                commandJson = cJSON_CreateNumber(response_command);
                cJSON_AddItemToObject(responseMessJson, "command", commandJson);
                /*cJSON *userIdJson = cJSON_CreateNumber(newUser.user_id);
                cJSON_AddItemToObject(responseMessJson, "user_id", userIdJson);*/
            } else{
                printf("Sign up fail !\n");
                response_command = -1;
                commandJson = cJSON_CreateNumber(response_command);
                cJSON_AddItemToObject(responseMessJson, "command", commandJson);
            }
            // Convert Json to string
            responseMess = (char *) malloc(MAXLINE*sizeof (char ));
            responseMess = cJSON_PrintUnformatted(responseMessJson);
            printf("Send to client: %s\n", responseMess);
            // response message to client
            responseMess[strlen(responseMess)+1] = '\0';
            responseMess[strlen(responseMess)]='\n';
            send(socketID , responseMess , strlen(responseMess) , 0 );
            memset(responseMess, '\0', MAXLINE);
            break;

            //LOGIN
        case 2:
            newUser = readInfoClient(messageFromClient);
            /*printf("LOGIN (command: %d)\n", command);
            printf("Username: \"%s\"\n", newUser.username);
            printf("Password: \"%s\"\n", newUser.password);*/
            result = login_user(newUser.username, newUser.password);
            isUserRunning = checkUserRunning(result);
            if(result > 0 && isUserRunning){
                newUser.user_id = result;
                printf("LOGIN success !!!\nUserid: %d\n", result);
                AddClient(newUser.user_id, socketID, &count_user);
                /*printClient();*/
                response_command = 2;
                commandJson = cJSON_CreateNumber(response_command);
                cJSON_AddItemToObject(responseMessJson, "command", commandJson);
                cJSON *userIdJson = cJSON_CreateNumber(newUser.user_id);
                cJSON_AddItemToObject(responseMessJson, "user_id", userIdJson);

                // create string json array current list lot
                cJSON *lotsJson = cJSON_CreateArray();
                cJSON_AddItemToObject(responseMessJson, "lots", lotsJson);
                /*available_lots();
                printfListLot();*/

                // Add tung phan tu vao list
                cJSON *lotJson;
                for (int i = 0; i < lotTotal; i++) {
                    cJSON_AddItemToArray(lotsJson, lotJson = cJSON_CreateObject());
                    cJSON_AddItemToObject(lotJson, "lot_id", cJSON_CreateNumber(currentListLots[i].lot_id));
                    cJSON_AddItemToObject(lotJson, "title", cJSON_CreateString(currentListLots[i].title));
                    cJSON_AddItemToObject(lotJson, "description", cJSON_CreateString(currentListLots[i].description));
                    cJSON_AddItemToObject(lotJson, "winning_bid", cJSON_CreateNumber(currentListLots[i].winning_bid));
                    cJSON_AddItemToObject(lotJson, "winning_bidder", cJSON_CreateNumber(currentListLots[i].winning_bidder));
                    cJSON_AddItemToObject(lotJson, "owner_id",cJSON_CreateNumber(currentListLots[i].owner_id));
                    cJSON_AddItemToObject(lotJson, "time_start",cJSON_CreateString(convertTimeToString(currentListLots[i].start_time)));
                    cJSON_AddItemToObject(lotJson, "time_stop",cJSON_CreateString(convertTimeToString(currentListLots[i].stop_time)));
                    cJSON_AddItemToObject(lotJson, "image_link",cJSON_CreateString(currentListLots[i].image_link));
                }
            } else{
                printf("Login fail !\n");
                response_command = -2;
                commandJson = cJSON_CreateNumber(response_command);
                cJSON_AddItemToObject(responseMessJson, "command", commandJson);
            }
            // Convert Json to string]
            responseMess = (char *) malloc(MAXLINE*sizeof (char ));
            responseMess = cJSON_PrintUnformatted(responseMessJson);
            printf("Send to client: %s\n", responseMess);
            sendOne(socketID);
            break;

        case 3:
            newBid = readInfoBid(messageFromClient);
            result = create_bid(newBid.lot_id, newBid.bid_amount, newBid.bidder_user_id);
            newBid.bid_id = result;
            indexLot = SearchLot(newBid.lot_id);
            responseMess = (char *) malloc(MAXLINE*sizeof (char ));
            if(result >= 0){
                response_command = 3;
                commandJson = cJSON_CreateNumber(response_command);
                cJSON_AddItemToObject(responseMessJson, "command", commandJson);
                cJSON *lotJson = cJSON_CreateObject();
                cJSON_AddItemToObject(responseMessJson, "lot", lotJson);
                cJSON_AddItemToObject(lotJson, "lot_id", cJSON_CreateNumber(currentListLots[indexLot].lot_id));
                cJSON_AddItemToObject(lotJson, "title", cJSON_CreateString(currentListLots[indexLot].title));
                cJSON_AddItemToObject(lotJson, "description", cJSON_CreateString(currentListLots[indexLot].description));
                cJSON_AddItemToObject(lotJson, "winning_bid", cJSON_CreateNumber(currentListLots[indexLot].winning_bid));
                cJSON_AddItemToObject(lotJson, "winning_bidder", cJSON_CreateNumber(currentListLots[indexLot].winning_bidder));
                cJSON_AddItemToObject(lotJson, "owner_id",cJSON_CreateNumber(currentListLots[indexLot].owner_id));
                cJSON_AddItemToObject(lotJson, "time_start",cJSON_CreateString(convertTimeToString(currentListLots[indexLot].start_time)));
                cJSON_AddItemToObject(lotJson, "image_link",cJSON_CreateString(currentListLots[indexLot].image_link));
                responseMess = cJSON_PrintUnformatted(responseMessJson);
                sendALL();
            } else{
                response_command = -3;
                commandJson = cJSON_CreateNumber(response_command);
                cJSON_AddItemToObject(responseMessJson, "command", commandJson);
                responseMess = cJSON_PrintUnformatted(responseMessJson);
                sendOne(socketID);
            }

            break;

        case 4:
            newUser = readInfoClient(messageFromClient);
            newLot = readInfoLot(messageFromClient);
            printf("|%s - %s - %.2f - %s - %s|\n", newLot.title, newLot.description, newLot.min_price, convertTimeToString(newLot.stop_time), newLot.image_link);
            /*char str[20];
            strcpy(str, convertTimeToString(newLot.stop_time));
            printf("%s\n", str);*/
            /*result = create_lot(newLot.min_price, newLot.title, newLot.description, newUser.user_id,
                                convertTimeToString(newLot.stop_time));
            if (result >= 0){
                addLotToList(newLot.min_price, newLot.title, newLot.description, newUser.user_id, convertTimeToString(newLot.stop_time));
                printfListLot();
            }*/
            //Ket qua tra ve la lot_id moi duoc them vao
            result = addLotToList(newLot.min_price, newLot.title, newLot.description, newUser.user_id, convertTimeToString(newLot.stop_time), newLot.image_link);
            indexLot = SearchLot(result);
            if(result >= 0){
                response_command = 4;
                commandJson = cJSON_CreateNumber(response_command);
                cJSON_AddItemToObject(responseMessJson, "command", commandJson);
                cJSON *lotJson = cJSON_CreateObject();
                cJSON_AddItemToObject(responseMessJson, "lot", lotJson);
                cJSON_AddItemToObject(lotJson, "lot_id", cJSON_CreateNumber(currentListLots[indexLot].lot_id));
                cJSON_AddItemToObject(lotJson, "title", cJSON_CreateString(currentListLots[indexLot].title));
                cJSON_AddItemToObject(lotJson, "description", cJSON_CreateString(currentListLots[indexLot].description));
                cJSON_AddItemToObject(lotJson, "winning_bid", cJSON_CreateNumber(currentListLots[indexLot].winning_bid));
                cJSON_AddItemToObject(lotJson, "winning_bidder", cJSON_CreateNumber(currentListLots[indexLot].winning_bidder));
                cJSON_AddItemToObject(lotJson, "owner_id",cJSON_CreateNumber(currentListLots[indexLot].owner_id));
                cJSON_AddItemToObject(lotJson, "time_start",cJSON_CreateString(convertTimeToString(currentListLots[indexLot].start_time)));
                cJSON_AddItemToObject(lotJson, "time_stop",cJSON_CreateString(convertTimeToString(currentListLots[indexLot].stop_time)));
                cJSON_AddItemToObject(lotJson, "image_link",cJSON_CreateString(currentListLots[indexLot].image_link));
                responseMess = cJSON_PrintUnformatted(responseMessJson);
                sendALL();
            }else {
                response_command = -4;
                commandJson = cJSON_CreateNumber(response_command);
                cJSON_AddItemToObject(responseMessJson, "command", commandJson);
                responseMess = cJSON_PrintUnformatted(responseMessJson);
                sendOne(socketID);
            }
            //printfListLot();
            break;
        case 5:
            newUser = readInfoClient(messageFromClient);
            //  printf("%d\n",newUser.user_id);
            result = checkUserID(newUser.user_id);
            if(result){
                response_command = 5;
                commandJson = cJSON_CreateNumber(response_command);
                cJSON_AddItemToObject(responseMessJson, "command", commandJson);
                cJSON *userIdJson = cJSON_CreateNumber(newUser.user_id);
                cJSON_AddItemToObject(responseMessJson, "user_id", userIdJson);

                // create string json array current list lot
                cJSON *lotsJson = cJSON_CreateArray();
                cJSON_AddItemToObject(responseMessJson, "lots", lotsJson);
                lotHistory(newUser.user_id);
                //printfListLot();

                // Add tung phan tu vao list
                cJSON *lotJson;
                for (int i = 0; i < lotTotalHistory; i++) {
                    cJSON_AddItemToArray(lotsJson, lotJson = cJSON_CreateObject());
                    cJSON_AddItemToObject(lotJson, "lot_id", cJSON_CreateNumber(listLotsHistory[i].lot_id));
                    cJSON_AddItemToObject(lotJson, "title", cJSON_CreateString(listLotsHistory[i].title));
                    cJSON_AddItemToObject(lotJson, "description", cJSON_CreateString(listLotsHistory[i].description));
                    cJSON_AddItemToObject(lotJson, "winning_bid", cJSON_CreateNumber(listLotsHistory[i].winning_bid));
                    cJSON_AddItemToObject(lotJson, "winning_bidder", cJSON_CreateNumber(listLotsHistory[i].winning_bidder));
                    cJSON_AddItemToObject(lotJson, "owner_id",cJSON_CreateNumber(listLotsHistory[i].owner_id));
                    cJSON_AddItemToObject(lotJson, "time_start",cJSON_CreateString(convertTimeToString(listLotsHistory[i].start_time)));
                    cJSON_AddItemToObject(lotJson, "time_stop",cJSON_CreateString(convertTimeToString(listLotsHistory[i].stop_time)));
                    cJSON_AddItemToObject(lotJson, "image_link",cJSON_CreateString(listLotsHistory[i].image_link));
                }
            } else{

                response_command = -5;
                commandJson = cJSON_CreateNumber(response_command);
                cJSON_AddItemToObject(responseMessJson, "command", commandJson);
            }
            // Convert Json to string
            responseMess = (char *) malloc(MAXLINE*sizeof (char ));
            responseMess = cJSON_PrintUnformatted(responseMessJson);
            printf("Send to client: %s\n", responseMess);
            // response message to client
            sendOne(socketID);
            break;

        case 6:
            newUser = readInfoClient(messageFromClient);
            result = DeleteClientUserID(newUser.user_id, &count_user);
            if(result >= 0){
                response_command = 6;
                commandJson = cJSON_CreateNumber(response_command);
                cJSON_AddItemToObject(responseMessJson, "command", commandJson);

                // Convert Json to string
                responseMess = (char *) malloc(MAXLINE*sizeof (char ));
                responseMess = cJSON_PrintUnformatted(responseMessJson);
                printf("Send to client: %s\n", responseMess);

                // response message to client
                responseMess[strlen(responseMess)+1] = '\0';
                responseMess[strlen(responseMess)]='\n';
                send(socketID , responseMess , strlen(responseMess) , 0 );
                //memset(responseMess, '\0', MAXLINE);
                free(responseMess);
                printf("Dang xuat thanh cong !!!\n");
                //CloseSocket(socketID); // Dang xuat nhung ko dong socket ket noi (De co the dang nhap lai)
            } else{
                response_command = -6;
                commandJson = cJSON_CreateNumber(response_command);
                cJSON_AddItemToObject(responseMessJson, "command", commandJson);
                // Convert Json to string
                responseMess = (char *) malloc(MAXLINE*sizeof (char ));
                responseMess = cJSON_PrintUnformatted(responseMessJson);
                printf("Send to client: %s\n", responseMess);

                // response message to client
                responseMess[strlen(responseMess)+1] = '\0';
                responseMess[strlen(responseMess)]='\n';
                send(socketID , responseMess , strlen(responseMess) , 0 );
                free(responseMess);
                //memset(responseMess, '\0', MAXLINE);
            }
            break;

        case 8:
            newUser = readInfoClient(messageFromClient);
            cJSON *messJson = cJSON_Parse(messageFromClient);
            cJSON *lot_id = cJSON_GetObjectItemCaseSensitive(messJson, "lot_id");
            newLot.lot_id = lot_id->valueint;

            response_command = 8;
            commandJson = cJSON_CreateNumber(response_command);
            cJSON_AddItemToObject(responseMessJson, "command", commandJson);

            // create string json array list Bid History
            cJSON *bidsJson = cJSON_CreateArray();
            cJSON_AddItemToObject(responseMessJson, "bids", bidsJson);
            bidsHistory(newLot.lot_id);
            //printListBids();

            // Add tung phan tu vao list
            cJSON *bidJson;
            for (int i = 0; i < totalBidsHistory; i++) {
                cJSON_AddItemToArray(bidsJson, bidJson = cJSON_CreateObject());
                cJSON_AddItemToObject(bidJson, "bid_id", cJSON_CreateNumber(listBidsHistory[i].bid_id));
                cJSON_AddItemToObject(bidJson, "lot_id", cJSON_CreateNumber(listBidsHistory[i].lot_id));
                cJSON_AddItemToObject(bidJson, "bid_amount", cJSON_CreateNumber(listBidsHistory[i].bid_amount));
                cJSON_AddItemToObject(bidJson, "bid_amount", cJSON_CreateNumber(listBidsHistory[i].bidder_user_id));
                cJSON_AddItemToObject(bidJson, "created", cJSON_CreateString(convertTimeToString(listBidsHistory[i].created)));
            }

            // Convert Json to string
            responseMess = (char *) malloc(MAXLINE*sizeof (char ));
            responseMess = cJSON_PrintUnformatted(responseMessJson);
            printf("Send to client: %s\n", responseMess);
            sendOne(socketID);

            break;

    }
}