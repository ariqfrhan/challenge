package ap.mobile.challenge;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.VH> {

  private final Context context;
  private List<ToDo> toDoList = new ArrayList<>();

  ToDoAdapter(Context context) {
    this.context = context;
  }

  public void setData(List<ToDo> toDoList) {
    this.toDoList = toDoList;
    notifyDataSetChanged();
  }

  public List<ToDo> getToDoList() {
    return this.toDoList;
  }

  @NonNull
  @Override
  public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View card = LayoutInflater.from(context)
            .inflate(R.layout.item_todo, parent, false);
    VH viewholder = new VH(card);
    return viewholder;
  }

  @Override
  public void onBindViewHolder(@NonNull VH holder, int position) {
    ToDo t = this.toDoList.get(position);
    holder.tvItemTime.setText(t.time);
    holder.tvItemTodo.setText(t.what);
    holder.tvItemTime.setText(t.time);
    holder.tvToday.setText(t.day);

    holder.btDelete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Do you want to delete this item?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            int adapterPosition = holder.getAdapterPosition();

            Handler h = new Handler(Looper.getMainLooper());
            Thread thread = new Thread(()->{
              ToDoDAO dao = ToDoDatabase.getDb(context).toDoDAO();
              dao.delete(getToDoList().get(adapterPosition));
              h.post(()->{
                getToDoList().remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
              });
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
      }
    });
  }

  @Override
  public int getItemCount() {
    return this.toDoList.size();
  }

  public class VH extends RecyclerView.ViewHolder {
    private TextView tvItemTime;
    private TextView tvItemTodo;
    private TextView tvToday;
    private Button btDelete;

    public VH(View itemView) {
      super(itemView);

      this.tvItemTime = (TextView) itemView.findViewById(R.id.tvItemTime);
      this.tvItemTodo = (TextView) itemView.findViewById(R.id.tvItemTodo);
      this.tvToday = (TextView) itemView.findViewById(R.id.tvToday);
      this.btDelete = (Button) itemView.findViewById(R.id.btDel);
      this.tvToday = (TextView) itemView.findViewById(R.id.tvToday);

    }
  }

}
