package ap.mobile.challenge;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class API {

  public static List<ToDo> getAllData(Context context, DataFetchListener listener) {
    List<ToDo> toDos = new ArrayList<>();
    // This method get all data from URL:
    String url = "https://mgm.ub.ac.id/todo.php";
    // Use volley/retrofit.
    RequestQueue requestQueue = Volley.newRequestQueue(context);
    // 1. get JSON array from URL
    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
      @RequiresApi(api = Build.VERSION_CODES.O)
      @Override
      public void onResponse(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
          try {
            // 2. Convert JSON array to list of objects
            JSONObject responseObj = response.getJSONObject(i);
            String id = responseObj.getString("id");
            String time = responseObj.getString("time");
            String what = responseObj.getString("what");
            String day = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")).toString();

            // return the list of objects
            toDos.add(new ToDo(time,what,day));


          } catch (JSONException e) {
            throw new RuntimeException(e);
          }
        }
        listener.onDataFetched(toDos);
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Log.d("API Error", "Error fetching data from API: " + error.toString());
      }
    });
    requestQueue.add(jsonArrayRequest);

    return toDos;
  }

}
