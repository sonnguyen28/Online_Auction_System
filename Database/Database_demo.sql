-- MySQL dump 10.13  Distrib 8.0.26, for macos11 (x86_64)
--
-- Host: localhost    Database: online_auction
-- ------------------------------------------------------
-- Server version	8.0.27

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bids`
--

DROP TABLE IF EXISTS `bids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bids` (
  `id` int NOT NULL AUTO_INCREMENT,
  `lot_id` int DEFAULT NULL,
  `bid_amount` decimal(12,2) DEFAULT NULL,
  `bidder_user_id` int DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `lot_bid_idx` (`lot_id`),
  KEY `user_bid_id_idx` (`bidder_user_id`),
  CONSTRAINT `lot_bid_id` FOREIGN KEY (`lot_id`) REFERENCES `lots` (`id`),
  CONSTRAINT `user_bid_id` FOREIGN KEY (`bidder_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bids`
--

LOCK TABLES `bids` WRITE;
/*!40000 ALTER TABLE `bids` DISABLE KEYS */;
INSERT INTO `bids` VALUES (93,684,501.00,2,'2022-02-15 23:44:14'),(94,683,2001.00,1,'2022-02-15 23:44:21'),(95,682,1001.00,3,'2022-02-15 23:44:30'),(96,685,201.00,2,'2022-02-15 23:47:30'),(97,685,205.00,1,'2022-02-15 23:47:37'),(98,685,206.00,2,'2022-02-15 23:47:43'),(99,685,250.00,1,'2022-02-15 23:47:49');
/*!40000 ALTER TABLE `bids` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `images`
--

DROP TABLE IF EXISTS `images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `images` (
  `id` int NOT NULL AUTO_INCREMENT,
  `lot_id` int NOT NULL,
  `image_name` varchar(255) NOT NULL,
  `image_size` int NOT NULL,
  `path_image` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `image_lot_id_idx` (`lot_id`),
  CONSTRAINT `image_lot_id` FOREIGN KEY (`lot_id`) REFERENCES `lots` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=676 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `images`
--

LOCK TABLES `images` WRITE;
/*!40000 ALTER TABLE `images` DISABLE KEYS */;
INSERT INTO `images` VALUES (653,681,'airpod1.jpg',2073,'./image/681/airpod1.jpg'),(654,681,'airpod2.jpg',2512,'./image/681/airpod2.jpg'),(655,681,'airpod3.jpg',2368,'./image/681/airpod3.jpg'),(656,681,'airpod4.jpg',2965,'./image/681/airpod4.jpg'),(657,682,'iphone1.jpg',4433,'./image/682/iphone1.jpg'),(658,682,'iphone2.jpg',4330,'./image/682/iphone2.jpg'),(659,682,'iphone3.jpg',2615,'./image/682/iphone3.jpg'),(660,682,'iphone4.jpg',3792,'./image/682/iphone4.jpg'),(661,682,'iphone5.jpg',4417,'./image/682/iphone5.jpg'),(662,683,'macbook1.jpg',5394,'./image/683/macbook1.jpg'),(663,683,'macbook2.jpg',4974,'./image/683/macbook2.jpg'),(664,683,'macbook3.jpg',6877,'./image/683/macbook3.jpg'),(665,683,'macbook4.jpg',6512,'./image/683/macbook4.jpg'),(666,683,'macbook5.jpg',3315,'./image/683/macbook5.jpg'),(667,684,'chanel1.jpg',3698,'./image/684/chanel1.jpg'),(668,684,'chanel2.jpg',4847,'./image/684/chanel2.jpg'),(669,684,'chanel3.jpg',3884,'./image/684/chanel3.jpg'),(670,684,'chanel4.jpg',3376,'./image/684/chanel4.jpg'),(671,685,'nike1.jpg',2401,'./image/685/nike1.jpg'),(672,685,'nike2.jpg',3327,'./image/685/nike2.jpg'),(673,685,'nike3.jpg',3168,'./image/685/nike3.jpg'),(674,685,'nike4.jpg',5765,'./image/685/nike4.jpg'),(675,685,'nike5.jpg',3474,'./image/685/nike5.jpg');
/*!40000 ALTER TABLE `images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lots`
--

DROP TABLE IF EXISTS `lots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lots` (
  `id` int NOT NULL AUTO_INCREMENT,
  `min_price` decimal(12,2) DEFAULT NULL,
  `winning_bid` decimal(12,2) DEFAULT NULL,
  `winning_bidder` int DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `owner_id` int DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `stop_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=686 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lots`
--

LOCK TABLES `lots` WRITE;
/*!40000 ALTER TABLE `lots` DISABLE KEYS */;
INSERT INTO `lots` VALUES (681,100.00,0.00,0,'airpod','tai nghe khong day',1,'2022-02-15 23:40:18','2022-02-17 12:00:00'),(682,1000.00,1001.00,3,'iphone 12','smart phone',2,'2022-02-15 23:41:42','2022-02-17 12:00:00'),(683,2000.00,2001.00,1,'macbook','laptop',3,'2022-02-15 23:42:26','2022-02-17 12:00:00'),(684,500.00,501.00,2,'perfume','nuoc hoa chanel',1,'2022-02-15 23:44:01','2022-02-17 12:00:00'),(685,200.00,250.00,1,'nike air max','giay the thao nike',3,'2022-02-15 23:47:20','2022-02-15 23:48:00');
/*!40000 ALTER TABLE `lots` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'sonnguyen','password'),(2,'baluan','password'),(3,'vanhong','password');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-02-15 23:50:19
