package ec.edu.uteq.alumnossupabase

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ec.edu.uteq.alumnossupabase.adapter.AlumnoAdapter
import ec.edu.uteq.alumnossupabase.data.SupabaseManager
import ec.edu.uteq.alumnossupabase.models.Alumno
import ec.edu.uteq.alumnossupabase.utils.SupabaseErrorHandler
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch

class AlumnosActivity : AppCompatActivity() {

    private lateinit var rvAlumnos: RecyclerView
    private lateinit var progressMaterias: ProgressBar
    private lateinit var adapter: AlumnoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumnos)

        rvAlumnos = findViewById(R.id.rvAlumnos)
        progressMaterias = findViewById(R.id.progressMaterias)

        // Configurar botón Volver
        findViewById<android.widget.ImageButton>(R.id.btnVolver).setOnClickListener {
            finish()
        }

        // Configurar RecyclerView
        rvAlumnos.layoutManager = LinearLayoutManager(this)
        adapter = AlumnoAdapter(emptyList())
        rvAlumnos.adapter = adapter

        findViewById<Button>(R.id.btnConsultar)
            .setOnClickListener { consultarAlumnos() }

        consultarAlumnos()
    }

    private fun consultarAlumnos() {
        lifecycleScope.launch {
            progressMaterias.visibility = View.VISIBLE
            try {
                val alumnos = SupabaseManager.client
                    .from("alumnos")
                    .select {
                        order("nombres", Order.ASCENDING)
                    }
                    .decodeList<Alumno>()

                if (alumnos.isEmpty()) {
                    Toast.makeText(this@AlumnosActivity, "No se encontraron alumnos.", Toast.LENGTH_SHORT).show()
                } else {
                    adapter.updateData(alumnos)
                }
            } catch (e: Exception) {
                SupabaseErrorHandler.show(this@AlumnosActivity, e)
            } finally {
                progressMaterias.visibility = View.GONE
            }
        }
    }
}