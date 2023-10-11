package com.adrywill.orgs.ui.activity

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.adrywill.orgs.R
import com.adrywill.orgs.ui.recyclerview.adapter.ListaProdutosAdapter

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = ListaProdutosAdapter()
    }

}