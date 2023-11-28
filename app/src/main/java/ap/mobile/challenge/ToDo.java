package ap.mobile.challenge;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ToDo {

  @PrimaryKey(autoGenerate = true)
  public int id;
  @ColumnInfo(name = "what")
  public String what;
  @ColumnInfo(name = "time")
  public String time;

  @ColumnInfo(name = "day")
  public String day;

  public ToDo(String time, String what, String day) {
    this.time = time;
    this.what = what;
    this.day = day;
  }

  // add one more String members
  // also add respective view for the new members
  // on layout/item_todo.xml

}
