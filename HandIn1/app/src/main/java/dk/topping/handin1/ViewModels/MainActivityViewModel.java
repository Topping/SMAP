package dk.topping.handin1.ViewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;

import dk.topping.handin1.Models.Book;


public class MainActivityViewModel extends ViewModel {
    private MutableLiveData<Book> book;
    private MutableLiveData<Bitmap> bookCover;
    public LiveData<Bitmap> getCover() {
        if(bookCover == null) {
            bookCover = new MutableLiveData<>();
        }
        return bookCover;
    }
    public LiveData<Book> getBook() {
        if(book == null) {
            book = new MutableLiveData<>();
        }
        return book;
    }

    public void setBook(Book book) {
        this.book.setValue(book);
    }
    public void setCover(Bitmap cover) { this.bookCover.setValue(cover);}
}
