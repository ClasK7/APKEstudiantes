package ec.edu.uteq.alumnossupabase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnGestionAlumnos).setOnClickListener {
            startActivity(Intent(this, AlumnosActivity::class.java))
        }

        findViewById<Button>(R.id.btnGestionMaterias).setOnClickListener {
            startActivity(Intent(this, MateriasActivity::class.java))
        }
    }
}