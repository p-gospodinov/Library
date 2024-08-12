package com.academy.models;

import com.academy.JDBCConnectionManager;
import com.academy.models.enums.Role;

import java.sql.*;
import java.util.*;

public class User {
    private String name;
    private String username;
    private String password;
    private Role role;
    private boolean isActive = false;

    public User() {
        this.isActive = true;
        getInfo();
        loginUser();
    }

    public User(String name, String username, String password, Role role) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isActive = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void getInfo(){
        System.out.println("----Creating account----");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your name:");
        this.name = scanner.nextLine();
        System.out.println("Please enter your username:");
        this.username = scanner.nextLine();
        System.out.println("Please enter your password:");
        this.password = scanner.nextLine();
        System.out.println("Please select a role from: reader, author and admin");
        String role = scanner.nextLine();
        if(role.equals("reader")){
            this.role = Role.READER;
        }
        else if(role.equals("author")){
            this.role = Role.AUTHOR;
        }
        else if(role.equals("admin")){
            this.role = Role.ADMIN;
        }
        else{
            System.out.println("Enter a valid role!");
        }
    }

    public void loginUser(){
        String insertQuery = "INSERT INTO users(Name, Username, Password, Role) VALUES(?,?,?,?)";

        try {
            JDBCConnectionManager connectionManager = JDBCConnectionManager.getInstance();
            Connection connection = connectionManager.getConnection();
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setString( 1, this.name);
            insertStatement.setString( 2, this.username);
            insertStatement.setString(3, this.password);
            insertStatement.setString(4,this.role.toString());
            int rowsInserted = insertStatement.executeUpdate();
            System.out.println("Your account have been created successfully!");
            connectionManager.closeConnection();
        } catch (SQLException e) {
            System.err.println("Грешка при вмъкване на потребител: "+ e.getMessage());
        }
    }

    public ArrayList<Books> searchBook() {
        ArrayList<Books> filteredBooks = new ArrayList<>();
        System.out.println("How would you like to search the book?");
        System.out.println("If you want to search by book heading type: 1");
        System.out.println("If you want to search by author ID type: 2");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if(choice == 1) {
            System.out.println("What is the heading of the book you are looking for?");
            scanner.nextLine();
            String bookTitle = scanner.nextLine();
            String selectQuery = "SELECT * FROM books WHERE BookTitle = '" + bookTitle + "'";
            try {
                JDBCConnectionManager connectionManager = JDBCConnectionManager.getInstance();
                Connection connection = connectionManager.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(selectQuery);
                while (resultSet.next()) {
                    String bookName = resultSet.getString("BookTitle");
                    String genre = resultSet.getString("Genre");
                    int pagesNum = resultSet.getInt("Pages");
                    int stars = resultSet.getInt("Rating");
                    int authorID = resultSet.getInt("AuthorID");
                    Books book = new Books(bookName, genre, pagesNum, stars, authorID);
                    filteredBooks.add(book);
                }
                connectionManager.closeConnection();
            } catch (SQLException e) {
                System.err.println("Грешка при извличне на книга по заглавие: " + e.getMessage());
            }
        } else if (choice == 2) {
        System.out.println("What is the ID of the author that wrote the book you are looking for?");
                int authorId = scanner.nextInt();
                String selectQuery2 = "SELECT * FROM books WHERE AuthorID = '" + authorId + "'";
                try {
                    JDBCConnectionManager connectionManager = JDBCConnectionManager.getInstance();
                    Connection connection = connectionManager.getConnection();
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(selectQuery2);
                    while (resultSet.next()) {
                        String bookName = resultSet.getString("BookTitle");
                        String genre = resultSet.getString("Genre");
                        int pagesNum = resultSet.getInt("Pages");
                        int stars = resultSet.getInt("Rating");
                        int authorID = resultSet.getInt("AuthorID");
                        Books book = new Books(bookName, genre, pagesNum, stars, authorID);
                        filteredBooks.add(book);
                    }
                    connectionManager.closeConnection();
                } catch (SQLException e) {
                    System.err.println("Грешка при вмъкване на потребител по име на автор: " + e.getMessage());
                }
        }
            return filteredBooks;
        }

    public void addBooksToPrivateLibrary(ArrayList<Books> books) {
        if(this.isActive == true) {
        ArrayList<String> queries = new ArrayList<>();
        String createTableQuery = "CREATE TABLE IF NOT EXISTS "+this.name+" " +
                                  "(BookID int NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                                  "BookTitle varchar(255) NOT NULL, " +
                                  "Genre varchar(255) NOT NULL, " +
                                  "Pages int NOT NULL, " +
                                  "Rating int NOT NULL," +
                                  "AuthorID int NOT NULL," +
                                  "IsRead boolean )";
        queries.add(createTableQuery);
        boolean IsBookRead = false;
        for(Books book : books){
            String insertBook = "INSERT INTO "+this.name+"(BookTitle, Genre, Pages, Rating, AuthorID, IsRead) VALUES('"+book.getNameBook()+"','"+book.getGenre()+"',"+book.getPages()+","+book.getStars()+","+book.getAuthorID()+","+IsBookRead+")";
            queries.add(insertBook);
        }
        try {
            JDBCConnectionManager connectionManager = JDBCConnectionManager.getInstance();
            Connection connection = connectionManager.getConnection();
            Statement statement = connection.createStatement();
            for(String query : queries) {
                statement.addBatch(query);
            }
            statement.executeBatch();
            connectionManager.closeConnection();
        } catch (SQLException e) {
            System.err.println("Грешка при създаване на таблица: "+ e.getMessage());
        }
        }
        else {
            System.out.println("Your account is deactivated!");
        }
    }

