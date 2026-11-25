package ec.edu.uteq.alumnossupabase

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import ec.edu.uteq.alumnossupabase.models.Materia
import ec.edu.uteq.alumnossupabase.utils.SupabaseErrorHandler
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch

class MateriasActivity : AppCompatActivity() {

    private lateinit var listaNiveles: AutoCompleteTextView
    private lateinit var actvMaterias: AutoCompleteTextView
    private lateinit var btnMostrarAlumnos: Button
    private lateinit var rvAlumnosMaterias: RecyclerView
    private lateinit var progressMaterias: ProgressBar
    private lateinit var adapterAlumnos: AlumnoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_materias)

        listaNiveles = findViewById(R.id.listaNiveles)
        actvMaterias = findViewById(R.id.actvmaterias)
        btnMostrarAlumnos = findViewById(R.id.btnMostrarAlumnos)
        rvAlumnosMaterias = findViewById(R.id.rvAlumnosMaterias)
        progressMaterias = findViewById(R.id.progressMaterias)

        // Configurar botón Volver
        findViewById<android.widget.ImageButton>(R.id.btnVolver).setOnClickListener {
            finish()
        }

        // Configurar RecyclerView (para mostrar las tarjetitas de los alumnos aquí)
        rvAlumnosMaterias.layoutManager = LinearLayoutManager(this)
        adapterAlumnos = AlumnoAdapter(emptyList())
        rvAlumnosMaterias.adapter = adapterAlumnos

        // Configurar arreglo local de niveles
        val niveles = resources.getStringArray(R.array.niveles)
        val adapterNiveles = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, niveles)
        listaNiveles.setAdapter(adapterNiveles)

        listaNiveles.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            Toast.makeText(this, "Seleccionaste: $selectedItem", Toast.LENGTH_SHORT).show()

            // Limpiar adaptador anterior de materias y lista de alumnos
            actvMaterias.setAdapter(null)
            actvMaterias.setText("")
            adapterAlumnos.updateData(emptyList())
            
            progressMaterias.visibility = View.VISIBLE

            lifecycleScope.launch {
                try {
                    val result = SupabaseManager.client.from("materias")
                        .select(columns = io.github.jan.supabase.postgrest.query.Columns.list("id, nombre")) {
                            filter {
                                eq("nivel", position + 1)
                            }
                            order("nombre", Order.ASCENDING)
                        }
                        .decodeList<Materia>()

                    val nombresMaterias = ArrayList<String>()
                    result.forEach { m ->
                        m.nombre?.let { nombresMaterias.add(it) }
                    }

                    val adapterMaterias = ArrayAdapter(
                        this@MateriasActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        nombresMaterias
                    )
                    actvMaterias.setAdapter(adapterMaterias)

                } catch (e: RestException) {
                    SupabaseErrorHandler.show(this@MateriasActivity, e)
                } catch (e: Exception) {
                    SupabaseErrorHandler.show(this@MateriasActivity, e)
                } finally {
                    progressMaterias.visibility = View.GONE
                }
            }
        }

        btnMostrarAlumnos.setOnClickListener {
            val nivelSeleccionado = listaNiveles.text.toString()
            val materiaSeleccionada = actvMaterias.text.toString()

            if (nivelSeleccionado.isEmpty() || materiaSeleccionada.isEmpty()) {
                Toast.makeText(this, "Seleccione un semestre y una materia primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            consultarAlumnos()
        }
    }

    private fun consultarAlumnos() {
        lifecycleScope.launch {
            progressMaterias.visibility = View.VISIBLE
            try {
                // Consultamos todos los alumnos y reutilizamos el adaptador visual
                val alumnos = SupabaseManager.client.from("alumnos")
                    .select {
                        order("nombres", Order.ASCENDING)
                    }
                    .decodeList<Alumno>()

                if (alumnos.isEmpty()) {
                    Toast.makeText(this@MateriasActivity, "No se encontraron alumnos.", Toast.LENGTH_SHORT).show()
                } else {
                    adapterAlumnos.updateData(alumnos)
                }
            } catch (e: Exception) {
                SupabaseErrorHandler.show(this@MateriasActivity, e)
            } finally {
                progressMaterias.visibility = View.GONE
            }
        }
    }
}