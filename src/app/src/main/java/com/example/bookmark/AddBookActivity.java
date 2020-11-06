package com.example.bookmark;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.bookmark.fragments.ImageSelectDialogFragment;

/**
 * This activity allows a user to add a new book. It provides fields
 * to enter all book details, scan isbn and add a photo.
 * <p>
 * TODO: Add more to these? Classes/Listeners?
 *
 * @author Mitch Adam.
 */
public class AddBookActivity extends AppCompatActivity implements ImageSelectDialogFragment.ImageSelectListener {
    // TODO: Figure out the back navigation
    private static final int ISBN_REQUEST_CODE = 100;
    private static final String IMG_SELECT_TAG = "ImageSelectFragment";

    private ImageButton scanISBNButton;
    private ImageButton addPhotoButton;
    private Button doneAddBookButton;

    private EditText titleEditText;
    private EditText authorEditText;
    private EditText isbnEditText;
    private EditText descriptionEditText;


    private View.OnClickListener scanISBNListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goToScanISBN();
        }
    };

    private View.OnClickListener addPhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addPhoto();
        }
    };

    private View.OnClickListener doneAddBookListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doneAddBook();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        getSupportActionBar().setTitle("Add Book");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        scanISBNButton = findViewById(R.id.add_book_scan_isbn_btn);
        scanISBNButton.setOnClickListener(scanISBNListener);

        addPhotoButton = findViewById(R.id.add_book_add_photo_btn);
        addPhotoButton.setOnClickListener(addPhotoListener);

        doneAddBookButton = findViewById((R.id.add_book_done_btn));
        doneAddBookButton.setOnClickListener(doneAddBookListener);

        titleEditText = findViewById(R.id.add_book_title_field);
        authorEditText = findViewById(R.id.add_book_author_field);
        isbnEditText = findViewById(R.id.add_book_isbn_field);
        descriptionEditText = findViewById(R.id.add_book_description_field);
    }

    private void goToScanISBN() {
        Intent intent = new Intent(AddBookActivity.this, ScanIsbnActivity.class);
        startActivityForResult(intent, ISBN_REQUEST_CODE);
    }

    private void addPhoto() {
       ImageSelectDialogFragment.newInstance().show(getSupportFragmentManager(), IMG_SELECT_TAG);
    }

    public void onImageSelect(Uri uri) {
        // TODO: Save URI for when creating a book class
        addPhotoButton.setImageURI(uri);
    }

    private void doneAddBook() {
        // TODO: Create a book class and return isbn possibly?
        // TODO: Call uriToPhotograph() only here, once the done button has been pressed (Uri to Bitmap is relatively expensive)
        Log.d("Add Book", "Click done add book");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) { return; }

        // Get ISBN
        if (requestCode == ISBN_REQUEST_CODE) {
            String isbn = data.getStringExtra("ISBN");
            isbnEditText.setText(isbn);
        }
    }
}
