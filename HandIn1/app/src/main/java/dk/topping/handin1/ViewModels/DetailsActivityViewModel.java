package dk.topping.handin1.ViewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import dk.topping.handin1.Models.Book;


public class DetailsActivityViewModel extends ViewModel {
    private MutableLiveData<Book> book;
    public LiveData<Book> getBook() {
        if(book == null) {
            book = new MutableLiveData<>();
        }
        return book;
    }

    public void setBook(Book book) {
        this.book.setValue(book);
    }
}
