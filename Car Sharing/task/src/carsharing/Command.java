package carsharing;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.SocketHandler;


public class Command {
    private enum Action {   CREATE_COMPANY,
                            CREATE_CAR,
                            CREATE_CUSTOMER,
                            CAR_LIST,
                            RENT_A_CAR,
                            MY_RENTED_CAR,
                            RETURN_CAR,
                            EXIT};
    private enum MenuType {MAIN, MANAGER, CHOOSE_COMPANY, COMPANY,
                            CHOOSE_CUSTOMER, CUSTOMER}

    private Action nextAction = Action.EXIT;
    private MenuType menu = MenuType.MAIN;

    private final String[] mainMenuOptions = {  "Log in as a manager",
                                                "Log in as a customer",
                                                "Create a customer",
                                                "Exit"};

    private final String[] managerMenuOptions = {   "Company list",
                                                    "Create a company",
                                                    "Back" };

    private final String[] companyMenuOptions = {   "Car list",
                                                    "Create a car",
                                                    "Back" };
    private final String[] customerMenuOptions = {  "Rent a car",
                                                    "Return a rented car",
                                                    "My rented car",
                                                    "Back" };

    private Scanner scanner = new Scanner(System.in);

    private String companyName = "";
    private String companyID = "";
    private String customerName = "";
    private String customerID = "";
    private boolean carReturned = false;

    public boolean finished() {
        return nextAction.equals(Action.EXIT);
    }

    public void getNextAction(DataBase db) {
        int choice = 0;
        boolean ok = false;

        do {
            switch (menu) {
                case MAIN:      choice = getMenuOption(mainMenuOptions);
                                switch (choice) {
                                    case 0:     menu = MenuType.MANAGER;
                                                ok = false;
                                                break;
                                    case 1:     menu = MenuType.CHOOSE_CUSTOMER;
                                                ok = false;
                                                break;
                                    case 2:     ok = true;
                                                nextAction = Action.CREATE_CUSTOMER;
                                                break;

                                    case 3:     ok = true;
                                                nextAction = Action.EXIT;
                                                break;

                                    default:    ok = false;
                                                break;
                                };
                                break;

                case MANAGER:   choice = getMenuOption(managerMenuOptions);
                                switch (choice) {
                                case 0:
                                    ok = false;
                                    menu = MenuType.CHOOSE_COMPANY;
                                    break;
                                case 1:
                                    ok = true;
                                    nextAction = Action.CREATE_COMPANY;
                                    break;
                                default:    //Choice 2 back or something else, back to main menu
                                    ok = false;
                                    menu = MenuType.MAIN;
                                    break;
                                };
                                break;

                case CHOOSE_COMPANY:
                    companyName = getCompany(db);
                    if (companyName == null) {
                        ok = false;
                        menu = MenuType.MANAGER;
                    } else {
                        menu = MenuType.COMPANY;
                        ok = false;
                    };
                    break;

                case COMPANY:
                    System.out.println("Company name " + companyName);
                    choice = getMenuOption(companyMenuOptions);
                    switch (choice) {
                        case 0: ok = true;
                                nextAction = Action.CAR_LIST;
                                break;
                        case 1: ok = true;
                                nextAction = Action.CREATE_CAR;
                                break;
                        case 2: ok = false;
                                menu = MenuType.MANAGER;
                                break;
                        default: ok = false;
                                // System.out.println("Invalid choice!, try again");
                                break; }
                    break;

                case CHOOSE_CUSTOMER:
                        customerName = getCustomer(db);
                        if (customerName == null) {
                            menu = MenuType.MAIN;
                        } else {
                            menu = MenuType.CUSTOMER;
                        }
                        ok = false;
                        break;

                case CUSTOMER:
                    choice = getMenuOption(customerMenuOptions);
                    switch (choice) {
                        case 0:
                            nextAction = Action.RENT_A_CAR;
                            ok = true;
                            break;
                        case 1:
                            nextAction = Action.RETURN_CAR;
                            ok = true;
                            break;
                        case 2:
                            nextAction = Action.MY_RENTED_CAR;
                            ok = true;
                            break;
                        default:
                            menu = MenuType.MAIN;
                            ok = false;
                    };


            };

        } while (!ok);

    };


