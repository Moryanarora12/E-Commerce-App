package in.shop.myShop.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.shop.myShop.adapters.ProductAdapter;
import in.shop.myShop.databinding.ActivitySearchBinding;
import in.shop.myShop.model.Product;
import in.shop.myShop.utilites.Constants;

public class SearchActivity extends AppCompatActivity {
    ActivitySearchBinding binding;
    ProductAdapter productAdapter;
    ArrayList<Product> products;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        products = new ArrayList<>();
        String query = getIntent().getStringExtra("query");
        String categoryName = getIntent().getStringExtra("categoryName");
        getSupportActionBar().setTitle(query);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getProducts(query);
        productAdapter = new ProductAdapter(this,products);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        binding.productList.setLayoutManager(gridLayoutManager);
        binding.productList.setAdapter(productAdapter);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    void getProducts(String query){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Constants.GET_PRODUCTS_URL + "?q="+ query;
        StringRequest productRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if(object.getString("status").equals("success")){
                                JSONArray productsArray = object.getJSONArray("products");
                                for(int i = 0; i < productsArray.length(); i++){
                                    JSONObject productObject = productsArray.getJSONObject(i);
                                    Product product = new Product(
                                            productObject.getString("name"),
                                            Constants.PRODUCTS_IMAGE_URL +productObject.getString("image"),
                                            productObject.getString("status"),
                                            productObject.getDouble("price"),
                                            productObject.getDouble("price_discount"),
                                            productObject.getInt("stock"),
                                            productObject.getInt("id")
                                    );

                                    products.add(product);
                                }
                                productAdapter.notifyDataSetChanged();
                            }else{

                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(productRequest);
    }
}
