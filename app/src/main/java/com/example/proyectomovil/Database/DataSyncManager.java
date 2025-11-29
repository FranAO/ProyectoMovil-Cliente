package com.example.proyectomovil.Database;

import android.content.Context;
import android.os.StrictMode;
import android.widget.Toast;

import com.example.proyectomovil.Models.Package;
import com.example.proyectomovil.Models.Student;
import com.example.proyectomovil.Models.Ticket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DataSyncManager {

    private static final String API_URL = "http://10.0.2.2:5090/api";
    private DBHelper dbHelper;
    private Context context;

    public DataSyncManager(Context context) {
        this.context = context;
        this.dbHelper = new DBHelper(context);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
    }

    // Sincronizar Paquetes (GET)
    public void sincronizarPaquetes() {
        try {
            URL url = new URL(API_URL + "/package");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder respuesta = new StringBuilder();
                String linea;

                while ((linea = reader.readLine()) != null) {
                    respuesta.append(linea);
                }

                JSONArray array = new JSONArray(respuesta.toString());

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);

                    Package pkg = new Package(
                            obj.optString("id"),
                            obj.optString("name"),
                            obj.optString("description"),
                            String.valueOf(obj.optInt("ticketCount")),
                            obj.optDouble("price"),
                            obj.optInt("durationDays"),
                            obj.optBoolean("active")
                    );

                    if (!dbHelper.editarPackage(pkg)) {
                        dbHelper.insertarPackage(pkg);
                    }
                }
                Toast.makeText(context, "Paquetes sincronizados", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al sincronizar paquetes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Sincronizar estudiante por email desde web (GET)
    public Student sincronizarEstudiantePorEmail(String email) {
        try {
            URL url = new URL(API_URL + "/student/email/" + email);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder respuesta = new StringBuilder();
                String linea;

                while ((linea = reader.readLine()) != null) {
                    respuesta.append(linea);
                }

                JSONObject obj = new JSONObject(respuesta.toString());

                String fechaStr = obj.optString("createdAt");
                Date fecha = null;
                if (fechaStr != null && !fechaStr.isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                        fecha = sdf.parse(fechaStr);
                    } catch (Exception ex) {
                        // Ignorar error de fecha
                    }
                }

                Student student = new Student(
                        obj.optString("id"),
                        obj.optString("firstName"),
                        obj.optString("lastName"),
                        obj.optString("institutionalEmail"),
                        obj.optString("phone"),
                        obj.optString("passwordHash"),
                        obj.optString("role"),
                        fecha
                );

                // Guardar en base de datos local
                if (!dbHelper.editarStudent(student)) {
                    dbHelper.insertarStudent(student);
                }

                return student;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Sincronizar Estudiantes (GET)
    public void sincronizarEstudiantes() {
        try {
            URL url = new URL(API_URL + "/student");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder respuesta = new StringBuilder();
                String linea;

                while ((linea = reader.readLine()) != null) {
                    respuesta.append(linea);
                }

                JSONArray array = new JSONArray(respuesta.toString());

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);

                    String fechaStr = obj.optString("createdAt");
                    Date fecha = null;
                    if (fechaStr != null && !fechaStr.isEmpty()) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                            fecha = sdf.parse(fechaStr);
                        } catch (Exception ex) {
                            // Ignorar error de fecha
                        }
                    }

                    Student student = new Student(
                            obj.optString("id"),
                            obj.optString("firstName"),
                            obj.optString("lastName"),
                            obj.optString("institutionalEmail"),
                            obj.optString("phone"),
                            obj.optString("passwordHash"), // Nota: la API podría no devolver el hash por seguridad
                            obj.optString("role"),
                            fecha
                    );

                    if (!dbHelper.editarStudent(student)) {
                        dbHelper.insertarStudent(student);
                    }
                }
                Toast.makeText(context, "Estudiantes sincronizados", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al sincronizar estudiantes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void sincronizarTickets() {
        try {
            URL url = new URL(API_URL + "/ticket");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder respuesta = new StringBuilder();
                String linea;

                while ((linea = reader.readLine()) != null) {
                    respuesta.append(linea);
                }

                JSONArray array = new JSONArray(respuesta.toString());

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);

                    String fechaStr = obj.optString("purchaseDate");
                    Date fecha = null;
                    if (fechaStr != null && !fechaStr.isEmpty()) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                            fecha = sdf.parse(fechaStr);
                        } catch (Exception ex) {
                        }
                    }

                    Ticket ticket = new Ticket(
                            obj.optString("id"),
                            obj.optString("studentId"),
                            obj.optString("packageId"),
                            obj.optString("status"),
                            fecha
                    );

                    if (!dbHelper.editarTicket(ticket)) {
                        dbHelper.insertarTicket(ticket);
                    }
                }
                Toast.makeText(context, "Tickets sincronizados", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Enviar Estudiante (POST) - Retorna el estudiante con el ID generado por la web
    public Student enviarEstudiante(Student student) {
        try {
            URL url = new URL(API_URL + "/student");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            JSONObject json = new JSONObject();
            // No enviamos ID, la web lo genera
            json.put("firstName", student.getFirstName() != null ? student.getFirstName() : "");
            json.put("lastName", student.getLastName() != null ? student.getLastName() : "");
            json.put("institutionalEmail", student.getInstitutionalEmail());
            json.put("phone", student.getPhone() != null ? student.getPhone() : "");
            json.put("passwordHash", student.getPaswordHash());
            json.put("role", student.getRole());

            String jsonString = json.toString();
            android.util.Log.d("DataSync", "Enviando JSON: " + jsonString);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            android.util.Log.d("DataSync", "Response code: " + code);
            
            if (code == HttpURLConnection.HTTP_CREATED || code == HttpURLConnection.HTTP_OK) {
                // Leer la respuesta para obtener el ID generado
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder respuesta = new StringBuilder();
                String linea;

                while ((linea = reader.readLine()) != null) {
                    respuesta.append(linea);
                }

                String responseStr = respuesta.toString();
                android.util.Log.d("DataSync", "Response body: " + responseStr);
                
                JSONObject response = new JSONObject(responseStr);
                String webId = response.optString("id");
                
                // Crear nuevo estudiante con el ID de la web
                Student studentConIdWeb = new Student(
                    webId,
                    student.getFirstName(),
                    student.getLastName(),
                    student.getInstitutionalEmail(),
                    student.getPhone(),
                    student.getPaswordHash(),
                    student.getRole(),
                    student.getCreatedAt()
                );

                Toast.makeText(context, "✓ Registrado en servidor", Toast.LENGTH_SHORT).show();
                return studentConIdWeb;
            } else if (code == HttpURLConnection.HTTP_CONFLICT || code == 409) {
                Toast.makeText(context, "Email ya registrado en servidor", Toast.LENGTH_LONG).show();
                return null;
            } else {
                // Leer el error
                try {
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    StringBuilder errorMsg = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorMsg.append(errorLine);
                    }
                    android.util.Log.e("DataSync", "Error response: " + errorMsg.toString());
                    Toast.makeText(context, "Error servidor: " + code, Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    Toast.makeText(context, "Error servidor: " + code, Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e("DataSync", "Exception: " + e.getMessage(), e);
            Toast.makeText(context, "Sin conexión al servidor", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    // Enviar Ticket (Compra)
    public void enviarCompraTicket(String studentId, String packageId) {
        try {
            String urlParams = API_URL + "/ticket/purchase-package?studentId=" + studentId + "&packageId=" + packageId;
            URL url = new URL(urlParams);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            int code = con.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                Toast.makeText(context, "Compra realizada en web", Toast.LENGTH_SHORT).show();
                sincronizarTickets();
            } else {
                Toast.makeText(context, "Error en compra web: " + code, Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error de conexión: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // --- MÉTODOS PUT (Actualizar) ---

    public void actualizarEstudiante(Student student) {
        try {
            URL url = new URL(API_URL + "/student/" + student.getId());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            con.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("id", student.getId());
            json.put("firstName", student.getFirstName());
            json.put("lastName", student.getLastName());
            json.put("institutionalEmail", student.getInstitutionalEmail());
            json.put("phone", student.getPhone());
            json.put("passwordHash", student.getPaswordHash());
            json.put("role", student.getRole());

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                Toast.makeText(context, "Estudiante actualizado en web", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error al actualizar estudiante: " + code, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void actualizarPaquete(Package pkg) {
        try {
            URL url = new URL(API_URL + "/package/" + pkg.getId());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            con.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("id", pkg.getId());
            json.put("name", pkg.getName());
            json.put("description", pkg.getDescription());
            json.put("ticketCount", Integer.parseInt(pkg.getTicketCount()));
            json.put("price", pkg.getPrice());
            json.put("durationDays", pkg.getDurationsDays());
            json.put("active", pkg.isActive());

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                Toast.makeText(context, "Paquete actualizado en web", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error al actualizar paquete: " + code, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // --- MÉTODOS DELETE (Eliminar) ---

    public void eliminarEstudiante(String id) {
        try {
            URL url = new URL(API_URL + "/student/" + id);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("DELETE");

            int code = con.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                Toast.makeText(context, "Estudiante eliminado de web", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error al eliminar estudiante: " + code, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void eliminarPaquete(String id) {
        try {
            URL url = new URL(API_URL + "/package/" + id);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("DELETE");

            int code = con.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                Toast.makeText(context, "Paquete eliminado de web", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error al eliminar paquete: " + code, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Sincronizar todos los estudiantes locales a la web
    public void sincronizarEstudiantesLocalesAWeb() {
        try {
            ArrayList<Student> estudiantesLocales = dbHelper.obtenerStudents();
            int enviados = 0;
            
            for (Student student : estudiantesLocales) {
                Student resultado = enviarEstudiante(student);
                if (resultado != null) {
                    enviados++;
                }
            }
            
            Toast.makeText(context, enviados + " estudiantes enviados a la web", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al sincronizar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