    public void readBook(){
        if(this.isActive == true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("These are the books in your private library:");
            String selectQuery2 = "SELECT * FROM " + this.name ;
            try {
                JDBCConnectionManager connectionManager = JDBCConnectionManager.getInstance();
                Connection connection = connectionManager.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(selectQuery2);
                while (resultSet.next()) {
                    System.out.print("ID: " + resultSet.getInt("BookID"));
                    System.out.print(", Title: " + resultSet.getString("BookTitle"));
                    System.out.print(", Genre: " + resultSet.getString("Genre"));
                    System.out.print(", Pages: " + resultSet.getInt("Pages"));
                    System.out.print(", Rating: " + resultSet.getInt("Rating"));
                    System.out.println(", AuthorID: " + resultSet.getInt("AuthorID"));
                }
                connectionManager.closeConnection();
            } catch (SQLException e) {
                System.err.println("Грешка при вмъкване на потребител по име на автор: " + e.getMessage());
            }

            System.out.println("Select which book do you want to read by book ID:");
            int bookID = scanner.nextInt();

        boolean setTrue = true;
        String updateQuery = "UPDATE " + this.name +
                             " SET IsRead = " + setTrue +
                             " WHERE BookID = " + bookID + ";";
        try {
            JDBCConnectionManager connectionManager = JDBCConnectionManager.getInstance();
            Connection connection = connectionManager.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate(updateQuery);
            connectionManager.closeConnection();
        } catch (SQLException e) {
            System.err.println("Грешка при четене на книга: "+ e.getMessage());
        }
        System.out.println("Book with ID: '" + bookID + "' has been read!");
        }
        else {
            System.out.println("Your account is deactivated!");
        }
    }

    public void printReadBooks(){
        if(this.isActive == true) {
        System.out.println("These are the books you have read:");
        String selectQuery2 = "SELECT * FROM " + this.name +" WHERE IsRead = " + true ;
        try {
            JDBCConnectionManager connectionManager = JDBCConnectionManager.getInstance();
            Connection connection = connectionManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(selectQuery2);
            while (resultSet.next()) {
                System.out.print("ID: " + resultSet.getInt("BookID"));
                System.out.print(", Title: " + resultSet.getString("BookTitle"));
                System.out.print(", Genre: " + resultSet.getString("Genre"));
                System.out.print(", Pages: " + resultSet.getInt("Pages"));
                System.out.print(", Rating: " + resultSet.getInt("Rating"));
                System.out.println(", AuthorID: " + resultSet.getInt("AuthorID"));
            }
            connectionManager.closeConnection();
        } catch (SQLException e) {
            System.err.println("Грешка при вмъкване на потребител по име на автор: " + e.getMessage());
        }
        }
        else {
            System.out.println("Your account is deactivated!");
        }
    }

    public void sortLibrary(){
        if(this.isActive == true) {
        String orderBy = null;
        Scanner scanner = new Scanner(System.in);
        System.out.println("How do you want to sort your library: by Author, by Genre or by Title");
        System.out.println("Press '1' for sorting by author!");
        System.out.println("Press '2' for sorting by genre!");
        System.out.println("Press '3' for sorting by title!");
        int input = scanner.nextInt();
        if(input == 1){
            orderBy = "AuthorID";
        } else if (input == 2) {
            orderBy = "Genre";
        } else if (input == 3) {
            orderBy = "BookTitle";
        }
        else {
            System.out.println("Please enter a valid value!");
        }


        String selectQuery2 = "SELECT * FROM " + this.name +" ORDER BY " + orderBy;
        try {
            JDBCConnectionManager connectionManager = JDBCConnectionManager.getInstance();
            Connection connection = connectionManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(selectQuery2);
            while (resultSet.next()) {
                System.out.print("ID: " + resultSet.getInt("BookID"));
                System.out.print(", Title: " + resultSet.getString("BookTitle"));
                System.out.print(", Genre: " + resultSet.getString("Genre"));
                System.out.print(", Pages: " + resultSet.getInt("Pages"));
                System.out.print(", Rating: " + resultSet.getInt("Rating"));
                System.out.println(", AuthorID: " + resultSet.getInt("AuthorID"));
            }
            connectionManager.closeConnection();
        } catch (SQLException e) {
            System.err.println("Грешка при вмъкване на потребител по име на автор: " + e.getMessage());
        }
        }
        else {
            System.out.println("Your account is deactivated!");
        }
    }

