package com.example.sistemainventario;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity_Entrada extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String idAutomatic;
    double mTotalexistencias = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Referenciar los IDs del archivo activity_entrada.xml
        setContentView(R.layout.activity_main_entrada);
        EditText identradas = findViewById(R.id.etidentradas);
        EditText idref = findViewById(R.id.etidref);
        EditText cantidad = findViewById(R.id.etcantidad);
        EditText precioentrada = findViewById(R.id.etprecioentrada);
        ImageButton btnsavesale = findViewById(R.id.btnsavesale);
        ImageButton btnback = findViewById(R.id.btnback);


        //Recibir la identificación enviada desde la actividad MainActivity
        idref.setText(getIntent().getStringExtra("eidref"));
        //Toast.makeText(getApplicationContext(),"IdAutomatic: ...: ..."+ getIntent().getStringExtra("eidautomatic"),Toast.LENGTH_SHORT).show();
        idAutomatic =  getIntent().getStringExtra("eidautomatic");
        mTotalexistencias = parseDouble(getIntent().getStringExtra("eexistencias"));

        //Botón regresar forma básica

        //btnback.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        onBackPressed();
        //    }
        //});

        //Botón regresar otra manera
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ir a menu ppal
                Intent cmain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(cmain);
            }
        });

        //Eventos
        //Guardar entrada
        btnsavesale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // buscar el id de la entrada en base de datos de sales
                db.collection("entrada")
                        .whereEqualTo("identradas",identradas.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().isEmpty()){
                                        //Guardar la venta
                                        Map<String, Object> cEntradas = new HashMap<>();
                                        cEntradas.put("identradas", identradas.getText().toString());
                                        cEntradas.put("idref", idref.getText().toString());
                                        cEntradas.put("cantidad", cantidad.getText().toString());
                                        cEntradas.put("precioentrada", precioentrada.getText().toString());
                                        // llamar coleccion
                                        db.collection("entrada")
                                                .add(cEntradas)
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        // buscar el idref para que retorne el total de la comision actual para acumular el total
                                                        //  de la comision con base en la comision de la venta
                                                        db.collection("producto").document(idAutomatic)
                                                                .update("existencias", mTotalexistencias + (parseInt(cantidad.getText().toString())))
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(getApplicationContext(), "Entrada guardada exitosamente...", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(getApplicationContext(), "Error al guardar la entrada....", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), "Id de la entrada ya existe...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });
    }
}