-- MySQL dump 10.13  Distrib 5.1.41, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: move2alf
-- ------------------------------------------------------
-- Server version	5.1.41-3ubuntu12.7

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `configuredAction`
--

DROP TABLE IF EXISTS `configuredAction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `configuredAction` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `actionClassName` varchar(255) NOT NULL,
  `appliedConfiguredActionOnSuccessId` int(10) unsigned DEFAULT NULL,
  `appliedConfiguredActionOnFailureId` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuredAction`
--

LOCK TABLES `configuredAction` WRITE;
/*!40000 ALTER TABLE `configuredAction` DISABLE KEYS */;
/*!40000 ALTER TABLE `configuredAction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configuredActionConfiguredSourceSink`
--

DROP TABLE IF EXISTS `configuredActionConfiguredSourceSink`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `configuredActionConfiguredSourceSink` (
  `configuredActionId` int(10) unsigned NOT NULL,
  `configuredSourceSinkId` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuredActionConfiguredSourceSink`
--

LOCK TABLES `configuredActionConfiguredSourceSink` WRITE;
/*!40000 ALTER TABLE `configuredActionConfiguredSourceSink` DISABLE KEYS */;
/*!40000 ALTER TABLE `configuredActionConfiguredSourceSink` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configuredActionParameter`
--

DROP TABLE IF EXISTS `configuredActionParameter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `configuredActionParameter` (
  `configuredActionId` int(10) unsigned NOT NULL,
  `name` varchar(50) NOT NULL,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY (`configuredActionId`,`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuredActionParameter`
--

LOCK TABLES `configuredActionParameter` WRITE;
/*!40000 ALTER TABLE `configuredActionParameter` DISABLE KEYS */;
/*!40000 ALTER TABLE `configuredActionParameter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configuredReport`
--

DROP TABLE IF EXISTS `configuredReport`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `configuredReport` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `reportClassName` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuredReport`
--

LOCK TABLES `configuredReport` WRITE;
/*!40000 ALTER TABLE `configuredReport` DISABLE KEYS */;
/*!40000 ALTER TABLE `configuredReport` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configuredReportConfiguredSourceSink`
--

DROP TABLE IF EXISTS `configuredReportConfiguredSourceSink`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `configuredReportConfiguredSourceSink` (
  `configuredReportId` int(10) unsigned NOT NULL,
  `configuredSourceSinkId` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuredReportConfiguredSourceSink`
--

LOCK TABLES `configuredReportConfiguredSourceSink` WRITE;
/*!40000 ALTER TABLE `configuredReportConfiguredSourceSink` DISABLE KEYS */;
/*!40000 ALTER TABLE `configuredReportConfiguredSourceSink` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configuredReportParameter`
--

DROP TABLE IF EXISTS `configuredReportParameter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `configuredReportParameter` (
  `configuredReportId` int(10) unsigned NOT NULL,
  `name` varchar(50) NOT NULL,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY (`configuredReportId`,`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuredReportParameter`
--

LOCK TABLES `configuredReportParameter` WRITE;
/*!40000 ALTER TABLE `configuredReportParameter` DISABLE KEYS */;
/*!40000 ALTER TABLE `configuredReportParameter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configuredSourceSink`
--

DROP TABLE IF EXISTS `configuredSourceSink`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `configuredSourceSink` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `sourceSinkClassName` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuredSourceSink`
--

LOCK TABLES `configuredSourceSink` WRITE;
/*!40000 ALTER TABLE `configuredSourceSink` DISABLE KEYS */;
/*!40000 ALTER TABLE `configuredSourceSink` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configuredSourceSinkParameter`
--

DROP TABLE IF EXISTS `configuredSourceSinkParameter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `configuredSourceSinkParameter` (
  `configuredSourceSinkId` int(10) unsigned NOT NULL,
  `name` varchar(50) NOT NULL,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY (`configuredSourceSinkId`,`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuredSourceSinkParameter`
--

LOCK TABLES `configuredSourceSinkParameter` WRITE;
/*!40000 ALTER TABLE `configuredSourceSinkParameter` DISABLE KEYS */;
/*!40000 ALTER TABLE `configuredSourceSinkParameter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cycle`
--

DROP TABLE IF EXISTS `cycle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cycle` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `jobId` int(10) unsigned NOT NULL,
  `startDateTime` datetime NOT NULL,
  `endDateTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

CREATE INDEX `cycleJobIdIdx` ON `cycle` (`jobId`);

--
-- Dumping data for table `cycle`
--

LOCK TABLES `cycle` WRITE;
/*!40000 ALTER TABLE `cycle` DISABLE KEYS */;
/*!40000 ALTER TABLE `cycle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `job`
--

DROP TABLE IF EXISTS `job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `job` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) NOT NULL,
  `creatorId` int(10) unsigned NOT NULL,
  `creationDateTime` datetime NOT NULL,
  `lastModifyDateTime` datetime NOT NULL,
  `firstConfiguredActionId` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `job`
--

LOCK TABLES `job` WRITE;
/*!40000 ALTER TABLE `job` DISABLE KEYS */;
/*!40000 ALTER TABLE `job` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `processedDocument`
--

DROP TABLE IF EXISTS `processedDocument`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `processedDocument` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `cycleId` int(10) unsigned NOT NULL,
  `name` varchar(255) NOT NULL,
  `processingDateTime` datetime NOT NULL,
  `status` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

CREATE INDEX `processedDocumentCycleIdIdx` ON `processedDocument` (`cycleId`);

--
-- Dumping data for table `processedDocument`
--

LOCK TABLES `processedDocument` WRITE;
/*!40000 ALTER TABLE `processedDocument` DISABLE KEYS */;
/*!40000 ALTER TABLE `processedDocument` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `processedDocumentParameter`
--

DROP TABLE IF EXISTS `processedDocumentParameter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `processedDocumentParameter` (
  `processedDocumentId` int(10) unsigned NOT NULL,
  `configuredActionId` int(10) unsigned NOT NULL,
  `reportPropertyName` varchar(50) NOT NULL,
  `value` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

CREATE INDEX `processedDocumentIdIdx` ON `processedDocumentParameter` (`processedDocumentId`);

--
-- Dumping data for table `processedDocumentParameter`
--

LOCK TABLES `processedDocumentParameter` WRITE;
/*!40000 ALTER TABLE `processedDocumentParameter` DISABLE KEYS */;
/*!40000 ALTER TABLE `processedDocumentParameter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `runningAction`
--

DROP TABLE IF EXISTS `runningAction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `runningAction` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `cycleId` int(10) unsigned NOT NULL,
  `configuredActionId` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `runningAction`
--

LOCK TABLES `runningAction` WRITE;
/*!40000 ALTER TABLE `runningAction` DISABLE KEYS */;
/*!40000 ALTER TABLE `runningAction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schedule`
--

DROP TABLE IF EXISTS `schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schedule` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `jobId` int(10) unsigned NOT NULL,
  `creatorId` int(10) unsigned NOT NULL,
  `creationDateTime` datetime NOT NULL,
  `lastModifyDateTime` datetime NOT NULL,
  `quartzScheduling` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedule`
--

LOCK TABLES `schedule` WRITE;
/*!40000 ALTER TABLE `schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `threadPool`
--

DROP TABLE IF EXISTS `threadPool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `threadPool` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `size` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `threadPool`
--

LOCK TABLES `threadPool` WRITE;
/*!40000 ALTER TABLE `threadPool` DISABLE KEYS */;
/*!40000 ALTER TABLE `threadPool` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userPswd`
--

DROP TABLE IF EXISTS `userPswd`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userPswd` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `userName` varchar(30) NOT NULL,
  `password` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `userName` (`userName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
INSERT INTO `userPswd` VALUES (1, 'admin', '21232f297a57a5a743894a0e4a801fc3');

--
-- Dumping data for table `userPswd`
--

LOCK TABLES `userPswd` WRITE;
/*!40000 ALTER TABLE `userPswd` DISABLE KEYS */;
/*!40000 ALTER TABLE `userPswd` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userRole`
--

DROP TABLE IF EXISTS `userRole`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userRole` (
  `userName` varchar(30) NOT NULL,
  `role` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
INSERT INTO `userRole` VALUES ('admin', 'CONSUMER');
INSERT INTO `userRole` VALUES ('admin', 'SCHEDULE_ADMIN');
INSERT INTO `userRole` VALUES ('admin', 'JOB_ADMIN');
INSERT INTO `userRole` VALUES ('admin', 'SYSTEM_ADMIN');
--
-- Dumping data for table `userRole`
--

LOCK TABLES `userRole` WRITE;
/*!40000 ALTER TABLE `userRole` DISABLE KEYS */;
/*!40000 ALTER TABLE `userRole` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2010-12-09 17:03:13
