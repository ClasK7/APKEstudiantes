package ec.edu.uteq.alumnossupabase

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import ec.edu.uteq.alumnossupabase.data.SupabaseManager
import ec.edu.uteq.alumnossupabase.models.Materia
import ec.edu.uteq.alumnossupabase.utils.SupabaseErrorHandler
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch

class MateriasActivity : AppCompatActivity() {

    private lateinit var listaNiveles: AutoCompleteTextView
    private lateinit var actvMaterias: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_materias)

        listaNiveles = findViewById(R.id.listaNiveles)
        actvMaterias = findViewById(R.id.actvmaterias)

        // Configurar botón Volver
        findViewById<android.widget.ImageButton>(R.id.btnVolver).setOnClickListener {
            finish()
        }

        // Configurar arreglo local de niveles
        val niveles = resources.getStringArray(R.array.niveles)
        val adapterNiveles = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, niveles)
        listaNiveles.setAdapter(adapterNiveles)

        listaNiveles.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            Toast.makeText(this, "Seleccionaste: $selectedItem", Toast.LENGTH_SHORT).show()

            // Limpiar adaptador anterior de materias
            actvMaterias.setAdapter(null)
            actvMaterias.setText("")

            lifecycleScope.launch {
                try {
                    val result = SupabaseManager.client.from("materias")
                        .select {
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
                }
            }
        }
    }
}