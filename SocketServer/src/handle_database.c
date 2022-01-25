//
// Created by Nguyễn Sơn on 22/12/2021.
//
#include "handle_database.h"
#include "time.h"
#include "string.h"


void connect_to_database()
{
    conn = mysql_init(NULL);
    /* Connect to database */
    if (!mysql_real_connect(conn, server, user, psswd, database, 0, NULL, 0))
    {
        fprintf(stderr, "%s\n", mysql_error(conn));
        exit(1);
    }else{
        /*printf("Connected to database ................\n");*/
    }

}

void close_connection()   /* close connection */
{
    /*printf("Closing to database ................\n");*/
    mysql_close(conn);
}

void finish_with_error(MYSQL *con)
{
    fprintf(stderr, "%s\n", mysql_error(con));
    mysql_close(con);
    exit(1);
}

int get_user_id(char username[30])/*Gets user's id*/
{
    char query[1024];
    sprintf(query,"SELECT id FROM users WHERE username=\'%s\'",username);
    int id = 0;
    connect_to_database();
    if(mysql_query(conn,query))
    {
        finish_with_error(conn);
        exit(1);
    }
    else{
        res=mysql_store_result(conn);

        if((row = mysql_fetch_row(res))!=NULL )
        {
            id = atoi(row[0]);
        }
        mysql_free_result(res);
    }
    close_connection();
    return id;
}

// Kiem tra userID co ton tai khong
int checkUserID(int userID){
    char query[1024];
    sprintf(query,"SELECT COUNT(id) FROM users WHERE id = %d",userID);
    int countID=0;
    connect_to_database();
    if(mysql_query(conn,query))
    {
        finish_with_error(conn);
        exit(1);
    }
    else{
        res=mysql_store_result(conn);

        if((row = mysql_fetch_row(res))!=NULL )
        {
            countID = atoi(row[0]); // TRUE: countID = 1 // FALSE: countID == 0
        }
        mysql_free_result(res);
    }
    close_connection();
    return countID;
}

int register_user(char username[30],char password[64])
{
    int id = get_user_id(username);
    connect_to_database();
    if (id == 0){
        char query[1024];
        sprintf(query,"INSERT INTO users (username, password) VALUES( \'%s\',\'%s\')",username,password);
        if(mysql_query(conn, query)){
            finish_with_error(conn);
            id = -1;
        } else{
            printf("%s\n\n","user created");
            close_connection();
            id = get_user_id(username);
        }
    } else id = -1;
    return id;
}

int login_user(char username[30], char password[64]){
    char query[1024];
    char username_tmp[30];
    char password_tmp[64];
    int id;
    sprintf(query,"SELECT * FROM users WHERE username=\'%s\' and password=\'%s\'",username,password);
    connect_to_database();
    if(mysql_query(conn, query)){
        finish_with_error(conn);
        id = -2;
    } else{
        res=mysql_store_result(conn);
        row=mysql_fetch_row(res);
        if(res != NULL && row){
            id = atoi(row[0]);
        } else{
            id = -2;
        }
    }
    mysql_free_result(res);
    close_connection();
    return id;
}

/*create  lot */
int create_lot(float min_price, char title[255], char description[255], int owner_id, char *stop_time){
    connect_to_database();
    /*time_t t = get_last_time();*/
    time_t t = time(NULL);
    struct tm tm = *localtime(&t);
    char start_time[20];
    sprintf(start_time, "%.4d-%.2d-%.2d %.2d:%.2d:%.2d", tm.tm_year+1900,tm.tm_mon+1, tm.tm_mday, tm.tm_hour, tm.tm_min, tm.tm_sec);
    //printf("|%s|-|%s|\n", start_time, stop_time);
    char query[1024];
    sprintf(query,"INSERT INTO lots (min_price,title,description,owner_id,start_time ,stop_time) VALUES( %2f,\'%s\',\'%s\',%d,\'%s\', \'%s\')",min_price,title,description,owner_id,start_time,stop_time);
    if (mysql_query(conn,query))
    {
        finish_with_error(conn);
        return -1;
    }else{
        printf("%s\n\n","Lot created !!!");
    }
    int last_lot_id = mysql_insert_id(conn);
    close_connection();
    return last_lot_id;
}

void available_lots(){
    currentListLots = NULL;
    lotTotal = 0;
    connect_to_database();
    if (mysql_query(conn, "SELECT * FROM lots WHERE start_time <= curtime() and stop_time >= curtime() order by start_time"))
    {
        finish_with_error(conn);
        exit(1);
    }else {
        res = mysql_store_result(conn);

        if (res == NULL)
        {
            finish_with_error(conn);
            exit(1);
        }

        while ((row = mysql_fetch_row(res)))
        {
            if(lotTotal == 0) currentListLots = (Lot *) calloc(1, sizeof(Lot));
            else currentListLots = (Lot *) realloc(currentListLots, (lotTotal + 1)*sizeof(Lot));
            currentListLots[lotTotal].lot_id = atoi(row[0]);
            currentListLots[lotTotal].min_price = atof(row[1]);
            currentListLots[lotTotal].winning_bid = row[2] ? atof(row[2]) : 0;
            currentListLots[lotTotal].winning_bidder = row[3] ? atoi(row[3]) : 0;
            strcpy(currentListLots[lotTotal].title, (char *)row[4]);
            strcpy(currentListLots[lotTotal].description, (char *)row[5]);
            currentListLots[lotTotal].owner_id = atoi(row[6]);
            currentListLots[lotTotal].start_time = convert((char *)row[7]);
            currentListLots[lotTotal].stop_time = convert((char *)row[8]);
            strcpy(currentListLots[lotTotal].image_link,(char *)row[9]);
            lotTotal++;
        }
        mysql_free_result(res);
    }
    close_connection();
}

