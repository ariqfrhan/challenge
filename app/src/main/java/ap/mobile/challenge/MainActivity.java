package ap.mobile.challenge;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
  implements View.OnClickListener {

  private EditText etActivity;
  private Button btAdd;
  private Button btReset;
  private RecyclerView rvData;
  private Button btRemoveRandom;
  private Button btCustom;
  private List<ToDo> toDoList;
  private ToDoAdapter toDoAdapter;
  private boolean isDescend = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    etActivity = (EditText) findViewById(R.id.etActivity);
    btAdd = (Button) findViewById(R.id.btAdd);
    btReset = (Button) findViewById(R.id.btReset);
    rvData = (RecyclerView) findViewById(R.id.rvData);
    btRemoveRandom = (Button) findViewById(R.id.btRemoveRandom);
    btCustom = (Button) findViewById(R.id.btCustom);

    btAdd.setOnClickListener(this);
    btReset.setOnClickListener(this);
    btRemoveRandom.setOnClickListener(this);
    btCustom.setOnClickListener(this);

    this.toDoAdapter = new ToDoAdapter(this);
    this.rvData.setAdapter(this.toDoAdapter);

    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
    this.rvData.setLayoutManager(layoutManager);

    this.cekData();

  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.btAdd) {
      String what = etActivity.getText().toString();
      String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")).toString();
      String day = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")).toString();
      ToDo t = new ToDo(time,what,day);

      Thread thread = new Thread(()->{
        ToDoDAO dao = ToDoDatabase.getDb(this).toDoDAO();
        dao.insertAll(t);
        tampilData();
      });
      thread.start();
      return;
    }
    if (v.getId() == R.id.btReset) {
      Thread thread = new Thread(()->{
        ToDoDAO dao = ToDoDatabase.getDb(this).toDoDAO();
        dao.clearAll();
        tampilData();
      });
      thread.start();

      ambilDataAPI();
      return;
    }
    if (v.getId() == R.id.btRemoveRandom) {
      // remove random data item from database,
      // and update list
      // show confirm dialog before removal
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle("Confirm Delete");
      builder.setMessage("Do you want to delete random item?");
      builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          Thread thread = new Thread(()->{
            ToDoDAO dao = ToDoDatabase.getDb(MainActivity.this).toDoDAO();
            dao.deleteRandom();
            tampilData();
          });
          thread.start();
        }
      });
      builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
        }
      });
      AlertDialog alert = builder.create();
      alert.show();
      return;
    }
    if (v.getId() == R.id.btCustom) {
      // do custom ToDoDAO command
      Handler h = new Handler(Looper.getMainLooper());
      Thread thread = new Thread(()->{
        ToDoDAO dao = ToDoDatabase.getDb(this).toDoDAO();

        List<ToDo> sorted;

        if (isDescend) {
          sorted = dao.sortByTimeAsc();
        }else{
          sorted = dao.sortByTimeDesc();
        }
        isDescend = !isDescend;

        h.post(()->{
          toDoAdapter.setData(sorted);
          toDoAdapter.notifyDataSetChanged();
        });
      });
      thread.start();
    }
  }

  private void ambilDataAPI() {
    API.getAllData(this, new DataFetchListener() {
      @Override
      public void onDataFetched(List<ToDo> todos) {
        Thread thread = new Thread(() -> {
          ToDoDAO dao = ToDoDatabase.getDb(MainActivity.this).toDoDAO();
          dao.insertAllApi(todos);
          tampilData();
        });
        thread.start();
      }
    });
  }


  private void tampilData() {
    Handler h = new Handler(Looper.getMainLooper());
    Thread t = new Thread(()->{
      ToDoDAO dao = ToDoDatabase.getDb(this).toDoDAO();
      toDoList = dao.getAll();
      h.post(()->{
        toDoAdapter.setData(toDoList);
        toDoAdapter.notifyDataSetChanged();
        Log.d("todo", "current size" + toDoList.size());
      });
    });
    t.start();
  }

  private void cekData(){
    Handler h = new Handler(Looper.getMainLooper());
    Thread t = new Thread(() ->{
      ToDoDAO dao = ToDoDatabase.getDb(this).toDoDAO();
      toDoList = dao.getAll();
      h.post(()->{
        if (toDoList.isEmpty()) {
          ambilDataAPI();
        }else{
          tampilData();
        }
      });
    });
    t.start();
  }
}