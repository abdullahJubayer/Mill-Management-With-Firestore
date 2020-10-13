package com.example.bp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bp.DataModel.User;
import com.example.bp.R;
import com.example.bp.util.DataUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetMillAdapter extends RecyclerView.Adapter<SetMillAdapter.SetMillRecyclerViewHolder>{
    private List<User> userList;
    private FirebaseFirestore db ;
    private String path;
    private String field;

    public SetMillAdapter(List<User> userList,String path,String field) {
        this.userList = userList;
        this.path=path;
        this.field=field;
        db= FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public SetMillAdapter.SetMillRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.deposit_recycler_item,parent,false);
        return new SetMillRecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final SetMillAdapter.SetMillRecyclerViewHolder holder, final int position) {
        holder.name.setText(userList.get(position).getName());
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String m=holder.mill.getText().toString();
                if (!m.isEmpty() && !m.replaceAll(" ","").equals("") && m.matches("^[0-9]*$")){
                    holder.update.setEnabled(false);
                    Map<String,String> map=new HashMap<>();
                    map.put("mill",holder.mill.getText().toString().trim());
                    map.put("date",new DataUtil().getDate());
                    db.collection("Member").document(userList.get(position).getEmail())
                            .collection("mill")
                            .document(new DataUtil().getDate())
                            .set(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    holder.mill.setText(holder.mill.getText().toString());
                                }
                            });
                }else {
                    holder.mill.setError("Data Not Valid");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class SetMillRecyclerViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private EditText mill;
        private Button update;
        public SetMillRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.name_txt);
            mill=itemView.findViewById(R.id.mill);
            update=itemView.findViewById(R.id.update_button);
        }
    }
}
