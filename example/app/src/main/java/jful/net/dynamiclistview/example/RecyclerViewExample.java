package jful.net.dynamiclistview.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.jful.dynamiclistview.DynamicRecyclerView;

/**
 * Created by jaehochoe on 2019-08-23.
 */
public class RecyclerViewExample extends Activity {

    DynamicRecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamiclist_recyclerview);
        recyclerView = findViewById(R.id.dynamiclist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new Adapter());
    }

    private static class Adapter extends RecyclerView.Adapter<Holder> {
        String[] items = new String[]{
                "Activity",
                "Bundle",
                "Nullable",
                "LayoutInflater",
                "View",
                "ViewGroup",
                "TextView",
                "NonNull",
                "LinearLayoutManager",
                "RecyclerView",
                "Activity",
                "Bundle",
                "Nullable",
                "LayoutInflater",
                "View",
                "ViewGroup",
                "TextView",
                "NonNull",
                "LinearLayoutManager",
                "RecyclerView",
                "Activity",
                "Bundle",
                "Nullable",
                "LayoutInflater",
                "View",
                "ViewGroup",
                "TextView",
                "NonNull",
                "LinearLayoutManager",
                "RecyclerView",
                "Activity",
                "Bundle",
                "Nullable",
                "LayoutInflater",
                "View",
                "ViewGroup",
                "TextView",
                "NonNull",
                "LinearLayoutManager",
                "RecyclerView",
        };

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_contents, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.tv.setText(items[position]);
        }

        @Override
        public int getItemCount() {
            return items.length;
        }
    }

    private static class Holder extends RecyclerView.ViewHolder {
        TextView tv;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.list_row_contents_tv);
        }
    }
}
