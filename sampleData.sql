--Truncate tables
TRUNCATE TABLE Register RESTART IDENTITY CASCADE;
TRUNCATE TABLE FitnessGoal RESTART IDENTITY CASCADE;
TRUNCATE TABLE Routine RESTART IDENTITY CASCADE;
TRUNCATE TABLE FitnessAchievement RESTART IDENTITY CASCADE;
TRUNCATE TABLE HealthMetrics RESTART IDENTITY CASCADE;
TRUNCATE TABLE ClassSchedule RESTART IDENTITY CASCADE;
TRUNCATE TABLE RoomSchedule RESTART IDENTITY CASCADE;
TRUNCATE TABLE TrainerSchedule RESTART IDENTITY CASCADE;
TRUNCATE TABLE Equipment RESTART IDENTITY CASCADE;
TRUNCATE TABLE FitnessClass RESTART IDENTITY CASCADE;
TRUNCATE TABLE Bill RESTART IDENTITY CASCADE;
TRUNCATE TABLE Room RESTART IDENTITY CASCADE;
TRUNCATE TABLE Member RESTART IDENTITY CASCADE;
TRUNCATE TABLE Trainer RESTART IDENTITY CASCADE;
TRUNCATE TABLE Admin RESTART IDENTITY CASCADE;
TRUNCATE TABLE Users RESTART IDENTITY CASCADE;


-- Users
INSERT INTO Users (FirstName, LastName, Password, Email) VALUES 
('John', 'Doe', '123', 'member'),
('Jane', 'Doe', '123', 'jane.doe@example.com'),
('Alice', 'Smith', '123', 'both'),
('Bob', 'Brown', '123', 'trainer'),
('Emma', 'Wilson', '123', 'emma.wilson@example.com'),
('Liam', 'Johnson', '123', 'admin');

-- Member
INSERT INTO Member (MemberID, RegisterDate) VALUES 
(1, '2022-01-01'),
(2, '2023-02-14');

-- Trainer
INSERT INTO Trainer (TrainerID, Specialization) VALUES 
(3, 'Yoga'),
(4, 'Weightlifting');

-- Admin
INSERT INTO Admin (AdminID, Title) VALUES 
(3, 'Assistant Manager'),
(5, 'Manager'),
(6, 'Assistant Manager');

-- Bill
INSERT INTO Bill (MemberID, AdminID, BillDate, Amount, Status) VALUES 
(1, 5, '2020-03-01', 100.00, 'PENDING'),
(2, 5, '2023-06-05', 150.00, 'PAID');

-- Room
INSERT INTO Room (RoomName, Capacity) VALUES 
('Yoga Studio', 20),
('Weight Room', 10);

-- FitnessClass
INSERT INTO FitnessClass (ClassName, TrainerID, RoomID, Status) VALUES 
('Morning Yoga', 3, 1, 'OPEN'),
('Evening Weights', 4, 2, 'FULL');

-- Register
INSERT INTO Register (MemberID, ClassID) VALUES 
(1, 1),
(1, 2),
(2, 1),
(2, 2);

-- FitnessGoal
INSERT INTO FitnessGoal (MemberID, StartDate, EndDate, TargetWeight, Status) VALUES 
(1, '2023-01-01', '2023-06-01', 65, 'IN PROGRESS'),
(2, '2023-02-01', '2023-07-01', 80, 'IN PROGRESS');

-- Routine
INSERT INTO Routine (MemberID, Type, Reps, Sets) VALUES 
(1, 'Push-ups', 10, 3),
(2, 'Squats', 15, 4);

-- FitnessAchievement
INSERT INTO FitnessAchievement (MemberID, Description, DateAchieved) VALUES 
(1, 'Completed first marathon', '2023-03-15'),
(2, 'Lifted personal best of 200 lbs', '2023-04-10');

-- HealthMetrics
INSERT INTO HealthMetrics (MemberID, Weight, Height, RecordDate) VALUES 
(1, 70, 175, '2023-01-15'),
(2, 85, 180, '2023-02-15');

-- ClassSchedule
INSERT INTO ClassSchedule (ClassID, StartTime, EndTime, Date) VALUES 
(1, '08:00', '09:00', '2023-04-01'),
(2, '18:00', '19:00', '2023-04-01');

-- RoomSchedule
INSERT INTO RoomSchedule (RoomID, StartTime, EndTime, Date) VALUES 
(1, '08:00', '09:00', '2023-04-01'),
(2, '18:00', '19:00', '2023-04-01');

-- TrainerSchedule
INSERT INTO TrainerSchedule (TrainerID, StartTime, EndTime, Date) VALUES 
(3, '08:00', '09:00', '2023-04-01'),
(4, '18:00', '19:00', '2023-04-01');

-- Equipment
INSERT INTO Equipment (EquipmentName, PurchaseDate, LastMaintenanceDate) VALUES 
('Yoga Mats', '2022-01-01', '2023-05-05'),
('Dumbbells', '2022-02-01', '2023-08-03');
