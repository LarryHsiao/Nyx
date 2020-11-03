package com.larryhsiao.nyx

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Entry Activity of Nyx.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val frag =supportFragmentManager.findFragmentById(R.id.main_navigation) as NavHostFragment
        NavigationUI.setupWithNavController(
            main_bottomNavigationView,
            frag.navController
        )
    }
}