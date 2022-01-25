//
// Created by Nguyễn Sơn on 22/12/2021.
//
#include <sys/socket.h>
#include "handle_client.h"

int AddClient(int userID, int socketID, int *countUser){
    if(*countUser >= MAXCLIENT){

        printf("MAX client !!!!");
        return -1;
    }
    int pos = 0, i;
    for (i = 0; i < *countUser; i++) {
        if(listUser[i].user_id > userID){
            pos = i;
            break;
        }
    }
    if(i == *countUser){
        pos = *countUser;
    }
    for (int j = *countUser; j > pos; j--) {
        listUser[j].user_id = listUser[j-1].user_id;
        listUser[j].socket_id = listUser[j-1].socket_id;
    }

    listUser[pos].user_id = userID;
    listUser[pos].socket_id = socketID;

    (*countUser)++;

    return (*countUser);
}


int SearchClientUserID(int UserID, int countUser){
    for (int i = 0; i < countUser; ++i) {
        if(listUser[i].user_id == UserID) return i;
    }
    return -1;
}

int DeleteClientUserID(int userID, int *countUser){
    if((*countUser) <= 0){
        printf("List client rong !!!");
        return -1;
    }
    int pos = SearchClientUserID(userID, *countUser);

    if(pos < 0){
        /*printf("Ko tim thay !!!\n");*/
    } else {

        for (int i = pos; i < (*countUser) - 1; ++i) {
            listUser[i].user_id = listUser[i + 1].user_id;
            listUser[i].socket_id = listUser[i + 1].socket_id;
        }
    }

    (*countUser)--;
    return (*countUser);
}

int SearchClientSocketID(int socketID, int countUser){
    for (int i = 0; i < countUser; ++i) {
        if(listUser[i].socket_id == socketID) return i;
    }
    return -1;
}

int DeleteClientSocketID(int socketID, int *countUser){
    if((*countUser) <= 0){
        /*printf("List client rong !!!");*/
        return -1;
    }
    int pos = SearchClientSocketID(socketID, *countUser);

    if(pos < 0){
        /*printf("Ko tim thay !!!\n");*/
    } else {

        for (int i = pos; i < (*countUser) - 1; ++i) {
            listUser[i].user_id = listUser[i + 1].user_id;
            listUser[i].socket_id = listUser[i + 1].socket_id;
        }
    }

    (*countUser)--;
    return (*countUser);
}

void CloseSocket(int socketID){
    struct sockaddr_in address;
    int addrlen;
    for (int i = 0; i < MAXCLIENT; ++i) {
        if(client_socket[i] == socketID){
            getpeername(socketID , (struct sockaddr*)&address , \
						(socklen_t*)&addrlen);
            printf("Host disconnected , ip %s , port %d \n" ,
                   inet_ntoa(address.sin_addr) , ntohs(address.sin_port));

            //Close the socket and mark as 0 in list for reuse
            client_socket[i] = 0;
            close(socketID);
        }
    }
}
