package ec.edu.uteq.alumnossupabase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import ec.edu.uteq.alumnossupabase.R
import ec.edu.uteq.alumnossupabase.models.Alumno

class AlumnoAdapter(private var alumnos: List<Alumno>) : RecyclerView.Adapter<AlumnoAdapter.AlumnoViewHolder>() {

    class AlumnoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgFoto: com.google.android.material.imageview.ShapeableImageView = view.findViewById(R.id.imgFoto)
        val txtNombre: TextView = view.findViewById(R.id.txtNombre)
        val txtParalelo: TextView = view.findViewById(R.id.txtParalelo)
        val txtCorreo: TextView = view.findViewById(R.id.txtCorreo)
        val txtTelefono: TextView = view.findViewById(R.id.txtTelefono)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alumno, parent, false)
        return AlumnoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        val alumno = alumnos[position]
        
        // Cargar imagen con Coil
        val fotoUrl = alumno.foto
        if (!fotoUrl.isNullOrEmpty()) {
            holder.imgFoto.load(fotoUrl) {
                crossfade(true)
                placeholder(R.drawable.logotipofccdd)
                error(R.drawable.logotipofccdd)
            }
        } else {
            // Placeholder por defecto si no hay foto
            holder.imgFoto.setImageResource(R.drawable.logotipofccdd)
        }
        
        holder.txtNombre.text = alumno.nombres
        holder.txtParalelo.text = "Paralelo: ${alumno.paralelo}"
        holder.txtCorreo.text = alumno.correo ?: "Sin registrar"
        holder.txtTelefono.text = alumno.telefono ?: "Sin registrar"
    }

    override fun getItemCount() = alumnos.size

    fun updateData(newAlumnos: List<Alumno>) {
        alumnos = newAlumnos
        notifyDataSetChanged()
    }
}