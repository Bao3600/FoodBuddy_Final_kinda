package com.example.foodbuddy

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.foodbuddy.API.ApiInterface
import com.example.foodbuddy.API.LogoutBody
import com.example.foodbuddy.API.RetrofitInstance
import com.google.android.material.navigation.NavigationView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyAccountActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_account)

        val bundle: Bundle? = intent.extras
        val username: String? = bundle?.getString("username")
        val fName: String? = bundle?.getString("fName")
        val lName: String? = bundle?.getString("lName")
        val email: String? = bundle?.getString("email")
        val tokenValue: String? = bundle?.getString("tokenValue")

        val tv_first_name = findViewById<TextView>(R.id.tv_first_name)
        val tv_last_name = findViewById<TextView>(R.id.tv_last_name)
        val tv_user_name = findViewById<TextView>(R.id.tv_user_name)
        val tv_email = findViewById<TextView>(R.id.tv_email)

        tv_first_name.setText("First Name: " + fName)
        tv_last_name.setText("Last Name: " + lName)
        tv_user_name.setText("Username: "+ username)
        tv_email.setText("Email: " + email)

        val drawerLayout =
                findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawerLayout)
        val navView =
                findViewById<com.google.android.material.navigation.NavigationView>(R.id.navView)

        val headerView = navView.getHeaderView(0)
        val headerText = headerView.findViewById<TextView>(R.id.Header_username)
        headerText.setText(username)

        toggle = ActionBarDrawerToggle(this, drawerLayout,
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
                    val recipes = Intent(this, MyAccountActivity::class.java)
                    recipes.putExtra("username", username)
                    recipes.putExtra("fName",fName)
                    recipes.putExtra("lName",lName)
                    recipes.putExtra("email",email)
                    recipes.putExtra("tokenValue",tokenValue)
                    startActivity(recipes)
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
            }
            drawerLayout.closeDrawers()
            true
        }
/*
        if(User.fName!=null && User.fName!="") {
            findViewById<TextView>(R.id.tv_first_name).text = "First Name："+User.fName
        }

        if(User.lName!=null&&User.lName!=""){
            findViewById<TextView>(R.id.tv_last_name).text = "Last Name："+User.lName
        }

        if(User.email!=null&&User.email!=""){
            findViewById<TextView>(R.id.tv_email).text = "Email："+User.email
        }
 */
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
        val login = Intent(this@MyAccountActivity, MainActivity::class.java)

        retIn.logout(logoutInfo).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                        this@MyAccountActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(this@MyAccountActivity, "Logout Successful", Toast.LENGTH_SHORT)
                            .show()
                    startActivity(login)
                } else {
                    Toast.makeText(this@MyAccountActivity, "Logout failed!", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }
}