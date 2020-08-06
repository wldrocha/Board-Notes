package wladcom.example.board.app;

import android.app.Application;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;
import wladcom.example.board.models.Board;
import wladcom.example.board.models.Note;

public class MyAplication extends Application {

    public static AtomicInteger BoardID = new AtomicInteger();
    public static AtomicInteger NoteID = new AtomicInteger();

    //Escribir código de configuración global y es ejecutada antes del Main activity
    @Override
    public void onCreate() {
        setUpRealmConfig();

        Realm realm = Realm.getDefaultInstance();
        BoardID = getIdByTable(realm, Board.class);
        NoteID = getIdByTable(realm, Note.class);
        realm.close();
        super.onCreate();
    }

    private void setUpRealmConfig(){
        //inicializando realm
        Realm.init(getApplicationContext());

        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass){
        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0) ? new AtomicInteger(results.max("id").intValue()) : new AtomicInteger();
    }

}
