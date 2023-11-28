package ap.mobile.challenge;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ToDoDAO {

  @Query("SELECT * FROM ToDo")
  List<ToDo> getAll();
//
  @Query("SELECT * FROM ToDo WHERE substr(time, 1, 2) >= '06' AND substr(time, 1, 2) < '18'")
  List<ToDo> getAllDay(); // 6AM to 6PM
  @Query("SELECT * FROM ToDo WHERE substr(time, 1, 2) >= '18' OR substr(time, 1, 2) < '06'")
  List<ToDo> getAllNight(); // 6PM to 6AM
//  List<ToDo> search(String keyword);
//

  @Insert
  void insert(ToDo toDo);
  @Insert
  void insertAll(ToDo... toDos);
  @Delete
  void delete(ToDo toDo);
  @Delete()
  void clear(ToDo toDo);

  // Add 1 more SELECT operation
  // Add 1 more INSERT operation
  // Add 1 more DELETE operation

  @Insert
  void insertAllApi(List<ToDo> toDo);
  @Query("DELETE FROM ToDo")
  void clearAll();
  @Query("DELETE FROM ToDo WHERE id IN(SELECT id FROM ToDo ORDER BY RANDOM() LIMIT 1)")
  void deleteRandom();
  @Query("SELECT * FROM ToDo ORDER BY time DESC")
  List<ToDo> sortByTimeDesc();
  @Query("SELECT * FROM ToDo ORDER BY time ASC")
  List<ToDo> sortByTimeAsc();


}