int SearchLot(int lotID){
    for (int i = 0; i < lotTotal; i++) {
        if(currentListLots[i].lot_id == lotID) return i; //return index lot
    }
    return -1; // search that bai
}


time_t convert(char str[20]){
    struct tm tmp;
    time_t t = time(NULL);
    tmp = *localtime(&t);
    strptime(str, "%Y-%m-%d %H:%M:%S", &tmp);
    t = mktime(&tmp);  // t is now your desired time_t
    //tmp = *localtime(&t);
    //printf("now: %d-%d-%d %d:%d:%d\n", tmp.tm_year + 1900, tmp.tm_mon + 1, tmp.tm_mday, tmp.tm_hour, tmp.tm_min, tmp.tm_sec);
    return t;
}

char* convertTimeToString(time_t timet)
{

    char *s = (char *) malloc(1024*sizeof (char));
    struct tm *timeptr;

    timeptr = localtime(&timet);

    strftime(s, 1024, "%Y-%m-%d %H:%M:%S", timeptr);

    return s;
}

time_t UnixTimeFromMysqlString(char *s)
{
    struct tm tmlol;
    strptime(s, "%Y-%m-%d %H:%M:%S", &tmlol);

    time_t t = mktime(&tmlol);
    return t;
}

int addLotToList(float min_price, char title[255], char description[255], int owner_id, char stop_time[20], char image_link[4096]){
    /*time_t t = get_last_time();*/
    time_t t = time(NULL);
    struct tm tm = *localtime(&t);
    printf("|%s|\n", stop_time);

    //printf("%.4d-%.2d-%.2d %.2d:%.2d:%.2d", tm_tmp.tm_year+1900,tm_tmp.tm_mon+1, tm_tmp.tm_mday, tm_tmp.tm_hour, tm_tmp.tm_min, tm_tmp.tm_sec);
    char start_time[20];
    sprintf(start_time, "%.4d-%.2d-%.2d %.2d:%.2d:%.2d", tm.tm_year+1900,tm.tm_mon+1, tm.tm_mday, tm.tm_hour, tm.tm_min, tm.tm_sec);
    int last_lotID = create_lot(min_price, title, description, owner_id, stop_time);
    if(last_lotID < 0){
        printf("create lot that bai");
        return -1;
    } else{
        currentListLots[lotTotal].lot_id = last_lotID;
        currentListLots[lotTotal].min_price = min_price;
        currentListLots[lotTotal].winning_bid = 0;
        currentListLots[lotTotal].winning_bidder = 0;
        strcpy(currentListLots[lotTotal].title, title);
        strcpy(currentListLots[lotTotal].description, description);
        currentListLots[lotTotal].owner_id = owner_id;
        //printf("-----\n");
        currentListLots[lotTotal].start_time = convert(start_time);
        //printf("*****\n");
        //printf("%d\n", (*localtime(&currentListLots[lotTotal].start_time)).tm_hour);
        currentListLots[lotTotal].stop_time = convert(stop_time);
        //printf("%d\n", (*localtime(&currentListLots[lotTotal].stop_time)).tm_hour);
        strcpy(currentListLots[lotTotal].image_link, image_link);
        lotTotal++;
        return last_lotID;
    }
}

int DeleteLotInList(int lotID){
    if(lotTotal <= 0){
        printf("List lot rong !!!\n");
        return -1;
    }
    int pos = SearchLot(lotID);
    printf("|%d|\n", pos);
    if(pos < 0){
        /*printf("Ko tim thay !!!\n");*/
    } else{
        for (int i = pos; i < lotTotal-1; ++i) {
            currentListLots[i] = currentListLots[i+1];
        }
    }
    lotTotal--;
    return  lotTotal;
}

void printfListLot(){
    for (int i = 0; i < lotTotal; i++) {
        printf("|%d-%f-%f-%d-%s-%s-%d-{%s|%s}-{%s}\n", currentListLots[i].lot_id, currentListLots[i].min_price, currentListLots[i].winning_bid,
               currentListLots[i].winning_bidder,currentListLots[i].title, currentListLots[i].description, currentListLots[i].owner_id,
               convertTimeToString(currentListLots[i].start_time), convertTimeToString(currentListLots[i].stop_time), currentListLots[i].image_link);
    }
}

