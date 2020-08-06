package wladcom.example.board.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import wladcom.example.board.app.MyAplication;

public class Note extends RealmObject {
    @PrimaryKey
    private int id;
    @Required
    private String description;
    @Required
    private Date createdAt;

    public Note() {
    }

    public Note(String description){
        this.id = MyAplication.NoteID.incrementAndGet();
        this.description = description;
        this.createdAt = new Date();
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
