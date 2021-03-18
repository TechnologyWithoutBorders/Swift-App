package ngo.teog.swift.helpers.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

//TODO observables are more or less a workaround
@Entity(tableName = "observables")
public class Observable {
    @PrimaryKey
    private final int id;

    public Observable(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
