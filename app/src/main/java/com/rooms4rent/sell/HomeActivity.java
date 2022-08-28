package com.rooms4rent.sell;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import com.rooms4rent.sell.firebase.FirebaseHandler;
import com.rooms4rent.sell.model.Rent;
import com.rooms4rent.sell.model.User;

public class HomeActivity extends AppCompatActivity implements RentAdapter.ItemClicked, SearchView.OnQueryTextListener {

    private RecyclerView rvHouses;
    private RentAdapter houseAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Rent> rent;

    private String USER_TABLE = "RoomsUser";
    private String RENT_TABLE = "RoomsRent";

    FirebaseHandler firebaseHandler;
    DatabaseReference rentDatabase;
    DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseHandler = new FirebaseHandler();
        rentDatabase = firebaseHandler.getFirebaseConnection(RENT_TABLE);
        userDatabase = firebaseHandler.getFirebaseConnection(USER_TABLE);

        rvHouses = findViewById(R.id.rvHouses);
        rvHouses.setHasFixedSize(true);

        rent = new ArrayList<Rent>();
        rentDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot rentSnapshot: dataSnapshot.getChildren()) {
                    Rent rents = rentSnapshot.getValue(Rent.class);

                    rent.add(new Rent(rents.getTitle(), rents.getLocation(), rents.getAddress(),
                            rents.getFee(), rents.getPeriod(), rents.getDescription(), rents.getNumOfBeds(),
                            rents.getNumOfBaths(), rents.getUserName(), rents.getContact(), rents.getDate(), rents.getKey()));
                }
                layoutManager = new LinearLayoutManager(HomeActivity.this);
                houseAdapter = new RentAdapter(HomeActivity.this, rent);

                rvHouses.setLayoutManager(layoutManager);
                rvHouses.setItemAnimator(new DefaultItemAnimator());
                rvHouses.setAdapter(houseAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menusearch, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public void onItemClicked(final int index) {
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            Intent homeDetailsIntent = new Intent(HomeActivity.this, HomeDetailsActivity.class);
            Bundle bundle = new Bundle();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bundle.putString("title", rent.get(index).getTitle());
                bundle.putString("date", rent.get(index).getDate());
                bundle.putString("location", rent.get(index).getLocation());
                bundle.putString("fee", rent.get(index).getFee());
                bundle.putString("period", rent.get(index).getPeriod());
                bundle.putString("address", rent.get(index).getAddress());
                bundle.putString("description", rent.get(index).getDescription());
                bundle.putInt("beds", rent.get(index).getNumOfBeds());
                bundle.putInt("baths", rent.get(index).getNumOfBaths());

                User user = dataSnapshot.child(rent.get(index).getUserName()).getValue(User.class);

                bundle.putString("postBy", user.getName());
                bundle.putString("contact", rent.get(index).getContact());
                bundle.putString("email", user.getEmail());

                homeDetailsIntent.putExtras(bundle);
                startActivity(homeDetailsIntent);

//                Toast.makeText(HomeActivity.this, rent.get(index).getContact(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        Toast.makeText(this, "৳ " + rent.get(index).getFee() + " /" + rent.get(index).getPeriod(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<Rent> newList = new ArrayList<>();
        for (Rent rents: rent) {

            String title = rents.getTitle().toLowerCase();
            String location = rents.getLocation().toLowerCase();
            String price = rents.getFee().toLowerCase();
            String address = rents.getAddress().toLowerCase();

            if (location.contains(newText) || title.contains(newText) ||
                    price.contains(newText) || address.contains(newText)) {
                newList.add(rents);
            }
        }
        houseAdapter.setFilter(newList);
        return true;
    }
}
