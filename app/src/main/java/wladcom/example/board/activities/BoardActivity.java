package wladcom.example.board.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import wladcom.example.board.BuildConfig;
import wladcom.example.board.R;
import wladcom.example.board.adapters.BoardAdapter;
import wladcom.example.board.models.Board;

//cuando se actualiza la lista de Boards de forma más limpia
public class BoardActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<Board>>, AdapterView.OnItemClickListener {

    private Realm realm;
    private FloatingActionButton fab;
    private ListView listView;
    private BoardAdapter adapter;


    private RealmResults<Board> boards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        //Inicializa la bd
        realm = Realm.getDefaultInstance();
        boards = realm.where(Board.class).findAll();
        boards.addChangeListener(this);
        //cuando se actualiza la lista de Boards
        /*boards.addChangeListener(new RealmChangeListener<RealmResults<Board>>() {
            @Override
            public void onChange(RealmResults<Board> boards) {

            }
        });*/

        //ListView
        listView = findViewById(R.id.listViewBoard);

        adapter = new BoardAdapter(this, boards, R.layout.list_view_board_item);

        //añadirle el adaptador a la vista
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);

        //inicializa el botón
        fab = findViewById(R.id.fabAddBoard);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertForCreatingBoard("Add new Board", "Type a name for you new board");
            }
        });

        registerForContextMenu(listView);

        //Borra toda la bd
        /*realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();*/
    }

    //Crud Actions
    private void createNewBoard(String boardName){
        realm.beginTransaction();
        Board board = new Board(boardName);
        realm.copyToRealm(board);
        realm.commitTransaction();
    }

    private void updateBoard(String newName ,Board board){
        realm.beginTransaction();
        board.setTitle(newName);
        realm.copyToRealmOrUpdate(board);
        realm.commitTransaction();
    }

    private void deleteBoard(Board board){
        realm.beginTransaction();
        board.deleteFromRealm();
        realm.commitTransaction();
    }


    //Dialog
    private void showAlertForCreatingBoard(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(title != null) builder.setTitle(title);
        if(message != null) builder.setMessage(message);

        View viewInflate = LayoutInflater.from(this).inflate(R.layout.dialog_create_board, null);
        builder.setView(viewInflate);

        final EditText input = viewInflate.findViewById(R.id.editTextNewBoard);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String boardName = input.getText().toString().trim();
                if(boardName.length() > 0)
                    createNewBoard(boardName);
                else
                    Toast.makeText(BoardActivity.this, "The name is required to create  a bew Board", Toast.LENGTH_LONG).show();
            }


        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertForUpdate(String title, String message,final Board board) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(title != null) builder.setTitle(title);
        if(message != null) builder.setMessage(message);

        View viewInflate = LayoutInflater.from(this).inflate(R.layout.dialog_create_board, null);
        builder.setView(viewInflate);

        final EditText input = viewInflate.findViewById(R.id.editTextNewBoard);
        input.setText(board.getTitle());

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String boardName = input.getText().toString().trim();
                if(boardName.length() == 0){
                    Toast.makeText(BoardActivity.this, "The name is required to edit current Board", Toast.LENGTH_LONG).show();
                }else if(boardName.equals(board.getTitle())){
                    Toast.makeText(BoardActivity.this, "The name is the same than is was before", Toast.LENGTH_LONG).show();
                }else{
                    updateBoard(boardName, board);
                }

            }


        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Events
    //va de la mano con la implementación para actualizar


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.deletAll:
                realm.beginTransaction();
                realm.deleteAll();
                realm.commitTransaction();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(boards.get(info.position).getTitle());
        getMenuInflater().inflate(R.menu.context_menu_board_activity,menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.deletBoard:
                deleteBoard(boards.get(info.position));
                return true;
            case R.id.editBoard:
                showAlertForUpdate("Edit Board", "change the name on the board", boards.get(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    @Override
    public void onChange(RealmResults<Board> boards) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_board_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //pasar parametros al siguiente activity
        Intent intent = new Intent(BoardActivity.this, NoteActivity.class);
        intent.putExtra("boardId",boards.get(position).getId());
        startActivity(intent);
    }
}
