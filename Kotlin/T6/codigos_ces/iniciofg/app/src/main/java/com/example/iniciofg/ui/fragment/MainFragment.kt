package com.example.iniciofg.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.iniciofg.R
import com.example.iniciofg.adapter.AdapterProducto
import com.example.iniciofg.databinding.FragmentDosBinding
import com.example.iniciofg.databinding.FragmentMainBinding
import com.example.iniciofg.databinding.FragmenteLoginBinding
import com.example.iniciofg.model.Producto
import com.example.iniciofg.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var adapterProducto: AdapterProducto
    private lateinit var lista: ArrayList<Producto>
    private lateinit var usuario: Usuario
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser
        firebaseDatabase =
            FirebaseDatabase.getInstance("https://compras-ec8a2-default-rtdb.europe-west1.firebasedatabase.app/")
        cargarDatos()
        lista = ArrayList()
        adapterProducto = AdapterProducto(lista,requireContext())
        firebaseDatabase.reference.child("productos")
            .addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children){
                    val producto = i.getValue(Producto::class.java)
                    adapterProducto.addProducto(producto!!)
                    //lista.add(producto!!)
                    //adapterProducto.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun cargarDatos() {
        // 'https://dummyjson.com/products'
        val gson = Gson()
        val peticion = JsonObjectRequest("https://dummyjson.com/products", {
            val products = it.getJSONArray("products")
            for (i in 0..products.length() - 1) {
                val productJSON = products.getJSONObject(i)
                val product = gson.fromJson(productJSON.toString(), Producto::class.java)
                firebaseDatabase.reference.child("productos")
                    .child(product.id.toString()).setValue(product)
            }
        }, {})
        Volley.newRequestQueue(requireContext()).add(peticion)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // XML -> inflater-> View
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()


        /*firebaseDatabase.reference.child("user")
            .child(firebaseAuth.currentUser!!.uid).get().addOnCompleteListener {
                val user = it.result.getValue(Usuario::class.java)
            }*/

        firebaseDatabase.reference.child("user")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for ( i in snapshot.children ){
                        i.getValue(Usuario::class.java)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        binding.toolbarFMain.inflateMenu(R.menu.menu_main)
        binding.toolbarFMain.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.mainFragment->{
                    return@setOnMenuItemClickListener true
                }
            }

            return@setOnMenuItemClickListener true



        }

        binding.recyclerProductos.adapter = adapterProducto;
        if (resources.configuration.orientation == 1){
            binding.recyclerProductos.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        } else if (resources.configuration.orientation == 2){
            binding.recyclerProductos.layoutManager = GridLayoutManager(requireContext(),2)
        }


        /*binding.btnLeer.setOnClickListener {
            firebaseDatabase.reference.child("usuarios")
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        *//*val nombre = snapshot.child("nombre").value
                        val correo = snapshot.child("correo").value
                        val pass = snapshot.child("pass").value
                        Log.v("datos",nombre.toString())
                        Log.v("datos",correo.toString())
                        Log.v("datos",pass.toString())*//*
                        for (i in snapshot.children){
                            val usuario = i.getValue(Usuario::class.java)
                            Log.v("datos",usuario!!.nombre.toString())
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            *//*firebaseDatabase.reference.child("usuarios")
                .addChildEventListener(object : ChildEventListener{
                    override fun onChildAdded(snapshot: DataSnapshot,
                                              previousChildName: String?) {
                        Log.v("datos","nodo añadido")
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {

                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {

                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })*//*
        }
        binding.btnEscribir.setOnClickListener {

            Log.v("datos", "Pulsacion detectada")
            firebaseDatabase.reference.child("datos")
                .child("version_app").setValue("compras_app")
        }*/
    }
    override fun onDetach() {
        super.onDetach()
        firebaseAuth.signOut()
    }
}