package com.and.whacaquokkaapplication

import android.os.Bundle
import com.and.whacaquokkaapplication.bluetoothmanager.BluetoothConnectionService
import com.and.whacaquokkaapplication.databinding.ActivityWhackGameBinding
import com.and.whacaquokkaapplication.gamelogic.GameMaster

/**
 * Activity used to handle the Whack Quokka Part of game / player.
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
class WhackGameActivity : GameActivity() {

    private lateinit var binding: ActivityWhackGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityWhackGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        game = GameMaster()

        val master = game as GameMaster

        // ---------------------- Set Game values ----------------------
        spawns = arrayOf(
            binding.spawn1,  binding.spawn2,  binding.spawn3,
            binding.spawn4,  binding.spawn5, binding.spawn6,
            binding.spawn7,  binding.spawn8, binding.spawn9
        )
        quokkaScore = binding.scoreQuokka
        whackScore = binding.scoreWhack
        time = binding.time
        quitButton = binding.quitImageButton


        super.onCreate(savedInstanceState)

        game.startGame()

        // ---------------------- Listeners ---------------------------
        binding.quitImageButton.setOnClickListener {
            finish()
        }
       
        binding.spawn1.setOnClickListener {
            master.hitHole(0)
        }

        binding.spawn2.setOnClickListener {
            master.hitHole(1)
        }

        binding.spawn3.setOnClickListener {
            master.hitHole(2)
        }

        binding.spawn4.setOnClickListener {
            master.hitHole(3)
        }

        binding.spawn5.setOnClickListener {
            master.hitHole(4)
        }

        binding.spawn6.setOnClickListener {
            master.hitHole(5)
        }

        binding.spawn7.setOnClickListener {
            master.hitHole(6)
        }

        binding.spawn8.setOnClickListener {
            master.hitHole(7)
        }

        binding.spawn9.setOnClickListener {
            master.hitHole(8)
        }
    }

    override fun onStop() {
        super.onStop()
        BluetoothConnectionService.disconnectFromAllEndpoints()
        game.stopGame()
    }
}