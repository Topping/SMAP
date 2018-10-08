package dk.topping.handin1;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import dk.topping.handin1.Models.Book;
import dk.topping.handin1.ViewModels.DetailsActivityViewModel;
import static dk.topping.handin1.MainActivity.BOOK_RETURN;

public class DetailsActivity extends AppCompatActivity {

    public static final int EDIT_REQUEST_CODE = 420;

    private TextView title;
    private TextView author;
    private TextView pages;
    private TextView fiction;

    private DetailsActivityViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        title = findViewById(R.id.detailsTitleValue);
        author = findViewById(R.id.detailsAuthorValue);
        pages = findViewById(R.id.detailsPagesValue);
        fiction = findViewById(R.id.detailsFictionValue);

        vm = ViewModelProviders.of(this).get(DetailsActivityViewModel.class);
        vm.getBook().observe(this, book -> {
            updateUi(book);
        });

        Book book = (Book) getIntent().getSerializableExtra(BOOK_RETURN);
        if(book != null) {
            vm.setBook(book);
        }

    }

    public void returnToMain(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void editDetails(View view) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(BOOK_RETURN, vm.getBook().getValue());
        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    private void updateUi(Book book) {
        title.setText(book.title);
        author.setText(book.author);
        pages.setText(Integer.toString(book.pageCount));
        fiction.setText(book.isFiction ? "Yes" : "No" );
    }


}
