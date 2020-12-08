package com.example.foodbuddy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.foodbuddy.API.*
import com.google.android.material.navigation.NavigationView
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipesSuggestionActivity : AppCompatActivity() {

    var list: ArrayList<String> = ArrayList()
    lateinit var listView: ListView
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var arrayAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes)

        listView = findViewById(R.id.listView)

        val bundle: Bundle? = intent.extras
        val username: String? = bundle?.getString("username")
        val fName: String? = bundle?.getString("fName")
        val lName: String? = bundle?.getString("lName")
        val email: String? = bundle?.getString("email")
        val tokenValue: String? = bundle?.getString("tokenValue")

        if (tokenValue != null) {
            loadFridge(tokenValue)
        }

        val drawerLayout =
                findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawerLayout)
        val navView =
                findViewById<com.google.android.material.navigation.NavigationView>(R.id.navView)

        val headerView = navView.getHeaderView(0)
        val headerText = headerView.findViewById<TextView>(R.id.Header_username)
        headerText.setText(username)

        toggle = ActionBarDrawerToggle(
                this, drawerLayout,
                R.string.open,
                R.string.close
        )

        toggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        var navigationView: NavigationView = findViewById(R.id.navView)

        navigationView.setNavigationItemSelectedListener { item ->

            // Related fragement
            when (item.itemId) {
                R.id.itemHome -> {
                    val home = Intent(this, NavbarActivity::class.java)
                    home.putExtra("username", username)
                    home.putExtra("fName",fName)
                    home.putExtra("lName",lName)
                    home.putExtra("email",email)
                    home.putExtra("tokenValue",tokenValue)
                    startActivity(home)
                }
                R.id.itemRecipes -> {
                    val recipes = Intent(this, RecipesSuggestionActivity::class.java)
                    recipes.putExtra("username", username)
                    recipes.putExtra("fName",fName)
                    recipes.putExtra("lName",lName)
                    recipes.putExtra("email",email)
                    recipes.putExtra("tokenValue",tokenValue)
                    startActivity(recipes)
                }
                //region
                R.id.itemAccount -> {
                    val account = Intent(this, MyAccountActivity::class.java)
                    account.putExtra("username", username)
                    account.putExtra("fName",fName)
                    account.putExtra("lName",lName)
                    account.putExtra("email",email)
                    account.putExtra("tokenValue",tokenValue)
                    startActivity(account)
                }

                R.id.itemFeed -> {
                    val feed = Intent(this, MyFeedActivity::class.java)
                    feed.putExtra("username", username)
                    feed.putExtra("fName",fName)
                    feed.putExtra("lName",lName)
                    feed.putExtra("email",email)
                    feed.putExtra("tokenValue",tokenValue)
                    startActivity(feed)
                }

                R.id.itemLogout -> {
                    if (tokenValue != null) {
                        logout(tokenValue)
                    }
                    launchActivity<MainActivity>()
                }

                //endregion
            }
            drawerLayout.closeDrawers()
            true
        }

        val search = findViewById<SearchView>(R.id.searchView)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                search.clearFocus()
                if (list.contains(query)) {
                    arrayAdapter.filter.filter((query))
                } else {
                    Toast.makeText(applicationContext, "Item not found", Toast.LENGTH_LONG).show()
                }
                arrayAdapter.notifyDataSetChanged()
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                arrayAdapter.filter.filter(query)
                arrayAdapter.notifyDataSetChanged()
                return false
            }
        })

        val dialog = RecipesDialog(this)
        listView.setOnItemClickListener { adapterView, view, i, l ->
            run {
                val bean = list[i]
                val split = bean.split(" ")
                dialog.itemClickListener = {
                    if(tokenValue != null)
                    {
                        if (username != null) {
                            if (fName != null) {
                                if (lName != null) {
                                    if (email != null) {
                                        getRecipes(split[0], tokenValue, username, fName, lName, email)
                                    }
                                }
                            }
                        }
                    }
                }
                dialog.show()
                Log.d("ez", "onCreate: ${split[0]} ${split[1]} ${split[2]} ${split[3]}")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // LOAD FRIDGE FUNCTION
    private fun loadFridge(token: String) {
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        val loadFridgeInfo = LoadFridgeBody(token)
        //val dash = Intent(this@NavbarActivity, NavbarActivity::class.java)

        retIn.loadFridge(loadFridgeInfo).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                        this@RecipesSuggestionActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    val fridge = response.body()?.string()
                    val fridgeJson = JSONObject(fridge)
                    val jsonArray = fridgeJson.optJSONArray("fridge")

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val foodItem = jsonObject.optString("item")
                        val amount = jsonObject.optString("foodAmt")
                        val unit = jsonObject.optString("foodUt")
                        val expire = jsonObject.optString("expDate")

                        list.add("$foodItem $amount $unit $expire")
                        arrayAdapter = MessageAdapter(this@RecipesSuggestionActivity, R.layout.item_nav, list)
                        listView.adapter = arrayAdapter
                    }

                    //startActivity(dash)
                } else {
                    Toast.makeText(this@RecipesSuggestionActivity, "Load fridge failed!", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }

    // DELETE FOOD FUNCTION
    private fun deleteFood(item: String, token: String) {
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        val deleteFoodInfo = DeleteFoodBody(item, token)

        retIn.deleteFood(deleteFoodInfo).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                        this@RecipesSuggestionActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(this@RecipesSuggestionActivity, "Delete food Successful", Toast.LENGTH_SHORT)
                            .show()
                } else {
                    Toast.makeText(this@RecipesSuggestionActivity, "Delete Food failed!", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }

    // GET RECIPES FUNCTION
    private fun getRecipes(item: String, token: String, username: String, fName: String, lName: String, email: String) {
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        val getRecipesInfo = GetRecipesBody(token)
        val getrecipes = Intent(this@RecipesSuggestionActivity, GetRecipes::class.java)

        retIn.getRecipes(getRecipesInfo, item).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                        this@RecipesSuggestionActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(this@RecipesSuggestionActivity, "Get recipes Successful", Toast.LENGTH_SHORT)
                            .show()
                    val responseString = response.body()?.string()
                    getrecipes.putExtra("response",responseString)
                    getrecipes.putExtra("username", username)
                    getrecipes.putExtra("fName",fName)
                    getrecipes.putExtra("lName",lName)
                    getrecipes.putExtra("email",email)
                    getrecipes.putExtra("tokenValue",token)
                    startActivity(getrecipes)

                }
                else {
                    Toast.makeText(this@RecipesSuggestionActivity, "Recipes not found", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }

    // LOGOUT FUNCTION
    private fun logout(token: String) {
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        val logoutInfo = LogoutBody(token)
        val login = Intent(this@RecipesSuggestionActivity, MainActivity::class.java)

        retIn.logout(logoutInfo).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                        this@RecipesSuggestionActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(this@RecipesSuggestionActivity, "Logout Successful", Toast.LENGTH_SHORT)
                            .show()
                    startActivity(login)
                } else {
                    Toast.makeText(this@RecipesSuggestionActivity, "Logout failed!", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }
}