int create_bid(int lot_id,float bid_amount, int bidder_user_id) /*create  bid */
{
    int last_bidID;
    char query[1024];
    char query_lot[1024];
    char query_check_update[1024];
    sprintf(query_lot,"UPDATE lots SET winning_bid=%.2f, winning_bidder=%d WHERE id = %d and %.2f > min_price and %.2f > winning_bid and (SELECT curtime()) >= start_time and (SELECT curtime()) <= stop_time",bid_amount,bidder_user_id, lot_id, bid_amount, bid_amount);
    sprintf(query_check_update,"SELECT * FROM lots WHERE id=%d and winning_bid=%.2f and winning_bidder=%d",lot_id, bid_amount,bidder_user_id);
    connect_to_database();
    if (mysql_query(conn,query_lot))
    {
        finish_with_error(conn);
        exit(1);
    }
    else{
        if(mysql_query(conn, query_check_update)){
            finish_with_error(conn);
            exit(1);
        } else{
            res = mysql_store_result(conn);
            if(mysql_num_rows(res) > 0){
                printf("Update thanh cong -> create bid\n");
                sprintf(query,"INSERT INTO bids(lot_id, bid_amount, bidder_user_id, created) SELECT id, winning_bid, winning_bidder, CURRENT_TIMESTAMP() FROM lots WHERE id = %d",lot_id);
                if (mysql_query(conn, query)){
                    finish_with_error(conn);
                    exit(1);
                } else{
                    printf("created Bid !\n");
                    last_bidID = mysql_insert_id(conn);
                    int index_lot = SearchLot(lot_id);
                    currentListLots[index_lot].winning_bid = bid_amount;
                    currentListLots[index_lot].winning_bidder = bidder_user_id;
                }
            } else{
                printf("Create bid that bai !\n");
                last_bidID = -1;
            }
        }
    }
    close_connection();
    return last_bidID;
}

void bidsHistory(int lotID){
    char query[1024];
    listBidsHistory = NULL;
    totalBidsHistory = 0;
    sprintf(query,"SELECT * FROM bids WHERE lot_id = %d ORDER BY bid_amount DESC", lotID);
    connect_to_database();
    if (mysql_query(conn,query))
    {
        finish_with_error(conn);
        exit(1);
    }else {
        res = mysql_store_result(conn);

        if (res == NULL)
        {
            finish_with_error(conn);
            exit(1);
        }

        while ((row = mysql_fetch_row(res)))
        {
            if(totalBidsHistory == 0) listBidsHistory = (Bid *) calloc(1, sizeof(Bid));
            else listBidsHistory = (Bid *) realloc(listBidsHistory, (totalBidsHistory + 1)*sizeof(Bid));
            listBidsHistory[totalBidsHistory].bid_id = atoi(row[0]);
            listBidsHistory[totalBidsHistory].lot_id = atoi(row[1]);
            listBidsHistory[totalBidsHistory].bid_amount = atof(row[2]);
            listBidsHistory[totalBidsHistory].bidder_user_id = atoi(row[3]);
            listBidsHistory[totalBidsHistory].created = convert((char *)row[4]);
            totalBidsHistory++;
        }
        mysql_free_result(res);
    }
    close_connection();
}

void lotHistory(int winning_bidder){
    char query[1024];
    listLotsHistory = NULL;
    lotTotalHistory = 0;
    sprintf(query,"SELECT * FROM lots WHERE stop_time <= curtime() AND winning_bidder = %d",winning_bidder);
    connect_to_database();
    if (mysql_query(conn,query))
    {
        finish_with_error(conn);
        exit(1);
    }else {
        res = mysql_store_result(conn);

        if (res == NULL)
        {
            finish_with_error(conn);
            exit(1);
        }

        while ((row = mysql_fetch_row(res)))
        {
            if(lotTotal == 0) listLotsHistory = (Lot *) calloc(1, sizeof(Lot));
            else listLotsHistory = (Lot *) realloc(listLotsHistory, (lotTotalHistory + 1)*sizeof(Lot));
            listLotsHistory[lotTotalHistory].lot_id = atoi(row[0]);
            listLotsHistory[lotTotalHistory].min_price = atof(row[1]);
            listLotsHistory[lotTotalHistory].winning_bid = row[2] ? atof(row[2]) : 0;
            listLotsHistory[lotTotalHistory].winning_bidder = row[3] ? atoi(row[3]) : 0;
            strcpy(listLotsHistory[lotTotalHistory].title, (char *)row[4]);
            strcpy(listLotsHistory[lotTotalHistory].description, (char *)row[5]);
            listLotsHistory[lotTotalHistory].owner_id = atoi(row[6]);
            listLotsHistory[lotTotalHistory].start_time = convert((char *)row[7]);
            listLotsHistory[lotTotalHistory].stop_time = convert((char *)row[8]);
            strcpy(listLotsHistory[lotTotalHistory].image_link,(char *)row[9]);
            lotTotalHistory++;
        }
        mysql_free_result(res);
    }
    close_connection();
}

void printListBids(){
    for (int i = 0; i < totalBidsHistory; ++i) {
        printf("|%d - %.2f - %d - %d|\n", listBidsHistory[i].bid_id, listBidsHistory[i].lot_id, listBidsHistory[i].bid_amount, listBidsHistory[i].bidder_user_id);
    }
}
