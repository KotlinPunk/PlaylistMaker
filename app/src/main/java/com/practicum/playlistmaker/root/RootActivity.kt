package com.practicum.playlistmaker.root

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {

    private var _binding: ActivityRootBinding? = null
    private val binding: ActivityRootBinding get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment

        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.newPlaylistFragment, R.id.audioplayerActivity -> {
                    binding.bottomNavigationView.isVisible = false
                }
                else -> binding.bottomNavigationView.isVisible = true
            }
        }

        binding.bottomNavigationView.setupWithNavController(navController)

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}