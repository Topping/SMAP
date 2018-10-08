package dk.topping.handin1;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import dk.topping.handin1.Models.Book;
import dk.topping.handin1.ViewModels.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {

    private static final int DETAILS_REQUEST_CODE = 6969;
    public static final String BOOK_RETURN = "BOOK_RETURN";

    private TextView author;
    private TextView title;
    private ImageView image;

    private MainActivityViewModel vm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        author = findViewById(R.id.bookAuthor);
        title = findViewById(R.id.bookTitle);
        image = findViewById(R.id.bookImage);
        vm = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        vm.getBook().observe(this, book -> {
            updateUI(book);
        });
        vm.getCover().observe(this, cover -> {
            image.setImageBitmap(cover);
        });
    }

    public void showDetails(View view) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(BOOK_RETURN, vm.getBook().getValue());
        startActivityForResult(intent, DETAILS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DETAILS_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Book book = (Book)data.getSerializableExtra(BOOK_RETURN);
                vm.setBook(book);
            }
        }
    }

    private void updateUI(Book book) {
        author.setText(book.author);
        title.setText(book.title);
        if(book.isFiction) {
            vm.setCover(BitmapFactory.decodeResource(getResources(), R.drawable.anders));
        } else {
            vm.setCover(BitmapFactory.decodeResource(getResources(), R.drawable.face));
        }
    }
}
