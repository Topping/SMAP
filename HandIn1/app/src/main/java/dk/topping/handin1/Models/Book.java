package dk.topping.handin1.Models;


import java.io.Serializable;

public class Book implements Serializable {
    public final String author;
    public final String title;
    public final int pageCount;
    public final boolean isFiction;
    public Book() {
        author = "";
        title = "";
        pageCount = 0;
        isFiction = false;
    }
    public Book(String author, String title, int pageCount, boolean isFiction) {
        this.author = author;
        this.title = title;
        this.pageCount = pageCount;
        this.isFiction = isFiction;
    }
}