    public void giveReview(){
        if(this.isActive == true) {
        Scanner scanner = new Scanner(System.in);
        printReadBooks();
        System.out.println("Please select which book do you want to rate by typing it's ID:");
        int selectedID = scanner.nextInt();
        System.out.println("Please leave a review using a number from 1 to 10");
        int review = scanner.nextInt();
        String updateQuery = "UPDATE " + this.name +
                " SET Rating = " + review +
                " WHERE BookID = " + selectedID + ";";
        try {
            JDBCConnectionManager connectionManager = JDBCConnectionManager.getInstance();
            Connection connection = connectionManager.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate(updateQuery);
            connectionManager.closeConnection();
        } catch (SQLException e) {
            System.err.println("Грешка при четене на книга: "+ e.getMessage());
        }
        }
        else {
            System.out.println("Your account is deactivated!");
        }
    }

    public void printLibraryContent(){
        System.out.println("These are the books in main library:");
        String selectQuery2 = "SELECT * FROM books" ;
        try {
            JDBCConnectionManager connectionManager = JDBCConnectionManager.getInstance();
            Connection connection = connectionManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(selectQuery2);
            while (resultSet.next()) {
                System.out.print("ID: " + resultSet.getInt("BookID"));
                System.out.print(", Title: " + resultSet.getString("BookTitle"));
                System.out.print(", Genre: " + resultSet.getString("Genre"));
                System.out.print(", Pages: " + resultSet.getInt("Pages"));
                System.out.print(", Rating: " + resultSet.getInt("Rating"));
                System.out.println(", AuthorID: " + resultSet.getInt("AuthorID"));
            }
            connectionManager.closeConnection();
        } catch (SQLException e) {
            System.err.println("Грешка при принтиране на съдъжанието на библиотеката: " + e.getMessage());
        }
    }



    public void deactivateAccount(User user){
        if(this.isActive == true) {
            if (this.role.name() == "ADMIN") {
                user.isActive = false;
                System.out.println("User " + user.getName() + " has been deactivated!");
            } else {
                System.out.println("You do not have the permission to perform this operation!");
            }
        }
        else {
            System.out.println("Your account is deactivated!");
        }
    }

    public void insertBook() {
        if(this.isActive == true && this.role == Role.AUTHOR || this.role == Role.ADMIN) {
            String nameBook;
            String genre;
            Scanner scanner = new Scanner(System.in);
            System.out.println();
            System.out.println("----Add a book----");
            System.out.print("Book Name: ");
            nameBook = scanner.nextLine();
            System.out.print("Genre: ");
            genre = scanner.nextLine();

            int pages = 0;
            boolean validPages = false;
            while (!validPages) {
                try {
                    System.out.print("Pages: ");
                    pages = Integer.parseInt(scanner.nextLine());
                    validPages = true;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number for pages.");
                }
            }

            int stars = 0;
            boolean validStars = false;
            while (!validStars) {
                try {
                    System.out.print("Stars: ");
                    stars = Integer.parseInt(scanner.nextLine());
                    validStars = true;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number for stars.");
                }
            }

            System.out.print("Author ID:");
            int authorID = Integer.parseInt(scanner.nextLine());

            System.out.println("Is it accessible to readers? (true/false)");
            boolean makePublish = Boolean.parseBoolean(scanner.nextLine());

            Books book = new Books(nameBook, genre, pages, stars, authorID, makePublish);
            book.insertBookDB();
        }
        else {
            System.out.println("You don't have permission to perform this operation!");
        }
    }

    public void viewBookStats(ArrayList<Books> authorBooks) {
        if (this.isActive == true && this.role == Role.AUTHOR || this.role == Role.ADMIN) {
            int authorID = authorBooks.get(0).getAuthorID();
            String selectQuery = "SELECT BookTitle, COUNT(*) AS LibrariesCount, AVG(Rating) AS AverageRating " +
                    "FROM books " +
                    "WHERE AuthorID = " + authorID + " " +
                    "GROUP BY BookTitle;";

            try {
                JDBCConnectionManager connectionManager = JDBCConnectionManager.getInstance();
                Connection connection = connectionManager.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(selectQuery);

                while (resultSet.next()) {
                    String bookTitle = resultSet.getString("BookTitle");
                    int librariesCount = resultSet.getInt("LibrariesCount");
                    double averageRating = resultSet.getDouble("AverageRating");
                    System.out.println("Book Title: " + bookTitle);
                    System.out.println("Number of Libraries: " + librariesCount);
                    System.out.println("Average Rating: " + averageRating);
                    System.out.println("-----------------------------");
                }

                connectionManager.closeConnection();

            } catch (SQLException e) {
                System.err.println("Error retrieving book stats: " + e.getMessage());
            }
        }
        else {
            System.out.println("You don't have permission to perform this operation!");
        }
    }

}
