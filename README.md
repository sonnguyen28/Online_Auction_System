# Online Auction System
The online auction system is built with the client-server model.

- Server is built in c, Client is built in Java
- People everywhere can post their products for sale
- Participate in the auction and bid the highest price to buy the products that are for sale
- View the auction history of a product
- View purchased products

# Screenshots
### _Login_

![Login](https://github.com/sonnguyen28/Online_Auction_System/blob/main/Database/image_readme/login.png?raw=true)

### _Register_

![Register](https://github.com/sonnguyen28/Online_Auction_System/blob/main/Database/image_readme/register.png?raw=true)

### _Home_
![Home](https://github.com/sonnguyen28/Online_Auction_System/blob/main/Database/image_readme/home.png?raw=true)

### _Item detail_
![Item detail](https://github.com/sonnguyen28/Online_Auction_System/blob/main/Database/image_readme/item_detail.png?raw=true)

### _Bid list_
![Bid list](https://github.com/sonnguyen28/Online_Auction_System/blob/main/Database/image_readme/bid_list.png?raw=true)

### _Sell_
![Sell](https://github.com/sonnguyen28/Online_Auction_System/blob/main/Database/image_readme/sell.png?raw=true)

### _Purchase history_
![Purchase history](https://github.com/sonnguyen28/Online_Auction_System/blob/main/Database/image_readme/history.png?raw=true)



# C make config file
## _Ubuntu_

```sh
cmake_minimum_required(VERSION 3.21)
project(SocketServer C) set(CMAKE_C_STANDARD 99) #
# Add mysql lib
#
set(PROJECT_LINK_LIBS libmysqlclient.so)
set(CMAKE_C_FLAGS -pthread)
link_directories(/usr/lib/x86_64-linux-gnu) #
# Thêm thư mục chứa các file header (.h)
#
include_directories(include /usr/include/mariadb) #
# Thêm một tập các file source
#
file(GLOB SOURCES "src/*.c") add_executable(SocketServer ${​​​​​SOURCES}​​​​​) target_link_libraries(SocketServer ${​​​​​PROJECT_LINK_LIBS}​​​​​)
# Cmake file of MacOS
cmake_minimum_required(VERSION 3.21)
project(SocketServer C)
set(CMAKE_C_STANDARD 99)
#
# Add mysql lib
#
set (PROJECT_LINK_LIBS libmysqlclient.a)
link_directories(/usr/local/opt/mysql-client/lib)
#
# Thêm thư mục chứa các file header (.h)
#
include_directories(include /usr/local/opt/mysql-client/include/mysql)
#
# Thêm một tập các file source
#
file(GLOB SOURCES "src/*.c")
add_executable(SocketServer ${SOURCES})
target_link_libraries(SocketServer ${PROJECT_LINK_LIBS})
```
## _MacOS_
```sh
cmake_minimum_required(VERSION 3.21)
project(SocketServer C)

set(CMAKE_C_STANDARD 99)

#
# Add mysql lib
#
set (PROJECT_LINK_LIBS libmysqlclient.a)
link_directories(/usr/local/opt/mysql-client/lib)

#
# Thêm thư mục chứa các file header (.h)
#
include_directories(include /usr/local/opt/mysql-client/include/mysql)

#
# Thêm một tập các file source
#
file(GLOB SOURCES "src/*.c")

add_executable(SocketServer ${SOURCES})

target_link_libraries(SocketServer ${PROJECT_LINK_LIBS})
```
