package com.example.foodbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.foodbuddy.API.ApiInterface
import com.example.foodbuddy.API.LoadFeedBody
import com.example.foodbuddy.API.LogoutBody
import com.example.foodbuddy.API.RetrofitInstance
import com.google.android.material.navigation.NavigationView
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFeedActivity : AppCompatActivity() {

    var list: ArrayList<String> = ArrayList()
    lateinit var listView: ListView
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var arrayAdapter: FeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_feed)

        listView = findViewById(R.id.listView)

        val bundle: Bundle? = intent.extras
        val username: String? = bundle?.getString("username")
        val fName: String? = bundle?.getString("fName")
        val lName: String? = bundle?.getString("lName")
        val email: String? = bundle?.getString("email")
        val tokenValue: String? = bundle?.getString("tokenValue")

        if (tokenValue != null) {
            loadFeed(tokenValue)
        }

        val drawerLayout = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawerLayout)
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // LOGOUT FUNCTION
    private fun logout(token: String) {
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        val logoutInfo = LogoutBody(token)
        val login = Intent(this@MyFeedActivity, MainActivity::class.java)

        retIn.logout(logoutInfo).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                        this@MyFeedActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(this@MyFeedActivity, "Logout Successful", Toast.LENGTH_SHORT)
                            .show()
                    startActivity(login)
                } else {
                    Toast.makeText(this@MyFeedActivity,"Logout failed!", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }

    // LOAD FEED FUNCTION
    private fun loadFeed(token: String) {
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        val loadFeedInfo = LoadFeedBody(token)

        retIn.loadFeed(loadFeedInfo).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                    this@MyFeedActivity,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    val response = response.body()?.string()
                    val feedJson = JSONArray(response)

                    for (i in 0 until feedJson.length()) {
                        val jsonObject = feedJson.getJSONObject(i)
                        val name = jsonObject.optString("name")
                        val event = jsonObject.optString("eventType")
                        val item = jsonObject.optString("item")

                        val feed = name + " " + event + " " + item

                        list.add("$feed")
                        arrayAdapter = FeedAdapter(this@MyFeedActivity, R.layout.feed_nav, list)
                        listView.adapter = arrayAdapter
                    }
                } else {
                    Toast.makeText(this@MyFeedActivity,"Logout failed!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }
}