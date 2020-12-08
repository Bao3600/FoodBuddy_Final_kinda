package com.example.foodbuddy

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.foodbud.UpdateFoodBody
import com.example.foodbuddy.API.*
import com.google.android.material.navigation.NavigationView
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpCookie

class NavbarActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var editText: EditText
    lateinit var editText2: EditText
    lateinit var editText3: EditText
    lateinit var editText4: EditText
    lateinit var button: Button
    lateinit var btnUpdate: Button
    lateinit var listView: ListView
    var list: ArrayList<String> = ArrayList()
    lateinit var arrayAdapter: MessageAdapter
    var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navbar)
        initView()

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

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        toggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.itemHome -> Toast.makeText(
                    applicationContext,
                    "Clicked Home",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.itemRecipes -> Toast.makeText(
                    applicationContext,
                    "Clicked Recipes",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.itemAccount -> Toast.makeText(
                    applicationContext,
                    "Clicked Account",
                    Toast.LENGTH_SHORT
                ).show()
            }
            true
        }
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

        // Add
        button.setOnClickListener {
            val text = editText.text.toString()
            val text2 = editText2.text.toString()
            val text3 = editText3.text.toString()
            val text4 = editText4.text.toString()
            if (TextUtils.isEmpty(text) || TextUtils.isEmpty(text2) || TextUtils.isEmpty(text4)) {
                Toast.makeText(this, "Content can not be blank", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (tokenValue != null) {
                addFood(text, text2 ,text3, text4, tokenValue)
            }

            list.add("$text $text2 $text3 $text4")

            arrayAdapter = MessageAdapter(this, R.layout.item_nav, list)
            listView.adapter = arrayAdapter
            editText.setText("")
            editText2.setText("")
            editText3.setText("")
            editText4.setText("")
        }

        // Update
        btnUpdate.setOnClickListener {
            val text = editText.text.toString()
            val text2 = editText2.text.toString()
            val text3 = editText3.text.toString()
            val text4 = editText4.text.toString()
            if (TextUtils.isEmpty(text) || TextUtils.isEmpty(text2) || TextUtils.isEmpty(text3) || TextUtils.isEmpty(text4)) {
                Toast.makeText(this, "Please enter the content to be updated", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (tokenValue != null) {
                editFood(text, text2, text3, text4, tokenValue)
            }

            list[position.toInt()] = "$text $text2 $text3 $text4"
            editText.setText("")
            editText2.setText("")
            editText3.setText("")
            editText4.setText("")
            arrayAdapter.notifyDataSetChanged()
        }

        val dialog = MyDialog(this)

        listView.setOnItemClickListener { adapterView, view, i, l ->
            run {
                position = i
                val bean = list[i]
                val split = bean.split(" ")
                dialog.deleteListener = {
                    val delete = split[0]
                    if (tokenValue != null) {
                        deleteFood(split[0], tokenValue)
                    }
                    list.removeAt(position)
                    arrayAdapter = MessageAdapter(this, R.layout.item_nav, list)
                    listView.adapter = arrayAdapter
                    arrayAdapter.notifyDataSetChanged()
                }
                dialog.updateListener = {
                    editText.setText(split[0])
                    editText2.setText(split[1])
                    editText3.setText(split[2])
                    editText4.setText(split[3])
                }
                dialog.show()
                Log.d("ez", "onCreate: ${split[0]} ${split[1]} ${split[2]} ${split[3]}")
            }
        }
    }

    private fun initView() {
        listView = findViewById(R.id.listView)
        editText = findViewById(R.id.editText)
        editText2 = findViewById(R.id.editText2)
        editText3 = findViewById(R.id.editText3)
        editText4 = findViewById(R.id.editText4)
        button = findViewById(R.id.btnAdd)
        btnUpdate = findViewById(R.id.btnUpdate)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toRecipes() {
        launchActivity<RecipesSuggestionActivity>()
    }

    private fun toMyAccount() {
        launchActivity<MyAccountActivity>()
    }

    // ADD FOOD FUNCTION
    private fun addFood(item: String, foodAmt: String, foodUt: String, expDate: String, token: String) {
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        val addFoodInfo = AddFoodBody(item, foodAmt, foodUt, expDate, token)

        retIn.addFood(addFoodInfo).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                    this@NavbarActivity,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {

                    Toast.makeText(this@NavbarActivity, "Add food Successful", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this@NavbarActivity, "Add Food failed!", Toast.LENGTH_SHORT)
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
                        this@NavbarActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(this@NavbarActivity, "Delete food Successful", Toast.LENGTH_SHORT)
                            .show()
                } else {
                    Toast.makeText(this@NavbarActivity, "Delete Food failed!", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }

    // UPDATE FOOD FUNCTION
    private fun editFood(item: String, foodAmt: String, foodUt: String, expDate: String, token: String) {
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        val updateFoodInfo = UpdateFoodBody(item, foodAmt, foodUt, expDate, token)

        retIn.editFood(updateFoodInfo).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                        this@NavbarActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(this@NavbarActivity, "Update food Successful", Toast.LENGTH_SHORT)
                            .show()
                } else {
                    Toast.makeText(this@NavbarActivity, "Update Food failed!", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }

    // LOAD FRIDGE FUNCTION
    private fun loadFridge(token: String) {
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        val loadFridgeInfo = LoadFridgeBody(token)
        //val dash = Intent(this@NavbarActivity, NavbarActivity::class.java)

        retIn.loadFridge(loadFridgeInfo).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                        this@NavbarActivity,
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
                        arrayAdapter = MessageAdapter(this@NavbarActivity, R.layout.item_nav, list)
                        listView.adapter = arrayAdapter
                    }

                    //startActivity(dash)
                } else {
                    Toast.makeText(this@NavbarActivity, "Load fridge failed!", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }

    // LOGOUT FUNCTION
    private fun logout(token: String) {
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        val logoutInfo = LogoutBody(token)
        val login = Intent(this@NavbarActivity, MainActivity::class.java)

        retIn.logout(logoutInfo).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                        this@NavbarActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    Toast.makeText(this@NavbarActivity, "Logout Successful", Toast.LENGTH_SHORT)
                            .show()
                    startActivity(login)
                } else {
                    Toast.makeText(this@NavbarActivity, "Logout failed!", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }
}
