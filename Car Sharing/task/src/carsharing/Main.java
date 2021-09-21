package carsharing;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class Main {


    public static void main(String[] args) {

        String name = "carsharing";
        // Init database & open connection

        if (args.length == 2) {
            if (args[0] == "-databaseFileName") {
                name = args[1];
            };
        };
        DataBase db = new DataBase(name);

        //Create table COMPANY  if it does not exist
        System.out.println("Creating table COMPANY...");
        db.updateQuery("CREATE TABLE IF NOT EXISTS COMPANY "
                                + "(ID INT PRIMARY KEY AUTO_INCREMENT, "
                                + "NAME VARCHAR(255) UNIQUE NOT NULL)" );

        //Create table CAR  if it does not exist
        System.out.println("Creating table CAR...");
        db.updateQuery("CREATE TABLE IF NOT EXISTS CAR "
                + "(ID INT PRIMARY KEY AUTO_INCREMENT, "
                + "NAME VARCHAR(255) UNIQUE NOT NULL, "
                + "COMPANY_ID INT NOT NULL, "
                + "CONSTRAINT fk_company_id FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID) )"
                );

        //Create table CUSTOMER  if it does not exist
        System.out.println("Creating table CUSTOMER...");
        db.updateQuery("CREATE TABLE IF NOT EXISTS CUSTOMER "
                + "(ID INT PRIMARY KEY AUTO_INCREMENT, "
                + "NAME VARCHAR(255) UNIQUE NOT NULL, "
                + "RENTED_CAR_ID INT, "
                + "CONSTRAINT fk_rented_car_id FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR(ID) )"
        );



        Command c = new Command();
        do {    c.getNextAction(db);
                c.NextAction(db);

        } while (!c.finished());


        //Dropping tables is necessary to past tests
        // System.out.println("Cleaning up...");
        // db.updateQuery("DROP TABLE CUSTOMER");
        // db.updateQuery("DROP TABLE CAR");
        // db.updateQuery("DROP TABLE COMPANY");



        //Close connection and finish
        System.out.println("Closing connection...");
        db.closeConnection();
        System.out.println("Goodbye!");
    }
}