    public void NextAction(DataBase db) {

        switch (nextAction) {

            case CREATE_COMPANY:    createCompany(db);
                                    break;
            case CREATE_CAR:        createCar(db);
                                    break;
            case CAR_LIST:          listCars(db);
                                    break;
            case CREATE_CUSTOMER:   createCustomer(db);
                                    break;
            case MY_RENTED_CAR:     myRentedCar(db);
                                    break;
            case RENT_A_CAR:        if (rentedCar(db) == null) {
                                        rentCar(db);
                                    } else {
                                        System.out.println("You've already rented a car!");
                                        System.out.println();
                                    };
                                    break;
            case RETURN_CAR:        returnCar(db);
                                    break;
            case EXIT:              System.out.println("Shutting down...");
                                    break;
            default:                System.out.println("Error, unknown command");
                                    break;


        };
    }

    private int getMenuOption(String[] options) {
        int result = 0;
        boolean ok = false;

        do {
            printMenu(options);

            result = getInt();
            System.out.println();


            if (0 <= result && result < options.length ) {
                ok = true;
            } else {
                System.out.println("Invalid choice!  Please, try again!");
                ok = false;
                result = -1;
            }
        } while (!ok);
        if (result == 0) {
            result = options.length - 1;
        } else {
            result--;
        }
        return result; //Return index of menu option
    };

    private void printMenu(String[] options) {
        for (int i = 0; i < options.length - 1; i++) {
            System.out.print(i + 1);
            System.out.print(". ");
            System.out.println(options[i]);
        }
        System.out.print("0. ");
        System.out.println(options[options.length - 1]);
        System.out.println();
    }
    private boolean listCompanies(DataBase db) {
        String[][] strings = db.query("SELECT NAME FROM COMPANY ORDER BY ID");
        if (strings == null ) {
            System.out.println("The company list is empty!");
            return false;
        } else {
            System.out.println("Choose a company:");
            for (int row = 0; row < strings.length; row++ ) {
                System.out.print(row + 1);
                System.out.println(". " + strings[row][0]);
            }
            return true;
        }
    };
    private String getCompany(DataBase db) {
        String[][] strings = db.query("SELECT NAME, ID FROM COMPANY ORDER BY ID");
        int choice = 0;
        boolean ok = false;
        String name = null;
        if (strings == null ) {
            System.out.println("The company list is empty!");
            return null;
        } else {
            System.out.println("Choose a company:");
            for (int row = 0; row < strings.length; row++ ) {
                System.out.print(row + 1);
                System.out.println(". " + strings[row][0]);
            };
            System.out.println("0. Back");

            do {
                choice = getInt();

                System.out.println();


                if (1 <= choice && choice <= strings.length ) {
                    ok = true;
                    companyID = strings[choice - 1][1];
                    name = strings[choice - 1][0];
                } else if (choice == 0) {
                    ok = true;
                    name = null;
                } else {
                    System.out.println("Invalid choice!  Please, try again!");
                    ok = false;
                }
            } while (!ok);

            return name;
        }
    };


    private void createCompany(DataBase db) {
        scanner.nextLine();
        System.out.println("Enter the company name:");
        String companyName = scanner.nextLine();
        String sql = "INSERT INTO COMPANY (NAME) VALUES( '" + companyName + "' )";
        db.updateQuery(sql);
        System.out.println();
    };

    private void createCar(DataBase db) {
        scanner.nextLine();
        System.out.println("Enter the car name:");
        String companyName = scanner.nextLine();
        String sql = "INSERT INTO CAR (COMPANY_ID, NAME) VALUES( " + companyID + ", '" + companyName + "')";
        db.updateQuery(sql);
        System.out.println("The car was added");
    };

    private void listCars(DataBase db) {
        String[][] strings = carList(db);
        if (strings == null ) {
            System.out.println("The car list is empty!");
        } else {
            System.out.println("Car list:");
            for (int row = 0; row < strings.length; row++ ) {
                System.out.print(row + 1);
                System.out.println(". " + strings[row][0]);
            };

        };
        System.out.println();
    };

    private String[][] carList(DataBase db) {
        return db.query("SELECT NAME, ID FROM CAR WHERE "
                + "COMPANY_ID = " + companyID
                + "  ORDER BY ID");
    };

