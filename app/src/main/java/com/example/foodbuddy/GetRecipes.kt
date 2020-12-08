package com.example.foodbuddy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.foodbuddy.API.ApiInterface
import com.example.foodbuddy.API.LogoutBody
import com.example.foodbuddy.API.RetrofitInstance
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetRecipes : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_recipes)

        val bundle: Bundle? = intent.extras
        val response: String? = bundle?.getString("response")
        val username: String? = bundle?.getString("username")
        val fName: String? = bundle?.getString("fName")
        val lName: String? = bundle?.getString("lName")
        val email: String? = bundle?.getString("email")
        val tokenValue: String? = bundle?.getString("tokenValue")

        val title1 = findViewById<TextView>(R.id.gettext1)
        val title2 = findViewById<TextView>(R.id.gettext2)
        val image1 = findViewById<ImageView>(R.id.imageView)
        val image2 = findViewById<ImageView>(R.id.imageView2)

        val json = JSONObject(response)
        val jsonArray = json.optJSONArray("results")

        val object1 = jsonArray.getJSONObject(0)
        val link = object1.optString("sourceUrl")
        val title = object1.optString("title")
        val image_1 = object1.optString("image")

        val object2 = jsonArray.getJSONObject(1)
        val link2 = object2.optString("sourceUrl")
        val title_2 = object2.optString("title")
        val image_2 = object2.optString("image")

        title1.setText(title)
        title2.setText(title_2)
        Picasso.get().load(image_1).into(image1)
        Picasso.get().load(image_2).into(image2)

        title1.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(browserIntent)
        }

        title2.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link2))
            startActivity(browserIntent)
        }
2

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
                    home.putExtra("fName", fName)
                    home.putExtra("lName", lName)
                    home.putExtra("email", email)
                    home.putExtra("tokenValue", tokenValue)
                    startActivity(home)
                }
                R.id.itemRecipes -> {
                    val recipes = Intent(this, RecipesSuggestionActivity::class.java)
                    recipes.putExtra("username", username)
                    recipes.putExtra("fName", fName)
                    recipes.putExtra("lName", lName)
                    recipes.putExtra("email", email)
                    recipes.putExtra("tokenValue", tokenValue)
                    startActivity(recipes)
                }
                //region

                R.id.itemAccount -> {
                    val account = Intent(this, MyAccountActivity::class.java)
                    account.putExtra("username", username)
                    account.putExtra("fName", fName)
                    account.putExtra("lName", lName)
                    account.putExtra("email", email)
                    account.putExtra("tokenValue", tokenValue)
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
        val login = Intent(this@GetRecipes, MainActivity::class.java)

        retIn.logout(logoutInfo).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                    this@GetRecipes,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(this@GetRecipes, "Logout Successful", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(login)
                } else {
                    Toast.makeText(this@GetRecipes, "Logout failed!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

}