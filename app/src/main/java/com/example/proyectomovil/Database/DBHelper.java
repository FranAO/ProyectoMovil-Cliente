package com.example.proyectomovil.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import com.example.proyectomovil.Models.Student;
import com.example.proyectomovil.Models.Ticket;
import com.example.proyectomovil.Models.Package;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ValleRouteDB";
    private static final int DATABASE_VERSION = 1;
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE students (" +
                "id TEXT PRIMARY KEY, " +
                "firstName TEXT, " +
                "lastName TEXT, " +
                "institutionalEmail TEXT UNIQUE, " +
                "phone TEXT, " +
                "passwordHash TEXT, " +
                "role TEXT, " +
                "createdAt TEXT)");

        db.execSQL("CREATE TABLE tickets (" +
                "id TEXT PRIMARY KEY, " +
                "studentId TEXT, " +
                "packageId TEXT, " +
                "status TEXT, " +
                "purchaseDate TEXT, " +
                "FOREIGN KEY(studentId) REFERENCES students(id))");

        db.execSQL("CREATE TABLE packages (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT, " +
                "description TEXT, " +
                "ticketCount TEXT, " +
                "price REAL, " +
                "durationDays INTEGER, " +
                "active INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS students");
        db.execSQL("DROP TABLE IF EXISTS tickets");
        db.execSQL("DROP TABLE IF EXISTS packages");
        onCreate(db);
    }

    public boolean insertarStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", student.getId());
        values.put("firstName", student.getFirstName());
        values.put("lastName", student.getLastName());
        values.put("institutionalEmail", student.getInstitutionalEmail());
        values.put("phone", student.getPhone());
        values.put("passwordHash", student.getPaswordHash());
        values.put("role", student.getRole());
        values.put("createdAt", student.getCreatedAt() != null ? dateFormatter.format(student.getCreatedAt()) : null);

        long resultado = db.insert("students", null, values);
        return resultado != -1;
    }

    public boolean editarStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("firstName", student.getFirstName());
        values.put("lastName", student.getLastName());
        values.put("institutionalEmail", student.getInstitutionalEmail());
        values.put("phone", student.getPhone());
        values.put("passwordHash", student.getPaswordHash());
        values.put("role", student.getRole());
        values.put("createdAt", student.getCreatedAt() != null ? dateFormatter.format(student.getCreatedAt()) : null);

        int editar = db.update("students", values, "id = ?", new String[]{student.getId()});
        return editar > 0;
    }

    public boolean eliminarStudent(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int eliminar = db.delete("students", "id = ?", new String[]{id});
        return eliminar > 0;
    }

    public ArrayList<Student> obtenerStudents() {
        ArrayList<Student> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM students", null);
        if (cursor.moveToFirst()) {
            do {
                Date createdAt = null;
                try {
                    if (cursor.getString(7) != null) {
                        createdAt = dateFormatter.parse(cursor.getString(7));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Student student = new Student(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        createdAt
                );
                lista.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public Student obtenerStudentPorEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM students WHERE institutionalEmail = ?", new String[]{email});
        Student student = null;
        if (cursor.moveToFirst()) {
            Date createdAt = null;
            try {
                if (cursor.getString(7) != null) {
                    createdAt = dateFormatter.parse(cursor.getString(7));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            student = new Student(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    createdAt
            );
        }
        cursor.close();
        return student;
    }

    public boolean insertarTicket(Ticket ticket) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", ticket.getId());
        values.put("studentId", ticket.getStudentId());
        values.put("packageId", ticket.getPackageId());
        values.put("status", ticket.getStatus());
        values.put("purchaseDate", ticket.getPurchaseDate() != null ? dateFormatter.format(ticket.getPurchaseDate()) : null);

        long resultado = db.insert("tickets", null, values);
        return resultado != -1;
    }

    public boolean editarTicket(Ticket ticket) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("studentId", ticket.getStudentId());
        values.put("packageId", ticket.getPackageId());
        values.put("status", ticket.getStatus());
        values.put("purchaseDate", ticket.getPurchaseDate() != null ? dateFormatter.format(ticket.getPurchaseDate()) : null);

        int editar = db.update("tickets", values, "id = ?", new String[]{ticket.getId()});
        return editar > 0;
    }

    public boolean eliminarTicket(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int eliminar = db.delete("tickets", "id = ?", new String[]{id});
        return eliminar > 0;
    }

    public ArrayList<Ticket> obtenerTickets() {
        ArrayList<Ticket> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tickets", null);
        if (cursor.moveToFirst()) {
            do {
                Date purchaseDate = null;
                try {
                    if (cursor.getString(4) != null) {
                        purchaseDate = dateFormatter.parse(cursor.getString(4));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Ticket ticket = new Ticket(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        purchaseDate
                );
                lista.add(ticket);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public ArrayList<Ticket> obtenerTicketsPorEstudiante(String studentId) {
        ArrayList<Ticket> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tickets WHERE studentId = ?", new String[]{studentId});
        if (cursor.moveToFirst()) {
            do {
                Date purchaseDate = null;
                try {
                    if (cursor.getString(4) != null) {
                        purchaseDate = dateFormatter.parse(cursor.getString(4));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Ticket ticket = new Ticket(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        purchaseDate
                );
                lista.add(ticket);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public boolean insertarPackage(Package pkg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", pkg.getId());
        values.put("name", pkg.getName());
        values.put("description", pkg.getDescription());
        values.put("ticketCount", pkg.getTicketCount());
        values.put("price", pkg.getPrice());
        values.put("durationDays", pkg.getDurationsDays());
        values.put("active", pkg.isActive() ? 1 : 0);

        long resultado = db.insert("packages", null, values);
        return resultado != -1;
    }

    public boolean editarPackage(Package pkg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", pkg.getName());
        values.put("description", pkg.getDescription());
        values.put("ticketCount", pkg.getTicketCount());
        values.put("price", pkg.getPrice());
        values.put("durationDays", pkg.getDurationsDays());
        values.put("active", pkg.isActive() ? 1 : 0);

        int editar = db.update("packages", values, "id = ?", new String[]{pkg.getId()});
        return editar > 0;
    }

    public boolean eliminarPackage(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int eliminar = db.delete("packages", "id = ?", new String[]{id});
        return eliminar > 0;
    }

    public ArrayList<Package> obtenerPackages() {
        ArrayList<Package> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM packages", null);
        if (cursor.moveToFirst()) {
            do {
                Package pkg = new Package(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4),
                        cursor.getInt(5),
                        cursor.getInt(6) == 1
                );
                lista.add(pkg);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public ArrayList<Package> obtenerPackagesActivos() {
        ArrayList<Package> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM packages WHERE active = 1", null);
        if (cursor.moveToFirst()) {
            do {
                Package pkg = new Package(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4),
                        cursor.getInt(5),
                        cursor.getInt(6) == 1
                );
                lista.add(pkg);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public void limpiarTodasLasTablas() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM tickets");
        db.execSQL("DELETE FROM packages");
        db.execSQL("DELETE FROM students");
    }
}
