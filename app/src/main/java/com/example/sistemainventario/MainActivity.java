package com.example.sistemainventario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String idAutomatic;
    String mTotalexistencias;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Referenciar los IDs del archivo activity_main.xml
        EditText idref = findViewById(R.id.etidref);
        EditText descripcion = findViewById(R.id.etdescripcion);
        EditText preciounitario = findViewById(R.id.etpreciounitario);
        TextView existencias = findViewById(R.id.tvexistencia);
        ImageButton btnsave = findViewById(R.id.btnsave);
        ImageButton btnsearch = findViewById(R.id.btnsearch);
        ImageButton btnexistencias = findViewById(R.id.btnexistencias);


        // Eventos

        // BOTÓN DE GUARDAR DATO
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validación de que los datos estén diligenciados
                String midref = idref.getText().toString();
                String mdescripcion = descripcion.getText().toString();
                String mpreciounitario = preciounitario.getText().toString();

                // validar que esten diligenciados los datos
                if (!midref.isEmpty() && !mdescripcion.isEmpty() && !mpreciounitario.isEmpty()) {

                    //Crear una tabla temporal con los mismos campos de la colección entrada, mproducto es la tabla temporal
                    Map<String, Object> mproducto = new HashMap<>();
                    mproducto.put("idref", midref);
                    mproducto.put("descripcion", mdescripcion);
                    mproducto.put("preciounitario", mpreciounitario);
                    mproducto.put("existencias", 0);

                    //Buscar el id seller en la base de datos
                    db.collection("producto")
                            .whereEqualTo("idref", idref.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (!task.getResult().isEmpty()) {//si lo encontro en la base de datos
                                            // la instantanea tiene informacion del documento
                                            Toast.makeText(getApplicationContext(), "La referencia del producto ya existe en la Base de datos", Toast.LENGTH_SHORT).show();
                                        } else {
                                            //Si no encuentra el Id Seller del producto
                                            //agregar el documento a la coleccion (tabla) producto a traves de la tabla temporal mproducto
                                            db.collection("producto")
                                                    .add(mproducto)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Toast.makeText(getApplicationContext(), "Producto guardado correctamente", Toast.LENGTH_SHORT).show();
                                                            idref.setText("");
                                                            descripcion.setText("");
                                                            preciounitario.setText("");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext(), "Error al agregar producto...", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                }
                            });

                } else {
                    Toast.makeText(getApplicationContext(), "Debe completars todos los datos..", Toast.LENGTH_SHORT).show();
                }

            }
        });
        // BOTÓN DE BUSCAR
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Buscar por idseller y recuperar todos los datos de este idseller
                db.collection("producto")
                        .whereEqualTo("idref", idref.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        // la instancia tiene información del documento
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            idAutomatic = document.getId();
                                            //Mostrar información en cada uno de los objetos referenciados
                                            descripcion.setText(document.getString("descripcion"));
                                            preciounitario.setText(document.getString("preciounitario"));
                                            existencias.setText(String.valueOf(document.getDouble("existencias")));
                                        }
                                    } else {
                                        // si no encuentra el idref
                                        Toast.makeText(getApplicationContext(), "Referencia del producto no existe...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });
        btnexistencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Buscar por idseller y recuperar todos los datos
                db.collection("producto")
                        .whereEqualTo("idref", idref.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            mTotalexistencias = String.valueOf(document.getDouble("existencias"));
                                        }
                                        Intent iEntrada = new Intent(getApplicationContext(), MainActivity_Entrada.class);
                                        //pasar el parametro de la identificación del producto
                                        iEntrada.putExtra("eidref",idref.getText().toString());
                                        iEntrada.putExtra("eexistencias",mTotalexistencias);
                                        iEntrada.putExtra("eidautomatic",idAutomatic);
                                        startActivity(iEntrada);
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "Id del producto NO EXISTE...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });


    }
}