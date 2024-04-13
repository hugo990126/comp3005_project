package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5433/comp3005_project";
        String user = "postgres";
        String password = "admin";

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            if (connection != null)
                System.out.println("Connected");
            else {
                System.out.println("Failed to connect");
                return;
            }


            Statement statement = connection.createStatement();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            PreparedStatement ps;
            String SQL;
            ResultSet rs;
            int input, id, count = 0;
            String lname, fname, email, pwd;

            while (true) {
                System.out.print("1. Login\n" +
                        "2. Member registration\n" +
                        "3. Exit\n" +
                        "Enter: ");

                try {
                    input = Integer.parseInt(reader.readLine());

                    switch (input) {
                        case 1:
                            System.out.print("Email: ");
                            email = reader.readLine();
                            System.out.print("Password: ");
                            pwd = reader.readLine();

                            id = getUserIdByEmail(connection, email);
                            SQL = "SELECT COUNT(UserID) from Users where UserID = ? AND Password = ?";
                            ps = connection.prepareStatement(SQL);
                            ps.setInt(1, id);
                            ps.setString(2, pwd);
                            rs = ps.executeQuery();

                            if (rs.next())
                                count = rs.getInt(1);

                            if (count == 0) {
                                System.out.println("Incorrect ID or Password\n");
                                continue;
                            }

                            boolean isAdmin = false, isMember = false, isTrainer = false;

                            SQL = "SELECT COUNT(AdminID) from Admin where AdminID = ?";
                            ps = connection.prepareStatement(SQL);
                            ps.setInt(1, id);
                            rs = ps.executeQuery();
                            count = 0;
                            if (rs.next())
                                count = rs.getInt(1);
                            if (count != 0)
                                isAdmin = true;

                            SQL = "SELECT COUNT(MemberID) from Member where MemberID = ?";
                            ps = connection.prepareStatement(SQL);
                            ps.setInt(1, id);
                            rs = ps.executeQuery();
                            count = 0;
                            if (rs.next())
                                count = rs.getInt(1);
                            if (count != 0)
                                isMember = true;

                            SQL = "SELECT COUNT(TrainerID) from Trainer where TrainerID = ?";
                            ps = connection.prepareStatement(SQL);
                            ps.setInt(1, id);
                            rs = ps.executeQuery();
                            count = 0;
                            if (rs.next())
                                count = rs.getInt(1);
                            if (count != 0)
                                isTrainer = true;

                            if (isMember) {
                                memberInterface(connection, reader, id);
                                continue;
                            }

                            if (isAdmin && isTrainer) {
                                System.out.println("1. Login as admin\n" +
                                        "2. Login as Trainer\n" +
                                        "Enter: ");
                                input = Integer.parseInt(reader.readLine());
                                if (input == 1) {
                                    adminInterface(connection, reader, id);
                                    continue;
                                } else if (input == 2) {
                                    trainerInterface(connection, reader, id);
                                    continue;
                                } else {
                                    System.out.println("Incorrect input");
                                    continue;
                                }
                            }

                            if (isAdmin) {
                                adminInterface(connection, reader, id);
                                continue;
                            }

                            if (isTrainer) {
                                trainerInterface(connection, reader, id);
                                continue;
                            }

                        case 2:
                            System.out.print("First name: ");
                            fname = reader.readLine();
                            System.out.print("Last name: ");
                            lname = reader.readLine();
                            System.out.print("Email: ");
                            email = reader.readLine();
                            System.out.print("Password: ");
                            pwd = reader.readLine();

                            SQL = "INSERT INTO Users (FirstName , LastName, Password, Email) VALUES (?,?,?,?) RETURNING UserID";
                            ps = connection.prepareStatement(SQL);
                            ps.setString(1, fname);
                            ps.setString(2, lname);
                            ps.setString(3, pwd);
                            ps.setString(4, email);
                            rs = ps.executeQuery();

                            int userID = 0;
                            if (rs.next()) {
                                userID = rs.getInt("UserID");
                            }

                            SQL = "INSERT INTO Member (MemberID, RegisterDate) VALUES(?,?)";
                            ps = connection.prepareStatement(SQL);
                            ps.setInt(1, userID);
                            ps.setDate(2, Date.valueOf(LocalDate.now()));
                            ps.executeUpdate();
                            System.out.println("Registration Successful\n");
                            continue;

                        case 3:
                            return;

                        default:
                            System.out.println("Incorrect input\n");
                            continue;
                    }
                } catch (Exception e) {
                    System.out.println("Incorrect input\n");
                    continue;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static void memberInterface(Connection connection, BufferedReader reader, int id) {
        PreparedStatement ps;
        int input;

        while (true) {
            System.out.println("\nMember menu:\n" +
                    "1. Profile Management\n" +
                    "2. Dashboard Display\n" +
                    "3. Schedule Management\n" +
                    "4. Logout\n" +
                    "Enter: ");
            try {
                input = Integer.parseInt(reader.readLine());
            } catch (Exception e) {
                System.out.println("Incorrect input");
                continue;
            }
            switch (input) {
                case 1:
                    profileManagement(connection, reader, id);
                    break;
                case 2:
                    displayExerciseRoutines(connection, id);
                    displayFitnessAchievements(connection, id);
                    displayHealthStatistics(connection, id);
                    break;
                case 3:
                    memberScheduleManagement(connection, reader, id);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Incorrect input");
                    continue;
            }
        }

    }

    public static void trainerInterface(Connection connection, BufferedReader reader, int id) {
        PreparedStatement ps;
        int input;

        while (true) {
            System.out.println("\nTrainer menu:\n" +
                    "1. Schedule Management\n" +
                    "2. Member Profile Viewing\n" +
                    "3. Logout\n" +
                    "Enter:");
            try {
                input = Integer.parseInt(reader.readLine());
            } catch (Exception e) {
                System.out.println("Incorrect input");
                continue;
            }
            switch (input) {
                case 1:
                    while (true) {
                        try {
                            displayTrainerSchedules(connection, id);
                            System.out.println("1. Add New Availability Slot\n" +
                                    "2. Remove Availability Slot\n" +
                                    "3. Back\n" +
                                    "Enter: ");
                            input = Integer.parseInt(reader.readLine());
                            if (input == 1) {
                                System.out.println("Date of schedule (YYYY-MM-DD): ");
                                String dateString = reader.readLine();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                java.util.Date utilDate = formatter.parse(dateString);
                                Date date = new Date(utilDate.getTime());

                                System.out.println("Start Time (HH:MM): ");
                                String timeString = reader.readLine();
                                formatter = new SimpleDateFormat("HH:mm");
                                utilDate = formatter.parse(timeString);
                                Time startTime = new Time(utilDate.getTime());

                                System.out.println("End Time (HH:MM): ");
                                String endtimeString = reader.readLine();
                                utilDate = formatter.parse(endtimeString);
                                Time endTime = new Time(utilDate.getTime());

                                insertTrainerSchedule(connection, id, date, startTime, endTime);
                                break;
                            } else if (input == 2) {
                                System.out.println("Date of schedule (YYYY-MM-DD) to remove: ");
                                String dateString = reader.readLine();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                java.util.Date utilDate = formatter.parse(dateString);
                                Date date = new Date(utilDate.getTime());

                                removeTrainerSchedule(connection, id, date);
                                break;
                            } else if (input == 3) {
                                break;
                            } else {
                                System.out.println("Incorrect input");
                                continue;
                            }
                        } catch (Exception e) {
                            System.out.println("Incorrect input");
                            continue;
                        }
                    }
                    break;
                case 2:
                    while (true) {
                        try {
                            displayAllMembers(connection);
                            System.out.println("First name of the member: ");
                            String firstName = reader.readLine();
                            System.out.println("Last name of the member: ");
                            String lastName = reader.readLine();
                            int memberID = getMemberUserId(connection, firstName, lastName);
                            if (memberID == -1)
                                continue;
                            System.out.println("1. Member's exercise routine\n" +
                                    "2. Member's fitness achievements\n" +
                                    "3. Member's health statistics\n" +
                                    "4. Member's fitness goals\n" +
                                    "Enter: ");
                            input = Integer.parseInt(reader.readLine());
                            switch (input) {
                                case 1:
                                    displayExerciseRoutines(connection, memberID);
                                    break;
                                case 2:
                                    displayFitnessAchievements(connection, memberID);
                                    break;
                                case 3:
                                    displayHealthStatistics(connection, memberID);
                                    break;
                                case 4:
                                    displayFitnessGoals(connection, memberID);
                                    break;
                                default:
                                    System.out.println("Incorrect input");
                                    continue;
                            }
                            break;
                        } catch (Exception e) {
                            System.out.println("Incorrect input");
                            continue;
                        }
                    }
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Incorrect input");
                    continue;
            }
        }

    }

    public static void adminInterface(Connection connection, BufferedReader reader, int id) {
        PreparedStatement ps;
        int input;

        while (true) {
            System.out.println("\nAdmin menu:\n" +
                    "1. Room Booking Management\n" +
                    "2. Equipment Maintenance Monitoring\n" +
                    "3. View all classes and sessions\n" +
                    "4. Class Schedule Update\n" +
                    "5. Billing and Payment Processing\n" +
                    "6. Logout\n" +
                    "Enter: ");
            try {
                input = Integer.parseInt(reader.readLine());

                switch (input) {
                    case 1:
                        roomManagement(connection, reader);
                        break;
                    case 2:
                        displayAllEquipment(connection);
                        System.out.println("1. Update last maintenance date \n" +
                                "2. Back\n" +
                                "Enter: ");
                        input = Integer.parseInt(reader.readLine());
                        if (input == 1) {
                            System.out.println("Equipment ID to be updated: ");
                            int equipmentID = Integer.parseInt(reader.readLine());
                            System.out.println("Last maintenance date (YYYY-MM-DD): ");
                            String dateString = reader.readLine();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            java.util.Date utilDate = formatter.parse(dateString);
                            Date date = new Date(utilDate.getTime());
                            updateEquipmentMaintenanceDate(connection, equipmentID, date);
                            break;
                        }
                    case 3:
                        displayOpenFitnessClasses(connection);
                        displayFullFitnessClasses(connection);
                        break;
                    case 4:
                        System.out.println("Class to update (ID): ");
                        int classID = Integer.parseInt(reader.readLine());

                        System.out.println("New schedule date(YYYY-MM-DD): ");
                        String dateString = reader.readLine();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        java.util.Date utilDate = formatter.parse(dateString);
                        Date date = new Date(utilDate.getTime());

                        System.out.println("Start Time (HH:MM): ");
                        String timeString = reader.readLine();
                        formatter = new SimpleDateFormat("HH:mm");
                        utilDate = formatter.parse(timeString);
                        Time startTime = new Time(utilDate.getTime());

                        System.out.println("End Time (HH:MM): ");
                        String endtimeString = reader.readLine();
                        utilDate = formatter.parse(endtimeString);
                        Time endTime = new Time(utilDate.getTime());

                        updateClassSchedule(connection, classID, date, startTime, endTime);
                        break;
                    case 5:
                        while (true) {
                            int billID;
                            System.out.println("1. Issue new bill\n" +
                                    "2. Received a payment\n" +
                                    "3. Cancel a bill\n" +
                                    "4. Display all bills\n" +
                                    "5. Back\n" +
                                    "Enter: ");
                            input = Integer.parseInt(reader.readLine());
                            switch (input) {
                                case 1:
                                    System.out.println("Bill to (member ID): ");
                                    int memberID = Integer.parseInt(reader.readLine());
                                    System.out.println("Amount: ");
                                    float amount = Float.parseFloat(reader.readLine());
                                    insertNewBill(connection, memberID, id, amount);
                                    break;
                                case 2:
                                    System.out.println("Bill id: ");
                                    billID = Integer.parseInt(reader.readLine());
                                    updateBillStatusToPaid(connection, billID);
                                    break;
                                case 3:
                                    System.out.println("Bill id: ");
                                    billID = Integer.parseInt(reader.readLine());
                                    updateBillStatusToCancel(connection, billID);
                                    break;
                                case 4:
                                    displayAllBills(connection);
                                    break;
                                case 5:
                                    break;
                                default:
                                    System.out.println("Incorrect input");
                                    continue;
                            }
                            break;
                        }
                        break;
                    case 6:
                        return;
                    default:
                        System.out.println("Incorrect input");
                        continue;
                }
            } catch (Exception e) {
                System.out.println("Incorrect input");
                continue;
            }
        }

    }

    public static void displayAllMembers(Connection connection) {
        String query = "SELECT m.MemberID, u.FirstName, u.LastName " +
                "FROM Member m INNER JOIN Users u ON m.MemberID = u.UserID " +
                "ORDER BY m.MemberID ASC";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No members found.");
                return;
            }

            System.out.println("+----------+------------+-----------+");
            System.out.println("| MemberID | First Name | Last Name |");
            System.out.println("+----------+------------+-----------+");

            while (rs.next()) {
                int memberId = rs.getInt("MemberID");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");

                System.out.format("| %-8d | %-10s | %-9s |\n", memberId, firstName, lastName);
            }

            System.out.println("+----------+------------+-----------+");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void displayUserEmail(Connection connection, int userId) {
        String query = "SELECT Email FROM Users WHERE UserID = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String userEmail = resultSet.getString("Email");
                System.out.println("Current email: " + userEmail);
            } else {
                System.out.println("No user found");
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
        }
    }

    public static void displayAllBills(Connection connection) {
        String query = "SELECT BillID, MemberID, AdminID, BillDate, Amount, Status FROM Bill ORDER BY BillID ASC";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No bills found.");
                return;
            }

            System.out.println("+--------+----------+---------+------------+--------+----------+");
            System.out.println("| BillID | MemberID | AdminID | BillDate   | Amount | Status   |");
            System.out.println("+--------+----------+---------+------------+--------+----------+");

            while (rs.next()) {
                int billID = rs.getInt("BillID");
                int memberID = rs.getInt("MemberID");
                int adminID = rs.getInt("AdminID");
                Date billDate = rs.getDate("BillDate");
                double amount = rs.getDouble("Amount");
                String status = rs.getString("Status");

                System.out.format("| %-6d | %-8d | %-7d | %-10s | %-6.2f | %-8s |\n",
                        billID, memberID, adminID, billDate.toString(), amount, status);
            }

            System.out.println("+--------+----------+---------+------------+--------+----------+");
        } catch (Exception e) {
            System.err.println("Error displaying all bills: " + e.getMessage());
        }
    }

    public static void displayAllEquipment(Connection connection) {
        String query = "SELECT EquipmentID, EquipmentName, PurchaseDate, LastMaintenanceDate FROM Equipment ORDER BY EquipmentID ASC";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No equipment found.");
                return;
            }

            System.out.println("+-------------+---------------------------+----------------+-------------------+");
            System.out.println("| EquipmentID | EquipmentName             | PurchaseDate   | LastMaintenanceDate |");
            System.out.println("+-------------+---------------------------+----------------+-------------------+");

            while (rs.next()) {
                int equipmentID = rs.getInt("EquipmentID");
                String equipmentName = rs.getString("EquipmentName");
                Date purchaseDate = rs.getDate("PurchaseDate");
                Date lastMaintenanceDate = rs.getDate("LastMaintenanceDate");

                System.out.format("| %-11d | %-25s | %-14s | %-17s |\n",
                        equipmentID, equipmentName, purchaseDate.toString(), lastMaintenanceDate.toString());
            }

            System.out.println("+-------------+---------------------------+----------------+-------------------+");
        } catch (Exception e) {
            System.err.println("Database error " + e.getMessage());
        }
    }

    public static void displayRoomSchedules(Connection connection, int roomID) {
        String query = "SELECT ScheduleId, RoomID, Date, StartTime, EndTime FROM RoomSchedule WHERE RoomID = ? ORDER BY Date ASC, StartTime ASC";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, roomID);
            ResultSet rs = statement.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No schedules found");
                return;
            }

            System.out.println("+------------+--------+------------+-----------+----------+");
            System.out.println("| ScheduleID | RoomID | Date       | Start Time| End Time |");
            System.out.println("+------------+--------+------------+-----------+----------+");

            while (rs.next()) {
                int scheduleId = rs.getInt("ScheduleId");
                Date date = rs.getDate("Date");
                Time startTime = rs.getTime("StartTime");
                Time endTime = rs.getTime("EndTime");

                System.out.format("| %-10d | %-6d | %-10s | %-9s | %-8s |\n",
                        scheduleId, roomID, date.toString(), startTime.toString(), endTime.toString());
            }

            System.out.println("+------------+--------+------------+-----------+----------+");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void displayRooms(Connection connection) {
        String query = "SELECT RoomID, RoomName, Capacity FROM Room ORDER BY RoomID ASC";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No rooms found.");
                return;
            }

            System.out.println("+--------+--------------------------------+----------+");
            System.out.println("| RoomID | RoomName                       | Capacity |");
            System.out.println("+--------+--------------------------------+----------+");

            while (rs.next()) {
                int roomID = rs.getInt("RoomID");
                String roomName = rs.getString("RoomName");
                int capacity = rs.getInt("Capacity");

                System.out.format("| %-6d | %-30s | %-8d |\n", roomID, roomName, capacity);
            }

            System.out.println("+--------+--------------------------------+----------+");
        } catch (Exception e) {
            System.err.println("Database Error: " + e.getMessage());
        }
    }

    public static void displayTrainerSchedules(Connection connection, int trainerId) {
        String query = "SELECT ScheduleId, Date, StartTime, EndTime FROM TrainerSchedule " +
                "WHERE TrainerID = ? " +
                "ORDER BY Date ASC, StartTime ASC";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, trainerId);

            ResultSet rs = statement.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No schedules found");
                return;
            }

            System.out.println("+------------+------------+------------+----------+");
            System.out.println("| ScheduleID | Date       | Start Time | End Time |");
            System.out.println("+------------+------------+------------+----------+");

            while (rs.next()) {
                int scheduleId = rs.getInt("ScheduleId");
                Date date = rs.getDate("Date");
                Time startTime = rs.getTime("StartTime");
                Time endTime = rs.getTime("EndTime");

                System.out.format("| %-10d | %-10s | %-10s | %-8s |\n", scheduleId, date.toString(), startTime.toString(), endTime.toString());
            }

            System.out.println("+------------+------------+------------+----------+");
        } catch (Exception e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void displayHealthMetrics(Connection connection, int id) {
        String SQL = "SELECT Weight, Height, RecordDate FROM HealthMetrics WHERE MemberID = ? ORDER BY RecordDate DESC";
        try (PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No health metrics records found");
                return;
            }

            System.out.println("+------------+--------+--------+");
            System.out.println("| Date       | Weight | Height |");
            System.out.println("+------------+--------+--------+");

            while (rs.next()) {
                int weight = rs.getInt("Weight");
                int height = rs.getInt("Height");
                Date recordDate = rs.getDate("RecordDate");

                System.out.format("| %-10s | %-6d | %-6d |\n", recordDate.toString(), weight, height);
            }

            System.out.println("+------------+--------+--------+");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void displayExerciseRoutines(Connection connection, int memberId) {
        String SQL = "SELECT RoutineID, Type, Reps, Sets FROM Routine WHERE MemberID = ?";
        try (PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, memberId);

            ResultSet rs = ps.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No exercise routines found");
                return;
            }

            System.out.println("+-----------+----------------------+------+-----+");
            System.out.println("| RoutineID | Type                 | Reps | Sets|");
            System.out.println("+-----------+----------------------+------+-----+");

            while (rs.next()) {
                int routineId = rs.getInt("RoutineID");
                String type = rs.getString("Type");
                int reps = rs.getInt("Reps");
                int sets = rs.getInt("Sets");

                System.out.format("| %-9d | %-20s | %-4d | %-4d|\n", routineId, type, reps, sets);
            }

            System.out.println("+-----------+----------------------+------+-----+");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void displayFitnessAchievements(Connection connection, int memberId) {
        String SQL = "SELECT AchievementID, Description, DateAchieved FROM FitnessAchievement WHERE MemberID = ? ORDER BY DateAchieved DESC";
        try (PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, memberId);

            ResultSet rs = ps.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No fitness achievements found for Member ID: " + memberId);
                return;
            }

            System.out.println("+---------------+-----------------+------------------------------------------------+");
            System.out.println("| AchievementID | Date Achieved   | Description                                    |");
            System.out.println("+---------------+-----------------+------------------------------------------------+");

            while (rs.next()) {
                int achievementId = rs.getInt("AchievementID");
                Date dateAchieved = rs.getDate("DateAchieved");
                String description = rs.getString("Description");

                String formattedDescription = description.length() > 50 ? description.substring(0, 47) + "..." : description;

                System.out.format("| %-13d | %-15s | %-50s |\n", achievementId, dateAchieved.toString(), formattedDescription);
            }

            System.out.println("+---------------+-----------------+------------------------------------------------+");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void displayHealthStatistics(Connection connection, int id) {
        // Display the latest BMI
        String latestSQL = "SELECT Weight, Height FROM HealthMetrics WHERE MemberID = ? ORDER BY RecordDate DESC LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(latestSQL)) {
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("No health metrics found for MemberID " + id);
            } else {
                int weight = rs.getInt("Weight");
                int height = rs.getInt("Height");
                double heightInMeters = height / 100.0; // Convert height from cm to meters
                double bmi = weight / (heightInMeters * heightInMeters); // Calculate BMI
                System.out.printf("Latest BMI for MemberID %d: %.2f\n", id, bmi);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving latest BMI: " + e.getMessage());
            return;
        }

        // Calculate and display the average weight
        String avgSQL = "SELECT AVG(Weight) AS AverageWeight FROM HealthMetrics WHERE MemberID = ?";
        try (PreparedStatement psAvg = connection.prepareStatement(avgSQL)) {
            psAvg.setInt(1, id);

            ResultSet rsAvg = psAvg.executeQuery();

            if (rsAvg.next()) {
                double averageWeight = rsAvg.getDouble("AverageWeight");
                if (!rsAvg.wasNull()) {
                    System.out.printf("Average weight for MemberID %d: %.2f kg\n", id, averageWeight);
                } else {
                    System.out.println("Unable to calculate average weight; no weight records found for MemberID " + id);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void displayOpenFitnessClasses(Connection connection) {
        String sql = "SELECT fc.ClassID, fc.ClassName, fc.Status, cs.Date, cs.StartTime, cs.EndTime " +
                "FROM FitnessClass fc " +
                "JOIN ClassSchedule cs ON fc.ClassID = cs.ClassID " +
                "WHERE fc.Status = 'OPEN' " +
                "ORDER BY fc.ClassName ASC, cs.Date ASC, cs.StartTime ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("There are no open fitness classes currently.");
                return;
            }

            System.out.println("+---------+--------------------------------+--------+------------+-----------+----------+");
            System.out.println("| ClassID | ClassName                      | Status | Date       | Start Time| End Time |");
            System.out.println("+---------+--------------------------------+--------+------------+-----------+----------+");

            while (rs.next()) {
                int classId = rs.getInt("ClassID");
                String className = rs.getString("ClassName");
                String status = rs.getString("Status");
                Date date = rs.getDate("Date");
                Time startTime = rs.getTime("StartTime");
                Time endTime = rs.getTime("EndTime");

                System.out.format("| %-7d | %-30s | %-6s | %-10s | %-9s | %-8s |\n",
                        classId, className, status, date.toString(), startTime.toString(), endTime.toString());
            }

            System.out.println("+---------+--------------------------------+--------+------------+-----------+----------+");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void displayFullFitnessClasses(Connection connection) {
        String sql = "SELECT fc.ClassID, fc.ClassName, fc.Status, cs.Date, cs.StartTime, cs.EndTime " +
                "FROM FitnessClass fc " +
                "JOIN ClassSchedule cs ON fc.ClassID = cs.ClassID " +
                "WHERE fc.Status = 'FULL' " +
                "ORDER BY fc.ClassName ASC, cs.Date ASC, cs.StartTime ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("There are no open fitness classes currently.");
                return;
            }

            System.out.println("+---------+--------------------------------+--------+------------+-----------+----------+");
            System.out.println("| ClassID | ClassName                      | Status | Date       | Start Time| End Time |");
            System.out.println("+---------+--------------------------------+--------+------------+-----------+----------+");

            while (rs.next()) {
                int classId = rs.getInt("ClassID");
                String className = rs.getString("ClassName");
                String status = rs.getString("Status");
                Date date = rs.getDate("Date");
                Time startTime = rs.getTime("StartTime");
                Time endTime = rs.getTime("EndTime");

                System.out.format("| %-7d | %-30s | %-6s | %-10s | %-9s | %-8s |\n",
                        classId, className, status, date.toString(), startTime.toString(), endTime.toString());
            }

            System.out.println("+---------+--------------------------------+--------+------------+-----------+----------+");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void displayFitnessGoals(Connection connection, int id) {
        String SQL;
        PreparedStatement ps;

        try {
            SQL = "SELECT GoalID, StartDate, EndDate, TargetWeight, Status FROM FitnessGoal WHERE MemberID = ?";
            ps = connection.prepareStatement(SQL);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No fitness goals found for Member ID: " + id);
                return;
            }

            System.out.println("+--------+------------+------------+--------------+-----------+");
            System.out.println("| GoalID | Start Date | End Date   | Target Weight| Status    |");
            System.out.println("+--------+------------+------------+--------------+-----------+");

            while (rs.next()) {
                int goalId = rs.getInt("GoalID");
                Date startDate = rs.getDate("StartDate");
                Date endDate = rs.getDate("EndDate");
                int targetWeight = rs.getInt("TargetWeight");
                String status = rs.getString("Status");

                System.out.format("| %-6d | %-10s | %-10s | %-12d | %-9s |\n",
                        goalId, startDate.toString(), endDate != null ? endDate.toString() : "N/A",
                        targetWeight, status);
            }

            System.out.println("+--------+------------+------------+--------------+-----------+");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void displayMemberSchedule(Connection connection, int memberId) {
        // Updated SQL to join with ClassSchedule and fetch date and time
        String sql = "SELECT fc.ClassID, fc.ClassName, fc.Status, cs.Date, cs.StartTime, cs.EndTime " +
                "FROM Register r " +
                "JOIN FitnessClass fc ON r.ClassID = fc.ClassID " +
                "JOIN ClassSchedule cs ON fc.ClassID = cs.ClassID " +
                "WHERE r.MemberID = ? AND fc.Status <> 'FINISHED' " +
                "ORDER BY cs.Date ASC, cs.StartTime ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, memberId);

            ResultSet rs = ps.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No ongoing or upcoming classes found ");
                return;
            }

            System.out.println("+---------+--------------------------------+----------+------------+------------+----------+");
            System.out.println("| ClassID | ClassName                      | Status   | Date       | Start Time | End Time |");
            System.out.println("+---------+--------------------------------+----------+------------+------------+----------+");

            while (rs.next()) {
                System.out.format("| %-7d | %-30s | %-8s | %-10s | %-10s | %-8s |\n",
                        rs.getInt("ClassID"),
                        rs.getString("ClassName"),
                        rs.getString("Status"),
                        rs.getDate("Date").toString(),
                        rs.getTime("StartTime").toString(),
                        rs.getTime("EndTime").toString());
            }

            System.out.println("+---------+--------------------------------+----------+------------+------------+----------+");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void displayTrainers(Connection connection) {
        String query = "SELECT u.FirstName, u.LastName, t.Specialization " +
                "FROM Users u INNER JOIN Trainer t ON u.UserID = t.TrainerID";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            boolean found = false;
            while (resultSet.next()) {
                found = true;
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                String specialization = resultSet.getString("Specialization");
                System.out.println("Trainer Name: " + firstName + " " + lastName + ", Specialization: " + specialization);
            }

            if (!found) {
                System.out.println("No trainers found.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void displayUserPassword(Connection connection, int userId) {
        String query = "SELECT Password FROM Users WHERE UserID = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String userPassword = resultSet.getString("Password");
                System.out.println("Current password: " + userPassword);
            } else {
                System.out.println("No user found");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void updateBillStatusToCancel(Connection connection, int billID) {
        String updateSql = "UPDATE Bill SET Status = 'CANCELED'::bill_status WHERE BillID = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setInt(1, billID);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Bill canceled successfully");
            } else {
                System.out.println("No bill found with ID: " + billID + ", or bill is already CANCELED.");
            }
        } catch (SQLException e) {
            System.err.println("Database error " + e.getMessage());
        }
    }

    public static void updateBillStatusToPaid(Connection connection, int billID) {
        String updateSql = "UPDATE Bill SET Status = 'PAID'::bill_status WHERE BillID = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setInt(1, billID);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Bill paid");
            } else {
                System.out.println("No bill found with ID: " + billID + ", or bill is already PAID.");
            }
        } catch (SQLException e) {
            System.err.println("Database error " + e.getMessage());
        }
    }

    public static void updateClassSchedule(Connection connection, int classID, Date date, Time startTime, Time endTime) {
        String updateSql = "UPDATE ClassSchedule SET Date = ?, StartTime = ?, EndTime = ? WHERE ClassID = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setDate(1, date);
            statement.setTime(2, startTime);
            statement.setTime(3, endTime);
            statement.setInt(4, classID);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Class schedule updated successfully");
            } else {
                System.out.println("No class found");
            }
        } catch (SQLException e) {
            System.err.println("Database error " + e.getMessage());
        }
    }

    public static void updatePersonalInformation(Connection connection, BufferedReader reader, int id) {
        PreparedStatement ps;
        String SQL;
        while (true) {
            displayUserEmail(connection, id);
            displayUserPassword(connection, id);
            System.out.println("1. Update email address\n" +
                    "2. Update password\n" +
                    "3. Back\n" +
                    "Enter: ");
            try {
                int input = Integer.parseInt(reader.readLine());
                switch (input) {
                    case 1:
                        System.out.println("Enter new email: ");
                        String email = reader.readLine();
                        SQL = "UPDATE users SET email = ? WHERE UserID = ?";
                        ps = connection.prepareStatement(SQL);
                        ps.setString(1, email);
                        ps.setInt(2, id);
                        ps.executeUpdate();
                        System.out.println("Updated");
                        continue;
                    case 2:
                        System.out.println("Enter new password: ");
                        String pwd = reader.readLine();
                        SQL = "UPDATE users SET password = ? WHERE UserID = ?";
                        ps = connection.prepareStatement(SQL);
                        ps.setString(1, pwd);
                        ps.setInt(2, id);
                        ps.executeUpdate();
                        System.out.println("Updated");
                        continue;
                    case 3:
                        return;
                    default:
                        System.out.println("Incorrect input");
                        continue;
                }
            } catch (SQLException se) {
                System.err.println("Error: " + se.getMessage());
            } catch (Exception e) {
                System.out.println("Incorrect input");
                continue;
            }
        }
    }

    public static void updateFitnessGoals(Connection connection, BufferedReader reader, int id) {
        String SQL;

        try {
            System.out.println("Do you want to update a current goal or create a new one?");
            System.out.println("1. Update current goal");
            System.out.println("2. Create a new goal");
            System.out.println("3. Back to main menu");
            System.out.print("Enter choice: ");
            int choice = Integer.parseInt(reader.readLine());

            if (choice == 1) { // Update current goal
                System.out.print("Enter the GoalID you want to update: ");
                int goalId = Integer.parseInt(reader.readLine());

                System.out.println("Do you want to update the endDate or status?");
                System.out.println("1. Update endDate");
                System.out.println("2. Update status");
                System.out.print("Enter choice: ");
                int updateChoice = Integer.parseInt(reader.readLine());

                if (updateChoice == 1) { // Update endDate
                    System.out.print("Enter new endDate (YYYY-MM-DD): ");
                    String newEndDate = reader.readLine();

                    SQL = "UPDATE FitnessGoal SET EndDate = ? WHERE GoalID = ?";
                    try (PreparedStatement ps = connection.prepareStatement(SQL)) {
                        ps.setDate(1, Date.valueOf(newEndDate));
                        ps.setInt(2, goalId);
                        ps.executeUpdate();
                        System.out.println("Goal endDate updated successfully.");
                    } catch (SQLException e) {
                        System.err.println("Error updating goal endDate: " + e.getMessage());
                    }
                } else if (updateChoice == 2) { // Update status
                    System.out.println("Enter new status ('IN PROGRESS', 'FULFILLED', 'CANCELED'):");
                    String newStatus = reader.readLine();

                    if (!newStatus.equals("IN PROGRESS") && !newStatus.equals("FULFILLED") && !newStatus.equals("CANCELED")) {
                        System.out.println("Invalid status entered. Please start over.");
                        return;
                    }

                    SQL = "UPDATE FitnessGoal SET Status = ?::goal_status WHERE GoalID = ?";
                    try (PreparedStatement ps = connection.prepareStatement(SQL)) {
                        ps.setString(1, newStatus);
                        ps.setInt(2, goalId);
                        ps.executeUpdate();
                        System.out.println("Goal status updated successfully.");
                    } catch (SQLException e) {
                        System.err.println("Error updating goal status: " + e.getMessage());
                    }
                }
            } else if (choice == 2) {
                System.out.print("Enter TargetWeight: ");
                int targetWeight = Integer.parseInt(reader.readLine());

                System.out.println("Choose status for new goal ('IN PROGRESS', 'FULFILLED', 'CANCELED'):");
                String status = reader.readLine().toUpperCase();

                if (!status.equals("IN PROGRESS") && !status.equals("FULFILLED") && !status.equals("CANCELED")) {
                    System.out.println("Invalid status entered. Please start over.");
                    return;
                }

                LocalDate startDate = LocalDate.now();

                SQL = "INSERT INTO FitnessGoal (MemberID, StartDate, TargetWeight, Status) VALUES (?, ?, ?, ?::goal_status)";
                try (PreparedStatement ps = connection.prepareStatement(SQL)) {
                    ps.setInt(1, id);
                    ps.setDate(2, Date.valueOf(startDate));
                    ps.setInt(3, targetWeight);
                    ps.setString(4, status);

                    int affectedRows = ps.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("New fitness goal added successfully.");
                    } else {
                        System.out.println("Failed to add new fitness goal.");
                    }
                } catch (SQLException e) {
                    System.err.println("Error creating a new fitness goal: " + e.getMessage());
                }
            } else if (choice == 3)
                return;
        } catch (IOException e) {
            System.out.println("Incorrect Input.");
        }

    }

    public static void updateHealthMetrics(Connection connection, BufferedReader reader, int id) {
        // Ask the user if they want to enter a new record or return to the main menu
        System.out.println("Do you want to enter a new health metrics record?");
        System.out.println("1. Yes");
        System.out.println("2. No, return to main menu");
        System.out.print("Enter choice (1 or 2): ");
        try {
            int choice = Integer.parseInt(reader.readLine());

            if (choice == 1) {
                System.out.print("Enter your weight: ");
                int weight = Integer.parseInt(reader.readLine());

                System.out.print("Enter your height: ");
                int height = Integer.parseInt(reader.readLine());

                LocalDate today = LocalDate.now();

                String insertSQL = "INSERT INTO HealthMetrics (MemberID, Weight, Height, RecordDate) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertPs = connection.prepareStatement(insertSQL)) {
                    insertPs.setInt(1, id);
                    insertPs.setInt(2, weight);
                    insertPs.setInt(3, height);
                    insertPs.setDate(4, Date.valueOf(today));

                    int affectedRows = insertPs.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("New health metrics record added successfully.");
                    } else {
                        System.out.println("Failed to add new health metrics record.");
                    }
                } catch (SQLException e) {
                    System.err.println("Error inserting new health metrics: " + e.getMessage());
                }
            } else if (choice == 2) {
                return;
            } else {
                System.out.println("Invalid choice. Returning to main menu.");
            }
        } catch (IOException e) {
            System.out.println("Incorrect input");
        }
    }

    public static void updateEquipmentMaintenanceDate(Connection connection, int equipmentID, Date date) {
        String updateSql = "UPDATE Equipment SET LastMaintenanceDate = ? WHERE EquipmentID = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setDate(1, date);
            statement.setInt(2, equipmentID);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Last maintenance date updated successfully");
            } else {
                System.out.println("Equipment not found");
            }
        } catch (SQLException e) {
            System.err.println("Database error " + e.getMessage());
        }
    }

    public static void insertNewBill(Connection connection, int memberID, int adminID, float amount) {
        String insertSql = "INSERT INTO Bill (MemberID, AdminID, BillDate, Amount, Status) VALUES (?, ?, ?, ?, ?::bill_status)";

        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            statement.setInt(1, memberID);
            statement.setInt(2, adminID);
            statement.setDate(3, Date.valueOf(LocalDate.now()));
            statement.setFloat(4, amount);
            statement.setString(5, "PENDING");

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("New bill inserted successfully.");
            } else {
                System.out.println("Failed to insert the new bill.");
            }
        } catch (SQLException e) {
            System.err.println("Database Error " + e.getMessage());
        }
    }

    public static void insertTrainerSchedule(Connection connection, int trainerId, Date date, Time startTime, Time endTime) {
        String insertSql = "INSERT INTO TrainerSchedule (TrainerID, Date, StartTime, EndTime) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            statement.setInt(1, trainerId);
            statement.setDate(2, date);
            statement.setTime(3, startTime);
            statement.setTime(4, endTime);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Schedule successfully inserted.");
            } else {
                System.out.println("Failed to insert the schedule.");
            }
        } catch (Exception e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void roomManagement(Connection connection, BufferedReader reader) {
        while (true) {
            try {
                System.out.println("\n1. View rooms and schedules\n" +
                        "2. Book a room for fitness class\n" +
                        "3. Book a room for other purposes\n" +
                        "4. Cancel Booking\n" +
                        "5. Exit\n" +
                        "Enter: ");
                int input = Integer.parseInt(reader.readLine());
                int roomID;
                switch (input) {
                    case 1:
                        displayRooms(connection);
                        while (true) {
                            System.out.println("1. View a room schedule\n" +
                                    "2. Back\n" +
                                    "Enter: ");
                            input = Integer.parseInt(reader.readLine());
                            if (input == 1) {
                                System.out.println("Enter the room id: ");
                                roomID = Integer.parseInt(reader.readLine());
                                displayRoomSchedules(connection, roomID);
                                break;
                            } else if (input == 2) {
                                break;
                            } else {
                                System.out.println("Incorrect input");
                                continue;
                            }
                        }
                        break;
                    case 2:
                        displayRooms(connection);
                        System.out.println("Room ID to be scheduled: ");
                        roomID = Integer.parseInt(reader.readLine());
                        displayOpenFitnessClasses(connection);
                        displayFullFitnessClasses(connection);
                        System.out.println("Schedule room for class (ID): ");
                        int fitnessClassID = Integer.parseInt(reader.readLine());
                        scheduleRoomForClass(connection, roomID, fitnessClassID);
                        break;
                    case 3:
                        displayRooms(connection);
                        System.out.println("Room ID to be scheduled: ");
                        roomID = Integer.parseInt(reader.readLine());

                        System.out.println("Date of schedule (YYYY-MM-DD): ");
                        String dateString = reader.readLine();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        java.util.Date utilDate = formatter.parse(dateString);
                        Date date = new Date(utilDate.getTime());

                        System.out.println("Start Time (HH:MM): ");
                        String timeString = reader.readLine();
                        formatter = new SimpleDateFormat("HH:mm");
                        utilDate = formatter.parse(timeString);
                        Time startTime = new Time(utilDate.getTime());

                        System.out.println("End Time (HH:MM): ");
                        String endtimeString = reader.readLine();
                        utilDate = formatter.parse(endtimeString);
                        Time endTime = new Time(utilDate.getTime());

                        scheduleRoom(connection, roomID, date, startTime, endTime);

                        break;
                    case 4:
                        displayRooms(connection);
                        while (true) {
                            System.out.println("1. View a room schedule\n" +
                                    "2. Back\n" +
                                    "Enter: ");
                            input = Integer.parseInt(reader.readLine());
                            if (input == 1) {
                                System.out.println("Enter the room id: ");
                                roomID = Integer.parseInt(reader.readLine());
                                displayRoomSchedules(connection, roomID);
                                while (true) {
                                    System.out.println("1.Delete a schedule:\n" +
                                            "2. Back\n" +
                                            "Enter:");
                                    input = Integer.parseInt(reader.readLine());
                                    if (input == 1) {
                                        System.out.println("Schedule to be delete (ID): ");
                                        int scheduleId = Integer.parseInt(reader.readLine());
                                        deleteRoomSchedule(connection, scheduleId);
                                    } else if (input == 2) {
                                        break;
                                    } else {
                                        System.out.println("Incorrect input");
                                        continue;
                                    }
                                }
                                break;
                            } else if (input == 2) {
                                break;
                            } else {
                                System.out.println("Incorrect input");
                                continue;
                            }
                        }
                        break;
                    case 5:
                        return;
                    default:
                        System.out.println("Incorrect input");
                        continue;
                }
            } catch (Exception e) {
                System.out.println("Incorrect input");
                continue;
            }
        }
    }

    public static void deleteRoomSchedule(Connection connection, int scheduleId) {
        // Step 1: Retrieve the room schedule details
        String fetchScheduleSql = "SELECT RoomID, Date, StartTime, EndTime FROM RoomSchedule WHERE ScheduleId = ?";

        // Step 2: Delete the schedule from RoomSchedule
        String deleteScheduleSql = "DELETE FROM RoomSchedule WHERE ScheduleId = ?";

        // Step 3: Update the FitnessClass table if any class is assigned to the room at the same time slot
        String updateFitnessClassSql = "UPDATE FitnessClass SET RoomID = NULL " +
                "WHERE RoomID = ? AND EXISTS (" +
                "SELECT 1 FROM ClassSchedule " +
                "WHERE FitnessClass.ClassID = ClassSchedule.ClassID " +
                "AND ClassSchedule.Date = ? AND ClassSchedule.StartTime = ? AND ClassSchedule.EndTime = ?)";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement fetchStmt = connection.prepareStatement(fetchScheduleSql)) {
                fetchStmt.setInt(1, scheduleId);
                ResultSet rs = fetchStmt.executeQuery();
                if (rs.next()) {
                    int roomID = rs.getInt("RoomID");
                    java.sql.Date date = rs.getDate("Date");
                    java.sql.Time startTime = rs.getTime("StartTime");
                    java.sql.Time endTime = rs.getTime("EndTime");

                    try (PreparedStatement updateStmt = connection.prepareStatement(updateFitnessClassSql)) {
                        updateStmt.setInt(1, roomID);
                        updateStmt.setDate(2, date);
                        updateStmt.setTime(3, startTime);
                        updateStmt.setTime(4, endTime);
                        updateStmt.executeUpdate();
                    }

                    try (PreparedStatement deleteStmt = connection.prepareStatement(deleteScheduleSql)) {
                        deleteStmt.setInt(1, scheduleId);
                        deleteStmt.executeUpdate();
                        System.out.println("Room schedule deleted successfully.");
                    }
                } else {
                    System.out.println("Schedule not found. No action taken.");
                    return;
                }
            }

            connection.commit();
        } catch (SQLException e) {
            System.err.println("Database error " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void scheduleRoom(Connection connection, int roomID, Date date, Time startTime, Time endTime) {
        // check for existing room schedule conflicts
        String checkConflictSql = "SELECT COUNT(*) FROM RoomSchedule WHERE RoomID = ? AND Date = ? AND NOT (EndTime <= ? OR StartTime >= ?)";

        // insert new schedule into RoomSchedule if no conflict is found
        String insertRoomScheduleSql = "INSERT INTO RoomSchedule (RoomID, Date, StartTime, EndTime) VALUES (?, ?, ?, ?)";

        try {
            try (PreparedStatement checkStmt = connection.prepareStatement(checkConflictSql)) {
                checkStmt.setInt(1, roomID);
                checkStmt.setDate(2, date);
                checkStmt.setTime(3, startTime);
                checkStmt.setTime(4, endTime);

                ResultSet conflictRs = checkStmt.executeQuery();

                if (conflictRs.next() && conflictRs.getInt(1) > 0) {
                    System.out.println("Schedule conflict detected for the room. No action taken.");
                    return;
                }
            }

            try (PreparedStatement insertStmt = connection.prepareStatement(insertRoomScheduleSql)) {
                insertStmt.setInt(1, roomID);
                insertStmt.setDate(2, date);
                insertStmt.setTime(3, startTime);
                insertStmt.setTime(4, endTime);

                int affectedRows = insertStmt.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Room scheduled successfully.");
                } else {
                    System.out.println("Failed to schedule the room.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void scheduleRoomForClass(Connection connection, int roomID, int fitnessClassID) {
        // Step 1: Fetch the date and time for the fitness class
        String fetchClassScheduleSql = "SELECT Date, StartTime, EndTime FROM ClassSchedule WHERE ClassID = ?";

        // Step 2: Check for schedule conflicts
        String checkConflictSql = "SELECT COUNT(*) FROM RoomSchedule WHERE RoomID = ? AND Date = ? AND NOT (EndTime <= ? OR StartTime >= ?)";

        // Step 3: Insert into RoomSchedule and update FitnessClass if no conflict
        String insertRoomScheduleSql = "INSERT INTO RoomSchedule (RoomID, StartTime, EndTime, Date) VALUES (?, ?, ?, ?)";
        String updateFitnessClassSql = "UPDATE FitnessClass SET RoomID = ? WHERE ClassID = ?";

        try {
            PreparedStatement fetchStmt = connection.prepareStatement(fetchClassScheduleSql);
            fetchStmt.setInt(1, fitnessClassID);
            ResultSet rs = fetchStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Fitness class schedule not found.");
                return;
            }

            java.sql.Date classDate = rs.getDate("Date");
            java.sql.Time startTime = rs.getTime("StartTime");
            java.sql.Time endTime = rs.getTime("EndTime");

            PreparedStatement checkStmt = connection.prepareStatement(checkConflictSql);
            checkStmt.setInt(1, roomID);
            checkStmt.setDate(2, classDate);
            checkStmt.setTime(3, startTime);
            checkStmt.setTime(4, endTime);
            ResultSet conflictRs = checkStmt.executeQuery();

            if (conflictRs.next() && conflictRs.getInt(1) > 0) {
                System.out.println("Schedule conflict detected for the room.");
                return;
            }

            connection.setAutoCommit(false);

            PreparedStatement insertStmt = connection.prepareStatement(insertRoomScheduleSql);
            insertStmt.setInt(1, roomID);
            insertStmt.setTime(2, startTime);
            insertStmt.setTime(3, endTime);
            insertStmt.setDate(4, classDate);
            insertStmt.executeUpdate();

            PreparedStatement updateStmt = connection.prepareStatement(updateFitnessClassSql);
            updateStmt.setInt(1, roomID);
            updateStmt.setInt(2, fitnessClassID);
            updateStmt.executeUpdate();

            connection.commit();
            System.out.println("Room scheduled successfully for the fitness class.");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            return;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static int getMemberUserId(Connection connection, String firstName, String lastName) {
        String query = "SELECT u.UserID FROM Users u " +
                "JOIN Member m ON u.UserID = m.MemberID " +
                "WHERE u.FirstName = ? AND u.LastName = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getInt("UserID");
            } else {
                System.out.println("Member not found or not a member.");
                return -1;
            }
        } catch (Exception e) {
            System.err.println("Database error: " + e.getMessage());
            return -1;
        }
    }

    public static void removeTrainerSchedule(Connection connection, int trainerId, Date date) {
        String deleteSql = "DELETE FROM TrainerSchedule WHERE TrainerID = ? AND Date = ?";

        try (PreparedStatement statement = connection.prepareStatement(deleteSql)) {
            statement.setInt(1, trainerId);
            statement.setDate(2, date);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("All time slots for the given date have been successfully removed.");
            } else {
                System.out.println("No time slots found for the given date");
            }
        } catch (Exception e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void memberScheduleManagement(Connection connection, BufferedReader reader, int id) {
        while (true) {
            try {
                System.out.println("\n1. View or Drop Current Classes/Sessions\n" +
                        "2. View or Register Available Fitness Classes\n" +
                        "3. Schedule a Personal Training Session\n" +
                        "4. Return to Main Menu\n" +
                        "Enter: ");

                int choice = Integer.parseInt(reader.readLine());
                int classID;
                switch (choice) {
                    case 1:
                        displayMemberSchedule(connection, id);
                        while (true) {
                            System.out.println("1. Drop a class\n" +
                                    "2. Back\n" +
                                    "Enter: ");
                            choice = Integer.parseInt(reader.readLine());
                            if (choice == 1) {
                                System.out.println("Enter the ID of the class you would like to drop: ");
                                classID = Integer.parseInt(reader.readLine());
                                dropClass(connection, id, classID);
                                break;
                            } else if (choice == 2) {
                                break;
                            } else {
                                System.out.println("Incorrect input");
                                continue;
                            }
                        }
                        break;
                    case 2:
                        displayOpenFitnessClasses(connection);
                        while (true) {
                            System.out.println("1. Join a class\n" +
                                    "2. Back\n" +
                                    "Enter: ");
                            choice = Integer.parseInt(reader.readLine());
                            if (choice == 1) {
                                System.out.println("Enter the ID of the class you would like to register: ");
                                classID = Integer.parseInt(reader.readLine());
                                joinClassById(connection, id, classID);
                                break;
                            } else if (choice == 2) {
                                break;
                            } else {
                                System.out.println("Incorrect input");
                                continue;
                            }
                        }
                        break;
                    case 3:
                        while (true) {
                            try {
                                displayTrainers(connection);
                                System.out.println("Enter the first name of the trainer you wish to book an appointment with:");
                                String firstName = reader.readLine();
                                System.out.println("Last name of the trainer: ");
                                String lastName = reader.readLine();

                                System.out.println("Date of schedule (YYYY-MM-DD): ");
                                String dateString = reader.readLine();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                java.util.Date utilDate = formatter.parse(dateString);
                                Date sessionDate = new Date(utilDate.getTime());

                                System.out.println("Start Time (HH:MM): ");
                                String timeString = reader.readLine();
                                formatter = new SimpleDateFormat("HH:mm");
                                utilDate = formatter.parse(timeString);
                                Time startTime = new Time(utilDate.getTime());

                                System.out.println("End Time (HH:MM): ");
                                String endtimeString = reader.readLine();
                                utilDate = formatter.parse(endtimeString);
                                Time endTime = new Time(utilDate.getTime());

                                bookPersonalSession(connection, firstName, lastName, id, sessionDate, startTime, endTime);
                                break;

                            } catch (Exception e) {
                                System.out.println("Incorrect input");
                                continue;
                            }

                        }
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Incorrect input");
                        continue;
                }

            } catch (IOException e) {
                System.out.println("Incorrect input");
                continue;
            }
        }
    }

    public static void bookPersonalSession(Connection connection, String firstName, String lastName, int memberId, Date sessionDate, Time startTime, Time endTime) {
        // Step 1: Find the trainer ID by first name and last name
        String findTrainerIdSql = "SELECT t.TrainerID FROM Trainer t JOIN Users u ON t.TrainerID = u.UserID WHERE u.FirstName = ? AND u.LastName = ?";
        int trainerId = -1;

        try (PreparedStatement ps = connection.prepareStatement(findTrainerIdSql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                trainerId = rs.getInt("TrainerID");
            } else {
                System.out.println("Trainer not found.");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Error finding trainer: " + e.getMessage());
            return;
        }

        // Step 2: Check trainer availability
        if (!checkTrainerAvailability(connection, trainerId, sessionDate, startTime, endTime)) {
            System.out.println("Trainer is not available at the requested time.");
            return;
        }

        // Step 3: Insert "Personal Session" and register the member
        try {
            connection.setAutoCommit(false);

            String insertClassSql = "INSERT INTO FitnessClass (ClassName, TrainerID, Status) VALUES ('Personal Session', ?, 'FULL')";
            try (PreparedStatement ps = connection.prepareStatement(insertClassSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, trainerId);
                int affectedRows = ps.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating class failed, no rows affected.");
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int classId = generatedKeys.getInt(1);

                        String registerSql = "INSERT INTO Register (MemberID, ClassID) VALUES (?, ?)";
                        try (PreparedStatement registerPs = connection.prepareStatement(registerSql)) {
                            registerPs.setInt(1, memberId);
                            registerPs.setLong(2, classId);
                            registerPs.executeUpdate();
                        }

                        String insertScheduleSql = "INSERT INTO ClassSchedule (ClassID, Date, StartTime, EndTime) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement schedulePs = connection.prepareStatement(insertScheduleSql)) {
                            schedulePs.setLong(1, classId);
                            schedulePs.setDate(2, sessionDate);
                            schedulePs.setTime(3, startTime);
                            schedulePs.setTime(4, endTime);
                            schedulePs.executeUpdate();
                        }
                    } else {
                        throw new SQLException("Creating class failed, no ID obtained.");
                    }
                }
            }

            connection.commit();
            System.out.println("Personal session booked successfully.");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException se) {
                System.err.println("Rollback error: " + se.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Auto-commit reset error: " + e.getMessage());
            }
        }
    }

    public static boolean checkTrainerAvailability(Connection connection, int trainerId, Date sessionDate, Time startTime, Time endTime) {
        // Check Trainer Personal Schedule Availability
        String checkTrainerScheduleSql = "SELECT * FROM TrainerSchedule WHERE TrainerID = ? AND Date = ? AND (StartTime <= ? AND EndTime >= ?)";
        // Check Trainer Class Schedule Availability
        String checkClassScheduleSql = "SELECT cs.* FROM ClassSchedule cs JOIN FitnessClass fc ON cs.ClassID = fc.ClassID WHERE fc.TrainerID = ? AND cs.Date = ? AND (cs.StartTime < ? AND cs.EndTime > ?)";

        try {
            try (PreparedStatement ps = connection.prepareStatement(checkTrainerScheduleSql)) {
                ps.setInt(1, trainerId);
                ps.setDate(2, sessionDate);
                ps.setTime(3, startTime);
                ps.setTime(4, endTime);

                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    return false;
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(checkClassScheduleSql)) {
                ps.setInt(1, trainerId);
                ps.setDate(2, sessionDate);
                ps.setTime(3, startTime);
                ps.setTime(4, endTime);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    System.out.println("Trainer is already scheduled for a class at the requested time.");
                    return false;
                }
            }

            return true;  // Trainer is available if there are no scheduling conflicts
        } catch (SQLException e) {
            System.err.println("Error checking trainer availability: " + e.getMessage());
            return false;
        }
    }

    public static void joinClassById(Connection connection, int memberId, int classId) {
        // Check if the class is open
        String checkClassStatusSql = "SELECT Status FROM FitnessClass WHERE ClassID = ?";

        String joinClassSql = "INSERT INTO Register (MemberID, ClassID) VALUES (?, ?)";

        try (PreparedStatement checkStatusPs = connection.prepareStatement(checkClassStatusSql)) {
            checkStatusPs.setInt(1, classId);
            ResultSet rs = checkStatusPs.executeQuery();

            if (rs.next() && "OPEN".equals(rs.getString("Status"))) {
                try (PreparedStatement joinClassPs = connection.prepareStatement(joinClassSql)) {
                    joinClassPs.setInt(1, memberId);
                    joinClassPs.setInt(2, classId);
                    joinClassPs.executeUpdate();
                    System.out.println("You've successfully joined the class with ClassID: " + classId);
                } catch (SQLException e) {
                    System.err.println("You already registered to the class or classID doesn't exist");
                }
            } else {
                System.out.println("The class with ClassID: " + classId + " might be full or finished.");
            }
        } catch (SQLException e) {
            System.err.println("Error checking class status: " + e.getMessage());
        }
    }

    public static void dropClass(Connection connection, int memberId, int classId) {
        String SQL = "DELETE FROM Register WHERE MemberID = ? AND ClassID = ?";

        try (PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, memberId);
            ps.setInt(2, classId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Successfully dropped the class with ClassID: " + classId);
            } else {
                System.out.println("Could not find the registration for ClassID: " + classId + " to drop, or you were not registered for it.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void profileManagement(Connection connection, BufferedReader reader, int id) {
        while (true) {
            System.out.println("\nProfile Management:\n" +
                    "1. Update Personal Information\n" +
                    "2. Update Fitness Goals\n" +
                    "3. Update Health Metrics\n" +
                    "4. Back to Main Menu\n" +
                    "Enter: ");
            try {
                int profileInput = Integer.parseInt(reader.readLine());
                switch (profileInput) {
                    case 1:
                        updatePersonalInformation(connection, reader, id);
                        break;
                    case 2:
                        displayFitnessGoals(connection, id);
                        updateFitnessGoals(connection, reader, id);
                        break;
                    case 3:
                        displayHealthMetrics(connection, id);
                        updateHealthMetrics(connection, reader, id);
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Incorrect input, please choose a valid option.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Incorrect input");
            }
        }
    }

    public static int getUserIdByEmail(Connection connection, String email) {
        String query = "SELECT UserID FROM Users WHERE Email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getInt("UserID");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return -1;
        }
    }


}