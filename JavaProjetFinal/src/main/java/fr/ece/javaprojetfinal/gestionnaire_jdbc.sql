-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 07, 2025 at 05:10 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gestionnaire_jdbc`
--

-- --------------------------------------------------------

--
-- Table structure for table `commentaires`
--

CREATE TABLE `commentaires` (
  `ID` int(11) NOT NULL,
  `User_id` int(11) NOT NULL,
  `Tache_id` int(11) NOT NULL,
  `description` text NOT NULL,
  `Date_creation` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `commentaires`
--

INSERT INTO `commentaires` (`ID`, `User_id`, `Tache_id`, `description`, `Date_creation`) VALUES
(1, 2, 1, 'J\'ai commencé l\'intégration de Stripe. Configuration de base terminée.', '2025-12-01'),
(2, 3, 1, 'N\'oublie pas de tester avec des cartes de test avant de passer en production.', '2025-12-02'),
(3, 2, 2, 'Bug identifié : problème de gestion du state Redux. En cours de correction.', '2025-12-03'),
(4, 4, 4, 'Les notifications push fonctionnent bien sur iOS 16+', '2025-12-04'),
(5, 2, 9, 'JWT implémenté avec refresh token. À valider par l\'équipe.', '2025-12-05');

-- --------------------------------------------------------

--
-- Table structure for table `projet`
--

CREATE TABLE `projet` (
  `ID` int(11) NOT NULL,
  `Nom` varchar(255) NOT NULL,
  `Description` text DEFAULT NULL,
  `Date_creation` date NOT NULL,
  `Date_echeance` date DEFAULT NULL,
  `Responsable` int(11) DEFAULT NULL,
  `Statut` varchar(50) DEFAULT 'En cours'
) ;

--
-- Dumping data for table `projet`
--

INSERT INTO `projet` (`ID`, `Nom`, `Description`, `Date_creation`, `Date_echeance`, `Responsable`, `Statut`) VALUES
(1, 'Refonte Site E-commerce', 'Modernisation complète de la plateforme e-commerce avec nouvelles fonctionnalités', '2025-10-01', '2025-12-15', 1, 'En cours'),
(2, 'Application Mobile iOS', 'Développement d\'une application mobile native pour iOS', '2025-09-15', '2025-12-20', 1, 'En cours'),
(3, 'Dashboard Analytics', 'Création d\'un tableau de bord analytique avec visualisation de données', '2025-12-05', '2026-01-28', 1, 'Planification'),
(4, 'API REST v2.0', 'Refactorisation et optimisation de l\'API REST', '2025-09-20', '2025-12-10', 1, 'En cours');

-- --------------------------------------------------------

--
-- Table structure for table `taches`
--

CREATE TABLE `taches` (
  `ID` int(11) NOT NULL,
  `Nom` varchar(255) NOT NULL,
  `Description` text DEFAULT NULL,
  `Date_creation` date NOT NULL,
  `Date_echeances` date DEFAULT NULL,
  `Id_projet` int(11) NOT NULL,
  `Statut` varchar(50) DEFAULT 'À faire',
  `Priorite` varchar(50) DEFAULT 'Moyen'
) ;
' ||
    CREATE TABLE IF NOT EXISTS events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    is_full_day BOOLEAN DEFAULT FALSE,
    admin_id INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT NULL
);
--
-- Dumping data for table `taches`
--

INSERT INTO `taches` (`ID`, `Nom`, `Description`, `Date_creation`, `Date_echeances`, `Id_projet`, `Statut`, `Priorite`) VALUES
(1, 'Développer le système de paiement', 'Intégration du système de paiement Stripe', '2025-11-01', '2025-12-08', 1, 'En cours', 'Urgent'),
(2, 'Corriger bug panier d\'achat', 'Résoudre le problème d\'affichage du panier', '2025-11-15', '2025-12-07', 1, 'En cours', 'Urgent'),
(3, 'Intégration Stripe Payment', 'Configuration complète de Stripe', '2025-10-15', '2025-12-03', 1, 'Terminé', 'Urgent'),
(4, 'Intégrer les notifications push', 'Mise en place des notifications push avec Firebase', '2025-10-01', '2025-12-11', 2, 'En cours', 'Moyen'),
(5, 'Designer l\'écran de connexion', 'Créer l\'interface de l\'écran de connexion', '2025-10-10', '2025-12-10', 2, 'En cours', 'Moyen'),
(6, 'Setup environnement de test', 'Configuration de l\'environnement TestFlight', '2025-11-01', '2025-12-13', 2, 'À faire', 'Moyen'),
(7, 'Créer les wireframes de l\'interface', 'Conception des maquettes de l\'interface', '2025-12-01', '2025-12-12', 3, 'À faire', 'Moyen'),
(8, 'Créer la base de données', 'Mise en place de la structure BDD', '2025-11-20', '2025-12-04', 3, 'Terminé', 'Moyen'),
(9, 'Implémenter l\'authentification JWT', 'Mise en place de l\'authentification par token', '2025-10-05', '2025-12-10', 4, 'À faire', 'Urgent'),
(10, 'Optimiser les requêtes SQL', 'Amélioration des performances des requêtes', '2025-10-20', '2025-12-09', 4, 'En cours', 'Urgent'),
(11, 'Configurer le serveur de production', 'Déploiement sur le serveur de production', '2025-11-01', '2025-12-05', 4, 'Terminé', 'Urgent'),
(12, 'Tests unitaires API', 'Écriture des tests unitaires pour l\'API', '2025-11-10', '2025-12-14', 4, 'En cours', 'Faible'),
(13, 'Rédiger la documentation technique', 'Documentation complète de l\'API', '2025-11-15', '2025-12-15', 4, 'À faire', 'Faible');

-- --------------------------------------------------------

--
-- Table structure for table `utilisateur`
--

CREATE TABLE `utilisateur` (
  `ID` int(11) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `Address` text DEFAULT NULL,
  `Role` int(11) NOT NULL,
  `MDP` varchar(255) NOT NULL
) ;

--
-- Dumping data for table `utilisateur`
--

INSERT INTO `utilisateur` (`ID`, `Name`, `Address`, `Role`, `MDP`) VALUES
(1, 'Admin Principal', 'Paris, France', 1, '$2y$10$adminHashedPassword123'),
(2, 'Jean Dupont', 'Lyon, France', 0, '$2y$10$userHashedPassword123'),
(3, 'Marie Petit', 'Marseille, France', 0, '$2y$10$userHashedPassword456'),
(4, 'Sophie Laurent', 'Toulouse, France', 0, '$2y$10$userHashedPassword789'),
(5, 'Thomas Colin', 'Bordeaux, France', 0, '$2y$10$userHashedPassword321');

-- --------------------------------------------------------

--
-- Table structure for table `utilisateurs_projet`
--

CREATE TABLE `utilisateurs_projet` (
  `ID` int(11) NOT NULL,
  `User_id` int(11) NOT NULL,
  `Projet_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `utilisateurs_projet`