    private String[][] availableCars(DataBase db) {
        return db.query("SELECT NAME, ID FROM CAR WHERE "
                + "COMPANY_ID = " + companyID
                + " AND ID NOT IN (SELECT RENTED_CAR_ID FROM CUSTOMER "
                            + "WHERE RENTED_CAR_ID IS NOT NULL) ORDER BY ID");
    };

    private void createCustomer(DataBase db) {
        scanner.nextLine();
        System.out.println("Enter the customer name:");
        String customerName = scanner.nextLine();
        String sql = "INSERT INTO CUSTOMER (NAME) VALUES('" + customerName +"')";
        db.updateQuery(sql);
        System.out.println("The customer was added");
    };

    private String getCustomer(DataBase db) {
        String[][] strings = db.query("SELECT NAME, ID FROM CUSTOMER ORDER BY ID");
        int choice = 0;
        boolean ok = false;
        String name = null;
        if (strings == null ) {
            System.out.println("The customer list is empty!");
            name = null;
        } else {
            System.out.println("Choose a customer:");
            for (int row = 0; row < strings.length; row++ ) {
                System.out.print(row + 1);
                System.out.println(". " + strings[row][0]);
            };
            System.out.println("0. Back");

            do {
                choice = getInt();

                System.out.println();


                if (1 <= choice && choice <= strings.length ) {
                    ok = true;
                    customerID = strings[choice - 1][1];
                    name = strings[choice - 1][0];
                    carReturned = false;
                } else if (choice == 0) {
                    ok = true;
                    name = null;
                } else {
                    System.out.println("Invalid choice!  Please, try again!");
                    ok = false;
                    name = null;
                }
            } while (!ok);


        }
        return name;
    };

    private void myRentedCar(DataBase db) {

         final String[][] queryResult = rentedCar(db);
         if (queryResult == null) {
             System.out.println("You didn't rent a car!");
         } else {
             System.out.println("Your rented car:");
             System.out.println(queryResult[0][0]);
             System.out.println("Company:");
             System.out.println(queryResult[0][1]);
         }
    };

    private String[][] rentedCar(DataBase db) {
        final String sql = "SELECT CAR.NAME, COMPANY.NAME FROM CUSTOMER "
                + "INNER JOIN CAR ON (CUSTOMER.RENTED_CAR_ID = CAR.ID) "
                + "INNER JOIN COMPANY ON (CAR.COMPANY_ID = COMPANY.ID) "
                + "WHERE CUSTOMER.ID = " + customerID;
        return db.query(sql);

    };

    private void rentCar(DataBase db) {
        companyName = getCompany(db);
        if (companyName == null) {
            menu = MenuType.CUSTOMER;
        } else {

            String[][] list = availableCars(db);
            if (list == null) {
                System.out.println("No available cars in the company " + companyName);
            } else {
                boolean finished = false;
                do {
                    System.out.println("Choose a car:");
                    for (int row = 0; row < list.length; row++) {
                        System.out.print(row + 1);
                        System.out.println(". " + list[row][0]);
                    }
                    ;
                    System.out.println("0. Back");
                    int choice = getInt();
                    if (choice == 0) {
                        finished = true;
                    } else if (choice < 0 || choice > list.length) {
                        finished = false;
                        System.out.println("Incorrect choice, try again!");
                    } else {
                        finished = true;
                        String sql = "UPDATE CUSTOMER SET RENTED_CAR_ID = "
                                + list[choice - 1][1]
                                + "WHERE ID = " + customerID;
                        db.updateQuery(sql);
                        System.out.println("You rented '" + list[choice - 1][0] + "'");
                        carReturned = false;
                    }
                } while (!finished);


            };
        };
    };

    private void returnCar(DataBase db) {
        if (rentedCar(db) == null) {
            if (carReturned) {
                System.out.println("You've already returned a rented car!");
            } else {
                System.out.println("You didn't rent a car!");
            };
        } else {
            final String sql = "UPDATE CUSTOMER SET RENTED_CAR_ID = NULL WHERE ID = " + customerID;
            db.updateQuery(sql);
            System.out.println("You've returned a rented car!");
            System.out.println();};
            carReturned = true;
    };

    private int getInt() {
        int i = -1;
        if (scanner.hasNextInt()) {
                i = scanner.nextInt();
        } else {
            scanner.nextLine();
        }
        return i;
    };
}
