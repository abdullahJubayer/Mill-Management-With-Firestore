package com.example.bp;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.example.bp.DataModel.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tejpratapsingh.pdfcreator.activity.PDFCreatorActivity;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;
import com.tejpratapsingh.pdfcreator.views.PDFBody;
import com.tejpratapsingh.pdfcreator.views.PDFHeaderView;
import com.tejpratapsingh.pdfcreator.views.PDFTableView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFTextView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFVerticalView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PDFActivity extends PDFCreatorActivity {

    private List<User> users = new ArrayList<>();
    private MutableLiveData<User> usersLive = new MutableLiveData<>();
    private MutableLiveData<User> userCounter = new MutableLiveData<>();
    private   int size=0;
    private double deposit,expence,balance,totalMill,millRate;
    private FirebaseFirestore db ;
    private EventListener<QuerySnapshot> memberListner;
    private static int count=0;
    private User tempUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db= FirebaseFirestore.getInstance();
      //  getExpance();
        memberListner =new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                users.clear();
                if (e != null) {
                    Log.e("TAG", "Listen failed.", e);
                    return;
                }
                size= value != null ? value.size() : 0;
                //Toast.makeText(PDFActivity.this, ""+size, Toast.LENGTH_SHORT).show();
                for (QueryDocumentSnapshot doc : value) {
                    if (doc != null) {
                        try {
                             User currentUser=new User();
                            String name=doc.getString("name");
                            String email=doc.getString("email");
                            currentUser.setName(name);
                            currentUser.setEmail(email);
                            usersLive.setValue(currentUser);
                        }catch (Exception err){
                            Toast.makeText(PDFActivity.this, ""+err.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };

        db.collection("Member")
                .addSnapshotListener(memberListner);

        usersLive.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User appUser) {
                tempUser=appUser;
                getUserBalance(appUser);
            }
        });

        userCounter.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User data) {
                Toast.makeText(PDFActivity.this, "Count"+count, Toast.LENGTH_SHORT).show();
                count++;
                Toast.makeText(PDFActivity.this, ""+users.size()+"   "+size, Toast.LENGTH_SHORT).show();
                if (size!=0 && users.size()<=size){
                    users.add(data);
                }
                if (size!=0 && users.size()==size){
                    Toast.makeText(PDFActivity.this, "Count"+count, Toast.LENGTH_SHORT).show();
                    createPDF("test", new PDFUtil.PDFUtilListener() {
                        @Override
                        public void pdfGenerationSuccess(File savedPDFFile) {
                            Toast.makeText(PDFActivity.this, "PDF Created", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void pdfGenerationFailure(Exception exception) {
                            Toast.makeText(PDFActivity.this, "PDF NOT Created", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void getUserBalance(final User appUser) {
        //Toast.makeText(PDFActivity.this, "usersLive", Toast.LENGTH_SHORT).show();
        db.collection("Member").document(appUser.getEmail()).collection("deposit")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        double balanc=0.0;
                        User user=new User();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            balanc=balanc+Double.parseDouble(doc.get("amount").toString());
                        }
                        user.setName(appUser.getName());
                        user.setEmail(appUser.getEmail());
                        user.setBalance(String.valueOf(balance));
                        Toast.makeText(PDFActivity.this, balanc+""+user.getBalance(), Toast.LENGTH_SHORT).show();
                        getUserMill(user);
                    }
                });
    }

    private void getUserMill(final User userM) {
        //Toast.makeText(PDFActivity.this, "usersLive", Toast.LENGTH_SHORT).show();
        db.collection("Member").document(userM.getEmail()).collection("mill")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        double mill=0.0;
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            mill=mill+Double.parseDouble(doc.get("mill").toString());
                        }
                        User user=new User();
                        user.setName(userM.getName());
                        user.setEmail(userM.getEmail());
                        user.setBalance(userM.getBalance());
                        user.setMill(mill);
                        userCounter.setValue(user);
                    }
                });
    }

    private void getExpance(){
        expence=0.0;
        db.collection("Bazar")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot doc : value) {
                            expence+=Double.parseDouble(doc.get("amount").toString());
                        }
                        db.collection("Member")
                                .addSnapshotListener(memberListner);
                    }
                });
    }


    @Override
    protected PDFHeaderView getHeaderView(int page) {
        PDFHeaderView headerView = new PDFHeaderView(getApplicationContext());

        PDFTextView pdfTextViewPage = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.HEADER);
        pdfTextViewPage.setText("Bachelor Engineer");
        pdfTextViewPage.setLayout(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 0));
        pdfTextViewPage.getView().setGravity(Gravity.CENTER_HORIZONTAL);

        headerView.addView(pdfTextViewPage);
        return headerView;
    }

    @Override
    protected PDFBody getBodyViews() {
        PDFBody pdfBody = new PDFBody();

//        PDFTextView pdfTextViewPage = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
//        pdfTextViewPage.setText("Deposit");
//        pdfTextViewPage.setLayout(new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT, 0));
//        pdfTextViewPage.getView().setGravity(Gravity.CENTER_HORIZONTAL);
//        pdfBody.addView(pdfTextViewPage);
//
//        String[] textInTable = {"Name", "Amount"};

// Create table column headers
//        PDFTableView.PDFTableRowView tableHeader = new PDFTableView.PDFTableRowView(getApplicationContext());
//        for (String s : textInTable) {
//            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
//            pdfTextView.setText(s);
//            tableHeader.addToRow(pdfTextView);
//        }
// Create first row
//        PDFTableView.PDFTableRowView tableRowView1 = new PDFTableView.PDFTableRowView(getApplicationContext());
//            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
//            pdfTextView.setText(usersLive.get(0).getName());
//            PDFTextView pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
//            pdfTextView2.setText(usersLive.get(0).getBalance());
//            tableRowView1.addToRow(pdfTextView);
//            tableRowView1.addToRow(pdfTextView2);

// PDFTableView takes table header and first row at once because if page ends after adding header then first row will be on next page. To avoid confusion to user, table header and first row is printed together.
//        ArrayList<User> data=removeDuplicates(new ArrayList<>(usersLive));
//        Log.e("LLLL",data.size()+"");
//        PDFTableView tableView = new PDFTableView(getApplicationContext(), tableHeader, tableRowView1);
//        for (int i = 0; i < data.size(); i++) {
//            // Create 10 rows and add to table.
//            Log.e("LLLL",data.get(i).getName());
//
//            PDFTableView.PDFTableRowView tableRowView = new PDFTableView.PDFTableRowView(getApplicationContext());
//                PDFTextView pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
//                pdfTextView1.setText(data.get(i).getName());
//                PDFTextView pdfTextView3 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
//                pdfTextView3.setText(data.get(i).getBalance());
//                tableRowView.addToRow(pdfTextView1);
//                tableRowView.addToRow(pdfTextView3);
//
//            tableView.addRow(tableRowView);
//        }
//        pdfBody.addView(tableView);




        PDFTextView BalancepdfTextViewPage = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
        BalancepdfTextViewPage.setText("Balance");
        BalancepdfTextViewPage.setLayout(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 0));
        BalancepdfTextViewPage.getView().setGravity(Gravity.CENTER_HORIZONTAL);
        pdfBody.addView(BalancepdfTextViewPage);

        String[] BalancetextInTable = {"Name", "Amount"};

        PDFTableView.PDFTableRowView BalancetableHeader = new PDFTableView.PDFTableRowView(getApplicationContext());
        for (String s : BalancetextInTable) {
            PDFTextView Balance = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            Balance.setText(s);
            BalancetableHeader.addToRow(Balance);
        }

        PDFTableView.PDFTableRowView BalancetableRowView1 = new PDFTableView.PDFTableRowView(getApplicationContext());
        PDFTextView BalancepdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        BalancepdfTextView.setText(users.get(0).getName());
        PDFTextView BalancepdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        BalancepdfTextView2.setText(users.get(0).getBalance());
        BalancetableRowView1.addToRow(BalancepdfTextView);
        BalancetableRowView1.addToRow(BalancepdfTextView2);

        ArrayList<User> data=removeDuplicates(new ArrayList<>(users));
        Log.e("LLLL",data.size()+"");
        PDFTableView tableView = new PDFTableView(getApplicationContext(), BalancetableHeader, BalancetableRowView1);
        for (int i = 0; i < data.size(); i++) {
            // Create 10 rows and add to table.
            Log.e("LLLL",data.get(i).getName());

            PDFTableView.PDFTableRowView tableRowView = new PDFTableView.PDFTableRowView(getApplicationContext());
            PDFTextView pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextView1.setText(data.get(i).getName());
            PDFTextView pdfTextView3 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextView3.setText(new DecimalFormat("##.##").format(Double.parseDouble(data.get(i).getBalance()) - (data.get(i).getMill() * expence/totalMill)));
            tableRowView.addToRow(pdfTextView1);
            tableRowView.addToRow(pdfTextView3);

            tableView.addRow(tableRowView);
        }
        pdfBody.addView(tableView);

