package wladcom.example.board.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import wladcom.example.board.R;
import wladcom.example.board.adapters.NoteAdapter;
import wladcom.example.board.models.Board;
import wladcom.example.board.models.Note;

public class NoteActivity extends AppCompatActivity implements RealmChangeListener<Board> {

    private ListView listView;
    private FloatingActionButton fab;

    private NoteAdapter adapter;
    private RealmList<Note> notes;
    private Realm realm;

    private int boardId;
    private Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);


        realm = Realm.getDefaultInstance();

        if(getIntent().getExtras() != null){
            boardId = getIntent().getExtras().getInt("boardId");
        }

        //
        board = realm.where(Board.class).equalTo("id",boardId).findFirst();
        board.addChangeListener(this);
        notes = board.getNotes();

        this.setTitle(board.getTitle());


        fab = findViewById(R.id.fabAddNote);
        listView = findViewById(R.id.listViewNote);
        adapter = new NoteAdapter(this, notes, R.layout.list_view_note_item);

        listView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertForCreatingNote("Add new note", "Type a note for "+ board.getTitle()+ ".");
            }
        });

        registerForContextMenu(listView);
    }

    //Crud

    private void createNewNote(String note){
        realm.beginTransaction();
        Note _note = new Note(note);
        realm.copyToRealm(_note);
        //para guardar con la relación
        board.getNotes().add(_note);
        realm.commitTransaction();
    }

    private void updateNote(String description ,Note note){
        realm.beginTransaction();
        note.setDescription(description);
        realm.copyToRealmOrUpdate(board);
        realm.commitTransaction();
    }

    private void deleteNote(Note note){
        realm.beginTransaction();
        note.deleteFromRealm();
        realm.commitTransaction();
    }

    private void deleteAllNotes(){
        realm.beginTransaction();
        board.getNotes().deleteAllFromRealm();
        realm.commitTransaction();
    }


    private void showAlertForCreatingNote(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(title != null) builder.setTitle(title);
        if(message != null) builder.setMessage(message);

        View viewInflate = LayoutInflater.from(this).inflate(R.layout.dialog_create_board, null);
        builder.setView(viewInflate);

        final EditText input = viewInflate.findViewById(R.id.editTextNewBoard);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String note = input.getText().toString().trim();
                if(note.length() > 0)
                    createNewNote(note);
                else
                    Toast.makeText(NoteActivity.this, "The note can't not is empty", Toast.LENGTH_LONG).show();
            }


        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showAlertForUpdateNote(String title, String message,final Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(title != null) builder.setTitle(title);
        if(message != null) builder.setMessage(message);

        View viewInflate = LayoutInflater.from(this).inflate(R.layout.dialog_create_board, null);
        builder.setView(viewInflate);

        final EditText input = viewInflate.findViewById(R.id.editTextNewBoard);
        input.setText(note.getDescription());

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String Description = input.getText().toString().trim();
                if(Description.length() == 0){
                    Toast.makeText(NoteActivity.this, "The name is required to edit current Board", Toast.LENGTH_LONG).show();
                }else if(Description.equals(board.getTitle())){
                    Toast.makeText(NoteActivity.this, "The name is the same than is was before", Toast.LENGTH_LONG).show();
                }else{
                    updateNote(Description, note);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.deleteAll:
                deleteAllNotes();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        getMenuInflater().inflate(R.menu.context_menu_board_activity,menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.deletBoard:
                deleteNote(notes.get(info.position));
                return true;
            case R.id.editBoard:
                showAlertForUpdateNote("Edit Board", "change the name on the board", notes.get(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    @Override
    public void onChange(Board board) {
        adapter.notifyDataSetChanged();
    }
}