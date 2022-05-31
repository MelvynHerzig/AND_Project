package com.and.whacaquokkaapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.and.whacaquokkaapplication.databinding.ActivityWhackGameBinding

class WhackGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWhackGameBinding

    private lateinit var game : GameMaster

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWhackGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spawns = arrayOf(
            binding.spawn1,  binding.spawn2,  binding.spawn3,
            binding.spawn4,  binding.spawn5, binding.spawn6,
            binding.spawn7,  binding.spawn8, binding.spawn9
        )
        game = GameMaster(spawns, binding.scoreQuokka, binding.scoreWhack, binding.time)

        binding.quitImageButton.setOnClickListener {
            finish()
        }

        binding.spawn1.setOnClickListener {
            game.hitHole(1)
        }

        binding.spawn2.setOnClickListener {
            game.hitHole(2)
        }

        binding.spawn3.setOnClickListener {
            game.hitHole(3)
        }

        binding.spawn4.setOnClickListener {
            game.hitHole(4)
        }

        binding.spawn5.setOnClickListener {
            game.hitHole(5)
        }

        binding.spawn6.setOnClickListener {
            game.hitHole(6)
        }

        binding.spawn7.setOnClickListener {
            game.hitHole(7)
        }

        binding.spawn8.setOnClickListener {
            game.hitHole(8)
        }

        binding.spawn9.setOnClickListener {
            game.hitHole(9)
        }
    }

    override fun onDestroy() {
        game.exitGame()
        super.onDestroy()
    }
}