//        PDFTextView Balance_table = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
//        Balance_table.setText("Balance");
//        Balance_table.setLayout(new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT, 0));
//        Balance_table.getView().setGravity(Gravity.CENTER_HORIZONTAL);
//        pdfBody.addView(Balance_table);
//
//        String[] balance_Row = {"Name", "Amount"};
//
//// Create table column headers
//        PDFTableView.PDFTableRowView balance_tableHeader = new PDFTableView.PDFTableRowView(getApplicationContext());
//        for (String s : balance_Row) {
//            PDFTextView balance_header = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
//            balance_header.setText(s);
//            balance_tableHeader.addToRow(balance_header);
//        }
//// Create first row
//        PDFTableView.PDFTableRowView balance_Row_1 = new PDFTableView.PDFTableRowView(getApplicationContext());
//        PDFTextView balance_row_1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
//        balance_row_1.setText(usersLive.get(0).getName());
//        PDFTextView balance_row_2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
//        balance_row_2.setText(new DecimalFormat("##.##").format(Double.parseDouble(usersLive.get(0).getBalance()) - (usersLive.get(0).getMill() * expence/totalMill)));
//        balance_Row_1.addToRow(balance_row_1);
//        balance_Row_1.addToRow(balance_row_2);
//
// PDFTableView takes table header and first row at once because if page ends after adding header then first row will be on next page. To avoid confusion to user, table header and first row is printed together.
//        PDFTableView balance_tableView = new PDFTableView(getApplicationContext(), balance_tableHeader, balance_Row_1);
//        for (int i = 0; i < usersLive.size(); i++) {
//            // Create 10 rows and add to table.
//            PDFTableView.PDFTableRowView balance_tableRowView = new PDFTableView.PDFTableRowView(getApplicationContext());
//            PDFTextView balance_table_row1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
//            balance_table_row1.setText(usersLive.get(i).getName());
//            PDFTextView balance_table_row2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
//            balance_table_row2.setText();
//            balance_tableRowView.addToRow(balance_table_row1);
//            balance_tableRowView.addToRow(balance_table_row2);
//
//            balance_tableView.addRow(balance_tableRowView);
//        }
//        pdfBody.addView(Balance_table);


        return pdfBody;
    }

    @Override
    protected void onNextClicked(File savedPDFFile) {

    }

    public  <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
    {

        ArrayList<T> newList = new ArrayList<T>();

        // Traverse through the first list
        for (T element : list) {

            if (!newList.contains(element)) {
                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }

}