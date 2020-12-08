package com.example.foodbuddy

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView



class RecipesDialog : Dialog {

    constructor(context: Context) : this(context,0)
    constructor(context: Context, themeResId: Int) : super(context,
            R.style.MyDialog
    ){
        setContentView(R.layout.dialog_get_recipes)
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        initView()
        //Initialize the event of the interface control
        initEvent()
    }

    private lateinit var tvRecipes: TextView
    var itemClickListener: (() -> Unit)? = null
    /**
     * Initialize interface controls
     */
    private fun initView() {
        tvRecipes = findViewById<View>(R.id.tv_recipes) as TextView
    }

    /**
     * Confirm and cancel listening of the initialization interface
     */
    private fun initEvent() {
        tvRecipes.setOnClickListener {
            itemClickListener?.invoke()
            dismiss()
        }
    }
}