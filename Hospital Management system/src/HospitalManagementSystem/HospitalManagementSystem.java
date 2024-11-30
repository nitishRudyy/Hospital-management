package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;



public class HospitalManagementSystem {
    private static final String url ="jdbc:mysql://localhost:3306/database";
    private static final String username ="root";
    private static final String password ="password";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);

        try {
            Connection connection = DriverManager.getConnection(url,username,password);
            Patient patient = new Patient(connection,scanner);
            Doctor doctor = new Doctor(connection);
            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. View patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book appointment");
                System.out.println("5. Exit");
                System.out.println("Enter you choice");
                int choice = scanner.nextInt();

                switch (choice){
                    case 1:
                        // ADD Patient
                        patient.addpatient();
                        break;
                    case 2:
                        //View Patient
                        patient.viewpatients();
                        break;
                    case 3:
                        // View Doctors
                        doctor.viewDoctors();
                        break;
                    case 4:
                        // Book Appointments
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;

                    case 5:
                        return;
                    default:
                        System.out.println("Enter valid choice!!!");
                        break;
                }

            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void bookAppointment(Patient patient,Doctor doctor,Connection connection,Scanner scanner){
        System.out.println("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.println("Enter Doctor Id: ");
        int doctortId = scanner.nextInt();
        System.out.println("Enter appointnment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();
        if(patient.getpatientbyid(patientId) && doctor.getDoctorbyid(doctortId)){
           if(checkDoctorAvailability(doctortId,appointmentDate,connection)){
               String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id,  appointment_date) VALUES(?,?,?)";
          try {
              PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
              preparedStatement.setInt(1,patientId);
              preparedStatement.setInt(2,doctortId);
              preparedStatement.setString(3,appointmentDate);
              int rowaffected = preparedStatement.executeUpdate();
              if (rowaffected>0){
                  System.out.println("Appointment Booked!");
              }else {
                  System.out.println("Failed Booked Appointment");
              }
          }catch (SQLException e){
              e.printStackTrace();
          }
           }
        }else{
            System.out.println("Either doctor or patient doesn't exist");
        }

    }

     public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointment WHERE doctor_id = ? AND appointment_date = ?";

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                int count = resultSet.getInt(1);
                if (count==0){
                    return true;
                }else{
                    return false;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }


}
