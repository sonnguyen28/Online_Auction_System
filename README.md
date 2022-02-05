# Online_Auction_System

# Cmake file of Ubuntu

cmake_minimum_required(VERSION 3.21)

project(SocketServer C) set(CMAKE_C_STANDARD 99) #

# Add mysql lib

#

set (PROJECT_LINK_LIBS libmysqlclient.so)

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