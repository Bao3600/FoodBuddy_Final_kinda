package com.example.foodbuddy

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class DeleteDialog : Dialog {

    constructor(context: Context) : this(context,0)
    constructor(context: Context, themeResId: Int) : super(context,
            R.style.MyDialog
    ){
        setContentView(R.layout.delete_dialog)
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        initView()
        initEvent()
    }

    private lateinit var tvConfirm: TextView
    private lateinit var tvDelete: TextView
    var deleteListener: (() -> Unit)? = null

    private fun initView() {
        tvConfirm = findViewById<View>(R.id.tv_confirm) as TextView
        tvDelete = findViewById<View>(R.id.tv_cancel) as TextView
    }


    private fun initEvent() {
        tvConfirm.setOnClickListener {
            deleteListener?.invoke()
            dismiss()
        }

        tvDelete.setOnClickListener {
            dismiss()
        }
    }
}