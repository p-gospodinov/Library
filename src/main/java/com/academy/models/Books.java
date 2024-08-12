package com.academy.models;

import com.academy.JDBCConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Books {
    private String nameBook;
    private String genre;
    private  int pages;
    private int stars;
    private ArrayList<Integer> reviews;
    private int authorID;
    private boolean makePublic;

    public Books(String nameBook, String genre, int pages, int stars, int author, boolean makePublic) {
        this.nameBook = nameBook;
        this.genre = genre;
        this.pages = pages;
        this.stars = stars;
        this.authorID = author;
        this.makePublic = makePublic;
    }

    public Books(String nameBook, String genre, int pages, int stars, int author) {
        this.nameBook = nameBook;
        this.genre = genre;
        this.pages = pages;
        this.stars = stars;
        this.authorID = author;
    }
    public String getNameBook() {
        return nameBook;
    }

    public void setNameBook(String nameBook) {
        this.nameBook = nameBook;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public ArrayList<Integer> getReviews() {
        return reviews;
    }

    public void setReviews(int review) {
        this.reviews.add(review);
    }

    public int getAuthorID() {
        return authorID;
    }

    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }

    public int getAuthor() {
        return authorID;
    }

    public void setAuthor(int author) {
        this.authorID = author;
    }

    public void insertBookDB() {
        String insertQuery = "INSERT INTO books(BookTitle, Genre, Pages, Rating, AuthorID,InPublic) VALUES(?,?,?,?,?,?)";
        try {
            JDBCConnectionManager connectionManager = JDBCConnectionManager.getInstance();
            Connection connection = connectionManager.getConnection();
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            insertStatement.setString(1, this.nameBook);
            insertStatement.setString(2, this.genre);
            insertStatement.setInt(3, this.pages);
            insertStatement.setInt(4, this.stars);
            insertStatement.setInt(5, this.authorID);
            insertStatement.setBoolean(6, this.makePublic);
            int rowsInserted = insertStatement.executeUpdate();
            System.out.println(rowsInserted + " book(s) inserted.");
            connectionManager.closeConnection();
        } catch (SQLException e) {
            System.err.println("Error uploading book to library: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Books{" +
                "nameBook='" + nameBook + '\'' +
                ", genre='" + genre + '\'' +
                ", pages=" + pages +
                ", stars=" + stars +
                ", reviews=" + reviews +
                ", authorID=" + authorID +
                ", makePublic=" + makePublic +
                '}';
    }
}

