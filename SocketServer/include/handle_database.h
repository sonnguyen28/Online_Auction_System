//
// Created by Nguyễn Sơn on 26/12/2021.
//

#ifndef SOCKETSERVER_HANDLE_DATABASE_H
#define SOCKETSERVER_HANDLE_DATABASE_H

#endif //SOCKETSERVER_HANDLE_DATABASE_H

#include <stdio.h>
#include <mysql.h>
#include <stdlib.h>
#include "cJSON.h"

#define server "localhost"
#define user "root"
#define psswd "password" /* set connection variables */
#define database "online_auction"

typedef struct {
    int bid_id;
    int lot_id;
    float bid_amount;
    int bidder_user_id;
    time_t created;
} Bid;

typedef struct {
    int lot_id;
    float min_price;
    float winning_bid;
    int winning_bidder;
    char title[255];
    char description[255];
    int owner_id;
    time_t start_time;
    time_t stop_time;
} Lot;

// Danh sach lot dang duoc dang ban
Lot *currentListLots;
int lotTotal;

// Danh sach lot da mua cua 1 User
Lot *listLotsHistory;
int lotTotalHistory;

// Danh sach luu lich su tra gia cua mot lot
Bid *listBidsHistory;
int totalBidsHistory;

MYSQL *conn;
MYSQL_RES *res;
MYSQL_ROW row;

void connect_to_database();
void close_connection();
void finish_with_error(MYSQL *con);
int get_user_id(char username[30]);/*Gets user's id*/
int checkUserID(int userID);// Check userID co ton tai khong
int register_user(char username[30],char password[64]);
int login_user(char username[30], char password[64]);

// handle Lot
int create_lot(float min_price, char title[255], char description[255], int owner_id, char stop_time[20]);
int addLotToList(float min_price, char title[255], char description[255], int owner_id, char stop_time[20]);
void available_lots();
int SearchLot(int lotID);
void printfListLot();
int DeleteLotInList(int lotID);
time_t convert(char *str);
long UnixTimeFromMysqlString(char *s);
char* convertTimeToString(time_t time);
void lotHistory(int winning_bidder);

//handle Bid
int create_bid(int lot_id,float bid_amount, int bidder_user_id);
void bidsHistory(int lotID);
void printListBids();