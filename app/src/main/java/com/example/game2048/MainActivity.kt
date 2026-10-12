package com.asadbek.noir2048
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.asadbek.noir2048.Fragments.MenuFragment
import com.asadbek.noir2048.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MenuFragment())
                .commit()
        }
    }
}