package dk.topping.handin1;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import dk.topping.handin1.Models.Book;
import dk.topping.handin1.ViewModels.EditActivityViewModel;

import static dk.topping.handin1.MainActivity.BOOK_RETURN;

public class EditActivity extends AppCompatActivity {

    private EditText title;
    private EditText author;
    private EditText pageCount;
    private RadioGroup fiction;

    private EditActivityViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        vm = ViewModelProviders.of(this).get(EditActivityViewModel.class);

        title = findViewById(R.id.editTitleBox);
        author = findViewById(R.id.editAuthorBox);
        pageCount = findViewById(R.id.editPagesBox);
        fiction = findViewById(R.id.editRadioGroup);
        fiction.check(R.id.editRadioYes);

        vm.getBook().observe(this, book -> {
            updateUi(book);
        });

        Book book = (Book) getIntent().getSerializableExtra(BOOK_RETURN);
        if(book != null) {
            vm.setBook(book);
        }
    }

    public void cancelEdit(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void saveChanges(View view) {
        if (isValidInput()) {
            Book book = new Book(
                    author.getText().toString(),
                    title.getText().toString(),
                    Integer.parseInt(pageCount.getText().toString()),
                    fiction.getCheckedRadioButtonId() == R.id.editRadioYes);
            Intent intent = new Intent();
            intent.putExtra(BOOK_RETURN, book);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private boolean isValidInput() {
        boolean isValid = true;
        if(title.getText().toString().isEmpty()) {
            title.setError(getResources().getString(R.string.requiredError));
            isValid = false;
        }
        if(author.getText().toString().isEmpty()) {
            author.setError(getResources().getString(R.string.requiredError));
            isValid = false;
        }
        if(pageCount.getText().toString().isEmpty()) {
            pageCount.setError(getResources().getString(R.string.requiredError));
            isValid = false;
        }

        try {
            Integer.parseInt(pageCount.getText().toString());
        } catch (NumberFormatException e) {
            pageCount.setError(getResources().getString(R.string.notANumberError));
            isValid = false;
            return isValid;
        }

        return isValid;
    }

    private void updateUi(Book book) {
        if(book.isFiction) {
            fiction.check(R.id.editRadioYes);
        } else {
            fiction.check(R.id.editRadioNo);
        }
        title.setText(book.title);
        author.setText(book.author);
        pageCount.setText(Integer.toString(book.pageCount));
    }
}
