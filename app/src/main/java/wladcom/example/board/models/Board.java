package wladcom.example.board.models;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import wladcom.example.board.app.MyAplication;

public class Board extends RealmObject {
    @PrimaryKey
    private int id;
    @Required
    private String title;
    @Required
    private Date createdAt;
    private RealmList<Note> notes;

    public Board() {
    }

    public Board(String title) {
        this.id = MyAplication.BoardID.incrementAndGet();
        this.title = title;
        this.createdAt = new Date();
        this.notes = new RealmList<Note>();
        this.createdAt = new Date();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RealmList<Note> getNotes() {
        return notes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }



}