--

INSERT INTO `utilisateurs_projet` (`ID`, `User_id`, `Projet_id`) VALUES
(1, 2, 1),
(2, 2, 2),
(3, 2, 3),
(4, 2, 4),
(5, 3, 1),
(6, 3, 3),
(7, 4, 1),
(8, 4, 2),
(9, 4, 3),
(10, 5, 1),
(11, 5, 4);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `commentaires`
--
ALTER TABLE `commentaires`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `idx_comment_user` (`User_id`),
  ADD KEY `idx_comment_tache` (`Tache_id`),
  ADD KEY `idx_comment_date` (`Date_creation`);

--
-- Indexes for table `projet`
--
ALTER TABLE `projet`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `idx_projet_responsable` (`Responsable`),
  ADD KEY `idx_projet_statut` (`Statut`),
  ADD KEY `idx_projet_date_echeance` (`Date_echeance`);

--
-- Indexes for table `taches`
--
ALTER TABLE `taches`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `idx_tache_projet` (`Id_projet`),
  ADD KEY `idx_tache_statut` (`Statut`),
  ADD KEY `idx_tache_priorite` (`Priorite`),
  ADD KEY `idx_tache_date_echeance` (`Date_echeances`);

--
-- Indexes for table `utilisateur`
--
ALTER TABLE `utilisateur`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `idx_utilisateur_role` (`Role`),
  ADD KEY `idx_utilisateur_name` (`Name`);

--
-- Indexes for table `utilisateurs_projet`
--
ALTER TABLE `utilisateurs_projet`
  ADD PRIMARY KEY (`ID`),
  ADD UNIQUE KEY `uq_user_projet` (`User_id`,`Projet_id`),
  ADD KEY `idx_up_user` (`User_id`),
  ADD KEY `idx_up_projet` (`Projet_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `commentaires`
--
ALTER TABLE `commentaires`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `projet`
--
ALTER TABLE `projet`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `taches`
--
ALTER TABLE `taches`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `utilisateur`
--
ALTER TABLE `utilisateur`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `utilisateurs_projet`
--
ALTER TABLE `utilisateurs_projet`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `commentaires`
--
ALTER TABLE `commentaires`
  ADD CONSTRAINT `fk_comment_tache` FOREIGN KEY (`Tache_id`) REFERENCES `taches` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_comment_user` FOREIGN KEY (`User_id`) REFERENCES `utilisateur` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `projet`
--
ALTER TABLE `projet`
  ADD CONSTRAINT `fk_projet_responsable` FOREIGN KEY (`Responsable`) REFERENCES `utilisateur` (`ID`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `taches`
--
ALTER TABLE `taches`
  ADD CONSTRAINT `fk_tache_projet` FOREIGN KEY (`Id_projet`) REFERENCES `projet` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `utilisateurs_projet`
--
ALTER TABLE `utilisateurs_projet`
  ADD CONSTRAINT `fk_up_projet` FOREIGN KEY (`Projet_id`) REFERENCES `projet` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_up_user` FOREIGN KEY (`User_id`) REFERENCES `utilisateur` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
-- Table pour stocker les événements du planning
CREATE TABLE IF NOT EXISTS events (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    is_full_day BOOLEAN DEFAULT FALSE,
    admin_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Index pour améliorer les performances
    INDEX idx_admin_id (admin_id),
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time)
);

-- Exemple d'événements de test (optionnel)
INSERT INTO events (title, description, start_time, end_time, is_full_day, admin_id) VALUES
('Réunion d\'équipe', 'Salle de conférence A - Point hebdomadaire', '2025-12-08 09:00:00', '2025-12-08 10:30:00', FALSE, 1),
('Formation Java', 'Formation avancée JavaFX et bases de données', '2025-12-09 14:00:00', '2025-12-09 17:00:00', FALSE, 1),
('Congé', 'Jour férié', '2025-12-25 00:00:00', '2025-12-25 23:59:59', TRUE, 1);

