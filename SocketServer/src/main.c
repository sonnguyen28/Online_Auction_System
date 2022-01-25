#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include "cJSON.h"
#include "handle_message.h"
#include "handle_client.h"
#include "string.h"
#include <errno.h>
#include <pthread.h>

#define MAXLINE 4096
#define TRUE 1
#define FALSE 0
#define PORT 8888

void *threadTime(void *data){
    while (TRUE){
        for (int i = 0; i < lotTotal; ++i) {
            time_t t = time(NULL);
            struct tm tm = *localtime(&t);
            char start_time[20];
            sprintf(start_time, "%.4d-%.2d-%.2d %.2d:%.2d:%.2d", tm.tm_year + 1900, tm.tm_mon + 1, tm.tm_mday,
                    tm.tm_hour, tm.tm_min, tm.tm_sec);
            printf("|%s|-|%s|\n", start_time, convertTimeToString(currentListLots[i].stop_time));
            if (difftime(currentListLots[i].stop_time, t) <= 0) {
                DeleteLotInList(currentListLots[i].lot_id);
                printf("Lot %d het han !!!\n", currentListLots[i].lot_id);
                int response_command = 7;
                cJSON *commandJson = cJSON_CreateNumber(response_command);
                cJSON *responseMessJson = cJSON_CreateObject();
                cJSON_AddItemToObject(responseMessJson, "command", commandJson);
                cJSON *lotJson = cJSON_CreateObject();
                cJSON_AddItemToObject(responseMessJson, "lot", lotJson);
                cJSON_AddItemToObject(lotJson, "lot_id", cJSON_CreateNumber(currentListLots[i].lot_id));
                cJSON_AddItemToObject(lotJson, "title", cJSON_CreateString(currentListLots[i].title));
                cJSON_AddItemToObject(lotJson, "description", cJSON_CreateString(currentListLots[i].description));
                cJSON_AddItemToObject(lotJson, "winning_bid", cJSON_CreateNumber(currentListLots[i].winning_bid));
                cJSON_AddItemToObject(lotJson, "winning_bidder", cJSON_CreateNumber(currentListLots[i].winning_bidder));
                cJSON_AddItemToObject(lotJson, "owner_id",cJSON_CreateNumber(currentListLots[i].owner_id));
                cJSON_AddItemToObject(lotJson, "time_start",cJSON_CreateString(convertTimeToString(currentListLots[i].start_time)));
                cJSON_AddItemToObject(lotJson, "time_stop",cJSON_CreateString(convertTimeToString(currentListLots[i].stop_time)));
                cJSON_AddItemToObject(lotJson, "image_link",cJSON_CreateString(currentListLots[i].image_link));
                responseMess = cJSON_PrintUnformatted(responseMessJson);
                sendALL();
            }
        }
        sleep(1);
    }
}

int main(void) {
    int opt = TRUE;
    int master_socket, addrlen, new_socket;
    int max_sd;
    struct sockaddr_in address;
    count_user = 0; //Bien dem client da login thanh cong

    char buffer[MAXLINE];

    //initialise all client_socket[] to 0 so not checked
    for (int i = 0; i < MAXCLIENT; i++)
    {
        client_socket[i] = 0;
    }

    //create a master socket
    if((master_socket = socket(AF_INET, SOCK_STREAM, 0)) == 0){
        perror("Socket failed");
        exit(EXIT_FAILURE);
    }

    //set master socket to allow multiple connections ,
    //this is just a good habit, it will work without this
    if(setsockopt(master_socket, SOL_SOCKET, SO_REUSEADDR, (char *)&opt, sizeof(opt)) < 0){
        perror("setsockopt");
        exit(EXIT_FAILURE);
    }

    //type of socket created
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = inet_addr("0.0.0.0");
    address.sin_port = htons( PORT );

    //bind the socket to localhost port 8888
    if (bind(master_socket, (struct sockaddr *)&address, sizeof(address))<0)
    {
        perror("bind failed");
        exit(EXIT_FAILURE);
    }
    printf("Listener on port %d \n", PORT);

    //try to specify maximum of 3 pending connections for the master socket
    if (listen(master_socket, 3) < 0)
    {
        perror("listen");
        exit(EXIT_FAILURE);
    }

    //accept the incoming connection
    addrlen = sizeof(address);
    puts("Waiting for connections ...");
    // Load data for the first time
    available_lots();
    printfListLot();
   /* // Tao thread bo dem thoi gian
    pthread_t thread_time;
    pthread_create(&thread_time, NULL, threadTime, (void *)&thread_time);*/
    while (TRUE){
        //clear the socket set
        FD_ZERO(&readfds);

        //add master socket to set
        FD_SET(master_socket, &readfds);
        max_sd = master_socket;

        //add child sockets to set
        for (int i = 0 ; i < MAXCLIENT ; i++)
        {
            //socket descriptor
            int sd = client_socket[i];

            //if valid socket descriptor then add to read list
            if(sd > 0)
                FD_SET( sd , &readfds);

            //highest file descriptor number, need it for the select function
            if(sd > max_sd)
                max_sd = sd;
        }

        //wait for an activity on one of the sockets , timeout is NULL ,
        //so wait indefinitely
        int activity = select( max_sd + 1 , &readfds , NULL , NULL , NULL);

        if ((activity < 0) && (errno != EINTR))
        {
            printf("select error");
        }

        //If something happened on the master socket ,
        //then its an incoming connection
        if(FD_ISSET(master_socket, &readfds))
        {
            if ((new_socket = accept(master_socket, (struct sockaddr *)&address, (socklen_t*)&addrlen))<0)
            {
                perror("accept");
                exit(EXIT_FAILURE);
            }
            printf("New connection , socket fd is %d , ip is : %s , port : %d \n" , new_socket , inet_ntoa(address.sin_addr) , ntohs(address.sin_port));

            //add new socket to array of sockets
            for (int i = 0; i < MAXCLIENT; i++)
            {
                //if position is empty
                if( client_socket[i] == 0 )
                {
                    client_socket[i] = new_socket;
                    /*printf("Adding to list of sockets as %d\n" , i);*/

                    break;
                }
            }
        }
        //else its some IO operation on some other socket
        for (int i = 0; i < MAXCLIENT; i++)
        {
            int sd = client_socket[i];

            if (FD_ISSET( sd , &readfds))
            {
                //Check if it was for closing , and also read the
                //incoming message
                int valread;
                if ((valread = read( sd , buffer, MAXLINE)) == 0)
                {
                    //Somebody disconnected , get his details and print
                    getpeername(sd , (struct sockaddr*)&address , \
						(socklen_t*)&addrlen);
                    printf("Host disconnected , ip %s , port %d \n" ,
                           inet_ntoa(address.sin_addr) , ntohs(address.sin_port));

                    //Close the socket and mark as 0 in list for reuse
                    DeleteClientSocketID(sd, &count_user);
                    close( sd );
                    client_socket[i] = 0;
                }

                    //Echo back the message that came in
                else
                {

                    //set the string terminating NULL byte on the end
                    //of the data read
                    buffer[valread] = '\0';
                    buffer[strcspn(buffer, "\n")] = 0;
                    printf("Message from client: %s\n", buffer);
                    int command = readCommand(buffer);
                    handleRequest(command, buffer, sd);
                    memset(buffer, '\0', sizeof(buffer));
                }
            }
        }
    }

    pthread_exit(NULL);
    return 0;
}