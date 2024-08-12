package com.academy;

import com.academy.models.Books;
import com.academy.models.User;

import java.awt.print.Book;
import java.util.ArrayList;

public class Main {
    public static void main(String[] arg) {
        User reader = new User();
        User author = new User();
        User admin = new User();
        //        For later use
//        User reader = new User("Ivan","ivan5","1234",Role.READER);
//        User author = new User("Hristiyan","hris33","12354654",Role.AUTHOR);
//        User admin = new User("Radoslav", "rado13", "12942", Role.ADMIN);
//        reader.deactivateAccount(author);
//        admin.deactivateAccount(author);

        author.insertBook();
        admin.insertBook();
        reader.insertBook();
        System.out.println();
        reader.printLibraryContent();
        System.out.println();
        ArrayList<Books> bookArr = reader.searchBook();
        ArrayList<Books> bookArr2 = reader.searchBook();

        reader.addBooksToPrivateLibrary(bookArr);
        reader.addBooksToPrivateLibrary(bookArr2);
        reader.readBook();
        reader.giveReview();
        reader.sortLibrary();
        author.viewBookStats(bookArr);

    }
}