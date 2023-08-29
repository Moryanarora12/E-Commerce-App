package in.shop.myShop.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Queue;

import in.shop.myShop.R;
import in.shop.myShop.adapters.CategoryAdapter;
import in.shop.myShop.adapters.ProductAdapter;
import in.shop.myShop.databinding.ActivityMainBinding;
import in.shop.myShop.model.Category;
import in.shop.myShop.model.Product;
import in.shop.myShop.utilites.Constants;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    CategoryAdapter categoryAdapter;
    ArrayList<Category> categories;
    ProductAdapter productAdapter;
    ArrayList<Product> products;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("query",text.toString());
                startActivity(intent);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
        initCategories();
        initProducts();
        initSlider();

    }
    public void initSlider(){
        /*binding.carousel.addData(new CarouselItem("https://t4.ftcdn.net/jpg/02/61/01/87/360_F_261018762_f15Hmze7A0oL58Uwe7SrDKNS4fZIjLiF.jpg","Some Caption here"));
        binding.carousel.addData(new CarouselItem("https://tutorials.mianasad.com/ecommerce/uploads/news/summer%20sales%20offer.jpg","Some Caption here"));
        binding.carousel.addData(new CarouselItem("https://tutorials.mianasad.com/ecommerce/uploads/news/Summer%20special%20offer.jpg","Some Caption here"));
*/
        getRecentOffers();
    }
    public void initCategories(){
        categories = new ArrayList<>();
        /*categories.add(new Category("Chips","https://tutorials.mianasad.com/ecommerce/uploads/category/1682609744913.png","#4db151","Chips are crunchy and spicy",1));
        categories.add(new Category("Chips","https://tutorials.mianasad.com/ecommerce/uploads/category/1682609744913.png","#6d652e","Chips are crunchy and spicy",1));
        categories.add(new Category("Chips","https://tutorials.mianasad.com/ecommerce/uploads/category/1682609744913.png","#41d8c2","Chips are crunchy and spicy",1));
        categories.add(new Category("Chips","https://tutorials.mianasad.com/ecommerce/uploads/category/1682609744913.png","#b1994d","Chips are crunchy and spicy",1));
        categories.add(new Category("Chips","https://tutorials.mianasad.com/ecommerce/uploads/category/1682609744913.png","#e42020","Chips are crunchy and spicy",1));
        categories.add(new Category("Chips","https://tutorials.mianasad.com/ecommerce/uploads/category/1682609744913.png","#ddb60b","Chips are crunchy and spicy",1));
        */
        categoryAdapter = new CategoryAdapter(this,categories);
        getCategories();
        GridLayoutManager layoutManager = new GridLayoutManager(this,4);
        binding.categoriesList.setLayoutManager(layoutManager);
        binding.categoriesList.setAdapter(categoryAdapter);
    }
    void getRecentProducts(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Constants.GET_PRODUCTS_URL + "?count=10";
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
    void getCategories(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Constants.GET_CATEGORIES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getString("status").equals("success")){
                                JSONArray categoriesArray = jsonObject.getJSONArray("categories");
                                for(int i = 0; i < categoriesArray.length(); i++){
                                    JSONObject object = categoriesArray.getJSONObject(i);
                                    Category category = new Category(
                                            object.getString("name"),
                                            Constants.CATEGORIES_IMAGE_URL+object.getString("icon"),
                                            object.getString("color"),
                                            object.getString("brief"),
                                            object.getInt("id")
                                    );
                                    categories.add(category);
                                }
                                categoryAdapter.notifyDataSetChanged();
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
        queue.add(stringRequest);

    }
    void getRecentOffers(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_OFFERS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("status").equals("success")){
                        JSONArray jsonArray = jsonObject.getJSONArray("news_infos");
                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            binding.carousel.addData(
                                    new CarouselItem(Constants.NEWS_IMAGE_URL+object.getString("image"),object.getString("title"))
                            );
                        }
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
        queue.add(request);
    }
    public void initProducts(){

        /*products.add(new Product("Milk","https://tutorials.mianasad.com/ecommerce/uploads/product/1682614129021.jpg","READY STOCK",12,12,1,1));
        products.add(new Product("Milk","https://tutorials.mianasad.com/ecommerce/uploads/product/1682614129021.jpg","READY STOCK",12,12,1,1));
        products.add(new Product("Milk","https://tutorials.mianasad.com/ecommerce/uploads/product/1682614129021.jpg","READY STOCK",12,12,1,1));
        products.add(new Product("Milk","https://tutorials.mianasad.com/ecommerce/uploads/product/1682614129021.jpg","READY STOCK",12,12,1,1));
        products.add(new Product("Milk","https://tutorials.mianasad.com/ecommerce/uploads/product/1682614129021.jpg","READY STOCK",12,12,1,1));
        products.add(new Product("Milk","https://tutorials.mianasad.com/ecommerce/uploads/product/1682614129021.jpg","READY STOCK",12,12,1,1));
*/
        products = new ArrayList<>();
        getRecentProducts();
        productAdapter = new ProductAdapter(this,products);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        binding.productList.setLayoutManager(gridLayoutManager);
        binding.productList.setAdapter(productAdapter);
    }
}