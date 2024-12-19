-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Erstellungszeit: 15. Dez 2024 um 21:07
-- Server-Version: 10.4.32-MariaDB
-- PHP-Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `lebenslauf-creator`
--

-- --------------------------------------------------------

DROP TABLE IF EXISTS `resume_versions`;
DROP TABLE IF EXISTS `users`;

--
-- Tabellenstruktur für Tabelle `users`
--

CREATE TABLE `users` (
                         `id` INT(11) NOT NULL AUTO_INCREMENT,
                         `Full_Name` VARCHAR(255) NOT NULL,
                         `Email` VARCHAR(255) NOT NULL UNIQUE,
                         `Password` VARCHAR(255) NOT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `users`
--

INSERT INTO `users` (`Full_Name`, `Email`, `Password`) VALUES
    ('Darko Kolak', 'darko.kolak@yahoo.com', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `resume_versions`
--

CREATE TABLE `resume_versions` (
                                   `id` INT(11) NOT NULL AUTO_INCREMENT,
                                   `user_id` INT NOT NULL,
                                   `version_number` INT NOT NULL,
                                   `first_name` VARCHAR(255),
                                   `last_name` VARCHAR(255),
                                   `gender` VARCHAR(50),
                                   `birth_place` VARCHAR(255),
                                   `birth_date` DATE,
                                   `city` VARCHAR(255),
                                   `address` VARCHAR(255),
                                   `postal_code` VARCHAR(20),
                                   `nationality` VARCHAR(100),
                                   `phone_number` VARCHAR(50),
                                   `email` VARCHAR(255),
                                   `experience` TEXT,
                                   `education` TEXT,
                                   `image_base64` LONGTEXT,
                                   `last_updated` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`),
                                   FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `users`
--

--
-- Indizes für die Tabelle `resume_versions`
--

--
-- Constraints der exportierten Tabellen
--

--
-- Constraints für Tabelle `resume_versions`
--


COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;