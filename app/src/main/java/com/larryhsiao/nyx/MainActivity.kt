package com.larryhsiao.nyx

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView


/**
 * Entry Activity of Nyx.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NavigationUI.setupWithNavController(
            findViewById<BottomNavigationView>(R.id.main_bottomNavigationView),
            (supportFragmentManager.findFragmentById(
                R.id.main_fragmentContainer
            ) as NavHostFragment).navController
        )
    }
}