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

import in.shop.myShop.R;
import in.shop.myShop.adapters.ProductAdapter;
import in.shop.myShop.databinding.ActivityCategoryBinding;
import in.shop.myShop.databinding.ActivityPaymentBinding;
import in.shop.myShop.model.Product;
import in.shop.myShop.utilites.Constants;

public class CategoryActivity extends AppCompatActivity {
    ActivityCategoryBinding binding;
    ProductAdapter productAdapter;
    ArrayList<Product> products;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        products = new ArrayList<>();
        int catId = getIntent().getIntExtra("catId",0);
        String categoryName = getIntent().getStringExtra("categoryName");
        getSupportActionBar().setTitle(categoryName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getProducts(catId);
        productAdapter = new ProductAdapter(this,products);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        binding.productList.setLayoutManager(gridLayoutManager);
        binding.productList.setAdapter(productAdapter);
    }
    void getProducts(int catId){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Constants.GET_PRODUCTS_URL + "?category_id="+ catId;
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}